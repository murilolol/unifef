/*
 * Disciplina: Estrutura de Dados I
 * Professor: Prof. Wesley Soares
 * Tema: Pilhas - Aplicações, Manipulação e Análise de Complexidade
 *
 * Como compilar e executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
 */

public class ExemplosAula {

    // -------------------------------------------------------------------------
    // 1. Definição do Nó para Lista Ligada
    // -------------------------------------------------------------------------
    static class No {
        int valor;
        No proximo;

        public No(int valor) {
            this.valor = valor;
            this.proximo = null;
        }
    }

    // -------------------------------------------------------------------------
    // 2. Implementação da Pilha Encadeada Dinâmica
    // -------------------------------------------------------------------------
    static class Pilha {
        private No topo;
        private int tamanho;

        public Pilha() {
            this.topo = null;
            this.tamanho = 0;
        }

        // Inserção no topo - O(1)
        public void push(int valor) {
            No novo = new No(valor);
            novo.proximo = topo;
            topo = novo;
            tamanho++;
        }

        // Remoção do topo - O(1)
        public int pop() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            int valor = topo.valor;
            topo = topo.proximo;
            tamanho--;
            return valor;
        }

        // Consulta do topo - O(1)
        public int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            return topo.valor;
        }

        // Verificação de vacuidade - O(1)
        public boolean isEmpty() {
            return topo == null;
        }

        // Retorno da quantidade de elementos - O(1)
        public int size() {
            return tamanho;
        }

        // Busca de elemento intermediário via pilha auxiliar sem quebrar o LIFO - O(n)
        public boolean contem(int valor) {
            Pilha auxiliar = new Pilha();
            boolean encontrou = false;

            while (!isEmpty()) {
                int atual = pop();
                if (atual == valor) {
                    encontrou = true;
                    auxiliar.push(atual);
                    break;
                }
                auxiliar.push(atual);
            }

            // Restauração da pilha original
            while (!auxiliar.isEmpty()) {
                push(auxiliar.pop());
            }

            return encontrou;
        }

        // Substituição de valor intermediário utilizando pilha auxiliar - O(n)
        public boolean substituir(int valorAntigo, int valorNovo) {
            Pilha auxiliar = new Pilha();
            boolean encontrou = false;

            while (!isEmpty()) {
                int atual = pop();
                if (atual == valorAntigo && !encontrou) {
                    auxiliar.push(valorNovo);
                    encontrou = true;
                } else {
                    auxiliar.push(atual);
                }
            }

            // Restauração da pilha original na ordem correta
            while (!auxiliar.isEmpty()) {
                push(auxiliar.pop());
            }

            return encontrou;
        }

        // Remoção da primeira ocorrência a partir do topo via pilha auxiliar - O(n)
        public boolean removerValor(int valor) {
            Pilha auxiliar = new Pilha();
            boolean removeu = false;

            while (!isEmpty()) {
                int atual = pop();
                if (!removeu && atual == valor) {
                    removeu = true;
                    break; // Não transfere para a auxiliar, efetivando a remoção
                }
                auxiliar.push(atual);
            }

            // Restaura os elementos desempilhados anteriormente
            while (!auxiliar.isEmpty()) {
                push(auxiliar.pop());
            }

            return removeu;
        }

        // Exibição amigável do topo para a base (para conferência sem quebrar encapsulamento)
        public void exibir() {
            Pilha temp = new Pilha();
            System.out.print("[TOPO] -> ");
            while (!isEmpty()) {
                int val = pop();
                System.out.print(val + " ");
                temp.push(val);
            }
            System.out.println("-> [BASE]");
            while (!temp.isEmpty()) {
                push(temp.pop());
            }
        }
    }

    // -------------------------------------------------------------------------
    // 3. Implementação da Pilha com Array Estático (Comparativo)
    // -------------------------------------------------------------------------
    static class PilhaSequencial {
        private int[] elementos;
        private int topo;
        private int capacidade;

        public PilhaSequencial(int capacidade) {
            this.capacidade = capacidade;
            this.elementos = new int[capacidade];
            this.topo = -1;
        }

        public void push(int valor) {
            if (topo == capacidade - 1) {
                throw new IllegalStateException("Pilha cheia (Stack Overflow)");
            }
            topo++;
            elementos[topo] = valor;
        }

        public int pop() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia (Stack Underflow)");
            }
            int val = elementos[topo];
            topo--;
            return val;
        }

        public int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            return elementos[topo];
        }

        public boolean isEmpty() {
            return topo == -1;
        }

        public int size() {
            return topo + 1;
        }
    }

    // -------------------------------------------------------------------------
    // Método Principal: Demonstração dos Códigos da Aula
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("  ESTRUTURA DE DADOS I - AULA 09: TAD PILHA");
        System.out.println("====================================================\n");

        Pilha pilha = new Pilha();

        System.out.println("1. Inserindo elementos na Pilha Encadeada (push)...");
        pilha.push(10); // Base
        pilha.push(20);
        pilha.push(30);
        pilha.push(40); // Topo
        pilha.exibir();
        System.out.println("Tamanho da pilha: " + pilha.size());
        System.out.println("Elemento no topo (peek): " + pilha.peek());

        System.out.println("\n2. Teste do método contem(int valor):");
        System.out.println("A pilha contém 20? " + pilha.contem(20));
        System.out.println("A pilha contém 99? " + pilha.contem(99));
        System.out.print("Estado após buscas (deve permanecer inalterado): ");
        pilha.exibir();

        System.out.println("\n3. Teste do método substituir(20, 99):");
        boolean substituiu = pilha.substituir(20, 99);
        System.out.println("Substituição realizada com sucesso? " + substituiu);
        pilha.exibir();

        System.out.println("\n4. Teste do método removerValor(30):");
        boolean removeu = pilha.removerValor(30);
        System.out.println("Elemento 30 removido com sucesso? " + removeu);
        pilha.exibir();

        System.out.println("\n5. Desempilhando elementos (pop):");
        while (!pilha.isEmpty()) {
            System.out.println("Desempilhado: " + pilha.pop() + " | Restam: " + pilha.size());
        }
        System.out.println("Pilha vazia? " + pilha.isEmpty());

        System.out.println("\n6. Teste da Pilha Sequencial (Array Estático):");
        PilhaSequencial pilhaSeq = new PilhaSequencial(3);
        pilhaSeq.push(100);
        pilhaSeq.push(200);
        pilhaSeq.push(300);
        System.out.println("Topo pilha sequencial: " + pilhaSeq.peek() + " (Tamanho: " + pilhaSeq.size() + ")");
        System.out.println("Removido: " + pilhaSeq.pop());
        System.out.println("Novo topo: " + pilhaSeq.peek());
    }
}
