/*
 * Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Manutenção do Cadastro de Usuários - Resolução dos Exercícios
 *
 * Como compilar e executar:
 *   javac Exercicios.java
 *   java Exercicios
 */

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Exercicios {

    // =========================================================================
    // MODELO DE DOMÍNIO
    // =========================================================================
    public static class Usuario implements Serializable {
        private static final long serialVersionUID = 1L;

        private int id;
        private String nome;
        private Date dataNascimento;
        private String cpf;
        private String email;
        private String senha;
        private double salario;

        public Usuario() {
            this.id = 0;
            this.nome = "";
            this.cpf = "";
            this.email = "";
            this.senha = "";
            this.salario = 0.0;
        }

        public Usuario(int id, String nome, Date dataNascimento, String cpf, String email, String senha, double salario) {
            this.id = id;
            this.nome = nome;
            this.dataNascimento = dataNascimento;
            this.cpf = cpf;
            this.email = email;
            this.senha = senha;
            this.salario = salario;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public Date getDataNascimento() { return dataNascimento; }
        public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public double getSalario() { return salario; }
        public void setSalario(double salario) { this.salario = salario; }

        @Override
        public String toString() {
            return "Usuario{" +
                    "id=" + id +
                    ", nome='" + nome + '\'' +
                    ", cpf='" + cpf + '\'' +
                    ", email='" + email + '\'' +
                    ", dataNascimento=" + dataNascimento +
                    ", salario=" + salario +
                    '}';
        }
    }

    // =========================================================================
    // BANCO DE DADOS EM MEMÓRIA PARA TESTES DOS EXERCÍCIOS
    // =========================================================================
    public static class BancoMemoriaExercicios {
        public static final Map<Integer, Usuario> tabela = new HashMap<>();
        private static int seq = 1;

        public static void inicializar() {
            tabela.clear();
            seq = 1;
            Usuario u1 = new Usuario(seq++, "ROBERTO ALVES", Date.valueOf("1988-03-15"),
                    "12345678909", "roberto@empresa.com", "pass123", 4200.0);
            Usuario u2 = new Usuario(seq++, "FERNANDA COSTA", Date.valueOf("1992-07-22"),
                    "52998224725", "fernanda@empresa.com", "pass456", 5800.0);
            tabela.put(u1.getId(), u1);
            tabela.put(u2.getId(), u2);
        }

        public static Connection getConexao() {
            ClassLoader cl = Connection.class.getClassLoader();
            return (Connection) Proxy.newProxyInstance(cl, new Class<?>[]{Connection.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String m = method.getName();
                    if ("prepareStatement".equals(m)) {
                        String sql = (String) args[0];
                        return getPreparedStatement(sql);
                    }
                    if ("commit".equals(m) || "rollback".equals(m) || "close".equals(m)) {
                        return null;
                    }
                    return null;
                }
            });
        }

        private static PreparedStatement getPreparedStatement(String sql) {
            ClassLoader cl = PreparedStatement.class.getClassLoader();
            return (PreparedStatement) Proxy.newProxyInstance(cl, new Class<?>[]{PreparedStatement.class}, new InvocationHandler() {
                private final Map<Integer, Object> params = new HashMap<>();

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String m = method.getName();
                    if (m.startsWith("set") && args != null && args.length >= 2) {
                        params.put((Integer) args[0], args[1]);
                        return null;
                    }
                    if ("executeUpdate".equals(m) || "execute".equals(m)) {
                        String sqlLower = sql.toLowerCase().trim();
                        if (sqlLower.startsWith("update")) {
                            String nome = (String) params.get(1);
                            Date dtNasc = (Date) params.get(2);
                            String cpf = (String) params.get(3);
                            String email = (String) params.get(4);
                            String senha = (String) params.get(5);
                            double salario = (Double) params.get(6);
                            int id = (Integer) params.get(7);

                            Usuario u = tabela.get(id);
                            if (u != null) {
                                u.setNome(nome);
                                u.setDataNascimento(dtNasc);
                                u.setCpf(cpf);
                                u.setEmail(email);
                                u.setSenha(senha);
                                u.setSalario(salario);
                                return "executeUpdate".equals(m) ? 1 : true;
                            }
                            return "executeUpdate".equals(m) ? 0 : false;
                        }
                        if (sqlLower.startsWith("delete")) {
                            int id = (Integer) params.get(1);
                            Usuario removido = tabela.remove(id);
                            int rows = (removido != null) ? 1 : 0;
                            return "executeUpdate".equals(m) ? rows : (rows > 0);
                        }
                        return "executeUpdate".equals(m) ? 1 : true;
                    }
                    if ("executeQuery".equals(m)) {
                        String sqlLower = sql.toLowerCase().trim();
                        if (sqlLower.contains("count(*) as quantidade_email")) {
                            String email = (String) params.get(1);
                            int idAtual = (Integer) params.get(2);
                            long count = tabela.values().stream()
                                    .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getId() != idAtual)
                                    .count();
                            return getResultSetCount((int) count);
                        }
                        if (sqlLower.contains("where id=?") || sqlLower.contains("where id = ?")) {
                            int id = (Integer) params.get(1);
                            Usuario u = tabela.get(id);
                            List<Usuario> list = new ArrayList<>();
                            if (u != null) list.add(u);
                            return getResultSetUsuarios(list);
                        }
                    }
                    if ("close".equals(m)) return null;
                    return null;
                }
            });
        }

        private static ResultSet getResultSetCount(int count) {
            ClassLoader cl = ResultSet.class.getClassLoader();
            return (ResultSet) Proxy.newProxyInstance(cl, new Class<?>[]{ResultSet.class}, new InvocationHandler() {
                private int cursor = 0;
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("next".equals(method.getName())) {
                        cursor++;
                        return cursor == 1;
                    }
                    if ("getInt".equals(method.getName())) return count;
                    if ("close".equals(method.getName())) return null;
                    return null;
                }
            });
        }

        private static ResultSet getResultSetUsuarios(List<Usuario> lista) {
            ClassLoader cl = ResultSet.class.getClassLoader();
            return (ResultSet) Proxy.newProxyInstance(cl, new Class<?>[]{ResultSet.class}, new InvocationHandler() {
                private int idx = -1;
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("next".equals(method.getName())) {
                        idx++;
                        return idx < lista.size();
                    }
                    Usuario u = (idx >= 0 && idx < lista.size()) ? lista.get(idx) : null;
                    if (u != null) {
                        if ("getInt".equals(method.getName())) return u.getId();
                        if ("getString".equals(method.getName())) {
                            String c = (String) args[0];
                            if ("nome".equalsIgnoreCase(c)) return u.getNome();
                            if ("cpf".equalsIgnoreCase(c)) return u.getCpf();
                            if ("email".equalsIgnoreCase(c)) return u.getEmail();
                            if ("senha".equalsIgnoreCase(c)) return u.getSenha();
                        }
                        if ("getDouble".equals(method.getName())) return u.getSalario();
                        if ("getDate".equals(method.getName())) return u.getDataNascimento();
                    }
                    if ("close".equals(method.getName())) return null;
                    return null;
                }
            });
        }
    }

    // =========================================================================
    // EXERCÍCIO 1: Implementação do método alterar na camada DAO
    // =========================================================================
    public static class UsuarioDAOExercicio {
        private Connection conexao;

        public UsuarioDAOExercicio() throws Exception {
            conexao = BancoMemoriaExercicios.getConexao();
        }

        /**
         * Exercício 1: Implementação do método alterar(Object objeto)
         * Realiza o UPDATE dos dados do usuário existente com PreparedStatement e controle transacional.
         */
        public Boolean alterar(Object objeto) {
            Usuario oUsuario = (Usuario) objeto;
            PreparedStatement stmt = null;
            String sql = "UPDATE usuario SET nome=?, datanascimento=?, cpf=?, email=?, senha=?, salario=? "
                    + "WHERE id=?";
            try {
                stmt = conexao.prepareStatement(sql);
                stmt.setString(1, oUsuario.getNome());
                stmt.setDate(2, new java.sql.Date(oUsuario.getDataNascimento().getTime()));
                stmt.setString(3, oUsuario.getCpf());
                stmt.setString(4, oUsuario.getEmail());
                stmt.setString(5, oUsuario.getSenha());
                stmt.setDouble(6, oUsuario.getSalario());
                stmt.setInt(7, oUsuario.getId());

                stmt.execute();
                conexao.commit();
                return true;
            } catch (Exception ex) {
                try {
                    System.out.println("Problemas ao alterar o Usuário! Erro: " + ex.getMessage());
                    ex.printStackTrace();
                    conexao.rollback();
                } catch (SQLException e) {
                    System.out.println("Erro no rollback: " + e.getMessage());
                    e.printStackTrace();
                }
                return false;
            }
        }

        public Object carregar(int numero) {
            String sql = "SELECT * FROM usuario WHERE id=?";
            Usuario oUsuario = null;

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setInt(1, numero);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    oUsuario = new Usuario();
                    oUsuario.setId(rs.getInt("id"));
                    oUsuario.setNome(rs.getString("nome"));
                    oUsuario.setCpf(rs.getString("cpf"));
                    oUsuario.setEmail(rs.getString("email"));
                    oUsuario.setSalario(rs.getDouble("salario"));
                    oUsuario.setDataNascimento(rs.getDate("datanascimento"));
                }
                return oUsuario;
            } catch (SQLException ex) {
                System.out.println("Erro ao carregar usuário: " + ex.getMessage());
                ex.printStackTrace();
            }
            return oUsuario;
        }

        /**
         * Exercício 3: Verificação de duplicidade de e-mail desconsiderando o ID atual
         */
        public boolean emailExiste(String email, int idAtual) {
            String sql = "SELECT COUNT(*) as quantidade_email FROM usuario WHERE email = ? AND id <> ?";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setInt(2, idAtual);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("quantidade_email") > 0) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erro ao checar unicidade de e-mail: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Exercício 4: Implementação da operação de exclusão
         */
        public Boolean excluir(int numero) {
            String sql = "DELETE FROM usuario WHERE id=?";
            PreparedStatement stmt = null;
            try {
                stmt = conexao.prepareStatement(sql);
                stmt.setInt(1, numero);
                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    conexao.commit();
                    return true;
                } else {
                    conexao.rollback();
                    return false;
                }
            } catch (Exception ex) {
                try {
                    System.out.println("Problemas ao excluir Usuário! Erro: " + ex.getMessage());
                    ex.printStackTrace();
                    conexao.rollback();
                } catch (SQLException e) {
                    System.out.println("Erro ao executar rollback na exclusão: " + e.getMessage());
                    e.printStackTrace();
                }
                return false;
            }
        }
    }

    // =========================================================================
    // EXERCÍCIO 2: Simulação do Servlet UsuarioCarregar
    // =========================================================================
    public static class SimulaServletUsuarioCarregar {
        public static class MockHttpServletRequest {
            private final Map<String, Object> attributes = new HashMap<>();
            private final Map<String, String> parameters = new HashMap<>();
            private String forwardedPath;

            public void setParameter(String key, String value) { parameters.put(key, value); }
            public String getParameter(String key) { return parameters.get(key); }
            public void setAttribute(String key, Object value) { attributes.put(key, value); }
            public Object getAttribute(String key) { return attributes.get(key); }
            public void setForwardedPath(String path) { this.forwardedPath = path; }
            public String getForwardedPath() { return forwardedPath; }
        }

        public static void processRequest(MockHttpServletRequest request) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                UsuarioDAOExercicio dao = new UsuarioDAOExercicio();
                Usuario oUsuario = (Usuario) dao.carregar(id);

                request.setAttribute("usuario", oUsuario);
                request.setForwardedPath("/cadastros/usuario/usuarioCadastrar.jsp");
            } catch (Exception ex) {
                System.out.println("Problema no Servlet UsuarioCarregar! Erro: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // =========================================================================
    // EXERCÍCIO 3: Validador de Formato e Unicidade de E-mail
    // =========================================================================
    public static class ValidadorEmail {
        private static final Pattern EMAIL_PATTERN =
                Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        public static boolean isEmailValido(String email) {
            if (email == null) return false;
            return EMAIL_PATTERN.matcher(email.trim()).matches();
        }

        public static String validarEmailCadastro(String email, int idAtual, UsuarioDAOExercicio dao) {
            if (!isEmailValido(email)) {
                return "6_INVALIDO";
            }
            if (dao.emailExiste(email, idAtual)) {
                return "6_DUPLICADO";
            }
            return "OK";
        }
    }

    // =========================================================================
    // MÉTODO PRINCIPAL: Execução e verificação dos 4 exercícios
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println(" UniFEF - Laboratório de Programação III");
        System.out.println(" Prof. Jefferson Passerini");
        System.out.println(" Resolução dos Exercícios Práticos");
        System.out.println("===================================================================\n");

        BancoMemoriaExercicios.inicializar();

        try {
            UsuarioDAOExercicio dao = new UsuarioDAOExercicio();

            // -----------------------------------------------------------------
            // TESTE EXERCÍCIO 1: Alteração de Dados
            // -----------------------------------------------------------------
            System.out.println(">>> Exercício 1: Testando método alterar(Object objeto) <<<");
            Usuario uOriginal = (Usuario) dao.carregar(1);
            System.out.println("Dados originais do ID 1: " + uOriginal);

            Usuario uAlterado = new Usuario(1, "ROBERTO ALVES SILVA", Date.valueOf("1988-03-15"),
                    "12345678909", "roberto.silva@empresa.com", "novaSenha789", 4950.0);
            Boolean alterou = dao.alterar(uAlterado);
            System.out.println("Resultado da execução do UPDATE: " + alterou);

            Usuario uConferido = (Usuario) dao.carregar(1);
            System.out.println("Dados após alteração no banco: " + uConferido);
            System.out.println("Exercício 1 concluído com sucesso!\n");

            // -----------------------------------------------------------------
            // TESTE EXERCÍCIO 2: Servlet UsuarioCarregar
            // -----------------------------------------------------------------
            System.out.println(">>> Exercício 2: Testando Servlet UsuarioCarregar <<<");
            SimulaServletUsuarioCarregar.MockHttpServletRequest req =
                    new SimulaServletUsuarioCarregar.MockHttpServletRequest();
            req.setParameter("id", "2");

            SimulaServletUsuarioCarregar.processRequest(req);
            Usuario usuarioCarregado = (Usuario) req.getAttribute("usuario");
            System.out.println("Atributo 'usuario' no request: " + usuarioCarregado);
            System.out.println("Encaminhamento (forward) para: " + req.getForwardedPath());
            System.out.println("Exercício 2 concluído com sucesso!\n");

            // -----------------------------------------------------------------
            // TESTE EXERCÍCIO 3: Validação e Unicidade de E-mail
            // -----------------------------------------------------------------
            System.out.println(">>> Exercício 3: Validação e Unicidade de E-mail <<<");
            String emailInvalido = "email_sem_arroba.com";
            String emailDuplicado = "fernanda@empresa.com"; // Pertence ao ID 2
            String emailLivre = "novo.contato@dominio.com";

            System.out.println("Validando '" + emailInvalido + "': "
                    + ValidadorEmail.validarEmailCadastro(emailInvalido, 1, dao));
            System.out.println("Validando '" + emailDuplicado + "' para usuário ID 1: "
                    + ValidadorEmail.validarEmailCadastro(emailDuplicado, 1, dao));
            System.out.println("Validando '" + emailDuplicado + "' para o próprio usuário ID 2: "
                    + ValidadorEmail.validarEmailCadastro(emailDuplicado, 2, dao));
            System.out.println("Validando '" + emailLivre + "': "
                    + ValidadorEmail.validarEmailCadastro(emailLivre, 1, dao));
            System.out.println("Exercício 3 concluído com sucesso!\n");

            // -----------------------------------------------------------------
            // TESTE EXERCÍCIO 4: Exclusão de Registro
            // -----------------------------------------------------------------
            System.out.println(">>> Exercício 4: Testando método excluir(int numero) <<<");
            System.out.println("Tentando excluir usuário ID 2...");
            Boolean excluiu = dao.excluir(2);
            System.out.println("Exclusão bem-sucedida? " + excluiu);

            Usuario buscaExcluido = (Usuario) dao.carregar(2);
            System.out.println("Busca pelo ID 2 após exclusão: " + buscaExcluido + " (esperado: null)");

            System.out.println("Tentando excluir ID inexistente (999)...");
            Boolean excluiuInexistente = dao.excluir(999);
            System.out.println("Exclusão de ID inexistente: " + excluiuInexistente + " (esperado: false)");
            System.out.println("Exercício 4 concluído com sucesso!\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("===================================================================");
        System.out.println(" Todos os exercícios foram executados e validados!");
        System.out.println("===================================================================");
    }
}
