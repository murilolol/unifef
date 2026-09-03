FILENAME: exercicios_subselects_parte01.sql
---CODE_START---
-- =========================================================================
-- FACULDADE DE SISTEMAS DE INFORMAÇÃO
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR: Prof. Welington Garcia
-- TÍTULO: Exercicios SubSelects - parte 01
-- =========================================================================

-- =========================================================================
-- 1. REMOÇÃO DAS TABELAS CASO JÁ EXISTAM
-- =========================================================================
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS vendedores CASCADE;

-- =========================================================================
-- 2. CRIAÇÃO DAS TABELAS
-- =========================================================================
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2),
    limite_credito NUMERIC(10,2)
);

CREATE TABLE vendedores (
    id_vendedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario NUMERIC(10,2) NOT NULL,
    comissao NUMERIC(5,2)
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    estoque INTEGER NOT NULL,
    id_categoria INTEGER,
    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias(id_categoria)
);

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    id_cliente INTEGER NOT NULL,
    id_vendedor INTEGER NOT NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente),
    CONSTRAINT fk_pedido_vendedor
        FOREIGN KEY (id_vendedor)
        REFERENCES vendedores(id_vendedor)
);

CREATE TABLE itens_pedido (
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

-- =========================================================================
-- 3. INSERTS - CLIENTES
-- =========================================================================
INSERT INTO clientes (nome, cidade, estado, limite_credito) VALUES
('Ana Souza', 'Sao Paulo', 'SP', 5000.00),
('Bruno Lima', 'Campinas', 'SP', 3000.00),
('Carla Mendes', 'Curitiba', 'PR', 7000.00),
('Daniel Rocha', 'Londrina', 'PR', 2500.00),
('Eduarda Alves', 'Belo Horizonte', 'MG', 10000.00),
('Felipe Martins', 'Sao Jose do Rio Preto', 'SP', 4500.00),
('Gabriela Costa', 'Florianopolis', 'SC', 8000.00),
('Henrique Silva', 'Goiania', 'GO', 2000.00),
('Isabela Fernandes', 'Sao Paulo', 'SP', 6000.00),
('Joao Pereira', 'Curitiba', 'PR', 3500.00);

-- =========================================================================
-- 4. INSERTS - VENDEDORES
-- =========================================================================
INSERT INTO vendedores (nome, salario, comissao) VALUES
('Carlos Almeida', 3500.00, 5.00),
('Fernanda Souza', 4200.00, 6.00),
('Ricardo Lima', 3000.00, 4.00),
('Juliana Martins', 5000.00, 7.00),
('Paulo Costa', 2800.00, 3.50);

-- =========================================================================
-- 5. INSERTS - CATEGORIAS
-- =========================================================================
INSERT INTO categorias (nome_categoria) VALUES
('Informatica'),
('Telefonia'),
('Escritorio'),
('Acessorios'),
('Games'),
('Eletronicos');

-- =========================================================================
-- 6. INSERTS - PRODUTOS
-- =========================================================================
INSERT INTO produtos (nome_produto, preco, estoque, id_categoria) VALUES
('Notebook Dell', 4500.00, 10, 1),
('Notebook Lenovo', 3800.00, 8, 1),
('Mouse Logitech', 150.00, 50, 4),
('Teclado Mecanico', 350.00, 25, 4),
('Monitor 24 polegadas', 1200.00, 15, 1),
('Smartphone Samsung', 2500.00, 20, 2),
('Smartphone Motorola', 1800.00, 18, 2),
('Cadeira Gamer', 1300.00, 7, 3),
('Mesa Escritorio', 800.00, 12, 3),
('Headset Gamer', 450.00, 30, 5),
('PlayStation 5', 4200.00, 6, 5),
('Xbox Series X', 4000.00, 5, 5),
('Webcam Full HD', 300.00, 20, 4),
('Impressora Epson', 950.00, 9, 3),
('Smart TV 50', 2800.00, 11, 6),
('Caixa de Som Bluetooth', 500.00, 40, 6),
('Tablet Samsung', 1600.00, 14, 2),
('HD Externo 2TB', 600.00, 17, 1);

-- =========================================================================
-- 7. INSERTS - PEDIDOS
-- =========================================================================
INSERT INTO pedidos (data_pedido, status, id_cliente, id_vendedor) VALUES
('2026-07-01', 'Pago', 1, 1),
('2026-07-03', 'Pago', 2, 2),
('2026-07-05', 'Enviado', 1, 1),
('2026-07-07', 'Pendente', 3, 3),
('2026-07-10', 'Pago', 5, 4),
('2026-07-11', 'Cancelado', 6, 2),
('2026-07-14', 'Pago', 7, 5),
('2026-07-16', 'Enviado', 3, 3),
('2026-07-20', 'Pago', 9, 4),
('2026-07-23', 'Pendente', 2, 2),
('2026-08-01', 'Pago', 1, 1),
('2026-08-02', 'Pago', 5, 4),
('2026-08-04', 'Enviado', 7, 5),
('2026-08-05', 'Pago', 9, 4),
('2026-08-07', 'Pendente', 10, 3);
-- Clientes 4 e 8 nao possuem pedidos.

-- =========================================================================
-- 8. INSERTS - ITENS DOS PEDIDOS
-- =========================================================================
INSERT INTO itens_pedido(id_pedido, id_produto, quantidade, preco_unitario) VALUES
(1, 1, 1, 4500.00),
(1, 3, 2, 150.00),
(2, 6, 1, 2500.00),
(2, 13, 1, 300.00),
(3, 5, 2, 1200.00),
(3, 4, 1, 350.00),
(4, 11, 1, 4200.00),
(5, 15, 1, 2800.00),
(5, 16, 2, 500.00),
(6, 8, 1, 1300.00),
(7, 12, 1, 4000.00),
(7, 10, 2, 450.00),
(8, 2, 1, 3800.00),
(8, 5, 1, 1200.00),
(9, 17, 2, 1600.00),
(10, 7, 1, 1800.00),
(11, 11, 1, 4200.00),
(11, 10, 1, 450.00),
(12, 1, 2, 4500.00),
(13, 15, 1, 2800.00),
(13, 16, 1, 500.00),
(14, 6, 1, 2500.00),
(14, 3, 1, 150.00),
(15, 9, 1, 800.00);


-- =========================================================================
-- 9. RESOLUÇÃO DOS EXERCÍCIOS DE SUBCONSULTAS (SUBSELECTS)
-- =========================================================================

-- Exercício 01: Subconsulta escalar
-- Liste todos os produtos cujo preço seja maior que a média de preços de todos os produtos.
SELECT nome_produto, preco 
FROM produtos 
WHERE preco > (SELECT AVG(preco) FROM produtos);


-- Exercício 02: Subconsulta escalar
-- Liste o produto ou os produtos que possuem o maior preço cadastrado.
SELECT nome_produto, preco 
FROM produtos 
WHERE preco = (SELECT MAX(preco) FROM produtos);


-- Exercício 03: Subconsulta escalar
-- Liste o produto ou os produtos que possuem o menor preço cadastrado.
SELECT nome_produto, preco 
FROM produtos 
WHERE preco = (SELECT MIN(preco) FROM produtos);


-- Exercício 04: Subconsulta no SELECT
-- Exiba nome e preço de cada produto e, em uma terceira coluna, apresente a média geral de preços.
SELECT 
    nome_produto, 
    preco, 
    (SELECT ROUND(AVG(preco), 2) FROM produtos) AS media_geral 
FROM produtos;


-- Exercício 05: Subconsulta no SELECT
-- Exiba nome, preço, média geral e a diferença entre o preço do produto e a média geral.
SELECT 
    nome_produto, 
    preco, 
    (SELECT ROUND(AVG(preco), 2) FROM produtos) AS media_geral,
    ROUND(preco - (SELECT AVG(preco) FROM produtos), 2) AS diferenca_media 
FROM produtos;


-- Exercício 06: IN
-- Liste os clientes que realizaram pelo menos um pedido.
SELECT nome, cidade, estado 
FROM clientes 
WHERE id_cliente IN (SELECT id_cliente FROM pedidos);


-- Exercício 07: IN
-- Liste os produtos que já apareceram em algum item de pedido.
SELECT nome_produto, preco 
FROM produtos 
WHERE id_produto IN (SELECT id_produto FROM itens_pedido);


-- Exercício 08: NOT IN
-- Liste os clientes que não aparecem em nenhum pedido utilizando NOT IN.
SELECT nome, cidade, estado 
FROM clientes 
WHERE id_cliente NOT IN (SELECT id_cliente FROM pedidos);


-- Exercício 09: NOT EXISTS
-- Reescreva o exercício anterior utilizando NOT EXISTS (clientes sem pedidos).
SELECT c.nome, c.cidade, c.estado 
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);


-- Exercício 10: EXISTS
-- Liste os vendedores que possuem pelo menos um pedido registrado.
SELECT v.nome, v.salario 
FROM vendedores v
WHERE EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_vendedor = v.id_vendedor
);


-- Exercício 11: NOT EXISTS
-- Liste os produtos que nunca foram vendidos.
SELECT p.nome_produto, p.preco 
FROM produtos p
WHERE NOT EXISTS (
    SELECT 1 
    FROM itens_pedido ip 
    WHERE ip.id_produto = p.id_produto
);


-- Exercício 12: IN com mais de uma tabela
-- Liste os clientes que compraram o produto "Notebook Dell".
SELECT nome, cidade, estado 
FROM clientes 
WHERE id_cliente IN (
    SELECT p.id_cliente 
    FROM pedidos p
    JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
    JOIN produtos pr ON ip.id_produto = pr.id_produto
    WHERE pr.nome_produto = 'Notebook Dell'
);


-- Exercício 13: ANY
-- Liste os produtos cujo preço seja maior que o preço de pelo menos um produto da categoria Telefonia.
SELECT nome_produto, preco 
FROM produtos 
WHERE preco > ANY (
    SELECT p.preco 
    FROM produtos p
    JOIN categorias c ON p.id_categoria = c.id_categoria
    WHERE c.nome_categoria = 'Telefonia'
);


-- Exercício 14: ALL
-- Liste os produtos cujo preço seja maior que o preço de todos os produtos da categoria Acessórios.
SELECT nome_produto, preco 
FROM produtos 
WHERE preco > ALL (
    SELECT p.preco 
    FROM produtos p
    JOIN categorias c ON p.id_categoria = c.id_categoria
    WHERE c.nome_categoria = 'Acessorios'
);


-- Exercício 15: ANY
-- Liste os vendedores cujo salário seja maior que pelo menos um salário existente entre os demais vendedores.
SELECT nome, salario 
FROM vendedores 
WHERE salario > ANY (SELECT salario FROM vendedores);


-- Exercício 16: ALL
-- Liste o vendedor ou os vendedores cujo salário seja maior ou igual a todos os salários cadastrados.
SELECT nome, salario 
FROM vendedores 
WHERE salario >= ALL (SELECT salario FROM vendedores);


-- Exercício 17: Subconsulta correlacionada
-- Liste os produtos cujo preço seja maior que a média de preços de sua própria categoria.
SELECT p.nome_produto, p.preco, p.id_categoria 
FROM produtos p
WHERE p.preco > (
    SELECT AVG(sub.preco) 
    FROM produtos sub 
    WHERE sub.id_categoria = p.id_categoria
);


-- Exercício 18: Subconsulta correlacionada
-- Liste os produtos cujo estoque seja maior que a média de estoque de sua própria categoria.
SELECT p.nome_produto, p.estoque, p.id_categoria 
FROM produtos p
WHERE p.estoque > (
    SELECT AVG(sub.estoque) 
    FROM produtos sub 
    WHERE sub.id_categoria = p.id_categoria
);


-- Exercício 19: Subconsulta correlacionada
-- Liste os clientes cujo limite de crédito seja maior que a média de limite de crédito dos clientes do mesmo estado.
SELECT c.nome, c.estado, c.limite_credito 
FROM clientes c
WHERE c.limite_credito > (
    SELECT AVG(sub.limite_credito) 
    FROM clientes sub 
    WHERE sub.estado = c.estado
);


-- Exercício 20: Subconsulta correlacionada
-- Liste, para cada categoria, o produto ou os produtos de maior preço daquela categoria.
SELECT p.nome_produto, p.preco