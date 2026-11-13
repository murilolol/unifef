package br.edu.catalogocursos.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: DTOs, Mappers e Prevenção de Recursão Cíclica JSON
 * Como executar:
 *   javac -d . ApiContratosDto.java
 *   java br.edu.catalogocursos.api.dto.ApiContratosDto
 */
public class ApiContratosDto {

    /**
     * Exercício 4: Demonstração do desacoplamento de contratos e prevenção de ciclo JSON.
     */
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("Laboratório de Programação IV - Prof. Jefferson Passerini");
        System.out.println("Exercício 4: DTOs, Mapeamento e Prevenção de Recursão Cíclica JSON");
        System.out.println("====================================================================\n");

        // 1. Simulação do payload de entrada: InstrutorRequest
        InstrutorRequest instrutorReq = new InstrutorRequest("Marina Costa", "marina.costa@example.com");
        System.out.println("1. DTO de Entrada (InstrutorRequest):");
        System.out.println(instrutorReq.toJson());

        // 2. Simulação da resposta de Instrutor (InstrutorResponse - sem coleção de cursos)
        InstrutorResponse instrutorResp = new InstrutorResponse(1L, instrutorReq.getNome(), instrutorReq.getEmail());
        System.out.println("\n2. DTO de Resposta (InstrutorResponse):");
        System.out.println(instrutorResp.toJson());

        // 3. Simulação do payload de entrada: CursoRequest
        CursoRequest cursoReq = new CursoRequest("APIs REST com Spring Boot", 40, "INTERMEDIARIO", 1L);
        System.out.println("\n3. DTO de Entrada (CursoRequest):");
        System.out.println(cursoReq.toJson());

        // 4. Simulação da resposta de Curso (CursoResponse - com resumo do instrutor)
        InstrutorResumo instrutorResumo = new InstrutorResumo(1L, "Marina Costa");
        CursoResponse cursoResp = new CursoResponse(10L, cursoReq.getTitulo(), cursoReq.getCargaHoraria(), cursoReq.getNivel(), instrutorResumo);
        System.out.println("\n4. DTO de Resposta (CursoResponse com InstrutorResumo):");
        System.out.println(cursoResp.toJson());

        // 5. Simulação da listagem de cursos por instrutor (List<CursoResumoResponse>)
        List<CursoResumoResponse> listaCursos = new ArrayList<>();
        listaCursos.add(new CursoResumoResponse(10L, "APIs REST com Spring Boot", 40, "INTERMEDIARIO"));
        listaCursos.add(new CursoResumoResponse(12L, "Java orientado a objetos", 60, "INICIANTE"));

        System.out.println("\n5. DTO de Resposta da Listagem (GET /api/instrutores/1/cursos):");
        System.out.println("[");
        for (int i = 0; i < listaCursos.size(); i++) {
            System.out.print("  " + listaCursos.get(i).toJson().replace("\n", "\n  "));
            if (i < listaCursos.size() - 1) System.out.println(",");
            else System.out.println();
        }
        System.out.println("]");

        System.out.println("\nPor que isso impede o loop infinito?");
        System.out.println("-> CursoResponse contém apenas InstrutorResumo (id, nome).");
        System.out.println("-> InstrutorResumo NÃO possui a coleção de cursos.");
        System.out.println("-> O ciclo [curso -> instrutor -> cursos -> instrutor...] foi interrompido com sucesso!");

        System.out.println("\nExercício 4 executado e validado com sucesso!");
    }
}

/* ------------------ DTOs DE ENTRADA (REQUEST) ------------------ */
class InstrutorRequest {
    private String nome;
    private String email;

    public InstrutorRequest(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }

    public String toJson() {
        return "{\n" +
               "  \"nome\": \"" + nome + "\",\n" +
               "  \"email\": \"" + email + "\"\n" +
               "}";
    }
}

class CursoRequest {
    private String titulo;
    private Integer cargaHoraria;
    private String nivel;
    private Long instrutorId;

    public CursoRequest(String titulo, Integer cargaHoraria, String nivel, Long instrutorId) {
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
        this.nivel = nivel;
        this.instrutorId = instrutorId;
    }

    public String getTitulo() { return titulo; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public String getNivel() { return nivel; }
    public Long getInstrutorId() { return instrutorId; }

    public String toJson() {
        return "{\n" +
               "  \"titulo\": \"" + titulo + "\",\n" +
               "  \"cargaHoraria\": " + cargaHoraria + ",\n" +
               "  \"nivel\": \"" + nivel + "\",\n" +
               "  \"instrutorId\": " + instrutorId + "\n" +
               "}";
    }
}

/* ------------------ DTOs DE SAÍDA (RESPONSE) ------------------ */
class InstrutorResponse {
    private Long id;
    private String nome;
    private String email;

    public InstrutorResponse(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    public String toJson() {
        return "{\n" +
               "  \"id\": " + id + ",\n" +
               "  \"nome\": \"" + nome + "\",\n" +
               "  \"email\": \"" + email + "\"\n" +
               "}";
    }
}

class InstrutorResumo {
    private Long id;
    private String nome;

    public InstrutorResumo(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }

    public String toJson() {
        return "{\n" +
               "    \"id\": " + id + ",\n" +
               "    \"nome\": \"" + nome + "\"\n" +
               "  }";
    }
}

class CursoResponse {
    private Long id;
    private String titulo;
    private Integer cargaHoraria;
    private String nivel;
    private InstrutorResumo instrutor;

    public CursoResponse(Long id, String titulo, Integer cargaHoraria, String nivel, InstrutorResumo instrutor) {
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
    public InstrutorResumo getInstrutor() { return instrutor; }

    public String toJson() {
        return "{\n" +
               "  \"id\": " + id + ",\n" +
               "  \"titulo\": \"" + titulo + "\",\n" +
               "  \"cargaHoraria\": " + cargaHoraria + ",\n" +
               "  \"nivel\": \"" + nivel + "\",\n" +
               "  \"instrutor\": " + (instrutor != null ? instrutor.toJson() : "null") + "\n" +
               "}";
    }
}

class CursoResumoResponse {
    private Long id;
    private String titulo;
    private Integer cargaHoraria;
    private String nivel;

    public CursoResumoResponse(Long id, String titulo, Integer cargaHoraria, String nivel) {
        this.id = id;
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
        this.nivel = nivel;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public String getNivel() { return nivel; }

    public String toJson() {
        return "{\n" +
               "  \"id\": " + id + ",\n" +
               "  \"titulo\": \"" + titulo + "\",\n" +
               "  \"cargaHoraria\": " + cargaHoraria + ",\n" +
               "  \"nivel\": \"" + nivel + "\"\n" +
               "}";
    }
}
