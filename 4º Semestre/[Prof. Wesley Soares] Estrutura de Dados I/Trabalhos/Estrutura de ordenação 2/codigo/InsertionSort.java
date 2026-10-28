/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Instituição: Centro Universitário de Santa Fé do Sul (UniFEF)
 * Professor: Prof. Wesley Soares
 * Tema: Implementação Didática, Rastreamento Passo a Passo e Análise Empírica do Insertion Sort
 *
 * Como compilar:
 *   javac InsertionSort.java
 * Como executar:
 *   java InsertionSort
 */

import java.util.Arrays;

public class InsertionSort {

    /**
     * Classe auxiliar para encapsular os resultados da análise empírica.
     */
    public static class Metricas {
        public long comparacoes = 0;
        public long escritas = 0;
        public long tempoNanos = 0;
    }

    /**
     * Implementação clássica e eficiente do Insertion Sort utilizando deslocamento.
     *
     * @param vetor Vetor de inteiros a ser ordenado in-place.
     */
    public static void ordenar(int[] vetor) {
        if (vetor == null || vetor.length <= 1) {
            return;
        }

        int n = vetor.length;
        for (int i = 1; i < n; i++) {
            int chave = vetor[i];
            int j = i - 1;

            // Desloca os elementos maiores que a chave uma posição para a direita
            while (j >= 0 && vetor[j] > chave) {
                vetor[j + 1] = vetor[j];
                j--;
            }
            // Insere a chave na posição correta encontrada
            vetor[j + 1] = chave;
        }
    }

    /**
     * Implementação didática com impressão detalhada do estado do vetor em cada iteração.
     *
     * @param vetor Vetor a ser ordenado com visualização passo a passo.
     */
    public static void ordenarComRastreamento(int[] vetor) {
        System.out.println("Estado Inicial: " + Arrays.toString(vetor));
        System.out.println("---------------------------------------------------------------------");

        int n = vetor.length;
        for (int i = 1; i < n; i++) {
            int chave = vetor[i];
            int j = i - 1;
            System.out.printf("Iteração i = %d | Chave = %2d | Sublista já ordenada: %s%n",
                    i, chave, Arrays.toString(Arrays.copyOfRange(vetor, 0, i)));

            int deslocamentosNaIteracao = 0;
            while (j >= 0 && vetor[j] > chave) {
                System.out.printf("   -> vetor[%d] (%d) > chave (%d): deslocando %d para o índice %d%n",
                        j, vetor[j], chave, vetor[j], j + 1);
                vetor[j + 1] = vetor[j];
                j--;
                deslocamentosNaIteracao++;
            }
            vetor[j + 1] = chave;

            if (deslocamentosNaIteracao == 0) {
                System.out.println("   -> Nenhum elemento maior que a chave: permanece no mesmo índice.");
            } else {
                System.out.printf("   -> Chave %d inserida no índice %d.%n", chave, j + 1);
            }

            System.out.println("   Vetor parcial: " + Arrays.toString(vetor));
            System.out.println();
        }
        System.out.println("Vetor Final Ordenado: " + Arrays.toString(vetor));
    }

    /**
     * Versão instrumentada do Insertion Sort clássico (com deslocamentos).
     */
    public static Metricas ordenarInstrumentado(int[] vetor) {
        Metricas m = new Metricas();
        long inicio = System.nanoTime();

        int n = vetor.length;
        for (int i = 1; i < n; i++) {
            int chave = vetor[i];
            m.escritas++; // Leitura para variável local chave
            int j = i - 1;

            while (j >= 0) {
                m.comparacoes++;
                if (vetor[j] > chave) {
                    vetor[j + 1] = vetor[j];
                    m.escritas++; // Deslocamento
                    j--;
                } else {
                    break;
                }
            }
            vetor[j + 1] = chave;
            m.escritas++; // Escrita final da chave
        }

        m.tempoNanos = System.nanoTime() - inicio;
        return m;
    }

    /**
     * Versão ingênua do Insertion Sort implementada com trocas consecutivas (swaps).
     * Utilizada para comprovar a diferença solicitada na Questão 6.
     */
    public static Metricas ordenarComTrocasSucessivas(int[] vetor) {
        Metricas m = new Metricas();
        long inicio = System.nanoTime();

        int n = vetor.length;
        for (int i = 1; i < n; i++) {
            int j = i;
            while (j > 0) {
                m.comparacoes++;
                if (vetor[j - 1] > vetor[j]) {
                    // Troca (swap) completa usando 3 escritas
                    int temp = vetor[j];
                    vetor[j] = vetor[j - 1];
                    vetor[j - 1] = temp;
                    m.escritas += 3;
                    j--;
                } else {
                    break;
                }
            }
        }

        m.tempoNanos = System.nanoTime() - inicio;
        return m;
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("      DEMONSTRAÇÃO PRÁTICA DO ALGORITMO INSERTION SORT (JAVA)          ");
        System.out.println("========================================================================\n");

        // Parte 1: Rastreamento detalhado passo a passo
        System.out.println("[PARTE 1] Rastreamento Passo a Passo de um Vetor Pequeno:");
        int[] dadosExemplo = { 29, 10, 14, 37, 13 };
        ordenarComRastreamento(dadosExemplo);
        System.out.println("\n========================================================================\n");

        // Parte 2: Análise Comparativa dos Três Casos de Complexidade
        System.out.println("[PARTE 2] Análise Empírica dos Cenários de Complexidade:");
        int tamanho = 1000;

        // 1. Melhor caso: vetor já ordenado
        int[] melhorCaso = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            melhorCaso[i] = i;
        }
        Metricas metMelhor = ordenarInstrumentado(melhorCaso.clone());
        System.out.printf("1. Melhor Caso (Vetor Ordenado, N = %d):%n", tamanho);
        System.out.printf("   Comparações: %d (esperado linear: ~%d)%n", metMelhor.comparacoes, tamanho - 1);
        System.out.printf("   Escritas:    %d%n", metMelhor.escritas);
        System.out.printf("   Tempo:       %.3f ms%n%n", metMelhor.tempoNanos / 1e6);

        // 2. Pior caso: vetor em ordem decrescente (inversa)
        int[] piorCaso = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            piorCaso[i] = tamanho - i;
        }
        Metricas metPior = ordenarInstrumentado(piorCaso.clone());
        long comparacoesEsperadasPior = ((long) tamanho * (tamanho - 1)) / 2;
        System.out.printf("2. Pior Caso (Vetor Invertido, N = %d):%n", tamanho);
        System.out.printf("   Comparações: %d (esperado quadrático: %d)%n", metPior.comparacoes, comparacoesEsperadasPior);
        System.out.printf("   Escritas:    %d%n", metPior.escritas);
        System.out.printf("   Tempo:       %.3f ms%n%n", metPior.tempoNanos / 1e6);

        // 3. Caso Médio: vetor aleatório
        int[] casoMedio = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            casoMedio[i] = (int) (Math.random() * 10000);
        }
        Metricas metMedio = ordenarInstrumentado(casoMedio.clone());
        System.out.printf("3. Caso Médio (Vetor Aleatório, N = %d):%n", tamanho);
        System.out.printf("   Comparações: %d%n", metMedio.comparacoes);
        System.out.printf("   Escritas:    %d%n", metMedio.escritas);
        System.out.printf("   Tempo:       %.3f ms%n%n", metMedio.tempoNanos / 1e6);

        System.out.println("========================================================================\n");

        // Parte 3: Comparação entre Deslocamento (Canônico) e Trocas Sucessivas (Ingênuo)
        System.out.println("[PARTE 3] Comparação de Eficiência: Deslocamento vs. Trocas (Swaps):");
        int[] testeDeslocamento = piorCaso.clone();
        int[] testeTrocas = piorCaso.clone();

        Metricas mDeslocamento = ordenarInstrumentado(testeDeslocamento);
        Metricas mTrocas = ordenarComTrocasSucessivas(testeTrocas);

        System.out.println("Cenário: Vetor Decrescente (N = 1000)");
        System.out.println("Versão Canônica (Deslocamento):");
        System.out.printf("  - Operações de escrita: %d%n", mDeslocamento.escritas);
        System.out.printf("  - Tempo de execução:   %.3f ms%n", mDeslocamento.tempoNanos / 1e6);
        System.out.println("Versão com Trocas Sucessivas (Swaps):");
        System.out.printf("  - Operações de escrita: %d%n", mTrocas.escritas);
        System.out.printf("  - Tempo de execução:   %.3f ms%n", mTrocas.tempoNanos / 1e6);
        System.out.printf("Conclusão: A abordagem por deslocamento economiza %d escritas em memória!%n",
                mTrocas.escritas - mDeslocamento.escritas);
        System.out.println("========================================================================");
    }
}
