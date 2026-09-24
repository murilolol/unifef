-- =============================================================================
-- Disciplina : Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor  : Prof. Welington Garcia
-- Tema       : Resolução de Exercícios - Joins, Subconsultas e Views
-- SGBD       : PostgreSQL 14+
-- Execução   : psql -U postgres -d loja_exercicios -f resolucao_exercicios.sql
-- =============================================================================

-- =============================================================================
-- Exercício 1: Relatório de Vendas e Hierarquia de Vendedores (Self-Join e Agrupamento)
-- =============================================================================
-- Objetivo: Ligar a tabela vendedores a si mesma para capturar o supervisor e
-- agregar o total de pedidos e montante líquido vendido (considerando desconto).
SELECT 
    v.id_vendedor,
    v.nome AS vendedor,
    COALESCE(sup.nome, 'Sem Supervisor') AS supervisor,
    COUNT(DISTINCT p.id_pedido) AS total_pedidos,
    COALESCE(ROUND(SUM(ip.quantidade * ip.preco_unitario * (1.0 - (ip.desconto / 100.0))), 2), 0.00) AS total_faturado_liquido
FROM vendedores v
LEFT JOIN vendedores sup ON v.id_supervisor = sup.id_vendedor
LEFT JOIN pedidos p ON v.id_vendedor = p.id_vendedor
LEFT JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY v.id_vendedor, v.nome, sup.nome
ORDER BY total_faturado_liquido DESC, v.nome ASC;


-- =============================================================================
-- Exercício 2: Produtos com Preço Superior à Média da Categoria (Subquery Correlacionada)
-- =============================================================================
-- Objetivo: Filtrar produtos cujo preço excede a média aritmética dos produtos
-- da mesma categoria, através de subconsulta correlacionada no WHERE.
SELECT 
    p.id_produto,
    p.nome_produto,
    c.nome_categoria,
    p.preco AS preco_produto,
    ROUND((
        SELECT AVG(p2.preco)
        FROM produtos p2
        WHERE p2.id_categoria = p.id_categoria
    ), 2) AS media_categoria,
    ROUND(p.preco - (
        SELECT AVG(p2.preco)
        FROM produtos p2
        WHERE p2.id_categoria = p.id_categoria
    ), 2) AS diferenca_acima_media
FROM produtos p
INNER JOIN categorias c ON p.id_categoria = c.id_categoria
WHERE p.preco > (
    SELECT AVG(p2.preco)
    FROM produtos p2
    WHERE p2.id_categoria = p.id_categoria
)
ORDER BY c.nome_categoria ASC, p.preco DESC;


-- =============================================================================
-- Exercício 3: Identificação de Clientes sem Compras Efetivas (LEFT JOIN vs NOT EXISTS)
-- =============================================================================
-- Solução A: Abordagem via LEFT JOIN com filtragem de pedidos válidos
SELECT 
    c.id_cliente,
    c.nome AS cliente,
    c.cidade,
    c.estado,
    c.limite_credito
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente 
                   AND p.status IN ('Pago', 'Enviado')
WHERE p.id_pedido IS NULL
ORDER BY c.id_cliente ASC;

-- Solução B: Abordagem equivalente e de alta performance via NOT EXISTS
SELECT 
    c.id_cliente,
    c.nome AS cliente,
    c.cidade,
    c.estado,
    c.limite_credito
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1
    FROM pedidos p
    WHERE p.id_cliente = c.id_cliente
      AND p.status IN ('Pago', 'Enviado')
)
ORDER BY c.id_cliente ASC;


-- =============================================================================
-- Exercício 4: Performance Financeira por Categoria com Filtro de Grupo (HAVING)
-- =============================================================================
-- Objetivo: Consolidar vendas por categoria de produto, avaliando volume físico,
-- faturamento bruto, descontos e faturamento líquido, filtrando via HAVING.
SELECT 
    c.id_categoria,
    c.nome_categoria,
    SUM(ip.quantidade) AS total_unidades_vendidas,
    ROUND(SUM(ip.quantidade * ip.preco_unitario), 2) AS faturamento_bruto,
    ROUND(SUM(ip.quantidade * ip.preco_unitario * (1.0 - (ip.desconto / 100.0))), 2) AS faturamento_liquido,
    ROUND(AVG(ip.desconto), 2) AS media_desconto_percentual
FROM categorias c
INNER JOIN produtos p ON c.id_categoria = p.id_categoria
INNER JOIN itens_pedido ip ON p.id_produto = ip.id_produto
INNER JOIN pedidos ped ON ip.id_pedido = ped.id_pedido
WHERE ped.status IN ('Pago', 'Enviado')
GROUP BY c.id_categoria, c.nome_categoria
HAVING SUM(ip.quantidade * ip.preco_unitario * (1.0 - (ip.desconto / 100.0))) > 5000.00
ORDER BY faturamento_liquido DESC;


-- =============================================================================
-- Exercício 5: Criação e Análise de Visão Gerencial de Comissões (VIEW)
-- =============================================================================
-- Definição da View consolidando faturamento e cálculo de comissões por mês/vendedor
CREATE OR REPLACE VIEW vw_faturamento_vendedores AS
SELECT 
    TO_CHAR(p.data_pedido, 'YYYY-MM') AS ano_mes,
    v.id_vendedor,
    v.nome AS vendedor,
    v.comissao AS taxa_comissao,
    ROUND(SUM(ip.quantidade * ip.preco_unitario * (1.0 - (ip.desconto / 100.0))), 2) AS total_liquido_vendido,
    ROUND(SUM(ip.quantidade * ip.preco_unitario * (1.0 - (ip.desconto / 100.0))) * (v.comissao / 100.0), 2) AS valor_comissao
FROM vendedores v
INNER JOIN pedidos p ON v.id_vendedor = p.id_vendedor
INNER JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
WHERE p.status IN ('Pago', 'Enviado')
GROUP BY TO_CHAR(p.data_pedido, 'YYYY-MM'), v.id_vendedor, v.nome, v.comissao;

-- Consulta de Teste e Validação da View criada para o 1º Trimestre de 2026
SELECT 
    ano_mes,
    id_vendedor,
    vendedor,
    total_liquido_vendido,
    taxa_comissao,
    valor_comissao
FROM vw_faturamento_vendedores
WHERE ano_mes BETWEEN '2026-01' AND '2026-03'
ORDER BY ano_mes ASC, valor_comissao DESC;
