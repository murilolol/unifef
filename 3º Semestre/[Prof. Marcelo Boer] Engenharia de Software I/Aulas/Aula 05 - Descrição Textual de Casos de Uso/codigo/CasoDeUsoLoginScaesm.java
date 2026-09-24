import java.util.HashMap;
import java.util.Map;

/**
 * Disciplina: Engenharia de Software I (UniFEF)
 * Professor: Marcelo Boer
 * Tema: Aula 05 - Execução Prática do Quadro 5: DCU Individual Funcionário Logar (SCAESM)
 * 
 * Como compilar:
 *   javac CasoDeUsoLoginScaesm.java
 * 
 * Como executar:
 *   java CasoDeUsoLoginScaesm
 */
public class CasoDeUsoLoginScaesm {

    // =========================================================================
    // 1. SEÇÃO DO QUADRO DCU: DADOS (Campos de Entrada e Saída manipulados)
    // =========================================================================

    /**
     * Representa a seção "Dados: login e senha" do Quadro 5.
     * DTO de entrada fornecido pelo ator no Passo 03 do Fluxo Normal.
     */
    public static class DadosLoginDTO {
        private final String login;
        private final String senha;

        public DadosLoginDTO(String login, String senha) {
            this.login = login;
            this.senha = senha;
        }

        public String getLogin() {
            return login;
        }

        public String getSenha() {
            return senha;
        }
    }

    /**
     * Representa a resposta do sistema descrita nos passos 06, 5.1 e 5.2.
     */
    public static class RespostaSistemaDTO {
        private final boolean autenticado;
        private final String mensagemExibida;
        private final String telaDestino;
        private final String usuarioId;

        public RespostaSistemaDTO(boolean autenticado, String mensagemExibida, String telaDestino, String usuarioId) {
            this.autenticado = autenticado;
            this.mensagemExibida = mensagemExibida;
            this.telaDestino = telaDestino;
            this.usuarioId = usuarioId;
        }

        public boolean isAutenticado() {
            return autenticado;
        }

        public String getMensagemExibida() {
            return mensagemExibida;
        }

        public String getTelaDestino() {
            return telaDestino;
        }

        public String getUsuarioId() {
            return usuarioId;
        }
    }

    // =========================================================================
    // 2. CAMADA DE PERSISTÊNCIA: SUPORTE AO PRÉ-REQUISITO OPERACIONAL
    // =========================================================================

    /**
     * Entidade de banco de dados interna da fronteira do software.
     * Nota conceitual: O Ator Funcionário (externo) não se confunde com esta entidade.
     */
    public static class FuncionarioEntidade {
        private final String id;
        private final String nome;
        private final String login;
        private final String senhaCadastrada;
        private int tentativasFalhas;
        private boolean bloqueado;

        public FuncionarioEntidade(String id, String nome, String login, String senhaCadastrada) {
            this.id = id;
            this.nome = nome;
            this.login = login;
            this.senhaCadastrada = senhaCadastrada;
            this.tentativasFalhas = 0;
            this.bloqueado = false;
        }

        public String getId() { return id; }
        public String getNome() { return nome; }
        public String getLogin() { return login; }
        public String getSenhaCadastrada() { return senhaCadastrada; }
        public int getTentativasFalhas() { return tentativasFalhas; }
        public boolean isBloqueado() { return bloqueado; }

        public void incrementarTentativas() { this.tentativasFalhas++; }
        public void zerarTentativas() { this.tentativasFalhas = 0; }
        public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
    }

    /**
     * Repositório responsável por satisfazer e verificar o Pré-requisito:
     * "Usuário deverá estar pré-cadastrado no sistema."
     */
    public static class RepositorioFuncionarios {
        private final Map<String, FuncionarioEntidade> tabela = new HashMap<>();

        public void salvarPreviamente(FuncionarioEntidade f) {
            tabela.put(f.getLogin(), f);
        }

        public FuncionarioEntidade buscarPorLogin(String login) {
            if (login == null) return null;
            return tabela.get(login);
        }
    }

    // =========================================================================
    // 3. CONTROLADOR: DIÁLOGO DO FLUXO NORMAL E FLUXOS ALTERNATIVOS
    // =========================================================================

    public static class ControladorCasoDeUsoLogin {
        private final RepositorioFuncionarios repositorio;

        public ControladorCasoDeUsoLogin(RepositorioFuncionarios repositorio) {
            this.repositorio = repositorio;
        }

        /**
         * Passo 01 e 02 do Fluxo Normal:
         * 01. Usuário acessa o URL do sistema;
         * 02. Sistema gera tela de login para o usuário.
         */
        public String carregarTelaLogin() {
            System.out.println("[Passo 01 - Ação do Ator] Ator acessa o URL do sistema SCAESM.");
            System.out.println("[Passo 02 - Resposta Sistema] Sistema gera tela de login com campos de identificação.");
            return "/login";
        }

        /**
         * Execução orquestrada dos Passos 03 a 06 do Fluxo Normal e dos Fluxos Alternativos.
         */
        public RespostaSistemaDTO processarAutenticacao(DadosLoginDTO dados) {
            // [Passo 03 do Fluxo Normal]: Usuário informa seus dados nos respectivos campos solicitados;
            System.out.println("[Passo 03 - Ação do Ator] Usuário informa dados nos campos (login='" + dados.getLogin() + "', senha='***').");

            // [Passo 04 do Fluxo Normal]: Usuário clica em logar;
            System.out.println("[Passo 04 - Ação do Ator] Usuário clica em logar.");

            // [Passo 05 do Fluxo Normal]: Sistema verifica se o usuário está cadastrado;
            System.out.println("[Passo 05 - Processamento Sistema] Sistema verifica se o usuário está cadastrado...");
            FuncionarioEntidade funcionario = repositorio.buscarPorLogin(dados.getLogin());

            // -----------------------------------------------------------------
            // FLUXO ALTERNATIVO 5.1 e 5.1.1 (Quadro 5):
            // 5.1. Se o usuário não estiver cadastrado no sistema será exibido a mensagem: “Usuário não cadastrado”;
            // 5.1.1. Sistema retorna ao item 1, mas com tela de cadastro.
            // -----------------------------------------------------------------
            if (funcionario == null) {
                System.out.println("  -> [Fluxo Alternativo 5.1] Usuário não localizado na base de dados.");
                System.out.println("  -> [Fluxo Alternativo 5.1] Mensagem exibida: 'Usuário não cadastrado'");
                System.out.println("  -> [Fluxo Alternativo 5.1.1] Ponto de Retorno: Sistema retorna ao item 1, mas com tela de cadastro.");
                return new RespostaSistemaDTO(false, "Usuário não cadastrado", "/cadastro-funcionario", null);
            }

            // -----------------------------------------------------------------
            // FLUXO ALTERNATIVO 5.2 (Exercício 1 da aula): Senha Incorreta e Controle de Tentativas
            // 5.2.1. O sistema verifica que a senha informada não coincide com a cadastrada;
            // 5.2.2. O sistema incrementa em 1 o contador de tentativas de login;
            // 5.2.3. Se atingir 3 tentativas, bloqueia e encerra com falha;
            // 5.2.4. Se menor que 3, emite aviso e retorna ao Passo 03.
            // -----------------------------------------------------------------
            if (funcionario.isBloqueado()) {
                System.out.println("  -> [Fluxo Alternativo 5.2.3] Bloqueio ativo. Encerrando caso de uso com falha.");
                return new RespostaSistemaDTO(false, "Conta bloqueada temporariamente por excesso de tentativas inválidas.", "/recuperar-conta", funcionario.getId());
            }

            if (!funcionario.getSenhaCadastrada().equals(dados.getSenha())) {
                funcionario.incrementarTentativas();
                System.out.println("  -> [Fluxo Alternativo 5.2.1] Senha informada diverge da senha cadastrada.");
                System.out.println("  -> [Fluxo Alternativo 5.2.2] Tentativas falhas acumuladas: " + funcionario.getTentativasFalhas() + "/3");

                if (funcionario.getTentativasFalhas() >= 3) {
                    funcionario.setBloqueado(true);
                    System.out.println("  -> [Fluxo Alternativo 5.2.3.1] Limite atingido! Conta bloqueada por 15 minutos.");
                    System.out.println("  -> [Fluxo Alternativo 5.2.3.2] Mensagem: 'Conta bloqueada temporariamente por excesso de tentativas inválidas.'");
                    System.out.println("  -> [Fluxo Alternativo 5.2.3.3] Caso de uso é encerrado com falha.");
                    return new RespostaSistemaDTO(false, "Conta bloqueada temporariamente por excesso de tentativas inválidas.", "/recuperar-conta", funcionario.getId());
                } else {
                    System.out.println("  -> [Fluxo Alternativo 5.2.4.1] Mensagem: 'Senha incorreta. Tentativa " + funcionario.getTentativasFalhas() + " de 3.'");
                    System.out.println("  -> [Fluxo Alternativo 5.2.4.2] Retorna ao Passo 03 preservando o login informado.");
                    return new RespostaSistemaDTO(false, "Senha incorreta. Tentativa " + funcionario.getTentativasFalhas() + " de 3.", "/login", null);
                }
            }

            // -----------------------------------------------------------------
            // CONCLUSÃO DO FLUXO NORMAL (Passo 06 do Quadro 5):
            // 06. Sistema exibe a página inicial referente ao usuário.
            // -----------------------------------------------------------------
            funcionario.zerarTentativas();
            System.out.println("[Passo 06 - Resposta Final Sistema] Sistema exibe a página inicial referente ao usuário.");
            return new RespostaSistemaDTO(true, "Acesso concedido. Bem-vindo(a), " + funcionario.getNome() + "!", "/home-funcionario", funcionario.getId());
        }
    }

    // =========================================================================
    // MÉTODO MAIN: SIMULAÇÃO EXECUTÁVEL DOS CENÁRIOS E DIÁLOGOS
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("SISTEMA SCAESM - SIMULAÇÃO DO CASO DE USO: FUNCIONÁRIO LOGAR (QUADRO 5)");
        System.out.println("Professor: Marcelo Boer | Engenharia de Software I");
        System.out.println("================================================================================\n");

        // 1. Preparação da infraestrutura satisfazendo o Pré-requisito:
        // "Usuário deverá estar pré-cadastrado no sistema."
        RepositorioFuncionarios repo = new RepositorioFuncionarios();
        FuncionarioEntidade fAtivo = new FuncionarioEntidade("F-101", "Prof. Marcelo Boer", "marcelo.boer", "UniFEF@2026");
        repo.salvarPreviamente(fAtivo);

        ControladorCasoDeUsoLogin controller = new ControladorCasoDeUsoLogin(repo);

        // ---------------------------------------------------------------------
        // CENÁRIO 1: Usuário inexistente na base (Disparo do Fluxo Alternativo 5.1 e 5.1.1)
        // ---------------------------------------------------------------------
        System.out.println(">>> [TESTE 1] Cenário com Usuário Não Cadastrado (Fluxo Alternativo 5.1)");
        controller.carregarTelaLogin();
        DadosLoginDTO entradaInvalida = new DadosLoginDTO("aluno.desconhecido", "123456");
        RespostaSistemaDTO r1 = controller.processarAutenticacao(entradaInvalida);
        System.out.println("Status: [Autenticado: " + r1.isAutenticado() + "] | Mensagem: '" + r1.getMensagemExibida() + "' | Redirecionamento: " + r1.getTelaDestino());
        System.out.println("--------------------------------------------------------------------------------\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 2: Senha incorreta acumulando tentativas até bloqueio (Fluxo Alternativo 5.2)
        // ---------------------------------------------------------------------
        System.out.println(">>> [TESTE 2] Cenário com Senha Incorreta e Bloqueio (Fluxo Alternativo 5.2)");
        controller.carregarTelaLogin();
        
        System.out.println("-- Tentativa 1 --");
        controller.processarAutenticacao(new DadosLoginDTO("marcelo.boer", "senhaErrada1"));

        System.out.println("-- Tentativa 2 --");
        controller.processarAutenticacao(new DadosLoginDTO("marcelo.boer", "senhaErrada2"));

        System.out.println("-- Tentativa 3 (Gatilho de Bloqueio) --");
        RespostaSistemaDTO r2 = controller.processarAutenticacao(new DadosLoginDTO("marcelo.boer", "senhaErrada3"));
        System.out.println("Status: [Autenticado: " + r2.isAutenticado() + "] | Mensagem: '" + r2.getMensagemExibida() + "' | Redirecionamento: " + r2.getTelaDestino());
        System.out.println("--------------------------------------------------------------------------------\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 3: Fluxo Normal Completo (Caminho Feliz: Passos 01 a 06)
        // ---------------------------------------------------------------------
        System.out.println(">>> [TESTE 3] Cenário de Sucesso (Fluxo Normal - Passos 01 a 06)");
        // Restabelece a conta para teste do fluxo normal
        fAtivo.setBloqueado(false);
        fAtivo.zerarTentativas();

        controller.carregarTelaLogin();
        DadosLoginDTO entradaSucesso = new DadosLoginDTO("marcelo.boer", "UniFEF@2026");
        RespostaSistemaDTO r3 = controller.processarAutenticacao(entradaSucesso);
        System.out.println("Status: [Autenticado: " + r3.isAutenticado() + "] | Mensagem: '" + r3.getMensagemExibida() + "' | Tela Carregada: " + r3.getTelaDestino());
        System.out.println("================================================================================");
    }
}
