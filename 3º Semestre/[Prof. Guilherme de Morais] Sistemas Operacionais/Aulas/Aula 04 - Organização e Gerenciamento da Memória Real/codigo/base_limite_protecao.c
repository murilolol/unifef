// Disciplina: Sistemas Operacionais
// Tema: Mecanismo de Protecao e Realocacao com Registradores Base e Limite
// Como compilar: gcc -std=c11 base_limite_protecao.c -o protecao
// Como executar: ./protecao

#include <stdio.h>
#include <stdbool.h>

typedef struct {
    size_t base;
    size_t limite;
} Processo;

bool traduzir_endereco(Processo* p, size_t endereco_logico, size_t* endereco_fisico) {
    if (endereco_logico >= p->limite) {
        printf("[TRAP] Segment Fault! Endereco logico %zu viola o limite %zu.\n", endereco_logico, p->limite);
        return false;
    }
    *endereco_fisico = p->base + endereco_logico;
    return true;
}

int main() {
    Processo p1 = { .base = 5000, .limite = 1000 };
    size_t fisico = 0;
    printf("Testando enderecos para Processo (Base: 5000, Limite: 1000):\n");
    if (traduzir_endereco(&p1, 250, &fisico)) {
        printf("Endereco logico 250 mapeado com sucesso para o fisico %zu.\n", fisico);
    }
    if (traduzir_endereco(&p1, 1005, &fisico)) {
        printf("Endereco logico 1005 mapeado com sucesso para o fisico %zu.\n", fisico);
    } else {
        printf("Acesso negado pelo hardware de protecao.\n");
    }
    return 0;
}
