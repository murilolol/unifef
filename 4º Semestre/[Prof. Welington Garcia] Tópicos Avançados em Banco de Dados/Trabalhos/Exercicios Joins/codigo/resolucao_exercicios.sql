-- =========================================================
-- RESOLUÇÃO DOS EXERCÍCIOS DE JOINS
-- Disciplina: Tópicos Avançados em Banco de Dados
-- Professor: Welington Garcia
-- Instituição: UniFEF
-- PostgreSQL
-- =========================================================

-- Exercício 1: Cliente e seus pedidos
-- Utilizando INNER JOIN, apresente o código do pedido, a data do pedido e o nome do cliente.
-- Ordene o resultado pelo código do pedido.
SELECT 
    p.id_pedido,
    p.data_pedido,
    c.nome AS nome_cliente
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
ORDER BY p.id_pedido;

-- Exercício 2: Relatório completo dos itens vendidos
-- Apresente o código do pedido, o nome do cliente, o nome do produto, a quantidade e o preço unitário.
SELECT 
    p.id_pedido,
    c.nome AS nome_cliente,
    pr.nome AS nome_produto,
    ip.quantidade,
    ip.preco_unitario
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN produtos pr ON ip.id_produto = pr.id_produto;

-- Exercício 3: Clientes sem pedidos
-- Utilizando LEFT JOIN, liste somente os clientes que nunca realizaram pedidos.
SELECT 
    c.id_cliente,
    c.nome
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL;

-- Exercício 4: Produtos nunca vendidos
-- Utilizando LEFT JOIN, liste os produtos que ainda não aparecem em nenhum item de pedido.
SELECT 
    pr.id_produto,
    pr.nome,
    pr.preco
FROM produtos pr
LEFT JOIN itens_pedido ip ON pr.id_produto = ip.id_produto
WHERE ip.id_item IS NULL;

-- Exercício 5: Quantidade de pedidos por cliente
-- Mostre o nome de cada cliente e a quantidade de pedidos realizados (incluindo zero pedidos).
SELECT 
    c.nome,
    COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome;

-- Exercício 6: Faturamento por produto
-- Calcule quanto cada produto gerou em vendas (quantidade * preco_unitario).
SELECT 
    pr.nome AS produto,
    SUM(ip.quantidade * ip.preco_unitario) AS faturamento_total
FROM produtos pr
INNER JOIN itens_pedido ip ON pr.id_produto = ip.id_produto
GROUP BY pr.id_produto, pr.nome
ORDER BY faturamento_total DESC;

-- Exercício 7: Total de cada pedido
-- Calcule o valor total de cada pedido somando os subtotais de seus itens.
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

-- Exercício 8: Produtos vendidos para clientes de São Paulo
-- Liste os produtos vendidos para clientes cuja cidade seja São Paulo.
SELECT 
    c.nome AS cliente,
    c.cidade,
    pr.nome AS produto,
    ip.quantidade
FROM clientes c
INNER JOIN pedidos p ON c.id_cliente = p.id_cliente
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN produtos pr ON ip.id_produto = pr.id_produto
WHERE c.cidade = 'São Paulo';

-- Exercício 9: Faturamento por vendedor
-- Mostre o nome do vendedor e seu faturamento total (vendedores sem vendas com 0).
SELECT 
    v.nome AS vendedor,
    COALESCE(SUM(ip.quantidade * ip.preco_unitario), 0) AS faturamento_total
FROM vendedores v
LEFT JOIN pedidos p ON v.id_vendedor = p.id_vendedor
LEFT JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY v.id_vendedor, v.nome;

-- Exercício 10: Desafio – relatório geral de vendas
-- Monte uma consulta que apresente todos os detalhes e o subtotal calculado.
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
ORDER BY p.id_pedido, pr.nome;
