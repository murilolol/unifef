-- ============================================================================
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR : Prof. Welington Garcia
-- TEMA      : Subconsultas Avançadas no PostgreSQL - Modelagem e Carga de Dados
-- EXECUÇÃO  : psql -U <usuario> -d <nome_banco> -f estrutura_dados.sql
-- ============================================================================

-- Limpeza controlada de objetos anteriores (idempotência)
DROP TABLE IF EXISTS produtos_promocao CASCADE;
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- ----------------------------------------------------------------------------
-- 1. Criação das Tabelas
-- ----------------------------------------------------------------------------

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(150) NOT NULL,
    id_categoria INT NOT NULL REFERENCES categorias(id_categoria),
    preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    estoque INT NOT NULL DEFAULT 0 CHECK (estoque >= 0)
);

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(150) NOT NULL,
    limite_credito NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (limite_credito >= 0)
);

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL REFERENCES clientes(id_cliente),
    data_pedido DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE itens_pedido (
    id_pedido INT NOT NULL REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    id_produto INT NOT NULL REFERENCES produtos(id_produto),
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    PRIMARY KEY (id_pedido, id_produto)
);

-- ----------------------------------------------------------------------------
-- 2. Inserção de Dados de Teste
-- ----------------------------------------------------------------------------

INSERT INTO categorias (id_categoria, nome_categoria) VALUES
(1, 'Informática'),
(2, 'Eletrodomésticos'),
(3, 'Móveis Corporativos'),
(4, 'Acessórios');

INSERT INTO produtos (id_produto, nome_produto, id_categoria, preco, estoque) VALUES
(1, 'Notebook Profissional i7', 1, 4800.00, 15),
(2, 'Monitor UltraWide 34"', 1, 2400.00, 20),
(3, 'Teclado Mecânico RGB', 1, 350.00, 40),
(4, 'Refrigerador Frost Free 450L', 2, 3800.00, 8),
(5, 'Forno Micro-ondas 32L', 2, 650.00, 25),
(6, 'Estação de Trabalho Modular', 3, 1600.00, 12),
(7, 'Cadeira Ergonômica NR-17', 3, 1100.00, 18),
(8, 'Mouse Sem Fio Básico', 4, 80.00, 60),
(9, 'Suporte Articulado Monitor', 4, 150.00, 45);

INSERT INTO clientes (id_cliente, nome_cliente, limite_credito) VALUES
(1, 'Ana Clara Silveira', 5000.00),
(2, 'Bruno Henrique Ramos', 3500.00),
(3, 'Carla Mendes Rezende', 4200.00),
(4, 'Diego Ferreira Lima', 2000.00),
(5, 'Eduardo Paes Botelho', 1500.00), -- Cliente sem pedidos (para teste de NOT EXISTS)
(6, 'Fernanda Nogueira Costa', 2800.00); -- Cliente sem pedidos (para teste de NOT EXISTS)

INSERT INTO pedidos (id_pedido, id_cliente, data_pedido) VALUES
(101, 1, '2026-08-01'),
(102, 1, '2026-08-10'),
(103, 2, '2026-08-05'),
(104, 2, '2026-08-15'),
(105, 3, '2026-08-12'),
(106, 4, '2026-08-20');

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
-- Pedido 101 (Ana): Total = 4800.00 (Notebook)
(101, 1, 1, 4800.00),
-- Pedido 102 (Ana): Total = 700.00 (Teclados)
(102, 3, 2, 350.00),
-- Pedido 103 (Bruno): Total = 3800.00 (Refrigerador) -> Total > 3000
(103, 4, 1, 3800.00),
-- Pedido 104 (Bruno): Total = 650.00 (Micro-ondas)
(104, 5, 1, 650.00),
-- Pedido 105 (Carla): Total = 4800.00 (Monitores) -> Total > 3000
(105, 2, 2, 2400.00),
-- Pedido 106 (Diego): Total = 230.00 (Acessórios)
(106, 8, 1, 80.00),
(106, 9, 1, 150.00);

-- Ajuste de sequências automáticas após carga manual
SELECT setval('categorias_id_categoria_seq', (SELECT MAX(id_categoria) FROM categorias));
SELECT setval('produtos_id_produto_seq', (SELECT MAX(id_produto) FROM produtos));
SELECT setval('clientes_id_cliente_seq', (SELECT MAX(id_cliente) FROM clientes));
SELECT setval('pedidos_id_pedido_seq', (SELECT MAX(id_pedido) FROM pedidos));
