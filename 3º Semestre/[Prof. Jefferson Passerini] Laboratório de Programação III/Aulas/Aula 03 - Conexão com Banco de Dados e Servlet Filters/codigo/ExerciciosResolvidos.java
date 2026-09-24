/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Resolução dos Exercícios Práticos
 * Como executar: Compile e execute esta classe para testar o funcionamento estático dos métodos.
 */
package br.com.aplcurso;

import br.com.aplcurso.utils.SingleConnection;
import java.sql.Connection;

public class ExerciciosResolvidos {

    public static void main(String[] args) {
        System.out.println("--- Exercício 1: Script de Tabela de Categoria ---");
        imprimirScriptCategoria();
        
        System.out.println("\n--- Exercício 2: Teste de Conexão com SingleConnection ---");
        testarConexao();
        
        System.out.println("\n--- Exercício 3: Log de Requisições Simuladas ---");
        simularLogFiltro("/AplCurso/usuario/salvar");
    }

    // Exercício 1
    public static void imprimirScriptCategoria() {
        String sql = "CREATE TABLE categoria (\n"
                   + "    id SERIAL PRIMARY KEY,\n"
                   + "    nome VARCHAR(100) NOT NULL,\n"
                   + "    descricao TEXT\n"
                   + ");\n"
                   + "INSERT INTO categoria (nome, descricao) VALUES ('Informática', 'Produtos e acessório de TI');\n"
                   + "INSERT INTO categoria (nome, descricao) VALUES ('Escritório', 'Materiais de escritório em geral');";
        System.out.println(sql);
    }

    // Exercício 2
    public static void testarConexao() {
        try {
            Connection conn = SingleConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexão obtida com sucesso e está ativa!");
            } else {
                System.out.println("Falha ao obter conexão ativa.");
            }
        } catch (Exception e) {
            System.out.println("Exceção capturada: " + e.getMessage());
        }
    }

    // Exercício 3
    public static void simularLogFiltro(String uri) {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        Connection conn = SingleConnection.getConnection();
        boolean ativa = false;
        try {
            ativa = (conn != null && !conn.isClosed());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[" + agora + "] Requisição interceptada para: " + uri + " | Conexão Ativa: " + ativa);
    }
}
