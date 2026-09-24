/*
 * UniFEF - Centro Universitário de Santa Fé do Sul
 * Curso: Sistemas de Informação
 * Disciplina: Banco de Dados II (3º Semestre)
 * Professor: Prof. Guilherme de Morais
 * Tema: Resolução dos Exercícios das Aulas 03, 04 e 05
 *
 * Como executar:
 *   No terminal: psql -U postgres -d seu_banco -f exercicios.sql
 */

-- ============================================================================
-- 0. AMBIENTE IDEMPOTENTE: CRIAÇÃO E POVOAMENTO DAS TABELAS
-- ============================================================================
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS alu_aluno CASCADE;
DROP TABLE IF EXISTS empregado CASCADE;

CREATE TABLE produto (
    codigo_produto INTEGER PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    unidade VARCHAR(10) NOT NULL,
    val_unit NUMERIC(10, 2) NOT NULL
);

CREATE TABLE alu_aluno (
    matricula INTEGER PRIMARY KEY,
    nome_aluno VARCHAR(100) NOT NULL,
    tel_aluno VARCHAR(20),
    data_nasc_aluno DATE NOT NULL,
    cidade_aluno VARCHAR(60)
);

CREATE TABLE empregado (
    id_emp SERIAL PRIMARY KEY,
    pnome VARCHAR(50) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    salario NUMERIC(10, 2) NOT NULL
);

-- Carga de dados de teste para execução imediata
INSERT INTO produto (codigo_produto, descricao, unidade, val_unit) VALUES
(1, 'Arame Galvanizado', 'M', 0.11),
(2, 'Cabo de Cobre 4mm', 'M', 1.80),
(3, 'Cordoalha de Aço', 'M', 2.00),
(4, 'Mangueira Corrugada', 'M', 0.85),
(5, 'Parafuso Rosca Soberba', 'UN', 0.11),
(6, 'Eletroduto Flexível', 'M', 1.50);

INSERT INTO alu_aluno (matricula, nome_aluno, tel_aluno, data_nasc_aluno, cidade_aluno) VALUES
(101, 'Ana Paula Silva', '17991110001', '1980-02-01', 'Santa Fé do Sul'),
(102, 'Bruno Cesar Santos', '17991110002', '1985-06-15', 'Jales'),
(103, 'Carla Cristina Dias', '17991110003', '1990-10-30', 'Votuporanga'),
(104, 'Daniel de Oliveira', '17991110004', '1978-12-25', 'São José do Rio Preto'),
(105, 'Eduardo Ferraz', '17991110005', '1995-04-10', 'Fernandópolis');

INSERT INTO empregado (pnome, cargo, salario) VALUES
('Antonio', 'Gerente', 8500.00),
('Amanda', 'Analista', 5200.00),
('Carlos', 'Desenvolvedor', 4800.00),
('alice', 'Assistente', 3100.00),
('SEBASTIAO', 'Gerente', 9100.00);


-- ============================================================================
-- EXERCÍCIO 1
-- Enunciado: Encontre a descrição, unidade e o valor unitário dos produtos de
--            unidade 'M' e cujos valores unitários sejam 0.11 ou 1.8 ou 2.
-- Referência: Aula 04 - Slide 16
-- ============================================================================
SELECT unidade, descricao, val_unit
FROM produto
WHERE unidade = 'M'
  AND (val_unit = 0.11 OR val_unit = 1.8 OR val_unit = 2);


-- ============================================================================
-- EXERCÍCIO 2
-- Enunciado: Selecione o nome e cargo de todos os empregados que possuem uma
--            letra 'a' no nome, utilizando o operador ILIKE para demonstrar
--            o comportamento case-insensitive.
-- Referência: Aula 05 - Slide 5
-- ============================================================================
-- Utilizando LIKE convencional (retorna apenas minúsculos em PostgreSQL):
SELECT pnome, cargo
FROM empregado
WHERE pnome LIKE '%a%';

-- Resolução com ILIKE (retorna registros com 'a' ou 'A', como 'Antonio', 'alice' e 'SEBASTIAO'):
SELECT pnome, cargo
FROM empregado
WHERE pnome ILIKE '%a%';


-- ============================================================================
-- EXERCÍCIO 3
-- Enunciado: Simule um aumento de 25% nos produtos. Projetar a descrição, a
--            unidade, o preço atual e o preço recalculado com aumento.
-- Referência: Aula 04 - Slide 19
-- ============================================================================
SELECT descricao,
       unidade,
       val_unit AS "Preço Atual",
       val_unit * 1.25 AS "Preço com Aumento"
FROM produto;


-- ============================================================================
-- EXERCÍCIO 4
-- Enunciado: Simule um desconto de 12% nos produtos cuja unidade seja 'M'.
--            Exibir descrição, unidade, valor unitário atual e o valor com desconto.
-- Referência: Aula 04 - Slide 19
-- ============================================================================
SELECT descricao,
       unidade,
       val_unit AS "Preço Atual",
       val_unit - (val_unit * 0.12) AS "Preço com Desconto"
FROM produto
WHERE unidade = 'M';


-- ============================================================================
-- EXERCÍCIO 5
-- Enunciado: Listar o código, descrição e o valor unitário dos produtos que
--            tenham o valor unitário na faixa de R$ 0.32 até R$ 2.00.
-- Referência: Aula 04 - Slide 21
-- ============================================================================
SELECT codigo_produto, descricao, val_unit
FROM produto
WHERE val_unit BETWEEN 0.32 AND 2.00;


-- ============================================================================
-- EXERCÍCIO 6
-- Enunciado: Encontre o RA (matrícula), nome e data de nascimento dos alunos
--            nascidos no intervalo de 01/02/1980 a 30/10/1990.
-- Referência: Aula 04 - Slide 21
-- ============================================================================
SELECT matricula, nome_aluno, data_nasc_aluno
FROM alu_aluno
WHERE data_nasc_aluno BETWEEN '1980-02-01' AND '1990-10-30';
