/*
 * Disciplina: Engenharia de Software I
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Marcelo Boer
 * Tema: Atividade Avaliativa 01 - Atores, Máquina de Estados e Integração Financeira (Açaiteria Sabor da Amazônia)
 *
 * Como compilar:
 *   javac AcaiteriaFluxoPedidoDemo.java
 * Como executar:
 *   java AcaiteriaFluxoPedidoDemo
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Classe executável principal que demonstra a interação dinâmica entre Atores (Item 4),
 * o processamento de pagamentos (RF08, RF09), a máquina de estados do pedido (RF07),
 * a pontuação de fidelidade (RF02) e a aferição de métrica de desempenho (RNF02).
 */
public class AcaiteriaFluxoPedidoDemo {

    public enum FormaPagamento {
        PIX("Pix Instantâneo"),
        CARTAO_CREDITO("Cartão de Crédito"),
        CARTAO_DEBITO("Cartão de Débito");

        private final String descricao;
        FormaPagamento(String descricao) { this.descricao = descricao; }
        public String getDescricao() { return descricao; }
    }

    public enum SituacaoTransacao {
        PENDENTE, APROVADO, RECUSADO
    }

    public enum StatusPedido {
        AGUARDANDO("Aguardando"),
        EM_PREPARO("Em Preparo"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");

        private final String descricao;
        StatusPedido(String descricao) { this.descricao = descricao; }
        public String getDescricao() { return descricao; }
    }

    /**
     * Entidade Pagamento (Item 2 - Classes do Projeto).
     * Registra o método escolhido, o valor e o resultado da transação.
     */
    public static class Pagamento {
        private final int idPagamento;
        private final FormaPagamento formaPagamento;
        private SituacaoTransacao situacaoTransacao;
        private LocalDateTime dataHoraPagamento;
        private final double valorTransacao;

        public Pagamento(int idPagamento, FormaPagamento formaPagamento, double valorTransacao) {
            this.idPagamento = idPagamento;
            this.formaPagamento = formaPagamento;
            this.valorTransacao = valorTransacao;
            this.situacaoTransacao = SituacaoTransacao.PENDENTE;
        }

        public void confirmarAprovacao() {
            this.situacaoTransacao = SituacaoTransacao.APROVADO;
            this.dataHoraPagamento = LocalDateTime.now();
        }

        public void registrarRecusa() {
            this.situacaoTransacao = SituacaoTransacao.RECUSADO;
            this.dataHoraPagamento = LocalDateTime.now();
        }

        public int getIdPagamento() { return idPagamento; }
        public FormaPagamento getFormaPagamento() { return formaPagamento; }
        public SituacaoTransacao getSituacaoTransacao() { return situacaoTransacao; }
        public double getValorTransacao() { return valorTransacao; }
        public LocalDateTime getDataHoraPagamento() { return dataHoraPagamento; }
    }

    /**
     * Ator Secundário da UML: Gateway de Pagamento / Provedor Bancário Externo (Item 4).
     * Não é um usuário humano de tela, mas uma fronteira de software que interage via API.
     */
    public static class GatewayPagamentoExterno {
        private final Random sorteio = new Random();

        public boolean autorizarTransacao(Pagamento pagamento) {
            System.out.printf("   [GATEWAY EXTERNO] Processando cobrança de R$ %.2f via %s...%n",
                    pagamento.getValorTransacao(), pagamento.getFormaPagamento().getDescricao());

            // Simulação de latência de rede externa (20 a 100ms)
            try {
                Thread.sleep(40);
            } catch (InterruptedException ignored) {}

            // Simulação: 90% de taxa de aprovação
            boolean aprovado = sorteio.nextInt(100) < 90;
            if (aprovado) {
                pagamento.confirmarAprovacao();
                System.out.println("   [GATEWAY EXTERNO] Transação APROVADA com sucesso pela operadora bancária.");
            } else {
                pagamento.registrarRecusa();
                System.out.println("   [GATEWAY EXTERNO] Transação RECUSADA por saldo insuficiente ou erro no emissor.");
            }
            return aprovado;
        }
    }

    /**
     * Entidade Pedido simplificada para a demonstração do ciclo de vida e dos atores.
     */
    public static class PedidoCicloDeVida {
        private final int idPedido;
        private final String nomeCliente;
        private int pontosCliente;
        private StatusPedido status;
        private final double valorTotal;
        private Pagamento pagamento;

        public PedidoCicloDeVida(int idPedido, String nomeCliente, double valorTotal) {
            this.idPedido = idPedido;
            this.nomeCliente = nomeCliente;
            this.pontosCliente = 0;
            this.valorTotal = valorTotal;
            this.status = StatusPedido.AGUARDANDO;
        }

        // RF08 e RF09: Selecionar e processar pagamento
        public boolean pagar(FormaPagamento forma, GatewayPagamentoExterno gateway) {
            this.pagamento = new Pagamento(this.idPedido * 10, forma, this.valorTotal);
            boolean sucesso = gateway.autorizarTransacao(this.pagamento);

            if (sucesso) {
                // Regra de Negócio de Fidelidade: 1 ponto a cada R$ 10,00 gastos
                int pontosGanhos = (int) (this.valorTotal / 10.0);
                this.pontosCliente += pontosGanhos;
                System.out.printf("   [FIDELIDADE] Cliente %s recebeu +%d pontos de fidelidade! Saldo atual: %d%n",
                        nomeCliente, pontosGanhos, pontosCliente);
            } else {
                this.status = StatusPedido.CANCELADO;
                System.out.println("   [PEDIDO] Pagamento não efetivado. Pedido cancelado.");
            }
            return sucesso;
        }

        // RF07: Transição de Estados da Máquina de Preparo
        public void avancarParaPreparo() {
            if (this.status == StatusPedido.AGUARDANDO && this.pagamento != null 
                    && this.pagamento.getSituacaoTransacao() == SituacaoTransacao.APROVADO) {
                this.status = StatusPedido.EM_PREPARO;
                System.out.printf("   [COZINHA] Pedido #%d entrou na esteira: Status = %s%n",
                        idPedido, status.getDescricao());
            } else {
                System.out.println("   [ERRO] Não é possível iniciar preparo sem aprovação financeira prévia.");
            }
        }

        public void finalizarPreparo() {
            if (this.status == StatusPedido.EM_PREPARO) {
                this.status = StatusPedido.FINALIZADO;
                System.out.printf("   [COZINHA] Pedido #%d CONCLUÍDO! Status = %s. Notificando cliente no app.%n",
                        idPedido, status.getDescricao());
            }
        }

        public int getIdPedido() { return idPedido; }
        public StatusPedido getStatus() { return status; }
        public Pagamento getPagamento() { return pagamento; }
    }

    public static void main(String[] args) {
        System.out.println("############################################################");
        System.out.println("#  UNIFEF - ENGENHARIA DE SOFTWARE I - PROF. MARCELO BOER  #");
        System.out.println("#  CICLO DE VIDA, MÁQUINA DE ESTADOS E ATORES DA AÇAITERIA #");
        System.out.println("############################################################\n");

        GatewayPagamentoExterno gatewayBancario = new GatewayPagamentoExterno();

        // =========================================================================
        // CENÁRIO 1: Fluxo Feliz (Pedido Aprovado via PIX, Preparo e Notificação)
        // =========================================================================
        System.out.println("--- CENÁRIO 1: Fluxo Principal com Aprovação de Pagamento Pix ---");
        long inicioCronometro = System.currentTimeMillis();

        System.out.println("1. [Ator: Cliente] Conclui o carrinho e solicita fechamento de Pedido #2001 (R$ 48,00).");
        PedidoCicloDeVida pedido1 = new PedidoCicloDeVida(2001, "Renata Guimarães", 48.00);

        System.out.println("2. [Ator: Cliente] Escolhe pagamento via Pix (RF08).");
        boolean pago = pedido1.pagar(FormaPagamento.PIX, gatewayBancario);

        if (pago) {
            System.out.println("3. [Ator: Atendente/Cozinha] Recebe comanda no tablet e muda status:");
            pedido1.avancarParaPreparo();

            System.out.println("4. [Ator: Atendente/Cozinha] Conclui montagem do açaí com complementos:");
            pedido1.finalizarPreparo();
        }

        long fimCronometro = System.currentTimeMillis();
        long tempoTotalMs = fimCronometro - inicioCronometro;
        System.out.printf("\n[Métrica RNF02] Tempo total de processamento: %d ms (Critério: <= 1500 ms) -> %s%n\n",
                tempoTotalMs, (tempoTotalMs <= 1500 ? "CONFORME [OK]" : "NÃO CONFORME [VIOLADO]"));

        // =========================================================================
        // CENÁRIO 2: Fluxo Alternativo com Recusa de Pagamento
        // =========================================================================
        System.out.println("--- CENÁRIO 2: Fluxo de Exceção com Pagamento Recusado no Cartão ---");
        PedidoCicloDeVida pedido2 = new PedidoCicloDeVida(2002, "Lucas Mendonça", 85.00);

        // Criamos um gateway forçado a recusar para teste do tratamento de erro
        GatewayPagamentoExterno gatewayComFalha = new GatewayPagamentoExterno() {
            @Override
            public boolean autorizarTransacao(Pagamento p) {
                p.registrarRecusa();
                System.out.println("   [GATEWAY EXTERNO] Transação RECUSADA (Cartão bloqueado pelo banco).");
                return false;
            }
        };

        System.out.println("1. [Ator: Cliente] Solicita pagamento via Cartão de Crédito de R$ 85,00.");
        boolean resultadoRecusa = pedido2.pagar(FormaPagamento.CARTAO_CREDITO, gatewayComFalha);

        System.out.printf("2. Situação do Pedido após recusa: %s (Nenhum ponto creditado)%n",
                pedido2.getStatus().getDescricao());

        System.out.println("3. Tentativa de avançar para a cozinha com pedido não pago:");
        pedido2.avancarParaPreparo();
        System.out.println(" -> Blindagem de integridade garantida!");
    }
}
