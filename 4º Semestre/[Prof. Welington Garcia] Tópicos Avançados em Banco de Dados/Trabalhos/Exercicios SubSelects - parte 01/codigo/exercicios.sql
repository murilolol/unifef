-- =========================================================
-- UniFEF - Centro Universitario de Santa Fe do Sul
-- Curso: Sistemas de Informacao (4o Semestre)
-- Disciplina: Topicos Avancados em Banco de Dados
-- Docente: Prof. Welington Garcia
-- Tema: Resolucao da Lista de Exercicios de Subconsultas (1 a 20)
-- SGBD: PostgreSQL 14+
-- Execucao:
--   psql -U postgres -d seu_banco -f exercicios.sql
-- =========================================================

-- ---------------------------------------------------------
-- Exercicio 1: Subconsulta escalar no WHERE
-- Enunciado: Liste todos os produtos cujo preco seja maior que a media de precos de todos os produtos.
-- ---------------------------------------------------------
SELECT 
    id_produto,
    nome_produto,
    preco
FROM produtos
WHERE preco > (SELECT AVG(preco) FROM produtos)
ORDER BY preco DESC;

-- ---------------------------------------------------------
-- Exercicio 2: Subconsulta escalar com MAX()
-- Enunciado: Liste o produto ou os produtos que possuem o maior preco cadastrado.
-- ---------------------------------------------------------
SELECT 
    id_produto,
    nome_produto,
    preco
FROM produtos
WHERE preco = (SELECT MAX(preco) FROM produtos);

-- ---------------------------------------------------------
-- Exercicio 3: Subconsulta escalar com MIN()
-- Enunciado: Liste o produto ou os produtos que possuem o menor preco cadastrado.
-- ---------------------------------------------------------
SELECT 
    id_produto,
    nome_produto,
    preco
FROM produtos
WHERE preco = (SELECT MIN(preco) FROM produtos);

-- ---------------------------------------------------------
-- Exercicio 4: Subconsulta no SELECT (coluna derivada)
-- Enunciado: Exiba nome e preco de cada produto e, em uma terceira coluna, apresente a media geral de precos.
-- ---------------------------------------------------------
SELECT 
    nome_produto,
    preco,
    ROUND((SELECT AVG(preco) FROM produtos), 2) AS media_geral_precos
FROM produtos
ORDER BY nome_produto;

-- ---------------------------------------------------------
-- Exercicio 5: Subconsulta no SELECT com operacao aritmetica
-- Enunciado: Exiba nome, preco, media geral e a diferenca entre o preco do produto e a media geral.
-- ---------------------------------------------------------
SELECT 
    nome_produto,
    preco,
    ROUND((SELECT AVG(preco) FROM produtos), 2) AS media_geral,
    ROUND(preco - (SELECT AVG(preco) FROM produtos), 2) AS diferenca_media
FROM produtos
ORDER BY diferenca_media DESC;

-- ---------------------------------------------------------
-- Exercicio 6: Operador IN
-- Enunciado: Liste os clientes que realizaram pelo menos um pedido.
-- ---------------------------------------------------------
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

-- ---------------------------------------------------------
-- Exercicio 7: Operador IN com tabela itens_pedido
-- Enunciado: Liste os produtos que ja apareceram em algum item de pedido.
-- ---------------------------------------------------------
SELECT 
    id_produto,
    nome_produto,
    preco
FROM produtos
WHERE id_produto IN (
    SELECT id_produto 
    FROM itens_pedido
)
ORDER BY id_produto;

-- ---------------------------------------------------------
-- Exercicio 8: Operador NOT IN
-- Enunciado: Liste os clientes que nao aparecem em nenhum pedido utilizando NOT IN.
-- ---------------------------------------------------------
SELECT 
    id_cliente,
    nome,
    cidade,
    estado
FROM clientes
WHERE id_cliente NOT IN (
    SELECT id_cliente 
    FROM pedidos
)
ORDER BY id_cliente;

-- ---------------------------------------------------------
-- Exercicio 9: Reescrita com NOT EXISTS
-- Enunciado: Reescreva o exercicio anterior utilizando NOT EXISTS correlacionando pedidos com clientes.
-- ---------------------------------------------------------
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
)
ORDER BY c.id_cliente;

-- ---------------------------------------------------------
-- Exercicio 10: Operador EXISTS com correlacao
-- Enunciado: Liste os vendedores que possuem pelo menos um pedido registrado.
-- ---------------------------------------------------------
SELECT 
    v.id_vendedor,
    v.nome,
    v.salario
FROM vendedores v
WHERE EXISTS (
    SELECT 1 
    FROM pedidos p
    WHERE p.id_vendedor = v.id_vendedor
)
ORDER BY v.id_vendedor;

-- ---------------------------------------------------------
-- Exercicio 11: Operador NOT EXISTS
-- Enunciado: Liste os produtos que nunca foram vendidos.
-- ---------------------------------------------------------
SELECT 
    p.id_produto,
    p.nome_produto,
    p.preco
FROM produtos p
WHERE NOT EXISTS (
    SELECT 1 
    FROM itens_pedido ip
    WHERE ip.id_produto = p.id_produto
)
ORDER BY p.id_produto;

-- ---------------------------------------------------------
-- Exercicio 12: Subconsultas aninhadas com IN
-- Enunciado: Liste os clientes que compraram o produto 'Notebook Dell'.
-- ---------------------------------------------------------
SELECT 
    id_cliente,
    nome,
    cidade,
    estado
FROM clientes
WHERE id_cliente IN (
    SELECT p.id_cliente
    FROM pedidos p
    WHERE p.id_pedido IN (
        SELECT ip.id_pedido
        FROM itens_pedido ip
        WHERE ip.id_produto = (
            SELECT prod.id_produto
            FROM produtos prod
            WHERE prod.nome_produto = 'Notebook Dell'
        )
    )
)
ORDER BY id_cliente;

-- ---------------------------------------------------------
-- Exercicio 13: Operador ANY (> ANY)
-- Enunciado: Liste os produtos cujo preco seja maior que o preco de pelo menos um produto da categoria Telefonia.
-- ---------------------------------------------------------
SELECT 
    nome_produto,
    preco
FROM produtos
WHERE preco > ANY (
    SELECT p.preco
    FROM produtos p
    WHERE p.id_categoria = (
        SELECT c.id_categoria 
        FROM categorias c 
        WHERE c.nome_categoria = 'Telefonia'
    )
)
ORDER BY preco;

-- ---------------------------------------------------------
-- Exercicio 14: Operador ALL (> ALL)
-- Enunciado: Liste os produtos cujo preco seja maior que o preco de todos os produtos da categoria Acessorios.
-- ---------------------------------------------------------
SELECT 
    nome_produto,
    preco
FROM produtos
WHERE preco > ALL (
    SELECT p.preco
    FROM produtos p
    WHERE p.id_categoria = (
        SELECT c.id_categoria 
        FROM categorias c 
        WHERE c.nome_categoria = 'Acessorios'
    )
)
ORDER BY preco;

-- ---------------------------------------------------------
-- Exercicio 15: Operador ANY com auto-comparacao
-- Enunciado: Liste os vendedores cujo salario seja maior que pelo menos um salario existente entre os demais vendedores.
-- ---------------------------------------------------------
SELECT 
    id_vendedor,
    nome,
    salario
FROM vendedores
WHERE salario > ANY (
    SELECT salario 
    FROM vendedores
)
ORDER BY salario;

-- ---------------------------------------------------------
-- Exercicio 16: Operador ALL (>= ALL)
-- Enunciado: Liste o vendedor ou os vendedores cujo salario seja maior ou igual a todos os salarios cadastrados.
-- ---------------------------------------------------------
SELECT 
    id_vendedor,
    nome,
    salario
FROM vendedores
WHERE salario >= ALL (
    SELECT salario 
    FROM vendedores
);

-- ---------------------------------------------------------
-- Exercicio 17: Subconsulta correlacionada (preco vs media da categoria)
-- Enunciado: Liste os produtos cujo preco seja maior que a media de precos de sua propria categoria.
-- ---------------------------------------------------------
SELECT 
    p1.id_produto,
    p1.nome_produto,
    p1.preco,
    p1.id_categoria
FROM produtos p1
WHERE p1.preco > (
    SELECT AVG(p2.preco)
    FROM produtos p2
    WHERE p2.id_categoria = p1.id_categoria
)
ORDER BY p1.id_categoria, p1.preco DESC;

-- ---------------------------------------------------------
-- Exercicio 18: Subconsulta correlacionada (estoque vs media da categoria)
-- Enunciado: Liste os produtos cujo estoque seja maior que a media de estoque de sua propria categoria.
-- ---------------------------------------------------------
SELECT 
    p1.id_produto,
    p1.nome_produto,
    p1.estoque,
    p1.id_categoria
FROM produtos p1
WHERE p1.estoque > (
    SELECT AVG(p2.estoque)
    FROM produtos p2
    WHERE p2.id_categoria = p1.id_categoria
)
ORDER BY p1.id_categoria, p1.estoque DESC;

-- ---------------------------------------------------------
-- Exercicio 19: Subconsulta correlacionada (credito vs media do estado)
-- Enunciado: Liste os clientes cujo limite de credito seja maior que a media de limite de credito dos clientes do mesmo estado.
-- ---------------------------------------------------------
SELECT 
    c1.id_cliente,
    c1.nome,
    c1.estado,
    c1.limite_credito
FROM clientes c1
WHERE c1.limite_credito > (
    SELECT AVG(c2.limite_credito)
    FROM clientes c2
    WHERE c2.estado = c1.estado
)
ORDER BY c1.estado, c1.limite_credito DESC;

-- ---------------------------------------------------------
-- Exercicio 20: Subconsulta correlacionada para identificar maximo por grupo
-- Enunciado: Liste, para cada categoria, o produto ou os produtos de maior preco daquela categoria.
-- ---------------------------------------------------------
SELECT 
    p1.id_categoria,
    p1.id_produto,
    p1.nome_produto,
    p1.preco
FROM produtos p1
WHERE p1.preco = (
    SELECT MAX(p2.preco)
    FROM produtos p2
    WHERE p2.id_categoria = p1.id_categoria
)
ORDER BY p1.id_categoria;
