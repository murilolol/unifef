package br.com.aplcurso.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor : Prof. Jefferson Passerini
 * Tema      : Conexão com o Banco de Dados - Padrão Singleton JDBC
 *
 * Como Executar:
 *   1. Certifique-se de que o PostgreSQL está ativo e o banco bdaplcurso criado.
 *   2. Adicione o driver JDBC do PostgreSQL (postgresql-xx.jar) ao classpath.
 *   3. Compile:
 *      javac -d bin -cp ".:lib/postgresql.jar" src/br/com/aplcurso/utils/SingleConnection.java
 *   4. Execute a validação em linha de comando:
 *      java -cp "bin:lib/postgresql.jar" br.com.aplcurso.utils.SingleConnection
 */
public class SingleConnection {

    // Instância única mantida estaticamente (Padrão Singleton)
    private static Connection conexao = null;
    
    // Parâmetros de conexão com o banco de dados PostgreSQL
    private static String servidor = "jdbc:postgresql://localhost:5432/bdaplcurso?autoReconnect=true";
    private static String usuario = "postgres";
    private static String senha = "postdba";

    // Bloco static executado exatamente uma vez quando a classe é carregada na memória
    static {
        try {
            conectar();
        } catch (Exception ex) {
            System.out.println("Erro ao conectar ao banco de dados no bloco static:");
            ex.printStackTrace();
        }
    }

    // Construtor público que invoca conectar(), mantendo compatibilidade com instâncias manuais
    public SingleConnection() throws Exception {
        conectar();
    }

    /**
     * Estabelece a conexão com o banco caso ainda não exista.
     * Carrega o driver JDBC e define setAutoCommit(false) para controle manual de transações.
     */
    public static void conectar() throws Exception {
        try {
            if (conexao == null || conexao.isClosed()) {
                // Carrega a classe do Driver PostgreSQL em tempo de execução
                Class.forName("org.postgresql.Driver");
                
                // Abre a conexão com os parâmetros configurados
                conexao = DriverManager.getConnection(servidor, usuario, senha);
                
                // Desativa o autocommit para permitir transações manuais (commit / rollback)
                conexao.setAutoCommit(false);
            }
        } catch (ClassNotFoundException ex) {
            throw new Exception("Driver JDBC do PostgreSQL não encontrado no classpath: " + ex.getMessage());
        } catch (SQLException ex) {
            throw new Exception("Erro SQL ao conectar ao banco bdaplcurso: " + ex.getMessage());
        } catch (Exception ex) {
            throw new Exception("Erro genérico ao estabelecer conexão: " + ex.getMessage());
        }
    }

    /**
     * Fornece o ponto global de acesso à instância única da conexão.
     * Inclui saída padrão amigável para acessibilidade e leitores de tela durante debug.
     */
    public static Connection getConnection() {
        if (conexao == null) {
            System.out.println("DEBUG ACESSIBILIDADE: conexao está NULA!");
        } else {
            System.out.println("DEBUG ACESSIBILIDADE: conexao NÃO está nula! Objeto: " + conexao);
        }
        return conexao;
    }

    /**
     * Método main para teste e verificação direta em console da classe Singleton.
     */
    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("Teste de Conexão - Classe SingleConnection (Singleton)");
        System.out.println("========================================================");
        try {
            Connection c1 = SingleConnection.getConnection();
            Connection c2 = SingleConnection.getConnection();

            if (c1 != null && c1 == c2) {
                System.out.println("SUCESSO: Conexão estabelecida e padrão Singleton verificado!");
                System.out.println("Referência c1 == c2: " + (c1 == c2));
                System.out.println("AutoCommit status : " + c1.getAutoCommit() + " (esperado: false)");
            } else {
                System.out.println("FALHA: A conexão não pôde ser estabelecida ou instâncias diferem.");
            }
        } catch (Exception e) {
            System.out.println("Falha durante a execução do teste:");
            e.printStackTrace();
        }
    }
}
