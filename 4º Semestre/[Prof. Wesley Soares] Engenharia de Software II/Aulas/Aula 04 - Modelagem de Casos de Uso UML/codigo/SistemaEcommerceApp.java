/**
 * Disciplina: Engenharia de Software II
 * Tema: Modelagem de Casos de Uso UML - Estudo de Caso E-commerce
 * 
 * Como compilar e executar:
 *   javac SistemaEcommerceApp.java
 *   java SistemaEcommerceApp
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ============================================================================
// ELEMENTO UML: ATORES DO SISTEMA
// ============================================================================

/**
 * Ator Primário: Cliente.
 * Representa o papel do usuário comum que navega no catálogo e realiza compras.
 */
class Cliente {
    private final String id;
    private final String nome;
    private final String cep;

    public Cliente(String id, String nome, String cep) {
        this.id = id;
        this.nome = nome;
        this.cep = cep;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCep() { return cep; }
}

/**
 * Generalização de Atores na UML: Cliente Especial herda de Cliente.
 * O ator especializado herda todas as associações do ator pai (como Realizar Pedido)
 * e ganha acesso a casos de uso exclusivos (como Desconto de Fidelidade VIP).
 */
class ClienteEspecial extends Cliente {
    private final double taxaDescontoVip;

    public ClienteEspecial(String id, String nome, String cep, double taxaDescontoVip) {
        super(id, nome, cep);
        this.taxaDescontoVip = taxaDescontoVip;
    }

    public double getTaxaDescontoVip() {
        return taxaDescontoVip;
    }
}

/**
 * Ator Secundário (Externo): Transportadora.
 * Provê o serviço autônomo de cálculo de postagem e despacho de encomendas.
 */
class Transportadora {
    public double calcularFrete(String cepOrigem, String cepDestino, double pesoKg) {
        System.out.println("   [Ator Secundário: Transportadora] Cotando frete entre CEPs: " + cepOrigem + " -> " + cepDestino);
        if (cepDestino.startsWith("0")) {
            return 15.00 + (pesoKg * 2.50); // Região metropolitana
        }
        return 32.50 + (pesoKg * 4.00);     // Demais regiões
    }
}

/**
 * Ator Secundário (Externo): Gateway de Pagamento.
 * Intermediador financeiro responsável pela autorização transacional.
 */
class GatewayPagamento {
    public boolean processarTransacao(String cartao, double valorTotal) {
        System.out.println("   [Ator Secundário: Gateway de Pagamento] Autorizando cobrança de R$ " 
                + String.format("%.2f", valorTotal) + " no cartão final " + cartao.substring(Math.max(0, cartao.length() - 4)));
        return !cartao.startsWith("0000"); // Cartão iniciado com 0000 simula recusa/fraude
    }
}

// ============================================================================
// ENTIDADES DE DOMÍNIO (SUPORTE AOS CASOS DE USO)
// ============================================================================

class Produto {
    private final String id;
    private final String nome;
    private final double preco;
    private int estoque;

    public Produto(String id, String nome, double preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }

    public void baixarEstoque(int quantidade) {
        this.estoque -= quantidade;
    }

    public void reporEstoque(int quantidade) {
        this.estoque += quantidade;
    }
}

class ItemCarrinho {
    private final Produto produto;
    private final int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getSubtotal() { return produto.getPreco() * quantidade; }
}

enum StatusPedido {
    CRIADO, CONFERIDO, PAGO, CANCELADO
}

class Pedido {
    private final String numero;
    private final Cliente cliente;
    private final List<ItemCarrinho> itens;
    private double valorFrete;
    private double valorDesconto;
    private StatusPedido status;

    public Pedido(String numero, Cliente cliente, List<ItemCarrinho> itens) {
        this.numero = numero;
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.status = StatusPedido.CRIADO;
    }

    public String getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public List<ItemCarrinho> getItens() { return Collections.unmodifiableList(itens); }
    public double getValorFrete() { return valorFrete; }
    public void setValorFrete(double valorFrete) { this.valorFrete = valorFrete; }
    public double getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(double valorDesconto) { this.valorDesconto = valorDesconto; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public double calcularTotalItens() {
        return itens.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    public double calcularValorFinal() {
        return Math.max(0, calcularTotalItens() - valorDesconto + valorFrete);
    }
}

// ============================================================================
// IMPLEMENTAÇÃO DOS CASOS DE USO (FRONTEIRA DA APLICAÇÃO)
// ============================================================================

/**
 * Caso de Uso Incluído: Conferir Itens (<<include>>).
 * Não é chamado diretamente pelo cliente no vácuo; é acoplado de forma mandatória
 * ao fluxo de Realizar Pedido para validar saldo de estoque antes da cobrança.
 */
class ConferirItensUseCase {
    public void executar(Pedido pedido) {
        System.out.println(" -> Executando Caso de Uso Incluído: <<include>> Conferir Itens");
        for (ItemCarrinho item : pedido.getItens()) {
            if (item.getQuantidade() > item.getProduto().getEstoque()) {
                throw new IllegalStateException("Fluxo de Exceção FE01: Estoque insuficiente para o produto " 
                        + item.getProduto().getNome() + ". Solicitado: " + item.getQuantidade() 
                        + ", Disponível: " + item.getProduto().getEstoque());
            }
            System.out.println("    Item validado: " + item.getProduto().getNome() + " x" + item.getQuantidade());
        }
        pedido.setStatus(StatusPedido.CONFERIDO);
    }
}

/**
 * Caso de Uso Incluído: Calcular Postagem (<<include>>).
 * Integra obrigatoriamente a base Realizar Pedido, delegando a cotação ao Ator Secundário.
 */
class CalcularPostagemUseCase {
    private final Transportadora transportadora;

    public CalcularPostagemUseCase(Transportadora transportadora) {
        this.transportadora = transportadora;
    }

    public double executar(Pedido pedido) {
        System.out.println(" -> Executando Caso de Uso Incluído: <<include>> Calcular Postagem");
        double pesoEstimado = pedido.getItens().size() * 0.8; // 800g por item
        double frete = transportadora.calcularFrete("14000-000", pedido.getCliente().getCep(), pesoEstimado);
        pedido.setValorFrete(frete);
        System.out.println("    Frete calculado: R$ " + String.format("%.2f", frete));
        return frete;
    }
}

/**
 * Caso de Uso de Extensão: Finalizar Compra (<<extend>>).
 * Conecta-se ao caso de uso base em um ponto de extensão após a conferência e cálculo do frete.
 * Se o cliente optar por pagar, o caso de uso de extensão é acionado.
 */
class FinalizarCompraUseCase {
    private final GatewayPagamento gateway;

    public FinalizarCompraUseCase(GatewayPagamento gateway) {
        this.gateway = gateway;
    }

    public boolean executar(Pedido pedido, String dadosCartao) {
        System.out.println(" -> Executando Caso de Uso de Extensão: <<extend>> Finalizar Compra");
        double valorTotal = pedido.calcularValorFinal();
        boolean sucesso = gateway.processarTransacao(dadosCartao, valorTotal);
        
        if (!sucesso) {
            System.out.println("    Fluxo de Exceção FE02: Pagamento recusado pela operadora financeira.");
            return false;
        }

        // Pós-condição: Baixa definitiva de estoque e transição de estado
        for (ItemCarrinho item : pedido.getItens()) {
            item.getProduto().baixarEstoque(item.getQuantidade());
        }
        pedido.setStatus(StatusPedido.PAGO);
        System.out.println("    Pós-condição satisfeita: Pedido " + pedido.getNumero() + " PAGO. Estoques atualizados.");
        return true;
    }
}

/**
 * Caso de Uso de Extensão: Cancelar Compra (<<extend>>).
 * Fluxo alternativo/opcional disparado se o cliente desistir da compra no ponto de confirmação.
 */
class CancelarCompraUseCase {
    public void executar(Pedido pedido, String motivo) {
        System.out.println(" -> Executando Caso de Uso de Extensão: <<extend>> Cancelar Compra");
        pedido.setStatus(StatusPedido.CANCELADO);
        System.out.println("    Fluxo Alternativo FA02: Pedido cancelado pelo cliente. Motivo: " + motivo);
    }
}

/**
 * Caso de Uso Exclusivo: Acessar Desconto de Fidelidade.
 * Acessível somente por instâncias do ator especializado ClienteEspecial.
 */
class AcessarDescontoFidelidadeUseCase {
    public void executar(Pedido pedido, ClienteEspecial clienteVip) {
        System.out.println(" -> Executando Caso de Uso Exclusivo VIP: Acessar Desconto de Fidelidade");
        double desconto = pedido.calcularTotalItens() * clienteVip.getTaxaDescontoVip();
        pedido.setValorDesconto(desconto);
        System.out.println("    Desconto de fidelidade aplicado: R$ " + String.format("%.2f", desconto));
    }
}

/**
 * Caso de Uso Base: Realizar Pedido.
 * Ponto focal do modelo de casos de uso do e-commerce.
 */
class RealizarPedidoUseCase {
    private final ConferirItensUseCase conferirItensUC;
    private final CalcularPostagemUseCase calcularPostagemUC;
    private final FinalizarCompraUseCase finalizarCompraUC;
    private final CancelarCompraUseCase cancelarCompraUC;
    private final AcessarDescontoFidelidadeUseCase descontoVipUC;

    public RealizarPedidoUseCase(
            ConferirItensUseCase conferirItensUC,
            CalcularPostagemUseCase calcularPostagemUC,
            FinalizarCompraUseCase finalizarCompraUC,
            CancelarCompraUseCase cancelarCompraUC,
            AcessarDescontoFidelidadeUseCase descontoVipUC) {
        this.conferirItensUC = conferirItensUC;
        this.calcularPostagemUC = calcularPostagemUC;
        this.finalizarCompraUC = finalizarCompraUC;
        this.cancelarCompraUC = cancelarCompraUC;
        this.descontoVipUC = descontoVipUC;
    }

    public void executar(Pedido pedido, boolean desejaConcluir, String dadosPagamento) {
        System.out.println("\n======================================================================");
        System.out.println("Iniciando Caso de Uso Base: Realizar Pedido (Ator: " + pedido.getCliente().getNome() + ")");
        System.out.println("======================================================================");

        // 1. Invocação incondicional de <<include>> Conferir Itens
        conferirItensUC.executar(pedido);

        // 2. Invocação incondicional de <<include>> Calcular Postagem
        calcularPostagemUC.executar(pedido);

        // 3. Verificação de ator especializado (Herança/Generalização de Ator)
        if (pedido.getCliente() instanceof ClienteEspecial) {
            ClienteEspecial vip = (ClienteEspecial) pedido.getCliente();
            descontoVipUC.executar(pedido, vip);
        }

        System.out.println("   Resumo do Pedido:");
        System.out.println("   Subtotal itens: R$ " + String.format("%.2f", pedido.calcularTotalItens()));
        System.out.println("   Descontos:      R$ " + String.format("%.2f", pedido.getValorDesconto()));
        System.out.println("   Frete:          R$ " + String.format("%.2f", pedido.getValorFrete()));
        System.out.println("   Total Final:    R$ " + String.format("%.2f", pedido.calcularValorFinal()));

        // Ponto de Extensão (Extension Point): Decisão do cliente no checkout
        if (desejaConcluir) {
            // Disparo condicional do <<extend>> Finalizar Compra
            boolean pago = finalizarCompraUC.executar(pedido, dadosPagamento);
            if (!pago) {
                System.out.println("   Aviso: O pedido " + pedido.getNumero() + " aguarda novo pagamento.");
            }
        } else {
            // Disparo condicional do <<extend>> Cancelar Compra
            cancelarCompraUC.executar(pedido, "Cliente desistiu na tela de checkout.");
        }
    }
}

// ============================================================================
// CLASSE PRINCIPAL EXECUTÁVEL (DEMONSTRAÇÃO DOS FLUXOS)
// ============================================================================
public class SistemaEcommerceApp {
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   ENGENHARIA DE SOFTWARE II - DEMONSTRAÇÃO PRÁTICA DE CASOS DE USO");
        System.out.println("   Tema: E-commerce (Include, Extend, Generalização de Atores)");
        System.out.println("====================================================================");

        // Instanciação da Infraestrutura / Atores Secundários
        Transportadora transportadora = new Transportadora();
        GatewayPagamento gateway = new GatewayPagamento();

        // Instanciação dos Casos de Uso
        ConferirItensUseCase conferirUC = new ConferirItensUseCase();
        CalcularPostagemUseCase postagemUC = new CalcularPostagemUseCase(transportadora);
        FinalizarCompraUseCase finalizarUC = new FinalizarCompraUseCase(gateway);
        CancelarCompraUseCase cancelarUC = new CancelarCompraUseCase();
        AcessarDescontoFidelidadeUseCase descontoVipUC = new AcessarDescontoFidelidadeUseCase();

        RealizarPedidoUseCase realizarPedidoUC = new RealizarPedidoUseCase(
                conferirUC, postagemUC, finalizarUC, cancelarUC, descontoVipUC
        );

        // Catálogo de Produtos da Loja
        Produto p1 = new Produto("PRD-01", "Livro Clean Architecture", 89.90, 10);
        Produto p2 = new Produto("PRD-02", "Monitor Ultrawide 29\"", 1250.00, 3);

        // --------------------------------------------------------------------
        // CENÁRIO 1: Fluxo Principal (Caminho Feliz) com Ator Cliente Comum
        // --------------------------------------------------------------------
        Cliente clienteComum = new Cliente("CLI-101", "Mariana Costa", "01310-100");
        List<ItemCarrinho> itens1 = new ArrayList<>();
        itens1.add(new ItemCarrinho(p1, 1));
        Pedido pedido1 = new Pedido("PED-2026-001", clienteComum, itens1);

        realizarPedidoUC.executar(pedido1, true, "5555-4444-3333-2222");

        // --------------------------------------------------------------------
        // CENÁRIO 2: Generalização de Ator - Cliente Especial (VIP)
        // O cliente especial usufrui do fluxo comum e estende com desconto de fidelidade
        // --------------------------------------------------------------------
        ClienteEspecial clienteVip = new ClienteEspecial("VIP-999", "Dr. Roberto Rocha", "14010-000", 0.15); // 15% desc
        List<ItemCarrinho> itens2 = new ArrayList<>();
        itens2.add(new ItemCarrinho(p2, 1));
        Pedido pedido2 = new Pedido("PED-2026-002", clienteVip, itens2);

        realizarPedidoUC.executar(pedido2, true, "4111-2222-3333-4444");

        // --------------------------------------------------------------------
        // CENÁRIO 3: Ponto de Extensão alternativo - Disparo de <<extend>> Cancelar Compra
        // --------------------------------------------------------------------
        List<ItemCarrinho> itens3 = new ArrayList<>();
        itens3.add(new ItemCarrinho(p1, 2));
        Pedido pedido3 = new Pedido("PED-2026-003", clienteComum, itens3);

        realizarPedidoUC.executar(pedido3, false, null); // Cliente desiste no checkout

        // --------------------------------------------------------------------
        // CENÁRIO 4: Fluxo de Exceção FE01 - Saldo de Estoque Insuficiente no <<include>>
        // --------------------------------------------------------------------
        System.out.println("\n======================================================================");
        System.out.println("Tentativa de compra que aciona Fluxo de Exceção no <<include>>");
        System.out.println("======================================================================");
        List<ItemCarrinho> itens4 = new ArrayList<>();
        itens4.add(new ItemCarrinho(p2, 5)); // Restam apenas 2 monitores em estoque
        Pedido pedido4 = new Pedido("PED-2026-004", clienteComum, itens4);

        try {
            realizarPedidoUC.executar(pedido4, true, "1234-5678-9012-3456");
        } catch (IllegalStateException e) {
            System.err.println("Bloqueio de Regra de Negócio: " + e.getMessage());
        }
    }
}
