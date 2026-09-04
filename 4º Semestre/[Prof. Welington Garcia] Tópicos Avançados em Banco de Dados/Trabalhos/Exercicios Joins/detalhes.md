# Trabalho — Exercícios de Joins no PostgreSQL

> **Professor:** Welington Garcia
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)
> **Pontuação máxima:** 100 pontos
> **Conteúdo cobrado:** `INNER JOIN`, `LEFT JOIN`, múltiplas tabelas, agregações (`COUNT`, `SUM`, `GROUP BY`), `COALESCE`

## Descrição da atividade

Lista de 10 exercícios práticos consolidando o uso de junções (`INNER JOIN`, `LEFT JOIN`) sobre um banco de exemplo de uma loja (`loja_joins`), com as tabelas `clientes`, `vendedores`, `categorias`, `produtos`, `pedidos` e `itens_pedido`. O aluno deve resolver um *case* real modelando consultas que cruzam dados de clientes, pedidos, produtos e fornecedores/vendedores. Recomenda-se executar primeiro o script de criação do banco (ver [`script_resolucao_joins.sql`](./script_resolucao_joins.sql)) e só então resolver os exercícios abaixo.

## Estrutura do banco de apoio (`loja_joins`)

| Tabela | Finalidade |
| :--- | :--- |
| `clientes` | Cadastro de clientes |
| `vendedores` | Cadastro de vendedores |
| `categorias` | Categorias dos produtos |
| `produtos` | Produtos disponíveis |
| `pedidos` | Cabeçalho das vendas |
| `itens_pedido` | Produtos e quantidades de cada pedido |

## Enunciado dos 10 exercícios

1. **Cliente e seus pedidos** — usando `INNER JOIN`, apresente código do pedido, data do pedido e nome do cliente, ordenado pelo código do pedido.
2. **Relatório completo dos itens vendidos** — apresente código do pedido, nome do cliente, nome do produto, quantidade e preço unitário (relaciona `clientes`, `pedidos`, `itens_pedido` e `produtos`).
3. **Clientes sem pedidos** — usando `LEFT JOIN`, liste somente os clientes que nunca realizaram pedidos (verifique onde o código do pedido é `NULL`).
4. **Produtos nunca vendidos** — usando `LEFT JOIN`, liste os produtos que ainda não aparecem em nenhum item de pedido (código, nome e preço).
5. **Quantidade de pedidos por cliente** — nome de cada cliente e a quantidade de pedidos realizados; todos os clientes devem aparecer, inclusive com zero pedidos (`LEFT JOIN` + `COUNT()` + `GROUP BY`).
6. **Faturamento por produto** — quanto cada produto gerou em vendas (`quantidade × preco_unitario`), ordenado do maior para o menor.
7. **Total de cada pedido** — valor total de cada pedido somando os subtotais dos itens; exiba código, data, cliente e valor total.
8. **Produtos vendidos para clientes de São Paulo** — cliente, cidade, produto e quantidade comprada, filtrando `cidade = 'São Paulo'`.
9. **Faturamento por vendedor** — nome do vendedor e faturamento total; todos os vendedores devem aparecer, com `0` quando não houver venda (dica: `COALESCE()`).
10. **Desafio — relatório geral de vendas** — pedido, data, cliente, cidade, estado, vendedor, produto, categoria, quantidade, preço unitário e subtotal, ordenado por código do pedido e nome do produto.

## Modelo relacional

O mesmo modelo utilizado na aula de Joins e Subconsultas se aplica aqui, com o acréscimo da tabela `vendedores`:

![Modelo relacional — Joins e Subconsultas](../../Aulas/Aulas%20Joins%20e%20Sub%20selects/diagramas/modelo-relacional-joins-subselects.svg)

## Arquivos entregues

- [`exercicios Joins.docx`](./exercicios%20Joins.docx) — enunciado original com o script de criação do banco `loja_joins`.
- [`script_resolucao_joins.sql`](./script_resolucao_joins.sql) — script completo de setup (DDL + `INSERT`s) e resolução comentada dos 10 exercícios.

## Material relacionado

- [Aula: Joins e Subconsultas](../../Aulas/Aulas%20Joins%20e%20Sub%20selects/detalhes.md)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
