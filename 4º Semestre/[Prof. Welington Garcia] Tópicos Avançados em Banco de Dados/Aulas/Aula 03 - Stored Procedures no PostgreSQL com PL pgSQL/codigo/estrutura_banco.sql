-- Disciplina: Tópicos Avançados em Banco de Dados
-- Professor: Prof. Welington Garcia
-- Tema: Stored Procedures no PostgreSQL com PL/pgSQL
-- Descrição: Script de criação de tabelas e carga inicial de dados para os testes das procedures.

-- Limpeza prévia do esquema para garantir idempotência
DROP TABLE IF EXISTS logs_sistema CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS contas CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- 1. Tabela de Clientes
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Logs de Sistema
CREATE TABLE logs_sistema (
    id SERIAL PRIMARY KEY,
    acao VARCHAR(255) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabela de Contas Bancárias
CREATE TABLE contas (
    id SERIAL PRIMARY KEY,
    titular VARCHAR(100) NOT NULL,
    saldo NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (saldo >= 0)
);

-- 4. Tabela de Categorias
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

-- 5. Tabela de Produtos
CREATE TABLE produtos (
    id SERIAL PRIMARY KEY,
    categoria_id INT REFERENCES categorias(id),
    nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL DEFAULT 0.00
);

-- Inserção de dados iniciais para testes
INSERT INTO contas (titular, saldo) VALUES 
('Ana Silva', 1500.00),
('Bruno Souza', 350.00);

INSERT INTO categorias (nome) VALUES 
('Eletrônicos'),
('Livros');

INSERT INTO produtos (categoria_id, nome, preco) VALUES 
(1, 'Smartphone X', 2500.00),
(1, 'Fone de Ouvido Bluetooth', 150.00),
(2, 'Banco de Dados Prático', 89.90);
