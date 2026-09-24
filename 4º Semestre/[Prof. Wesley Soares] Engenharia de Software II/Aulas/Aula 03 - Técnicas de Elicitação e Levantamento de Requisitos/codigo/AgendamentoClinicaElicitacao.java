/**
 * Instituicao: Centro Universitario de Santa Fe do Sul (UniFEF)
 * Curso: Bacharelado em Sistemas de Informacao
 * Disciplina: Engenharia de Software II
 * Docente: Prof. Ms. Wesley Soares de Souza
 * Tema: Refatoracao de Fala Ambigua em Requisitos Formais e BDD (Exercicio 1)
 *       Fala original: "Eu quero que o sistema me avise quando o paciente desmarcar a consulta em cima da hora..."
 *       Implementacao: RF-021 (Notificacao), RNF-008 (SLA <= 10s), RN-01 (< 2h) e RN-02 (Politica de Horario)
 * 
 * Como compilar:
 *   javac AgendamentoClinicaElicitacao.java
 * 
 * Como executar:
 *   java AgendamentoClinicaElicitacao
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AgendamentoClinicaElicitacao {

    public enum StatusConsulta {
        AGENDADA,
        CONFIRMADA,
        DISPONIVEL,
        CANCELADA,
        REALIZADA
    }

    public enum TipoNotificacao {
        PUSH_SONORO_URGENTE,   // No expediente e cancelamento em cima da hora (< 2h)
        PUSH_SILENCIOSO,       // Fora do expediente comercial
        NOTIFICACAO_ROTINA     // Cancelamento com bastante antecedencia
    }

    /**
     * Entidade Paciente.
     */
    public static class Paciente {
        private final int id;
        private final String nome;
        private final String telefone;

        public Paciente(int id, String nome, String telefone) {
            this.id = id;
            this.nome = nome;
            this.telefone = telefone;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getTelefone() { return telefone; }
    }

    /**
     * Entidade Dentista.
     */
    public static class Dentista {
        private final int id;
        private final String nome;
        private final String celular;

        public Dentista(int id, String nome, String celular) {
            this.id = id;
            this.nome = nome;
            this.celular = celular;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getCelular() { return celular; }
    }

    /**
     * Entidade Consulta que compoe a grade de horarios do profissional.
     */
    public static class Consulta {
        private final String id;
        private final Dentista dentista;
        private Paciente paciente;
        private final LocalDateTime dataHora;
        private StatusConsulta status;

        public Consulta(String id, Dentista dentista, Paciente paciente, LocalDateTime dataHora) {
            this.id = id;
            this.dentista = dentista;
            this.paciente = paciente;
            this.dataHora = dataHora;
            this.status = StatusConsulta.AGENDADA;
        }

        public String getId() { return id; }
        public Dentista getDentista() { return dentista; }
        public Paciente getPaciente() { return paciente; }
        public LocalDateTime getDataHora() { return dataHora; }
        public StatusConsulta getStatus() { return status; }

        public void setPaciente(Paciente paciente) { this.paciente = paciente; }
        public void setStatus(StatusConsulta status) { this.status = status; }
    }

    /**
     * Alerta formal gerado pelo motor de eventos atendendo ao RF-021 e RNF-008.
     */
    public static class NotificacaoCancelamento {
        private final String destinatario;
        private final TipoNotificacao tipo;
        private final String mensagem;
        private final double latenciaSegundos;
        private final boolean dentroSla;
        private final Paciente pacienteSubstitutoSugerido;

        public NotificacaoCancelamento(String destinatario, TipoNotificacao tipo, String mensagem,
                                       double latenciaSegundos, Paciente substituto) {
            this.destinatario = destinatario;
            this.tipo = tipo;
            this.mensagem = mensagem;
            this.latenciaSegundos = latenciaSegundos;
            this.dentroSla = latenciaSegundos <= 10.0; // RNF-008: latencia <= 10 segundos
            this.pacienteSubstitutoSugerido = substituto;
        }

        public void exibirRelatorio() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("[NOTIFICACAO RF-021] Para: " + destinatario);
            System.out.println("Modalidade do Disparo: " + tipo);
            System.out.println("Mensagem: " + mensagem);
            System.out.printf("Latencia Registrada: %.3f segundos (SLA RNF-008 <= 10s: %s)\n",
                    latenciaSegundos, dentroSla ? "CONFORME" : "VIOLADO");
            if (pacienteSubstitutoSugerido != null) {
                System.out.println("Otimizacao de Agenda: Sugerido encaixe de '" +
                        pacienteSubstitutoSugerido.getNome() + "' (Tel: " + pacienteSubstitutoSugerido.getTelefone() + ")");
            }
            System.out.println("----------------------------------------------------------------------");
        }
    }

    /**
     * Servico de agendamento que executa as regras elicitadas com medicao de tempo.
     */
    public static class ServicoAgendamentoClinica {
        private final Queue<Paciente> filaDeEspera = new ArrayDeque<>();

        public void adicionarFilaEspera(Paciente paciente) {
            filaDeEspera.add(paciente);
        }

        /**
         * Metodo que formaliza as regras de negocio do Exercicio 1.
         */
        public NotificacaoCancelamento processarCancelamento(Consulta consulta, LocalDateTime momentoCancelamento) {
            long inicioMillis = System.currentTimeMillis();

            // RN-01: Calculo da antecedencia em relacao ao horario agendado
            Duration antecedencia = Duration.between(momentoCancelamento, consulta.getDataHora());
            boolean ehEmCimaDaHora = antecedencia.toMinutes() < 120 && !antecedencia.isNegative(); // Menos de 2 horas

            // RN-02: Verificacao do expediente comercial (07:00 as 20:00)
            LocalTime horaAtual = momentoCancelamento.toLocalTime();
            boolean emExpediente = !horaAtual.isBefore(LocalTime.of(7, 0)) && !horaAtual.isAfter(LocalTime.of(20, 0));

            // Atualizacao da Grade da Agenda para DISPONIVEL
            consulta.setStatus(StatusConsulta.DISPONIVEL);

            // Identificacao do primeiro da fila de espera compativel
            Paciente substituto = filaDeEspera.poll();

            // Definicao da politica de alerta conforme RN-01 e RN-02
            TipoNotificacao tipo;
            String texto;
            if (ehEmCimaDaHora && emExpediente) {
                tipo = TipoNotificacao.PUSH_SONORO_URGENTE;
                texto = String.format("ALERTA URGENTE: Consulta das %02d:%02d cancelada por %s com apenas %d minutos de antecedencia!",
                        consulta.getDataHora().getHour(), consulta.getDataHora().getMinute(),
                        consulta.getPaciente().getNome(), antecedencia.toMinutes());
            } else if (!emExpediente) {
                tipo = TipoNotificacao.PUSH_SILENCIOSO;
                texto = String.format("AVISO SILENCIOSO (Fora do expediente): Consulta de %s desmarcada.",
                        consulta.getPaciente().getNome());
            } else {
                tipo = TipoNotificacao.NOTIFICACAO_ROTINA;
                texto = String.format("Agenda liberada: Consulta das %02d:%02d desmarcada com antecedencia regular de %d horas.",
                        consulta.getDataHora().getHour(), consulta.getDataHora().getMinute(), antecedencia.toHours());
            }

            // Simula latencia de disparo para teste do RNF-008 (350ms)
            try {
                Thread.sleep(350);
            } catch (InterruptedException ignored) {}

            double latencia = (System.currentTimeMillis() - inicioMillis) / 1000.0;
            return new NotificacaoCancelamento(consulta.getDentista().getNome(), tipo, texto, latencia, substituto);
        }
    }

    /**
     * Execucao demonstrativa dos cenarios BDD definidos no documento.
     */
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("UniFEF - Engenharia de Software II | Prof. Ms. Wesley Soares de Souza");
        System.out.println("Exercicio 1: Formalizacao BDD de Notificacao de Cancelamento Tardio");
        System.out.println("======================================================================\n");

        Dentista drEduardo = new Dentista(10, "Dr. Eduardo - Cirurgiao Dentista", "(17) 99876-5432");
        Paciente pacienteLucas = new Paciente(101, "Lucas Silva", "(17) 99111-2233");
        Paciente pacienteEspera = new Paciente(102, "Mariana Costa (Fila Espera)", "(17) 98888-7766");

        ServicoAgendamentoClinica servico = new ServicoAgendamentoClinica();
        servico.adicionarFilaEspera(pacienteEspera);

        // CENARIO 1 (Gherkin do Documento):
        // Consulta as 15:00 cancelada as 13:45 (antecedencia de 1h15m = 75 minutos < 2h)
        LocalDateTime dataHoraConsulta = LocalDateTime.of(2026, 10, 15, 15, 0);
        Consulta consulta1 = new Consulta("CONS-1001", drEduardo, pacienteLucas, dataHoraConsulta);

        LocalDateTime momentoCancelamento1 = LocalDateTime.of(2026, 10, 15, 13, 45);
        System.out.println(">>> EXECUTANDO CENARIO 1: Cancelamento Tardio dentro do Expediente (13:45)... ");
        NotificacaoCancelamento notif1 = servico.processarCancelamento(consulta1, momentoCancelamento1);
        notif1.exibirRelatorio();
        System.out.println("Estado da vaga na agenda: " + consulta1.getStatus() + " (Pronta para reencaixe)\n");

        // CENARIO 2: Cancelamento com antecedencia confortavel (24 horas antes)
        Paciente pacienteJoao = new Paciente(103, "Joao Santos", "(17) 97777-3344");
        Consulta consulta2 = new Consulta("CONS-1002", drEduardo, pacienteJoao, dataHoraConsulta);
        LocalDateTime momentoCancelamento2 = LocalDateTime.of(2026, 10, 14, 15, 0); // 24 horas antes

        System.out.println(">>> EXECUTANDO CENARIO 2: Cancelamento com 24 horas de antecedencia...");
        NotificacaoCancelamento notif2 = servico.processarCancelamento(consulta2, momentoCancelamento2);
        notif2.exibirRelatorio();

        // CENARIO 3: Cancelamento tardio ocorrido na madrugada (22:30) -> RN-02 (Modo Silencioso)
        Consulta consulta3 = new Consulta("CONS-1003", drEduardo, pacienteLucas, LocalDateTime.of(2026, 10, 16, 8, 30));
        LocalDateTime momentoCancelamento3 = LocalDateTime.of(2026, 10, 15, 22, 30);

        System.out.println("\n>>> EXECUTANDO CENARIO 3: Cancelamento Tardio no Periodo Noturno (22:30)...");
        NotificacaoCancelamento notif3 = servico.processarCancelamento(consulta3, momentoCancelamento3);
        notif3.exibirRelatorio();

        System.out.println("Demonstracao concluida: fala vaga refinada em especificacao verificavel e executavel.");
    }
}
