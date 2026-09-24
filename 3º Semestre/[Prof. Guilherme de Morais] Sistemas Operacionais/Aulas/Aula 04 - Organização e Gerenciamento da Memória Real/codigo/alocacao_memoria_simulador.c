// Disciplina: Sistemas Operacionais
// Tema: Simulador de Alocacao Contigua (First-Fit, Best-Fit e Worst-Fit)
// Como compilar: gcc -std=c11 alocacao_memoria_simulador.c -o alocacao
// Como executar: ./alocacao

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

typedef struct Bloco {
    int id_processo;
    size_t base;
    size_t tamanho;
    struct Bloco* prev;
    struct Bloco* next;
} Bloco;

Bloco* criar_memoria(size_t tamanho_total) {
    Bloco* inicio = (Bloco*)malloc(sizeof(Bloco));
    inicio->id_processo = -1;
    inicio->base = 0;
    inicio->tamanho = tamanho_total;
    inicio->prev = NULL;
    inicio->next = NULL;
    return inicio;
}

bool alocar_first_fit(Bloco* inicio, int id, size_t tam) {
    Bloco* atual = inicio;
    while (atual != NULL) {
        if (atual->id_processo == -1 && atual->tamanho >= tam) {
            if (atual->tamanho > tam) {
                Bloco* sobra = (Bloco*)malloc(sizeof(Bloco));
                sobra->id_processo = -1;
                sobra->base = atual->base + tam;
                sobra->tamanho = atual->tamanho - tam;
                sobra->next = atual->next;
                sobra->prev = atual;
                if (atual->next != NULL) atual->next->prev = sobra;
                atual->next = sobra;
            }
            atual->id_processo = id;
            atual->tamanho = tam;
            return true;
        }
        atual = atual->next;
    }
    return false;
}

void exibir(Bloco* inicio) {
    Bloco* atual = inicio;
    printf("\n--- MAPA DE MEMORIA ---\n");
    while (atual != NULL) {
        if (atual->id_processo == -1) {
            printf("[LIVRE] Base: %zu | Tam: %zu\n", atual->base, atual->tamanho);
        } else {
            printf("[PROC %d] Base: %zu | Tam: %zu\n", atual->id_processo, atual->base, atual->tamanho);
        }
        atual = atual->next;
    }
    printf("------------------------\n");
}

int main() {
    Bloco* memoria = criar_memoria(1024);
    printf("Memoria inicializada com 1024 KB.\n");
    alocar_first_fit(memoria, 1, 200);
    alocar_first_fit(memoria, 2, 300);
    alocar_first_fit(memoria, 3, 150);
    exibir(memoria);
    return 0;
}
