package br.edu.catalogocursos.test;

import java.util.*;

/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Testes Automatizados Obrigatórios (Unitários, Integração e API)
 * Como executar:
 *   javac -d . TestesObrigatorios.java
 *   java br.edu.catalogocursos.test.TestesObrigatorios
 */
public class TestesObrigatorios {

    private static int testesExecutados = 0;
    private static int testesAprovados = 0;

    /**
     * Exercício 6: Execução da suíte de testes automatizados obrigatórios.
     */
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("Laboratório de Programação IV - Prof. Jefferson Passerini");
        System.out.println("Exercício 6: Execução dos Quatro Testes Automatizados Obrigatórios");
        System.out.println("====================================================================\n");

        System.out.println("Iniciando suíte de testes (equivalente a: ./mvnw clean test)...\n");

        rodarTeste("Teste 1: Instrutor associa um curso e mantém os dois lados consistentes", () -> {
            TesteInstrutor inst = new TesteInstrutor(1L, "Carlos Eduardo", "carlos@fef.edu.br");
            TesteCurso c1 = new TesteCurso(100L, "Estrutura de Dados em Java", 80);

            inst.adicionarCurso(c1);

            afirmar(inst.getCursos().size() == 1, "Coleção do instrutor deve possuir exatamente 1 curso");
            afirmar(inst.getCursos().contains(c1), "Coleção deve conter a instância associada");
            afirmar(c1.getInstrutor() == inst, "Curso deve referenciar a instância do instrutor correspondente");
        });

        rodarTeste("Teste 2: Cadastro de curso com instrutor inexistente é rejeitado", () -> {
            TesteCursoService service = new TesteCursoService();
            boolean lancouExcecao = false;
            try {
                // Tentativa de associar a instrutor ID 999 (não cadastrado)
                service.cadastrarCurso("Banco de Dados Avançado", 60, 999L);
            } catch (RecursoNaoEncontradoTestException e) {
                lancouExcecao = true;
            }
            afirmar(lancouExcecao, "Deveria lançar RecursoNaoEncontradoException para instrutor inexistente");
        });

        rodarTeste("Teste 3: Cadastro HTTP de instrutor inválido retorna 400 Bad Request", () -> {
            TesteApiController controller = new TesteApiController();
            // Payload com nome com menos de 3 caracteres e e-mail mal formatado
            int status = controller.postInstrutor("Al", "email-invalido");
            afirmar(status == 400, "Validação do Bean Validation deve retornar código HTTP 400 Bad Request");
        });

        rodarTeste("Teste 4: Listagem por instrutor retorna somente seus cursos ordenados por título", () -> {
            TesteCursoService service = new TesteCursoService();
            Long prof1Id = service.criarInstrutor("Marina Costa");
            Long prof2Id = service.criarInstrutor("Roberto Santos");

            // Cadastrando cursos fora de ordem alfabética
            service.cadastrarCurso("Java Orientado a Objetos", 60, prof1Id);
            service.cadastrarCurso("APIs REST com Spring Boot", 40, prof1Id);
            service.cadastrarCurso("Algoritmos em C", 80, prof2Id); // Curso de outro instrutor

            List<String> titulosProf1 = service.listarCursosOrdenadosPorTitulo(prof1Id);

            afirmar(titulosProf1.size() == 2, "Deve retornar apenas os 2 cursos do instrutor 1");
            afirmar("APIs REST com Spring Boot".equals(titulosProf1.get(0)), "Primeiro curso deve ser 'APIs REST com Spring Boot'");
            afirmar("Java Orientado a Objetos".equals(titulosProf1.get(1)), "Segundo curso deve ser 'Java Orientado a Objetos'");
        });

        System.out.println("\n====================================================================");
        System.out.println("RELATÓRIO FINAL DOS TESTES");
        System.out.println("Testes Executados: " + testesExecutados);
        System.out.println("Testes Aprovados:  " + testesAprovados);
        System.out.println("Testes Falhos:     " + (testesExecutados - testesAprovados));
        System.out.println("Status:            " + (testesExecutados == testesAprovados ? "BUILD SUCCESS" : "BUILD FAILURE"));
        System.out.println("====================================================================");
    }

    private static void rodarTeste(String nome, Runnable teste) {
        testesExecutados++;
        try {
            teste.run();
            testesAprovados++;
            System.out.println("[OK] " + nome);
        } catch (AssertionError | Exception e) {
            System.out.println("[FALHA] " + nome + " -> Motivo: " + e.getMessage());
        }
    }

    private static void afirmar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}

/* ------------------ MODELOS E SERVIÇOS FOCADOS EM TESTE ------------------ */
class RecursoNaoEncontradoTestException extends RuntimeException {
    public RecursoNaoEncontradoTestException(String msg) { super(msg); }
}

class TesteInstrutor {
    private Long id;
    private String nome;
    private String email;
    private List<TesteCurso> cursos = new ArrayList<>();

    public TesteInstrutor(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public List<TesteCurso> getCursos() { return Collections.unmodifiableList(cursos); }

    public void adicionarCurso(TesteCurso curso) {
        if (curso == null) throw new IllegalArgumentException("Curso nulo");
        if (!cursos.contains(curso)) cursos.add(curso);
        if (curso.getInstrutor() != this) curso.setInstrutor(this);
    }
}

class TesteCurso {
    private Long id;
    private String titulo;
    private Integer cargaHoraria;
    private TesteInstrutor instrutor;

    public TesteCurso(Long id, String titulo, Integer cargaHoraria) {
        this.id = id;
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
    }

    public String getTitulo() { return titulo; }
    public TesteInstrutor getInstrutor() { return instrutor; }
    public void setInstrutor(TesteInstrutor inst) {
        this.instrutor = inst;
        if (inst != null && !inst.getCursos().contains(this)) inst.adicionarCurso(this);
    }
}

class TesteCursoService {
    private final Map<Long, String> instrutores = new HashMap<>();
    private final List<TesteCursoEntry> cursos = new ArrayList<>();
    private long seq = 1;

    static class TesteCursoEntry {
        String titulo;
        Long instrutorId;
        TesteCursoEntry(String t, Long id) { this.titulo = t; this.instrutorId = id; }
    }

    public Long criarInstrutor(String nome) {
        Long id = seq++;
        instrutores.put(id, nome);
        return id;
    }

    public void cadastrarCurso(String titulo, int carga, Long instrutorId) {
        if (!instrutores.containsKey(instrutorId)) {
            throw new RecursoNaoEncontradoTestException("Instrutor não cadastrado: " + instrutorId);
        }
        cursos.add(new TesteCursoEntry(titulo, instrutorId));
    }

    public List<String> listarCursosOrdenadosPorTitulo(Long instrutorId) {
        List<String> list = new ArrayList<>();
        for (TesteCursoEntry e : cursos) {
            if (Objects.equals(e.instrutorId, instrutorId)) list.add(e.titulo);
        }
        Collections.sort(list);
        return list;
    }
}

class TesteApiController {
    public int postInstrutor(String nome, String email) {
        if (nome == null || nome.trim().length() < 3 || email == null || !email.contains("@")) {
            return 400; // Bad Request (Bean Validation)
        }
        return 201; // Created
    }
}
