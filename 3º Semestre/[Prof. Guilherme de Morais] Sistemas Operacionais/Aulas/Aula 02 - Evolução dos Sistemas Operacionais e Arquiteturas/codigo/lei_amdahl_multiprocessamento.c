/*
 * Disciplina: Sistemas Operacionais
 * Tema: Multiprocessamento Simetrico (SMP), Paralelismo Real e Validacao Pratica da Lei de Amdahl
 *
 * Como compilar:
 *   gcc -std=c11 -pthread lei_amdahl_multiprocessamento.c -o lei_amdahl_multiprocessamento -lm
 *
 * Como executar:
 *   ./lei_amdahl_multiprocessamento
 */

#define _POSIX_C_SOURCE 200809L
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include <math.h>

#define TAMANHO_VETOR 25000000

// Estrutura de dados para passar argumentos para cada thread trabalhadora
typedef struct {
    int id_thread;
    int inicio;
    int fim;
    double* vetor;
    double soma_parcial;
} ArgumentoThread;

// Retorna o tempo decorrido em segundos com precisao de nanossegundos
double obter_tempo_segundos(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (double)ts.tv_sec + (double)ts.tv_nsec / 1e9;
}

/*
 * ETAPA 1: FRACAO PURAMENTE SEQUENCIAL (1 - P)
 * Esta rotina representa a porcao de um programa que NAO PODE ser paralelizada
 * (ex: E/S inicial, alocacao estrita, preparacao de cabecalhos de dados).
 */
void fase_sequencial_obrigatoria(double* vetor, int tamanho) {
    // Inicializacao linear sequencial
    for (int i = 0; i < tamanho; i++) {
        vetor[i] = ((double)(i % 100)) * 0.05;
    }
}

/*
 * ETAPA 2: FRACAO PARALELIZAVEL (P)
 * Esta rotina representa o processamento matematico pesado que pode ser
 * fatiado e executado simultaneamente por multiplos nucleos de UCP.
 */
void* rotina_trabalhador_smp(void* arg) {
    ArgumentoThread* dados = (ArgumentoThread*)arg;
    double soma = 0.0;

    for (int i = dados->inicio; i < dados->fim; i++) {
        // Carga matematica com operacoes trigonometricas para estressar a UCP
        dados->vetor[i] = sin(dados->vetor[i]) * cos(dados->vetor[i]) + sqrt(dados->vetor[i] + 1.0);
        soma += dados->vetor[i];
    }

    dados->soma_parcial = soma;
    return NULL;
}

/*
 * Executa a carga de trabalho com N threads e mede o tempo de execucao total.
 */
double executar_experimento(double* vetor, int tamanho, int num_threads) {
    pthread_t threads[num_threads];
    ArgumentoThread args[num_threads];
    int fatia = tamanho / num_threads;

    double t_inicio = obter_tempo_segundos();

    // 1. Fase Sequencial (1 - P)
    fase_sequencial_obrigatoria(vetor, tamanho);

    // 2. Fase Paralela (P) - Disparo de N threads em Paralelismo Real (SMP)
    for (int i = 0; i < num_threads; i++) {
        args[i].id_thread = i;
        args[i].inicio = i * fatia;
        args[i].fim = (i == num_threads - 1) ? tamanho : (i + 1) * fatia;
        args[i].vetor = vetor;
        args[i].soma_parcial = 0.0;

        if (pthread_create(&threads[i], NULL, rotina_trabalhador_smp, &args[i]) != 0) {
            perror("Erro ao criar thread");
            exit(EXIT_FAILURE);
        }
    }

    // 3. Ponto de Sincronizacao (Barreira / Join)
    double soma_total = 0.0;
    for (int i = 0; i < num_threads; i++) {
        pthread_join(threads[i], NULL);
        soma_total += args[i].soma_parcial;
    }

    double t_fim = obter_tempo_segundos();
    
    // Evita otimizacao agressiva de remocao de codigo morto pelo compilador
    if (soma_total == 0.0) {
        printf("Aviso estatistico inesperado.\n");
    }

    return (t_fim - t_inicio);
}

/*
 * Formula analitica da Lei de Amdahl:
 *   S(N) = 1 / ((1 - P) + (P / N))
 */
double calcular_speedup_teorico_amdahl(double frac_paralela, int num_nucleos) {
    double frac_serial = 1.0 - frac_paralela;
    return 1.0 / (frac_serial + (frac_paralela / (double)num_nucleos));
}

int main(void) {
    printf("==============================================================\n");
    printf("      VALIDACAO PRATICA DA LEI DE AMDAHL E SMP MULTICORE      \n");
    printf("==============================================================\n");
    printf("Alocando vetor de %d elementos (%.1f MB na RAM)...\n",
           TAMANHO_VETOR, (double)(TAMANHO_VETOR * sizeof(double)) / (1024.0 * 1024.0));

    double* vetor = (double*)malloc(TAMANHO_VETOR * sizeof(double));
    if (vetor == NULL) {
        perror("Falha ao alocar memoria principal");
        return 1;
    }

    int configuracoes_threads[] = {1, 2, 4, 8};
    int total_testes = sizeof(configuracoes_threads) / sizeof(configuracoes_threads[0]);
    double tempos[total_testes];

    // Supondo empiricamente uma fracao paralelizavel P de 85%% (P = 0.85) nesta aplicacao
    const double P_ESTIMADO = 0.85;

    printf("\nExecutando testes com diferentes quantidades de threads...\n");
    printf("--------------------------------------------------------------\n");

    for (int i = 0; i < total_testes; i++) {
        int n_threads = configuracoes_threads[i];
        printf("Executando com %2d thread(s)... ", n_threads);
        fflush(stdout);

        tempos[i] = executar_experimento(vetor, TAMANHO_VETOR, n_threads);
        printf("Concluido em %6.3f segundos.\n", tempos[i]);
    }

    double tempo_base_1_thread = tempos[0];

    printf("\n========================================================================\n");
    printf("        TABELA COMPARATIVA: SPEEDUP REAL vs. LEI DE AMDAHL              \n");
    printf("========================================================================\n");
    printf("| Threads (N) | Tempo (s) | Speedup Real (T1/TN) | Amdahl Teorico (P=%.2f) |\n", P_ESTIMADO);
    printf("|-------------|-----------|----------------------|-----------------------|\n");

    for (int i = 0; i < total_testes; i++) {
        int n_threads = configuracoes_threads[i];
        double speedup_real = tempo_base_1_thread / tempos[i];
        double speedup_teorico = calcular_speedup_teorico_amdahl(P_ESTIMADO, n_threads);

        printf("| %11d | %9.3f | %19.2fx | %20.2fx |\n",
               n_threads, tempos[i], speedup_real, speedup_teorico);
    }
    printf("========================================================================\n");

    double limite_infinito_amdahl = 1.0 / (1.0 - P_ESTIMADO);
    printf("\n--- LIMITES DE ESCALABILIDADE (LEI DE AMDAHL) ---\n");
    printf("Fracao Serial Inflexivel (1 - P) : %.2f (%.0f%% do tempo total)\n",
           1.0 - P_ESTIMADO, (1.0 - P_ESTIMADO) * 100.0);
    printf("Fracao Paralelizavel (P)         : %.2f (%.0f%% do tempo total)\n",
           P_ESTIMADO, P_ESTIMADO * 100.0);
    printf("Teto Maximo de Speedup (N -> inf): %.2fx (Inultrapassavel)\n", limite_infinito_amdahl);
    printf("Mesmo com infinitos nucleos, o ganho NUNCA ultrapassara %.2fx!\n", limite_infinito_amdahl);

    free(vetor);
    return 0;
}
