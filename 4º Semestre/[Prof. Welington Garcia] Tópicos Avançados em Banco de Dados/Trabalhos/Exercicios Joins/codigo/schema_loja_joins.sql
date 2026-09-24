-- =========================================================
-- BANCO DE DADOS PARA PRÁTICA DE JOINS
-- Disciplina: Tópicos Avançados em Banco de Dados
-- Professor: Welington Garcia
-- Instituição: UniFEF
-- PostgreSQL
-- =========================================================

CREATE DATABASE loja_joins;

-- Após criar o banco, conecte-se ao banco loja_joins
-- e execute os comandos abaixo.

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2)
);

CREATE TABLE vendedores (
    id_vendedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario NUMERIC(10,2)
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    id_categoria INTEGER,
    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias(id_categoria)
);

CREATE TABLE pedidos (
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

INSERT INTO itens_pedido
(id_pedido, id_produto, quantidade, preco_unitario) VALUES
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
