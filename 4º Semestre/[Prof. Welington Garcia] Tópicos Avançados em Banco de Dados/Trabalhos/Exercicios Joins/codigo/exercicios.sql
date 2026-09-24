-- =====================================================================
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR : Prof. Welington Garcia
-- TEMA      : Resolução da Lista de Exercícios - JOINs no PostgreSQL
-- EXECUÇÃO  : psql -U postgres -d loja_joins -f exercicios.sql
-- =====================================================================

-- =====================================================================
-- Exercício 1: Cliente e seus pedidos
-- Utilizando INNER JOIN, apresente o código do pedido, a data do pedido
-- e o nome do cliente. Ordene o resultado pelo código do pedido.
-- =====================================================================
SELECT 
    p.id_pedido,
    p.data_pedido,
    c.nome AS nome_cliente
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
ORDER BY p.id_pedido;

-- =====================================================================
-- Exercício 2: Relatório completo dos itens vendidos
-- Apresente o código do pedido, o nome do cliente, o nome do produto,
-- a quantidade e o preço unitário relacionando clientes, pedidos,
-- itens_pedido e produtos.
-- =====================================================================
SELECT 
    p.id_pedido,
    c.nome AS nome_cliente,
    pr.nome AS nome_produto,
    ip.quantidade,
    ip.preco_unitario
FROM clientes c
INNER JOIN pedidos p ON c.id_cliente = p.id_cliente
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN produtos pr ON ip.id_produto = pr.id_produto
ORDER BY p.id_pedido, pr.nome;

-- =====================================================================
-- Exercício 3: Clientes sem pedidos
-- Utilizando LEFT JOIN, liste somente os clientes que nunca realizaram
-- pedidos (código do pedido IS NULL). Exiba código e nome do cliente.
-- =====================================================================
SELECT 
    c.id_cliente,
    c.nome AS nome_cliente
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL
ORDER BY c.id_cliente;

-- =====================================================================
-- Exercício 4: Produtos nunca vendidos
-- Utilizando LEFT JOIN, liste os produtos que ainda não aparecem em
-- nenhum item de pedido. Exiba o código do produto, nome e preço.
-- =====================================================================
SELECT 
    pr.id_produto,
    pr.nome AS nome_produto,
    pr.preco
FROM produtos pr
LEFT JOIN itens_pedido ip ON pr.id_produto = ip.id_produto
WHERE ip.id_item IS NULL
ORDER BY pr.id_produto;

-- =====================================================================
-- Exercício 5: Quantidade de pedidos por cliente
-- Mostre o nome de cada cliente e a quantidade de pedidos realizados,
-- incluindo clientes com zero pedidos (LEFT JOIN + COUNT + GROUP BY).
-- =====================================================================
SELECT 
    c.nome AS nome_cliente,
    COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome
ORDER BY quantidade_pedidos DESC, c.nome ASC;

-- =====================================================================
-- Exercício 6: Faturamento por produto
-- Calcule quanto cada produto gerou em vendas (quantidade * preco_unitario).
-- Exiba produto e faturamento total, ordenando do maior para o menor.
-- =====================================================================
SELECT 
    pr.nome AS produto,
    SUM(ip.quantidade * ip.preco_unitario) AS faturamento_total
FROM produtos pr
INNER JOIN itens_pedido ip ON pr.id_produto = ip.id_produto
GROUP BY pr.id_produto, pr.nome
ORDER BY faturamento_total DESC;

-- =====================================================================
-- Exercício 7: Total de cada pedido
-- Calcule o valor total de cada pedido somando os subtotais dos itens.
-- Exiba código do pedido, data, nome do cliente e valor total.
-- =====================================================================
SELECT 
    p.id_pedido,
    p.data_pedido,
    c.nome AS nome_cliente,
    SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, p.data_pedido, c.nome
ORDER BY p.id_pedido;

-- =====================================================================
-- Exercício 8: Produtos vendidos para clientes de São Paulo
-- Liste os produtos vendidos para clientes com cidade 'São Paulo'.
-- Exiba cliente, cidade, produto e quantidade comprada.
-- =====================================================================
SELECT 
    c.nome AS cliente,
    c.cidade,
    pr.nome AS produto,
    ip.quantidade AS quantidade_comprada
FROM clientes c
INNER JOIN pedidos p ON c.id_cliente = p.id_cliente
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN produtos pr ON ip.id_produto = pr.id_produto
WHERE c.cidade = 'São Paulo'
ORDER BY c.nome, pr.nome;

-- =====================================================================
-- Exercício 9: Faturamento por vendedor
-- Mostre o nome do vendedor e seu faturamento total considerando todos
-- os itens atendidos. Vendedores sem venda devem exibir 0 via COALESCE.
-- =====================================================================
SELECT 
    v.nome AS nome_vendedor,
    COALESCE(SUM(ip.quantidade * ip.preco_unitario), 0.00) AS faturamento_total
FROM vendedores v
LEFT JOIN pedidos p ON v.id_vendedor = p.id_vendedor
LEFT JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY v.id_vendedor, v.nome
ORDER BY faturamento_total DESC, v.nome ASC;

-- =====================================================================
-- Exercício 10: Desafio – Relatório geral de vendas
-- Consulta consolidada contendo pedido, data, cliente, cidade, estado,
-- vendedor, produto, categoria, quantidade, preco_unitario e subtotal.
-- =====================================================================
SELECT 
    p.id_pedido AS pedido,
    p.data_pedido AS data,
    c.nome AS cliente,
    c.cidade,
    c.estado,
    v.nome AS vendedor,
    pr.nome AS produto,
    cat.nome AS categoria,
    ip.quantidade,
    ip.preco_unitario,
    (ip.quantidade * ip.preco_unitario) AS subtotal
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
INNER JOIN vendedores v ON p.id_vendedor = v.id_vendedor
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN produtos pr ON ip.id_produto = pr.id_produto
INNER JOIN categorias cat ON pr.id_categoria = cat.id_categoria
ORDER BY p.id_pedido ASC, pr.nome ASC;
