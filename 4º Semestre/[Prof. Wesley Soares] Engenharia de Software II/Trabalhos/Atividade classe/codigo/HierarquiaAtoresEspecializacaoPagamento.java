/*
 * Disciplina : Engenharia de Software II (4º Semestre) - UniFEF
 * Professor  : Wesley Soares
 * Tema       : Generalização de Atores e Especialização de Casos de Uso (UML 2.5)
 * 
 * Como compilar: javac HierarquiaAtoresEspecializacaoPagamento.java
 * Como executar: java HierarquiaAtoresEspecializacaoPagamento
 *
 * Descrição:
 * Este programa demonstra a aplicação de dois conceitos estruturais e taxonômicos da UML:
 * 1. Generalização entre Atores: Administrador herda todas as associações de Cliente e
 *    adiciona casos de uso exclusivos (ex: UC10: Manter Catálogo de Produtos).
 * 2. Generalização/Especialização de Casos de Uso: UC06 (Realizar Pagamento) modelado
 *    como contrato abstrato, com especializações concretas UC07 (Cartão de Crédito) e UC08 (PIX).
 * 3. Integração com o Ator Secundário (Gateway de Pagamento Externo).
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HierarquiaAtoresEspecializacaoPagamento {

    // =========================================================================
    // ATORES DO SISTEMA (Modelagem da Herança de Papéis)
    // =========================================================================

    /**
     * Ator Primário Base: Cliente
     * Possui acesso às funções de consulta ao catálogo, gestão de carrinho e compra.
     */
    public static class AtorCliente {
        protected final String idUsuario;
        protected final String nome;
        protected final String email;

        public AtorCliente(String idUsuario, String nome, String email) {
            this.idUsuario = idUsuario;
            this.nome = nome;
            this.email = email;
        }

        public String getNome() { return nome; }
        public String getEmail() { return email; }

        // Casos de Uso associados ao Cliente
        public void executarUC01ConsultarCatalogo(List<String> catalogo) {
            System.out.printf("[%s - %s] Executando UC01: Consultar Catálogo. Total de itens disponíveis: %d%n",
                    getClass().getSimpleName(), nome, catalogo.size());
        }

        public void executarUC03FinalizarCompra(double valorTotal) {
            System.out.printf("[%s - %s] Executando UC03: Finalizar Compra no valor de R$ %.2f%n",
                    getClass().getSimpleName(), nome, valorTotal);
        }
    }

    /**
     * Ator Primário Especializado: Administrador
     * Relação no Diagrama UML: Administrador --|> Cliente
     * O Administrador herda todas as capacidades do Cliente, e adiciona casos
     * administrativos exclusivos (UC10: Manter Catálogo de Produtos).
     */
    public static class AtorAdministrador extends AtorCliente {
        private final String nivelAcesso;

        public AtorAdministrador(String idUsuario, String nome, String email, String nivelAcesso) {
            super(idUsuario, nome, email);
            this.nivelAcesso = nivelAcesso;
        }

        public String getNivelAcesso() { return nivelAcesso; }

        // Caso de Uso exclusivo de Administrador (UC10)
        public void executarUC10ManterCatalogo(List<String> catalogo, String operacao, String item) {
            System.out.printf("[%s (Admin Nível: %s) - %s] Executando UC10: Manter Catálogo -> Operação: %s no item '%s'%n",
                    getClass().getSimpleName(), nivelAcesso, nome, operacao, item);
            if (operacao.equalsIgnoreCase("ADICIONAR")) {
                catalogo.add(item);
                System.out.println("   -> Item adicionado com sucesso ao catálogo.");
            } else if (operacao.equalsIgnoreCase("REMOVER")) {
                catalogo.remove(item);
                System.out.println("   -> Item removido com sucesso do catálogo.");
            }
        }
    }

    // =========================================================================
    // ATOR SECUNDÁRIO (Externo à fronteira do sistema)
    // =========================================================================

    public static class AtorSecundarioGatewayBancario {
        public boolean autorizarCartao(String cartaoMascara, double valor) {
            System.out.printf("   [Ator Secundário: Gateway Externo] Validando limites e token do cartão %s para R$ %.2f...%n",
                    cartaoMascara, valor);
            return true; // Simula autorização imediata
        }

        public String gerarCobrancaPix(String chavePix, double valor) {
            String payloadPix = "00020126580014BR.GOV.BCB.PIX0136" + UUID.randomUUID() + "520400005303986540" + (int)valor;
            System.out.printf("   [Ator Secundário: Gateway Externo] Cobrança Pix dinâmica gerada para chave %s no valor R$ %.2f%n",
                    chavePix, valor);
            return payloadPix;
        }
    }

    // =========================================================================
    // GENERALIZAÇÃO/ESPECIALIZAÇÃO DE CASOS DE USO
    // =========================================================================

    /**
     * UC06: Realizar Pagamento (Caso de Uso Abstrato / Genérico)
     * Na UML, não é instanciado diretamente; define o contrato compartilhado
     * por todas as formas especializadas de pagamento.
     */
    public abstract static class CasoDeUsoRealizarPagamento {
        protected final String codigoTransacao;
        protected final double valor;
        protected boolean processadoComSucesso;
        protected final LocalDateTime dataHora;

        public CasoDeUsoRealizarPagamento(double valor) {
            this.codigoTransacao = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.valor = valor;
            this.processadoComSucesso = false;
            this.dataHora = LocalDateTime.now();
        }

        public String getCodigoTransacao() { return codigoTransacao; }
        public double getValor() { return valor; }
        public boolean isProcessadoComSucesso() { return processadoComSucesso; }

        // Contrato abstrato que as especializações devem concretizar
        public abstract boolean liquidarTransacao(AtorSecundarioGatewayBancario gateway);

        public void exibirComprovante() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            System.out.printf("   [Recibo de Transação] ID: %s | Data: %s | Valor: R$ %.2f | Status: %s%n",
                    codigoTransacao, dataHora.format(dtf), valor, (processadoComSucesso ? "LIQUIDADO" : "PENDENTE/RECUSADO"));
        }
    }

    /**
     * UC07: Pagar via Cartão de Crédito
     * Relação na UML: UC07 --|> UC06 (Generalização/Especialização)
     */
    public static class UC07PagarViaCartaoCredito extends CasoDeUsoRealizarPagamento {
        private final String numeroCartaoMascarado;
        private final int parcelas;

        public UC07PagarViaCartaoCredito(double valor, String numeroCartao, int parcelas) {
            super(valor);
            // Mascara os dígitos para segurança (exibe apenas os últimos 4)
            this.numeroCartaoMascarado = "****-****-****-" + numeroCartao.substring(numeroCartao.length() - 4);
            this.parcelas = parcelas;
        }

        @Override
        public boolean liquidarTransacao(AtorSecundarioGatewayBancario gateway) {
            System.out.printf("-> Executando Especialização [UC07: Pagar via Cartão de Crédito] em %dx de R$ %.2f%n",
                    parcelas, (valor / parcelas));
            boolean aprovado = gateway.autorizarCartao(numeroCartaoMascarado, valor);
            this.processadoComSucesso = aprovado;
            return aprovado;
        }
    }

    /**
     * UC08: Pagar via PIX
     * Relação na UML: UC08 --|> UC06 (Generalização/Especialização)
     */
    public static class UC08PagarViaPix extends CasoDeUsoRealizarPagamento {
        private final String chavePixDestino;
        private String payloadPixCopiaECola;
        private final int tempoExpiracaoMinutos;

        public UC08PagarViaPix(double valor, String chavePixDestino) {
            super(valor);
            this.chavePixDestino = chavePixDestino;
            this.tempoExpiracaoMinutos = 15; // Regra de negócio do UC08
        }

        @Override
        public boolean liquidarTransacao(AtorSecundarioGatewayBancario gateway) {
            System.out.println("-> Executando Especialização [UC08: Pagar via PIX Instantâneo]");
            this.payloadPixCopiaECola = gateway.gerarCobrancaPix(chavePixDestino, valor);
            System.out.printf("   [QR Code Ativo] Tempo limite para pagamento: %d minutos.%n", tempoExpiracaoMinutos);
            System.out.println("   [Copia e Cola]: " + payloadPixCopiaECola);
            
            // Simula a recepção de webhook bancário confirmando liquidação
            System.out.println("   [Webhook Recebido] Confirmação bancária de transferência via Bacen recebida.");
            this.processadoComSucesso = true;
            return true;
        }
    }

    // =========================================================================
    // PROGRAMA PRINCIPAL
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("UNIFEF - ENGENHARIA DE SOFTWARE II - PROF. WESLEY SOARES");
        System.out.println("GENERALIZAÇÃO DE ATORES E ESPECIALIZAÇÃO DE CASOS DE USO");
        System.out.println("====================================================================\n");

        List<String> catalogoProdutos = new ArrayList<>();
        catalogoProdutos.add("Livro: UML e Padrões - Craig Larman");
        catalogoProdutos.add("Livro: Engenharia de Software - Roger Pressman");

        // Instanciando os atores externos
        AtorCliente clienteComum = new AtorCliente("CLI-100", "Maria da Silva", "maria@gmail.com");
        AtorAdministrador adminEstoque = new AtorAdministrador("ADM-001", "Prof. Wesley Soares", "wesley@unifef.edu.br", "ROOT_MASTER");

        // ---------------------------------------------------------------------
        // 1. DEMONSTRAÇÃO DA GENERALIZAÇÃO ENTRE ATORES
        // ---------------------------------------------------------------------
        System.out.println("1. DEMONSTRAÇÃO DA HERANÇA DE ATORES (Administrador herda de Cliente):");
        
        // Cliente comum executa seus casos de uso autorizados
        clienteComum.executarUC01ConsultarCatalogo(catalogoProdutos);
        clienteComum.executarUC03FinalizarCompra(320.00);

        System.out.println();
        // Administrador executa os casos de uso herdados de Cliente (Polimorfismo comportamental)
        adminEstoque.executarUC01ConsultarCatalogo(catalogoProdutos);
        adminEstoque.executarUC03FinalizarCompra(150.00);

        // Administrador executa caso de uso que APENAS ele possui (UC10)
        adminEstoque.executarUC10ManterCatalogo(catalogoProdutos, "ADICIONAR", "Livro: Writing Effective Use Cases - Alistair Cockburn");
        adminEstoque.executarUC01ConsultarCatalogo(catalogoProdutos);

        // ---------------------------------------------------------------------
        // 2. DEMONSTRAÇÃO DA ESPECIALIZAÇÃO DE CASOS DE USO
        // ---------------------------------------------------------------------
        System.out.println("\n2. DEMONSTRAÇÃO DE ESPECIALIZAÇÃO DE CASOS DE USO (UC06 genérico, UC07 e UC08 concretos):");
        AtorSecundarioGatewayBancario gatewayMock = new AtorSecundarioGatewayBancario();

        // Lista polimórfica que opera sob a abstração do caso de uso base UC06
        List<CasoDeUsoRealizarPagamento> pagamentos = new ArrayList<>();
        pagamentos.add(new UC07PagarViaCartaoCredito(450.00, "4111111111119999", 3));
        pagamentos.add(new UC08PagarViaPix(270.50, "financeiro@unifef.edu.br"));

        for (CasoDeUsoRealizarPagamento ucPagamento : pagamentos) {
            System.out.println("--------------------------------------------------------------------");
            boolean resultado = ucPagamento.liquidarTransacao(gatewayMock);
            System.out.println("Status da Execução: " + (resultado ? "SUCESSO" : "FALHA"));
            ucPagamento.exibirComprovante();
        }
        System.out.println("--------------------------------------------------------------------");
    }
}
