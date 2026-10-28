/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Insertion Sort - Ordenação por Inserção
 *
 * Como compilar e executar:
 * javac InsertionSortExemplo.java
 * java InsertionSortExemplo
 */
public class InsertionSortExemplo {

    /**
     * Ordena um vetor de inteiros em ordem crescente utilizando o algoritmo Insertion Sort.
     *
     * @param vetor Vetor de inteiros a ser ordenado
     */
    public static void ordenar(int[] vetor) {
        for (int i = 1; i < vetor.length; i++) {
            int chave = vetor[i];
            int j = i - 1;

            // Desloca elementos maiores que a chave para a direita
            while (j >= 0 && vetor[j] > chave) {
                vetor[j + 1] = vetor[j];
                j--;
            }

            // Insere a chave na posicao correta aberta pelo deslocamento
            vetor[j + 1] = chave;
        }
    }

    public static void main(String[] args) {
        int[] numeros = {7, 3, 8, 2, 6};

        System.out.println("Vetor original:");
        for (int numero : numeros) {
            System.out.print(numero + " ");
        }
        System.out.println();

        ordenar(numeros);

        System.out.println("Vetor ordenado com Insertion Sort:");
        for (int numero : numeros) {
            System.out.print(numero + " ");
        }
        System.out.println();
    }
}
