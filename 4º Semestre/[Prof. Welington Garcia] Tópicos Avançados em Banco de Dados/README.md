# 🎓 [Prof. Welington Garcia] Tópicos Avançados em Banco de Dados
> **Semestre:** 4º Semestre  
> **Curso:** Bacharelado em Sistemas de Informação (UniFEF)  
> **Professor:** Welington Garcia  

---

## 🎯 Objetivos de Aprendizagem e Ementa

A disciplina de **Tópicos Avançados em Banco de Dados** foi desenhada para elevar o conhecimento do aluno de Sistemas de Informação além do básico de SQL. No 4º semestre, o foco é aprofundar a lógica de manipulação e otimização de dados relacionais e avançar em estruturas que garantem performance, reuso e segurança em sistemas corporativos.

### **Ementa e Tópicos Principais:**
1. **Consultas Complexas e Otimização:** Uso avançado de *Joins* (INNER, LEFT, RIGHT, FULL, CROSS) para cruzamento de grandes volumétricas de dados.
2. **Subconsultas (*Subqueries*):** Emprego de subselects correlacionadas e não correlacionadas, cláusulas `EXISTS`, `IN`, `ANY` e `ALL`.
3. **Visões (*Views*):** Criação e gerenciamento de tabelas virtuais para segurança, simplificação de consultas e abstração de dados.
4. **Programação em Banco de Dados:** Introdução a Stored Procedures, Triggers e Funções (conforme aplicável no ecossistema da disciplina).
5. **Boas Práticas e Performance:** Análise de planos de execução e estruturação eficiente de consultas para ambientes de alta demanda.

---

## 🏗️ Arquitetura e Modelagem do Conhecimento

O diagrama abaixo ilustra a relação conceitual entre os principais módulos abordados ao longo do semestre na disciplina:

```mermaid
classDiagram
    class BancoDeDados {
        +String nome
        +conectar()
    }

    class Tabela {
        +String nome
        +listarRegistros()
    }

    class Joins {
        +innerJoin()
        +leftRightJoin()
        +fullOuterJoin()
    }

    class SubSelects {
        +subqueryEscalar()
        +subqueryCorrelacionada()
    }

    class Views {
        +criarView()
        +consultarView()
    }

    class Avaliacao {
        +double notaMaxima
        +validarPratica()
    }

    BancoDeDados --> Tabela : Contém
    Tabela --> Joins : Relaciona via Chaves
    Tabela --> SubSelects : Filtra via
    Tabela --> Views : Abstrai em
    Avaliacao --> Joins : Cobre
    Avaliacao --> SubSelects : Cobre
```

---

## 📂 Estrutura das Pastas e Organização

O repositório está estruturado de forma modular para facilitar a navegação e o estudo autônomo:

* 📁 **Aulas/**: Materiais didáticos, slides teóricos, scripts SQL de exemplo e comunicados oficiais.
* 📁 **Trabalhos/**: Atividades práticas avaliativas com resoluções comentadas em código SQL.
* 📁 **Provas/**: Avaliações semestrais, simulados e critérios de correção.
* 📁 **Resumos-IA/**: Resumos executivos, simulados comentados, scripts de código, flashcards em formato Anki (`.tsv`), *CheatSheets* rápidos e apresentações em PPTX.

---

## 🚀 Como Estudar com Este Material

1. **Base Teórica:** Comece revisando os scripts e materiais disponíveis na pasta `Aulas/`.
2. **Prática Ativa:** Execute os códigos SQL em seu SGBD de preferência (PostgreSQL, MySQL, SQL Server, etc.).
3. **Fixação:** Utilize os flashcards e resumos da pasta `Resumos-IA/` para memorização ativa dos conceitos de *Joins*, *Subqueries* e *Views*.
4. **Desafios:** Resolva os exercícios práticos presentes nas seções de `Trabalhos/` e `Provas/` sem olhar o gabarito primeiro.

---

# 📚 Conteúdo Acadêmico

---

## 📘 Aulas: Joins e Subselects

> **Professor:** Prof. Welington Garcia  
> **Disciplina:** Tópicos Avançados em Banco de Dados  

### 📌 Visão Geral
Módulo dedicado ao estudo profundo de cruzamento de tabelas relacionais. O aluno aprende a combinar dados de múltiplas fontes utilizando os diferentes tipos de `JOIN` e a aplicar lógica avançada de filtragem através de subconsultas aninhadas.

---

## 📘 Views

> **Professor:** Prof. Welington Garcia  
> **Disciplina:** Tópicos Avançados em Banco de Dados  

### 📌 Visão Geral
Estudo sobre a criação e utilização de **Views (Visões)**. O conteúdo aborda como encapsular consultas complexas, restringir o acesso direto a colunas sensíveis de tabelas e simplificar a interface de desenvolvimento para aplicações cliente.

---

## 📝 Prova / Avaliação: Exercicios SubSelects - parte 01

> **Professor:** Prof. Welington Garcia  
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)  
> **Pontuação Máxima:** 100 pontos  

### 🎯 Descrição da Atividade
Primeira bateria de exercícios práticos focada no uso de subconsultas (*SubSelects*). Os desafios exigem o uso de subqueries em cláusulas `WHERE` e `HAVING`, avaliando a capacidade do aluno de resolver problemas lógicos complexos dividindo-os em consultas menores.

---

## 📝 Prova / Avaliação: Exercicios - SubSelect - parte 2

> **Professor:** Prof. Welington Garcia  
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)  
> **Prazo de Entrega:** 03/09/2026 às 02:59  
> **Pontuação Máxima:** 100 pontos  

### 🎯 Descrição da Atividade
Continuação avançada da avaliação de subselects. Esta parte introduz subconsultas correlacionadas e o uso de operadores avançados (`EXISTS`, `NOT EXISTS`), testando a performance e o raciocínio lógico em cenários de banco de dados corporativos.

---

## 📋 Trabalho: Exercicios Joins

> **Professor:** Prof. Welington Garcia  
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)  
> **Pontuação Máxima:** 100 pontos  

### 🎯 Descrição da Atividade
Trabalho prático consolidando o uso de junções (`INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, `FULL OUTER JOIN` e *Cross Joins*). O aluno deve resolver um *case* real modelando consultas que cruzam dados de clientes, pedidos, produtos e fornecedores.