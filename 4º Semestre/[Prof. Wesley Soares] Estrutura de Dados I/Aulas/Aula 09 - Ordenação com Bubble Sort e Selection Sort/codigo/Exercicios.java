/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares - UniFEF
 * Tema: Resolução de Exercícios - Bubble Sort e Selection Sort
 *
 * Como compilar e executar:
 *   javac Exercicios.java
 *   java Exercicios
 */

public class Exercicios {

    // ==============================================================
    // Exercício 1: Bubble Sort Otimizado com Interrupção Antecipada
    // ==============================================================
    public static int bubbleSortOtimizado(int[] vetor) {
        int passagens = 0;
        for (int i = 0; i < vetor.length - 1; i++) {
            passagens++;
            boolean houveTroca = false;
            for (int j = 0; j < vetor.length - 1 - i; j++) {
                if (vetor[j] > vetor[j + 1]) {
                    int temp = vetor[j];
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temp;
                    houveTroca = true;
                }
            }
            // Se não houve nenhuma troca nesta passagem, o vetor já está ordenado
            if (!houveTroca) {
                break;
            }
        }
        return passagens;
    }

    // ==============================================================
    // Exercício 2: Contadores de Comparações e Trocas em Ambos
    // ==============================================================
    public static class MetricasOrdenacao {
        public int comparacoes = 0;
        public int trocas = 0;
    }

    public static MetricasOrdenacao bubbleSortComMetricas(int[] vetor) {
        MetricasOrdenacao metricas = new MetricasOrdenacao();
        for (int i = 0; i < vetor.length - 1; i++) {
            for (int j = 0; j < vetor.length - 1 - i; j++) {
                metricas.comparacoes++;
                if (vetor[j] > vetor[j + 1]) {
                    int temp = vetor[j];
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temp;
                    metricas.trocas++;
                }
            }
        }
        return metricas;
    }

    public static MetricasOrdenacao selectionSortComMetricas(int[] vetor) {
        MetricasOrdenacao metricas = new MetricasOrdenacao();
        for (int i = 0; i < vetor.length - 1; i++) {
            int indiceMenor = i;
            for (int j = i + 1; j < vetor.length; j++) {
                metricas.comparacoes++;
                if (vetor[j] < vetor[indiceMenor]) {
                    indiceMenor = j;
                }
            }
            if (indiceMenor != i) {
                int temp = vetor[i];
                vetor[i] = vetor[indiceMenor];
                vetor[indiceMenor] = temp;
                metricas.trocas++;
            }
        }
        return metricas;
    }

    // ==============================================================
    // Exercício 3: Selection Sort Decrescente
    // ==============================================================
    public static void selectionSortDecrescente(int[] vetor) {
        for (int i = 0; i < vetor.length - 1; i++) {
            int indiceMaior = i;
            for (int j = i + 1; j < vetor.length; j++) {
                if (vetor[j] > vetor[indiceMaior]) {
                    indiceMaior = j;
                }
            }
            int temp = vetor[i];
            vetor[i] = vetor[indiceMaior];
            vetor[indiceMaior] = temp;
        }
    }

    // ==============================================================
    // Exercício 4: Ordenação de Objetos com Critério de Desempate
    // ==============================================================
    public static class Aluno {
        private String nome;
        private double nota;

        public Aluno(String nome, double nota) {
            this.nome = nome;
            this.nota = nota;
        }

        public String getNome() {
            return nome;
        }

        public double getNota() {
            return nota;
        }

        @Override
        public String toString() {
            return nome + " (Nota: " + nota + ")";
        }
    }

    public static void bubbleSortAlunos(Aluno[] alunos) {
        for (int i = 0; i < alunos.length - 1; i++) {
            for (int j = 0; j < alunos.length - 1 - i; j++) {
                boolean deveTrocar = false;
                // Critério primário: nota crescente
                if (alunos[j].getNota() > alunos[j + 1].getNota()) {
                    deveTrocar = true;
                } else if (alunos[j].getNota() == alunos[j + 1].getNota()) {
                    // Critério secundário: ordem alfabética do nome
                    if (alunos[j].getNome().compareToIgnoreCase(alunos[j + 1].getNome()) > 0) {
                        deveTrocar = true;
                    }
                }

                if (deveTrocar) {
                    Aluno temp = alunos[j];
                    alunos[j] = alunos[j + 1];
                    alunos[j + 1] = temp;
                }
            }
        }
    }

    // Método utilitário para imprimir vetor
    public static void imprimirVetor(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + (i < vetor.length - 1 ? " " : ""));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Exercício 1: Bubble Sort Otimizado (Melhor Caso)");
        System.out.println("==================================================");
        int[] vetorJaOrdenado = {1, 2, 3, 4, 5, 6};
        System.out.print("Vetor inicial já ordenado: ");
        imprimirVetor(vetorJaOrdenado);
        int passagens = bubbleSortOtimizado(vetorJaOrdenado);
        System.out.println("Passagens necessárias: " + passagens + " (Complexidade O(n))");

        System.out.println("\n==================================================");
        System.out.println("Exercício 2: Comparação de Comparações e Trocas");
        System.out.println("==================================================");
        int[] entradaBubble = {9, 5, 1, 4, 3, 8, 2};
        int[] entradaSelection = {9, 5, 1, 4, 3, 8, 2};

        MetricasOrdenacao mBubble = bubbleSortComMetricas(entradaBubble);
        MetricasOrdenacao mSelection = selectionSortComMetricas(entradaSelection);

        System.out.println("Bubble Sort    -> Comparações: " + mBubble.comparacoes + " | Trocas: " + mBubble.trocas);
        System.out.println("Selection Sort -> Comparações: " + mSelection.comparacoes + " | Trocas: " + mSelection.trocas);

        System.out.println("\n==================================================");
        System.out.println("Exercício 3: Selection Sort Decrescente");
        System.out.println("==================================================");
        int[] vetorDecrescente = {12, 3, 45, 7, 23, 9};
        System.out.print("Antes: ");
        imprimirVetor(vetorDecrescente);
        selectionSortDecrescente(vetorDecrescente);
        System.out.print("Depois (Decrescente): ");
        imprimirVetor(vetorDecrescente);

        System.out.println("\n==================================================");
        System.out.println("Exercício 4: Bubble Sort em Vetor de Objetos Aluno");
        System.out.println("==================================================");
        Aluno[] turma = {
            new Aluno("Carlos", 7.5),
            new Aluno("Beatriz", 9.0),
            new Aluno("Ana", 7.5),
            new Aluno("Daniel", 6.0),
            new Aluno("Eduardo", 9.0)
        };
        System.out.println("Lista de alunos antes da ordenação:");
        for (Aluno a : turma) {
            System.out.println("  " + a);
        }
        bubbleSortAlunos(turma);
        System.out.println("\nLista de alunos ordenada (Nota crescente e desempate por Nome):");
        for (Aluno a : turma) {
            System.out.println("  " + a);
        }
    }
}
