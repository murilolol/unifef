/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Insertion Sort - Atividade Pratica e Desafio
 *
 * Como compilar e executar:
 * javac InsertionSortAtividade.java
 * java InsertionSortAtividade
 */
public class InsertionSortAtividade {

    /**
     * Exercicio 2 (Parte B): Implementacao do metodo ordenar em ordem crescente.
     *
     * @param vetor Vetor a ser ordenado
     */
    public static void ordenar(int[] vetor) {
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

    /**
     * Exercicio 4 (Desafio): Implementacao do metodo ordenar em ordem decrescente.
     *
     * @param vetor Vetor a ser ordenado em ordem decrescente
     */
    public static void ordenarDecrescente(int[] vetor) {
        for (int i = 1; i < vetor.length; i++) {
            int chave = vetor[i];
            int j = i - 1;

            // Inversao da condicao: vetor[j] < chave para posicionar os maiores no inicio
            while (j >= 0 && vetor[j] < chave) {
                vetor[j + 1] = vetor[j];
                j--;
            }

            vetor[j + 1] = chave;
        }
    }

    /**
     * Metodo auxiliar para imprimir o vetor formatado.
     *
     * @param vetor Vetor a ser impresso
     */
    public static void imprimirVetor(int[] vetor) {
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
        System.out.println("=== ATIVIDADE PRATICA - INSERTION SORT ===\n");

        // -------------------------------------------------------------
        // Exercicio 1: Parte A - Simulacao Manual
        // -------------------------------------------------------------
        System.out.println("--- Exercicio 1: Simulacao Manual (Passo a Passo) ---");
        System.out.println("Vetor inicial: [9, 4, 7, 2, 8, 3, 6]");
        System.out.println("Passo 0: Parte ordenada inicial: [9]");
        System.out.println("Passo 1 (Inserir 4): [4, 9, 7, 2, 8, 3, 6]");
        System.out.println("Passo 2 (Inserir 7): [4, 7, 9, 2, 8, 3, 6]");
        System.out.println("Passo 3 (Inserir 2): [2, 4, 7, 9, 8, 3, 6]");
        System.out.println("Passo 4 (Inserir 8): [2, 4, 7, 8, 9, 3, 6]");
        System.out.println("Passo 5 (Inserir 3): [2, 3, 4, 7, 8, 9, 6]");
        System.out.println("Passo 6 (Inserir 6): [2, 3, 4, 6, 7, 8, 9]");
        System.out.println("Vetor final ordenado: [2, 3, 4, 6, 7, 8, 9]\n");

        // -------------------------------------------------------------
        // Exercicio 2: Parte B - Implementacao e Execucao
        // -------------------------------------------------------------
        System.out.println("--- Exercicio 2: Execucao Pratica do Algoritmo ---");
        int[] notas = {9, 4, 7, 2, 8, 3, 6};

        System.out.print("Vetor antes da ordenacao: ");
        imprimirVetor(notas);

        ordenar(notas);

        System.out.print("Vetor depois da ordenacao crescente: ");
        imprimirVetor(notas);
        System.out.println();

        // -------------------------------------------------------------
        // Exercicio 3: Parte C - Analise Conceitual
        // -------------------------------------------------------------
        System.out.println("--- Exercicio 3: Respostas da Analise Conceitual ---");
        System.out.println("1. O laco for comeca em i = 1 porque um unico elemento isolado (indice 0) ja esta ordenado por definicao.");
        System.out.println("2. A variavel 'chave' preserva o elemento a ser inserido, permitindo que as posicoes anteriores sejam deslocadas.");
        System.out.println("3. O deslocamento evita realizar trocas completas (swaps), minimizando atribuicoes na memoria.");
        System.out.println("4. O melhor caso e O(n), quando o vetor ja esta ordenado e cada elemento requer apenas 1 comparacao sem deslocamentos.");
        System.out.println("5. O pior caso e O(n^2), quando o vetor esta em ordem inversa e cada novo elemento deve percorrer toda a particao ordenada.");
        System.out.println("6. A comparacao 'vetor[j] > chave' preserva a ordem relativa de valores iguais, conferindo estabilidade ao algoritmo.\n");

        // -------------------------------------------------------------
        // Exercicio 4: Desafio - Ordenacao Decrescente
        // -------------------------------------------------------------
        System.out.println("--- Exercicio 4: Desafio (Ordem Decrescente) ---");
        int[] notasDesafio = {9, 4, 7, 2, 8, 3, 6};

        System.out.print("Vetor original para o desafio: ");
        imprimirVetor(notasDesafio);

        ordenarDecrescente(notasDesafio);

        System.out.print("Vetor ordenado decrescente: ");
        imprimirVetor(notasDesafio);
    }
}
