/**
 * Disciplina: Engenharia de Software I
 * Tema: Curva de Custo de Mudança de Barry Boehm e Exposição de Riscos
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Marcelo Boer
 *
 * Como compilar:
 *   javac SimuladorCustoMudancaBoehm.java
 * Como executar:
 *   java SimuladorCustoMudancaBoehm
 */

import java.util.ArrayList;
import java.util.List;

public class SimuladorCustoMudancaBoehm {

    /**
     * Enumeração das fases clássicas do ciclo de vida de desenvolvimento de software.
     * Cada fase carrega o multiplicador empírico documentado por Barry Boehm e Roger Pressman,
     * que quantifica o custo relativo de corrigir um erro de especificação conforme o tempo avança.
     */
    public enum FaseCicloVida {
        ESPECIFICACAO_REQUISITOS("Requisitos", 1.0),
        PROJETO_ARQUITETURA("Projeto/Arquitetura", 3.0),
        CODIFICACAO("Codificação", 10.0),
        TESTES_SISTEMA("Testes de Sistema", 40.0),
        OPERACAO_PRODUCAO("Operação em Produção", 100.0);

        private final String descricao;
        private final double multiplicadorCusto;

        FaseCicloVida(String descricao, double multiplicadorCusto) {
            this.descricao = descricao;
            this.multiplicadorCusto = multiplicadorCusto;
        }

        public String getDescricao() {
            return descricao;
        }

        public double getMultiplicadorCusto() {
            return multiplicadorCusto;
        }
    }

    /**
     * Representa um defeito conceitual originado na fase de Requisitos.
     */
    public static class DefeitoRequisito {
        private final String id;
        private final String descricao;
        private final double custoBaseCorrecao;
        private final FaseCicloVida faseDescoberta;

        public DefeitoRequisito(String id, String descricao, double custoBaseCorrecao, FaseCicloVida faseDescoberta) {
            this.id = id;
            this.descricao = descricao;
            this.custoBaseCorrecao = custoBaseCorrecao;
            this.faseDescoberta = faseDescoberta;
        }

        public double calcularCustoReal() {
            return custoBaseCorrecao * faseDescoberta.getMultiplicadorCusto();
        }

        public String getId() {
            return id;
        }

        public String getDescricao() {
            return descricao;
        }

        public FaseCicloVida getFaseDescoberta() {
            return faseDescoberta;
        }
    }

    /**
     * Representa a fórmula de Barry Boehm para Exposição ao Risco: RE = Probabilidade * Impacto.
     */
    public static class RiscoProjeto {
        private final String id;
        private final String descricao;
        private final double probabilidade; // 0.0 a 1.0
        private final double impactoFinanceiro; // em Reais (BRL)

        public RiscoProjeto(String id, String descricao, double probabilidade, double impactoFinanceiro) {
            if (probabilidade < 0.0 || probabilidade > 1.0) {
                throw new IllegalArgumentException("Probabilidade deve estar entre 0.0 e 1.0");
            }
            this.id = id;
            this.descricao = descricao;
            this.probabilidade = probabilidade;
            this.impactoFinanceiro = impactoFinanceiro;
        }

        public double calcularExposicaoRisco() {
            return this.probabilidade * this.impactoFinanceiro;
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

        public double getImpactoFinanceiro() {
            return impactoFinanceiro;
        }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE I - SIMULAÇÃO: CURVA DE MUDANÇA E RISCOS (BOEHM)          ");
        System.out.println("  Docente: Prof. Marcelo Boer | UniFEF - Sistemas de Informação                    ");
        System.out.println("===================================================================================\n");

        // 1. Tabela Base da Curva Exponencial de Mudança de Barry Boehm
        double custoBaseUnitario = 500.00; // Custo médio para corrigir erro de requisito na Aula 01
        System.out.println("1. IMPACTO DO MOMENTO DA DETECÇÃO DO ERRO DE REQUISITO (Custo Base = R$ 500,00):");
        System.out.printf("%-25s | %-14s | %-16s%n", "Fase do Ciclo de Vida", "Multiplicador", "Custo Corrigido");
        System.out.println("-----------------------------------------------------------------------------------");
        for (FaseCicloVida fase : FaseCicloVida.values()) {
            double custoCalculado = custoBaseUnitario * fase.getMultiplicadorCusto();
            System.out.printf("%-25s | %12.1fx | R$ %13.2f%n",
                    fase.getDescricao(),
                    fase.getMultiplicadorCusto(),
                    custoCalculado);
        }
        System.out.println("-----------------------------------------------------------------------------------\n");

        // 2. Comparativo Estrutural: Projeto Cascata vs. Projeto Incremental/Ágil
        // Cenário: 10 defeitos críticos concebidos durante a fase de especificação
        List<DefeitoRequisito> cenarioCascata = new ArrayList<>();
        // No Cascata, requisitos congelados sem iterações levam a descoberta tardia
        cenarioCascata.add(new DefeitoRequisito("DEF-01", "Regra de arredondamento de juros bancários", custoBaseUnitario, FaseCicloVida.CODIFICACAO));
        cenarioCascata.add(new DefeitoRequisito("DEF-02", "Validação de CPF/CNPJ divergente do SEFAZ", custoBaseUnitario, FaseCicloVida.CODIFICACAO));
        cenarioCascata.add(new DefeitoRequisito("DEF-03", "Limite de conexões simultâneas subdimensionado", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioCascata.add(new DefeitoRequisito("DEF-04", "Incompatibilidade com certificado A1/A3", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioCascata.add(new DefeitoRequisito("DEF-05", "Protocolo de telemetria sem suporte a timeout", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioCascata.add(new DefeitoRequisito("DEF-06", "Falta de tratamento de estorno em cartões", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioCascata.add(new DefeitoRequisito("DEF-07", "Vazamento de memória em processamento em lote", custoBaseUnitario, FaseCicloVida.OPERACAO_PRODUCAO));
        cenarioCascata.add(new DefeitoRequisito("DEF-08", "Inconsistência de concorrência no estoque", custoBaseUnitario, FaseCicloVida.OPERACAO_PRODUCAO));
        cenarioCascata.add(new DefeitoRequisito("DEF-09", "Falha de segurança: injeção em endpoint SOAP", custoBaseUnitario, FaseCicloVida.OPERACAO_PRODUCAO));
        cenarioCascata.add(new DefeitoRequisito("DEF-10", "Violação da LGPD no log de auditoria fiscal", custoBaseUnitario, FaseCicloVida.OPERACAO_PRODUCAO));

        List<DefeitoRequisito> cenarioIncremental = new ArrayList<>();
        // No Incremental/Ágil, feedback precoce e testes contínuos antecipam a detecção (Shift-Left)
        cenarioIncremental.add(new DefeitoRequisito("DEF-01", "Regra de arredondamento de juros bancários", custoBaseUnitario, FaseCicloVida.ESPECIFICACAO_REQUISITOS));
        cenarioIncremental.add(new DefeitoRequisito("DEF-02", "Validação de CPF/CNPJ divergente do SEFAZ", custoBaseUnitario, FaseCicloVida.ESPECIFICACAO_REQUISITOS));
        cenarioIncremental.add(new DefeitoRequisito("DEF-03", "Limite de conexões simultâneas subdimensionado", custoBaseUnitario, FaseCicloVida.PROJETO_ARQUITETURA));
        cenarioIncremental.add(new DefeitoRequisito("DEF-04", "Incompatibilidade com certificado A1/A3", custoBaseUnitario, FaseCicloVida.PROJETO_ARQUITETURA));
        cenarioIncremental.add(new DefeitoRequisito("DEF-05", "Protocolo de telemetria sem suporte a timeout", custoBaseUnitario, FaseCicloVida.CODIFICACAO));
        cenarioIncremental.add(new DefeitoRequisito("DEF-06", "Falta de tratamento de estorno em cartões", custoBaseUnitario, FaseCicloVida.CODIFICACAO));
        cenarioIncremental.add(new DefeitoRequisito("DEF-07", "Vazamento de memória em processamento em lote", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioIncremental.add(new DefeitoRequisito("DEF-08", "Inconsistência de concorrência no estoque", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioIncremental.add(new DefeitoRequisito("DEF-09", "Falha de segurança: injeção em endpoint SOAP", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));
        cenarioIncremental.add(new DefeitoRequisito("DEF-10", "Violação da LGPD no log de auditoria fiscal", custoBaseUnitario, FaseCicloVida.TESTES_SISTEMA));

        double custoTotalCascata = 0.0;
        for (DefeitoRequisito def : cenarioCascata) {
            custoTotalCascata += def.calcularCustoReal();
        }

        double custoTotalIncremental = 0.0;
        for (DefeitoRequisito def : cenarioIncremental) {
            custoTotalIncremental += def.calcularCustoReal();
        }

        System.out.println("2. ESTUDO COMPARATIVO DE CORREÇÃO DE DEFEITOS (10 DEFEITOS EQUIVALENTES):");
        System.out.printf(" - Custo Total no Modelo Cascata     : R$ %11.2f (Detecção tardia em Testes/Operação)%n", custoTotalCascata);
        System.out.printf(" - Custo Total no Modelo Incremental : R$ %11.2f (Detecção antecipada via Sprints)%n", custoTotalIncremental);
        double economia = custoTotalCascata - custoTotalIncremental;
        double percentualEconomia = (economia / custoTotalCascata) * 100.0;
        System.out.printf(" -> ECONOMIA FINANCEIRA TOTAL       : R$ %11.2f (%.1f%% de redução de retrabalho)%n\n",
                economia, percentualEconomia);

        // 3. Matriz de Exposição ao Risco (Barry Boehm - Modelo Espiral)
        System.out.println("3. AUDITORIA QUANTITATIVA DE RISCOS (EXPOSIÇÃO DE BOEHM: RE = P * I):");
        List<RiscoProjeto> matrizRiscos = new ArrayList<>();
        matrizRiscos.add(new RiscoProjeto("RSK-01", "Latência crítica no barramento de frenagem eletrônica", 0.35, 600000.00));
        matrizRiscos.add(new RiscoProjeto("RSK-02", "Mudança de regulação tributária durante o projeto", 0.20, 150000.00));
        matrizRiscos.add(new RiscoProjeto("RSK-03", "Equipe sem experiência prévia no framework de concorrência", 0.50, 80000.00));
        matrizRiscos.add(new RiscoProjeto("RSK-04", "Incompatibilidade com hardware legado de telemetria", 0.40, 250000.00));

        double exposicaoTotal = 0.0;
        System.out.printf("%-8s | %-4s | %-15s | %-15s | %-35s%n", "ID", "Prob", "Impacto Base", "Exposição (RE)", "Descrição do Risco");
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (RiscoProjeto r : matrizRiscos) {
            double exp = r.calcularExposicaoRisco();
            exposicaoTotal += exp;
            System.out.printf("%-8s | %4.2f | R$ %12.2f | R$ %12.2f | %-35s%n",
                    r.getId(),
                    r.getProbabilidade(),
                    r.getImpactoFinanceiro(),
                    exp,
                    r.getDescricao());
        }
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf("EXPOSIÇÃO TOTAL ACUMULADA AO RISCO: R$ %12.2f%n", exposicaoTotal);
        System.out.println("DIRETRIZ DE ENGENHARIA: O Quadrante 2 da Espiral deve prototipar o risco RSK-01 imediatamente\n" +
                           "antes de autorizar qualquer contratação para a fase de construção maciça.");
        System.out.println("===================================================================================");
    }
}
