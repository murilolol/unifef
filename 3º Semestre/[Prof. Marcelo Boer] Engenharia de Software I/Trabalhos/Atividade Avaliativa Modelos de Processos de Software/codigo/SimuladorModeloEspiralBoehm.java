/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Exercício 2 - Decomposição dos Quatro Quadrantes do Modelo Espiral de Barry Boehm
 *       e Analise de Riscos Tecnicos com Spike Arquitetural de Banco de Dados Distribuido.
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
     * Identificacao formal dos quatro quadrantes propostos por Barry Boehm (1988).
     */
    public enum QuadranteEspiral {
        QUADRANTE_1("1. Determinacao de Objetivos, Alternativas e Restricoes"),
        QUADRANTE_2("2. Identificacao, Avaliacao e Mitigacao de Riscos"),
        QUADRANTE_3("3. Desenvolvimento, Construcao e Verificacao do Produto"),
        QUADRANTE_4("4. Revisao pelos Stakeholders e Planejamento da Proxima Fase");

        private final String rotulo;

        QuadranteEspiral(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    /**
     * Representacao formal de um risco tecnico ou de negocio mapeado no Quadrante 2.
     */
    public static class RiscoEngenharia {
        private final String codigo;
        private final String descricao;
        private final double probabilidade; // Escala 0.0 a 1.0
        private final int impacto;           // Escala 1 (baixo) a 5 (catastrofico)
        private final String acaoMitigacao;

        public RiscoEngenharia(String codigo, String descricao, double probabilidade,
                               int impacto, String acaoMitigacao) {
            this.codigo = codigo;
            this.descricao = descricao;
            this.probabilidade = probabilidade;
            this.impacto = impacto;
            this.acaoMitigacao = acaoMitigacao;
        }

        public double calcularExposicaoRisco() {
            return probabilidade * impacto;
        }

        public String classificarSeveridade() {
            double exposicao = calcularExposicaoRisco();
            if (exposicao >= 3.5) return "CRITICA";
            if (exposicao >= 2.0) return "ALTA";
            if (exposicao >= 1.0) return "MEDIA";
            return "BAIXA";
        }

        public String getCodigo() { return codigo; }
        public String getDescricao() { return descricao; }
        public String getAcaoMitigacao() { return acaoMitigacao; }
    }

    /**
     * Modelo de transacao financeira para o sistema de liquidacao bancaria de alta performance.
     */
    public static class TransacaoFinanceira {
        private final String idTransacao;
        private final String contaOrigem;
        private final String contaDestino;
        private final double valor;

        public TransacaoFinanceira(String idTransacao, String contaOrigem, String contaDestino, double valor) {
            this.idTransacao = idTransacao;
            this.contaOrigem = contaOrigem;
            this.contaDestino = contaDestino;
            this.valor = valor;
        }

        public String getIdTransacao() { return idTransacao; }
        public double getValor() { return valor; }
    }

    /**
     * Interface para os motores de banco de dados avaliados durante o Spike Tecnico.
     */
    public interface MotorPersistencia {
        String getNomeTecnologia();
        boolean executarTransacao(TransacaoFinanceira transacao, boolean particaoDeRedeAtiva);
        int getTotalInconsistenciasDetectadas();
        double getSaldoFinalContaOrigem();
    }

    /**
     * Simulacao da tecnologia inovadora 'Database X' (NoSQL / Consistencia Eventual).
     * Em situacao de particao de rede, prioriza disponibilidade e gera gasto duplo (double-spending).
     */
    public static class DatabaseXSpike implements MotorPersistencia {
        private double saldoContaOrigem = 10000.00;
        private int inconsistencias = 0;

        @Override
        public String getNomeTecnologia() {
            return "Database X (NoSQL Inovador - Consistencia Eventual / Teorema CAP)";
        }

        @Override
        public boolean executarTransacao(TransacaoFinanceira transacao, boolean particaoDeRedeAtiva) {
            if (particaoDeRedeAtiva) {
                // Falha do algoritmo sob particao: ambos os nós debitam sem lock distribuido garantido
                saldoContaOrigem -= transacao.getValor();
                // Inconsistencia contabil: o saldo nao deveria permitir saques concorrentes alem do limite
                if (saldoContaOrigem < 0) {
                    inconsistencias++;
                }
                return true; // Retorna sucesso falso-positivo (violacao de integridade ACID)
            }
            if (saldoContaOrigem >= transacao.getValor()) {
                saldoContaOrigem -= transacao.getValor();
                return true;
            }
            return false;
        }

        @Override
        public int getTotalInconsistenciasDetectadas() {
            return inconsistencias;
        }

        @Override
        public double getSaldoFinalContaOrigem() {
            return saldoContaOrigem;
        }
    }

    /**
     * Simulacao da tecnologia alternativa: Cluster Relacional com Consenso Estrito (Raft/Paxos).
     * Em situacao de particao de rede, rejeita ou enfileira operacoes para preservar integridade ACID.
     */
    public static class ClusterPostgresConsistente implements MotorPersistencia {
        private double saldoContaOrigem = 10000.00;
        private int inconsistencias = 0;

        @Override
        public String getNomeTecnologia() {
            return "Cluster Relacional Particionado com Consenso Estrito (Garantia ACID)";
        }

        @Override
        public boolean executarTransacao(TransacaoFinanceira transacao, boolean particaoDeRedeAtiva) {
            if (particaoDeRedeAtiva) {
                // Comportamento correto de engenharia bancaria: fail-fast para proteger o patrimonio
                // Rejeita a transacao com codigo de erro temporario em vez de corromper o saldo
                return false;
            }
            if (saldoContaOrigem >= transacao.getValor()) {
                saldoContaOrigem -= transacao.getValor();
                return true;
            }
            return false;
        }

        @Override
        public int getTotalInconsistenciasDetectadas() {
            return inconsistencias;
        }

        @Override
        public double getSaldoFinalContaOrigem() {
            return saldoContaOrigem;
        }
    }

    /**
     * Executa a demonstracao completa de um ciclo da Espiral de Barry Boehm.
     */
    public static void executarCicloEspiral() {
        System.out.println("================================================================================");
        System.out.println("SIMULACAO DO MODELO ESPIRAL DE BOEHM: PROJETO LIQUIDACAO BANCARIA 60.000 TPS");
        System.out.println("================================================================================\n");

        // --------------------------------------------------------------------
        // QUADRANTE 1: DEFINICAO DE OBJETIVOS, ALTERNATIVAS E RESTRICOES
        // --------------------------------------------------------------------
        System.out.println("[" + QuadranteEspiral.QUADRANTE_1.getRotulo() + "]");
        System.out.println("- Meta do Ciclo: Selecionar e homologar a arquitetura do motor de persistencia.");
        System.out.println("- Restricoes Tecnicas: Suportar 60.000 TPS, latencia < 15ms e conformidade estrita");
        System.out.println("  as regras de compensacao financeira do Banco Central (Zero perda / Sem gasto duplo).");
        System.out.println("- Alternativas Mapeadas:");
        System.out.println("    Alternativa A: Database X (NoSQL distribuido novo com promessa de 100k TPS).");
        System.out.println("    Alternativa B: Cluster Relacional Distribuido com consenso Raft e particionamento.");
        System.out.println();

        // --------------------------------------------------------------------
        // QUADRANTE 2: IDENTIFICACAO, AVALIACAO E MITIGACAO DE RISCOS
        // --------------------------------------------------------------------
        System.out.println("[" + QuadranteEspiral.QUADRANTE_2.getRotulo() + "]");
        RiscoEngenharia riscoBanco = new RiscoEngenharia(
            "RISK-ARCH-01",
            "Database X utiliza consistencia eventual e pode gerar gasto duplo sob particao de rede",
            0.75, // 75% de probabilidade em redes geograficamente distribuidas
            5,    // Impacto catastrofico (desbalanceamento patrimonial milionario e intervencao regulatoria)
            "Executar Spike Tecnico isolado com simulacao de caos (Jepsen test) antes de fechar a arquitetura."
        );

        System.out.printf("Risco Identificado: [%s] %s%n", riscoBanco.getCodigo(), riscoBanco.getDescricao());
        System.out.printf("Exposicao ao Risco: %.2f | Severidade: %s%n",
            riscoBanco.calcularExposicaoRisco(), riscoBanco.classificarSeveridade());
        System.out.printf("Acao de Mitigacao Obrigatória: %s%n%n", riscoBanco.getAcaoMitigacao());

        // --------------------------------------------------------------------
        // QUADRANTE 3: DESENVOLVIMENTO E VALIDACAO DO ENTREGAVEL DO CICLO (SPIKE)
        // --------------------------------------------------------------------
        System.out.println("[" + QuadranteEspiral.QUADRANTE_3.getRotulo() + "]");
        System.out.println("Construindo ambiente de bancada (Spike Tecnico) com 500 transacoes concorrentes de R$ 50,00");
        System.out.println("Saldo Inicial da Conta Teste: R$ 10.000,00 (Capacidade exata para 200 saques legitiomos).");
        System.out.println("Injetando falha controlada de particao de rede entre os nos durante os saques...\n");

        MotorPersistencia dbX = new DatabaseXSpike();
        MotorPersistencia clusterSql = new ClusterPostgresConsistente();

        // Executa 250 transacoes com particao de rede no Database X
        for (int i = 1; i <= 250; i++) {
            TransacaoFinanceira tx = new TransacaoFinanceira("TX-DBX-" + i, "CTA-001", "CTA-002", 50.00);
            dbX.executarTransacao(tx, true);
        }

        // Executa 250 transacoes com particao de rede no Cluster Relacional
        for (int i = 1; i <= 250; i++) {
            TransacaoFinanceira tx = new TransacaoFinanceira("TX-SQL-" + i, "CTA-001", "CTA-002", 50.00);
            clusterSql.executarTransacao(tx, true);
        }

        System.out.println("Resultados do Spike de Engenharia:");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-35s | %-18s | %-16s%n", "Motor Avaliado", "Inconsistencias", "Saldo Final");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-35s | %18d | R$ %,13.2f%n",
            "Database X (NoSQL Inovador)", dbX.getTotalInconsistenciasDetectadas(), dbX.getSaldoFinalContaOrigem());
        System.out.printf("%-35s | %18d | R$ %,13.2f%n",
            "Cluster Relacional Consistente", clusterSql.getTotalInconsistenciasDetectadas(), clusterSql.getSaldoFinalContaOrigem());
        System.out.println("--------------------------------------------------------------------------------\n");

        // --------------------------------------------------------------------
        // QUADRANTE 4: REVISAO PELOS STAKEHOLDERS E PLANEJAMENTO DA PROXIMA FASE
        // --------------------------------------------------------------------
        System.out.println("[" + QuadranteEspiral.QUADRANTE_4.getRotulo() + "]");
        System.out.println("Parecer do Comite de Engenharia e Arquitetura:");
        System.out.println("- DECISAO: GO / NO-GO -> **NO-GO** para adocao do Database X no nucleo financeiro.");
        System.out.println("- Evidencia Empirica: O Database X permitiu saldo negativo irregular de R$ " +
            String.format("%,.2f", dbX.getSaldoFinalContaOrigem()) + " gerando " + dbX.getTotalInconsistenciasDetectadas() +
            " violacoes de saldo concorrente.");
        System.out.println("- Decisao de Continuidade: Aprovado o avanco para o proximo ciclo adotando a Alternativa B");
        System.out.println("  (Cluster Relacional Particionado), alocando orcamento para modelagem detalhada de tabelas.");
        System.out.println("- Economia do Modelo Espiral: O risco foi eliminado gastando R$ 35.000,00 em 10 dias de spike,");
        System.out.println("  evitando um prejuizo projetado de R$ 12.000.000,00 se a falha ocorresse em producao.");
        System.out.println("================================================================================");
    }

    public static void main(String[] args) {
        System.out.println("UNIVERSIDADE BRASIL - UniFEF / ENGENHARIA DE SOFTWARE I");
        System.out.println("Estudo Pratico: Gestao de Riscos no Modelo Espiral de Barry Boehm\n");

        executarCicloEspiral();
    }
}
