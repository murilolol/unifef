/**
 * Disciplina: Engenharia de Software I
 * Tema: O Processo Unificado e o RUP (Rational Unified Process)
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Marcelo Boer
 *
 * Como compilar:
 *   javac SimuladorProcessoUnificadoRup.java
 * Como executar:
 *   java SimuladorProcessoUnificadoRup
 */

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SimuladorProcessoUnificadoRup {

    /**
     * As 4 Fases Sequenciais do RUP (Eixo Horizontal - Tempo)
     */
    public enum FaseRup {
        INCEPTION("Concepção (Inception)", "LCO - Lifecycle Objective"),
        ELABORATION("Elaboração (Elaboration)", "LCA - Lifecycle Architecture"),
        CONSTRUCTION("Construção (Construction)", "IOC - Initial Operational Capability"),
        TRANSITION("Transição (Transition)", "PR - Product Release");

        private final String nome;
        private final String marcoDecisorio;

        FaseRup(String nome, String marcoDecisorio) {
            this.nome = nome;
            this.marcoDecisorio = marcoDecisorio;
        }

        public String getNome() {
            return nome;
        }

        public String getMarcoDecisorio() {
            return marcoDecisorio;
        }
    }

    /**
     * Disciplinas de Engenharia de Software (Eixo Vertical do RUP)
     */
    public enum DisciplinaRup {
        MODELAGEM_NEGOCIO("Modelagem de Negócios"),
        REQUISITOS("Engenharia de Requisitos"),
        ANALISE_E_PROJETO("Análise e Projeto Arquitetural"),
        IMPLEMENTACAO("Implementação e Codificação"),
        TESTES("Garantia da Qualidade e Testes"),
        IMPLANTACAO("Implantação e Transição");

        private final String rotulo;

        DisciplinaRup(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    /**
     * Estrutura que mapeia a intensidade de esforço (0%% a 100%%) de cada disciplina por fase,
     * evidenciando a bidimensionalidade canônica do RUP.
     */
    public static class MatrizBidimensionalRup {
        private final Map<FaseRup, Map<DisciplinaRup, Integer>> distribuicaoEsforco = new EnumMap<>(FaseRup.class);

        public MatrizBidimensionalRup() {
            for (FaseRup fase : FaseRup.values()) {
                distribuicaoEsforco.put(fase, new EnumMap<>(DisciplinaRup.class));
            }
            configurarMatrizPadrao();
        }

        private void configurarMatrizPadrao() {
            // Inception: foco pesado em Negócio e Requisitos de alto nível
            definir(FaseRup.INCEPTION, DisciplinaRup.MODELAGEM_NEGOCIO, 40);
            definir(FaseRup.INCEPTION, DisciplinaRup.REQUISITOS, 35);
            definir(FaseRup.INCEPTION, DisciplinaRup.ANALISE_E_PROJETO, 15);
            definir(FaseRup.INCEPTION, DisciplinaRup.IMPLEMENTACAO, 5);
            definir(FaseRup.INCEPTION, DisciplinaRup.TESTES, 5);
            definir(FaseRup.INCEPTION, DisciplinaRup.IMPLANTACAO, 0);

            // Elaboration: foco máximo em Arquitetura, Casos de Uso críticos e Prova de Conceito
            definir(FaseRup.ELABORATION, DisciplinaRup.MODELAGEM_NEGOCIO, 10);
            definir(FaseRup.ELABORATION, DisciplinaRup.REQUISITOS, 30);
            definir(FaseRup.ELABORATION, DisciplinaRup.ANALISE_E_PROJETO, 40);
            definir(FaseRup.ELABORATION, DisciplinaRup.IMPLEMENTACAO, 10);
            definir(FaseRup.ELABORATION, DisciplinaRup.TESTES, 10);
            definir(FaseRup.ELABORATION, DisciplinaRup.IMPLANTACAO, 0);

            // Construction: codificação maciça e testes intensos
            definir(FaseRup.CONSTRUCTION, DisciplinaRup.MODELAGEM_NEGOCIO, 0);
            definir(FaseRup.CONSTRUCTION, DisciplinaRup.REQUISITOS, 5);
            definir(FaseRup.CONSTRUCTION, DisciplinaRup.ANALISE_E_PROJETO, 15);
            definir(FaseRup.CONSTRUCTION, DisciplinaRup.IMPLEMENTACAO, 50);
            definir(FaseRup.CONSTRUCTION, DisciplinaRup.TESTES, 25);
            definir(FaseRup.CONSTRUCTION, DisciplinaRup.IMPLANTACAO, 5);

            // Transition: testes finais de aceitação, carga, homologação e deploy
            definir(FaseRup.TRANSITION, DisciplinaRup.MODELAGEM_NEGOCIO, 0);
            definir(FaseRup.TRANSITION, DisciplinaRup.REQUISITOS, 0);
            definir(FaseRup.TRANSITION, DisciplinaRup.ANALISE_E_PROJETO, 5);
            definir(FaseRup.TRANSITION, DisciplinaRup.IMPLEMENTACAO, 10);
            definir(FaseRup.TRANSITION, DisciplinaRup.TESTES, 35);
            definir(FaseRup.TRANSITION, DisciplinaRup.IMPLANTACAO, 50);
        }

        public void definir(FaseRup fase, DisciplinaRup disc, int percentual) {
            distribuicaoEsforco.get(fase).put(disc, percentual);
        }

        public int obter(FaseRup fase, DisciplinaRup disc) {
            return distribuicaoEsforco.get(fase).getOrDefault(disc, 0);
        }
    }

    /**
     * Simulação de Validação do Marco LCA (Lifecycle Architecture)
     */
    public static class LinhaBaseArquitetural {
        private final String nomeProjeto;
        private boolean esqueletoExecutavelTestado;
        private boolean casosDeUsoCriticosEstabilizados; // Pelo menos 80%%
        private boolean riscosArquiteturaisMitigados;
        private double latenciaBarramentoMs;
        private final double limiteLatenciaPermitidoMs;

        public LinhaBaseArquitetural(String nomeProjeto, double limiteLatenciaPermitidoMs) {
            this.nomeProjeto = nomeProjeto;
            this.limiteLatenciaPermitidoMs = limiteLatenciaPermitidoMs;
        }

        public void executarTesteCargaEsqueleto(double latenciaMedidaMs) {
            this.latenciaBarramentoMs = latenciaMedidaMs;
            this.esqueletoExecutavelTestado = true;
            this.riscosArquiteturaisMitigados = (latenciaMedidaMs <= limiteLatenciaPermitidoMs);
        }

        public void setCasosDeUsoCriticosEstabilizados(boolean estabilizados) {
            this.casosDeUsoCriticosEstabilizados = estabilizados;
        }

        public boolean avaliarMarcoLca(List<String> logsAuditoria) {
            logsAuditoria.add("Verificando Marco LCA para o projeto: " + nomeProjeto);
            boolean aprovado = true;

            if (!esqueletoExecutavelTestado) {
                logsAuditoria.add(" [FALHA LCA] Esqueleto executável da arquitetura não foi construído!");
                aprovado = false;
            } else {
                logsAuditoria.add(" [OK] Esqueleto executável validado em execução.");
            }

            if (!casosDeUsoCriticosEstabilizados) {
                logsAuditoria.add(" [FALHA LCA] Casos de uso de alto risco não foram detalhados (mínimo 80%).");
                aprovado = false;
            } else {
                logsAuditoria.add(" [OK] Casos de uso arquiteturais completamente especificados.");
            }

            if (latenciaBarramentoMs > limiteLatenciaPermitidoMs) {
                logsAuditoria.add(String.format(" [FALHA LCA] Latência de %.2f ms excede o teto regulatório de %.2f ms.",
                        latenciaBarramentoMs, limiteLatenciaPermitidoMs));
                aprovado = false;
            } else {
                logsAuditoria.add(String.format(" [OK] Desempenho arquitetural aprovado: %.2f ms (Teto: %.2f ms).",
                        latenciaBarramentoMs, limiteLatenciaPermitidoMs));
            }

            return aprovado;
        }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE I - SIMULADOR DO PROCESSO UNIFICADO (RUP)                 ");
        System.out.println("  Docente: Prof. Marcelo Boer | UniFEF - Sistemas de Informação                    ");
        System.out.println("===================================================================================\n");

        MatrizBidimensionalRup matriz = new MatrizBidimensionalRup();

        // 1. Exibição da Matriz Bidimensional de Esforço (Fases vs. Disciplinas)
        System.out.println("1. MATRIZ BIDIMENSIONAL DO RUP: ESFORÇO RELATIVO POR FASE E DISCIPLINA (%):");
        System.out.printf("%-32s | %-12s | %-12s | %-12s | %-12s%n",
                "Disciplina (Eixo Vertical)", "Inception", "Elaboration", "Construction", "Transition");
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (DisciplinaRup d : DisciplinaRup.values()) {
            System.out.printf("%-32s | %10d%% | %10d%% | %10d%% | %10d%%%n",
                    d.getRotulo(),
                    matriz.obter(FaseRup.INCEPTION, d),
                    matriz.obter(FaseRup.ELABORATION, d),
                    matriz.obter(FaseRup.CONSTRUCTION, d),
                    matriz.obter(FaseRup.TRANSITION, d));
        }
        System.out.println("---------------------------------------------------------------------------------------------------\n");

        // 2. Marcos do RUP
        System.out.println("2. MARCOS ARQUITETURAIS CANÔNICOS DO CICLO DE VIDA RUP:");
        for (FaseRup f : FaseRup.values()) {
            System.out.printf(" - Fase: %-25s -> Marco Conclusivo: %s%n", f.getNome(), f.getMarcoDecisorio());
        }
        System.out.println();

        // 3. Auditoria do Marco Mais Crítico: LCA (Final da Elaboração)
        System.out.println("3. AUDITORIA FORMAL DO MARCO LCA (LIFECYCLE ARCHITECTURE):");
        LinhaBaseArquitetural projetoTrem = new LinhaBaseArquitetural("Controle Ferroviário de Passageiros", 15.0);
        List<String> relatorioAuditoria = new ArrayList<>();

        // Cenário 1: Tentativa precipitada de avançar para Construção com arquitetura falha
        System.out.println("--- Tentativa 1: Arquitetura com Gargalo de Latência no Barramento ---");
        projetoTrem.setCasosDeUsoCriticosEstabilizados(true);
        projetoTrem.executarTesteCargaEsqueleto(28.4); // 28.4 ms ultrapassa o limite de 15.0 ms
        boolean resultadoTentativa1 = projetoTrem.avaliarMarcoLca(relatorioAuditoria);

        for (String log : relatorioAuditoria) {
            System.out.println(log);
        }
        if (!resultadoTentativa1) {
            System.out.println(">> DECISÃO DA GOVERNANÇA: Transição para Construção BLOQUEADA!");
            System.out.println(">> JUSTIFICATIVA TÉCNICA: Iniciar Construção sem LCA aprovado causaria retrabalho massivo\n" +
                               "   em dezenas de componentes. O time deve permanecer na Elaboração para refatorar o esqueleto.\n");
        }

        // Cenário 2: Após refatoração arquitetural (Spike de otimização de I/O não-bloqueante)
        System.out.println("--- Tentativa 2: Após Refatoração do Barramento para I/O Assíncrono ---");
        relatorioAuditoria.clear();
        projetoTrem.executarTesteCargaEsqueleto(8.2); // Latência dentro da margem segura de 15 ms
        boolean resultadoTentativa2 = projetoTrem.avaliarMarcoLca(relatorioAuditoria);

        for (String log : relatorioAuditoria) {
            System.out.println(log);
        }
        if (resultadoTentativa2) {
            System.out.println(">> DECISÃO DA GOVERNANÇA: Marco LCA HOMOLOGADO COM SUCESSO!");
            System.out.println(">> AUTORIZAÇÃO: O projeto avança para a fase de CONSTRUÇÃO (Marco IOC).\n" +
                               "   A equipe pode ser ampliada para implementar os casos de uso restantes em paralelo.");
        }
        System.out.println("===================================================================================");
    }
}
