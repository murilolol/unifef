/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Padrão Singleton para Conexão JDBC
 */
package br.com.aplcurso.utils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Classe responsável por gerenciar uma conexão única com o banco de dados (Singleton).
 */
public class SingleConnection {
   
    private static Connection conexao = null;
    private static String servidor = "jdbc:postgresql://localhost:5432/bdaplcurso?autoReconnect=true";
    private static String usuario = "postgres";
    private static String senha = "postdba";
    
    static {
        try {
            conectar();
        } catch (Exception ex) {
            System.out.println("Erro ao conectar ao banco de dados na inicialização estática.");
            ex.printStackTrace();
        }
    }
    
    public SingleConnection() throws Exception {
        conectar();
    }
    
    public static void conectar() throws Exception {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexao = DriverManager.getConnection(servidor, usuario, senha);		
                conexao.setAutoCommit(false);
            }
        } catch (Exception ex) {
            throw new Exception("Falha na conexão com o banco de dados: " + ex.getMessage());
        }
    }
    
    public static Connection getConnection() {
        if (conexao == null) {
            System.out.println("DEBUG: conexao está NULA!");
        } else {
            System.out.println("DEBUG: conexao NÃO está nula! " + conexao);
        }
        return conexao;
    }
}
