/*
 * Estrutura de Dados I - Aula 01: Introdução a Algoritmos e Estrutura de Dados
 * Estudo de caso guiado: busca do maior elemento em uma lista.
 *
 * Ilustra as seis etapas de projeto de algoritmos vistas em aula:
 * entender o problema, definir entradas/saídas, escolher a estrutura,
 * escrever o algoritmo, verificar (casos de teste) e analisar (contagem de passos).
 *
 * Compilar/executar: javac MaiorElemento.java && java MaiorElemento
 */
public class MaiorElemento {

    /** Contador de comparações: mede o custo do algoritmo (base da análise de complexidade). */
    private static long comparacoes;

    /**
     * Algoritmo: percorre a lista uma única vez guardando o maior valor visto.
     * Entrada: vetor não vazio de inteiros. Saída: o maior valor.
     * Custo: n - 1 comparações -> O(n).
     */
    public static int maior(int[] valores) {
        // Característica "entrada definida": rejeita entradas inválidas em vez de produzir lixo.
        if (valores == null || valores.length == 0) {
            throw new IllegalArgumentException("A lista precisa ter ao menos um elemento");
        }
        int maior = valores[0];                 // passo 1: o primeiro é o maior até agora
        for (int i = 1; i < valores.length; i++) {
            comparacoes++;
            if (valores[i] > maior) {           // passo 2: compara com o maior conhecido
                maior = valores[i];             // passo 3: atualiza quando encontra um maior
            }
        }
        return maior;                           // "finitude": termina após n - 1 iterações
    }

    /** Verificação: executa o algoritmo e confere com o resultado esperado. */
    private static void testar(String nome, int[] entrada, int esperado) {
        comparacoes = 0;
        int obtido = maior(entrada);
        System.out.printf("%-28s -> maior = %4d | esperado = %4d | comparações = %d | %s%n",
                nome, obtido, esperado, comparacoes, obtido == esperado ? "OK" : "FALHOU");
    }

    public static void main(String[] args) {
        System.out.println("=== Busca do maior elemento (O(n)) ===");
        testar("maior no início", new int[] {90, 10, 20, 30}, 90);
        testar("maior no meio", new int[] {10, 95, 20, 30}, 95);
        testar("maior no fim", new int[] {10, 20, 30, 99}, 99);
        testar("todos negativos", new int[] {-8, -3, -15, -4}, -3);
        testar("um único elemento", new int[] {42}, 42);
        testar("valores repetidos", new int[] {7, 7, 7, 7}, 7);

        // Análise empírica: as comparações crescem linearmente com n.
        System.out.println("\n=== Crescimento do custo com o tamanho da entrada ===");
        for (int n = 10; n <= 100_000; n *= 10) {
            int[] v = new int[n];
            for (int i = 0; i < n; i++) v[i] = (i * 7919) % 1000;
            comparacoes = 0;
            maior(v);
            System.out.printf("n = %7d | comparações = %7d (n - 1)%n", n, comparacoes);
        }

        // Entrada inválida: o algoritmo deve recusar, não "inventar" uma resposta.
        try {
            maior(new int[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("\nLista vazia recusada: " + e.getMessage());
        }
    }
}
