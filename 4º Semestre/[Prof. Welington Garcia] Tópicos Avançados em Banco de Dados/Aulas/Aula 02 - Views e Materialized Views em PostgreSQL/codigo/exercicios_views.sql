-- Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor: Prof. Welington Garcia
-- Assunto: Resolução dos Exercícios de Fixação e Estudo de Caso (Biblioteca)

-- Schema base replicado para os exercícios
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    limite_credito NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(150) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    id_cliente INT REFERENCES clientes(id_cliente)
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id_pedido INT REFERENCES pedidos(id_pedido),
    id_produto INT REFERENCES produtos(id_produto),
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,
    PRIMARY KEY (id_pedido, id_produto)
);

-- Exercício 1 - View de Clientes por Estado
CREATE OR REPLACE VIEW vw_ex1_clientes_sp AS
SELECT id_cliente, nome, cidade, estado
FROM clientes
WHERE estado = 'SP';

-- Exercício 2 - View de Produtos por Faixa de Preço
CREATE OR REPLACE VIEW vw_ex2_produtos_caros AS
SELECT id_produto, nome_produto, preco
FROM produtos
WHERE preco > 1000.00;

-- Exercício 3 - View de Pedidos e Clientes
CREATE OR REPLACE VIEW vw_ex3_pedidos_clientes AS
SELECT p.id_pedido, p.data_pedido, p.status, c.nome AS nome_cliente
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente;

-- Exercício 4 - Consulta Filtrada em View
SELECT * 
FROM vw_ex3_pedidos_clientes 
WHERE status = 'Pago';

-- Exercício 5 - View de Valor Total do Pedido
CREATE OR REPLACE VIEW vw_ex5_valor_total_pedido AS
SELECT p.id_pedido, SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido;

-- Exercício 6 - View de Resumo por Cliente
CREATE OR REPLACE VIEW vw_ex6_resumo_cliente AS
SELECT c.id_cliente, c.nome, COUNT(p.id_pedido) AS quantidade_pedidos
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome;

-- Exercício 7 - Alteração de Definição de View
CREATE OR REPLACE VIEW vw_ex1_clientes_sp AS
SELECT id_cliente, nome, cidade, estado, limite_credito
FROM clientes
WHERE estado = 'SP';

-- Exercício 8 - View Atualizável com Proteção de Filtro (WITH CHECK OPTION)
CREATE OR REPLACE VIEW vw_ex8_clientes_sp_protegida AS
SELECT id_cliente, nome, cidade, estado
FROM clientes
WHERE estado = 'SP'
WITH CHECK OPTION;

-- Exercício 9 - View para Camada de Segurança
CREATE OR REPLACE VIEW vw_ex9_clientes_publico AS
SELECT id_cliente, nome, cidade, estado
FROM clientes;

-- Exercício 10 - Materialized View de Vendas por Cliente
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_ex10_vendas_cliente AS
SELECT c.id_cliente, c.nome, SUM(ip.quantidade * ip.preco_unitario) AS total_vendas
FROM clientes c
JOIN pedidos p ON c.id_cliente = p.id_cliente
JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY c.id_cliente, c.nome;

REFRESH MATERIALIZED VIEW mv_ex10_vendas_cliente;

-- ATIVIDADE PRÁTICA: Sistema de Biblioteca
CREATE TABLE IF NOT EXISTS autores (
    id_autor SERIAL PRIMARY KEY,
    nome_autor VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS livros (
    id_livro SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    id_autor INT REFERENCES autores(id_autor)
);

CREATE TABLE IF NOT EXISTS leitores (
    id_leitor SERIAL PRIMARY KEY,
    nome_leitor VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS emprestimos (
    id_emprestimo SERIAL PRIMARY KEY,
    id_livro INT REFERENCES livros(id_livro),
    id_leitor INT REFERENCES leitores(id_leitor),
    data_emprestimo DATE NOT NULL
);

-- 1. Livros com nome do autor
CREATE OR REPLACE VIEW vw_bib_livros_autores AS
SELECT l.id_livro, l.titulo, a.nome_autor
FROM livros l
JOIN autores a ON l.id_autor = a.id_autor;

-- 2. Empréstimos com leitor e livro
CREATE OR REPLACE VIEW vw_bib_emprestimos_detalhes AS
SELECT e.id_emprestimo, l.nome_leitor, lv.titulo, e.data_emprestimo
FROM emprestimos e
JOIN leitores l ON e.id_leitor = l.id_leitor
JOIN livros lv ON e.id_livro = lv.id_livro;

-- 3. Quantidade de livros por autor
CREATE OR REPLACE VIEW vw_bib_qtd_livros_autor AS
SELECT a.id_autor, a.nome_autor, COUNT(l.id_livro) AS quantidade_livros
FROM autores a
LEFT JOIN livros l ON a.id_autor = l.id_autor
GROUP BY a.id_autor, a.nome_autor;

-- Desafio A: Leitores sem empréstimos
CREATE OR REPLACE VIEW vw_bib_leitores_sem_emprestimos AS
SELECT l.id_leitor, l.nome_leitor
FROM leitores l
LEFT JOIN emprestimos e ON l.id_leitor = e.id_leitor
WHERE e.id_emprestimo IS NULL;

-- Desafio B: Livros nunca emprestados
CREATE OR REPLACE VIEW vw_bib_livros_nunca_emprestados AS
SELECT lv.id_livro, lv.titulo
FROM livros lv
LEFT JOIN emprestimos e ON lv.id_livro = e.id_livro
WHERE e.id_emprestimo IS NULL;

-- Desafio C: Resumo de empréstimos por leitor
CREATE OR REPLACE VIEW vw_bib_resumo_emprestimos_leitor AS
SELECT l.id_leitor, l.nome_leitor, COUNT(e.id_emprestimo) AS total_emprestimos
FROM leitores l
LEFT JOIN emprestimos e ON l.id_leitor = e.id_leitor
GROUP BY l.id_leitor, l.nome_leitor;
