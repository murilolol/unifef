-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre) - UniFEF
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Criação do Banco EscolaDB e Carga Inicial de Dados
-- Como Executar: mysql -u seu_usuario -p < exemplos.sql
--                Ou execute os comandos dentro do MySQL Workbench / DBeaver / CLI
-- =============================================================================

CREATE DATABASE IF NOT EXISTS EscolaDB;
USE EscolaDB;

-- Remocao preventiva para garantir idempotencia na execucao repetida
DROP TABLE IF EXISTS ALUNO;
DROP TABLE IF EXISTS VENDEDOR;
DROP TABLE IF EXISTS PRODUTO;
DROP TABLE IF EXISTS CLIENTES;

-- -----------------------------------------------------------------------------
-- 1. Definicao das Tabelas (DDL)
-- -----------------------------------------------------------------------------

CREATE TABLE CLIENTES (
    CodCliente INT PRIMARY KEY,
    NomeCliente VARCHAR(100),
    EndCliente VARCHAR(150),
    Estado CHAR(2),
    Idade INT
);

CREATE TABLE PRODUTO (
    CodigoProduto INT PRIMARY KEY,
    Descricao VARCHAR(100),
    Unidade CHAR(2),
    Val_Unit DECIMAL(10,2)
);

CREATE TABLE VENDEDOR (
    CodigoVendedor INT PRIMARY KEY,
    NomeVendedor VARCHAR(100),
    Salario_Fixo DECIMAL(10,2)
);

CREATE TABLE ALUNO (
    Matricula INT PRIMARY KEY,
    Nome_Aluno VARCHAR(100),
    Data_Nasc DATE,
    Cidade VARCHAR(50)
);

-- -----------------------------------------------------------------------------
-- 2. Carga Inicial dos Dados (DML)
-- -----------------------------------------------------------------------------

INSERT INTO CLIENTES (CodCliente, NomeCliente, EndCliente, Estado, Idade) VALUES
(1, 'Ana Silva', 'Rua A', 'SP', 25),
(2, 'Bruno Souza', 'Rua B', 'RJ', 30),
(3, 'Carlos Lima', 'Rua C', 'SP', 22),
(4, 'Daniela Rocha', 'Rua D', 'MG', 28);

INSERT INTO PRODUTO (CodigoProduto, Descricao, Unidade, Val_Unit) VALUES
(1, 'Caneta', 'UN', 1.50),
(2, 'Caderno', 'UN', 10.00),
(3, 'Tecido', 'M', 2.00),
(4, 'Linha', 'M', 0.50);

INSERT INTO VENDEDOR (CodigoVendedor, NomeVendedor, Salario_Fixo) VALUES
(1, 'João', 2500.00),
(2, 'Maria', 3000.00),
(3, 'Pedro', 4500.00);

INSERT INTO ALUNO (Matricula, Nome_Aluno, Data_Nasc, Cidade) VALUES
(1, 'Lucas', '1990-05-10', 'Campinas'),
(2, 'Mariana', '1985-08-20', 'São Paulo'),
(3, 'Rafael', '2000-01-15', 'Campinas');
