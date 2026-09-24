import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Disciplina: Engenharia de Software I (UniFEF)
 * Professor: Marcelo Boer
 * Tema: Aula 05 - Exercício 4: Padrão Estendido de DCU (Pré-requisitos, Pós-condições,
 *       Regras de Negócio e Matriz de I/O)
 * 
 * Como compilar:
 *   javac EspecificacaoDcuCompletaScaesm.java
 * 
 * Como executar:
 *   java EspecificacaoDcuCompletaScaesm
 */
public class EspecificacaoDcuCompletaScaesm {

    // =========================================================================
    // REGRAS DE NEGÓCIO ASSOCIADAS (Formalizadas na Seção de Regras do Quadro)
    // =========================================================================

    /**
     * [RN01 - Complexidade de Senha]:
     * A senha deve possuir no mínimo 8 caracteres, contendo letras maiúsculas,
     * minúsculas, números e caracteres especiais.
     */
    public static class ValidadorRegraNegocio01 {
        private static final Pattern PADRAO_COMPLEXIDADE =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!._-]).{8,}$");

        public static boolean validar(String senha) {
            if (senha == null) return false;
            return PADRAO_COMPLEXIDADE.matcher(senha).matches();
        }
    }

    /**
     * [RN03 - Auditoria de Acesso]:
     * Toda tentativa de login (sucesso ou falha) deve gerar registro de log
     * imutável contendo IP, data, hora e identificador do usuário.
     */
    public static class RegistroAuditoria {
        private final LocalDateTime dataHora;
        private final String ipOrigem;
        private final String loginInformado;
        private final String status;
        private final String motivo;

        public RegistroAuditoria(String ipOrigem, String loginInformado, String status, String motivo) {
            this.dataHora = LocalDateTime.now();
            this.ipOrigem = ipOrigem;
            this.loginInformado = loginInformado;
            this.status = status;
            this.motivo = motivo;
        }

        @Override
        public String toString() {
            return String.format("[%s] IP: %-15s | Usuário: %-15s | Status: %-8s | Motivo: %s",
                dataHora, ipOrigem, loginInformado, status, motivo);
        }
    }

    public static class TrilhaAuditoriaService {
        private final List<RegistroAuditoria> registros = new ArrayList<>();

        public void registrar(String ip, String login, String status, String motivo) {
            registros.add(new RegistroAuditoria(ip, login, status, motivo));
        }

        public List<RegistroAuditoria> getRegistros() {
            return Collections.unmodifiableList(registros);
        }
    }

    // =========================================================================
    // DADOS MANIPULADOS (MATRIZ DE I/O E CONTEXTO DE SESSÃO)
    // =========================================================================

    public static class DadosEntrada {
        private final String login;
        private final String senha;
        private final String ipOrigem;

        public DadosEntrada(String login, String senha, String ipOrigem) {
            this.login = login;
            this.senha = senha;
            this.ipOrigem = ipOrigem;
        }

        public String getLogin() { return login; }
        public String getSenha() { return senha; }
        public String getIpOrigem() { return ipOrigem; }
    }

    public static class DadosSaida {
        private final boolean autenticado;
        private final String tokenSessao;
        private final String nomeExibicao;
        private final String menuPermissoes;
        private final String mensagem;

        public DadosSaida(boolean autenticado, String tokenSessao, String nomeExibicao,
                          String menuPermissoes, String mensagem) {
            this.autenticado = autenticado;
            this.tokenSessao = tokenSessao;
            this.nomeExibicao = nomeExibicao;
            this.menuPermissoes = menuPermissoes;
            this.mensagem = mensagem;
        }

        public boolean isAutenticado() { return autenticado; }
        public String getTokenSessao() { return tokenSessao; }
        public String getNomeExibicao() { return nomeExibicao; }
        public String getMenuPermissoes() { return menuPermissoes; }
        public String getMensagem() { return mensagem; }
    }

    // =========================================================================
    // MOTOR DO CASO DE USO COM PÓS-CONDIÇÃO (GARANTIA DE SUCESSO)
    // =========================================================================

    public static class ServicoLoginEstendido {
        private final TrilhaAuditoriaService auditoria;

        public ServicoLoginEstendido(TrilhaAuditoriaService auditoria) {
            this.auditoria = auditoria;
        }

        /**
         * Executa o caso de uso verificando o pré-requisito, aplicando as regras de negócio
         * e assegurando formalmente a PÓS-CONDIÇÃO de engenharia de software.
         */
        public DadosSaida executar(DadosEntrada entrada, boolean usuarioExisteNaBase, boolean usuarioAtivo) {
            System.out.println("\n--- EXECUTANDO DCU REFINADO: FUNCIONÁRIO LOGAR ---");

            // [PRÉ-REQUISITO]: Usuário deverá estar pré-cadastrado no sistema e com status ativo.
            System.out.println("[Verificação de Pré-requisito] Checando existência e ativação cadastral...");
            if (!usuarioExisteNaBase || !usuarioAtivo) {
                // [Fluxo Alternativo 5.1] e [RN03 - Log de auditoria]
                auditoria.registrar(entrada.getIpOrigem(), entrada.getLogin(), "FALHA", "Usuário inexistente ou inativo no SCAESM");
                System.out.println("[Pré-requisito Violado] Usuário não cadastrado ou com cadastro suspenso.");
                return new DadosSaida(false, null, null, null, "Usuário não cadastrado ou inativo.");
            }
            System.out.println("[Pré-requisito Satisfeito] Cadastro ativo localizado.");

            // [RN01 - Validação da regra de complexidade de senha]
            System.out.println("[Validação RN01] Checando conformidade da complexidade de senha...");
            if (!ValidadorRegraNegocio01.validar(entrada.getSenha())) {
                auditoria.registrar(entrada.getIpOrigem(), entrada.getLogin(), "FALHA", "Senha viola política RN01 de complexidade");
                System.out.println("[Violação de RN01] Senha informada não atende aos requisitos corporativos de segurança.");
                return new DadosSaida(false, null, null, null, "Credencial inválida: formato de senha incompatível com a política.");
            }
            System.out.println("[RN01 Validada] Padrão de complexidade aceito.");

            // [Passos do Fluxo Normal: Autenticação com Sucesso]
            String tokenGerado = "JWT-SCAESM-" + Math.abs(entrada.getLogin().hashCode()) + "-" + System.currentTimeMillis();
            String nomeColaborador = "Prof. Marcelo Boer";
            String menuModulo = "[Acesso Liberado: Módulo Pessoa Funcionário | Consultar Dados | Alterar Senha]";

            // [RN03 - Registro obrigatório de auditoria de sucesso]
            auditoria.registrar(entrada.getIpOrigem(), entrada.getLogin(), "SUCESSO", "Sessão iniciada e credenciais aceitas");

            DadosSaida saida = new DadosSaida(true, tokenGerado, nomeColaborador, menuModulo, "Autenticado com sucesso.");

            // -----------------------------------------------------------------
            // VERIFICAÇÃO DA PÓS-CONDIÇÃO (Garantia de Sucesso):
            // "Sessão de usuário autenticada é criada no servidor, token de autorização emitido,
            // registro de auditoria gravado com timestamp e tela inicial carregada com permissões."
            // -----------------------------------------------------------------
            validarPosCondicao(saida);

            return saida;
        }

        /**
         * Verificação formal da garantia de sucesso estipulada no Quadro 5-Refinado.
         */
        private void validarPosCondicao(DadosSaida saida) {
            System.out.println("\n[Asserção de Pós-condição de Engenharia]");
            boolean tokenPresente = saida.getTokenSessao() != null && !saida.getTokenSessao().isEmpty();
            boolean menuCarregado = saida.getMenuPermissoes() != null;
            boolean auditoriaGravada = !auditoria.getRegistros().isEmpty();

            if (tokenPresente && menuCarregado && auditoriaGravada) {
                System.out.println("  -> Pós-condição SATISFEITA: Sessão gerada, token emitido e log imutável persistido.");
            } else {
                throw new IllegalStateException("Violação da Pós-condição! O estado esperado não foi alcançado.");
            }
        }
    }

    // =========================================================================
    // MÉTODO MAIN: DEMONSTRAÇÃO DO FRAMEWORK ESTENDIDO DE ENGENHARIA
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("SCAESM - EXECUÇÃO COM PÓS-CONDIÇÕES E REGRAS DE NEGÓCIO (EXERCÍCIO 4)");
        System.out.println("Professor: Marcelo Boer | Engenharia de Software I");
        System.out.println("================================================================================");

        TrilhaAuditoriaService auditoria = new TrilhaAuditoriaService();
        ServicoLoginEstendido servico = new ServicoLoginEstendido(auditoria);

        // Cenário A: Violação do Pré-requisito (Usuário não existe)
        System.out.println(">>> CENÁRIO A: Tentativa com Pré-requisito Não Atendido");
        DadosEntrada entrada1 = new DadosEntrada("carlos.inexistente", "Senha@Forte2026", "192.168.1.15");
        DadosSaida saida1 = servico.executar(entrada1, false, false);
        System.out.println("Resposta: " + saida1.getMensagem());

        // Cenário B: Violação da Regra de Negócio RN01 (Senha sem complexidade)
        System.out.println("\n>>> CENÁRIO B: Violação da Regra de Negócio RN01 (Complexidade)");
        DadosEntrada entrada2 = new DadosEntrada("marcelo.boer", "12345", "192.168.1.20");
        DadosSaida saida2 = servico.executar(entrada2, true, true);
        System.out.println("Resposta: " + saida2.getMensagem());

        // Cenário C: Caminho Feliz com Atendimento de RN01, RN02, RN03 e Pós-condição
        System.out.println("\n>>> CENÁRIO C: Fluxo Normal Válido com Garantia de Pós-condição");
        DadosEntrada entrada3 = new DadosEntrada("marcelo.boer", "UniFEF#Eng2026", "192.168.1.25");
        DadosSaida saida3 = servico.executar(entrada3, true, true);
        System.out.println("Resposta: " + saida3.getMensagem());
        System.out.println("Token de Sessão: " + saida3.getTokenSessao());
        System.out.println("Permissões Carregadas: " + saida3.getMenuPermissoes());

        // Exibição da Trilha de Auditoria imutável (RN03)
        System.out.println("\n================================================================================");
        System.out.println("RELATÓRIO DE AUDITORIA IMUTÁVEL GERADO PELO SISTEMA (RN03)");
        System.out.println("================================================================================");
        for (RegistroAuditoria log : auditoria.getRegistros()) {
            System.out.println(log);
        }
        System.out.println("================================================================================");
    }
}
