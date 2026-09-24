-- ============================================================
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR : Prof. Welington Garcia
-- TEMA      : Sistema de Clínica Médica - Criação e Carga (DDL e DML)
-- SGBD      : PostgreSQL 14+
-- EXECUÇÃO  : psql -U postgres -d clinica_avaliacao -f banco_clinica.sql
-- ============================================================

-- Caso o banco ainda não exista, crie-o antes:
-- CREATE DATABASE clinica_avaliacao;
-- \c clinica_avaliacao

-- ============================================================
-- REMOÇÃO DAS TABELAS CASO JÁ EXISTAM (ORDEM DE DEPENDÊNCIA)
-- ============================================================

DROP TABLE IF EXISTS pagamentos CASCADE;
DROP TABLE IF EXISTS exames CASCADE;
DROP TABLE IF EXISTS consultas CASCADE;
DROP TABLE IF EXISTS pacientes CASCADE;
DROP TABLE IF EXISTS medicos CASCADE;
DROP TABLE IF EXISTS especialidades CASCADE;

-- ============================================================
-- TABELA: ESPECIALIDADES
-- ============================================================

CREATE TABLE especialidades (
    id_especialidade SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(200)
);

-- ============================================================
-- TABELA: MÉDICOS
-- ============================================================

CREATE TABLE medicos (
    id_medico SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    crm VARCHAR(20) UNIQUE NOT NULL,
    salario NUMERIC(10,2) NOT NULL,
    id_especialidade INTEGER,

    CONSTRAINT fk_medico_especialidade
        FOREIGN KEY (id_especialidade)
        REFERENCES especialidades(id_especialidade)
);

-- ============================================================
-- TABELA: PACIENTES
-- ============================================================

CREATE TABLE pacientes (
    id_paciente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    data_nascimento DATE NOT NULL,
    convenio VARCHAR(100)
);

-- ============================================================
-- TABELA: CONSULTAS
-- ============================================================

CREATE TABLE consultas (
    id_consulta SERIAL PRIMARY KEY,
    data_consulta DATE NOT NULL,
    horario TIME NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    id_paciente INTEGER NOT NULL,
    id_medico INTEGER NOT NULL,

    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES pacientes(id_paciente),

    CONSTRAINT fk_consulta_medico
        FOREIGN KEY (id_medico)
        REFERENCES medicos(id_medico)
);

-- ============================================================
-- TABELA: EXAMES
-- ============================================================

CREATE TABLE exames (
    id_exame SERIAL PRIMARY KEY,
    nome_exame VARCHAR(100) NOT NULL,
    data_solicitacao DATE NOT NULL,
    data_realizacao DATE,
    valor NUMERIC(10,2) NOT NULL,
    resultado VARCHAR(200),
    id_consulta INTEGER NOT NULL,

    CONSTRAINT fk_exame_consulta
        FOREIGN KEY (id_consulta)
        REFERENCES consultas(id_consulta)
);

-- ============================================================
-- TABELA: PAGAMENTOS
-- ============================================================

CREATE TABLE pagamentos (
    id_pagamento SERIAL PRIMARY KEY,
    data_pagamento DATE NOT NULL,
    valor_pago NUMERIC(10,2) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    id_consulta INTEGER UNIQUE NOT NULL,

    CONSTRAINT fk_pagamento_consulta
        FOREIGN KEY (id_consulta)
        REFERENCES consultas(id_consulta)
);

-- ============================================================
-- INSERTS: ESPECIALIDADES
-- ============================================================

INSERT INTO especialidades (nome, descricao) VALUES
('Cardiologia', 'Diagnóstico e tratamento de doenças cardiovasculares'),
('Dermatologia', 'Tratamento de doenças da pele'),
('Ortopedia', 'Tratamento de problemas ósseos e musculares'),
('Pediatria', 'Atendimento infantil'),
('Neurologia', 'Diagnóstico de doenças neurológicas'),
('Oftalmologia', 'Tratamento da visão e dos olhos'),
('Psiquiatria', 'Diagnóstico e acompanhamento psiquiátrico');
-- Nota: Psiquiatria fica inicialmente sem médico associado.

-- ============================================================
-- INSERTS: MÉDICOS
-- ============================================================

INSERT INTO medicos (nome, crm, salario, id_especialidade) VALUES
('Ricardo Menezes', 'CRM1001', 12000.00, 1),
('Fernanda Lima', 'CRM1002', 9500.00, 2),
('Carlos Andrade', 'CRM1003', 10500.00, 3),
('Juliana Martins', 'CRM1004', 9800.00, 4),
('Paulo Ribeiro', 'CRM1005', 13000.00, 5),
('Renata Souza', 'CRM1006', 11000.00, 6),
('Marcelo Vieira', 'CRM1007', 9000.00, 1),
('Camila Rocha', 'CRM1008', 9200.00, 2);

-- ============================================================
-- INSERTS: PACIENTES
-- ============================================================

INSERT INTO pacientes (nome, cidade, estado, data_nascimento, convenio) VALUES
('Ana Paula Souza', 'São Paulo', 'SP', '1985-04-12', 'SaúdeMais'),
('Bruno Ferreira', 'Campinas', 'SP', '1992-08-20', 'VidaPlena'),
('Carla Mendes', 'Curitiba', 'PR', '1978-03-11', 'SaúdeMais'),
('Daniel Silva', 'Londrina', 'PR', '1988-11-02', NULL),
('Eduarda Ramos', 'Belo Horizonte', 'MG', '1995-06-17', 'BemEstar'),
('Felipe Costa', 'Jales', 'SP', '2001-01-29', 'VidaPlena'),
('Gabriela Martins', 'Florianópolis', 'SC', '1983-09-09', 'SaúdeMais'),
('Henrique Oliveira', 'Goiânia', 'GO', '1970-12-22', NULL),
('Isabela Santos', 'São Paulo', 'SP', '1999-05-15', 'BemEstar'),
('João Fernandes', 'Curitiba', 'PR', '1981-07-30', 'VidaPlena'),
('Karen Alves', 'Urânia', 'SP', '1990-02-18', 'SaúdeMais'),
('Lucas Pereira', 'Fernandópolis', 'SP', '2003-10-10', NULL);
-- Nota: O paciente id 12 (Lucas Pereira) não possui consultas cadastradas.

-- ============================================================
-- INSERTS: CONSULTAS
-- ============================================================

INSERT INTO consultas (data_consulta, horario, valor, status, id_paciente, id_medico) VALUES
('2026-01-10', '08:00', 350.00, 'Realizada', 1, 1),
('2026-01-15', '09:30', 280.00, 'Realizada', 2, 2),
('2026-01-20', '10:00', 320.00, 'Realizada', 3, 3),
('2026-02-02', '14:00', 300.00, 'Cancelada', 4, 4),
('2026-02-08', '15:30', 420.00, 'Realizada', 5, 5),
('2026-02-15', '11:00', 300.00, 'Realizada', 6, 4),
('2026-03-01', '08:30', 350.00, 'Realizada', 7, 1),
('2026-03-07', '13:00', 390.00, 'Realizada', 8, 6),
('2026-03-12', '16:00', 280.00, 'Realizada', 9, 2),
('2026-03-20', '09:00', 340.00, 'Realizada', 10, 3),
('2026-04-03', '10:30', 350.00, 'Realizada', 1, 7),
('2026-04-11', '14:30', 420.00, 'Realizada', 5, 5),
('2026-04-18', '11:30', 280.00, 'Cancelada', 2, 8),
('2026-05-05', '09:45', 390.00, 'Realizada', 3, 6),
('2026-05-17', '15:00', 350.00, 'Realizada', 7, 1),
('2026-06-02', '08:15', 300.00, 'Realizada', 9, 4),
('2026-06-15', '13:30', 340.00, 'Realizada', 10, 3),
('2026-07-01', '10:00', 420.00, 'Agendada', 11, 5),
('2026-07-10', '14:00', 350.00, 'Agendada', 1, 7);

-- ============================================================
-- INSERTS: EXAMES
-- ============================================================

INSERT INTO exames (nome_exame, data_solicitacao, data_realizacao, valor, resultado, id_consulta) VALUES
('Eletrocardiograma', '2026-01-10', '2026-01-12', 180.00, 'Normal', 1),
('Hemograma Completo', '2026-01-15', '2026-01-16', 90.00, 'Normal', 2),
('Raio-X de Joelho', '2026-01-20', '2026-01-22', 150.00, 'Desgaste leve', 3),
('Ressonância Magnética', '2026-02-08', '2026-02-18', 850.00, 'Sem alterações graves', 5),
('Ultrassom', '2026-02-15', '2026-02-17', 220.00, 'Normal', 6),
('Ecocardiograma', '2026-03-01', '2026-03-03', 300.00, 'Normal', 7),
('Mapeamento de Retina', '2026-03-07', '2026-03-10', 250.00, 'Normal', 8),
('Biópsia de Pele', '2026-03-12', '2026-03-15', 400.00, 'Benigno', 9),
('Tomografia', '2026-04-11', '2026-04-18', 700.00, 'Sem alterações', 12),
('Raio-X de Coluna', '2026-05-17', NULL, 180.00, NULL, 15),
('Eletrocardiograma', '2026-07-01', NULL, 180.00, NULL, 18);

-- ============================================================
-- INSERTS: PAGAMENTOS
-- ============================================================

INSERT INTO pagamentos (data_pagamento, valor_pago, forma_pagamento, id_consulta) VALUES
('2026-01-10', 350.00, 'Cartão', 1),
('2026-01-15', 280.00, 'PIX', 2),
('2026-01-20', 320.00, 'Dinheiro', 3),
('2026-02-08', 420.00, 'Cartão', 5),
('2026-02-15', 300.00, 'PIX', 6),
('2026-03-01', 350.00, 'PIX', 7),
('2026-03-07', 390.00, 'Cartão', 8),
('2026-03-12', 280.00, 'Cartão', 9),
('2026-03-20', 340.00, 'Dinheiro', 10),
('2026-04-03', 350.00, 'PIX', 11),
('2026-04-11', 420.00, 'Cartão', 12),
('2026-05-05', 390.00, 'PIX', 14),
('2026-05-17', 350.00, 'Cartão', 15),
('2026-06-02', 300.00, 'PIX', 16);
