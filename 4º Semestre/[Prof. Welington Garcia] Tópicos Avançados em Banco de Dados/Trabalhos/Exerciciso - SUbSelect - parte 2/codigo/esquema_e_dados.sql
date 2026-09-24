/*
  =========================================================================
  Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
  Professor: Prof. Welington Garcia
  Atividade: Esquema de Banco de Dados e Carga Inicial para Subconsultas Avançadas
  =========================================================================
  Como executar:
  1. Abra o terminal psql ou pgAdmin conectado ao PostgreSQL.
  2. Crie ou selecione um banco de dados de testes:
     CREATE DATABASE unifef_topicos_bd;
     \c unifef_topicos_bd;
  3. Execute este script SQL:
     \i esquema_e_dados.sql
  =========================================================================
*/

-- Remoção idempotente de tabelas (na ordem reversa de dependência de FK)
DROP TABLE IF EXISTS produtos_promocao CASCADE;
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- 1. Criação das Tabelas Principais

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    id_categoria INT REFERENCES categorias(id_categoria) ON DELETE SET NULL,
    preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    quantidade_estoque INT NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0)
);

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    limite_credito NUMERIC(10, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES clientes(id_cliente) ON DELETE CASCADE,
    data_pedido DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id_pedido INT REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    id_produto INT REFERENCES produtos(id_produto) ON DELETE CASCADE,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    PRIMARY KEY (id_pedido, id_produto)
);

-- 2. Carga de Dados de Teste

INSERT INTO categorias (nome_categoria) VALUES
('Eletrônicos'),
('Informática'),
('Móveis'),
('Vestuário');

INSERT INTO produtos (nome_produto, id_categoria, preco, quantidade_estoque) VALUES
('Smartphone X', 1, 2500.00, 10),
('Smart TV 55"', 1, 3200.00, 5),
('Fone Bluetooth', 1, 200.00, 30),
('Notebook Pro', 2, 4500.00, 8),
('Mouse Óptico', 2, 80.00, 50),
('Teclado Mecânico', 2, 350.00, 20),
('Cadeira Ergonômica', 3, 1200.00, 12),
('Mesa para Escritório', 3, 800.00, 15),
('Camiseta Algodão', 4, 60.00, 100),
('Calça Jeans', 4, 150.00, 60);

INSERT INTO clientes (nome_cliente, limite_credito) VALUES
('Ana Silva', 5000.00),
('Bruno Costa', 3000.00),
('Carla Souza', 1500.00),
('Diego Oliveira', 8000.00),
('Eduardo Rocha', 2000.00); -- Cliente sem pedidos

INSERT INTO pedidos (id_cliente, data_pedido) VALUES
(1, '2026-08-01'),
(1, '2026-08-10'),
(2, '2026-08-15'),
(3, '2026-08-20'),
(4, '2026-08-22'),
(4, '2026-08-25');

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(1, 1, 1, 2500.00), -- Pedido 1: 2500.00
(1, 3, 2, 200.00),  -- Pedido 1: + 400.00 = 2900.00
(2, 4, 1, 4500.00), -- Pedido 2: 4500.00
(3, 2, 1, 3200.00), -- Pedido 3: 3200.00
(4, 5, 2, 80.00),   -- Pedido 4: 160.00
(5, 4, 2, 4500.00), -- Pedido 5: 9000.00
(6, 7, 1, 1200.00); -- Pedido 6: 1200.00
