/**
 * Disciplina: Engenharia de Software I
 * Tema: Modelo Espiral de Barry Boehm - Condução Dirigida por Riscos
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Marcelo Boer
 *
 * Como compilar:
 *   javac SimuladorModeloEspiralBoehm.java
 * Como executar:
 *   java SimuladorModeloEspiralBoehm
 */

import java.util.ArrayList;
import java.util.List;

public class SimuladorModeloEspiralBoehm {

    /**
     * Representa uma avaliação técnica de risco com mitigação via protótipo executável (Spike).
     */
    public static class RiscoEspiral {
        private final String id;
        private final String descricao;
        private double probabilidade;
        private final double impacto;
        private boolean mitigado;

        public RiscoEspiral(String id, String descricao, double probabilidade, double impacto) {
            this.id = id;
            this.descricao = descricao;
            this.probabilidade = probabilidade;
            this.impacto = impacto;
            this.mitigado = false;
        }

        public double calcularExposicao() {
            return probabilidade * impacto;
        }

        public void aplicarPrototipoMitigador(double novaProbabilidade) {
            this.probabilidade = novaProbabilidade;
            this.mitigado = true;
        }

        public String getId() {
            return id;
        }

        public String getDescricao() {
            return descricao;
        }

        public double getProbabilidade() {
            return probabilidade;
        }

        public double getImpacto() {
            return impacto;
        }

        public boolean isMitigado() {
            return mitigado;
        }
    }

    /**
     * Representa uma volta completa (Loop) dentro da Espiral de Boehm,
     * percorrendo compulsoriamente os quatro quadrantes.
     */
    public static class VoltaEspiral {
        private final int numeroVolta;
        private final String metaPrincipal;
        private final List<RiscoEspiral> riscosIdentificados = new ArrayList<>();
        private double orcamentoAlocado;
        private double orcamentoConsumido;
        private boolean aprovadoParaProximaVolta;

        public VoltaEspiral(int numeroVolta, String metaPrincipal, double orcamentoAlocado) {
            this.numeroVolta = numeroVolta;
            this.metaPrincipal = metaPrincipal;
            this.orcamentoAlocado = orcamentoAlocado;
            this.orcamentoConsumido = 0.0;
        }

        public void adicionarRisco(RiscoEspiral risco) {
            this.riscosIdentificados.add(risco);
        }

        /**
         * Executa a rotação formal pelos quatro quadrantes da Espiral de Barry Boehm
         */
        public void executarCiclo() {
            System.out.printf("%n>>> INICIANDO VOLTA %d DA ESPIRAL: %s <<<%n", numeroVolta, metaPrincipal);

            // QUADRANTE 1: Determinar Objetivos, Alternativas e Restrições
            System.out.println("[QUADRANTE 1] Objetivos e Restrições:");
            System.out.println(" - Meta: " + metaPrincipal);
            System.out.printf(" - Orçamento Alocado para o Ciclo: R$ %.2f%n", orcamentoAlocado);

            // QUADRANTE 2: Avaliar Alternativas e Mitigar Riscos via Prototipação
            System.out.println("[QUADRANTE 2] Avaliação e Resolução de Riscos (Coração do Modelo): ");
            double exposicaoAntes = calcularExposicaoTotal();
            System.out.printf(" - Exposição Inicial de Risco: R$ %.2f%n", exposicaoAntes);

            for (RiscoEspiral r : riscosIdentificados) {
                System.out.printf("   * Analisando [%s]: %s (RE Inicial: R$ %.2f)%n",
                        r.getId(), r.getDescricao(), r.calcularExposicao());
                // Execução de Spike/Protótipo técnico para reduzir incerteza
                double reducaoFator = 0.25; // O protótipo reduz a probabilidade de falha em 75%
                r.aplicarPrototipoMitigador(r.getProbabilidade() * reducaoFator);
                System.out.printf("     -> Spike executado com sucesso! Nova Probabilidade: %.2f | Nova Exposição: R$ %.2f%n",
                        r.getProbabilidade(), r.calcularExposicao());
            }
            double exposicaoDepois = calcularExposicaoTotal();
            System.out.printf(" - Exposição Residual Mitigada: R$ %.2f (Redução de R$ %.2f)%n",
                    exposicaoDepois, (exposicaoAntes - exposicaoDepois));

            // QUADRANTE 3: Desenvolver e Verificar o Produto da Fase
            System.out.println("[QUADRANTE 3] Engenharia e Verificação do Nível:");
            this.orcamentoConsumido = this.orcamentoAlocado * 0.85;
            System.out.println(" - Artefatos construídos: Modelagem formal, esqueleto de código e testes automatizados.");
            System.out.printf(" - Custo Efetivo da Iteração: R$ %.2f%n", orcamentoConsumido);

            // QUADRANTE 4: Revisão com Stakeholders e Decisão de Avançar/Abortar (Go / No-Go)
            System.out.println("[QUADRANTE 4] Avaliação de Governança (Decisão Go / No-Go):");
            double tetoRiscoToleravel = 100000.00;
            if (exposicaoDepois <= tetoRiscoToleravel && orcamentoConsumido <= orcamentoAlocado) {
                this.aprovadoParaProximaVolta = true;
                System.out.println(" - DECISÃO EXECUTIVA: GO! Riscos controlados e orçamento cumprido.");
                System.out.println("   O projeto tem autorização para avançar para a próxima volta mais externa da espiral.");
            } else {
                this.aprovadoParaProximaVolta = false;
                System.out.println(" - DECISÃO EXECUTIVA: NO-GO! Exposição de risco excede os limites contratuais. Projeto suspenso.");
            }
        }

        public double calcularExposicaoTotal() {
            double total = 0.0;
            for (RiscoEspiral r : riscosIdentificados) {
                total += r.calcularExposicao();
            }
            return total;
        }

        public boolean isAprovadoParaProximaVolta() {
            return aprovadoParaProximaVolta;
        }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE I - SIMULADOR DO MODELO ESPIRAL DE BARRY BOEHM           ");
        System.out.println("  Estudo de Caso: Sistema Crítico Ferroviário de Frenagem e Telemetria            ");
        System.out.println("  Docente: Prof. Marcelo Boer | UniFEF - Sistemas de Informação                    ");
        System.out.println("===================================================================================");

        // Construção de 3 Voltas Sucessivas da Espiral de Boehm
        List<VoltaEspiral> espiralProjeto = new ArrayList<>();

        // Volta 1: Prova de Conceito do Algoritmo de Frenagem Eletrônica
        VoltaEspiral volta1 = new VoltaEspiral(1, "Viabilidade do Algoritmo Matemático de Frenagem", 150000.00);
        volta1.adicionarRisco(new RiscoEspiral("RSK-101", "Incerteza na convergência numérica do cálculo de atrito", 0.60, 200000.00));
        espiralProjeto.add(volta1);

        // Volta 2: Arquitetura de Comunicação em Tempo Real e Tolerância a Falhas
        VoltaEspiral volta2 = new VoltaEspiral(2, "Arquitetura de Barramento e Comunicação Redundante", 300000.00);
        volta2.adicionarRisco(new RiscoEspiral("RSK-201", "Tempo de resposta do barramento CAN sob sobrecarga", 0.50, 400000.00));
        volta2.adicionarRisco(new RiscoEspiral("RSK-202", "Falha no chaveamento para o canal redundante B", 0.30, 300000.00));
        espiralProjeto.add(volta2);

        // Volta 3: Homologação e Conformidade Regulatória (Norma EN 50128)
        VoltaEspiral volta3 = new VoltaEspiral(3, "Certificação e Validação em Trilha de Teste Real", 500000.00);
        volta3.adicionarRisco(new RiscoEspiral("RSK-301", "Incompatibilidade com sistema de sinalização em trilho úmido", 0.20, 250000.00));
        espiralProjeto.add(volta3);

        // Execução sequencial da Espiral
        for (VoltaEspiral volta : espiralProjeto) {
            volta.executarCiclo();
            if (!volta.isAprovadoParaProximaVolta()) {
                System.out.println("\n[ALERTA DE SEGURANÇA] Ciclo interrompido prematuramente para proteger vidas e capital.");
                break;
            }
        }

        System.out.println("\n===================================================================================");
        System.out.println("SÍNTESE ACADÊMICA: O Modelo Espiral prova sua superioridade em sistemas críticos\n" +
                           "porque cada centavo investido no Quadrante 3 é precedido pela eliminação empírica\n" +
                           "de riscos no Quadrante 2, evitando catástrofes operacionais em produção.");
        System.out.println("===================================================================================");
    }
}
