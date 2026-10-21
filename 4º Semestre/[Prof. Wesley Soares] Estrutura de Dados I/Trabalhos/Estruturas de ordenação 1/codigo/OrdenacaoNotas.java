/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Estruturas de ordenação 1 (Bubble Sort e Selection Sort)
 * 
 * Como compilar:
 * javac OrdenacaoNotas.java
 * 
 * Como executar:
 * java OrdenacaoNotas
 */
import java.util.Arrays;

public class OrdenacaoNotas {

    // Variaveis estaticas para contabilizar comparacoes e trocas
    private static int comparacoesBubble = 0;
    private static int trocasBubble = 0;
    private static int comparacoesSelection = 0;
    private static int trocasSelection = 0;

    /**
     * Exercicio 1: Implementacao do metodo Bubble Sort.
     * Percorre o vetor repetidamente trocando elementos adjacentes fora de ordem.
     */
    public static void bubbleSort(int[] notas) {
        int n = notas.length;
        comparacoesBubble = 0;
        trocasBubble = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                comparacoesBubble++;
                if (notas[j] > notas[j + 1]) {
                    int temp = notas[j];
                    notas[j] = notas[j + 1];
                    notas[j + 1] = temp;
                    trocasBubble++;
                }
            }
        }
    }

    /**
     * Exercicio 1: Implementacao do metodo Selection Sort.
     * Localiza o menor elemento da sublista nao ordenada e o coloca na posicao correta.
     */
    public static void selectionSort(int[] notas) {
        int n = notas.length;
        comparacoesSelection = 0;
        trocasSelection = 0;

        for (int i = 0; i < n - 1; i++) {
            int indiceMenor = i;
            for (int j = i + 1; j < n; j++) {
                comparacoesSelection++;
                if (notas[j] < notas[indiceMenor]) {
                    indiceMenor = j;
                }
            }
            if (indiceMenor != i) {
                int temp = notas[i];
                notas[i] = notas[indiceMenor];
                notas[indiceMenor] = temp;
                trocasSelection++;
            }
        }
    }

    public static void main(String[] args) {
        // Vetor fornecido pela instituicao
        int[] notasOriginal = {8, 5, 10, 3, 7, 2, 9, 6};

        // Criacao de copias independentes para execucao isolada dos algoritmos
        int[] notasBubble = Arrays.copyOf(notasOriginal, notasOriginal.length);
        int[] notasSelection = Arrays.copyOf(notasOriginal, notasOriginal.length);

        System.out.println("====================================================");
        System.out.println("  RELATORIO DE DESEMPENHO - ALGORITMOS DE ORDENACAO ");
        System.out.println("====================================================");
        System.out.println("Vetor original: " + Arrays.toString(notasOriginal));
        System.out.println();

        // Execucao e exibicao do Bubble Sort
        System.out.println("--- Bubble Sort ---");
        System.out.println("Antes da ordenacao:  " + Arrays.toString(notasBubble));
        bubbleSort(notasBubble);
        System.out.println("Depois da ordenacao: " + Arrays.toString(notasBubble));
        System.out.println("Comparacoes realizadas: " + comparacoesBubble);
        System.out.println("Trocas realizadas:      " + trocasBubble);
        System.out.println();

        // Execucao e exibicao do Selection Sort
        System.out.println("--- Selection Sort ---");
        System.out.println("Antes da ordenacao:  " + Arrays.toString(notasSelection));
        selectionSort(notasSelection);
        System.out.println("Depois da ordenacao: " + Arrays.toString(notasSelection));
        System.out.println("Comparacoes realizadas: " + comparacoesSelection);
        System.out.println("Trocas realizadas:      " + trocasSelection);
        System.out.println();

        // Exibicao estruturada das respostas para as perguntas do formulario
        System.out.println("====================================================");
        System.out.println("      RESPOSTAS DO FORMULARIO DE AVALIACAO          ");
        System.out.println("====================================================");
        System.out.println("Questao 1: Comparacoes:");
        System.out.println("  - Bubble Sort:    " + comparacoesBubble + " comparacoes");
        System.out.println("  - Selection Sort: " + comparacoesSelection + " comparacoes");
        System.out.println();
        System.out.println("Questao 2: Trocas:");
        System.out.println("  - Bubble Sort:    " + trocasBubble + " trocas");
        System.out.println("  - Selection Sort: " + trocasSelection + " trocas");
        System.out.println();
        System.out.println("Questao 3: Resultado final:");
        System.out.println("  - Sim, o resultado foi o mesmo: [2, 3, 5, 6, 7, 8, 9, 10].");
        System.out.println("  - Ambos sao algoritmos determinísticos de ordenacao que garantem o vetor ordenado.");
        System.out.println();
        System.out.println("Questao 4: Menor numero de trocas:");
        System.out.println("  - O Selection Sort realiza menos trocas (no maximo n - 1 trocas no total).");
        System.out.println();
        System.out.println("Questao 5: Desempenho com 1.000 elementos:");
        System.out.println("  - Ambos tem complexidade O(n^2). Para n = 1.000, seriam aproximadamente");
        System.out.println("    (1000 * 999) / 2 = 499.500 comparacoes, aumentando drasticamente o tempo de execucao.");
        System.out.println();
        System.out.println("Questao 6: Otimizacao com vetor pre-ordenado:");
        System.out.println("  - O Bubble Sort pode ser otimizado com uma variavel booleana (flag de troca).");
        System.out.println("    Se nenhuma troca ocorrer na primeira varredura, o laco encerra em tempo linear O(n).");
        System.out.println("====================================================");
    }
}
