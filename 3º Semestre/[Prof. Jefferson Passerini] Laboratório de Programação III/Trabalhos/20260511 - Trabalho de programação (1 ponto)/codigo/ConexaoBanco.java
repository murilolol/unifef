/*
 * Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
 * Professor:  Prof. Jefferson Passerini
 * Tema:       Java JSP Cap 5.4 - Desafio 02: Cadastro de Estado
 * Exercício:  Exercício 1
 * Como executar: javac ConexaoBanco.java && java ConexaoBanco
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fábrica responsável pela obtenção e liberação de conexões JDBC com o banco de dados.
 */
public class ConexaoBanco {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/lp3_projeto?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "root";

    public static Connection getConexao() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC MySQL não encontrado no classpath: " + e.getMessage(), e);
        }
    }

    public static void fechar(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static void fechar(Connection conn, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar Statement: " + e.getMessage());
            }
        }
        fechar(conn);
    }

    public static void fechar(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar ResultSet: " + e.getMessage());
            }
        }
        fechar(conn, stmt);
    }

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("UniFEF - LP3 - Teste de Conectividade JDBC");
        System.out.println("====================================================");
        System.out.println("Tentando conectar a: " + URL);

        try (Connection conn = getConexao()) {
            System.out.println("Conexão com MySQL estabelecida com sucesso!");
            System.out.println("Catálogo ativo: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("Aviso de teste: Falha na conexão real com MySQL (" + e.getMessage() + ").");
            System.out.println("Certifique-se de que o servidor MySQL está em execução na porta 3306 com o banco 'lp3_projeto'.");
        }
    }
}
