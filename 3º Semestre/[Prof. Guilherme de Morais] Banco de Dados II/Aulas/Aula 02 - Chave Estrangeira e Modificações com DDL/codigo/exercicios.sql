-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- PROFESSOR : Prof. Guilherme de Morais
-- TEMA      : Resolução dos Exercícios de Chave Estrangeira e DDL
-- EXECUÇÃO  : Compatível com PostgreSQL / padrão SQL ANSI.
-- ============================================================================

-- Limpeza inicial para execução idempotente
DROP TABLE IF EXISTS LIVRO CASCADE;
DROP TABLE IF EXISTS AUTOR CASCADE;
DROP TABLE IF EXISTS PRODUTO_EX1 CASCADE;
DROP TABLE IF EXISTS CATEGORIA CASCADE;
DROP TABLE IF EXISTS CLIENTE CASCADE;
DROP TABLE IF EXISTS FORNECEDORES CASCADE;
DROP TABLE IF EXISTS FORNECEDOR_TEMP CASCADE;

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 1: Integridade Referencial na Criação de Tabelas
-- ----------------------------------------------------------------------------

CREATE TABLE CATEGORIA (
    COD_CATEGORIA INTEGER NOT NULL,
    NOME_CATEGORIA VARCHAR(50) NOT NULL,
    CONSTRAINT PK_CATEGORIA PRIMARY KEY (COD_CATEGORIA)
);

CREATE TABLE PRODUTO_EX1 (
    COD_PRODUTO INTEGER NOT NULL,
    NOME VARCHAR(50) NOT NULL,
    VALOR NUMERIC(10,2),
    COD_CATEGORIA INTEGER,
    CONSTRAINT PK_PRODUTO_EX1 PRIMARY KEY (COD_PRODUTO),
    CONSTRAINT FK_PRODUTO_CATEGORIA FOREIGN KEY (COD_CATEGORIA) REFERENCES CATEGORIA (COD_CATEGORIA)
);

-- Teste de inserção e verificação da integridade referencial
INSERT INTO CATEGORIA (COD_CATEGORIA, NOME_CATEGORIA) VALUES (1, 'Periféricos');
INSERT INTO PRODUTO_EX1 (COD_PRODUTO, NOME, VALOR, COD_CATEGORIA) VALUES (100, 'Mouse Gamer', 150.00, 1);

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 2: Associação Posterior com ALTER TABLE (PKs e FKs)
-- ----------------------------------------------------------------------------

-- Criação sem constraints
CREATE TABLE AUTOR (
    COD_AUTOR INTEGER NOT NULL,
    NOME VARCHAR(50)
);

CREATE TABLE LIVRO (
    COD_LIVRO INTEGER NOT NULL,
    TITULO VARCHAR(100),
    COD_AUTOR INTEGER
);

-- (a) Adição da Primary Key na tabela AUTOR
ALTER TABLE AUTOR ADD CONSTRAINT PK_AUTOR PRIMARY KEY (COD_AUTOR);

-- (b) Adição da Primary Key na tabela LIVRO
ALTER TABLE LIVRO ADD CONSTRAINT PK_LIVRO PRIMARY KEY (COD_LIVRO);

-- (c) Adição da Foreign Key na tabela LIVRO referenciando AUTOR
ALTER TABLE LIVRO ADD CONSTRAINT FK_LIVRO_AUTOR FOREIGN KEY (COD_AUTOR) REFERENCES AUTOR (COD_AUTOR);

-- Teste de validação
INSERT INTO AUTOR (COD_AUTOR, NOME) VALUES (1, 'Machado de Assis');
INSERT INTO LIVRO (COD_LIVRO, TITULO, COD_AUTOR) VALUES (10, 'Dom Casmurro', 1);

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 3: Evolução de Esquema com Adição e Remoção de Colunas
-- ----------------------------------------------------------------------------

CREATE TABLE CLIENTE (
    COD_CLI INTEGER NOT NULL,
    NOME VARCHAR(50),
    CONSTRAINT PK_CLIENTE PRIMARY KEY (COD_CLI)
);

-- (a) Adicionar coluna EMAIL
ALTER TABLE CLIENTE ADD COLUMN EMAIL VARCHAR(80);

-- (b) Adicionar coluna TELEFONE_RECADO
ALTER TABLE CLIENTE ADD COLUMN TELEFONE_RECADO VARCHAR(20);

-- (c) Remover coluna TELEFONE_RECADO com DROP COLUMN
ALTER TABLE CLIENTE DROP COLUMN TELEFONE_RECADO;

-- Teste de validação após alteração da estrutura
INSERT INTO CLIENTE (COD_CLI, NOME, EMAIL) VALUES (1, 'Ana Clara', 'ana@unifef.edu.br');

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 4: Refatoração de Nomes de Coluna e Tabela
-- ----------------------------------------------------------------------------

CREATE TABLE FORNECEDOR_TEMP (
    COD INTEGER NOT NULL,
    FANTASIA VARCHAR(50),
    CONSTRAINT PK_FORNECEDOR_TEMP PRIMARY KEY (COD)
);

-- (a) Renomear coluna FANTASIA para NOME_FANTASIA
ALTER TABLE FORNECEDOR_TEMP RENAME COLUMN FANTASIA TO NOME_FANTASIA;

-- (b) Renomear a tabela FORNECEDOR_TEMP para FORNECEDORES
ALTER TABLE FORNECEDOR_TEMP RENAME TO FORNECEDORES;

-- Teste de validação na tabela renomeada
INSERT INTO FORNECEDORES (COD, NOME_FANTASIA) VALUES (500, 'Distribuidora Central');
