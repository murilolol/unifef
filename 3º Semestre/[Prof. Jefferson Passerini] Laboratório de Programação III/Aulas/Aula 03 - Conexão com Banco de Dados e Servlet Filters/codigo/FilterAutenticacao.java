/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Servlet Filter para Gerenciamento de Conexão
 */
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
 * Filtro que intercepta todas as requisições da aplicação web.
 */
@WebFilter(urlPatterns = {"/*"})
public class FilterAutenticacao implements Filter {

    private static Connection conexao;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicializa a conexão ao subir a aplicação
        conexao = SingleConnection.getConnection(); 
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        try {
            // Encaminha a requisição para a próxima etapa/servlet
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("Erro na execução do filtro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        // Encerra a conexão ao desligar a aplicação
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao fechar conexão no filtro: " + ex.getMessage());
            ex.printStackTrace();
        } 
    }
}
