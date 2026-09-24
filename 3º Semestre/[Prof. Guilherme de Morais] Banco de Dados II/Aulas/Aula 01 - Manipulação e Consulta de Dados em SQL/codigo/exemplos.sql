/*
 * UniFEF - Centro Universitário de Santa Fé do Sul
 * Curso: Sistemas de Informação
 * Disciplina: Banco de Dados II (3º Semestre)
 * Professor: Prof. Guilherme de Morais
 * Tema: Manipulação e Consulta de Dados em SQL (Aulas 03, 04 e 05)
 *
 * Como executar:
 *   Compatível com PostgreSQL (ou adaptável para MySQL/MariaDB).
 *   No terminal: psql -U postgres -d seu_banco -f exemplos.sql
 */

-- ============================================================================
-- 0. AMBIENTE IDEMPOTENTE: LIMPEZA DE TABELAS
-- ============================================================================
DROP TABLE IF EXISTS pedido CASCADE;
DROP TABLE IF EXISTS alu_instrutor CASCADE;
DROP TABLE IF EXISTS alu_aluno CASCADE;
DROP TABLE IF EXISTS vendedor CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS empregado CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS funcionario CASCADE;

-- ============================================================================
-- 1. ESTRUTURAS DDL E CARGA INICIAL PARA OS EXEMPLOS
-- ============================================================================

-- Tabela de funcionários (Aula 03)
CREATE TABLE funcionario (
    cpf INTEGER NOT NULL,
    nome VARCHAR(50),
    funcao VARCHAR(20),
    data_nasc DATE,
    CONSTRAINT pk_fun_cpf PRIMARY KEY (cpf)
);

-- Tabela de clientes (Aula 04)
CREATE TABLE clientes (
    codcliente INTEGER PRIMARY KEY,
    nomecliente VARCHAR(100) NOT NULL,
    endcliente VARCHAR(150),
    idade INTEGER,
    cidade VARCHAR(60),
    estado CHAR(2)
);

-- Tabela de produtos (Aula 04)
CREATE TABLE produto (
    codigo_produto INTEGER PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    unidade VARCHAR(10) NOT NULL,
    val_unit NUMERIC(10, 2) NOT NULL
);

-- Tabela de vendedores (Aula 04)
CREATE TABLE vendedor (
    codigo_vendedor INTEGER PRIMARY KEY,
    nome_vendedor VARCHAR(100) NOT NULL,
    salario_fixo NUMERIC(10, 2) NOT NULL,
    faixa_comissao VARCHAR(10)
);

-- Tabela de alunos (Aula 04)
CREATE TABLE alu_aluno (
    matricula INTEGER PRIMARY KEY,
    nome_aluno VARCHAR(100) NOT NULL,
    tel_aluno VARCHAR(20),
    data_nasc_aluno DATE NOT NULL,
    cidade_aluno VARCHAR(60)
);

-- Tabela de instrutores (Aula 04)
CREATE TABLE alu_instrutor (
    cod_instrut INTEGER PRIMARY KEY,
    nome_instrut VARCHAR(100) NOT NULL,
    data_admissao_instrut DATE NOT NULL
);

-- Tabela de pedidos (Aula 04)
CREATE TABLE pedido (
    num_pedido INTEGER PRIMARY KEY,
    codigo_vendedor INTEGER REFERENCES vendedor(codigo_vendedor),
    data_pedido DATE NOT NULL,
    valor_total NUMERIC(10, 2)
);

-- Tabela de empregados (Aula 05)
CREATE TABLE empregado (
    id_emp SERIAL PRIMARY KEY,
    pnome VARCHAR(50) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    salario NUMERIC(10, 2) NOT NULL
);

-- ============================================================================
-- 2. AULA 03: MANIPULAÇÃO DE DADOS (INSERT, UPDATE, DELETE)
-- ============================================================================

-- 2.1 Inserções com especificação explícita de colunas
INSERT INTO funcionario (cpf, nome, funcao, data_nasc)
VALUES (2, 'SEBASTIAO', 'PROFESSOR', '2020-03-03');

-- 2.2 Inserção omitindo a lista de campos (requer todos os valores na ordem da tabela)
INSERT INTO funcionario
VALUES (3, 'PEDRO', 'SECRETARIO', '1963-08-20');

-- 2.3 Inserção com colunas parciais (campos omitidos assumem valor NULL ou DEFAULT)
INSERT INTO funcionario (cpf, nome, data_nasc)
VALUES (4, 'RAUL', '2020-03-08');

-- 2.4 Inserção de registro passando valor vazio explicitamente
INSERT INTO funcionario (cpf, nome, funcao, data_nasc)
VALUES (5, 'MARIA', '', '2020-03-03');

-- 2.5 Atualização de dados com UPDATE condicional (WHERE)
UPDATE funcionario
SET funcao = 'DIRETOR'
WHERE cpf = 4;

-- 2.6 Remoção de registros com DELETE condicional
DELETE FROM funcionario
WHERE cpf = 5;

-- Consulta de validação da tabela funcionario
SELECT * FROM funcionario;


-- ============================================================================
-- 3. AULA 04: CONSULTAS BÁSICAS, ORDENAÇÃO, FILTROS E OPERADORES
-- ============================================================================

-- Carga de dados para a tabela clientes
INSERT INTO clientes (codcliente, nomecliente, endcliente, idade, cidade, estado)
VALUES
(1, 'Carlos Silva', 'Rua das Flores, 123', 28, 'Campinas', 'SP'),
(2, 'Ana Souza', 'Av. Central, 500', 35, 'São Paulo', 'SP'),
(3, 'Bruna Lima', 'Rua do Sol, 88', 22, 'Rio de Janeiro', 'RJ'),
(4, 'Carlos Silva', 'Rua das Flores, 123', 28, 'Campinas', 'SP'), -- Registro duplicado para teste de DISTINCT
(5, 'Diego Martins', 'Av. Brasil, 1020', 41, 'Santos', 'SP');

-- Carga de dados para a tabela produto
INSERT INTO produto (codigo_produto, descricao, unidade, val_unit)
VALUES
(10, 'Parafuso Sextavado', 'UN', 0.45),
(20, 'Cabo Flexível 2.5mm', 'M', 1.80),
(30, 'Tubo PVC 100mm', 'M', 2.00),
(40, 'Porca de Aço', 'UN', 0.11),
(50, 'Arame Galvanizado', 'M', 0.11),
(60, 'Fita Isolante', 'RL', 3.50);

-- Carga de dados para a tabela vendedor
INSERT INTO vendedor (codigo_vendedor, nome_vendedor, salario_fixo, faixa_comissao)
VALUES
(101, 'João da Silva', 2780.00, 'A'),
(102, 'Mariana Costa', 4600.00, 'B'),
(103, 'Roberto Dias', 2100.00, 'A'),
(104, 'Fernanda Alves', 3500.00, 'C');

-- Carga de dados para a tabela alu_aluno
INSERT INTO alu_aluno (matricula, nome_aluno, tel_aluno, data_nasc_aluno, cidade_aluno)
VALUES
(1001, 'Marcos Souza', '19991112233', '1982-05-14', 'Campinas'),
(1002, 'Julia Ribeiro', '11988887766', '1989-11-20', 'São Paulo'),
(1003, 'Lucas Prado', '19977776655', '1979-01-10', 'Campinas');

-- Carga de dados para a tabela alu_instrutor
INSERT INTO alu_instrutor (cod_instrut, nome_instrut, data_admissao_instrut)
VALUES
(501, 'Prof. Sergio', '1995-02-10'),
(502, 'Profa. Claudia', '1998-08-15');

-- Carga de dados para a tabela pedido
INSERT INTO pedido (num_pedido, codigo_vendedor, data_pedido, valor_total)
VALUES
(10001, 101, '2026-03-01', 1500.00),
(10002, 102, '2026-03-02', 3200.00);

-- 3.1 Projeção total e específica
SELECT * FROM clientes;
SELECT codcliente, nomecliente, endcliente FROM clientes;

-- 3.2 Ordenação simples e múltipla com ORDER BY
SELECT * FROM clientes ORDER BY nomecliente;
SELECT codcliente, nomecliente, idade FROM clientes ORDER BY nomecliente, idade;

-- 3.3 Filtragem simples por predicado relacional
SELECT * FROM clientes WHERE estado = 'SP';
SELECT codcliente, nomecliente, endcliente FROM clientes WHERE estado = 'SP';

-- 3.4 Filtragem combinada com ORDER BY
SELECT * FROM clientes WHERE estado = 'SP' ORDER BY nomecliente;
SELECT codcliente, nomecliente, endcliente FROM clientes WHERE estado = 'SP' ORDER BY nomecliente;

-- 3.5 Operadores de comparação com valores numéricos e datas
SELECT descricao FROM produto WHERE val_unit >= 1.00;
SELECT nome_aluno, data_nasc_aluno FROM alu_aluno WHERE data_nasc_aluno >= '1980-01-30';
SELECT * FROM pedido WHERE codigo_vendedor = 101;
SELECT cod_instrut, nome_instrut FROM alu_instrutor WHERE data_admissao_instrut >= '1997-01-15';

-- 3.6 Operador lógico AND
SELECT descricao FROM produto WHERE val_unit >= 0.50 AND val_unit <= 2.00;
SELECT nome_aluno, tel_aluno FROM alu_aluno WHERE data_nasc_aluno >= '1981-02-24' AND cidade_aluno = 'Campinas';
SELECT nomecliente FROM clientes WHERE cidade = 'Campinas' AND cidade = 'São Paulo'; -- Demonstração de condição mutuamente exclusiva (retorna vazio)

-- 3.7 Operador lógico OR
SELECT codigo_vendedor, nome_vendedor, salario_fixo, faixa_comissao
FROM vendedor
WHERE salario_fixo = 2780 OR salario_fixo = 4600;

-- 3.8 Remoção de tuplas redundantes com DISTINCT
SELECT DISTINCT nomecliente FROM clientes;

-- 3.9 Expressões aritméticas e pseudônimos de coluna (AS)
SELECT salario_fixo * 1.1 AS salario_com_reajuste FROM vendedor;

-- 3.10 Operador de intervalo BETWEEN
SELECT * FROM vendedor WHERE salario_fixo BETWEEN 2000 AND 3000;
-- Nota técnica: BETWEEN 3000 AND 2000 não retorna tuplas porque a semântica exige limite inferior primeiro
SELECT * FROM vendedor WHERE salario_fixo BETWEEN 3000 AND 2000;


-- ============================================================================
-- 4. AULA 05: OPERADOR LIKE/ILIKE E FUNÇÕES AGREGADAS
-- ============================================================================

-- Carga de dados para a tabela empregado
INSERT INTO empregado (pnome, cargo, salario)
VALUES
('Antonio', 'Gerente', 8500.00),
('Amanda', 'Analista', 5200.00),
('Carlos', 'Desenvolvedor', 4800.00),
('Sebastiao', 'Gerente', 9100.00),
('alice', 'Assistente', 3100.00), -- Nome em minúsculo para demonstrar case-sensitivity
('Alan', 'Estagiario', 1800.00);

-- 4.1 Operador LIKE (case-sensitive)
-- Empregados cujo primeiro nome inicia com 'A'
SELECT * FROM empregado WHERE pnome LIKE 'A%';

-- Empregados com letra 'a' no meio ou em qualquer posição
SELECT pnome, cargo FROM empregado WHERE pnome LIKE '%a%';

-- 4.2 Operador ILIKE (case-insensitive - extensão padrão PostgreSQL)
SELECT pnome, cargo FROM empregado WHERE pnome ILIKE '%a%';

-- 4.3 Funções agregadas
-- AVG: Média salarial da organização
SELECT avg(salario) AS media_salarial FROM empregado;

-- COUNT: Contagem com filtro de linha
SELECT count(*) AS "Quantidade de Gerente na Empresa"
FROM empregado
WHERE cargo = 'Gerente';

-- MAX e MIN: Maior e menor remuneração
SELECT max(salario) AS maior_salario, min(salario) AS menor_salario
FROM empregado;

-- SUM: Somatório da folha de pagamento
SELECT sum(salario) AS "Soma salarial"
FROM empregado;
