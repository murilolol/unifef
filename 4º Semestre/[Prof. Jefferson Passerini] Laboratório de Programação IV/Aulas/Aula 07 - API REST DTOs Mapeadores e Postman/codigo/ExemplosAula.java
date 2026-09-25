/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: API REST, DTOs, Mapeadores e Testes com Postman (Aula 07)
 *
 * Como compilar e executar:
 * javac ExemplosAula.java
 * java ExemplosAula
 * (Compatível com Java 21+)
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class ExemplosAula {

    // =========================================================================
    // 1. DOMÍNIO (Domain)
    // =========================================================================

    public enum Status {
        ATIVO, INATIVO
    }

    public static class GrupoProduto {
        private Long id;
        private String nome;
        private Status status;
        private final List<Produto> produtos = new ArrayList<>();

        public GrupoProduto(String nome) {
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Nome do grupo é obrigatório");
            }
            this.nome = nome.trim();
            this.status = Status.ATIVO;
        }

        public GrupoProduto(Long id, String nome) {
            this(nome);
            this.id = id;
        }

        public void adicionarProduto(Produto produto) {
            Objects.requireNonNull(produto, "Produto não pode ser nulo");
            this.produtos.add(produto);
            produto.associarAo(this);
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

        public Fornecedor(String razaoSocial, String cnpj) {
            this.razaoSocial = validarTextoObrigatorio(razaoSocial, "Razão social é obrigatória");
            this.cnpj = validarCnpj(cnpj);
            this.status = Status.ATIVO;
        }

        public Fornecedor(Long id, String razaoSocial, String cnpj) {
            this(razaoSocial, cnpj);
            this.id = id;
        }

        public void ativar() { this.status = Status.ATIVO; }
        public void inativar() { this.status = Status.INATIVO; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRazaoSocial() { return razaoSocial; }
        public String getCnpj() { return cnpj; }
        public Status getStatus() { return status; }

        private static String validarTextoObrigatorio(String texto, String mensagem) {
            if (texto == null || texto.isBlank()) {
                throw new IllegalArgumentException(mensagem);
            }
            return texto.trim();
        }

        private static String validarCnpj(String cnpj) {
            String valor = validarTextoObrigatorio(cnpj, "CNPJ é obrigatório");
            if (!valor.matches("\\d{14}")) {
                throw new IllegalArgumentException("CNPJ deve possuir 14 dígitos");
            }
            return valor;
        }
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

        public Produto(String codigoBarras, String descricao, BigDecimal saldoEstoque,
                       BigDecimal valorUnitario, BigDecimal estoqueMinimo, LocalDate dataCadastro) {
            this.codigoBarras = validarTextoObrigatorio(codigoBarras, "Código de barras é obrigatório");
            this.descricao = validarTextoObrigatorio(descricao, "Descrição é obrigatória");
            this.saldoEstoque = validarNaoNegativo(saldoEstoque, "Saldo de estoque não pode ser negativo");
            this.valorUnitario = validarNaoNegativo(valorUnitario, "Valor unitário não pode ser negativo");
            this.estoqueMinimo = validarNaoNegativo(estoqueMinimo, "Estoque mínimo não pode ser negativo");
            this.dataCadastro = Objects.requireNonNull(dataCadastro, "Data de cadastro é obrigatória");
            this.status = Status.ATIVO;
        }

        public BigDecimal calcularValorEstoque() {
            return saldoEstoque.multiply(valorUnitario).setScale(2, RoundingMode.HALF_UP);
        }

        public void receberEstoque(BigDecimal quantidade) {
            validarPositivo(quantidade, "Quantidade recebida deve ser maior que zero");
            this.saldoEstoque = this.saldoEstoque.add(quantidade);
        }

        public void retirarEstoque(BigDecimal quantidade) {
            validarPositivo(quantidade, "Quantidade retirada deve ser maior que zero");
            if (saldoEstoque.compareTo(quantidade) < 0) {
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

        private static String validarTextoObrigatorio(String texto, String mensagem) {
            if (texto == null || texto.isBlank()) {
                throw new IllegalArgumentException(mensagem);
            }
            return texto.trim();
        }

        private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
            Objects.requireNonNull(valor, mensagem);
            if (valor.signum() < 0) {
                throw new IllegalArgumentException(mensagem);
            }
            return valor;
        }

        private static void validarPositivo(BigDecimal valor, String mensagem) {
            Objects.requireNonNull(valor, mensagem);
            if (valor.signum() <= 0) {
                throw new IllegalArgumentException(mensagem);
            }
        }
    }

    // =========================================================================
    // 2. CONTRATOS DTO (Data Transfer Objects)
    // =========================================================================

    public record GrupoProdutoRequest(String nome) {}
    public record GrupoProdutoResponse(Long id, String nome, Status status) {}

    public record FornecedorRequest(String razaoSocial, String cnpj) {}
    public record FornecedorResponse(Long id, String razaoSocial, String cnpj, Status status) {}

    public record ProdutoRequest(
        String codigoBarras,
        String descricao,
        BigDecimal saldoEstoque,
        BigDecimal valorUnitario,
        BigDecimal estoqueMinimo,
        Long grupoId,
        Long fornecedorId
    ) {}

    public record ProdutoResponse(
        Long id,
        String codigoBarras,
        String descricao,
        BigDecimal saldoEstoque,
        BigDecimal valorUnitario,
        BigDecimal estoqueMinimo,
        BigDecimal valorEstoque,
        LocalDate dataCadastro,
        Status status,
        Long grupoId,
        String grupoNome,
        Long fornecedorId,
        String fornecedorRazaoSocial
    ) {}

    public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fields
    ) {}

    // =========================================================================
    // 3. EXCEÇÕES DE NEGÓCIO
    // =========================================================================

    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String mensagem) { super(mensagem); }
    }

    public static class RecursoDuplicadoException extends RuntimeException {
        public RecursoDuplicadoException(String mensagem) { super(mensagem); }
    }

    public static class ValidacaoException extends RuntimeException {
        private final Map<String, String> errors;
        public ValidacaoException(Map<String, String> errors) {
            super("Um ou mais campos são inválidos");
            this.errors = errors;
        }
        public Map<String, String> getErrors() { return errors; }
    }

    // =========================================================================
    // 4. MAPEADORES MANUAIS (Mappers)
    // =========================================================================

    public static class GrupoProdutoMapper {
        public GrupoProdutoResponse toResponse(GrupoProduto grupo) {
            return new GrupoProdutoResponse(grupo.getId(), grupo.getNome(), grupo.getStatus());
        }
    }

    public static class FornecedorMapper {
        public Fornecedor toEntity(FornecedorRequest request) {
            return new Fornecedor(request.razaoSocial(), request.cnpj());
        }
        public FornecedorResponse toResponse(Fornecedor fornecedor) {
            return new FornecedorResponse(fornecedor.getId(), fornecedor.getRazaoSocial(), fornecedor.getCnpj(), fornecedor.getStatus());
        }
    }

    public static class ProdutoMapper {
        public Produto toEntity(ProdutoRequest request) {
            return new Produto(
                request.codigoBarras(),
                request.descricao(),
                request.saldoEstoque(),
                request.valorUnitario(),
                request.estoqueMinimo(),
                LocalDate.now()
            );
        }

        public ProdutoResponse toResponse(Produto produto) {
            Fornecedor fornecedor = produto.getFornecedor();
            return new ProdutoResponse(
                produto.getId(),
                produto.getCodigoBarras(),
                produto.getDescricao(),
                produto.getSaldoEstoque(),
                produto.getValorUnitario(),
                produto.getEstoqueMinimo(),
                produto.calcularValorEstoque(),
                produto.getDataCadastro(),
                produto.getStatus(),
                produto.getGrupo() != null ? produto.getGrupo().getId() : null,
                produto.getGrupo() != null ? produto.getGrupo().getNome() : null,
                fornecedor == null ? null : fornecedor.getId(),
                fornecedor == null ? null : fornecedor.getRazaoSocial()
            );
        }
    }

    // =========================================================================
    // 5. CAMADA DE APLICAÇÃO / SERVIÇOS (Services)
    // =========================================================================

    public static class GrupoProdutoService {
        private final Map<Long, GrupoProduto> banco = new LinkedHashMap<>();
        private long sequence = 1;

        public GrupoProduto cadastrar(String nome) {
            for (GrupoProduto g : banco.values()) {
                if (g.getNome().equalsIgnoreCase(nome)) {
                    throw new RecursoDuplicadoException("Nome do grupo já cadastrado");
                }
            }
            GrupoProduto grupo = new GrupoProduto(nome);
            grupo.setId(sequence++);
            banco.put(grupo.getId(), grupo);
            return grupo;
        }

        public GrupoProduto buscarPorId(Long id) {
            GrupoProduto grupo = banco.get(id);
            if (grupo == null) {
                throw new RecursoNaoEncontradoException("Grupo de produto não encontrado");
            }
            return grupo;
        }

        public List<GrupoProduto> listar() {
            return new ArrayList<>(banco.values());
        }
    }

    public static class FornecedorService {
        private final Map<Long, Fornecedor> banco = new LinkedHashMap<>();
        private long sequence = 1;

        public Fornecedor cadastrar(Fornecedor fornecedor) {
            for (Fornecedor f : banco.values()) {
                if (f.getCnpj().equals(fornecedor.getCnpj())) {
                    throw new RecursoDuplicadoException("CNPJ já cadastrado");
                }
            }
            fornecedor.setId(sequence++);
            banco.put(fornecedor.getId(), fornecedor);
            return fornecedor;
        }

        public Fornecedor buscarPorId(Long id) {
            Fornecedor f = banco.get(id);
            if (f == null) {
                throw new RecursoNaoEncontradoException("Fornecedor não encontrado");
            }
            return f;
        }

        public List<Fornecedor> listar() {
            return new ArrayList<>(banco.values());
        }
    }

    public static class ProdutoService {
        private final Map<Long, Produto> banco = new LinkedHashMap<>();
        private final GrupoProdutoService grupoService;
        private final FornecedorService fornecedorService;
        private long sequence = 1;

        public ProdutoService(GrupoProdutoService grupoService, FornecedorService fornecedorService) {
            this.grupoService = grupoService;
            this.fornecedorService = fornecedorService;
        }

        public Produto cadastrar(Produto produto, Long grupoId, Long fornecedorId) {
            for (Produto p : banco.values()) {
                if (p.getCodigoBarras().equalsIgnoreCase(produto.getCodigoBarras())) {
                    throw new RecursoDuplicadoException("Código de barras já cadastrado");
                }
            }
            GrupoProduto grupo = grupoService.buscarPorId(grupoId);
            grupo.adicionarProduto(produto);

            if (fornecedorId != null) {
                Fornecedor fornecedor = fornecedorService.buscarPorId(fornecedorId);
                produto.associarFornecedor(fornecedor);
            }

            produto.setId(sequence++);
            banco.put(produto.getId(), produto);
            return produto;
        }

        public Produto buscarPorId(Long id) {
            Produto p = banco.get(id);
            if (p == null) {
                throw new RecursoNaoEncontradoException("Produto não encontrado");
            }
            return p;
        }

        public List<Produto> listar() {
            return new ArrayList<>(banco.values());
        }
    }

    // =========================================================================
    // 6. CONTROLADORES REST E RESPOSTAS HTTP
    // =========================================================================

    public record HttpResponse<T>(int status, String statusText, Map<String, String> headers, T body) {}

    public static class ProdutoController {
        private final ProdutoService service;
        private final ProdutoMapper mapper;

        public ProdutoController(ProdutoService service, ProdutoMapper mapper) {
            this.service = service;
            this.mapper = mapper;
        }

        public HttpResponse<?> cadastrar(ProdutoRequest request, String path) {
            Map<String, String> erros = validarRequest(request);
            if (!erros.isEmpty()) {
                ApiError erro = new ApiError(Instant.now(), 400, "Bad Request",
                        "Um ou mais campos são inválidos", path, erros);
                return new HttpResponse<>(400, "Bad Request", Map.of("Content-Type", "application/json"), erro);
            }

            try {
                Produto produto = mapper.toEntity(request);
                Produto cadastrado = service.cadastrar(produto, request.grupoId(), request.fornecedorId());
                URI location = URI.create("/api/produtos/" + cadastrado.getId());
                Map<String, String> headers = Map.of(
                    "Location", location.toString(),
                    "Content-Type", "application/json"
                );
                return new HttpResponse<>(201, "Created", headers, mapper.toResponse(cadastrado));
            } catch (RecursoNaoEncontradoException e) {
                ApiError erro = new ApiError(Instant.now(), 404, "Not Found", e.getMessage(), path, Map.of());
                return new HttpResponse<>(404, "Not Found", Map.of("Content-Type", "application/json"), erro);
            } catch (RecursoDuplicadoException e) {
                ApiError erro = new ApiError(Instant.now(), 409, "Conflict", e.getMessage(), path, Map.of());
                return new HttpResponse<>(409, "Conflict", Map.of("Content-Type", "application/json"), erro);
            }
        }

        public HttpResponse<?> buscarPorId(Long id, String path) {
            try {
                Produto p = service.buscarPorId(id);
                return new HttpResponse<>(200, "OK", Map.of("Content-Type", "application/json"), mapper.toResponse(p));
            } catch (RecursoNaoEncontradoException e) {
                ApiError erro = new ApiError(Instant.now(), 404, "Not Found", e.getMessage(), path, Map.of());
                return new HttpResponse<>(404, "Not Found", Map.of("Content-Type", "application/json"), erro);
            }
        }

        public HttpResponse<?> listar() {
            List<ProdutoResponse> lista = service.listar().stream().map(mapper::toResponse).toList();
            return new HttpResponse<>(200, "OK", Map.of("Content-Type", "application/json"), lista);
        }

        private Map<String, String> validarRequest(ProdutoRequest r) {
            Map<String, String> fields = new LinkedHashMap<>();
            if (r.codigoBarras() == null || r.codigoBarras().isBlank()) {
                fields.put("codigoBarras", "Código de barras é obrigatório");
            } else if (r.codigoBarras().length() > 50) {
                fields.put("codigoBarras", "Código de barras deve possuir no máximo 50 caracteres");
            }
            if (r.descricao() == null || r.descricao().isBlank()) {
                fields.put("descricao", "Descrição é obrigatória");
            } else if (r.descricao().length() > 150) {
                fields.put("descricao", "Descrição deve possuir no máximo 150 caracteres");
            }
            if (r.saldoEstoque() == null) {
                fields.put("saldoEstoque", "Saldo de estoque é obrigatório");
            } else if (r.saldoEstoque().signum() < 0) {
                fields.put("saldoEstoque", "Saldo de estoque não pode ser negativo");
            }
            if (r.valorUnitario() == null) {
                fields.put("valorUnitario", "Valor unitário é obrigatório");
            } else if (r.valorUnitario().signum() < 0) {
                fields.put("valorUnitario", "Valor unitário não pode ser negativo");
            }
            if (r.estoqueMinimo() == null) {
                fields.put("estoqueMinimo", "Estoque mínimo é obrigatório");
            } else if (r.estoqueMinimo().signum() < 0) {
                fields.put("estoqueMinimo", "Estoque mínimo não pode ser negativo");
            }
            if (r.grupoId() == null) {
                fields.put("grupoId", "Grupo é obrigatório");
            } else if (r.grupoId() <= 0) {
                fields.put("grupoId", "Identificador do grupo deve ser positivo");
            }
            if (r.fornecedorId() != null && r.fornecedorId() <= 0) {
                fields.put("fornecedorId", "Identificador do fornecedor deve ser positivo");
            }
            return fields;
        }
    }

    // =========================================================================
    // 7. MÉTODO PRINCIPAL: SIMULAÇÃO DAS REQUISIÇÕES DA AULA 07
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("==============================================================================");
        System.out.println("DEMONSTRAÇÃO DA AULA 07: API REST, DTOS, MAPPEADORES E TRATAMENTO DE ERROS");
        System.out.println("Projeto de Referência: Suporte OS 2026");
        System.out.println("==============================================================================\n");

        GrupoProdutoService grupoService = new GrupoProdutoService();
        FornecedorService fornecedorService = new FornecedorService();
        ProdutoService produtoService = new ProdutoService(grupoService, fornecedorService);
        ProdutoMapper produtoMapper = new ProdutoMapper();
        ProdutoController controller = new ProdutoController(produtoService, produtoMapper);

        // 1. Setup prévio de relacionamentos (Simulando POST /api/grupos-produtos e POST /api/fornecedores)
        GrupoProduto grupoPerifericos = grupoService.cadastrar("Periféricos");
        Fornecedor fornecedorDistribuidora = fornecedorService.cadastrar(
            new Fornecedor("Distribuidora Acadêmica Ltda", "12345678000199")
        );
        System.out.println("[SETUP] Grupo criado: ID " + grupoPerifericos.getId() + " - " + grupoPerifericos.getNome());
        System.out.println("[SETUP] Fornecedor criado: ID " + fornecedorDistribuidora.getId() + " - " + fornecedorDistribuidora.getRazaoSocial());
        System.out.println("------------------------------------------------------------------------------");

        // Cenário 1: POST /api/produtos (Caminho Feliz - 201 Created)
        System.out.println("\n>>> CENÁRIO 1: POST /api/produtos (Cadastro Válido)");
        ProdutoRequest reqValido = new ProdutoRequest(
            "AULA07-MOUSE-001",
            "Mouse sem fio",
            new BigDecimal("20.000"),
            new BigDecimal("89.90"),
            new BigDecimal("5.000"),
            grupoPerifericos.getId(),
            fornecedorDistribuidora.getId()
        );
        HttpResponse<?> res1 = controller.cadastrar(reqValido, "/api/produtos");
        System.out.println("Status HTTP: " + res1.status() + " " + res1.statusText());
        System.out.println("Headers: " + res1.headers());
        System.out.println("Corpo: " + res1.body());

        // Cenário 2: GET /api/produtos/1 (Consulta por ID - 200 OK)
        System.out.println("\n>>> CENÁRIO 2: GET /api/produtos/1 (Consulta por ID)");
        HttpResponse<?> res2 = controller.buscarPorId(1L, "/api/produtos/1");
        System.out.println("Status HTTP: " + res2.status() + " " + res2.statusText());
        System.out.println("Corpo: " + res2.body());

        // Cenário 3: POST /api/produtos (Erro de Validação - 400 Bad Request)
        System.out.println("\n>>> CENÁRIO 3: POST /api/produtos (Campos Inválidos / Bean Validation)");
        ProdutoRequest reqInvalido = new ProdutoRequest(
            "",
            "",
            new BigDecimal("-2.000"),
            new BigDecimal("-1.00"),
            new BigDecimal("-3.000"),
            0L,
            null
        );
        HttpResponse<?> res3 = controller.cadastrar(reqInvalido, "/api/produtos");
        System.out.println("Status HTTP: " + res3.status() + " " + res3.statusText());
        System.out.println("Corpo do Erro Padronizado: " + res3.body());

        // Cenário 4: GET /api/produtos/9999 (Recurso Inexistente - 404 Not Found)
        System.out.println("\n>>> CENÁRIO 4: GET /api/produtos/9999 (Recurso Não Encontrado)");
        HttpResponse<?> res4 = controller.buscarPorId(9999L, "/api/produtos/9999");
        System.out.println("Status HTTP: " + res4.status() + " " + res4.statusText());
        System.out.println("Corpo do Erro Padronizado: " + res4.body());

        // Cenário 5: POST /api/produtos duplicado (Conflito de Regra de Negócio - 409 Conflict)
        System.out.println("\n>>> CENÁRIO 5: POST /api/produtos com Código de Barras Repetido (409 Conflict)");
        HttpResponse<?> res5 = controller.cadastrar(reqValido, "/api/produtos");
        System.out.println("Status HTTP: " + res5.status() + " " + res5.statusText());
        System.out.println("Corpo do Erro Padronizado: " + res5.body());

        System.out.println("\n==============================================================================");
        System.out.println("TODOS OS CENÁRIOS DA AULA FORAM EXECUTADOS COM SUCESSO!");
        System.out.println("==============================================================================");
    }
}
