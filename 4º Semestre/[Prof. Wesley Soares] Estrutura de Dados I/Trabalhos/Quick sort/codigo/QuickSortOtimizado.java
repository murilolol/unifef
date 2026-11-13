/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Tema: Quick Sort Industrial Otimizado (Mediana de Três + Híbrido com Insertion Sort)
 *
 * Como compilar:
 *   javac QuickSortOtimizado.java
 * Como executar:
 *   java QuickSortOtimizado
 */

import java.util.Arrays;
import java.util.Random;

public class QuickSortOtimizado {

    // Ponto de corte experimental: para partições <= 10 elementos, o Insertion Sort
    // é mais eficiente por não ter custo de overhead da pilha recursiva.
    private static final int CORTE_INSERTION_SORT = 10;

    /**
     * Quick Sort Otimizado.
     * Combina mediana de três, ordenação por inserção para subvetores pequenos
     * e recursão na partição menor para garantir pilha de profundidade O(log n).
     */
    public static void quickSortOtimizado(int[] vetor, int inicio, int fim) {
        while (inicio < fim) {
            // Otimização 1: Subvetores pequenos usam Insertion Sort
            if (fim - inicio + 1 <= CORTE_INSERTION_SORT) {
                insertionSort(vetor, inicio, fim);
                break;
            }

            // Otimização 2: Mediana de três posiciona um pivô equilibrado no final
            int pivoIndice = particionarComMediana(vetor, inicio, fim);

            // Otimização 3: Eliminação de recursão em cauda (Tail Call Optimization)
            // Processa a partição menor recursivamente e itera sobre a maior
            if (pivoIndice - inicio < fim - pivoIndice) {
                quickSortOtimizado(vetor, inicio, pivoIndice - 1);
                inicio = pivoIndice + 1; // Laço continua na partição direita
            } else {
                quickSortOtimizado(vetor, pivoIndice + 1, fim);
                fim = pivoIndice - 1;    // Laço continua na partição esquerda
            }
        }
    }

    /**
     * Seleciona a mediana entre inicio, meio e fim, posicionando-a no fim da partição.
     */
    private static int particionarComMediana(int[] v, int inicio, int fim) {
        int meio = inicio + (fim - inicio) / 2;

        // Ordena os 3 pontos: v[inicio] <= v[meio] <= v[fim]
        if (v[inicio] > v[meio]) {
            trocar(v, inicio, meio);
        }
        if (v[inicio] > v[fim]) {
            trocar(v, inicio, fim);
        }
        if (v[meio] > v[fim]) {
            trocar(v, meio, fim);
        }

        // Troca a mediana (no meio) com a penúltima posição ou última
        trocar(v, meio, fim);
        int pivo = v[fim];

        int i = inicio - 1;
        for (int j = inicio; j < fim; j++) {
            if (v[j] <= pivo) {
                i++;
                trocar(v, i, j);
            }
        }
        trocar(v, i + 1, fim);
        return i + 1;
    }

    /**
     * Insertion Sort para subvetores pequenos.
     */
    private static void insertionSort(int[] v, int inicio, int fim) {
        for (int i = inicio + 1; i <= fim; i++) {
            int chave = v[i];
            int j = i - 1;
            while (j >= inicio && v[j] > chave) {
                v[j + 1] = v[j];
                j--;
            }
            v[j + 1] = chave;
        }
    }

    private static void trocar(int[] v, int i, int j) {
        int temp = v[i];
        v[i] = v[j];
        v[j] = temp;
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  UniFEF - Estrutura de Dados I - Prof. Wesley Soares");
        System.out.println("  Comparativo: Quick Sort Canônico vs. Quick Sort Otimizado");
        System.out.println("==========================================================\n");

        int N = 50000;
        int[] dados1 = new int[N];
        int[] dados2 = new int[N];
        Random gerador = new Random(42);

        for (int k = 0; k < N; k++) {
            int valor = gerador.nextInt(1000000);
            dados1[k] = valor;
            dados2[k] = valor;
        }

        System.out.println("Ordenando " + N + " inteiros aleatórios...");

        long tInicio1 = System.nanoTime();
        QuickSortLomuto.quickSort(dados1, 0, dados1.length - 1);
        long tFim1 = System.nanoTime();
        double tempoLomutoMs = (tFim1 - tInicio1) / 1e6;

        long tInicio2 = System.nanoTime();
        quickSortOtimizado(dados2, 0, dados2.length - 1);
        long tFim2 = System.nanoTime();
        double tempoOtimizadoMs = (tFim2 - tInicio2) / 1e6;

        System.out.printf("Tempo Quick Sort Canônico (Lomuto):   %.3f ms\n", tempoLomutoMs);
        System.out.printf("Tempo Quick Sort Otimizado (Híbrido):  %.3f ms\n", tempoOtimizadoMs);
        System.out.println("Vetores ordenados com sucesso e integridade validada: " + Arrays.equals(dados1, dados2));
    }
}
