import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Disciplina: Engenharia de Software I (UniFEF)
 * Professor: Marcelo Boer
 * Tema: Aula 05 - Resolução do Exercício 2: DCU Individual Cadastrar Funcionário
 * 
 * Como compilar:
 *   javac CadastrarFuncionarioCasoDeUso.java
 * 
 * Como executar:
 *   java CadastrarFuncionarioCasoDeUso
 */
public class CadastrarFuncionarioCasoDeUso {

    // =========================================================================
    // 1. SEÇÃO DE DADOS: TRANSFERÊNCIA DOS DADOS DE CADASTRO
    // =========================================================================

    /**
     * Dados manipulados no caso de uso Cadastrar Funcionário.
     * Mapeia os atributos declarados na seção Dados do Quadro X do exercício.
     */
    public static class DadosCadastroDTO {
        private final String nome;
        private final String cpf;
        private final String emailInstitucional;
        private final String cargo;
        private final String login;
        private final String senhaProvisoria;
        private final LocalDate dataAdmissao;

        public DadosCadastroDTO(String nome, String cpf, String emailInstitucional, String cargo,
                                String login, String senhaProvisoria, LocalDate dataAdmissao) {
            this.nome = nome;
            this.cpf = cpf;
            this.emailInstitucional = emailInstitucional;
            this.cargo = cargo;
            this.login = login;
            this.senhaProvisoria = senhaProvisoria;
            this.dataAdmissao = dataAdmissao;
        }

        public String getNome() { return nome; }
        public String getCpf() { return cpf; }
        public String getEmailInstitucional() { return emailInstitucional; }
        public String getCargo() { return cargo; }
        public String getLogin() { return login; }
        public String getSenhaProvisoria() { return senhaProvisoria; }
        public LocalDate getDataAdmissao() { return dataAdmissao; }
    }

    public static class RespostaCadastroDTO {
        private final boolean sucesso;
        private final String mensagem;
        private final String campoComErro;
        private final String pontoRetorno;

        public RespostaCadastroDTO(boolean sucesso, String mensagem, String campoComErro, String pontoRetorno) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.campoComErro = campoComErro;
            this.pontoRetorno = pontoRetorno;
        }

        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public String getCampoComErro() { return campoComErro; }
        public String getPontoRetorno() { return pontoRetorno; }
    }

    // =========================================================================
    // 2. CONTEXTO OPERACIONAL E PRÉ-REQUISITOS
    // =========================================================================

    /**
     * Representa a sessão do Ator Principal:
     * Pré-requisito: "O operador deve estar autenticado com perfil administrativo".
     */
    public static class ContextoSessaoOperador {
        private final String operadorLogin;
        private final String perfilAcesso;

        public ContextoSessaoOperador(String operadorLogin, String perfilAcesso) {
            this.operadorLogin = operadorLogin;
            this.perfilAcesso = perfilAcesso;
        }

        public boolean isAdministrador() {
            return "ADMINISTRADOR".equalsIgnoreCase(this.perfilAcesso);
        }

        public String getOperadorLogin() {
            return operadorLogin;
        }
    }

    public static class BancoDadosScaesm {
        private final Map<String, DadosCadastroDTO> funcionariosPorCpf = new HashMap<>();
        private final Map<String, String> cargosCadastrados = new HashMap<>();

        public void cadastrarCargoValido(String codigo, String descricao) {
            cargosCadastrados.put(codigo.toUpperCase(), descricao);
        }

        public boolean existeCargo(String cargo) {
            if (cargo == null) return false;
            return cargosCadastrados.containsKey(cargo.toUpperCase());
        }

        public boolean existeCpf(String cpf) {
            return funcionariosPorCpf.containsKey(cpf);
        }

        public void persistir(DadosCadastroDTO dto) {
            funcionariosPorCpf.put(dto.getCpf(), dto);
        }
    }

    // =========================================================================
    // 3. FLUXO DE EXECUÇÃO: QUADRO X (CADASTRAR FUNCIONÁRIO)
    // =========================================================================

    public static class ServicoCadastrarFuncionario {
        private final BancoDadosScaesm banco;

        public ServicoCadastrarFuncionario(BancoDadosScaesm banco) {
            this.banco = banco;
        }

        public RespostaCadastroDTO executarCadastro(ContextoSessaoOperador sessao, DadosCadastroDTO dados) {
            System.out.println("\n--- INICIANDO CASO DE USO: CADASTRAR FUNCIONÁRIO ---");
            
            // [Validação do Pré-requisito 1]: Operador deve estar autenticado com perfil administrativo
            System.out.println("[Verificação de Pré-requisito] Checando credencial do operador: " + sessao.getOperadorLogin());
            if (!sessao.isAdministrador()) {
                System.out.println("[Pré-requisito Violado] Operador não possui perfil administrativo! Execução bloqueada.");
                return new RespostaCadastroDTO(false, "Acesso Negado: Pré-requisito de perfil administrativo não atendido.", "permissao", "/acesso-negado");
            }
            System.out.println("[Pré-requisito Cumprido] Operador é ADMINISTRADOR com permissão ativa.");

            // [Validação do Pré-requisito 2]: O cargo do novo funcionário já deve estar previamente parametrizado
            System.out.println("[Verificação de Pré-requisito] Checando parametrização prévia do cargo: " + dados.getCargo());
            if (!banco.existeCargo(dados.getCargo())) {
                System.out.println("[Pré-requisito Violado] Cargo '" + dados.getCargo() + "' não cadastrado no sistema.");
                return new RespostaCadastroDTO(false, "Cargo não parametrizado previamente no sistema.", "cargo", "/parametrizacao-cargos");
            }
            System.out.println("[Pré-requisito Cumprido] Cargo previamente homologado no sistema.");

            // [Passo 01]: O ator acessa o menu "Funcionários" e seleciona a opção "Novo Cadastro"
            System.out.println("[Passo 01 - Ator] Acessa menu 'Funcionários' -> 'Novo Cadastro'.");

            // [Passo 02]: O sistema apresenta o formulário com campos de dados pessoais e de acesso
            System.out.println("[Passo 02 - Sistema] Apresenta formulário de cadastro com campos para dados pessoais e credenciais.");

            // [Passo 03]: O ator preenche nome, CPF, e-mail institucional, cargo, login inicial e senha provisória
            System.out.println("[Passo 03 - Ator] Preenche campos: Nome='" + dados.getNome() + "', CPF='" + dados.getCpf() + "', Cargo='" + dados.getCargo() + "'.");

            // [Passo 04]: O ator clica no botão "Salvar Cadastro"
            System.out.println("[Passo 04 - Ator] Clica no botão 'Salvar Cadastro'.");

            // [Passo 05]: O sistema valida os campos obrigatórios e verifica a unicidade do CPF na base de dados
            System.out.println("[Passo 05 - Sistema] Valida campos obrigatórios e verifica unicidade do CPF '" + dados.getCpf() + "'...");
            
            // -----------------------------------------------------------------
            // FLUXO ALTERNATIVO 5.1:
            // 5.1. Se o CPF informado já estiver cadastrado no sistema:
            // 5.1.1. O sistema exibe a mensagem de erro: "CPF já cadastrado para outro funcionário.";
            // 5.1.2. O sistema destaca o campo CPF em vermelho, mantendo os demais dados preenchidos;
            // 5.1.3. O sistema retorna ao Passo 03 do Fluxo Normal.
            // -----------------------------------------------------------------
            if (banco.existeCpf(dados.getCpf())) {
                System.out.println("  -> [Fluxo Alternativo 5.1] Conflito: CPF já cadastrado na base de dados.");
                System.out.println("  -> [Fluxo Alternativo 5.1.1] Sistema exibe: 'CPF já cadastrado para outro funcionário.'");
                System.out.println("  -> [Fluxo Alternativo 5.1.2] Campo CPF é destacado em vermelho no formulário.");
                System.out.println("  -> [Fluxo Alternativo 5.1.3] Ponto de retorno: Sistema volta ao Passo 03 mantendo os demais dados.");
                return new RespostaCadastroDTO(false, "CPF já cadastrado para outro funcionário.", "cpf", "Passo 03");
            }

            // [Passo 06]: O sistema persiste os dados do novo funcionário na base de dados
            System.out.println("[Passo 06 - Sistema] Persiste registro do funcionário no banco de dados.");
            banco.persistir(dados);

            // [Passo 07]: O sistema envia um e-mail com as credenciais provisórias para o funcionário
            System.out.println("[Passo 07 - Sistema] Dispara e-mail para '" + dados.getEmailInstitucional() + "' contendo login '" + dados.getLogin() + "' e senha provisória.");

            // [Passo 08]: O sistema exibe a mensagem: "Funcionário cadastrado com sucesso!" e apresenta a lista de funcionários
            System.out.println("[Passo 08 - Sistema] Exibe mensagem: 'Funcionário cadastrado com sucesso!' e redireciona para a lista.");

            return new RespostaCadastroDTO(true, "Funcionário cadastrado com sucesso!", null, "/funcionarios/lista");
        }
    }

    // =========================================================================
    // MÉTODO MAIN: DEMONSTRAÇÃO DO CASO DE USO E SUAS RAMIFICAÇÕES
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("SCAESM - EXECUÇÃO DO CASO DE USO: CADASTRAR FUNCIONÁRIO (EXERCÍCIO 2)");
        System.out.println("Professor: Marcelo Boer | Engenharia de Software I");
        System.out.println("================================================================================");

        BancoDadosScaesm banco = new BancoDadosScaesm();
        // Parametrização prévia exigida no pré-requisito de cargos
        banco.cadastrarCargoValido("PROF", "Professor Universitário");
        banco.cadastrarCargoValido("COORD", "Coordenador de Curso");

        ServicoCadastrarFuncionario servico = new ServicoCadastrarFuncionario(banco);

        // ---------------------------------------------------------------------
        // CASO 1: Tentativa com operador sem privilégio (Violação do Pré-requisito)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> TESTE 1: Violação de Pré-requisito (Operador sem Perfil Admin)");
        ContextoSessaoOperador sessaoComum = new ContextoSessaoOperador("estagiario.joao", "OPERADOR_BASICO");
        DadosCadastroDTO novoFunc1 = new DadosCadastroDTO(
            "Prof. Ana Paula", "111.222.333-44", "ana.paula@unifef.edu.br",
            "PROF", "ana.paula", "Mudar@123", LocalDate.now()
        );
        RespostaCadastroDTO r1 = servico.executarCadastro(sessaoComum, novoFunc1);
        System.out.println("Resultado: [Sucesso: " + r1.isSucesso() + "] Mensagem: " + r1.getMensagem());

        // ---------------------------------------------------------------------
        // CASO 2: Cadastro com sucesso (Fluxo Normal: Passos 01 a 08)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> TESTE 2: Execução do Fluxo Normal Completo");
        ContextoSessaoOperador sessaoAdmin = new ContextoSessaoOperador("admin.gestor", "ADMINISTRADOR");
        RespostaCadastroDTO r2 = servico.executarCadastro(sessaoAdmin, novoFunc1);
        System.out.println("Resultado: [Sucesso: " + r2.isSucesso() + "] Mensagem: " + r2.getMensagem() + " -> Tela: " + r2.getPontoRetorno());

        // ---------------------------------------------------------------------
        // CASO 3: Tentativa com CPF duplicado (Disparo do Fluxo Alternativo 5.1)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> TESTE 3: Tentativa de Cadastro com CPF Duplicado (Fluxo Alternativo 5.1)");
        DadosCadastroDTO funcionarioCpfDuplicado = new DadosCadastroDTO(
            "Ana Paula Silva", "111.222.333-44", "ana.silva2@unifef.edu.br",
            "PROF", "ana.silva2", "OutraSenha@456", LocalDate.now()
        );
        RespostaCadastroDTO r3 = servico.executarCadastro(sessaoAdmin, funcionarioCpfDuplicado);
        System.out.println("Resultado: [Sucesso: " + r3.isSucesso() + "] | Campo com Erro: " + r3.getCampoComErro() + " | Retorno para: " + r3.getPontoRetorno());
        System.out.println("\n================================================================================");
    }
}
