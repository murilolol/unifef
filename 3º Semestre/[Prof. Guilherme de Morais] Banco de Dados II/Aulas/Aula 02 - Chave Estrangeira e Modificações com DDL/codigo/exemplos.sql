-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- PROFESSOR : Prof. Guilherme de Morais
-- TEMA      : Chave Estrangeira e Modificações com DDL
-- EXECUÇÃO  : Compatível com PostgreSQL / padrão SQL ANSI.
-- ============================================================================

-- Limpeza preventiva de tabelas anteriores para idempotência
DROP TABLE IF EXISTS PEDIDOS CASCADE;
DROP TABLE IF EXISTS CLIENTES CASCADE;
DROP TABLE IF EXISTS PRODUTO CASCADE;
DROP TABLE IF EXISTS LOTE CASCADE;
DROP TABLE IF EXISTS FUNCIONARIO CASCADE;
DROP TABLE IF EXISTS DEPARTAMENTO CASCADE;
DROP TABLE IF EXISTS equipamentos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;

-- ----------------------------------------------------------------------------
-- 1. CHAVE ESTRANGEIRA DEFINIDA NO CREATE TABLE (SLIDES 3 E 4)
-- Relacionamento: FUNCIONARIO (1..1) TRABALHA EM DEPARTAMENTO (1..N)
-- ----------------------------------------------------------------------------

CREATE TABLE DEPARTAMENTO (
    COD_DEP INTEGER NOT NULL,
    NOME VARCHAR(50),
    CONSTRAINT PK_COD_DEP PRIMARY KEY (COD_DEP)
);

CREATE TABLE FUNCIONARIO (
    CPF INTEGER NOT NULL,
    NOME VARCHAR(50),
    CIDADE VARCHAR(50),
    COD_DEP INTEGER,
    CONSTRAINT PK_CPF PRIMARY KEY (CPF),
    CONSTRAINT FK_COD_DEP FOREIGN KEY (COD_DEP) REFERENCES DEPARTAMENTO (COD_DEP)
);

-- Dados de teste para validação da integridade referencial
INSERT INTO DEPARTAMENTO (COD_DEP, NOME) VALUES (10, 'Tecnologia da Informação');
INSERT INTO DEPARTAMENTO (COD_DEP, NOME) VALUES (20, 'Recursos Humanos');

INSERT INTO FUNCIONARIO (CPF, NOME, CIDADE, COD_DEP) VALUES (111222333, 'Carlos Silva', 'Fernandópolis', 10);
INSERT INTO FUNCIONARIO (CPF, NOME, CIDADE, COD_DEP) VALUES (444555666, 'Maria Oliveira', 'Jales', 20);

-- ----------------------------------------------------------------------------
-- 2. CHAVE ESTRANGEIRA ADICIONADA COM ALTER TABLE (SLIDES 6 E 7)
-- Relacionamento: PRODUTO (1..1) CONTÉM LOTE (0..N)
-- ----------------------------------------------------------------------------

CREATE TABLE PRODUTO (
    COD_PRODUTO INTEGER NOT NULL,
    NOME VARCHAR(50),
    DESCRICAO VARCHAR(50),
    COD_LOTE INTEGER,
    CONSTRAINT PK_COD_PRODUTO PRIMARY KEY (COD_PRODUTO)
);

CREATE TABLE LOTE (
    COD_LOTE INTEGER NOT NULL,
    NOME_LOTE VARCHAR(50),
    CONSTRAINT PK_COD_LOTE PRIMARY KEY (COD_LOTE)
);

-- Adição de Chave Estrangeira em tabela já existente
ALTER TABLE PRODUTO ADD CONSTRAINT FK_COD_LOTE1 FOREIGN KEY (COD_LOTE) REFERENCES LOTE (COD_LOTE);

-- Teste de inserção
INSERT INTO LOTE (COD_LOTE, NOME_LOTE) VALUES (101, 'Lote Alfa 2026');
INSERT INTO PRODUTO (COD_PRODUTO, NOME, DESCRICAO, COD_LOTE) VALUES (1, 'Teclado Mecânico', 'Switch Azul ABNT2', 101);

-- ----------------------------------------------------------------------------
-- 3. COMANDOS DE ALTERAÇÃO ESTRUTURAL DDL (SLIDES 8 A 10 E SLIDES 2 A 5)
-- ----------------------------------------------------------------------------

CREATE TABLE produtos (
    cod_prod INTEGER NOT NULL,
    nome VARCHAR(50)
);

-- Slide 8 (Aula 2) e Slide 2 (Modificações): Adicionar coluna
ALTER TABLE produtos ADD COLUMN descricao text;

-- Slide 4 (Modificações): Renomear coluna
ALTER TABLE produtos RENAME COLUMN cod_prod TO cod_produto;

-- Slide 3 (Modificações) e Slide 9 (Aula 2): Excluir coluna
ALTER TABLE produtos DROP COLUMN descricao;

-- Slide 5 (Modificações): Mudar nome de tabela
ALTER TABLE produtos RENAME TO equipamentos;

-- ----------------------------------------------------------------------------
-- 4. ADIÇÃO DE PK E FK COM ALTER TABLE (MODIFICAÇÕES SLIDES 6 E 7)
-- ----------------------------------------------------------------------------

CREATE TABLE CLIENTES (
    COD INTEGER NOT NULL,
    NOME VARCHAR(50)
);

CREATE TABLE PEDIDOS (
    NUMERO INTEGER NOT NULL,
    VALOR NUMERIC(10,2),
    COD_CLI INTEGER
);

-- Slide 6: Adicionar Chave Primária com ALTER TABLE
ALTER TABLE CLIENTES ADD CONSTRAINT PK_CLIENTES PRIMARY KEY (COD);

-- Slide 7: Adicionar Chave Estrangeira com ALTER TABLE
ALTER TABLE PEDIDOS ADD CONSTRAINT FK_COD_CLI FOREIGN KEY (COD_CLI) REFERENCES CLIENTES(COD);

-- Slide 10 (Aula 2): Excluir uma constraint de uma tabela (exemplo demonstrativo)
-- ALTER TABLE PEDIDOS DROP CONSTRAINT FK_COD_CLI;
