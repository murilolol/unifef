/**
 * Disciplina: Estrutura de Dados I (4º Semestre) - UniFEF
 * Professor: Prof. Wesley Soares
 * Tema: Quick Sort - Resolução de Exercícios e Desafios
 *
 * Como executar:
 * javac Exercicios.java
 * java Exercicios
 */
import java.util.Random;

public class Exercicios {

    private static int totalParticoes = 0;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("ESTRUTURA DE DADOS I - AULA 14: QUICK SORT");
        System.out.println("Professor: Prof. Wesley Soares - UniFEF");
        System.out.println("====================================================\n");

        // Exercício 1: Questões teóricas e rastreamento da Parte A
        exercicio1();

        // Exercício 2: Implementação em Java com vetor proposto na Parte B
        exercicio2();

        // Exercício 3: Desafio de padrões e análise de complexidade O(n^2)
        exercicio3();

        // Exercício 4: Desafio 1 - Ordenação decrescente
        exercicio4();

        // Exercício 5: Desafio 2 - Pivô aleatório (Randomized Quick Sort)
        exercicio5();
    }

    /**
     * Exercício 1: Simulação Manual e Questões Conceituais
     */
    private static void exercicio1() {
        System.out.println("----------------------------------------------------");
        System.out.println("Exercício 1 - Gabarito e Explicação da Simulação Manual");
        System.out.println("Vetor sob análise: {29, 10, 14, 37, 13, 5, 22}");
        System.out.println("----------------------------------------------------");
        System.out.println("1. Primeiro pivô escolhido: 22 (último elemento do trecho, índice 6).");
        System.out.println("2. Rastreamento das trocas de Lomuto com pivô = 22:");
        System.out.println("   - 29 > 22  -> permanece na região dos maiores;");
        System.out.println("   - 10 <= 22 -> i avança para 0, troca 10 com 29 -> {10, 29, 14, 37, 13, 5, 22};");
        System.out.println("   - 14 <= 22 -> i avança para 1, troca 14 com 29 -> {10, 14, 29, 37, 13, 5, 22};");
        System.out.println("   - 37 > 22  -> permanece na região dos maiores;");
        System.out.println("   - 13 <= 22 -> i avança para 2, troca 13 com 29 -> {10, 14, 13, 37, 29, 5, 22};");
        System.out.println("   - 5 <= 22  -> i avança para 3, troca 5 com 37  -> {10, 14, 13, 5, 29, 37, 22};");
        System.out.println("   - Fim da varredura: troca pivô (22) com vetor[i+1] (29).");
        System.out.println("   - Vetor após 1ª partição: [10, 14, 13, 5] 22 [37, 29]");
        System.out.println("3. Índice definitivo do pivô: índice 4.");
        System.out.println("4. O pivô não participa das próximas chamadas recursivas porque já está na");
        System.out.println("   sua posição correta definitiva. As duas partições resultantes são independentes.");
        System.out.println("5. A recursão encerra quando inicio >= fim (trechos com 0 ou 1 elemento).\n");
    }

    /**
     * Exercício 2: Implementação da Atividade Prática
     */
    private static void exercicio2() {
        System.out.println("----------------------------------------------------");
        System.out.println("Exercício 2 - Execução com o Vetor Proposto");
        System.out.println("----------------------------------------------------");
        int[] valores = {29, 10, 14, 37, 13, 5, 22};

        System.out.print("Antes da ordenação: ");
        imprimirVetor(valores);

        quickSort(valores, 0, valores.length - 1);

        System.out.print("Depois da ordenação: ");
        imprimirVetor(valores);
        System.out.println();
    }

    /**
     * Exercício 3: Análise de Padrões e Pior Caso
     */
    private static void exercicio3() {
        System.out.println("----------------------------------------------------");
        System.out.println("Exercício 3 - Desafio: Análise de Padrões e Pior Caso");
        System.out.println("----------------------------------------------------");

        testarPadrao("Vetor Crescente (1 a 7)", new int[]{1, 2, 3, 4, 5, 6, 7});
        testarPadrao("Vetor Decrescente (7 a 1)", new int[]{7, 6, 5, 4, 3, 2, 1});
        testarPadrao("Vetor Repetidos (todos 5)", new int[]{5, 5, 5, 5, 5});

        System.out.println("Conclusão sobre o Pior Caso O(n²):");
        System.out.println("Quando o vetor já se encontra ordenado e escolhemos o último elemento como pivô,");
        System.out.println("o pivô é invariavelmente o maior valor do trecho. A divisão resulta em uma partição");
        System.out.println("com (n - 1) elementos e outra vazia (tamanho 0). A profundidade da recursão chega a n,");
        System.out.println("produzindo n * (n - 1) / 2 comparações, caracterizando a complexidade O(n²).\n");
    }

    private static void testarPadrao(String rotulo, int[] arr) {
        totalParticoes = 0;
        System.out.print(rotulo + " original: ");
        imprimirVetor(arr);
        quickSortComContador(arr, 0, arr.length - 1);
        System.out.print(rotulo + " ordenado: ");
        imprimirVetor(arr);
        System.out.println("Partições executadas: " + totalParticoes + "\n");
    }

    /**
     * Exercício 4: Quick Sort Decrescente
     */
    private static void exercicio4() {
        System.out.println("----------------------------------------------------");
        System.out.println("Exercício 4 - Desafio 1: Ordenação Decrescente");
        System.out.println("----------------------------------------------------");
        int[] valores = {29, 10, 14, 37, 13, 5, 22};

        System.out.print("Vetor original: ");
        imprimirVetor(valores);

        quickSortDecrescente(valores, 0, valores.length - 1);

        System.out.print("Vetor ordenado decrescente: ");
        imprimirVetor(valores);
        System.out.println();
    }

    /**
     * Exercício 5: Randomized Quick Sort
     */
    private static void exercicio5() {
        System.out.println("----------------------------------------------------");
        System.out.println("Exercício 5 - Desafio 2: Quick Sort com Pivô Aleatório");
        System.out.println("----------------------------------------------------");
        int[] valores = {29, 10, 14, 37, 13, 5, 22};

        System.out.print("Vetor original: ");
        imprimirVetor(valores);

        quickSortAleatorio(valores, 0, valores.length - 1);

        System.out.print("Vetor ordenado com pivô aleatório: ");
        imprimirVetor(valores);

        System.out.println("\nPor que o pivô aleatório não elimina matematicamente O(n²)?");
        System.out.println("A aleatoriedade desvincula o desempenho da ordem inicial dos dados de entrada.");
        System.out.println("No entanto, existe uma probabilidade não-nula (embora infinitesimal em vetores grandes)");
        System.out.println("de o gerador randômico sortear em cada nível de recursão exatamente o maior ou o menor");
        System.out.println("elemento do trecho, recaindo no mesmo particionamento desbalanceado de O(n²).\n");
    }

    // --- IMPLEMENTAÇÕES DOS ALGORITMOS ---

    public static void quickSort(int[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int indicePivo = particionar(vetor, inicio, fim);
            quickSort(vetor, inicio, indicePivo - 1);
            quickSort(vetor, indicePivo + 1, fim);
        }
    }

    private static int particionar(int[] vetor, int inicio, int fim) {
        int pivo = vetor[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            if (vetor[j] <= pivo) {
                i++;
                trocar(vetor, i, j);
            }
        }
        trocar(vetor, i + 1, fim);
        return i + 1;
    }

    public static void quickSortComContador(int[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            totalParticoes++;
            int indicePivo = particionar(vetor, inicio, fim);
            quickSortComContador(vetor, inicio, indicePivo - 1);
            quickSortComContador(vetor, indicePivo + 1, fim);
        }
    }

    public static void quickSortDecrescente(int[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int indicePivo = particionarDecrescente(vetor, inicio, fim);
            quickSortDecrescente(vetor, inicio, indicePivo - 1);
            quickSortDecrescente(vetor, indicePivo + 1, fim);
        }
    }

    private static int particionarDecrescente(int[] vetor, int inicio, int fim) {
        int pivo = vetor[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            // Inversão da condição: elementos maiores ou iguais ao pivô ficam à esquerda
            if (vetor[j] >= pivo) {
                i++;
                trocar(vetor, i, j);
            }
        }
        trocar(vetor, i + 1, fim);
        return i + 1;
    }

    public static void quickSortAleatorio(int[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int indicePivo = particionarAleatorio(vetor, inicio, fim);
            quickSortAleatorio(vetor, inicio, indicePivo - 1);
            quickSortAleatorio(vetor, indicePivo + 1, fim);
        }
    }

    private static int particionarAleatorio(int[] vetor, int inicio, int fim) {
        // Sorteia um índice aleatório dentro do trecho corrente
        int indiceSorteado = inicio + random.nextInt(fim - inicio + 1);
        // Troca o elemento sorteado com o último elemento para reutilizar o Lomuto padrão
        trocar(vetor, indiceSorteado, fim);
        return particionar(vetor, inicio, fim);
    }

    private static void trocar(int[] vetor, int i, int j) {
        int temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
    }

    private static void imprimirVetor(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + (i < vetor.length - 1 ? " " : ""));
        }
        System.out.println();
    }
}
