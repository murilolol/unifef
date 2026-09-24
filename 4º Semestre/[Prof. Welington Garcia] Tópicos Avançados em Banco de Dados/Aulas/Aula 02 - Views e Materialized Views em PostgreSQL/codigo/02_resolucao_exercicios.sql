-- ==========================================================================
-- Disciplina: Tópicos Avançados em Banco de Dados
-- Tema: Resolução dos Exercícios de Fixação (Partes 1, 2 e 3)
-- Professor: Prof. Welington Garcia
-- Como executar: Certifique-se de executar o script '01_estrutura_e_exemplos.sql' antes.
-- ==========================================================================

-- ========================================== 
-- PARTE 1
-- ========================================== 

-- Exercício 1: Crie uma view contendo apenas clientes do estado de SP.
CREATE OR REPLACE VIEW vw_clientes_sp_fixacao AS
SELECT id_cliente, nome, cidade, estado
FROM clientes
WHERE estado = 'SP';

-- Exercício 2: Crie uma view que exiba produtos com preço superior a R$ 1.000,00.
CREATE OR REPLACE VIEW vw_produtos_caros_fixacao AS
SELECT id_produto, nome_produto, preco, id_categoria
FROM produtos
WHERE preco > 1000.00;

-- Exercício 3: Crie uma view com pedidos e nomes dos respectivos clientes.
CREATE OR REPLACE VIEW vw_pedidos_com_clientes AS
SELECT p.id_pedido, p.data_pedido, p.status, c.nome AS nome_cliente
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente;

-- Exercício 4: Consulte a view do exercício 3 exibindo somente pedidos pagos.
SELECT *
FROM vw_pedidos_com_clientes
WHERE status = 'Pago';

-- ========================================== 
-- PARTE 2
-- ========================================== 

-- Exercício 5: Crie uma view que calcule o valor total de cada pedido.
CREATE OR REPLACE VIEW vw_valor_total_pedidos AS
SELECT id_pedido, SUM(quantidade * preco_unitario) AS valor_total
FROM itens_pedido
GROUP BY id_pedido;

-- Exercício 6: Crie uma view que mostre cada cliente e sua quantidade de pedidos.
CREATE OR REPLACE VIEW vw_clientes_qtd_pedidos AS
SELECT c.id_cliente, c.nome, COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome;

-- Exercício 7: Modifique uma view existente utilizando CREATE OR REPLACE VIEW.
CREATE OR REPLACE VIEW vw_clientes_sp_fixacao AS
SELECT id_cliente, nome, cidade, estado, limite_credito
FROM clientes
WHERE estado = 'SP';

-- Exercício 8: Crie uma view atualizável de clientes de SP utilizando WITH CHECK OPTION.
CREATE OR REPLACE VIEW vw_clientes_sp_atualizavel AS
SELECT id_cliente, nome, cidade, estado
FROM clientes
WHERE estado = 'SP'
WITH CHECK OPTION;

-- ========================================== 
-- PARTE 3
-- ========================================== 

-- Exercício 9: Crie uma view que exponha somente id, nome, cidade e estado dos clientes.
CREATE OR REPLACE VIEW vw_clientes_seguro AS
SELECT id_cliente, nome, cidade, estado
FROM clientes;

-- Exercício 10: Crie uma materialized view com o total de vendas por cliente e depois execute seu REFRESH.
CREATE MATERIALIZED VIEW mv_total_vendas_por_cliente AS
SELECT c.id_cliente, c.nome, COALESCE(SUM(ip.quantidade * ip.preco_unitario), 0) AS total_comprado
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
LEFT JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY c.id_cliente, c.nome;

-- Executando a atualização da Materialized View
REFRESH MATERIALIZED VIEW mv_total_vendas_por_cliente;
