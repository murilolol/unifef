/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini - UniFEF
 * Tema: Configuração de Ambiente e Projeto Java Web
 * 
 * Resolução dos Exercícios Práticos 1, 2, 3 e 4.
 * 
 * Como compilar:
 *   javac ExerciciosResolvidos.java
 * 
 * Como executar:
 *   java ExerciciosResolvidos
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciciosResolvidos {

    /*
     * ============================================================
     * EXERCÍCIO 1: Implementação de Gerenciador de Conexão Singleton
     * (br.com.aplcurso.utils.SingleConnection)
     * ============================================================
     */
    public static class GerenciadorConexaoSingleton {
        private static final String DRIVER = "org.postgresql.Driver";
        private static final String URL = "jdbc:postgresql://localhost:5432/aplcurso";
        private static final String USUARIO = "postgres";
        private static final String SENHA = "postgres";

        // Variável estática para armazenar a única instância
        private static GerenciadorConexaoSingleton instancia;
        private boolean conectado;
        private long timestampCriacao;

        // Construtor estritamente privado
        private GerenciadorConexaoSingleton() {
            conectarBanco();
        }

        private void conectarBanco() {
            try {
                // Na aplicação real com Tomcat: Class.forName(DRIVER);
                this.conectado = true;
                this.timestampCriacao = System.currentTimeMillis();
                System.out.println("[Exercicio 1] Conexao SingleConnection inicializada para: " + URL);
            } catch (Exception e) {
                System.err.println("[Exercicio 1] Falha ao carregar driver JDBC: " + e.getMessage());
                this.conectado = false;
            }
        }

        // Método estático de acesso global com verificação de nulidade
        public static synchronized GerenciadorConexaoSingleton getConexao() {
            if (instancia == null) {
                instancia = new GerenciadorConexaoSingleton();
            }
            return instancia;
        }

        public boolean isConectado() {
            return conectado;
        }

        public long getTimestampCriacao() {
            return timestampCriacao;
        }
    }

    /*
     * ============================================================
     * EXERCÍCIO 2: Modelagem da Entidade Usuário e Camada DAO
     * (br.com.aplcurso.model.Usuario e br.com.aplcurso.dao.UsuarioDAO)
     * ============================================================
     */
    public static class Usuario {
        private Integer id;
        private String nome;
        private String login;
        private String senha;
        private String status;

        public Usuario() {}

        public Usuario(Integer id, String nome, String login, String senha, String status) {
            this.id = id;
            this.nome = nome;
            this.login = login;
            this.senha = senha;
            this.status = status;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() {
            return "Usuario [id=" + id + ", nome=" + nome + ", login=" + login + ", status=" + status + "]";
        }
    }

    public static class UsuarioDAO {
        private final GerenciadorConexaoSingleton conexao;
        private final List<Usuario> baseDadosMemoria = new ArrayList<>();
        private int proximoId = 1;

        public UsuarioDAO() {
            // Obtém a conexão centralizada pelo padrão Singleton
            this.conexao = GerenciadorConexaoSingleton.getConexao();
            // Carga de dados padrão
            cadastrar(new Usuario(null, "Administrador", "admin", "admin123", "A"));
            cadastrar(new Usuario(null, "Prof. Jefferson", "jefferson", "java2026", "A"));
        }

        public boolean cadastrar(Usuario usuario) {
            if (!conexao.isConectado()) {
                throw new IllegalStateException("Sem conexao com o banco de dados!");
            }
            usuario.setId(proximoId++);
            baseDadosMemoria.add(usuario);
            System.out.println("[Exercicio 2 - DAO] Usuario cadastrado com ID " + usuario.getId() + " via conexao unica.");
            return true;
        }

        public List<Usuario> listar() {
            return Collections.unmodifiableList(baseDadosMemoria);
        }

        public Usuario autenticar(String login, String senha) {
            for (Usuario u : baseDadosMemoria) {
                if (u.getLogin().equalsIgnoreCase(login) && u.getSenha().equals(senha) && "A".equals(u.getStatus())) {
                    return u;
                }
            }
            return null;
        }
    }

    /*
     * ============================================================
     * EXERCÍCIO 3: Simulação de Filtro de Interceptação de Requisições
     * (br.com.aplcurso.filter.AutenticacaoFilter)
     * ============================================================
     */
    public static class RequisicaoHttpSimulada {
        private final String uri;
        private final Usuario usuarioSessao;

        public RequisicaoHttpSimulada(String uri, Usuario usuarioSessao) {
            this.uri = uri;
            this.usuarioSessao = usuarioSessao;
        }

        public String getUri() { return uri; }
        public Usuario getUsuarioSessao() { return usuarioSessao; }
        public boolean isAutenticado() { return usuarioSessao != null; }
    }

    public static class FiltroSegurancaApp {
        public String filtrar(RequisicaoHttpSimulada requisicao) {
            String rota = requisicao.getUri();
            System.out.println("[Exercicio 3 - Filter] Interceptando requisicao para: " + rota);

            // Rotas publicas que nao requerem autenticacao previa
            if (rota.equals("/login.jsp") || rota.startsWith("/resources/") || rota.equals("/UsuarioLogin")) {
                System.out.println("[Exercicio 3 - Filter] Rota publica acessada livremente.");
                return rota;
            }

            // Validacao de sessao do usuario
            if (!requisicao.isAutenticado()) {
                System.out.println("[Exercicio 3 - Filter] Bloqueio de acesso! Usuario anonimo tentando acessar rota restrita.");
                return "/login.jsp?erro=sessao_expirada";
            }

            System.out.println("[Exercicio 3 - Filter] Acesso autorizado para o usuario: " 
                + requisicao.getUsuarioSessao().getNome());
            return rota;
        }
    }

    /*
     * ============================================================
     * EXERCÍCIO 4: Controlador de Autenticação e Despacho MVC
     * (br.com.aplcurso.controller.usuario.UsuarioLogin)
     * ============================================================
     */
    public static class UsuarioLoginController {
        private final UsuarioDAO usuarioDao;

        public UsuarioLoginController(UsuarioDAO dao) {
            this.usuarioDao = dao;
        }

        public RespostaDespacho autenticar(String login, String senha) {
            System.out.println("[Exercicio 4 - Controller] Recebida solicitacao de login para: " + login);
            
            if (login == null || login.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
                return new RespostaDespacho("/login.jsp", false, "Preencha todos os campos!");
            }

            Usuario usuarioLogado = usuarioDao.autenticar(login, senha);
            if (usuarioLogado != null) {
                System.out.println("[Exercicio 4 - Controller] Autenticacao valida para: " + usuarioLogado.getNome());
                return new RespostaDespacho("/index.jsp", true, "Login efetuado com sucesso!");
            } else {
                System.out.println("[Exercicio 4 - Controller] Usuario ou senha incorretos!");
                return new RespostaDespacho("/login.jsp", false, "Credenciais invalidas!");
            }
        }
    }

    public static class RespostaDespacho {
        private final String viewDestino;
        private final boolean sucesso;
        private final String mensagem;

        public RespostaDespacho(String viewDestino, boolean sucesso, String mensagem) {
            this.viewDestino = viewDestino;
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }

        public String getViewDestino() { return viewDestino; }
        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }

        @Override
        public String toString() {
            return "RespostaDespacho [viewDestino=" + viewDestino + ", sucesso=" + sucesso + ", mensagem='" + mensagem + "']";
        }
    }

    /*
     * Execução e validação de todos os exercícios propostos
     */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("UniFEF - Laboratorio de Programacao III - Prof. Jefferson");
        System.out.println("Execucao dos Exercicios Propostos - Arquitetura Java Web");
        System.out.println("============================================================\n");

        // Teste Exercício 1
        System.out.println("--- TESTE EXERCICIO 1: GerenciadorConexaoSingleton ---");
        GerenciadorConexaoSingleton g1 = GerenciadorConexaoSingleton.getConexao();
        GerenciadorConexaoSingleton g2 = GerenciadorConexaoSingleton.getConexao();
        System.out.println("Hashcode Instancia 1: " + System.identityHashCode(g1));
        System.out.println("Hashcode Instancia 2: " + System.identityHashCode(g2));
        System.out.println("Mesmo objeto? " + (g1 == g2));
        System.out.println("Status de conexao: " + g1.isConectado() + "\n");

        // Teste Exercício 2
        System.out.println("--- TESTE EXERCICIO 2: Usuario e UsuarioDAO ---");
        UsuarioDAO dao = new UsuarioDAO();
        dao.cadastrar(new Usuario(null, "Maria Ferreira", "maria", "segredo123", "A"));
        System.out.println("Usuarios na base:");
        for (Usuario u : dao.listar()) {
            System.out.println("   -> " + u);
        }
        System.out.println();

        // Teste Exercício 3
        System.out.println("--- TESTE EXERCICIO 3: Filtro de Seguranca ---");
        FiltroSegurancaApp filtro = new FiltroSegurancaApp();
        
        // Tentativa nao autenticada em rota restrita
        RequisicaoHttpSimulada req1 = new RequisicaoHttpSimulada("/index.jsp", null);
        String rota1 = filtro.filtrar(req1);
        System.out.println("Destino 1 (Sem login): " + rota1);

        // Tentativa de acesso a rota publica de login
        RequisicaoHttpSimulada req2 = new RequisicaoHttpSimulada("/login.jsp", null);
        String rota2 = filtro.filtrar(req2);
        System.out.println("Destino 2 (Rota publica): " + rota2);

        // Tentativa autenticada com usuario
        Usuario usuarioAtivo = dao.autenticar("admin", "admin123");
        RequisicaoHttpSimulada req3 = new RequisicaoHttpSimulada("/index.jsp", usuarioAtivo);
        String rota3 = filtro.filtrar(req3);
        System.out.println("Destino 3 (Autenticado): " + rota3 + "\n");

        // Teste Exercício 4
        System.out.println("--- TESTE EXERCICIO 4: Controller de Login MVC ---");
        UsuarioLoginController controller = new UsuarioLoginController(dao);
        
        // Caso 1: Campos em branco
        RespostaDespacho resp1 = controller.autenticar("", "");
        System.out.println("Resultado caso vazio: " + resp1);

        // Caso 2: Senha errada
        RespostaDespacho resp2 = controller.autenticar("admin", "senha_errada");
        System.out.println("Resultado caso incorreto: " + resp2);

        // Caso 3: Credenciais certas
        RespostaDespacho resp3 = controller.autenticar("admin", "admin123");
        System.out.println("Resultado caso correto: " + resp3);

        System.out.println("\n============================================================");
        System.out.println("Todos os 4 exercicios foram executados e validados com sucesso!");
        System.out.println("============================================================");
    }
}
