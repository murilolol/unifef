/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Resolução Completa dos Exercícios - Merge Sort
 *
 * Como compilar e executar:
 *   javac Exercicios.java
 *   java Exercicios
 */
public class Exercicios {

    // Implementação auxiliar do Insertion Sort para comparação prática
    public static void insertionSort(int[] vetor) {
        for (int i = 1; i < vetor.length; i++) {
            int chave = vetor[i];
            int j = i - 1;
            while (j >= 0 && vetor[j] > chave) {
                vetor[j + 1] = vetor[j];
                j--;
            }
            vetor[j + 1] = chave;
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  UniFEF - Estrutura de Dados I (4º Semestre) - Prof. Wesley Soares");
        System.out.println("  Gabarito e Demonstração Prática dos Exercícios de Merge Sort");
        System.out.println("========================================================================\n");

        // --------------------------------------------------------------------
        // Exercício 1
        // --------------------------------------------------------------------
        System.out.println("--- [Exercício 1] ---");
        System.out.println("Enunciado: Qual é a finalidade de dividir o vetor em partes menores?");
        System.out.println("Resposta:");
        System.out.println("A finalidade é aplicar a estratégia de Divisão e Conquista. Ao quebrar");
        System.out.println("um vetor grande sucessivamente pela metade, reduz-se a complexidade do");
        System.out.println("problema original até alcançar subproblemas triviais (vetores com 1 ou 0");
        System.out.println("elementos), os quais, por definição matemática, já estão ordenados.");
        System.out.println();

        // --------------------------------------------------------------------
        // Exercício 2
        // --------------------------------------------------------------------
        System.out.println("--- [Exercício 2] ---");
        System.out.println("Enunciado: Por que a recursão precisa de um caso base?");
        System.out.println("Resposta:");
        System.out.println("O caso base (inicio >= fim) é essencial para determinar o momento de parada");
        System.out.println("das chamadas recursivas na pilha de execução. Sem o caso base, o algoritmo");
        System.out.println("entraria em recursão infinita, resultando em estouro da pilha de memória");
        System.out.println("(StackOverflowError). É o caso base que sinaliza o início do retorno e a");
        System.out.println("fase de combinação (intercalação).");
        System.out.println();

        // --------------------------------------------------------------------
        // Exercício 3
        // --------------------------------------------------------------------
        System.out.println("--- [Exercício 3] ---");
        System.out.println("Enunciado: Em que momento ocorre a ordenação efetiva dos elementos?");
        System.out.println("Resposta:");
        System.out.println("A ordenação efetiva NÃO ocorre durante a fase de divisão, mas sim durante a");
        System.out.println("fase de intercalação (merge). É no método de intercalação que os elementos");
        System.out.println("dos dois subvetores adjacentes são comparados par a par e copiados de forma");
        System.out.println("crescente para o vetor auxiliar.");
        System.out.println();

        // --------------------------------------------------------------------
        // Exercício 4
        // --------------------------------------------------------------------
        System.out.println("--- [Exercício 4] ---");
        System.out.println("Enunciado: Qual é a função do vetor auxiliar?");
        System.out.println("Resposta:");
        System.out.println("O vetor auxiliar atua como uma área temporária de armazenamento durante o");
        System.out.println("processo de intercalação. Ele impede que elementos ainda não processados do");
        System.out.println("vetor original sejam sobrescritos acidentalmente enquanto a combinação ordenada");
        System.out.println("das duas metades é construída.");
        System.out.println();

        // --------------------------------------------------------------------
        // Exercício 5
        // --------------------------------------------------------------------
        System.out.println("--- [Exercício 5] ---");
        System.out.println("Enunciado: Por que a complexidade do Merge Sort é O(n log n)?");
        System.out.println("Resposta:");
        System.out.println("Porque a árvore de recursão possui profundidade log2(n), correspondente ao");
        System.out.println("número de vezes que o vetor é dividido pela metade. Em cada um dos log2(n)");
        System.out.println("níveis dessa árvore, a intercalação percorre todos os n elementos em tempo");
        System.out.println("linear O(n). Multiplicando a profundidade pelo trabalho por nível, obtém-se");
        System.out.println("a complexidade determinística O(n log n) nos melhores, piores e médios casos.");
        System.out.println();

        // --------------------------------------------------------------------
        // Exercício 6
        // --------------------------------------------------------------------
        System.out.println("--- [Exercício 6] ---");
        System.out.println("Enunciado: Qual é a principal desvantagem do Merge Sort em relação ao Insertion Sort?");
        System.out.println("Resposta:");
        System.out.println("A principal desvantagem é o consumo de memória adicional: o Merge Sort exige");
        System.out.println("espaço auxiliar O(n) para a intercalação, enquanto o Insertion Sort é in-place");
        System.out.println("(requer O(1) de memória extra). Além disso, para vetores pequenos ou quase");
        System.out.println("totalmente ordenados, o Insertion Sort apresenta menor overhead de execução");
        System.out.println("e atinge desempenho linear O(n) no melhor caso.");
        System.out.println();

        // Demonstração prática comparativa
        System.out.println("========================================================================");
        System.out.println("  Demonstração Prática Comparativa: Merge Sort vs Insertion Sort");
        System.out.println("========================================================================");

        int[] testeMerge = { 45, 12, 89, 34, 70, 23, 11, 6 };
        int[] testeInsertion = testeMerge.clone();

        System.out.print("Vetor desordenado: ");
        MergeSort.exibirVetor(testeMerge);

        MergeSort.ordenar(testeMerge);
        System.out.print("Resultado Merge Sort (O(n log n), espaço O(n)): ");
        MergeSort.exibirVetor(testeMerge);

        insertionSort(testeInsertion);
        System.out.print("Resultado Insertion Sort (in-place, espaço O(1)): ");
        MergeSort.exibirVetor(testeInsertion);
    }
}
