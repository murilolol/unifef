package br.com.aplcurso.filter;

import br.com.aplcurso.utils.SingleConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor : Prof. Jefferson Passerini
 * Tema      : Gerenciamento de Ciclo de Conexão com Servlet Filter
 *
 * Como Configurar no Servidor (Tomcat):
 *   A anotação @WebFilter("/*") garante que toda requisição HTTP feita à aplicação
 *   passe pelo método doFilter antes de alcançar as Servlets ou JSPs.
 */
@WebFilter(urlPatterns = {"/*"})
public class FilterAutenticacao implements Filter {

    // Referência da conexão gerenciada durante o ciclo de vida do filtro
    private static Connection conexao;

    /**
     * Executado uma única vez quando o servidor web inicializa a aplicação.
     * Responsável por inicializar e preparar a conexão com o banco de dados.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[FilterAutenticacao] Inicializando filtro e abrindo conexao com bdaplcurso...");
        conexao = SingleConnection.getConnection();
    }

    /**
     * Executado a cada requisição HTTP recebida pela aplicação web.
     * Intercepta a requisição, permitindo validações antes e depois do processamento da Servlet.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // Repassa a requisição para o próximo filtro ou servlet de destino
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("[FilterAutenticacao] Erro capturado no fluxo da requisicao: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executado quando a aplicação web é parada ou desfeita no servidor Tomcat.
     * Garante o encerramento gracioso da conexão com o PostgreSQL, evitando vazamento de recursos.
     */
    @Override
    public void destroy() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("[FilterAutenticacao] Conexao com o banco de dados encerrada com sucesso.");
            }
        } catch (SQLException ex) {
            System.out.println("[FilterAutenticacao] Erro ao fechar a conexao no destroy: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Método main para demonstrar o ciclo de vida do Filter sem necessidade de subir o Tomcat.
     */
    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("Simulação do Ciclo de Vida: FilterAutenticacao");
        System.out.println("========================================================");
        FilterAutenticacao filtro = new FilterAutenticacao();
        try {
            // 1. Simula a inicialização no deploy
            filtro.init(null);
            
            // 2. Simula o destroy no undeploy
            filtro.destroy();
            
            System.out.println("Ciclo de vida do Filter demonstrado com êxito.");
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
