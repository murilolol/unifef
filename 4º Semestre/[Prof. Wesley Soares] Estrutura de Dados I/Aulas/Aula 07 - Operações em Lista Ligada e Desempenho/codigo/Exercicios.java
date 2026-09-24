/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Operações em Lista Ligada e Desempenho
 * 
 * Como compilar e executar:
 * javac Exercicios.java
 * java Exercicios
 */
public class Exercicios {

    // Estrutura básica de Nó para as listas ligadas
    static class No {
        int valor;
        No proximo;

        public No(int valor) {
            this.valor = valor;
            this.proximo = null;
        }
    }

    // ==========================================
    // Exercício 1: Inserção Robusta no Fim com Manutenção de Ponteiros
    // ==========================================
    static class ListaLigadaEx1 {
        private No inicio;
        private No fim;
        private int tamanho;

        public ListaLigadaEx1() {
            this.inicio = null;
            this.fim = null;
            this.tamanho = 0;
        }

        public void adicionarFim(int valor) {
            No novo = new No(valor);
            if (inicio == null) {
                // Caso a lista esteja inicialmente vazia
                inicio = novo;
                fim = novo;
            } else {
                // Caso a lista já possua elementos: O(1) usando a referência fim
                fim.proximo = novo;
                fim = novo;
            }
            tamanho++;
        }

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
            System.out.println(" ] (Início: " + (inicio != null ? inicio.valor : "null") +
                               ", Fim: " + (fim != null ? fim.valor : "null") +
                               ", Tamanho: " + tamanho + ")");
        }
    }

    // ==========================================
    // Exercício 2: Inserção Ordenada no Meio da Lista Ligada
    // ==========================================
    static class ListaLigadaEx2 {
        private No inicio;
        private No fim;
        private int tamanho;

        public ListaLigadaEx2() {
            this.inicio = null;
            this.fim = null;
            this.tamanho = 0;
        }

        public void inserirOrdenado(int valor) {
            No novo = new No(valor);

            // Caso 1: Lista vazia ou elemento menor/igual ao primeiro nó (inserção no início)
            if (inicio == null || valor <= inicio.valor) {
                novo.proximo = inicio;
                inicio = novo;
                if (fim == null) {
                    fim = novo;
                }
                tamanho++;
                return;
            }

            // Caso 2: Elemento maior ou igual ao último nó (inserção no fim)
            if (valor >= fim.valor) {
                fim.proximo = novo;
                fim = novo;
                tamanho++;
                return;
            }

            // Caso 3: Inserção no meio - Busca O(n) + Religamento O(1)
            No atual = inicio;
            while (atual.proximo != null && atual.proximo.valor < valor) {
                atual = atual.proximo;
            }

            novo.proximo = atual.proximo;
            atual.proximo = novo;
            tamanho++;
        }

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
    }

    // ==========================================
    // Exercício 3: Busca e Remoção de Elemento por Valor
    // ==========================================
    static class ListaLigadaEx3 {
        private No inicio;
        private No fim;
        private int tamanho;

        public ListaLigadaEx3() {
            this.inicio = null;
            this.fim = null;
            this.tamanho = 0;
        }

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

        public boolean removerPorValor(int valor) {
            if (inicio == null) {
                return false;
            }

            // Caso o elemento a ser removido seja o primeiro nó
            if (inicio.valor == valor) {
                inicio = inicio.proximo;
                if (inicio == null) {
                    fim = null;
                }
                tamanho--;
                return true;
            }

            // Busca linear pelo nó anterior ao elemento visado
            No anterior = inicio;
            while (anterior.proximo != null && anterior.proximo.valor != valor) {
                anterior = anterior.proximo;
            }

            // Valor não existe na lista
            if (anterior.proximo == null) {
                return false;
            }

            // Se o nó a ser removido for o último, atualiza o ponteiro fim
            if (anterior.proximo == fim) {
                fim = anterior;
            }

            // Desvincula o nó da cadeia de ponteiros
            anterior.proximo = anterior.proximo.proximo;
            tamanho--;
            return true;
        }

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
    }

    // ==========================================
    // Exercício 4: Simulação Comparativa de Custo: Deslocamento vs Percurso
    // ==========================================
    static class ComparadorDesempenho {

        // Simula inserção em Lista Sequencial com vetor contíguo
        public static int simularInsercaoSequencial(int[] vetor, int totalElementos, int indiceInsercao, int novoValor) {
            int deslocamentos = 0;
            for (int i = totalElementos; i > indiceInsercao; i--) {
                vetor[i] = vetor[i - 1];
                deslocamentos++;
            }
            vetor[indiceInsercao] = novoValor;
            return deslocamentos;
        }

        // Simula percurso de ponteiros em Lista Ligada até a posição desejada
        public static int simularInsercaoLigada(int posicaoDesejada) {
            int passosPercorridos = 0;
            for (int i = 0; i < posicaoDesejada; i++) {
                passosPercorridos++;
            }
            return passosPercorridos;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Resolução dos Exercícios Práticos - Estrutura de Dados I");
        System.out.println("==================================================");

        // Demonstração Exercício 1
        System.out.println("\n--- [Exercício 1] Inserção Robusta no Fim O(1) ---");
        ListaLigadaEx1 ex1 = new ListaLigadaEx1();
        System.out.println("Adicionando 10 em lista vazia:");
        ex1.adicionarFim(10);
        ex1.exibir();
        System.out.println("Adicionando 20, 30 e 40 em sequência:");
        ex1.adicionarFim(20);
        ex1.adicionarFim(30);
        ex1.adicionarFim(40);
        ex1.exibir();

        // Demonstração Exercício 2
        System.out.println("\n--- [Exercício 2] Inserção Ordenada no Meio (Exemplo com 35) ---");
        ListaLigadaEx2 ex2 = new ListaLigadaEx2();
        ex2.inserirOrdenado(10);
        ex2.inserirOrdenado(20);
        ex2.inserirOrdenado(30);
        ex2.inserirOrdenado(40);
        ex2.inserirOrdenado(50);
        System.out.println("Lista ordenada inicial:");
        ex2.exibir();
        System.out.println("Inserindo o valor 35 no meio (entre 30 e 40):");
        ex2.inserirOrdenado(35);
        ex2.exibir();
        System.out.println("Inserindo o valor 5 (novo início):");
        ex2.inserirOrdenado(5);
        ex2.exibir();
        System.out.println("Inserindo o valor 60 (novo fim):");
        ex2.inserirOrdenado(60);
        ex2.exibir();

        // Demonstração Exercício 3
        System.out.println("\n--- [Exercício 3] Busca e Remoção por Valor ---");
        ListaLigadaEx3 ex3 = new ListaLigadaEx3();
        ex3.adicionarFim(10);
        ex3.adicionarFim(20);
        ex3.adicionarFim(30);
        ex3.adicionarFim(40);
        ex3.exibir();
        System.out.println("Removendo valor 30 (nó intermediário): " + ex3.removerPorValor(30));
        ex3.exibir();
        System.out.println("Removendo valor 10 (primeiro nó): " + ex3.removerPorValor(10));
        ex3.exibir();
        System.out.println("Removendo valor 40 (último nó): " + ex3.removerPorValor(40));
        ex3.exibir();
        System.out.println("Tentando remover valor 99 (inexistente): " + ex3.removerPorValor(99));
        ex3.exibir();

        // Demonstração Exercício 4
        System.out.println("\n--- [Exercício 4] Comparativo: Deslocamento vs Percurso ---");
        int tamanhoEntrada = 1000;
        int posicaoMeio = tamanhoEntrada / 2;

        int[] vetor = new int[tamanhoEntrada + 10];
        for (int i = 0; i < tamanhoEntrada; i++) {
            vetor[i] = i * 2;
        }

        int deslocamentosVetor = ComparadorDesempenho.simularInsercaoSequencial(vetor, tamanhoEntrada, posicaoMeio, 999);
        int percursosLigada = ComparadorDesempenho.simularInsercaoLigada(posicaoMeio);

        System.out.println("Critérios da Revisão para Prova aplicados:");
        System.out.println("• Tamanho da entrada (n): " + tamanhoEntrada);
        System.out.println("• Operação realizada: Inserção na posição central (índice " + posicaoMeio + ")");
        System.out.println("• Lista Sequencial: " + deslocamentosVetor + " deslocamentos físicos de memória (O(n))");
        System.out.println("• Lista Ligada: " + percursosLigada + " percursos de referências (O(n) busca) + 2 religamentos de nós (O(1))");
        System.out.println("• Comportamento da memória: Vetor contíguo (localidade de cache) vs Lista Ligada (alocação dinâmica dispersa)");
    }
}
