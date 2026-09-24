/**
 * Disciplina : Laboratório de Programação III (3º Semestre)
 * Professor  : Prof. Jefferson Passerini
 * Tema       : Resolução dos Exercícios Práticos da Aula 05
 * 
 * Como compilar:
 *   javac Exercicios.java
 * 
 * Como executar:
 *   java Exercicios
 */

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Exercicios {

    // Modelo simplificado para suporte aos testes dos exercícios
    static class UsuarioExercicio {
        private int id;
        private String nome;
        private Date dataNascimento;
        private String cpf;
        private String email;
        private double salario;

        public UsuarioExercicio(int id, String nome, Date dataNascimento, String cpf, String email, double salario) {
            this.id = id;
            this.nome = nome;
            this.dataNascimento = dataNascimento;
            this.cpf = cpf;
            this.email = email;
            this.salario = salario;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public Date getDataNascimento() { return dataNascimento; }
        public String getCpf() { return cpf; }
        public String getEmail() { return email; }
        public double getSalario() { return salario; }
    }

    // Repositório de dados para simulação dos exercícios
    private static final List<UsuarioExercicio> bancoMock = new ArrayList<>();

    static {
        bancoMock.add(new UsuarioExercicio(1, "João José Gomes da Silva", new Date(90, 7, 10), "08243060073", "joaojosegomes@gmail.com", 5200.00));
        bancoMock.add(new UsuarioExercicio(2, "Maria Aparecida Santos", new Date(88, 3, 22), "12345678901", "maria.santos@email.com", 6450.50));
        bancoMock.add(new UsuarioExercicio(3, "Carlos Eduardo Ferreira", new Date(95, 10, 15), "98765432100", "carlos.ferreira@email.com", 3800.00));
    }

    // =========================================================================
    // Exercício 1: Filtro de Usuários por Nome no DAO e Servlet
    // =========================================================================
    public static List<UsuarioExercicio> listarPorNome(String termoBusca) {
        List<UsuarioExercicio> resultado = new ArrayList<>();
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            return new ArrayList<>(bancoMock);
        }
        
        String termoNormalizado = termoBusca.trim().toUpperCase();
        for (UsuarioExercicio u : bancoMock) {
            // Emulação do SQL: WHERE UPPER(nome) LIKE '%TERMO%'
            if (u.getNome().toUpperCase().contains(termoNormalizado)) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    // =========================================================================
    // Exercício 2: Tratamento de Lista Vazia e Feedback na View JSP
    // =========================================================================
    public static String simularRenderizacaoJsp(List<UsuarioExercicio> usuarios) {
        StringBuilder html = new StringBuilder();
        // Emulação da lógica das tags <c:choose><c:when test="${empty usuarios}">...</c:choose>
        if (usuarios == null || usuarios.isEmpty()) {
            html.append("<div class=\"alert alert-info\" role=\"alert\">\n");
            html.append("  Nenhum registro de usuário encontrado no sistema.\n");
            html.append("</div>");
        } else {
            html.append("<table class=\"table table-bordered\">\n");
            html.append("  <thead><tr><th>ID</th><th>Nome</th><th>CPF</th></tr></thead>\n");
            html.append("  <tbody>\n");
            for (UsuarioExercicio u : usuarios) {
                html.append("    <tr><td>").append(u.getId()).append("</td><td>")
                    .append(u.getNome()).append("</td><td>").append(formatarCpf(u.getCpf())).append("</td></tr>\n");
            }
            html.append("  </tbody>\n");
            html.append("</table>");
        }
        return html.toString();
    }

    // =========================================================================
    // Exercício 3: Mascaramento Dinâmico de CPF e Formatação Monetária
    // =========================================================================
    public static String formatarCpf(String cpf) {
        if (cpf == null) {
            return "";
        }
        String apenasDigitos = cpf.replaceAll("\\D", "");
        if (apenasDigitos.length() == 11) {
            return apenasDigitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }

    public static String formatarMoeda(double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf.format(valor);
    }

    // =========================================================================
    // Exercício 4: Auditoria de Performance e Telemetria no Método listar()
    // =========================================================================
    public static List<UsuarioExercicio> listarComTelemetria() {
        long inicio = System.nanoTime();
        
        // Simulação da consulta ao banco
        List<UsuarioExercicio> dados = new ArrayList<>(bancoMock);
        try {
            Thread.sleep(15); // Simula latência de I/O de rede e banco
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long fim = System.nanoTime();
        double tempoMs = (fim - inicio) / 1_000_000.0;
        System.out.println(String.format("[Telemetria DAO] Consulta executada em %.2f ms | Registros retornados: %d", 
                tempoMs, dados.size()));
        return dados;
    }

    // =========================================================================
    // Método Principal: Execução dos Casos de Teste
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println(" Resolução e Teste dos Exercícios da Aula 05");
        System.out.println("====================================================================\n");

        // Teste Exercício 1
        System.out.println("--- Teste Exercício 1: Filtro de Usuários por Nome ---");
        String termo = "Silva";
        List<UsuarioExercicio> filtrados = listarPorNome(termo);
        System.out.println("Busca por '" + termo + "': Encontrado(s) " + filtrados.size() + " registro(s):");
        for (UsuarioExercicio u : filtrados) {
            System.out.println(" -> " + u.getNome() + " (" + u.getEmail() + ")");
        }

        // Teste Exercício 2
        System.out.println("\n--- Teste Exercício 2: Tratamento de Lista Vazia na View ---");
        List<UsuarioExercicio> listaVazia = new ArrayList<>();
        System.out.println("Saída HTML quando lista está vazia:");
        System.out.println(simularRenderizacaoJsp(listaVazia));

        // Teste Exercício 3
        System.out.println("\n--- Teste Exercício 3: Formatação de CPF e Moeda ---");
        String cpfPuro = "08243060073";
        double salarioExemplo = 5200.0;
        System.out.println("CPF Original : " + cpfPuro + " -> Formatado: " + formatarCpf(cpfPuro));
        System.out.println("Salário Bruto: " + salarioExemplo + " -> Formatado: " + formatarMoeda(salarioExemplo));

        // Teste Exercício 4
        System.out.println("\n--- Teste Exercício 4: Auditoria de Performance e Telemetria ---");
        List<UsuarioExercicio> auditoria = listarComTelemetria();
        System.out.println("Finalizado com sucesso. Registros prontos para envio ao Servlet.");
    }
}
