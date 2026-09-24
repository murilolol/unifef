/*
 * Disciplina : Tópicos Avançados em Banco de Dados (4º Semestre)
 * Professor  : Prof. Welington Garcia
 * Tema       : Exemplos Práticos de Aula - JOINs e Subconsultas
 * SGBD       : PostgreSQL 14+
 *
 * Como executar:
 *   psql -U postgres -d seu_banco -f exemplos_aula.sql
 */

-- ========================================================================
-- PARTE 1: JOINS EM SQL (POSTGRESQL)
-- ========================================================================

-- Exemplo 1: INNER JOIN com apelidos (aliases)
-- Retorna apenas os registros com correspondência direta nas duas tabelas.
SELECT
    p.id_pedido AS codigo,
    p.data_pedido AS data,
    p.status,
    c.nome AS cliente
FROM pedidos AS p
INNER JOIN clientes AS c
    ON p.id_cliente = c.id_cliente;

-- Exemplo 2: LEFT JOIN preservando registros da tabela à esquerda
-- Clientes sem pedido recebem valores NULL nas colunas projetadas de pedidos.
SELECT
    c.nome,
    p.id_pedido,
    p.status
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente;

-- Exemplo 3: Anti-Join - Encontrando clientes sem pedidos
SELECT
    c.id_cliente,
    c.nome
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL;

-- Exemplo 4: Tratamento de NULL com COALESCE
SELECT
    pr.nome_produto,
    pr.preco,
    COALESCE(ca.nome_categoria, 'Sem categoria') AS categoria
FROM produtos pr
LEFT JOIN categorias ca
    ON pr.id_categoria = ca.id_categoria;

-- Exemplo 5: RIGHT JOIN e a equivalência com LEFT JOIN
-- Forma com RIGHT JOIN
SELECT
    p.id_pedido,
    c.id_cliente,
    c.nome
FROM pedidos p
RIGHT JOIN clientes c
    ON p.id_cliente = c.id_cliente;

-- Forma recomendada (LEFT JOIN invertendo a ordem das tabelas)
SELECT
    p.id_pedido,
    c.id_cliente,
    c.nome
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente;

-- Exemplo 6: FULL OUTER JOIN para comparação e auditoria
SELECT
    c.id_cliente,
    c.nome,
    p.id_pedido,
    p.status
FROM clientes c
FULL OUTER JOIN pedidos p
    ON c.id_cliente = p.id_cliente;

-- Exemplo 7: CROSS JOIN (Produto Cartesiano - todas as combinações possíveis)
SELECT
    c.nome,
    ca.nome_categoria
FROM clientes c
CROSS JOIN categorias ca;

-- Exemplo 8: SELF JOIN (Auto-relacionamento de Funcionário e Supervisor)
SELECT
    f.nome AS funcionario,
    f.cargo,
    COALESCE(s.nome, 'Sem supervisor') AS supervisor
FROM funcionarios f
LEFT JOIN funcionarios s
    ON f.id_supervisor = s.id_funcionario;

-- Exemplo 9: Junção com quatro tabelas e cálculo de subtotal
SELECT
    p.id_pedido,
    c.nome AS cliente,
    pr.nome_produto,
    ip.quantidade,
    ip.preco_unitario,
    ip.quantidade * ip.preco_unitario AS subtotal
FROM pedidos p
INNER JOIN clientes c
    ON p.id_cliente = c.id_cliente
INNER JOIN itens_pedido ip
    ON p.id_pedido = ip.id_pedido
INNER JOIN produtos pr
    ON ip.id_produto = pr.id_produto;

-- Exemplo 10: JOIN combinado com WHERE e ordenação
SELECT
    p.id_pedido,
    p.data_pedido,
    c.nome AS cliente
FROM pedidos p
INNER JOIN clientes c
    ON p.id_cliente = c.id_cliente
WHERE p.status = 'Pago'
ORDER BY p.data_pedido DESC;

-- Exemplo 11: Agregações com LEFT JOIN (Contagem de pedidos por cliente)
SELECT
    c.id_cliente,
    c.nome,
    COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome
ORDER BY quantidade_pedidos DESC;

-- Exemplo 12: Agregação de valor total por pedido
SELECT
    p.id_pedido,
    c.nome AS cliente,
    SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
JOIN clientes c
    ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip
    ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, c.nome;

-- Exemplo 13: Ponto Crítico - Filtro no ON versus Filtro no WHERE em LEFT JOIN
-- (a) Filtro no WHERE elimina clientes sem pedidos pagos (comporta-se como INNER JOIN):
SELECT c.nome, p.id_pedido, p.status
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
WHERE p.status = 'Pago';

-- (b) Filtro no ON preserva todos os clientes, relacionando apenas os pedidos que atendem ao critério:
SELECT c.nome, p.id_pedido, p.status
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
    AND p.status = 'Pago';

-- Exemplo 14: Cláusula USING
SELECT p.id_pedido, c.nome
FROM pedidos p
JOIN clientes c
    USING (id_cliente);


-- ========================================================================
-- PARTE 2: SUBCONSULTAS (SUBSELECTS)
-- ========================================================================

-- Exemplo 15: Subconsulta Escalar no WHERE (Preço maior que a média)
SELECT nome_produto, preco
FROM produtos
WHERE preco > (
    SELECT AVG(preco)
    FROM produtos
);

-- Exemplo 16: Subconsulta Escalar para encontrar o produto mais caro (com empates)
SELECT id_produto, nome_produto, preco
FROM produtos
WHERE preco = (
    SELECT MAX(preco)
    FROM produtos
);

-- Exemplo 17: Subconsulta Escalar na lista de projeção do SELECT
SELECT
    nome_produto,
    preco,
    (SELECT AVG(preco) FROM produtos) AS media_geral
FROM produtos;

-- Exemplo 18: Comparação e cálculo da diferença em relação à média geral
SELECT
    nome_produto,
    preco,
    preco - (
        SELECT AVG(preco)
        FROM produtos
    ) AS diferenca_media
FROM produtos
ORDER BY diferenca_media DESC;

-- Exemplo 19: Operador IN com subconsulta de coluna única
SELECT nome, cidade
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
);

-- Exemplo 20: Operador NOT IN seguro (tratando explicitamente valores NULL)
SELECT nome
FROM clientes
WHERE id_cliente NOT IN (
    SELECT id_cliente
    FROM pedidos
    WHERE id_cliente IS NOT NULL
);

-- Exemplo 21: Operador EXISTS com SELECT 1 (Verificação de existência)
SELECT c.id_cliente, c.nome
FROM clientes c
WHERE EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- Exemplo 22: Operador NOT EXISTS (Clientes que não possuem pedidos)
SELECT c.id_cliente, c.nome
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- Exemplo 23: Operador ANY (Produtos mais caros que ao menos um da categoria 4)
SELECT nome_produto, preco
FROM produtos
WHERE preco > ANY (
    SELECT preco
    FROM produtos
    WHERE id_categoria = 4
);

-- Exemplo 24: Operador ALL (Produtos mais caros que todos os da categoria 4)
SELECT nome_produto, preco
FROM produtos
WHERE preco > ALL (
    SELECT preco
    FROM produtos
    WHERE id_categoria = 4
);

-- Exemplo 25: Subconsulta Correlacionada (Preço acima da média da própria categoria)
SELECT p1.nome_produto, p1.preco, p1.id_categoria
FROM produtos p1
WHERE p1.preco > (
    SELECT AVG(p2.preco)
    FROM produtos p2
    WHERE p2.id_categoria = p1.id_categoria
);

-- Exemplo 26: Subconsulta na cláusula FROM (Tabela Derivada com alias obrigatório)
SELECT
    resumo.id_cliente,
    resumo.total_pedidos
FROM (
    SELECT id_cliente,
           COUNT(*) AS total_pedidos
    FROM pedidos
    GROUP BY id_cliente
) AS resumo
WHERE resumo.total_pedidos > 1;

-- Exemplo 27: Tabela Derivada combinada com JOIN
SELECT
    c.nome,
    r.total_pedidos
FROM clientes c
JOIN (
    SELECT id_cliente,
           COUNT(*) AS total_pedidos
    FROM pedidos
    GROUP BY id_cliente
) r ON r.id_cliente = c.id_cliente
ORDER BY r.total_pedidos DESC;

-- Exemplo 28: Subconsulta na cláusula HAVING
SELECT id_cliente, COUNT(*) AS total
FROM pedidos
GROUP BY id_cliente
HAVING COUNT(*) > (
    SELECT AVG(quantidade)
    FROM (
        SELECT COUNT(*) AS quantidade
        FROM pedidos
        GROUP BY id_cliente
    ) q
);

-- Exemplo 29: Subconsulta em DML - UPDATE baseado em outra tabela
UPDATE produtos
SET preco = preco * 1.10
WHERE id_categoria = (
    SELECT id_categoria
    FROM categorias
    WHERE nome_categoria = 'Informática'
);

-- Exemplo 30: Subconsulta em DML - INSERT ... SELECT
INSERT INTO clientes_vip (id_cliente, nome, total_comprado)
SELECT
    c.id_cliente,
    c.nome,
    COALESCE((
        SELECT SUM(ip.quantidade * ip.preco_unitario)
        FROM pedidos p
        JOIN itens_pedido ip ON ip.id_pedido = p.id_pedido
        WHERE p.id_cliente = c.id_cliente
    ), 0.00)
FROM clientes c;

-- Exemplo 31: Subconsulta em DML - DELETE com NOT EXISTS
DELETE FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);

-- Exemplo 32: Common Table Expression (CTE) com WITH
WITH resumo AS (
    SELECT id_cliente,
           COUNT(*) AS total_pedidos
    FROM pedidos
    GROUP BY id_cliente
)
SELECT c.nome, r.total_pedidos
FROM clientes c
JOIN resumo r
    ON r.id_cliente = c.id_cliente;

-- Exemplo 33: Inspeção de Plano de Execução com EXPLAIN ANALYZE
EXPLAIN ANALYZE
SELECT c.nome
FROM clientes c
WHERE EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);
