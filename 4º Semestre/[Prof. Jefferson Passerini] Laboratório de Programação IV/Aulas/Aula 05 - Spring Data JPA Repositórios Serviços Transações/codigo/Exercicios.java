/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Resolução dos Exercícios da Aula 05 (Atividade de Transferência e Revisão)
 *
 * Como executar:
 *   javac Exercicios.java
 *   java Exercicios
 * Ou diretamente (Java 21+):
 *   java Exercicios.java
 */

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Exercicios {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("UniFEF - Laboratório de Programação IV");
        System.out.println("Resolução Completa dos Exercícios e Questões da Aula 05");
        System.out.println("===============================================================\n");

        // =====================================================================
        // EXERCÍCIO 1: ATIVIDADE DE TRANSFERÊNCIA NO PROJETO INDIVIDUAL
        // =====================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("Exercício 1 (Atividade de Transferência) - Projeto Individual: Suporte OS");
        System.out.println("---------------------------------------------------------------");

        SimuladorJPAContexto context = new SimuladorJPAContexto();
        CategoriaOSRepositorySimulado categoriaRepo = new CategoriaOSRepositorySimulado(context);
        OrdemServicoRepositorySimulado osRepo = new OrdemServicoRepositorySimulado(context);
        OrdemServicoServiceSimulado osService = new OrdemServicoServiceSimulado(osRepo, categoriaRepo, context);

        // Passo 1 e 4: Criar classificação e cadastrar ordem transacionalmente
        CategoriaOS redes = categoriaRepo.save(new CategoriaOS("Infraestrutura de Redes"));
        System.out.println("1. Categoria cadastrada com sucesso: [ID: " + redes.getId() + ", Nome: " + redes.getNome() + "]");

        OrdemServico osValida = new OrdemServico("OS-2026-001", "Falha no Switch Central da Administração");
        OrdemServico osCadastrada = osService.abrirOrdemServico(osValida, redes.getId());
        System.out.println("4. Ordem cadastrada transacionalmente: [Protocolo: " + osCadastrada.getNumeroProtocolo() +
                ", Categoria: " + osCadastrada.getCategoria().getNome() +
                ", Status: " + osCadastrada.getStatus() + "]");

        // Passo 2: Consulta derivada por chave de negócio (Protocolo único)
        System.out.println("\n2. Consulta por chave de negócio (findByNumeroProtocolo): ");
        Optional<OrdemServico> osConsultada = osRepo.findByNumeroProtocolo("OS-2026-001");
        System.out.println("   Resultado encontrado: " + osConsultada.map(OrdemServico::getDescricao).orElse("Nenhum"));

        // Passo 3: Consulta derivada por relacionamento (findByCategoriaId)
        System.out.println("\n3. Consulta pelo relacionamento (findByCategoriaId): ");
        List<OrdemServico> listaPorCategoria = osRepo.findByCategoriaId(redes.getId());
        System.out.println("   Total de chamados vinculados à categoria '" + redes.getNome() + "': " + listaPorCategoria.size());

        // Passo 5: Provocar falha após operação inicial e demonstrar Rollback
        System.out.println("\n5. Teste de Falha Transacional e Rollback:");
        try {
            OrdemServico osInvalida = new OrdemServico("OS-2026-999", "Falha em Servidor Virtual");
            System.out.println("   Tentando registrar chamada associada a Categoria inexistente (ID: 8888L)...");
            osService.abrirOrdemServico(osInvalida, 8888L);
        } catch (RecursoNaoEncontradoException e) {
            System.out.println("   Capturada falha esperada de negócio: " + e.getMessage());
            boolean existe = osRepo.existsByNumeroProtocolo("OS-2026-999");
            System.out.println("   A ordem foi persistida no banco? " + (existe ? "SIM (Erro no Rollback!)" : "NÃO (Rollback atômico garantido!)"));
        }

        // Passo 6: Justificativa arquitetural de alocação de regras
        System.out.println("\n6. Justificativa Arquitetural de Onde Cada Regra Pertence:");
        System.out.println("   - DOMÍNIO: Invariantes do objeto, como descrição não vazia, validação de formato do protocolo e transição de status (ex.: ABERTA -> EM_ATENDIMENTO). Protege a coerência intrínseca do estado.");
        System.out.println("   - SERVIÇO: Coordenação do caso de uso, orquestração de múltiplos repositórios, verificação de duplicidade no repositório antes de persistir e fronteira de transação (@Transactional).");
        System.out.println("   - BANCO: Última linha de integridade estrutural via Constraints (UNIQUE no protocolo, NOT NULL nas colunas, CHECK constraints de status e FOREIGN KEY com RESTRICT).");

        // =====================================================================
        // EXERCÍCIO 2: QUESTÃO DE REVISÃO 1
        // =====================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Exercício 2 (Questão de Revisão 1) - Quem implementa JpaRepository?");
        System.out.println("---------------------------------------------------------------");
        System.out.println("RESPOSTA:");
        System.out.println("O desenvolvedor declara apenas a interface. Durante a inicialização do container,");
        System.out.println("o Spring Data JPA utiliza geração dinâmica de proxies (baseado em JDK Dynamic Proxies / ByteBuddy),");
        System.out.println("instanciando a classe padrão 'SimpleJpaRepository' do Spring e acoplando adaptadores para as");
        System.out.println("consultas derivadas e customizadas. O proxy gerado é registrado como bean gerenciado no Spring ApplicationContext.");

        // =====================================================================
        // EXERCÍCIO 3: QUESTÃO DE REVISÃO 2
        // =====================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Exercício 3 (Questão de Revisão 2) - Por que o serviço define a fronteira transacional?");
        System.out.println("---------------------------------------------------------------");
        System.out.println("RESPOSTA:");
        System.out.println("Porque um caso de uso de negócio geralmente envolve múltiplas operações de leitura e escrita");
        System.out.println("coordenadas entre diferentes entidades e repositórios. Se a transação ficasse restrita ao repository,");
        System.out.println("cada chamada a 'save' ou 'findById' abriria e fecharia sua própria transação isolada.");
        System.out.println("Colocando @Transactional no método do serviço, assegura-se a atomicidade total (ACID):");
        System.out.println("ou todas as alterações do caso de uso são confirmadas juntas (commit), ou tudo é desfeito (rollback) caso ocorra erro.");

        // =====================================================================
        // EXERCÍCIO 4: QUESTÃO DE REVISÃO 3
        // =====================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Exercício 4 (Questão de Revisão 3) - Entidade Managed vs Detached");
        System.out.println("---------------------------------------------------------------");
        System.out.println("RESPOSTA:");
        System.out.println("- Entidade MANAGED: Está ativamente associada a um PersistenceContext do JPA. O EntityManager");
        System.out.println("  monitora qualquer modificação em seus atributos em memória para sincronização automática (Dirty Checking)");
        System.out.println("  e permite carregamento preguiçoso (Lazy Loading) de associações.");
        System.out.println("- Entidade DETACHED: Possui identidade de banco de dados (ID preenchido), mas não está mais vinculada");
        System.out.println("  a uma sessão/PersistenceContext ativo (por exemplo, após o fechamento da transação ou chamada a clear/detach).");
        System.out.println("  Alterações em seus atributos não geram UPDATE automático no banco e acessos a proxies Lazy não inicializados");
        System.out.println("  resultam em LazyInitializationException.");

        // =====================================================================
        // EXERCÍCIO 5: QUESTÃO DE REVISÃO 4
        // =====================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Exercício 5 (Questão de Revisão 4) - Dirty Checking e Métodos de Negócio");
        System.out.println("---------------------------------------------------------------");
        System.out.println("RESPOSTA:");
        System.out.println("O Dirty Checking é apenas um mecanismo de persistência e sincronização de dados do ORM.");
        System.out.println("Ele não substitui métodos de domínio porque um modelo rico deve proteger suas regras invariantes,");
        System.out.println("evitando estado inconsistente. Permitir setters anêmicos públicos cria código procedural frágil.");
        System.out.println("Exemplo: um método 'receberEstoque(quantidade)' valida se a quantidade é positiva e recalcula");
        System.out.println("regras associadas, enquanto um 'setSaldoEstoque' arbitrário permitiria saldos negativos e violaria regras de negócio.");

        // =====================================================================
        // EXERCÍCIO 6: QUESTÃO DE REVISÃO 5
        // =====================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Exercício 6 (Questão de Revisão 5) - Quando nomes de consultas derivadas deixam de ser adequados?");
        System.out.println("---------------------------------------------------------------");
        System.out.println("RESPOSTA:");
        System.out.println("Nomes de métodos derivados tornam-se inadequados quando:");
        System.out.println("1. O nome fica excessivamente longo e ilegível (ex.: findByStatusAndCategoriaIdAndDataAberturaAfterOrderByPrioridadeDesc);");
        System.out.println("2. Há necessidade de JOIN FETCH otimizado ou @EntityGraph para evitar problemas de N+1 consultas;");
        System.out.println("3. Há necessidade de agregações complexas, subconsultas, funções do banco de dados ou paginação com filtros dinâmicos.");
        System.out.println("Nesses cenários, deve-se adotar anotação @Query com JPQL/HQL, Criteria API ou Spring Data Specifications.");

        // =====================================================================
        // EXERCÍCIO 7: QUESTÃO DE REVISÃO 6
        // =====================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Exercício 7 (Questão de Revisão 6) - Desacoplamento entre Exceção de Aplicação e HTTP");
        System.out.println("---------------------------------------------------------------");
        System.out.println("RESPOSTA:");
        System.out.println("Exceções da camada de aplicação (como RecursoNaoEncontradoException ou RecursoDuplicadoException)");
        System.out.println("expressam falhas lógicas e semânticas do caso de uso e não devem ter conhecimento de protocolos de transporte.");
        System.out.println("O mesmo serviço pode ser consumido por uma API REST (HTTP 404/409), por um consumidor de fila RabbitMQ/Kafka,");
        System.out.println("por uma CLI de terminal ou por testes unitários. O mapeamento para códigos de status HTTP pertence exclusivamente");
        System.out.println("à camada de API/Controller (através de @RestControllerAdvice e @ExceptionHandler).");

        System.out.println("\nTodos os exercícios e questões de revisão foram concluídos com êxito!");
    }

    // =========================================================================
    // DOMÍNIO DO PROJETO INDIVIDUAL (Ordens de Serviço)
    // =========================================================================
    public enum StatusOS {
        ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA
    }

    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String msg) { super(msg); }
    }

    public static class RecursoDuplicadoException extends RuntimeException {
        public RecursoDuplicadoException(String msg) { super(msg); }
    }

    public static class CategoriaOS {
        private Long id;
        private String nome;

        public CategoriaOS(String nome) {
            if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome da categoria é obrigatório");
            this.nome = nome.trim();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
    }

    public static class OrdemServico {
        private Long id;
        private String numeroProtocolo;
        private String descricao;
        private LocalDateTime dataAbertura;
        private StatusOS status;
        private CategoriaOS categoria;

        public OrdemServico(String numeroProtocolo, String descricao) {
            if (numeroProtocolo == null || numeroProtocolo.isBlank()) {
                throw new IllegalArgumentException("Protocolo é obrigatório");
            }
            if (descricao == null || descricao.isBlank()) {
                throw new IllegalArgumentException("Descrição é obrigatória");
            }
            this.numeroProtocolo = numeroProtocolo.trim();
            this.descricao = descricao.trim();
            this.dataAbertura = LocalDateTime.now();
            this.status = StatusOS.ABERTA;
        }

        public void associarCategoria(CategoriaOS categoria) {
            this.categoria = Objects.requireNonNull(categoria, "Categoria é obrigatória");
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNumeroProtocolo() { return numeroProtocolo; }
        public String getDescricao() { return descricao; }
        public LocalDateTime getDataAbertura() { return dataAbertura; }
        public StatusOS getStatus() { return status; }
        public CategoriaOS getCategoria() { return categoria; }
    }

    // =========================================================================
    // SIMULADOR DE CONTEXTO E TRANSAÇÃO
    // =========================================================================
    public static class SimuladorJPAContexto {
        private long autoId = 1;
        private final Map<Long, Object> dados = new ConcurrentHashMap<>();
        private final List<Runnable> batchTransacional = new ArrayList<>();
        private boolean transacaoAtiva = false;

        public void begin() {
            this.transacaoAtiva = true;
            this.batchTransacional.clear();
        }

        public void commit() {
            if (transacaoAtiva) {
                batchTransacional.forEach(Runnable::run);
                batchTransacional.clear();
                transacaoAtiva = false;
            }
        }

        public void rollback() {
            batchTransacional.clear();
            transacaoAtiva = false;
        }

        public <T> T persistir(T entidade, Long idAtual, java.util.function.BiConsumer<T, Long> definerId) {
            Long id = idAtual;
            if (id == null) {
                id = autoId++;
                definerId.accept(entidade, id);
            }
            Long finalId = id;
            if (transacaoAtiva) {
                batchTransacional.add(() -> dados.put(finalId, entidade));
            } else {
                dados.put(finalId, entidade);
            }
            return entidade;
        }

        public Map<Long, Object> getDados() { return dados; }
    }

    // =========================================================================
    // REPOSITORIES DA ATIVIDADE
    // =========================================================================
    public static class CategoriaOSRepositorySimulado {
        private final SimuladorJPAContexto ctx;
        public CategoriaOSRepositorySimulado(SimuladorJPAContexto ctx) { this.ctx = ctx; }

        public CategoriaOS save(CategoriaOS cat) {
            return ctx.persistir(cat, cat.getId(), CategoriaOS::setId);
        }

        public Optional<CategoriaOS> findById(Long id) {
            Object o = ctx.getDados().get(id);
            if (o instanceof CategoriaOS c) return Optional.of(c);
            return Optional.empty();
        }
    }

    public static class OrdemServicoRepositorySimulado {
        private final SimuladorJPAContexto ctx;
        public OrdemServicoRepositorySimulado(SimuladorJPAContexto ctx) { this.ctx = ctx; }

        public OrdemServico save(OrdemServico os) {
            return ctx.persistir(os, os.getId(), OrdemServico::setId);
        }

        public boolean existsByNumeroProtocolo(String protocolo) {
            return ctx.getDados().values().stream()
                    .filter(o -> o instanceof OrdemServico)
                    .map(o -> (OrdemServico) o)
                    .anyMatch(os -> os.getNumeroProtocolo().equalsIgnoreCase(protocolo));
        }

        public Optional<OrdemServico> findByNumeroProtocolo(String protocolo) {
            return ctx.getDados().values().stream()
                    .filter(o -> o instanceof OrdemServico)
                    .map(o -> (OrdemServico) o)
                    .filter(os -> os.getNumeroProtocolo().equalsIgnoreCase(protocolo))
                    .findFirst();
        }

        public List<OrdemServico> findByCategoriaId(Long categoriaId) {
            return ctx.getDados().values().stream()
                    .filter(o -> o instanceof OrdemServico)
                    .map(o -> (OrdemServico) o)
                    .filter(os -> os.getCategoria() != null && Objects.equals(os.getCategoria().getId(), categoriaId))
                    .toList();
        }
    }

    // =========================================================================
    // SERVIÇO COM FRONTEIRA TRANSACIONAL
    // =========================================================================
    public static class OrdemServicoServiceSimulado {
        private final OrdemServicoRepositorySimulado osRepo;
        private final CategoriaOSRepositorySimulado catRepo;
        private final SimuladorJPAContexto ctx;

        public OrdemServicoServiceSimulado(OrdemServicoRepositorySimulado osRepo,
                                           CategoriaOSRepositorySimulado catRepo,
                                           SimuladorJPAContexto ctx) {
            this.osRepo = osRepo;
            this.catRepo = catRepo;
            this.ctx = ctx;
        }

        public OrdemServico abrirOrdemServico(OrdemServico os, Long categoriaId) {
            ctx.begin();
            try {
                if (osRepo.existsByNumeroProtocolo(os.getNumeroProtocolo())) {
                    throw new RecursoDuplicadoException("Protocolo de OS já cadastrado no sistema");
                }

                CategoriaOS categoria = catRepo.findById(categoriaId)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria de OS não encontrada com ID: " + categoriaId));

                os.associarCategoria(categoria);
                OrdemServico salva = osRepo.save(os);
                ctx.commit();
                return salva;
            } catch (RuntimeException ex) {
                ctx.rollback();
                throw ex;
            }
        }
    }
}
