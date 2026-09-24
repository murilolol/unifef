/*
 * Disciplina : Tópicos Avançados em Banco de Dados (4º Semestre)
 * Professor  : Prof. Welington Garcia
 * Tema       : Atividade Prática de Laboratório - Sistema de Biblioteca
 * SGBD       : PostgreSQL 14+
 *
 * Como executar:
 *   psql -U postgres -d seu_banco -f atividade_biblioteca.sql
 */

BEGIN;

-- ========================================================================
-- 1. MODELAGEM RELACIONAL (DDL)
-- ========================================================================

DROP TABLE IF EXISTS emprestimos CASCADE;
DROP TABLE IF EXISTS livros CASCADE;
DROP TABLE IF EXISTS leitores CASCADE;
DROP TABLE IF EXISTS autores CASCADE;

CREATE TABLE autores (
    id_autor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE livros (
    id_livro SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    ano_publicacao INTEGER CHECK (ano_publicacao > 1400),
    id_autor INTEGER REFERENCES autores(id_autor)
);

CREATE TABLE leitores (
    id_leitor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE emprestimos (
    id_emprestimo SERIAL PRIMARY KEY,
    id_livro INTEGER NOT NULL REFERENCES livros(id_livro),
    id_leitor INTEGER NOT NULL REFERENCES leitores(id_leitor),
    data_emprestimo DATE NOT NULL DEFAULT CURRENT_DATE,
    data_devolucao DATE -- NULL indica que o exemplar ainda não foi devolvido
);


-- ========================================================================
-- 2. CARGA DE DADOS OBRIGATÓRIA (DML)
-- Requisitos: 5 autores, 8 livros, 5 leitores, 6 empréstimos
-- Casos especiais: Autor sem livro, Livro nunca emprestado, Leitor sem empréstimo
-- ========================================================================

-- 5 autores (Autor 5: 'Clarice Lispector' não terá livros associados)
INSERT INTO autores (id_autor, nome) VALUES
(1, 'Machado de Assis'),
(2, 'George Orwell'),
(3, 'J. R. R. Tolkien'),
(4, 'Fiódor Dostoiévski'),
(5, 'Clarice Lispector');

-- 8 livros (Livros 7 e 8 nunca foram emprestados)
INSERT INTO livros (id_livro, titulo, ano_publicacao, id_autor) VALUES
(1, 'Dom Casmurro', 1899, 1),
(2, 'Memórias Póstumas de Brás Cubas', 1881, 1),
(3, '1984', 1949, 2),
(4, 'A Revolução dos Bichos', 1945, 2),
(5, 'O Senhor dos Anéis: A Sociedade do Anel', 1954, 3),
(6, 'Crime e Castigo', 1866, 4),
(7, 'O Hobbit', 1937, 3),        -- Livro nunca emprestado
(8, 'Os Irmãos Karamazov', 1880, 4); -- Livro nunca emprestado

-- 5 leitores (Leitor 5: 'Helena Ramos' não possui empréstimos)
INSERT INTO leitores (id_leitor, nome, email) VALUES
(1, 'Lucas Mendes', 'lucas.mendes@email.com'),
(2, 'Beatriz Alencar', 'beatriz.alencar@email.com'),
(3, 'Gabriel Pires', 'gabriel.pires@email.com'),
(4, 'Juliana Castro', 'juliana.castro@email.com'),
(5, 'Helena Ramos', 'helena.ramos@email.com');

-- 6 empréstimos (Contém empréstimos devolvidos e pendentes)
INSERT INTO emprestimos (id_emprestimo, id_livro, id_leitor, data_emprestimo, data_devolucao) VALUES
(1, 1, 1, '2026-07-10', '2026-07-20'), -- Devolvido
(2, 3, 1, '2026-08-01', NULL),         -- Em aberto (Ainda não devolvido)
(3, 2, 2, '2026-07-15', '2026-07-28'), -- Devolvido
(4, 5, 2, '2026-08-02', NULL),         -- Em aberto (Ainda não devolvido)
(5, 4, 3, '2026-08-03', NULL),         -- Em aberto (Ainda não devolvido)
(6, 6, 4, '2026-07-25', '2026-08-04'); -- Devolvido

COMMIT;


-- ========================================================================
-- 3. CONSULTAS OBRIGATÓRIAS - PARTE I: OPERAÇÕES COM JOINS
-- ========================================================================

-- Consulta 1: Livros com seus autores
SELECT
    l.id_livro,
    l.titulo,
    l.ano_publicacao,
    a.nome AS autor
FROM livros l
INNER JOIN autores a
    ON l.id_autor = a.id_autor
ORDER BY l.id_livro;

-- Consulta 2: Todos os autores, inclusive sem livros
SELECT
    a.id_autor,
    a.nome AS autor,
    COALESCE(l.titulo, 'Nenhum livro cadastrado') AS livro
FROM autores a
LEFT JOIN livros l
    ON a.id_autor = l.id_autor
ORDER BY a.id_autor;

-- Consulta 3: Leitores e livros emprestados
SELECT
    le.nome AS leitor,
    li.titulo AS livro,
    e.data_emprestimo,
    e.data_devolucao
FROM emprestimos e
INNER JOIN leitores le
    ON e.id_leitor = le.id_leitor
INNER JOIN livros li
    ON e.id_livro = li.id_livro
ORDER BY e.data_emprestimo DESC;

-- Consulta 4: Todos os leitores, inclusive sem empréstimos
SELECT
    le.id_leitor,
    le.nome AS leitor,
    e.id_emprestimo,
    e.data_emprestimo
FROM leitores le
LEFT JOIN emprestimos e
    ON le.id_leitor = e.id_leitor
ORDER BY le.id_leitor;

-- Consulta 5: Leitores que nunca fizeram empréstimos (Anti-Join)
SELECT
    le.id_leitor,
    le.nome AS leitor_sem_emprestimo,
    le.email
FROM leitores le
LEFT JOIN emprestimos e
    ON le.id_leitor = e.id_leitor
WHERE e.id_emprestimo IS NULL;

-- Consulta 6: Livros que nunca foram emprestados (Anti-Join)
SELECT
    li.id_livro,
    li.titulo,
    li.ano_publicacao
FROM livros li
LEFT JOIN emprestimos e
    ON li.id_livro = e.id_livro
WHERE e.id_emprestimo IS NULL;

-- Consulta 7: Quantidade de livros por autor
SELECT
    a.id_autor,
    a.nome AS autor,
    COUNT(l.id_livro) AS quantidade_livros
FROM autores a
LEFT JOIN livros l
    ON a.id_autor = l.id_autor
GROUP BY a.id_autor, a.nome
ORDER BY quantidade_livros DESC, a.nome ASC;

-- Consulta 8: Empréstimos ainda não devolvidos
SELECT
    e.id_emprestimo,
    le.nome AS leitor,
    li.titulo AS livro,
    e.data_emprestimo
FROM emprestimos e
INNER JOIN leitores le
    ON e.id_leitor = le.id_leitor
INNER JOIN livros li
    ON e.id_livro = li.id_livro
WHERE e.data_devolucao IS NULL
ORDER BY e.data_emprestimo ASC;


-- ========================================================================
-- 4. CONSULTAS OBRIGATÓRIAS - PARTE II: OPERAÇÕES COM SUBCONSULTAS
-- ========================================================================

-- Consulta Básica 1: Livros publicados acima da média de ano
SELECT
    titulo,
    ano_publicacao
FROM livros
WHERE ano_publicacao > (
    SELECT AVG(ano_publicacao)
    FROM livros
)
ORDER BY ano_publicacao DESC;

-- Consulta Básica 2: Autores que possuem livros (usando IN)
SELECT
    id_autor,
    nome
FROM autores
WHERE id_autor IN (
    SELECT id_autor
    FROM livros
)
ORDER BY id_autor;

-- Consulta Básica 3: Leitores sem empréstimos (usando NOT EXISTS)
SELECT
    id_leitor,
    nome,
    email
FROM leitores le
WHERE NOT EXISTS (
    SELECT 1
    FROM emprestimos e
    WHERE e.id_leitor = le.id_leitor
);

-- Consulta Avançada 4: Autor com maior quantidade de livros
SELECT
    a.nome,
    COUNT(l.id_livro) AS total_livros
FROM autores a
INNER JOIN livros l
    ON a.id_autor = l.id_autor
GROUP BY a.id_autor, a.nome
HAVING COUNT(l.id_livro) = (
    SELECT MAX(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM livros
        GROUP BY id_autor
    ) AS contagens
);

-- Consulta Avançada 5: Leitores acima da média de empréstimos
SELECT
    le.id_leitor,
    le.nome,
    COUNT(e.id_emprestimo) AS total_emprestimos
FROM leitores le
INNER JOIN emprestimos e
    ON le.id_leitor = e.id_leitor
GROUP BY le.id_leitor, le.nome
HAVING COUNT(e.id_emprestimo) > (
    SELECT AVG(qtd_leitor)
    FROM (
        SELECT COUNT(id_emprestimo) AS qtd_leitor
        FROM emprestimos
        GROUP BY id_leitor
    ) AS sub_media_emp
);

-- Consulta Avançada 6: Livros nunca emprestados (usando NOT IN com proteção de NULL)
SELECT
    id_livro,
    titulo,
    ano_publicacao
FROM livros
WHERE id_livro NOT IN (
    SELECT id_livro
    FROM emprestimos
    WHERE id_livro IS NOT NULL
);
