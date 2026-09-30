-- ============================================================================
-- Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor : Prof. Welington Garcia
-- Tema      : Criação do Esquema e Carga de Dados Inicial
-- Como executar:
--   psql -U <usuario> -d <banco_de_dados> -f schema.sql
-- ============================================================================

DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    cidade VARCHAR(80),
    saldo DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE funcionarios (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    salario DECIMAL(10,2) NOT NULL
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    categoria VARCHAR(50),
    preco DECIMAL(10,2) NOT NULL,
    estoque INT NOT NULL
);

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL,
    data_pedido DATE DEFAULT CURRENT_DATE,
    status VARCHAR(30) DEFAULT 'ABERTO',
    valor_total DECIMAL(10,2) DEFAULT 0,
    CONSTRAINT fk_pedidos_clientes
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE itens_pedido (
    id_item SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_produto INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_itens_pedido_pedidos
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    CONSTRAINT fk_itens_pedido_produtos
        FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
);

-- =========================================
-- CLIENTES
-- =========================================
INSERT INTO clientes (nome, email, cidade, saldo) VALUES
('Ana Paula', 'ana@gmail.com', 'Jales', 500.00),
('Carlos Silva', 'carlos@gmail.com', 'Fernandópolis', 300.00),
('Mariana Souza', 'mariana@gmail.com', 'Urânia', 800.00),
('João Pedro', 'joao@gmail.com', 'Santa Fé do Sul', 150.00),
('Juliana Lima', 'juliana@gmail.com', 'Votuporanga', 1000.00);

-- =========================================
-- FUNCIONÁRIOS
-- =========================================
INSERT INTO funcionarios (nome, cargo, salario) VALUES
('Roberto Alves', 'Gerente', 5000.00),
('Fernanda Costa', 'Vendedora', 2500.00),
('Lucas Martins', 'Caixa', 2200.00),
('Patrícia Gomes', 'Estoquista', 2100.00),
('Ricardo Mendes', 'Vendedor', 2600.00);

-- =========================================
-- PRODUTOS
-- =========================================
INSERT INTO produtos (nome, categoria, preco, estoque) VALUES
('Notebook Lenovo', 'Informática', 3500.00, 10),
('Mouse Logitech', 'Informática', 120.00, 50),
('Teclado Mecânico', 'Informática', 250.00, 20),
('Monitor 24', 'Informática', 900.00, 15),
('Cadeira Gamer', 'Móveis', 1500.00, 8),
('Mesa Escritório', 'Móveis', 700.00, 12),
('Headset Gamer', 'Acessórios', 300.00, 25),
('Webcam HD', 'Acessórios', 180.00, 30);

-- =========================================
-- PEDIDOS
-- =========================================
INSERT INTO pedidos (id_cliente, data_pedido, status, valor_total) VALUES
(1, '2026-03-01', 'FECHADO', 3740.00),
(2, '2026-03-02', 'ABERTO', 0.00),
(3, '2026-03-03', 'FECHADO', 1080.00),
(4, '2026-03-04', 'CANCELADO', 300.00),
(5, '2026-03-05', 'ABERTO', 0.00);

-- =========================================
-- ITENS DO PEDIDO
-- =========================================
INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario, subtotal) VALUES
(1, 1, 1, 3500.00, 3500.00),
(1, 2, 2, 120.00, 240.00),
(3, 4, 1, 900.00, 900.00),
(3, 8, 1, 180.00, 180.00),
(4, 7, 1, 300.00, 300.00);
