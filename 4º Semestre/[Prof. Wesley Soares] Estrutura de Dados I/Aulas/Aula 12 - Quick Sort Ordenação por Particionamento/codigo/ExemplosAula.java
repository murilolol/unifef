/**
 * Disciplina: Estrutura de Dados I (4º Semestre) - UniFEF
 * Professor: Prof. Wesley Soares
 * Tema: Quick Sort - Ordenação por Particionamento
 *
 * Como executar:
 * javac ExemplosAula.java
 * java ExemplosAula
 */
public class ExemplosAula {

    /**
     * Método principal de ordenação recursiva do Quick Sort.
     * Define as fronteiras e invoca as chamadas recursivas para as partições.
     */
    public static void ordenar(int[] vetor, int inicio, int fim) {
        // Condição de parada: executa apenas enquanto houver 2 ou mais elementos
        if (inicio < fim) {
            // Organiza os elementos em torno do pivô e obtém seu índice final
            int indicePivo = particionar(vetor, inicio, fim);

            // Ordena recursivamente a sub-região à esquerda do pivô
            ordenar(vetor, inicio, indicePivo - 1);

            // Ordena recursivamente a sub-região à direita do pivô
            ordenar(vetor, indicePivo + 1, fim);
        }
    }

    /**
     * Particionamento de Lomuto:
     * Utiliza o último elemento como pivô e varre o trecho com os índices i e j.
     */
    private static int particionar(int[] vetor, int inicio, int fim) {
        int pivo = vetor[fim];
        int i = inicio - 1; // Limite da fronteira de elementos menores ou iguais ao pivô

        for (int j = inicio; j < fim; j++) {
            if (vetor[j] <= pivo) {
                i++;
                // Troca vetor[i] com vetor[j] para expandir a região dos menores
                int temporario = vetor[i];
                vetor[i] = vetor[j];
                vetor[j] = temporario;
            }
        }

        // Posiciona o pivô entre os elementos menores e maiores
        int temporario = vetor[i + 1];
        vetor[i + 1] = vetor[fim];
        vetor[fim] = temporario;

        return i + 1; // Retorna o índice definitivo do pivô
    }

    public static void main(String[] args) {
        int[] numeros = {15, 8, 23, 4, 16, 42, 7, 10};

        System.out.println("Antes da ordenação:");
        imprimirVetor(numeros);

        ordenar(numeros, 0, numeros.length - 1);

        System.out.println("Depois da ordenação:");
        imprimirVetor(numeros);
    }

    private static void imprimirVetor(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + (i < vetor.length - 1 ? " " : ""));
        }
        System.out.println();
    }
}
