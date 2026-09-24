/*
 * ============================================================================
 * Disciplina : Sistemas Operacionais
 * Tema       : Aula 05 - Deadlock Simples, Condicoes de Coffman e Prevencao
 * Arquivo    : deadlock_simples_circular.c
 *
 * Como compilar:
 *   gcc -std=c11 -pthread deadlock_simples_circular.c -o deadlock_simples_circular
 *
 * Como executar no Modo Padrao (Demonstracao real de Deadlock com Watchdog):
 *   ./deadlock_simples_circular
 *
 * Como executar no Modo Prevencao (Quebra da Espera Circular via Havender):
 *   ./deadlock_simples_circular --prevenir
 * ============================================================================
 */

#define _DEFAULT_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <pthread.h>
#include <unistd.h>

/*
 * MODELAGEM DOS RECURSOS DO SISTEMA:
 * R1 e R2 sao recursos nao-preemptiveis de instancia unica (ex.: impressora e gravador de fita).
 * Cada recurso e protegido por uma trava de exclusao mutua (mutex).
 */
typedef struct {
    const char *nome;
    pthread_mutex_t trava;
} Recurso;

static Recurso g_recurso1 = {.nome = "Recurso R1 (Impressora)", .trava = PTHREAD_MUTEX_INITIALIZER};
static Recurso g_recurso2 = {.nome = "Recurso R2 (Fita Magnetica)", .trava = PTHREAD_MUTEX_INITIALIZER};

static bool g_modo_prevencao = false;
static volatile bool g_t1_concluiu = false;
static volatile bool g_t2_concluiu = false;

/*
 * THREAD 1 (Processo P1):
 * Ordem de solicitacao: Tenta alocar R1 primeiro; mantem R1 e tenta alocar R2.
 */
void* thread_processo1(void *arg) {
    (void)arg;
    printf("[P1] Solicitando %s...\n", g_recurso1.nome);
    pthread_mutex_lock(&g_recurso1.trava); // Condicao 1: Exclusao Mutua satisfeita
    printf("[P1] ALOCOU %s com sucesso.\n", g_recurso1.nome);

    // Pausa deliberada para permitir que P2 execute e aloque R2 (Posse e Espera)
    printf("[P1] Realizando operacao local e retendo %s...\n", g_recurso1.nome);
    usleep(150000); // 150ms

    printf("[P1] Solicitando %s (enquanto ainda retem %s)...\n", g_recurso2.nome, g_recurso1.nome);
    pthread_mutex_lock(&g_recurso2.trava);
    printf("[P1] Obteve ambos os recursos! Processando...\n");

    pthread_mutex_unlock(&g_recurso2.trava);
    pthread_mutex_unlock(&g_recurso1.trava);
    g_t1_concluiu = true;
    pthread_exit(NULL);
}

/*
 * THREAD 2 (Processo P2):
 * No modo com deadlock: Tenta alocar R2 primeiro; mantem R2 e tenta alocar R1.
 * No modo de prevencao (Havender): Aloca na mesma ordem hierarquica global (R1 depois R2).
 */
void* thread_processo2(void *arg) {
    (void)arg;

    if (g_modo_prevencao) {
        /*
         * TECNICA DE PREVENCAO DE HAVENDER (QUEBRA DA ESPERA CIRCULAR):
         * Define-se uma ordenacao total dos recursos: R1 < R2.
         * Nenhum processo pode solicitar R1 se ja estiver retendo um recurso de ordem superior.
         * Portanto, P2 tambem adquire R1 antes de R2.
         */
        printf("[P2 - MODO PREVENCAO] Solicitando primeiro %s (Ordem Hierarquica Global)...\n", g_recurso1.nome);
        pthread_mutex_lock(&g_recurso1.trava);
        printf("[P2 - MODO PREVENCAO] ALOCOU %s.\n", g_recurso1.nome);

        usleep(100000);

        printf("[P2 - MODO PREVENCAO] Solicitando %s...\n", g_recurso2.nome);
        pthread_mutex_lock(&g_recurso2.trava);
        printf("[P2 - MODO PREVENCAO] Obteve ambos os recursos! Finalizando...\n");

        pthread_mutex_unlock(&g_recurso2.trava);
        pthread_mutex_unlock(&g_recurso1.trava);
    } else {
        /*
         * MODO DEADLOCK (Inversao de Alocacao):
         * P2 retem R2 e tenta alocar R1 retido por P1.
         * Cria o ciclo direcionado: P1 -> R2 -> P2 -> R1 -> P1
         */
        printf("[P2] Solicitando %s...\n", g_recurso2.nome);
        pthread_mutex_lock(&g_recurso2.trava);
        printf("[P2] ALOCOU %s com sucesso.\n", g_recurso2.nome);

        printf("[P2] Realizando operacao local e retendo %s...\n", g_recurso2.nome);
        usleep(150000); // 150ms

        printf("[P2] Solicitando %s (enquanto ainda retem %s)...\n", g_recurso1.nome, g_recurso2.nome);
        pthread_mutex_lock(&g_recurso1.trava);
        printf("[P2] Obteve ambos os recursos! Processando...\n");

        pthread_mutex_unlock(&g_recurso1.trava);
        pthread_mutex_unlock(&g_recurso2.trava);
    }

    g_t2_concluiu = true;
    pthread_exit(NULL);
}

/*
 * THREAD VIGIA (WATCHDOG):
 * Diagnostica o estado do sistema. Se as threads nao concluirem em 2 segundos,
 * atesta a presenca do Deadlock e relata as quatro condicoes de Coffman satisfeitas.
 */
void* thread_vigia(void *arg) {
    (void)arg;
    sleep(2); // Aguarda tempo suficiente para execucao

    if (!g_t1_concluiu || !g_t2_concluiu) {
        printf("\n=================================================================\n");
        printf(" [ALERTA DE IMPASSE] DIAGNOSTICO DO SISTEMA OPERACIONAL\n");
        printf("=================================================================\n");
        printf(" Estado detectado: DEADLOCK CONFIRMADO no grafo de recursos.\n");
        printf(" Detalhamento das 4 Condicoes de Coffman simultaneas:\n");
        printf("  1. Exclusao Mutua   : R1 e R2 estao em posse exclusiva por travas binarias.\n");
        printf("  2. Posse e Espera   : P1 segura R1 esperando R2; P2 segura R2 esperando R1.\n");
        printf("  3. Nao-Preempcao    : O kernel nao confisca as travas de P1 nem de P2.\n");
        printf("  4. Espera Circular  : Cadeia fechada P1 -> R2 -> P2 -> R1 -> P1.\n");
        printf(" Consequencia        : Nenhuma thread consegue avancar. CPU em espera zero.\n");
        printf(" Dica para solucao  : Execute com './deadlock_simples_circular --prevenir'\n");
        printf("=================================================================\n");
        exit(0); // Encerramento limpo da demonstracao
    }
    pthread_exit(NULL);
}

int main(int argc, char *argv[]) {
    if (argc > 1 && strcmp(argv[1], "--prevenir") == 0) {
        g_modo_prevencao = true;
        printf("=================================================================\n");
        printf(" EXECUCAO EM MODO PREVENCAO DE DEADLOCK (ORDENACAO GLOBAL)\n");
        printf("=================================================================\n");
    } else {
        printf("=================================================================\n");
        printf(" EXECUCAO EM MODO DEMONSTRACAO DE DEADLOCK (INVERSAO DE RECURSOS)\n");
        printf("=================================================================\n");
    }

    pthread_t t1, t2, tvigia;

    pthread_create(&tvigia, NULL, thread_vigia, NULL);
    pthread_create(&t1, NULL, thread_processo1, NULL);
    pthread_create(&t2, NULL, thread_processo2, NULL);

    pthread_join(t1, NULL);
    pthread_join(t2, NULL);
    pthread_cancel(tvigia);
    pthread_join(tvigia, NULL);

    printf("\n[SUCESSO] Processos P1 e P2 finalizaram normalmente sem entrar em Deadlock!\n");
    return 0;
}
