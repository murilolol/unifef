#  cheat sheet: laboratório de programação iii
**Professor:** Jefferson Passerini | **Disciplina:** Lab. Programação III (3º Sem. - Java Web / JSP / Servlets)

---

### 🚀 1. Ambiente & Estrutura de Projeto Web
*   **Pilares:** Java Web, Servlets, JSP (JavaServer Pages), Banco de Dados (JDBC).
*   **Estrutura Front-End / Back-End:** Organização de páginas JSP para interface de usuário e Servlets para controle de fluxo (MVC simplificado).
*   **Deploy/Execução:** Servidor de aplicação compatível com Servlet (ex: Apache Tomcat).

---

### 🗄️ 2. Banco de Dados & Conexão
*   **Conexão (JDBC):** Driver Manager, Connection, PreparedStatement, ResultSet.
*   **Operações Básicas (SQL via Java):** 
    *   `INSERT INTO` (Incluir)
    *   `SELECT * FROM` (Listar)
    *   `UPDATE ... SET ... WHERE id = ?` (Alterar)
    *   `DELETE FROM ... WHERE id = ?` (Excluir)

---

### ⚙️ 3. Implementação do CRUD (Create, Read, Update, Delete)
*   **Fluxo do CRUD:**
    1.  **Create (Incluir):** Captura dados via formulário JSP $\rightarrow$ Envia para o Servlet $\rightarrow$ Executa `INSERT` no banco.
    2.  **Read (Listar):** Servlet consulta o banco (`SELECT`) $\rightarrow$ Retorna lista $\rightarrow$ Encaminha para JSP exibir em tabela (`<table>`).
    3.  **Update (Alterar):** Carrega dados atuais do registro pelo `id` $\rightarrow$ Edita no formulário $\rightarrow$ Executa `UPDATE` no banco.
    4.  **Delete (Excluir):** Recebe o `id` via parâmetro na requisição $\rightarrow$ Executa `DELETE` no banco.

---

### 🛠️ 4. Padrões de Entidades & Exemplos Práticos
*   **Exemplo de Entidade (`Livro` / `Usuario`):**
    ```java
    // Atributos típicos exigidos em trabalhos
    private int id;
    private String nome; // ou nomeLivro / email
    private String autor; // ou isbn / dataPublicacao / valorLivro
    ```
*   **Validações Comuns:** Verificação de campos obrigatórios (ex: validação de formato de e-mail) antes de persistir no banco de dados.

---

### 📌 5. Checklist rápido para Provas e Trabalhos
1. [ ] Configurar corretamente o projeto Web e o Tomcat na IDE.
2. [ ] Garantir a classe de conexão com o Banco de Dados ativa.
3. [ ] Mapear as Servlets (`@WebServlet`) corretamente.
4. [ ] Validar se os formulários HTML/JSP apontam para os métodos corretos (`GET` / `POST`).
5. [ ] Tratar parâmetros nulos ou conversões de tipos (`Integer.parseInt(request.getParameter("id"))`).