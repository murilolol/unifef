/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Atividade Avaliativa Pratica 02 - Atores do Aplicativo e Casos de Uso
 *
 * Descricao:
 * Este programa simula os papeis e responsabilidades de todos os Atores mapeados
 * na Atividade 02 interagindo com a fronteira do sistema MediSys:
 * - Atores Humanos Primarios: Recepcionista (ACT01), Medico (ACT02), Paciente (ACT03).
 * - Ator Humano Secundario: Administrador (ACT04).
 * - Atores de Sistemas Externos: SistemaOperadoraSaude (ACT05), ServicoNotificacaoExterna (ACT06).
 * - Ator Temporal: TemporizadorDoSistema (ACT07).
 *
 * Demonstra a rastreabilidade direta com os Requisitos Funcionais da Atividade 01:
 * RF01 (Cadastro), RF02 (Agendamento), RF03 (Atendimento Clinico), RF04 (Validacao ANS),
 * e RF05 (Notificacoes e Rotina Noturna 24h antes).
 *
 * Como compilar:
 *   javac SimulacaoAtoresECasosDeUso.java
 *
 * Como executar:
 *   java SimulacaoAtoresECasosDeUso
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimulacaoAtoresECasosDeUso {

    // =========================================================================
    // INTERFACE BASE E CLASSES QUE REPRESENTAM OS ATORES NA FRONTEIRA
    // =========================================================================

    public interface Ator {
        String getIdentificador();
        String getNomePapel();
        String getTipoAtor(); // Humano, Sistema Externo ou Temporal
    }

    /** ACT01: Recepcionista (Humano Primario) */
    public static class AtorRecepcionista implements Ator {
        private String matricula;
        private String nomeFuncionario;

        public AtorRecepcionista(String matricula, String nomeFuncionario) {
            this.matricula = matricula;
            this.nomeFuncionario = nomeFuncionario;
        }

        @Override public String getIdentificador() { return "ACT01"; }
        @Override public String getNomePapel() { return "Recepcionista (" + nomeFuncionario + ")"; }
        @Override public String getTipoAtor() { return "Humano (Primario)"; }
    }

    /** ACT02: Medico (Humano Primario) */
    public static class AtorMedico implements Ator {
        private String crm;
        private String nome;

        public AtorMedico(String crm, String nome) {
            this.crm = crm;
            this.nome = nome;
        }

        public String getCrm() { return crm; }
        @Override public String getIdentificador() { return "ACT02"; }
        @Override public String getNomePapel() { return "Medico (" + nome + " - CRM " + crm + ")"; }
        @Override public String getTipoAtor() { return "Humano (Primario)"; }
    }

    /** ACT03: Paciente (Humano Primario) */
    public static class AtorPaciente implements Ator {
        private String cpf;
        private String nome;
        private String telefone;

        public AtorPaciente(String cpf, String nome, String telefone) {
            this.cpf = cpf;
            this.nome = nome;
            this.telefone = telefone;
        }

        public String getCpf() { return cpf; }
        public String getNome() { return nome; }
        public String getTelefone() { return telefone; }
        @Override public String getIdentificador() { return "ACT03"; }
        @Override public String getNomePapel() { return "Paciente (" + nome + ")"; }
        @Override public String getTipoAtor() { return "Humano (Primario)"; }
    }

    /** ACT05: Sistema Operadora de Saude (Sistema Externo Secundario - API TISS) */
    public static class AtorSistemaOperadoraSaude implements Ator {
        private String nomeOperadora;

        public AtorSistemaOperadoraSaude(String nomeOperadora) {
            this.nomeOperadora = nomeOperadora;
        }

        @Override public String getIdentificador() { return "ACT05"; }
        @Override public String getNomePapel() { return "Operadora Saude (API: " + nomeOperadora + ")"; }
        @Override public String getTipoAtor() { return "Sistema Externo (Secundario)"; }

        /** Simula chamada de web service SOAP/REST da operadora */
        public boolean validarElegibilidade(String numeroCarteirinha, String procedimento) {
            System.out.println("   [WEB-SERVICE API TISS] Consultando operadora " + nomeOperadora +
                               " para a carteira " + numeroCarteirinha + "...");
            // Regra simulada: carteirinhas validas contem mais de 8 digitos e nao terminam em '999'
            return numeroCarteirinha != null && numeroCarteirinha.length() >= 8 && !numeroCarteirinha.endsWith("999");
        }
    }

    /** ACT06: Servico de Notificacao Externa (Gateway SMS/WhatsApp) */
    public static class AtorServicoNotificacaoExterna implements Ator {
        private String provedorMensageria;

        public AtorServicoNotificacaoExterna(String provedorMensageria) {
            this.provedorMensageria = provedorMensageria;
        }

        @Override public String getIdentificador() { return "ACT06"; }
        @Override public String getNomePapel() { return "Gateway Mensageria (" + provedorMensageria + ")"; }
        @Override public String getTipoAtor() { return "Sistema Externo (Secundario)"; }

        public void enviarMensagemTexto(String destinatario, String mensagem) {
            System.out.println("   [GATEWAY SMS/WHATSAPP] Enviando via " + provedorMensageria +
                               " para " + destinatario + " -> \"" + mensagem + "\"");
        }
    }

    /** ACT07: Temporizador do Sistema (Ator Temporal - Cron / Scheduler) */
    public static class AtorTemporizadorDoSistema implements Ator {
        private String nomeJob;

        public AtorTemporizadorDoSistema(String nomeJob) {
            this.nomeJob = nomeJob;
        }

        @Override public String getIdentificador() { return "ACT07"; }
        @Override public String getNomePapel() { return "Cron Scheduler (" + nomeJob + ")"; }
        @Override public String getTipoAtor() { return "Ator Temporal (Sistema)"; }
    }

    // =========================================================================
    // FRONTEIRA DO SISTEMA: DISPARO DE CASOS DE USO E REQUISITOS
    // =========================================================================

    public static class RegistroAgendamento {
        String id;
        AtorPaciente paciente;
        AtorMedico medico;
        LocalDateTime dataHora;
        String carteirinha;
        boolean autorizado;

        public RegistroAgendamento(String id, AtorPaciente paciente, AtorMedico medico,
                                   LocalDateTime dataHora, String carteirinha, boolean autorizado) {
            this.id = id;
            this.paciente = paciente;
            this.medico = medico;
            this.dataHora = dataHora;
            this.carteirinha = carteirinha;
            this.autorizado = autorizado;
        }
    }

    /**
     * Fronteira da Aplicacao MediSys (System Boundary).
     * Isola as operacoes internas e recebe os estimulos externos dos Atores.
     */
    public static class SistemaMediSys {
        private final AtorSistemaOperadoraSaude operadoraSaude;
        private final AtorServicoNotificacaoExterna gatewayNotificacao;
        private final List<RegistroAgendamento> bancoAgendamentos = new ArrayList<>();

        public SistemaMediSys(AtorSistemaOperadoraSaude operadoraSaude,
                              AtorServicoNotificacaoExterna gatewayNotificacao) {
            this.operadoraSaude = operadoraSaude;
            this.gatewayNotificacao = gatewayNotificacao;
        }

        /**
         * RF01: Cadastrar Paciente
         * Disparado por: Recepcionista (ACT01) ou Paciente (ACT03)
         */
        public void casoDeUsoCadastrarPaciente(Ator atorIniciador, AtorPaciente novoPaciente) {
            System.out.println("\n--- [CASO DE USO UC01: CADASTRAR PACIENTE] (Rastreabilidade: RF01) ---");
            System.out.println("Ator Iniciador: " + atorIniciador.getNomePapel() + " [" + atorIniciador.getTipoAtor() + "]");
            System.out.println("Acao: Validando unicidade de CPF e criando ficha cadastral para: " + novoPaciente.getNome());
            System.out.println("Resultado: Paciente registrado com sucesso no banco de dados conceitual.");
        }

        /**
         * RF02 e RF04: Agendar Consulta com Validacao com a Operadora
         * Disparado por: Recepcionista (ACT01)
         * Colaborador: Sistema Operadora de Saude (ACT05)
         */
        public RegistroAgendamento casoDeUsoAgendarConsulta(AtorRecepcionista recepcionista,
                                                           AtorPaciente paciente,
                                                           AtorMedico medico,
                                                           LocalDateTime horario,
                                                           String numeroCarteira) {
            System.out.println("\n--- [CASO DE USO UC02: AGENDAR CONSULTA] (Rastreabilidade: RF02, RF04) ---");
            System.out.println("Ator Primario (Iniciador): " + recepcionista.getNomePapel());
            System.out.println("Acao: Verificando grade disponivel para o " + medico.getNomePapel());

            // Chamada ao ator secundario operadora
            System.out.println("Solicitando autorizacao previa ao Ator Secundario: " + operadoraSaude.getNomePapel());
            boolean autorizada = operadoraSaude.validarElegibilidade(numeroCarteira, "CONS-AMBULATORIAL-101");

            if (autorizada) {
                String id = "AGD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                RegistroAgendamento agendamento = new RegistroAgendamento(id, paciente, medico, horario, numeroCarteira, true);
                bancoAgendamentos.add(agendamento);
                System.out.println("Resultado: Guia autorizada pela operadora! Consulta agendada com protocolo: " + id);
                
                // Notificacao imediata via Gateway SMS
                gatewayNotificacao.enviarMensagemTexto(paciente.getTelefone(),
                    "MediSys: Ola " + paciente.getNome() + ", sua consulta com o " + medico.getNomePapel() +
                    " foi agendada para " + horario.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".");
                return agendamento;
            } else {
                System.out.println("Resultado: Negativa de autorizacao pela operadora. Agendamento cancelado.");
                return null;
            }
        }

        /**
         * RF03: Realizar Atendimento Clinico
         * Disparado por: Medico (ACT02)
         */
        public void casoDeUsoAtendimentoClinico(AtorMedico medico, RegistroAgendamento agendamento, String prescricao) {
            System.out.println("\n--- [CASO DE USO UC03: REALIZAR ATENDIMENTO CLINICO] (Rastreabilidade: RF03) ---");
            System.out.println("Ator Primario (Iniciador): " + medico.getNomePapel());
            System.out.println("Paciente em Atendimento: " + agendamento.paciente.getNome());
            System.out.println("Acao: Dr. consultou prontuario historico e registrou diagnostico na ficha clinica.");
            System.out.println("Emissao de Receita Digital: " + prescricao);
            System.out.println("Resultado: Consulta finalizada e assinada digitalmente com CRM " + medico.getCrm());
        }

        /**
         * RF05: Notificar Agendamentos (Rotina Diaria Automatizada 24h Antes)
         * Disparado por: Temporizador do Sistema (ACT07 - Ator Temporal)
         * Consome: Servico de Notificacao Externa (ACT06)
         */
        public void casoDeUsoRotinaLembretesNoturnos(AtorTemporizadorDoSistema temporizador) {
            System.out.println("\n--- [CASO DE USO UC05: DISPARAR LEMBRETES 24H ANTES] (Rastreabilidade: RF05) ---");
            System.out.println("Ator Iniciador: " + temporizador.getNomePapel() + " [" + temporizador.getTipoAtor() + "]");
            System.out.println("Gatilho: Rotina temporal disparada as 00:00 para consultas do dia seguinte.");
            System.out.println("Varrendo banco de agendamentos pendentes...");

            int totalLembretes = 0;
            for (RegistroAgendamento ag : bancoAgendamentos) {
                totalLembretes++;
                gatewayNotificacao.enviarMensagemTexto(ag.paciente.getTelefone(),
                    "MediSys Lembrete: Voce tem uma consulta amanha as " +
                    ag.dataHora.format(DateTimeFormatter.ofPattern("HH:mm")) + ". Responda 1 para CONFIRMAR.");
            }
            System.out.println("Resultado: Ciclo concluido. Total de " + totalLembretes + " notificacao(oes) enviada(s).");
        }
    }

    // =========================================================================
    // EXECUCAO DA SIMULACAO INTEGRADA DOS ATORES
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("UniFEF - Engenharia de Software I - Prof. Marcelo Boer");
        System.out.println("Simulacao Pratica: Atores do Aplicativo e Rastreabilidade de Requisitos");
        System.out.println("======================================================================");

        // 1. Instanciacao dos Atores de Servicos Externos
        AtorSistemaOperadoraSaude apiBradesco = new AtorSistemaOperadoraSaude("Bradesco Saude TISS v4.01");
        AtorServicoNotificacaoExterna gatewayZenvia = new AtorServicoNotificacaoExterna("Zenvia SMS Cloud Gateway");
        AtorTemporizadorDoSistema cronDiario = new AtorTemporizadorDoSistema("NightlyReminderJob-Daemon");

        // 2. Instanciacao dos Atores Humanos
        AtorRecepcionista recepcionistaAna = new AtorRecepcionista("REC-2026-09", "Ana Paula Mendes");
        AtorMedico drRenato = new AtorMedico("CRM/SP 98765", "Dr. Renato Oliveira");
        AtorPaciente pacienteLucas = new AtorPaciente("333.444.555-66", "Lucas Ferreira Santos", "(17) 99123-4567");

        // 3. Inicializacao da Fronteira do Sistema
        SistemaMediSys sistema = new SistemaMediSys(apiBradesco, gatewayZenvia);

        // 4. Execucao sequencial dos Casos de Uso com seus respectivos Atores
        // Passo A: Cadastro do Paciente (RF01)
        sistema.casoDeUsoCadastrarPaciente(recepcionistaAna, pacienteLucas);

        // Passo B: Agendamento da Consulta com validacao na operadora (RF02 e RF04)
        LocalDateTime horario = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        RegistroAgendamento agendamento = sistema.casoDeUsoAgendarConsulta(
            recepcionistaAna, pacienteLucas, drRenato, horario, "BRAD-8877665544"
        );

        // Passo C: Atendimento Clinico pelo Medico (RF03)
        if (agendamento != null) {
            sistema.casoDeUsoAtendimentoClinico(drRenato, agendamento, "Receita: Amoxicilina 500mg de 8/8h por 7 dias.");
        }

        // Passo D: Gatilho Temporal da Meia-Noite disparando lembretes automaticos (RF05)
        sistema.casoDeUsoRotinaLembretesNoturnos(cronDiario);

        System.out.println("\n======================================================================");
        System.out.println("Simulacao finalizada com sucesso! Todos os atores e fluxos foram validados.");
        System.out.println("======================================================================");
    }
}
