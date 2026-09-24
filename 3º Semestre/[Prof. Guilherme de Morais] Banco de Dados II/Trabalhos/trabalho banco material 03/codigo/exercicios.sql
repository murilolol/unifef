-- ============================================================================
-- DISCIPLINA : Banco de Dados II (3º Semestre) - UniFEF
-- PROFESSOR  : Prof. Guilherme de Morais
-- TEMA       : Manipulação de Dados (DML) - INSERT, UPDATE e DELETE
-- ARQUIVO    : exercicios.sql
-- COMO EXECUTAR:
--   Execute o arquivo diretamente em seu SGBD ou execute bloco a bloco.
--   Exemplo psql: psql -U postgres -d seu_banco -f exercicios.sql
-- ============================================================================

-- ----------------------------------------------------------------------------
-- PREPARAÇÃO DO AMBIENTE (Idempotência)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS FUNCIONARIO CASCADE;

CREATE TABLE FUNCIONARIO (
    cpf INTEGER NOT NULL,
    nome VARCHAR(50),
    funcao VARCHAR(30),
    salario DECIMAL(10,2),
    data_nasc DATE,
    departamento VARCHAR(30),
    CONSTRAINT pk_fun_cpf PRIMARY KEY (cpf)
);

-- ============================================================================
-- SEÇÃO 1: EXERCÍCIOS – INSERT
-- ============================================================================

-- Exercício 1
-- Insira um funcionário com todos os campos preenchidos.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES (101, 'Carlos Eduardo Silva', 'ANALISTA DE SUPORTE', 4500.00, '1988-05-14', 'TI');

-- Exercício 2
-- Insira três funcionários em departamentos diferentes utilizando a sintaxe com declaração explícita das colunas.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) VALUES
(102, 'Mariana Costa Ramos', 'ANALISTA DE RH', 4800.00, '1990-11-20', 'RH'),
(103, 'Rodrigo Alves Santos', 'CONTADOR SENIOR', 7200.00, '1982-03-10', 'FINANCEIRO'),
(104, 'Fernanda Gomes Lima', 'COMPRADORA', 5100.00, '1993-08-25', 'COMPRAS');

-- Exercício 3
-- Insira um funcionário informando apenas cpf, nome e data_nasc.
-- Os campos funcao, salario e departamento assumirão valor NULL por padrão.
INSERT INTO FUNCIONARIO (cpf, nome, data_nasc)
VALUES (105, 'Lucas Menezes Pires', '1995-07-19');

-- Exercício 4
-- Cadastre um funcionário cujo salário seja 8750.90 e função “ANALISTA DE SISTEMAS”.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES (106, 'Beatriz Souza Martins', 'ANALISTA DE SISTEMAS', 8750.90, '1986-09-02', 'TI');

-- Exercício 5
-- Insira dois funcionários com a mesma função, porém salários diferentes.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) VALUES
(107, 'Patricia Nogueira', 'DESIGNER UI/UX', 4200.00, '1992-04-12', 'MARKETING'),
(108, 'Gabriel Dias Rocha', 'DESIGNER UI/UX', 5900.00, '1989-12-30', 'MARKETING');

-- Exercício 6
-- Cadastre cinco funcionários do departamento “TI”.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) VALUES
(109, 'Alexandre Torres', 'DESENVOLVEDOR JUNIOR', 3800.00, '1997-01-18', 'TI'),
(110, 'Renata Vasconcelos', 'DESENVOLVEDOR PLENO', 6200.00, '1991-06-22', 'TI'),
(111, 'Danilo Faria Pinto', 'DBA', 8400.00, '1976-10-15', 'TI'),
(112, 'Tatiane Antunes', 'ENGENHEIRA DE DADOS', 8900.00, '1984-02-28', 'TI'),
(113, 'Igor Cavalcanti', 'ESTAGIARIO DE DEV', 1350.00, '2002-12-05', 'TI');

-- Exercício 7
-- Insira um registro omitindo propositalmente o campo funcao.
INSERT INTO FUNCIONARIO (cpf, nome, salario, data_nasc, departamento)
VALUES (114, 'Vanessa Helena Ribeiro', 4100.00, '1994-03-17', 'OPERACOES');

-- Registros auxiliares para garantir a cobertura completa dos cenários de teste subsequentes:
-- Funcionário do RH com data anterior a 1980:
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES (115, 'Antonio Celso Prado', 'GERENTE DE PESSOAL', 9500.00, '1972-04-08', 'RH');

-- Funcionário com salário abaixo de 1500:
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES (116, 'Juliana Morais Neto', 'AUXILIAR DE SERVICOS', 1420.00, '1998-10-11', 'ADMINISTRATIVO');

-- Funcionário com funcao preenchida com string vazia proposital:
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES (117, 'Moacir Guimaraes', '', 3100.00, '1979-05-23', 'LOGISTICA');

-- Visualização do estado da tabela após as inserções
SELECT * FROM FUNCIONARIO ORDER BY cpf;

-- ============================================================================
-- SEÇÃO 2: EXERCÍCIOS – UPDATE
-- ============================================================================

-- Exercício 8
-- Atualize o salário de um funcionário específico pelo CPF.
UPDATE FUNCIONARIO
SET salario = 4950.00
WHERE cpf = 101;

-- Exercício 9
-- Atualize a função de todos os funcionários do departamento “TI” para “DESENVOLVEDOR”.
UPDATE FUNCIONARIO
SET funcao = 'DESENVOLVEDOR'
WHERE departamento = 'TI';

-- Exercício 10
-- Aumente em 10% o salário de todos os funcionários.
-- Multiplicação por 1.10 preserva a precisão com DECIMAL(10,2).
UPDATE FUNCIONARIO
SET salario = salario * 1.10;

-- Exercício 11
-- Altere o departamento de um funcionário específico.
UPDATE FUNCIONARIO
SET departamento = 'INOVACAO E PROJETOS'
WHERE cpf = 106;

-- Exercício 12
-- Atualize a função e o salário simultaneamente de um funcionário.
UPDATE FUNCIONARIO
SET funcao = 'LIDER TECNICO',
    salario = 11500.00
WHERE cpf = 106;

-- Exercício 13
-- Defina o salário como 0 para funcionários que não possuem salário cadastrado.
-- Avaliação de nulos requer a sintaxe IS NULL (o operador = NULL resultaria em falso/desconhecido).
UPDATE FUNCIONARIO
SET salario = 0.00
WHERE salario IS NULL;

-- Exercício 14
-- Altere a função para “GERENTE” apenas para funcionários com salário superior a 8000.
UPDATE FUNCIONARIO
SET funcao = 'GERENTE'
WHERE salario > 8000.00;

-- Visualização do estado da tabela após os updates
SELECT * FROM FUNCIONARIO ORDER BY cpf;

-- ============================================================================
-- SEÇÃO 3: EXERCÍCIOS – DELETE
-- ============================================================================

-- Exercício 15
-- Exclua um funcionário específico pelo CPF.
DELETE FROM FUNCIONARIO
WHERE cpf = 105;

-- Exercício 16
-- Exclua todos os funcionários do departamento “RH”.
DELETE FROM FUNCIONARIO
WHERE departamento = 'RH';

-- Exercício 17
-- Remova funcionários com salário inferior a 1500.
DELETE FROM FUNCIONARIO
WHERE salario < 1500.00;

-- Exercício 18
-- Apague os registros de funcionários cuja função esteja vazia.
-- Abrange tanto a ausência de valor (NULL) quanto texto em branco/vazio ('').
DELETE FROM FUNCIONARIO
WHERE funcao IS NULL OR TRIM(funcao) = '';

-- Exercício 19
-- Exclua todos os funcionários nascidos antes de 1980.
DELETE FROM FUNCIONARIO
WHERE data_nasc < '1980-01-01';

-- Visualização dos registros remanescentes antes da limpeza total
SELECT * FROM FUNCIONARIO ORDER BY cpf;

-- Exercício 20
-- Apague todos os registros da tabela.
-- Execução do DELETE sem cláusula WHERE remove todas as tuplas preservando a estrutura da tabela.
DELETE FROM FUNCIONARIO;

-- Verificação final: a tabela deve retornar 0 tuplas
SELECT COUNT(*) AS total_registros_apos_limpeza FROM FUNCIONARIO;
