/*
 * Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
 * Professor:  Prof. Jefferson Passerini
 * Tema:       Java JSP Cap 5.4 - Desafio 02: Cadastro de Estado
 * Exercício:  Exercício 1
 * Como executar: Implantar em container de servlets (Apache Tomcat 9 ou 10) ou executar via main para teste de rotas.
 */

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador web Servlet para gerenciar o ciclo de vida e operações de Estado.
 */
@WebServlet(name = "EstadoServlet", urlPatterns = {"/EstadoServlet", "/estado"})
public class EstadoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private EstadoDAO estadoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.estadoDAO = new EstadoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processarRequisicao(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processarRequisicao(request, response);
    }

    protected void processarRequisicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");
        if (acao == null || acao.trim().isEmpty()) {
            acao = "listar";
        }

        switch (acao) {
            case "salvar":
                salvar(request, response);
                break;
            case "editar":
                carregarParaEdicao(request, response);
                break;
            case "excluir":
                excluir(request, response);
                break;
            case "novo":
                request.removeAttribute("estadoEdicao");
                listar(request, response);
                break;
            case "listar":
            default:
                listar(request, response);
                break;
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Estado> lista = estadoDAO.listarTodos();
        request.setAttribute("estados", lista);
        RequestDispatcher dispatcher = request.getRequestDispatcher("estados.jsp");
        dispatcher.forward(request, response);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("idEstado");
        String nome = request.getParameter("nomeEstado");
        String sigla = request.getParameter("siglaEstado");

        if (nome != null && !nome.trim().isEmpty() && sigla != null && !sigla.trim().isEmpty()) {
            Estado estado = new Estado();
            estado.setNomeEstado(nome.trim());
            estado.setSiglaEstado(sigla.trim());

            boolean sucesso;
            if (idStr == null || idStr.trim().isEmpty() || idStr.equals("0")) {
                sucesso = estadoDAO.cadastrar(estado);
            } else {
                estado.setIdEstado(Integer.parseInt(idStr));
                sucesso = estadoDAO.atualizar(estado);
            }

            String msg = sucesso ? "operacao_sucesso" : "operacao_erro";
            response.sendRedirect("EstadoServlet?acao=listar&status=" + msg);
        } else {
            response.sendRedirect("EstadoServlet?acao=listar&status=campos_invalidos");
        }
    }

    private void carregarParaEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("idEstado");
        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr);
            Estado estado = estadoDAO.buscarPorId(id);
            request.setAttribute("estadoEdicao", estado);
        }
        listar(request, response);
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("idEstado");
        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr);
            boolean sucesso = estadoDAO.excluir(id);
            String msg = sucesso ? "exclusao_sucesso" : "exclusao_erro";
            response.sendRedirect("EstadoServlet?acao=listar&status=" + msg);
        } else {
            response.sendRedirect("EstadoServlet?acao=listar&status=id_invalido");
        }
    }

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("UniFEF - LP3 - Teste do Servlet Controlador EstadoServlet");
        System.out.println("====================================================");
        System.out.println("Mapeamento de rotas configurado:");
        System.out.println(" - URL Pattern: /EstadoServlet e /estado");
        System.out.println(" - Ações tratadas: listar, salvar, editar, excluir, novo");
        System.out.println("Controlador pronto para implantação no container web Tomcat.");
    }
}
