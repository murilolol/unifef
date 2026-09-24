/*
  =========================================================================
  Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
  Professor: Prof. Welington Garcia
  Atividade: Resolução da Lista de Exercícios — Subconsultas Avançadas
  =========================================================================
  Como executar:
  1. Certifique-se de ter executado 'esquema_e_dados.sql' previamente.
  2. Execute este script no psql ou pgAdmin:
     \i exercicios_subconsultas.sql
  =========================================================================
*/

-- -------------------------------------------------------------------------
-- Exercício 1 — Subconsulta no FROM: média por categoria
-- Crie uma consulta que calcule, em uma subconsulta no FROM, o preço médio 
-- dos produtos de cada categoria. Na consulta externa, exiba apenas as 
-- categorias cuja média de preços seja superior a R$ 1.000,00.
-- -------------------------------------------------------------------------
SELECT 
    sub.id_categoria,
    sub.preco_medio
FROM (
    SELECT 
        id_categoria,
        ROUND(AVG(preco), 2) AS preco_medio
    FROM produtos
    WHERE id_categoria IS NOT NULL
    GROUP BY id_categoria
) AS sub
WHERE sub.preco_medio > 1000.00;

-- -------------------------------------------------------------------------
-- Exercício 2 — Tabela derivada: total de cada pedido
-- Crie uma subconsulta no FROM que calcule o valor total de cada pedido 
-- utilizando SUM(quantidade * preco_unitario). Na consulta externa, exiba 
-- apenas os pedidos cujo valor total seja superior a R$ 3.000,00.
-- -------------------------------------------------------------------------
SELECT 
    sub.id_pedido,
    sub.valor_total
FROM (
    SELECT 
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS sub
WHERE sub.valor_total > 3000.00;

-- -------------------------------------------------------------------------
-- Exercício 3 — Subconsulta no FROM combinada com JOIN
-- Utilizando uma tabela derivada, calcule o valor total de cada pedido. 
-- Em seguida, relacione o resultado com as tabelas pedidos e clientes para 
-- exibir: código do pedido, nome do cliente, data do pedido e valor total.
-- -------------------------------------------------------------------------
SELECT 
    p.id_pedido,
    c.nome_cliente,
    p.data_pedido,
    sub.valor_total
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
INNER JOIN (
    SELECT 
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS sub ON p.id_pedido = sub.id_pedido;

-- -------------------------------------------------------------------------
-- Exercício 4 — Clientes que gastaram acima da média
-- Calcule o total gasto por cada cliente utilizando uma subconsulta. Depois, 
-- exiba apenas os clientes cujo total gasto seja superior à média de gastos 
-- de todos os clientes que realizaram compras.
-- -------------------------------------------------------------------------
SELECT 
    totais_clientes.id_cliente,
    totais_clientes.nome_cliente,
    totais_clientes.total_gasto
FROM (
    SELECT 
        c.id_cliente,
        c.nome_cliente,
        SUM(ip.quantidade * ip.preco_unitario) AS total_gasto
    FROM clientes c
    INNER JOIN pedidos p ON c.id_cliente = p.id_cliente
    INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
    GROUP BY c.id_cliente, c.nome_cliente
) AS totais_clientes
WHERE totais_clientes.total_gasto > (
    SELECT 
        AVG(gastos.total_gasto)
    FROM (
        SELECT 
            p.id_cliente,
            SUM(ip.quantidade * ip.preco_unitario) AS total_gasto
        FROM pedidos p
        INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
        GROUP BY p.id_cliente
    ) AS gastos
);

-- -------------------------------------------------------------------------
-- Exercício 5 — Categoria com maior preço médio
-- Crie uma consulta que determine qual categoria possui o maior preço médio 
-- entre seus produtos. Utilize uma subconsulta no FROM para calcular as 
-- médias e outra subconsulta para identificar o maior valor.
-- -------------------------------------------------------------------------
SELECT 
    medias.id_categoria,
    c.nome_categoria,
    medias.preco_medio
FROM (
    SELECT 
        id_categoria,
        AVG(preco) AS preco_medio
    FROM produtos
    WHERE id_categoria IS NOT NULL
    GROUP BY id_categoria
) AS medias
INNER JOIN categorias c ON medias.id_categoria = c.id_categoria
WHERE medias.preco_medio = (
    SELECT 
        MAX(sub_medias.preco_medio)
    FROM (
        SELECT 
            AVG(preco) AS preco_medio
        FROM produtos
        WHERE id_categoria IS NOT NULL
        GROUP BY id_categoria
    ) AS sub_medias
);

-- -------------------------------------------------------------------------
-- Exercício 6 — INSERT utilizando subconsulta
-- Crie uma nova tabela chamada produtos_promocao contendo os campos 
-- id_produto, nome_produto, preco e preco_promocional. Depois, utilizando 
-- INSERT INTO ... SELECT, insira nessa tabela todos os produtos cujo preço 
-- esteja acima da média geral. O preco_promocional deverá representar um 
-- desconto de 10% sobre o preço original.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS produtos_promocao (
    id_produto INT PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    preco_promocional NUMERIC(10, 2) NOT NULL
);

INSERT INTO produtos_promocao (id_produto, nome_produto, preco, preco_promocional)
SELECT 
    id_produto,
    nome_produto,
    preco,
    ROUND(preco * 0.90, 2) AS preco_promocional
FROM produtos
WHERE preco > (
    SELECT AVG(preco) 
    FROM produtos
);

-- Validação do INSERT
SELECT * FROM produtos_promocao;

-- -------------------------------------------------------------------------
-- Exercício 7 — UPDATE utilizando subconsulta
-- Crie um comando UPDATE que aumente em 10% o limite de crédito dos clientes 
-- que já realizaram pelo menos dois pedidos. A identificação desses clientes 
-- deverá ser feita por meio de uma subconsulta.
-- -------------------------------------------------------------------------
UPDATE clientes
SET limite_credito = ROUND(limite_credito * 1.10, 2)
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
    GROUP BY id_cliente
    HAVING COUNT(id_pedido) >= 2
);

-- Validação do UPDATE
SELECT * FROM clientes;

-- -------------------------------------------------------------------------
-- Exercício 8 — UPDATE baseado na média da própria categoria
-- Atualize o estoque dos produtos cujo preço seja superior à média de preço 
-- de sua própria categoria. Para esses produtos, acrescente 5 unidades ao 
-- estoque atual. Utilize uma subconsulta correlacionada para comparar cada 
-- produto com a média de sua categoria.
-- -------------------------------------------------------------------------
UPDATE produtos p
SET quantidade_estoque = quantidade_estoque + 5
WHERE preco > (
    SELECT AVG(p_sub.preco)
    FROM produtos p_sub
    WHERE p_sub.id_categoria = p.id_categoria
);

-- Validação do UPDATE
SELECT * FROM produtos;

-- -------------------------------------------------------------------------
-- Exercício 9 — DELETE utilizando NOT EXISTS
-- Considere que seja necessário remover do cadastro todos os clientes que 
-- nunca realizaram pedidos. Crie o comando DELETE utilizando uma subconsulta 
-- com NOT EXISTS. Antes de executar o DELETE, escreva um SELECT utilizando 
-- a mesma condição para verificar quais registros seriam removidos.
-- -------------------------------------------------------------------------
-- Conferencia previa antes da remocao:
SELECT 
    id_cliente, 
    nome_cliente
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);

-- Execucao da remocao idempotente:
DELETE FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);

-- Validação do DELETE
SELECT * FROM clientes;

-- -------------------------------------------------------------------------
-- Exercício 10 — Análise de desempenho com EXPLAIN ANALYZE
-- Crie duas consultas que retornem os clientes que possuem pedidos: uma 
-- utilizando IN com subconsulta e outra utilizando EXISTS. Execute 
-- EXPLAIN ANALYZE antes de cada consulta e compare os planos de execução.
-- -------------------------------------------------------------------------

-- Consulta 1: Utilizando IN com subconsulta
EXPLAIN ANALYZE
SELECT * 
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente 
    FROM pedidos
);

-- Consulta 2: Utilizando EXISTS com subconsulta correlacionada
EXPLAIN ANALYZE
SELECT * 
FROM clientes c
WHERE EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);
