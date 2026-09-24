/*
 * Disciplina: Sistemas Operacionais
 * Professor: Guilherme de Morais
 * Tema: Simulador de Bloco de Controle de Processo (PCB), Tabela de Processos, Estados e Chaveamento de Contexto
 * Como compilar: gcc -std=c11 -pthread simulador_pcb_ciclo_vida.c -o simulador_pcb_ciclo_vida
 * Como executar: ./simulador_pcb_ciclo_vida
 *
 * Descricao:
 * Este programa implementa um simulador didatico das estruturas de dados nucleares do SO:
 * 1. O Bloco de Controle de Processo (PCB) com registradores simulados, PC, prioridade e estado.
 * 2. A Tabela Global de Processos indexada por PID.
 * 3. A Fila de Prontos ordenada estritamente por prioridade de escalonamento.
 * 4. A Fila de Bloqueados indexada pelo identificador do evento aguardado (E/S).
 * 5. O Despachante (Dispatcher) e a rotina de Chaveamento de Contexto (Context Switch).
 * 6. Preempcao por esgotamento de fatia de tempo (quantum) via temporizador.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_PROCESSOS 10
#define QUANTUM_PADRAO 3

// Estados fundamentais do ciclo de vida do processo (Modelo Tripartite)
typedef enum {
    ESTADO_NOVO,
    ESTADO_PRONTO,
    ESTADO_EXECUCAO,
    ESTADO_BLOQUEADO,
    ESTADO_TERMINADO
} EstadoProcesso;

// Contexto de execucao: copia fiel dos registradores da CPU
typedef struct {
    int pc;        // Contador de Programa (Program Counter)
    int sp;        // Apontador de Pilha (Stack Pointer)
    int acc;       // Registrador Acumulador / Dados
    int flags;     // Registro de status e condicoes
} RegistradoresCPU;

// Bloco de Controle de Processo (PCB - Process Control Block)
typedef struct PCB {
    int pid;                      // Identificador unico do processo
    int ppid;                     // PID do processo-pai
    char nome[32];                // Nome do programa associado
    EstadoProcesso estado;        // Estado atual no ciclo de vida
    RegistradoresCPU contexto;    // Contexto de hardware salvo
    int prioridade;               // Prioridade (quanto maior, mais prioritario)
    int quantum_restante;         // Tempo restante de CPU na rodada atual
    int id_evento_espera;         // Evento que causou o bloqueio (0 = nenhum)
    int total_instrucoes;         // Total de instrucoes a executar antes de terminar
} PCB;

// Tabela global de processos gerenciada pelo nucleo
static PCB tabela_processos[MAX_PROCESSOS];
static int total_processos = 0;

// CPU fisica simulada com um unico nucleo de execucao
static RegistradoresCPU cpu_hardware = { .pc = 0, .sp = 1000, .acc = 0, .flags = 0 };
static int pid_em_execucao = -1; // -1 indica CPU ociosa

// Converte o enum de estado para string textual legivel
static const char* obter_nome_estado(EstadoProcesso estado) {
    switch (estado) {
        case ESTADO_NOVO: return "NOVO";
        case ESTADO_PRONTO: return "PRONTO";
        case ESTADO_EXECUCAO: return "EXECUCAO";
        case ESTADO_BLOQUEADO: return "BLOQUEADO";
        case ESTADO_TERMINADO: return "TERMINADO";
        default: return "DESCONHECIDO";
    }
}

// Inicializa e insere um novo processo na Tabela de Processos do SO
int criar_processo(const char *nome, int ppid, int prioridade, int total_instrucoes) {
    if (total_processos >= MAX_PROCESSOS) {
        printf("[SO:ERRO] Tabela de processos esgotada. Falha ao instanciar: %s\n", nome);
        return -1;
    }

    int pid = total_processos + 1;
    PCB *novo = &tabela_processos[total_processos];
    novo->pid = pid;
    novo->ppid = ppid;
    strncpy(novo->nome, nome, sizeof(novo->nome) - 1);
    novo->estado = ESTADO_PRONTO;
    novo->prioridade = prioridade;
    novo->quantum_restante = QUANTUM_PADRAO;
    novo->id_evento_espera = 0;
    novo->total_instrucoes = total_instrucoes;

    // Contexto inicial virgem: PC aponta para a instrucao 0
    novo->contexto.pc = 0;
    novo->contexto.sp = 1000 - (pid * 100); // Faixa isolada de pilha
    novo->contexto.acc = 0;
    novo->contexto.flags = 0;

    printf("[SO:CRIACAO] PCB criado com sucesso -> PID: %d, Pai: %d, Nome: %s, Prioridade: %d, Instrucoes: %d\n",
           novo->pid, novo->ppid, novo->nome, novo->prioridade, novo->total_instrucoes);

    total_processos++;
    return pid;
}

// Localiza o primeiro processo no estado PRONTO com a maior prioridade
int selecionar_proximo_pronto(void) {
    int melhor_pid = -1;
    int maior_prioridade = -1;

    for (int i = 0; i < total_processos; i++) {
        if (tabela_processos[i].estado == ESTADO_PRONTO) {
            if (tabela_processos[i].prioridade > maior_prioridade) {
                maior_prioridade = tabela_processos[i].prioridade;
                melhor_pid = tabela_processos[i].pid;
            }
        }
    }
    return melhor_pid;
}

// Efetua o mecanismo de Chaveamento de Contexto (Context Switch)
void chavear_contexto(int pid_saindo, int pid_entrando) {
    printf("\n--- [CHAVEAMENTO DE CONTEXTO (CONTEXT SWITCH)] ---\n");

    // 1. Salva o contexto do processo que esta deixando a CPU no seu respectivo PCB
    if (pid_saindo != -1) {
        PCB *pcb_saindo = &tabela_processos[pid_saindo - 1];
        pcb_saindo->contexto = cpu_hardware;
        printf(" 1. Salvando contexto do PID %d no PCB -> PC: %d, SP: %d, ACC: %d, Flags: %d\n",
               pcb_saindo->pid, pcb_saindo->contexto.pc, pcb_saindo->contexto.sp,
               pcb_saindo->contexto.acc, pcb_saindo->contexto.flags);
    }

    // 2. O Despachante restaura os registradores do novo processo para o silicio da CPU
    if (pid_entrando != -1) {
        PCB *pcb_entrando = &tabela_processos[pid_entrando - 1];
        cpu_hardware = pcb_entrando->contexto;
        pcb_entrando->estado = ESTADO_EXECUCAO;
        pcb_entrando->quantum_restante = QUANTUM_PADRAO;
        pid_em_execucao = pid_entrando;

        printf(" 2. Despachante carregou registradores do PID %d (%s) para a CPU -> PC: %d\n",
               pcb_entrando->pid, pcb_entrando->nome, cpu_hardware.pc);
        printf(" 3. Modo comutado para Modo Usuario. Execucao transferida para instrucao no endereco PC.\n");
    } else {
        pid_em_execucao = -1;
        printf(" [SO] Nenhum processo pronto na fila. CPU entra em estado ocioso (HALT/IDLE).\n");
    }
    printf("-------------------------------------------------\n\n");
}

// Despacho: aloca o melhor processo da fila de prontos na CPU
void despachar(void) {
    int proximo_pid = selecionar_proximo_pronto();
    if (proximo_pid != -1) {
        chavear_contexto(pid_em_execucao, proximo_pid);
    } else if (pid_em_execucao == -1) {
        printf("[SO] CPU continua ociosa. Aguardando eventos ou novos processos.\n");
    }
}

// Transicao disparada pelo proprio processo: chamada de sistema de E/S bloqueante
void requisitar_io_bloqueante(int pid, int id_evento) {
    if (pid_em_execucao != pid) return;

    PCB *pcb = &tabela_processos[pid - 1];
    printf("[TRANSICAO: EXECUCAO -> BLOQUEADO] PID %d (%s) solicitou E/S (Evento ID %d) e adormeceu.\n",
           pcb->pid, pcb->nome, id_evento);

    pcb->estado = ESTADO_BLOQUEADO;
    pcb->id_evento_espera = id_evento;

    // Salva contexto e cede imediatamente a CPU para o proximo pronto
    int proximo = selecionar_proximo_pronto();
    chavear_contexto(pid, proximo);
}

// Tratador de interrupcao de hardware: dispositivo externo concluiu a operacao
void interrupcao_io_concluida(int id_evento) {
    printf("\n[INTERRUPCAO DE HARDWARE] O evento de E/S ID %d foi concluido!\n", id_evento);
    for (int i = 0; i < total_processos; i++) {
        if (tabela_processos[i].estado == ESTADO_BLOQUEADO && tabela_processos[i].id_evento_espera == id_evento) {
            tabela_processos[i].estado = ESTADO_PRONTO;
            tabela_processos[i].id_evento_espera = 0;
            printf("[TRANSICAO: BLOQUEADO -> PRONTO] Processo PID %d acordou e retornou a fila de prontos.\n",
                   tabela_processos[i].pid);
        }
    }

    // Se a CPU estiver ociosa ou se o acordado tiver maior prioridade, avalia reescalonamento
    if (pid_em_execucao == -1) {
        despachar();
    }
}

// Simula um ciclo de clock da CPU e o temporizador de intervalo (Quantum)
void ciclo_de_clock(void) {
    if (pid_em_execucao == -1) {
        despachar();
        if (pid_em_execucao == -1) return;
    }

    PCB *pcb = &tabela_processos[pid_em_execucao - 1];

    // CPU executa uma instrucao
    cpu_hardware.pc++;
    cpu_hardware.acc += 10; // Computacao ficticia
    pcb->total_instrucoes--;
    pcb->quantum_restante--;

    printf("  [CPU RUN] PID %d (%s) executou instrucao. Novo PC: %d, Instrucoes restantes: %d, Quantum restante: %d\n",
           pcb->pid, pcb->nome, cpu_hardware.pc, pcb->total_instrucoes, pcb->quantum_restante);

    // Verifica se o processo concluiu todas as suas instrucoes
    if (pcb->total_instrucoes <= 0) {
        printf("[TRANSICAO: EXECUCAO -> TERMINADO] Processo PID %d concluiu sua execucao e sera destruido.\n", pcb->pid);
        pcb->estado = ESTADO_TERMINADO;
        pid_em_execucao = -1;
        despachar();
        return;
    }

    // Preempcao por temporizador de hardware: Fim do Quantum
    if (pcb->quantum_restante <= 0) {
        printf("[TRANSICAO: EXECUCAO -> PRONTO] INTERRUPCAO DO TEMPORIZADOR! Quantum do PID %d expirou (Preempcao).\n",
               pcb->pid);
        pcb->estado = ESTADO_PRONTO;
        int proximo = selecionar_proximo_pronto();
        chavear_contexto(pcb->pid, proximo);
    }
}

// Exibe o painel de controle e o estado atual de toda a Tabela de Processos
void imprimir_tabela_processos(void) {
    printf("\n=================== TABELA DE PROCESSOS DO SISTEMA ===================\n");
    printf("PID | PPID | Nome Processo       | Estado     | Prioridade | PC Salvo | Evento\n");
    printf("----+------+---------------------+------------+------------+----------+-------\n");
    for (int i = 0; i < total_processos; i++) {
        PCB *p = &tabela_processos[i];
        int pc_exibicao = (p->pid == pid_em_execucao) ? cpu_hardware.pc : p->contexto.pc;
        printf("%3d | %4d | %-19s | %-10s | %10d | %8d | %6d\n",
               p->pid, p->ppid, p->nome, obter_nome_estado(p->estado),
               p->prioridade, pc_exibicao, p->id_evento_espera);
    }
    printf("====================================================================\n\n");
}

int main(void) {
    printf("=== INICIALIZACAO DO NUCLEO: GERENCIA DE PROCESSOS E PCB ===\n\n");

    // 1. Criacao dos processos com prioridades e cargas de trabalho distintas
    // PID 1: init/systemd ficticio
    int pid_init = criar_processo("systemd_init", 0, 1, 10);
    // PID 2: Renderizador Web (Prioridade 5, 6 instrucoes)
    int pid_web = criar_processo("renderizador_web", pid_init, 5, 6);
    // PID 3: Spooler de Impressao (Prioridade 2, 5 instrucoes)
    int pid_spool = criar_processo("spooler_impressao", pid_init, 2, 5);

    imprimir_tabela_processos();

    // 2. Primeiro despacho da CPU: o de maior prioridade (renderizador_web) deve assumir
    printf("[TESTE 1] Iniciando o ciclo de processamento da CPU...\n");
    despachar();

    // Executa instrucoes consumindo o quantum de tempo
    ciclo_de_clock();
    ciclo_de_clock();

    // 3. O processo web faz uma chamada de sistema solicitando leitura de rede (Evento 101)
    printf("\n[TESTE 2] Processo Web requisita dados da rede (E/S bloqueante)...\n");
    requisitar_io_bloqueante(pid_web, 101);
    imprimir_tabela_processos();

    // 4. Com o processo Web adormecido, o Spooler de Impressao (Prioridade 2) assume a CPU
    printf("[TESTE 3] Processando ciclos para a proxima tarefa pronta na fila...\n");
    ciclo_de_clock();
    ciclo_de_clock();
    ciclo_de_clock(); // Aqui o quantum do spooler esgotara, forcando preempcao

    imprimir_tabela_processos();

    // 5. Interrupcao assincrona da placa de rede avisa que os dados chegaram
    printf("[TESTE 4] Interrupcao de hardware conclui a E/S da rede...\n");
    interrupcao_io_concluida(101);
    imprimir_tabela_processos();

    // 6. Continua a execucao ate que os processos terminem
    printf("[TESTE 5] Executando ate a finalizacao das tarefas...\n");
    for (int ciclo = 0; ciclo < 10; ciclo++) {
        ciclo_de_clock();
    }

    imprimir_tabela_processos();
    printf("=== SIMULACAO CONCLUIDA COM SUCESSO ===\n");
    return 0;
}
