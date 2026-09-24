/**
 * Disciplina : Engenharia de Software I - UniFEF
 * Professor  : Marcelo Boer
 * Tema       : Atores, Matriz de Contexto Geral e Caso de Uso Realizar Login: UC01 (Secao 1.2 a 1.4 e 1.7.1)
 *
 * Como compilar e executar:
 *   javac AutenticacaoEControleAcesso.java
 *   java AutenticacaoEControleAcesso
 */

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutenticacaoEControleAcesso {

    /**
     * Identificadores formais dos Casos de Uso catalogados na Secao 1.5 e 1.4.
     */
    public enum CasoDeUso {
        UC01_REALIZAR_LOGIN("UC01", "Realizar Login"),
        UC02_CADASTRAR_USUARIO("UC02_USR", "Cadastrar Usuario"),
        UC02_CADASTRAR_CLIENTE("UC02", "Cadastrar Cliente"),
        UC03_LISTAR_CLIENTE("UC03", "Listar Clientes"),
        UC04_CARREGAR_CLIENTE("UC04", "Carregar Cliente"),
        UC05_ALTERAR_CLIENTE("UC05", "Alterar Cliente"),
        UC06_EXCLUIR_CLIENTE("UC06", "Excluir Cliente"),
        UC08_CONSULTAR_AUDITORIA("UC08", "Consultar Auditoria");

        private final String codigo;
        private final String titulo;

        CasoDeUso(String codigo, String titulo) {
            this.codigo = codigo;
            this.titulo = titulo;
        }

        public String getCodigo() { return codigo; }
        public String getTitulo() { return titulo; }
    }

    /**
     * Secao 1.2 e 1.3: Hierarquia de Atores do Sistema.
     * AtorBase (Abstrato) -> Administrador e OperadorVendas (Especializados via Heranca).
     */
    public abstract static class AtorBase {
        private final String idAtor;
        private final String nomePapel;
        protected final Set<CasoDeUso> permissoes;

        public AtorBase(String idAtor, String nomePapel) {
            this.idAtor = idAtor;
            this.nomePapel = nomePapel;
            this.permissoes = EnumSet.noneOf(CasoDeUso.class);
            // Heranca: Qualquer usuario do sistema tem permissao para autenticar
            this.permissoes.add(CasoDeUso.UC01_REALIZAR_LOGIN);
        }

        public boolean podeAcessar(CasoDeUso uc) {
            return permissoes.contains(uc);
        }

        public String getIdAtor() { return idAtor; }
        public String getNomePapel() { return nomePapel; }
        public Set<CasoDeUso> getPermissoes() { return permissoes; }
    }

    /**
     * Ator Especializado: Administrador (ACT02)
     * Possui permissoes de gestao, cadastro de usuario, auditoria e exclusao.
     */
    public static class AtorAdministrador extends AtorBase {
        public AtorAdministrador() {
            super("ACT02", "Administrador");
            this.permissoes.add(CasoDeUso.UC02_CADASTRAR_USUARIO);
            this.permissoes.add(CasoDeUso.UC06_EXCLUIR_CLIENTE);
            this.permissoes.add(CasoDeUso.UC08_CONSULTAR_AUDITORIA);
        }
    }

    /**
     * Ator Especializado: Operador de Vendas (ACT03)
     * Responsavel pelas operacoes rotineiras de cadastro, consulta e alteracao.
     */
    public static class AtorOperadorVendas extends AtorBase {
        public AtorOperadorVendas() {
            super("ACT03", "Operador de Vendas");
            this.permissoes.add(CasoDeUso.UC02_CADASTRAR_CLIENTE);
            this.permissoes.add(CasoDeUso.UC03_LISTAR_CLIENTE);
            this.permissoes.add(CasoDeUso.UC04_CARREGAR_CLIENTE);
            this.permissoes.add(CasoDeUso.UC05_ALTERAR_CLIENTE);
        }
    }

    /**
     * Registro de conta para autenticacao em memoria.
     */
    public static class ContaUsuario {
        private final String login;
        private final String senhaHash;
        private final String nome;
        private final AtorBase papelAtor;
        private boolean ativo;
        private int tentativasIncorretas;

        public ContaUsuario(String login, String senhaHash, String nome, AtorBase papelAtor, boolean ativo) {
            this.login = login;
            this.senhaHash = senhaHash;
            this.nome = nome;
            this.papelAtor = papelAtor;
            this.ativo = ativo;
            this.tentativasIncorretas = 0;
        }

        public String getLogin() { return login; }
        public String getSenhaHash() { return senhaHash; }
        public String getNome() { return nome; }
        public AtorBase getPapelAtor() { return papelAtor; }
        public boolean isAtivo() { return ativo; }
        public int getTentativasIncorretas() { return tentativasIncorretas; }
        public void registrarFalha() { this.tentativasIncorretas++; }
        public void zerarFalhas() { this.tentativasIncorretas = 0; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
    }

    /**
     * Sessao gerada como pos-condicao do UC01.
     */
    public static class SessaoUsuario {
        private final String token;
        private final ContaUsuario conta;
        private final long timestampInicio;

        public SessaoUsuario(String token, ContaUsuario conta) {
            this.token = token;
            this.conta = conta;
            this.timestampInicio = System.currentTimeMillis();
        }

        public ContaUsuario getConta() { return conta; }
        public String getToken() { return token; }
    }

    /**
     * Secao 1.7.1: Servico de Autenticacao executando UC01 - Realizar Login.
     */
    public static class ServicoAutenticacao {
        private final Map<String, ContaUsuario> baseContas = new HashMap<>();

        public void cadastrarConta(ContaUsuario conta) {
            baseContas.put(conta.getLogin(), conta);
        }

        /**
         * Executa o UC01 conforme fluxo principal e fluxos alternativos da especificacao.
         */
        public ResultadoLogin executarUC01RealizarLogin(String loginInformado, String senhaInformada) {
            System.out.println("-> [UC01: Realizar Login] Recebendo credenciais para login: '" + loginInformado + "'...");

            // Passo 2: Validacao de campos em branco
            if (loginInformado == null || loginInformado.trim().isEmpty() ||
                senhaInformada == null || senhaInformada.trim().isEmpty()) {
                return new ResultadoLogin(false, null, "[MSG04 - ERRO] Existem campos obrigatorios nao preenchidos: Login e Senha sao requeridos.");
            }

            ContaUsuario conta = baseContas.get(loginInformado);

            // Passo 3 e FA01: Usuario inexistente ou senha incorreta (MSG01)
            String hashEntrada = ModeloDominioSisVendas.Usuario.simularHash(senhaInformada);
            if (conta == null || !conta.getSenhaHash().equals(hashEntrada)) {
                if (conta != null) {
                    conta.registrarFalha();
                }
                return new ResultadoLogin(false, null, "[MSG01 - ERRO] Usuario ou senha invalidos. Por favor, verifique suas credenciais.");
            }

            // Passo 4 e FA02: Conta inativa (MSG02)
            if (!conta.isAtivo()) {
                return new ResultadoLogin(false, null, "[MSG02 - ALERTA] Usuario inativo no sistema. Contate o administrador.");
            }

            // Passo 5: Fluxo Principal - Criacao de sessao (MSG03)
            conta.zerarFalhas();
            String token = "SESSION_" + System.currentTimeMillis() + "_" + conta.getLogin();
            SessaoUsuario sessao = new SessaoUsuario(token, conta);
            return new ResultadoLogin(true, sessao, "[MSG03 - SUCESSO] Autenticacao realizada com sucesso. Redirecionando...");
        }

        /**
         * Secao 1.4: Verificacao do Diagrama de Contexto Geral por Ator.
         * Garante que o usuario autenticado so acesse os casos de uso de seu papel.
         */
        public boolean autorizarAcesso(SessaoUsuario sessao, CasoDeUso casoDeUso) {
            if (sessao == null) {
                System.out.println("   [ACESSO NEGADO] Sessao inexistente. Autentique-se primeiro.");
                return false;
            }
            AtorBase papel = sessao.getConta().getPapelAtor();
            boolean autorizado = papel.podeAcessar(casoDeUso);
            System.out.println("   -> Checando acesso do ator '" + papel.getNomePapel() +
                               "' ao " + casoDeUso.getCodigo() + " (" + casoDeUso.getTitulo() + "): " +
                               (autorizado ? "AUTORIZADO" : "BLOQUEADO POR CONTROLE DE ACESSO"));
            return autorizado;
        }
    }

    public static class ResultadoLogin {
        private final boolean sucesso;
        private final SessaoUsuario sessao;
        private final String mensagem;

        public ResultadoLogin(boolean sucesso, SessaoUsuario sessao, String mensagem) {
            this.sucesso = sucesso;
            this.sessao = sessao;
            this.mensagem = mensagem;
        }

        public boolean isSucesso() { return sucesso; }
        public SessaoUsuario getSessao() { return sessao; }
        public String getMensagem() { return mensagem; }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("   UNIFEF - AUTENTICACAO, ATORES E CONTROLE DE ACESSO (UC01)       ");
        System.out.println("===================================================================\n");

        ServicoAutenticacao auth = new ServicoAutenticacao();
        AtorAdministrador adminActor = new AtorAdministrador();
        AtorOperadorVendas operadorActor = new AtorOperadorVendas();

        // Criacao de contas para os cenarios de teste
        auth.cadastrarConta(new ContaUsuario("boer.admin", ModeloDominioSisVendas.Usuario.simularHash("admin123"), "Prof. Marcelo Boer", adminActor, true));
        auth.cadastrarConta(new ContaUsuario("joao.vendas", ModeloDominioSisVendas.Usuario.simularHash("vendas123"), "Joao Operador", operadorActor, true));
        auth.cadastrarConta(new ContaUsuario("pedro.inativo", ModeloDominioSisVendas.Usuario.simularHash("123456"), "Pedro Bloqueado", operadorActor, false));

        System.out.println("1. Teste do Fluxo Principal (Caminho Feliz - MSG03):");
        ResultadoLogin r1 = auth.executarUC01RealizarLogin("joao.vendas", "vendas123");
        System.out.println("   Resultado: " + r1.getMensagem());
        System.out.println("   Sessao Token: " + (r1.isSucesso() ? r1.getSessao().getToken() : "N/A"));
        System.out.println();

        System.out.println("2. Teste do Fluxo Alternativo FA01 (Credenciais Invalidas - MSG01):");
        ResultadoLogin r2 = auth.executarUC01RealizarLogin("joao.vendas", "senhaErrada");
        System.out.println("   Resultado: " + r2.getMensagem());
        System.out.println();

        System.out.println("3. Teste do Fluxo Alternativo FA02 (Conta Inativa/Bloqueada - MSG02):");
        ResultadoLogin r3 = auth.executarUC01RealizarLogin("pedro.inativo", "123456");
        System.out.println("   Resultado: " + r3.getMensagem());
        System.out.println();

        System.out.println("4. Verificacao da Matriz do Diagrama de Contexto Geral (Secao 1.4):");
        System.out.println("Testando permissoes da sessao do Operador de Vendas:");
        SessaoUsuario sessaoOperador = r1.getSessao();
        auth.autorizarAcesso(sessaoOperador, CasoDeUso.UC02_CADASTRAR_CLIENTE); // Permitido para Operador
        auth.autorizarAcesso(sessaoOperador, CasoDeUso.UC03_LISTAR_CLIENTE);    // Permitido para Operador
        auth.autorizarAcesso(sessaoOperador, CasoDeUso.UC06_EXCLUIR_CLIENTE);   // Exclusivo de Administrador!
        System.out.println();

        System.out.println("Testando permissoes da sessao do Administrador:");
        ResultadoLogin rAdmin = auth.executarUC01RealizarLogin("boer.admin", "admin123");
        SessaoUsuario sessaoAdmin = rAdmin.getSessao();
        auth.autorizarAcesso(sessaoAdmin, CasoDeUso.UC06_EXCLUIR_CLIENTE);      // Permitido para Administrador
        auth.autorizarAcesso(sessaoAdmin, CasoDeUso.UC08_CONSULTAR_AUDITORIA);   // Permitido para Administrador
        auth.autorizarAcesso(sessaoAdmin, CasoDeUso.UC02_CADASTRAR_CLIENTE);     // Nao mapeado para Admin no contexto direto
        System.out.println();

        System.out.println("Execucao e rastreabilidade entre Atores, Diagrama de Contexto e UC01 concluidos com sucesso.");
    }
}
