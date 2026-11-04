/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Tema: Algoritmo de Ordenação Merge Sort
 * Professor: Prof. Wesley Soares - UniFEF
 *
 * Como compilar e executar:
 *   javac MergeSort.java
 *   java MergeSort
 */

public class MergeSort {

    /**
     * Divide recursivamente o vetor em subvetores até atingir trechos unitários.
     *
     * @param vetor Vetor de inteiros a ser ordenado
     * @param inicio Índice inicial do subvetor
     * @param fim Índice final do subvetor
     */
    public static void mergeSort(int[] vetor, int inicio, int fim) {
        // Caso base: intervalo com zero ou um elemento já está ordenado
        if (inicio >= fim) {
            return;
        }

        // Encontra o ponto médio evitando possível overflow aritmético de (inicio + fim) / 2
        int meio = inicio + (fim - inicio) / 2;

        // Ordena a metade esquerda
        mergeSort(vetor, inicio, meio);

        // Ordena a metade direita
        mergeSort(vetor, meio + 1, fim);

        // Combina as duas metades ordenadas
        intercalar(vetor, inicio, meio, fim);
    }

    /**
     * Combina (intercala) duas partes contíguas e ordenadas do vetor original.
     *
     * @param vetor Vetor original
     * @param inicio Início da primeira parte
     * @param meio Término da primeira parte (meio + 1 é o início da segunda)
     * @param fim Término da segunda parte
     */
    private static void intercalar(int[] vetor, int inicio, int meio, int fim) {
        int tamanho = fim - inicio + 1;
        int[] auxiliar = new int[tamanho];

        int i = inicio;      // Cursor da parte esquerda
        int j = meio + 1;    // Cursor da parte direita
        int k = 0;           // Posição no vetor auxiliar temporário

        // Compara os elementos das duas partes e seleciona o menor
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

        // Copia os elementos restantes da metade esquerda (se houver)
        while (i <= meio) {
            auxiliar[k] = vetor[i];
            i++;
            k++;
        }

        // Copia os elementos restantes da metade direita (se houver)
        while (j <= fim) {
            auxiliar[k] = vetor[j];
            j++;
            k++;
        }

        // Devolve os valores ordenados do auxiliar para o vetor original
        for (int x = 0; x < tamanho; x++) {
            vetor[inicio + x] = auxiliar[x];
        }
    }

    public static void main(String[] args) {
        int[] numeros = {38, 27, 43, 3, 9, 82, 10};

        System.out.println("Vetor original:");
        imprimirVetor(numeros);

        mergeSort(numeros, 0, numeros.length - 1);

        System.out.println("\nVetor ordenado com Merge Sort:");
        imprimirVetor(numeros);
    }

    private static void imprimirVetor(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + (i < vetor.length - 1 ? " " : ""));
        }
        System.out.println();
    }
}
