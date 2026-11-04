/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Estrutura de ordenação 3 - Merge Sort
 *
 * Como compilar e executar:
 *   javac MergeSort.java
 *   java MergeSort
 */
public class MergeSort {

    /**
     * Método principal que inicializa o processo recursivo de ordenação.
     * @param vetor Vetor de inteiros a ser ordenado.
     */
    public static void ordenar(int[] vetor) {
        if (vetor == null || vetor.length <= 1) {
            return;
        }
        mergeSort(vetor, 0, vetor.length - 1);
    }

    /**
     * Divisão recursiva do vetor com base no paradigma de Divisão e Conquista.
     * @param vetor Vetor original.
     * @param inicio Índice inicial do subvetor.
     * @param fim Índice final do subvetor.
     */
    private static void mergeSort(int[] vetor, int inicio, int fim) {
        // Caso Base: se o subvetor possuir 0 ou 1 elemento, já está ordenado
        if (inicio >= fim) {
            return;
        }

        // Cálculo do ponto médio seguro contra overflow
        int meio = inicio + (fim - inicio) / 2;

        // Divisão da metade esquerda
        mergeSort(vetor, inicio, meio);

        // Divisão da metade direita
        mergeSort(vetor, meio + 1, fim);

        // Conquista: intercalação ordenada das duas metades já resolvidas
        intercalar(vetor, inicio, meio, fim);
    }

    /**
     * Realiza a intercalação (merge) das duas metades ordenadas utilizando vetor auxiliar.
     * É aqui que ocorre a ordenação efetiva dos elementos.
     */
    private static void intercalar(int[] vetor, int inicio, int meio, int fim) {
        int tamanho = fim - inicio + 1;
        int[] auxiliar = new int[tamanho];

        int i = inicio;    // Ponteiro da metade esquerda
        int j = meio + 1;  // Ponteiro da metade direita
        int k = 0;         // Ponteiro do vetor auxiliar

        // Compara os elementos das duas metades e aloca ordenadamente no auxiliar
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

        // Copia os elementos restantes da metade esquerda, se houver
        while (i <= meio) {
            auxiliar[k] = vetor[i];
            i++;
            k++;
        }

        // Copia os elementos restantes da metade direita, se houver
        while (j <= fim) {
            auxiliar[k] = vetor[j];
            j++;
            k++;
        }

        // Transfere os elementos ordenados do vetor auxiliar de volta para o vetor original
        for (int p = 0; p < tamanho; p++) {
            vetor[inicio + p] = auxiliar[p];
        }
    }

    /**
     * Utilitário para exibir o vetor no console.
     */
    public static void exibirVetor(int[] vetor) {
        System.out.print("[");
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i]);
            if (i < vetor.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("  UniFEF - Estrutura de Dados I - Prof. Wesley Soares");
        System.out.println("  Demonstração do Algoritmo Merge Sort");
        System.out.println("====================================================");

        int[] dados = { 38, 27, 43, 3, 9, 82, 10 };

        System.out.println("Vetor original:");
        exibirVetor(dados);

        ordenar(dados);

        System.out.println("\nVetor ordenado com Merge Sort:");
        exibirVetor(dados);
    }
}
