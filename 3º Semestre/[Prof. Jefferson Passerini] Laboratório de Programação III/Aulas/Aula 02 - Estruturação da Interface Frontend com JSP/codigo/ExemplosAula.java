/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Estruturação da Interface Frontend com JSP
 *
 * Como executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
 */

import java.util.*;

public class ExemplosAula {

    // Constantes reproduzindo fielmente os arquivos JSP criados na aula
    public static final String INDEX_JSP = 
        "<%@taglib prefix=\"c\" uri=\"http://java.sun.com/jsp/jstl/core\"%>\n" +
        "<jsp:include page=\"home.jsp\"/>";

    public static final String HEADER_JSP = 
        "<%@page contentType=\"text/html\" pageEncoding=\"iso-8859-1\"%>\n" +
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        " <head>\n" +
        " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n" +
        " <title>JSP Page</title>\n" +
        " <!-- JQuery -->\n" +
        " <script src=\"${pageContext.request.contextPath}/js/jquery-3.3.1.min.js\"></script>\n" +
        " <script src=\"${pageContext.request.contextPath}/js/jquery.mask.min.js\"></script>\n" +
        " <script src=\"${pageContext.request.contextPath}/js/jquery.maskMoney.min.js\"></script>\n" +
        " \n" +
        " <!-- Importação da minha biblioteca de javascript -->\n" +
        " <script src=\"${pageContext.request.contextPath}/js/app.js\" type=\"text/javascript\"></script>\n" +
        " \n" +
        " <!-- Bootstrap -->\n" +
        " <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\n" +
        " <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\"></script>\n" +
        " <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\n" +
        " \n" +
        " <!-- Datatable -->\n" +
        " <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css\"/>\n" +
        " <script src=\"https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js\" type=\"text/javascript\"></script>\n" +
        " \n" +
        " <!-- Mensagem alerta -->\n" +
        " <script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@10.3.1/dist/sweetalert2.all.min.js\" type=\"text/javascript\">\n" +
        " </script>\n" +
        " </head>\n" +
        " <body>";

    public static final String MENU_JSP = 
        "<h1>Módulo Cadastros</h1>\n" +
        "<hr>\n" +
        " <center>\n" +
        " <h2>Menu Principal</h2>\n" +
        " <a href=\"${pageContext.request.contextPath}/UsuarioListar\">Usuário</a>\n" +
        " </center>\n" +
        "<hr>";

    public static final String FOOTER_JSP = 
        " <hr>\n" +
        " <p>Desenvolvendo Aplicações com Java Web</p>\n" +
        " </body>\n" +
        "</html>";

    public static final String HOME_JSP = 
        "<%@taglib prefix=\"c\" uri=\"http://java.sun.com/jsp/jstl/core\" %>\n" +
        "<%@taglib prefix=\"fmt\" uri=\"http://java.sun.com/jsp/jstl/fmt\" %>\n" +
        "<%@page contentType=\"text/html\" pageEncoding=\"iso-8859-1\"%>\n" +
        "<jsp:include page=\"header.jsp\"/>\n" +
        "<jsp:include page=\"menu.jsp\"/>\n" +
        "\n" +
        " <h1>Sistema Exemplo - CRUD</h1>\n" +
        " \n" +
        "<jsp:include page=\"footer.jsp\"/>";

    // Implementação fiel da validação de CPF (conforme app.js)
    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^\\d]+", "");
        if (cpf.isEmpty() || cpf.length() != 11) return false;

        // Elimina CPFs inválidos conhecidos com dígitos repetidos
        String[] invalidos = {
            "00000000000", "11111111111", "22222222222", "33333333333",
            "44444444444", "55555555555", "66666666666", "77777777777",
            "88888888888", "99999999999"
        };
        for (String inv : invalidos) {
            if (cpf.equals(inv)) return false;
        }

        // Valida 1º dígito verificador
        int add = 0;
        for (int i = 0; i < 9; i++) {
            add += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int rev = 11 - (add % 11);
        if (rev == 10 || rev == 11) rev = 0;
        if (rev != Character.getNumericValue(cpf.charAt(9))) return false;

        // Valida 2º dígito verificador
        add = 0;
        for (int i = 0; i < 10; i++) {
            add += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        rev = 11 - (add % 11);
        if (rev == 10 || rev == 11) rev = 0;
        if (rev != Character.getNumericValue(cpf.charAt(10))) return false;

        return true;
    }

    // Implementação fiel do algoritmo de validação de CNPJ (conforme cnpjValidation de app.js)
    public static boolean cnpjValidation(String value) {
        if (value == null) return false;
        String clean = value.replaceAll("[^\\d]+", "");
        if (clean.length() != 14) return false;

        // Rejeita todos os dígitos iguais
        Set<Character> uniqueDigits = new HashSet<>();
        for (char c : clean.toCharArray()) uniqueDigits.add(c);
        if (uniqueDigits.size() == 1) return false;

        int[] numbers = new int[14];
        for (int i = 0; i < 14; i++) {
            numbers[i] = Character.getNumericValue(clean.charAt(i));
        }

        // Função auxiliar de cálculo validador (fator decrescente de x - 7 até 2, reiniciando em 9)
        int digit0 = calcularDigitoCNPJ(numbers, 12);
        if (digit0 != numbers[12]) return false;

        int digit1 = calcularDigitoCNPJ(numbers, 13);
        return digit1 == numbers[13];
    }

    private static int calcularDigitoCNPJ(int[] numbers, int x) {
        int factor = x - 7;
        int sum = 0;
        for (int i = x; i >= 1; i--) {
            int n = numbers[x - i];
            sum += n * factor--;
            if (factor < 2) factor = 9;
        }
        int result = 11 - (sum % 11);
        return result > 9 ? 0 : result;
    }

    // Validação unificada CPF/CNPJ (conforme validarCpfCnpj de app.js)
    public static boolean validarCpfCnpj(String doc) {
        if (doc == null) return false;
        String limpo = doc.replaceAll("[^\\d]+", "");
        if (limpo.length() == 14) {
            return cnpjValidation(limpo);
        } else {
            return validarCPF(limpo);
        }
    }

    // Alternância de máscara (conforme trocaMascaraCpfCnpj de app.js)
    public static String trocaMascaraCpfCnpj(String doc, boolean focoAberto) {
        if (focoAberto) {
            // Quando ganha foco ('A'), remove a máscara (unmask)
            return doc.replaceAll("[^\\d]+", "");
        }
        String clean = doc.replaceAll("[^\\d]+", "");
        if (clean.length() > 11) {
            // CNPJ: 99.999.999/9999-99
            if (clean.length() != 14) return clean;
            return clean.substring(0, 2) + "." + clean.substring(2, 5) + "." +
                   clean.substring(5, 8) + "/" + clean.substring(8, 12) + "-" +
                   clean.substring(12, 14);
        } else {
            // CPF: 999.999.999-99
            if (clean.length() != 11) return clean;
            return clean.substring(0, 3) + "." + clean.substring(3, 6) + "." +
                   clean.substring(6, 9) + "-" + clean.substring(9, 11);
        }
    }

    // Simulação da renderização e inclusão modular JSP com Expression Language
    public static String renderizarPaginaHome(String contextPath) {
        StringBuilder html = new StringBuilder();
        
        // Inclusão de header.jsp
        String headerProcessado = HEADER_JSP
            .replace("<%@page contentType=\"text/html\" pageEncoding=\"iso-8859-1\"%>\n", "")
            .replace("${pageContext.request.contextPath}", contextPath);
        html.append(headerProcessado).append("\n");

        // Inclusão de menu.jsp
        String menuProcessado = MENU_JSP
            .replace("${pageContext.request.contextPath}", contextPath);
        html.append(menuProcessado).append("\n");

        // Conteúdo central de home.jsp
        html.append(" <h1>Sistema Exemplo - CRUD</h1>\n");

        // Inclusão de footer.jsp
        html.append(FOOTER_JSP).append("\n");

        return html.toString();
    }

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("Laboratório de Programação III - Prof. Jefferson Passerini");
        System.out.println("Demonstração: Estrutura do Projeto Frontend e Validações");
        System.out.println("==============================================================\n");

        // 1. Teste de Validação de CPF
        System.out.println("--- 1. Testes de Validação de CPF ---");
        String cpfValido = "11144477735"; // Dígitos calculados corretos
        String cpfInvalido = "11111111111"; // Inválido por repetição
        System.out.println("CPF " + cpfValido + " é válido? " + validarCPF(cpfValido));
        System.out.println("CPF " + cpfInvalido + " é válido? " + validarCPF(cpfInvalido));

        // 2. Teste de Validação de CNPJ
        System.out.println("\n--- 2. Testes de Validação de CNPJ ---");
        String cnpjValido = "04252011000110"; // Exemplo estruturado com DVs válidos
        String cnpjInvalido = "22222222222222"; // Inválido por repetição
        System.out.println("CNPJ " + cnpjValido + " é válido? " + cnpjValidation(cnpjValido));
        System.out.println("CNPJ " + cnpjInvalido + " é válido? " + cnpjValidation(cnpjInvalido));

        // 3. Teste de Aplicação e Remoção de Máscaras
        System.out.println("\n--- 3. Testes de Alternância de Máscara (jQuery Mask) ---");
        String cpfMascara = trocaMascaraCpfCnpj("11144477735", false);
        String cnpjMascara = trocaMascaraCpfCnpj("04252011000110", false);
        String desmascarado = trocaMascaraCpfCnpj(cpfMascara, true);
        System.out.println("Mascara CPF: " + cpfMascara);
        System.out.println("Mascara CNPJ: " + cnpjMascara);
        System.out.println("Unmask ao focar: " + desmascarado);

        // 4. Teste da Montagem Modular JSP
        System.out.println("\n--- 4. Simulação da Montagem Modular (home.jsp com includes) ---");
        String contextPath = "/AplCurso2";
        String htmlGerado = renderizarPaginaHome(contextPath);
        System.out.println("HTML Renderizado (Primeiras 12 linhas):");
        String[] linhas = htmlGerado.split("\n");
        for (int i = 0; i < Math.min(12, linhas.length); i++) {
            System.out.println("  [HTML] " + linhas[i]);
        }
        System.out.println("  ...");
        System.out.println("HTML Finalizado com sucesso. Total de linhas geradas: " + linhas.length);
    }
}
