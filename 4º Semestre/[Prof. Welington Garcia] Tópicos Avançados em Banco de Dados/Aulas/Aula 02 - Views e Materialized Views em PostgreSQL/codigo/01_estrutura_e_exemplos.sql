-- ==========================================================================
-- Disciplina: Tópicos Avançados em Banco de Dados
-- Tema: Views e Materialized Views em PostgreSQL
-- Professor: Prof. Welington Garcia
-- Descrição: Criação de tabelas de exemplo, carga de dados e exemplos de aula.
-- Como executar: Execute este script em um banco de dados PostgreSQL.
-- ==========================================================================

-- 1. Limpeza prévia para garantir idempotência
DROP MATERIALIZED VIEW IF EXISTS mv_vendas_por_cliente CASCADE;
DROP VIEW IF EXISTS vw_relatorio_vendas CASCADE;
DROP VIEW IF EXISTS vw_produtos_categoria CASCADE;
DROP VIEW IF EXISTS vw_resumo_clientes CASCADE;
DROP VIEW IF EXISTS vw_total_pedidos CASCADE;
DROP VIEW IF EXISTS vw_detalhes_vendas CASCADE;
DROP VIEW IF EXISTS vw_pedidos_clientes CASCADE;
DROP VIEW IF EXISTS vw_clientes_sp CASCADE;
DROP VIEW IF EXISTS vw_produtos_caros CASCADE;

DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS vendedores CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- 2. Criação das Tabelas do Modelo da Aula
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2),
    limite_credito DECIMAL(10,2)
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(50) NOT NULL
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    id_categoria INT REFERENCES categorias(id_categoria)
);

CREATE TABLE vendedores (
    id_vendedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario DECIMAL(10,2)
);

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL,
    id_cliente INT REFERENCES clientes(id_cliente)
);

CREATE TABLE itens_pedido (
    id_pedido INT REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    id_produto INT REFERENCES produtos(id_produto),
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_pedido, id_produto)
);

-- 3. Inserção de Dados de Teste
INSERT INTO clientes (nome, cidade, estado, limite_credito) VALUES
('Ana Silva', 'São Paulo', 'SP', 5000.00),
('Bruno Souza', 'Campinas', 'SP', 3000.00),
('Carlos Oliveira', 'Belo Horizonte', 'MG', 4000.00),
('Daniela Lima', 'Rio de Janeiro', 'RJ', 6000.00);

INSERT INTO categorias (nome_categoria) VALUES
('Eletrônicos'),
('Móveis'),
('Livros');

INSERT INTO produtos (nome_produto, preco, id_categoria) VALUES
('Smartphone X', 1200.00, 1),
('Notebook Pro', 3500.00, 1),
('Mesa de Escritório', 450.00, 2),
('Livro de SQL Avançado', 120.00, 3);

INSERT INTO vendedores (nome, salario) VALUES
('Marcos Vendedor', 2500.00);

INSERT INTO pedidos (data_pedido, status, id_cliente) VALUES
('2026-09-01', 'Pago', 1),
('2026-09-02', 'Pendente', 2),
('2026-09-02', 'Pago', 1);

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(1, 1, 1, 1200.00),
(1, 4, 2, 120.00),
(2, 2, 1, 3500.00),
(3, 3, 1, 450.00);

-- 4. Exemplos de Aula

-- Exemplo: View Simples
CREATE VIEW vw_produtos_caros AS
SELECT id_produto, nome_produto, preco
FROM produtos
WHERE preco > 1000;

-- Exemplo: View com JOIN
CREATE VIEW vw_pedidos_clientes AS
SELECT
  p.id_pedido,
  p.data_pedido,
  p.status,
  c.id_cliente,
  c.nome AS cliente
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente;

-- Exemplo: View com Múltiplos JOINs
CREATE VIEW vw_detalhes_vendas AS
SELECT
  p.id_pedido,
  c.nome AS cliente,
  pr.nome_produto,
  ip.quantidade,
  ip.preco_unitario,
  ip.quantidade * ip.preco_unitario AS subtotal
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
JOIN produtos pr ON ip.id_produto = pr.id_produto;

-- Exemplo: View com Agregação
CREATE VIEW vw_total_pedidos AS
SELECT
  p.id_pedido,
  c.nome AS cliente,
  SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, c.nome;
