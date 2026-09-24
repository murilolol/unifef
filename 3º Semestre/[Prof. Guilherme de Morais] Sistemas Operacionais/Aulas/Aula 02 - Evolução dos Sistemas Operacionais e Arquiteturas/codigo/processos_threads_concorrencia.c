/*
 * Disciplina: Sistemas Operacionais
 * Tema: Diferenciacao entre Processos e Threads, Condicao de Corrida (Race Condition) e Exclusao Mutua (Mutex)
 *
 * Como compilar:
 *   gcc -std=c11 -pthread processos_threads_concorrencia.c -o processos_threads_concorrencia
 *
 * Como executar:
 *   ./processos_threads_concorrencia
 */

#define _POSIX_C_SOURCE 200809L
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/wait.h>
#include <sys/types.h>

#define NUM_ITERACOES 500000
#define NUM_THREADS 4

// Variaveis globais compartilhadas no espaco de enderecamento do processo
long contador_inseguro = 0;
long contador_seguro = 0;
pthread_mutex_t trava_mutex = PTHREAD_MUTEX_INITIALIZER;

/*
 * PARTE 1: DEMONSTRACAO DO ISOLAMENTO DE ESPACO DE ENDERECAMENTO (PROCESSOS)
 * Ao executar fork(), o sistema operacional cria um novo processo filho com uma COPIA
 * do espaco de memoria virtual. Qualquer alteracao feita pelo filho nao afeta o pai.
 */
void demonstrar_isolamento_processos(void) {
    printf("==============================================================\n");
    printf("   PARTE 1: ISOLAMENTO DE MEMORIA ENTRE PROCESSOS (fork)      \n");
    printf("==============================================================\n");

    int variavel_local = 100;
    printf("[Processo Pai - PID %d] Valor inicial da variavel: %d\n", getpid(), variavel_local);
    printf("[Processo Pai] Chamando fork()...\n");

    pid_t pid = fork();

    if (pid < 0) {
        perror("Falha ao executar fork");
        exit(EXIT_FAILURE);
    }

    if (pid == 0) {
        // Codigo executado exclusivamente pelo Processo Filho
        printf("  -> [Processo Filho - PID %d, Pai PID %d] Espaco de memoria duplicado.\n",
               getpid(), getppid());
        variavel_local += 500;
        printf("  -> [Processo Filho] Variavel modificada para: %d\n", variavel_local);
        printf("  -> [Processo Filho] Encerrando com exit(0)...\n");
        exit(0); // O filho finaliza aqui
    } else {
        // Codigo executado exclusivamente pelo Processo Pai
        wait(NULL); // Bloqueia ate o filho terminar (evita processo zumbi)
        printf("[Processo Pai] Filho encerrou. Verificando variavel local no Pai: %d\n", variavel_local);
        printf("[Processo Pai] CONCLUSAO: A memoria do pai permaneceu inalterada (Isolamento de Processos).\n\n");
    }
}

/*
 * PARTE 2: THREADS SEM PROTECAO -> CONDICAO DE CORRIDA (RACE CONDITION)
 * Threads compartilham a mesma regiao de memoria (Heap e Dados Globais).
 * A instrucao de incremento (contador++) nao e atomica em nivel de montagem:
 *   1. MOV [memoria] -> registrador
 *   2. ADD registrador, 1
 *   3. MOV registrador -> [memoria]
 * Se ocorrer uma interrupcao de preempcao entre essas instrucoes, ocorre perda de atualizacao.
 */
void* rotina_insegura(void* arg) {
    (void)arg;
    for (int i = 0; i < NUM_ITERACOES; i++) {
        // REGIAO CRITICA DESPROTEGIDA: race condition inevitavel
        contador_inseguro++;
    }
    return NULL;
}

/*
 * PARTE 3: THREADS COM EXCLUSAO MUTUA (MUTEX)
 * O Mutex garante atomicidade e exclusao mutua: apenas UMA thread por vez
 * tem permissao de entrar e executar a regiao critica.
 */
void* rotina_segura(void* arg) {
    (void)arg;
    for (int i = 0; i < NUM_ITERACOES; i++) {
        // ENTRADA NA REGIAO CRITICA
        pthread_mutex_lock(&trava_mutex);
        
        contador_seguro++; // Executado de forma estritamente atomica
        
        // SAIDA DA REGIAO CRITICA
        pthread_mutex_unlock(&trava_mutex);
    }
    return NULL;
}

void demonstrar_threads_e_race_condition(void) {
    printf("==============================================================\n");
    printf("   PARTE 2: THREADS COMPARTILHAM MEMORIA E RACE CONDITION     \n");
    printf("==============================================================\n");

    pthread_t threads[NUM_THREADS];
    long valor_esperado = (long)NUM_THREADS * NUM_ITERACOES;

    // Teste 1: Execucao Insegura (Sem sincronizacao)
    printf("Iniciando %d threads concorrentes SEM exclusao mutua...\n", NUM_THREADS);
    printf("Cada thread realizara %d incrementos.\n", NUM_ITERACOES);
    printf("Valor final teoricamente esperado: %ld\n", valor_esperado);

    for (int i = 0; i < NUM_THREADS; i++) {
        if (pthread_create(&threads[i], NULL, rotina_insegura, NULL) != 0) {
            perror("Erro ao criar thread");
            exit(EXIT_FAILURE);
        }
    }

    // pthread_join aguarda a conclusao da thread (equivalente ao wait de processos)
    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    printf("--> Resultado Inseguro Obtido: %ld\n", contador_inseguro);
    if (contador_inseguro != valor_esperado) {
        printf("--> DETECTADO: CONDICAO DE CORRIDA! Houve perda de %ld operacoes por intercalacao.\n\n",
               valor_esperado - contador_inseguro);
    } else {
        printf("--> Coincidentemente nao houve colisao nesta execucao.\n\n");
    }

    // Teste 2: Execucao Segura (Com Mutex)
    printf("==============================================================\n");
    printf("   PARTE 3: PREVENCAO DE RACE CONDITIONS COM MUTEX (ATOMICIDADE)\n");
    printf("==============================================================\n");
    printf("Iniciando %d threads concorrentes COM pthread_mutex_lock...\n", NUM_THREADS);

    for (int i = 0; i < NUM_THREADS; i++) {
        if (pthread_create(&threads[i], NULL, rotina_segura, NULL) != 0) {
            perror("Erro ao criar thread");
            exit(EXIT_FAILURE);
        }
    }

    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    printf("--> Resultado Seguro Obtido  : %ld\n", contador_seguro);
    printf("--> Valor Esperado           : %ld\n", valor_esperado);
    if (contador_seguro == valor_esperado) {
        printf("--> SUCESSO: Exclusao mutua garantiu integridade absoluta da memoria compartilhada!\n");
    }

    pthread_mutex_destroy(&trava_mutex);
}

int main(void) {
    demonstrar_isolamento_processos();
    demonstrar_threads_e_race_condition();
    return 0;
}
