/*
 * Disciplina: Engenharia de Software II (4º Semestre)
 * Instituição: UniFEF - Centro Universitário de Santa Fé do Sul
 * Professor: Prof. Ms. Wesley Soares de Souza
 * Tema: Implementação de Fluxos de Controle e Processos (Diagrama de Atividades UML)
 *
 * Como executar:
 *   javac OrquestracaoAtividades.java
 *   java OrquestracaoAtividades
 */

import java.util.concurrent.CompletableFuture;

public class OrquestracaoAtividades {

    public static void main(String[] args) throws Exception {
        System.out.println("===========================================================");
        System.out.println("ENGENHARIA DE SOFTWARE II - PROF. WESLEY SOARES");
        System.out.println("ORQUESTRAÇÃO EXECUTÁVEL DE DIAGRAMAS DE ATIVIDADE (UML)");
        System.out.println("===========================================================\n");

        // --------------------------------------------------------------------
        // Exercício 5 (Processo 1): Fluxo de Atividade de Checkout com Fork/Join
        // --------------------------------------------------------------------
        System.out.println("--- [ATIVIDADE 1] Processo de Negócio: Checkout Concorrente ---");
        executarFluxoCheckout("ITEM-ABC", 3, 120.00, true, true);

        System.out.println("\n--- [ATIVIDADE 1 - RAMO DE EXCEÇÃO] Checkout com Falha de Estoque ---");
        executarFluxoCheckout("ITEM-XYZ", 10, 500.00, false, true);

        // --------------------------------------------------------------------
        // Exercício 5 (Processo 2): Fluxo de Atividade de Cancelamento e Devolução
        // --------------------------------------------------------------------
        System.out.println("\n--- [ATIVIDADE 2] Processo de Negócio: Devolução e Conciliação ---");
        executarFluxoDevolucao("DEV-404", 189.90, true, true);

        System.out.println("\n--- [ATIVIDADE 2 - RAMO DE REJEIÇÃO] Devolução com Laudo Recusado ---");
        executarFluxoDevolucao("DEV-405", 89.00, false, false);

        System.out.println("\n===========================================================");
        System.out.println("TODOS OS FLUXOS DE ATIVIDADE FORAM EXECUTADOS COM SUCESSO!");
        System.out.println("===========================================================");
    }

    /**
     * Representa o Diagrama de Atividades de Checkout:
     * Início -> Validar Dados -> [Decisão: Dados OK?]
     *       -> FORK (Bifurcação Concorrente):
     *             Ramo A: Reservar Estoque de Itens
     *             Ramo B: Efetuar Análise Antifraude e Risco de Crédito
     *       -> JOIN (Sincronização de Barreiras)
     *       -> [Decisão: Ambos Ramos com Sucesso?]
     *             [Sim] -> Faturar e Gerar Nota Fiscal -> Fim
     *             [Não] -> Executar Compensação/Rollback -> Fim com Falha
     */
    public static void executarFluxoCheckout(String item, int qtd, double valor, boolean estoqueSuficiente, boolean antifraudeAprovado) throws Exception {
        System.out.println("  [Nó Inicial] Iniciando atividade de checkout...");
        System.out.println("  [Ação] Validando sintaxe e consistência dos parâmetros de entrada...");

        // Decisão inicial
        if (qtd <= 0 || valor <= 0) {
            System.out.println("  [Nó de Decisão: Guard [Inválido]] Parâmetros corrompidos. Encerrando.");
            return;
        }

        System.out.println("  [Nó Fork] Dividindo processamento em duas ações paralelas concorrentes:");

        // Fork Concorrente: Ramo A (Reserva de Estoque)
        CompletableFuture<Boolean> tarefaEstoque = CompletableFuture.supplyAsync(() -> {
            System.out.println("    -> [Ramo Paralelo A: Estoque] Verificando disponibilidade física do item " + item);
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            if (estoqueSuficiente) {
                System.out.println("    -> [Ramo Paralelo A: Estoque] Reserva física efetuada com sucesso!");
                return true;
            } else {
                System.out.println("    -> [Ramo Paralelo A: Estoque] Saldo insuficiente no armazém!");
                return false;
            }
        });

        // Fork Concorrente: Ramo B (Antifraude e Compliance Financeiro)
        CompletableFuture<Boolean> tarefaAntifraude = CompletableFuture.supplyAsync(() -> {
            System.out.println("    -> [Ramo Paralelo B: Compliance] Consultando score de risco antifraude...");
            try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            if (antifraudeAprovado) {
                System.out.println("    -> [Ramo Paralelo B: Compliance] Análise de crédito aprovada sem restrições!");
                return true;
            } else {
                System.out.println("    -> [Ramo Paralelo B: Compliance] Transação sinalizada como alto risco!");
                return false;
            }
        });

        // Join: Sincronização
        CompletableFuture.allOf(tarefaEstoque, tarefaAntifraude).join();
        System.out.println("  [Nó Join] Ramos concorrentes sincronizados na barreira de processamento.");

        boolean resEstoque = tarefaEstoque.get();
        boolean resAntifraude = tarefaAntifraude.get();

        // Nó de Decisão / Mesclagem
        if (resEstoque && resAntifraude) {
            System.out.println("  [Nó de Decisão: Guard [Aprovado]] Todos os pré-requisitos satisfeitos.");
            System.out.println("  [Ação] Emitindo Nota Fiscal Eletrônica e gerando código de rastreio...");
            System.out.println("  [Nó Final] Fluxo concluído com êxito transacional.");
        } else {
            System.out.println("  [Nó de Decisão: Guard [Falha]] Acionando compensação e estorno de reservas parciais...");
            System.out.println("  [Ação] Liberando travas de estoque e notificando cliente sobre recusa...");
            System.out.println("  [Nó Final] Fluxo encerrado com reversão.");
        }
    }

    /**
     * Representa o Diagrama de Atividades de Devolução e Conciliação:
     * Início -> Receber Produto Devolvido -> Inspecionar Integridade Física
     *       -> [Decisão: Avariado pelo Cliente?]
     *             [Sim] -> Rejeitar e Notificar Cliente -> Fim
     *             [Não] -> [Decisão: Devolver Dinheiro ou Vale-Compras?]
     *                         [Dinheiro] -> Disparar Estorno em Conta Bancária -> Mesclar
     *                         [Vale]     -> Creditar Cupom na Carteira Digital -> Mesclar
     *       -> Mesclar -> Repor Item no Estoque -> Fim
     */
    public static void executarFluxoDevolucao(String protocolo, double valor, boolean itemIntegro, boolean optarPorEstorno) {
        System.out.println("  [Nó Inicial] Protocolo de devolução aberto: " + protocolo);
        System.out.println("  [Ação] Vistoria técnica inspeciona conformidade da mercadoria devolvida...");

        // Decisão de Qualidade
        if (!itemIntegro) {
            System.out.println("  [Nó de Decisão: Guard [Produto Danificado]] Mau uso constatado no laudo.");
            System.out.println("  [Ação] Emitir termo formal de recusa e reenviar mercadoria ao remetente...");
            System.out.println("  [Nó Final] Processo de devolução finalizado como INDEFERIDO.");
            return;
        }

        System.out.println("  [Nó de Decisão: Guard [Item Conforme]] Mercadoria atende requisitos de devolução.");

        // Decisão de Modalidade de Ressarcimento
        if (optarPorEstorno) {
            System.out.println("  [Ramo Ação] Executando chamada à adquirente para estorno bancário de R$ " + valor);
        } else {
            System.out.println("  [Ramo Ação] Gerando cupom de crédito na carteira virtual de R$ " + valor);
        }

        System.out.println("  [Nó de Mesclagem (Merge)] Reintegrando fluxos de compensação.");
        System.out.println("  [Ação] Atualizando inventário e repondo item no saldo de prateleira.");
        System.out.println("  [Nó Final] Processo de devolução e conciliação homologado com sucesso.");
    }
}
