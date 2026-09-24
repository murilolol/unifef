/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Manutenção e Cadastro de Livros com Java Web e Servlets
 * 
 * Como implantar no Apache Tomcat / JEE:
 *   1. Criar um Dynamic Web Project na IDE (Eclipse, NetBeans ou IntelliJ).
 *   2. Adicionar Livro.java, LivroDAO.java e LivroServlet.java no pacote src.
 *   3. Mapear o Servlet na anotação @WebServlet("/livros") ou via web.xml.
 *   4. Executar no servidor Tomcat e acessar: http://localhost:8080/NomeDoProjeto/livros
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exercício 1: Servlet Controlador para a Manutenção (CRUD) de Livros.
 */
@WebServlet(name = "LivroServlet", urlPatterns = {"/livros"})
public class LivroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private LivroDAO livroDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.livroDAO = LivroDAO.getInstancia();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "listar";
        }

        switch (action) {
            case "novo":
                exibirFormulario(request, response, null);
                break;
            case "editar":
                carregarParaEdicao(request, response);
                break;
            case "excluir":
                excluirLivro(request, response);
                break;
            case "listar":
            default:
                listarLivros(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String idParam = request.getParameter("id");
        String nomeLivro = request.getParameter("nomeLivro");
        String isbn = request.getParameter("isbn");
        String autor = request.getParameter("autor");
        String dataPublicacaoParam = request.getParameter("dataPublicacao");
        String valorLivroParam = request.getParameter("valorLivro");

        int id = 0;
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                id = Integer.parseInt(idParam.trim());
            } catch (NumberFormatException e) {
                id = 0;
            }
        }

        LocalDate dataPublicacao = LocalDate.now();
        if (dataPublicacaoParam != null && !dataPublicacaoParam.trim().isEmpty()) {
            try {
                dataPublicacao = LocalDate.parse(dataPublicacaoParam.trim());
            } catch (DateTimeParseException e) {
                dataPublicacao = LocalDate.now();
            }
        }

        double valorLivro = 0.0;
        if (valorLivroParam != null && !valorLivroParam.trim().isEmpty()) {
            try {
                valorLivro = Double.parseDouble(valorLivroParam.replace(",", ".").trim());
            } catch (NumberFormatException e) {
                valorLivro = 0.0;
            }
        }

        Livro livro = new Livro(id, nomeLivro, isbn, autor, dataPublicacao, valorLivro);

        if (id > 0) {
            livroDAO.atualizar(livro);
        } else {
            livroDAO.inserir(livro);
        }

        response.sendRedirect(request.getContextPath() + "/livros");
    }

    private void listarLivros(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Livro> livros = livroDAO.listarTodos();
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'>");
        out.println("<head>");
        out.println("  <meta charset='UTF-8'>");
        out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("  <title>UniFEF - Manutenção de Livros</title>");
        out.println("  <style>");
        out.println("    body { font-family: 'Segoe UI', Tahoma, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }");
        out.println("    .container { max-width: 1000px; margin: 0 auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        out.println("    h1 { color: #1a365d; border-bottom: 2px solid #2b6cb0; padding-bottom: 10px; margin-top: 0; }");
        out.println("    .btn { display: inline-block; padding: 8px 16px; text-decoration: none; border-radius: 4px; font-weight: bold; cursor: pointer; }");
        out.println("    .btn-novo { background-color: #2b6cb0; color: white; margin-bottom: 20px; }");
        out.println("    .btn-editar { background-color: #d69e2e; color: white; padding: 4px 8px; font-size: 13px; }");
        out.println("    .btn-excluir { background-color: #e53e3e; color: white; padding: 4px 8px; font-size: 13px; margin-left: 5px; }");
        out.println("    table { width: 100%; border-collapse: collapse; margin-top: 10px; }");
        out.println("    th, td { border: 1px solid #e2e8f0; padding: 12px; text-align: left; }");
        out.println("    th { background-color: #edf2f7; color: #2d3748; }");
        out.println("    tr:hover { background-color: #f7fafc; }");
        out.println("    .acoes { width: 150px; text-align: center; }");
        out.println("    .rodape { margin-top: 25px; font-size: 12px; color: #718096; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 10px; }");
        out.println("  </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("  <div class='container'>");
        out.println("    <h1>Controle de Livros - Java Web (Servlets)</h1>");
        out.println("    <p>Disciplina: <strong>Laboratório de Programação III</strong> | Prof. Jefferson Passerini</p>");
        out.println("    <a class='btn btn-novo' href='" + request.getContextPath() + "/livros?action=novo'>+ Cadastrar Novo Livro</a>");
        out.println("    <table>");
        out.println("      <thead>");
        out.println("        <tr>");
        out.println("          <th>ID</th>");
        out.println("          <th>Título do Livro</th>");
        out.println("          <th>ISBN</th>");
        out.println("          <th>Autor</th>");
        out.println("          <th>Data Publicação</th>");
        out.println("          <th>Valor</th>");
        out.println("          <th class='acoes'>Ações</th>");
        out.println("        </tr>");
        out.println("      </thead>");
        out.println("      <tbody>");

        if (livros.isEmpty()) {
            out.println("        <tr><td colspan='7' style='text-align:center;'>Nenhum livro cadastrado no momento.</td></tr>");
        } else {
            for (Livro l : livros) {
                out.println("        <tr>");
                out.println("          <td>" + l.getId() + "</td>");
                out.println("          <td><strong>" + l.getNomeLivro() + "</strong></td>");
                out.println("          <td>" + l.getIsbn() + "</td>");
                out.println("          <td>" + l.getAutor() + "</td>");
                out.println("          <td>" + l.getDataPublicacaoFormatada() + "</td>");
                out.println("          <td>" + l.getValorLivroFormatado() + "</td>");
                out.println("          <td class='acoes'>");
                out.println("            <a class='btn btn-editar' href='" + request.getContextPath() + "/livros?action=editar&id=" + l.getId() + "'>Alterar</a>");
                out.println("            <a class='btn btn-excluir' href='" + request.getContextPath() + "/livros?action=excluir&id=" + l.getId() + "' onclick='return confirm(\"Deseja realmente excluir o livro selecionado?\");'>Excluir</a>");
                out.println("          </td>");
                out.println("        </tr>");
            }
        }

        out.println("      </tbody>");
        out.println("    </table>");
        out.println("    <div class='rodape'>");
        out.println("      UniFEF - Centro Universitário de Santa Fé do Sul | Trabalho de Programação em Grupo");
        out.println("    </div>");
        out.println("  </div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void carregarParaEdicao(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idParam = request.getParameter("id");
        Livro livro = null;
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                livro = livroDAO.buscarPorId(id);
            } catch (NumberFormatException e) {
                livro = null;
            }
        }
        exibirFormulario(request, response, livro);
    }

    private void exibirFormulario(HttpServletRequest request, HttpServletResponse response, Livro livro)
            throws IOException {
        PrintWriter out = response.getWriter();
        boolean edicao = (livro != null);
        String titulo = edicao ? "Alteração de Livro" : "Inclusão de Livro";

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'>");
        out.println("<head>");
        out.println("  <meta charset='UTF-8'>");
        out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("  <title>" + titulo + "</title>");
        out.println("  <style>");
        out.println("    body { font-family: 'Segoe UI', Tahoma, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }");
        out.println("    .container { max-width: 600px; margin: 0 auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        out.println("    h1 { color: #1a365d; border-bottom: 2px solid #2b6cb0; padding-bottom: 10px; margin-top: 0; }");
        out.println("    .form-group { margin-bottom: 15px; }");
        out.println("    label { display: block; margin-bottom: 5px; font-weight: bold; color: #2d3748; }");
        out.println("    input[type='text'], input[type='date'], input[type='number'] { width: 100%; padding: 10px; border: 1px solid #cbd5e0; border-radius: 4px; box-sizing: border-box; }");
        out.println("    .btn-salvar { background-color: #38a169; color: white; border: none; padding: 10px 20px; border-radius: 4px; font-weight: bold; cursor: pointer; }");
        out.println("    .btn-cancelar { background-color: #718096; color: white; text-decoration: none; padding: 10px 20px; border-radius: 4px; font-weight: bold; margin-left: 10px; }");
        out.println("  </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("  <div class='container'>");
        out.println("    <h1>" + titulo + "</h1>");
        out.println("    <form action='" + request.getContextPath() + "/livros' method='POST'>");
        out.println("      <input type='hidden' name='id' value='" + (edicao ? livro.getId() : "0") + "'>");
        
        out.println("      <div class='form-group'>");
        out.println("        <label for='nomeLivro'>Título do Livro (nomeLivro):</label>");
        out.println("        <input type='text' id='nomeLivro' name='nomeLivro' required value='" + (edicao ? livro.getNomeLivro() : "") + "'>");
        out.println("      </div>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='isbn'>ISBN:</label>");
        out.println("        <input type='text' id='isbn' name='isbn' required value='" + (edicao ? livro.getIsbn() : "") + "'>");
        out.println("      </div>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='autor'>Autor:</label>");
        out.println("        <input type='text' id='autor' name='autor' required value='" + (edicao ? livro.getAutor() : "") + "'>");
        out.println("      </div>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='dataPublicacao'>Data de Publicação:</label>");
        out.println("        <input type='date' id='dataPublicacao' name='dataPublicacao' required value='" + (edicao && livro.getDataPublicacao() != null ? livro.getDataPublicacao().toString() : "") + "'>");
        out.println("      </div>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='valorLivro'>Valor do Livro (R$):</label>");
        out.println("        <input type='number' step='0.01' id='valorLivro' name='valorLivro' required value='" + (edicao ? livro.getValorLivro() : "") + "'>");
        out.println("      </div>");

        out.println("      <button type='submit' class='btn-salvar'>Salvar Livro</button>");
        out.println("      <a href='" + request.getContextPath() + "/livros' class='btn-cancelar'>Voltar</a>");
        out.println("    </form>");
        out.println("  </div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void excluirLivro(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                livroDAO.excluir(id);
            } catch (NumberFormatException e) {
                // ID inválido ignorado
            }
        }
        response.sendRedirect(request.getContextPath() + "/livros");
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("UniFEF - Laboratório de Programação III - Prof. Jefferson");
        System.out.println("Classe: LivroServlet (Componente Web HttpServlet)");
        System.out.println("==========================================================");
        System.out.println("Este Servlet gerencia o CRUD completo via requisições HTTP:");
        System.out.println("  - GET  /livros             -> Listagem de livros");
        System.out.println("  - GET  /livros?action=novo -> Formulário de inclusão");
        System.out.println("  - GET  /livros?action=editar&id=X -> Formulário de alteração");
        System.out.println("  - GET  /livros?action=excluir&id=X -> Exclusão e redirecionamento");
        System.out.println("  - POST /livros             -> Processa inserção e atualização");
        System.out.println("----------------------------------------------------------");
        System.out.println("Para execução direta sem necessidade de instalar Tomcat,");
        System.out.println("utilize a classe utilitária: ServidorWebLivros.java");
        System.out.println("==========================================================");
    }
}
