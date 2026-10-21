/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares - UniFEF
 * Tema: Algoritmos de Ordenação - Bubble Sort e Selection Sort
 *
 * Como compilar e executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
 */

public class ExemplosAula {

    // Implementação do Bubble Sort básico conforme apresentado na aula
    public static void ordenarBubbleSort(int[] vetor) {
        for (int i = 0; i < vetor.length - 1; i++) {
            for (int j = 0; j < vetor.length - 1 - i; j++) {
                if (vetor[j] > vetor[j + 1]) {
                    int temporario = vetor[j];
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temporario;
                }
            }
        }
    }

    // Implementação do Selection Sort conforme apresentado na aula
    public static void ordenarSelectionSort(int[] vetor) {
        for (int i = 0; i < vetor.length - 1; i++) {
            int indiceMenor = i;
            for (int j = i + 1; j < vetor.length; j++) {
                if (vetor[j] < vetor[indiceMenor]) {
                    indiceMenor = j;
                }
            }
            int temporario = vetor[i];
            vetor[i] = vetor[indiceMenor];
            vetor[indiceMenor] = temporario;
        }
    }

    // Método auxiliar para exibição do vetor no terminal
    public static void imprimirVetor(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + (i < vetor.length - 1 ? " " : ""));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Demonstração dos Algoritmos da Aula (Prof. Wesley)");
        System.out.println("==================================================");

        // Exemplo 1: Bubble Sort com o vetor da aula
        int[] numerosBubble = {5, 3, 8, 1};
        System.out.print("Bubble Sort - Vetor Original: ");
        imprimirVetor(numerosBubble);
        ordenarBubbleSort(numerosBubble);
        System.out.print("Bubble Sort - Vetor Ordenado: ");
        imprimirVetor(numerosBubble);

        System.out.println();

        // Exemplo 2: Selection Sort com o vetor da aula
        int[] numerosSelection = {5, 3, 8, 1};
        System.out.print("Selection Sort - Vetor Original: ");
        imprimirVetor(numerosSelection);
        ordenarSelectionSort(numerosSelection);
        System.out.print("Selection Sort - Vetor Ordenado: ");
        imprimirVetor(numerosSelection);
    }
}
