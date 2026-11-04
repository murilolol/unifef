/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Tema: Algoritmo de Ordenação Merge Sort - Atividade Prática e Desafio
 * Professor: Prof. Wesley Soares - UniFEF
 *
 * Como compilar e executar:
 *   javac MergeSortAtividade.java
 *   java MergeSortAtividade
 */

public class MergeSortAtividade {

    // =========================================================================
    // Exercício 2 (Parte B): Implementação em Java do Merge Sort (Ordem Crescente)
    // =========================================================================

    public static void mergeSort(int[] vetor, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }

        int meio = inicio + (fim - inicio) / 2;
        mergeSort(vetor, inicio, meio);
        mergeSort(vetor, meio + 1, fim);
        intercalar(vetor, inicio, meio, fim);
    }

    private static void intercalar(int[] vetor, int inicio, int meio, int fim) {
        int tamanho = fim - inicio + 1;
        int[] auxiliar = new int[tamanho];

        int i = inicio;
        int j = meio + 1;
        int k = 0;

        while (i <= meio && j <= fim) {
            if (vetor[i] <= vetor[j]) {
                auxiliar[k] = vetor[i];
                i++;
            } else {
                auxiliar[k] = vetor[j];
                j++;
            }
            k++;
        }

        while (i <= meio) {
            auxiliar[k] = vetor[i];
            i++;
            k++;
        }

        while (j <= fim) {
            auxiliar[k] = vetor[j];
            j++;
            k++;
        }

        for (int x = 0; x < tamanho; x++) {
            vetor[inicio + x] = auxiliar[x];
        }
    }

    // =========================================================================
    // Exercício 3 (Desafio): Modificação do Algoritmo para Ordem Decrescente
    // =========================================================================

    public static void mergeSortDecrescente(int[] vetor, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }

        int meio = inicio + (fim - inicio) / 2;
        mergeSortDecrescente(vetor, inicio, meio);
        mergeSortDecrescente(vetor, meio + 1, fim);
        intercalarDecrescente(vetor, inicio, meio, fim);
    }

    /**
     * Intercala duas partes priorizando o maior valor para ordenação decrescente.
     */
    private static void intercalarDecrescente(int[] vetor, int inicio, int meio, int fim) {
        int tamanho = fim - inicio + 1;
        int[] auxiliar = new int[tamanho];

        int i = inicio;
        int j = meio + 1;
        int k = 0;

        // Alteração principal do Desafio: vetor[i] >= vetor[j] para priorizar o maior
        while (i <= meio && j <= fim) {
            if (vetor[i] >= vetor[j]) {
                auxiliar[k] = vetor[i];
                i++;
            } else {
                auxiliar[k] = vetor[j];
                j++;
            }
            k++;
        }

        while (i <= meio) {
            auxiliar[k] = vetor[i];
            i++;
            k++;
        }

        while (j <= fim) {
            auxiliar[k] = vetor[j];
            j++;
            k++;
        }

        for (int x = 0; x < tamanho; x++) {
            vetor[inicio + x] = auxiliar[x];
        }
    }

    public static void exibirVetor(int[] vetor) {
        System.out.print("[");
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + (i < vetor.length - 1 ? ", " : ""));
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("Exercício 1: Atividade Prática - Simulação Manual (Parte A)");
        System.out.println("===============================================================");
        System.out.println("Vetor fornecido: [15, 8, 23, 4, 16, 42, 7, 10]");
        System.out.println("\n1. Etapa de Divisão Recursiva:");
        System.out.println("   Nível 0: [15, 8, 23, 4, 16, 42, 7, 10]");
        System.out.println("   Nível 1: [15, 8, 23, 4]  |  [16, 42, 7, 10]");
        System.out.println("   Nível 2: [15, 8]  |  [23, 4]  |  [16, 42]  |  [7, 10]");
        System.out.println("   Nível 3 (Caso Base): [15], [8], [23], [4], [16], [42], [7], [10]");
        System.out.println("\n2. Etapa de Combinação (Intercalação Ordenada):");
        System.out.println("   Combina pares: [15] e [8] -> [8, 15] | [23] e [4] -> [4, 23]");
        System.out.println("                  [16] e [42] -> [16, 42] | [7] e [10] -> [7, 10]");
        System.out.println("   Combina quartetos: [8, 15] e [4, 23] -> [4, 8, 15, 23]");
        System.out.println("                      [16, 42] e [7, 10] -> [7, 10, 16, 42]");
        System.out.println("   Combina final: [4, 8, 15, 23] e [7, 10, 16, 42] -> [4, 7, 8, 10, 15, 16, 23, 42]");
        System.out.println("\nResultado final ordenado: [4, 7, 8, 10, 15, 16, 23, 42]");
        System.out.println();

        System.out.println("===============================================================");
        System.out.println("Exercício 2: Atividade Prática - Implementação Java (Parte B)");
        System.out.println("===============================================================");
        int[] valores = {15, 8, 23, 4, 16, 42, 7, 10};
        System.out.print("Vetor antes da ordenação: ");
        exibirVetor(valores);

        mergeSort(valores, 0, valores.length - 1);

        System.out.print("Vetor depois da ordenação (crescente): ");
        exibirVetor(valores);
        System.out.println();

        System.out.println("===============================================================");
        System.out.println("Exercício 3: Desafio - Modificação para Ordem Decrescente");
        System.out.println("===============================================================");
        int[] valoresDesafio = {15, 8, 23, 4, 16, 42, 7, 10};
        System.out.print("Vetor antes da ordenação: ");
        exibirVetor(valoresDesafio);

        mergeSortDecrescente(valoresDesafio, 0, valoresDesafio.length - 1);

        System.out.print("Vetor depois da ordenação (decrescente): ");
        exibirVetor(valoresDesafio);
        System.out.println("===============================================================");
    }
}
