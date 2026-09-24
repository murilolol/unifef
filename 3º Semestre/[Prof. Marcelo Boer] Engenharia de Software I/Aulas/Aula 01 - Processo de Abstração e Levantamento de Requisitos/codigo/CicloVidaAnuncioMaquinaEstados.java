/*
 * Disciplina: Engenharia de Software I
 * Professor: Marcelo Boer (UniFEF)
 * Tema: Modelagem de Estados e Regras de Negócio do Anúncio (Exercício 4)
 *
 * Como compilar:
 *   javac CicloVidaAnuncioMaquinaEstados.java
 * Como executar:
 *   java CicloVidaAnuncioMaquinaEstados
 *
 * Conceitos da aula demonstrados neste arquivo:
 *   1. Ciclo de vida da entidade Anuncio: RASCUNHO -> ATIVO -> PAUSADO -> VENDIDO / CANCELADO.
 *   2. Condições de Guarda (Guard Conditions): Validação de regras de negócio antes de autorizar
 *      uma transição de estado (ex.: anúncio só pode ser publicado com fotos e preço válido).
 *   3. Bloqueio de Transições Ilegais: Proteção contra modificações em anúncios já finalizados.
 *   4. Rastreabilidade Temporal: Log de auditoria registrando cada mudança de estado.
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CicloVidaAnuncioMaquinaEstados {

    // -------------------------------------------------------------------------
    // Estados possíveis de um anúncio (Conforme diagrama de estados UML da aula)
    // -------------------------------------------------------------------------
    public enum EstadoAnuncio {
        RASCUNHO("Rascunho - Edição inicial em andamento, invisível nas buscas"),
        ATIVO("Ativo - Disponível para consulta pública no catálogo da região"),
        PAUSADO("Pausado - Oculto temporariamente pelo anunciante para negociação"),
        VENDIDO("Vendido - Transação concluída com sucesso, mantido para histórico"),
        CANCELADO("Cancelado - Excluído ou invalidado permanentemente");

        private final String descricao;

        EstadoAnuncio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // -------------------------------------------------------------------------
    // Exceção de Negócio para Transições Inválidas ou Violação de Condições de Guarda
    // -------------------------------------------------------------------------
    public static class TransicaoInvalidaException extends IllegalStateException {
        public TransicaoInvalidaException(String mensagem) {
            super(mensagem);
        }
    }

    // -------------------------------------------------------------------------
    // Registro de Auditoria de Mudança de Estado
    // -------------------------------------------------------------------------
    public static class RegistroMudancaEstado {
        private final LocalDateTime dataHora;
        private final EstadoAnuncio estadoAnterior;
        private final EstadoAnuncio estadoNovo;
        private final String justificativa;

        public RegistroMudancaEstado(EstadoAnuncio estadoAnterior, EstadoAnuncio estadoNovo, String justificativa) {
            this.dataHora = LocalDateTime.now();
            this.estadoAnterior = estadoAnterior;
            this.estadoNovo = estadoNovo;
            this.justificativa = justificativa;
        }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return String.format("[%s] %s -> %s (Motivo: %s)",
                    dataHora.format(fmt), estadoAnterior, estadoNovo, justificativa);
        }
    }

    // -------------------------------------------------------------------------
    // Entidade Anúncio com Máquina de Estados Embutida
    // -------------------------------------------------------------------------
    public static class AnuncioControlado {
        private final int id;
        private final int vendedorId;
        private String titulo;
        private String descricao;
        private BigDecimal preco;
        private String categoria;
        private final List<String> fotosUrls;
        private EstadoAnuncio estadoAtual;
        private final List<RegistroMudancaEstado> historicoMudancas;
        private LocalDateTime dataPublicacao;
        private LocalDateTime dataFinalizacao;

        public AnuncioControlado(int id, int vendedorId, String titulo, String descricao,
                                 String categoria) {
            this.id = id;
            this.vendedorId = vendedorId;
            this.titulo = titulo;
            this.descricao = descricao;
            this.categoria = categoria;
            this.preco = BigDecimal.ZERO;
            this.fotosUrls = new ArrayList<>();
            this.estadoAtual = EstadoAnuncio.RASCUNHO; // Estado inicial obrigatório
            this.historicoMudancas = new ArrayList<>();
            this.dataPublicacao = null;
            this.dataFinalizacao = null;

            registrarLog(null, EstadoAnuncio.RASCUNHO, "Criação inicial do rascunho");
        }

        private void registrarLog(EstadoAnuncio anterior, EstadoAnuncio novo, String motivo) {
            historicoMudancas.add(new RegistroMudancaEstado(anterior, novo, motivo));
        }

        // Métodos de edição durante a fase de Rascunho
        public void setPreco(BigDecimal preco) {
            if (estadoAtual == EstadoAnuncio.VENDIDO || estadoAtual == EstadoAnuncio.CANCELADO) {
                throw new TransicaoInvalidaException("Não é permitido alterar preço de anúncio finalizado.");
            }
            this.preco = preco;
        }

        public void adicionarFoto(String urlFoto) {
            if (estadoAtual == EstadoAnuncio.VENDIDO || estadoAtual == EstadoAnuncio.CANCELADO) {
                throw new TransicaoInvalidaException("Não é permitido adicionar fotos a um anúncio finalizado.");
            }
            this.fotosUrls.add(urlFoto);
        }

        // ---------------------------------------------------------------------
        // Transição 1: RASCUNHO -> ATIVO (Publicação)
        // Condição de guarda: Título não vazio, pelo menos 1 foto e preço > 0
        // ---------------------------------------------------------------------
        public void publicar() {
            if (estadoAtual != EstadoAnuncio.RASCUNHO) {
                throw new TransicaoInvalidaException("Apenas anúncios no estado RASCUNHO podem ser publicados. Estado atual: " + estadoAtual);
            }

            // Verificação das Condições de Guarda
            if (titulo == null || titulo.trim().isEmpty()) {
                throw new TransicaoInvalidaException("Condição de guarda violada: O anúncio deve conter um título preenchido.");
            }
            if (fotosUrls.isEmpty()) {
                throw new TransicaoInvalidaException("Condição de guarda violada: É obrigatório incluir pelo menos 1 fotografia do produto.");
            }
            if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new TransicaoInvalidaException("Condição de guarda violada: O preço deve ser um valor monetário positivo maior que zero.");
            }

            EstadoAnuncio anterior = this.estadoAtual;
            this.estadoAtual = EstadoAnuncio.ATIVO;
            this.dataPublicacao = LocalDateTime.now();
            registrarLog(anterior, EstadoAnuncio.ATIVO, "Requisitos validados e anúncio publicado no catálogo");
        }

        // ---------------------------------------------------------------------
        // Transição 2: ATIVO -> PAUSADO
        // ---------------------------------------------------------------------
        public void pausar(String motivo) {
            if (estadoAtual != EstadoAnuncio.ATIVO) {
                throw new TransicaoInvalidaException("Apenas anúncios ATIVOS podem ser pausados. Estado atual: " + estadoAtual);
            }
            EstadoAnuncio anterior = this.estadoAtual;
            this.estadoAtual = EstadoAnuncio.PAUSADO;
            registrarLog(anterior, EstadoAnuncio.PAUSADO, motivo);
        }

        // ---------------------------------------------------------------------
        // Transição 3: PAUSADO -> ATIVO (Reativação)
        // ---------------------------------------------------------------------
        public void reativar() {
            if (estadoAtual != EstadoAnuncio.PAUSADO) {
                throw new TransicaoInvalidaException("Apenas anúncios PAUSADOS podem ser reativados. Estado atual: " + estadoAtual);
            }
            EstadoAnuncio anterior = this.estadoAtual;
            this.estadoAtual = EstadoAnuncio.ATIVO;
            registrarLog(anterior, EstadoAnuncio.ATIVO, "Anúncio reativado pelo vendedor e retornado às buscas");
        }

        // ---------------------------------------------------------------------
        // Transição 4: (ATIVO ou PAUSADO) -> VENDIDO (Estado Final Positivo)
        // ---------------------------------------------------------------------
        public void marcarComoVendido(String compradorIdentificador, BigDecimal valorFinalAcordado) {
            if (estadoAtual != EstadoAnuncio.ATIVO && estadoAtual != EstadoAnuncio.PAUSADO) {
                throw new TransicaoInvalidaException("Apenas anúncios ATIVOS ou PAUSADOS podem ser marcados como vendidos. Estado atual: " + estadoAtual);
            }
            EstadoAnuncio anterior = this.estadoAtual;
            this.estadoAtual = EstadoAnuncio.VENDIDO;
            this.dataFinalizacao = LocalDateTime.now();
            registrarLog(anterior, EstadoAnuncio.VENDIDO,
                    String.format("Negociação fechada com comprador '%s' por R$ %.2f", compradorIdentificador, valorFinalAcordado));
        }

        // ---------------------------------------------------------------------
        // Transição 5: Qualquer estado não terminal -> CANCELADO (Estado Final Negativo)
        // ---------------------------------------------------------------------
        public void cancelar(String motivo) {
            if (estadoAtual == EstadoAnuncio.VENDIDO) {
                throw new TransicaoInvalidaException("Anúncio já consolidado como VENDIDO não pode ser cancelado.");
            }
            if (estadoAtual == EstadoAnuncio.CANCELADO) {
                throw new TransicaoInvalidaException("O anúncio já se encontra cancelado.");
            }
            EstadoAnuncio anterior = this.estadoAtual;
            this.estadoAtual = EstadoAnuncio.CANCELADO;
            this.dataFinalizacao = LocalDateTime.now();
            registrarLog(anterior, EstadoAnuncio.CANCELADO, motivo);
        }

        public EstadoAnuncio getEstadoAtual() { return estadoAtual; }
        public List<RegistroMudancaEstado> getHistoricoMudancas() { return Collections.unmodifiableList(historicoMudancas); }

        public void exibirRelatorioAuditoria() {
            System.out.println("------------------------------------------------------------------------");
            System.out.printf("Histórico de Transições do Anúncio #%d ('%s')%n", id, titulo);
            System.out.println("Estado Final: " + estadoAtual + " (" + estadoAtual.getDescricao() + ")");
            System.out.println("Linha do tempo de transições:");
            for (RegistroMudancaEstado log : historicoMudancas) {
                System.out.println("  -> " + log);
            }
            System.out.println("------------------------------------------------------------------------\n");
        }
    }

    // -------------------------------------------------------------------------
    // Demonstração Prática da Máquina de Estados e das Condições de Guarda
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE I - MÁQUINA DE ESTADOS DO ANÚNCIO (EXERCÍCIO 4)");
        System.out.println("========================================================================\n");

        AnuncioControlado anuncio = new AnuncioControlado(
                801,
                101,
                "Bicicleta Aro 29 Mountain Bike 21V",
                "Bicicleta semi-nova com freios a disco e pneus em ótimo estado.",
                "Esportes e Lazer"
        );

        System.out.println("[CENÁRIO 1] Teste de violação de condição de guarda ao tentar publicar rascunho incompleto:");
        try {
            // Tentativa de publicação sem fotos e sem preço definido
            anuncio.publicar();
        } catch (TransicaoInvalidaException e) {
            System.out.println("  [BLOQUEIO ESPERADO] " + e.getMessage());
        }

        System.out.println("\n[CENÁRIO 2] Preenchimento dos requisitos obrigatórios e publicação legítima:");
        anuncio.setPreco(new BigDecimal("850.00"));
        anuncio.adicionarFoto("https://cdn.desapegaja.com/bike_lateral.jpg");
        anuncio.adicionarFoto("https://cdn.desapegaja.com/bike_marchas.jpg");
        anuncio.publicar();
        System.out.println("  Transição bem-sucedida! Estado atual: " + anuncio.getEstadoAtual());

        System.out.println("\n[CENÁRIO 3] Vendedor recebe mensagem no WhatsApp e pausa anúncio durante negociação:");
        anuncio.pausar("Comprador interessado agendou visita para ver a bicicleta hoje às 18h.");
        System.out.println("  Transição bem-sucedida! Estado atual: " + anuncio.getEstadoAtual());

        System.out.println("\n[CENÁRIO 4] Comprador não comparece; anunciante reativa o anúncio para o público:");
        anuncio.reativar();
        System.out.println("  Transição bem-sucedida! Estado atual: " + anuncio.getEstadoAtual());

        System.out.println("\n[CENÁRIO 5] Novo comprador fecha a transação; anúncio transita para VENDIDO (Estado Final):");
        anuncio.marcarComoVendido("Ana Beatriz (ID 102)", new BigDecimal("800.00"));
        System.out.println("  Transição bem-sucedida! Estado atual: " + anuncio.getEstadoAtual());

        System.out.println("\n[CENÁRIO 6] Tentativa ilegal de reativar ou alterar anúncio já consolidado como VENDIDO:");
        try {
            anuncio.reativar();
        } catch (TransicaoInvalidaException e) {
            System.out.println("  [BLOQUEIO ESPERADO] " + e.getMessage());
        }

        try {
            anuncio.setPreco(new BigDecimal("999.00"));
        } catch (TransicaoInvalidaException e) {
            System.out.println("  [BLOQUEIO ESPERADO] " + e.getMessage());
        }

        // Exibição do relatório de auditoria e linha do tempo
        System.out.println();
        anuncio.exibirRelatorioAuditoria();

        System.out.println("[CENÁRIO 7] Criação de segundo anúncio que é cancelado diretamente pelo usuário:");
        AnuncioControlado anuncioDesistencia = new AnuncioControlado(
                802,
                102,
                "Monitor 24 Polegadas Full HD",
                "Monitor com pequeno arranhão na base.",
                "Eletrônicos"
        );
        anuncioDesistencia.cancelar("Vendedor desistiu de vender o equipamento.");
        anuncioDesistencia.exibirRelatorioAuditoria();
    }
}
