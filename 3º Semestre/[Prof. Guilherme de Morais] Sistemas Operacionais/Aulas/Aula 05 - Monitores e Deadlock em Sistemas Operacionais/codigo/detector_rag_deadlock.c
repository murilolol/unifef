/*
 * ============================================================================
 * Disciplina : Sistemas Operacionais
 * Tema       : Aula 05 - Grafo de Alocacao de Recursos (RAG) e Deteccao de Deadlock
 * Arquivo    : detector_rag_deadlock.c
 *
 * Como compilar:
 *   gcc -std=c11 -pthread detector_rag_deadlock.c -o detector_rag_deadlock
 *
 * Como executar:
 *   ./detector_rag_deadlock
 * ============================================================================
 */

#define _DEFAULT_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#define MAX_VERTICES 20

typedef enum {
    TIPO_PROCESSO,
    TIPO_RECURSO
} TipoVertice;

typedef enum {
    COR_BRANCO,  // Vertice ainda nao visitado
    COR_CINZA,   // Vertice em processamento (presente na pilha de recursao)
    COR_PRETO    // Vertice completamente explorado
} CorVertice;

typedef struct {
    char nome[16];
    TipoVertice tipo;
} Vertice;

typedef struct {
    int num_vertices;
    Vertice vertices[MAX_VERTICES];
    // Matriz de adjacencia: adj[u][v] == 1 indica aresta direcionada u -> v
    int adj[MAX_VERTICES][MAX_VERTICES];
} GrafoRAG;

void rag_inicializar(GrafoRAG *g) {
    g->num_vertices = 0;
    memset(g->adj, 0, sizeof(g->adj));
}

int rag_adicionar_vertice(GrafoRAG *g, const char *nome, TipoVertice tipo) {
    for (int i = 0; i < g->num_vertices; i++) {
        if (strcmp(g->vertices[i].nome, nome) == 0) {
            return i;
        }
    }
    int id = g->num_vertices++;
    strncpy(g->vertices[id].nome, nome, sizeof(g->vertices[id].nome) - 1);
    g->vertices[id].nome[sizeof(g->vertices[id].nome) - 1] = '\0';
    g->vertices[id].tipo = tipo;
    return id;
}

/*
 * CONCEITO DA AULA: ARESTAS NO GRAFO RAG
 * 1. Aresta de Solicitacao (P -> R): Processo requisita e aguarda um recurso.
 * 2. Aresta de Alocacao    (R -> P): Recurso esta concedido e em posse do processo.
 */
void rag_adicionar_solicitacao(GrafoRAG *g, const char *processo, const char *recurso) {
    int u = rag_adicionar_vertice(g, processo, TIPO_PROCESSO);
    int v = rag_adicionar_vertice(g, recurso, TIPO_RECURSO);
    g->adj[u][v] = 1; // P -> R
}

void rag_adicionar_alocacao(GrafoRAG *g, const char *recurso, const char *processo) {
    int u = rag_adicionar_vertice(g, recurso, TIPO_RECURSO);
    int v = rag_adicionar_vertice(g, processo, TIPO_PROCESSO);
    g->adj[u][v] = 1; // R -> P
}

/*
 * ALGORITMO DE DETECCAO DE CICLOS EM GRAFOS DIRECIONADOS VIA DFS:
 * Em recursos de instancia unica, um ciclo direcionado e condicao
 * NECESSARIA e SUFICIENTE para a existencia de Deadlock.
 */
bool dfs_encontrar_ciclo(GrafoRAG *g, int u, CorVertice cores[], int predecessores[], int *inicio_ciclo, int *fim_ciclo) {
    cores[u] = COR_CINZA; // Entra na pilha de exploracao ativa

    for (int v = 0; v < g->num_vertices; v++) {
        if (g->adj[u][v]) {
            // Se encontrar vizinho CINZA, fechamos um ciclo direcionado (back-edge)
            if (cores[v] == COR_CINZA) {
                *inicio_ciclo = v;
                *fim_ciclo = u;
                return true;
            }
            if (cores[v] == COR_BRANCO) {
                predecessores[v] = u;
                if (dfs_encontrar_ciclo(g, v, cores, predecessores, inicio_ciclo, fim_ciclo)) {
                    return true;
                }
            }
        }
    }

    cores[u] = COR_PRETO; // Sai da pilha ativa
    return false;
}

void rag_analisar_deadlock(GrafoRAG *g, const char *identificador_cenario) {
    printf("\n=================================================================\n");
    printf(" ANALISE DE RAG: %s\n", identificador_cenario);
    printf("=================================================================\n");
    printf("Vertices mapeados no Grafo:\n");
    for (int i = 0; i < g->num_vertices; i++) {
        printf("  - [%s] %s\n", 
               g->vertices[i].tipo == TIPO_PROCESSO ? "PROCESSO" : "RECURSO ",
               g->vertices[i].nome);
    }

    printf("\nArestas de Relacionamento (Solicitacoes e Alocacoes):\n");
    for (int i = 0; i < g->num_vertices; i++) {
        for (int j = 0; j < g->num_vertices; j++) {
            if (g->adj[i][j]) {
                printf("  * %s %s -> %s %s\n",
                       g->vertices[i].tipo == TIPO_PROCESSO ? "(Req)" : "(Posse)",
                       g->vertices[i].nome,
                       g->vertices[j].nome,
                       g->vertices[j].tipo == TIPO_PROCESSO ? "[Alocado]" : "[Solicitado]");
            }
        }
    }

    CorVertice cores[MAX_VERTICES];
    int predecessores[MAX_VERTICES];
    for (int i = 0; i < g->num_vertices; i++) {
        cores[i] = COR_BRANCO;
        predecessores[i] = -1;
    }

    int inicio_ciclo = -1;
    int fim_ciclo = -1;
    bool tem_deadlock = false;

    for (int i = 0; i < g->num_vertices; i++) {
        if (cores[i] == COR_BRANCO) {
            if (dfs_encontrar_ciclo(g, i, cores, predecessores, &inicio_ciclo, &fim_ciclo)) {
                tem_deadlock = true;
                break;
            }
        }
    }

    if (tem_deadlock) {
        printf("\n[RESULTADO] DETECTADO CICLO DE ESPERA CIRCULAR (DEADLOCK)!\n");
        printf("Cadeia Fechada Encontrada: %s", g->vertices[inicio_ciclo].nome);
        
        int pilha_caminho[MAX_VERTICES];
        int tam = 0;
        int atual = fim_ciclo;
        
        while (atual != inicio_ciclo && atual != -1) {
            pilha_caminho[tam++] = atual;
            atual = predecessores[atual];
        }
        
        for (int k = tam - 1; k >= 0; k--) {
            printf(" -> %s", g->vertices[pilha_caminho[k]].nome);
        }
        printf(" -> %s\n", g->vertices[inicio_ciclo].nome);
        printf("Status: Todos os processos envolvidos no ciclo estao travados permanentemente.\n");
    } else {
        printf("\n[RESULTADO] ESTADO SEGURO: Nenhum ciclo direcionado encontrado.\n");
        printf("Status: Nao ha condicao de espera circular; o sistema pode progredir livremente.\n");
    }
}

int main(void) {
    printf("=================================================================\n");
    printf(" SISTEMAS OPERACIONAIS - DETECTOR DE DEADLOCK EM GRAFOS (RAG)\n");
    printf(" Analise Estrutural de Recursos de Instancia Unica\n");
    printf("=================================================================\n");

    /*
     * CENARIO 1: Exercício 3 da Aula 05 do Prof. Guilherme de Morais
     * Dados:
     *   - R1 alocado para P2
     *   - P1 solicitando R1
     *   - R2 alocado para P1
     *   - P2 solicitando R2
     *   - R3 alocado para P3
     *   - P3 solicitando R1
     */
    GrafoRAG cenario_aula;
    rag_inicializar(&cenario_aula);
    rag_adicionar_alocacao(&cenario_aula, "R1", "P2");
    rag_adicionar_solicitacao(&cenario_aula, "P1", "R1");
    rag_adicionar_alocacao(&cenario_aula, "R2", "P1");
    rag_adicionar_solicitacao(&cenario_aula, "P2", "R2");
    rag_adicionar_alocacao(&cenario_aula, "R3", "P3");
    rag_adicionar_solicitacao(&cenario_aula, "P3", "R1");
    rag_analisar_deadlock(&cenario_aula, "Exercicio 3 da Aula 05 (Cenario com Deadlock)");

    /*
     * CENARIO 2: Estado Seguro (Livre de Deadlock)
     * P1 aloca R1; P2 aloca R2; P1 solicita R3 (que esta livre).
     */
    GrafoRAG cenario_seguro;
    rag_inicializar(&cenario_seguro);
    rag_adicionar_alocacao(&cenario_seguro, "R1", "P1");
    rag_adicionar_alocacao(&cenario_seguro, "R2", "P2");
    rag_adicionar_solicitacao(&cenario_seguro, "P1", "R3");
    rag_analisar_deadlock(&cenario_seguro, "Cenario Linear Aciclico (Estado Seguro)");

    return 0;
}
