-- Disciplina: Topicos Avancados em Banco de Dados (4 Semestre)
-- Professor: Prof. Welington Garcia
-- Aula: Consultas Avancadas com Joins e Subselects
-- Como executar: Execute este script em um banco de dados PostgreSQL.

-- Limpeza de tabelas para recriacao idempotente
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;

-- Criacao das tabelas do modelo de vendas
CREATE TABLE clientes (
  id_cliente SERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cidade VARCHAR(100),
  estado CHAR(2)
);

CREATE TABLE categorias (
  id_categoria SERIAL PRIMARY KEY,
  nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE produtos (
  id_produto SERIAL PRIMARY KEY,
  nome_produto VARCHAR(100) NOT NULL,
  preco NUMERIC(10, 2) NOT NULL,
  id_categoria INTEGER REFERENCES categorias(id_categoria)
);

CREATE TABLE pedidos (
  id_pedido SERIAL PRIMARY KEY,
  data_pedido DATE NOT NULL,
  status VARCHAR(30) NOT NULL,
  id_cliente INTEGER REFERENCES clientes(id_cliente)
);

CREATE TABLE itens_pedido (
  id_pedido INTEGER REFERENCES pedidos(id_pedido),
  id_produto INTEGER REFERENCES produtos(id_produto),
  quantidade INTEGER NOT NULL,
  preco_unitario NUMERIC(10, 2) NOT NULL,
  PRIMARY KEY (id_pedido, id_produto)
);

CREATE TABLE funcionarios (
  id_funcionario SERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cargo VARCHAR(50),
  salario NUMERIC(10, 2),
  id_supervisor INTEGER REFERENCES funcionarios(id_funcionario)
);

-- Insercao de dados de exemplo
INSERT INTO clientes (nome, cidade, estado) VALUES
('Ana Souza', 'Sao Paulo', 'SP'),
('Bruno Lima', 'Rio de Janeiro', 'RJ'),
('Carla Dias', 'Belo Horizonte', 'MG'),
('Daniel Rocha', 'Curitiba', 'PR'),
('Eva Mendes', 'Porto Alegre', 'RS');

INSERT INTO categorias (nome_categoria) VALUES
('Informatica'),
('Acessorio'),
('Moveis'),
('Eletrodomesticos');

INSERT INTO produtos (nome_produto, preco, id_categoria) VALUES
('Teclado Mecanico', 250.00, 1),
('Mouse Gamer', 120.00, 1),
('Webcam HD', 300.00, 2),
('Cadeira Escritorio', 800.00, 3),
('Monitor Ultrawide', 1200.00, 1);

INSERT INTO pedidos (data_pedido, status, id_cliente) VALUES
('2026-05-01', 'Pago', 1),
('2026-05-02', 'Pendente', 2),
('2026-05-03', 'Pago', 1),
('2026-05-04', 'Cancelado', 3);

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(1, 1, 1, 250.00),
(1, 2, 2, 120.00),
(2, 3, 1, 300.00),
(3, 4, 1, 800.00),
(4, 5, 1, 1200.00);

INSERT INTO funcionarios (nome, cargo, salario, id_supervisor) VALUES
('Carlos Silva', 'Diretor', 15000.00, NULL),
('Mariana Costa', 'Gerente', 8000.00, 1),
('Pedro Santos', 'Analista', 5000.00, 2),
('Julia Oliveira', 'Assistente', 3000.00, 2);

-- 1. Exemplos de INNER JOIN
SELECT
  p.id_pedido,
  p.data_pedido,
  p.status,
  c.nome AS cliente
FROM pedidos p
INNER JOIN clientes c
  ON p.id_cliente = c.id_cliente;

-- 2. Exemplos de LEFT JOIN com COALESCE
SELECT
  pr.nome_produto,
  pr.preco,
  COALESCE(ca.nome_categoria, 'Sem categoria') AS categoria
FROM produtos pr
LEFT JOIN categorias ca
  ON pr.id_categoria = ca.id_categoria;

-- 3. Exemplos de RIGHT JOIN
SELECT
  p.id_pedido,
  c.id_cliente,
  c.nome
FROM pedidos p
RIGHT JOIN clientes c
  ON p.id_cliente = c.id_cliente;

-- 4. Exemplos de FULL OUTER JOIN
SELECT
  c.id_cliente,
  c.nome,
  p.id_pedido,
  p.status
FROM clientes c
FULL OUTER JOIN pedidos p
  ON c.id_cliente = p.id_cliente;

-- 5. Exemplos de CROSS JOIN
SELECT
  c.nome,
  ca.nome_categoria
FROM clientes c
CROSS JOIN categorias ca;

-- 6. Exemplos de SELF JOIN
SELECT
  f.nome AS funcionario,
  f.cargo,
  COALESCE(s.nome, 'Sem supervisor') AS supervisor
FROM funcionarios f
LEFT JOIN funcionarios s
  ON f.id_supervisor = s.id_funcionario;

-- 7. Subconsulta Escalar
SELECT nome_produto, preco
FROM produtos
WHERE preco > (
  SELECT AVG(preco)
  FROM produtos
);

-- 8. Subconsulta com IN
SELECT nome, cidade
FROM clientes
WHERE id_cliente IN (
  SELECT id_cliente
  FROM pedidos
);

-- 9. Subconsulta com EXISTS
SELECT c.id_cliente, c.nome
FROM clientes c
WHERE EXISTS (
  SELECT 1
  FROM pedidos p
  WHERE p.id_cliente = c.id_cliente
);

-- 10. Subconsulta Correlacionada
SELECT p1.nome_produto, p1.preco, p1.id_categoria
FROM produtos p1
WHERE p1.preco > (
  SELECT AVG(p2.preco)
  FROM produtos p2
  WHERE p2.id_categoria = p1.id_categoria
);
