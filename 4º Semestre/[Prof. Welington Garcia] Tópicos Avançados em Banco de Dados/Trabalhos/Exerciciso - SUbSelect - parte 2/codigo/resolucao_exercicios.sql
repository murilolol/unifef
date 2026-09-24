-- ============================================================================
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR : Prof. Welington Garcia
-- TEMA      : Resolução da Lista de Exercícios - Subconsultas Avançadas (Parte 2)
-- EXECUÇÃO  : psql -U <usuario> -d <nome_banco> -f resolucao_exercicios.sql
-- ============================================================================

-- ============================================================================
-- Exercício 1 — Subconsulta no FROM: média por categoria
-- Objetivo: Calcular a média na tabela derivada e filtrar na consulta externa (> 1000.00)
-- ============================================================================
SELECT
    sub.id_categoria,
    sub.nome_categoria,
    ROUND(sub.preco_medio, 2) AS preco_medio
FROM (
    SELECT
        c.id_categoria,
        c.nome_categoria,
        AVG(p.preco) AS preco_medio
    FROM categorias c
    JOIN produtos p ON c.id_categoria = p.id_categoria
    GROUP BY c.id_categoria, c.nome_categoria
) AS sub
WHERE sub.preco_medio > 1000.00
ORDER BY sub.preco_medio DESC;

-- ============================================================================
-- Exercício 2 — Tabela derivada: total de cada pedido
-- Objetivo: Calcular o valor total por pedido e filtrar pedidos com valor > 3000.00
-- ============================================================================
SELECT
    sub.id_pedido,
    ROUND(sub.valor_total, 2) AS valor_total
FROM (
    SELECT
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS sub
WHERE sub.valor_total > 3000.00
ORDER BY sub.valor_total DESC;

-- ============================================================================
-- Exercício 3 — Subconsulta no FROM combinada com JOIN
-- Objetivo: Relacionar a tabela derivada de totais com pedidos e clientes
-- ============================================================================
SELECT
    p.id_pedido AS codigo_pedido,
    c.nome_cliente,
    p.data_pedido,
    ROUND(totais.valor_total, 2) AS valor_total
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN (
    SELECT
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS totais ON p.id_pedido = totais.id_pedido
ORDER BY p.id_pedido;

-- ============================================================================
-- Exercício 4 — Clientes que gastaram acima da média
-- Objetivo: Clientes cujo montante acumulado supera a média gasta por clientes compradores
-- ============================================================================
SELECT
    c.id_cliente,
    c.nome_cliente,
    ROUND(gastos.total_gasto, 2) AS total_gasto
FROM (
    SELECT
        p.id_cliente,
        SUM(ip.quantidade * ip.preco_unitario) AS total_gasto
    FROM pedidos p
    JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
    GROUP BY p.id_cliente
) AS gastos
JOIN clientes c ON gastos.id_cliente = c.id_cliente
WHERE gastos.total_gasto > (
    SELECT AVG(totais_compradores.total_individual)
    FROM (
        SELECT
            p_aux.id_cliente,
            SUM(ip_aux.quantidade * ip_aux.preco_unitario) AS total_individual
        FROM pedidos p_aux
        JOIN itens_pedido ip_aux ON p_aux.id_pedido = ip_aux.id_pedido
        GROUP BY p_aux.id_cliente
    ) AS totais_compradores
)
ORDER BY gastos.total_gasto DESC;

-- ============================================================================
-- Exercício 5 — Categoria com maior preço médio
-- Objetivo: Subconsulta no FROM para médias e subconsulta aninhada para identificar o MAX
-- ============================================================================
SELECT
    medias.id_categoria,
    medias.nome_categoria,
    ROUND(medias.preco_medio, 2) AS maior_preco_medio
FROM (
    SELECT
        c.id_categoria,
        c.nome_categoria,
        AVG(p.preco) AS preco_medio
    FROM categorias c
    JOIN produtos p ON c.id_categoria = p.id_categoria
    GROUP BY c.id_categoria, c.nome_categoria
) AS medias
WHERE medias.preco_medio = (
    SELECT MAX(sub_max.media_calculada)
    FROM (
        SELECT
            AVG(p_inner.preco) AS media_calculada
        FROM produtos p_inner
        GROUP BY p_inner.id_categoria
    ) AS sub_max
);

-- ============================================================================
-- Exercício 6 — INSERT utilizando subconsulta
-- Objetivo: Criar produtos_promocao e carregar itens com preco > media geral (10% desconto)
-- ============================================================================
DROP TABLE IF EXISTS produtos_promocao CASCADE;

CREATE TABLE produtos_promocao (
    id_produto INT PRIMARY KEY,
    nome_produto VARCHAR(150) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    preco_promocional NUMERIC(10, 2) NOT NULL
);

INSERT INTO produtos_promocao (id_produto, nome_produto, preco, preco_promocional)
SELECT
    p.id_produto,
    p.nome_produto,
    p.preco,
    ROUND(p.preco * 0.90, 2) AS preco_promocional
FROM produtos p
WHERE p.preco > (
    SELECT AVG(preco)
    FROM produtos
);

-- Validação do INSERT executado
SELECT * FROM produtos_promocao ORDER BY preco DESC;

-- ============================================================================
-- Exercício 7 — UPDATE utilizando subconsulta
-- Objetivo: Aumentar em 10% o limite de crédito dos clientes com pelo menos 2 pedidos
-- ============================================================================
-- 1. Consulta preliminar de verificação dos registros a serem afetados
SELECT
    id_cliente,
    nome_cliente,
    limite_credito AS limite_atual,
    ROUND(limite_credito * 1.10, 2) AS limite_projetado
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
    GROUP BY id_cliente
    HAVING COUNT(id_pedido) >= 2
);

-- 2. Execução do comando UPDATE
UPDATE clientes
SET limite_credito = ROUND(limite_credito * 1.10, 2)
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
    GROUP BY id_cliente
    HAVING COUNT(id_pedido) >= 2
);

-- 3. Validação pós-atualização
SELECT id_cliente, nome_cliente, limite_credito
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
    GROUP BY id_cliente
    HAVING COUNT(id_pedido) >= 2
);

-- ============================================================================
-- Exercício 8 — UPDATE baseado na média da própria categoria
-- Objetivo: Subconsulta correlacionada para acrescer 5 unidades de estoque se preco > media da categoria
-- ============================================================================
-- 1. Consulta preliminar com subconsulta correlacionada
SELECT
    p.id_produto,
    p.nome_produto,
    p.id_categoria,
    p.preco,
    ROUND((
        SELECT AVG(p_sub.preco)
        FROM produtos p_sub
        WHERE p_sub.id_categoria = p.id_categoria
    ), 2) AS media_categoria,
    p.estoque AS estoque_atual,
    p.estoque + 5 AS estoque_projetado
FROM produtos p
WHERE p.preco > (
    SELECT AVG(p_sub.preco)
    FROM produtos p_sub
    WHERE p_sub.id_categoria = p.id_categoria
);

-- 2. Execução do UPDATE correlacionado
UPDATE produtos p
SET estoque = estoque + 5
WHERE p.preco > (
    SELECT AVG(p_sub.preco)
    FROM produtos p_sub
    WHERE p_sub.id_categoria = p.id_categoria
);

-- 3. Validação do estoque alterado
SELECT id_produto, nome_produto, id_categoria, preco, estoque
FROM produtos
ORDER BY id_categoria, preco DESC;

-- ============================================================================
-- Exercício 9 — DELETE utilizando NOT EXISTS
-- Objetivo: Excluir clientes sem pedidos associados, conferindo antes com SELECT
-- ============================================================================
-- 1. Consulta prévia obrigatória para auditar registros que serão excluídos
SELECT
    c.id_cliente,
    c.nome_cliente,
    c.limite_credito
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- 2. Execução do DELETE com NOT EXISTS
DELETE FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- 3. Validação: A consulta não deve retornar nenhuma linha
SELECT c.id_cliente, c.nome_cliente
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- ============================================================================
-- Exercício 10 — Análise de desempenho com EXPLAIN ANALYZE
-- Objetivo: Comparar planos de execução entre subconsulta IN e EXISTS
-- ============================================================================

-- Consulta A: Filtragem de clientes com pedidos usando IN
EXPLAIN ANALYZE
SELECT
    c.id_cliente,
    c.nome_cliente
FROM clientes c
WHERE c.id_cliente IN (
    SELECT p.id_cliente
    FROM pedidos p
);

-- Consulta B: Filtragem de clientes com pedidos usando EXISTS
EXPLAIN ANALYZE
SELECT
    c.id_cliente,
    c.nome_cliente
FROM clientes c
WHERE EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- NOTA DE ANÁLISE DE ENGENHARIA:
-- O otimizador de consultas do PostgreSQL (Cost-Based Optimizer - CBO) transforma
-- tanto o IN com subconsulta quanto o EXISTS correlacionado em uma operação interna
-- de Semi-Join (frequentemente Hash Semi Join ou Merge Semi Join dependendo do índice
-- e tamanho do conjunto). O EXISTS interrompe a busca na primeira correspondência
-- positiva (short-circuit), enquanto IN requer deduplicação dos valores nulos e repetidos
-- caso o planejador não consiga reescrever a árvore da consulta.
