/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Exercício 4 - Matriz de Decisão Multicriterio Parametrizada para
 *       Seleção e Justificativa de Modelos de Processos de Software (SDLC).
 *
 * Como compilar:
 *   javac MatrizDecisaoProcessoSoftware.java
 * Como executar:
 *   java MatrizDecisaoProcessoSoftware
 */

import java.util.ArrayList;
import java.util.List;

public class MatrizDecisaoProcessoSoftware {

    /**
     * Modelos classicos e evolutivos de processos avaliados na matriz.
     */
    public enum ModeloProcesso {
        CASCATA("Modelo Cascata (Linear Sequencial)"),
        PROTOTIPACAO("Modelo de Prototipacao"),
        INCREMENTAL("Modelo Incremental"),
        ESPIRAL("Modelo Espiral de Barry Boehm");

        private final String nomeExibicao;

        ModeloProcesso(String nomeExibicao) {
            this.nomeExibicao = nomeExibicao;
        }

        public String getNomeExibicao() {
            return nomeExibicao;
        }
    }

    /**
     * Escala qualitativa-quantitativa para ponderacao dos criterios de engenharia.
     */
    public enum GrauClassificacao {
        MUITO_BAIXO(1),
        BAIXO(2),
        MEDIO(3),
        ALTO(4),
        MUITO_ALTO(5);

        private final int pontuacao;

        GrauClassificacao(int pontuacao) {
            this.pontuacao = pontuacao;
        }

        public int getPontuacao() {
            return pontuacao;
        }
    }

    /**
     * Representacao das caracteristicas e restricoes de um projeto concreto.
     */
    public static class PerfilProjeto {
        private final String nomeProjeto;
        private final String descricao;
        private final GrauClassificacao clarezaEstabilidadeRequisitos;
        private final GrauClassificacao necessidadeEnvolvimentoCliente;
        private final GrauClassificacao criticidadeRiscoSeguranca;
        private final GrauClassificacao urgenciaTimeMarket;
        private final GrauClassificacao toleranciaRetrabalho;

        public PerfilProjeto(String nomeProjeto, String descricao,
                             GrauClassificacao clarezaEstabilidadeRequisitos,
                             GrauClassificacao necessidadeEnvolvimentoCliente,
                             GrauClassificacao criticidadeRiscoSeguranca,
                             GrauClassificacao urgenciaTimeMarket,
                             GrauClassificacao toleranciaRetrabalho) {
            this.nomeProjeto = nomeProjeto;
            this.descricao = descricao;
            this.clarezaEstabilidadeRequisitos = clarezaEstabilidadeRequisitos;
            this.necessidadeEnvolvimentoCliente = necessidadeEnvolvimentoCliente;
            this.criticidadeRiscoSeguranca = criticidadeRiscoSeguranca;
            this.urgenciaTimeMarket = urgenciaTimeMarket;
            this.toleranciaRetrabalho = toleranciaRetrabalho;
        }

        public String getNomeProjeto() { return nomeProjeto; }
        public String getDescricao() { return descricao; }
        public GrauClassificacao getClarezaEstabilidadeRequisitos() { return clarezaEstabilidadeRequisitos; }
        public GrauClassificacao getNecessidadeEnvolvimentoCliente() { return necessidadeEnvolvimentoCliente; }
        public GrauClassificacao getCriticidadeRiscoSeguranca() { return criticidadeRiscoSeguranca; }
        public GrauClassificacao getUrgenciaTimeMarket() { return urgenciaTimeMarket; }
        public GrauClassificacao getToleranciaRetrabalho() { return toleranciaRetrabalho; }
    }

    /**
     * Motor de calculo de aderencia entre o perfil do projeto e o modelo de ciclo de vida.
     */
    public static class MotorSelecaoSDLC {

        public static double calcularAderencia(PerfilProjeto projeto, ModeloProcesso modelo) {
            double score = 0.0;

            switch (modelo) {
                case CASCATA:
                    // Cascata exige requisitos claros (peso 30%), lida mal com retrabalho tardio (peso 30%)
                    // e e ideal para seguranca critica e conformidade formal (peso 40%)
                    score += (projeto.getClarezaEstabilidadeRequisitos().getPontuacao() / 5.0) * 35.0;
                    score += (projeto.getCriticidadeRiscoSeguranca().getPontuacao() / 5.0) * 35.0;
                    score += ((6 - projeto.getToleranciaRetrabalho().getPontuacao()) / 5.0) * 20.0;
                    score += ((6 - projeto.getNecessidadeEnvolvimentoCliente().getPontuacao()) / 5.0) * 10.0;
                    break;

                case PROTOTIPACAO:
                    // Prototipacao brilha quando requisitos sao incertos, cliente quer participar ativamente
                    // e o foco e descobrir UI/UX e regras nao documentadas
                    score += ((6 - projeto.getClarezaEstabilidadeRequisitos().getPontuacao()) / 5.0) * 35.0;
                    score += (projeto.getNecessidadeEnvolvimentoCliente().getPontuacao() / 5.0) * 35.0;
                    score += (projeto.getUrgenciaTimeMarket().getPontuacao() / 5.0) * 20.0;
                    score += (projeto.getToleranciaRetrabalho().getPontuacao() / 5.0) * 10.0;
                    break;

                case INCREMENTAL:
                    // Incremental e excelente para entrega de ROI parcelado, requisitos relativamente conhecidos
                    // no core, e time-to-market urgente para o primeiro modulo
                    score += (projeto.getUrgenciaTimeMarket().getPontuacao() / 5.0) * 35.0;
                    score += (projeto.getClarezaEstabilidadeRequisitos().getPontuacao() / 5.0) * 25.0;
                    score += (projeto.getNecessidadeEnvolvimentoCliente().getPontuacao() / 5.0) * 20.0;
                    score += (projeto.getToleranciaRetrabalho().getPontuacao() / 5.0) * 20.0;
                    break;

                case ESPIRAL:
                    // Espiral e dominado pela capacidade de mitigar riscos tecnicos/financeiros severos
                    // e gerenciar projetos inovadores de alta complexidade
                    score += (projeto.getCriticidadeRiscoSeguranca().getPontuacao() / 5.0) * 40.0;
                    score += (projeto.getNecessidadeEnvolvimentoCliente().getPontuacao() / 5.0) * 25.0;
                    score += ((6 - projeto.getClarezaEstabilidadeRequisitos().getPontuacao()) / 5.0) * 20.0;
                    score += (projeto.getToleranciaRetrabalho().getPontuacao() / 5.0) * 15.0;
                    break;
            }
            return score;
        }
    }

    /**
     * Executa a analise dos 4 cenarios da atividade e exibe a matriz de decisao parametrizada.
     */
    public static void executarAvaliacaoProjetos() {
        List<PerfilProjeto> projetos = new ArrayList<PerfilProjeto>();

        projetos.add(new PerfilProjeto(
            "Marcapasso Cardiaco Implantavel",
            "Sistema safety-critical; risco de morte; requisitos fisiologicos estritos e regulados.",
            GrauClassificacao.MUITO_ALTO, // Requisitos estaveis
            GrauClassificacao.BAIXO,      // Cliente final (coracao) nao avalia telas
            GrauClassificacao.MUITO_ALTO, // Risco critico maximo
            GrauClassificacao.BAIXO,      // Qualidade formal prevalece sobre pressa de lancamento
            GrauClassificacao.MUITO_BAIXO // Tolerancia ZERO a retrabalho em producao
        ));

        projetos.add(new PerfilProjeto(
            "App Gamificado de Habitos (Jovens)",
            "Aplicativo de consumo; interface altamente experimental; publico exigente e volatil.",
            GrauClassificacao.MUITO_BAIXO, // Requisitos incertos e desconhecidos
            GrauClassificacao.MUITO_ALTO,  // Envolvimento maximo de usuarios em testes de UI/UX
            GrauClassificacao.BAIXO,       // Nao ha risco a vida nem penalidade regulatoria
            GrauClassificacao.MUITO_ALTO,  // Time-to-market critico para pegar a onda de mercado
            GrauClassificacao.ALTO         // Retrabalho em prototipo visual e barato e aceitavel
        ));

        projetos.add(new PerfilProjeto(
            "Sistema Liquidacao Bancaria (Database X)",
            "Processamento de 60.000 TPS; adocao de tecnologia inovadora nao testada; risco de perda contabil.",
            GrauClassificacao.MEDIO,      // Objetivos claros, mas arquitetura incerta
            GrauClassificacao.ALTO,       // Diretores de TI e reguladores acompanham marcos
            GrauClassificacao.MUITO_ALTO, // Risco de dezenas de milhoes em colapso
            GrauClassificacao.MEDIO,      // Prazo relevante, mas solidez contabil e imperativa
            GrauClassificacao.BAIXO       // Retrabalho pos-construcao seria fatal
        ));

        projetos.add(new PerfilProjeto(
            "ERP Modular para Hospital Universitario",
            "Substituicao gradual de fichas de papel; necessidade de operacao imediata do cadastro.",
            GrauClassificacao.ALTO,       // Processos administrativos conhecidos
            GrauClassificacao.MEDIO,      // Homologacao modular por departamento
            GrauClassificacao.MEDIO,      // Criticidade institucional controlada
            GrauClassificacao.MUITO_ALTO, // Hospital precisa faturar e cadastrar no Mes 1
            GrauClassificacao.MEDIO       // Modulos futuros absorvem melhorias
        ));

        for (PerfilProjeto p : projetos) {
            System.out.println("================================================================================");
            System.out.printf("ANALISE MULTICRITERIO: %s%n", p.getNomeProjeto().toUpperCase());
            System.out.println("================================================================================");
            System.out.printf("Descricao: %s%n", p.getDescricao());
            System.out.printf("Parametros: Requisitos=%s | Envolvimento=%s | Risco=%s | Urgencia=%s%n%n",
                p.getClarezaEstabilidadeRequisitos(), p.getNecessidadeEnvolvimentoCliente(),
                p.getCriticidadeRiscoSeguranca(), p.getUrgenciaTimeMarket());

            ModeloProcesso modeloVencedor = null;
            double maiorScore = -1.0;

            System.out.printf("%-38s | %-15s | %-20s%n", "Modelo de Ciclo de Vida", "Pontuacao (0-100)", "Parecer de Engenharia");
            System.out.println("--------------------------------------------------------------------------------");

            for (ModeloProcesso m : ModeloProcesso.values()) {
                double score = MotorSelecaoSDLC.calcularAderencia(p, m);
                String parecer = (score >= 75.0) ? "ALTAMENTE RECOMENDADO" :
                                 (score >= 50.0) ? "ADMISSIVEL C/ RESSALVAS" : "CONTRAINDICADO";

                if (score > maiorScore) {
                    maiorScore = score;
                    modeloVencedor = m;
                }
                System.out.printf("%-38s | %13.1f pts | %-20s%n", m.getNomeExibicao(), score, parecer);
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("MODELO SELECIONADO: %s (Indice: %.1f%%)%n%n",
                modeloVencedor.getNomeExibicao(), maiorScore);
        }
    }

    public static void main(String[] args) {
        System.out.println("UNIVERSIDADE BRASIL - UniFEF / ENGENHARIA DE SOFTWARE I");
        System.out.println("Estudo Pratico: Matriz de Decisao Parametrizada de Processos de Software\n");

        executarAvaliacaoProjetos();
    }
}
