/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Estruturação da Interface Frontend com JSP
 *
 * Como executar:
 *   javac Exercicios.java
 *   java Exercicios
 */

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class Exercicios {

    // =========================================================================
    // Exercício 1: Validação Rigorosa de CPF e CNPJ com Cálculo de Dígitos Verificadores
    // =========================================================================
    public static class ValidadorDocumento {

        public static String limpar(String doc) {
            if (doc == null) return "";
            return doc.replaceAll("[^\\d]+", "");
        }

        public static boolean isRepetido(String str) {
            if (str == null || str.isEmpty()) return false;
            char c0 = str.charAt(0);
            for (int i = 1; i < str.length(); i++) {
                if (str.charAt(i) != c0) return false;
            }
            return true;
        }

        public static boolean validarCPF(String cpf) {
            String limpo = limpar(cpf);
            if (limpo.length() != 11 || isRepetido(limpo)) return false;

            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += Character.getNumericValue(limpo.charAt(i)) * (10 - i);
            }
            int dv1 = 11 - (soma1 % 11);
            if (dv1 >= 10) dv1 = 0;
            if (dv1 != Character.getNumericValue(limpo.charAt(9))) return false;

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += Character.getNumericValue(limpo.charAt(i)) * (11 - i);
            }
            int dv2 = 11 - (soma2 % 11);
            if (dv2 >= 10) dv2 = 0;
            return dv2 == Character.getNumericValue(limpo.charAt(10));
        }

        public static boolean validarCNPJ(String cnpj) {
            String limpo = limpar(cnpj);
            if (limpo.length() != 14 || isRepetido(limpo)) return false;

            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma1 = 0;
            for (int i = 0; i < 12; i++) {
                soma1 += Character.getNumericValue(limpo.charAt(i)) * peso1[i];
            }
            int resto1 = soma1 % 11;
            int dv1 = (resto1 < 2) ? 0 : (11 - resto1);
            if (dv1 != Character.getNumericValue(limpo.charAt(12))) return false;

            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma2 = 0;
            for (int i = 0; i < 13; i++) {
                soma2 += Character.getNumericValue(limpo.charAt(i)) * peso2[i];
            }
            int resto2 = soma2 % 11;
            int dv2 = (resto2 < 2) ? 0 : (11 - resto2);
            return dv2 == Character.getNumericValue(limpo.charAt(13));
        }

        public static boolean validar(String doc) {
            String limpo = limpar(doc);
            if (limpo.length() == 11) return validarCPF(limpo);
            if (limpo.length() == 14) return validarCNPJ(limpo);
            return false;
        }
    }

    // =========================================================================
    // Exercício 2: Alternância e Aplicação de Máscara de Documento
    // =========================================================================
    public static class GerenciadorMascara {

        public static String unmask(String valor) {
            return ValidadorDocumento.limpar(valor);
        }

        public static String mask(String valor) {
            String limpo = unmask(valor);
            if (limpo.length() <= 11) {
                // Máscara de CPF: 999.999.999-99
                if (limpo.length() < 11) return limpo;
                return limpo.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
            } else if (limpo.length() <= 14) {
                // Máscara de CNPJ: 99.999.999/9999-99
                if (limpo.length() < 14) return limpo;
                return limpo.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
            }
            return limpo;
        }
    }

    // =========================================================================
    // Exercício 3: Composição Modular de Páginas com Inclusão de Fragmentos
    // =========================================================================
    public static class MontadorLayoutJSP {
        private String contextPath;

        public MontadorLayoutJSP(String contextPath) {
            this.contextPath = contextPath;
        }

        public String processarInclude(String fragmentoJsp) {
            return fragmentoJsp.replace("${pageContext.request.contextPath}", this.contextPath);
        }

        public String comporPagina(String header, String menu, String body, String footer) {
            StringBuilder paginaCompleta = new StringBuilder();
            paginaCompleta.append(processarInclude(header)).append("\n");
            paginaCompleta.append(processarInclude(menu)).append("\n");
            paginaCompleta.append(processarInclude(body)).append("\n");
            paginaCompleta.append(processarInclude(footer));
            return paginaCompleta.toString();
        }
    }

    // =========================================================================
    // Exercício 4: Formatação e Tratamento de Valores Monetários
    // =========================================================================
    public static class FormatadorMoeda {

        public static String formatarBRL(double valor) {
            DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
            simbolos.setDecimalSeparator(',');
            simbolos.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("R$ #,##0.00", simbolos);
            return df.format(valor);
        }

        public static double unmaskMoeda(String valorFormatado) {
            if (valorFormatado == null || valorFormatado.trim().isEmpty()) return 0.0;
            String limpo = valorFormatado.replace("R$", "").trim();
            limpo = limpo.replace(".", "").replace(",", ".");
            try {
                return Double.parseDouble(limpo);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("Execução das Resoluções dos Exercícios Práticos");
        System.out.println("==============================================================\n");

        // Teste Exercício 1
        System.out.println("=== Exercício 1: Validação Rigorosa de CPF e CNPJ ===");
        String[] docsParaTestar = {
            "111.444.777-35",
            "111.111.111-11",
            "04.252.011/0001-10",
            "00.000.000/0000-00",
            "123456"
        };
        for (String doc : docsParaTestar) {
            boolean valido = ValidadorDocumento.validar(doc);
            System.out.printf("Documento: %-22s | Válido: %b%n", doc, valido);
        }

        // Teste Exercício 2
        System.out.println("\n=== Exercício 2: Alternância e Aplicação de Máscara ===");
        String cpfPuro = "11144477735";
        String cnpjPuro = "04252011000110";
        String cpfFormatado = GerenciadorMascara.mask(cpfPuro);
        String cnpjFormatado = GerenciadorMascara.mask(cnpjPuro);
        System.out.println("Entrada CPF puro: " + cpfPuro + " -> Mascarado: " + cpfFormatado);
        System.out.println("Entrada CNPJ puro: " + cnpjPuro + " -> Mascarado: " + cnpjFormatado);
        System.out.println("Unmask de CPF: " + GerenciadorMascara.unmask(cpfFormatado));
        System.out.println("Unmask de CNPJ: " + GerenciadorMascara.unmask(cnpjFormatado));

        // Teste Exercício 3
        System.out.println("\n=== Exercício 3: Composição Modular de Páginas JSP ===");
        MontadorLayoutJSP montador = new MontadorLayoutJSP("/AplCurso2");
        String cabecalho = "<head><link href=\"${pageContext.request.contextPath}/css/bootstrap.css\"/></head><body>";
        String menu = "<nav><a href=\"${pageContext.request.contextPath}/home\">Início</a></nav>";
        String conteudo = "<main><h1>Conteúdo da Página de Alunos</h1></main>";
        String rodape = "<footer>Rodapé do Sistema</footer></body>";
        String resultadoLayout = montador.comporPagina(cabecalho, menu, conteudo, rodape);
        System.out.println("Página Composta com Resolução de ContextPath:");
        System.out.println(resultadoLayout);

        // Teste Exercício 4
        System.out.println("\n=== Exercício 4: Formatação de Valores Monetários ===");
        double salario = 3850.75;
        String salarioBRL = FormatadorMoeda.formatarBRL(salario);
        double salarioExtraido = FormatadorMoeda.unmaskMoeda(salarioBRL);
        System.out.println("Valor numérico double: " + salario);
        System.out.println("Formatado (jQuery MaskMoney style): " + salarioBRL);
        System.out.println("Unmasked de volta para double: " + salarioExtraido);
        System.out.println("Validação de integridade numérica: " + (salario == salarioExtraido ? "OK (Exato)" : "Divergente"));
    }
}
