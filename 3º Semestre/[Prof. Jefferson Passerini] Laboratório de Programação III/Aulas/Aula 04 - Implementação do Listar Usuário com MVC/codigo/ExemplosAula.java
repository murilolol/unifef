/**
 * Disciplina : Laboratório de Programação III (3º Semestre)
 * Professor  : Prof. Jefferson Passerini
 * Tema       : Implementação do Recurso Listar Usuário com MVC (Model, DAO, Controller)
 * 
 * Como compilar:
 *   javac ExemplosAula.java
 * 
 * Como executar:
 *   java ExemplosAula
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// =============================================================================
// 1. CAMADA MODEL: br.com.aplcurso.model.Usuario
// =============================================================================
class Usuario {

    private int id;
    private String nome;
    private Date dataNascimento;
    private String cpf;
    private String email;
    private String senha;
    private double salario;

    // Construtor vazio inicializando atributos padrão
    public Usuario() {
        this.id = 0;
        this.nome = "";
        this.cpf = "";
        this.email = "";
        this.senha = "";
        this.salario = 0.0;
        this.dataNascimento = null;
    }

    // Construtor completo com todos os parâmetros
    public Usuario(int id, String nome, Date dataNascimento, String cpf, 
                   String email, String senha, double salario) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.salario = salario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        hash = 89 * hash + Objects.hashCode(this.cpf);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Usuario other = (Usuario) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.cpf, other.cpf);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = (dataNascimento != null) ? sdf.format(dataNascimento) : "N/D";
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return String.format("ID: %-4d | Nome: %-25s | CPF: %-14s | Email: %-25s | Nasc: %-10s | Salário: %s",
                id, nome, cpf, email, dataFormatada, nf.format(salario));
    }
}

// =============================================================================
// 2. INTERFACE DAO: br.com.aplcurso.dao.GenericDAO
// =============================================================================
interface GenericDAO {
    public Boolean cadastrar(Object objeto);
    public Boolean inserir(Object objeto);
    public Boolean alterar(Object objeto);
    public Boolean excluir(int numero);
    public Object carregar(int numero);
    public List<Object> listar();
}

// =============================================================================
// 3. GERENCIADOR DE CONEXÃO: br.com.aplcurso.utils.SingleConnection
// =============================================================================
class SingleConnection {
    private static Connection connection = null;

    // Retorna conexão JDBC ativa (ou emula caso PostgreSQL não esteja ativo localmente)
    public static synchronized Connection getConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            try {
                // Configurações reais do PostgreSQL conforme padrão de aula
                String url = "jdbc:postgresql://localhost:5432/aplcurso";
                String usuario = "postgres";
                String senha = "postgres";
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, usuario, senha);
            } catch (Exception ex) {
                // Fallback gracioso para testes acadêmicos sem banco local iniciado
                System.out.println("[Aviso] Banco PostgreSQL local offline. Utilizando mock de conexão para simulação.");
            }
        }
        return connection;
    }
}

// =============================================================================
// 4. CAMADA DAO: br.com.aplcurso.dao.UsuarioDAO
// =============================================================================
class UsuarioDAO implements GenericDAO {

    private Connection conexao;
    // Base de dados em memória para simulação quando não houver SGBD ativo
    private static final List<Usuario> baseEmMemoria = new ArrayList<>();

    static {
        // Dados de teste correspondentes à demonstração da Figura 56 do material
        baseEmMemoria.add(new Usuario(1, "João José Gomes da Silva", new Date(90, 7, 10), "08243060073", "joaojosegomes@gmail.com", "123456", 5200.00));
        baseEmMemoria.add(new Usuario(2, "Maria Aparecida Santos", new Date(88, 3, 22), "12345678901", "maria.santos@email.com", "senha789", 6450.50));
        baseEmMemoria.add(new Usuario(3, "Carlos Eduardo Ferreira", new Date(95, 10, 15), "98765432100", "carlos.ferreira@email.com", "mudar123", 3800.00));
    }

    public UsuarioDAO() throws Exception {
        this.conexao = SingleConnection.getConnection();
    }

    @Override
    public Boolean cadastrar(Object objeto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean inserir(Object objeto) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Implementação fiel do método listar() apresentado na Seção 5.1
     */
    @Override
    public List<Object> listar() {
        List<Object> resultado = new ArrayList<>();
        
        // Se a conexão física com o banco de dados estiver disponível
        if (conexao != null) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String sql = "Select * from usuario order by id";
            try {
                stmt = conexao.prepareStatement(sql);
                rs = stmt.executeQuery();
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
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Fallback para execução autônoma do exemplo
            resultado.addAll(baseEmMemoria);
        }

        return resultado;
    }
}

// =============================================================================
// 5. CLASSE PRINCIPAL EXECUTÁVEL: Simulação do Ciclo MVC
// =============================================================================
public class ExemplosAula {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println(" Laboratório de Programação III - Prof. Jefferson Passerini");
        System.out.println(" Simulação Completa da Aula 05: CRUD Usuario - Recurso Listar (MVC)");
        System.out.println("===============================================================================\n");

        try {
            System.out.println("[1] Instanciando DAO e consultando camada de persistência...");
            GenericDAO dao = new UsuarioDAO();

            System.out.println("[2] Executando dao.listar() [Equivalente à chamada dentro do Servlet UsuarioListar]...");
            List<Object> listaObjetos = dao.listar();

            System.out.println("[3] Dados retornados com sucesso! Total de registros: " + listaObjetos.size());
            System.out.println("\n--- Visualização da Tabela de Usuários (Simulação da View usuario.jsp) ---");
            System.out.println(String.format("%-4s | %-25s | %-14s | %-25s | %-10s | %-12s",
                    "ID", "NOME", "CPF", "EMAIL", "NASCIMENTO", "SALÁRIO"));
            System.out.println("--------------------------------------------------------------------------------------------------------");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (Object obj : listaObjetos) {
                Usuario u = (Usuario) obj;
                String dataStr = (u.getDataNascimento() != null) ? sdf.format(u.getDataNascimento()) : "N/D";
                String salStr = nf.format(u.getSalario());

                System.out.println(String.format("%-4d | %-25s | %-14s | %-25s | %-10s | %-12s",
                        u.getId(), u.getNome(), u.getCpf(), u.getEmail(), dataStr, salStr));
            }

            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("\n[4] Teste de integridade de equals() e hashCode() selecionados em aula (id e cpf):");
            Usuario u1 = new Usuario(1, "A", null, "08243060073", "a@a.com", "1", 1000.0);
            Usuario u2 = new Usuario(1, "B", null, "08243060073", "b@b.com", "2", 9000.0);
            System.out.println("u1.equals(u2) deve ser TRUE pois id e cpf são iguais: " + u1.equals(u2));
            System.out.println("HashCode u1: " + u1.hashCode() + " | HashCode u2: " + u2.hashCode());

        } catch (Exception ex) {
            System.out.println("Problemas no fluxo de execução: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
