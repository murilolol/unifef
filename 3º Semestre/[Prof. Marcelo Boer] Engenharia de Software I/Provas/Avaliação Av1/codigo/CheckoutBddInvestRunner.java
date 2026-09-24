/**
 * Disciplina : Engenharia de Software I (3º Semestre) - UniFEF
 * Professor  : Marcelo Boer
 * Tema       : Engenharia de Requisitos Ágil: Histórias de Usuário, Critérios BDD (Gherkin) e Auditoria INVEST
 *
 * Como compilar e executar:
 *   javac CheckoutBddInvestRunner.java
 *   java CheckoutBddInvestRunner
 *
 * CONCEITOS DEMONSTRADOS:
 * 1. História de Usuário Ágil formatada no padrão 'Como / Eu quero / Para que'.
 * 2. Motor de execução de Critérios de Aceitação BDD em sintaxe Gherkin pura (Dado / Quando / Então).
 * 3. Validação determinística de cenários: cupom válido, cupom expirado e cupom inexistente.
 * 4. Relatório executivo de conformidade com a taxonomia INVEST (Independent, Negotiable, Valuable, Estimable, Small, Testable).
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// --- ENTIDADES DO DOMÍNIO DE CHECKOUT ---
class ItemPedido {
    private final String sku;
    private final String descricao;
    private final double precoUnitario;
    private final int quantidade;

    public ItemPedido(String sku, String descricao, double precoUnitario, int quantidade) {
        this.sku = sku;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    public double getSubtotalItem() {
        return precoUnitario * quantidade;
    }

    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
}

class CupomPromocional {
    private final String codigo;
    private final double percentualDesconto; // Ex.: 0.10 para 10%
    private final LocalDate dataValidade;
    private final boolean ativo;

    public CupomPromocional(String codigo, double percentualDesconto, LocalDate dataValidade, boolean ativo) {
        this.codigo = codigo;
        this.percentualDesconto = percentualDesconto;
        this.dataValidade = dataValidade;
        this.ativo = ativo;
    }

    public String getCodigo() { return codigo; }
    public double getPercentualDesconto() { return percentualDesconto; }
    public LocalDate getDataValidade() { return dataValidade; }
    public boolean isAtivo() { return ativo; }
}

class CarrinhoCompras {
    private final List<ItemPedido> itens = new ArrayList<>();
    private CupomPromocional cupomAplicado = null;

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public double calcularSubtotal() {
        double soma = 0.0;
        for (ItemPedido item : itens) {
            soma += item.getSubtotalItem();
        }
        return Math.round(soma * 100.0) / 100.0;
    }

    public double calcularDesconto() {
        if (cupomAplicado == null) return 0.0;
        double subtotal = calcularSubtotal();
        double desconto = subtotal * cupomAplicado.getPercentualDesconto();
        return Math.round(desconto * 100.0) / 100.0;
    }

    public double calcularTotalFinal() {
        double total = calcularSubtotal() - calcularDesconto();
        return Math.round(total * 100.0) / 100.0;
    }

    public void aplicarCupom(CupomPromocional cupom) {
        this.cupomAplicado = cupom;
    }

    public void removerCupom() {
        this.cupomAplicado = null;
    }

    public CupomPromocional getCupomAplicado() {
        return cupomAplicado;
    }
}

// --- MOTOR DE REGRAS DE NEGÓCIO DE CHECKOUT ---
class CheckoutService {
    private final Map<String, CupomPromocional> catalogoCupons = new HashMap<>();

    public void cadastrarCupom(CupomPromocional cupom) {
        catalogoCupons.put(cupom.getCodigo().toUpperCase(), cupom);
    }

    public ResultadoAplicacaoCupom aplicarCupom(CarrinhoCompras carrinho, String codigoCupom, LocalDate dataOperacao) {
        if (codigoCupom == null || codigoCupom.trim().isEmpty()) {
            return new ResultadoAplicacaoCupom(false, "Código de cupom não informado.", 0.0);
        }

        CupomPromocional cupom = catalogoCupons.get(codigoCupom.trim().toUpperCase());
        if (cupom == null) {
            return new ResultadoAplicacaoCupom(false, "Cupom não encontrado no sistema.", 0.0);
        }

        if (!cupom.isAtivo()) {
            return new ResultadoAplicacaoCupom(false, "O cupom informado está inativo.", 0.0);
        }

        // Regra de Negócio: Verificação temporal de validade
        if (dataOperacao.isAfter(cupom.getDataValidade())) {
            return new ResultadoAplicacaoCupom(false, "O cupom informado está expirado e não pode ser utilizado.", 0.0);
        }

        carrinho.aplicarCupom(cupom);
        double descontoConcedido = carrinho.calcularDesconto();
        return new ResultadoAplicacaoCupom(true, "Cupom " + cupom.getCodigo() + " aplicado com sucesso!", descontoConcedido);
    }
}

class ResultadoAplicacaoCupom {
    private final boolean sucesso;
    private final String mensagem;
    private final double desconto;

    public ResultadoAplicacaoCupom(boolean sucesso, String mensagem, double desconto) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.desconto = desconto;
    }

    public boolean isSucesso() { return sucesso; }
    public String getMensagem() { return mensagem; }
    public double getDesconto() { return desconto; }
}

// --- EXECUTOR DE CENÁRIOS BDD (Estilo Gherkin) ---
class BddTestRunner {
    private int cenariosPassados = 0;
    private int cenariosFalhados = 0;

    public void executarCenario(
            String tituloCenario,
            Runnable dado,
            Runnable quando,
            Runnable entao) {
        
        System.out.println("----------------------------------------------------------------");
        System.out.println("Cenário: " + tituloCenario);
        System.out.println("----------------------------------------------------------------");
        
        try {
            dado.run();
            quando.run();
            entao.run();
            System.out.println("\n  [STATUS DO CENÁRIO]: APROVADO (PASSED) ✅\n");
            cenariosPassados++;
        } catch (AssertionError | Exception e) {
            System.out.println("\n  [STATUS DO CENÁRIO]: REPROVADO (FAILED) ❌");
            System.out.println("  Motivo da falha: " + e.getMessage() + "\n");
            cenariosFalhados++;
        }
    }

    public void exibirSumarioFinal() {
        System.out.println("================================================================");
        System.out.println("          SUMÁRIO DA SUÍTE DE TESTES DE ACEITAÇÃO BDD           ");
        System.out.println("================================================================");
        System.out.println("Total de Cenários Executados : " + (cenariosPassados + cenariosFalhados));
        System.out.println("Cenários Aprovados           : " + cenariosPassados);
        System.out.println("Cenários Reprovados          : " + cenariosFalhados);
        System.out.println("Taxa de Sucesso              : " + (cenariosPassados * 100 / (cenariosPassados + cenariosFalhados)) + "%");
        System.out.println("================================================================\n");
    }

    public static void assertThat(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError(mensagemErro);
        }
    }
}

// --- RELATÓRIO PEDAGÓGICO DE AUDITORIA INVEST ---
class InvestAuditor {
    public static void exibirAuditoria() {
        System.out.println("================================================================");
        System.out.println("       AUDITORIA DA HISTÓRIA DE USUÁRIO SOB A ÓTICA INVEST       ");
        System.out.println("================================================================");
        System.out.println("História: Como comprador, quero aplicar cupom, para economizar no checkout.");
        System.out.println("----------------------------------------------------------------");
        System.out.println("I - Independent (Independente) :");
        System.out.println("    * Conforme demonstrado no código, a lógica de cálculo de desconto");
        System.out.println("      opera exclusivamente sobre o subtotal do carrinho, sem depender");
        System.out.println("      de serviços de frete ou de gateways bancários de pagamento.");
        System.out.println("N - Negotiable (Negociável)   :");
        System.out.println("    * O escopo do cupom permite negociar regras como cumulatividade,");
        System.out.println("      limite de teto máximo em reais ou valor mínimo de compra.");
        System.out.println("V - Valuable (Valiosa)        :");
        System.out.println("    * Entrega valor palpável: economia real para o cliente final e");
        System.out.println("      aumento na taxa de conversão de vendas para o lojista.");
        System.out.println("E - Estimable (Estimável)     :");
        System.out.println("    * Complexidade técnica baixa e fórmula matemática bem definida,");
        System.out.println("      permitindo estimativa consensual em Story Points na Planning.");
        System.out.println("S - Small (Pequena)           :");
        System.out.println("    * Escopo atômico e enxuto. Desenvolvível e testável em 2 a 3 dias");
        System.out.println("      por uma dupla de desenvolvedores dentro da Sprint de 2 semanas.");
        System.out.println("T - Testable (Testável)       :");
        System.out.println("    * Totalmente verificável de forma determinística via cenários BDD");
        System.out.println("      com entradas e saídas binárias inequívocas.");
        System.out.println("================================================================\n");
    }
}

// --- CLASSE PRINCIPAL EXECUTÁVEL ---
public class CheckoutBddInvestRunner {

    public static void main(String[] args) {
        System.out.println("################################################################");
        System.out.println("#  TESTES DE ACEITAÇÃO BDD & QUALIDADE INVEST - ENGENHARIA ÁGIL #");
        System.out.println("#  Disciplina: Engenharia de Software I | Prof. Marcelo Boer   #");
        System.out.println("################################################################\n");

        CheckoutService checkoutService = new CheckoutService();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Cadastro de cupons no repositório de teste
        LocalDate dataReferenciaHoje = LocalDate.of(2026, 3, 24);
        checkoutService.cadastrarCupom(new CupomPromocional("VERAO10", 0.10, LocalDate.of(2026, 4, 30), true));
        checkoutService.cadastrarCupom(new CupomPromocional("NATAL2025", 0.20, LocalDate.of(2025, 12, 25), true));

        BddTestRunner runner = new BddTestRunner();

        // =========================================================================
        // CENÁRIO 1: Aplicação bem-sucedida de cupom promocional válido no prazo
        // =========================================================================
        final CarrinhoCompras carrinho1 = new CarrinhoCompras();
        final ResultadoAplicacaoCupom[] resultado1 = new ResultadoAplicacaoCupom[1];

        runner.executarCenario(
            "Aplicação bem-sucedida de cupom promocional válido dentro do prazo",
            // DADO
            () -> {
                System.out.println("  Dado que o comprador possui um carrinho ativo com subtotal de R$ 250,00");
                carrinho1.adicionarItem(new ItemPedido("SKU-01", "Camiseta DryFit", 100.00, 2));
                carrinho1.adicionarItem(new ItemPedido("SKU-02", "Boné Esportivo", 50.00, 1));
                BddTestRunner.assertThat(carrinho1.calcularSubtotal() == 250.00, "Subtotal inicial deve ser R$ 250,00");
                
                System.out.println("  E o comprador possui o cupom válido 'VERAO10' com benefício de 10% de desconto");
            },
            // QUANDO
            () -> {
                System.out.println("  Quando o comprador inserir o código 'VERAO10' e acionar 'Aplicar'");
                resultado1[0] = checkoutService.aplicarCupom(carrinho1, "VERAO10", dataReferenciaHoje);
            },
            // ENTÃO
            () -> {
                System.out.println("  Então o sistema deve recalcular o valor total do pedido para R$ 225,00");
                BddTestRunner.assertThat(carrinho1.calcularTotalFinal() == 225.00, 
                    "Total final esperado: 225.00 | Obtido: " + carrinho1.calcularTotalFinal());

                System.out.println("  E deve exibir a mensagem de confirmação: '" + resultado1[0].getMensagem() + "'");
                BddTestRunner.assertThat(resultado1[0].isSucesso(), "Operação deve ter retornado sucesso");
                BddTestRunner.assertThat(resultado1[0].getMensagem().contains("aplicado com sucesso"), "Mensagem deve indicar sucesso");

                System.out.println("  E deve destacar o desconto concedido de R$ " + String.format("%.2f", resultado1[0].getDesconto()));
                BddTestRunner.assertThat(resultado1[0].getDesconto() == 25.00, "Desconto esperado: 25.00 | Obtido: " + resultado1[0].getDesconto());
            }
        );

        // =========================================================================
        // CENÁRIO 2: Tentativa de aplicação de cupom com data de validade expirada
        // =========================================================================
        final CarrinhoCompras carrinho2 = new CarrinhoCompras();
        final ResultadoAplicacaoCupom[] resultado2 = new ResultadoAplicacaoCupom[1];

        runner.executarCenario(
            "Tentativa de aplicação de cupom com data de validade expirada",
            // DADO
            () -> {
                System.out.println("  Dado que o comprador possui um carrinho ativo com subtotal de R$ 100,00");
                carrinho2.adicionarItem(new ItemPedido("SKU-03", "Mochila Escolar", 100.00, 1));
                BddTestRunner.assertThat(carrinho2.calcularSubtotal() == 100.00, "Subtotal inicial deve ser R$ 100,00");
                
                System.out.println("  E o comprador tenta utilizar o cupom 'NATAL2025' cuja validade era 25/12/2025");
            },
            // QUANDO
            () -> {
                System.out.println("  Quando o comprador submeter o código 'NATAL2025' na data de " + dataReferenciaHoje.format(fmt));
                resultado2[0] = checkoutService.aplicarCupom(carrinho2, "NATAL2025", dataReferenciaHoje);
            },
            // ENTÃO
            () -> {
                System.out.println("  Então o sistema não deve aplicar nenhum desconto sobre o subtotal do carrinho");
                BddTestRunner.assertThat(carrinho2.calcularDesconto() == 0.0, "Desconto deve permanecer 0.0");

                System.out.println("  E deve manter o valor total inalterado em R$ 100,00");
                BddTestRunner.assertThat(carrinho2.calcularTotalFinal() == 100.00, "Total deve permanecer 100.00");

                System.out.println("  E deve exibir a mensagem de erro: '" + resultado2[0].getMensagem() + "'");
                BddTestRunner.assertThat(!resultado2[0].isSucesso(), "Operação deve falhar");
                BddTestRunner.assertThat(resultado2[0].getMensagem().contains("expirado"), "Mensagem deve alertar sobre expiração");
            }
        );

        // =========================================================================
        // CENÁRIO 3: Tentativa de aplicação de cupom inexistente
        // =========================================================================
        final CarrinhoCompras carrinho3 = new CarrinhoCompras();
        final ResultadoAplicacaoCupom[] resultado3 = new ResultadoAplicacaoCupom[1];

        runner.executarCenario(
            "Tentativa de aplicação de código de cupom inexistente no catálogo",
            // DADO
            () -> {
                System.out.println("  Dado que o comprador possui itens no carrinho totalizando R$ 80,00");
                carrinho3.adicionarItem(new ItemPedido("SKU-04", "Livro Técnico", 80.00, 1));
            },
            // QUANDO
            () -> {
                System.out.println("  Quando o comprador submeter o código inexistente 'CUPOM_FANTASMA'");
                resultado3[0] = checkoutService.aplicarCupom(carrinho3, "CUPOM_FANTASMA", dataReferenciaHoje);
            },
            // ENTÃO
            () -> {
                System.out.println("  Então o sistema rejeita a operação com erro de cupom não encontrado");
                BddTestRunner.assertThat(!resultado3[0].isSucesso(), "Operação deve falhar");
                BddTestRunner.assertThat(carrinho3.calcularTotalFinal() == 80.00, "Total deve continuar inalterado");
                System.out.println("  Mensagem retornada: '" + resultado3[0].getMensagem() + "'");
            }
        );

        // Exibição dos relatórios de engenharia
        runner.exibirSumarioFinal();
        InvestAuditor.exibirAuditoria();
    }
}
