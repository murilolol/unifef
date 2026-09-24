-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- INSTITUIÇÃO: Centro Universitário Municipal de Franca (UniFEF)
-- PROFESSOR: Prof. Guilherme de Morais
-- TEMA: Exercício 1 - DER Empresa (Modelo Relacional DDL e DML)
-- EXECUÇÃO: Compatível com PostgreSQL 12+, MySQL 8.0+ e SQLite 3
-- ============================================================================

-- Limpeza de tabelas para garantia de idempotência
DROP TABLE IF EXISTS funcionario_projeto CASCADE;
DROP TABLE IF EXISTS dependente CASCADE;
DROP TABLE IF EXISTS funcionario_telefone CASCADE;
DROP TABLE IF EXISTS projeto CASCADE;
DROP TABLE IF EXISTS departamento CASCADE;
DROP TABLE IF EXISTS funcionario CASCADE;

-- ----------------------------------------------------------------------------
-- 1. TABELA: departamento
-- Representa os departamentos da empresa (número, nome e local).
-- O relacionamento 1:1 de gerência será configurado após a criação de funcionário.
-- ----------------------------------------------------------------------------
CREATE TABLE departamento (
    numero_depto INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    local VARCHAR(150) NOT NULL,
    cpf_gerente CHAR(11) -- FK adicionada via ALTER TABLE por dependência circular
);

-- ----------------------------------------------------------------------------
-- 2. TABELA: funcionario
-- Atributos atômicos do nome (primeiro, segundo, último), dados demográficos,
-- auto-relacionamento (supervisor) e alocação departamental (1:N).
-- ----------------------------------------------------------------------------
CREATE TABLE funcionario (
    cpf CHAR(11) PRIMARY KEY,
    primeiro_nome VARCHAR(50) NOT NULL,
    segundo_nome VARCHAR(50),
    ultimo_nome VARCHAR(50) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    sexo CHAR(1) NOT NULL CHECK (sexo IN ('M', 'F', 'O')),
    data_nascimento DATE NOT NULL,
    cpf_supervisor CHAR(11),
    numero_depto INT NOT NULL,
    CONSTRAINT fk_func_supervisor FOREIGN KEY (cpf_supervisor)
        REFERENCES funcionario(cpf) ON DELETE SET NULL,
    CONSTRAINT fk_func_departamento FOREIGN KEY (numero_depto)
        REFERENCES departamento(numero_depto) ON DELETE RESTRICT
);

-- Fechamento do relacionamento 1:1: Todo departamento possui somente um gerente
ALTER TABLE departamento
    ADD CONSTRAINT fk_depto_gerente FOREIGN KEY (cpf_gerente)
    REFERENCES funcionario(cpf) ON DELETE RESTRICT;

-- ----------------------------------------------------------------------------
-- 3. TABELA: funcionario_telefone
-- Mapeamento de atributo multivalorado (um funcionário pode ter vários telefones)
-- ----------------------------------------------------------------------------
CREATE TABLE funcionario_telefone (
    cpf_funcionario CHAR(11) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    PRIMARY KEY (cpf_funcionario, telefone),
    CONSTRAINT fk_tel_funcionario FOREIGN KEY (cpf_funcionario)
        REFERENCES funcionario(cpf) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 4. TABELA: dependente
-- Entidade fraca associada ao funcionário mantenedor (1:N, dependente exclusivo)
-- ----------------------------------------------------------------------------
CREATE TABLE dependente (
    id_dependente INT NOT NULL,
    cpf_funcionario CHAR(11) NOT NULL,
    cpf CHAR(11) UNIQUE,
    primeiro_nome VARCHAR(50) NOT NULL,
    segundo_nome VARCHAR(50),
    ultimo_nome VARCHAR(50) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    sexo CHAR(1) NOT NULL CHECK (sexo IN ('M', 'F', 'O')),
    data_nascimento DATE NOT NULL,
    PRIMARY KEY (cpf_funcionario, id_dependente),
    CONSTRAINT fk_dep_funcionario FOREIGN KEY (cpf_funcionario)
        REFERENCES funcionario(cpf) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 5. TABELA: projeto
-- Projetos obtidos pela empresa, controlados por departamentos (1:N)
-- ----------------------------------------------------------------------------
CREATE TABLE projeto (
    codigo_projeto INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_criacao DATE NOT NULL,
    numero_depto INT NOT NULL,
    CONSTRAINT fk_proj_departamento FOREIGN KEY (numero_depto)
        REFERENCES departamento(numero_depto) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 6. TABELA: funcionario_projeto
-- Tabela associativa para relacionamento N:N (funcionários x projetos)
-- com atributo de relacionamento essencial: quantidade de horas trabalhadas
-- ----------------------------------------------------------------------------
CREATE TABLE funcionario_projeto (
    cpf_funcionario CHAR(11) NOT NULL,
    codigo_projeto INT NOT NULL,
    horas_semanais DECIMAL(5,2) NOT NULL CHECK (horas_semanais > 0),
    PRIMARY KEY (cpf_funcionario, codigo_projeto),
    CONSTRAINT fk_fp_funcionario FOREIGN KEY (cpf_funcionario)
        REFERENCES funcionario(cpf) ON DELETE CASCADE,
    CONSTRAINT fk_fp_projeto FOREIGN KEY (codigo_projeto)
        REFERENCES projeto(codigo_projeto) ON DELETE CASCADE
);

-- ============================================================================
-- CARGA DE DADOS DE TESTE (DML)
-- ============================================================================

-- Departamentos iniciais (gerente temporariamente nulo)
INSERT INTO departamento (numero_depto, nome, local, cpf_gerente) VALUES
(1, 'Pesquisa e Desenvolvimento', 'Bloco A - Campus I', NULL),
(2, 'Tecnologia da Informação', 'Bloco C - Campus II', NULL);

-- Funcionários com supervisão hierárquica
INSERT INTO funcionario (cpf, primeiro_nome, segundo_nome, ultimo_nome, endereco, sexo, data_nascimento, cpf_supervisor, numero_depto) VALUES
('11122233344', 'Carlos', 'Eduardo', 'Silva', 'Rua das Palmeiras, 120, Franca-SP', 'M', '1982-05-14', NULL, 1),
('22233344455', 'Beatriz', 'Helena', 'Morais', 'Av. Brasil, 450, Franca-SP', 'F', '1988-11-20', '11122233344', 1),
('33344455566', 'Lucas', 'Fernando', 'Santos', 'Rua Major Claudiano, 890, Franca-SP', 'M', '1995-03-08', '11122233344', 2);

-- Atualização dos gerentes de departamento (1:1)
UPDATE departamento SET cpf_gerente = '11122233344' WHERE numero_depto = 1;
UPDATE departamento SET cpf_gerente = '33344455566' WHERE numero_depto = 2;

-- Telefones dos funcionários (multivalorado)
INSERT INTO funcionario_telefone (cpf_funcionario, telefone) VALUES
('11122233344', '(16) 99111-2233'),
('11122233344', '(16) 3721-0001'),
('22233344455', '(16) 98888-4455');

-- Dependentes dos funcionários
INSERT INTO dependente (id_dependente, cpf_funcionario, cpf, primeiro_nome, segundo_nome, ultimo_nome, endereco, sexo, data_nascimento) VALUES
(1, '11122233344', '44455566677', 'Enzo', 'Gabriel', 'Silva', 'Rua das Palmeiras, 120, Franca-SP', 'M', '2015-07-22'),
(1, '22233344455', '55566677788', 'Alice', 'Helena', 'Morais', 'Av. Brasil, 450, Franca-SP', 'F', '2018-09-10');

-- Projetos vinculados aos departamentos
INSERT INTO projeto (codigo_projeto, nome, data_criacao, numero_depto) VALUES
(101, 'Sistema Integrado ERP 2.0', '2026-02-01', 1),
(102, 'Infraestrutura de Nuvem Híbrida', '2026-02-10', 2);

-- Alocação de funcionários em projetos com horas dedicadas
INSERT INTO funcionario_projeto (cpf_funcionario, codigo_projeto, horas_semanais) VALUES
('11122233344', 101, 20.00),
('22233344455', 101, 30.00),
('22233344455', 102, 10.00),
('33344455566', 102, 40.00);

-- Consulta de validação: Alocação completa de projetos, horas e supervisores
SELECT 
    p.nome AS projeto,
    d.nome AS departamento,
    CONCAT(f.primeiro_nome, ' ', f.ultimo_nome) AS funcionario,
    fp.horas_semanais AS horas_dedicadas,
    CONCAT(s.primeiro_nome, ' ', s.ultimo_nome) AS supervisor_direto
FROM funcionario_projeto fp
JOIN projeto p ON fp.codigo_projeto = p.codigo_projeto
JOIN departamento d ON p.numero_depto = d.numero_depto
JOIN funcionario f ON fp.cpf_funcionario = f.cpf
LEFT JOIN funcionario s ON f.cpf_supervisor = s.cpf
ORDER BY p.codigo_projeto, f.primeiro_nome;
