/**
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Java JSP Cap 5.3 - Desafio 01: Implementar as validações para o campo e-mail
 * 
 * Como compilar:
 *   javac ValidadorEmail.java
 * 
 * Como executar:
 *   java ValidadorEmail
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorEmail {

    // Regex aderente ao padrão RFC 5322 simplificado para validação prática de e-mails em Java Web
    private static final String REGEX_EMAIL = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);

    // Lista de domínios temporários/descartáveis bloqueados (Exercício 3)
    private static final List<String> DOMINIOS_DESCARTAVEIS = Arrays.asList(
        "mailinator.com",
        "tempmail.com",
        "10minutemail.com",
        "guerrillamail.com",
        "throwawaymail.com"
    );

    /**
     * Classe interna para encapsular o resultado da validação com status e mensagens.
     */
    public static class ResultadoValidacao {
        private final boolean valido;
        private final List<String> erros;
        private final String emailNormalizado;

        public ResultadoValidacao(boolean valido, List<String> erros, String emailNormalizado) {
            this.valido = valido;
            this.erros = erros;
            this.emailNormalizado = emailNormalizado;
        }

        public boolean isValido() {
            return valido;
        }

        public List<String> getErros() {
            return erros;
        }

        public String getEmailNormalizado() {
            return emailNormalizado;
        }

        @Override
        public String toString() {
            if (valido) {
                return "[SUCESSO] E-mail válido e pronto para uso: '" + emailNormalizado + "'";
            } else {
                return "[FALHA] Erros detectados: " + String.join(" | ", erros);
            }
        }
    }

    /**
     * Exercício 2: Normalização e Sanitização do e-mail.
     * Remove espaços das extremidades e converte para minúsculas.
     */
    public static String sanitizarENormalizar(String emailBruto) {
        if (emailBruto == null) {
            return null;
        }
        // Remove espaços antes e depois
        String sanitizado = emailBruto.trim();
        // Remove quebras de linha para evitar ataques de Header Injection
        sanitizado = sanitizado.replaceAll("[\\r\\n]", "");
        // Converte para letras minúsculas para padronização em banco de dados
        return sanitizado.toLowerCase();
    }

    /**
     * Exercício 3: Verificação de domínios descartáveis ou inválidos.
     */
    public static boolean ehDominioDescartavel(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String[] partes = email.split("@");
        if (partes.length == 2) {
            String dominio = partes[1].toLowerCase().trim();
            return DOMINIOS_DESCARTAVEIS.contains(dominio);
        }
        return false;
    }

    /**
     * Exercício 1: Implementação das validações para o campo e-mail conforme o Desafio 01.
     * Realiza todas as validações de presença, tamanho, formato regex e regras de negócio.
     */
    public static ResultadoValidacao validar(String emailEntrada) {
        List<String> erros = new ArrayList<>();

        // 1. Validação de campo obrigatório (nulo ou vazio)
        if (emailEntrada == null || emailEntrada.trim().isEmpty()) {
            erros.add("O campo de e-mail é obrigatório e não pode ser deixado em branco.");
            return new ResultadoValidacao(false, erros, null);
        }

        // Sanitização prévia
        String emailNormalizado = sanitizarENormalizar(emailEntrada);

        // 2. Validação de comprimento do campo
        if (emailNormalizado.length() < 5) {
            erros.add("O e-mail deve ter no mínimo 5 caracteres.");
        }
        if (emailNormalizado.length() > 254) {
            erros.add("O e-mail excede o limite máximo de 254 caracteres permitido pelo padrão RFC 5321.");
        }

        // 3. Validação de estrutura sintática via Expressão Regular
        Matcher matcher = PATTERN_EMAIL.matcher(emailNormalizado);
        if (!matcher.matches()) {
            erros.add("Formato de e-mail inválido. O formato correto esperado é: usuario@dominio.extensao");
        }

        // 4. Validação de regras de negócio adicionais (Exercício 3)
        if (ehDominioDescartavel(emailNormalizado)) {
            erros.add("Não são permitidos endereços de e-mail temporários ou descartáveis.");
        }

        boolean valido = erros.isEmpty();
        return new ResultadoValidacao(valido, erros, emailNormalizado);
    }

    /**
     * Método principal para execução e teste dos casos de uso de validação.
     */
    public static void main(String[] args) {
        System.out.println("=======================================================================");
        System.out.println("   LABORATÓRIO DE PROGRAMAÇÃO III - PROF. JEFFERSON PASSERINI");
        System.out.println("   Java JSP Cap 5.3 - Desafio 01: Validações para o campo E-mail");
        System.out.println("=======================================================================\n");

        String[] entradasTeste = {
            // Casos válidos
            "aluno.unifef@fev.edu.br",
            "  contato@empresa.com.br  ",
            "dev_senior123@sub.dominio.org",
            "suporte+ticket42@servico.io",
            
            // Casos inválidos (Exercício 1)
            null,
            "   ",
            "a@b.c",
            "alunosemarroba.com",
            "aluno@dominio",
            "aluno@.com.br",
            "@dominio.com",
            "aluno@dominio..com",
            "aluno com espaco@dominio.com",
            
            // Caso de domínio temporário (Exercício 3)
            "usuario_teste@mailinator.com",
            "fraude@tempmail.com"
        };

        int indice = 1;
        for (String teste : entradasTeste) {
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("Teste " + indice + ": Entrada recebida = " + (teste == null ? "null" : "\"" + teste + "\""));
            ResultadoValidacao resultado = validar(teste);
            System.out.println(resultado);
            indice++;
        }

        System.out.println("\n=======================================================================");
        System.out.println("   FIM DA EXECUÇÃO DOS TESTES DE VALIDAÇÃO");
        System.out.println("=======================================================================");
    }
}
