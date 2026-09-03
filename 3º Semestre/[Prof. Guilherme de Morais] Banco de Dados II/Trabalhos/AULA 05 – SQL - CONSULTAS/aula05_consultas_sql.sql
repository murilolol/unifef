FILENAME: aula05_consultas_sql.sql
---CODE_START---
-- ==============================================================================
-- FACULDADE DE SISTEMAS DE INFORMAÇÃO
-- DISCIPLINA: BANCO DE DADOS II
-- PROFESSOR: GUILHERME DE MORAIS
-- ATIVIDADE: AULA 05 – SQL - CONSULTAS
-- AUTOR: PROFESSOR & DESENVOLVEDOR SÊNIOR
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. ESTRUTURA DE LIMPEZA (Para permitir reexecução limpa do script)
-- ------------------------------------------------------------------------------
DROP TABLE IF EXISTS veiculos;
DROP TABLE IF EXISTS clientes;

-- ------------------------------------------------------------------------------
-- 2. CRIAÇÃO DAS TABELAS (DDL)
-- ------------------------------------------------------------------------------
CREATE TABLE clientes (
    cod_cli INTEGER NOT NULL,
    cpf BIGINT NOT NULL, -- Alterado para BIGINT para evitar estouro de inteiros em CPFs reais
    nome VARCHAR(50), 
    endereco VARCHAR(50),
    cidade VARCHAR(50),
    estado VARCHAR(02),
    salario INTEGER,
    idade INTEGER, 
    CONSTRAINT PK_CPFcliente PRIMARY KEY (cpf)
);

CREATE TABLE veiculos (
    chassi VARCHAR(50) NOT NULL,
    placa VARCHAR(10) NOT NULL, 
    cor VARCHAR(20),
    modelo VARCHAR(20),
    marca VARCHAR(20),
    ano_fabricacao INTEGER,
    preco_compra INTEGER,
    preco_venda INTEGER,
    motor NUMERIC(3,1), -- Ajustado de INTEGER para NUMERIC para suportar valores decimais (ex: 1.8, 2.0) dos INSERTS
    cpf_cli BIGINT NOT NULL, 
    CONSTRAINT pk_placa PRIMARY KEY (placa)
);

-- Definição da Chave Estrangeira
ALTER TABLE veiculos ADD CONSTRAINT fk_cpf_cli FOREIGN KEY (cpf_cli) REFERENCES clientes (cpf);

-- ------------------------------------------------------------------------------
-- 3. INSERÇÃO DOS DADOS (DML)
-- ------------------------------------------------------------------------------
-- Carga de Clientes
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (01, 11, 'BEATRIZ', 'RUA 01', 'FERNANDOPOLIS', 'SP', 1800, 32);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (02, 12, 'JOANA', 'RUA 031', 'SAO JOSE DO RIO PRETO', 'SP', 2000, 40);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (03, 13, 'LUANA', 'RUA 051', 'VOTUPORANGA', 'SP', 3500, 41);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (04, 114, 'KATIA', 'RUA 015', 'JALES', 'SP', 7000, 32);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (05, 115, 'AMANDA', 'RUA 061', 'FERNANDOPOLIS', 'SP', 5500, 40);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (06, 116, 'GIOVANA', 'RUA 021', 'SAO JOSE DO RIO PRETO', 'SP', 2000, 34);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (07, 117, 'TAIZ', 'RUA 011', 'MACEDONIA', 'SP', 1500, 32);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (08, 181, 'MILENA', 'RUA 013', 'SANTA FE DO SUL', 'SP', 2300, 42);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (09, 911, 'ELIZANGELA', 'RUA 015', 'CATANDUVA', 'SP', 2800, 62);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (010, 101, 'PATRACIA', 'RUA 012', 'ARAGUAINA', 'TO', 8800, 22);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (011, 131, 'MARIA', 'RUA 014', 'PALMAS', 'TO', 9000, 23);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (012, 141, 'FELISBINA', 'RUA 015', 'DOURADO', 'MS', 3100, 32);

-- Carga de Veículos
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM02', 'ESC2033', 'BRANCO', 'H