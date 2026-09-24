/**
 * Instituicao: Centro Universitario de Santa Fe do Sul (UniFEF)
 * Curso: Bacharelado em Sistemas de Informacao
 * Disciplina: Engenharia de Software II
 * Docente: Prof. Ms. Wesley Soares de Souza
 * Tema: Implementacao Pratica do Dominio de Pedidos Elicitado
 *       (Atendimento a RF-001, RNF-001, Maquina de Estados e Broker de Mensageria)
 * 
 * Como compilar:
 *   javac DominioPedidosElicitacao.java
 * 
 * Como executar:
 *   java DominioPedidosElicitacao
 */

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DominioPedidosElicitacao {

    /**
     * Secao 7.3: Maquina de Estados do Ciclo de Vida do Pedido.
     * Define as transicoes permitidas conforme elicitado nas regras de negocio.
     */
    public enum StatusPedido {
        ABERTO,
        AGUARDANDO_PAGAMENTO,
        PAGO,
        EM_SEPARACAO,
        DESPACHADO,
        ENTREGUE,
        CANCELADO
    }

    /**
     * Excecao formal para violacoes de regras de transicao de estado.
     */
    public static class TransicaoEstadoInvalidaException extends RuntimeException {
        public TransicaoEstadoInvalidaException(String mensagem) {
            super(mensagem);
        }
    }

    /**
     * Secao 7.1: Entidade Cliente.
     */
    public static class Cliente {
        private final int id;
        private final String nome;
        private final String documentoCpfCnpj;
        private final String email;
        private BigDecimal limiteCredito;

        public Cliente(int id, String nome, String documentoCpfCnpj, String email, BigDecimal limiteCredito) {
            this.id = id;
            this.nome = nome;
            this.documentoCpfCnpj = documentoCpfCnpj;
            this.email = email;
            this.limiteCredito = limiteCredito;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getDocumentoCpfCnpj() { return documentoCpfCnpj; }
        public String getEmail() { return email; }
        public BigDecimal getLimiteCredito() { return limiteCredito; }

        public boolean consultarLimiteCredito(BigDecimal valorCompra) {
            return this.limiteCredito.compareTo(valorCompra) >= 0;
        }
    }

    /**
     * Secao 7.1: Entidade Produto com controle de estoque e reserva logica.
     */
    public static class Produto {
        private final int id;
        private final String descricao;
        private final BigDecimal precoTabela;
        private int saldoEstoque;
        private int estoqueReservado;

        public Produto(int id, String descricao, BigDecimal precoTabela, int saldoEstoque) {
            this.id = id;
            this.descricao = descricao;
            this.precoTabela = precoTabela;
            this.saldoEstoque = saldoEstoque;
            this.estoqueReservado = 0;
        }

        public int getId() { return id; }
        public String getDescricao() { return descricao; }
        public BigDecimal getPrecoTabela() { return precoTabela; }
        public synchronized int getSaldoDisponivel() { return saldoEstoque - estoqueReservado; }

        public synchronized boolean reservarEstoque(int qtd) {
            if (getSaldoDisponivel() >= qtd) {
                estoqueReservado += qtd;
                return true;
            }
            return false;
        }

        public synchronized void baixarEstoque(int qtd) {
            if (estoqueReservado >= qtd) {
                estoqueReservado -= qtd;
                saldoEstoque -= qtd;
            } else if (saldoEstoque >= qtd) {
                saldoEstoque -= qtd;
            } else {
                throw new IllegalStateException("Saldo fisico insuficiente para baixa definitiva.");
            }
        }

        public synchronized void estornarReserva(int qtd) {
            estoqueReservado = Math.max(0, estoqueReservado - qtd);
        }
    }

    /**
     * Secao 7.1: Entidade ItemPedido.
     */
    public static class ItemPedido {
        private final Produto produto;
        private final int quantidade;
        private final BigDecimal precoUnitarioCobrado;

        public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitarioCobrado) {
            this.produto = produto;
            this.quantidade = quantidade;
            this.precoUnitarioCobrado = precoUnitarioCobrado;
        }

        public Produto getProduto() { return produto; }
        public int getQuantidade() { return quantidade; }
        public BigDecimal getPrecoUnitarioCobrado() { return precoUnitarioCobrado; }

        public BigDecimal calcularSubtotal() {
            return precoUnitarioCobrado.multiply(BigDecimal.valueOf(quantidade));
        }
    }

    /**
     * Secao 7.1 e 7.3: Agregado Pedido com maquina de estados estrita.
     */
    public static class Pedido {
        private final String id;
        private final Cliente cliente;
        private final List<ItemPedido> itens = new ArrayList<>();
        private final long timestampCriacao;
        private StatusPedido status;
        private BigDecimal valorTotal = BigDecimal.ZERO;

        public Pedido(String id, Cliente cliente) {
            this.id = id;
            this.cliente = cliente;
            this.timestampCriacao = System.currentTimeMillis();
            this.status = StatusPedido.ABERTO;
        }

        public String getId() { return id; }
        public Cliente getCliente() { return cliente; }
        public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }
        public long getTimestampCriacao() { return timestampCriacao; }
        public StatusPedido getStatus() { return status; }
        public BigDecimal getValorTotal() { return valorTotal; }

        public void adicionarItem(Produto produto, int quantidade) {
            if (this.status != StatusPedido.ABERTO) {
                throw new TransicaoEstadoInvalidaException("Itens so podem ser adicionados enquanto o pedido estiver ABERTO.");
            }
            if (!produto.reservarEstoque(quantidade)) {
                throw new IllegalStateException("Estoque insuficiente para reservar o produto: " + produto.getDescricao());
            }
            ItemPedido item = new ItemPedido(produto, quantidade, produto.getPrecoTabela());
            this.itens.add(item);
            this.valorTotal = this.valorTotal.add(item.calcularSubtotal());
        }

        public void finalizarCheckout() {
            if (this.status != StatusPedido.ABERTO) {
                throw new TransicaoEstadoInvalidaException("Checkout so pode ser finalizado a partir do status ABERTO.");
            }
            if (this.itens.isEmpty()) {
                throw new IllegalStateException("Nao e possivel finalizar checkout de pedido sem itens.");
            }
            this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        }

        public void confirmarPagamento() {
            if (this.status != StatusPedido.AGUARDANDO_PAGAMENTO) {
                throw new TransicaoEstadoInvalidaException("Pagamento so pode ser confirmado se o status for AGUARDANDO_PAGAMENTO.");
            }
            this.status = StatusPedido.PAGO;
            for (ItemPedido item : itens) {
                item.getProduto().baixarEstoque(item.getQuantidade());
            }
        }

        public void iniciarSeparacao() {
            if (this.status != StatusPedido.PAGO) {
                throw new TransicaoEstadoInvalidaException("Separacao de carga exige pedido no status PAGO.");
            }
            this.status = StatusPedido.EM_SEPARACAO;
        }

        public void despachar() {
            if (this.status != StatusPedido.EM_SEPARACAO) {
                throw new TransicaoEstadoInvalidaException("Despacho exige pedido no status EM_SEPARACAO.");
            }
            this.status = StatusPedido.DESPACHADO;
        }

        public void entregar() {
            if (this.status != StatusPedido.DESPACHADO) {
                throw new TransicaoEstadoInvalidaException("Entrega exige que o pedido esteja DESPACHADO.");
            }
            this.status = StatusPedido.ENTREGUE;
        }

        public void cancelar(String justificativa) {
            if (this.status == StatusPedido.DESPACHADO || this.status == StatusPedido.ENTREGUE) {
                throw new TransicaoEstadoInvalidaException("Pedidos despachados ou entregues nao podem ser cancelados diretamente.");
            }
            if (this.status == StatusPedido.ABERTO || this.status == StatusPedido.AGUARDANDO_PAGAMENTO) {
                for (ItemPedido item : itens) {
                    item.getProduto().estornarReserva(item.getQuantidade());
                }
            }
            this.status = StatusPedido.CANCELADO;
        }
    }

    /**
     * Secao 6.2: Evento assincrono de dominio para atender ao RF-001.
     */
    public static class PedidoCriadoEvent {
        private final String pedidoId;
        private final String clienteNome;
        private final BigDecimal valorTotal;
        private final long timestampCriacao;

        public PedidoCriadoEvent(String pedidoId, String clienteNome, BigDecimal valorTotal, long timestampCriacao) {
            this.pedidoId = pedidoId;
            this.clienteNome = clienteNome;
            this.valorTotal = valorTotal;
            this.timestampCriacao = timestampCriacao;
        }

        public String getPedidoId() { return pedidoId; }
        public String getClienteNome() { return clienteNome; }
        public BigDecimal getValorTotal() { return valorTotal;	}
        public long getTimestampCriacao() { return timestampCriacao; }
    }

    /**
     * Secao 6.4: Broker de mensageria assincrono que entrega o evento
     * e verifica matematicamente o cumprimento do RNF-001 (latencia <= 5.0 segundos).
     */
    public static class BrokerMensageria {
        private final ExecutorService executor = Executors.newCachedThreadPool();

        public void publicarNotificacaoVendedor(PedidoCriadoEvent evento, TerminalVendedor terminal) {
            executor.submit(() -> {
                try {
                    // Simula pequeno retardo de rede assincrona (120ms)
                    Thread.sleep(120);
                    long instanteRecebimento = System.currentTimeMillis();
                    double latenciaSegundos = (instanteRecebimento - evento.getTimestampCriacao()) / 1000.0;
                    terminal.receberAlerta(evento, latenciaSegundos);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        public void encerrar() {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    /**
     * Representa a interface visual/sonora do vendedor mencionada no RF-001 e RNF-001.
     */
    public static class TerminalVendedor {
        private final String nomeOperador;

        public TerminalVendedor(String nomeOperador) {
            this.nomeOperador = nomeOperador;
        }

        public void receberAlerta(PedidoCriadoEvent evento, double latenciaSegundos) {
            System.out.println("--------------------------------------------------");
            System.out.println("[NOTIFICACAO PUSH] Terminal do Operador: " + nomeOperador);
            System.out.println("Alerta Sonoro: *BEEP* Novo Pedido Recebido!");
            System.out.println("ID do Pedido: " + evento.getPedidoId() + " | Cliente: " + evento.getClienteNome());
            System.out.println("Valor Total: R$ " + evento.getValorTotal());
            System.out.printf("Latencia de Entrega: %.3f segundos\n", latenciaSegundos);

            // Verificacao explicita do RNF-001 (Limite <= 5.0 segundos)
            if (latenciaSegundos <= 5.0) {
                System.out.println("[CONFORMIDADE RNF-001] Sucesso: Entregue dentro do SLA maximo de 5s.");
            } else {
                System.err.println("[VIOLACAO RNF-001] Falha: Tempo limite de 5 segundos excedido!");
            }
            System.out.println("--------------------------------------------------");
        }
    }

    /**
     * Metodo principal demonstrando os fluxos e cenarios de teste modelados.
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("======================================================================");
        System.out.println("UniFEF - Engenharia de Software II | Prof. Ms. Wesley Soares de Souza");
        System.out.println("Execucao Pratica do Modelo de Dominio de Pedidos Elicitado");
        System.out.println("======================================================================\n");

        BrokerMensageria broker = new BrokerMensageria();
        TerminalVendedor terminalBalcao = new TerminalVendedor("Carlos - Balcao Central");

        Cliente clienteLucas = new Cliente(1, "Lucas Silva", "123.456.789-00", "lucas@email.com", new BigDecimal("2500.00"));
        Produto cimento = new Produto(101, "Cimento CP-II 50kg", new BigDecimal("35.00"), 50);
        Produto areia = new Produto(102, "Saco de Areia Lavada 20kg", new BigDecimal("12.50"), 100);

        System.out.println("Passo 1: Criacao de Novo Pedido e Reserva Logica de Estoque...");
        Pedido pedido = new Pedido("PED-2026-001", clienteLucas);
        pedido.adicionarItem(cimento, 10); // 10 * 35.00 = 350.00
        pedido.adicionarItem(areia, 4);     // 4 * 12.50 = 50.00
        pedido.finalizarCheckout();        // Total = 400.00

        System.out.println("Status atual do pedido: " + pedido.getStatus());
        System.out.println("Valor total: R$ " + pedido.getValorTotal());
        System.out.println("Estoque disponivel Cimento: " + cimento.getSaldoDisponivel() + " (Saldo fisico: 50, Reservado: 10)");

        System.out.println("\nPasso 2: Disparo de Evento Assincrono para Atendimento ao RF-001 e RNF-001...");
        PedidoCriadoEvent evento = new PedidoCriadoEvent(
                pedido.getId(),
                pedido.getCliente().getNome(),
                pedido.getValorTotal(),
                pedido.getTimestampCriacao()
        );
        broker.publicarNotificacaoVendedor(evento, terminalBalcao);

        // Aguarda a entrega assincrona no terminal do vendedor
        Thread.sleep(300);

        System.out.println("\nPasso 3: Avanco no Ciclo de Vida da Maquina de Estados (Secao 7.3)...");
        pedido.confirmarPagamento();
        System.out.println("Transitado para: " + pedido.getStatus() + " -> Baixa fisica definitiva efetuada no estoque.");
        pedido.iniciarSeparacao();
        System.out.println("Transitado para: " + pedido.getStatus());
        pedido.despachar();
        System.out.println("Transitado para: " + pedido.getStatus());
        pedido.entregar();
        System.out.println("Transitado para: " + pedido.getStatus() + " -> Ciclo concluido com sucesso.");

        System.out.println("\nPasso 4: Validacao de Regra de Protecao da Maquina de Estados (Fluxo de Excecao)...");
        try {
            System.out.println("Tentando cancelar pedido ja entregue...");
            pedido.cancelar("Cliente desistiu apos o recebimento.");
        } catch (TransicaoEstadoInvalidaException ex) {
            System.out.println("[REGRA DE NEGOCIO RESPEITADA] Excecao esperada capturada: " + ex.getMessage());
        }

        broker.encerrar();
        System.out.println("\nSimulacao concluida com total integridade referencial e funcional.");
    }
}
