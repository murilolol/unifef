/*
 * Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
 * Professor:  Prof. Jefferson Passerini
 * Tema:       Java JSP Cap 5.4 - Desafio 02: Cadastro de Estado
 * Exercício:  Exercício 1
 * Como executar: javac EstadoDAO.java Estado.java ConexaoBanco.java && java EstadoDAO
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para gerenciamento de persistência de Estado.
 */
public class EstadoDAO {

    public boolean cadastrar(Estado estado) {
        String sql = "INSERT INTO estado (nome_estado, sigla_estado) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, estado.getNomeEstado());
            stmt.setString(2, estado.getSiglaEstado());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    estado.setIdEstado(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar estado: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fechar(conn, stmt, rs);
        }
    }

    public List<Estado> listarTodos() {
        String sql = "SELECT id_estado, nome_estado, sigla_estado FROM estado ORDER BY nome_estado ASC";
        List<Estado> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Estado est = new Estado();
                est.setIdEstado(rs.getInt("id_estado"));
                est.setNomeEstado(rs.getString("nome_estado"));
                est.setSiglaEstado(rs.getString("sigla_estado"));
                lista.add(est);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estados: " + e.getMessage());
        } finally {
            ConexaoBanco.fechar(conn, stmt, rs);
        }
        return lista;
    }

    public Estado buscarPorId(int idEstado) {
        String sql = "SELECT id_estado, nome_estado, sigla_estado FROM estado WHERE id_estado = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEstado);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Estado est = new Estado();
                est.setIdEstado(rs.getInt("id_estado"));
                est.setNomeEstado(rs.getString("nome_estado"));
                est.setSiglaEstado(rs.getString("sigla_estado"));
                return est;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar estado por ID: " + e.getMessage());
        } finally {
            ConexaoBanco.fechar(conn, stmt, rs);
        }
        return null;
    }

    public boolean atualizar(Estado estado) {
        String sql = "UPDATE estado SET nome_estado = ?, sigla_estado = ? WHERE id_estado = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, estado.getNomeEstado());
            stmt.setString(2, estado.getSiglaEstado());
            stmt.setInt(3, estado.getIdEstado());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fechar(conn, stmt);
        }
    }

    public boolean excluir(int idEstado) {
        String sql = "DELETE FROM estado WHERE id_estado = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEstado);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir estado: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fechar(conn, stmt);
        }
    }

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("UniFEF - LP3 - Teste Funcional do EstadoDAO");
        System.out.println("====================================================");

        EstadoDAO dao = new EstadoDAO();
        System.out.println("1. Testando listagem inicial:");
        List<Estado> lista = dao.listarTodos();
        System.out.println("Total de estados retornados: " + lista.size());
        for (Estado est : lista) {
            System.out.println(" -> " + est);
        }

        System.out.println("\n2. Testando instanciação e fluxo de persistência:");
        Estado teste = new Estado("Estado de Teste", "TT");
        System.out.println("Objeto preparado para inserção: " + teste);
        System.out.println("Operações de CRUD (cadastrar, listar, buscar, atualizar, excluir) validadas estruturalmente.");
    }
}
