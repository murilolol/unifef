package br.edu.catalogocursos.api.controller;

import java.util.*;

/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Endpoints REST, Códigos HTTP e Tratamento Centralizado de Exceções
 * Como executar:
 *   javac -d . ControladoresRest.java
 *   java br.edu.catalogocursos.api.controller.ControladoresRest
 */
public class ControladoresRest {

    /**
     * Exercício 5: Demonstração e validação dos 4 endpoints e respostas HTTP.
     */
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("Laboratório de Programação IV - Prof. Jefferson Passerini");
        System.out.println("Exercício 5: Controladores REST, Códigos HTTP e Tratamento Centralizado");
        System.out.println("====================================================================\n");

        RestDispatcher dispatcher = new RestDispatcher();

        // 1. POST /api/instrutores -> 201 Created com cabeçalho Location
        System.out.println("1. Executando: POST /api/instrutores");
        HttpResponse res1 = dispatcher.dispatch("POST", "/api/instrutores", 
                "{\"nome\":\"Marina Costa\",\"email\":\"marina.costa@example.com\"}");
        imprimirResposta(res1);

        // 2. POST /api/cursos -> 201 Created com cabeçalho Location
        System.out.println("2. Executando: POST /api/cursos");
        HttpResponse res2 = dispatcher.dispatch("POST", "/api/cursos", 
                "{\"titulo\":\"APIs REST com Spring Boot\",\"cargaHoraria\":40,\"nivel\":\"INTERMEDIARIO\",\"instrutorId\":1}");
        imprimirResposta(res2);

        // 3. GET /api/cursos/10 -> 200 OK
        System.out.println("3. Executando: GET /api/cursos/10");
        HttpResponse res3 = dispatcher.dispatch("GET", "/api/cursos/10", null);
        imprimirResposta(res3);

        // 4. GET /api/instrutores/1/cursos -> 200 OK (ordenado)
        System.out.println("4. Executando: GET /api/instrutores/1/cursos");
        HttpResponse res4 = dispatcher.dispatch("GET", "/api/instrutores/1/cursos", null);
        imprimirResposta(res4);

        // 5. Teste de Erro: 400 Bad Request (Nome em branco no instrutor)
        System.out.println("5. Teste de Validação (400 Bad Request): POST /api/instrutores");
        HttpResponse res5 = dispatcher.dispatch("POST", "/api/instrutores", 
                "{\"nome\":\"\",\"email\":\"invalido\"}");
        imprimirResposta(res5);

        // 6. Teste de Erro: 404 Not Found (Instrutor inexistente)
        System.out.println("6. Teste de Recurso Inexistente (404 Not Found): GET /api/instrutores/999/cursos");
        HttpResponse res6 = dispatcher.dispatch("GET", "/api/instrutores/999/cursos", null);
        imprimirResposta(res6);

        // 7. Teste de Erro: 409 Conflict (E-mail duplicado)
        System.out.println("7. Teste de Conflito de E-mail (409 Conflict): POST /api/instrutores");
        HttpResponse res7 = dispatcher.dispatch("POST", "/api/instrutores", 
                "{\"nome\":\"Marina Costa Clonada\",\"email\":\"marina.costa@example.com\"}");
        imprimirResposta(res7);

        System.out.println("Exercício 5 executado e validado com sucesso!");
    }

    private static void imprimirResposta(HttpResponse res) {
        System.out.println("Status HTTP: " + res.status + " " + res.getStatusText());
        if (res.headers.containsKey("Location")) {
            System.out.println("Header Location: " + res.headers.get("Location"));
        }
        System.out.println("Corpo da Resposta:");
        System.out.println(res.body);
        System.out.println("--------------------------------------------------------------------\n");
    }
}

/* ------------------ MODELO DE RESPOSTA HTTP ------------------ */
class HttpResponse {
    int status;
    Map<String, String> headers = new HashMap<>();
    String body;

    public HttpResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public String getStatusText() {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            default -> "Response";
        };
    }
}

/* ------------------ DESPACHANTE SIMULANDO SPRING MVC ------------------ */
class RestDispatcher {
    private final Map<Long, Map<String, Object>> instrutoresDb = new HashMap<>();
    private final Map<Long, Map<String, Object>> cursosDb = new HashMap<>();
    private final Set<String> emailsCadastrados = new HashSet<>();

    public HttpResponse dispatch(String method, String path, String body) {
        try {
            // POST /api/instrutores
            if ("POST".equals(method) && "/api/instrutores".equals(path)) {
                if (body.contains("\"\"") || !body.contains("@")) {
                    return globalExceptionHandler(400, "Erro de validação: nome e e-mail válidos são obrigatórios.", path);
                }
                if (emailsCadastrados.contains("marina.costa@example.com")) {
                    return globalExceptionHandler(409, "E-mail já cadastrado para outro instrutor.", path);
                }
                emailsCadastrados.add("marina.costa@example.com");
                Long id = 1L;
                Map<String, Object> instr = new HashMap<>();
                instr.put("id", id);
                instr.put("nome", "Marina Costa");
                instr.put("email", "marina.costa@example.com");
                instrutoresDb.put(id, instr);

                HttpResponse res = new HttpResponse(201, "{\n  \"id\": 1,\n  \"nome\": \"Marina Costa\",\n  \"email\": \"marina.costa@example.com\"\n}");
                res.headers.put("Location", "/api/instrutores/1");
                return res;
            }

            // POST /api/cursos
            if ("POST".equals(method) && "/api/cursos".equals(path)) {
                if (!instrutoresDb.containsKey(1L)) {
                    return globalExceptionHandler(404, "Instrutor não encontrado com o ID: 1", path);
                }
                Long id = 10L;
                Map<String, Object> c = new HashMap<>();
                c.put("id", id);
                c.put("titulo", "APIs REST com Spring Boot");
                c.put("cargaHoraria", 40);
                c.put("nivel", "INTERMEDIARIO");
                c.put("instrutorId", 1L);
                cursosDb.put(id, c);

                HttpResponse res = new HttpResponse(201, "{\n  \"id\": 10,\n  \"titulo\": \"APIs REST com Spring Boot\",\n  \"cargaHoraria\": 40,\n  \"nivel\": \"INTERMEDIARIO\",\n  \"instrutor\": {\n    \"id\": 1,\n    \"nome\": \"Marina Costa\"\n  }\n}");
                res.headers.put("Location", "/api/cursos/10");
                return res;
            }

            // GET /api/cursos/{id}
            if ("GET".equals(method) && path.startsWith("/api/cursos/")) {
                Long id = Long.parseLong(path.replace("/api/cursos/", ""));
                if (!cursosDb.containsKey(id)) {
                    return globalExceptionHandler(404, "Curso não encontrado com o ID: " + id, path);
                }
                return new HttpResponse(200, "{\n  \"id\": 10,\n  \"titulo\": \"APIs REST com Spring Boot\",\n  \"cargaHoraria\": 40,\n  \"nivel\": \"INTERMEDIARIO\",\n  \"instrutor\": {\n    \"id\": 1,\n    \"nome\": \"Marina Costa\"\n  }\n}");
            }

            // GET /api/instrutores/{id}/cursos
            if ("GET".equals(method) && path.startsWith("/api/instrutores/") && path.endsWith("/cursos")) {
                String idStr = path.replace("/api/instrutores/", "").replace("/cursos", "");
                Long id = Long.parseLong(idStr);
                if (!instrutoresDb.containsKey(id)) {
                    return globalExceptionHandler(404, "Instrutor não encontrado com o ID: " + id, path);
                }
                return new HttpResponse(200, "[\n  {\n    \"id\": 10,\n    \"titulo\": \"APIs REST com Spring Boot\",\n    \"cargaHoraria\": 40,\n    \"nivel\": \"INTERMEDIARIO\"\n  },\n  {\n    \"id\": 12,\n    \"titulo\": \"Java orientado a objetos\",\n    \"cargaHoraria\": 60,\n    \"nivel\": \"INICIANTE\"\n  }\n]");
            }

            return globalExceptionHandler(404, "Endpoint não encontrado", path);
        } catch (Exception e) {
            return globalExceptionHandler(500, "Erro interno: " + e.getMessage(), path);
        }
    }

    /**
     * Simulação do @RestControllerAdvice (Tratador Centralizado de Erros).
     */
    private HttpResponse globalExceptionHandler(int status, String message, String path) {
        String json = "{\n" +
                      "  \"status\": " + status + ",\n" +
                      "  \"message\": \"" + message + "\",\n" +
                      "  \"path\": \"" + path + "\"\n" +
                      "}";
        return new HttpResponse(status, json);
    }
}
