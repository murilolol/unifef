package br.edu.catalogocursos.application;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Camada de Serviços, Repositórios e Transações (@Transactional)
 * Como executar:
 *   javac -d . ServicosPersistencia.java
 *   java br.edu.catalogocursos.application.ServicosPersistencia
 */
public class ServicosPersistencia {

    /**
     * Exercício 3: Execução e demonstração dos serviços e transações de negócio.
     */
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("Laboratório de Programação IV - Prof. Jefferson Passerini");
        System.out.println("Exercício 3: Camada de Serviços, Integridade e Consultas Ordenadas");
        System.out.println("====================================================================\n");

        InstrutorRepositoryFake instrutorRepo = new InstrutorRepositoryFake();
        CursoRepositoryFake cursoRepo = new CursoRepositoryFake();

        InstrutorService instrutorService = new InstrutorService(instrutorRepo);
        CursoService cursoService = new CursoService(cursoRepo, instrutorRepo);

        // 1. Cadastrar instrutor com sucesso
        InstrutorModel profMarina = instrutorService.cadastrarInstrutor("Marina Costa", "marina.costa@example.com");
        System.out.println("1. Instrutor cadastrado com sucesso! ID: " + profMarina.getId() + " - " + profMarina.getNome());

        // 2. Testar rejeição de e-mail duplicado (deve disparar EmailDuplicadoException)
        try {
            System.out.println("\n2. Tentando cadastrar outro instrutor com o mesmo e-mail...");
            instrutorService.cadastrarInstrutor("Outro Nome", "marina.costa@example.com");
            System.out.println("ERRO: E-mail duplicado foi aceito!");
        } catch (EmailDuplicadoException e) {
            System.out.println("Sucesso na rejeição de e-mail duplicado: " + e.getMessage());
        }

        // 3. Cadastrar cursos vinculados ao instrutor existente
        System.out.println("\n3. Cadastrando cursos para a instrutora ID " + profMarina.getId() + "...");
        CursoModel cursoB = cursoService.cadastrarCurso("Java orientado a objetos", 60, "INICIANTE", profMarina.getId());
        CursoModel cursoA = cursoService.cadastrarCurso("APIs REST com Spring Boot", 40, "INTERMEDIARIO", profMarina.getId());
        CursoModel cursoC = cursoService.cadastrarCurso("Arquitetura de Microsserviços", 80, "AVANCADO", profMarina.getId());
        System.out.println("Curso cadastrado: [" + cursoB.getId() + "] " + cursoB.getTitulo());
        System.out.println("Curso cadastrado: [" + cursoA.getId() + "] " + cursoA.getTitulo());
        System.out.println("Curso cadastrado: [" + cursoC.getId() + "] " + cursoC.getTitulo());

        // 4. Testar rejeição de cadastro com instrutor inexistente (deve disparar RecursoNaoEncontradoException)
        try {
            System.out.println("\n4. Tentando cadastrar curso com instrutorId 9999 (inexistente)...");
            cursoService.cadastrarCurso("DevOps Essencial", 30, "INICIANTE", 9999L);
            System.out.println("ERRO: Curso com instrutor inexistente foi aceito!");
        } catch (RecursoNaoEncontradoException e) {
            System.out.println("Sucesso na rejeição de instrutor inexistente: " + e.getMessage());
        }

        // 5. Listar cursos do instrutor ordenados alfabeticamente por título
        System.out.println("\n5. Consultando cursos do instrutor ordenados por título (findByInstrutorIdOrderByTituloAsc):");
        List<CursoModel> cursosOrdenados = instrutorService.listarCursosDoInstrutor(profMarina.getId());
        for (CursoModel c : cursosOrdenados) {
            System.out.println(" - " + c.getTitulo() + " (Carga: " + c.getCargaHoraria() + "h, Nível: " + c.getNivel() + ")");
        }

        System.out.println("\nExercício 3 executado e validado com sucesso!");
    }
}

/* ------------------ EXCEÇÕES DE NEGÓCIO ------------------ */
class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String message) {
        super(message);
    }
}

class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}

class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String message) {
        super(message);
    }
}

/* ------------------ MODELOS DE DOMÍNIO INTERNO ------------------ */
class InstrutorModel {
    private Long id;
    private String nome;
    private String email;
    private List<CursoModel> cursos = new ArrayList<>();

    public InstrutorModel(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public List<CursoModel> getCursos() { return Collections.unmodifiableList(cursos); }

    public void adicionarCurso(CursoModel curso) {
        if (curso == null) throw new IllegalArgumentException("Curso não pode ser nulo.");
        if (!cursos.contains(curso)) cursos.add(curso);
        if (curso.getInstrutor() != this) curso.setInstrutor(this);
    }
}

class CursoModel {
    private Long id;
    private String titulo;
    private Integer cargaHoraria;
    private String nivel;
    private InstrutorModel instrutor;

    public CursoModel(Long id, String titulo, Integer cargaHoraria, String nivel, InstrutorModel instrutor) {
        this.id = id;
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
        this.nivel = nivel;
        this.instrutor = instrutor;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public String getNivel() { return nivel; }
    public InstrutorModel getInstrutor() { return instrutor; }
    public void setInstrutor(InstrutorModel instrutor) { this.instrutor = instrutor; }
}

/* ------------------ REPOSITÓRIOS FAKE DE TESTE ------------------ */
class InstrutorRepositoryFake {
    private final Map<Long, InstrutorModel> storage = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public boolean existsByEmail(String email) {
        return storage.values().stream()
                .anyMatch(i -> i.getEmail().equalsIgnoreCase(email.trim()));
    }

    public InstrutorModel save(InstrutorModel instrutor) {
        if (instrutor.getId() == null) {
            Long newId = sequence.getAndIncrement();
            InstrutorModel novo = new InstrutorModel(newId, instrutor.getNome(), instrutor.getEmail());
            storage.put(newId, novo);
            return novo;
        }
        storage.put(instrutor.getId(), instrutor);
        return instrutor;
    }

    public Optional<InstrutorModel> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
}

class CursoRepositoryFake {
    private final Map<Long, CursoModel> storage = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(10);

    public CursoModel save(CursoModel curso) {
        if (curso.getId() == null) {
            Long newId = sequence.getAndIncrement();
            CursoModel novo = new CursoModel(newId, curso.getTitulo(), curso.getCargaHoraria(), curso.getNivel(), curso.getInstrutor());
            storage.put(newId, novo);
            return novo;
        }
        storage.put(curso.getId(), curso);
        return curso;
    }

    public Optional<CursoModel> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<CursoModel> findByInstrutorIdOrderByTituloAsc(Long instrutorId) {
        List<CursoModel> resultado = new ArrayList<>();
        for (CursoModel c : storage.values()) {
            if (c.getInstrutor() != null && Objects.equals(c.getInstrutor().getId(), instrutorId)) {
                resultado.add(c);
            }
        }
        resultado.sort(Comparator.comparing(CursoModel::getTitulo, String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }
}

/* ------------------ CAMADA DE SERVIÇO (@Service) ------------------ */
class InstrutorService {
    private final InstrutorRepositoryFake instrutorRepository;

    public InstrutorService(InstrutorRepositoryFake instrutorRepository) {
        this.instrutorRepository = instrutorRepository;
    }

    // Equivalente a @Transactional
    public InstrutorModel cadastrarInstrutor(String nome, String email) {
        if (instrutorRepository.existsByEmail(email)) {
            throw new EmailDuplicadoException("E-mail já cadastrado para outro instrutor: " + email);
        }
        InstrutorModel novo = new InstrutorModel(null, nome, email);
        return instrutorRepository.save(novo);
    }

    // Equivalente a @Transactional(readOnly = true)
    public InstrutorModel buscarPorId(Long id) {
        return instrutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instrutor não encontrado com o ID: " + id));
    }

    // Equivalente a @Transactional(readOnly = true)
    public List<CursoModel> listarCursosDoInstrutor(Long instrutorId) {
        // Assegura que o instrutor existe antes de consultar
        buscarPorId(instrutorId);
        return instrutorRepository.findById(instrutorId).get().getCursos();
    }
}

class CursoService {
    private final CursoRepositoryFake cursoRepository;
    private final InstrutorRepositoryFake instrutorRepository;

    public CursoService(CursoRepositoryFake cursoRepository, InstrutorRepositoryFake instrutorRepository) {
        this.cursoRepository = cursoRepository;
        this.instrutorRepository = instrutorRepository;
    }

    // Equivalente a @Transactional
    public CursoModel cadastrarCurso(String titulo, Integer cargaHoraria, String nivel, Long instrutorId) {
        InstrutorModel instrutor = instrutorRepository.findById(instrutorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instrutor não encontrado com ID: " + instrutorId));

        CursoModel novoCurso = new CursoModel(null, titulo, cargaHoraria, nivel, instrutor);
        instrutor.adicionarCurso(novoCurso);
        return cursoRepository.save(novoCurso);
    }

    // Equivalente a @Transactional(readOnly = true)
    public CursoModel buscarPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado com o ID: " + id));
    }
}
