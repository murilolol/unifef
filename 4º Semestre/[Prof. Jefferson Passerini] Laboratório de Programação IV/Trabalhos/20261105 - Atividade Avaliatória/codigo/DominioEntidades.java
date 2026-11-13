package br.edu.catalogocursos.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Modelagem de Domínio e Relacionamento 1:N Bidirecional (Instrutor e Curso)
 * Como executar:
 *   javac -d . DominioEntidades.java
 *   java br.edu.catalogocursos.domain.DominioEntidades
 */
public class DominioEntidades {

    /**
     * Exercício 1: Execução e demonstração das regras de negócio do domínio.
     */
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("Laboratório de Programação IV - Prof. Jefferson Passerini");
        System.out.println("Exercício 1: Modelagem de Domínio e Consistência Bidirecional 1:N");
        System.out.println("====================================================================\n");

        // 1. Instanciação de um Instrutor válido
        Instrutor instrutor = new Instrutor(1L, "Marina Costa", "marina.costa@example.com");
        System.out.println("Instrutor instanciado: " + instrutor.getNome() + " (" + instrutor.getEmail() + ")");

        // 2. Instanciação de Cursos válidos
        Curso curso1 = new Curso(10L, "APIs REST com Spring Boot", 40, NivelCurso.INTERMEDIARIO, null);
        Curso curso2 = new Curso(12L, "Java orientado a objetos", 60, NivelCurso.INICIANTE, null);

        // 3. Associação pelo método de domínio obrigatório
        instrutor.adicionarCurso(curso1);
        instrutor.adicionarCurso(curso2);

        // 4. Verificação da consistência bidirecional em memória
        System.out.println("\n--- Verificação de Consistência Bidirecional ---");
        System.out.println("Total de cursos na coleção do instrutor: " + instrutor.getCursos().size());
        for (Curso c : instrutor.getCursos()) {
            System.out.println(" - Curso: '" + c.getTitulo() + "' | Instrutor associado no curso: " + 
                (c.getInstrutor() != null ? c.getInstrutor().getNome() : "NENHUM"));
        }

        boolean consistenciaOk = (curso1.getInstrutor() == instrutor) && 
                                 (curso2.getInstrutor() == instrutor) && 
                                 instrutor.getCursos().contains(curso1) && 
                                 instrutor.getCursos().contains(curso2);
        System.out.println("\nResultado da consistência bidirecional: " + (consistenciaOk ? "SUCESSO (VÁLIDO)" : "FALHA"));

        // 5. Teste de proteção contra duplicação da mesma instância
        instrutor.adicionarCurso(curso1);
        System.out.println("Tentativa de adicionar curso duplicado. Total de cursos: " + instrutor.getCursos().size() + " (Esperado: 2)");

        // 6. Teste de rejeição de curso nulo
        try {
            instrutor.adicionarCurso(null);
            System.out.println("ERRO: curso nulo foi aceito!");
        } catch (IllegalArgumentException e) {
            System.out.println("Sucesso ao rejeitar curso nulo: " + e.getMessage());
        }

        System.out.println("\nExercício 1 executado e validado com sucesso!");
    }
}

/**
 * Enum representando a complexidade do curso.
 */
enum NivelCurso {
    INICIANTE,
    INTERMEDIARIO,
    AVANCADO
}

/**
 * Entidade JPA Instrutor (Lado 1 - Não Proprietário com mappedBy).
 */
class Instrutor {

    private Long id;
    private String nome;
    private String email;
    private List<Curso> cursos = new ArrayList<>();

    public Instrutor() {
    }

    public Instrutor(Long id, String nome, String email) {
        setNome(nome);
        setEmail(email);
        this.id = id;
        this.cursos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().length() < 3 || nome.trim().length() > 120) {
            throw new IllegalArgumentException("Nome do instrutor deve ter entre 3 e 120 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("E-mail do instrutor inválido.");
        }
        this.email = email.trim().toLowerCase();
    }

    /**
     * Retorna lista imutável para não permitir manipulação externa sem adicionarCurso.
     */
    public List<Curso> getCursos() {
        return Collections.unmodifiableList(this.cursos);
    }

    /**
     * Método obrigatório de domínio:
     * 1. Rejeita curso nulo;
     * 2. Associa o curso ao instrutor;
     * 3. Adiciona o curso à coleção;
     * 4. Evita inclusão duplicada da mesma instância.
     */
    public void adicionarCurso(Curso curso) {
        if (curso == null) {
            throw new IllegalArgumentException("Curso não pode ser nulo.");
        }
        if (!this.cursos.contains(curso)) {
            this.cursos.add(curso);
        }
        if (curso.getInstrutor() != this) {
            curso.setInstrutor(this);
        }
    }

    public void removerCurso(Curso curso) {
        if (curso != null && this.cursos.remove(curso)) {
            curso.setInstrutor(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrutor instrutor = (Instrutor) o;
        return Objects.equals(id, instrutor.id) || Objects.equals(email, instrutor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}

/**
 * Entidade JPA Curso (Lado N - Proprietário da Relação com FK instrutor_id).
 */
class Curso {

    private Long id;
    private String titulo;
    private Integer cargaHoraria;
    private NivelCurso nivel;
    private Instrutor instrutor;

    public Curso() {
    }

    public Curso(Long id, String titulo, Integer cargaHoraria, NivelCurso nivel, Instrutor instrutor) {
        this.id = id;
        setTitulo(titulo);
        setCargaHoraria(cargaHoraria);
        setNivel(nivel);
        if (instrutor != null) {
            setInstrutor(instrutor);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().length() < 3 || titulo.trim().length() > 150) {
            throw new IllegalArgumentException("Título do curso deve ter entre 3 e 150 caracteres.");
        }
        this.titulo = titulo.trim();
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        if (cargaHoraria == null || cargaHoraria < 1 || cargaHoraria > 400) {
            throw new IllegalArgumentException("Carga horária deve ser entre 1 e 400 horas.");
        }
        this.cargaHoraria = cargaHoraria;
    }

    public NivelCurso getNivel() {
        return nivel;
    }

    public void setNivel(NivelCurso nivel) {
        if (nivel == null) {
            throw new IllegalArgumentException("Nível do curso é obrigatório.");
        }
        this.nivel = nivel;
    }

    public Instrutor getInstrutor() {
        return instrutor;
    }

    /**
     * Define o instrutor e sincroniza com a coleção do instrutor se necessário.
     */
    public void setInstrutor(Instrutor instrutor) {
        this.instrutor = instrutor;
        if (instrutor != null && !instrutor.getCursos().contains(this)) {
            instrutor.adicionarCurso(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curso curso = (Curso) o;
        return Objects.equals(id, curso.id) && Objects.equals(titulo, curso.titulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo);
    }
}
