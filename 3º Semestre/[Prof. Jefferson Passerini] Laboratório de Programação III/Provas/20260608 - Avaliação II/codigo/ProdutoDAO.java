// Disciplina: Laboratório de Programação III (3º Semestre)
// Professor: Prof. Jefferson Passerini
// Tema: Avaliação II - Padrão Data Access Object (DAO)
// Como executar: javac *.java && java ProdutoDAO

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public ProdutoDAO() {
        FabricaConexao.inicializarBanco();
    }

    // Exercício 2: Inserção de registro (Create)
    public void inserir(Produto produto) throws SQLException {
        String sql = "INSERT INTO tb_produto (nome, preco, quantidade_estoque, data_cadastro) VALUES (?, ?, ?, ?)";
        try (Connection conn = FabricaConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getQuantidadeEstoque());
            stmt.setString(4, produto.getDataCadastro());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                }
            }
        }
    }

    // Exercício 2: Consulta de todos os registros (Read)
    public List<Produto> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, preco, quantidade_estoque, data_cadastro FROM tb_produto ORDER BY id ASC";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = FabricaConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade_estoque"),
                        rs.getString("data_cadastro")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    // Exercício 2: Consulta por chave primária (Read)
    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, preco, quantidade_estoque, data_cadastro FROM tb_produto WHERE id = ?";
        try (Connection conn = FabricaConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade_estoque"),
                            rs.getString("data_cadastro")
                    );
                }
            }
        }
        return null;
    }

    // Exercício 2: Atualização de registro existente (Update)
    public boolean atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE tb_produto SET nome = ?, preco = ?, quantidade_estoque = ?, data_cadastro = ? WHERE id = ?";
        try (Connection conn = FabricaConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getQuantidadeEstoque());
            stmt.setString(4, produto.getDataCadastro());
            stmt.setInt(5, produto.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Exercício 2: Exclusão de registro (Delete)
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM tb_produto WHERE id = ?";
        try (Connection conn = FabricaConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Teste Completo do Ciclo CRUD no DAO (Exercício 2) ===");
        ProdutoDAO dao = new ProdutoDAO();
        try {
            Produto teste = new Produto("Headset Gamer 7.1", 350.00, 8, "2026-06-08");
            dao.inserir(teste);
            System.out.println("Inserido: " + teste);

            Produto buscado = dao.buscarPorId(teste.getId());
            System.out.println("Buscado por ID: " + buscado);

            teste.setPreco(320.00);
            dao.atualizar(teste);
            System.out.println("Preço atualizado com sucesso.");

            dao.excluir(teste.getId());
            System.out.println("Excluído com sucesso.");
        } catch (SQLException e) {
            System.err.println("Falha no teste do DAO: " + e.getMessage());
        }
    }
}
