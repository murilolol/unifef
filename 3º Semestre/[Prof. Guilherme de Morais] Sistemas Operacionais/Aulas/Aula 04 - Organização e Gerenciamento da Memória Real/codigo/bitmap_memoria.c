// Disciplina: Sistemas Operacionais
// Tema: Gerenciamento de Memoria Real via Mapa de Bits (Bitmap)
// Como compilar: gcc -std=c11 bitmap_memoria.c -o bitmap
// Como executar: ./bitmap

#include <stdio.h>
#include <stdbool.h>

#define TOTAL_UNIDADES 32
unsigned int bitmap = 0;

void exibir_bitmap() {
    printf("Bitmap (0=Livre, 1=Ocupado): ");
    for (int i = 0; i < TOTAL_UNIDADES; i++) {
        int bit = (bitmap >> i) & 1;
        printf("%d", bit);
    }
    printf("\n");
}

bool alocar_unidades(int qtd, int id) {
    int consecutivas = 0;
    int inicio = -1;
    for (int i = 0; i < TOTAL_UNIDADES; i++) {
        if (((bitmap >> i) & 1) == 0) {
            if (consecutivas == 0) inicio = i;
            consecutivas++;
            if (consecutivas == qtd) {
                for (int j = inicio; j < inicio + qtd; j++) {
                    bitmap |= (1 << j);
                }
                printf("Processo %d alocado nas unidades %d a %d.\n", id, inicio, inicio + qtd - 1);
                return true;
            }
        } else {
            consecutivas = 0;
        }
    }
    printf("Falha ao alocar %d unidades para o processo %d.\n", qtd, id);
    return false;
}

int main() {
    printf("Estado inicial:\n");
    exibir_bitmap();
    alocar_unidades(5, 1);
    alocar_unidades(8, 2);
    exibir_bitmap();
    return 0;
}
