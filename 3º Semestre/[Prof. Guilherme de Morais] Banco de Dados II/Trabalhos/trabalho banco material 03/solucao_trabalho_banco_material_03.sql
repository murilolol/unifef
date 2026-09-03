FILENAME: solucao_trabalho_banco_material_03.sql
---CODE_START---
-- =============================================================================
-- FACULDADE DE SISTEMAS DE INFORMAÇÃO
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- PROFESSOR: Prof. Guilherme de Morais
-- ATIVIDADE: Trabalho Banco Material 03
-- RESOLUÇÃO COMPLETA E COMENTADA (Gabarito Oficial)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 0. CRIAÇÃO DA TABELA (Estrutura fornecida no enunciado)
-- -----------------------------------------------------------------------------
CREATE TABLE FUNCIONARIO (
    cpf INTEGER NOT NULL,
    nome VARCHAR(50),
    funcao VARCHAR(30),
    salario DECIMAL(10,2),
    data_nasc DATE,
    departamento VARCHAR(30),
    CONSTRAINT pk_fun_cpf PRIMARY KEY (cpf)
);

-- =============================================================================
-- 🟢 EXERCÍCIOS – INSERT
-- =============================================================================

-- 1) Insira um funcionário com todos os campos preenchidos.
-- Nota acadêmica: Na inserção implícita (sem declarar colunas), a ordem dos valores 
-- deve seguir exatamente a ordem de definição das colunas na tabela.
INSERT INTO FUNCIONARIO VALUES (
    111111111, 
    'João Silva', 
    'Analista de Suporte', 
    4500.00, 
    '1990-05-15', 
    'Suporte Técnico'
);

-- 2) Insira três funcionários em departamentos diferentes utilizando a sintaxe com declaração explícita das colunas.
-- Nota acadêmica: A declaração explícita de colunas é uma boa prática de engenharia de software, 
-- pois previne erros caso a estrutura da tabela mude no futuro.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) 
VALUES 
(222222222, 'Maria Souza', 'Gerente de Vendas', 8500.00, '1985-08-20', 'Vendas'),
(333333333, 'Pedro Santos', 'Analista de RH', 3800.00, '1995-03-10', 'RH'),
(444444444, 'Ana Oliveira', 'Contadora', 5200.00, '1992-11-12', 'Financeiro');

-- 3) Insira um funcionário informando apenas cpf, nome e data_nasc.
-- Nota acadêmica: Os campos omitidos (funcao, salario, departamento) assumirão o valor NULL, 
-- pois não possuem restrição NOT NULL ou valor DEFAULT definido.
INSERT INTO FUNCIONARIO (cpf, nome, data_nasc) 
VALUES (555555555, 'Carlos Lima', '1988-07-25');

-- 4) Cadastre um funcionário cujo salário seja 8750.90 e função “ANALISTA DE SISTEMAS”.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) 
VALUES (666666666, 'Beatriz Costa', 'ANALISTA DE SISTEMAS', 8750.90, '1991-01-30', 'TI');

-- 5) Insira dois funcionários com a mesma função, porém salários diferentes.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) 
VALUES 
(777777777, 'Lucas Rocha', 'Suporte', 2000.00, '1997-04-05', 'TI'),
(888888888, 'Juliana Alves', 'Suporte', 2800.00, '1996-09-18', 'TI');

-- 6) Cadastre cinco funcionários do departamento “TI”.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento) 
VALUES 
(999999991, 'Fernanda Dias', 'Desenvolvedor', 6000.00, '1993-02-14', 'TI'),
(999999992, 'Ricardo Melo', 'Desenvolvedor', 6200.00, '1994-06-22', 'TI'),
(999999993, 'Camila Cruz', 'Estagiário', 1200.00, '2001-10-05', 'TI'),
(999999994, 'Gabriel Neves', 'Coordenador', 9000.00, '1982-12-01', 'TI'),
(999999995, 'Amanda Reis', 'DBA', 7500.00, '1989-08-15', 'TI');

-- 7) Insira um registro omitindo propositalmente o campo funcao.
INSERT INTO FUNCIONARIO (cpf, nome, salario, data_nasc, departamento) 
VALUES (123456789, 'Roberto Carlos', 3100.00, '1975-04-19', 'Marketing');


-- =============================================================================
-- 🟡 EXERCÍCIOS – UPDATE
-- =============================================================================

-- 8) Atualize o salário de um funcionário específico pelo CPF.
-- Nota acadêmica: O uso da chave primária (CPF) no WHERE garante que apenas um único registro seja afetado.
UPDATE FUNCIONARIO 
SET salario = 5500.00 
WHERE cpf = 111111111;

-- 9) Atualize a função de todos os funcionários do departamento “TI” para “DESENVOLVEDOR”.
UPDATE FUNCIONARIO 
SET funcao = 'DESENVOLVEDOR' 
WHERE departamento = 'TI';

-- 10) Aumente em 10% o salário de todos os funcionários.
-- Nota acadêmica: A ausência da cláusula WHERE faz com que a alteração seja aplicada a toda a tabela.
UPDATE FUNCIONARIO 
SET salario = salario * 1.10;

-- 11) Altere o departamento de um funcionário específico.
UPDATE FUNCIONARIO 
SET departamento = 'Diretoria' 
WHERE cpf = 222222222;

-- 12) Atualize a função e o salário simultaneamente de um funcionário.
-- Nota acadêmica: Múltiplas atribuições no SET são separadas por vírgula.
UPDATE FUNCIONARIO 
SET funcao = 'Coordenador de TI', 
    salario = 10500.00 
WHERE cpf = 999999994;

-- 13) Defina o salário como 0 para funcionários que não possuem salário cadastrado.
-- Nota acadêmica: Para comparar valores nulos em SQL, utiliza-se obrigatoriamente o operador "IS NULL".
UPDATE FUNCIONARIO 
SET salario = 0.00 
WHERE salario IS NULL;

-- 14) Altere a função para “GERENTE” apenas para funcionários com salário superior a 8000.
UPDATE FUNCIONARIO 
SET funcao = 'GERENTE' 
WHERE salario > 8000.00;


-- =============================================================================
-- 🔴 EXERCÍCIOS – DELETE
-- =============================================================================

-- 15) Exclua um funcionário específico pelo CPF