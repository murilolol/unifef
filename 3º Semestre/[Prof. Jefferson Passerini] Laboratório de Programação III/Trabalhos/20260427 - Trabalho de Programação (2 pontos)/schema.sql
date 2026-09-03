-- Banco de dados para o sistema de cadastro de livros (Lab. Programação III)
CREATE DATABASE IF NOT EXISTS livraria_db;
USE livraria_db;

DROP TABLE IF EXISTS livros;

CREATE TABLE livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_livro VARCHAR(150) NOT NULL,
    isbn VARCHAR(50) NOT NULL UNIQUE,
    autor VARCHAR(100) NOT NULL,
    data_publicacao DATE NOT NULL,
    valor_livro DECIMAL(10, 2) NOT NULL
);

-- Dados iniciais para teste
INSERT INTO livros (nome_livro, isbn, autor, data_publicacao, valor_livro) VALUES
('Java: Como Programar', '978-8543004792', 'Harvey Deitel', '2016-05-01', 250.00),
('Arquitetura Limpa', '978-8550804606', 'Robert C. Martin', '2019-04-22', 109.90);