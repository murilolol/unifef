/*
 * Disciplina: Sistemas Operacionais
 * Tema: Simulador de Monoprogramacao vs. Multiprogramacao, Utilizacao da UCP, Turnaround e Throughput
 *
 * Como compilar:
 *   gcc -std=c11 -pthread simulador_multiprogramacao.c -o simulador_multiprogramacao
 *
 * Como executar:
 *   ./simulador_multiprogramacao
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#define MAX_PROCESSOS 10

typedef enum {
    NOVO,
    PRONTO,
    EXECUTANDO,
    BLOQUEADO_ES,
    FINALIZADO
} EstadoProcesso;

typedef struct {
    int id;
    int burst_cpu;          // Tempo total de computacao na UCP
    int tempo_restante_cpu; // Tempo de UCP que ainda precisa ser executado
    int burst_es;           // Tempo de espera em operacao de Entrada/Saida (E/S)
    int tempo_restante_es;  // Tempo de E/S pendente
    int tempo_chegada;      // Momento em que o processo chega ao sistema
    int tempo_termino;      // Momento em que o processo conclui tudo
    int turnaround;         // tempo_termino - tempo_chegada
    EstadoProcesso estado;
} Processo;

// Clona a lista de processos para manter os mesmos dados entre as simulacoes
void resetar_processos(Processo destino[], const Processo origem[], int n) {
    for (int i = 0; i < n; i++) {
        destino[i] = origem[i];
        destino[i].tempo_restante_cpu = origem[i].burst_cpu;
        destino[i].tempo_restante_es = origem[i].burst_es;
        destino[i].tempo_termino = 0;
        destino[i].turnaround = 0;
        destino[i].estado = PRONTO;
    }
}

/*
 * SIMULACAO 1: SISTEMA MONOPROGRAMAVEL (MONOTAREFA)
 * Em um sistema monotarefa, apenas UM processo reside na memoria por vez.
 * Quando o processo em execucao solicita E/S, a UCP permanece estritamente OCIOSA
 * aguardando o termino mecânico do periferico (gargalo da ociosidade).
 */
void simular_monoprogramacao(Processo procs[], int n) {
    printf("==============================================================\n");
    printf("          SIMULACAO 1: SISTEMA MONOPROGRAMAVEL (MONOTAREFA)   \n");
    printf("==============================================================\n");

    int tempo_atual = 0;
    int tempo_cpu_ocupada = 0;
    int tempo_cpu_ociosa = 0;

    for (int i = 0; i < n; i++) {
        printf("\n[t = %3d u.t.] Carregando Job %d na memoria RAM...\n", tempo_atual, procs[i].id);
        
        // Fase 1: Execucao na UCP
        printf("[t = %3d u.t.] Job %d inicia calculo na UCP (duracao: %d u.t.)...\n",
               tempo_atual, procs[i].id, procs[i].burst_cpu);
        tempo_atual += procs[i].burst_cpu;
        tempo_cpu_ocupada += procs[i].burst_cpu;

        // Fase 2: Operacao de Entrada e Saida (E/S)
        // A UCP fica completamente ociosa aguardando o periferico!
        printf("[t = %3d u.t.] Job %d solicita E/S lenta (duracao: %d u.t.)...\n",
               tempo_atual, procs[i].id, procs[i].burst_es);
        printf("             --> AVISO: UCP ESTA OCIOSA bloqueada aguardando periferico!\n");
        tempo_atual += procs[i].burst_es;
        tempo_cpu_ociosa += procs[i].burst_es;

        procs[i].tempo_termino = tempo_atual;
        procs[i].turnaround = procs[i].tempo_termino - procs[i].tempo_chegada;
        procs[i].estado = FINALIZADO;
        printf("[t = %3d u.t.] Job %d finalizado. Turnaround: %d u.t.\n",
               tempo_atual, procs[i].id, procs[i].turnaround);
    }

    double turnaround_medio = 0;
    for (int i = 0; i < n; i++) {
        turnaround_medio += procs[i].turnaround;
    }
    turnaround_medio /= n;

    double throughput = (double)n / tempo_atual;
    double utilizacao_cpu = ((double)tempo_cpu_ocupada / tempo_atual) * 100.0;

    printf("\n--- METRICAS DA MONOPROGRAMACAO ---\n");
    printf("Tempo Total de Simulacao : %d u.t.\n", tempo_atual);
    printf("Tempo de UCP Ocupada     : %d u.t.\n", tempo_cpu_ocupada);
    printf("Tempo de UCP Ociosa      : %d u.t. (Desperdicio de silicio)\n", tempo_cpu_ociosa);
    printf("Taxa de Utilizacao da UCP: %.2f%%\n", utilizacao_cpu);
    printf("Tempo Medio de Turnaround: %.2f u.t.\n", turnaround_medio);
    printf("Throughput (Vazao)       : %.4f processos/u.t.\n", throughput);
}

/*
 * SIMULACAO 2: SISTEMA MULTIPROGRAMAVEL (MULTITAREFA)
 * Multiplos processos residem na memoria simultaneamente.
 * Quando o processo atual entra em E/S, o escalonador faz a preempcao ou troca de contexto
 * e entrega a UCP imediatamente para outro processo que esteja pronto.
 */
void simular_multiprogramacao(Processo procs[], int n) {
    printf("\n==============================================================\n");
    printf("         SIMULACAO 2: SISTEMA MULTIPROGRAMAVEL (MULTITAREFA)  \n");
    printf("==============================================================\n");

    int tempo_atual = 0;
    int tempo_cpu_ocupada = 0;
    int tempo_cpu_ociosa = 0;
    int processos_finalizados = 0;
    int processo_na_cpu = -1;

    while (processos_finalizados < n) {
        // 1. Processar E/S de quem estiver bloqueado (E/S ocorre concorrentemente ao processamento da UCP)
        for (int i = 0; i < n; i++) {
            if (procs[i].estado == BLOQUEADO_ES) {
                procs[i].tempo_restante_es--;
                if (procs[i].tempo_restante_es == 0) {
                    procs[i].estado = FINALIZADO;
                    procs[i].tempo_termino = tempo_atual;
                    procs[i].turnaround = procs[i].tempo_termino - procs[i].tempo_chegada;
                    processos_finalizados++;
                    printf("[t = %3d u.t.] Processo %d concluiu E/S e foi FINALIZADO. Turnaround: %d u.t.\n",
                           tempo_atual, procs[i].id, procs[i].turnaround);
                }
            }
        }

        // 2. Se a UCP estiver livre, o escalonador seleciona o proximo processo pronto
        if (processo_na_cpu == -1) {
            for (int i = 0; i < n; i++) {
                if (procs[i].estado == PRONTO) {
                    processo_na_cpu = i;
                    procs[i].estado = EXECUTANDO;
                    printf("[t = %3d u.t.] Escalonador despacha Processo %d para a UCP.\n", tempo_atual, procs[i].id);
                    break;
                }
            }
        }

        // 3. Execucao do ciclo de clock na UCP
        if (processo_na_cpu != -1) {
            tempo_cpu_ocupada++;
            procs[processo_na_cpu].tempo_restante_cpu--;

            // Verificamos se o processo terminou a fatia de calculo
            if (procs[processo_na_cpu].tempo_restante_cpu == 0) {
                printf("[t = %3d u.t.] Processo %d terminou calculo de UCP -> Transiciona para BLOQUEADO_ES.\n",
                       tempo_atual + 1, procs[processo_na_cpu].id);
                procs[processo_na_cpu].estado = BLOQUEADO_ES;
                processo_na_cpu = -1; // Libera a UCP para troca de contexto imediata
            }
        } else {
            // Nenhum processo esta pronto (todos em E/S ou finalizados)
            if (processos_finalizados < n) {
                tempo_cpu_ociosa++;
            }
        }

        tempo_atual++;
    }

    // Ajuste do contador de tempo para o termino real
    int tempo_total = tempo_atual - 1;

    double turnaround_medio = 0;
    for (int i = 0; i < n; i++) {
        turnaround_medio += procs[i].turnaround;
    }
    turnaround_medio /= n;

    double throughput = (double)n / tempo_total;
    double utilizacao_cpu = ((double)tempo_cpu_ocupada / tempo_total) * 100.0;

    printf("\n--- METRICAS DA MULTIPROGRAMACAO ---\n");
    printf("Tempo Total de Simulacao : %d u.t.\n", tempo_total);
    printf("Tempo de UCP Ocupada     : %d u.t.\n", tempo_cpu_ocupada);
    printf("Tempo de UCP Ociosa      : %d u.t.\n", tempo_cpu_ociosa);
    printf("Taxa de Utilizacao da UCP: %.2f%%\n", utilizacao_cpu);
    printf("Tempo Medio de Turnaround: %.2f u.t.\n", turnaround_medio);
    printf("Throughput (Vazao)       : %.4f processos/u.t.\n", throughput);
}

int main(void) {
    printf("Simulacao Didatica de Sistemas Operacionais - Unidade 2\n");
    printf("Demonstracao de Desempenho: Monoprogramacao vs. Multiprogramacao\n\n");

    // Cenário identico de carga de trabalho:
    // 3 tarefas, cada uma com calculos de UCP e operacoes lentas de E/S
    Processo carga_trabalho[3] = {
        {.id = 1, .burst_cpu = 4, .burst_es = 8, .tempo_chegada = 0},
        {.id = 2, .burst_cpu = 3, .burst_es = 6, .tempo_chegada = 0},
        {.id = 3, .burst_cpu = 5, .burst_es = 7, .tempo_chegada = 0}
    };

    Processo lote_teste[3];

    // Executa Simulacao 1
    resetar_processos(lote_teste, carga_trabalho, 3);
    simular_monoprogramacao(lote_teste, 3);

    // Executa Simulacao 2 com a mesmissima carga
    resetar_processos(lote_teste, carga_trabalho, 3);
    simular_multiprogramacao(lote_teste, 3);

    printf("\n==============================================================\n");
    printf("                  CONCLUSAO PEDAGOGICA                        \n");
    printf("==============================================================\n");
    printf("A multiprogramacao sobrepoe o tempo de E/S de um processo com \n");
    printf("o tempo de calculo de outro, elevando drasticamente a taxa de \n");
    printf("utilizacao da UCP e o throughput (vazao global do sistema).\n");

    return 0;
}
