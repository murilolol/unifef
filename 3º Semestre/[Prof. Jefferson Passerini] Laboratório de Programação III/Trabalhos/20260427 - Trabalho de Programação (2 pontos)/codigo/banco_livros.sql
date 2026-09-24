-- ========================================================================
-- Disciplina: Laboratório de Programação III (3º Semestre)
-- Professor:  Prof. Jefferson Passerini
-- Tema:       Manutenção e Cadastro de Livros com Java Web e Servlets
-- Arquivo:    banco_livros.sql
--
-- Como executar:
--   mysql -u root -p < banco_livros.sql
-- ========================================================================

-- 1. Criação do Banco de Dados (Schema)
CREATE DATABASE IF NOT EXISTS unifef_livros_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE unifef_livros_db;

-- 2. Criação da Tabela Livros
DROP TABLE IF EXISTS livros;

CREATE TABLE livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_livro VARCHAR(255) NOT NULL,
    isbn VARCHAR(30) NOT NULL UNIQUE,
    autor VARCHAR(180) NOT NULL,
    data_publicacao DATE NOT NULL,
    valor_livro DECIMAL(10, 2) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Inserção de Registros de Exemplo (Carga Inicial)
INSERT INTO livros (nome_livro, isbn, autor, data_publicacao, valor_livro) VALUES
('Java: Como Programar', '978-8543004792', 'Paul Deitel', '2016-06-24', 289.90),
('Código Limpo: Habilidades Práticas do Agile Software', '978-8576082675', 'Robert C. Martin', '2009-09-08', 114.50),
('Padrões de Projetos: Soluções Reutilizáveis de Software Orientado a Objetos', '978-8573076103', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', '2000-01-01', 149.00),
('Arquitetura Limpa: O Guia do Artesão para Estrutura e Design de Software', '978-8550804606', 'Robert C. Martin', '2019-05-23', 98.00),
('Refatoração: Aperfeiçoando o Design de Código Existente', '978-8575227244', 'Martin Fowler', '2020-01-15', 135.00);

-- ========================================================================
-- 4. Consultas de Exemplo Representando as Operações do CRUD
-- ========================================================================

-- [R] Read: Listar todos os livros cadastrados
SELECT id, nome_livro, isbn, autor, DATE_FORMAT(data_publicacao, '%d/%m/%Y') AS data_formatada, valor_livro
FROM livros
ORDER BY id ASC;

-- [R] Read: Buscar livro específico por ID
SELECT id, nome_livro, isbn, autor, data_publicacao, valor_livro
FROM livros
WHERE id = 1;

-- [C] Create: Inserir novo livro
INSERT INTO livros (nome_livro, isbn, autor, data_publicacao, valor_livro)
VALUES ('Engenharia de Software', '978-8580550443', 'Ian Sommerville', '2011-08-10', 215.00);

-- [U] Update: Alterar dados de um livro existente
UPDATE livros
SET nome_livro = 'Java: Como Programar - 10ª Edição',
    valor_livro = 299.00
WHERE id = 1;

-- [D] Delete: Excluir livro pelo ID
DELETE FROM livros
WHERE id = 6;
