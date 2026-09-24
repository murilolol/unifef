/*
 * ============================================================================
 * Disciplina : Sistemas Operacionais
 * Tema       : Aula 05 - Monitores, Exclusao Mutua e Espera Controlada em C
 * Arquivo    : monitor_buffer_limitado.c
 *
 * Como compilar:
 *   gcc -std=c11 -pthread monitor_buffer_limitado.c -o monitor_buffer_limitado
 *
 * Como executar:
 *   ./monitor_buffer_limitado
 * ============================================================================
 */

#define _DEFAULT_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <pthread.h>
#include <unistd.h>

#define CAPACIDADE_BUFFER 5
#define TOTAL_PRODUCOES 12

/*
 * CONCEITO DA AULA: COMPONENTES ESSENCIAIS DE UM MONITOR
 * 1. Dados Privados: o array do buffer, ponteiros e contadores sao mantidos
 *    estritamente dentro da struct e manipulados apenas pelas funcoes do monitor.
 * 2. Trava Interna (Lock): garante que apenas uma thread execute um procedimento
 *    de acesso por vez (Exclusao Mutua estrita N <= 1).
 * 3. Fila de Espera Controlada (Condition Variables): threads bloqueadas sao
 *    suspensas pelo SO sem gastar ciclos de CPU (diferente de busy-waiting/spinlock).
 */
typedef struct {
    int itens[CAPACIDADE_BUFFER]; // Dado privado: armazenamento interno
    int inicio;                   // Dado privado: indice de remocao
    int fim;                      // Dado privado: indice de insercao
    int contador;                 // Dado privado: quantidade atual de elementos
    
    pthread_mutex_t trava;        // Mecanismo de entrada e exclusao mutua do monitor
    pthread_cond_t nao_cheio;     // Fila de espera controlada para produtores
    pthread_cond_t nao_vazio;     // Fila de espera controlada para consumidores
} MonitorBuffer;

// Inicializacao formal do monitor e suas primitivas
void monitor_inicializar(MonitorBuffer *m) {
    m->inicio = 0;
    m->fim = 0;
    m->contador = 0;
    pthread_mutex_init(&m->trava, NULL);
    pthread_cond_init(&m->nao_cheio, NULL);
    pthread_cond_init(&m->nao_vazio, NULL);
}

// Destruicao de recursos de sincronizacao
void monitor_destruir(MonitorBuffer *m) {
    pthread_mutex_destroy(&m->trava);
    pthread_cond_destroy(&m->nao_cheio);
    pthread_cond_destroy(&m->nao_vazio);
}

/*
 * PROCEDIMENTO DE ACESSO PUBLICO 1: Insercao no Monitor
 * Implementa o prologo (rotina de entrada) e o epilogo (rotina de saida).
 */
void monitor_inserir(MonitorBuffer *m, int valor, int thread_id) {
    // --- ROTINA DE ENTRADA: Adquire trava atomica do monitor ---
    pthread_mutex_lock(&m->trava);

    /*
     * REGRA DE OURO DA AULA:
     * Sempre usar 'while' em vez de 'if' para avaliar condicoes logicas.
     * Isso protege contra despertares espurios (spurious wakeups) e garante que,
     * ao acordar, a thread valide novamente se ha espaco livre no buffer.
     */
    while (m->contador == CAPACIDADE_BUFFER) {
        printf("[Produtor %d] Buffer CHEIO (%d/%d). Entrando em ESPERA CONTROLADA...\n",
               thread_id, m->contador, CAPACIDADE_BUFFER);
        
        // Libera a trava do monitor e coloca a thread no estado bloqueado no SO
        pthread_cond_wait(&m->nao_cheio, &m->trava);
        
        printf("[Produtor %d] Acordado da fila. Reavaliando condicao de insercao...\n", thread_id);
    }

    // --- SECAO CRITICA: Modificacao segura dos dados privados ---
    m->itens[m->fim] = valor;
    m->fim = (m->fim + 1) % CAPACIDADE_BUFFER;
    m->contador++;

    printf("[Produtor %d] Inseriu item %02d | Ocupacao atual do buffer: %d/%d\n",
           thread_id, valor, m->contador, CAPACIDADE_BUFFER);

    // Sinaliza eventuais consumidores bloqueados de que o buffer nao esta mais vazio
    pthread_cond_signal(&m->nao_vazio);

    // --- ROTINA DE SAIDA: Libera a trava do monitor para a proxima thread ---
    pthread_mutex_unlock(&m->trava);
}

/*
 * PROCEDIMENTO DE ACESSO PUBLICO 2: Remocao do Monitor
 */
int monitor_remover(MonitorBuffer *m, int thread_id) {
    int valor_removido;

    // --- ROTINA DE ENTRADA ---
    pthread_mutex_lock(&m->trava);

    // Espera controlada caso o buffer esteja vazio
    while (m->contador == 0) {
        printf("[Consumidor %d] Buffer VAZIO. Entrando em ESPERA CONTROLADA...\n", thread_id);
        pthread_cond_wait(&m->nao_vazio, &m->trava);
        printf("[Consumidor %d] Acordado da fila. Reavaliando condicao de remocao...\n", thread_id);
    }

    // --- SECAO CRITICA ---
    valor_removido = m->itens[m->inicio];
    m->inicio = (m->inicio + 1) % CAPACIDADE_BUFFER;
    m->contador--;

    printf("[Consumidor %d] Consumiu item %02d | Ocupacao restante: %d/%d\n",
           thread_id, valor_removido, m->contador, CAPACIDADE_BUFFER);

    // Notifica produtores que aguardavam liberacao de espaco
    pthread_cond_signal(&m->nao_cheio);

    // --- ROTINA DE SAIDA ---
    pthread_mutex_unlock(&m->trava);

    return valor_removido;
}

// Instancia global do monitor compartilhado entre threads
static MonitorBuffer g_monitor;

typedef struct {
    int id;
    int quantidade_tarefas;
} ContextoThread;

void* thread_produtora(void *arg) {
    ContextoThread *ctx = (ContextoThread*) arg;
    for (int i = 1; i <= ctx->quantidade_tarefas; i++) {
        int dado = (ctx->id * 100) + i;
        monitor_inserir(&g_monitor, dado, ctx->id);
        usleep(100000); // 100ms simulando processamento
    }
    pthread_exit(NULL);
}

void* thread_consumidora(void *arg) {
    ContextoThread *ctx = (ContextoThread*) arg;
    for (int i = 1; i <= ctx->quantidade_tarefas; i++) {
        int dado = monitor_remover(&g_monitor, ctx->id);
        (void)dado;
        usleep(180000); // 180ms simulando consumo
    }
    pthread_exit(NULL);
}

int main(void) {
    printf("=================================================================\n");
    printf(" SISTEMAS OPERACIONAIS - SIMULACAO DE MONITOR DE BUFFER LIMITADO\n");
    printf(" Demonstracao de Encapsulamento, Exclusao Mutua e Espera Controlada\n");
    printf("=================================================================\n\n");

    monitor_inicializar(&g_monitor);

    pthread_t prod[2];
    pthread_t cons[2];
    ContextoThread ctx_p[2] = {{.id = 1, .quantidade_tarefas = 6}, {.id = 2, .quantidade_tarefas = 6}};
    ContextoThread ctx_c[2] = {{.id = 1, .quantidade_tarefas = 6}, {.id = 2, .quantidade_tarefas = 6}};

    // Criacao dos fluxos concorrentes de execucao (threads)
    for (int i = 0; i < 2; i++) {
        pthread_create(&prod[i], NULL, thread_produtora, &ctx_p[i]);
        pthread_create(&cons[i], NULL, thread_consumidora, &ctx_c[i]);
    }

    // Aguarda sincronizacao e conclusao de todas as threads
    for (int i = 0; i < 2; i++) {
        pthread_join(prod[i], NULL);
        pthread_join(cons[i], NULL);
    }

    printf("\n[SUCESSO] Todas as %d insercoes e remocoes ocorreram sem violacao de concorrencia.\n", TOTAL_PRODUCOES);
    printf("[ESTADO FINAL] Ocupacao final do buffer: %d elementos.\n", g_monitor.contador);

    monitor_destruir(&g_monitor);
    return 0;
}
