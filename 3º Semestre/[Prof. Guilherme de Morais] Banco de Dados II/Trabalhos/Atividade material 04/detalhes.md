# Trabalho: Atividade material 04

> **Professor:** Prof. Guilherme de Morais
> **Disciplina:** Banco de Dados II (3º Semestre)
> **Prazo de Entrega:** 19/04/2026 às 02:59
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** `SELECT`, `WHERE`, `ORDER BY`, `AND`/`OR`, `DISTINCT`, operadores aritméticos, `BETWEEN`, consultas com múltiplas condições

## Descrição da atividade

Lista de 35 exercícios (25 básicos + 10 desafios) sobre o banco `CLIENTE`/`PRODUTO`/`VENDEDOR`/`PEDIDO`, aprofundando o conteúdo da [Aula 03/04](../../Aulas/aula%2003%20-%20insert%20delete%20e%20update%2C%20aula%2004%20consultado/detalhes.md). O script completo (DDL + `INSERT`s + resolução) está em [`atividade_material_04.sql`](./atividade_material_04.sql).

## Estrutura do banco de apoio

| Tabela | Finalidade |
| :--- | :--- |
| `CLIENTE` | Cadastro de clientes (código, nome, cidade, estado, idade) |
| `PRODUTO` | Catálogo de produtos (código, descrição, unidade, valor unitário) |
| `VENDEDOR` | Cadastro de vendedores (código, nome, salário) |
| `PEDIDO` | Pedidos, vinculando `cod_cliente` e `cod_vendedor` |

## Enunciado — Parte 1: Exercícios básicos (25 questões)

1. Liste todos os dados da tabela `CLIENTE`.
2. Liste apenas nome e idade dos clientes.
3. Liste código e descrição dos produtos.
4. Liste os clientes que moram no estado `SP`.
5. Liste os clientes com idade maior que 35 anos.
6. Liste os produtos com valor unitário menor que 10.
7. Liste os vendedores com salário maior ou igual a 3000.
8. Liste todos os clientes ordenados por nome.
9. Liste os clientes ordenados por estado e idade.
10. Liste os produtos ordenados pelo valor unitário (do maior para o menor).
11. Liste os clientes do estado `SP` e com idade maior que 30.
12. Liste os clientes do estado `RJ` ou `MG`.
13. Liste os produtos com valor maior que 10 e unidade `'KG'`.
14. Liste os vendedores com salário igual a 2800 ou 3000.
15. Liste os produtos cuja unidade seja `'UN'` e valor menor que 10.
16. Liste todas as cidades distintas dos clientes.
17. Liste todos os estados distintos cadastrados.
18. Liste os produtos com valor entre 5 e 20.
19. Liste os vendedores com salário entre 2500 e 3500.
20. Liste os clientes com idade entre 25 e 40 anos.
21. Mostre o nome do produto e o valor com aumento de 20%.
22. Mostre o nome do produto e o valor com desconto de 10%.
23. Liste os vendedores com salário e o salário com bônus de 500 reais.
24. Liste todos os pedidos realizados por um cliente específico (ex.: cliente 10).
25. Liste todos os pedidos realizados após a data `'2024-02-01'`.

## Enunciado — Parte 2: Desafios (10 questões)

1. Liste o nome, cidade e idade dos clientes que moram em `SP` e possuem idade entre 25 e 40 anos, ordenados por idade (decrescente).
2. Liste os produtos com descrição, valor atual e valor com aumento de 30% (alias), filtrando apenas produtos com valor entre 5 e 20.
3. Liste os vendedores com nome, salário atual e salário com bônus de 15%, somente os com salário entre 2500 e 3500, ordenados pelo maior salário ajustado.
4. Liste os clientes que moram em `SP` ou `RJ` e têm idade maior que 30, exibindo nome, estado e idade, ordenados por estado e nome.
5. Liste os produtos com unidade `'KG'` e valor menor que 10 ou maior que 30, exibindo descrição, unidade e valor.
6. Liste os pedidos realizados após `'2024-02-10'` e que pertencem a vendedores com código entre 10 e 30, ordenados por data do pedido.
7. Liste os clientes distintos por cidade, exibindo cidade e quantidade de clientes.
8. Liste os produtos com valor atual, valor com desconto de 20% e valor com aumento de 10%, filtrando apenas produtos com unidade `'UN'`.
9. Liste os vendedores que **não** possuem salário entre 2800 e 3200, exibindo nome e salário, ordenados por salário crescente.
10. **(Desafio de alto nível)** Liste os pedidos exibindo código do pedido, código do cliente e código do vendedor, com as condições: pedidos realizados em fevereiro de 2024, cliente entre 10 e 30, vendedor com salário maior que 3000; ordenados por cliente e data do pedido.

<details>
<summary>Gabarito — Parte 1 (seleção representativa)</summary>

```sql
-- 1.
SELECT * FROM CLIENTE;

-- 2.
SELECT nome, idade FROM CLIENTE;

-- 4.
SELECT * FROM CLIENTE WHERE estado = 'SP';

-- 8.
SELECT * FROM CLIENTE ORDER BY nome;

-- 11.
SELECT * FROM CLIENTE WHERE estado = 'SP' AND idade > 30;

-- 16.
SELECT DISTINCT cidade FROM CLIENTE;

-- 18.
SELECT * FROM PRODUTO WHERE valor_unitario BETWEEN 5 AND 20;

-- 21.
SELECT descricao, valor_unitario * 1.20 AS valor_com_aumento FROM PRODUTO;

-- 24.
SELECT * FROM PEDIDO WHERE cod_cliente = 10;

-- 25.
SELECT * FROM PEDIDO WHERE data_pedido > '2024-02-01';
```
</details>

<details>
<summary>Gabarito — Parte 2 (desafios)</summary>

```sql
-- 1.
SELECT nome, cidade, idade FROM CLIENTE
WHERE estado = 'SP' AND idade BETWEEN 25 AND 40
ORDER BY idade DESC;

-- 2.
SELECT descricao, valor_unitario AS valor_atual,
       valor_unitario * 1.30 AS valor_com_aumento
FROM PRODUTO
WHERE valor_unitario BETWEEN 5 AND 20;

-- 3.
SELECT nome, salario AS salario_atual,
       salario * 1.15 AS salario_com_bonus
FROM VENDEDOR
WHERE salario BETWEEN 2500 AND 3500
ORDER BY salario_com_bonus DESC;

-- 4.
SELECT nome, estado, idade FROM CLIENTE
WHERE estado IN ('SP', 'RJ') AND idade > 30
ORDER BY estado, nome;

-- 5.
SELECT descricao, unidade, valor_unitario FROM PRODUTO
WHERE unidade = 'KG' AND (valor_unitario < 10 OR valor_unitario > 30);

-- 6.
SELECT * FROM PEDIDO
WHERE data_pedido > '2024-02-10' AND cod_vendedor BETWEEN 10 AND 30
ORDER BY data_pedido;

-- 7.
SELECT cidade, COUNT(*) AS quantidade_clientes
FROM CLIENTE
GROUP BY cidade;

-- 8.
SELECT descricao, valor_unitario AS valor_atual,
       valor_unitario * 0.80 AS valor_com_desconto,
       valor_unitario * 1.10 AS valor_com_aumento
FROM PRODUTO
WHERE unidade = 'UN';

-- 9.
SELECT nome, salario FROM VENDEDOR
WHERE NOT (salario BETWEEN 2800 AND 3200)
ORDER BY salario ASC;

-- 10.
SELECT cod_pedido, cod_cliente, cod_vendedor
FROM PEDIDO p
JOIN VENDEDOR v ON p.cod_vendedor = v.cod_vendedor
WHERE EXTRACT(MONTH FROM p.data_pedido) = 2
  AND EXTRACT(YEAR FROM p.data_pedido) = 2024
  AND p.cod_cliente BETWEEN 10 AND 30
  AND v.salario > 3000
ORDER BY p.cod_cliente, p.data_pedido;
```
</details>

## Arquivos entregues

- [`lista de exercicios do material 04.docx`](./lista%20de%20exercicios%20do%20material%2004.docx) — enunciado original completo (35 questões).
- [`aula 04 - CONSULTANDO DADOS 1.pdf`](./aula%2004%20-%20CONSULTANDO%20DADOS%201.pdf) — slide de apoio em PDF.
- [`atividade_material_04.sql`](./atividade_material_04.sql) — script completo de setup (DDL + `INSERT`s) e resolução comentada.

## Material relacionado

- [Aula 03/04 — INSERT, DELETE, UPDATE e SELECT básico](../../Aulas/aula%2003%20-%20insert%20delete%20e%20update%2C%20aula%2004%20consultado/detalhes.md)
- [Atividade da banca — mesmo estilo de exercícios sobre schema EscolaDB](../../Aulas/atividade%20s=%20banca/detalhes.md)
