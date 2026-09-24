/*
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Ciclo de Vida e Tipos de Manutencao de Software (ISO/IEC 14764)
 *
 * Como compilar:
 *   javac SimuladorManutencaoSoftware.java
 * Como executar:
 *   java SimuladorManutencaoSoftware
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Item individual integrante de um faturamento.
 */
class ItemFatura {
    private final String descricao;
    private final double valorUnitario;
    private final int quantidade;

    public ItemFatura(String descricao, double valorUnitario, int quantidade) {
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return valorUnitario * quantidade;
    }

    public String getDescricao() {
        return descricao;
    }
}

/**
 * Sistema Central de Faturamento que atravessa as 4 modalidades de manutencao:
 * 1. Corretiva (conserto de bugs em producao)
 * 2. Adaptativa (adequacao a novas leis/ecossistemas)
 * 3. Preventiva (refatoracao proativa e otimizacao)
 * 4. Evolutiva (novas regras de negocio e capacidades)
 */
public class SimuladorManutencaoSoftware {

    // Versao operacional em producao
    private String versao = "1.0.0";

    // Taxa de imposto municipal/estadual original (10%)
    private double aliquotaImposto = 0.10;

    // Flag que indica se o bug de arredondamento de centavos esta ativo
    private boolean bugArredondamentoPresente = true;

    // Parametro introduzido pela Manutencao Adaptativa (Nova Lei Fiscal 2026)
    private boolean conformidadeReformaTributaria2026 = false;

    // Recurso introduzido pela Manutencao Preventiva (Cache de calculo)
    private boolean cachePreventivoAtivo = false;
    private final Map<String, Double> cacheCalculos = new HashMap<>();

    // Recurso introduzido pela Manutencao Evolutiva (Pagamento Pix com Desconto)
    private boolean moduloPixAtivo = false;

    /**
     * Operacao de calculo do valor faturado.
     */
    public double calcularTotalComImposto(ItemFatura item) {
        double subtotal = item.getSubtotal();

        // Uso do cache proativo (Manutencao Preventiva)
        if (cachePreventivoAtivo && cacheCalculos.containsKey(item.getDescricao())) {
            System.out.println("  [MANUTENCAO PREVENTIVA: Cache Hit] Recuperando calculo pre-processado para evitar carga de CPU.");
            return cacheCalculos.get(item.getDescricao());
        }

        double imposto = subtotal * aliquotaImposto;
        double total = subtotal + imposto;

        // Simulacao do Bug em Producao antes da Manutencao Corretiva
        if (bugArredondamentoPresente) {
            // Trunca erroneamente os decimais, gerando inconsistencia fiscal (centavos perdidos)
            total = (long) total; 
        }

        if (cachePreventivoAtivo) {
            cacheCalculos.put(item.getDescricao(), total);
        }

        return total;
    }

    /**
     * MODALIDADE 1: MANUTENCAO CORRETIVA
     * Disparador: Bug de arredondamento reportado pelo setor financeiro em producao.
     * Acao: Diagnosticar e aplicar hotfix para restaurar a integridade dos calculos monetarios.
     */
    public void executarManutencaoCorretiva() {
        System.out.println("\n>>> EXECUTANDO MANUTENCAO CORRETIVA (ISO/IEC 14764) <<<");
        System.out.println("Disparador: Chamado #8912 - Discrepancia no fechamento de centavos em producao.");
        System.out.println("Diagnostico: Casting indevido para (long) eliminava casas decimais.");
        
        // Correcao do erro interno de codigo
        this.bugArredondamentoPresente = false;
        this.versao = "1.0.1-hotfix";
        
        System.out.println("Resultado: Bug suprimido com sucesso. Versao atualizada para: " + this.versao);
    }

    /**
     * MODALIDADE 2: MANUTENCAO ADAPTATIVA
     * Disparador: Nova Portaria da Secretaria de Fazenda exigindo mudanca de aliquota para 15%.
     * Acao: Adaptar o sistema para permanecer operacional perante a nova legislacao externa.
     */
    public void executarManutencaoAdaptativa() {
        System.out.println("\n>>> EXECUTANDO MANUTENCAO ADAPTATIVA (ISO/IEC 14764) <<<");
        System.out.println("Disparador: Publicacao da Lei Fiscal 2026 alterando aliquota padrao de 10% para 15%.");
        System.out.println("Natureza: O software NAO continha erros; a mudanca foi imposta pelo ambiente externo.");

        this.aliquotaImposto = 0.15;
        this.conformidadeReformaTributaria2026 = true;
        this.versao = "1.1.0-adaptativa";
        this.cacheCalculos.clear(); // Invalida cache antigo por mudanca legal

        System.out.println("Resultado: Sistema adaptado a nova aliquota legal de 15%. Versao: " + this.versao);
    }

    /**
     * MODALIDADE 3: MANUTENCAO PREVENTIVA (PERFECTIVA DE MANUTENIBILIDADE)
     * Disparador: Analise proativa de telemetria prevendo gargalo de CPU em datas de alta demanda (Black Friday).
     * Acao: Refatorar o nucleo de calculos introduzindo cache em memoria para evitar falhas futuras.
     */
    public void executarManutencaoPreventiva() {
        System.out.println("\n>>> EXECUTANDO MANUTENCAO PREVENTIVA (ISO/IEC 14764) <<<");
        System.out.println("Disparador: Auditoria proativa de arquitetura (nenhuma falha ocorreu ainda).");
        System.out.println("Acao: Refatoracao para insercao de mecanismo de cache e diminuicao de debito tecnico.");

        this.cachePreventivoAtivo = true;
        this.versao = "1.2.0-preventiva";

        System.out.println("Resultado: Resiliencia aumentada; risco de sobrecarga sob picos de transacao neutralizado. Versao: " + this.versao);
    }

    /**
     * MODALIDADE 4: MANUTENCAO EVOLUTIVA (PERFECTIVA FUNCIONAL)
     * Disparador: Solicitacao da diretoria de negocios para permitir recebimento instantaneo via Pix com 5% de desconto.
     * Acao: Adicionar novas classes, regras e capacidades de negocio ao produto.
     */
    public void executarManutencaoEvolutiva() {
        System.out.println("\n>>> EXECUTANDO MANUTENCAO EVOLUTIVA (ISO/IEC 14764) <<<");
        System.out.println("Disparador: Solicitacao de novos requisitos de mercado (Meio de Pagamento Pix com Desconto).");
        System.out.println("Acao: Expansao de escopo funcional do sistema.");

        this.moduloPixAtivo = true;
        this.versao = "2.0.0-evolutiva";

        System.out.println("Resultado: Modulo de Pagamento Pix incorporado ao sistema. Versao: " + this.versao);
    }

    /**
     * Processamento de pagamento apos a manutencao evolutiva.
     */
    public void processarLiquidacao(double valorBase, boolean optarPorPix) {
        if (!moduloPixAtivo && optarPorPix) {
            System.out.println("[OPERACAO INDISPONIVEL] O modulo de pagamento Pix nao existe nesta versao.");
            return;
        }

        if (moduloPixAtivo && optarPorPix) {
            double valorComDesconto = valorBase * 0.95; // 5% de desconto via Pix
            System.out.printf("  [PAGAMENTO EVOLUIDO - PIX] Desconto de 5%% aplicado. Total a pagar: R$ %.2f (Economia de R$ %.2f)%n",
                    valorComDesconto, (valorBase - valorComDesconto));
        } else {
            System.out.printf("  [PAGAMENTO PADRAO - BOLETO/CARTAO] Total a pagar: R$ %.2f (Sem desconto promocional)%n", valorBase);
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  SIMULADOR DO CICLO DE MANUTENCAO DE SOFTWARE - ISO/IEC 14764");
        System.out.println("========================================================================");

        SimuladorManutencaoSoftware sistema = new SimuladorManutencaoSoftware();
        ItemFatura item = new ItemFatura("Licenca Corporativa UniFEF", 199.90, 1);

        // ---------------------------------------------------------------------
        // ESTADO INICIAL (Versao 1.0.0) - Manifestacao do Bug de Arredondamento
        // ---------------------------------------------------------------------
        System.out.println("\n--- [1] ESTADO INICIAL DO SISTEMA EM PRODUCAO (v1.0.0) ---");
        double totalComBug = sistema.calcularTotalComImposto(item);
        System.out.printf("Subtotal: R$ %.2f | Imposto (10%%): R$ 19,99 | Total Calculado: R$ %.2f (ATENCAO: Erro de centavos!)%n",
                item.getSubtotal(), totalComBug);

        // ---------------------------------------------------------------------
        // INTERVENCAO 1: MANUTENCAO CORRETIVA
        // ---------------------------------------------------------------------
        sistema.executarManutencaoCorretiva();
        double totalCorrigido = sistema.calcularTotalComImposto(item);
        System.out.printf("Pos-Correcao: Subtotal: R$ %.2f | Total Calculado Preciso: R$ %.2f%n",
                item.getSubtotal(), totalCorrigido);

        // ---------------------------------------------------------------------
        // INTERVENCAO 2: MANUTENCAO ADAPTATIVA
        // ---------------------------------------------------------------------
        sistema.executarManutencaoAdaptativa();
        double totalAdaptado = sistema.calcularTotalComImposto(item);
        System.out.printf("Pos-Adaptacao: Aliquota 15%% legal aplicada. Novo Total: R$ %.2f%n", totalAdaptado);

        // ---------------------------------------------------------------------
        // INTERVENCAO 3: MANUTENCAO PREVENTIVA
        // ---------------------------------------------------------------------
        sistema.executarManutencaoPreventiva();
        // Primeira chamada popula o cache
        sistema.calcularTotalComImposto(item);
        // Segunda chamada utiliza o cache preventivo
        sistema.calcularTotalComImposto(item);

        // ---------------------------------------------------------------------
        // INTERVENCAO 4: MANUTENCAO EVOLUTIVA
        // ---------------------------------------------------------------------
        System.out.println("\nTentando pagar com Pix antes da evolucao:");
        sistema.processarLiquidacao(totalAdaptado, true);

        sistema.executarManutencaoEvolutiva();

        System.out.println("Tentando pagar com Pix apos a manutencao evolutiva:");
        sistema.processarLiquidacao(totalAdaptado, true);

        System.out.println("\n========================================================================");
        System.out.println("  CICLO COMPLETO DE MANUTENCAO EXECUTADO COM SUCESSO!");
        System.out.println("========================================================================");
    }
}
