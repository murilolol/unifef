-- ==========================================================================
-- Disciplina: Tópicos Avançados em Banco de Dados
-- Tema: Atividade Prática - Sistema de Biblioteca
-- Professor: Prof. Welington Garcia
-- Descrição: Resolução dos desafios práticos do sistema de biblioteca.
-- ==========================================================================

-- 1. Limpeza prévia para garantir idempotência
DROP VIEW IF EXISTS vw_resumo_emprestimos_leitor CASCADE;
DROP VIEW IF EXISTS vw_livros_nunca_emprestados CASCADE;
DROP VIEW IF EXISTS vw_leitores_sem_emprestimos CASCADE;
DROP VIEW IF EXISTS vw_qtd_livros_por_autor CASCADE;
DROP VIEW IF EXISTS vw_emprestimos_detalhados CASCADE;
DROP VIEW IF EXISTS vw_livros_autores CASCADE;

DROP TABLE IF EXISTS emprestimos CASCADE;
DROP TABLE IF EXISTS leitores CASCADE;
DROP TABLE IF EXISTS livros CASCADE;
DROP TABLE IF EXISTS autores CASCADE;

-- 2. Criação das tabelas do sistema de biblioteca
CREATE TABLE autores (
    id_autor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE livros (
    id_livro SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    id_autor INT REFERENCES autores(id_autor)
);

CREATE TABLE leitores (
    id_leitor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE emprestimos (
    id_emprestimo SERIAL PRIMARY KEY,
    id_leitor INT REFERENCES leitores(id_leitor),
    id_livro INT REFERENCES livros(id_livro),
    data_emprestimo DATE NOT NULL DEFAULT CURRENT_DATE
);

-- 3. Inserção de dados de teste
INSERT INTO autores (nome) VALUES 
('J.R.R. Tolkien'),
('George R.R. Martin'),
('Stephen King'),
('Autor Sem Livros');

INSERT INTO livros (titulo, id_autor) VALUES 
('O Senhor dos Anéis', 1),
('O Hobbit', 1),
('A Guerra dos Tronos', 2),
('O Iluminado', 3),
('Livro Perdido', NULL);

INSERT INTO leitores (nome) VALUES 
('Carlos Silva'),
('Mariana Souza'),
('Pedro Santos'),
('Leitor Sem Empréstimos');

INSERT INTO emprestimos (id_leitor, id_livro, data_emprestimo) VALUES 
(1, 1, '2026-09-01'),
(1, 2, '2026-09-02'),
(2, 3, '2026-09-03');

-- 4. Criação das Views Solicitadas

-- View: Livros com nome do autor
CREATE VIEW vw_livros_autores AS
SELECT l.id_livro, l.titulo, COALESCE(a.nome, 'Autor Desconhecido') AS autor
FROM livros l
LEFT JOIN autores a ON l.id_autor = a.id_autor;

-- View: Empréstimos com leitor e livro
CREATE VIEW vw_emprestimos_detalhados AS
SELECT e.id_emprestimo, e.data_emprestimo, le.nome AS leitor, li.titulo AS livro
FROM emprestimos e
JOIN leitores le ON e.id_leitor = le.id_leitor
JOIN livros li ON e.id_livro = li.id_livro;

-- View: Quantidade de livros por autor
CREATE VIEW vw_qtd_livros_por_autor AS
SELECT a.id_autor, a.nome AS autor, COUNT(l.id_livro) AS quantidade_livros
FROM autores a
LEFT JOIN livros l ON a.id_autor = l.id_autor
GROUP BY a.id_autor, a.nome;

-- 5. Resolução dos Desafios

-- Desafio: Leitores sem empréstimos
CREATE VIEW vw_leitores_sem_emprestimos AS
SELECT l.id_leitor, l.nome
FROM leitores l
LEFT JOIN emprestimos e ON l.id_leitor = e.id_leitor
WHERE e.id_emprestimo IS NULL;

-- Desafio: Livros nunca emprestados
CREATE VIEW vw_livros_nunca_emprestados AS
SELECT l.id_livro, l.titulo
FROM livros l
LEFT JOIN emprestimos e ON l.id_livro = e.id_livro
WHERE e.id_emprestimo IS NULL;

-- Desafio: Resumo de empréstimos por leitor
CREATE VIEW vw_resumo_emprestimos_leitor AS
SELECT l.id_leitor, l.nome AS leitor, COUNT(e.id_emprestimo) AS total_emprestimos
FROM leitores l
LEFT JOIN emprestimos e ON l.id_leitor = e.id_leitor
GROUP BY l.id_leitor, l.nome;
