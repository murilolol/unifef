-- Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor: Prof. Welington Garcia
-- Assunto: Exemplos Práticos de Views, JOINs, Agregações, WITH CHECK OPTION e Materialized Views

-- Criação do Esquema do E-commerce
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(150) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    id_categoria INT REFERENCES categorias(id_categoria)
);

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    limite_credito NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    id_cliente INT REFERENCES clientes(id_cliente)
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id_pedido INT REFERENCES pedidos(id_pedido),
    id_produto INT REFERENCES produtos(id_produto),
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,
    PRIMARY KEY (id_pedido, id_produto)
);

-- Inserção de dados de exemplo
INSERT INTO categorias (id_categoria, nome_categoria) VALUES
(1, 'Eletrônicos'), (2, 'Livros') ON CONFLICT DO NOTHING;

INSERT INTO produtos (id_produto, nome_produto, preco, id_categoria) VALUES
(1, 'Smartphone XYZ', 1500.00, 1),
(2, 'Notebook Gamer', 4500.00, 1),
(3, 'Livro SQL Avançado', 120.00, 2) ON CONFLICT DO NOTHING;

INSERT INTO clientes (id_cliente, nome, cidade, estado, limite_credito) VALUES
(1, 'Ana Souza', 'São Paulo', 'SP', 5000.00),
(2, 'Carlos Lima', 'Campinas', 'SP', 3000.00),
(3, 'Mariana Costa', 'Belo Horizonte', 'MG', 4000.00) ON CONFLICT DO NOTHING;

INSERT INTO pedidos (id_pedido, data_pedido, status, id_cliente) VALUES
(101, '2026-09-01', 'Pago', 1),
(102, '2026-09-02', 'Pendente', 2) ON CONFLICT DO NOTHING;

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(101, 1, 1, 1500.00),
(101, 3, 2, 120.00),
(102, 2, 1, 4500.00) ON CONFLICT DO NOTHING;

-- 1. Conceito: View Simples
CREATE OR REPLACE VIEW vw_produtos_caros AS
SELECT id_produto, nome_produto, preco
FROM produtos
WHERE preco > 1000;

-- 2. View com Filtro Geográfico
CREATE OR REPLACE VIEW vw_clientes_sp AS
SELECT id_cliente, nome, cidade, estado, limite_credito
FROM clientes
WHERE estado = 'SP';

-- 3. View com Relacionamento (JOIN)
CREATE OR REPLACE VIEW vw_pedidos_clientes AS
SELECT
  p.id_pedido,
  p.data_pedido,
  p.status,
  c.id_cliente,
  c.nome AS cliente
FROM pedidos p
JOIN clientes c
  ON p.id_cliente = c.id_cliente;

-- 4. View com Múltiplos JOINs
CREATE OR REPLACE VIEW vw_detalhes_vendas AS
SELECT
  p.id_pedido,
  c.nome AS cliente,
  pr.nome_produto,
  ip.quantidade,
  ip.preco_unitario,
  ip.quantidade * ip.preco_unitario AS subtotal
FROM pedidos p
JOIN clientes c
  ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip
  ON p.id_pedido = ip.id_pedido
JOIN produtos pr
  ON ip.id_produto = pr.id_produto;

-- 5. View com Agregação (GROUP BY e SUM)
CREATE OR REPLACE VIEW vw_total_pedidos AS
SELECT
  p.id_pedido,
  c.nome AS cliente,
  SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
JOIN clientes c
  ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip
  ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, c.nome;

-- 6. View Atualizável com WITH CHECK OPTION
CREATE OR REPLACE VIEW vw_clientes_sp_protegida AS
SELECT id_cliente, nome, cidade, estado, limite_credito
FROM clientes
WHERE estado = 'SP'
WITH CHECK OPTION;

-- 7. Materialized View
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_vendas_por_cliente AS
SELECT
  c.id_cliente,
  c.nome,
  SUM(ip.quantidade * ip.preco_unitario) AS total
FROM clientes c
JOIN pedidos p
  ON c.id_cliente = p.id_cliente
JOIN itens_pedido ip
  ON p.id_pedido = ip.id_pedido
GROUP BY c.id_cliente, c.nome;

-- Atualização da Materialized View
REFRESH MATERIALIZED VIEW mv_vendas_por_cliente;

-- Índice na Materialized View
CREATE INDEX IF NOT EXISTS idx_mv_vendas_cliente
ON mv_vendas_por_cliente(id_cliente);
