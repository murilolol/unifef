/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Complexidade de Algoritmos
 * 
 * Como executar:
 * 1. Compile o arquivo: javac ComplexidadeAlgoritmos.java
 * 2. Execute a classe: java ComplexidadeAlgoritmos
 */
public class ComplexidadeAlgoritmos {

    public static void main(String[] args) {
        int[] valores = {1, 2, 3, 4, 5, 5, 7, 8, 9, 10};

        System.out.println("--- Executando Métodos de Análise de Complexidade ---\n");

        // Exercício 1: Somar (Complexidade esperada: O(n))
        int soma = somar(valores);
        System.out.println("Exercício 1 - Soma: " + soma + " [Complexidade: O(n)]\n");

        // Exercício 2: Busca Binária (Complexidade esperada: O(log n))
        int indiceBusca = buscaBinaria(valores, 7);
        System.out.println("Exercício 2 - Busca Binária (procurando 7, index): " + indiceBusca + " [Complexidade: O(log n)]\n");

        // Exercício 3: Imprimir Pares (Complexidade esperada: O(n^2))
        System.out.println("Exercício 3 - Imprimir Pares [Complexidade: O(n^2)]:");
        imprimirPares(new int[]{1, 2, 3}); // Vetor menor para não poluir o console
        System.out.println();

        // Exercício 4: Contar Iguais (Complexidade esperada: O(n^2))
        int iguais = contarIguais(valores);
        System.out.println("Exercício 4 - Contar Iguais: " + iguais + " [Complexidade: O(n^2)]\n");

        // Exercício 5: Maior Elemento (Complexidade esperada: O(n))
        int maiorValor = maior(valores);
        System.out.println("Exercício 5 - Maior Elemento: " + maiorValor + " [Complexidade: O(n)]\n");

        // Exercício 6: Reduzir (Complexidade esperada: O(log n))
        System.out.println("Exercício 6 - Reduzir n=16 [Complexidade: O(log n)]:");
        reduzir(16);
    }

    // 1. Complexidade: O(n) - O laço percorre o vetor de tamanho n exatamente uma vez.
    public static int somar(int[] valores) {
        int soma = 0;
        for (int i = 0; i < valores.length; i++) {
            soma += valores[i];
        }
        return soma;
    }

    // 2. Complexidade: O(log n) - A cada iteração do laço, o espaço de busca é dividido pela metade.
    public static int buscaBinaria(int[] valores, int procurado) {
        int inicio = 0;
        int fim = valores.length - 1;

        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;

            if (valores[meio] == procurado) {
                return meio;
            }

            if (valores[meio] < procurado) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return -1;
    }

    // 3. Complexidade: O(n^2) - Possui dois laços aninhados, ambos iterando de 0 até n.
    public static void imprimirPares(int[] valores) {
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores.length; j++) {
                System.out.println(valores[i] + " - " + valores[j]);
            }
        }
    }

    // 4. Complexidade: O(n^2) - Embora o laço interno dependa de 'i', o número de iterações é n*(n-1)/2, o que mantém a ordem quadrática.
    public static int contarIguais(int[] valores) {
        int contador = 0;
        for (int i = 0; i < valores.length; i++) {
            for (int j = i + 1; j < valores.length; j++) {
                if (valores[i] == valores[j]) {
                    contador++;
                }
            }
        }
        return contador;
    }

    // 5. Complexidade: O(n) - O laço percorre o vetor linearmente a partir do segundo elemento.
    public static int maior(int[] valores) {
        int maior = valores[0];
        int i = 1;

        while (i < valores.length) {
            if (valores[i] > maior) {
                maior = valores[i];
            }
            i++;
        }
        return maior;
    }

    // 6. Complexidade: O(log n) - O valor de n é dividido sucessivamente por 2 até atingir o critério de parada.
    public static void reduzir(int n) {
        int passos = 0;
        while (n > 1) {
            System.out.println("n atual: " + n);
            n = n / 2;
            passos++;
        }
        System.out.println("Redução concluída em " + passos + " passos.");
    }
}
