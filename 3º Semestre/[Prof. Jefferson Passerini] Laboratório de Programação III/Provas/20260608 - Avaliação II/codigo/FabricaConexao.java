// Disciplina: Laboratório de Programação III (3º Semestre)
// Professor: Prof. Jefferson Passerini
// Tema: Avaliação II - Conexão JDBC e Criação Automática do Banco
// Como executar: javac FabricaConexao.java && java FabricaConexao

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FabricaConexao {
    // Exercício 2: Configuração de conexão com banco de dados SQLite
    private static final String URL = "jdbc:sqlite:loja_avaliacao.db";

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBanco() {
        String sql = "CREATE TABLE IF NOT EXISTS tb_produto (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "nome VARCHAR(100) NOT NULL, " +
                     "preco DECIMAL(10,2) NOT NULL, " +
                     "quantidade_estoque INT NOT NULL, " +
                     "data_cadastro VARCHAR(20) NOT NULL);";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar schema do banco: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Teste de Conexão com o Banco de Dados (Exercício 2) ===");
        try {
            inicializarBanco();
            try (Connection conn = obterConexao()) {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("Conexão JDBC estabelecida com sucesso em: " + URL);
                }
            }
        } catch (SQLException e) {
            System.err.println("Falha ao conectar: " + e.getMessage());
        }
    }
}
