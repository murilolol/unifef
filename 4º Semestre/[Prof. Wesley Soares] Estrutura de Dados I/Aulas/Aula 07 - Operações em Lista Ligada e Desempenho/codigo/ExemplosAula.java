/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Operações em Lista Ligada e Desempenho
 * 
 * Como compilar e executar:
 * javac ExemplosAula.java
 * java ExemplosAula
 */
public class ExemplosAula {

    // Estrutura do Nó da Lista Ligada
    static class No {
        int valor;
        No proximo;

        public No(int valor) {
            this.valor = valor;
            this.proximo = null;
        }
    }

    // Estrutura da Lista Ligada apresentada na Aula 06
    static class ListaLigada {
        private No inicio;
        private No fim;
        private int tamanho;

        public ListaLigada() {
            this.inicio = null;
            this.fim = null;
            this.tamanho = 0;
        }

        // Inserção no início (operação O(1) mencionada como base da aula)
        public void adicionarInicio(int valor) {
            No novo = new No(valor);
            if (inicio == null) {
                inicio = novo;
                fim = novo;
            } else {
                novo.proximo = inicio;
                inicio = novo;
            }
            tamanho++;
        }

        // Inserção no fim com complexidade O(1)
        // Reproduz o código do professor com salvaguarda para lista inicialmente vazia
        public void adicionarFim(int valor) {
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

        // Busca linear por valor - Complexidade O(n)
        // Código idêntico ao apresentado pelo professor no Notion
        public No buscar(int valor) {
            No atual = inicio;

            while (atual != null) {
                if (atual.valor == valor) {
                    return atual;
                }
                atual = atual.proximo;
            }

            return null;
        }

        // Atualização de um elemento por índice - Complexidade O(n)
        // Código idêntico ao apresentado pelo professor no Notion
        public void atualizar(int indice, int novoValor) {
            if (indice < 0) {
                throw new IndexOutOfBoundsException("Índice inválido");
            }

            No atual = inicio;

            for (int i = 0; i < indice; i++) {
                if (atual == null) {
                    throw new IndexOutOfBoundsException("Índice fora da lista");
                }
                atual = atual.proximo;
            }

            if (atual == null) {
                throw new IndexOutOfBoundsException("Índice fora da lista");
            }

            atual.valor = novoValor;
        }

        // Método utilitário para visualização da lista encadeada
        public void exibir() {
            No atual = inicio;
            System.out.print("[ ");
            while (atual != null) {
                System.out.print(atual.valor);
                if (atual.proximo != null) {
                    System.out.print(" -> ");
                }
                atual = atual.proximo;
            }
            System.out.println(" ] (Tamanho: " + tamanho + ")");
        }

        public int getTamanho() {
            return tamanho;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Demonstração dos Exemplos da Aula 06 - Lista Ligada");
        System.out.println("==================================================");

        ListaLigada lista = new ListaLigada();

        System.out.println("\n1. Inserção no início e no fim O(1):");
        lista.adicionarInicio(20);
        lista.adicionarInicio(10);
        lista.adicionarFim(30);
        lista.adicionarFim(40);
        lista.exibir();

        System.out.println("\n2. Busca linear por valor O(n):");
        int valorBusca = 30;
        No encontrado = lista.buscar(valorBusca);
        if (encontrado != null) {
            System.out.println("Elemento " + valorBusca + " encontrado no nó de memória com valor: " + encontrado.valor);
        } else {
            System.out.println("Elemento " + valorBusca + " não encontrado.");
        }

        int valorInexistente = 99;
        No naoEncontrado = lista.buscar(valorInexistente);
        System.out.println("Busca por " + valorInexistente + ": " + (naoEncontrado == null ? "Retornou null (não encontrado)" : "Encontrado"));

        System.out.println("\n3. Atualização de elemento por índice O(n):");
        System.out.println("Lista antes da atualização:");
        lista.exibir();
        System.out.println("Alterando elemento no índice 2 (era 30) para 33...");
        lista.atualizar(2, 33);
        System.out.println("Lista após a atualização:");
        lista.exibir();

        System.out.println("\n4. Teste de tratamento de exceção ao atualizar índice inválido:");
        try {
            lista.atualizar(10, 50);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Exceção capturada com sucesso: " + e.getMessage());
        }
    }
}
