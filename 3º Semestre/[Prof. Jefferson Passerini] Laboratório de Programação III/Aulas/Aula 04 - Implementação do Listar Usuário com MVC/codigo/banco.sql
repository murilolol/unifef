-- =============================================================================
-- Disciplina : Laboratório de Programação III (3º Semestre)
-- Professor  : Prof. Jefferson Passerini
-- Tema       : Script de Banco de Dados PostgreSQL - Tabela usuario
-- =============================================================================

-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    datanascimento DATE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(50) NOT NULL,
    salario NUMERIC(10, 2) DEFAULT 0.00
);

-- Inserção de dados iniciais para validação do recurso Listar
INSERT INTO usuario (nome, datanascimento, cpf, email, senha, salario)
VALUES 
    ('João José Gomes da Silva', '1990-08-10', '08243060073', 'joaojosegomes@gmail.com', '123456', 5200.00),
    ('Maria Aparecida Santos', '1988-04-22', '12345678901', 'maria.santos@email.com', 'senha789', 6450.50),
    ('Carlos Eduardo Ferreira', '1995-11-15', '98765432100', 'carlos.ferreira@email.com', 'mudar123', 3800.00)
ON CONFLICT (cpf) DO NOTHING;

-- Consulta de conferência de dados
SELECT 
    id,
    nome,
    cpf,
    email,
    TO_CHAR(datanascimento, 'DD/MM/YYYY') AS data_nascimento_formatada,
    salario
FROM usuario
ORDER BY id;
