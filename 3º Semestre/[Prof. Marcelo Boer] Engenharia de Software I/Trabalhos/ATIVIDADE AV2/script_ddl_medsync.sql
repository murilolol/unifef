-- =====================================================================
-- SISTEMA MEDSYNC - SCRIPT DE MODELAGEM FÍSICA DO BANCO DE DADOS (DDL)
-- SGBD: PostgreSQL 15+
-- Disciplina: Engenharia de Software I - Prof. Marcelo Boer
-- =====================================================================

-- Extensão para geração de UUIDs (opcional, utilizando chaves primárias sequenciais neste exemplo)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. TABELA DE USUÁRIOS (Base para Herança/Polimorfismo de Perfis)
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('PACIENTE', 'MEDICO', 'RECEPCIONISTA', 'ADMINISTRADOR')),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABELA DE PACIENTES (Especialização de Usuário)
CREATE TABLE pacientes (
    id INT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    tipo_sanguineo VARCHAR(5) CHECK (tipo_sanguineo IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'))
);

-- 3. TABELA DE MÉDICOS (Especialização de Usuário)
CREATE TABLE medicos (
    id INT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    crm VARCHAR(20) UNIQUE NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    valor_consulta NUMERIC(10, 2) NOT NULL CHECK (valor_consulta >= 0)
);

-- 4. TABELA DE RECEPCIONISTAS (Especialização de Usuário)
CREATE TABLE recepcionistas (
    id INT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    carteira_trabalho VARCHAR(50) UNIQUE NOT NULL
);

-- 5. TABELA DE CONSULTAS (Gerenciamento de Agendamentos)
CREATE TABLE consultas (
    id SERIAL PRIMARY KEY,
    paciente_id INT NOT NULL REFERENCES pacientes(id),
    medico_id INT NOT NULL REFERENCES medicos(id),
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE', 'CONFIRMADA', 'CANCELADA', 'REALIZADA')),
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consulta_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    CONSTRAINT fk_consulta_medico FOREIGN KEY (medico_id) REFERENCES medicos(id)
);

-- 6. TABELA DE PRONTUÁRIOS ELETRÔNICOS (PEP)
CREATE TABLE prontuarios (
    id SERIAL PRIMARY KEY,
    consulta_id INT UNIQUE NOT NULL REFERENCES consultas(id),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    historico_familiar TEXT,
    alergias TEXT,
    evolucao_clinica TEXT NOT NULL,
    assinado_digitalmente BOOLEAN DEFAULT FALSE
);

-- 7. TABELA DE PRESCRIÇÕES MÉDICAS (Receituários e Atestados)
CREATE TABLE prescricoes (
    id SERIAL PRIMARY KEY,
    prontuario_id INT NOT NULL REFERENCES prontuarios(id) ON DELETE CASCADE,
    medicamentos TEXT NOT NULL,
    posologia TEXT NOT NULL,
    assinatura_digital VARCHAR(512) NOT NULL,
    gerado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
-- ÍNDICES PARA OTIMIZAÇÃO DE BUSCAS E PERFORMANCE
-- =====================================================================
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_consultas_data_hora ON consultas(data_hora);
CREATE INDEX idx_consultas_medico_id ON consultas(medico_id);
CREATE INDEX idx_consultas_paciente_id ON consultas(paciente_id);