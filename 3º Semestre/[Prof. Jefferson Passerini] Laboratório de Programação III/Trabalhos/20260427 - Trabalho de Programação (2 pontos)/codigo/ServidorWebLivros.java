/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Manutenção e Cadastro de Livros com Java Web e Servlets
 * 
 * Como executar:
 *   javac Livro.java LivroDAO.java ServidorWebLivros.java
 *   java ServidorWebLivros
 *   Em seguida, abra o navegador e acesse: http://localhost:8080/livros
 */

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercício 1: Servidor HTTP autônomo que executa a aplicação web completa.
 */
public class ServidorWebLivros {

    private static final int PORTA = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA), 0);
        server.createContext("/", new LivrosWebHandler());
        server.createContext("/livros", new LivrosWebHandler());
        server.setExecutor(null);

        System.out.println("==========================================================");
        System.out.println("UniFEF - Laboratório de Programação III - Prof. Jefferson");
        System.out.println("Aplicação Java Web de Cadastro e Manutenção de Livros");
        System.out.println("==========================================================");
        System.out.println("Servidor HTTP iniciado com sucesso na porta: " + PORTA);
        System.out.println("Acesse pelo seu navegador:");
        System.out.println("   -> http://localhost:" + PORTA + "/livros");
        System.out.println("Pressione CTRL+C no terminal para parar o servidor.");
        System.out.println("==========================================================");

        server.start();
    }

    static class LivrosWebHandler implements HttpHandler {
        private final LivroDAO dao = LivroDAO.getInstancia();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQuery(query);

            if ("GET".equalsIgnoreCase(method)) {
                tratarGet(exchange, queryParams);
            } else if ("POST".equalsIgnoreCase(method)) {
                tratarPost(exchange);
            } else {
                enviarResposta(exchange, 405, "text/plain", "Método não permitido");
            }
        }

        private void tratarGet(HttpExchange exchange, Map<String, String> params) throws IOException {
            String action = params.getOrDefault("action", "listar");

            if ("novo".equals(action)) {
                String html = renderizarFormulario(null);
                enviarResposta(exchange, 200, "text/html; charset=UTF-8", html);
            } else if ("editar".equals(action)) {
                int id = Integer.parseInt(params.getOrDefault("id", "0"));
                Livro livro = dao.buscarPorId(id);
                String html = renderizarFormulario(livro);
                enviarResposta(exchange, 200, "text/html; charset=UTF-8", html);
            } else if ("excluir".equals(action)) {
                int id = Integer.parseInt(params.getOrDefault("id", "0"));
                dao.excluir(id);
                redirecionar(exchange, "/livros");
            } else {
                String html = renderizarListagem(dao.listarTodos());
                enviarResposta(exchange, 200, "text/html; charset=UTF-8", html);
            }
        }

        private void tratarPost(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder formData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                formData.append(line);
            }

            Map<String, String> params = parseQuery(formData.toString());
            int id = 0;
            try {
                id = Integer.parseInt(params.getOrDefault("id", "0"));
            } catch (NumberFormatException ignored) {}

            String nomeLivro = params.getOrDefault("nomeLivro", "");
            String isbn = params.getOrDefault("isbn", "");
            String autor = params.getOrDefault("autor", "");
            LocalDate dataPublicacao;
            try {
                dataPublicacao = LocalDate.parse(params.getOrDefault("dataPublicacao", LocalDate.now().toString()));
            } catch (Exception e) {
                dataPublicacao = LocalDate.now();
            }

            double valorLivro = 0.0;
            try {
                valorLivro = Double.parseDouble(params.getOrDefault("valorLivro", "0").replace(",", "."));
            } catch (Exception ignored) {}

            Livro livro = new Livro(id, nomeLivro, isbn, autor, dataPublicacao, valorLivro);
            if (id > 0) {
                dao.atualizar(livro);
            } else {
                dao.inserir(livro);
            }

            redirecionar(exchange, "/livros");
        }

        private String renderizarListagem(List<Livro> livros) {
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>");
            sb.append("<title>UniFEF - Lista de Livros</title>");
            sb.append("<style>");
            sb.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #eef2f6; margin: 0; padding: 25px; }");
            sb.append(".card { max-width: 1000px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }");
            sb.append("h1 { color: #1e3a8a; margin-top: 0; border-bottom: 2px solid #3b82f6; padding-bottom: 12px; }");
            sb.append(".btn { text-decoration: none; padding: 8px 14px; border-radius: 6px; font-size: 14px; font-weight: 600; display: inline-block; }");
            sb.append(".btn-add { background: #2563eb; color: white; margin-bottom: 20px; }");
            sb.append(".btn-edit { background: #f59e0b; color: white; margin-right: 6px; }");
            sb.append(".btn-del { background: #ef4444; color: white; }");
            sb.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            sb.append("th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #e2e8f0; }");
            sb.append("th { background: #f8fafc; color: #475569; font-weight: 700; }");
            sb.append("tr:hover { background: #f1f5f9; }");
            sb.append(".footer { margin-top: 25px; text-align: center; color: #64748b; font-size: 13px; border-top: 1px solid #e2e8f0; padding-top: 15px; }");
            sb.append("</style></head><body>");
            sb.append("<div class='card'>");
            sb.append("<h1>Manutenção de Cadastro de Livros</h1>");
            sb.append("<p><strong>Disciplina:</strong> Laboratório de Programação III | <strong>Professor:</strong> Prof. Jefferson Passerini</p>");
            sb.append("<a href='/livros?action=novo' class='btn btn-add'>+ Incluir Novo Livro</a>");
            sb.append("<table><thead><tr>");
            sb.append("<th>ID</th><th>Título</th><th>ISBN</th><th>Autor</th><th>Data Publicação</th><th>Valor</th><th>Ações</th>");
            sb.append("</tr></thead><tbody>");

            if (livros.isEmpty()) {
                sb.append("<tr><td colspan='7' style='text-align:center;'>Nenhum livro cadastrado.</td></tr>");
            } else {
                for (Livro l : livros) {
                    sb.append("<tr>");
                    sb.append("<td>").append(l.getId()).append("</td>");
                    sb.append("<td><strong>").append(l.getNomeLivro()).append("</strong></td>");
                    sb.append("<td>").append(l.getIsbn()).append("</td>");
                    sb.append("<td>").append(l.getAutor()).append("</td>");
                    sb.append("<td>").append(l.getDataPublicacaoFormatada()).append("</td>");
                    sb.append("<td>").append(l.getValorLivroFormatado()).append("</td>");
                    sb.append("<td>");
                    sb.append("<a href='/livros?action=editar&id=").append(l.getId()).append("' class='btn btn-edit'>Alterar</a>");
                    sb.append("<a href='/livros?action=excluir&id=").append(l.getId()).append("' onclick='return confirm(\"Deseja excluir este livro?\");' class='btn btn-del'>Excluir</a>");
                    sb.append("</td></tr>");
                }
            }

            sb.append("</tbody></table>");
            sb.append("<div class='footer'>UniFEF Sistemas de Informação - Trabalho em Grupo (3 Alunos) | Versionado no GitHub</div>");
            sb.append("</div></body></html>");
            return sb.toString();
        }

        private String renderizarFormulario(Livro livro) {
            boolean isEdit = (livro != null);
            String titulo = isEdit ? "Alterar Cadastro do Livro" : "Cadastrar Novo Livro";
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>");
            sb.append("<title>").append(titulo).append("</title>");
            sb.append("<style>");
            sb.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #eef2f6; margin: 0; padding: 25px; }");
            sb.append(".card { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }");
            sb.append("h1 { color: #1e3a8a; margin-top: 0; border-bottom: 2px solid #3b82f6; padding-bottom: 12px; }");
            sb.append(".form-group { margin-bottom: 15px; }");
            sb.append("label { display: block; margin-bottom: 6px; font-weight: 600; color: #334155; }");
            sb.append("input { width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; font-size: 14px; }");
            sb.append(".btn-submit { background: #16a34a; color: white; border: none; padding: 10px 18px; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; }");
            sb.append(".btn-cancel { background: #64748b; color: white; text-decoration: none; padding: 10px 18px; border-radius: 6px; font-size: 15px; font-weight: 600; margin-left: 10px; display: inline-block; }");
            sb.append("</style></head><body>");
            sb.append("<div class='card'>");
            sb.append("<h1>").append(titulo).append("</h1>");
            sb.append("<form action='/livros' method='POST'>");
            sb.append("<input type='hidden' name='id' value='").append(isEdit ? livro.getId() : 0).append("'>");

            sb.append("<div class='form-group'>");
            sb.append("<label>Título do Livro (nomeLivro):</label>");
            sb.append("<input type='text' name='nomeLivro' required value='").append(isEdit ? livro.getNomeLivro() : "").append("'>");
            sb.append("</div>");

            sb.append("<div class='form-group'>");
            sb.append("<label>ISBN:</label>");
            sb.append("<input type='text' name='isbn' required value='").append(isEdit ? livro.getIsbn() : "").append("'>");
            sb.append("</div>");

            sb.append("<div class='form-group'>");
            sb.append("<label>Autor:</label>");
            sb.append("<input type='text' name='autor' required value='").append(isEdit ? livro.getAutor() : "").append("'>");
            sb.append("</div>");

            sb.append("<div class='form-group'>");
            sb.append("<label>Data de Publicação:</label>");
            sb.append("<input type='date' name='dataPublicacao' required value='").append(isEdit && livro.getDataPublicacao() != null ? livro.getDataPublicacao().toString() : "").append("'>");
            sb.append("</div>");

            sb.append("<div class='form-group'>");
            sb.append("<label>Valor do Livro (R$):</label>");
            sb.append("<input type='number' step='0.01' name='valorLivro' required value='").append(isEdit ? livro.getValorLivro() : "").append("'>");
            sb.append("</div>");

            sb.append("<button type='submit' class='btn-submit'>Salvar Dados</button>");
            sb.append("<a href='/livros' class='btn-cancel'>Cancelar e Voltar</a>");
            sb.append("</form></div></body></html>");
            return sb.toString();
        }

        private void enviarResposta(HttpExchange exchange, int status, String contentType, String corpo) throws IOException {
            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void redirecionar(HttpExchange exchange, String destino) throws IOException {
            exchange.getResponseHeaders().set("Location", destino);
            exchange.sendResponseHeaders(303, -1);
            exchange.close();
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null || query.isEmpty()) return map;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                } else if (pair.length == 1) {
                    map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8), "");
                }
            }
            return map;
        }
    }
}
