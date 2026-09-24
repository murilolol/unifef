-- =========================================================
-- CONSULTAS EXEMPLO - TÓPICOS AVANÇADOS EM BANCO DE DADOS
-- =========================================================

-- Exercício 1: Listar pedidos com clientes e vendedores
SELECT 
    p.id_pedido,
    p.data_pedido,
    p.status,
    c.nome AS cliente,
    v.nome AS vendedor
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN vendedores v ON p.id_vendedor = v.id_vendedor;

-- Exercício 2: Faturamento total por categoria
SELECT 
    cat.nome_categoria,
    SUM((ip.quantidade * ip.preco_unitario) - ip.desconto) AS faturamento_total
FROM itens_pedido ip
JOIN produtos pr ON ip.id_produto = pr.id_produto
JOIN categorias cat ON pr.id_categoria = cat.id_categoria
GROUP BY cat.nome_categoria;

-- Exercício 3: Clientes com limite acima da média
SELECT 
    nome,
    limite_credito
FROM clientes
WHERE limite_credito > (SELECT AVG(limite_credito) FROM clientes);
