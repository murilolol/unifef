# 🤖 Resumos-IA · Laboratório de Programação III
**Professor:** Jefferson Passerini · **Semestre:** 3º Semestre · **Foco:** Java Web (Servlets, JSP, JDBC, CRUD)

Material de apoio gerado por IA para revisão da disciplina, consolidado em um único
documento: resumo executivo, exercícios práticos com código real, simulado comentado,
cheatsheet de revisão rápida e diagramas de arquitetura/UML.

---

## 🧭 Índice
1. [Resumo Executivo](#-resumo-executivo)
2. [Exercícios Práticos Implementados](#-exercícios-práticos-implementados)
3. [Simulado Comentado](#-simulado-comentado)
4. [CheatSheet de Revisão Rápida](#-cheatsheet-de-revisão-rápida)
5. [Diagramas e Modelagem](#️-diagramas-e-modelagem)
6. [Apresentação de Revisão em Slides](#-apresentação-de-revisão-em-slides)
7. [Flashcards para Anki](#-flashcards-para-anki)
8. [Dataset de Perguntas e Respostas (JSONL)](#-dataset-de-perguntas-e-respostas-jsonl)

---

## 📖 Resumo Executivo

### 1. Visão Geral e Objetivos da Matéria
A disciplina de **Laboratório de Programação III** (oferecida no 3º semestre do curso
de Sistemas de Informação sob a tutela do Prof. Jefferson Passerini) tem como foco
principal a capacitação prática no desenvolvimento de **Aplicações Web utilizando a
linguagem Java** (Ecossistema Java Web / JSP / Servlets).

O objetivo central é transicionar o conhecimento de lógica e programação orientada a
objetos para o ambiente corporativo web, capacitando o aluno a projetar, estruturar,
codificar e persistir dados em sistemas web completos, aplicando o padrão
arquitetural clássico e operando sobre fluxos de requisição/resposta HTTP,
manipulação de interfaces (Front-end) e comunicação com Bancos de Dados Relacionais.

### 2. Conceitos-Chave e Terminologia Fundamental
* **Ambiente de Desenvolvimento (IDE / Servidor Web):** Configuração de ferramentas e containers web (como Apache Tomcat) para hospedar e executar aplicações Java EE/Jakarta EE.
* **Java Web / Servlets:** Classes Java que interceptam e respondem a requisições HTTP, atuando como o núcleo controlador (Controller) das aplicações.
* **JSP (JavaServer Pages):** Tecnologia que permite embutir código Java em páginas HTML, facilitando a construção dinâmica de interfaces de usuário (Front-end).
* **Arquitetura e Estrutura de Projeto:** Organização de pacotes e diretórios seguindo boas práticas de separação de responsabilidades (camadas de visão, controle e persistência).
* **Persistência e Conexão com Banco de Dados:** Estabelecimento de pontes de comunicação (JDBC/Drivers) entre a aplicação Java Web e sistemas gerenciadores de banco de dados relacionais.
* **CRUD (Create, Read, Update, Delete):** O acrônimo fundamental para as quatro operações básicas de manutenção de dados (Incluir, Listar/Consultar, Alterar e Excluir).

### 3. Principais Módulos Abordados
1. **Configuração de Ambiente (Aula 01):** Instalação e parametrização das ferramentas de desenvolvimento, JDK, IDEs compatíveis e configuração do servidor de aplicação (Tomcat) para suporte a projetos dinâmicos web.
2. **Criação de Projetos Web e Estrutura Front-end (Aulas 02 e 03):** Criação da estrutura de diretórios padrão de um projeto web Java (Dynamic Web Project / Maven), definição do arquivo descritor de deployment (`web.xml`), e construção da interface do usuário utilizando HTML/JSP para captação de dados.
3. **Conexão com o Banco de Dados (Aula 04):** Implementação das classes de conexão utilizando JDBC. Envolve o carregamento do driver de banco de dados, abertura de conexões seguras, tratamento de exceções de SQL e encerramento adequado de recursos (`Connection`, `PreparedStatement`).
4. **Listagem de Dados (Aula 05):** Execução de comandos SQL do tipo `SELECT` a partir do Servidor, mapeamento do ResultSet para objetos de domínio (ex: classe `Usuario`) e envio desses dados para a página JSP exibi-los dinamicamente em formato de tabela.
5. **Implementação Completa do CRUD (Aula 06 em diante):**
   * **Create (Incluir):** Captura de parâmetros via requisição HTTP (`request.getParameter`), persistência via `INSERT` no banco.
   * **Read (Listar):** Consulta e renderização dos registros.
   * **Update (Alterar):** Recuperação do registro por ID, preenchimento de formulário e execução de comando `UPDATE`.
   * **Delete (Excluir):** Remoção do registro com base em identificadores únicos (`DELETE FROM`).

### 4. Relações com o Mercado e Prática Profissional
O ecossistema Java corporativo continua sendo um dos pilares mais robustos no mercado
de desenvolvimento de software empresarial, sistemas bancários, e-commerce e APIs de
alta escala. Embora o mercado moderno utilize frameworks avançados (como Spring
Boot), a compreensão profunda de **Servlets, JSP e JDBC** fornece a base técnica
exata (*under the hood*) de como a web funciona em Java — saber como uma requisição
HTTP viaja do navegador até o banco de dados sem "mágicas" de frameworks é um
diferencial crítico para um analista de sistemas. Os trabalhos práticos (sistema de
manutenção de cadastros de Livros, desafios de validação de e-mails e estados) simulam
demandas reais de software corporativo, exigindo trabalho em equipe, versionamento de
código (Git/GitHub) e entrega de soluções funcionais ponta a ponta.

### 5. Dicas de Ouro para Estudo e Provas
* **Domine o Fluxo Requisição-Resposta:** entenda como um formulário HTML envia dados via POST/GET, como o Servlet os intercepta (`request.getParameter`), processa a regra de negócio/banco, e redireciona ou despacha (`RequestDispatcher`) a resposta de volta para o JSP.
* **Pratique a Sintaxe de Conexão JDBC:** erros ao abrir conexões ou esquecer de fechar `Statements` geram falhas críticas — tenha um template mental bem memorizado.
* **Atenção aos Detalhes dos Modelos:** garanta que os atributos da classe casem perfeitamente com as colunas do banco e com os nomes dos campos (`name="..."`) nos formulários HTML/JSP.
* **Utilize os Recursos Complementares:** revise os roteiros do Notion (links oficiais das aulas e desafios práticos), pois trazem o passo a passo exato exigido nas avaliações.

---

## 💻 Exercícios Práticos Implementados

### Módulo 1 — Configuração de Ambiente e Projeto Web
*Baseado nas Aulas 01, 02 e 03.* Ferramentas necessárias: **JDK 17+**, **Apache Tomcat 9/10**, **Eclipse IDE for Enterprise Java / IntelliJ IDEA Ultimate**, **MySQL ou PostgreSQL**.

Estrutura padrão de um projeto web (MVC simples):
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

### Módulo 2 — Conexão com o Banco de Dados (JDBC)
*Baseado na Aula 04.* Classe utilitária padrão para gerenciamento de conexões:
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

### Módulo 3 — Listar Usuário e Estrutura CRUD
*Baseado na Aula 05 e na aula de Funções do CRUD.*

**Classe de Modelo (`Usuario.java`):**
```java
package com.exemplo.model;

public class Usuario {
    private int id;
    private String nome;
    private String email;

    public Usuario() {}

    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

**Classe DAO (`UsuarioDAO.java`)** — encapsula as operações SQL (Inserir, Listar, Excluir):
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

### Exercício Resolvido — Trabalho Prático de Livros
*(Trabalho de Programação de 2 pontos — prazo 08/05/2026)*

**Enunciado:** construir um programa Java Web utilizando Servlets que faça a
manutenção de um cadastro de Livros (`id`, `nomeLivro`, `isbn`, `autor`,
`dataPublicação`, `valorLivro`), exibindo a listagem em tela.

Tabela SQL correspondente:
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

Servlet de controle (`LivroServlet.java`):
```java
package com.exemplo.controller;

import com.exemplo.dao.Conexao;
import com.exemplo.model.Livro;
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

        request.setAttribute("livros", listaLivros);
        request.getRequestDispatcher("listarLivros.jsp").forward(request, response);
    }
}
```

### Resumo das Avaliações da Disciplina
- **Avaliação I (AV1):** 23/03/2026 (questionário prático/teórico).
- **Trabalho de Programação (2 pts):** entrega até 08/05/2026 (CRUD de Livros com Servlets e GitHub).
- **Trabalho de Programação (1 pt):** entrega até 26/05/2026 (validações e cadastro de Estados — desafios JSP).
- **Avaliação II (AVII):** 08/06/2026 (projeto completo compactado `.zip`).
- **Avaliação Substitutiva:** 26/06/2026.

---

## 📝 Simulado Comentado

**Conteúdo abrangido:** Ambiente Java Web, Estrutura Front-end/Back-end, Conexão com
Banco de Dados, Listagem e Operações de CRUD (Incluir, Alterar, Excluir).

### Parte 1 — Múltipla Escolha

**Questão 1.** Na Aula 01, o foco inicial da disciplina é a preparação do ambiente de
desenvolvimento. Qual alternativa descreve corretamente os componentes essenciais
para iniciar projetos Java Web?
A) Apenas um editor de texto simples e o interpretador de comandos do sistema operacional.
B) Configuração de JDK, IDE de desenvolvimento (Eclipse/IntelliJ) e um Servidor de Aplicação/Web (Apache Tomcat).
C) Apenas a instalação do framework Angular e Node.js.
D) Um banco de dados NoSQL e um compilador C++.
E) Um navegador web atualizado e um editor de imagens vetoriais.
> **Gabarito: B** — o desenvolvimento Java Web tradicional (JSP/Servlets) exige o JDK para compilação, uma IDE para agilizar o código e um container web/servlet como o Apache Tomcat para hospedar e executar as aplicações.

**Questão 2.** Sobre a criação de um projeto web em Java (Aula 02), qual é a estrutura
de diretórios padrão recomendada (Maven/Dynamic Web Project) no contexto de Servlets e JSP?
A) `src/` contendo apenas arquivos HTML estáticos.
B) `src/main/java` para as classes Java (Servlets, DAOs, Models) e diretórios de webapp/WebContent para JSP e arquivos estáticos.
C) Apenas um arquivo único `.exe` contendo toda a aplicação compilada.
D) Pastas separadas exclusivamente para scripts em Python e Ruby.
E) Uma estrutura baseada em blocos de notas salvos na raiz do sistema operacional.
> **Gabarito: B** — o código-fonte backend fica separado dos recursos web (JSP, CSS/JS, `web.xml`).

**Questão 3.** No contexto de JSP, qual é o papel principal das páginas JSP na arquitetura da aplicação (Aula 03)?
A) Substituir completamente o banco de dados relacional.
B) Atuar exclusivamente como controladoras de requisições de rede de baixo nível.
C) Servir como camada de visualização (View), misturando HTML com elementos dinâmicos em Java.
D) Compilar o código-fonte em linguagem de máquina nativa.
E) Gerenciar transações complexas de concorrência diretamente no hardware.
> **Gabarito: C** — o JSP é voltado para a camada de apresentação (View).

**Questão 4.** Na Aula 04, qual API padrão do Java é usada para conectar e executar SQL em bancos relacionais?
A) Swing API B) **JDBC (Java Database Connectivity)** C) JavaFX Database Engine D) ServletContext Interface E) DOM Parser
> **Gabarito: B**

**Questão 5.** Para listar registros (Aula 05), qual classe do JDBC executa `SELECT` e retorna os dados?
A) `HttpServletRequest` B) `PrintWriter` C) **`ResultSet`** D) `HttpSession` E) `WebAppContext`
> **Gabarito: C** — o resultado da consulta vem encapsulado em um `ResultSet`, percorrido linha a linha.

**Questão 6.** Qual método HTTP formulários usam comumente para enviar dados de cadastro (inclusão/alteração) a um Servlet?
A) `GET` B) **`POST`** C) `TRACE` D) `OPTIONS` E) `HEAD`
> **Gabarito: B** — envia os parâmetros no corpo da requisição, com mais segurança e capacidade que `GET`.

**Questão 7.** Que padrão de projeto separa as regras de acesso ao banco das regras de negócio e da interface web (trabalho de Livros)?
A) Singleton estrito B) **DAO (Data Access Object)** C) Observer bidirecional assíncrono D) Factory Method de arquivos binários E) MVC via Servlets/JSP
> **Gabarito: E (arquitetural) / B (persistência)** — MVC separa camadas globalmente (Servlet=Controller, JSP=View, Model/DAO=dados); DAO isola especificamente o acesso ao banco.

**Questão 8.** Qual a principal vantagem do `PreparedStatement` sobre o `Statement` comum?
A) Acelerar carregamento de imagens B) **Prevenir SQL Injection via parametrização** C) Rodar só em Linux D) Dispensar drivers de banco E) Converter JSP em PDF
> **Gabarito: B**

**Questão 9.** Onde deve ocorrer a validação robusta de regras de negócio (ex: campo e-mail) antes de persistir dados?
A) Só no navegador via CSS B) No `server.xml` do Tomcat C) **No back-end (Servlet/Model), complementada por validação visual no front-end** D) No HTML estático E) Em triggers de banco em Assembly
> **Gabarito: C** — o front-end pode ser burlado, então a validação no servidor é mandatória.

**Questão 10.** Onde os projetos práticos da disciplina devem ser versionados e disponibilizados?
A) **GitHub** B) Photoshop C) Microsoft Word D) WinRAR E) FileZilla Server
> **Gabarito: A**

### Parte 2 — Discursivas e Estudos de Caso

**Questão 11.** Explique a importância da configuração correta do ambiente (JDK, IDE, Tomcat) nas Aulas 01/02, e o que acontece se o Tomcat não estiver integrado corretamente à IDE.
> **Resposta esperada:** a configuração correta garante que o compilador encontre as bibliotecas da API Servlet (`javax.servlet`/`jakarta.servlet`). Sem o Tomcat integrado, a aplicação não gera o `.war` corretamente nem faz deploy local; ao acessar a URL de um Servlet, o servidor retorna erro de compilação, caminho ou HTTP 404, pois o container não sabe onde/como executar as classes controladoras.

**Questão 12.** Escreva um trecho JDBC (Aula 04) que abra conexão com um banco MySQL via `DriverManager`.
```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    public static Connection conectar() {
        Connection conexao = null;
        String url = "jdbc:mysql://localhost:3306/nome_do_banco";
        String usuario = "root";
        String senha = "sua_senha";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexao = DriverManager.getConnection(url, usuario, senha);
            System.out.println("Conexão realizada com sucesso!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver do banco não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return conexao;
    }
}
```

**Questão 13.** Descreva o fluxo lógico completo, da requisição do navegador até a exibição na JSP, para listar livros cadastrados (Aula 05 + trabalho de Livros).
> **Resposta esperada:**
> 1. **Requisição:** usuário acessa uma URL (ex: `/listarLivros`).
> 2. **Controller (Servlet):** a requisição é interceptada pelo Servlet mapeado.
> 3. **Model/DAO:** o Servlet aciona `LivroDAO`, que executa `SELECT * FROM livro` via JDBC.
> 4. **Processamento:** o `ResultSet` retornado é convertido em `List<Livro>`.
> 5. **Encaminhamento:** o Servlet guarda a lista em `request.setAttribute("livros", listaLivros)` e usa `RequestDispatcher` para encaminhar a `listar.jsp`.
> 6. **View (JSP):** a página usa `<c:forEach>` (JSTL) para renderizar a tabela HTML com os dados.

**Questão 14.** Como o sistema diferencia se deve inserir ou atualizar um registro ao receber dados de um formulário (trabalho de Livros)?
> **Resposta esperada:** pela verificação do campo `id`. Se estiver vazio, nulo ou zero, é um registro novo → `INSERT`. Se tiver um valor numérico existente, é uma edição → `UPDATE` filtrando por esse `id` — geralmente auxiliado por um campo oculto (`<input type="hidden" name="id" value="...">`) no formulário.

**Questão 15.** Elabore a estrutura da classe Java `Livro` com os atributos especificados pelo professor (`id`, `nomeLivro`, `isbn`, `autor`, `dataPublicacao`, `valorLivro`), incluindo construtores, getters e setters.
> **Nota de curadoria:** o arquivo original desta questão (`Simulados/Simulado-Questoes-Comentadas.md`) foi salvo de forma incompleta, cortado antes do gabarito. A resposta abaixo é a implementação-padrão (POJO com construtor, getters e setters) para exatamente os atributos que o próprio enunciado já especifica — não introduz nenhum dado ou requisito novo.
>
> **Resposta esperada:**
> ```java
> package com.exemplo.model;
>
> import java.util.Date;
>
> public class Livro {
>     private int id;
>     private String nomeLivro;
>     private String isbn;
>     private String autor;
>     private Date dataPublicacao;
>     private double valorLivro;
>
>     public Livro() {}
>
>     public Livro(int id, String nomeLivro, String isbn, String autor, Date dataPublicacao, double valorLivro) {
>         this.id = id;
>         this.nomeLivro = nomeLivro;
>         this.isbn = isbn;
>         this.autor = autor;
>         this.dataPublicacao = dataPublicacao;
>         this.valorLivro = valorLivro;
>     }
>
>     public int getId() { return id; }
>     public void setId(int id) { this.id = id; }
>
>     public String getNomeLivro() { return nomeLivro; }
>     public void setNomeLivro(String nomeLivro) { this.nomeLivro = nomeLivro; }
>
>     public String getIsbn() { return isbn; }
>     public void setIsbn(String isbn) { this.isbn = isbn; }
>
>     public String getAutor() { return autor; }
>     public void setAutor(String autor) { this.autor = autor; }
>
>     public Date getDataPublicacao() { return dataPublicacao; }
>     public void setDataPublicacao(Date dataPublicacao) { this.dataPublicacao = dataPublicacao; }
>
>     public double getValorLivro() { return valorLivro; }
>     public void setValorLivro(double valorLivro) { this.valorLivro = valorLivro; }
> }
> ```
> Essa classe é o modelo (Model) usado pelo `LivroServlet` e por um futuro `LivroDAO`, espelhando exatamente as colunas da tabela `livro` criada em SQL.

---

## ⚡ CheatSheet de Revisão Rápida

**Disciplina:** Lab. Programação III (3º Sem. — Java Web / JSP / Servlets)

**1. Ambiente & Estrutura de Projeto Web**
- Pilares: Java Web, Servlets, JSP, Banco de Dados (JDBC).
- Estrutura: páginas JSP para interface, Servlets para controle de fluxo (MVC simplificado).
- Deploy: servidor de aplicação compatível com Servlet (ex: Apache Tomcat).

**2. Banco de Dados & Conexão**
- Conexão (JDBC): `DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`.
- Operações básicas: `INSERT INTO` (incluir) · `SELECT * FROM` (listar) · `UPDATE ... SET ... WHERE id = ?` (alterar) · `DELETE FROM ... WHERE id = ?` (excluir).

**3. Fluxo do CRUD**
1. **Create:** formulário JSP → Servlet → `INSERT` no banco.
2. **Read:** Servlet consulta (`SELECT`) → lista → JSP exibe em `<table>`.
3. **Update:** carrega dados pelo `id` → edita no formulário → `UPDATE` no banco.
4. **Delete:** recebe `id` via parâmetro → `DELETE` no banco.

**4. Padrões de Entidade**
```java
// Atributos típicos exigidos em trabalhos
private int id;
private String nome; // ou nomeLivro / email
private String autor; // ou isbn / dataPublicacao / valorLivro
```
Validações comuns: campos obrigatórios (ex: formato de e-mail) antes de persistir.

**5. Checklist rápido para provas e trabalhos**
- [ ] Configurar corretamente o projeto Web e o Tomcat na IDE.
- [ ] Garantir a classe de conexão com o Banco de Dados ativa.
- [ ] Mapear as Servlets (`@WebServlet`) corretamente.
- [ ] Validar se os formulários HTML/JSP apontam para os métodos corretos (`GET`/`POST`).
- [ ] Tratar parâmetros nulos ou conversões de tipos (`Integer.parseInt(request.getParameter("id"))`).

---

## 🗺️ Diagramas e Modelagem

### 1. Diagrama de Classes UML (Domínio do Sistema)
Entidades principais das aulas de estruturação de projetos web e CRUD (`Usuario` e `Livro`).

```mermaid
classDiagram
    class Usuario {
        - int id
        - String nome
        - String email
        - String senha
        + getId() int
        + setId(int id)
        + getNome() String
        + setNome(String nome)
        + getEmail() String
        + setEmail(String email)
    }

    class Livro {
        - int id
        - String nomeLivro
        - String isbn
        - String autor
        - Date dataPublicacao
        - double valorLivro
        + cadastrarLivro() void
        + alterarLivro() void
        + excluirLivro() void
    }

    class UsuarioDAO {
        - Connection conexao
        + inserir(Usuario u) boolean
        + listar() List~Usuario~
        + alterar(Usuario u) boolean
        + excluir(int id) boolean
    }

    class ConexaoBD {
        + static getConnection() Connection
    }

    UsuarioDAO --> ConexaoBD : usa
    UsuarioDAO ..> Usuario : manipula
```
A classe de acesso a dados (`UsuarioDAO`) gerencia a persistência utilizando uma
classe de conexão (`ConexaoBD`), operando sobre as entidades de domínio (`Usuario` e
`Livro`).

### 2. Diagrama de Sequência (Fluxo do CRUD — Listar e Incluir)
```mermaid
sequenceDiagram
    autonumber
    actor Usuario as Cliente / Navegador
    participant JSP as Interface (JSP / HTML)
    participant Servlet as Servlet Controller
    participant DAO as UsuarioDAO
    participant DB as Banco de Dados

    Usuario->>JSP: Preenche formulário de Cadastro
    JSP->>Servlet: Envia requisição POST (Dados do Usuário)
    Servlet->>DAO: Instancia objeto e chama inserir(usuario)
    DAO->>DB: Executa comando SQL (INSERT INTO...)
    DB-->>DAO: Confirmação de Sucesso
    DAO-->>Servlet: Retorna status (true/false)
    Servlet->>JSP: Redireciona para página de Listagem
    JSP->>Servlet: Requisição GET (Listar Usuários)
    Servlet->>DAO: Solicita lista de registros (listar())
    DAO->>DB: Executa SELECT * FROM usuarios
    DB-->>DAO: Retorna ResultSet
    DAO-->>Servlet: Retorna List<Usuario>
    Servlet->>JSP: Encaminha dados para exibição na tabela
    JSP-->>Usuario: Renderiza HTML com a lista atualizada
```
Ciclo completo de uma requisição Web em Java (MVC simplificado): a requisição via JSP
é processada pelo Servlet, repassada ao DAO para persistência, e o resultado volta em
formato de listagem renderizada na tela.

### 3. Diagrama Arquitetural (Ambiente e Camadas do Projeto)
```mermaid
graph TD
    A[Ambiente de Desenvolvimento Java] --> B[Configuração do Servidor / IDE]
    B --> C[Projeto JavaWeb / JSP]

    subgraph Camadas do Sistema
        C --> D[Front-end / Interface<br>HTML / CSS / JSP]
        C --> E[Controlador<br>Servlets]
        C --> F[Modelo / Domínio<br>Classes Usuario e Livro]
        C --> G[Persistência<br>Conexão JDBC / DAO]
    end

    G --> H[(Banco de Dados Relacional)]
    C --> I[Controle de Versão<br>GitHub]

    style A fill:#f9f,stroke:#333,stroke-width:2px
    style C fill:#bbf,stroke:#333,stroke-width:2px
    style H fill:#fbb,stroke:#333,stroke-width:2px
```
Mapeia a evolução da disciplina desde a preparação do ambiente (Aulas 01/02), pela
estruturação do front-end, implementação de Servlets, conexão com o banco (Aulas
03/04), até o versionamento via GitHub.

---

## 🎞️ Apresentação de Revisão em Slides

[`Slides-Revisao-[Prof. Jefferson Passerini] Laboratório de Programação III.pptx`](./Slides-Revisao-%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III.pptx) —
deck de 5 slides em dark mode (Slate/Navy/Teal/Indigo), 16:9 widescreen, cobrindo
Visão Geral, Conceitos Fundamentais, Exercícios/Prática e Dicas de Prova, com layout
programático (zero overflow de texto garantido por medição real de fonte).

---

## 🃏 Flashcards para Anki

[`flashcards-anki.tsv`](./flashcards-anki.tsv) — baralho pergunta/resposta (formato
TSV de 2 colunas) cobrindo professor, semestre, cronograma de aulas e conceitos
centrais da disciplina. Para importar: no Anki, **Arquivo → Importar**, selecione o
`.tsv` e mapeie as colunas para *Frente* e *Verso*.

---

## 🤖 Dataset de Perguntas e Respostas (JSONL)

[`dataset-estudo-qa.jsonl`](./dataset-estudo-qa.jsonl) — **15 pares** de
pergunta/resposta estruturados (`id`, `topico`, `pergunta`, `resposta`,
`dificuldade`), prontos para consumo por scripts/ferramentas de estudo ou fine-tuning
leve. Amostra:

```json
{"id": 1, "topico": "Ambiente de Desenvolvimento", "pergunta": "Qual é o objetivo da Aula 01 da disciplina de Laboratório de Programação III ministrada pelo Prof. Jefferson Passerini?", "resposta": "A Aula 01 é focada em preparar o ambiente de desenvolvimento necessário para o curso de Java Web.", "dificuldade": "facil"}
{"id": 2, "topico": "Projetos Web", "pergunta": "O que é abordado na Aula 02 da disciplina?", "resposta": "Na Aula 02, o foco é a criação de um projeto web utilizando Java.", "dificuldade": "facil"}
{"id": 3, "topico": "Front-end", "pergunta": "O que engloba a Aula 03 do curso de Laboratório de Programação III?", "resposta": "A Aula 03 aborda a estrutura do projeto voltada para o front-end.", "dificuldade": "medio"}
```
