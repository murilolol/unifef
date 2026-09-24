/*
 * Disciplina : Tópicos Avançados em Banco de Dados (4º Semestre)
 * Professor  : Prof. Welington Garcia
 * Tema       : Resolução Integral dos Exercícios de Fixação e Revisão
 * SGBD       : PostgreSQL 14+
 *
 * Como executar:
 *   psql -U postgres -d seu_banco -f resolucao_exercicios.sql
 */

-- ========================================================================
-- SEÇÃO 1: EXERCÍCIOS DE FIXAÇÃO - JOINS (NÍVEL INICIAL)
-- ========================================================================

-- Exercício 1:
-- Enunciado: Exiba código, data, status e nome do cliente de cada pedido.
SELECT
    p.id_pedido AS codigo_pedido,
    p.data_pedido,
    p.status,
    c.nome AS nome_cliente
FROM pedidos p
INNER JOIN clientes c
    ON p.id_cliente = c.id_cliente
ORDER BY p.id_pedido;

-- Exercício 2:
-- Enunciado: Exiba todos os clientes, inclusive os que não possuem pedidos.
SELECT
    c.id_cliente,
    c.nome AS cliente,
    p.id_pedido,
    p.data_pedido,
    p.status
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
ORDER BY c.id_cliente;

-- Exercício 3:
-- Enunciado: Exiba somente os clientes que nunca fizeram pedidos.
SELECT
    c.id_cliente,
    c.nome,
    c.cidade,
    c.estado
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL;

-- Exercício 4:
-- Enunciado: Exiba todos os produtos e suas categorias, inclusive sem categoria.
SELECT
    pr.id_produto,
    pr.nome_produto,
    pr.preco,
    COALESCE(ca.nome_categoria, 'Sem categoria') AS categoria
FROM produtos pr
LEFT JOIN categorias ca
    ON pr.id_categoria = ca.id_categoria
ORDER BY pr.id_produto;


-- ========================================================================
-- SEÇÃO 2: EXERCÍCIOS DE FIXAÇÃO - JOINS (NÍVEL INTERMEDIÁRIO)
-- ========================================================================

-- Exercício 5:
-- Enunciado: Calcule o subtotal de cada item do pedido.
SELECT
    ip.id_pedido,
    pr.nome_produto,
    ip.quantidade,
    ip.preco_unitario,
    (ip.quantidade * ip.preco_unitario) AS subtotal
FROM itens_pedido ip
INNER JOIN produtos pr
    ON ip.id_produto = pr.id_produto
ORDER BY ip.id_pedido, pr.nome_produto;

-- Exercício 6:
-- Enunciado: Conte quantos pedidos cada cliente realizou.
SELECT
    c.id_cliente,
    c.nome AS cliente,
    COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p
    ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome
ORDER BY quantidade_pedidos DESC, c.nome ASC;

-- Exercício 7:
-- Enunciado: Calcule o valor total de cada pedido.
SELECT
    p.id_pedido,
    c.nome AS cliente,
    SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
INNER JOIN clientes c
    ON p.id_cliente = c.id_cliente
INNER JOIN itens_pedido ip
    ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, c.nome
ORDER BY p.id_pedido;

-- Exercício 8:
-- Enunciado: Exiba cada funcionário e o nome do supervisor.
SELECT
    f.id_funcionario,
    f.nome AS funcionario,
    f.cargo,
    COALESCE(s.nome, 'Sem supervisor') AS supervisor
FROM funcionarios f
LEFT JOIN funcionarios s
    ON f.id_supervisor = s.id_funcionario
ORDER BY f.id_funcionario;


-- ========================================================================
-- SEÇÃO 3: EXERCÍCIOS DE FIXAÇÃO - SUBCONSULTAS (NÍVEL INICIAL)
-- ========================================================================

-- Exercício 9 (Subconsultas 1):
-- Enunciado: Exiba os produtos com preço acima da média geral.
SELECT
    id_produto,
    nome_produto,
    preco
FROM produtos
WHERE preco > (
    SELECT AVG(preco)
    FROM produtos
)
ORDER BY preco DESC;

-- Exercício 10 (Subconsultas 2):
-- Enunciado: Exiba o produto ou os produtos com o menor preço.
SELECT
    id_produto,
    nome_produto,
    preco
FROM produtos
WHERE preco = (
    SELECT MIN(preco)
    FROM produtos
);

-- Exercício 11 (Subconsultas 3):
-- Enunciado: Exiba os clientes que possuem pedidos utilizando IN.
SELECT
    id_cliente,
    nome,
    cidade,
    estado
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
)
ORDER BY id_cliente;

-- Exercício 12 (Subconsultas 4):
-- Enunciado: Exiba os clientes sem pedidos utilizando NOT EXISTS.
SELECT
    c.id_cliente,
    c.nome,
    c.cidade,
    c.estado
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
);


-- ========================================================================
-- SEÇÃO 4: EXERCÍCIOS DE FIXAÇÃO - SUBCONSULTAS (NÍVEL INTERMEDIÁRIO)
-- ========================================================================

-- Exercício 13 (Subconsultas 5):
-- Enunciado: Exiba produtos com preço acima da média da própria categoria.
SELECT
    p1.id_produto,
    p1.nome_produto,
    p1.preco,
    p1.id_categoria
FROM produtos p1
WHERE p1.id_categoria IS NOT NULL
  AND p1.preco > (
    SELECT AVG(p2.preco)
    FROM produtos p2
    WHERE p2.id_categoria = p1.id_categoria
  )
ORDER BY p1.id_categoria, p1.preco DESC;

-- Exercício 14 (Subconsultas 6):
-- Enunciado: Exiba clientes que realizaram mais pedidos que a média.
SELECT
    c.id_cliente,
    c.nome,
    COUNT(p.id_pedido) AS total_pedidos
FROM clientes c
INNER JOIN pedidos p
    ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome
HAVING COUNT(p.id_pedido) > (
    SELECT AVG(contagem_pedidos)
    FROM (
        SELECT COUNT(id_pedido) AS contagem_pedidos
        FROM pedidos
        GROUP BY id_cliente
    ) AS sub_media
);

-- Exercício 15 (Subconsultas 7):
-- Enunciado: Exiba categorias que não possuem produtos.
SELECT
    ca.id_categoria,
    ca.nome_categoria
FROM categorias ca
WHERE NOT EXISTS (
    SELECT 1
    FROM produtos pr
    WHERE pr.id_categoria = ca.id_categoria
);

-- Exercício 16 (Subconsultas 8):
-- Enunciado: Exiba pedidos cujo valor total seja maior que a média dos pedidos.
SELECT
    t.id_pedido,
    t.valor_total
FROM (
    SELECT
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS t
WHERE t.valor_total > (
    SELECT AVG(x.valor_total)
    FROM (
        SELECT SUM(quantidade * preco_unitario) AS valor_total
        FROM itens_pedido
        GROUP BY id_pedido
    ) AS x
)
ORDER BY t.valor_total DESC;


-- ========================================================================
-- SEÇÃO 5: RESPOSTAS CONCEITUAIS ÀS PERGUNTAS ESSENCIAIS DE REVISÃO
-- ========================================================================
/*
 * Resumo Teórico / Engenharia de Software:
 *
 * 1. Qual JOIN retorna somente correspondências?
 *    Resposta: O INNER JOIN. Linhas da tabela A sem correspondência na tabela B
 *    (ou vice-versa) com base no predicado ON são estritamente descartadas.
 *
 * 2. Como localizar registros sem correspondência?
 *    Resposta: Utilizando um LEFT JOIN associado a um filtro 'WHERE tabela_direita.pk IS NULL'
 *    (Anti-Join), ou através da cláusula 'WHERE NOT EXISTS (SELECT 1 ...)' correlacionada.
 *
 * 3. Por que a posição das tabelas importa no LEFT JOIN?
 *    Resposta: Porque o LEFT JOIN preserva integralmente todas as tuplas da relação
 *    declarada à esquerda (FROM), preenchendo com NULL colunas não casadas da direita (JOIN).
 *    Inverter as tabelas altera a cardinalidade e quais registros são mantidos incondicionalmente.
 *
 * 4. Qual a diferença entre ON e WHERE em Outer Joins?
 *    Resposta: A cláusula ON define os critérios de junção e emparelhamento antes ou durante
 *    a construção da junção externa (não filtrando tuplas da tabela preservada). O WHERE
 *    atua após a junção externa, descartando linhas que não atendam ao predicado final.
 *
 * 5. Quando usar SELF JOIN?
 *    Resposta: Em modelos com autorrelacionamento (relações unárias), tais como árvores
 *    hierárquicas (funcionário e supervisor), estruturas de montagem (BOM - Bill of Materials)
 *    ou comparação de tuplas da mesma tabela em janelas temporais distintas.
 *
 * 6. Por que CROSS JOIN exige cuidado?
 *    Resposta: Porque produz o produto cartesiano estrito (cardinalidade = |A| * |B|).
 *    Tabelas com 10.000 linhas geram 100.000.000 de registros intermediários, podendo
 *    esgotar memória de trabalho (work_mem) e gerar contenção severa de I/O em disco.
 *
 * 7. O que diferencia a consulta interna da externa?
 *    Resposta: A consulta interna (subconsulta) calcula um resultado intermediário
 *    (escalar, lista ou tabela temporária) que alimenta o escopo da consulta externa
 *    para filtragem, projeção calculada ou referência de junção.
 *
 * 8. Quando uma subconsulta é escalar?
 *    Resposta: Quando ela retorna estritamente uma única linha e uma única coluna.
 *    Pode ser empregada em operadores relacionais de comparação direta (=, <, >, <=, >=, <>).
 *
 * 9. Por que NOT IN exige cuidado com NULL?
 *    Resposta: Devido à lógica trivalente (Three-Valued Logic: TRUE, FALSE, UNKNOWN).
 *    Se a subconsulta contiver pelo menos um valor NULL, a expressão 'valor NOT IN (..., NULL)'
 *    avalia para UNKNOWN, fazendo com que nenhuma linha seja retornada pelo predicado WHERE.
 *
 * 10. Qual a diferença entre ANY e ALL?
 *     Resposta: 'op ANY' (equivalente a SOME) retorna TRUE se a comparação for satisfeita
 *     para ao menos um elemento do conjunto gerado. 'op ALL' exige que a comparação
 *     seja rigorosamente verdadeira para todos os elementos retornados.
 *
 * 11. O que torna uma subconsulta correlacionada?
 *     Resposta: O fato de a consulta interna referenciar colunas da linha em avaliação
 *     da consulta externa. Isso força o otimizador a avaliar a subconsulta contextualmente
 *     para cada tupla externa processada (a menos que reescrita internamente para Semi-Join).
 *
 * 12. Quando utilizar uma tabela derivada?
 *     Resposta: Quando é necessário pré-agregar ou transformar dados em um escopo intermediário
 *     na cláusula FROM para depois unir esse resultado agregado à tabela dimensional original,
 *     evitando agrupamentos excessivos na consulta principal.
 */
