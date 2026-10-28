/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Instituição: Centro Universitário de Santa Fé do Sul (UniFEF)
 * Professor: Prof. Wesley Soares
 * Tema: Resolução do Questionário de Estrutura de Ordenação 2 (Insertion Sort)
 *
 * Como compilar:
 *   javac Exercicios.java
 * Como executar:
 *   java Exercicios
 */

import java.util.Arrays;

public class Exercicios {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("   ESTRUTURA DE DADOS I - RESOLUÇÃO DO QUESTIONÁRIO: INSERTION SORT    ");
        System.out.println("   Professor: Prof. Wesley Soares | Aluno(a): Sistemas de Informação   ");
        System.out.println("========================================================================\n");

        exercicio1();
        exercicio2();
        exercicio3();
        exercicio4();
        exercicio5();
        exercicio6();
    }

    /**
     * Exercício 1
     * Enunciado: Por que o laço externo começa em i = 1?
     */
    public static void exercicio1() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Exercício 1: Por que o laço externo começa em i = 1?");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Resposta Teórica:");
        System.out.println("O Insertion Sort baseia-se na manutenção de uma sublista ordenada à esquerda");
        System.out.println("do vetor (invariante de laço). Um vetor unitário contendo apenas o primeiro");
        System.out.println("elemento (índice 0) é considerado trivialmente ordenado por definição.");
        System.out.println("Portanto, o algoritmo deve iniciar a verificação a partir do segundo elemento");
        System.out.println("(índice 1), comparando-o com a sublista já ordenada [0..i-1] para posicioná-lo");
        System.out.println("no local correto. Iniciar em i = 0 seria redundante e causaria tentativas");
        System.out.println("inválidas de acessar o índice j = -1.\n");

        System.out.println("Demonstração Prática:");
        int[] vetor = { 42, 17, 8 };
        System.out.println("Vetor inicial: " + Arrays.toString(vetor));
        System.out.println("Índice i = 0: Sublista [" + vetor[0] + "] já é considerada ordenada.");
        System.out.println("Início real do laço externo em i = 1: elemento " + vetor[1] + " será inserido.");
        System.out.println();
    }

    /**
     * Exercício 2
     * Enunciado: Qual é a função da variável chave?
     */
    public static void exercicio2() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Exercício 2: Qual é a função da variável chave?");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Resposta Teórica:");
        System.out.println("A variável 'chave' (key) armazena temporariamente o valor do elemento sob");
        System.out.println("avaliação (vetor[i]) antes que os elementos maiores que ele sejam deslocados");
        System.out.println("para a direita. Como a operação de deslocamento sobrescreve posições consecutivas");
        System.out.println("(vetor[j + 1] = vetor[j]), sem a variável chave o valor original do elemento");
        System.out.println("seria irremediavelmente perdido. Ao final do laço interno, a chave é copiada");
        System.out.println("para a lacuna aberta na posição correta (vetor[j + 1] = chave).\n");

        System.out.println("Demonstração Prática:");
        int[] vetor = { 25, 50, 10 };
        int i = 2;
        int chave = vetor[i];
        System.out.println("Vetor antes do deslocamento: " + Arrays.toString(vetor));
        System.out.println("Valor salvo na variável chave: " + chave);
        vetor[2] = vetor[1]; // Deslocamento do 50 para o índice 2 (sobrescrevendo o 10 original)
        System.out.println("Após deslocamento do 50 para a direita: " + Arrays.toString(vetor));
        vetor[1] = vetor[0]; // Deslocamento do 25 para o índice 1
        System.out.println("Após deslocamento do 25 para a direita: " + Arrays.toString(vetor));
        vetor[0] = chave;   // Restauração da chave na posição vaga (índice 0)
        System.out.println("Após inserir a chave no índice 0: " + Arrays.toString(vetor));
        System.out.println();
    }

    /**
     * Exercício 3
     * Enunciado: Por que os elementos maiores que a chave são deslocados para a direita?
     */
    public static void exercicio3() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Exercício 3: Por que os elementos maiores que a chave são deslocados para a direita?");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Resposta Teórica:");
        System.out.println("Os elementos maiores que a chave precisam ocupar posições posteriores à chave");
        System.out.println("para garantir que a relação de ordem crescente seja satisfeita. O deslocamento");
        System.out.println("para a direita (vetor[j + 1] = vetor[j]) transfere os elementos uma posição à");
        System.out.println("frente, abrindo a lacuna exata no índice j + 1 onde a chave é menor ou igual ao");
        System.out.println("seu sucessor e maior que o seu antecessor.\n");

        System.out.println("Demonstração Prática:");
        int[] ordenadoAteAgora = { 10, 20, 30, 40, 0 }; // 0 é a posição que receberá o elemento
        int novaChave = 25;
        ordenadoAteAgora[4] = novaChave;
        System.out.println("Sublista ordenada com novo elemento no final: " + Arrays.toString(ordenadoAteAgora));
        System.out.println("Elemento 40 > 25: deslocado do índice 3 para o índice 4.");
        ordenadoAteAgora[4] = ordenadoAteAgora[3];
        System.out.println("Elemento 30 > 25: deslocado do índice 2 para o índice 3.");
        ordenadoAteAgora[3] = ordenadoAteAgora[2];
        System.out.println("Elemento 20 <= 25: condição de parada satisfeita. Vaga aberta no índice 2.");
        ordenadoAteAgora[2] = novaChave;
        System.out.println("Resultado final da inserção: " + Arrays.toString(ordenadoAteAgora));
        System.out.println();
    }

    /**
     * Exercício 4
     * Enunciado: O que acontece quando o vetor já está ordenado?
     */
    public static void exercicio4() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Exercício 4: O que acontece quando o vetor já está ordenado?");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Resposta Teórica:");
        System.out.println("Quando o vetor já se encontra previamente ordenado em ordem crescente, o algoritmo");
        System.out.println("atinge o seu MELHOR CASO de desempenho:");
        System.out.println("1. O laço externo executa normalmente n - 1 vezes (de i = 1 até n - 1).");
        System.out.println("2. Em cada iteração, o teste do laço interno (vetor[j] > chave) falha logo na");
        System.out.println("   primeira comparação (j = i - 1), pois o elemento anterior já é menor que a chave.");
        System.out.println("3. Nenhum deslocamento de elementos é realizado (zero movimentações internas).");
        System.out.println("4. O número total de comparações é exatamente n - 1, resultando em complexidade");
        System.out.println("   estritamente linear: O(n).\n");

        System.out.println("Demonstração Prática com Contadores:");
        int[] vetorJaOrdenado = { 10, 20, 30, 40, 50 };
        int n = vetorJaOrdenado.length;
        int comparacoes = 0;
        int deslocamentos = 0;

        for (int i = 1; i < n; i++) {
            int chave = vetorJaOrdenado[i];
            int j = i - 1;
            // Primeira verificação da condição do laço
            comparacoes++;
            while (j >= 0 && vetorJaOrdenado[j] > chave) {
                deslocamentos++;
                vetorJaOrdenado[j + 1] = vetorJaOrdenado[j];
                j--;
                if (j >= 0) {
                    comparacoes++;
                }
            }
            vetorJaOrdenado[j + 1] = chave;
        }

        System.out.println("Tamanho do vetor (n): " + n);
        System.out.println("Total de comparações realizadas: " + comparacoes + " (n - 1)");
        System.out.println("Total de deslocamentos realizados: " + deslocamentos);
        System.out.println("Complexidade comprovada: Linear O(n)");
        System.out.println();
    }

    /**
     * Exercício 5
     * Enunciado: Qual é a complexidade do algoritmo no pior caso? Justifique.
     */
    public static void exercicio5() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Exercício 5: Qual é a complexidade do algoritmo no pior caso? Justifique.");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Resposta Teórica:");
        System.out.println("A complexidade no pior caso é O(n²) [quadrática].");
        System.out.println("Justificativa:");
        System.out.println("O pior caso ocorre quando o vetor está em ordem estritamente decrescente.");
        System.out.println("Para cada elemento no índice i (de 1 até n - 1), a chave é estritamente menor");
        System.out.println("que todos os i elementos anteriores. Portanto, em cada iteração i, o algoritmo");
        System.out.println("deve realizar i comparações e i deslocamentos até alcançar o índice 0.");
        System.out.println("O número total de operações é dado pela soma da Progressão Aritmética:");
        System.out.println("  Soma = 1 + 2 + 3 + ... + (n - 1) = (n * (n - 1)) / 2 = (n² - n) / 2.");
        System.out.println("Desprezando constantes e termos de menor ordem, a ordem de grandeza é O(n²).\n");

        System.out.println("Demonstração Prática com Vetor Decrescente:");
        int[] vetorDecrescente = { 50, 40, 30, 20, 10 };
        int n = vetorDecrescente.length;
        int comparacoes = 0;
        int deslocamentos = 0;

        for (int i = 1; i < n; i++) {
            int chave = vetorDecrescente[i];
            int j = i - 1;
            while (j >= 0) {
                comparacoes++;
                if (vetorDecrescente[j] > chave) {
                    deslocamentos++;
                    vetorDecrescente[j + 1] = vetorDecrescente[j];
                    j--;
                } else {
                    break;
                }
            }
            vetorDecrescente[j + 1] = chave;
        }

        int formulaTeorica = (n * (n - 1)) / 2;
        System.out.println("Tamanho do vetor (n): " + n);
        System.out.println("Total de comparações: " + comparacoes);
        System.out.println("Total de deslocamentos: " + deslocamentos);
        System.out.println("Fórmula teórica (n * (n - 1)) / 2 = " + formulaTeorica);
        System.out.println("Vetor final ordenado: " + Arrays.toString(vetorDecrescente));
        System.out.println();
    }

    /**
     * Exercício 6
     * Enunciado: O Insertion Sort realiza apenas trocas entre vizinhos ou também desloca elementos? Explique a diferença.
     */
    public static void exercicio6() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Exercício 6: Trocas entre vizinhos versus deslocamentos");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Resposta Teórica:");
        System.out.println("O Insertion Sort canônico e eficiente NÃO realiza trocas completas (swaps),");
        System.out.println("mas sim DESLOCAMENTOS sucessivos.");
        System.out.println("Diferença Técnica Fundamental:");
        System.out.println("1. Troca entre vizinhos (Swap - estilo Bubble Sort):");
        System.out.println("   Requer 3 operações de atribuição em memória para cada par invertido:");
        System.out.println("   temp = A; A = B; B = temp;");
        System.out.println("   Se um elemento precisa recuar k posições, uma versão ingênua com trocas realiza");
        System.out.println("   3 * k escritas na memória.");
        System.out.println("2. Deslocamento (Shift - Insertion Sort padrão):");
        System.out.println("   Guarda a chave uma única vez em variável local (1 atribuição);");
        System.out.println("   Copia cada elemento maior uma posição à frente: vetor[j + 1] = vetor[j] (1 atribuição);");
        System.out.println("   Insere a chave na posição final: vetor[j + 1] = chave (1 atribuição);");
        System.out.println("   Total de escritas para recuar k posições: k + 2 atribuições.");
        System.out.println("Vantagem: O deslocamento consome aproximadamente um terço das operações de escrita,");
        System.out.println("gerando menor sobrecarga no barramento de memória e melhor uso do cache L1/L2.\n");

        System.out.println("Demonstração Prática (Custo de Escrita em Memória):");
        int k = 4; // Elemento precisa recuar 4 posições
        int escritasComSwap = 3 * k;
        int escritasComDeslocamento = k + 2;
        System.out.println("Para mover um elemento por " + k + " posições à esquerda:");
        System.out.println("  - Abordagem ingênua com trocas (swaps sucessivos): " + escritasComSwap + " escritas em array.");
        System.out.println("  - Abordagem canônica do Insertion Sort (deslocamentos): " + escritasComDeslocamento + " escritas em memória.");
        System.out.println("  - Redução de escritas em memória: " + ((escritasComSwap - escritasComDeslocamento) * 100 / escritasComSwap) + "%");
        System.out.println("========================================================================");
    }
}
