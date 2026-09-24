/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Exercício 3 - Modelo Incremental Tradicional versus Abordagem Iterativa Ágil (Scrum),
 *       Fatiamento de Escopo, Linha de Base (Baseline) e Dinâmica de Mudanças.
 *
 * Como compilar:
 *   javac SimuladorIncrementalVsAgil.java
 * Como executar:
 *   java SimuladorIncrementalVsAgil
 */

import java.util.ArrayList;
import java.util.List;

public class SimuladorIncrementalVsAgil {

    /**
     * Representa uma Historia de Usuario / Requisito Funcional com fatiamento vertical
     * (cortando todas as camadas de arquitetura: Interface, Logica de Negocio e Persistencia).
     */
    public static class HistoriaUsuario {
        private final String id;
        private final String titulo;
        private final int valorNegocio; // 1 a 100
        private final int esforcoHoras;
        private final String modulo;

        public HistoriaUsuario(String id, String titulo, int valorNegocio, int esforcoHoras, String modulo) {
            this.id = id;
            this.titulo = titulo;
            this.valorNegocio = valorNegocio;
            this.esforcoHoras = esforcoHoras;
            this.modulo = modulo;
        }

        public String getId() { return id; }
        public String getTitulo() { return titulo; }
        public int getValorNegocio() { return valorNegocio; }
        public int getEsforcoHoras() { return esforcoHoras; }
        public String getModulo() { return modulo; }
    }

    /**
     * Simula o comportamento operacional do Modelo Incremental Tradicional.
     * Caracteristicas:
     * - BDUF Parcial (Big Design Up Front para a arquitetura global);
     * - Linha de base (baseline) contratual estatica de escopo;
     * - Entregas espacadas (a cada 3 meses);
     * - Alteracoes dependem de Comite Formal de Controle de Mudancas (CCB).
     */
    public static void simularModeloIncremental() {
        System.out.println("================================================================================");
        System.out.println("PARTE 1: MODELO INCREMENTAL TRADICIONAL (PLAN-DRIVEN COM BASELINE ESTÁTICO)");
        System.out.println("================================================================================");
        System.out.println("Cenario: ERP de Hospital Universitario planejado em 3 grandes incrementos.");
        System.out.println("- Incremento 1 (Mes 1 a 3): Nucleo do Sistema (Cadastro de Pacientes e Prontuario Basico)");
        System.out.println("- Incremento 2 (Mes 4 a 6): Modulo de Agendamento Ambulatorial e Leitos");
        System.out.println("- Incremento 3 (Mes 7 a 9): Modulo de Faturamento SUS e Estoque Farmaceutico");
        System.out.println();

        int tempoAtePrimeiroSoftwareFuncional = 90; // Dias (3 meses)
        double custoBurocraciaCCB = 18500.00;     // Custos de revisao documental, juridica e comite
        int atrasoDiasAprovacaoMudanca = 21;       // 3 semanas para reuniao e parecer do CCB

        System.out.println("Evento de Negocio: No final do Mes 2, a diretoria medica exige inclusao urgente");
        System.out.println("                  do modulo de Triagem com Protocolo Manchester por exigencia legal.");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Fluxo de Mudanca no Modelo Incremental:");
        System.out.println("1. Abertura formal de ECR (Engineering Change Request #402).");
        System.out.println("2. Bloqueio parcial das atividades ate analise de impacto no baseline original.");
        System.out.printf("3. Reuniao do Comite de Controle de Mudancas (CCB): Atraso de %d dias no cronograma.%n",
            atrasoDiasAprovacaoMudanca);
        System.out.printf("4. Custo administrativo e de retrabalho documental de baseline: R$ %,.2f%n",
            custoBurocraciaCCB);
        System.out.printf("5. Tempo total para o cliente operar o primeiro modulo: %d dias.%n",
            tempoAtePrimeiroSoftwareFuncional + atrasoDiasAprovacaoMudanca);
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    /**
     * Simula a Abordagem Iterativa Agil (Scrum).
     * Caracteristicas:
     * - Product Backlog vivo e repriorizado continuamente pelo Product Owner;
     * - Fatiamento vertical com geracao de valor funcional a cada 2 semanas (Sprint);
     * - Congelamento temporario de escopo restrito exclusivamente a Sprint em andamento;
     * - Feedback direto do usuario nas cerimonias de Sprint Review.
     */
    public static void simularAbordagemAgilScrum() {
        System.out.println("================================================================================");
        System.out.println("PARTE 2: ABORDAGEM ITERATIVA AGIL (SCRUM - VALUE-DRIVEN COM RETORNO RAPIDO)");
        System.out.println("================================================================================");
        System.out.println("Cenario: Mesmo ERP hospitalar executado em Sprints curtos de 2 semanas (10 dias uteis).");
        System.out.println();

        List<HistoriaUsuario> productBacklog = new ArrayList<HistoriaUsuario>();
        productBacklog.add(new HistoriaUsuario("US-01", "Cadastro rapido de paciente SUS com cartao nacional", 90, 40, "Nucleo"));
        productBacklog.add(new HistoriaUsuario("US-02", "Registro de anamnese basica do prontuario eletronico", 85, 45, "Nucleo"));
        productBacklog.add(new HistoriaUsuario("US-03", "Emissao de comprovante de atendimento", 50, 20, "Nucleo"));
        productBacklog.add(new HistoriaUsuario("US-04", "Agendamento de consulta ambulatorial simples", 70, 50, "Agendamento"));

        int sprintAtual = 1;
        int valorAcumuladoEntregue = 0;
        int diasTrabalhados = 0;

        System.out.println("Execucao da Sprint 1 (Semana 1 e 2 - 10 dias): Congelamento restrito ao Sprint Backlog");
        List<HistoriaUsuario> sprintBacklog = new ArrayList<HistoriaUsuario>();
        sprintBacklog.add(productBacklog.get(0));
        sprintBacklog.add(productBacklog.get(1));

        for (HistoriaUsuario us : sprintBacklog) {
            valorAcumuladoEntregue += us.getValorNegocio();
            System.out.printf("  -> Entregue [Vertical]: %s (%s) - Valor Gerado: %d pts%n",
                us.getId(), us.getTitulo(), us.getValorNegocio());
        }
        diasTrabalhados += 10;
        System.out.printf("Ao final de %d dias (Sprint 1 Review): Software funcionando entregue em producao!%n", diasTrabalhados);
        System.out.printf("Valor de negocio percebido pelo hospital: %d pontos acumulados.%n%n", valorAcumuladoEntregue);

        System.out.println("Evento de Negocio: No inicio da Sprint 2, a diretoria exige o Protocolo Manchester.");
        System.out.println("Resposta Agil:");
        System.out.println("- O Product Owner simplesmente insere a historia 'Triagem Manchester' no topo do Product Backlog.");
        System.out.println("- Custo burocratico de CCB: R$ 0,00 (Sem aditivos contratuais litigiosos).");
        System.out.println("- Atraso no cronograma: ZERO dias (A historia entra no planejamento normal da proxima Sprint).\n");

        HistoriaUsuario usTriagem = new HistoriaUsuario("US-MANCHESTER", "Classificacao de Risco Manchester na Recepcao", 98, 40, "Triagem");
        productBacklog.add(2, usTriagem); // Inserida com prioridade maxima

        System.out.println("Execucao da Sprint 2 (Semana 3 e 4 - 20 dias acumulados):");
        List<HistoriaUsuario> sprint2Backlog = new ArrayList<HistoriaUsuario>();
        sprint2Backlog.add(usTriagem);
        sprint2Backlog.add(productBacklog.get(3)); // US-03

        for (HistoriaUsuario us : sprint2Backlog) {
            valorAcumuladoEntregue += us.getValorNegocio();
            System.out.printf("  -> Entregue [Vertical]: %s (%s) - Valor Gerado: %d pts%n",
                us.getId(), us.getTitulo(), us.getValorNegocio());
        }
        diasTrabalhados += 10;
        System.out.printf("Ao final de %d dias (Sprint 2 Review): Triagem Manchester operando no hospital!%n", diasTrabalhados);
        System.out.printf("Valor de negocio consolidado entregue: %d pontos acumulados.%n", valorAcumuladoEntregue);
        System.out.println("================================================================================\n");
    }

    /**
     * Exibe o quadro comparativo consolidado dos dois paradigmas.
     */
    public static void exibirQuadroComparativo() {
        System.out.println("QUADRO COMPARATIVO CONSOLIDADO (EXERCICIO 3):");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.printf("%-30s | %-32s | %-32s%n", "Criterio", "Modelo Incremental Clássico", "Abordagem Iterativa Agil (Scrum)");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.printf("%-30s | %-32s | %-32s%n", "Tempo ate 1a entrega funcional", "3 a 6 meses", "1 a 4 semanas (Sprint)");
        System.out.printf("%-30s | %-32s | %-32s%n", "Congelamento de Escopo", "Macro baseline no inicio do projeto", "Micro congelamento apenas na Sprint ativa");
        System.out.printf("%-30s | %-32s | %-32s%n", "Absorcao de Mudancas", "Burocratica (Comite CCB e ECR)", "Continua (Repriorizacao de Backlog)");
        System.out.printf("%-30s | %-32s | %-32s%n", "Tipo de Fatiamento", "Horizontal (por subsistemas/camadas)", "Vertical (fim a fim: UI + Lógica + Banco)");
        System.out.printf("%-30s | %-32s | %-32s%n", "Feedback do Usuario", "Espacado (apenas nos marcos)", "Continuo (Daily, Review e Retrospective)");
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    public static void main(String[] args) {
        System.out.println("UNIVERSIDADE BRASIL - UniFEF / ENGENHARIA DE SOFTWARE I");
        System.out.println("Estudo Pratico: Modelo Incremental versus Ciclos Iterativos Ageis\n");

        simularModeloIncremental();
        simularAbordagemAgilScrum();
        exibirQuadroComparativo();
    }
}
