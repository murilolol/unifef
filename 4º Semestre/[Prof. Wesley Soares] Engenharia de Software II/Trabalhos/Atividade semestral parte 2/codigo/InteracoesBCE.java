/*
 * Disciplina: Engenharia de Software II (4º Semestre)
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Prof. Ms. Wesley Soares de Souza
 * Tema: Arquitetura Boundary-Control-Entity (BCE) e Simulação de Interações UML
 *
 * Como executar:
 *   javac InteracoesBCE.java
 *   java InteracoesBCE
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteracoesBCE {

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("ENGENHARIA DE SOFTWARE II - PROF. WESLEY SOARES");
        System.out.println("SIMULAÇÃO DE INTERAÇÃO (SEQUÊNCIA E COMUNICAÇÃO - PADRÃO BCE)");
        System.out.println("===========================================================\n");

        // Infraestrutura e Injeção de Dependências
        RepositorioMemoria repositorio = new RepositorioMemoria();
        GatewayPagamentoExterno gateway = new GatewayPagamentoExterno();
        ServicoNotificacaoExterno servicoNotificacao = new ServicoNotificacaoExterno();

        // Controlador (Control)
        PedidoController controller = new PedidoController(repositorio, gateway, servicoNotificacao);

        // Camada de Fronteira (Boundary)
        CheckoutUIBoundary boundaryUI = new CheckoutUIBoundary(controller);

        // --------------------------------------------------------------------
        // Cenário 1: Realização e Checkout do Pedido (Sequência / Comunicação 1)
        // --------------------------------------------------------------------
        System.out.println("--- [CENÁRIO 1] Cliente Submete Novo Pedido via Interface ---");
        List<ItemDTO> itens = new ArrayList<>();
        itens.add(new ItemDTO("Hamburguer Artesanal", 2, 35.00));
        itens.add(new ItemDTO("Refrigerante Lata", 2, 6.50));

        String idPedidoGerado = boundaryUI.acionarCheckout("CLI-101", itens, "4111.XXXX.XXXX.1234");
        System.out.println("[BOUNDARY RECEBE RETORNO]: Pedido gerado com identificador: " + idPedidoGerado);

        // --------------------------------------------------------------------
        // Cenário 2: Validação e Confirmação Financeira (Sequência / Comunicação 2)
        // --------------------------------------------------------------------
        System.out.println("\n--- [CENÁRIO 2] Processamento Financeiro e Notificação do Restaurante ---");
        boolean financeiroOk = controller.executarValidacaoFinanceira(idPedidoGerado);
        System.out.println("[RESULTADO FINANCEIRO]: Sucesso na liquidação = " + financeiroOk);

        // --------------------------------------------------------------------
        // Cenário 3: Operação de Cozinha e Despacho Logístico (Sequência / Comunicação 3)
        // --------------------------------------------------------------------
        System.out.println("\n--- [CENÁRIO 3] Painel do Restaurante: Atualização de Status Operacional ---");
        RestaurantePainelBoundary painelRestaurante = new RestaurantePainelBoundary(controller);
        painelRestaurante.sinalizarPreparoConcluido(idPedidoGerado);
        painelRestaurante.despacharParaEntregador(idPedidoGerado);

        // --------------------------------------------------------------------
        // Cenário 4: Cancelamento do Pedido com Auditoria e Estorno (Sequência / Comunicação 4)
        // --------------------------------------------------------------------
        System.out.println("\n--- [CENÁRIO 4] Atendimento ao Cliente: Solicitação de Cancelamento ---");
        String idPedido2 = boundaryUI.acionarCheckout("CLI-102", itens, "5502.XXXX.XXXX.9988");
        boundaryUI.solicitarCancelamento(idPedido2, "Desistência antes da aprovação");

        System.out.println("\n===========================================================");
        System.out.println("TODOS OS 4 CENÁRIOS DE INTERAÇÃO BCE FORAM CONCLUÍDOS!");
        System.out.println("===========================================================");
    }
}

// ============================================================================
// CAMADA DE DTOs E DADOS
// ============================================================================
class ItemDTO {
    public String nome;
    public int quantidade;
    public double precoUnitario;

    public ItemDTO(String nome, int quantidade, double precoUnitario) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }
}

// ============================================================================
// CAMADA ENTITY (Entidades de Negócio com Estado e Regras Puramente Internas)
// ============================================================================
class PedidoEntity {
    private final String id;
    private final String clienteId;
    private final List<ItemDTO> itens;
    private final double valorTotal;
    private String status;

    public PedidoEntity(String id, String clienteId, List<ItemDTO> itens) {
        this.id = id;
        this.clienteId = clienteId;
        this.itens = itens;
        double soma = 0;
        for (ItemDTO i : itens) {
            soma += (i.quantidade * i.precoUnitario);
        }
        this.valorTotal = soma;
        this.status = "PENDENTE_PAGAMENTO";
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public double getValorTotal() { return valorTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// ============================================================================
// CAMADA CONTROL (Orquestração de Casos de Uso, Transações e Regras)
// ============================================================================
class PedidoController {
    private final RepositorioMemoria repositorio;
    private final GatewayPagamentoExterno gateway;
    private final ServicoNotificacaoExterno notificacao;
    private int sequencial = 100;

    public PedidoController(RepositorioMemoria repo, GatewayPagamentoExterno gateway, ServicoNotificacaoExterno notif) {
        this.repositorio = repo;
        this.gateway = gateway;
        this.notificacao = notif;
    }

    // Cenário 1: Criar Pedido
    public String criarNovoPedido(String clienteId, List<ItemDTO> itens, String cartao) {
        System.out.println("  [CONTROLLER] Recebendo requisição de criação de pedido para cliente " + clienteId);
        String idPedido = "PED-" + (++sequencial);
        PedidoEntity pedido = new PedidoEntity(idPedido, clienteId, itens);

        repositorio.salvar(pedido);
        System.out.println("  [CONTROLLER -> REPOSITORIO] Pedido persistido com total R$ " + pedido.getValorTotal());
        return idPedido;
    }

    // Cenário 2: Validação Financeira
    public boolean executarValidacaoFinanceira(String idPedido) {
        PedidoEntity pedido = repositorio.buscarPorId(idPedido);
        if (pedido == null) return false;

        System.out.println("  [CONTROLLER -> GATEWAY] Solicitando autorização de R$ " + pedido.getValorTotal());
        boolean autorizado = gateway.autorizarCobranca(pedido.getValorTotal());

        if (autorizado) {
            pedido.setStatus("CONFIRMADO");
            repositorio.atualizar(pedido);
            notificacao.enviarMensagem(pedido.getClienteId(), "Seu pagamento foi aprovado com sucesso!");
            notificacao.enviarMensagem("RESTAURANTE-01", "Novo pedido " + idPedido + " para preparar.");
            return true;
        } else {
            pedido.setStatus("FALHA_PAGAMENTO");
            repositorio.atualizar(pedido);
            return false;
        }
    }

    // Cenário 3: Atualizações Operacionais
    public void atualizarStatusOperacional(String idPedido, String novoStatus) {
        PedidoEntity pedido = repositorio.buscarPorId(idPedido);
        if (pedido != null) {
            pedido.setStatus(novoStatus);
            repositorio.atualizar(pedido);
            System.out.println("  [CONTROLLER] Status do pedido " + idPedido + " atualizado para " + novoStatus);
            notificacao.enviarMensagem(pedido.getClienteId(), "Status atualizado: " + novoStatus);
        }
    }

    // Cenário 4: Cancelamento
    public void cancelarPedido(String idPedido, String motivo) {
        PedidoEntity pedido = repositorio.buscarPorId(idPedido);
        if (pedido != null) {
            pedido.setStatus("CANCELADO");
            repositorio.atualizar(pedido);
            System.out.println("  [CONTROLLER] Pedido " + idPedido + " cancelado. Motivo: " + motivo);
            notificacao.enviarMensagem(pedido.getClienteId(), "Pedido cancelado: " + motivo);
        }
    }
}

// ============================================================================
// CAMADA BOUNDARY (Fronteiras: Telas, APIS, Gateways Externos e Notificações)
// ============================================================================
class CheckoutUIBoundary {
    private final PedidoController controller;

    public CheckoutUIBoundary(PedidoController controller) {
        this.controller = controller;
    }

    public String acionarCheckout(String clienteId, List<ItemDTO> itens, String cartao) {
        System.out.println("[UI BOUNDARY] Usuário interagiu com a tela de Checkout.");
        return controller.criarNovoPedido(clienteId, itens, cartao);
    }

    public void solicitarCancelamento(String idPedido, String motivo) {
        System.out.println("[UI BOUNDARY] Solicitação de cancelamento disparada pela tela.");
        controller.cancelarPedido(idPedido, motivo);
    }
}

class RestaurantePainelBoundary {
    private final PedidoController controller;

    public RestaurantePainelBoundary(PedidoController controller) {
        this.controller = controller;
    }

    public void sinalizarPreparoConcluido(String idPedido) {
        System.out.println("[RESTAURANTE BOUNDARY] Cozinha finalizou a montagem dos pratos.");
        controller.atualizarStatusOperacional(idPedido, "PRONTO_PARA_DESPACHO");
    }

    public void despacharParaEntregador(String idPedido) {
        System.out.println("[RESTAURANTE BOUNDARY] Pedido entregue ao entregador parceiro.");
        controller.atualizarStatusOperacional(idPedido, "EM_ROTA");
    }
}

class GatewayPagamentoExterno {
    public boolean autorizarCobranca(double valor) {
        System.out.println("    [EXTERNO GATEWAY] Comunicando com adquirente financeira... Autorizado!");
        return true;
    }
}

class ServicoNotificacaoExterno {
    public void enviarMensagem(String destinatario, String texto) {
        System.out.println("    [EXTERNO PUSH NOTIFICATION] Para " + destinatario + ": \"" + texto + "\"");
    }
}

class RepositorioMemoria {
    private final Map<String, PedidoEntity> banco = new HashMap<>();

    public void salvar(PedidoEntity p) { banco.put(p.getId(), p); }
    public void atualizar(PedidoEntity p) { banco.put(p.getId(), p); }
    public PedidoEntity buscarPorId(String id) { return banco.get(id); }
}
