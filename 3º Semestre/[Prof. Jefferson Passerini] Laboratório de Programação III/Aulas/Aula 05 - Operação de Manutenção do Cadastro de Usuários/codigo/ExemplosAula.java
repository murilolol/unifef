/*
 * Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Manutenção do Cadastro de Usuários (DAO, Servlets e Validações)
 *
 * Como compilar e executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
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

public class ExemplosAula {

    // =========================================================================
    // 1. MODELO DE DOMÍNIO: Usuario
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
    // 2. INTERFACE DAO GENÉRICA: GenericDAO
    // =========================================================================
    public interface GenericDAO {
        Boolean cadastrar(Object objeto);
        Boolean inserir(Object objeto);
        Boolean alterar(Object objeto);
        Boolean excluir(int numero);
        Object carregar(int numero);
        List<Object> listar();
    }

    // =========================================================================
    // 3. CLASSE UTILITÁRIA: DocumentoValidador (Validação de CPF e CNPJ)
    // =========================================================================
    public static class DocumentoValidador {

        public static boolean isCPF(String cpf) {
            if (cpf == null) return false;
            cpf = cpf.replaceAll("[^\\d]", "");
            if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

            try {
                int d1 = 0, d2 = 0;
                for (int i = 0; i < 9; i++) {
                    int digito = cpf.charAt(i) - '0';
                    d1 += digito * (10 - i);
                    d2 += digito * (11 - i);
                }

                d1 = 11 - (d1 % 11);
                d1 = (d1 > 9) ? 0 : d1;
                d2 += d1 * 2;
                d2 = 11 - (d2 % 11);
                d2 = (d2 > 9) ? 0 : d2;

                return d1 == (cpf.charAt(9) - '0') && d2 == (cpf.charAt(10) - '0');
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean isCNPJ(String cnpj) {
            if (cnpj == null) return false;
            cnpj = cnpj.replaceAll("[^\\d]", "");
            if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

            try {
                int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
                int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

                int d1 = 0, d2 = 0;

                for (int i = 0; i < 12; i++) {
                    int digito = cnpj.charAt(i) - '0';
                    d1 += digito * pesos1[i];
                    d2 += digito * pesos2[i];
                }

                d1 = d1 % 11;
                d1 = (d1 < 2) ? 0 : 11 - d1;
                d2 += d1 * pesos2[12];
                d2 = d2 % 11;
                d2 = (d2 < 2) ? 0 : 11 - d2;

                return d1 == (cnpj.charAt(12) - '0') && d2 == (cnpj.charAt(13) - '0');
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean isDocumentoValido(String documento) {
            if (documento == null) return false;
            documento = documento.replaceAll("[^\\d]", "");
            if (documento.length() == 11) {
                return isCPF(documento);
            } else if (documento.length() == 14) {
                return isCNPJ(documento);
            }
            return false;
        }
    }

    // =========================================================================
    // 4. BANCO DE DADOS EM MEMÓRIA E PROXY JDBC (Permite compilar sem driver externo)
    // =========================================================================
    public static class BancoMemoria {
        private static final Map<Integer, Usuario> tabelaUsuario = new HashMap<>();
        private static int sequenceId = 1;

        static {
            // Carga inicial para testes de consulta e unicidade
            Usuario admin = new Usuario(sequenceId++, "ADMINISTRADOR", Date.valueOf("1990-01-01"),
                    "12345678909", "admin@curso.com.br", "123456", 5000.0);
            tabelaUsuario.put(admin.getId(), admin);
        }

        public static synchronized Connection criarConexaoSimulada() {
            ClassLoader cl = Connection.class.getClassLoader();
            return (Connection) Proxy.newProxyInstance(cl, new Class<?>[]{Connection.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String nomeMetodo = method.getName();
                    if ("prepareStatement".equals(nomeMetodo)) {
                        String sql = (String) args[0];
                        return criarPreparedStatementSimulado(sql);
                    } else if ("commit".equals(nomeMetodo)) {
                        return null;
                    } else if ("rollback".equals(nomeMetodo)) {
                        return null;
                    } else if ("close".equals(nomeMetodo)) {
                        return null;
                    }
                    return null;
                }
            });
        }

        private static PreparedStatement criarPreparedStatementSimulado(String sql) {
            ClassLoader cl = PreparedStatement.class.getClassLoader();
            return (PreparedStatement) Proxy.newProxyInstance(cl, new Class<?>[]{PreparedStatement.class}, new InvocationHandler() {
                private final Map<Integer, Object> parametros = new HashMap<>();

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String mName = method.getName();
                    if (mName.startsWith("set") && args != null && args.length >= 2) {
                        parametros.put((Integer) args[0], args[1]);
                        return null;
                    }
                    if ("execute".equals(mName)) {
                        if (sql.toLowerCase().startsWith("insert")) {
                            int novoId = sequenceId++;
                            String nome = (String) parametros.get(1);
                            Date dtNasc = (Date) parametros.get(2);
                            String cpf = (String) parametros.get(3);
                            String email = (String) parametros.get(4);
                            String senha = (String) parametros.get(5);
                            double salario = (Double) parametros.get(6);
                            Usuario u = new Usuario(novoId, nome, dtNasc, cpf, email, senha, salario);
                            tabelaUsuario.put(novoId, u);
                            return true;
                        }
                        return true;
                    }
                    if ("executeQuery".equals(mName)) {
                        if (sql.contains("count(*) as quantidade_cpf")) {
                            String cpfBuscado = (String) parametros.get(1);
                            boolean existe = tabelaUsuario.values().stream().anyMatch(u -> u.getCpf().equals(cpfBuscado));
                            return criarResultSetSimuladoCount(existe ? 1 : 0);
                        } else if (sql.toLowerCase().contains("where id=?") || sql.toLowerCase().contains("where id = ?")) {
                            int idBuscado = (Integer) parametros.get(1);
                            Usuario u = tabelaUsuario.get(idBuscado);
                            List<Usuario> lista = new ArrayList<>();
                            if (u != null) lista.add(u);
                            return criarResultSetSimuladoUsuarios(lista);
                        } else if (sql.toLowerCase().contains("select * from usuario")) {
                            return criarResultSetSimuladoUsuarios(new ArrayList<>(tabelaUsuario.values()));
                        }
                    }
                    if ("close".equals(mName)) return null;
                    return null;
                }
            });
        }

        private static ResultSet criarResultSetSimuladoCount(int quantidade) {
            ClassLoader cl = ResultSet.class.getClassLoader();
            return (ResultSet) Proxy.newProxyInstance(cl, new Class<?>[]{ResultSet.class}, new InvocationHandler() {
                private int cursor = 0;
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String mName = method.getName();
                    if ("next".equals(mName)) {
                        cursor++;
                        return cursor == 1;
                    }
                    if ("getInt".equals(mName)) {
                        return quantidade;
                    }
                    if ("close".equals(mName)) return null;
                    return null;
                }
            });
        }

        private static ResultSet criarResultSetSimuladoUsuarios(List<Usuario> usuarios) {
            ClassLoader cl = ResultSet.class.getClassLoader();
            return (ResultSet) Proxy.newProxyInstance(cl, new Class<?>[]{ResultSet.class}, new InvocationHandler() {
                private int index = -1;
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String mName = method.getName();
                    if ("next".equals(mName)) {
                        index++;
                        return index < usuarios.size();
                    }
                    Usuario atual = (index >= 0 && index < usuarios.size()) ? usuarios.get(index) : null;
                    if (atual != null) {
                        if ("getInt".equals(mName)) return atual.getId();
                        if ("getString".equals(mName)) {
                            String col = (String) args[0];
                            if ("nome".equalsIgnoreCase(col)) return atual.getNome();
                            if ("cpf".equalsIgnoreCase(col)) return atual.getCpf();
                            if ("email".equalsIgnoreCase(col)) return atual.getEmail();
                            if ("senha".equalsIgnoreCase(col)) return atual.getSenha();
                        }
                        if ("getDouble".equals(mName)) return atual.getSalario();
                        if ("getDate".equals(mName)) return atual.getDataNascimento();
                    }
                    if ("close".equals(mName)) return null;
                    return null;
                }
            });
        }
    }

    public static class SingleConnection {
        public static Connection getConnection() throws Exception {
            return BancoMemoria.criarConexaoSimulada();
        }
    }

    // =========================================================================
    // 5. CAMADA DAO: UsuarioDAO (Fiel ao código do professor)
    // =========================================================================
    public static class UsuarioDAO implements GenericDAO {

        private Connection conexao;

        public UsuarioDAO() throws Exception {
            conexao = SingleConnection.getConnection();
        }

        @Override
        public Boolean cadastrar(Object objeto) {
            Usuario oUsuario = (Usuario) objeto;
            Boolean retorno = false;
            if (oUsuario.getId() == 0) {
                retorno = this.inserir(oUsuario);
            } else {
                retorno = this.alterar(oUsuario);
            }
            return retorno;
        }

        @Override
        public Boolean inserir(Object objeto) {
            Usuario oUsuario = (Usuario) objeto;
            PreparedStatement stmt = null;
            String sql = "insert into usuario (nome, datanascimento, cpf, email, senha, salario) "
                    + "values (?,?,?,?,?,?)";
            try {
                stmt = conexao.prepareStatement(sql);
                stmt.setString(1, oUsuario.getNome());
                stmt.setDate(2, new java.sql.Date(oUsuario.getDataNascimento().getTime()));
                stmt.setString(3, oUsuario.getCpf());
                stmt.setString(4, oUsuario.getEmail());
                stmt.setString(5, oUsuario.getSenha());
                stmt.setDouble(6, oUsuario.getSalario());
                stmt.execute();
                conexao.commit();
                return true;
            } catch (Exception ex) {
                try {
                    System.out.println("Problemas ao cadastrar a Usuário! Erro: " + ex.getMessage());
                    ex.printStackTrace();
                    conexao.rollback();
                } catch (SQLException e) {
                    System.out.println("Erro:" + e.getMessage());
                    e.printStackTrace();
                }
                return false;
            }
        }

        @Override
        public Boolean alterar(Object objeto) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boolean excluir(int numero) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object carregar(int numero) {
            String sql = "Select * from usuario where id=?";
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
                System.out.println("Erro ao listar usuários: " + ex.getMessage());
                ex.printStackTrace();
            }

            return oUsuario;
        }

        @Override
        public List<Object> listar() {
            List<Object> resultado = new ArrayList<>();
            String sql = "Select * from usuario order by id";

            try (PreparedStatement stmt = conexao.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Usuario oUsuario = new Usuario();
                    oUsuario.setId(rs.getInt("id"));
                    oUsuario.setNome(rs.getString("nome"));
                    oUsuario.setCpf(rs.getString("cpf"));
                    oUsuario.setEmail(rs.getString("email"));
                    oUsuario.setSalario(rs.getDouble("salario"));
                    oUsuario.setDataNascimento(rs.getDate("datanascimento"));
                    resultado.add(oUsuario);
                }
            } catch (SQLException ex) {
                System.out.println("Erro ao listar usuários: " + ex.getMessage());
                ex.printStackTrace();
            }

            return resultado;
        }

        public boolean cpfExiste(String cpf) {
            String sql = "SELECT COUNT(*) as quantidade_cpf FROM usuario WHERE cpf = ?";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, cpf);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("quantidade_cpf") > 0)
                        return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    // =========================================================================
    // 6. SIMULAÇÃO DOS SERVLETS (Controller)
    // =========================================================================
    public static class ControladorSimulado {

        public static String simularUsuarioVerificarCPF(String cpf) {
            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                if (usuarioDAO.cpfExiste(cpf)) {
                    return "1"; // CPF já existe
                } else {
                    return "0"; // CPF não cadastrado
                }
            } catch (Exception ex) {
                System.out.println("Problemas no Servlet ao validar cpf de usuario! Erro: " + ex.getMessage());
                return "-1";
            }
        }

        public static String simularUsuarioCadastrar(Map<String, String> params) {
            try {
                UsuarioDAO dao = new UsuarioDAO();

                int id = Integer.parseInt(params.get("id"));
                String nome = params.get("nome");
                Date dataNascimento = Date.valueOf(params.get("datanascimento"));
                String cpf = params.get("cpf");
                String email = params.get("email");
                String senha = params.get("senha");

                // Tratamento para o campo salario R$1.000,00
                String salarioStr = params.get("salario");
                salarioStr = salarioStr.replace("R$", "")
                        .replace(".", "")
                        .replace(",", ".")
                        .trim();

                double salario = Double.parseDouble(salarioStr);

                // Tratamento para o CPF
                if (!DocumentoValidador.isDocumentoValido(cpf)) {
                    return "3"; // CPF inválido
                } else if (dao.cpfExiste(cpf)) {
                    return "4"; // CPF já cadastrado
                } else if (nome == null || nome.isEmpty() || nome.isBlank() || salario <= 0 ||
                        email == null || email.isBlank() || email.isEmpty() || senha == null ||
                        senha.isBlank() || senha.isEmpty()) {
                    return "5"; // Inconsistência em campos obrigatórios
                } else {
                    Usuario oUsuario = new Usuario();
                    oUsuario.setId(id);
                    oUsuario.setNome(nome);
                    oUsuario.setCpf(cpf);
                    oUsuario.setDataNascimento(dataNascimento);
                    oUsuario.setEmail(email);
                    oUsuario.setSenha(senha);
                    oUsuario.setSalario(salario);

                    if (dao.cadastrar(oUsuario)) {
                        return "1"; // Sucesso
                    } else {
                        return "0"; // Falha no banco
                    }
                }
            } catch (Exception ex) {
                System.out.println("Problemas no Servlet ao cadastrar Usuario! Erro: " + ex.getMessage());
                return "0";
            }
        }
    }

    // =========================================================================
    // 7. MÉTODO PRINCIPAL DE EXECUÇÃO
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println(" UniFEF - Laboratório de Programação III");
        System.out.println(" Prof. Jefferson Passerini");
        System.out.println(" Demonstração dos Códigos da Aula: Manutenção de Usuários");
        System.out.println("===================================================================\n");

        // 1. Teste de Validação de Documentos
        System.out.println("--- 1. Teste de DocumentoValidador ---");
        String cpfValido = "12345678909";
        String cpfInvalido = "12345678900";
        String cpfRepetido = "11111111111";
        System.out.println("CPF " + cpfValido + " é válido? " + DocumentoValidador.isCPF(cpfValido));
        System.out.println("CPF " + cpfInvalido + " é válido? " + DocumentoValidador.isCPF(cpfInvalido));
        System.out.println("CPF " + cpfRepetido + " é válido? " + DocumentoValidador.isCPF(cpfRepetido));

        // 2. Teste da camada DAO
        System.out.println("\n--- 2. Teste da Camada DAO (UsuarioDAO) ---");
        try {
            UsuarioDAO dao = new UsuarioDAO();

            // Checagem de CPF pré-existente
            System.out.println("CPF 12345678909 existe no banco? " + dao.cpfExiste("12345678909"));
            System.out.println("CPF 52998224725 existe no banco? " + dao.cpfExiste("52998224725"));

            // Inclusão via método cadastrar (id == 0)
            Usuario novo = new Usuario(0, "MARIA SILVA", Date.valueOf("1995-05-20"),
                    "52998224725", "maria@email.com", "senha123", 3500.50);
            Boolean inseriu = dao.cadastrar(novo);
            System.out.println("Inclusão de Maria Silva (id=0) foi bem-sucedida? " + inseriu);

            // Carregamento por ID
            Usuario carregado = (Usuario) dao.carregar(1);
            System.out.println("Usuário ID 1 carregado do banco: " + carregado);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Teste dos Servlets Simulados
        System.out.println("\n--- 3. Simulação de Requisições dos Servlets ---");

        // Simulação do UsuarioVerificarCPF
        String respVerif1 = ControladorSimulado.simularUsuarioVerificarCPF("12345678909");
        String respVerif2 = ControladorSimulado.simularUsuarioVerificarCPF("00000000000");
        System.out.println("Servlet UsuarioVerificarCPF (12345678909): " + respVerif1 + " (esperado 1 - existe)");
        System.out.println("Servlet UsuarioVerificarCPF (00000000000): " + respVerif2 + " (esperado 0 - livre)");

        // Simulação do UsuarioCadastrar
        Map<String, String> reqSucesso = new HashMap<>();
        reqSucesso.put("id", "0");
        reqSucesso.put("nome", "CARLOS EDUARDO");
        reqSucesso.put("datanascimento", "1998-10-12");
        reqSucesso.put("cpf", "52998224725");
        reqSucesso.put("email", "carlos@provedor.com");
        reqSucesso.put("senha", "abc@123");
        reqSucesso.put("salario", "R$ 4.500,00");

        String resCad = ControladorSimulado.simularUsuarioCadastrar(reqSucesso);
        System.out.println("Servlet UsuarioCadastrar (Carlos Eduardo): Código " + resCad + " (1 = Sucesso)");

        // Teste de validação: CPF inválido
        Map<String, String> reqCpfInv = new HashMap<>(reqSucesso);
        reqCpfInv.put("cpf", "11122233344");
        System.out.println("Servlet UsuarioCadastrar (CPF Inválido): Código "
                + ControladorSimulado.simularUsuarioCadastrar(reqCpfInv) + " (3 = CPF Inválido)");

        System.out.println("\n===================================================================");
        System.out.println(" Execução finalizada com êxito!");
        System.out.println("===================================================================");
    }
}
