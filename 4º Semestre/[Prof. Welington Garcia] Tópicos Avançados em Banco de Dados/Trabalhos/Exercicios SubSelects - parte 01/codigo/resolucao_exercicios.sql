-- Exercicio 01
SELECT nome_produto, preco FROM produtos WHERE preco > (SELECT AVG(preco) FROM produtos);

-- Exercicio 02
SELECT nome_produto, preco FROM produtos WHERE preco = (SELECT MAX(preco) FROM produtos);

-- Exercicio 03
SELECT nome_produto, preco FROM produtos WHERE preco = (SELECT MIN(preco) FROM produtos);

-- Exercicio 04
SELECT nome_produto, preco, (SELECT AVG(preco) FROM produtos) AS media_geral FROM produtos;

-- Exercicio 05
SELECT nome_produto, preco, (SELECT AVG(preco) FROM produtos) AS media_geral, preco - (SELECT AVG(preco) FROM produtos) AS diferenca FROM produtos;

-- Exercicio 06
SELECT * FROM clientes WHERE id_cliente IN (SELECT id_cliente FROM pedidos);

-- Exercicio 07
SELECT * FROM produtos WHERE id_produto IN (SELECT id_produto FROM itens_pedido);

-- Exercicio 08
SELECT * FROM clientes WHERE id_cliente NOT IN (SELECT id_cliente FROM pedidos);

-- Exercicio 09
SELECT * FROM c WHERE NOT EXISTS (SELECT 1 FROM pedidos p WHERE p.id_cliente = c.id_cliente);

-- Exercicio 10
SELECT * FROM vendedores v WHERE EXISTS (SELECT 1 FROM pedidos p WHERE p.id_vendedor = v.id_vendedor);

-- Exercicio 11
SELECT * FROM produtos p WHERE NOT EXISTS (SELECT 1 FROM itens_pedido ip WHERE ip.id_produto = p.id_produto);

-- Exercicio 12
SELECT * FROM clientes WHERE id_cliente IN (SELECT p.id_cliente FROM pedidos p JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido JOIN produtos pr ON ip.id_produto = pr.id_produto WHERE pr.nome_produto = 'Notebook Dell');

-- Exercicio 13
SELECT nome_produto, preco FROM produtos WHERE preco > ANY (SELECT p.preco FROM produtos p JOIN categorias c ON p.id_categoria = c.id_categoria WHERE c.nome_categoria = 'Telefonia');

-- Exercicio 14
SELECT nome_produto, preco FROM produtos WHERE preco > ALL (SELECT p.preco FROM produtos p JOIN categorias c ON p.id_categoria = c.id_categoria WHERE c.nome_categoria = 'Acessorios');

-- Exercicio 15
SELECT * FROM vendedores WHERE salario > ANY (SELECT salario FROM vendedores);

-- Exercicio 16
SELECT * FROM vendedores WHERE salario >= ALL (SELECT salario FROM vendedores);

-- Exercicio 17
SELECT p1.nome_produto, p1.preco, p1.id_categoria FROM produtos p1 WHERE p1.preco > (SELECT AVG(p2.preco) FROM produtos p2 WHERE p2.id_categoria = p1.id_categoria);

-- Exercicio 18
SELECT p1.nome_produto, p1.estoque, p1.id_categoria FROM produtos p1 WHERE p1.estoque > (SELECT AVG(p2.estoque) FROM produtos p2 WHERE p2.id_categoria = p1.id_categoria);

-- Exercicio 19
SELECT c1.nome, c1.estado, c1.limite_credito FROM clientes c1 WHERE c1.limite_credito > (SELECT AVG(c2.limite_credito) FROM clientes c2 WHERE c2.estado = c1.estado);

-- Exercicio 20
SELECT p1.nome_produto, p1.preco, p1.id_categoria FROM produtos p1 WHERE p1.preco = (SELECT MAX(p2.preco) FROM produtos p2 WHERE p2.id_categoria = p1.id_categoria);
