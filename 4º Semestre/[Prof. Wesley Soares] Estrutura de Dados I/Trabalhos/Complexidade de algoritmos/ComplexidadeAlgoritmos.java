import java.util.Arrays;
import java.util.Random;

/**
 * ============================================================================
 * TRABALHO PRÁTICO: Complexidade de Algoritmos
 * Disciplina: Estrutura de Dados I (4º Semestre - Sistemas de Informação)
 * Professor: Prof. Wesley Soares
 * 
 * Descrição:
 * Esta classe implementa, testa e analisa empiricamente a complexidade de tempo 
 * de diferentes algoritmos de busca e ordenação, demonstrando a aplicação prática 
 * da Notação Big-O ($O(n)$, $O(\log n)$, $O(n^2)$, $O(n \log n)$).
 * ============================================================================
 */
public class ComplexidadeAlgoritmos {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" FACULDADE DE SISTEMAS DE INFORMACAO");
        System.out.println(" Disciplina: Estrutura de Dados I");
        System.out.println(" Professor: Prof. Wesley Soares");
        System.out.println(" Atividade: Complexidade de Algoritmos");
        System.out.println("==================================================\n");

        // 1. Demonstração Teórica e Prática de Buscas
        executarTestesBusca();

        // 2. Demonstração Teórica e Prática de Ordenações
        executarTestesOrdenacao();
        
        // 3. Análise de Crescimento Assintótico (Big-O)
        executarAnaliseEscalabilidade();
    }

    /**
     * =================================================================U
     * 1. ALGORITMOS DE BUSCA
     * =================================================================
     */

    /**
     * Busca Linear: O(n) no pior caso.
     * Percorre o vetor elemento por elemento sequencialmente.
     */
    public static int buscaLinear(int[] vetor, int valor) {
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] == valor) {
                return i; // Retorna o índice onde foi encontrado
            }
        }
        return -1; // Não encontrado
    }

    /**
     * Busca Binária: O(log n) no pior caso.
     * Requer o vetor previamente ordenado. Divide o espaço de busca ao meio a cada passo.
     */
    public static int buscaBinaria(int[] vetorOrdenado, int valor) {
        int inicio = 0;
        int fim = vetorOrdenado.length - 1;

        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;

            if (vetorOrdenado[meio] == valor) {
                return meio;
            }
            if (vetorOrdenado[meio] < valor) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return -1;
    }

    /**
     * =================================================================
     * 2. ALGORITMOS DE ORDENAÇÃO
     * =================================================================
     */

    /**
     * Bubble Sort: O(n^2) no pior e caso médio.
     * Compara elementos adjacentes e os troca se estiverem na ordem errada.
     */
    public static void bubbleSort(int[] vetor) {
        int n = vetor.length;
        boolean trocou;
        for (int i = 0; i < n - 1; i++) {
            trocou = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (vetor[j] > vetor[j + 1]) {
                    // Troca
                    int temp = vetor[j];
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temp;
                    trocou = true;
                }
            }
            // Otimização: se não houve trocas, o vetor já está ordenado
            if (!trocou) break;
        }
    }

    /**
     * Merge Sort: O(n log n) em todos os casos.
     * Utiliza a estratégia de Divisão e Conquista.
     */
    public static void mergeSort(int[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;

            mergeSort(vetor, inicio, meio);
            mergeSort(vetor, meio + 1, fim);

            merge(vetor, inicio, meio, fim);
        }
    }

    private static void merge(int[] vetor, int inicio, int meio, int fim) {
        int n1 = meio - inicio + 1;
        int n2 = fim - meio;

        int[] esq = new int[n1];
        int[] dir = new int[n2];

        for (int i = 0; i < n1; ++i)
            esq[i] = vetor[inicio + i];
        for (int j = 0; j < n2; ++j)
            dir[j] = vetor[meio + 1 + j];

        int i = 0, j = 0;
        int k = inicio;

        while (i < n1 && j < n2) {
            if (esq[i] <= dir[j]) {
                vetor[k] = esq[i];
                i++;
            } else {
                vetor[k] = dir[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            vetor[k] = esq[i];
            i++;
            k++;
        }

        while (j < n2) {
            vetor[k] = dir[j];
            j++;
            k++;
        }
    }

    /**
     * =================================================================
     * 3. BATERIA DE TESTES E MEDIÇÃO DE DESEMPENHO
     * =================================================================
     */

    public static void executarTestesBusca() {
        System.out.println("--- 1. TESTES DE ALGORITMOS DE BUSCA ---");
        int tamanho = 1000000; // 1 milhão de elementos
        int[] dados = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            dados[i] = i * 2; // Valores pares ordenados
        }

        int valorProcurado = 150042;

        // Teste Busca Linear O(n)
        long inicioTempo = System.nanoTime();
        int idxLinear = buscaLinear(dados, valorProcurado);
        long fimTempo = System.nanoTime();
        double tempoLinearMs = (fimTempo - inicioTempo) / 1e6;

        // Teste Busca Binária O(log n)
        inicioTempo = System.nanoTime();
        int idxBinaria = buscaBinaria(dados, valorProcurado);
        fimTempo = System.nanoTime();
        double tempoBinariaMs = (fimTempo - inicioTempo) / 1e6;

        System.out.println("Tamanho da base de dados: " + tamanho + " elementos");
        System.out.println("Busca Linear -> Índice: " + idxLinear + " | Tempo: " + tempoLinearMs + " ms");
        System.out.println("Busca Binária -> Índice: " + idxBinaria + " | Tempo: " + tempoBinariaMs + " ms\n");
    }

    public static void executarTestesOrdenacao() {
        System.out.println("--- 2. TESTES DE ALGORITMOS DE ORDENAÇÃO ---");
        int tamanho = 20000; // 20 mil elementos (valor seguro para O(n^2))
        
        int[] vetorOriginal = gerarVetorAleatorio(tamanho);
        
        // Copiando o vetor para testes justos
        int[] vetorBubble = vetorOriginal.clone();
        int[] vetorMerge = vetorOriginal.clone();

        // Teste Bubble Sort O(n^2)
        long inicioTempo = System.nanoTime();
        bubbleSort(vetorBubble);
        long fimTempo = System.nanoTime();
        double tempoBubbleMs = (fimTempo - inicioTempo) / 1e6;

        // Teste Merge Sort O(n log n)
        inicioTempo = System.nanoTime();
        mergeSort(vetorMerge, 0, vetorMerge.length - 1);
        fimTempo = System.nanoTime();
        double tempoMergeMs = (fimTempo - inicioTempo) / 1e6;

        System.out.println("Tamanho do vetor não ordenado: " + tamanho + " elementos");
        System.out.println("Bubble Sort O(n^2)     -> Tempo: " + tempoBubbleMs + " ms");
        System.out.println("Merge Sort  O(n log n) -> Tempo: " + tempoMergeMs + " ms\n");
    }

    public static void executarAnaliseEscalabilidade() {
        System.out.println("--- 3. ANÁLISE DE ESCALABILIDADE (COMPLEXIDADE ESPACIAL E TEMPORAL) ---");
        System.out.println("Ordem de Crescimento Teórica vs Prática:");
        System.out.println("|- N       | O(1) [Constante] | O(log n) [Logarítmico] | O(n) [Linear] | O(n log n) [Linearítmico] | O(n^2) [Quadrático] |");
        System.out.println("|----------|------------------|------------------------|---------------|---------------------------|---------------------|");
        
        int[] amostras = { 1000, 10000, 50000 };
        for (int n : amostras) {
            long operacoesO1 = 1;
            long operacoesOlogN = (long) (Math.log(n) / Math.log(2));
            long operacoesOn = n;
            long operacoesOnLogN = (long) (n * (Math.log(n) / Math.log(2)));
            long operacoesOn2 = (long) n * n;

            System.out.printf("| %-8d | %-16d | %-22d | %-13d | %-25d | %-19d |\n", 
                n, operacoesO1, operacoesOlogN, operacoesOn, operacoesOnLogN, operacoesOn2);
        }
        System.out.println("==================================================");
        System.out.println("Fim da execução da atividade de Complexidade de Algoritmos.");
    }

    private static int[] gerarVetorAleatorio(int tamanho) {
        Random rand = new Random(42); // Seed fixa para reprodutibilidade
        int[] vetor = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            vetor[i] = rand.nextInt(tamanho * 10);
        }
        return vetor;
    }
}