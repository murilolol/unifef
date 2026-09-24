package br.com.aplcurso.exemplos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Disciplina : Laboratório de Programação III (3º Semestre)
 * Professor  : Prof. Jefferson Passerini
 * Tema       : Demonstração e Organização dos Exemplos da Aula 04
 *
 * Como Executar:
 *   javac -d bin src/br/com/aplcurso/exemplos/ExemplosAula.java
 *   java -cp "bin:lib/postgresql.jar" br.com.aplcurso.exemplos.ExemplosAula
 */
public class ExemplosAula {

    // =========================================================================
    // 1. EXEMPLO CLÁSSICO DO PADRÃO CRIACIONAL SINGLETON (CONCEITO TEÓRICO)
    // =========================================================================
    public static class ExemploSingletonClassico {
        // Atributo estático que armazena a instância única
        private static ExemploSingletonClassico instancia;

        // Construtor privado para evitar instanciação direta via operador new
        private ExemploSingletonClassico() {
            System.out.println("  -> [SingletonClassico] Instância criada exclusivamente uma vez.");
        }

        // Método estático global de recuperação da instância
        public static ExemploSingletonClassico getInstance() {
            if (instancia == null) {
                instancia = new ExemploSingletonClassico();
            }
            return instancia;
        }
    }

    // =========================================================================
    // 2. DEMONSTRAÇÃO DE CONTROLE DE TRANSAÇÕES JDBC (ACID: COMMIT E ROLLBACK)
    // =========================================================================
    public static void demonstrarControleTransacao(Connection conn) {
        System.out.println("\n--- 2. Demonstrando Transações Manuais em JDBC (ACID) ---");
        if (conn == null) {
            System.out.println("Conexão nula. Pulando teste transacional em banco real.");
            return;
        }

        PreparedStatement stmt = null;
        try {
            // Assegura autocommit desativado (início implícito de transação)
            conn.setAutoCommit(false);
            System.out.println("Passo 1: Transação iniciada com setAutoCommit(false).");

            // Operação 1: Atualização salarial fictícia
            String sqlUpdate = "UPDATE usuario SET salario = salario + 100 WHERE id = 1";
            stmt = conn.prepareStatement(sqlUpdate);
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Passo 2: Query executada. Linhas modificadas: " + linhasAfetadas);

            // Confirmação da transação (COMMIT)
            conn.commit();
            System.out.println("Passo 3: Transação confirmada com sucesso (COMMIT)!");

        } catch (SQLException e) {
            System.out.println("Erro durante a execução transacional: " + e.getMessage());
            try {
                // Em caso de falha, desfaz todas as alterações pendentes da unidade de trabalho
                conn.rollback();
                System.out.println("Rollback executado: alterações revertidas com integridade!");
            } catch (SQLException exRollback) {
                System.out.println("Falha crítica no rollback: " + exRollback.getMessage());
            }
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // =========================================================================
    // 3. CONSULTA SIMPLES À TABELA USUARIO
    // =========================================================================
    public static void consultarUsuarios(Connection conn) {
        System.out.println("\n--- 3. Consulta de Verificação na Tabela Usuario ---");
        if (conn == null) return;

        String sql = "SELECT id, nome, cpf, email, salario FROM usuario";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("ID: %d | Nome: %s | CPF: %s | Email: %s | Salário: R$ %.2f%n",
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("email"),
                        rs.getDouble("salario"));
            }
        } catch (SQLException e) {
            System.out.println("Aviso na consulta (tabela pode ainda não ter sido criada no ambiente local): " + e.getMessage());
        }
    }

    // =========================================================================
    // MÉTODO MAIN DE EXECUÇÃO E APRESENTAÇÃO DOS CONCEITOS
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("UniFEF - Laboratório de Programação III - Prof. Jefferson Passerini");
        System.out.println("Execução dos Exemplos Teórico-Práticos da Aula 04");
        System.out.println("====================================================================");

        // Teste do Singleton Didático
        System.out.println("\n--- 1. Validação do Singleton em Memória ---");
        ExemploSingletonClassico s1 = ExemploSingletonClassico.getInstance();
        ExemploSingletonClassico s2 = ExemploSingletonClassico.getInstance();
        System.out.println("Instâncias s1 e s2 são idênticas? " + (s1 == s2));

        // Teste de Conexão com PostgreSQL
        System.out.println("\n--- 2. Teste de Conexão com o SGBD PostgreSQL ---");
        String url = "jdbc:postgresql://localhost:5432/bdaplcurso?autoReconnect=true";
        String user = "postgres";
        String pass = "postdba";

        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexão com PostgreSQL obtida com sucesso: " + conn);

            // Executa transação controlada e consultas
            demonstrarControleTransacao(conn);
            consultarUsuarios(conn);

        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC PostgreSQL não encontrado na execução. Certifique-se de incluir o .jar no classpath.");
        } catch (SQLException e) {
            System.out.println("Não foi possível conectar ao PostgreSQL local (verifique se o serviço está ativo e o banco 'bdaplcurso' criado):");
            System.out.println("Detalhe do erro: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\nConexão finalizada de forma segura.");
                } catch (SQLException ignored) {}
            }
        }
    }
}
