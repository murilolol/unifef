/*
 * Disciplina: Engenharia de Software I
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Marcelo Boer
 * Tema: Atividade Avaliativa 01 - Módulo Gerencial, Controle de Vendas e Estoque (Açaiteria Sabor da Amazônia)
 *
 * Como compilar:
 *   javac AcaiteriaGestaoRelatoriosDemo.java
 * Como executar:
 *   java AcaiteriaGestaoRelatoriosDemo
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe executável principal que modela o papel do Ator Gerente (Item 1 e Item 4),
 * demonstrando o controle de vendas diárias e a emissão de relatórios estruturados (RF10, RF11, RF12)
 * para mitigar o gargalo de falta de relatórios e apoiar o controle de estoque de insumos.
 */
public class AcaiteriaGestaoRelatoriosDemo {

    /**
     * Registro de venda individual consolidado para a retaguarda do sistema.
     */
    public static class RegistroVenda {
        private final int idPedido;
        private final LocalDate dataVenda;
        private final String formaPagamento;
        private final double valorTotal;
        private final Map<String, Integer> itensConsumidos;

        public RegistroVenda(int idPedido, LocalDate dataVenda, String formaPagamento, double valorTotal) {
            this.idPedido = idPedido;
            this.dataVenda = dataVenda;
            this.formaPagamento = formaPagamento;
            this.valorTotal = valorTotal;
            this.itensConsumidos = new HashMap<>();
        }

        public void registrarItem(String nomeProduto, int quantidade) {
            this.itensConsumidos.put(nomeProduto,
                    this.itensConsumidos.getOrDefault(nomeProduto, 0) + quantidade);
        }

        public int getIdPedido() { return idPedido; }
        public LocalDate getDataVenda() { return dataVenda; }
        public String getFormaPagamento() { return formaPagamento; }
        public double getValorTotal() { return valorTotal; }
        public Map<String, Integer> getItensConsumidos() { return itensConsumidos; }
    }

    /**
     * Classe de Análise RelatorioVendas (Item 2 - Classes do Projeto).
     * Consolida métricas de faturamento e volumes consumidos de insumos/produtos.
     */
    public static class RelatorioVendas {
        private final int idRelatorio;
        private final LocalDate dataInicio;
        private final LocalDate dataFim;
        private double faturamentoTotal;
        private int totalPedidos;
        private final Map<String, Double> faturamentoPorFormaPagamento;
        private final Map<String, Integer> consumoInsumosEstoque;

        public RelatorioVendas(int idRelatorio, LocalDate dataInicio, LocalDate dataFim) {
            this.idRelatorio = idRelatorio;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
            this.faturamentoTotal = 0.0;
            this.totalPedidos = 0;
            this.faturamentoPorFormaPagamento = new HashMap<>();
            this.consumoInsumosEstoque = new HashMap<>();
        }

        public void processarVenda(RegistroVenda venda) {
            this.totalPedidos++;
            this.faturamentoTotal += venda.getValorTotal();

            // Consolidação por modalidade financeira (Pix vs Cartões)
            this.faturamentoPorFormaPagamento.put(
                    venda.getFormaPagamento(),
                    this.faturamentoPorFormaPagamento.getOrDefault(venda.getFormaPagamento(), 0.0) + venda.getValorTotal()
            );

            // Consolidação para suporte ao controle de estoque (Açaí, frutas, granola, leite condensado)
            for (Map.Entry<String, Integer> item : venda.getItensConsumidos().entrySet()) {
                this.consumoInsumosEstoque.put(
                        item.getKey(),
                        this.consumoInsumosEstoque.getOrDefault(item.getKey(), 0) + item.getValue()
                );
            }
        }

        public void imprimirRelatorioGerencial() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            System.out.println("========================================================================");
            System.out.printf("RELATÓRIO GERENCIAL DE VENDAS #%03d | Período: %s até %s%n",
                    idRelatorio, dataInicio.format(dtf), dataFim.format(dtf));
            System.out.println("========================================================================");
            System.out.printf("Total de Pedidos Faturados : %d%n", totalPedidos);
            System.out.printf("Faturamento Total Bruto    : R$ %,.2f%n", faturamentoTotal);
            double ticketMedio = totalPedidos > 0 ? (faturamentoTotal / totalPedidos) : 0.0;
            System.out.printf("Ticket Médio por Pedido    : R$ %,.2f%n", ticketMedio);
            System.out.println("------------------------------------------------------------------------");
            System.out.println("DISTRIBUIÇÃO POR FORMA DE PAGAMENTO:");
            for (Map.Entry<String, Double> entry : faturamentoPorFormaPagamento.entrySet()) {
                double perc = (entry.getValue() / faturamentoTotal) * 100.0;
                System.out.printf("   * %-20s: R$ %8.2f (%5.1f%%)%n",
                        entry.getKey(), entry.getValue(), perc);
            }
            System.out.println("------------------------------------------------------------------------");
            System.out.println("BALANÇO DE PRODUTOS E COMPLEMENTOS CONSUMIDOS (CONTROLE DE ESTOQUE):");
            for (Map.Entry<String, Integer> entry : consumoInsumosEstoque.entrySet()) {
                System.out.printf("   * %-30s : %3d unidades despachadas%n",
                        entry.getKey(), entry.getValue());
            }
            System.out.println("========================================================================");
        }
    }

    /**
     * Entidade e Ator Gerente (Item 1, Item 2 e Item 4).
     * Responsável pela administração do negócio, auditoria de pedidos e geração de relatórios.
     */
    public static class Gerente {
        private final int idGerente;
        private final String nome;
        private final String login;
        private final String senhaHash;

        public Gerente(int idGerente, String nome, String login, String senhaHash) {
            this.idGerente = idGerente;
            this.nome = nome;
            this.login = login;
            this.senhaHash = senhaHash;
        }

        public boolean autenticar(String loginInformado, String senhaInformada) {
            return this.login.equals(loginInformado) && this.senhaHash.equals(senhaInformada);
        }

        // RF12: Emissão de Relatório Administrativo para o período
        public RelatorioVendas emitirRelatorioPeriodo(int idRelatorio, LocalDate inicio, LocalDate fim, List<RegistroVenda> vendas) {
            RelatorioVendas relatorio = new RelatorioVendas(idRelatorio, inicio, fim);
            for (RegistroVenda venda : vendas) {
                if (!venda.getDataVenda().isBefore(inicio) && !venda.getDataVenda().isAfter(fim)) {
                    relatorio.processarVenda(venda);
                }
            }
            return relatorio;
        }

        public String getNome() { return nome; }
    }

    public static void main(String[] args) {
        System.out.println("############################################################");
        System.out.println("#  UNIFEF - ENGENHARIA DE SOFTWARE I - PROF. MARCELO BOER  #");
        System.out.println("#  MÓDULO GERENCIAL E RELATÓRIO DE VENDAS / ESTOQUE (RF12) #");
        System.out.println("############################################################\n");

        // 1. Instanciação do Ator Gerente (Item 4)
        Gerente gerente = new Gerente(1, "Carlos Eduardo (Gestor)", "carlos.gerente", "segredo123");
        System.out.printf("Autenticando Gerente: %s...%n", gerente.getNome());
        boolean logado = gerente.autenticar("carlos.gerente", "segredo123");
        System.out.println("Status de login: " + (logado ? "ACESSO PERMITIDO" : "ACESSO NEGADO"));
        System.out.println();

        // 2. Base de vendas simulada no expediente da Açaiteria Sabor da Amazônia
        LocalDate hoje = LocalDate.now();
        List<RegistroVenda> vendasExpediente = new ArrayList<>();

        // Venda 1: 1 Açai 500ml, 1 Granola, 1 Leite Condensado (Pix)
        RegistroVenda v1 = new RegistroVenda(101, hoje, "Pix", 27.50);
        v1.registrarItem("Açaí na Tigela 500ml", 1);
        v1.registrarItem("Complemento: Granola", 1);
        v1.registrarItem("Complemento: Leite Condensado", 1);
        vendasExpediente.add(v1);

        // Venda 2: 2 Smoothies de Morango (Cartão de Crédito)
        RegistroVenda v2 = new RegistroVenda(102, hoje, "Cartão de Crédito", 33.00);
        v2.registrarItem("Smoothie de Morango", 2);
        vendasExpediente.add(v2);

        // Venda 3: 2 Açaís 500ml, 2 Bananas, 2 Granolas (Pix)
        RegistroVenda v3 = new RegistroVenda(103, hoje, "Pix", 54.00);
        v3.registrarItem("Açaí na Tigela 500ml", 2);
        v3.registrarItem("Complemento: Banana", 2);
        v3.registrarItem("Complemento: Granola", 2);
        vendasExpediente.add(v3);

        // Venda 4: 1 Vitamina e 1 Leite Condensado (Cartão de Débito)
        RegistroVenda v4 = new RegistroVenda(104, hoje, "Cartão de Débito", 18.50);
        v4.registrarItem("Vitamina Especial", 1);
        v4.registrarItem("Complemento: Leite Condensado", 1);
        vendasExpediente.add(v4);

        // Venda 5: 1 Açaí 500ml puro (Pix)
        RegistroVenda v5 = new RegistroVenda(105, hoje, "Pix", 22.00);
        v5.registrarItem("Açaí na Tigela 500ml", 1);
        vendasExpediente.add(v5);

        // 3. Emissão do Relatório Gerencial pelo Gerente (RF10, RF11, RF12)
        System.out.println("[AÇÃO GERENCIAL] Gerando consolidação diária do caixa e saídas de estoque...");
        RelatorioVendas relatorioHoje = gerente.emitirRelatorioPeriodo(1, hoje, hoje, vendasExpediente);
        System.out.println();

        // 4. Apresentação dos resultados para tomada de decisão
        relatorioHoje.imprimirRelatorioGerencial();

        System.out.println("\n[CONEXÃO COM O ESTUDO DE CASO]:");
        System.out.println(" -> A dor 'gestão não possui relatórios organizados de vendas' foi resolvida pelo cálculo");
        System.out.println("    consolidado de faturamento, ticket médio e segregação de métodos de pagamento.");
        System.out.println(" -> A dor 'dificuldade no controle de estoque' foi resolvida com a contabilização exata");
        System.out.println("    das porções de açaí, smoothies e complementos consumidos no período.");
    }
}
