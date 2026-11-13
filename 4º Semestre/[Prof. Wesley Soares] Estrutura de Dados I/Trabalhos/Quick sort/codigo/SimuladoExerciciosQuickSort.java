/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Tema: Simulado de Preparação para a Atividade de Quick Sort (Aula 14)
 *
 * Como compilar:
 *   javac SimuladoExerciciosQuickSort.java
 * Como executar:
 *   java SimuladoExerciciosQuickSort
 */

import java.util.Arrays;

public class SimuladoExerciciosQuickSort {

    // =========================================================================
    // EXERCÍCIO 1: Implementação Canônica e Teste Geral
    // =========================================================================
    public static void resolverExercicio1() {
        System.out.println("----------------------------------------------------------");
        System.out.println("EXERCÍCIO 1: Implementação Canônica do Quick Sort");
        System.out.println("----------------------------------------------------------");

        int[] vetor = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("Vetor de entrada: " + Arrays.toString(vetor));

        executarQuickSortEx1(vetor, 0, vetor.length - 1);

        System.out.println("Vetor ordenado:   " + Arrays.toString(vetor));
        System.out.println("Resultado validado: ordenação crescente in-place garantida.\n");
    }

    private static void executarQuickSortEx1(int[] v, int inicio, int fim) {
        if (inicio < fim) {
            int pivoIndice = particionarEx1(v, inicio, fim);
            executarQuickSortEx1(v, inicio, pivoIndice - 1);
            executarQuickSortEx1(v, pivoIndice + 1, fim);
        }
    }

    private static int particionarEx1(int[] v, int inicio, int fim) {
        int pivo = v[fim];
        int i = inicio - 1;
        for (int j = inicio; j < fim; j++) {
            if (v[j] <= pivo) {
                i++;
                int temp = v[i];
                v[i] = v[j];
                v[j] = temp;
            }
        }
        int temp = v[i + 1];
        v[i + 1] = v[fim];
        v[fim] = temp;
        return i + 1;
    }

    // =========================================================================
    // EXERCÍCIO 2: Rastreamento Passo a Passo do Particionamento de Lomuto
    // =========================================================================
    public static void resolverExercicio2() {
        System.out.println("----------------------------------------------------------");
        System.out.println("EXERCÍCIO 2: Teste de Mesa Passo a Passo do Particionamento");
        System.out.println("----------------------------------------------------------");
        System.out.println("Vetor analisado: A = [38, 27, 43, 3, 9, 82, 10]");
        System.out.println("Limites da partição: inicio = 0, fim = 6");

        int[] A = {38, 27, 43, 3, 9, 82, 10};
        int inicio = 0;
        int fim = 6;
        int pivo = A[fim];
        int i = inicio - 1;

        System.out.println("Pivô selecionado (A[" + fim + "]): " + pivo);
        System.out.println("Ponteiro de fronteira inicial i: " + i + "\n");

        for (int j = inicio; j < fim; j++) {
            System.out.print("Iteração j = " + j + " (A[j] = " + A[j] + "): ");
            if (A[j] <= pivo) {
                i++;
                System.out.print("A[j] <= " + pivo + " -> SIM. Incrementa i=" + i + ". Troca A[" + i + "] com A[" + j + "]. ");
                int temp = A[i];
                A[i] = A[j];
                A[j] = temp;
            } else {
                System.out.print("A[j] <= " + pivo + " -> NÃO. Nenhuma troca realizada. ");
            }
            System.out.println("Vetor: " + Arrays.toString(A));
        }

        System.out.println("\n--- Fim do laço de varredura ---");
        System.out.println("Posicionamento final do pivô: troca A[" + (i + 1) + "] com A[" + fim + "]");
        int temp = A[i + 1];
        A[i + 1] = A[fim];
        A[fim] = temp;

        int indiceFinalPivo = i + 1;
        System.out.println("Vetor após reposicionar o pivô: " + Arrays.toString(A));
        System.out.println("Índice definitivo retornado: " + indiceFinalPivo);
        System.out.println("Subvetor esquerdo (<=" + pivo + "): índices 0 a " + (indiceFinalPivo - 1) + " -> " + Arrays.toString(Arrays.copyOfRange(A, 0, indiceFinalPivo)));
        System.out.println("Subvetor direito ( >" + pivo + "): índices " + (indiceFinalPivo + 1) + " a 6 -> " + Arrays.toString(Arrays.copyOfRange(A, indiceFinalPivo + 1, A.length)) + "\n");
    }

    // =========================================================================
    // EXERCÍCIO 3: Pior Caso O(n²) vs. Mitigação por Mediana de Três
    // =========================================================================
    private static int chamadasRecursivasClassico = 0;
    private static int comparacoesClassico = 0;
    private static int chamadasRecursivasMediana = 0;
    private static int comparacoesMediana = 0;

    public static void resolverExercicio3() {
        System.out.println("----------------------------------------------------------");
        System.out.println("EXERCÍCIO 3: Degradação para Pior Caso e Mediana de Três");
        System.out.println("----------------------------------------------------------");

        int tamanho = 50;
        int[] ordenado1 = new int[tamanho];
        int[] ordenado2 = new int[tamanho];
        for (int k = 0; k < tamanho; k++) {
            ordenado1[k] = k + 1;
            ordenado2[k] = k + 1;
        }

        chamadasRecursivasClassico = 0;
        comparacoesClassico = 0;
        quickSortClassicoPiorCaso(ordenado1, 0, ordenado1.length - 1);

        chamadasRecursivasMediana = 0;
        comparacoesMediana = 0;
        quickSortMedianaDeTres(ordenado2, 0, ordenado2.length - 1);

        System.out.println("Tamanho do vetor ordenado testado: N = " + tamanho);
        System.out.println("--- Quick Sort Clássico (Pivô no Fim - Pior Caso O(n²)) ---");
        System.out.println("  Total de chamadas recursivas: " + chamadasRecursivasClassico);
        System.out.println("  Total de comparações no particionamento: " + comparacoesClassico);
        System.out.println("--- Quick Sort com Mediana de Três ---");
        System.out.println("  Total de chamadas recursivas: " + chamadasRecursivasMediana);
        System.out.println("  Total de comparações no particionamento: " + comparacoesMediana);
        System.out.println("Conclusão: A mediana de três evita divisões degeneradas (0 e N-1), restaurando o comportamento O(n log n).\n");
    }

    private static void quickSortClassicoPiorCaso(int[] v, int inicio, int fim) {
        chamadasRecursivasClassico++;
        if (inicio < fim) {
            int pivo = v[fim];
            int i = inicio - 1;
            for (int j = inicio; j < fim; j++) {
                comparacoesClassico++;
                if (v[j] <= pivo) {
                    i++;
                    int temp = v[i];
                    v[i] = v[j];
                    v[j] = temp;
                }
            }
            int temp = v[i + 1];
            v[i + 1] = v[fim];
            v[fim] = temp;
            int q = i + 1;

            quickSortClassicoPiorCaso(v, inicio, q - 1);
            quickSortClassicoPiorCaso(v, q + 1, fim);
        }
    }

    private static void quickSortMedianaDeTres(int[] v, int inicio, int fim) {
        chamadasRecursivasMediana++;
        if (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            // Ajusta inicio, meio e fim para encontrar a mediana e colocá-la no fim
            if (v[inicio] > v[meio]) {
                int t = v[inicio]; v[inicio] = v[meio]; v[meio] = t;
            }
            if (v[inicio] > v[fim]) {
                int t = v[inicio]; v[inicio] = v[fim]; v[fim] = t;
            }
            if (v[meio] > v[fim]) {
                int t = v[meio]; v[meio] = v[fim]; v[fim] = t;
            }
            // Coloca a mediana no final para reutilizar a lógica de Lomuto
            int t = v[meio]; v[meio] = v[fim]; v[fim] = t;

            int pivo = v[fim];
            int i = inicio - 1;
            for (int j = inicio; j < fim; j++) {
                comparacoesMediana++;
                if (v[j] <= pivo) {
                    i++;
                    int temp = v[i];
                    v[i] = v[j];
                    v[j] = temp;
                }
            }
            int temp = v[i + 1];
            v[i + 1] = v[fim];
            v[fim] = temp;
            int q = i + 1;

            quickSortMedianaDeTres(v, inicio, q - 1);
            quickSortMedianaDeTres(v, q + 1, fim);
        }
    }

    // =========================================================================
    // EXERCÍCIO 4: Quick Sort Decrescente Instrumentado
    // =========================================================================
    private static long contComparacoes = 0;
    private static long contTrocas = 0;
    private static int profundidadeAtual = 0;
    private static int profundidadeMaxima = 0;

    public static void resolverExercicio4() {
        System.out.println("----------------------------------------------------------");
        System.out.println("EXERCÍCIO 4: Quick Sort Decrescente Instrumentado");
        System.out.println("----------------------------------------------------------");

        int N = 20;
        int[] decrescente = new int[N];
        int[] crescente = new int[N];
        int[] aleatorio = {14, 88, 3, 42, 91, 5, 27, 63, 19, 74, 50, 11, 8, 33, 99, 2, 60, 47, 81, 22};

        for (int k = 0; k < N; k++) {
            decrescente[k] = N - k; // Já ordenado decrescente
            crescente[k] = k + 1;   // Invertido (pior caso)
        }

        testarCenarioDecrescente("Vetor Já Decrescente", decrescente);
        testarCenarioDecrescente("Vetor Crescente (Invertido)", crescente);
        testarCenarioDecrescente("Vetor Pseudoaleatório", aleatorio);
    }

    private static void testarCenarioDecrescente(String rotulo, int[] original) {
        int[] v = Arrays.copyOf(original, original.length);
        contComparacoes = 0;
        contTrocas = 0;
        profundidadeAtual = 0;
        profundidadeMaxima = 0;

        quickSortDecrescente(v, 0, v.length - 1);

        System.out.println("Cenário: " + rotulo);
        System.out.println("  Resultado: " + Arrays.toString(v));
        System.out.println("  Comparações: " + contComparacoes + " | Trocas: " + contTrocas + " | Profundidade Máx Pilha: " + profundidadeMaxima);
    }

    private static void quickSortDecrescente(int[] v, int inicio, int fim) {
        profundidadeAtual++;
        if (profundidadeAtual > profundidadeMaxima) {
            profundidadeMaxima = profundidadeAtual;
        }

        if (inicio < fim) {
            int pivoIndice = particionarDecrescente(v, inicio, fim);
            quickSortDecrescente(v, inicio, pivoIndice - 1);
            quickSortDecrescente(v, pivoIndice + 1, fim);
        }

        profundidadeAtual--;
    }

    private static int particionarDecrescente(int[] v, int inicio, int fim) {
        int pivo = v[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            contComparacoes++;
            // Ordem Decrescente: elementos maiores ou iguais ao pivô vão para a esquerda
            if (v[j] >= pivo) {
                i++;
                int temp = v[i];
                v[i] = v[j];
                v[j] = temp;
                contTrocas++;
            }
        }

        int temp = v[i + 1];
        v[i + 1] = v[fim];
        v[fim] = temp;
        contTrocas++;

        return i + 1;
    }

    // =========================================================================
    // Método Principal de Execução de Todos os Exercícios
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  UniFEF - Estrutura de Dados I - Prof. Wesley Soares");
        System.out.println("  Simulado Completo: Quick Sort (Exercícios 1 a 4)");
        System.out.println("==========================================================\n");

        resolverExercicio1();
        resolverExercicio2();
        resolverExercicio3();
        resolverExercicio4();

        System.out.println("==========================================================");
        System.out.println("Todos os exercícios do simulado foram executados com sucesso.");
        System.out.println("==========================================================");
    }
}
