# 📘 Apostila Prática: Laboratório de Programação III
**Professor:** Prof. Jefferson Passerini  
**Semestre:** 3º Semestre  
**Tecnologias Foco:** Java Web (Servlets, JSP, JDBC, Banco de Dados Relacional)

---

## 🚀 Apresentação
Esta apostila foi desenvolvida com base no conteúdo programático da disciplina de **Laboratório de Programação III**, ministrada pelo Prof. Jefferson Passerini. O material foca no ecossistema **Java Web**, cobrindo desde a preparação do ambiente de desenvolvimento, estruturação de projetos front-end/back-end, conexões com bancos de dados, até a implementação completa de operações **CRUD** (Create, Read, Update, Delete).

---

## 🛠️ Módulo 1: Configuração de Ambiente e Projeto Web
*Baseado nas Aulas 01, 02 e 03 (Fevereiro/Março de 2026)*

Para iniciar o desenvolvimento Java Web, certifique-se de ter instalado:
1. **JDK (Java Development Kit)** - Versão 17 ou superior.
2. **Apache Tomcat** - Servidor de aplicação servlet (Versão 9 ou 10).
3. **IDE** - Eclipse IDE for Enterprise Java and Web Developers ou IntelliJ IDEA Ultimate.
4. **Banco de Dados** - MySQL ou PostgreSQL.

### Estrutura Padrão de um Projeto Web (MVC Simples)
```text
MeuProjetoWeb/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/exemplo/controller/  (Servlets)
│       │   └── com/exemplo/model/       (Classes de Domínio/DAO)
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml
│           ├── index.jsp
│           └── listar.jsp
└── pom.xml (ou configuração de dependências)
```

---

## 🗄️ Módulo 2: Conexão com o Banco de Dados (JDBC)
*Baseado na Aula 04*

Para conectar nossa aplicação Java ao banco de dados relacional, utilizamos o JDBC (*Java Database Connectivity*). Abaixo temos uma classe utilitária padrão para gerenciamento de conexões.

### Código Pronto: `Conexao.java`
```java
package com.exemplo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/banco_sistema?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "sua_senha";

    public static Connection conectar() {
        Connection conexao = null;
        try {
            // Registra o driver do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Conexão realizada com sucesso!");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver do banco não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return conexao;
    }
}
```

---

## 👤 Módulo 3: Criando o Listar Usuário e Estrutura CRUD
*Baseado nas Aulas 05 e Aula de Funções do CRUD*

Para demonstrar a operação de listagem e manutenção de dados, vamos utilizar o modelo conceitual de **Usuário** ou **Livro** (conforme exigido nos trabalhos práticos da disciplina).

### 1. Classe de Modelo (Java Bean): `Usuario.java`
```java
package com.exemplo.model;

public class Usuario {
    private int id;
    private String nome;
    private String email;

    // Construtores
    public Usuario() {}

    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### 2. Classe DAO (Data Access Object): `UsuarioDAO.java`
Esta classe encapsula todas as operações SQL (Inserir, Listar, Atualizar e Excluir).

```java
package com.exemplo.dao;

import com.exemplo.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // CREATE (Inserir)
    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email) VALUES (?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ (Listar todos)
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                usuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // DELETE (Excluir)
    public void excluir(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 🎯 Exercício Resolvido: Trabalho Prático de Livros
*(Baseado no Trabalho de Programação de 2 pontos - Prazo: 08/05/2026)*

**Enunciado:** Construir um programa Java Web utilizando Servlets que faça a manutenção de um cadastro de Livros (`id`, `nomeLivro`, `isbn`, `autor`, `dataPublicação`, `valorLivro`), exibindo a listagem em tela.

### Estrutura da Tabela SQL correspondente:
```sql
CREATE DATABASE sistema_livros;
USE sistema_livros;

CREATE TABLE livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_livro VARCHAR(150) NOT NULL,
    isbn VARCHAR(50) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    data_publicacao DATE,
    valor_livro DECIMAL(10,2)
);
```

### Servlet de Controle: `LivroServlet.java`
```java
package com.exemplo.controller;

import com.exemplo.dao.Conexao;
import com.exemplo.model.Livro; // Assumindo criação prévia do modelo
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/livros")
public class LivroServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Livro> listaLivros = new ArrayList<>();
        String sql = "SELECT * FROM livro";

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livro l = new Livro();
                l.setId(rs.getInt("id"));
                l.setNomeLivro(rs.getString("nome_livro"));
                l.setIsbn(rs.getString("isbn"));
                l.setAutor(rs.getString("autor"));
                l.setDataPublicacao(rs.getDate("data_publicacao"));
                l.setValorLivro(rs.getDouble("valor_livro"));
                listaLivros.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Passa a lista para o JSP exibir
        request.setAttribute("livros", listaLivros);
        request.getRequestDispatcher("listarLivros.jsp").forward(request, response);
    }
}
```

---

## 📝 Resumo das Avaliações da Disciplina
Fique atento aos prazos e entregas oficiais do semestre:
- **Avaliação I (AV1):** 23/03/2026 (Questionário prático/teórico).
- **Trabalho de Programação (2 pts):** Entrega até 08/05/2026 (CRUD de Livros com Servlets e GitHub).
- **Trabalho de Programação (1 pt):** Entrega até 26/05/2026 (Validações e Cadastro de Estados - Desafios JSP).
- **Avaliação II (AVII):** 08/06/2026 (Projeto completo compactado .zip).
- **Avaliação Substitutiva:** 26/06/2026.