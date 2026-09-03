Com base no conteúdo programático da disciplina **Laboratório de Programação III**, ministrada pelo **Prof. Jefferson Passerini**, elaboramos um simulado completo focando em desenvolvimento **Java Web, Servlets, JSP, Conexão com Banco de Dados e operações de CRUD**.

---

# 🧠 SIMULADO: Laboratório de Programação III
**Professor:** Jefferson Passerini  
**Conteúdo Abrangido:** Ambiente Java Web, Estrutura Front-end/Back-end, Conexão com Banco de Dados, Listagem e Operações de CRUD (Incluir, Alterar, Excluir).

---

## 📌 PARTE 1: Questões de Múltipla Escolha (1 a 10)

### **Questão 1**
Na Aula 01, o foco inicial da disciplina é a preparação do ambiente de desenvolvimento. Qual das alternativas abaixo descreve corretamente os componentes essenciais típicos para iniciar o desenvolvimento de projetos Java Web abordados na disciplina?
A) Apenas um editor de texto simples e o interpretador de comandos do sistema operacional.  
B) Configuração de JDK (Java Development Kit), IDE de desenvolvimento (como Eclipse ou IntelliJ) e um Servidor de Aplicação/Web (como Apache Tomcat).  
C) Apenas a instalação do framework Angular e Node.js.  
D) Um banco de dados NoSQL e um compilador C++.  
E) Um navegador web atualizado e um editor de imagens vetorias.  

* **Gabarito Comentado:** **B**  
  *Explicação:* O desenvolvimento Java Web tradicional (JSP/Servlets) exige o JDK para compilação, uma IDE para agilizar o código e um container web/servlet como o Apache Tomcat para hospedar e executar as aplicações web baseadas em HTTP.

---

### **Questão 2**
Sobre a criação de um projeto web em Java (Aula 02), qual é a estrutura de diretórios padrão recomendada para aplicações Web Java baseadas no padrão Maven ou Dynamic Web Project no contexto de Servlets e JSP?
A) `src/` contendo apenas arquivos HTML estáticos sem suporte a classes Java.  
B) `src/main/java` para as classes Java (Servlets, DAOs, Models) e diretórios específicos de webapp/WebContent para páginas JSP e arquivos estáticos.  
C) Apenas um arquivo único `.exe` contendo toda a aplicação compilada.  
D) Pastas separadas exclusivamente para scripts em Python e Ruby.  
E) Uma estrutura baseada em blocos de notas salvos na raiz do sistema operacional.  

* **Gabarito Comentado:** **B**  
  *Explicação:* Projetos Java Web seguem uma convenção estrutural bem definida onde o código-fonte backend (classes) fica separado dos recursos web (páginas JSP, arquivos CSS/JS e o descritor de implantação `web.xml`).

---

### **Questão 3**
A Aula 03 aborda a estrutura do projeto focada no Front-end. No contexto de aplicações Java Web utilizando JSP (JavaServer Pages), qual é o papel principal das páginas JSP na arquitetura da aplicação?
A) Substituir completamente o banco de dados relacional.  
B) Atuar exclusivamente como controladoras de requisições de rede de baixo nível (TCP/IP).  
C) Servir como a camada de visualização (View), permitindo misturar códigos HTML com elementos dinâmicos em Java para renderizar a interface para o usuário.  
D) Compilar o código-fonte em linguagem de máquina nativa para sistemas Windows.  
E) Gerenciar transações complexas de concorrência diretamente no servidor de hardware.  

* **Gabarito Comentado:** **C**  
  *Explicação:* O JSP é uma tecnologia voltada para a camada de apresentação (View). Ele facilita a criação de páginas web dinâmicas inserindo trechos de código Java (ou JSTL/Expression Language) em meio ao código HTML.

---

### **Questão 4**
Na Aula 04, estuda-se a **Conexão com o Banco de Dados**. Qual biblioteca/API padrão do Java é amplamente utilizada para estabelecer a conexão e executar comandos SQL em bancos de dados relacionais?
A) Swing API  
B) JDBC (Java Database Connectivity)  
C) JavaFX Database Engine  
D) ServletContext Interface  
E) DOM Parser  

* **Gabarito Comentado:** **B**  
  *Explicação:* O JDBC é a API padrão do Java para conexão e execução de instruções SQL com bancos de dados relacionais, permitindo abrir conexões, criar prepared statements e processar result sets.

---

### **Questão 5**
Para realizar operações de persistência e consulta, como a listagem de usuários abordada na Aula 05 ("Criando o Listar Usuário"), qual classe ou interface do JDBC é utilizada tipicamente para executar consultas SQL do tipo `SELECT` e retornar os registros para a aplicação?
A) `HttpServletRequest`  
B) `PrintWriter`  
C) `ResultSet`  
D) `HttpSession`  
E) `WebAppContext`  

* **Gabarito Comentado:** **C**  
  *Explicação:* Após executar um comando de consulta (`Statement.executeQuery()`), o banco retorna os dados em formato de tabela virtual encapsulada em um objeto do tipo `ResultSet`, que pode ser percorrido linha a linha para popular objetos de modelo (ex: `Usuario`).

---

### **Questão 6**
No contexto da implementação das funções do CRUD (Incluir, Alterar e Excluir), estudadas na aula correspondente, qual método HTTP é comumente utilizado por formulários HTML tradicionais para enviar dados sensíveis ou de cadastro (como inclusão e alteração) para um Servlet?
A) `GET`  
B) `POST`  
C) `TRACE`  
D) `OPTIONS`  
E) `HEAD`  

* **Gabarito Comentado:** **B**  
  *Explicação:* O método `POST` é o mais adequado para submissão de formulários de alteração de estado (inserção, atualização e remoção de dados), pois envia os parâmetros encapsulados no corpo da requisição HTTP, oferecendo maior segurança e capacidade de dados que a URL (`GET`).

---

### **Questão 7**
Com base no **Trabalho de Programação de Livros** proposto na disciplina (que exige uma classe `Livro` com atributos como `id`, `nomeLivro`, `isbn`, `autor`, `dataPublicação`, `valorLivro`), qual padrão de projeto (Design Pattern) estrutural/arquitetural é ideal para separar as regras de acesso ao banco de dados das regras de negócio e da interface web?
A) Singleton Pattern estrito sem instâncias  
B) DAO (Data Access Object)  
C) Observer bidirecional assíncrono  
D) Factory Method de arquivos binários  
E) MVC (Model-View-Controller) implementado através de Servlets e JSPs  

* **Gabarito Comentado:** **E** (e B para persistência) / *Nota:* O padrão arquitetural global para separar camadas em Java Web é o **MVC** (Model-View-Controller), onde Servlets atuam como Controllers, JSPs como Views e as classes de domínio/DAO como Models.

---

### **Questão 8**
O uso de **PreparedStatements** em Java JDBC (essencial para a segurança das operações de CRUD) traz como principal vantagem em relação ao `Statement` comum:
A) Acelerar o carregamento de imagens no Front-end.  
B) Prevenir ataques de Injeção de SQL (SQL Injection) através da parametrização prévia dos valores inseridos na query.  
C) Permitir que o código execute exclusivamente em servidores Linux.  
D) Dispensar o uso de drivers de banco de dados.  
E) Converter automaticamente páginas JSP em arquivos PDF.  

* **Gabarito Comentado:** **B**  
  *Explicação:* O `PreparedStatement` compila a instrução SQL antecipadamente no banco e trata os parâmetros de entrada estritamente como dados, bloqueando injeções maliciosas de comandos SQL.

---

### **Questão 9**
Um dos desafios práticos propostos na disciplina (visto nos links de recursos) envolve a implementação de validações para o campo e-mail. Onde idealmente deve ocorrer a validação robusta de regras de negócio de um cadastro antes de persistir os dados no banco?
A) Apenas no navegador do usuário utilizando estilização CSS.  
B) Exclusivamente no arquivo de configuração do Tomcat (`server.xml`).  
C) Na camada de controle/modelo no Servidor (Back-end), podendo ser complementada por validações visuais no Front-end.  
D) No arquivo HTML estático antes do carregamento da página.  
E) Diretamente na tabela física do banco de dados via triggers complexas em Assembly.  

* **Gabarito Comentado:** **C**  
  *Explicação:* Embora a validação no front-end melhore a experiência do usuário, a validação no back-end (Servlets/Classes de Negócio) é mandatória por questões de segurança e integridade de dados, já que o front-end pode ser burlado.

---

### **Questão 10**
Ao finalizar o desenvolvimento de um projeto prático na disciplina do Prof. Jefferson Passerini, uma das exigências comuns é versionar e disponibilizar o código em qual plataforma de controle de versão?
A) GitHub  
B) Photoshop  
C) Microsoft Word  
D) WinRAR  
E) FileZilla Server  

* **Gabarito Comentado:** **A**  
  *Explicação:* Conforme estipulado nas diretrizes dos trabalhos práticos (ex: Trabalho de Programação de Livros), colocar o projeto no **GitHub** é um requisito essencial de entrega.

---

## 📌 PARTE 2: Questões Discursivas e Estudos de Casos Práticos (11 a 15)

### **Questão 11 (Estudo de Caso: Arquitetura e Configuração)**
Explique qual é a importância da configuração correta do ambiente de desenvolvimento (JDK, IDE e Servidor Tomcat) na **Aula 01 e Aula 02** para um projeto Java Web. O que acontece se o container web (Tomcat) não estiver integrado corretamente à IDE no momento de executar um Servlet?

* **Gabarito / Resposta Esperada:**  
  A configuração correta garante que o compilador encontre as bibliotecas necessárias da API Servlet (`javax.servlet` / `jakarta.servlet`). Se o Tomcat não estiver configurado corretamente na IDE, a aplicação não conseguirá gerar o empacotamento web adequado (`.war`) nem realizar o deploy local. Como resultado, ao tentar acessar uma URL mapeada para um Servlet, o servidor retornará erros de compilação, falhas de caminho ou erros HTTP 404 (Recurso não encontrado), pois o container web não saberá onde encontrar ou como executar as classes controladoras da aplicação.

---

### **Questão 12 (Estudo de Caso: Conexão e Banco de Dados)**
Na **Aula 04**, os alunos aprendem a conectar a aplicação Java ao Banco de Dados. Escreva um trecho de código em Java utilizando JDBC que demonstre a abertura de uma conexão com um banco de dados relacional (ex: MySQL) utilizando `DriverManager`.

* **Gabarito / Resposta Esperada:**  
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
              // Carrega o driver (opcional dependendo da versão do JDBC, mas boa prática didática)
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

---

### **Questão 13 (Estudo de Caso: Listagem de Dados)**
Com base na **Aula 05 (Criando o Listar Usuário)** e considerando a necessidade do Trabalho de Programação de exibir uma lista de livros cadastrados, descreva o fluxo lógico completo (desde a requisição do usuário no navegador até a exibição na página JSP) para listar registros vindos do banco de dados.

* **Gabarito / Resposta Esperada:**  
  1. **Requisição:** O usuário acessa uma URL no navegador (ex: `/listarLivros`).  
  2. **Controller (Servlet):** A requisição é interceptada por um Servlet mapeado para essa URL.  
  3. **Model / DAO:** O Servlet aciona uma classe de consulta (ex: `LivroDAO`), que executa uma query SQL (`SELECT * FROM livro`) utilizando JDBC.  
  4. **Processamento:** O banco retorna um `ResultSet`, cujos dados são convertidos em uma `List<Livro>` preenchendo os objetos da classe modelo.  
  5. **Encaminhamento (Forward):** O Servlet armazena a lista de livros no escopo da requisição (`request.setAttribute("livros", listaLivros)`) e utiliza o `RequestDispatcher` para redirecionar o fluxo para a página JSP de exibição (`listar.jsp`).  
  6. **View (JSP):** A página JSP utiliza tags de repetição (como JSTL `<c:forEach>`) para iterar sobre a lista recebida e renderizar dinamicamente uma tabela HTML com os dados dos livros para o usuário.

---

### **Questão 14 (Estudo de Caso: Implementação do CRUD)**
O **Trabalho de Programação de Livros** exige a implementação completa das operações de manutenção (Incluir, Alterar e Excluir). Explique como o sistema diferencia qual operação (inserir ou atualizar) deve ser executada ao receber os dados de um formulário preenchido pelo usuário.

* **Gabarito / Resposta Esperada:**  
  Geralmente, essa diferenciação é feita através da verificação do campo identificador (`id`) do registro. 
  - Se o campo `id` estiver **vazio, nulo ou igual a zero**, significa que o registro é novo, portanto o Servlet deve acionar o método de **Inserção (`INSERT`)** no banco de dados.
  - Se o campo `id` possuir um **valor numérico válido existente**, significa que o registro já está cadastrado e está sendo editado, logo o Servlet deve acionar o método de **Atualização (`UPDATE`)** filtrando pelo respectivo `id`. Essa verificação costuma ser auxiliada por um campo oculto (`<input type="hidden" name="id" value="...">`) no formulário HTML.

---

### **Questão 15 (Estudo de Caso Prático: Modelagem de Classes)**
Com base nas especificações do trabalho de programação, elabore a estrutura da classe Java **`Livro`** contendo os atributos especificados pelo professor (*id, nomeLivro, isbn, autor, dataPublicação, valorLivro*), incluindo o