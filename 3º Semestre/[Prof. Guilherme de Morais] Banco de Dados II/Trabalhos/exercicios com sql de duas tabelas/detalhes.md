# Trabalho: exercicios com sql de duas tabelas

> **Professor:** Prof. Guilherme de Morais
> **Disciplina:** Banco de Dados II (3º Semestre)
> **Prazo de Entrega:** 14/05/2026 às 02:59
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** `JOIN`, `LEFT JOIN`, agregação (`SUM`, `COUNT`, `AVG`, `MAX`), filtros por data e valor

## Descrição da atividade

O enunciado original traz duas variações do mesmo domínio (`Clientes`/`Pedidos`), cada uma com sua própria carga de dados, ampliando os exercícios de junção trabalhados na [Aula — Exercícios de SQL com Duas Tabelas](../../Aulas/exercicios%20com%20sql%20de%20duas%20tabelas/detalhes.md). A resolução completa está em [`solucao_exercicios_sql.sql`](./solucao_exercicios_sql.sql).

## Estrutura do banco de apoio

**Parte 1** — `Clientes(ClienteID, Nome, Cidade, Email)` / `Pedidos(PedidoID, ClienteID, DataPedido, Valor)`, com 10 clientes e 10 pedidos.

**Parte 2** — `Clientes(ClienteID, Nome, Cidade, Estado)` / `Pedidos(PedidoID, ClienteID, DataPedido, Valor)`, com outros 10 clientes e 10 pedidos (um cliente, o de `ClienteID = 10`, sem pedidos).

## Enunciado — Parte 1

1. Listar todos os clientes e seus pedidos.
2. Mostrar o valor total de pedidos por cliente.
3. Exibir clientes sem pedidos.
4. Listar pedidos acima de 300 reais com o nome do cliente.
5. Contar pedidos por cliente.
6. Listar clientes que fizeram pedidos em maio de 2026.
7. Mostrar o pedido mais caro de cada cliente.
8. Listar clientes com mais de um pedido.
9. Exibir pedidos com nome do cliente e data.
10. Calcular a média de valor dos pedidos por cliente.

## Enunciado — Parte 2

1. Mostrar pedidos com nome do cliente.
2. Listar clientes que têm pedidos.
3. Calcular o total gasto por cliente.
4. Listar clientes sem pedidos, exibindo cidade e nome.
5. Listar pedidos acima de 1000 reais com os nomes dos clientes.
6. Contar a quantidade de pedidos por cliente.
7. Listar os pedidos realizados em maio de 2026.
8. Mostrar o maior pedido por cliente.

<details>
<summary>Gabarito — Parte 1</summary>

```sql
-- 1.
SELECT c.Nome, p.PedidoID, p.DataPedido, p.Valor
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID;

-- 2.
SELECT c.Nome, SUM(p.Valor) AS ValorTotal
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;

-- 3.
SELECT c.Nome FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.PedidoID IS NULL;

-- 4.
SELECT c.Nome, p.Valor FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.Valor > 300;

-- 5.
SELECT c.Nome, COUNT(p.PedidoID) AS QtdPedidos
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;

-- 6.
SELECT DISTINCT c.Nome FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.DataPedido BETWEEN '2026-05-01' AND '2026-05-31';

-- 7.
SELECT c.Nome, MAX(p.Valor) AS PedidoMaisCaro
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;

-- 8.
SELECT c.Nome, COUNT(p.PedidoID) AS QtdPedidos
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
HAVING COUNT(p.PedidoID) > 1;

-- 9.
SELECT c.Nome, p.DataPedido FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID;

-- 10.
SELECT c.Nome, AVG(p.Valor) AS MediaPedidos
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;
```
</details>

<details>
<summary>Gabarito — Parte 2</summary>

```sql
-- 1.
SELECT p.PedidoID, c.Nome, p.DataPedido, p.Valor
FROM Pedidos p JOIN Clientes c ON p.ClienteID = c.ClienteID;

-- 2.
SELECT DISTINCT c.Nome FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID;

-- 3.
SELECT c.Nome, SUM(p.Valor) AS TotalGasto
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;

-- 4.
SELECT c.Cidade, c.Nome FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.PedidoID IS NULL;

-- 5.
SELECT c.Nome, p.Valor FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.Valor > 1000;

-- 6.
SELECT c.Nome, COUNT(p.PedidoID) AS QtdPedidos
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;

-- 7.
SELECT * FROM Pedidos
WHERE DataPedido BETWEEN '2026-05-01' AND '2026-05-31';

-- 8.
SELECT c.Nome, MAX(p.Valor) AS MaiorPedido
FROM Clientes c JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome;
```
</details>

## Arquivos entregues

- [`exercicios com sql de duas tabelas.docx`](./exercicios%20com%20sql%20de%20duas%20tabelas.docx) — enunciado original com as duas partes (DDL + `INSERT`s + questões).
- [`solucao_exercicios_sql.sql`](./solucao_exercicios_sql.sql) — resolução completa comentada, compatível com os principais SGBDs (PostgreSQL, MySQL, SQL Server, Oracle, SQLite).

## Material relacionado

- [Aula — Exercícios de SQL com Duas Tabelas (JOIN)](../../Aulas/exercicios%20com%20sql%20de%20duas%20tabelas/detalhes.md)
- [Aula 06 — Operador IN e Consultas com Múltiplas Tabelas](../../Aulas/COMANDO%20IN%20E%20SQL%20MAIS%20COMPLEXAS/detalhes.md)
