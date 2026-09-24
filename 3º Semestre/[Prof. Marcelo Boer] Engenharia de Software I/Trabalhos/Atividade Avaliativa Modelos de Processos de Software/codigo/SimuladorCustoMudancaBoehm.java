/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Exercício 1 - Estudo Comparativo entre Modelo Cascata e Prototipação
 *       e a Curva Exponencial de Custo de Mudança de Barry Boehm.
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
     * Representacao das fases genericas de processo de software e o multiplicador
     * empirico de custo relativo proposto por Barry Boehm.
     */
    public enum FaseSDLC {
        REQUISITOS(1.0, "Fase 1: Levantamento e Analise de Requisitos"),
        PROJETO_ARQUITETURAL(5.0, "Fase 2: Projeto de Arquitetura e Modelagem"),
        IMPLEMENTACAO(10.0, "Fase 3: Construcao e Codificacao"),
        TESTES_SISTEMA(50.0, "Fase 4: Integracao e Testes de Sistema"),
        PRODUCAO_MANUTENCAO(200.0, "Fase 5: Operacao e Manutencao em Producao");

        private final double multiplicadorBoehm;
        private final String descricao;

        FaseSDLC(double multiplicadorBoehm, String descricao) {
            this.multiplicadorBoehm = multiplicadorBoehm;
            this.descricao = descricao;
        }

        public double getMultiplicadorBoehm() {
            return multiplicadorBoehm;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Classe que modela um defeito de requisito e o impacto financeiro de sua deteccao
     * tardia versus deteccao precoce.
     */
    public static class DefeitoRequisito {
        private final String identificador;
        private final String descricao;
        private final double custoBaseCorrecao;
        private final boolean criticoParaVida;
        private final double penalidadeRecallRegulatorio;

        public DefeitoRequisito(String identificador, String descricao, double custoBaseCorrecao,
                                boolean criticoParaVida, double penalidadeRecallRegulatorio) {
            this.identificador = identificador;
            this.descricao = descricao;
            this.custoBaseCorrecao = custoBaseCorrecao;
            this.criticoParaVida = criticoParaVida;
            this.penalidadeRecallRegulatorio = penalidadeRecallRegulatorio;
        }

        public double calcularCustoCorrecao(FaseSDLC faseDescoberta) {
            double custoCalculado = custoBaseCorrecao * faseDescoberta.getMultiplicadorBoehm();
            if (faseDescoberta == FaseSDLC.PRODUCAO_MANUTENCAO && criticoParaVida) {
                custoCalculado += penalidadeRecallRegulatorio;
            }
            return custoCalculado;
        }

        public String getIdentificador() {
            return identificador;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isCriticoParaVida() {
            return criticoParaVida;
        }
    }

    /**
     * Simula o Cenário A do exercício: Software de Marcapasso Cardíaco Implantável.
     * Demonstra por que modelos rigorosos (Cascata/Modelo V com verificação formal)
     * buscam eliminar defeitos antes da producao, onde o custo de mudanca e catastrofico.
     */
    public static void simularCenarioMarcapasso() {
        System.out.println("================================================================================");
        System.out.println("CENARIO A: SOFTWARE DE SEGURANCA CRITICA - MARCAPASSO CARDIACO (IEC 62304 / ISO 14971)");
        System.out.println("================================================================================");
        System.out.println("Contexto: Requisitos altamente estaveis, baseados em fisiologia cardiaca.");
        System.out.println("Objetivo: Evitar a qualquer custo que um defeito atinja a fase de Producao.");
        System.out.println();

        DefeitoRequisito defeitoRitmo = new DefeitoRequisito(
            "DEF-CARD-001",
            "Calculo incorreto do intervalo de estimulacao ventricular sob taquicardia",
            1200.00,
            true,
            4500000.00 // Penalidade de recall cirurgico, hospitalar e processos regulatorios
        );

        System.out.printf("Defeito Analisado: [%s] %s%n", defeitoRitmo.getIdentificador(), defeitoRitmo.getDescricao());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-35s | %-15s | %-20s%n", "Fase de Deteccao", "Multiplicador", "Custo Total (R$)");
        System.out.println("--------------------------------------------------------------------------------");

        for (FaseSDLC fase : FaseSDLC.values()) {
            double custo = defeitoRitmo.calcularCustoCorrecao(fase);
            System.out.printf("%-35s | %13.1fx | R$ %,16.2f%n",
                fase.getDescricao(),
                fase.getMultiplicadorBoehm(),
                custo
            );
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Conclusao Tecnica Cenário A:");
        System.out.println("- Deteccao em Requisitos: Correcao documental com revisao tecnica formal (R$ 1.200,00).");
        System.out.println("- Deteccao em Producao: Cirurgia de explicacao de dispositivo invasivo, indenizacoes");
        System.out.println("  e sancoes da ANVISA/FDA (Mais de R$ 4,7 milhoes). Custo virtualmente infinito.");
        System.out.println("- Justificativa para Modelo Cascata / Modelo V: Especificacao matematica rigorosa e");
        System.out.println("  congelamento (baseline) antes de codificar sao obrigatorios por lei.");
        System.out.println();
    }

    /**
     * Simula o Cenário B do exercício: Aplicativo Móvel de Gamificação de Hábitos Saudáveis.
     * Compara o desenvolvimento sem protótipo (Cascata puro) versus abordagem orientada a Prototipação.
     */
    public static void simularCenarioAplicativoGamificado() {
        System.out.println("================================================================================");
        System.out.println("CENARIO B: APP MOVEL DE GAMIFICACAO DE HABITOS - PUBLICO JOVEM (UI/UX VOLATIL)");
        System.out.println("================================================================================");
        System.out.println("Contexto: Alta volatilidade de requisitos. O usuario final nao sabe o que quer");
        System.out.println("          ate ver e tocar na tela (efeito IKIWISI: 'I will know it when I see it').");
        System.out.println();

        List<DefeitoRequisito> defeitosInterface = new ArrayList<DefeitoRequisito>();
        defeitosInterface.add(new DefeitoRequisito(
            "DEF-UI-01", "Mecanica de pontuacao de habitos frustrante para jovens (abandono em 30s)",
            800.00, false, 0.0
        ));
        defeitosInterface.add(new DefeitoRequisito(
            "DEF-UI-02", "Fluxo de cadastro longo e sem suporte a login social rapido",
            500.00, false, 0.0
        ));
        defeitosInterface.add(new DefeitoRequisito(
            "DEF-UI-03", "Paleta de cores e tipografia de dificil legibilidade sob luz solar",
            400.00, false, 0.0
        ));

        // Estrategia 1: Modelo Cascata Puro (Requisitos congelados sem prototipo visual)
        // Os problemas de rejeicao de tela so sao descobertos em PRODUCAO (pelo publico nas lojas)
        double custoTotalCascata = 0.0;
        for (DefeitoRequisito d : defeitosInterface) {
            custoTotalCascata += d.calcularCustoCorrecao(FaseSDLC.PRODUCAO_MANUTENCAO);
        }

        // Estrategia 2: Modelo de Prototipacao Descartavel (Prototipos no Figma e maquete rapida)
        // Os problemas de UI/UX sao detectados e corrigidos ainda no ciclo de REQUISITOS
        double custoInvestimentoPrototipo = 3500.00; // Custo de confeccao e testes com usuarios
        double custoCorrecaoPrototipo = 0.0;
        for (DefeitoRequisito d : defeitosInterface) {
            custoCorrecaoPrototipo += d.calcularCustoCorrecao(FaseSDLC.REQUISITOS);
        }
        double custoTotalPrototipacao = custoInvestimentoPrototipo + custoCorrecaoPrototipo;

        System.out.printf("Abordagem 1 - Modelo Cascata (Tentativa de congelar requisitos textuais de UI):%n");
        System.out.printf("  -> Defeitos descobertos somente apos publicacao na Google Play e App Store.%n");
        System.out.printf("  -> Retrabalho completo de telas, refatoracao de endpoints e perda de usuarios.%n");
        System.out.printf("  -> Custo Total de Retrabalho: R$ %,.2f%n%n", custoTotalCascata);

        System.out.printf("Abordagem 2 - Modelo de Prototipacao (Validacao rapida com usuarios reais):%n");
        System.out.printf("  -> Investimento em construcao e sessoes de teste do prototipo: R$ %,.2f%n", custoInvestimentoPrototipo);
        System.out.printf("  -> Custo de readequacao dos requisitos na fase inicial: R$ %,.2f%n", custoCorrecaoPrototipo);
        System.out.printf("  -> Custo Total Consolidado: R$ %,.2f%n%n", custoTotalPrototipacao);

        double economia = custoTotalCascata - custoTotalPrototipacao;
        double percentualEconomia = (economia / custoTotalCascata) * 100.0;

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("ECONOMIA OBTIDA PELA PROTOTIPACAO: R$ %,.2f (%.1f%% de reducao de custo)%n",
            economia, percentualEconomia);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Conclusao Tecnica Cenário B:");
        System.out.println("- Prototipar interfaces incertas quebra a Curva Exponencial de Boehm, mantendo as");
        System.out.println("  modificacoes no multiplicador 1x antes de iniciar a construcao de software definitiva.");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("UNIVERSIDADE BRASIL - UniFEF / ENGENHARIA DE SOFTWARE I");
        System.out.println("Estudo Pratico: Curva de Custo de Mudanca de Boehm (Cascata vs Prototipacao)\n");

        simularCenarioMarcapasso();
        simularCenarioAplicativoGamificado();
    }
}
