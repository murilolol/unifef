# [Prof. Jefferson Passerini] Laboratório de Programação III

Abaixo estão os diagramas em Mermaid modelados com base no conteúdo da disciplina de **Laboratório de Programação III**, contemplando o desenvolvimento Web com Java (JSP, Servlets, Conexão com Banco de Dados e padrão CRUD).

---

### 1. Diagrama de Classes UML (Domínio do Sistema)
Representação das entidades principais abordadas nas aulas de estruturação de projetos web e implementação do CRUD (com base nas classes `Usuario` e no trabalho prático da classe `Livro`).

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
* **Explicação do Objeto:** O diagrama ilustra a arquitetura orientada a dados do sistema Java Web. A classe de acesso a dados (`UsuarioDAO`) gerencia a persistência utilizando uma classe de conexão (`ConexaoBD`), operando sobre as entidades de domínio (`Usuario` e `Livro`).

---

### 2. Diagrama de Sequência (Fluxo do CRUD - Listar e Incluir)
Ilustra a interação entre a interface (JSP/Front-end), o Controlador (Servlet) e a camada de persistência (DAO/Banco de Dados) durante uma operação de CRUD.

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
* **Explicação do Fluxo:** Demonstra o ciclo completo de uma requisição Web em Java (arquitetura MVC simplificada). A requisição do usuário via interface JSP é processada pelo Servlet, repassada ao DAO para persistência no Banco de Dados Relacional, e o resultado é devolvido em formato de listagem renderizada na tela.

---

### 3. Diagrama Arquitetural (Ambiente e Camadas do Projeto Java Web)
Visão geral da estrutura de pastas, configuração de ambiente e camadas lógicas construídas ao longo das aulas (Ambiente -> Servidor/Projeto -> Camadas Web/DAO).

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
* **Explicação Arquitetural:** Mapeia a evolução da disciplina desde a preparação do ambiente de desenvolvimento (Aulas 01 e 02), passando pela estruturação da interface front-end, implementação de Servlets, conexão com o banco de dados (Aulas 03 e 04), até o versionamento de código e entregas de trabalhos práticos via GitHub.