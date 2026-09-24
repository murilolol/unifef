/**
 * ============================================================================
 * DISCIPLINA : Engenharia de Software II (4º Semestre) - UniFEF
 * PROFESSOR  : Wesley Soares
 * TEMA       : Automação do Requisito RF07 - Motor de Alertas Metrológicos ANVISA
 * CONTEXTO   : Atividade da Aula 3 - HealthTech Solutions (Plataforma MedTrack)
 * ============================================================================
 *
 * CONCEITOS COBERTOS:
 * 1. Requisito Funcional RF07: Painel de Alertas de Vencimento ANVISA.
 * 2. Monitoramento de conformidade da base instalada (RDC ANVISA nº 63/2011).
 * 3. Classificação de criticidade temporal (CRÍTICO/VENCIDO, 5 DIAS, 15 DIAS,
 *    30 DIAS e CONFORME).
 * 4. Diagnóstico de causa raiz: blindagem contra multas e acidentes hospitalares.
 *
 * COMO COMPILAR:
 *   javac MedTrackAlertasAnvisa.java
 *
 * COMO EXECUTAR:
 *   java MedTrackAlertasAnvisa
 * ============================================================================
 */

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MedTrackAlertasAnvisa {

    /**
     * Categorização da gravidade do alerta de conformidade metrológica.
     */
    public enum SeveridadeAlerta {
        VENCIDO_CRITICO(1, "VENCIDO - CRÍTICO", "Interdição Imediata e Recolhimento de Emergência"),
        URGENCIA_5_DIAS(2, "ALERTA 5 DIAS", "Prioridade Máxima na Rota de Coleta/Troca"),
        AVISO_15_DIAS(3, "ALERTA 15 DIAS", "Notificar Engenharia Clínica e Agendar Substituição"),
        PREVENCAO_30_DIAS(4, "ATENÇÃO 30 DIAS", "Planejamento de Roteirização do Mês"),
        REGULAR_CONFORME(5, "CONFORME", "Operação Regular Autorizada");

        private final int prioridade;
        private final String rotulo;
        private final String acaoRecomendada;

        SeveridadeAlerta(int prioridade, String rotulo, String acaoRecomendada) {
            this.prioridade = prioridade;
            this.rotulo = rotulo;
            this.acaoRecomendada = acaoRecomendada;
        }

        public int getPrioridade() {
            return prioridade;
        }

        public String getRotulo() {
            return rotulo;
        }

        public String getAcaoRecomendada() {
            return acaoRecomendada;
        }
    }

    /**
     * Registro de ativo alocado em hospital monitorado periodicamente pelo MedTrack.
     */
    public static class AtivoHospitalar {
        private final String patrimonio;
        private final String numeroSerie;
        private final String modelo;
        private final String hospitalNome;
        private final String leitoOuAla;
        private final LocalDate dataVencimentoCalibracao;

        public AtivoHospitalar(
                String patrimonio,
                String numeroSerie,
                String modelo,
                String hospitalNome,
                String leitoOuAla,
                LocalDate dataVencimentoCalibracao) {
            this.patrimonio = patrimonio;
            this.numeroSerie = numeroSerie;
            this.modelo = modelo;
            this.hospitalNome = hospitalNome;
            this.leitoOuAla = leitoOuAla;
            this.dataVencimentoCalibracao = dataVencimentoCalibracao;
        }

        public String getPatrimonio() {
            return patrimonio;
        }

        public String getNumeroSerie() {
            return numeroSerie;
        }

        public String getModelo() {
            return modelo;
        }

        public String getHospitalNome() {
            return hospitalNome;
        }

        public String getLeitoOuAla() {
            return leitoOuAla;
        }

        public LocalDate getDataVencimentoCalibracao() {
            return dataVencimentoCalibracao;
        }
    }

    /**
     * Representação da notificação disparada pelo serviço automático de auditoria.
     */
    public static class NotificacaoConformidade {
        private final AtivoHospitalar ativo;
        private final SeveridadeAlerta severidade;
        private final long diasRestantes;

        public NotificacaoConformidade(AtivoHospitalar ativo, SeveridadeAlerta severidade, long diasRestantes) {
            this.ativo = ativo;
            this.severidade = severidade;
            this.diasRestantes = diasRestantes;
        }

        public AtivoHospitalar getAtivo() {
            return ativo;
        }

        public SeveridadeAlerta getSeveridade() {
            return severidade;
        }

        public long getDiasRestantes() {
            return diasRestantes;
        }
    }

    /**
     * Motor de Auditoria: Analisa a base e emite alertas automatizados (RF07).
     */
    public static class MotorAuditoriaAnvisa {

        public List<NotificacaoConformidade> processarAuditoria(List<AtivoHospitalar> ativos, LocalDate dataBase) {
            List<NotificacaoConformidade> notificacoes = new ArrayList<>();

            for (AtivoHospitalar ativo : ativos) {
                long dias = ChronoUnit.DAYS.between(dataBase, ativo.getDataVencimentoCalibracao());
                SeveridadeAlerta severidade;

                if (dias < 0) {
                    severidade = SeveridadeAlerta.VENCIDO_CRITICO;
                } else if (dias <= 5) {
                    severidade = SeveridadeAlerta.URGENCIA_5_DIAS;
                } else if (dias <= 15) {
                    severidade = SeveridadeAlerta.AVISO_15_DIAS;
                } else if (dias <= 30) {
                    severidade = SeveridadeAlerta.PREVENCAO_30_DIAS;
                } else {
                    severidade = SeveridadeAlerta.REGULAR_CONFORME;
                }

                notificacoes.add(new NotificacaoConformidade(ativo, severidade, dias));
            }

            // Ordenar por prioridade (mais críticos primeiro)
            notificacoes.sort(Comparator.comparingInt(n -> n.getSeveridade().getPrioridade()));
            return notificacoes;
        }
    }

    /**
     * Demonstração prática do painel gerencial em console.
     */
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("    MEDTRACK SOLUTIONS - PAINEL AUTOMÁTICO DE AUDITORIA ANVISA (RF07)   ");
        System.out.println("========================================================================\n");

        LocalDate dataAtualSimulada = LocalDate.now();
        System.out.println("Data Base da Auditoria: " + dataAtualSimulada + "\n");

        // Construção do parque de equipamentos locados nos hospitais parceiros
        List<AtivoHospitalar> parqueEquipamentos = new ArrayList<>();

        parqueEquipamentos.add(new AtivoHospitalar(
                "PAT-0101", "SN-9981-A", "Bomba Infusão Infusomat",
                "Hospital Santa Catarina", "UTI Coronariana - Leito 04",
                dataAtualSimulada.minusDays(12) // VENCIDO HÁ 12 DIAS!
        ));

        parqueEquipamentos.add(new AtivoHospitalar(
                "PAT-0102", "SN-8821-B", "Ventilador Vela Viasys",
                "Hospital das Clínicas", "UTI Respiratória - Leito 11",
                dataAtualSimulada.plusDays(3) // VENCE EM 3 DIAS!
        ));

        parqueEquipamentos.add(new AtivoHospitalar(
                "PAT-0103", "SN-7734-C", "Monitor Multiparamétrico MX450",
                "Hospital Sírio-Libanês", "Pronto Atendimento - Sala 02",
                dataAtualSimulada.plusDays(14) // VENCE EM 14 DIAS!
        ));

        parqueEquipamentos.add(new AtivoHospitalar(
                "PAT-0104", "SN-6651-D", "Cardioversor Lifepak 20",
                "Hospital São Camilo", "Centro Cirúrgico - Sala 05",
                dataAtualSimulada.plusDays(28) // VENCE EM 28 DIAS!
        ));

        parqueEquipamentos.add(new AtivoHospitalar(
                "PAT-0105", "SN-5542-E", "Ventilador Mindray SV300",
                "Hospital Oswaldo Cruz", "Semi-Intensiva - Leito 08",
                dataAtualSimulada.plusDays(210) // CONFORME (Vence em 7 meses)
        ));

        MotorAuditoriaAnvisa motor = new MotorAuditoriaAnvisa();
        List<NotificacaoConformidade> resultadoAuditoria = motor.processarAuditoria(parqueEquipamentos, dataAtualSimulada);

        System.out.printf("%-12s | %-16s | %-8s | %-24s | %-18s | %s%n",
                "PATRIMÔNIO", "STATUS / ALERTA", "DIAS", "HOSPITAL LOCAL", "MODELO", "AÇÃO DO MEDTRACK");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

        int contagemCriticos = 0;
        for (NotificacaoConformidade notif : resultadoAuditoria) {
            AtivoHospitalar a = notif.getAtivo();
            String prazoTexto = (notif.getDiasRestantes() < 0)
                    ? Math.abs(notif.getDiasRestantes()) + "d ATRÁS"
                    : notif.getDiasRestantes() + "d rest.";

            System.out.printf("%-12s | %-16s | %-8s | %-24s | %-18s | %s%n",
                    a.getPatrimonio(),
                    notif.getSeveridade().getRotulo(),
                    prazoTexto,
                    a.getHospitalNome(),
                    a.getModelo(),
                    notif.getSeveridade().getAcaoRecomendada());

            if (notif.getSeveridade() == SeveridadeAlerta.VENCIDO_CRITICO ||
                notif.getSeveridade() == SeveridadeAlerta.URGENCIA_5_DIAS) {
                contagemCriticos++;
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

        System.out.println("\nSÍNTESE EXECUTIVA DE GESTÃO DE RISCO (AUDITORIA ANVISA):");
        System.out.printf("Total de Ativos Auditados : %d%n", parqueEquipamentos.size());
        System.out.printf("Ativos em Situação Crítica: %d%n", contagemCriticos);
        if (contagemCriticos > 0) {
            System.out.println("-> PLANO DE AÇÃO DISPARADO: Foram criadas 2 Ordens de Coleta/Troca de Emergência");
            System.out.println("   para envio automático aos motoristas no aplicativo móvel.");
            System.out.println("-> Notificação formal via Webhook despachada aos diretores clínicos dos hospitais.");
        } else {
            System.out.println("-> Operação em 100% de conformidade regulatória com a RDC ANVISA nº 63/2011.");
        }
    }
}
