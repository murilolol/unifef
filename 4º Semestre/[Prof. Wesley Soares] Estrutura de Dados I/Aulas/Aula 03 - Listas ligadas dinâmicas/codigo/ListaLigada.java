package unifef.ed1.aula05;

/**
 * Disciplina: Estrutura de Dados I
 * Tema: Listas ligadas dinâmicas
 * Professor: Prof. Wesley Soares
 *
 * Classe que gerencia a lista encadeada e suas operações.
 */
public class ListaLigada {
    private No inicio;
    private No fim;
    private int tamanho;

    public ListaLigada() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    // Inserção no fim otimizada para O(1)
    public void adicionar(int valor) {
        No novo = new No(valor);
        if (inicio == null) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
        tamanho++;
    }

    // Inserção no início O(1)
    public void adicionarInicio(int valor) {
        No novo = new No(valor);
        novo.proximo = inicio;
        inicio = novo;
        if (fim == null) {
            fim = novo;
        }
        tamanho++;
    }

    // Busca por índice O(n)
    public int obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice fora dos limites da lista.");
        }
        No atual = inicio;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.valor;
    }

    // Remoção no início O(1)
    public void removerInicio() {
        if (inicio == null) {
            return;
        }
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
    }

    // Remoção no meio ou fim O(n)
    public void removerMeio(int indice) {
        if (inicio == null || indice < 0 || indice >= tamanho) {
            return;
        }
        if (indice == 0) {
            removerInicio();
            return;
        }
        No atual = inicio;
        for (int i = 0; i < indice - 1; i++) {
            atual = atual.proximo;
        }
        No removido = atual.proximo;
        atual.proximo = removido.proximo;
        if (removido == fim) {
            fim = atual;
        }
        tamanho--;
    }

    public int getTamanho() {
        return this.tamanho;
    }

    public void imprimir() {
        No atual = inicio;
        System.out.print("[");
        while (atual != null) {
            System.out.print(atual.valor);
            if (atual.proximo != null) {
                System.out.print(" -> ");
            }
            atual = atual.proximo;
        }
        System.out.println("]");
    }
}
