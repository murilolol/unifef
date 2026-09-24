-- Disciplina: Topicos Avancados em Banco de Dados (4 Semestre)
-- Professor: Prof. Welington Garcia
-- Script de resolucao dos exercicios de fixacao sobre Joins e Subselects.

-- Exercicio 1: Exibicao de Pedidos
SELECT
  p.id_pedido,
  p.data_pedido,
  p.status,
  c.nome AS cliente
FROM pedidos p
INNER JOIN clientes c
  ON p.id_cliente = c.id_cliente;

-- Exercicio 2: Clientes e Pedidos (Left)
SELECT
  c.id_cliente,
  c.nome,
  p.id_pedido,
  p.status
FROM clientes c
LEFT JOIN pedidos p
  ON c.id_cliente = p.id_cliente;

-- Exercicio 3: Clientes sem Pedidos
SELECT
  c.id_cliente,
  c.nome
FROM clientes c
LEFT JOIN pedidos p
  ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL;

-- Exercicio 4: Produtos e Categorias
SELECT
  pr.id_produto,
  pr.nome_produto,
  COALESCE(ca.nome_categoria, 'Sem categoria') AS categoria
FROM produtos pr
LEFT JOIN categorias ca
  ON pr.id_categoria = ca.id_categoria;

-- Exercicio 5: Subtotal de Itens
SELECT
  ip.id_pedido,
  pr.nome_produto,
  ip.quantidade,
  ip.preco_unitario,
  (ip.quantidade * ip.preco_unitario) AS subtotal
FROM itens_pedido ip
INNER JOIN produtos pr
  ON ip.id_produto = pr.id_produto;

-- Exercicio 6: Contagem de Pedidos por Cliente
SELECT
  c.id_cliente,
  c.nome,
  COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p
  ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome
ORDER BY quantidade_pedidos DESC;

-- Exercicio 7: Valor Total do Pedido
SELECT
  p.id_pedido,
  c.nome AS cliente,
  SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
INNER JOIN clientes c
  ON p.id_cliente = c.id_cliente
INNER JOIN itens_pedido ip
  ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, c.nome;

-- Exercicio 8: Funcionario e Supervisor
SELECT
  f.nome AS funcionario,
  f.cargo,
  COALESCE(s.nome, 'Sem supervisor') AS supervisor
FROM funcionarios f
LEFT JOIN funcionarios s
  ON f.id_supervisor = s.id_funcionario;

-- Exercicio 9: Produtos acima da Media
SELECT
  id_produto,
  nome_produto,
  preco
FROM produtos
WHERE preco > (
  SELECT AVG(preco)
  FROM produtos
);

-- Exercicio 10: Menor Preco
SELECT
  id_produto,
  nome_produto,
  preco
FROM produtos
WHERE preco = (
  SELECT MIN(preco)
  FROM produtos
);
