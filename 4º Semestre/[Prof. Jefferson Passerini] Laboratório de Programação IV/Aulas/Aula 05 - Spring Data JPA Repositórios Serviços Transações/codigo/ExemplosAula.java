/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Spring Data JPA, repositories, serviços e transações
 *
 * Como executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
 * Ou diretamente (Java 21+):
 *   java ExemplosAula.java
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExemplosAula {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("UniFEF - Laboratório de Programação IV - Aula 05");
        System.out.println("Tema: Spring Data JPA, Repositories, Serviços e Transações");
        System.out.println("===============================================================\n");

        // 1. Inicialização do ambiente em memória simulando o contexto Spring
        SimuladorEntityManager em = new SimuladorEntityManager();
        GrupoProdutoRepositorySimulado grupoRepo = new GrupoProdutoRepositorySimulado(em);
        FornecedorRepositorySimulado fornecedorRepo = new FornecedorRepositorySimulado(em);
        ProdutoRepositorySimulado produtoRepo = new ProdutoRepositorySimulado(em);

        GrupoProdutoServiceSimulado grupoService = new GrupoProdutoServiceSimulado(grupoRepo);
        FornecedorServiceSimulado fornecedorService = new FornecedorServiceSimulado(fornecedorRepo);
        ProdutoServiceSimulado produtoService = new ProdutoServiceSimulado(produtoRepo, grupoRepo, fornecedorRepo, em);

        // 2. Demonstração: Cadastro de Grupo e Fornecedor através dos Serviços
        System.out.println("--- [1] Cadastro Transacional de Grupo e Fornecedor ---");
        GrupoProduto grupoPerifericos = grupoService.cadastrar("Periféricos");
        System.out.println("Grupo cadastrado com ID: " + grupoPerifericos.getId() + " | Nome: " + grupoPerifericos.getNome());

        Fornecedor fornecedorDell = fornecedorService.cadastrar(new Fornecedor("Dell Computadores do Brasil", "11222333000199"));
        System.out.println("Fornecedor cadastrado com ID: " + fornecedorDell.getId() + " | CNPJ: " + fornecedorDell.getCnpj());

        // 3. Demonstração: Cadastro de Produto com relacionamentos obrigatórios
        System.out.println("\n--- [2] Cadastro de Produto e Validação de Unicidade ---");
        Produto novoProduto = new Produto(
                "7891234567890",
                "Teclado Mecânico RGB",
                new BigDecimal("15.000"),
                new BigDecimal("250.00"),
                new BigDecimal("5.000"),
                LocalDate.now()
        );

        Produto produtoSalvo = produtoService.cadastrar(novoProduto, grupoPerifericos.getId(), fornecedorDell.getId());
        System.out.println("Produto cadastrado: " + produtoSalvo.getDescricao() +
                " | Código: " + produtoSalvo.getCodigoBarras() +
                " | Grupo: " + produtoSalvo.getGrupo().getNome() +
                " | Valor Estoque: R$ " + produtoSalvo.calcularValorEstoque());

        // Teste de duplicidade de código de barras
        try {
            System.out.println("Tentando cadastrar código de barras duplicado...");
            Produto duplicado = new Produto("7891234567890", "Teclado Clone", BigDecimal.TEN, BigDecimal.TEN, LocalDate.now());
            produtoService.cadastrar(duplicado, grupoPerifericos.getId(), fornecedorDell.getId());
        } catch (RecursoDuplicadoException ex) {
            System.out.println("Exceção capturada com sucesso: " + ex.getMessage());
        }

        // 4. Demonstração: Dirty Checking e Ciclo de Vida Managed
        System.out.println("\n--- [3] Demonstração de Dirty Checking (Sem chamada explícita de save) ---");
        System.out.println("Saldo antes da movimentação: " + produtoSalvo.getSaldoEstoque());
        Produto produtoAtualizado = produtoService.receberEstoque(produtoSalvo.getId(), new BigDecimal("10.000"));
        System.out.println("Saldo após receberEstoque via transação: " + produtoAtualizado.getSaldoEstoque());
        System.out.println("Novo valor total em estoque: R$ " + produtoAtualizado.calcularValorEstoque());

        // 5. Demonstração: Rollback Transacional em Falha de Aplicação
        System.out.println("\n--- [4] Demonstração de Transação Atômica e Rollback ---");
        try {
            Produto produtoComErro = new Produto(
                    "9999999999999",
                    "Monitor 27 Polegadas",
                    new BigDecimal("2.000"),
                    new BigDecimal("1200.00"),
                    LocalDate.now()
            );
            System.out.println("Tentando cadastrar com ID de grupo inexistente (ID: 9999L)...");
            produtoService.cadastrar(produtoComErro, 9999L, fornecedorDell.getId());
        } catch (RecursoNaoEncontradoException ex) {
            System.out.println("Falha interceptada: " + ex.getMessage());
            boolean existe = produtoRepo.existsByCodigoBarras("9999999999999");
            System.out.println("O produto com falha foi gravado no banco? " + (existe ? "SIM (Erro de Rollback)" : "NÃO (Rollback executado com sucesso)"));
        }

        System.out.println("\nExecução dos exemplos da Aula 05 finalizada com sucesso!");
    }

    // =========================================================================
    // ENUMERAÇÕES E EXCEÇÕES DE APLICAÇÃO
    // =========================================================================
    public enum Status {
        ATIVO, INATIVO
    }

    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String mensagem) {
            super(mensagem);
        }
    }

    public static class RecursoDuplicadoException extends RuntimeException {
        public RecursoDuplicadoException(String mensagem) {
            super(mensagem);
        }
    }

    // =========================================================================
    // ENTIDADES DO DOMÍNIO
    // =========================================================================
    public static class GrupoProduto {
        private Long id;
        private String nome;
        private Status status;
        private final List<Produto> produtos = new ArrayList<>();

        protected GrupoProduto() {
        }

        public GrupoProduto(String nome) {
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Nome do grupo é obrigatório");
            }
            this.nome = nome.trim();
            this.status = Status.ATIVO;
        }

        public void adicionarProduto(Produto produto) {
            Objects.requireNonNull(produto, "Produto é obrigatório");
            if (!this.produtos.contains(produto)) {
                this.produtos.add(produto);
                produto.associarAo(this);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public Status getStatus() { return status; }
        public List<Produto> getProdutos() { return Collections.unmodifiableList(produtos); }
    }

    public static class Fornecedor {
        private Long id;
        private String razaoSocial;
        private String cnpj;
        private Status status;

        protected Fornecedor() {
        }

        public Fornecedor(String razaoSocial, String cnpj) {
            if (razaoSocial == null || razaoSocial.isBlank()) {
                throw new IllegalArgumentException("Razão social é obrigatória");
            }
            if (cnpj == null || !cnpj.trim().matches("\\d{14}")) {
                throw new IllegalArgumentException("CNPJ deve possuir 14 dígitos numéricos");
            }
            this.razaoSocial = razaoSocial.trim();
            this.cnpj = cnpj.trim();
            this.status = Status.ATIVO;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRazaoSocial() { return razaoSocial; }
        public String getCnpj() { return cnpj; }
        public Status getStatus() { return status; }
    }

    public static class Produto {
        private Long id;
        private String codigoBarras;
        private String descricao;
        private BigDecimal saldoEstoque;
        private BigDecimal valorUnitario;
        private BigDecimal estoqueMinimo;
        private LocalDate dataCadastro;
        private Status status;
        private GrupoProduto grupo;
        private Fornecedor fornecedor;

        protected Produto() {
        }

        public Produto(String codigoBarras, String descricao, BigDecimal saldoEstoque,
                       BigDecimal valorUnitario, LocalDate dataCadastro) {
            this(codigoBarras, descricao, saldoEstoque, valorUnitario, BigDecimal.ZERO, dataCadastro);
        }

        public Produto(String codigoBarras, String descricao, BigDecimal saldoEstoque,
                       BigDecimal valorUnitario, BigDecimal estoqueMinimo, LocalDate dataCadastro) {
            if (codigoBarras == null || codigoBarras.isBlank()) {
                throw new IllegalArgumentException("Código de barras é obrigatório");
            }
            if (descricao == null || descricao.isBlank()) {
                throw new IllegalArgumentException("Descrição é obrigatória");
            }
            if (saldoEstoque == null || saldoEstoque.signum() < 0) {
                throw new IllegalArgumentException("Saldo de estoque não pode ser negativo");
            }
            if (valorUnitario == null || valorUnitario.signum() < 0) {
                throw new IllegalArgumentException("Valor unitário não pode ser negativo");
            }
            if (estoqueMinimo == null || estoqueMinimo.signum() < 0) {
                throw new IllegalArgumentException("Estoque mínimo não pode ser negativo");
            }
            this.codigoBarras = codigoBarras.trim();
            this.descricao = descricao.trim();
            this.saldoEstoque = saldoEstoque;
            this.valorUnitario = valorUnitario;
            this.estoqueMinimo = estoqueMinimo;
            this.dataCadastro = Objects.requireNonNull(dataCadastro, "Data de cadastro é obrigatória");
            this.status = Status.ATIVO;
        }

        public BigDecimal calcularValorEstoque() {
            return saldoEstoque.multiply(valorUnitario).setScale(2, RoundingMode.HALF_UP);
        }

        public void receberEstoque(BigDecimal quantidade) {
            if (quantidade == null || quantidade.signum() <= 0) {
                throw new IllegalArgumentException("Quantidade recebida deve ser maior que zero");
            }
            this.saldoEstoque = this.saldoEstoque.add(quantidade);
        }

        public void retirarEstoque(BigDecimal quantidade) {
            if (quantidade == null || quantidade.signum() <= 0) {
                throw new IllegalArgumentException("Quantidade retirada deve ser maior que zero");
            }
            if (this.saldoEstoque.compareTo(quantidade) < 0) {
                throw new IllegalArgumentException("Saldo de estoque insuficiente");
            }
            this.saldoEstoque = this.saldoEstoque.subtract(quantidade);
        }

        void associarAo(GrupoProduto grupo) {
            Objects.requireNonNull(grupo, "Grupo de produto é obrigatório");
            if (this.grupo != null && this.grupo != grupo) {
                throw new IllegalStateException("Produto já pertence a outro grupo");
            }
            this.grupo = grupo;
        }

        public void associarFornecedor(Fornecedor fornecedor) {
            this.fornecedor = Objects.requireNonNull(fornecedor, "Fornecedor é obrigatório");
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCodigoBarras() { return codigoBarras; }
        public String getDescricao() { return descricao; }
        public BigDecimal getSaldoEstoque() { return saldoEstoque; }
        public BigDecimal getValorUnitario() { return valorUnitario; }
        public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
        public LocalDate getDataCadastro() { return dataCadastro; }
        public Status getStatus() { return status; }
        public GrupoProduto getGrupo() { return grupo; }
        public Fornecedor getFornecedor() { return fornecedor; }
    }

    // =========================================================================
    // SIMULAÇÃO DO CONTEXTO DE PERSISTÊNCIA JPA E TRANSAÇÕES
    // =========================================================================
    public static class SimuladorEntityManager {
        private long sequence = 1;
        private final Map<Long, Object> bancoDados = new ConcurrentHashMap<>();
        private final Map<Long, Object> snapshotManaged = new ConcurrentHashMap<>();
        private boolean emTransacao = false;
        private final List<Runnable> operacoesPendentes = new ArrayList<>();

        public void iniciarTransacao() {
            this.emTransacao = true;
            this.operacoesPendentes.clear();
        }

        public void commit() {
            if (!emTransacao) return;
            for (Runnable op : operacoesPendentes) {
                op.run();
            }
            operacoesPendentes.clear();
            emTransacao = false;
        }

        public void rollback() {
            operacoesPendentes.clear();
            emTransacao = false;
        }

        public <T> T persist(T entidade, Long idAtual, java.util.function.BiConsumer<T, Long> setId) {
            Long id = idAtual;
            if (id == null) {
                id = sequence++;
                setId.accept(entidade, id);
            }
            Long idFinal = id;
            if (emTransacao) {
                operacoesPendentes.add(() -> bancoDados.put(idFinal, entidade));
            } else {
                bancoDados.put(idFinal, entidade);
            }
            snapshotManaged.put(idFinal, entidade);
            return entidade;
        }

        public Map<Long, Object> getBancoDados() {
            return bancoDados;
        }
    }

    // =========================================================================
    // REPOSITORIES SIMULADOS (Consultas derivadas pelo nome)
    // =========================================================================
    public static class GrupoProdutoRepositorySimulado {
        private final SimuladorEntityManager em;

        public GrupoProdutoRepositorySimulado(SimuladorEntityManager em) {
            this.em = em;
        }

        public GrupoProduto save(GrupoProduto grupo) {
            return em.persist(grupo, grupo.getId(), GrupoProduto::setId);
        }

        public Optional<GrupoProduto> findById(Long id) {
            Object obj = em.getBancoDados().get(id);
            if (obj instanceof GrupoProduto gp) {
                return Optional.of(gp);
            }
            return Optional.empty();
        }

        public boolean existsByNomeIgnoreCase(String nome) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof GrupoProduto)
                    .map(o -> (GrupoProduto) o)
                    .anyMatch(gp -> gp.getNome().equalsIgnoreCase(nome));
        }

        public Optional<GrupoProduto> findByNomeIgnoreCase(String nome) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof GrupoProduto)
                    .map(o -> (GrupoProduto) o)
                    .filter(gp -> gp.getNome().equalsIgnoreCase(nome))
                    .findFirst();
        }

        public List<GrupoProduto> findAll() {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof GrupoProduto)
                    .map(o -> (GrupoProduto) o)
                    .toList();
        }
    }

    public static class FornecedorRepositorySimulado {
        private final SimuladorEntityManager em;

        public FornecedorRepositorySimulado(SimuladorEntityManager em) {
            this.em = em;
        }

        public Fornecedor save(Fornecedor f) {
            return em.persist(f, f.getId(), Fornecedor::setId);
        }

        public Optional<Fornecedor> findById(Long id) {
            Object obj = em.getBancoDados().get(id);
            if (obj instanceof Fornecedor f) return Optional.of(f);
            return Optional.empty();
        }

        public boolean existsByCnpj(String cnpj) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof Fornecedor)
                    .map(o -> (Fornecedor) o)
                    .anyMatch(f -> f.getCnpj().equals(cnpj));
        }

        public List<Fornecedor> findAll() {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof Fornecedor)
                    .map(o -> (Fornecedor) o)
                    .toList();
        }
    }

    public static class ProdutoRepositorySimulado {
        private final SimuladorEntityManager em;

        public ProdutoRepositorySimulado(SimuladorEntityManager em) {
            this.em = em;
        }

        public Produto save(Produto p) {
            return em.persist(p, p.getId(), Produto::setId);
        }

        public Optional<Produto> findById(Long id) {
            Object obj = em.getBancoDados().get(id);
            if (obj instanceof Produto p) return Optional.of(p);
            return Optional.empty();
        }

        public boolean existsByCodigoBarras(String codigoBarras) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof Produto)
                    .map(o -> (Produto) o)
                    .anyMatch(p -> p.getCodigoBarras().equals(codigoBarras));
        }

        public Optional<Produto> findByCodigoBarras(String codigoBarras) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof Produto)
                    .map(o -> (Produto) o)
                    .filter(p -> p.getCodigoBarras().equals(codigoBarras))
                    .findFirst();
        }

        public List<Produto> findByGrupoId(Long grupoId) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof Produto)
                    .map(o -> (Produto) o)
                    .filter(p -> p.getGrupo() != null && Objects.equals(p.getGrupo().getId(), grupoId))
                    .toList();
        }

        public List<Produto> findByStatus(Status status) {
            return em.getBancoDados().values().stream()
                    .filter(o -> o instanceof Produto)
                    .map(o -> (Produto) o)
                    .filter(p -> p.getStatus() == status)
                    .toList();
        }
    }

    // =========================================================================
    // CAMADA DE SERVIÇO COM FRONTEIRA TRANSACIONAL
    // =========================================================================
    public static class GrupoProdutoServiceSimulado {
        private final GrupoProdutoRepositorySimulado repository;

        public GrupoProdutoServiceSimulado(GrupoProdutoRepositorySimulado repository) {
            this.repository = repository;
        }

        public GrupoProduto cadastrar(String nome) {
            if (repository.existsByNomeIgnoreCase(nome)) {
                throw new RecursoDuplicadoException("Nome do grupo já cadastrado");
            }
            return repository.save(new GrupoProduto(nome));
        }

        public GrupoProduto buscarPorId(Long id) {
            return repository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo de produto não encontrado"));
        }

        public List<GrupoProduto> listar() {
            return repository.findAll();
        }
    }

    public static class FornecedorServiceSimulado {
        private final FornecedorRepositorySimulado repository;

        public FornecedorServiceSimulado(FornecedorRepositorySimulado repository) {
            this.repository = repository;
        }

        public Fornecedor cadastrar(Fornecedor fornecedor) {
            if (repository.existsByCnpj(fornecedor.getCnpj())) {
                throw new RecursoDuplicadoException("CNPJ já cadastrado");
            }
            return repository.save(fornecedor);
        }

        public Fornecedor buscarPorId(Long id) {
            return repository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
        }
    }

    public static class ProdutoServiceSimulado {
        private final ProdutoRepositorySimulado produtoRepository;
        private final GrupoProdutoRepositorySimulado grupoRepository;
        private final FornecedorRepositorySimulado fornecedorRepository;
        private final SimuladorEntityManager em;

        public ProdutoServiceSimulado(ProdutoRepositorySimulado produtoRepository,
                                      GrupoProdutoRepositorySimulado grupoRepository,
                                      FornecedorRepositorySimulado fornecedorRepository,
                                      SimuladorEntityManager em) {
            this.produtoRepository = produtoRepository;
            this.grupoRepository = grupoRepository;
            this.fornecedorRepository = fornecedorRepository;
            this.em = em;
        }

        // Representa método com @Transactional (unidade atômica com rollback automático em falhas)
        public Produto cadastrar(Produto produto, Long grupoId, Long fornecedorId) {
            em.iniciarTransacao();
            try {
                if (produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
                    throw new RecursoDuplicadoException("Código de barras já cadastrado");
                }

                GrupoProduto grupo = grupoRepository.findById(grupoId)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo de produto não encontrado"));
                grupo.adicionarProduto(produto);

                if (fornecedorId != null) {
                    Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
                    produto.associarFornecedor(fornecedor);
                }

                Produto salvo = produtoRepository.save(produto);
                em.commit();
                return salvo;
            } catch (RuntimeException ex) {
                em.rollback();
                throw ex;
            }
        }

        // Representa dirty checking do JPA: alterações na entidade managed são persistidas no commit
        public Produto receberEstoque(Long id, BigDecimal quantidade) {
            em.iniciarTransacao();
            try {
                Produto produto = produtoRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
                produto.receberEstoque(quantidade);
                // Em JPA real com @Transactional não é necessário chamar repository.save(produto)
                em.commit();
                return produto;
            } catch (RuntimeException ex) {
                em.rollback();
                throw ex;
            }
        }
    }
}
