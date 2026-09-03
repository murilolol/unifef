-- =====================================================================
-- TRABALHO: EXERCÍCIOS DE JOINs NO POSTGRESQL
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR: Prof. Welington Garcia
-- ALUNO / DESENVOLVEDOR SÊNIOR: Sistema Acadêmico de Sistemas de Informação
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0. CRIAÇÃO E CONFIGURAÇÃO DO BANCO DE DADOS DE APOIO
-- ---------------------------------------------------------------------
-- Nota: O comando CREATE DATABASE não pode ser executado dentro de uma 
-- transação ou bloco do PL/pgSQL se já estiver conectado ao banco. 
-- Certifique-se de executar o comando abaixo conectado ao banco 'postgres'.
-- ---------------------------------------------------------------------

-- DROP DATABASE IF EXISTS loja_joins;
-- CREATE DATABASE loja_joins;

-- \c loja_joins;

-- (Caso esteja executando via script único e já esteja conectado ao banco 'loja_joins', 
-- ignore o CREATE DATABASE e utilize as instruções DDL abaixo).

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2)
);

CREATE TABLE IF NOT EXISTS vendedores (
    id_vendedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    id_categoria INTEGER,
    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias(id_categoria)
);

CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    id_cliente INTEGER,
    id_vendedor INTEGER,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente),
    CONSTRAINT fk_pedido_vendedor
        FOREIGN KEY (id_vendedor)
        REFERENCES vendedores(id_vendedor)
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id_item SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL,
    id_produto INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id_pedido),
    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto)
        REFERENCES produtos(id_produto)
);

-- Limpeza prévia para garantir idempotência em reexecuções
TRUNCATE TABLE itens_pedido, pedidos, produtos, categorias, vendedores, clientes RESTART IDENTITY CASCADE;

-- Inserção de Dados
INSERT INTO clientes (nome, cidade, estado) VALUES
('Ana Silva', 'São Paulo', 'SP'),
('Bruno Souza', 'Campinas', 'SP'),
('Carla Mendes', 'Belo Horizonte', 'MG'),
('Daniel Oliveira', 'Rio de Janeiro', 'RJ'),
('Eduardo Santos', 'Curitiba', 'PR'),
('Fernanda Lima', 'Florianópolis', 'SC'),
('Gabriel Costa', 'São Paulo', 'SP'),
('Helena Rocha', 'Salvador', 'BA'),
('Igor Martins', 'Vitória', 'ES'),
('Juliana Alves', 'Goiânia', 'GO');

INSERT INTO vendedores (nome, salario) VALUES
('Carlos Ferreira', 3500.00),
('Mariana Lopes', 4200.00),
('Pedro Almeida', 3800.00),
('Renata Gomes', 4500.00),
('Lucas Ribeiro', 3200.00);

INSERT INTO categorias (nome) VALUES
('Informática'),
('Periféricos'),
('Escritório'),
('Eletrônicos'),
('Acessórios'),
('Games');

INSERT INTO produtos (nome, preco, id_categoria) VALUES
('Notebook Dell', 4500.00, 1),
('Notebook Lenovo', 3800.00, 1),
('Monitor 24 polegadas', 899.90, 1),
('Teclado Mecânico', 350.00, 2),
('Mouse Gamer', 180.00, 2),
('Headset USB', 290.00, 2),
('Cadeira Escritório', 1200.00, 3),
('Mesa Escritório', 950.00, 3),
('Smartphone Samsung', 2200.00, 4),
('Smart TV 50', 2800.00, 4),
('Cabo HDMI', 45.00, 5),
('Suporte Notebook', 120.00, 5),
('Webcam Full HD', 230.00, 2),
('Impressora Laser', 1600.00, 3);

INSERT INTO pedidos (data_pedido, id_cliente, id_vendedor) VALUES
('2026-08-01', 1, 1),
('2026-08-01', 2, 2),
('2026-08-02', 3, 1),
('2026-08-03', 1, 3),
('2026-08-03', 4, 2),
('2026-08-04', 5, 4),
('2026-08-05', 6, 1),
('2026-08-05', 2, 3),
('2026-08-06', 7, 4),
('2026-08-07', 3, 2),
('2026-08-08', 8, 1),
('2026-08-09', 1, 4);

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(1, 1, 1, 4500.00),
(1, 5, 1, 180.00),
(2, 4, 1, 350.00),
(2, 5, 2, 180.00),
(3, 7, 1, 1200.00),
(3, 8, 1, 950.00),
(4, 3, 2, 899.90),
(4, 6, 1, 290.00),
(5, 9, 1, 2200.00),
(5, 11, 2, 45.00),
(6, 10, 1, 2800.00),
(7, 2, 1, 3800.00),
(7, 12, 1, 120.00),
(8, 5, 1, 180.00),
(8, 13, 1, 230.00),
(9, 1, 1, 4500.00),
(9, 4, 1, 350.00),
(10, 14, 1, 1600.00),
(11, 6, 2, 290.00),
(12, 9, 1, 2200.00),
(12, 13, 1, 230.00);


-- =====================================================================
-- RESOLUÇÃO DOS 10 EXERCÍCIOS DA LISTA DE JOINs
-- =====================================================================

-- ---------------------------------------------------------------------
-- Exercício 1: Cliente e seus pedidos
-- Utilizando INNER JOIN, apresente o código do pedido, a data do pedido 
-- e o nome do cliente. Ordene o resultado pelo código do pedido.
-- ---------------------------------------------------------------------
SELECT 
    p.id_pedido, 
    p.data_pedido, 
    c.nome AS nome_cliente
FROM 
    pedidos p
INNER JOIN 
    clientes c ON p.id_cliente = c.id_cliente
ORDER BY 
    p.id_pedido;


-- ---------------------------------------------------------------------
-- Exercício 2: Relatório completo dos itens vendidos
-- Apresente o código do pedido, o nome do cliente, o nome do produto, 
-- a quantidade e o preço unitário.
-- ---------------------------------------------------------------------
SELECT 
    p.id_pedido, 
    c.nome AS nome_cliente, 
    pr.nome AS nome_produto, 
    ip.quantidade, 
    ip.preco_unitario
FROM 
    pedidos p
INNER JOIN 
    clientes c ON p.id_cliente = c.id_cliente
INNER JOIN 
    itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN 
    produtos pr ON ip.id_produto = pr.id_produto
ORDER BY 
    p.id_pedido, pr.nome;


-- ---------------------------------------------------------------------
-- Exercício 3: Clientes sem pedidos
-- Utilizando LEFT JOIN, liste somente os clientes que nunca realizaram pedidos.
-- Exiba o código e o nome do cliente.
-- ---------------------------------------------------------------------
SELECT 
    c.id_cliente, 
    c.nome AS nome_cliente
FROM 
    clientes c
LEFT JOIN 
    pedidos p ON c.id_cliente = p.id_cliente
WHERE 
    p.id_pedido IS NULL;


-- ---------------------------------------------------------------------
-- Exercício 4: Produtos nunca vendidos
-- Utilizando LEFT JOIN, liste os produtos que ainda não aparecem em nenhum item de pedido.
-- Exiba o código do produto, nome e preço.
-- ---------------------------------------------------------------------
SELECT 
    pr.id_produto, 
    pr.nome AS nome_produto, 
    pr.preco
FROM 
    produtos pr
LEFT JOIN 
    itens_pedido ip ON pr.id_produto = ip.id_produto
WHERE 
    ip.id_item IS NULL;


-- ---------------------------------------------------------------------
-- Exercício 5: Quantidade de pedidos por cliente
-- Mostre o nome de cada cliente e a quantidade de pedidos realizados.
-- Todos os clientes devem aparecer, inclusive os que possuem zero pedidos.
-- Utilize LEFT JOIN, COUNT() e GROUP BY.
-- ---------------------------------------------------------------------
SELECT 
    c.nome AS nome_cliente, 
    COUNT(p.id_pedido) AS quantidade_pedidos
FROM 
    clientes c
LEFT JOIN 
    pedidos p ON c.id_cliente = p.id_cliente
GROUP BY 
    c.id_cliente, c.nome
ORDER BY 
    quantidade_pedidos DESC, c.nome;


-- ---------------------------------------------------------------------
-- Exercício 6: Faturamento por produto
-- Calcule quanto cada produto gerou em vendas.
-- O valor de cada item é quantidade × preco_unitario.
-- Exiba produto e faturamento total, ordenando do maior para o menor.
-- ---------------------------------------------------------------------
SELECT 
    pr.nome AS nome_produto, 
    SUM(ip.quantidade * ip.preco_unitario) AS faturamento_total
FROM 
    produtos pr
INNER JOIN 
    itens_pedido ip ON pr.id_produto = ip.id_produto
GROUP BY 
    pr.id_produto, pr.nome
ORDER BY 
    faturamento_total DESC;


-- ---------------------------------------------------------------------
-- Exercício 7: Total de cada pedido
-- Calcule o valor total de cada pedido somando os subtotais de seus itens.
-- Exiba código do pedido, data, nome do cliente e valor total.
-- ---------------------------------------------------------------------
SELECT 
    p.id_pedido, 
    p.data_pedido, 
    c.nome AS nome_cliente, 
    SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM 
    pedidos p
INNER JOIN 
    clientes c ON p.id_cliente = c.id_cliente
INNER JOIN 
    itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY 
    p.id_pedido, p.data_pedido, c.nome
ORDER BY 
    p.id_pedido;


-- ---------------------------------------------------------------------
-- Exercício 8: Produtos vendidos para clientes de São Paulo
-- Liste os produtos vendidos para clientes cuja cidade seja São Paulo.
-- Exiba cliente, cidade, produto e quantidade comprada.
-- ---------------------------------------------------------------------
SELECT 
    c.nome AS nome_cliente, 
    c.cidade, 
    pr.nome AS nome_produto, 
    ip.quantidade
FROM 
    clientes c
INNER JOIN 
    pedidos p ON c.id_cliente = p.id_cliente
INNER JOIN 
    itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN 
    produtos pr ON ip.id_produto = pr.id_produto
WHERE 
    c.cidade = 'São Paulo'
ORDER BY 
    c.nome, pr.nome;


-- ---------------------------------------------------------------------
-- Exercício 9: Faturamento por vendedor
-- Mostre o nome do vendedor e seu faturamento total.
-- Todos os vendedores devem aparecer; quando não houver venda, o faturamento deve ser 0.
-- ---------------------------------------------------------------------
SELECT 
    v.nome AS nome_vendedor, 
    COALESCE(SUM(ip.quantidade * ip.preco_unitario), 0.00) AS faturamento_total
FROM 
    vendedores v
LEFT JOIN 
    pedidos p ON v.id_vendedor = p.id_vendedor
LEFT JOIN 
    itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY 
    v.id_vendedor, v.nome
ORDER BY 
    faturamento_total DESC;


-- ---------------------------------------------------------------------
-- Exercício 10: Desafio – relatório geral de vendas
-- Monte uma consulta que apresente: pedido, data, cliente, cidade, estado, 
-- vendedor, produto, categoria, quantidade, preço unitário e subtotal.
-- Ordene por código do pedido e nome do produto.
-- ---------------------------------------------------------------------
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
FROM 
    pedidos p
INNER JOIN 
    clientes c ON p.id_cliente = c.id_cliente
INNER JOIN 
    vendedores v ON p.id_vendedor = v.id_vendedor
INNER JOIN 
    itens_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN 
    produtos pr ON ip.id_produto = pr.id_produto
INNER JOIN 
    categorias cat ON pr.id_categoria = cat.id_categoria
ORDER BY 
    p.id_pedido, 
    pr.nome;