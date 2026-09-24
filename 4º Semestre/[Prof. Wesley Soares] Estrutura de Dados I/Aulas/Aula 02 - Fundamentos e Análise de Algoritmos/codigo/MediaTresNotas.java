import java.util.Locale;

/*
 * Estrutura de Dados I - Aula 02: Fundamentos e Análise de Algoritmos
 * Exemplo guiado: cálculo da média de três notas por REFINAMENTO SUCESSIVO.
 *
 * Nível 0 (problema):   "calcular a situação do aluno a partir de três notas".
 * Nível 1 (etapas):     ler notas -> validar -> calcular média -> classificar -> exibir.
 * Nível 2 (detalhes):   cada etapa vira um método pequeno, testável e com uma responsabilidade.
 *
 * Compilar/executar: javac MediaTresNotas.java && java MediaTresNotas
 */
public class MediaTresNotas {

    static final double MEDIA_APROVACAO = 6.0;
    static final double MEDIA_EXAME = 4.0;

    // ----- Nível 1: o algoritmo inteiro em poucas linhas, legível como a descrição da aula -----
    static String avaliar(double n1, double n2, double n3) {
        validar(n1, n2, n3);
        double media = calcularMedia(n1, n2, n3);
        return String.format(Locale.ROOT, "média %.2f -> %s", media, classificar(media));
    }

    // ----- Nível 2: refinamento de cada etapa -----
    static void validar(double... notas) {
        for (double n : notas) {
            if (Double.isNaN(n) || n < 0 || n > 10) {
                throw new IllegalArgumentException("Nota fora do intervalo [0, 10]: " + n);
            }
        }
    }

    static double calcularMedia(double n1, double n2, double n3) {
        return (n1 + n2 + n3) / 3.0;   // armadilha comum: n1 + n2 + n3 / 3 (precedência!)
    }

    static String classificar(double media) {
        if (media >= MEDIA_APROVACAO) return "APROVADO";
        if (media >= MEDIA_EXAME) return "EXAME";
        return "REPROVADO";
    }

    public static void main(String[] args) {
        double[][] casos = {{8, 7, 9}, {5, 6, 4}, {2, 3, 4.5}, {6, 6, 6}, {10, 0, 8}};
        System.out.println("=== Refinamento sucessivo: média de três notas ===");
        for (double[] c : casos) {
            System.out.printf(Locale.ROOT, "notas %.1f, %.1f, %.1f: %s%n", c[0], c[1], c[2], avaliar(c[0], c[1], c[2]));
        }

        // Contraexemplo da aula: sem parênteses o resultado sai errado.
        double errado = 8 + 7 + 9 / 3.0;
        System.out.printf(Locale.ROOT, "%nArmadilha de precedência: 8 + 7 + 9 / 3 = %.2f (esperado 8.00)%n", errado);

        try {
            avaliar(11, 5, 5);
        } catch (IllegalArgumentException e) {
            System.out.println("Validação: " + e.getMessage());
        }
    }
}
