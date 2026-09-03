# Resumo Consolidado: [Prof. Jefferson Passerini] Laboratório de Programação III

## 1. Visão Geral e Objetivos da Matéria
A disciplina de **Laboratório de Programação III** (oferecida no 3º semestre do curso de Sistemas de Informação sob a tutela do Prof. Jefferson Passerini) tem como foco principal a capacitação prática no desenvolvimento de **Aplicações Web utilizando a linguagem Java** (Ecossistema Java Web / JSP / Servlets). 

O objetivo central é transicionar o conhecimento de lógica e programação orientada a objetos para o ambiente corporativo web, capacitando o aluno a projetar, estruturar, codificar e persistir dados em sistemas web completos, aplicando o padrão arquitetural clássico e operando sobre fluxos de requisição/resposta HTTP, manipulação de interfaces (Front-end) e comunicação com Bancos de Dados Relacionais.

---

## 2. Conceitos-Chave e Terminologia Fundamental
Para dominar os tópicos abordados ao longo do semestre, é essencial compreender a taxonomia e os conceitos estruturantes da tecnologia Java Web:

*   **Ambiente de Desenvolvimento (IDE / Servidor Web):** Configuração de ferramentas e containers web (como Apache Tomcat) para hospedar e executar aplicações Java EE/Jakarta EE.
*   **Java Web / Servlets:** Classes Java que interceptam e respondem a requisições HTTP, atuando como o núcleo controlador (Controller) das aplicações.
*   **JSP (JavaServer Pages):** Tecnologia que permite embutir código Java em páginas HTML, facilitando a construção dinâmica de interfaces de usuário (Front-end).
*   **Arquitetura e Estrutura de Projeto:** Organização de pacotes e diretórios seguindo boas práticas de separação de responsabilidades (camadas de visão, controle e persistência).
*   **Persistência e Conexão com Banco de Dados:** Estabelecimento de pontes de comunicação (JDBC/Drivers) entre a aplicação Java Web e sistemas gerenciadores de banco de dados relacionais.
*   **CRUD (Create, Read, Update, Delete):** O acrônimo fundamental para as quatro operações básicas de manutenção de dados (Incluir, Listar/Consultar, Alterar e Excluir).

---

## 3. Principais Módulos / Tópicos Abordados (com explicações técnicas)

A disciplina seguiu uma abordagem incremental, construindo um sistema web do zero aula a aula:

1.  **Configuração de Ambiente (Aula 01):** 
    *   *Explicação Técnica:* Instalação e parametrização das ferramentas de desenvolvimento, JDK, IDEs compatíveis e configuração do servidor de aplicação (Tomcat) para suporte a projetos dinâmicos web.
2.  **Criação de Projetos Web e Estrutura Front-end (Aulas 02 e 03):**
    *   *Explicação Técnica:* Criação da estrutura de diretórios padrão de um projeto web Java (Dynamic Web Project / Maven), definição do arquivo descritor de deployment (`web.xml`), e construção da interface do usuário utilizando HTML/JSP para captação de dados.
3.  **Conexão com o Banco de Dados (Aula 04):**
    *   *Explicação Técnica:* Implementação das classes de conexão utilizando JDBC. Envolve o carregamento do driver de banco de dados, abertura de conexões seguras, tratamento de exceções de SQL e encerramento adequado de recursos (`Connection`, `PreparedStatement`).
4.  **Listagem de Dados (Aula 05):**
    *   *Explicação Técnica:* Execução de comandos SQL do tipo `SELECT` a partir do Servidor, mapeamento do ResultSet para objetos de domínio (ex: classe `Usuario`) e envio desses dados para a página JSP exibila-los dinamicamente em formato de tabela.
5.  **Implementação Completa do CRUD (Aula 06 em diante):**
    *   *Explicação Técnica:* Desenvolvimento das operações de manutenção completas:
        *   **Create (Incluir):** Captura de parâmetros via requisição HTTP (`request.getParameter`), persistência via `INSERT` no banco.
        *   **Read (Listar):** Consulta e renderização dos registros.
        *   **Update (Alterar):** Recuperação do registro por ID, preenchimento de formulário e execução de comando `UPDATE`.
        *   **Delete (Excluir):** Remoção lógica ou física do registro com base em identificadores únicos (`DELETE FROM`).

---

## 4. Relações com o Mercado e Prática Profissional
O ecossistema Java corporativo continua sendo um dos pilares mais robustos no mercado de desenvolvimento de software empresarial, sistemas bancários, e-commerce e APIs de alta escala. 

*   **Padrões de Mercado:** Embora o mercado moderno utilize frameworks avançados (como Spring Boot), a compreensão profunda de **Servlets, JSP e JDBC** (ensinada na disciplina) fornece a base técnica exata (*under the hood*) de como a web funciona em Java. Saber como uma requisição HTTP viaja do navegador até o banco de dados sem "mágicas" de frameworks é um diferencial crítico para um analista de sistemas.
*   **Trabalhos Práticos:** Os trabalhos exigidos no semestre (como o sistema de manutenção de cadastros de Livros e os desafios de validação de e-mails e estados) simulam demandas reais de software corporativo, exigindo trabalho em equipe (metodologias ágeis/colaborativas), versionamento de código (Git/GitHub) e entrega de soluções funcionais ponta a ponta.

---

## 5. Dicas de Ouro para Estudo e Provas
Para obter excelente desempenho nas avaliações (AV1, AV2 e Avaliação Substitutiva) ministradas pelo Prof. Jefferson Passerini, siga estas diretrizes:

*   **Domine o Fluxo Requisição-Resposta:** Entenda perfeitamente como um formulário HTML envia dados via método POST/GET, como o Servlet os intercepta (`request.getParameter`), processa a regra de negócio/banco, e redireciona ou despacha (`RequestDispatcher`) a resposta de volta para o JSP.
*   **Pratique a Sintaxe de Conexão JDBC:** Em provas práticas e entregas de código (como na Avaliação II), erros de sintaxe ao abrir conexões com o banco de dados ou esquecer de fechar `Statements` geram falhas críticas. Tenha um template mental ou trechos de código de conexão bem memorizados.
*   **Atenção aos Detalhes dos Modelos (Classes de Domínio):** Nas atividades práticas (como o CRUD de Livros ou Usuários), garanta que os atributos da classe casem perfeitamente com as colunas do banco de dados e com os nomes dos campos (`name="..."`) nos formulários HTML/JSP.
*   **Utilize os Recursos Complementares:** Revise sempre os roteiros e materiais de apoio disponibilizados via Notion (links oficiais das aulas e desafios práticos, como os Capítulos de Java JSP), pois eles trazem o passo a passo exato das implementações exigidas nas avaliações.