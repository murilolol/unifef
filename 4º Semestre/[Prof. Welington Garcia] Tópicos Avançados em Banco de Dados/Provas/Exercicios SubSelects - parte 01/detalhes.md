# Prova / Avaliação — Exercícios SubSelects (parte 1)

> **Professor:** Welington Garcia
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)
> **Pontuação máxima:** 100 pontos
> **Escopo:** conteúdo apresentado até o slide 20 da aula de Subconsultas — subconsultas escalares, subconsultas no `SELECT`/`WHERE`, `IN`, `NOT IN`, `EXISTS`, `NOT EXISTS`, `ANY`, `ALL` e subconsultas correlacionadas. **Tabelas derivadas no `FROM` não fazem parte desta lista** (são cobradas na parte 2).

## Descrição da atividade

Primeira bateria de exercícios práticos focada no uso de subconsultas (*subselects*) sobre um pequeno sistema comercial (`clientes`, `vendedores`, `categorias`, `produtos`, `pedidos`, `itens_pedido`). Os desafios exigem o uso de subqueries em cláusulas `SELECT` e `WHERE`, avaliando a capacidade do aluno de resolver problemas lógicos complexos dividindo-os em consultas menores.

## Lista de exercícios (20)

| Nº | Conteúdo | Enunciado |
| :-: | :--- | :--- |
| 1 | Subconsulta escalar | Liste os produtos cujo preço seja maior que a média de preços de todos os produtos. |
| 2 | Subconsulta escalar | Liste o(s) produto(s) com o maior preço cadastrado (`MAX(preco)`). |
| 3 | Subconsulta escalar | Liste o(s) produto(s) com o menor preço cadastrado (`MIN(preco)`). |
| 4 | Subconsulta no `SELECT` | Exiba nome e preço de cada produto e, numa terceira coluna, a média geral de preços. |
| 5 | Subconsulta no `SELECT` | Exiba nome, preço, média geral e a diferença entre o preço do produto e a média geral. |
| 6 | `IN` | Liste os clientes que realizaram pelo menos um pedido. |
| 7 | `IN` | Liste os produtos que já apareceram em algum item de pedido. |
| 8 | `NOT IN` | Liste os clientes que não aparecem em nenhum pedido, usando `NOT IN`. |
| 9 | `NOT EXISTS` | Reescreva o exercício 8 utilizando `NOT EXISTS`. |
| 10 | `EXISTS` | Liste os vendedores que possuem pelo menos um pedido registrado. |
| 11 | `NOT EXISTS` | Liste os produtos que nunca foram vendidos. |
| 12 | `IN` com mais de uma tabela | Liste os clientes que compraram o produto "Notebook Dell". |
| 13 | `ANY` | Liste os produtos com preço maior que o de pelo menos um produto da categoria Telefonia. |
| 14 | `ALL` | Liste os produtos com preço maior que o de **todos** os produtos da categoria Acessórios. |
| 15 | `ANY` | Liste os vendedores cujo salário seja maior que o de pelo menos um dos demais vendedores. |
| 16 | `ALL` | Liste o(s) vendedor(es) cujo salário seja maior ou igual ao de todos os vendedores cadastrados. |
| 17 | Correlacionada | Liste os produtos cujo preço seja maior que a média de preços da própria categoria. |
| 18 | Correlacionada | Liste os produtos cujo estoque seja maior que a média de estoque da própria categoria. |
| 19 | Correlacionada | Liste os clientes cujo limite de crédito seja maior que a média de limite de crédito dos clientes do mesmo estado. |
| 20 | Correlacionada | Liste, para cada categoria, o(s) produto(s) de maior preço daquela categoria. |

**Observações para a resolução:** evite `SELECT *` quando só algumas colunas forem necessárias; identifique se a subconsulta retorna um único valor ou várias linhas antes de escolher entre `IN`, `EXISTS`, `ANY` e `ALL`; em subconsultas correlacionadas, identifique qual coluna da consulta externa é usada na consulta interna; teste a subconsulta isoladamente sempre que ajudar a entender o resultado.

## Modelo de dados desta prova

Banco próprio para os exercícios (mais completo que o da aula, com `estoque` em `produtos` e `limite_credito`/`comissao`):

```sql
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2),
    limite_credito NUMERIC(10,2)
);

CREATE TABLE vendedores (
    id_vendedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario NUMERIC(10,2) NOT NULL,
    comissao NUMERIC(5,2)
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    estoque INTEGER NOT NULL,
    id_categoria INTEGER REFERENCES categorias(id_categoria)
);

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    id_cliente INTEGER NOT NULL REFERENCES clientes(id_cliente),
    id_vendedor INTEGER NOT NULL REFERENCES vendedores(id_vendedor)
);

CREATE TABLE itens_pedido (
    id_item SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL REFERENCES pedidos(id_pedido),
    id_produto INTEGER NOT NULL REFERENCES produtos(id_produto),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL
);
```

O script completo (com todos os `INSERT`s de dados) está em [`exercicios_subselects_parte01.sql`](./exercicios_subselects_parte01.sql).

## Arquivos entregues

- [`Exercicios_Subselects_PostgreSQL.docx`](./Exercicios_Subselects_PostgreSQL.docx) — enunciado original completo, com orientações e tabela de exercícios.
- [`exercicios_subselects_parte01.sql`](./exercicios_subselects_parte01.sql) — script de criação e preenchimento do banco de apoio.

## Material relacionado

- [Aula: Joins e Subconsultas](../../Aulas/Aulas%20Joins%20e%20Sub%20selects/detalhes.md) — teoria de `IN`, `NOT IN`, `EXISTS`, `ANY`/`ALL` e subconsultas correlacionadas.
- [Prova — parte 2 (tabelas derivadas, `INSERT`/`UPDATE`/`DELETE`, `EXPLAIN ANALYZE`)](../Exerciciso%20-%20SUbSelect%20-%20parte%202/detalhes.md)
