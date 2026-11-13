/*
 * Disciplina: Engenharia de Software II (4º Semestre)
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Prof. Ms. Wesley Soares de Souza
 * Tema: Implementação Executável de Diagramas de Máquina de Estados (State Pattern)
 *
 * Como executar:
 *   javac CicloVidaEstados.java
 *   java CicloVidaEstados
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CicloVidaEstados {

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("ENGENHARIA DE SOFTWARE II - PROF. WESLEY SOARES");
        System.out.println("SIMULAÇÃO EXECUTÁVEL DE DIAGRAMAS DE MÁQUINA DE ESTADOS");
        System.out.println("===========================================================\n");

        // --------------------------------------------------------------------
        // Exercício 4 (Parte A): Máquina de Estados da Entidade Pedido
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 1] Ciclo de Vida do Pedido: Fluxo Normal ---");
        Pedido pedido1 = new Pedido("PED-2026-001", 189.90);
        System.out.println("Estado inicial: " + pedido1.getNomeEstado());

        pedido1.confirmarPagamento();
        pedido1.iniciarPreparo();
        pedido1.despacharEntrega();
        pedido1.confirmarEntrega();

        System.out.println("\n--- [TESTE 2] Ciclo de Vida do Pedido: Tentativa de Transição Inválida ---");
        Pedido pedido2 = new Pedido("PED-2026-002", 95.00);
        try {
            System.out.println("Tentando despachar pedido recém-criado sem confirmação de pagamento...");
            pedido2.despacharEntrega();
        } catch (IllegalStateException e) {
            System.out.println("[REGRA DE TRANSIÇÃO BARRADA]: " + e.getMessage());
        }

        System.out.println("\n--- [TESTE 3] Ciclo de Vida do Pedido: Cancelamento Permitido ---");
        pedido2.cancelar("Cliente solicitou exclusão antes da produção");

        // --------------------------------------------------------------------
        // Exercício 4 (Parte B): Máquina de Estados da Entidade Pagamento
        // --------------------------------------------------------------------
        System.out.println("\n--- [TESTE 4] Ciclo de Vida do Pagamento: Aprovação e Liquidação ---");
        PagamentoTransacao pag1 = new PagamentoTransacao("TX-9901", 189.90);
        System.out.println("Estado do pagamento: " + pag1.getNomeEstado());
        pag1.processarAutorizacao(true);
        pag1.liquidar();

        System.out.println("\n--- [TESTE 5] Ciclo de Vida do Pagamento: Recusa e Reversão ---");
        PagamentoTransacao pag2 = new PagamentoTransacao("TX-9902", 450.00);
        pag2.processarAutorizacao(false);
        try {
            pag2.liquidar();
        } catch (IllegalStateException e) {
            System.out.println("[REGRA DE TRANSIÇÃO BARRADA]: " + e.getMessage());
        }

        System.out.println("\n===========================================================");
        System.out.println("EXECUÇÃO DOS ESTADOS CONCLUÍDA COM SUCESSO CONTRATUAL!");
        System.out.println("===========================================================");
    }
}

// ============================================================================
// MODELO DE ESTADOS: PEDIDO (State Pattern)
// ============================================================================

interface EstadoPedido {
    void confirmarPagamento(Pedido pedido);
    void iniciarPreparo(Pedido pedido);
    void despacharEntrega(Pedido pedido);
    void confirmarEntrega(Pedido pedido);
    void cancelar(Pedido pedido, String motivo);
    String getNome();
}

class Pedido {
    private final String codigo;
    private final double valorTotal;
    private EstadoPedido estadoAtual;
    private final List<String> historicoEventos = new ArrayList<>();

    public Pedido(String codigo, double valorTotal) {
        this.codigo = codigo;
        this.valorTotal = valorTotal;
        this.estadoAtual = new EstadoCriado();
        registrarLog("Instanciação do Pedido no estado inicial: CRIADO");
    }

    public void setEstado(EstadoPedido novoEstado) {
        registrarLog("Transição de [" + this.estadoAtual.getNome() + "] para [" + novoEstado.getNome() + "]");
        this.estadoAtual = novoEstado;
    }

    public void confirmarPagamento() { estadoAtual.confirmarPagamento(this); }
    public void iniciarPreparo() { estadoAtual.iniciarPreparo(this); }
    public void despacharEntrega() { estadoAtual.despacharEntrega(this); }
    public void confirmarEntrega() { estadoAtual.confirmarEntrega(this); }
    public void cancelar(String motivo) { estadoAtual.cancelar(this, motivo); }

    public String getNomeEstado() { return estadoAtual.getNome(); }
    public String getCodigo() { return codigo; }
    public double getValorTotal() { return valorTotal; }

    public void registrarLog(String mensagem) {
        String entrada = "[" + LocalDateTime.now() + "] [" + codigo + "] " + mensagem;
        historicoEventos.add(entrada);
        System.out.println("  -> " + entrada);
    }
}

class EstadoCriado implements EstadoPedido {
    public void confirmarPagamento(Pedido pedido) {
        pedido.setEstado(new EstadoPago());
    }
    public void iniciarPreparo(Pedido pedido) {
        throw new IllegalStateException("Não é possível iniciar preparo de pedido não pago.");
    }
    public void despacharEntrega(Pedido pedido) {
        throw new IllegalStateException("Não é possível despachar pedido ainda não pago e não preparado.");
    }
    public void confirmarEntrega(Pedido pedido) {
        throw new IllegalStateException("Não é possível entregar pedido em estado inicial.");
    }
    public void cancelar(Pedido pedido, String motivo) {
        pedido.registrarLog("Pedido cancelado enquanto estava CRIADO. Motivo: " + motivo);
        pedido.setEstado(new EstadoCancelado());
    }
    public String getNome() { return "CRIADO"; }
}

class EstadoPago implements EstadoPedido {
    public void confirmarPagamento(Pedido pedido) {
        throw new IllegalStateException("O pagamento deste pedido já foi confirmado previamente.");
    }
    public void iniciarPreparo(Pedido pedido) {
        pedido.setEstado(new EstadoEmPreparo());
    }
    public void despacharEntrega(Pedido pedido) {
        throw new IllegalStateException("O pedido precisa passar pela cozinha (preparo) antes do despacho.");
    }
    public void confirmarEntrega(Pedido pedido) {
        throw new IllegalStateException("Pedido ainda não despachado.");
    }
    public void cancelar(Pedido pedido, String motivo) {
        pedido.registrarLog("Cancelamento após pagamento. Disparando estorno financeiro: " + motivo);
        pedido.setEstado(new EstadoCancelado());
    }
    public String getNome() { return "PAGO"; }
}

class EstadoEmPreparo implements EstadoPedido {
    public void confirmarPagamento(Pedido pedido) {
        throw new IllegalStateException("Pagamento já confirmado.");
    }
    public void iniciarPreparo(Pedido pedido) {
        throw new IllegalStateException("O pedido já está na esteira de produção/cozinha.");
    }
    public void despacharEntrega(Pedido pedido) {
        pedido.setEstado(new EstadoEmTransito());
    }
    public void confirmarEntrega(Pedido pedido) {
        throw new IllegalStateException("O pedido precisa estar em trânsito antes de ser entregue.");
    }
    public void cancelar(Pedido pedido, String motivo) {
        pedido.registrarLog("Cancelamento durante o preparo com perda de insumos. Motivo: " + motivo);
        pedido.setEstado(new EstadoCancelado());
    }
    public String getNome() { return "EM_PREPARO"; }
}

class EstadoEmTransito implements EstadoPedido {
    public void confirmarPagamento(Pedido pedido) { throw new IllegalStateException("Pedido já pago."); }
    public void iniciarPreparo(Pedido pedido) { throw new IllegalStateException("Pedido já preparado."); }
    public void despacharEntrega(Pedido pedido) { throw new IllegalStateException("Pedido já está em rota."); }
    public void confirmarEntrega(Pedido pedido) {
        pedido.setEstado(new EstadoEntregue());
    }
    public void cancelar(Pedido pedido, String motivo) {
        throw new IllegalStateException("Pedido em rota de entrega não pode ser cancelado diretamente.");
    }
    public String getNome() { return "EM_TRANSITO"; }
}

class EstadoEntregue implements EstadoPedido {
    public void confirmarPagamento(Pedido pedido) { throw new IllegalStateException("Pedido finalizado."); }
    public void iniciarPreparo(Pedido pedido) { throw new IllegalStateException("Pedido finalizado."); }
    public void despacharEntrega(Pedido pedido) { throw new IllegalStateException("Pedido finalizado."); }
    public void confirmarEntrega(Pedido pedido) { throw new IllegalStateException("Pedido já entregue."); }
    public void cancelar(Pedido pedido, String motivo) {
        throw new IllegalStateException("Não é permitido cancelamento de pedido já entregue. Use fluxo de devolução.");
    }
    public String getNome() { return "ENTREGUE"; }
}

class EstadoCancelado implements EstadoPedido {
    public void confirmarPagamento(Pedido pedido) { throw new IllegalStateException("Pedido cancelado."); }
    public void iniciarPreparo(Pedido pedido) { throw new IllegalStateException("Pedido cancelado."); }
    public void despacharEntrega(Pedido pedido) { throw new IllegalStateException("Pedido cancelado."); }
    public void confirmarEntrega(Pedido pedido) { throw new IllegalStateException("Pedido cancelado."); }
    public void cancelar(Pedido pedido, String motivo) { throw new IllegalStateException("Pedido já se encontra cancelado."); }
    public String getNome() { return "CANCELADO"; }
}

// ============================================================================
// MODELO DE ESTADOS: TRANSAÇÃO DE PAGAMENTO
// ============================================================================

interface EstadoPagamento {
    void processarAutorizacao(PagamentoTransacao ctx, boolean aprovado);
    void liquidar(PagamentoTransacao ctx);
    void estornar(PagamentoTransacao ctx);
    String getNome();
}

class PagamentoTransacao {
    private final String idTransacao;
    private final double valor;
    private EstadoPagamento estadoAtual;

    public PagamentoTransacao(String idTransacao, double valor) {
        this.idTransacao = idTransacao;
        this.valor = valor;
        this.estadoAtual = new EstadoPagamentoPendente();
        System.out.println("  [PAGAMENTO " + idTransacao + "] Criado com valor R$ " + valor);
    }

    public void setEstado(EstadoPagamento novoEstado) {
        System.out.println("  [PAGAMENTO " + idTransacao + "] Transição de " + estadoAtual.getNome() + " para " + novoEstado.getNome());
        this.estadoAtual = novoEstado;
    }

    public void processarAutorizacao(boolean aprovado) { estadoAtual.processarAutorizacao(this, aprovado); }
    public void liquidar() { estadoAtual.liquidar(this); }
    public void estornar() { estadoAtual.estornar(this); }
    public String getNomeEstado() { return estadoAtual.getNome(); }
}

class EstadoPagamentoPendente implements EstadoPagamento {
    public void processarAutorizacao(PagamentoTransacao ctx, boolean aprovado) {
        if (aprovado) {
            ctx.setEstado(new EstadoPagamentoAutorizado());
        } else {
            ctx.setEstado(new EstadoPagamentoRecusado());
        }
    }
    public void liquidar(PagamentoTransacao ctx) {
        throw new IllegalStateException("Transação pendente não pode ser liquidada sem autorização prévia da operadora.");
    }
    public void estornar(PagamentoTransacao ctx) {
        throw new IllegalStateException("Transação pendente não pode ser estornada.");
    }
    public String getNome() { return "PENDENTE"; }
}

class EstadoPagamentoAutorizado implements EstadoPagamento {
    public void processarAutorizacao(PagamentoTransacao ctx, boolean aprovado) {
        throw new IllegalStateException("Transação já autorizada.");
    }
    public void liquidar(PagamentoTransacao ctx) {
        ctx.setEstado(new EstadoPagamentoLiquidado());
    }
    public void estornar(PagamentoTransacao ctx) {
        ctx.setEstado(new EstadoPagamentoEstornado());
    }
    public String getNome() { return "AUTORIZADO"; }
}

class EstadoPagamentoLiquidado implements EstadoPagamento {
    public void processarAutorizacao(PagamentoTransacao ctx, boolean aprovado) {
        throw new IllegalStateException("Transação já liquidada.");
    }
    public void liquidar(PagamentoTransacao ctx) {
        throw new IllegalStateException("Transação já liquidada em conta.");
    }
    public void estornar(PagamentoTransacao ctx) {
        ctx.setEstado(new EstadoPagamentoEstornado());
    }
    public String getNome() { return "LIQUIDADO"; }
}

class EstadoPagamentoRecusado implements EstadoPagamento {
    public void processarAutorizacao(PagamentoTransacao ctx, boolean aprovado) { throw new IllegalStateException("Pagamento recusado."); }
    public void liquidar(PagamentoTransacao ctx) { throw new IllegalStateException("Não é possível liquidar transação recusada."); }
    public void estornar(PagamentoTransacao ctx) { throw new IllegalStateException("Não há saldo a estornar em transação recusada."); }
    public String getNome() { return "RECUSADO"; }
}

class EstadoPagamentoEstornado implements EstadoPagamento {
    public void processarAutorizacao(PagamentoTransacao ctx, boolean aprovado) { throw new IllegalStateException("Finalizado."); }
    public void liquidar(PagamentoTransacao ctx) { throw new IllegalStateException("Transação estornada."); }
    public void estornar(PagamentoTransacao ctx) { throw new IllegalStateException("Já estornada."); }
    public String getNome() { return "ESTORNADO"; }
}
