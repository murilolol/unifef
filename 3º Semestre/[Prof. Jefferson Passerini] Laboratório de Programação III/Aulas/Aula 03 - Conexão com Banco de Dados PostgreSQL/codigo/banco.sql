-- ============================================================================
-- Disciplina : Laboratório de Programação III (3º Semestre)
-- Professor  : Prof. Jefferson Passerini
-- Tema       : Criação do Banco de Dados e Script DDL/DML da Tabela Usuario
-- Como Executar:
--   1. No pgAdmin 4 ou via terminal psql conectado ao PostgreSQL:
--      CREATE DATABASE bdaplcurso OWNER postgres;
--   2. Conecte-se ao banco bdaplcurso:
--      \c bdaplcurso;
--   3. Execute o script abaixo na Query Tool do pgAdmin ou via psql.
-- ============================================================================

-- Remove a tabela se já existir para manter o script idempotente
DROP TABLE IF EXISTS usuario CASCADE;

-- Criação da tabela usuario conforme especificações da Aula 04
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    datanascimento DATE NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(20) NOT NULL,
    salario DECIMAL(15,2) NOT NULL
);

-- Comentários documentando os propósitos dos campos
COMMENT ON TABLE usuario IS 'Tabela responsável pelo armazenamento de usuários da aplicação AplCurso';
COMMENT ON COLUMN usuario.id IS 'Identificador primário e auto-incrementado';
COMMENT ON COLUMN usuario.cpf IS 'Cadastro de Pessoa Física, restrição única com 11 dígitos numéricos';
COMMENT ON COLUMN usuario.email IS 'Endereço de e-mail institucional/pessoal único';
COMMENT ON COLUMN usuario.salario IS 'Remuneração monetária com precisão de 15 dígitos e 2 casas decimais';

-- Inserção de registro inicial para testes de conexão e autenticação
INSERT INTO usuario (nome, datanascimento, cpf, email, senha, salario)
VALUES (
    'João José Gomes da Silva',
    '1990-08-10',
    '08243060073',
    'joaojosegomes@gmail.com',
    'senha123',
    5200.00
);

-- Consulta de verificação da inserção
SELECT id, nome, datanascimento, cpf, email, senha, salario FROM usuario;
