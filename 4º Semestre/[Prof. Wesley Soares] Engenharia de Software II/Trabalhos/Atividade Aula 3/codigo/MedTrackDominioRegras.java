/**
 * ============================================================================
 * DISCIPLINA : Engenharia de Software II (4º Semestre) - UniFEF
 * PROFESSOR  : Wesley Soares
 * TEMA       : Modelagem Orientada a Objetos e Regras de Negócio (RN02)
 * CONTEXTO   : Atividade da Aula 3 - HealthTech Solutions (Plataforma MedTrack)
 * ============================================================================
 *
 * CONCEITOS COBERTOS:
 * 1. Abstração e Encapsulamento de Domínio (Aula 02 - Fundamentos OO).
 * 2. Máquina de Estados Finitos do Equipamento Biomédico.
 * 3. Regra de Negócio RN02: Bloqueio de locação por calibração metrológica
 *    vencida ou com margem inferior a 60 dias da data de término do contrato
 *    (atendimento à norma sanitária RDC ANVISA nº 63/2011).
 *
 * COMO COMPILAR:
 *   javac MedTrackDominioRegras.java
 *
 * COMO EXECUTAR:
 *   java MedTrackDominioRegras
 * ============================================================================
 */

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MedTrackDominioRegras {

    /**
     * Ciclo de vida operacional do ativo biomédico mapeado no diagrama
     * de estados concebido na especificação da solução de software.
     */
    public enum EstadoEquipamento {
        CADASTRADO,
        EM_ARMAZEM,
        LOCADO,
        COLETA_SOLICITADA,
        EM_TRANSITO,
        QUARENTENA,
        MANUTENCAO_CALIBRACAO,
        BAIXADO_SUCATA
    }

    /**
     * Entidade de valor que encapsula a certificação metrológica da ANVISA.
     */
    public static class LaudoMetrologico {
        private final String numeroCertificado;
        private final String orgaoAcreditado;
        private final LocalDate dataEmissao;
        private final LocalDate dataValidade;
        private final boolean aprovado;

        public LaudoMetrologico(
                String numeroCertificado,
                String orgaoAcreditado,
                LocalDate dataEmissao,
                LocalDate dataValidade,
                boolean aprovado) {
            this.numeroCertificado = numeroCertificado;
            this.orgaoAcreditado = orgaoAcreditado;
            this.dataEmissao = dataEmissao;
            this.dataValidade = dataValidade;
            this.aprovado = aprovado;
        }

        public String getNumeroCertificado() {
            return numeroCertificado;
        }

        public String getOrgaoAcreditado() {
            return orgaoAcreditado;
        }

        public LocalDate getDataValidade() {
            return dataValidade;
        }

        public boolean isAprovado() {
            return aprovado;
        }

        @Override
        public String toString() {
            return String.format("Laudo[Cert=%s, Org=%s, Validade=%s, Aprovado=%s]",
                    numeroCertificado, orgaoAcreditado, dataValidade, aprovado);
        }
    }

    /**
     * Exceção de domínio disparada quando uma regra de negócio corporativa ou
     * regulatória da ANVISA é violada durante o processamento.
     */
    public static class RegraNegocioException extends RuntimeException {
        public RegraNegocioException(String mensagem) {
            super(mensagem);
        }
    }

    /**
     * Entidade central do modelo de domínio: representa um equipamento hospitalar
     * rastreado individualmente por patrimônio e número de série.
     */
    public static class EquipamentoBiomedico {
        private final String numeroPatrimonio;
        private final String numeroSerie;
        private final String modelo;
        private final String fabricante;
        private EstadoEquipamento estadoAtual;
        private LaudoMetrologico ultimoLaudo;
        private final List<String> historicoOcorrencias;

        public EquipamentoBiomedico(
                String numeroPatrimonio,
                String numeroSerie,
                String modelo,
                String fabricante) {
            this.numeroPatrimonio = numeroPatrimonio;
            this.numeroSerie = numeroSerie;
            this.modelo = modelo;
            this.fabricante = fabricante;
            this.estadoAtual = EstadoEquipamento.CADASTRADO;
            this.historicoOcorrencias = new ArrayList<>();
            registrarHistorico("Ativo cadastrado no acervo patrimonial da HealthTech.");
        }

        public String getNumeroPatrimonio() {
            return numeroPatrimonio;
        }

        public String getNumeroSerie() {
            return numeroSerie;
        }

        public String getModelo() {
            return modelo;
        }

        public EstadoEquipamento getEstadoAtual() {
            return estadoAtual;
        }

        public LaudoMetrologico getUltimoLaudo() {
            return ultimoLaudo;
        }

        public void vincularLaudoCalibracao(LaudoMetrologico laudo) {
            this.ultimoLaudo = laudo;
            if (laudo.isAprovado()) {
                registrarHistorico("Novo laudo anexado: Certificado " + laudo.getNumeroCertificado()
                        + " com validade até " + laudo.getDataValidade());
            } else {
                registrarHistorico("Laudo REPROVADO recebido: Ativo retido na Engenharia Clínica.");
            }
        }

        /**
         * REGRA DE NEGÓCIO RN02: Bloqueio de locação por calibração vencida ou
         * próxima ao vencimento (margem de segurança exigida de 60 dias pós-contrato).
         */
        public boolean validarAptoParaNovaLocacao(int diasDuracaoContrato, LocalDate dataInicioLocacao) {
            if (this.estadoAtual != EstadoEquipamento.EM_ARMAZEM) {
                throw new RegraNegocioException(String.format(
                        "Violação de Estado: O equipamento %s não está no armazém (status atual: %s).",
                        this.numeroPatrimonio, this.estadoAtual));
            }

            if (this.ultimoLaudo == null || !this.ultimoLaudo.isAprovado()) {
                throw new RegraNegocioException(String.format(
                        "Violação de Qualidade: O equipamento %s não possui laudo metrológico aprovado.",
                        this.numeroPatrimonio));
            }

            LocalDate dataFimContrato = dataInicioLocacao.plusDays(diasDuracaoContrato);
            long margemSegurancaDias = 60;
            LocalDate dataMinimaPermitida = dataFimContrato.plusDays(margemSegurancaDias);

            if (this.ultimoLaudo.getDataValidade().isBefore(dataMinimaPermitida)) {
                long diasFaltantesAteFim = ChronoUnit.DAYS.between(dataFimContrato, this.ultimoLaudo.getDataValidade());
                throw new RegraNegocioException(String.format(
                        "Violação da Regra RN02 (ANVISA): A calibração vencerá em %s. " +
                        "O contrato encerra em %s. A margem restante pós-contrato é de apenas %d dias " +
                        "(mínimo regulatório exigido: %d dias). Locação bloqueada!",
                        this.ultimoLaudo.getDataValidade(), dataFimContrato, diasFaltantesAteFim, margemSegurancaDias));
            }

            return true;
        }

        /**
         * Transição controlada de estados validando os fluxos da máquina de estados.
         */
        public void transicionarPara(EstadoEquipamento novoEstado, String justificativa) {
            boolean transicaoValida = switch (this.estadoAtual) {
                case CADASTRADO -> (novoEstado == EstadoEquipamento.EM_ARMAZEM);
                case EM_ARMAZEM -> (novoEstado == EstadoEquipamento.LOCADO || novoEstado == EstadoEquipamento.QUARENTENA);
                case LOCADO -> (novoEstado == EstadoEquipamento.COLETA_SOLICITADA);
                case COLETA_SOLICITADA -> (novoEstado == EstadoEquipamento.EM_TRANSITO);
                case EM_TRANSITO -> (novoEstado == EstadoEquipamento.QUARENTENA);
                case QUARENTENA -> (novoEstado == EstadoEquipamento.MANUTENCAO_CALIBRACAO);
                case MANUTENCAO_CALIBRACAO -> (novoEstado == EstadoEquipamento.EM_ARMAZEM || novoEstado == EstadoEquipamento.BAIXADO_SUCATA);
                case BAIXADO_SUCATA -> false;
            };

            if (!transicaoValida) {
                throw new RegraNegocioException(String.format(
                        "Transição Inválida de Estados: Não é permitido mover de [%s] para [%s].",
                        this.estadoAtual, novoEstado));
            }

            EstadoEquipamento anterior = this.estadoAtual;
            this.estadoAtual = novoEstado;
            registrarHistorico(String.format("Transição de estado: [%s] -> [%s]. Motivo: %s",
                    anterior, novoEstado, justificativa));
        }

        private void registrarHistorico(String evento) {
            this.historicoOcorrencias.add(LocalDate.now() + " - " + evento);
        }

        public void imprimirFichaCadastral() {
            System.out.println("------------------------------------------------------------------------");
            System.out.printf("PATRIMÔNIO : %-15s | SÉRIE      : %s%n", numeroPatrimonio, numeroSerie);
            System.out.printf("MODELO     : %-15s | FABRICANTE : %s%n", modelo, fabricante);
            System.out.printf("STATUS     : %-15s | ÚLTIMO LAUDO: %s%n",
                    estadoAtual, (ultimoLaudo != null ? ultimoLaudo.getNumeroCertificado() : "Nenhum"));
            if (ultimoLaudo != null) {
                System.out.printf("VALIDADE LAUDO: %s (%s)%n",
                        ultimoLaudo.getDataValidade(),
                        ultimoLaudo.isAprovado() ? "Conforme" : "Reprovado");
            }
            System.out.println("HISTÓRICO:");
            for (String h : historicoOcorrencias) {
                System.out.println("  * " + h);
            }
            System.out.println("------------------------------------------------------------------------");
        }
    }

    /**
     * Demonstração prática e bateria de testes automatizados via console.
     */
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("      MEDTRACK SOLUTIONS - SIMULADOR DE DOMÍNIO E REGRAS DE NEGÓCIO     ");
        System.out.println("========================================================================\n");

        LocalDate hoje = LocalDate.now();

        // 1. Criação de Ativo Biomédico de Alta Complexidade (Ventilador Pulmonar)
        EquipamentoBiomedico ventilador1 = new EquipamentoBiomedico(
                "PAT-88019",
                "SN-VP-99321-BR",
                "Mindray SV300",
                "Mindray Bio-Medical"
        );

        // 2. Transição inicial: De cadastrado para armazém após calibração de fábrica
        LaudoMetrologico laudoValido = new LaudoMetrologico(
                "CERT-INMETRO-2026-091",
                "Laboratório CalibraMed Ltda.",
                hoje.minusMonths(2),
                hoje.plusMonths(10), // Válido por mais 10 meses
                true
        );
        ventilador1.vincularLaudoCalibracao(laudoValido);
        ventilador1.transicionarPara(EstadoEquipamento.EM_ARMAZEM, "Calibração de fábrica conferida e estocada.");

        System.out.println("[TESTE 1] Tentativa de locação regular (Contrato de 90 dias com laudo de 10 meses):");
        try {
            boolean apto = ventilador1.validarAptoParaNovaLocacao(90, hoje);
            if (apto) {
                ventilador1.transicionarPara(EstadoEquipamento.LOCADO, "Contrato de locação firmado com Hospital São Paulo.");
                System.out.println("-> SUCESSO: Equipamento aprovado para locação e status alterado para LOCADO!");
            }
        } catch (RegraNegocioException e) {
            System.err.println("-> FALHA: " + e.getMessage());
        }

        System.out.println("\n[TESTE 2] Violação da Regra RN02: Tentativa de locar com laudo próximo do vencimento:");
        EquipamentoBiomedico bombaInfusao = new EquipamentoBiomedico(
                "PAT-44102",
                "SN-BI-77100-SP",
                "Alaris MedSystem III",
                "Becton Dickinson"
        );

        // Laudo com validade que cobre o contrato mas NÃO cobre a margem de 60 dias pós-contrato
        // Contrato de 120 dias, laudo vence em 140 dias (sobrariam apenas 20 dias, violando os 60 dias mínimos)
        LaudoMetrologico laudoAperto = new LaudoMetrologico(
                "CERT-INMETRO-2026-118",
                "LabMet Hospitalar",
                hoje.minusMonths(6),
                hoje.plusDays(140),
                true
        );
        bombaInfusao.vincularLaudoCalibracao(laudoAperto);
        bombaInfusao.transicionarPara(EstadoEquipamento.EM_ARMAZEM, "Aguardando alocação comercial.");

        try {
            System.out.println("Tentando aprovar contrato de 120 dias para equipamento com laudo de 140 dias...");
            bombaInfusao.validarAptoParaNovaLocacao(120, hoje);
            System.out.println("-> SUCESSO INESPERADO: O sistema deveria ter bloqueado!");
        } catch (RegraNegocioException e) {
            System.out.println("-> BLOQUEIO CORRETO PELA REGRA RN02:");
            System.out.println("   " + e.getMessage());
        }

        System.out.println("\n[TESTE 3] Violação da Máquina de Estados (Tentativa de pular da Quarentena direto para Locado):");
        try {
            bombaInfusao.transicionarPara(EstadoEquipamento.QUARENTENA, "Devolução do cliente anterior.");
            // Pulo ilegal:
            bombaInfusao.transicionarPara(EstadoEquipamento.LOCADO, "Tentativa de entrega expressa sem calibração.");
        } catch (RegraNegocioException e) {
            System.out.println("-> BLOQUEIO CORRETO DA MÁQUINA DE ESTADOS:");
            System.out.println("   " + e.getMessage());
        }

        System.out.println("\n[TESTE 4] Exibição da Trilha de Auditoria e Integridade:");
        ventilador1.imprimirFichaCadastral();
        bombaInfusao.imprimirFichaCadastral();
    }
}
