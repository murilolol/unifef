/*
 * Disciplina: Engenharia de Software I
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Marcelo Boer
 * Tema: Atividade Avaliativa 01 - Modelagem Estrutural e Composição do Domínio (Açaiteria Sabor da Amazônia)
 *
 * Como compilar:
 *   javac AcaiteriaDominioDemo.java
 * Como executar:
 *   java AcaiteriaDominioDemo
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe executável principal que demonstra a implementação prática do Modelo de Domínio,
 * focando nas entidades identificadas no Item 2 e nos relacionamentos do Item 5 (Diagrama de Classes).
 * Demonstra a relação de COMPOSIÇÃO entre Pedido e ItemPedido, e a importância de congelar
 * o preço histórico na entidade associativa ItemPedido.
 */
public class AcaiteriaDominioDemo {

    /**
     * Enumeração dos estados operacionais do Pedido (conforme Item 2 do estudo de caso).
     */
    public enum StatusPedido {
        AGUARDANDO("Aguardando"),
        EM_PREPARO("Em Preparo"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");

        private final String descricao;

        StatusPedido(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Entidade Cliente (Item 2 - Classes do Projeto).
     * Representa o consumidor final (Item 1 - Usuários do Aplicativo).
     * Atende ao RF01 (Autocadastro) e RF02 (Gestão de Pontos de Fidelidade).
     */
    public static class Cliente {
        private final int idCliente;
        private String nome;
        private String telefone;
        private int pontosFidelidade;

        public Cliente(int idCliente, String nome, String telefone) {
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do cliente é obrigatório.");
            }
            if (telefone == null || telefone.trim().isEmpty()) {
                throw new IllegalArgumentException("Telefone do cliente é obrigatório.");
            }
            this.idCliente = idCliente;
            this.nome = nome;
            this.telefone = telefone;
            this.pontosFidelidade = 0; // Inicia com saldo zero conforme regra de autocadastro
        }

        public int getIdCliente() {
            return idCliente;
        }

        public String getNome() {
            return nome;
        }

        public String getTelefone() {
            return telefone;
        }

        public int getPontosFidelidade() {
            return pontosFidelidade;
        }

        // Regra de Negócio: Adicionar pontos com base no valor da compra faturada
        public void adicionarPontos(int pontos) {
            if (pontos > 0) {
                this.pontosFidelidade += pontos;
            }
        }

        @Override
        public String toString() {
            return String.format("Cliente [ID: %d, Nome: %s, Tel: %s, Pontos: %d]",
                    idCliente, nome, telefone, pontosFidelidade);
        }
    }

    /**
     * Entidade Produto (Item 2 - Classes do Projeto).
     * Catálogo de itens disponíveis: açaí na tigela, smoothies, vitaminas e complementos.
     * Atende ao RF03 (Visualização do Catálogo de Produtos).
     */
    public static class Produto {
        private final int idProduto;
        private String nome;
        private String descricao;
        private double preco;
        private String categoria;

        public Produto(int idProduto, String nome, String descricao, double preco, String categoria) {
            if (preco <= 0.0) {
                throw new IllegalArgumentException("Preço do produto deve ser estritamente positivo.");
            }
            this.idProduto = idProduto;
            this.nome = nome;
            this.descricao = descricao;
            this.preco = preco;
            this.categoria = categoria;
        }

        public int getIdProduto() {
            return idProduto;
        }

        public String getNome() {
            return nome;
        }

        public String getDescricao() {
            return descricao;
        }

        public double getPreco() {
            return preco;
        }

        public void setPreco(double novoPreco) {
            if (novoPreco <= 0.0) {
                throw new IllegalArgumentException("Novo preço deve ser estritamente positivo.");
            }
            this.preco = novoPreco;
        }

        public String getCategoria() {
            return categoria;
        }

        @Override
        public String toString() {
            return String.format("Produto [ID: %d | %-22s | Cat: %-11s | R$ %6.2f]",
                    idProduto, nome, categoria, preco);
        }
    }

    /**
     * Entidade Associativa ItemPedido (Item 2 e Item 5).
     * PONTO-CHAVE PARA A PROVA: Decompõe a relação N:N entre Pedido e Produto.
     * Guarda a quantidade e CONGELA o preço unitário praticado no momento da venda,
     * blindando o pedido contra futuros reajustes no catálogo.
     */
    public static class ItemPedido {
        private final int idItemPedido;
        private final Produto produto;       // Referência ao produto de catálogo
        private final int quantidade;
        private final double precoUnitario;  // Preço congelado no momento da compra
        private final double subtotal;

        public ItemPedido(int idItemPedido, Produto produto, int quantidade) {
            if (produto == null) {
                throw new IllegalArgumentException("Item deve referenciar um produto existente.");
            }
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade do item deve ser no mínimo 1.");
            }
            this.idItemPedido = idItemPedido;
            this.produto = produto;
            this.quantidade = quantidade;
            this.precoUnitario = produto.getPreco(); // Congelamento de preço histórico
            this.subtotal = this.quantidade * this.precoUnitario;
        }

        public int getIdItemPedido() {
            return idItemPedido;
        }

        public Produto getProduto() {
            return produto;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public double getPrecoUnitario() {
            return precoUnitario;
        }

        public double getSubtotal() {
            return subtotal;
        }

        @Override
        public String toString() {
            return String.format("   - [%d] %-22s x %2d un @ R$ %5.2f = R$ %6.2f",
                    idItemPedido, produto.getNome(), quantidade, precoUnitario, subtotal);
        }
    }

    /**
     * Entidade Agregadora Pedido (Item 2 e Item 5).
     * Relação de COMPOSIÇÃO com ItemPedido: se o pedido for destruído, seus itens também são.
     * Atende aos requisitos RF04, RF05, RF06 e RF07.
     */
    public static class Pedido {
        private final int idPedido;
        private final Cliente cliente;
        private final LocalDateTime dataHoraSolicitacao;
        private StatusPedido statusAcompanhamento;
        private final List<ItemPedido> itens;
        private double valorTotal;

        public Pedido(int idPedido, Cliente cliente) {
            if (cliente == null) {
                throw new IllegalArgumentException("Pedido deve obrigatoriamente estar vinculado a um cliente.");
            }
            this.idPedido = idPedido;
            this.cliente = cliente;
            this.dataHoraSolicitacao = LocalDateTime.now();
            this.statusAcompanhamento = StatusPedido.AGUARDANDO; // Status inicial conforme RF06
            this.itens = new ArrayList<>();
            this.valorTotal = 0.0;
        }

        // RF04: Adição de itens e cálculo automático do subtotal
        public void adicionarItem(Produto produto, int quantidade) {
            int proximoIdItem = this.itens.size() + 1;
            ItemPedido item = new ItemPedido(proximoIdItem, produto, quantidade);
            this.itens.add(item);
            calcularTotal(); // RF05: Recálculo automático do valor total
        }

        // RF05: Cálculo Automático do Pedido
        public double calcularTotal() {
            double soma = 0.0;
            for (ItemPedido item : this.itens) {
                soma += item.getSubtotal();
            }
            this.valorTotal = soma;
            return this.valorTotal;
        }

        public void atualizarStatus(StatusPedido novoStatus) {
            this.statusAcompanhamento = novoStatus;
        }

        public int getIdPedido() {
            return idPedido;
        }

        public Cliente getCliente() {
            return cliente;
        }

        public LocalDateTime getDataHoraSolicitacao() {
            return dataHoraSolicitacao;
        }

        public StatusPedido getStatusAcompanhamento() {
            return statusAcompanhamento;
        }

        public List<ItemPedido> getItens() {
            return Collections.unmodifiableList(itens);
        }

        public double getValorTotal() {
            return valorTotal;
        }

        public void exibirComanda() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            System.out.println("============================================================");
            System.out.printf("PEDIDO #%04d | Data: %s%n", idPedido, dataHoraSolicitacao.format(dtf));
            System.out.printf("Cliente: %s (Tel: %s)%n", cliente.getNome(), cliente.getTelefone());
            System.out.printf("Status: [%s]%n", statusAcompanhamento.getDescricao());
            System.out.println("------------------------------------------------------------");
            System.out.println("Itens solicitados:");
            for (ItemPedido item : itens) {
                System.out.println(item);
            }
            System.out.println("------------------------------------------------------------");
            System.out.printf("VALOR TOTAL CALCULADO AUTOMATICAMENTE: R$ %.2f%n", valorTotal);
            System.out.println("============================================================");
        }
    }

    public static void main(String[] args) {
        System.out.println("############################################################");
        System.out.println("#  UNIFEF - ENGENHARIA DE SOFTWARE I - PROF. MARCELO BOER  #");
        System.out.println("#  DEMONSTRAÇÃO PRÁTICA: MODELAGEM DE DOMÍNIO E COMPOSIÇÃO #");
        System.out.println("############################################################\n");

        // 1. Simulação do RF01: Autocadastro de Clientes
        System.out.println("[PASSO 1] Autocadastro de Clientes (RF01 - Usuário Consumidor):");
        Cliente c1 = new Cliente(1, "Matheus Albuquerque", "(17) 99876-5432");
        Cliente c2 = new Cliente(2, "Camila Fernandes", "(17) 98111-2233");
        System.out.println(" -> Cadastrado: " + c1);
        System.out.println(" -> Cadastrado: " + c2);
        System.out.println();

        // 2. Simulação do RF03: Catálogo de Produtos da Açaiteria
        System.out.println("[PASSO 2] Catálogo de Produtos e Complementos Disponíveis (RF03):");
        Produto pAcai500 = new Produto(101, "Açaí na Tigela 500ml", "Açaí puro batido com xarope", 22.00, "Tigela");
        Produto pSmoothie = new Produto(102, "Smoothie de Morango", "Morango fresco batido com iogurte", 16.50, "Bebida");
        Produto pGranola = new Produto(103, "Complemento: Granola", "Porção extra de granola crocante", 3.00, "Complemento");
        Produto pLeiteCond = new Produto(104, "Complemento: Leite Cond.", "Cobertura de leite condensado", 2.50, "Complemento");
        Produto pBanana = new Produto(105, "Complemento: Banana", "Porção de banana fatiada fresca", 2.00, "Complemento");

        System.out.println(" -> " + pAcai500);
        System.out.println(" -> " + pSmoothie);
        System.out.println(" -> " + pGranola);
        System.out.println(" -> " + pLeiteCond);
        System.out.println(" -> " + pBanana);
        System.out.println();

        // 3. Simulação dos RF04, RF05 e RF06: Abertura e Composição do Pedido
        System.out.println("[PASSO 3] Montagem do Pedido com Cálculo Automático (RF04, RF05, RF06):");
        Pedido pedido1 = new Pedido(1001, c1);
        pedido1.adicionarItem(pAcai500, 1);    // 1x Açaí 500ml = 22.00
        pedido1.adicionarItem(pGranola, 2);    // 2x Granola @ 3.00 = 6.00
        pedido1.adicionarItem(pLeiteCond, 1);  // 1x Leite Cond. @ 2.50 = 2.50
        pedido1.adicionarItem(pBanana, 1);     // 1x Banana @ 2.00 = 2.00
        // Total esperado: 22.00 + 6.00 + 2.50 + 2.00 = R$ 32.50

        pedido1.exibirComanda();
        System.out.println();

        // 4. PROVA PRÁTICA DO CONCEITO: Congelamento de Preço em ItemPedido
        System.out.println("[PASSO 4] Teste Conceitual: Resiliência a Reajuste de Preço de Catálogo");
        System.out.println("Cenário: Açaí sofre reajuste de inflação no catálogo de R$ 22.00 para R$ 28.00.");
        pAcai500.setPreco(28.00);
        System.out.println("Novo preço no catálogo: R$ " + pAcai500.getPreco());

        System.out.println("Verificando integridade financeira do Pedido #1001 já efetuado:");
        System.out.printf("Preço unitário no Pedido #1001: R$ %.2f (DEVE permanecer R$ 22.00)%n",
                pedido1.getItens().get(0).getPrecoUnitario());
        System.out.printf("Valor Total do Pedido #1001: R$ %.2f (DEVE permanecer R$ 32.50)%n",
                pedido1.getValorTotal());

        if (Math.abs(pedido1.getValorTotal() - 32.50) < 0.001) {
            System.out.println(" -> SUCESSO: A classe associativa ItemPedido protegeu o histórico financeiro!");
        } else {
            System.err.println(" -> FALHA: O total do pedido antigo foi corrompido!");
        }
    }
}
