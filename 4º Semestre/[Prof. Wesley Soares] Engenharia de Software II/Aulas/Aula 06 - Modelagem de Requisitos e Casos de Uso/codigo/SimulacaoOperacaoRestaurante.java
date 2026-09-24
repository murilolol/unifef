/**
 * Disciplina: Engenharia de Software II - UniFEF
 * Professor: Wesley Soares
 * Tema: Modelagem de Requisitos, Casos de Uso e Diagrama de Classes (Food Delivery)
 *
 * Arquivo: SimulacaoOperacaoRestaurante.java
 * Descricao: Implementacao executavel do Caso de Uso "Controlar Pedidos Recebidos"
 *            (Restaurante), demonstrando a fila Kanban de operacao da cozinha,
 *            a maquina de transicao de status do pedido, o canal de eventos assincronos
 *            para o cliente (RNF-02) e o tratamento do Fluxo Alternativo com estorno
 *            automatico em caso de cancelamento por falta de insumo (FA-01 / RF-PAG-01).
 *
 * Como compilar e executar:
 *   javac SimulacaoOperacaoRestaurante.java
 *   java SimulacaoOperacaoRestaurante
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SimulacaoOperacaoRestaurante {

    // =========================================================================
    // ENUMERACOES E EVENTOS
    // =========================================================================

    public enum EstadoPedido {
        PENDENTE,       // Aguardando confirmacao inicial da cozinha
        EM_PREPARO,     // Aceito pelos cozinheiros, em confeccao
        A_CAMINHO,      // Despachado com entregador parceiro
        ENTREGUE,       // Concluido com sucesso na porta do cliente
        CANCELADO       // Abortado (com estorno financeiro)
    }

    /**
     * Interface de escuta que simula a transmissao de eventos via WebSocket (RNF-02)
     * para atualizar a tela do aplicativo do cliente sem recarregar a pagina.
     */
    @FunctionalInterface
    public interface NotificacaoClienteListener {
        void onStatusAtualizado(int numeroPedido, EstadoPedido novoEstado, String mensagem);
    }

    // =========================================================================
    // MODELO OPERACIONAL DE COMANDA E ESTOQUE
    // =========================================================================

    public static class ItemComanda {
        private final String nomePrato;
        private final int quantidade;
        private final String observacoes;

        public ItemComanda(String nomePrato, int quantidade, String observacoes) {
            this.nomePrato = nomePrato;
            this.quantidade = quantidade;
            this.observacoes = observacoes;
        }

        public String getNomePrato() { return nomePrato; }
        public int getQuantidade() { return quantidade; }
        public String getObservacoes() { return observacoes; }

        @Override
        public String toString() {
            String obs = observacoes.isEmpty() ? "" : " (Obs: " + observacoes + ")";
            return quantidade + "x " + nomePrato + obs;
        }
    }

    public static class ComandaOperacional {
        private final int numeroPedido;
        private final String clienteNome;
        private final String enderecoEntrega;
        private final List<ItemComanda> itens;
        private final double valorTotal;
        private EstadoPedido estado;
        private final LocalDateTime dataHoraRecebimento;

        public ComandaOperacional(int numeroPedido, String clienteNome, String enderecoEntrega,
                                  List<ItemComanda> itens, double valorTotal) {
            this.numeroPedido = numeroPedido;
            this.clienteNome = clienteNome;
            this.enderecoEntrega = enderecoEntrega;
            this.itens = new ArrayList<>(itens);
            this.valorTotal = valorTotal;
            this.estado = EstadoPedido.PENDENTE;
            this.dataHoraRecebimento = LocalDateTime.now();
        }

        public int getNumeroPedido() { return numeroPedido; }
        public String getClienteNome() { return clienteNome; }
        public String getEnderecoEntrega() { return enderecoEntrega; }
        public List<ItemComanda> getItens() { return Collections.unmodifiableList(itens); }
        public double getValorTotal() { return valorTotal; }
        public EstadoPedido getEstado() { return estado; }
        public void setEstado(EstadoPedido estado) { this.estado = estado; }
    }

    // =========================================================================
    // PAINEL DE GESTAO DE OPERACOES (ERP DO RESTAURANTE)
    // =========================================================================

    public static class PainelCozinhaRestaurante {
        private final String nomeRestaurante;
        private final Map<EstadoPedido, List<ComandaOperacional>> colunasKanban = new EnumMap<>(EstadoPedido.class);
        private final List<NotificacaoClienteListener> ouvintes = new ArrayList<>();

        public PainelCozinhaRestaurante(String nomeRestaurante) {
            this.nomeRestaurante = nomeRestaurante;
            for (EstadoPedido estado : EstadoPedido.values()) {
                colunasKanban.put(estado, new ArrayList<>());
            }
        }

        public void registrarOuvinte(NotificacaoClienteListener ouvinte) {
            this.ouvintes.add(ouvinte);
        }

        private void notificarCliente(int numeroPedido, EstadoPedido novoEstado, String msg) {
            for (NotificacaoClienteListener ouvinte : ouvintes) {
                ouvinte.onStatusAtualizado(numeroPedido, novoEstado, msg);
            }
        }

        // Passo 2 do Caso de Uso: Chegada de nova comanda
        public void receberNovoPedido(ComandaOperacional comanda) {
            colunasKanban.get(EstadoPedido.PENDENTE).add(comanda);
            System.out.println("\n[ALERTA SONORO DA COZINHA - BEEP!] Novo pedido #" + comanda.getNumeroPedido() +
                    " recebido de " + comanda.getClienteNome());
            notificarCliente(comanda.getNumeroPedido(), EstadoPedido.PENDENTE,
                    "Seu pedido foi recebido pelo restaurante e aguarda aceite da cozinha.");
        }

        // Passo 4 do Caso de Uso: Aceitar pedido e colocar no fogo
        public void aceitarEIniciarPreparo(int numeroPedido) {
            ComandaOperacional comanda = extrairComanda(numeroPedido, EstadoPedido.PENDENTE);
            if (comanda != null) {
                comanda.setEstado(EstadoPedido.EM_PREPARO);
                colunasKanban.get(EstadoPedido.EM_PREPARO).add(comanda);
                System.out.println("[PAINEL COZINHA] Pedido #" + numeroPedido + " movido para: EM PREPARO.");
                notificarCliente(numeroPedido, EstadoPedido.EM_PREPARO,
                        "O chefe aceitou sua comanda! Seus pratos estao sendo preparados agora.");
            } else {
                System.err.println("[ERRO] Pedido #" + numeroPedido + " nao encontrado na fila de Pendentes.");
            }
        }

        // Passo 6 do Caso de Uso: Expedicao
        public void despacharParaEntrega(int numeroPedido) {
            ComandaOperacional comanda = extrairComanda(numeroPedido, EstadoPedido.EM_PREPARO);
            if (comanda != null) {
                comanda.setEstado(EstadoPedido.A_CAMINHO);
                colunasKanban.get(EstadoPedido.A_CAMINHO).add(comanda);
                System.out.println("[PAINEL COZINHA] Pedido #" + numeroPedido + " empacotado e DESPACHADO.");
                notificarCliente(numeroPedido, EstadoPedido.A_CAMINHO,
                        "O entregador coletou seu pedido e ja esta a caminho do seu endereco!");
            } else {
                System.err.println("[ERRO] Pedido #" + numeroPedido + " nao esta pronto para expedicao.");
            }
        }

        // Passo 8 do Caso de Uso: Confirmacao final de entrega
        public void confirmarEntregaConcluida(int numeroPedido) {
            ComandaOperacional comanda = extrairComanda(numeroPedido, EstadoPedido.A_CAMINHO);
            if (comanda != null) {
                comanda.setEstado(EstadoPedido.ENTREGUE);
                colunasKanban.get(EstadoPedido.ENTREGUE).add(comanda);
                System.out.println("[PAINEL COZINHA] Pedido #" + numeroPedido + " marcado como ENTREGUE.");
                notificarCliente(numeroPedido, EstadoPedido.ENTREGUE,
                        "Pedido entregue com sucesso! Bom apetite. Nao esqueca de avaliar o restaurante.");
            } else {
                System.err.println("[ERRO] Pedido #" + numeroPedido + " nao consta como a caminho.");
            }
        }

        // Fluxo Alternativo FA-01 / RF-PAG-01: Rejeicao por falta de insumo com estorno
        public void rejeitarPedidoComEstorno(int numeroPedido, String justificativa) {
            ComandaOperacional comanda = extrairComanda(numeroPedido, EstadoPedido.PENDENTE);
            if (comanda != null) {
                comanda.setEstado(EstadoPedido.CANCELADO);
                colunasKanban.get(EstadoPedido.CANCELADO).add(comanda);
                System.out.println("\n[JUSTIFICATIVA DE REJEICAO] Pedido #" + numeroPedido + " cancelado: " + justificativa);

                // Execucao do RF-PAG-01: Estorno automatico do valor cobrado
                System.out.println("-> [ESTORNO FINANCEIRO RF-PAG-01] Solicitando estorno de R$ " +
                        String.format("%.2f", comanda.getValorTotal()) + " a operadora bancaria...");
                System.out.println("-> [ESTORNO CONCLUIDO] Codigo de liquidacao estornada: REFUND-" + System.currentTimeMillis());

                notificarCliente(numeroPedido, EstadoPedido.CANCELADO,
                        "Infelizmente o restaurante precisou cancelar seu pedido: " + justificativa +
                        ". O estorno integral de R$ " + String.format("%.2f", comanda.getValorTotal()) + " ja foi efetuado.");
            } else {
                System.err.println("[ERRO] Nao foi possivel rejeitar o pedido #" + numeroPedido);
            }
        }

        public void exibirRelatorioKanban() {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("QUADRO KANBAN DA COZINHA: " + nomeRestaurante.toUpperCase());
            System.out.println("------------------------------------------------------------");
            for (EstadoPedido estado : EstadoPedido.values()) {
                List<ComandaOperacional> lista = colunasKanban.get(estado);
                System.out.println("[" + estado + "] (" + lista.size() + " comandas)");
                for (ComandaOperacional c : lista) {
                    System.out.println("   #" + c.getNumeroPedido() + " - " + c.getClienteNome() + " | Total: R$ " +
                            String.format("%.2f", c.getValorTotal()) + " | Itens: " + c.getItens());
                }
            }
            System.out.println("------------------------------------------------------------\n");
        }

        private ComandaOperacional extrairComanda(int numeroPedido, EstadoPedido estadoOrigem) {
            List<ComandaOperacional> lista = colunasKanban.get(estadoOrigem);
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getNumeroPedido() == numeroPedido) {
                    return lista.remove(i);
                }
            }
            return null;
        }
    }

    // =========================================================================
    // EXECUCAO PRATICA DA ROTINA DO RESTAURANTE
    // =========================================================================

    public static void main(String[] args) {
        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        System.out.println("====================================================================");
        System.out.println("ENGENHARIA DE SOFTWARE II - DEMONSTRACAO: OPERACAO DO RESTAURANTE");
        System.out.println("Professor: Wesley Soares | Tema: Controle de Pedidos e Maquina de Estados");
        System.out.println("====================================================================");

        PainelCozinhaRestaurante painel = new PainelCozinhaRestaurante("Pizzaria Bella Massa");

        // Registra o ouvinte WebSocket que simula a notificacao push no celular do cliente
        painel.registrarOuvinte((numeroPedido, novoEstado, mensagem) -> {
            System.out.println("   >>> [PUSH P/ CLIENTE - WS 500ms] Pedido #" + numeroPedido +
                    " [" + novoEstado + "]: " + mensagem);
        });

        // Montando Comanda #201
        List<ItemComanda> itens201 = new ArrayList<>();
        itens201.add(new ItemComanda("Pizza Margherita Grande", 1, "Massa fina e crocante"));
        itens201.add(new ItemComanda("Refrigerante Guaraná 2L", 1, "Gelado"));
        ComandaOperacional comanda201 = new ComandaOperacional(
                201, "Mariana Costa", "Av. Brasil, 1200, Apto 42", itens201, 74.50
        );

        // Montando Comanda #202 (destinada a simular falta de estoque)
        List<ItemComanda> itens202 = new ArrayList<>();
        itens202.add(new ItemComanda("Lasanha Quatro Queijos", 1, "Sem oregano"));
        ComandaOperacional comanda202 = new ComandaOperacional(
                202, "Rodrigo Silva", "Rua Sergipe, 88", itens202, 42.00
        );

        // ---------------------------------------------------------------------
        // EXECUCAO DO FLUXO PRINCIPAL: CICLO COMPLETO DO PEDIDO #201
        // ---------------------------------------------------------------------
        System.out.println("\n--- 1. PROCESSANDO FLUXO REGULAR (PEDIDO #201) ---");
        painel.receberNovoPedido(comanda201);

        // Cozinha visualiza e inicia confeccao
        painel.aceitarEIniciarPreparo(201);

        // Refeicao pronta, despachando para entrega
        painel.despacharParaEntrega(201);

        // Entregador confirma que entregou na residencia do cliente
        painel.confirmarEntregaConcluida(201);

        // ---------------------------------------------------------------------
        // EXECUCAO DO FLUXO ALTERNATIVO FA-01: REJEICAO POR FALTA DE INSUMO (#202)
        // ---------------------------------------------------------------------
        System.out.println("\n--- 2. PROCESSANDO FLUXO ALTERNATIVO FA-01 (REJEICAO DE PEDIDO #202) ---");
        painel.receberNovoPedido(comanda202);

        // Cozinha constata falta de queijo gorgonzola para a lasanha
        painel.rejeitarPedidoComEstorno(202, "Ingredientes frescos da Lasanha esgotados no turno atual.");

        // ---------------------------------------------------------------------
        // VALIDACAO DE ROBUSTEZ: TENTATIVA DE TRANSICAO ILEGAL
        // ---------------------------------------------------------------------
        System.out.println("\n--- 3. TESTE DE VIOLACAO DE TRANSICAO DE ESTADOS ---");
        System.out.println("Tentando forcar entrega de pedido que nao esta na coluna 'A Caminho'...");
        painel.confirmarEntregaConcluida(999); // Nao existe

        // Exibicao final do estado dos pedidos no Kanban
        painel.exibirRelatorioKanban();
    }
}
