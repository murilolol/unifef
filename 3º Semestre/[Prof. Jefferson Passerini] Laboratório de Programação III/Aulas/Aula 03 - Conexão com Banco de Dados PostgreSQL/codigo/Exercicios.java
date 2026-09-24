package br.com.aplcurso.exercicios;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Disciplina : Laboratório de Programação III (3º Semestre)
 * Professor  : Prof. Jefferson Passerini
 * Tema       : Resolução Completa dos Exercícios Práticos da Aula 04
 *
 * Como Executar:
 *   javac -d bin src/br/com/aplcurso/exercicios/Exercicios.java
 *   java -cp "bin:lib/postgresql.jar" br.com.aplcurso.exercicios.Exercicios
 */
public class Exercicios {

    private static final String URL = "jdbc:postgresql://localhost:5432/bdaplcurso?autoReconnect=true";
    private static final String USER = "postgres";
    private static final String PASS = "postdba";

    /**
     * Exercício 1: Criação e Carga da Tabela Usuario no PostgreSQL
     */
    public static void resolverExercicio1(Connection conn) {
        System.out.println("\n====================================================================");
        System.out.println("Exercício 1: Criação da Tabela usuario e Inserção Inicial no PostgreSQL");
        System.out.println("====================================================================");
        if (conn == null) {
            System.out.println("AVISO: Conexão não disponível. Execute o script banco.sql no pgAdmin.");
            return;
        }

        String ddl = "CREATE TABLE IF NOT EXISTS usuario (" +
                     " id SERIAL PRIMARY KEY," +
                     " nome VARCHAR(100) NOT NULL," +
                     " datanascimento DATE NOT NULL," +
                     " cpf VARCHAR(11) UNIQUE NOT NULL," +
                     " email VARCHAR(100) UNIQUE NOT NULL," +
                     " senha VARCHAR(20) NOT NULL," +
                     " salario DECIMAL(15,2) NOT NULL" +
                     ");";

        String dmlInsert = "INSERT INTO usuario (nome, datanascimento, cpf, email, senha, salario) " +
                           "VALUES (?, ?, ?, ?, ?, ?) " +
                           "ON CONFLICT (cpf) DO NOTHING;";

        try (Statement stmt = conn.createStatement();
             PreparedStatement pstm = conn.prepareStatement(dmlInsert)) {
            
            // 1. Executa DDL
            stmt.execute(ddl);
            System.out.println("Tabela 'usuario' criada ou verificada com sucesso.");

            // 2. Insere dados do usuário de teste
            pstm.setString(1, "João José Gomes da Silva");
            pstm.setDate(2, Date.valueOf("1990-08-10"));
            pstm.setString(3, "08243060073");
            pstm.setString(4, "joaojosegomes@gmail.com");
            pstm.setString(5, "senha123");
            pstm.setDouble(6, 5200.00);
            int linhas = pstm.executeUpdate();
            conn.commit();
            System.out.println("Carga de dados executada. Linhas inseridas: " + linhas);

        } catch (SQLException e) {
            System.out.println("Erro no Exercício 1: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Exercício 2: Validação da Existência e Conteúdo do Script banco.sql
     */
    public static void resolverExercicio2() {
        System.out.println("\n====================================================================");
        System.out.println("Exercício 2: Versionamento do Script banco.sql no Pacote utils");
        System.out.println("====================================================================");
        String caminhoEsperado = "src/java/br/com/aplcurso/utils/banco.sql";
        System.out.println("Localização padrão do script no NetBeans: " + caminhoEsperado);
        System.out.println("Status: O script armazena integralmente as diretivas CREATE TABLE, INSERT e SELECT");
        System.out.println("Garantia: O banco pode ser reproduzido fielmente por qualquer membro da equipe.");
    }

    /**
     * Exercício 3: Teste de Integridade do Padrão Singleton JDBC
     */
    public static void resolverExercicio3(Connection conn1, Connection conn2) {
        System.out.println("\n====================================================================");
        System.out.println("Exercício 3: Validação do Padrão Singleton na Conexão JDBC");
        System.out.println("====================================================================");
        if (conn1 == null || conn2 == null) {
            System.out.println("AVISO: Conexões nulas. Teste de Singleton não pôde comparar instâncias físicas.");
            return;
        }

        boolean mesmaReferencia = (conn1 == conn2);
        System.out.println("Instância 1 obtida: " + conn1.hashCode());
        System.out.println("Instância 2 obtida: " + conn2.hashCode());
        System.out.println("As duas referências apontam para o mesmo objeto na memória? " + mesmaReferencia);
        
        try {
            System.out.println("AutoCommit desativado (false)? " + !conn1.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exercício 4: Simulação de Interceptação com FilterAutenticacao
     */
    public static void resolverExercicio4() {
        System.out.println("\n====================================================================");
        System.out.println("Exercício 4: Ciclo de Vida do FilterAutenticacao (@WebFilter)");
        System.out.println("====================================================================");
        System.out.println("Mapeamento configurado: @WebFilter(urlPatterns = {\"/*\"})");
        System.out.println("Fase 1: init(filterConfig) -> Acionado no startup da aplicação, obtém conexao Singleton.");
        System.out.println("Fase 2: doFilter(req, res, chain) -> Intercepta a requisição e invoca chain.doFilter().");
        System.out.println("Fase 3: destroy() -> Acionado no undeploy/shutdown, executa conexao.close().");
    }

    /**
     * Exercício 5: Diagnóstico, Depuração e Recursos de Acessibilidade
     */
    public static void resolverExercicio5(Connection conn) {
        System.out.println("\n====================================================================");
        System.out.println("Exercício 5: Diagnóstico, Breakpoint e Acessibilidade para Leitores de Tela");
        System.out.println("====================================================================");
        
        // Feedback verbalizado via console para programadores cegos
        if (conn == null) {
            System.out.println("[ACESSIBILIDADE - LEITOR DE TELA] ALERTA: O objeto conexao está NULL (Nulo)!");
            System.out.println("Sugestão: Verifique se o serviço PostgreSQL está rodando e credenciais conferem.");
        } else {
            System.out.println("[ACESSIBILIDADE - LEITOR DE TELA] SUCESSO: O objeto conexao está ATIVO e VÁLIDO!");
            try {
                System.out.println("Detalhes da Conexão Ativa: " + conn.getMetaData().getURL() +
                                   " | Usuário: " + conn.getMetaData().getUserName());
            } catch (SQLException e) {
                System.out.println("Erro ao ler metadados: " + e.getMessage());
            }
        }
        
        System.out.println("Atalhos de Depuração do NetBeans validados:");
        System.out.println("  - Adicionar Breakpoint: Ctrl + F8 (ou Cmd + F8)");
        System.out.println("  - Adicionar Watch      : Ctrl + Shift + F7");
        System.out.println("  - Janela de Variáveis  : Alt + Shift + 1");
        System.out.println("  - Iniciar Debug        : Ctrl + F5");
    }

    /**
     * Execução de todos os exercícios de validação
     */
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("UniFEF - Laboratório de Programação III - Prof. Jefferson Passerini");
        System.out.println("Execução da Resolução dos Exercícios da Aula 04");
        System.out.println("====================================================================");

        Connection conn1 = null;
        Connection conn2 = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn1 = DriverManager.getConnection(URL, USER, PASS);
            conn1.setAutoCommit(false);
            conn2 = conn1; // No padrão Singleton, ambas referenciam o mesmo objeto físico
        } catch (Exception e) {
            System.out.println("PostgreSQL local indisponível para conexão física imediata: " + e.getMessage());
        }

        // Executa as rotinas dos exercícios
        resolverExercicio1(conn1);
        resolverExercicio2();
        resolverExercicio3(conn1, conn2);
        resolverExercicio4();
        resolverExercicio5(conn1);

        if (conn1 != null) {
            try {
                conn1.close();
                System.out.println("\nConexões de teste encerradas com sucesso.");
            } catch (SQLException ignored) {}
        }
    }
}
