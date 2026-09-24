/*
 * Disciplina : Tópicos Avançados em Banco de Dados (4º Semestre)
 * Professor  : Prof. Welington Garcia
 * Tema       : Modelo de Dados de Vendas para JOINs e Subconsultas
 * SGBD       : PostgreSQL 14+
 *
 * Como executar:
 *   psql -U postgres -d seu_banco -f schema_vendas.sql
 */

BEGIN;

-- Limpeza idempotente de tabelas prévias
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes_vip CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;

-- 1. Tabela de Clientes
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2)
);

-- 2. Tabela de Categorias
CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

-- 3. Tabela de Produtos
CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    id_categoria INTEGER REFERENCES categorias(id_categoria)
);

-- 4. Tabela de Pedidos
CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(30) NOT NULL,
    id_cliente INTEGER REFERENCES clientes(id_cliente)
);

-- 5. Tabela de Itens de Pedido
CREATE TABLE itens_pedido (
    id_pedido INTEGER REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    id_produto INTEGER REFERENCES produtos(id_produto),
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    PRIMARY KEY (id_pedido, id_produto)
);

-- 6. Tabela de Funcionários (Hierarquia de Supervisão / Auto-relacionamento)
CREATE TABLE funcionarios (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    salario NUMERIC(10, 2) NOT NULL CHECK (salario >= 0),
    departamento VARCHAR(50) NOT NULL,
    id_supervisor INTEGER REFERENCES funcionarios(id_funcionario)
);

-- 7. Tabela de Clientes VIP (Destino de carga DML / INSERT ... SELECT)
CREATE TABLE clientes_vip (
    id_cliente INTEGER PRIMARY KEY REFERENCES clientes(id_cliente),
    nome VARCHAR(100) NOT NULL,
    total_comprado NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

-- Inserção de dados para testes didáticos

-- Categorias
INSERT INTO categorias (id_categoria, nome_categoria) VALUES
(1, 'Informática'),
(2, 'Acessórios'),
(3, 'Móveis Corporativos'),
(4, 'Games'),
(5, 'Eletrodomésticos'); -- Categoria propositalmente sem produtos

-- Clientes
INSERT INTO clientes (id_cliente, nome, cidade, estado) VALUES
(1, 'Ana Silva', 'São Paulo', 'SP'),
(2, 'Bruno Santos', 'Campinas', 'SP'),
(3, 'Carla Oliveira', 'Belo Horizonte', 'MG'),
(4, 'Diego Souza', 'Curitiba', 'PR'),
(5, 'Eduardo Lima', 'Rio de Janeiro', 'RJ'); -- Cliente sem pedidos cadastrados

-- Produtos
INSERT INTO produtos (id_produto, nome_produto, preco, id_categoria) VALUES
(1, 'Notebook Pro 15', 4500.00, 1),
(2, 'Mouse Sem Fio Ergonomico', 120.00, 2),
(3, 'Teclado Mecanico RGB', 280.00, 2),
(4, 'Monitor UltraWide 29', 1450.00, 1),
(5, 'Cadeira Diretor Mesh', 890.00, 3),
(6, 'Console NextGen X', 3800.00, 4),
(7, 'Jogo Cyber Adventure', 250.00, 4),
(8, 'Webcam 1080p Streamer', 210.00, NULL); -- Produto propositalmente sem categoria (id_categoria NULL)

-- Pedidos
INSERT INTO pedidos (id_pedido, data_pedido, status, id_cliente) VALUES
(101, '2026-08-01', 'Pago', 1),
(102, '2026-08-02', 'Pago', 1),
(103, '2026-08-03', 'Pendente', 2),
(104, '2026-08-04', 'Pago', 3),
(105, '2026-08-05', 'Cancelado', 4);

-- Itens de Pedido
INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(101, 1, 1, 4500.00),
(101, 2, 2, 120.00),
(102, 3, 1, 280.00),
(103, 4, 1, 1450.00),
(103, 5, 1, 890.00),
(104, 6, 1, 3800.00),
(104, 7, 2, 250.00),
(105, 8, 1, 210.00);

-- Funcionários
INSERT INTO funcionarios (id_funcionario, nome, cargo, salario, departamento, id_supervisor) VALUES
(1, 'Roberto Justus', 'Diretor Geral', 18500.00, 'Diretoria', NULL),
(2, 'Mariana Rios', 'Gerente de Vendas', 9200.00, 'Comercial', 1),
(3, 'Carlos Drummond', 'Vendedor Sênior', 4800.00, 'Comercial', 2),
(4, 'Fernanda Montenegro', 'Analista de Suporte', 3600.00, 'TI', 1);

-- Atualização de sequences para novos inserts
SELECT setval('clientes_id_cliente_seq', (SELECT MAX(id_cliente) FROM clientes));
SELECT setval('categorias_id_categoria_seq', (SELECT MAX(id_categoria) FROM categorias));
SELECT setval('produtos_id_produto_seq', (SELECT MAX(id_produto) FROM produtos));
SELECT setval('pedidos_id_pedido_seq', (SELECT MAX(id_pedido) FROM pedidos));
SELECT setval('funcionarios_id_funcionario_seq', (SELECT MAX(id_funcionario) FROM funcionarios));

COMMIT;
