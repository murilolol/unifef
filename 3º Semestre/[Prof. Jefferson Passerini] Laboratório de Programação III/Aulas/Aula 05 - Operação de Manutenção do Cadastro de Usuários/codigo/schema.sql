-- =============================================================================
-- Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
-- Professor: Prof. Jefferson Passerini
-- Tema: Script DDL/DML Idempotente para a Tabela 'usuario'
--
-- Como executar:
--   PostgreSQL: psql -U usuario -d bancocurso -f schema.sql
--   MySQL:      mysql -u root -p bancocurso < schema.sql
-- =============================================================================

-- 1. Criação da tabela de usuários de forma idempotente
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    datanascimento DATE NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    salario NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Criação de índices para otimização das consultas de validação e filtros
CREATE INDEX IF NOT EXISTS idx_usuario_cpf ON usuario (cpf);
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario (email);

-- 3. Inserção de dados iniciais de teste (garantindo idempotência com checagem de existência)
INSERT INTO usuario (id, nome, datanascimento, cpf, email, senha, salario)
SELECT 1, 'ADMINISTRADOR DO SISTEMA', '1990-01-01', '12345678909', 'admin@curso.com.br', 'admin123', 6500.00
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 1);

INSERT INTO usuario (id, nome, datanascimento, cpf, email, senha, salario)
SELECT 2, 'MARIA SILVA SANTOS', '1995-05-20', '52998224725', 'maria.silva@curso.com.br', 'senha456', 4200.50
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 2);

-- 4. Consultas de verificação utilizadas na camada DAO do projeto
-- Consulta de unicidade de CPF (UsuarioDAO.cpfExiste):
SELECT COUNT(*) AS quantidade_cpf FROM usuario WHERE cpf = '12345678909';

-- Consulta de carregamento por ID (UsuarioDAO.carregar):
SELECT * FROM usuario WHERE id = 1;

-- Listagem ordenada (UsuarioDAO.listar):
SELECT * FROM usuario ORDER BY id;
