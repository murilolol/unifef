/*
 * Disciplina : Engenharia de Software II (4º Semestre) - UniFEF
 * Professor  : Wesley Soares
 * Tema       : Modelagem de Casos de Uso UML - Execução Prática de Include, Extend e Fluxos
 * 
 * Como compilar: javac SimuladorCasosDeUsoEcommerce.java
 * Como executar: java SimuladorCasosDeUsoEcommerce
 *
 * Descrição:
 * Este programa implementa em código Java executável a orquestração do Caso de Uso
 * Base 'UC03: Finalizar Compra', demonstrando a semântica formal dos relacionamentos:
 * 1. <<include>> : Invocação mandatória e incondicional (UC04: Autenticar Usuário e UC06: Realizar Pagamento).
 * 2. <<extend>>  : Injeção condicional no Ponto de Extensão 'PontoDeCupom' (UC05: Aplicar Cupom de Desconto).
 * 3. Fluxo Principal (Caminho Feliz), Fluxos Alternativos e Fluxos de Exceção.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SimuladorCasosDeUsoEcommerce {

    // =========================================================================
    // ENTIDADES DE DOMÍNIO (Contidas dentro da Fronteira do Sistema)
    // =========================================================================

    public static class ItemProduto {
        private final String codigo;
        private final String nome;
        private final double precoUnitario;
        private int estoqueDisponivel;

        public ItemProduto(String codigo, String nome, double precoUnitario, int estoqueDisponivel) {
            this.codigo = codigo;
            this.nome = nome;
            this.precoUnitario = precoUnitario;
            this.estoqueDisponivel = estoqueDisponivel;
        }

        public String getCodigo() { return codigo; }
        public String getNome() { return nome; }
        public double getPrecoUnitario() { return precoUnitario; }
        public int getEstoqueDisponivel() { return estoqueDisponivel; }

        public void decrementarEstoque(int quantidade) {
            this.estoqueDisponivel -= quantidade;
        }

        public void restaurarEstoque(int quantidade) {
            this.estoqueDisponivel += quantidade;
        }
    }

    public static class ItemCarrinho {
        private final ItemProduto produto;
        private final int quantidade;

        public ItemCarrinho(ItemProduto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public ItemProduto getProduto() { return produto; }
        public int getQuantidade() { return quantidade; }
        public double getSubtotal() { return produto.getPrecoUnitario() * quantidade; }
    }

    public static class UsuarioSessao {
        private final String email;
        private final String senhaHash;
        private final String enderecoEntrega;
        private boolean autenticado;

        public UsuarioSessao(String email, String senhaHash, String enderecoEntrega) {
            this.email = email;
            this.senhaHash = senhaHash;
            this.enderecoEntrega = enderecoEntrega;
            this.autenticado = false;
        }

        public String getEmail() { return email; }
        public String getEnderecoEntrega() { return enderecoEntrega; }
        public boolean isAutenticado() { return autenticado; }
        public void setAutenticado(boolean autenticado) { this.autenticado = autenticado; }
    }

    public static class CupomDesconto {
        private final String codigo;
        private final double percentualDesconto; // Ex: 0.15 = 15%
        private final boolean ativo;

        public CupomDesconto(String codigo, double percentualDesconto, boolean ativo) {
            this.codigo = codigo;
            this.percentualDesconto = percentualDesconto;
            this.ativo = ativo;
        }

        public String getCodigo() { return codigo; }
        public double getPercentualDesconto() { return percentualDesconto; }
        public boolean isAtivo() { return ativo; }
    }

    public enum StatusPedido {
        CRIADO, AGUARDANDO_PAGAMENTO, APROVADO, CANCELADO
    }

    public static class Pedido {
        private final String idPedido;
        private final String emailCliente;
        private final List<ItemCarrinho> itens;
        private final double valorProdutos;
        private final double valorFrete;
        private final double valorDesconto;
        private final double valorTotal;
        private StatusPedido status;

        public Pedido(String emailCliente, List<ItemCarrinho> itens, double valorProdutos,
                      double valorFrete, double valorDesconto, double valorTotal) {
            this.idPedido = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.emailCliente = emailCliente;
            this.itens = new ArrayList<>(itens);
            this.valorProdutos = valorProdutos;
            this.valorFrete = valorFrete;
            this.valorDesconto = valorDesconto;
            this.valorTotal = valorTotal;
            this.status = StatusPedido.CRIADO;
        }

        public String getIdPedido() { return idPedido; }
        public double getValorTotal() { return valorTotal;
        }
        public StatusPedido getStatus() { return status; }
        public void setStatus(StatusPedido status) { this.status = status; }

        @Override
        public String toString() {
            return String.format("[Pedido #%s | Cliente: %s | Produtos: R$ %.2f | Frete: R$ %.2f | Desconto: R$ %.2f | Total: R$ %.2f | Status: %s]",
                    idPedido, emailCliente, valorProdutos, valorFrete, valorDesconto, valorTotal, status);
        }
    }

    // Exceções de Regra de Negócio (Mapeiam os Fluxos de Exceção dos Casos de Uso)
    public static class FalhaAutenticacaoException extends Exception {
        public FalhaAutenticacaoException(String msg) { super(msg); }
    }

    public static class EstoqueInsuficienteException extends Exception {
        public EstoqueInsuficienteException(String msg) { super(msg); }
    }

    public static class PagamentoRecusadoException extends Exception {
        public PagamentoRecusadoException(String msg) { super(msg); }
    }

    // =========================================================================
    // CASOS DE USO AUXILIARES (Include e Extend)
    // =========================================================================

    /**
     * UC04: Autenticar Usuário
     * Relação com UC03: <<include>> (mandatório). Sem autenticação bem-sucedida,
     * a pós-condição de UC03 jamais pode ser atingida.
     */
    public static class UC04AutenticarUsuario {
        public static void executar(UsuarioSessao usuario, String senhaInformada) throws FalhaAutenticacaoException {
            System.out.println("   [<<include>> UC04: Autenticar Usuário] Verificando credenciais para: " + usuario.getEmail());
            // Validação semântica: a senha válida neste mock é '123456'
            if (usuario.senhaHash.equals(senhaInformada)) {
                usuario.setAutenticado(true);
                System.out.println("   [<<include>> UC04: Autenticar Usuário] Sucesso: Usuário autenticado na sessão.");
            } else {
                usuario.setAutenticado(false);
                throw new FalhaAutenticacaoException("Credenciais inválidas fornecidas no UC04.");
            }
        }
    }

    /**
     * UC05: Aplicar Cupom de Desconto
     * Relação com UC03: <<extend>> (opcional e condicional).
     * Só é acionado no ponto de extensão 'PontoDeCupom' se o cliente informar um cupom.
     */
    public static class UC05AplicarCupomDesconto {
        public static double executarPontoDeExtensao(CupomDesconto cupom, double subtotal) {
            System.out.println("   [<<extend>> UC05: Aplicar Cupom] Ponto de Extensão disparado para código: "
                    + (cupom != null ? cupom.getCodigo() : "Nenhum"));

            if (cupom == null || !cupom.isAtivo()) {
                System.out.println("   [<<extend>> UC05: Aplicar Cupom] Condição de extensão não atendida ou cupom inativo. Desconto R$ 0,00.");
                return 0.0;
            }

            double desconto = subtotal * cupom.getPercentualDesconto();
            System.out.printf("   [<<extend>> UC05: Aplicar Cupom] Sucesso: Desconto de %.0f%% aplicado (-R$ %.2f)%n",
                    cupom.getPercentualDesconto() * 100, desconto);
            return desconto;
        }
    }

    /**
     * Mock de Gateway de Pagamento Externo (Ator Secundário fora da fronteira).
     */
    public static class AtorSecundarioGateway {
        public static boolean processarTransacao(String idPedido, double valor, boolean simularSaldoSuficiente) {
            System.out.printf("   [Ator Secundário: Gateway de Pagamento] Processando cobrança de R$ %.2f para Pedido #%s...%n", valor, idPedido);
            return simularSaldoSuficiente;
        }
    }

    /**
     * UC06: Realizar Pagamento
     * Relação com UC03: <<include>> (mandatório). Invoca o ator secundário Gateway.
     */
    public static class UC06RealizarPagamento {
        public static void executar(Pedido pedido, boolean simularAprovacao) throws PagamentoRecusadoException {
            System.out.println("   [<<include>> UC06: Realizar Pagamento] Iniciando liquidação financeira do pedido...");
            pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
            
            boolean aprovado = AtorSecundarioGateway.processarTransacao(pedido.getIdPedido(), pedido.getValorTotal(), simularAprovacao);
            if (aprovado) {
                pedido.setStatus(StatusPedido.APROVADO);
                System.out.println("   [<<include>> UC06: Realizar Pagamento] Sucesso: Pagamento aprovado pelo Gateway.");
            } else {
                pedido.setStatus(StatusPedido.CANCELADO);
                throw new PagamentoRecusadoException("Gateway externo recusou a transação (saldo insuficiente ou cartão inválido).");
            }
        }
    }

    // =========================================================================
    // CASO DE USO BASE ORQUESTRADOR
    // =========================================================================

    /**
     * UC03: Finalizar Compra (Caso de Uso Base)
     * Orquestra o fluxo principal, verifica pré-condições, invoca os includes mandatórios
     * e avalia os pontos de extensão opcionais.
     */
    public static class UC03FinalizarCompra {

        public static Pedido executarFluxo(
                UsuarioSessao usuario,
                String senhaInformada,
                List<ItemCarrinho> carrinho,
                CupomDesconto cupomOpcional,
                String tipoFrete, // 'NORMAL' ou 'EXPRESSO'
                boolean simularSucessoPagamento
        ) throws EstoqueInsuficienteException, FalhaAutenticacaoException, PagamentoRecusadoException {

            System.out.println("\n======================================================================");
            System.out.println("EXECUTANDO UC03: Finalizar Compra (Caso Base)");
            System.out.println("======================================================================");

            // Passo 1 do Fluxo Principal: Pré-condição (Carrinho não vazio)
            if (carrinho == null || carrinho.isEmpty()) {
                throw new IllegalArgumentException("Pré-condição violada: Carrinho de compras vazio.");
            }
            System.out.println("Passo 1: Cliente aciona finalização com " + carrinho.size() + " itens no carrinho.");

            // Passo 1a (Fluxo de Exceção): Validação de estoque físico em tempo real
            for (ItemCarrinho item : carrinho) {
                if (item.getProduto().getEstoqueDisponivel() < item.getQuantidade()) {
                    System.err.println("Fluxo de Exceção 1a: Estoque esgotado para o produto " + item.getProduto().getNome());
                    throw new EstoqueInsuficienteException("Estoque insuficiente para: " + item.getProduto().getNome());
                }
            }
            System.out.println("Passo 1: Estoque verificado e reservado provisoriamente.");
            for (ItemCarrinho item : carrinho) {
                item.getProduto().decrementarEstoque(item.getQuantidade());
            }

            try {
                // Passo 2 do Fluxo Principal: <<include>> UC04: Autenticar Usuário (Mandatório)
                System.out.println("Passo 2: Invocando inclusão mandatória de autenticação...");
                UC04AutenticarUsuario.executar(usuario, senhaInformada);

                // Passo 3 e 4: Confirmação de endereço de entrega
                System.out.println("Passo 3/4: Endereço de entrega confirmado: " + usuario.getEnderecoEntrega());

                // Passo 5 e 6: Cálculo de frete
                double valorFrete = tipoFrete.equalsIgnoreCase("EXPRESSO") ? 45.00 : 20.00;
                System.out.printf("Passo 5/6: Modalidade de frete selecionada: %s (R$ %.2f)%n", tipoFrete, valorFrete);

                // Passo 7: Cálculo do subtotal dos produtos
                double subtotalProdutos = 0.0;
                for (ItemCarrinho item : carrinho) {
                    subtotalProdutos += item.getSubtotal();
                }

                // Passo 8 / 8a: Ponto de Extensão 'PontoDeCupom' para <<extend>> UC05 (Opcional)
                System.out.println("Passo 8: Verificando Ponto de Extensão [PontoDeCupom]...");
                double valorDesconto = 0.0;
                if (cupomOpcional != null) {
                    // Dispara o caso extensor UC05
                    valorDesconto = UC05AplicarCupomDesconto.executarPontoDeExtensao(cupomOpcional, subtotalProdutos);
                } else {
                    System.out.println("   [Extensão não acionada] Cliente optou por não aplicar cupom promocional.");
                }

                double totalFinal = subtotalProdutos + valorFrete - valorDesconto;
                if (totalFinal < 0) totalFinal = 0.0;

                // Instanciação preliminar do pedido
                Pedido pedido = new Pedido(usuario.getEmail(), carrinho, subtotalProdutos, valorFrete, valorDesconto, totalFinal);
                System.out.println("Passo 8: Resumo gerado -> " + pedido);

                // Passo 9 e 10: <<include>> UC06: Realizar Pagamento (Mandatório)
                System.out.println("Passo 9: Invocando inclusão mandatória de pagamento...");
                UC06RealizarPagamento.executar(pedido, simularSucessoPagamento);

                // Passo 11 e 12: Conclusão bem-sucedida (Garantia de Sucesso / Pós-condição)
                System.out.println("Passo 11/12: Baixa definitiva de estoque e limpeza do carrinho de compras.");
                System.out.println("Pós-condição atingida: Pedido confirmado com sucesso!");
                return pedido;

            } catch (FalhaAutenticacaoException | PagamentoRecusadoException e) {
                // Reversão de salvaguarda: restaura estoque reservado em caso de falha
                System.err.println("Fluxo de Exceção acionado: Revertendo reserva de estoque provisória...");
                for (ItemCarrinho item : carrinho) {
                    item.getProduto().restaurarEstoque(item.getQuantidade());
                }
                throw e; // Repassa a exceção formalmente
            }
        }
    }

    // =========================================================================
    // MÉTODO PRINCIPAL DE DEMONSTRAÇÃO DOS CENÁRIOS E FLUXOS
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("UNIFEF - ENGENHARIA DE SOFTWARE II - PROF. WESLEY SOARES");
        System.out.println("SIMULAÇÃO PRÁTICA: EXECUÇÃO FORMAL DE CASOS DE USO UML 2.5");
        System.out.println("====================================================================");

        // Criação do catálogo base de produtos
        ItemProduto notebook = new ItemProduto("PRD01", "Notebook Dell XPS 15", 8500.00, 3);
        ItemProduto mouse = new ItemProduto("PRD02", "Mouse Sem Fio Logitech", 150.00, 10);

        UsuarioSessao clienteValido = new UsuarioSessao("aluno.unifef@edu.br", "123456", "Rua Universitária, 500 - Fernandópolis/SP");
        CupomDesconto cupom15 = new CupomDesconto("UNIFEF15", 0.15, true);

        // ---------------------------------------------------------------------
        // CENÁRIO 1: CAMINHO FELIZ COM EXTENSÃO (CUPOM APLICADO COM SUCESSO)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> CENÁRIO 1: Caminho Feliz com disparo de <<extend>> UC05 (Cupom Ativo)");
        List<ItemCarrinho> carrinho1 = new ArrayList<>();
        carrinho1.add(new ItemCarrinho(notebook, 1));
        carrinho1.add(new ItemCarrinho(mouse, 2));

        try {
            Pedido pedidoSucesso = UC03FinalizarCompra.executarFluxo(
                    clienteValido,
                    "123456",      // Senha correta (UC04 sucesso)
                    carrinho1,
                    cupom15,       // Dispara ponto de extensão UC05
                    "NORMAL",
                    true           // Gateway aprova UC06
            );
            System.out.println("RESULTADO CENÁRIO 1: " + pedidoSucesso);
            System.out.println("Estoque restante do Notebook: " + notebook.getEstoqueDisponivel());
        } catch (Exception e) {
            System.err.println("Erro inesperado no Cenário 1: " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENÁRIO 2: CAMINHO SEM EXTENSÃO (O Caso Base funciona sozinho)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> CENÁRIO 2: Caminho Feliz sem <<extend>> (Demonstra independência do Caso Base)");
        List<ItemCarrinho> carrinho2 = new ArrayList<>();
        carrinho2.add(new ItemCarrinho(mouse, 1));

        try {
            Pedido pedidoSemCupom = UC03FinalizarCompra.executarFluxo(
                    clienteValido,
                    "123456",
                    carrinho2,
                    null,          // Sem cupom: o caso extensor NÃO é chamado
                    "EXPRESSO",
                    true
            );
            System.out.println("RESULTADO CENÁRIO 2: " + pedidoSemCupom);
        } catch (Exception e) {
            System.err.println("Erro inesperado no Cenário 2: " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENÁRIO 3: FLUXO DE EXCEÇÃO NO INCLUDE MANDATÓRIO (Falha de Autenticação)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> CENÁRIO 3: Fluxo de Exceção no <<include>> UC04 (Senha Incorreta)");
        List<ItemCarrinho> carrinho3 = new ArrayList<>();
        carrinho3.add(new ItemCarrinho(mouse, 1));

        try {
            UC03FinalizarCompra.executarFluxo(
                    clienteValido,
                    "SENHA_ERRADA", // Provoca falha no include mandatório
                    carrinho3,
                    null,
                    "NORMAL",
                    true
            );
        } catch (FalhaAutenticacaoException e) {
            System.out.println("CONFIRMAÇÃO DIDÁTICA DO CENÁRIO 3: Como UC04 é <<include>>, a falha abortou UC03 com mensagem: '" + e.getMessage() + "'");
        } catch (Exception e) {
            System.err.println("Outro erro capturado: " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENÁRIO 4: FLUXO DE EXCEÇÃO NO INCLUDE MANDATÓRIO (Pagamento Recusado)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> CENÁRIO 4: Fluxo de Exceção no <<include>> UC06 (Gateway recusa pagamento)");
        int estoqueMouseAntes = mouse.getEstoqueDisponivel();
        List<ItemCarrinho> carrinho4 = new ArrayList<>();
        carrinho4.add(new ItemCarrinho(mouse, 2));

        try {
            UC03FinalizarCompra.executarFluxo(
                    clienteValido,
                    "123456",
                    carrinho4,
                    null,
                    "NORMAL",
                    false          // Simula recusa pelo gateway externo
            );
        } catch (PagamentoRecusadoException e) {
            System.out.println("CONFIRMAÇÃO DIDÁTICA DO CENÁRIO 4: UC06 falhou. Pedido não gerado. Exceção: '" + e.getMessage() + "'");
            System.out.println("Garantia de Integridade: Estoque foi restaurado de " + mouse.getEstoqueDisponivel() + " para " + estoqueMouseAntes + ".");
        } catch (Exception e) {
            System.err.println("Outro erro capturado: " + e.getMessage());
        }
    }
}
