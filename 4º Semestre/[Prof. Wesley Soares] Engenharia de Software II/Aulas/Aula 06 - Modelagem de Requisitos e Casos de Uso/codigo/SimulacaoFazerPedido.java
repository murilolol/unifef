/**
 * Disciplina: Engenharia de Software II - UniFEF
 * Professor: Wesley Soares
 * Tema: Modelagem de Requisitos, Casos de Uso e Diagrama de Classes (Food Delivery)
 *
 * Arquivo: SimulacaoFazerPedido.java
 * Descricao: Implementacao executavel do Caso de Uso "Fazer Pedido" (Cliente),
 *            demonstrando a generalizacao da classe abstrata Usuario, composicao
 *            entre Pedido e ItemPedido, associacao com Prato, congelamento de preco
 *            unitario na comanda, validacao de disponibilidade de pratos (Exercicio 3)
 *            e autorizacao transacional com Gateway de Pagamento (RF-02 e RNF-01).
 *
 * Como compilar e executar:
 *   javac SimulacaoFazerPedido.java
 *   java SimulacaoFazerPedido
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimulacaoFazerPedido {

    // =========================================================================
    // ENUMERACOES DO DOMINIO
    // =========================================================================

    public enum MetodoPagamento {
        CARTAO_CREDITO,
        PIX,
        DINHEIRO
    }

    public enum StatusPedido {
        PENDENTE,
        EM_PREPARO,
        A_CAMINHO,
        ENTREGUE,
        CANCELADO
    }

    public enum StatusPagamento {
        PENDENTE,
        APROVADO,
        RECUSADO
    }

    // =========================================================================
    // EXCECOES DE REGRAS DE NEGOCIO
    // =========================================================================

    public static class ItemIndisponivelException extends Exception {
        public ItemIndisponivelException(String mensagem) {
            super(mensagem);
        }
    }

    public static class PagamentoRecusadoException extends Exception {
        public PagamentoRecusadoException(String mensagem) {
            super(mensagem);
        }
    }

    // =========================================================================
    // MODELAGEM ESTRUTURAL DE CLASSES (UML)
    // =========================================================================

    /**
     * Superclasse abstrata que centraliza atributos e comportamentos comuns
     * de autenticacao e identificacao (Generalizacao/Heranca).
     */
    public static abstract class Usuario {
        private final int id;
        private final String nome;
        private final String email;
        private final String senhaHash;
        private final String telefone;

        public Usuario(int id, String nome, String email, String senhaHash, String telefone) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.senhaHash = senhaHash;
            this.telefone = telefone;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getEmail() { return email; }
        public String getTelefone() { return telefone; }

        public boolean autenticar(String emailTentativa, String senhaTentativa) {
            // RNF-01: Criptografia e verificacao de credenciais seguras
            return this.email.equalsIgnoreCase(emailTentativa) && this.senhaHash.equals(senhaTentativa);
        }
    }

    /**
     * Especializacao de Usuario para o ator Cliente.
     */
    public static class Cliente extends Usuario {
        private final String cpf;
        private final String enderecoPadrao;
        private int pontosFidelidade;

        public Cliente(int id, String nome, String email, String senhaHash, String telefone,
                       String cpf, String enderecoPadrao) {
            super(id, nome, email, senhaHash, telefone);
            this.cpf = cpf;
            this.enderecoPadrao = enderecoPadrao;
            this.pontosFidelidade = 0;
        }

        public String getCpf() { return cpf; }
        public String getEnderecoPadrao() { return enderecoPadrao; }
        public int getPontosFidelidade() { return pontosFidelidade; }

        public void adicionarPontos(int pts) {
            if (pts > 0) {
                this.pontosFidelidade += pts;
            }
        }
    }

    /**
     * Especializacao de Usuario para o parceiro comercial Restaurante.
     */
    public static class Restaurante extends Usuario {
        private final String cnpj;
        private final String razaoSocial;
        private boolean aberto;
        private final double taxaComissao;
        private final List<Prato> cardapio = new ArrayList<>();

        public Restaurante(int id, String nome, String email, String senhaHash, String telefone,
                           String cnpj, String razaoSocial, double taxaComissao) {
            super(id, nome, email, senhaHash, telefone);
            this.cnpj = cnpj;
            this.razaoSocial = razaoSocial;
            this.aberto = true;
            this.taxaComissao = taxaComissao;
        }

        public String getCnpj() { return cnpj; }
        public String getRazaoSocial() { return razaoSocial; }
        public boolean isAberto() { return aberto; }
        public double getTaxaComissao() { return taxaComissao; }
        public List<Prato> getCardapio() { return cardapio; }

        public void alternarFuncionamento() {
            this.aberto = !this.aberto;
        }

        // Relacao de Composicao conceitual: o cardapio e gerido pelo Restaurante
        public void adicionarPrato(Prato prato) {
            this.cardapio.add(prato);
        }
    }

    /**
     * Entidade que define o item comercializado no catalogo.
     */
    public static class Prato {
        private final int id;
        private String nome;
        private String descricao;
        private double preco;
        private final String categoria;
        private boolean disponivel; // Atributo solicitado no Exercicio 3

        public Prato(int id, String nome, String descricao, double preco, String categoria, boolean disponivel) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.preco = preco;
            this.categoria = categoria;
            this.disponivel = disponivel;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public double getPreco() { return preco; }
        public String getCategoria() { return categoria; }
        public boolean isDisponivel() { return disponivel; }

        public void alterarPreco(double novoPreco) {
            if (novoPreco <= 0) {
                throw new IllegalArgumentException("Preco deve ser estritamente positivo.");
            }
            this.preco = novoPreco;
        }

        // Metodo solicitado no Exercicio 3 para controle de estoque sem quebra referencial
        public void alternarDisponibilidade() {
            this.disponivel = !this.disponivel;
        }
    }

    /**
     * Linha associativa entre Pedido e Prato.
     * Aplica o principio critico de congelamento de preco unitario no ato da compra.
     */
    public static class ItemPedido {
        private final Prato prato;
        private final int quantidade;
        private final double precoUnitarioCobrado; // Preco congelado
        private final String observacao;

        public ItemPedido(Prato prato, int quantidade, String observacao) throws ItemIndisponivelException {
            // Regra de Negocio do Exercicio 3: Impedir venda de pratos indisponiveis
            if (!prato.isDisponivel()) {
                throw new ItemIndisponivelException("O prato '" + prato.getNome() + "' esta temporariamente esgotado.");
            }
            if (quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade de itens deve ser maior que zero.");
            }
            this.prato = prato;
            this.quantidade = quantidade;
            this.precoUnitarioCobrado = prato.getPreco(); // Congela o valor histórico
            this.observacao = observacao;
        }

        public Prato getPrato() { return prato; }
        public int getQuantidade() { return quantidade; }
        public double getPrecoUnitarioCobrado() { return precoUnitarioCobrado; }
        public String getObservacao() { return observacao; }

        public double calcularSubtotal() {
            return this.precoUnitarioCobrado * this.quantidade;
        }
    }

    /**
     * Entidade central do negocio que agrega os itens da comanda (Composicao estrita).
     */
    public static class Pedido {
        private final int numeroPedido;
        private final Cliente cliente;
        private final Restaurante restaurante;
        private final LocalDateTime dataHoraCriacao;
        private StatusPedido status;
        private final double taxaEntrega;
        private final List<ItemPedido> itens = new ArrayList<>(); // Composicao (1..*)
        private Pagamento pagamento;

        public Pedido(int numeroPedido, Cliente cliente, Restaurante restaurante, double taxaEntrega) {
            this.numeroPedido = numeroPedido;
            this.cliente = cliente;
            this.restaurante = restaurante;
            this.dataHoraCriacao = LocalDateTime.now();
            this.status = StatusPedido.PENDENTE;
            this.taxaEntrega = taxaEntrega;
        }

        public void adicionarItem(Prato prato, int quantidade, String observacao) throws ItemIndisponivelException {
            if (this.status != StatusPedido.PENDENTE) {
                throw new IllegalStateException("Nao e possivel adicionar itens a um pedido ja processado.");
            }
            if (!this.restaurante.isAberto()) {
                throw new IllegalStateException("O restaurante selecionado encontra-se fechado no momento.");
            }
            ItemPedido item = new ItemPedido(prato, quantidade, observacao);
            this.itens.add(item);
        }

        public double calcularSubtotalItens() {
            double subtotal = 0.0;
            for (ItemPedido item : itens) {
                subtotal += item.calcularSubtotal();
            }
            return subtotal;
        }

        public double calcularTotal() {
            return calcularSubtotalItens() + taxaEntrega;
        }

        public void vincularPagamento(Pagamento pagamento) {
            this.pagamento = pagamento;
        }

        public void atualizarStatus(StatusPedido novoStatus) {
            this.status = novoStatus;
        }

        public int getNumeroPedido() { return numeroPedido; }
        public Cliente getCliente() { return cliente; }
        public Restaurante getRestaurante() { return restaurante; }
        public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
        public StatusPedido getStatus() { return status; }
        public double getTaxaEntrega() { return taxaEntrega; }
        public List<ItemPedido> getItens() { return itens; }
        public Pagamento getPagamento() { return pagamento; }
    }

    /**
     * Transacao financeira associada de forma biunivoca (1 para 1) ao Pedido.
     */
    public static class Pagamento {
        private final int id;
        private final MetodoPagamento metodo;
        private final double valor;
        private final LocalDateTime dataHora;
        private StatusPagamento status;
        private String codigoAutorizacao;

        public Pagamento(int id, MetodoPagamento metodo, double valor) {
            this.id = id;
            this.metodo = metodo;
            this.valor = valor;
            this.dataHora = LocalDateTime.now();
            this.status = StatusPagamento.PENDENTE;
        }

        public int getId() { return id; }
        public MetodoPagamento getMetodo() { return metodo; }
        public double getValor() { return valor;	}
        public StatusPagamento getStatus() { return status; }
        public String getCodigoAutorizacao() { return codigoAutorizacao; }

        public void registrarAprovacao(String codigo) {
            this.status = StatusPagamento.APROVADO;
            this.codigoAutorizacao = codigo;
        }

        public void registrarRecusa() {
            this.status = StatusPagamento.RECUSADO;
        }
    }

    /**
     * Simulador do Ator Secundario Gateway de Pagamento.
     */
    public static class GatewayPagamento {
        public static String autorizarCartao(String numeroCartao, double valor, boolean simularSaldoSuficiente)
                throws PagamentoRecusadoException {
            if (!simularSaldoSuficiente) {
                throw new PagamentoRecusadoException("FE-01: Transacao recusada pela operadora do cartao (Saldo/Limite insuficiente).");
            }
            return "AUTH-VISA-" + System.currentTimeMillis();
        }
    }

    // =========================================================================
    // METODO PRINCIPAL: DEMONSTRACAO DOS FLUXOS DE ENGENHARIA
    // =========================================================================

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        System.out.println("====================================================================");
        System.out.println("ENGENHARIA DE SOFTWARE II - DEMONSTRACAO: CASO DE USO FAZER PEDIDO");
        System.out.println("Professor: Wesley Soares | Tema: Modelagem de Classes e Requisitos");
        System.out.println("====================================================================\n");

        // 1. Instanciacao dos Atores e Catalogo
        Cliente cliente = new Cliente(
                101, "Carlos Eduardo", "carlos@email.com", "hash_segura_123",
                "(17) 99888-1122", "123.456.789-00", "Rua das Palmeiras, 450, Centro"
        );

        Restaurante restaurante = new Restaurante(
                501, "Fogao Mineiro Delivery", "contato@fogaomineiro.com", "hash_rest_456",
                "(17) 3421-9900", "12.345.678/0001-90", "Fogao Mineiro Alimentos LTDA", 0.12
        );

        Prato prato1 = new Prato(1, "Feijoada Tradicional", "Acompanha arroz, couve e farofa", 45.00, "Prato Principal", true);
        Prato prato2 = new Prato(2, "Suco de Laranja 500ml", "Suco natural sem adicao de acucar", 9.00, "Bebidas", true);
        Prato pratoEsgotado = new Prato(3, "Costela ao Barbecue", "Costela defumada 600g", 65.00, "Especiais", false); // Indisponivel

        restaurante.adicionarPrato(prato1);
        restaurante.adicionarPrato(prato2);
        restaurante.adicionarPrato(pratoEsgotado);

        // ---------------------------------------------------------------------
        // CENARIO 1: FLUXO PRINCIPAL (Happy Path) COM CONGELAMENTO DE PRECO
        // ---------------------------------------------------------------------
        System.out.println("--- CENARIO 1: FLUXO PRINCIPAL (SUCESSO NA MONTAGEM E PAGAMENTO) ---");
        Pedido pedido1 = new Pedido(1001, cliente, restaurante, 8.00);

        try {
            // Passo 3 do Caso de Uso: Adiciona pratos ao carrinho
            pedido1.adicionarItem(prato1, 2, "Sem cebola na farofa");
            pedido1.adicionarItem(prato2, 1, "Com gelo");
            System.out.println("[OK] Itens adicionados com sucesso ao Pedido #" + pedido1.getNumeroPedido());

            // Demonstracao da Regra de Negocio: Congelamento de Preco Unitario
            System.out.println("Subtotal dos itens calculado: R$ " + String.format("%.2f", pedido1.calcularSubtotalItens()));
            System.out.println("Taxa de frete calculada:      R$ " + String.format("%.2f", pedido1.getTaxaEntrega()));
            System.out.println("Valor total da fatura:        R$ " + String.format("%.2f", pedido1.calcularTotal()));

            // Restaurante altera o preco do cardapio apos a inclusao do item
            System.out.println("\n-> [EVENTO DE NEGOCIO] Restaurante reajusta preco da Feijoada de R$ 45.00 para R$ 52.00 no cardapio...");
            prato1.alterarPreco(52.00);

            System.out.println("Preco atual do prato no cardapio: R$ " + String.format("%.2f", prato1.getPreco()));
            System.out.println("Preco congelado no ItemPedido:     R$ " + String.format("%.2f", pedido1.getItens().get(0).getPrecoUnitarioCobrado()));
            System.out.println("Total do pedido permanece imutavel: R$ " + String.format("%.2f", pedido1.calcularTotal()) +
                    " (Integridade de Faturamento Garantida)");

            // Passo 5 e 6 do Caso de Uso: Processamento do Pagamento (<<include>> Realizar Pagamento)
            Pagamento pagamento1 = new Pagamento(7001, MetodoPagamento.CARTAO_CREDITO, pedido1.calcularTotal());
            String auth = GatewayPagamento.autorizarCartao("4111.xxxx.xxxx.1111", pagamento1.getValor(), true);
            pagamento1.registrarAprovacao(auth);
            pedido1.vincularPagamento(pagamento1);

            // Pós-condicao satisfeita: Pedido Pendente e despachado para a fila da cozinha
            pedido1.atualizarStatus(StatusPedido.PENDENTE);
            cliente.adicionarPontos((int) pedido1.calcularTotal());

            System.out.println("\n[SUCESSO] Pedido aprovado via Gateway com codigo: " + auth);
            System.out.println("Status atual do pedido: " + pedido1.getStatus());
            System.out.println("Pontos de fidelidade acumulados pelo cliente: " + cliente.getPontosFidelidade() + " pts\n");

        } catch (Exception e) {
            System.err.println("[ERRO INESPERADO]: " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENARIO 2: EXCECAO DO EXERCICIO 3 (TENTATIVA DE PEDIR ITEM ESGOTADO)
        // ---------------------------------------------------------------------
        System.out.println("--- CENARIO 2: REGRA DE NEGOCIO - VALIDACAO DE DISPONIBILIDADE (EXERCICIO 3) ---");
        Pedido pedido2 = new Pedido(1002, cliente, restaurante, 8.00);

        try {
            System.out.println("Tentando adicionar item indisponivel: '" + pratoEsgotado.getNome() + "'...");
            pedido2.adicionarItem(pratoEsgotado, 1, "Ao ponto");
            System.out.println("[FALHA DE REGRA] O item foi aceito indevidamente!");
        } catch (ItemIndisponivelException e) {
            System.out.println("[EXCECAO TRATADA COM SUCESSO]: " + e.getMessage());
            System.out.println("A comanda protegeu o cliente de comprar um produto esgotado na cozinha.\n");
        }

        // ---------------------------------------------------------------------
        // CENARIO 3: FLUXO DE EXCECAO FE-01 (RECUSA DE PAGAMENTO NO CHECKOUT)
        // ---------------------------------------------------------------------
        System.out.println("--- CENARIO 3: FLUXO DE EXCECAO FE-01 (CARTAO RECUSADO PELA OPERADORA) ---");
        Pedido pedido3 = new Pedido(1003, cliente, restaurante, 8.00);

        try {
            pedido3.adicionarItem(prato2, 2, "Sem gelo");
            Pagamento pagamentoFalho = new Pagamento(7002, MetodoPagamento.CARTAO_CREDITO, pedido3.calcularTotal());

            System.out.println("Enviando cobranca de R$ " + String.format("%.2f", pedido3.calcularTotal()) + " ao Gateway...");
            // Simulando recusa pelo banco (saldo insuficiente)
            GatewayPagamento.autorizarCartao("4111.xxxx.xxxx.9999", pagamentoFalho.getValor(), false);

        } catch (PagamentoRecusadoException e) {
            System.out.println("[FE-01 ATIVADO]: " + e.getMessage());
            pedido3.atualizarStatus(StatusPedido.CANCELADO);
            System.out.println("Status final do Pedido #1003: " + pedido3.getStatus());
            System.out.println("Aviso exibido na tela do Cliente: 'Por favor, revise os dados ou selecione outro meio de pagamento.'");
            System.out.println("Resultado: O pedido NAO foi despachado para a fila do restaurante.");
        } catch (Exception e) {
            System.err.println("[ERRO]: " + e.getMessage());
        }

        System.out.println("\n====================================================================");
        System.out.println("SIMULACAO CONCLUIDA COM SUCESSO SEGUNDO O MODELO DA AULA 06");
        System.out.println("====================================================================");
    }
}
