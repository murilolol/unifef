/**
 * Disciplina: Engenharia de Software II - UniFEF
 * Professor: Wesley Soares
 * Tema: Modelagem de Requisitos, Casos de Uso e Diagrama de Classes (Food Delivery)
 *
 * Arquivo: SimulacaoGovernancaAdministrador.java
 * Descricao: Implementacao executavel dos Casos de Uso sob a visao do Administrador:
 *            - "Visualizar Desempenho dos Restaurantes" (UC13 e RF-04: analise de faturamento e comissao)
 *            - "Moderar Avaliacoes" (UC12: expurgo de linguagem impropria ou ofensiva)
 *            - "Gerenciar Contas de Usuarios" (UC11: auditoria e suspensao de parceiros irregulares)
 *            Demonstrando o tratamento do Fluxo Alternativo FA-01 do Administrador,
 *            com deteccao proativa de estabelecimentos com indice de cancelamento > 10%.
 *
 * Como compilar e executar:
 *   javac SimulacaoGovernancaAdministrador.java
 *   java SimulacaoGovernancaAdministrador
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SimulacaoGovernancaAdministrador {

    // =========================================================================
    // ENUMERACOES DE AUDITORIA E GOVERNANCA
    // =========================================================================

    public enum StatusConta {
        ATIVA,
        EM_AUDITORIA,
        SUSPENSA
    }

    public enum StatusModeracao {
        PENDENTE,
        APROVADA,
        REJEITADA_CONTEUDO_OFENSIVO
    }

    // =========================================================================
    // ENTIDADES DO DOMINIO ADMINISTRATIVO
    // =========================================================================

    public static class Avaliacao {
        private final int id;
        private final String clienteNome;
        private final String restauranteNome;
        private final int nota; // 1 a 5 estrelas
        private final String comentario;
        private StatusModeracao status;
        private String justificativaModeracao;

        public Avaliacao(int id, String clienteNome, String restauranteNome, int nota, String comentario) {
            if (nota < 1 || nota > 5) {
                throw new IllegalArgumentException("A nota deve estar estritamente entre 1 e 5 estrelas.");
            }
            this.id = id;
            this.clienteNome = clienteNome;
            this.restauranteNome = restauranteNome;
            this.nota = nota;
            this.comentario = comentario;
            this.status = StatusModeracao.PENDENTE;
        }

        public int getId() { return id; }
        public String getClienteNome() { return clienteNome; }
        public String getRestauranteNome() { return restauranteNome; }
        public int getNota() { return nota; }
        public String getComentario() { return comentario; }
        public StatusModeracao getStatus() { return status; }

        public void aprovar() {
            this.status = StatusModeracao.APROVADA;
            this.justificativaModeracao = "Conteudo adequado aos termos de uso da comunidade.";
        }

        public void rejeitar(String justificativa) {
            this.status = StatusModeracao.REJEITADA_CONTEUDO_OFENSIVO;
            this.justificativaModeracao = justificativa;
        }

        public String getJustificativaModeracao() {
            return justificativaModeracao;
        }
    }

    public static class DadosOperacionaisRestaurante {
        private final int id;
        private final String nomeFantasia;
        private final String cnpj;
        private StatusConta statusConta;
        private int totalPedidosRecebidos;
        private int pedidosEntregues;
        private int pedidosCancelados;
        private double faturamentoBruto;
        private final double percentualComissao; // Ex: 0.12 para 12%

        public DadosOperacionaisRestaurante(int id, String nomeFantasia, String cnpj, double percentualComissao) {
            this.id = id;
            this.nomeFantasia = nomeFantasia;
            this.cnpj = cnpj;
            this.statusConta = StatusConta.ATIVA;
            this.percentualComissao = percentualComissao;
        }

        public void registrarPedidoConcluido(double valor) {
            this.totalPedidosRecebidos++;
            this.pedidosEntregues++;
            this.faturamentoBruto += valor;
        }

        public void registrarPedidoCancelado() {
            this.totalPedidosRecebidos++;
            this.pedidosCancelados++;
        }

        public double calcularTaxaCancelamento() {
            if (totalPedidosRecebidos == 0) return 0.0;
            return ((double) pedidosCancelados / totalPedidosRecebidos) * 100.0;
        }

        public double calcularComissaoDevida() {
            return this.faturamentoBruto * this.percentualComissao;
        }

        public double calcularRepasseLiquido() {
            return this.faturamentoBruto - calcularComissaoDevida();
        }

        public int getId() { return id; }
        public String getNomeFantasia() { return nomeFantasia; }
        public String getCnpj() { return cnpj; }
        public StatusConta getStatusConta() { return statusConta; }
        public void setStatusConta(StatusConta status) { this.statusConta = status; }
        public int getTotalPedidosRecebidos() { return totalPedidosRecebidos; }
        public int getPedidosEntregues() { return pedidosEntregues; }
        public int getPedidosCancelados() { return pedidosCancelados; }
        public double getFaturamentoBruto() { return faturamentoBruto; }
    }

    // =========================================================================
    // MODULOS DE SERVICO DO ADMINISTRADOR
    // =========================================================================

    public static class ModuloModeracao {
        private static final List<String> PALAVRAS_OFENSIVAS =
                Arrays.asList("golpe", "fraude", "ladrao", "sujo", "lixo", "idiota");

        public static void analisarEModerar(Avaliacao avaliacao, boolean intervencaoManualForcarAprovacao) {
            String textoMinusculo = avaliacao.getComentario().toLowerCase();
            boolean contemOfensa = false;

            for (String palavraProibida : PALAVRAS_OFENSIVAS) {
                if (textoMinusculo.contains(palavraProibida)) {
                    contemOfensa = true;
                    break;
                }
            }

            if (contemOfensa && !intervencaoManualForcarAprovacao) {
                avaliacao.rejeitar("Detectada presenca de termos que violam as diretrizes de respeito mutuo.");
                System.out.println("   [MODERACAO: REPROVADA] Avaliacao #" + avaliacao.getId() +
                        " ocultada do publico. Motivo: Termos ofensivos.");
            } else {
                avaliacao.aprovar();
                System.out.println("   [MODERACAO: APROVADA] Avaliacao #" + avaliacao.getId() +
                        " liberada na pagina de " + avaliacao.getRestauranteNome());
            }
        }
    }

    public static class ModuloAuditoriaEAnalise {

        // RF-04 / UC13: Consolidacao de Relatorio Analitico e Deteccao de Parceiros Criticos
        public static void gerarRelatorioConsolidado(List<DadosOperacionaisRestaurante> restaurantes) {
            Locale.setDefault(Locale.US);
            System.out.println("\n==============================================================================");
            System.out.println("RELATORIO ANALITICO DE DESEMPENHO E COMISSOES DA PLATAFORMA (RF-04)");
            System.out.println("Data de Emissao: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("==============================================================================");
            System.out.printf("%-22s | %-10s | %-12s | %-10s | %-12s | %-10s%n",
                    "Restaurante", "Recebidos", "Entregues", "Cancelados", "Fat. Bruto", "Taxa Canc.");
            System.out.println("------------------------------------------------------------------------------");

            double faturamentoTotalRede = 0.0;
            double comissaoTotalPlataforma = 0.0;
            int totalPedidosGeral = 0;

            List<DadosOperacionaisRestaurante> restaurantesCriticos = new ArrayList<>();

            for (DadosOperacionaisRestaurante r : restaurantes) {
                double taxaCanc = r.calcularTaxaCancelamento();
                faturamentoTotalRede += r.getFaturamentoBruto();
                comissaoTotalPlataforma += r.calcularComissaoDevida();
                totalPedidosGeral += r.getTotalPedidosRecebidos();

                System.out.printf("%-22s | %-10d | %-12d | %-10d | R$ %-9.2f | %-5.1f%%%n",
                        r.getNomeFantasia(),
                        r.getTotalPedidosRecebidos(),
                        r.getPedidosEntregues(),
                        r.getPedidosCancelados(),
                        r.getFaturamentoBruto(),
                        taxaCanc
                );

                // Fluxo Alternativo FA-01 do Administrador: Flag de parceiro critico (> 10% de cancelamento)
                if (taxaCanc > 10.0 && r.getTotalPedidosRecebidos() >= 5) {
                    restaurantesCriticos.add(r);
                }
            }

            System.out.println("------------------------------------------------------------------------------");
            double ticketMedio = (totalPedidosGeral > 0) ? (faturamentoTotalRede / totalPedidosGeral) : 0.0;

            System.out.printf("Volume Total Transacionado:      R$ %.2f%n", faturamentoTotalRede);
            System.out.printf("Receita de Comissao da Plataforma: R$ %.2f%n", comissaoTotalPlataforma);
            System.out.printf("Ticket Medio Global:              R$ %.2f%n", ticketMedio);
            System.out.println("==============================================================================");

            // Tratamento do Fluxo Alternativo: Inspecao e Acao Disciplinar
            if (!restaurantesCriticos.isEmpty()) {
                System.out.println("\n[ALERTA DE CONFORMIDADE - FA-01] PARCEIROS COM TAXA DE CANCELAMENTO CRITICA (>10%):");
                for (DadosOperacionaisRestaurante critico : restaurantesCriticos) {
                    System.out.printf("-> AVISO: %s (CNPJ: %s) possui %.1f%% de pedidos cancelados!%n",
                            critico.getNomeFantasia(), critico.getCnpj(), critico.calcularTaxaCancelamento());
                    
                    // Acao Administrativa (UC11: Gerenciar Contas de Usuarios)
                    critico.setStatusConta(StatusConta.EM_AUDITORIA);
                    System.out.println("   [ACAO DO ADMINISTRADOR] Conta colocada em: " + critico.getStatusConta() +
                            " para averiguação da vigilancia operacional.");
                }
            }
        }
    }

    // =========================================================================
    // METODO PRINCIPAL DE SIMULACAO
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("ENGENHARIA DE SOFTWARE II - DEMONSTRACAO: GOVERNANCA ADMINISTRATIVA");
        System.out.println("Professor: Wesley Soares | Tema: Moderacao, Auditoria e Relatorios (MoSCoW)");
        System.out.println("====================================================================");

        // ---------------------------------------------------------------------
        // 1. DEMONSTRACAO DO CASO DE USO UC12: MODERAR AVALIACOES
        // ---------------------------------------------------------------------
        System.out.println("\n--- 1. PROCESSAMENTO DE MODERACAO DE AVALIACOES (UC12) ---");

        Avaliacao av1 = new Avaliacao(801, "Renata Lima", "Cantina Napolitana", 5,
                "Comida excelente, entrega super rapida e motoboy educado!");

        Avaliacao av2 = new Avaliacao(802, "Pedro Alvares", "Sushi Express", 1,
                "O restaurante cometeu uma fraude no pedido e os atendentes sao um bando de idiotas!");

        ModuloModeracao.analisarEModerar(av1, false);
        ModuloModeracao.analisarEModerar(av2, false);

        System.out.println("Justificativa da avaliacao #802 arquivada em auditoria: \"" +
                av2.getJustificativaModeracao() + "\"");

        // ---------------------------------------------------------------------
        // 2. DEMONSTRACAO DO CASO DE USO UC13 / RF-04: CONSOLIDACAO FINANCEIRA
        // ---------------------------------------------------------------------
        System.out.println("\n--- 2. CARGA DE DADOS DE VENDAS E CANCELAMENTOS DE PARCEIROS ---");

        DadosOperacionaisRestaurante r1 = new DadosOperacionaisRestaurante(
                1, "Churrascaria Boi Bravo", "11.222.333/0001-10", 0.10 // 10% comissao
        );
        DadosOperacionaisRestaurante r2 = new DadosOperacionaisRestaurante(
                2, "Taverna dos Burgers", "44.555.666/0001-20", 0.12 // 12% comissao
        );
        DadosOperacionaisRestaurante r3 = new DadosOperacionaisRestaurante(
                3, "Wok Sabores da Asia", "77.888.999/0001-30", 0.12 // 12% comissao (Critico)
        );

        // Populando restaurante 1 (Regular)
        r1.registrarPedidoConcluido(120.00);
        r1.registrarPedidoConcluido(95.00);
        r1.registrarPedidoConcluido(210.00);
        r1.registrarPedidoConcluido(140.00);
        r1.registrarPedidoConcluido(85.00);
        r1.registrarPedidoCancelado(); // 1 cancelamento em 6 (16.6%, mas apenas 1 cancelamento)

        // Populando restaurante 2 (Excelente conformidade)
        for (int i = 0; i < 15; i++) {
            r2.registrarPedidoConcluido(50.00);
        }

        // Populando restaurante 3 (Critico: muitos cancelamentos no meio do expediente)
        r3.registrarPedidoConcluido(60.00);
        r3.registrarPedidoConcluido(75.00);
        r3.registrarPedidoConcluido(80.00);
        r3.registrarPedidoCancelado();
        r3.registrarPedidoCancelado();
        r3.registrarPedidoCancelado(); // 3 cancelamentos em 6 pedidos (50% de cancelamento!)

        List<DadosOperacionaisRestaurante> listaRestaurantes = Arrays.asList(r1, r2, r3);

        // Execucao da consolidacao de relatorio analitico
        ModuloAuditoriaEAnalise.gerarRelatorioConsolidado(listaRestaurantes);

        System.out.println("\n====================================================================");
        System.out.println("EXECUCAO DAS ROTINAS DE GOVERNANCA CONCLUIDA COM SUCESSO");
        System.out.println("====================================================================");
    }
}
