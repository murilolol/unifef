/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini - UniFEF
 * Tema: Configuração de Ambiente e Projeto Java Web
 * 
 * Como compilar:
 *   javac ExemplosAmbienteWeb.java
 * 
 * Como executar:
 *   java ExemplosAmbienteWeb
 */

import java.util.ArrayList;
import java.util.List;

public class ExemplosAmbienteWeb {

    /*
     * ------------------------------------------------------------
     * CAMADA UTILS: br.com.aplcurso.utils
     * Padrão de Projeto Singleton para Conexão Única (Single Connection)
     * ------------------------------------------------------------
     */
    public static class SingleConnectionSimulada {
        private static final String BANCO_URL = "jdbc:postgresql://localhost:5432/aplcurso";
        private static final String USUARIO = "postgres";
        private static final String SENHA = "postgres";
        
        // Instância única mantida na classe
        private static SingleConnectionSimulada instancia;
        private boolean conectada;
        private int totalOperacoesRealizadas;

        // Construtor privado impede instanciação externa direta
        private SingleConnectionSimulada() {
            conectar();
        }

        private void conectar() {
            this.conectada = true;
            this.totalOperacoesRealizadas = 0;
            System.out.println("[SingleConnection] Inicializando conexao unica com: " + BANCO_URL);
        }

        // Método estático sincronizado que garante apenas uma instância
        public static synchronized SingleConnectionSimulada getInstancia() {
            if (instancia == null) {
                instancia = new SingleConnectionSimulada();
            }
            return instancia;
        }

        public void registrarOperacao(String sql) {
            if (!conectada) {
                throw new IllegalStateException("Conexao com banco fechada!");
            }
            totalOperacoesRealizadas++;
            System.out.println("[SingleConnection] Executando SQL #" + totalOperacoesRealizadas + ": " + sql);
        }

        public boolean isConectada() {
            return conectada;
        }

        public int getTotalOperacoesRealizadas() {
            return totalOperacoesRealizadas;
        }
    }

    /*
     * ------------------------------------------------------------
     * CAMADA MODEL: br.com.aplcurso.model
     * Entidade que representa os dados de negócio
     * ------------------------------------------------------------
     */
    public static class UsuarioModel {
        private Integer idUsuario;
        private String nome;
        private String login;
        private String senha;
        private String status;

        public UsuarioModel() {}

        public UsuarioModel(Integer idUsuario, String nome, String login, String senha, String status) {
            this.idUsuario = idUsuario;
            this.nome = nome;
            this.login = login;
            this.senha = senha;
            this.status = status;
        }

        public Integer getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
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
            return "UsuarioModel [id=" + idUsuario + ", nome=" + nome + ", login=" + login + ", status=" + status + "]";
        }
    }

    /*
     * ------------------------------------------------------------
     * CAMADA DAO: br.com.aplcurso.dao
     * Responsável pela persistência e operações com banco de dados
     * ------------------------------------------------------------
     */
    public static class UsuarioDAO {
        private final SingleConnectionSimulada conexao;
        private final List<UsuarioModel> tabelaSimulada;

        public UsuarioDAO() {
            this.conexao = SingleConnectionSimulada.getInstancia();
            this.tabelaSimulada = new ArrayList<>();
            // Dados iniciais de teste
            tabelaSimulada.add(new UsuarioModel(1, "Administrador", "admin", "123456", "A"));
            tabelaSimulada.add(new UsuarioModel(2, "Professor Jefferson", "jefferson", "javaweb", "A"));
        }

        public boolean autenticar(String login, String senha) {
            conexao.registrarOperacao("SELECT * FROM usuario WHERE login = '" + login + "' AND senha = '***' AND status = 'A'");
            for (UsuarioModel u : tabelaSimulada) {
                if (u.getLogin().equals(login) && u.getSenha().equals(senha) && "A".equals(u.getStatus())) {
                    return true;
                }
            }
            return false;
        }

        public boolean salvar(UsuarioModel usuario) {
            conexao.registrarOperacao("INSERT INTO usuario (nome, login, senha, status) VALUES ('" 
                + usuario.getNome() + "', '" + usuario.getLogin() + "', '***', '" + usuario.getStatus() + "')");
            usuario.setIdUsuario(tabelaSimulada.size() + 1);
            tabelaSimulada.add(usuario);
            return true;
        }

        public List<UsuarioModel> listar() {
            conexao.registrarOperacao("SELECT id_usuario, nome, login, status FROM usuario ORDER BY id_usuario");
            return new ArrayList<>(tabelaSimulada);
        }
    }

    /*
     * ------------------------------------------------------------
     * CAMADA CONTROLLER: br.com.aplcurso.controller.usuario
     * Atende requisições do cliente e aciona Model e DAO
     * ------------------------------------------------------------
     */
    public static class UsuarioLoginController {
        private final UsuarioDAO dao;

        public UsuarioLoginController() {
            this.dao = new UsuarioDAO();
        }

        public String processarLogin(String login, String senha) {
            System.out.println("[Controller] Processando requisicao de login para o usuario: " + login);
            if (login == null || login.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
                return "login.jsp?erro=campos_vazios";
            }
            boolean autenticado = dao.autenticar(login, senha);
            if (autenticado) {
                System.out.println("[Controller] Credenciais autenticadas com sucesso!");
                return "index.jsp";
            } else {
                System.out.println("[Controller] Falha de autenticacao.");
                return "login.jsp?erro=credenciais_invalidas";
            }
        }
    }

    /*
     * ------------------------------------------------------------
     * CAMADA FILTER: br.com.aplcurso.filter
     * Intercepta requisições antes de atingirem a Controller/View
     * ------------------------------------------------------------
     */
    public static class AutenticacaoFilter {
        public boolean interceptarRequisicao(String rotaDestino, boolean usuarioAutenticado) {
            System.out.println("[Filter] Interceptando rota: " + rotaDestino);
            if (rotaDestino.equals("login.jsp")) {
                System.out.println("[Filter] Rota publica liberada.");
                return true;
            }
            if (!usuarioAutenticado) {
                System.out.println("[Filter] Acesso negado! Redirecionando para login.jsp");
                return false;
            }
            System.out.println("[Filter] Requisicao permitida para destino: " + rotaDestino);
            return true;
        }
    }

    /*
     * Metodo principal demonstrando o ciclo completo da arquitetura
     */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("UniFEF - Laboratorio de Programacao III - Prof. Jefferson");
        System.out.println("Simulacao do Projeto AplCurso (Padrao MVC Monolitico e Singleton)");
        System.out.println("============================================================\n");

        // 1. Validando o padrao Singleton de Conexao
        System.out.println("--- 1. Teste do Padrao Singleton (SingleConnection) ---");
        SingleConnectionSimulada conn1 = SingleConnectionSimulada.getInstancia();
        SingleConnectionSimulada conn2 = SingleConnectionSimulada.getInstancia();
        
        System.out.println("Referencia Conn 1: " + System.identityHashCode(conn1));
        System.out.println("Referencia Conn 2: " + System.identityHashCode(conn2));
        if (conn1 == conn2) {
            System.out.println("-> SUCESSO: O padrao Singleton garantiu a mesma instancia em memoria!\n");
        } else {
            System.out.println("-> ERRO: Instancias diferentes detectadas!\n");
        }

        // 2. Fluxo da Camada Filter
        System.out.println("--- 2. Teste da Camada Filter (Filtro de Seguranca) ---");
        AutenticacaoFilter filtro = new AutenticacaoFilter();
        boolean sessaoAtiva = false;
        
        System.out.println("Tentando acessar 'index.jsp' sem autenticacao:");
        boolean permitido = filtro.interceptarRequisicao("index.jsp", sessaoAtiva);
        System.out.println("Permitido? " + permitido + "\n");

        // 3. Fluxo da Camada Controller, DAO e Model
        System.out.println("--- 3. Teste da Camada Controller e Autenticacao ---");
        UsuarioLoginController controller = new UsuarioLoginController();
        
        // Tentativa 1: Falha
        String destinoFalha = controller.processarLogin("admin", "senhaIncorreta");
        System.out.println("Destino apos login invalido: " + destinoFalha + "\n");

        // Tentativa 2: Sucesso
        String destinoSucesso = controller.processarLogin("admin", "123456");
        System.out.println("Destino apos login valido: " + destinoSucesso);
        sessaoAtiva = true;
        System.out.println("\nTentando acessar 'index.jsp' agora com sessao autenticada:");
        filtro.interceptarRequisicao("index.jsp", sessaoAtiva);

        // 4. Operacoes adicionais na DAO usando a mesma conexao
        System.out.println("\n--- 4. Listagem e Cadastro de Usuarios via DAO ---");
        UsuarioDAO dao = new UsuarioDAO();
        dao.salvar(new UsuarioModel(null, "Novo Aluno", "aluno", "123", "A"));
        List<UsuarioModel> lista = dao.listar();
        for (UsuarioModel u : lista) {
            System.out.println("  -> " + u);
        }

        System.out.println("\nTotal acumulado de operacoes na SingleConnection: " 
            + SingleConnectionSimulada.getInstancia().getTotalOperacoesRealizadas());
        System.out.println("\n============================================================");
        System.out.println("Demonstracao concluida com exito!");
        System.out.println("============================================================");
    }
}
