/*
 * Disciplina: Sistemas Operacionais
 * Professor: Guilherme de Morais
 * Tema: Espaco de Enderecamento do Processo: Regioes de Texto, Dados, Heap e Pilha
 * Como compilar: gcc -std=c11 -pthread espaco_enderecamento_processo.c -o espaco_enderecamento_processo
 * Como executar: ./espaco_enderecamento_processo
 *
 * Descricao:
 * Este programa demonstra empiricamente a anatomia do espaco de enderecamento virtual:
 * 1. Regiao de Texto (Text/Code): Instrucoes de maquina e ponteiros de funcao (Somente Leitura R-X).
 * 2. Regiao de Dados Inicializados (.data): Variaveis globais e estaticas inicializadas.
 * 3. Regiao de Dados Nao-Inicializados (.bss): Variaveis globais sem valor explicito.
 * 4. Regiao de Alocacao Dinamica (Heap): Memoria requisitada via malloc (cresce para cima).
 * 5. Regiao de Pilha (Stack): Registros de ativacao de funcoes e variaveis locais (cresce para baixo).
 * 6. Protecao de Memoria: Captura de sinal SIGSEGV ao tentar violar a protecao de somente leitura da area de texto.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <signal.h>
#include <setjmp.h>
#include <unistd.h>

// Variaveis alocadas na Regiao de Dados Inicializados (.data)
int global_inicializada = 42;
static int estatica_inicializada = 100;

// Variaveis alocadas na Regiao de Dados Nao-Inicializados (.bss)
int global_nao_inicializada;
static int estatica_nao_inicializada;

// Buffer para recuperar execucao caso a MMU emita falta de segmentacao
static sigjmp_buf ambiente_recuperacao;

// Tratador de interrupcao sincrona (Trap de Violacao de Segmento / SIGSEGV)
void tratador_segmentation_fault(int sinal) {
    printf("\n [TRAP SINCRONA / INTERRUPCAO DETECTADA]\n");
    printf("  Sinal recebido: %d (SIGSEGV - Segmentation Fault).\n", sinal);
    printf("  Causa: A MMU barrou a tentativa de escrita em pagina de memoria marcada como Somente Leitura (R-X)!\n");
    printf("  O nucleo interceptou a violacao e impediu a corrupcao do codigo binario.\n");
    // Retorna o fluxo de execucao de forma segura para o ponto de teste
    siglongjmp(ambiente_recuperacao, 1);
}

// Funcao alocada na Regiao de Texto (Instrucoes de Maquina)
void funcao_exemplo_texto(void) {
    printf("Executando instrucao dentro da Regiao de Texto.\n");
}

// Funcao recursiva para demonstrar o crescimento da Pilha (Stack Frames)
void inspecionar_pilha(int nivel) {
    int variavel_local_pilha = nivel * 10;
    printf("  [Pilha / Nivel %d] Endereco da variavel local: %p\n", nivel, (void*)&variavel_local_pilha);
    
    if (nivel < 3) {
        inspecionar_pilha(nivel + 1);
    }
}

int main(void) {
    printf("========================================================================\n");
    printf("   MAPA DO ESPACO DE ENDERECAMENTO VIRTUAL DO PROCESSO (PID: %d)       \n", getpid());
    printf("========================================================================\n\n");

    // Variaveis locais na pilha da funcao main
    int local_main_1 = 1;
    int local_main_2 = 2;

    // Alocacoes no Heap via chamada ao gerenciador de memoria do SO
    int *heap_bloco_1 = (int*)malloc(sizeof(int) * 10);
    int *heap_bloco_2 = (int*)malloc(sizeof(int) * 10);

    if (!heap_bloco_1 || !heap_bloco_2) {
        perror("Falha na alocacao dinamica de memoria");
        return EXIT_FAILURE;
    }

    // 1. Regiao de Texto (Text Segment)
    printf("1. REGIAO DE TEXTO (CODE SEGMENT) - Permissao: R-X (Leitura e Execucao)\n");
    printf("   Funcao 'main':                 %p\n", (void*)(uintptr_t)main);
    printf("   Funcao 'funcao_exemplo_texto': %p\n", (void*)(uintptr_t)funcao_exemplo_texto);
    printf("   Funcao 'inspecionar_pilha':    %p\n\n", (void*)(uintptr_t)inspecionar_pilha);

    // 2. Regiao de Dados Inicializados (.data)
    printf("2. REGIAO DE DADOS INICIALIZADOS (.data) - Permissao: RW- (Leitura e Escrita)\n");
    printf("   Global Inicializada:           %p (Valor: %d)\n", (void*)&global_inicializada, global_inicializada);
    printf("   Estatica Inicializada:         %p (Valor: %d)\n\n", (void*)&estatica_inicializada, estatica_inicializada);

    // 3. Regiao de Dados Nao-Inicializados (.bss)
    printf("3. REGIAO DE DADOS NAO-INICIALIZADOS (.bss) - Permissao: RW- (Leitura e Escrita)\n");
    printf("   Global Nao Inicializada:       %p\n", (void*)&global_nao_inicializada);
    printf("   Estatica Nao Inicializada:     %p\n\n", (void*)&estatica_nao_inicializada);

    // 4. Regiao de Heap (Alocacao Dinamica)
    printf("4. REGIAO DE HEAP (MEMORIA DINAMICA) - Permissao: RW- (Cresce para enderecos maiores)\n");
    printf("   Bloco Heap 1:                  %p\n", (void*)heap_bloco_1);
    printf("   Bloco Heap 2:                  %p\n", (void*)heap_bloco_2);
    if (heap_bloco_2 > heap_bloco_1) {
        printf("   -> Confirmacao: Bloco 2 esta em endereco MAIOR que Bloco 1 (Crescimento ascendente).\n\n");
    }

    // 5. Regiao de Pilha (Stack Segment)
    printf("5. REGIAO DE PILHA (STACK) - Permissao: RW- (Cresce para enderecos menores)\n");
    printf("   Variavel Local Main 1:         %p\n", (void*)&local_main_1);
    printf("   Variavel Local Main 2:         %p\n", (void*)&local_main_2);
    printf("   Demonstrando crescimento dos frames de ativacao em chamadas aninhadas:\n");
    inspecionar_pilha(1);
    printf("   -> Observacao: Conforme a recursao aprofunda, os enderecos DIMINUEM.\n\n");

    // 6. Teste de Violacao de Protecao e Interrupcao Sincrona da MMU
    printf("========================================================================\n");
    printf("6. TESTE DE REGRAS DE ACESSO E PROTECAO DE MEMORIA DA REGIAO DE TEXTO\n");
    printf("========================================================================\n");
    printf("Tentando modificar arbitrariamente o codigo executavel da funcao 'funcao_exemplo_texto'...\n");

    // Instala tratador de sinal para lidar com a falta de segmentacao
    struct sigaction sa;
    sa.sa_handler = tratador_segmentation_fault;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    sigaction(SIGSEGV, &sa, NULL);

    if (sigsetjmp(ambiente_recuperacao, 1) == 0) {
        // Converte o ponteiro de codigo em ponteiro de escrita para simular erro/ataque
        unsigned char *codigo_texto = (unsigned char*)(uintptr_t)funcao_exemplo_texto;
        *codigo_texto = 0x90; // Tentativa de sobrescrever com opcode NOP
        printf("[ERRO CRITICO] O sistema permitiu alterar a regiao de texto! Protecao inativa.\n");
    } else {
        printf("\n[CONCLUSAO DIDATICA] O sistema operacional e o hardware protegeram com sucesso a regiao de texto.\n");
    }

    // Liberacao de recursos da memoria
    free(heap_bloco_1);
    free(heap_bloco_2);

    return 0;
}
