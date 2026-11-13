/*
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Tema: Algoritmo de Ordenação Quick Sort (Particionamento de Lomuto)
 *
 * Como compilar:
 *   javac QuickSortLomuto.java
 * Como executar:
 *   java QuickSortLomuto
 */

public class QuickSortLomuto {

    /**
     * Método recursivo principal do Quick Sort.
     * Aplica o paradigma de Divisão e Conquista.
     *
     * @param vetor  Arranjo de inteiros a ser ordenado in-place
     * @param inicio Índice inicial da partição atual
     * @param fim    Índice final da partição atual
     */
    public static void quickSort(int[] vetor, int inicio, int fim) {
        // Condição de parada (caso base): partições com 0 ou 1 elemento
        if (inicio < fim) {
            // Fase de Divisão: rearranja os elementos e localiza a posição definitiva do pivô
            int indicePivo = particionar(vetor, inicio, fim);

            // Fase de Conquista: chamada recursiva para a partição à esquerda do pivô
            quickSort(vetor, inicio, indicePivo - 1);

            // Fase de Conquista: chamada recursiva para a partição à direita do pivô
            quickSort(vetor, indicePivo + 1, fim);

            // Fase de Combinação: trivial / custo O(1), pois a ordenação ocorre in-place
        }
    }

    /**
     * Particionamento de Lomuto.
     * Adota o último elemento (vetor[fim]) como pivô.
     * Mantém um ponteiro i para delimitar os elementos menores ou iguais ao pivô
     * e um ponteiro j para varrer a partição.
     *
     * @param vetor  Arranjo contendo os dados
     * @param inicio Índice de início da subfaixa
     * @param fim    Índice final (posição inicial do pivô)
     * @return Índice final definitivo onde o pivô foi alocado
     */
    public static int particionar(int[] vetor, int inicio, int fim) {
        int pivo = vetor[fim]; // Escolha do pivô no fim da partição
        int i = inicio - 1;    // Fronteira dos elementos menores ou iguais ao pivô

        // Varre do início até o penúltimo elemento da partição
        for (int j = inicio; j < fim; j++) {
            if (vetor[j] <= pivo) {
                i++;
                trocar(vetor, i, j);
            }
        }

        // Posiciona o pivô imediatamente após o último elemento menor que ele
        trocar(vetor, i + 1, fim);
        return i + 1; // Retorna o índice definitivo do pivô
    }

    /**
     * Método auxiliar para realizar a troca de valores entre duas posições do vetor.
     */
    private static void trocar(int[] vetor, int pos1, int pos2) {
        int temp = vetor[pos1];
        vetor[pos1] = vetor[pos2];
        vetor[pos2] = temp;
    }

    /**
     * Imprime o conteúdo do vetor no console de forma formatada.
     */
    public static void imprimirVetor(int[] vetor) {
        System.out.print("[");
        for (int k = 0; k < vetor.length; k++) {
            System.out.print(vetor[k]);
            if (k < vetor.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  UniFEF - Estrutura de Dados I - Prof. Wesley Soares");
        System.out.println("  Demonstração: Quick Sort Canônico (Lomuto)");
        System.out.println("==========================================================\n");

        int[] dados = {38, 27, 43, 3, 9, 82, 10};

        System.out.print("Vetor original (desordenado): ");
        imprimirVetor(dados);

        quickSort(dados, 0, dados.length - 1);

        System.out.print("Vetor final (ordenado):       ");
        imprimirVetor(dados);
        System.out.println("\nOrdenação concluída com sucesso via Quick Sort.");
    }
}
