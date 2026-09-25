/*
 * Disciplina: Laboratório de Programação IV (4º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Simulado A1 - Exercício 2: Serviços Transacionais, Repositórios e Rollback
 * Como executar:
 *   javac SimuladoTransacaoService.java
 *   java SimuladoTransacaoService
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class SimuladoTransacaoService {

    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String msg) { super(msg); }
    }

    public static class RecursoDuplicadoException extends RuntimeException {
        public RecursoDuplicadoException(String msg) { super(msg); }
    }

    public static class GrupoProdutoEntidade {
        private Long id;
        private String nome;

        public GrupoProdutoEntidade(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public Long getId() { return id; }
        public String getNome() { return nome; }
    }

    public static class ProdutoEntidade {
        private Long id;
        private String codigoBarras;
        private String descricao;
        private BigDecimal saldoEstoque;
        private BigDecimal valorUnitario;
        private Long grupoId;

        public ProdutoEntidade(Long id, String codigoBarras, String descricao, BigDecimal saldoEstoque, BigDecimal valorUnitario, Long grupoId) {
            this.id = id;
            this.codigoBarras = codigoBarras;
            this.descricao = descricao;
            this.saldoEstoque = saldoEstoque;
            this.valorUnitario = valorUnitario;
            this.grupoId = grupoId;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCodigoBarras() { return codigoBarras; }
        public String getDescricao() { return descricao; }
        public BigDecimal getSaldoEstoque() { return saldoEstoque; }
        public void setSaldoEstoque(BigDecimal saldoEstoque) { this.saldoEstoque = saldoEstoque; }
        public BigDecimal getValorUnitario() { return valorUnitario; }
        public Long getGrupoId() { return grupoId; }
    }

    // Repositório simulando o proxy Spring Data JPA
    public static class ProdutoRepositoryEmMemoria {
        private final Map<Long, ProdutoEntidade> tabela = new HashMap<>();
        private long sequenciaId = 1L;

        public synchronized ProdutoEntidade save(ProdutoEntidade p) {
            if (p.getId() == null) {
                p.setId(sequenciaId++);
            }
            tabela.put(p.getId(), p);
            return p;
        }

        public Optional<ProdutoEntidade> findById(Long id) {
            return Optional.ofNullable(tabela.get(id));
        }

        public boolean existsByCodigoBarras(String codigoBarras) {
            return tabela.values().stream().anyMatch(p -> p.getCodigoBarras().equals(codigoBarras));
        }

        public void deleteById(Long id) {
            tabela.remove(id);
        }

        public Map<Long, ProdutoEntidade> getSnapshot() {
            return new HashMap<>(tabela);
        }

        public void restoreSnapshot(Map<Long, ProdutoEntidade> snapshot) {
            tabela.clear();
            tabela.putAll(snapshot);
        }
    }

    public static class GrupoProdutoRepositoryEmMemoria {
        private final Map<Long, GrupoProdutoEntidade> tabela = new HashMap<>();
        public GrupoProdutoRepositoryEmMemoria() {
            tabela.put(1L, new GrupoProdutoEntidade(1L, "Hardware"));
            tabela.put(2L, new GrupoProdutoEntidade(2L, "Periféricos"));
        }
        public Optional<GrupoProdutoEntidade> findById(Long id) {
            return Optional.ofNullable(tabela.get(id));
        }
    }

    // Serviço de aplicação com fronteira transacional simulada
    public static class ProdutoService {
        private final ProdutoRepositoryEmMemoria produtoRepo;
        private final GrupoProdutoRepositoryEmMemoria grupoRepo;

        public ProdutoService(ProdutoRepositoryEmMemoria produtoRepo, GrupoProdutoRepositoryEmMemoria grupoRepo) {
            this.produtoRepo = produtoRepo;
            this.grupoRepo = grupoRepo;
        }

        // Simulação do @Transactional
        public ProdutoEntidade cadastrarTransacional(ProdutoEntidade novoProduto, Long grupoId) {
            Map<Long, ProdutoEntidade> snapshotAntes = produtoRepo.getSnapshot();
            try {
                if (produtoRepo.existsByCodigoBarras(novoProduto.getCodigoBarras())) {
                    throw new RecursoDuplicadoException("Código de barras já cadastrado: " + novoProduto.getCodigoBarras());
                }
                GrupoProdutoEntidade grupo = grupoRepo.findById(grupoId)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo de produto não encontrado para ID: " + grupoId));

                novoProduto.grupoId = grupo.getId();
                return produtoRepo.save(novoProduto);
            } catch (RuntimeException ex) {
                // Simulação do Rollback automático promovido pelo Spring em Unchecked Exceptions
                produtoRepo.restoreSnapshot(snapshotAntes);
                throw ex;
            }
        }

        // Operação em lote demonstrando rollback atômico
        public void cadastrarLoteTransacional(List<ProdutoEntidade> lote, Long grupoId) {
            Map<Long, ProdutoEntidade> snapshotAntes = produtoRepo.getSnapshot();
            try {
                for (ProdutoEntidade item : lote) {
                    cadastrarTransacional(item, grupoId);
                }
            } catch (RuntimeException ex) {
                System.out.println("     [ROLLBACK ACIONADO] Falha durante o processamento do lote. Revertendo alterações...");
                produtoRepo.restoreSnapshot(snapshotAntes);
                throw ex;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("EXECUÇÃO: SIMULADO A1 - EXERCÍCIO 2: SERVIÇOS E TRANSAÇÃO");
        System.out.println("==========================================================");

        ProdutoRepositoryEmMemoria prodRepo = new ProdutoRepositoryEmMemoria();
        GrupoProdutoRepositoryEmMemoria grupoRepo = new GrupoProdutoRepositoryEmMemoria();
        ProdutoService service = new ProdutoService(prodRepo, grupoRepo);

        // 1. Cadastro com sucesso
        ProdutoEntidade p1 = new ProdutoEntidade(null, "789001", "Monitor 27 Pol", new BigDecimal("5.000"), new BigDecimal("1200.00"), null);
        ProdutoEntidade cadastrado = service.cadastrarTransacional(p1, 1L);
        System.out.println("[OK] Produto 1 cadastrado com sucesso. ID gerado: " + cadastrado.getId());

        // 2. Tentativa de cadastro duplicado (espera RecursoDuplicadoException)
        try {
            ProdutoEntidade p1Duplicado = new ProdutoEntidade(null, "789001", "Monitor Clone", new BigDecimal("2.000"), new BigDecimal("1100.00"), null);
            service.cadastrarTransacional(p1Duplicado, 1L);
            throw new AssertionError("Falha: Permitiu cadastrar produto duplicado!");
        } catch (RecursoDuplicadoException ex) {
            System.out.println("[OK] Validação de duplicidade disparada: " + ex.getMessage());
        }

        // 3. Teste de transação atômica em lote com Rollback
        List<ProdutoEntidade> lote = new ArrayList<>();
        lote.add(new ProdutoEntidade(null, "789002", "Mouse Gamer", new BigDecimal("20.000"), new BigDecimal("80.00"), null));
        lote.add(new ProdutoEntidade(null, "789001", "Teclado Conflitante", new BigDecimal("10.000"), new BigDecimal("150.00"), null)); // código repetido

        System.out.println("\nIniciando cadastro em lote contendo item inválido para verificar atomicidade...");
        try {
            service.cadastrarLoteTransacional(lote, 2L);
            throw new AssertionError("Falha: O lote deveria ter sido rejeitado!");
        } catch (RecursoDuplicadoException ex) {
            System.out.println("[OK] Exceção capturada no lote: " + ex.getMessage());
        }

        // Verifica se o item 789002 foi desfeito (Rollback)
        boolean existe789002 = prodRepo.existsByCodigoBarras("789002");
        System.out.println("     O item válido '789002' está presente no banco? " + existe789002);
        if (existe789002) {
            throw new AssertionError("Falha grave: Rollback falhou! O item do lote não foi descartado.");
        }
        System.out.println("[OK] Atomicidade comprovada: nenhum item do lote foi persistido após o erro.");

        System.out.println("\nTODOS OS TESTES DE SERVIÇO E TRANSAÇÃO FORAM CONCLUÍDOS COM SUCESSO!");
    }
}
