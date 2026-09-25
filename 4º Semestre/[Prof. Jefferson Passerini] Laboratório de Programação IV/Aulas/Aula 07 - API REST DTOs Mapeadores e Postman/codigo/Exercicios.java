/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Resolução da Atividade Prática e Questões de Discussão da Aula 07
 *
 * Como compilar e executar:
 * javac Exercicios.java
 * java Exercicios
 * (Compatível com Java 21+)
 */

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class Exercicios {

    // =========================================================================
    // EXERCÍCIO 1: ATIVIDADE PRÁTICA (Implementação Completa no Tema de Suporte OS)
    // Recursos Escolhidos: Cliente, Tecnico, OrdemServico
    // =========================================================================

    public enum StatusOS { ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA }

    // --- 1. Domínio ---
    public static class Cliente {
        private Long id;
        private String nome;
        private String cpf;
        private String telefone;

        public Cliente(String nome, String cpf, String telefone) {
            if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do cliente é obrigatório");
            if (cpf == null || !cpf.matches("\\d{11}")) throw new IllegalArgumentException("CPF deve conter 11 dígitos");
            this.nome = nome.trim();
            this.cpf = cpf;
            this.telefone = telefone != null ? telefone.trim() : null;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public String getCpf() { return cpf; }
        public String getTelefone() { return telefone; }
    }

    public static class Tecnico {
        private Long id;
        private String nome;
        private String matricula;

        public Tecnico(String nome, String matricula) {
            if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do técnico é obrigatório");
            if (matricula == null || matricula.isBlank()) throw new IllegalArgumentException("Matrícula é obrigatória");
            this.nome = nome.trim();
            this.matricula = matricula.trim();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public String getMatricula() { return matricula; }
    }

    public static class OrdemServico {
        private Long id;
        private String protocolo;
        private String descricaoProblema;
        private BigDecimal valorEstimado;
        private LocalDateTime dataAbertura;
        private StatusOS status;
        private Cliente cliente;
        private Tecnico tecnico;

        public OrdemServico(String protocolo, String descricaoProblema, BigDecimal valorEstimado) {
            if (protocolo == null || protocolo.isBlank()) throw new IllegalArgumentException("Protocolo é obrigatório");
            if (descricaoProblema == null || descricaoProblema.isBlank()) throw new IllegalArgumentException("Descrição é obrigatória");
            if (valorEstimado != null && valorEstimado.signum() < 0) throw new IllegalArgumentException("Valor estimado não pode ser negativo");
            this.protocolo = protocolo.trim();
            this.descricaoProblema = descricaoProblema.trim();
            this.valorEstimado = valorEstimado != null ? valorEstimado : BigDecimal.ZERO;
            this.dataAbertura = LocalDateTime.now();
            this.status = StatusOS.ABERTA;
        }

        public void associarCliente(Cliente cliente) {
            this.cliente = Objects.requireNonNull(cliente, "Cliente é obrigatório");
        }

        public void associarTecnico(Tecnico tecnico) {
            this.tecnico = tecnico;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getProtocolo() { return protocolo; }
        public String getDescricaoProblema() { return descricaoProblema; }
        public BigDecimal getValorEstimado() { return valorEstimado; }
        public LocalDateTime getDataAbertura() { return dataAbertura; }
        public StatusOS getStatus() { return status; }
        public Cliente getCliente() { return cliente; }
        public Tecnico getTecnico() { return tecnico; }
    }

    // --- 2. DTOs Imutáveis (Records) ---
    public record OrdemServicoRequest(
        String protocolo,
        String descricaoProblema,
        BigDecimal valorEstimado,
        Long clienteId,
        Long tecnicoId
    ) {}

    public record OrdemServicoResponse(
        Long id,
        String protocolo,
        String descricaoProblema,
        BigDecimal valorEstimado,
        LocalDateTime dataAbertura,
        StatusOS status,
        Long clienteId,
        String clienteNome,
        Long tecnicoId,
        String tecnicoNome
    ) {}

    public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fields
    ) {}

    // --- 3. Mapeador Manual ---
    public static class OrdemServicoMapper {
        public OrdemServico toEntity(OrdemServicoRequest request) {
            return new OrdemServico(request.protocolo(), request.descricaoProblema(), request.valorEstimado());
        }

        public OrdemServicoResponse toResponse(OrdemServico os) {
            return new OrdemServicoResponse(
                os.getId(),
                os.getProtocolo(),
                os.getDescricaoProblema(),
                os.getValorEstimado(),
                os.getDataAbertura(),
                os.getStatus(),
                os.getCliente() != null ? os.getCliente().getId() : null,
                os.getCliente() != null ? os.getCliente().getNome() : null,
                os.getTecnico() != null ? os.getTecnico().getId() : null,
                os.getTecnico() != null ? os.getTecnico().getNome() : null
            );
        }
    }

    // --- 4. Exceções e Serviço ---
    public static class RecursoNaoEncontradoException extends RuntimeException { public RecursoNaoEncontradoException(String msg) { super(msg); } }
    public static class RecursoDuplicadoException extends RuntimeException { public RecursoDuplicadoException(String msg) { super(msg); } }

    public static class OrdemServicoService {
        private final Map<Long, OrdemServico> banco = new HashMap<>();
        private final Map<Long, Cliente> clientes = new HashMap<>();
        private final Map<Long, Tecnico> tecnicos = new HashMap<>();
        private long seqOS = 1;

        public void salvarCliente(Cliente c) { c.setId((long) (clientes.size() + 1)); clientes.put(c.getId(), c); }
        public void salvarTecnico(Tecnico t) { t.setId((long) (tecnicos.size() + 1)); tecnicos.put(t.getId(), t); }
        public Cliente buscarCliente(Long id) { return Optional.ofNullable(clientes.get(id)).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado")); }
        public Tecnico buscarTecnico(Long id) { return Optional.ofNullable(tecnicos.get(id)).orElseThrow(() -> new RecursoNaoEncontradoException("Técnico não encontrado")); }

        public OrdemServico cadastrar(OrdemServico os, Long clienteId, Long tecnicoId) {
            for (OrdemServico item : banco.values()) {
                if (item.getProtocolo().equalsIgnoreCase(os.getProtocolo())) {
                    throw new RecursoDuplicadoException("Protocolo de OS já cadastrado");
                }
            }
            Cliente cliente = buscarCliente(clienteId);
            os.associarCliente(cliente);
            if (tecnicoId != null) {
                Tecnico tecnico = buscarTecnico(tecnicoId);
                os.associarTecnico(tecnico);
            }
            os.setId(seqOS++);
            banco.put(os.getId(), os);
            return os;
        }

        public OrdemServico buscarPorId(Long id) {
            return Optional.ofNullable(banco.get(id))
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de Serviço não encontrada"));
        }

        public List<OrdemServico> listar() { return new ArrayList<>(banco.values()); }
    }

    // --- 5. Controller REST com Respostas HTTP Semânticas ---
    public record HttpResponse<T>(int status, String reason, Map<String, String> headers, T body) {}

    public static class OrdemServicoController {
        private final OrdemServicoService service;
        private final OrdemServicoMapper mapper;

        public OrdemServicoController(OrdemServicoService service, OrdemServicoMapper mapper) {
            this.service = service;
            this.mapper = mapper;
        }

        public HttpResponse<?> cadastrar(OrdemServicoRequest req, String path) {
            Map<String, String> erros = new LinkedHashMap<>();
            if (req.protocolo() == null || req.protocolo().isBlank()) erros.put("protocolo", "Protocolo é obrigatório");
            if (req.descricaoProblema() == null || req.descricaoProblema().isBlank()) erros.put("descricaoProblema", "Descrição é obrigatória");
            if (req.clienteId() == null || req.clienteId() <= 0) erros.put("clienteId", "Identificador do cliente deve ser positivo");
            if (req.valorEstimado() != null && req.valorEstimado().signum() < 0) erros.put("valorEstimado", "Valor estimado não pode ser negativo");

            if (!erros.isEmpty()) {
                ApiError err = new ApiError(Instant.now(), 400, "Bad Request", "Um ou mais campos são inválidos", path, erros);
                return new HttpResponse<>(400, "Bad Request", Map.of(), err);
            }

            try {
                OrdemServico entity = mapper.toEntity(req);
                OrdemServico salva = service.cadastrar(entity, req.clienteId(), req.tecnicoId());
                URI location = URI.create("/api/ordens-servico/" + salva.getId());
                return new HttpResponse<>(201, "Created", Map.of("Location", location.toString()), mapper.toResponse(salva));
            } catch (RecursoNaoEncontradoException e) {
                return new HttpResponse<>(404, "Not Found", Map.of(), new ApiError(Instant.now(), 404, "Not Found", e.getMessage(), path, Map.of()));
            } catch (RecursoDuplicadoException e) {
                return new HttpResponse<>(409, "Conflict", Map.of(), new ApiError(Instant.now(), 409, "Conflict", e.getMessage(), path, Map.of()));
            }
        }

        public HttpResponse<?> buscarPorId(Long id, String path) {
            try {
                OrdemServico os = service.buscarPorId(id);
                return new HttpResponse<>(200, "OK", Map.of(), mapper.toResponse(os));
            } catch (RecursoNaoEncontradoException e) {
                return new HttpResponse<>(404, "Not Found", Map.of(), new ApiError(Instant.now(), 404, "Not Found", e.getMessage(), path, Map.of()));
            }
        }
    }

    // =========================================================================
    // MÉTODO EXECUTÁVEL: RESOLUÇÃO DOS TESTES E QUESTÕES TEÓRICAS
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("==============================================================================");
        System.out.println("RESOLUÇÃO COMPLETA: ATIVIDADE PRÁTICA E DISCUSSÕES DA AULA 07");
        System.out.println("==============================================================================\n");

        // Execução Prática do Exercício 1
        System.out.println("--- EXERCÍCIO 1: Teste dos Endpoints da Atividade Prática (Ordem de Serviço) ---");
        OrdemServicoService osService = new OrdemServicoService();
        OrdemServicoMapper osMapper = new OrdemServicoMapper();
        OrdemServicoController controller = new OrdemServicoController(osService, osMapper);

        Cliente c1 = new Cliente("Ana Paula da Silva", "12345678901", "(17) 99999-1111");
        osService.salvarCliente(c1);
        Tecnico t1 = new Tecnico("Carlos Eduardo", "TEC-2026-01");
        osService.salvarTecnico(t1);

        // Teste 1: 201 Created com Location
        OrdemServicoRequest reqValida = new OrdemServicoRequest("OS-2026-001", "Notebook não liga após pico de energia", new BigDecimal("250.00"), c1.getId(), t1.getId());
        HttpResponse<?> r1 = controller.cadastrar(reqValida, "/api/ordens-servico");
        System.out.println("[Teste 1.1 - 201 Created] Status: " + r1.status() + " | Location: " + r1.headers().get("Location"));

        // Teste 2: 400 Bad Request por validação
        OrdemServicoRequest reqInvalida = new OrdemServicoRequest("", "", new BigDecimal("-50.00"), 0L, null);
        HttpResponse<?> r2 = controller.cadastrar(reqInvalida, "/api/ordens-servico");
        System.out.println("[Teste 1.2 - 400 Bad Request] Status: " + r2.status() + " | Erros: " + ((ApiError) r2.body()).fields());

        // Teste 3: 404 Not Found para cliente inexistente
        OrdemServicoRequest reqClienteInexistente = new OrdemServicoRequest("OS-2026-002", "Troca de display", new BigDecimal("300.00"), 999L, null);
        HttpResponse<?> r3 = controller.cadastrar(reqClienteInexistente, "/api/ordens-servico");
        System.out.println("[Teste 1.3 - 404 Not Found] Status: " + r3.status() + " | Mensagem: " + ((ApiError) r3.body()).message());

        // Teste 4: 409 Conflict para protocolo duplicado
        HttpResponse<?> r4 = controller.cadastrar(reqValida, "/api/ordens-servico");
        System.out.println("[Teste 1.4 - 409 Conflict] Status: " + r4.status() + " | Mensagem: " + ((ApiError) r4.body()).message());

        // Teste 5: 200 OK na consulta por ID
        HttpResponse<?> r5 = controller.buscarPorId(1L, "/api/ordens-servico/1");
        System.out.println("[Teste 1.5 - 200 OK Consulta] Status: " + r5.status() + " | Protocolo: " + ((OrdemServicoResponse) r5.body()).protocolo());

        System.out.println("\n==============================================================================");
        System.out.println("GABARITO DETALHADO DAS QUESTÕES PARA DISCUSSÃO");
        System.out.println("==============================================================================");

        System.out.println("\n[EXERCÍCIO 2 / Questão 1] Por que um DTO não deve substituir as regras da entidade?");
        System.out.println("Resposta: O DTO (Data Transfer Object) pertence à camada de adaptação web e valida apenas\n" +
                "o contrato sintático da requisição HTTP (ex: não nulo, tamanho máximo, regex). A entidade de domínio\n" +
                "é responsável pelas invariantes essenciais do negócio (ex: cálculo de estoque, transições de estado,\n" +
                "regras financeiras). Se a entidade não se autovalidar, operações disparadas por outros canais\n" +
                "(jobs agendados, testes de unidade, filas RabbitMQ/Kafka ou migrações em lote) poderiam corromper\n" +
                "o banco de dados com instâncias em estado inconsistente.");

        System.out.println("\n[EXERCÍCIO 3 / Questão 2] Qual problema pode surgir ao serializar diretamente uma associação JPA bidirecional?");
        System.out.println("Resposta: Quando duas entidades possuem relacionamento bidirecional (ex: GrupoProduto possui\n" +
                "List<Produto> e Produto possui GrupoProduto), bibliotecas de serialização JSON (Jackson) entram\n" +
                "em recursão infinita ao tentar serializar o grupo, que chama os produtos, que chamam o grupo,\n" +
                "culminando em estouro de pilha (StackOverflowError). Além disso, o Jackson pode tentar disparar\n" +
                "consultas para coleções LAZY fora de transação ativa, gerando LazyInitializationException.");

        System.out.println("\n[EXERCÍCIO 4 / Questão 3] Por que POST /api/produtos retorna 201 Created, e não apenas 200 OK?");
        System.out.println("Resposta: Conforme a RFC 9110 do protocolo HTTP, o status 201 Created indica explicitamente que\n" +
                "a requisição resultou na criação efetiva de um novo recurso persistido no servidor. Adicionalmente,\n" +
                "o status 201 deve ser acompanhado pelo cabeçalho HTTP 'Location', informando a URI direta pela qual\n" +
                "o novo recurso pode ser consultado imediatamente pelo cliente (ex: Location: /api/produtos/42).\n" +
                "O 200 OK representa sucesso genérico, mas não carrega essa semântica contratual de criação de recurso.");

        System.out.println("\n[EXERCÍCIO 5 / Questão 4] Em que situação um erro de negócio deve ser 409 Conflict em vez de 500?");
        System.out.println("Resposta: O código 409 Conflict deve ser usado quando a requisição do cliente é sintaticamente\n" +
                "válida, mas colide com o estado atual do banco de dados (ex: tentativa de cadastrar um CNPJ,\n" +
                "código de barras, e-mail ou protocolo que já existe sob uma restrição de unicidade). O 500 Internal\n" +
                "Server Error denota falha inesperada ou bug no próprio servidor. Retornar 500 para dados duplicados\n" +
                "é uma falha grave de API, pois mascara um erro do cliente como se fosse falha de infraestrutura.");

        System.out.println("\n[EXERCÍCIO 6 / Questão 5] Qual é a diferença entre um teste MockMvc e uma requisição manual do Postman?");
        System.out.println("Resposta: O MockMvc executa dentro do ciclo de vida dos testes automatizados (JUnit), sem abrir\n" +
                "uma porta de rede real, simulando a camada de despacho do Spring MVC e permitindo transações reversíveis\n" +
                "com @Transactional (rollback automático). Ele é executado a cada build no Maven/CI. O Postman é um\n" +
                "cliente HTTP externo real que envia pacotes via rede para a aplicação rodando com porta aberta,\n" +
                "sendo ideal para validação fim a fim, exploração manual, documentação viva e testes de aceitação.");

        System.out.println("\n[EXERCÍCIO 7 / Questão 6] Por que IDs produzidos por uma requisição devem ser armazenados em variáveis no Postman?");
        System.out.println("Resposta: Em bancos relacionais com chaves primárias auto-incrementadas ou geradas por sequências,\n" +
                "o ID de um registro recém-criado não pode ser previsto estaticamente. Ao capturar o ID da resposta no script\n" +
                "Post-response (ex: pm.collectionVariables.set('produtoId', body.id)), as requisições seguintes\n" +
                "(como GET /api/produtos/{{produtoId}}) tornam-se dinâmicas e encadeadas automaticamente,\n" +
                "eliminando a necessidade de copiar e colar identificadores manualmente entre chamadas.");

        System.out.println("\n[EXERCÍCIO 8 / Questão 7] O que seria necessário para tornar os cenários do Collection Runner independentes?");
        System.out.println("Resposta: Para garantir total independência e idempotência em testes automatizados no Postman:\n" +
                "1. Geração dinâmica de dados únicos: utilizar geradores aleatórios (ex: {{$randomUUID}} ou timestamps)\n" +
                "   para campos com constraint unique (CNPJ, código de barras);\n" +
                "2. Scripts de Pre-request e Post-response com limpeza: implementar endpoints de exclusão controlada ou\n" +
                "   scripts que criem suas próprias massas de dados antes do teste e limpem após a asserção;\n" +
                "3. Banco de dados efêmero/isolado: apontar a execução para uma base de testes dedicada ou container\n" +
                "   descartável (Testcontainers), resetando a base entre as execuções da coleção.");
    }
}
