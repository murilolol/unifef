-- ============================================================================
-- DISCIPLINA : Banco de Dados II (3º Semestre) - UniFEF
-- PROFESSOR  : Prof. Guilherme de Morais
-- CONTEÚDO   : Criação da Estrutura Relacional (DDL)
-- ARQUIVO    : schema.sql
-- COMO EXECUTAR:
--   PostgreSQL : psql -U postgres -d seu_banco -f schema.sql
--   MySQL      : mysql -u root -p seu_banco < schema.sql
-- ============================================================================

-- Garantia de idempotência: remove a tabela caso já exista previamente
DROP TABLE IF EXISTS FUNCIONARIO CASCADE;

-- Criação da tabela com os tipos e restrições idênticos ao material da aula
CREATE TABLE FUNCIONARIO (
    cpf INTEGER NOT NULL,
    nome VARCHAR(50),
    funcao VARCHAR(30),
    salario DECIMAL(10,2),
    data_nasc DATE,
    departamento VARCHAR(30),
    CONSTRAINT pk_fun_cpf PRIMARY KEY (cpf)
);

-- Confirmação da estrutura criada
COMMENT ON TABLE FUNCIONARIO IS 'Tabela de funcionários para prática de comandos DML';
