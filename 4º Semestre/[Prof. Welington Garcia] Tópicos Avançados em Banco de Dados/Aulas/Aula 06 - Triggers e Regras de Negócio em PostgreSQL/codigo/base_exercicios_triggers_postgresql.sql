-- ============================================================================
-- DISCIPLINA : Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR  : Prof. Welington Garcia
-- INSTITUIÇÃO: UniFEF - Centro Universitário de Votuporanga
-- TEMA       : Base de Dados para Exercícios de Triggers (PostgreSQL)
-- BANCO      : db_loja_triggers
-- COMO EXECUTAR:
--   psql -U postgres -c "CREATE DATABASE db_loja_triggers;"
--   psql -U postgres -d db_loja_triggers -f base_exercicios_triggers_postgresql.sql
-- ============================================================================

DROP TABLE IF EXISTS auditoria_produtos CASCADE;
DROP TABLE IF EXISTS historico_estoque CASCADE;
DROP TABLE IF EXISTS clientes_excluidos CASCADE;
DROP TABLE IF EXISTS produtos_excluidos CASCADE;
DROP TABLE IF EXISTS itens_venda CASCADE;
DROP TABLE IF EXISTS vendas CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE funcionarios (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cargo VARCHAR(80) NOT NULL,
    salario NUMERIC(10,2) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_admissao DATE NOT NULL
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE,
    descricao VARCHAR(200)
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    id_categoria INTEGER REFERENCES categorias(id_categoria),
    preco NUMERIC(10,2) NOT NULL,
    estoque INTEGER NOT NULL,
    estoque_minimo INTEGER DEFAULT 5,
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendas (
    id_venda SERIAL PRIMARY KEY,
    id_cliente INTEGER REFERENCES clientes(id_cliente),
    id_funcionario INTEGER REFERENCES funcionarios(id_funcionario),
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_total NUMERIC(12,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ABERTA'
);

CREATE TABLE itens_venda (
    id_item SERIAL PRIMARY KEY,
    id_venda INTEGER NOT NULL REFERENCES vendas(id_venda),
    id_produto INTEGER NOT NULL REFERENCES produtos(id_produto),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(12,2)
);

CREATE TABLE auditoria_produtos (
    id_auditoria SERIAL PRIMARY KEY,
    id_produto INTEGER,
    operacao VARCHAR(20),
    nome_antigo VARCHAR(120),
    nome_novo VARCHAR(120),
    preco_antigo NUMERIC(10,2),
    preco_novo NUMERIC(10,2),
    estoque_antigo INTEGER,
    estoque_novo INTEGER,
    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE historico_estoque (
    id_historico SERIAL PRIMARY KEY,
    id_produto INTEGER,
    estoque_antigo INTEGER,
    estoque_novo INTEGER,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clientes_excluidos (
    id_historico SERIAL PRIMARY KEY,
    id_cliente INTEGER,
    nome VARCHAR(120),
    email VARCHAR(120),
    telefone VARCHAR(20),
    data_exclusao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE produtos_excluidos (
    id_historico SERIAL PRIMARY KEY,
    id_produto INTEGER,
    nome VARCHAR(120),
    preco NUMERIC(10,2),
    estoque INTEGER,
    data_exclusao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO clientes (nome, email, telefone) VALUES
('Ana Martins', 'ana@email.com', '(17) 99911-1001'),
('Bruno Alves', 'bruno@email.com', '(17) 99911-1002'),
('Carlos Souza', 'carlos@email.com', '(17) 99911-1003'),
('Daniela Lima', 'daniela@email.com', '(17) 99911-1004'),
('Eduardo Rocha', 'eduardo@email.com', '(17) 99911-1005'),
('Fernanda Costa', 'fernanda@email.com', '(17) 99911-1006');

INSERT INTO funcionarios (nome, cargo, salario, data_admissao) VALUES
('João Mendes', 'Vendedor', 2800.00, '2024-03-01'),
('Mariana Lopes', 'Vendedor', 3000.00, '2023-08-15'),
('Paulo Ribeiro', 'Gerente', 5500.00, '2022-01-10');

INSERT INTO categorias (nome, descricao) VALUES
('Informática', 'Periféricos e equipamentos de informática'),
('Escritório', 'Produtos para escritório'),
('Acessórios', 'Acessórios diversos'),
('Armazenamento', 'Dispositivos para armazenamento de dados');

INSERT INTO produtos (nome, id_categoria, preco, estoque, estoque_minimo) VALUES
('Teclado Mecânico', 1, 250.00, 20, 5),
('Mouse Gamer', 1, 180.00, 35, 8),
('Monitor 24 Polegadas', 1, 950.00, 12, 4),
('Notebook', 1, 4500.00, 7, 2),
('Cadeira de Escritório', 2, 850.00, 8, 3),
('Mouse Pad', 3, 65.00, 40, 10),
('Pendrive 64GB', 4, 55.00, 50, 10),
('SSD 1TB', 4, 480.00, 16, 5),
('Webcam Full HD', 1, 320.00, 9, 3),
('Headset USB', 3, 220.00, 18, 5);

INSERT INTO vendas (id_cliente, id_funcionario, valor_total, status) VALUES
(1, 1, 430.00, 'FINALIZADA'),
(2, 2, 950.00, 'FINALIZADA'),
(3, 1, 0.00, 'ABERTA');

INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES
(1, 1, 1, 250.00, 250.00),
(1, 2, 1, 180.00, 180.00),
(2, 3, 1, 950.00, 950.00);
