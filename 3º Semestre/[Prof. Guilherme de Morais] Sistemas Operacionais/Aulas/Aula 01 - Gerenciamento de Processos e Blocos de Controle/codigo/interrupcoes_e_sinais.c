/*
 * Disciplina: Sistemas Operacionais
 * Professor: Guilherme de Morais
 * Tema: Interrupcoes Sincronas (Traps), Assincronas (Hardware/Timer) e Comparacao com Sondagem (Polling)
 * Como compilar: gcc -std=c11 -pthread interrupcoes_e_sinais.c -o interrupcoes_e_sinais
 * Como executar: ./interrupcoes_e_sinais
 *
 * Descricao:
 * Este programa implementa em codigo C real o comportamento dos mecanismos de interrupcao:
 * 1. Interrupcao Assincrona: Simula o temporizador de intervalo (Interval Timer / Quantum) usando SIGALRM
 *    e a chamada setitimer, disparada pelo relogio sem correlacao com a instrucao em execucao.
 * 2. Interrupcao Sincrona (Trap / Excecao): Simula erro aritmetico (divisao por zero) capturado via SIGFPE,
 *    reproduzivel deterministicamente pelo codigo do proprio processo.
 * 3. Comparacao de Desempenho e Consumo de CPU: Demonstra o desperdicio de ciclos em Sondagem (Polling)
 *    contra a eficiencia do modelo Orientado a Interrupcao (onde o processo adormece liberando o processador).
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <sys/time.h>
#include <setjmp.h>
#include <stdbool.h>
#include <time.h>

// Variaveis de controle para o temporizador assincrono
static volatile int ticks_relogio = 0;
static volatile bool temporizador_ativo = true;

// Buffer para recuperar fluxo apos Trap de divisao por zero
static sigjmp_buf ambiente_trap_aritmetica;

// =========================================================================
// 1. TRATADOR DE INTERRUPCAO ASSINCRONA (ISR DO TEMPORIZADOR DE INTERVALO)
// =========================================================================
void tratador_temporizador_assincrono(int sinal) {
    (void)sinal;
    ticks_relogio++;
    printf(" [INTERRUPCAO ASSINCRONA - RELOGIO] Tick %d do Quantum recebido! (Origem: Timer de Hardware)\n",
           ticks_relogio);
    
    if (ticks_relogio >= 3) {
        printf(" [TEMPORIZADOR] 3 Ticks acumulados: Fatia de tempo (Quantum) esgotada! Sinalizando preempcao.\n");
        temporizador_ativo = false;
    }
}

// =========================================================================
// 2. TRATADOR DE INTERRUPCAO SINCRONA (TRAP DE ARITMETICA / EXCECAO)
// =========================================================================
void tratador_trap_aritmetica(int sinal) {
    (void)sinal;
    printf("\n [TRAP SINCRONA / EXCECAO INTERNA DETECTADA]\n");
    printf("  Sinal recebido: SIGFPE (Floating Point Exception / Divisao por zero).\n");
    printf("  Causa: A ULA da CPU detectou divisao por zero no fluxo da instrucao corrente.\n");
    printf("  A interrupcao ocorreu de forma sincrona e reproduzivel exatamente nesta instrucao.\n");
    siglongjmp(ambiente_trap_aritmetica, 1);
}

// =========================================================================
// 3. COMPARACAO: SONDAGEM (POLLING) VERSUS ORIENTADO A INTERRUPCAO
// =========================================================================
void demonstrar_sondagem_vs_interrupcao(void) {
    printf("\n========================================================================\n");
    printf("3. COMPARATIVO: MODELO POR SONDAGEM (POLLING) VS ORIENTADO A INTERRUPCAO\n");
    printf("========================================================================\n");

    // Simula a chegada de um evento externo apos 100 milissegundos
    clock_t inicio, fim;
    unsigned long long ciclos_desperdiçados = 0;
    volatile bool dispositivo_pronto = false;

    printf("A) Executando modelo arcaico de Sondagem (Polling / Busy Waiting)...\n");
    inicio = clock();

    // Laço de espera ocupada (queima milhoes de ciclos de CPU inutilmente)
    for (unsigned long long i = 0; i < 50000000ULL; i++) {
        ciclos_desperdiçados++;
        if (i == 49999999ULL) {
            dispositivo_pronto = true;
        }
    }
    fim = clock();
    double tempo_polling = ((double)(fim - inicio)) / CLOCKS_PER_SEC;
    printf("   -> Concluido: CPU realizou %llu verificacoes no dispositivo.\n", ciclos_desperdiçados);
    printf("   -> Tempo de processador queimado exclusivamente em espera ocupada: %.4f segundos.\n\n", tempo_polling);

    printf("B) Executando modelo moderno Orientado a Interrupcao...\n");
    printf("   -> O processo requisita a E/S e cede a CPU adormecendo (chamada nanosleep/pause).\n");
    inicio = clock();

    // O processo adormece voluntariamente, permitindo ao SO despachar outras tarefas
    struct timespec tempo_espera = { .tv_sec = 0, .tv_nsec = 100000000L }; // 100 ms
    nanosleep(&tempo_espera, NULL);

    fim = clock();
    double tempo_interrupcao = ((double)(fim - inicio)) / CLOCKS_PER_SEC;
    printf("   -> Concluido: Hardware avisou a conclusao por sinal/interrupcao.\n");
    printf("   -> Ciclos de CPU consumidos pelo processo durante a espera: ZERO (Uso: %.6f s).\n", tempo_interrupcao);
    printf("   -> Ganho de eficiencia: Enquanto a E/S ocorria, outros processos puderam usar a CPU.\n");
}

int main(void) {
    printf("=== MECANISMOS DE INTERRUPCAO: SINCRONAS, ASSINCRONAS E POLING ===\n\n");

    // -------------------------------------------------------------------------
    // TESTE 1: Interrupcao Sincrona (Trap de divisao por zero)
    // -------------------------------------------------------------------------
    printf("1. DEMONSTRACAO DE INTERRUPCAO SINCRONA (TRAP / FALTA INTERNA)\n");
    struct sigaction sa_trap;
    sa_trap.sa_handler = tratador_trap_aritmetica;
    sigemptyset(&sa_trap.sa_mask);
    sa_trap.sa_flags = 0;
    sigaction(SIGFPE, &sa_trap, NULL);

    if (sigsetjmp(ambiente_trap_aritmetica, 1) == 0) {
        printf("   Executando operacao matematica invalida (10 / 0)...\n");
        volatile int divisor = 0;
        volatile int resultado = 10 / divisor;
        (void)resultado;
    } else {
        printf("   Fluxo recuperado com sucesso apos a rotina de tratamento da Trap.\n\n");
    }

    // -------------------------------------------------------------------------
    // TESTE 2: Interrupcao Assincrona (Relogio de Intervalo / Timer de Quantum)
    // -------------------------------------------------------------------------
    printf("2. DEMONSTRACAO DE INTERRUPCAO ASSINCRONA (TIMER DE HARDWARE)\n");
    printf("   Configurando temporizador periodico com intervalo de 200ms...\n");

    struct sigaction sa_timer;
    sa_timer.sa_handler = tratador_temporizador_assincrono;
    sigemptyset(&sa_timer.sa_mask);
    sa_timer.sa_flags = 0;
    sigaction(SIGALRM, &sa_timer, NULL);

    // Configura o temporizador de intervalo para disparar a cada 200ms
    struct itimerval timer_config;
    timer_config.it_value.tv_sec = 0;
    timer_config.it_value.tv_usec = 200000; // Primeiro disparo em 200ms
    timer_config.it_interval.tv_sec = 0;
    timer_config.it_interval.tv_usec = 200000; // Repeticao periodica a cada 200ms

    setitimer(ITIMER_REAL, &timer_config, NULL);

    printf("   Processo simulando trabalho computacional em modo usuario:\n");
    while (temporizador_ativo) {
        printf("    [Processo em Execucao] Realizando calculos na CPU...\n");
        usleep(100000); // 100ms de computacao ficticia
    }

    // Desativa o temporizador
    timer_config.it_value.tv_sec = 0;
    timer_config.it_value.tv_usec = 0;
    setitimer(ITIMER_REAL, &timer_config, NULL);
    printf("   Temporizador desativado. Preempcao concluida.\n");

    // -------------------------------------------------------------------------
    // TESTE 3: Comparativo Polling vs Interrupcao
    // -------------------------------------------------------------------------
    demonstrar_sondagem_vs_interrupcao();

    printf("\n=== DEMONSTRACAO CONCLUIDA COM SUCESSO ===\n");
    return 0;
}
