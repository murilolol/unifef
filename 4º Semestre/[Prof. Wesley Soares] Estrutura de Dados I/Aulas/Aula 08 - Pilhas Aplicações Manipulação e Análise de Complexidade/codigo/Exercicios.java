/*
 * Disciplina: Estrutura de Dados I
 * Professor: Prof. Wesley Soares
 * Tema: Resolução dos Exercícios Práticos - TAD Pilha
 *
 * Como compilar e executar:
 *   javac Exercicios.java
 *   java Exercicios
 */

public class Exercicios {

    // Estrutura de nó compartilhada para as resoluções encadeadas
    static class No {
        int valor;
        No proximo;

        public No(int valor) {
            this.valor = valor;
            this.proximo = null;
        }
    }

    // Implementação canônica do TAD Pilha Encadeada de inteiros
    static class Pilha {
        private No topo;
        private int tamanho;

        public Pilha() {
            this.topo = null;
            this.tamanho = 0;
        }

        public void push(int valor) {
            No novo = new No(valor);
            novo.proximo = topo;
            topo = novo;
            tamanho++;
        }

        public int pop() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            int val = topo.valor;
            topo = topo.proximo;
            tamanho--;
            return val;
        }

        public int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            return topo.valor;
        }

        public boolean isEmpty() {
            return topo == null;
        }

        public int size() {
            return tamanho;
        }

        // Método para exibir a pilha sem quebrar o estado original
        public void exibir() {
            Pilha aux = new Pilha();
            System.out.print("[TOPO] -> ");
            while (!isEmpty()) {
                int v = pop();
                System.out.print(v + " ");
                aux.push(v);
            }
            System.out.println("-> [BASE]");
            while (!aux.isEmpty()) {
                push(aux.pop());
            }
        }

        // ---------------------------------------------------------------------
        // Exercício 3: Contagem de Ocorrências com Restauração de Pilha
        // ---------------------------------------------------------------------
        public int contarOcorrencias(int valorBuscado) {
            Pilha auxiliar = new Pilha();
            int contador = 0;

            // Desempilha todos os elementos para contagem linear O(n)
            while (!isEmpty()) {
                int atual = pop();
                if (atual == valorBuscado) {
                    contador++;
                }
                auxiliar.push(atual);
            }

            // Restauração estrita da pilha original - O(n)
            while (!auxiliar.isEmpty()) {
                push(auxiliar.pop());
            }

            return contador;
        }
    }

    // -------------------------------------------------------------------------
    // Exercício 1: Inversão de Conteúdo de Pilha Preservando Abstração
    // -------------------------------------------------------------------------
    public static Pilha inverterPilha(Pilha original) {
        Pilha invertida = new Pilha();
        Pilha auxiliar = new Pilha();

        // Desempilha da original alimentando a invertida e a auxiliar
        while (!original.isEmpty()) {
            int v = original.pop();
            invertida.push(v);
            auxiliar.push(v);
        }

        // Restaura a pilha original ao seu estado inicial
        while (!auxiliar.isEmpty()) {
            original.push(auxiliar.pop());
        }

        return invertida;
    }

    // -------------------------------------------------------------------------
    // Pilha genérica simples para caracteres (usada no Exercício 2)
    // -------------------------------------------------------------------------
    static class PilhaChar {
        static class NoChar {
            char c;
            NoChar prox;
            NoChar(char c) { this.c = c; this.prox = null; }
        }
        private NoChar topo = null;

        public void push(char c) {
            NoChar n = new NoChar(c);
            n.prox = topo;
            topo = n;
        }

        public char pop() {
            if (isEmpty()) throw new IllegalStateException("Pilha vazia");
            char c = topo.c;
            topo = topo.prox;
            return c;
        }

        public boolean isEmpty() {
            return topo == null;
        }
    }

    // -------------------------------------------------------------------------
    // Exercício 2: Verificador de Delimitadores Balanceados
    // -------------------------------------------------------------------------
    public static boolean verificarBalanceamento(String expressao) {
        PilhaChar pilha = new PilhaChar();

        for (int i = 0; i < expressao.length(); i++) {
            char c = expressao.charAt(i);

            if (c == '(' || c == '[' || c == '{') {
                pilha.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (pilha.isEmpty()) {
                    return false; // Fechamento sem abertura correspondente
                }
                char aberto = pilha.pop();
                if (!corresponde(aberto, c)) {
                    return false; // Tipo incompatível de fechamento
                }
            }
        }

        return pilha.isEmpty(); // Se sobrar algo na pilha, faltou fechar delimitador
    }

    private static boolean corresponde(char abertura, char fechamento) {
        return (abertura == '(' && fechamento == ')') ||
               (abertura == '[' && fechamento == ']') ||
               (abertura == '{' && fechamento == '}');
    }

    // -------------------------------------------------------------------------
    // Exercício 4: Pilha Sequencial com Redimensionamento Automático
    // -------------------------------------------------------------------------
    static class PilhaSequencialDinamica {
        private int[] dados;
        private int topo;
        private final int capacidadeInicial;

        public PilhaSequencialDinamica(int capacidadeInicial) {
            if (capacidadeInicial <= 0) capacidadeInicial = 4;
            this.capacidadeInicial = capacidadeInicial;
            this.dados = new int[capacidadeInicial];
            this.topo = -1;
        }

        public void push(int valor) {
            if (topo == dados.length - 1) {
                redimensionar(dados.length * 2); // Dobra capacidade
            }
            topo++;
            dados[topo] = valor;
        }

        public int pop() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            int val = dados[topo];
            topo--;

            // Redução por 2 se o tamanho for <= 25% e não for menor que capacidadeInicial
            if (size() > 0 && size() <= dados.length / 4 && dados.length / 2 >= capacidadeInicial) {
                redimensionar(dados.length / 2);
            }
            return val;
        }

        public int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("Pilha vazia");
            }
            return dados[topo];
        }

        public boolean isEmpty() {
            return topo == -1;
        }

        public int size() {
            return topo + 1;
        }

        public int getCapacidade() {
            return dados.length;
        }

        private void redimensionar(int novaCapacidade) {
            int[] novoVetor = new int[novaCapacidade];
            int qtd = size();
            for (int i = 0; i < qtd; i++) {
                novoVetor[i] = dados[i];
            }
            this.dados = novoVetor;
        }
    }

    // -------------------------------------------------------------------------
    // Execução e Validação dos Exercícios
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("  RESOLUCAO DOS EXERCICIOS PRATICOS - TAD PILHA");
        System.out.println("====================================================\n");

        // Teste Exercício 1
        System.out.println("--- Exercicio 1: Inversao de Pilha ---");
        Pilha p1 = new Pilha();
        p1.push(1);
        p1.push(2);
        p1.push(3);
        p1.push(4);
        System.out.print("Pilha original: ");
        p1.exibir();
        Pilha pInvertida = inverterPilha(p1);
        System.out.print("Pilha invertida: ");
        pInvertida.exibir();
        System.out.print("Pilha original intacta: ");
        p1.exibir();

        // Teste Exercício 2
        System.out.println("\n--- Exercicio 2: Delimitadores Balanceados ---");
        String expr1 = "{ [ ( 2 + 3 ) * 5 ] - 1 }";
        String expr2 = "( [ ) ]";
        String expr3 = "{ [ ( ) ]";
        System.out.println(expr1 + " -> Valido? " + verificarBalanceamento(expr1));
        System.out.println(expr2 + " -> Valido? " + verificarBalanceamento(expr2));
        System.out.println(expr3 + " -> Valido? " + verificarBalanceamento(expr3));

        // Teste Exercício 3
        System.out.println("\n--- Exercicio 3: Contagem de Ocorrencias ---");
        Pilha p3 = new Pilha();
        p3.push(10);
        p3.push(20);
        p3.push(10);
        p3.push(30);
        p3.push(10);
        System.out.print("Pilha de teste: ");
        p3.exibir();
        System.out.println("Ocorrencias de 10: " + p3.contarOcorrencias(10));
        System.out.println("Ocorrencias de 50: " + p3.contarOcorrencias(50));
        System.out.print("Pilha apos contagem (restaurada): ");
        p3.exibir();

        // Teste Exercício 4
        System.out.println("\n--- Exercicio 4: Pilha Sequencial com Redimensionamento ---");
        PilhaSequencialDinamica pDin = new PilhaSequencialDinamica(2);
        System.out.println("Capacidade inicial: " + pDin.getCapacidade());
        pDin.push(10);
        pDin.push(20);
        System.out.println("Inseridos 2 elementos. Capacidade: " + pDin.getCapacidade());
        pDin.push(30); // Dispara expansão
        System.out.println("Inserido 3 elemento. Nova Capacidade: " + pDin.getCapacidade() + " (Tamanho: " + pDin.size() + ")");
        pDin.push(40);
        pDin.push(50); // Nova expansão para 8
        System.out.println("Inserido 5 elemento. Nova Capacidade: " + pDin.getCapacidade() + " (Tamanho: " + pDin.size() + ")");
        pDin.pop();
        pDin.pop();
        pDin.pop();
        pDin.pop(); // Reduz quando chega a 25%
        System.out.println("Apos multiplos pops -> Tamanho: " + pDin.size() + ", Capacidade adaptada: " + pDin.getCapacidade());
    }
}
