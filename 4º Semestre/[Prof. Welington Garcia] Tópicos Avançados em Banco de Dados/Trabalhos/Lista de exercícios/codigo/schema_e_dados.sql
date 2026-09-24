-- =============================================================================
-- Disciplina : Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor  : Prof. Welington Garcia
-- Tema       : Criação do Esquema e Carga de Dados - Loja Exercícios
-- SGBD       : PostgreSQL 14+
-- Execução   : psql -U postgres -d loja_exercicios -f schema_e_dados.sql
-- =============================================================================

DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS vendedores CASCADE;

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    limite_credito NUMERIC(10,2) NOT NULL,
    data_cadastro DATE NOT NULL
);

CREATE TABLE vendedores (
    id_vendedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario NUMERIC(10,2) NOT NULL,
    comissao NUMERIC(5,2),
    id_supervisor INTEGER,
    CONSTRAINT fk_vendedor_supervisor
        FOREIGN KEY (id_supervisor)
        REFERENCES vendedores(id_vendedor)
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL,
    descricao VARCHAR(200)
);

CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    estoque INTEGER NOT NULL,
    id_categoria INTEGER,
    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias(id_categoria)
);

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    id_cliente INTEGER NOT NULL,
    id_vendedor INTEGER NOT NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente),
    CONSTRAINT fk_pedido_vendedor
        FOREIGN KEY (id_vendedor)
        REFERENCES vendedores(id_vendedor)
);

CREATE TABLE itens_pedido (
    id_item SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL,
    id_produto INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    desconto NUMERIC(5,2) DEFAULT 0,
    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id_pedido),
    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto)
        REFERENCES produtos(id_produto)
);

-- Inserção de Clientes
INSERT INTO clientes
(nome, cidade, estado, limite_credito, data_cadastro)
VALUES
('Ana Souza', 'São Paulo', 'SP', 8000.00, '2025-01-10'),
('Bruno Lima', 'Campinas', 'SP', 4000.00, '2025-02-15'),
('Carla Mendes', 'Curitiba', 'PR', 12000.00, '2025-03-12'),
('Daniel Rocha', 'Londrina', 'PR', 3500.00, '2025-04-05'),
('Eduarda Alves', 'Belo Horizonte', 'MG', 15000.00, '2025-05-20'),
('Felipe Martins', 'Jales', 'SP', 6000.00, '2025-06-11'),
('Gabriela Costa', 'Florianópolis', 'SC', 9000.00, '2025-07-08'),
('Henrique Silva', 'Goiânia', 'GO', 3000.00, '2025-08-17'),
('Isabela Fernandes', 'São Paulo', 'SP', 10000.00, '2025-09-03'),
('João Pereira', 'Curitiba', 'PR', 5000.00, '2025-10-22'),
('Karen Oliveira', 'Urânia', 'SP', 7500.00, '2026-01-10'),
('Lucas Ferreira', 'Fernandópolis', 'SP', 2500.00, '2026-02-18');

-- Inserção de Vendedores (Supervisor Geral)
INSERT INTO vendedores
(nome, salario, comissao, id_supervisor)
VALUES
('Marcos Silva', 9000.00, 8.00, NULL);

-- Inserção de Supervisores Intermediários
INSERT INTO vendedores
(nome, salario, comissao, id_supervisor)
VALUES
('Fernanda Costa', 5500.00, 6.00, 1),
('Ricardo Alves', 5000.00, 5.50, 1);

-- Inserção de Vendedores Operacionais
INSERT INTO vendedores
(nome, salario, comissao, id_supervisor)
VALUES
('Juliana Martins', 3800.00, 5.00, 2),
('Paulo Souza', 3400.00, 4.50, 2),
('Renata Lima', 4200.00, 5.00, 3),
('Carlos Pereira', 3200.00, 4.00, 3);

-- Inserção de Categorias
INSERT INTO categorias
(nome_categoria, descricao)
VALUES
('Informática', 'Computadores e periféricos'),
('Telefonia', 'Smartphones e dispositivos móveis'),
('Acessórios', 'Acessórios para informática'),
('Games', 'Produtos relacionados a jogos'),
('Escritório', 'Móveis e equipamentos de escritório'),
('Eletrônicos', 'Equipamentos eletrônicos diversos'),
('Áudio', 'Equipamentos de áudio'),
('Fotografia', 'Produtos relacionados a fotografia');

-- Inserção de Produtos
INSERT INTO produtos
(nome_produto, preco, estoque, id_categoria)
VALUES
('Notebook Dell', 4500.00, 10, 1),
('Notebook Lenovo', 3800.00, 8, 1),
('Monitor 24 Polegadas', 1200.00, 15, 1),
('SSD 1TB', 550.00, 30, 1),
('HD Externo 2TB', 650.00, 20, 1),
('Smartphone Samsung', 2600.00, 18, 2),
('Smartphone Motorola', 1900.00, 25, 2),
('Tablet Samsung', 1700.00, 12, 2),
('Mouse Logitech', 150.00, 50, 3),
('Teclado Mecânico', 350.00, 35, 3),
('Webcam Full HD', 300.00, 22, 3),
('Hub USB', 120.00, 40, 3),
('PlayStation 5', 4200.00, 7, 4),
('Xbox Series X', 4000.00, 6, 4),
('Controle Bluetooth', 450.00, 30, 4),
('Headset Gamer', 500.00, 28, 4),
('Cadeira Escritório', 1100.00, 9, 5),
('Mesa Escritório', 850.00, 14, 5),
('Impressora Epson', 950.00, 11, 5),
('Smart TV 50', 2900.00, 13, 6),
('Projetor Full HD', 2400.00, 5, 6),
('Caixa de Som Bluetooth', 550.00, 25, 7),
('Fone Bluetooth', 320.00, 35, 7),
('Microfone USB', 650.00, 10, NULL);

-- Inserção de Pedidos
INSERT INTO pedidos
(data_pedido, status, id_cliente, id_vendedor)
VALUES
('2026-01-05', 'Pago', 1, 4),
('2026-01-10', 'Pago', 2, 5),
('2026-01-18', 'Enviado', 3, 6),
('2026-02-02', 'Pendente', 1, 4),
('2026-02-14', 'Pago', 5, 7),
('2026-02-20', 'Cancelado', 6, 5),
('2026-03-03', 'Pago', 7, 6),
('2026-03-15', 'Enviado', 3, 6),
('2026-03-25', 'Pago', 9, 4),
('2026-04-05', 'Pendente', 2, 5),
('2026-04-18', 'Pago', 1, 4),
('2026-05-01', 'Pago', 5, 7),
('2026-05-13', 'Enviado', 7, 6),
('2026-06-08', 'Pago', 9, 4),
('2026-06-22', 'Pago', 10, 5),
('2026-07-04', 'Pendente', 11, 7),
('2026-07-19', 'Pago', 3, 6),
('2026-08-02', 'Enviado', 1, 4),
('2026-08-11', 'Pago', 5, 7),
('2026-08-25', 'Pago', 11, 5);

-- Inserção de Itens de Pedido
INSERT INTO itens_pedido
(id_pedido, id_produto, quantidade, preco_unitario, desconto)
VALUES
(1, 1, 1, 4500.00, 0),
(1, 9, 2, 150.00, 0),
(2, 6, 1, 2600.00, 5),
(2, 23, 1, 320.00, 0),
(3, 13, 1, 4200.00, 0),
(3, 16, 1, 500.00, 0),
(4, 3, 2, 1200.00, 5),
(5, 20, 1, 2900.00, 0),
(5, 22, 2, 550.00, 10),
(6, 17, 1, 1100.00, 0),
(7, 14, 1, 4000.00, 0),
(7, 15, 2, 450.00, 0),
(8, 2, 1, 3800.00, 5),
(8, 3, 1, 1200.00, 0),
(9, 8, 2, 1700.00, 0),
(10, 7, 1, 1900.00, 0),
(11, 13, 1, 4200.00, 5),
(11, 16, 1, 500.00, 0),
(12, 1, 2, 4500.00, 10),
(13, 20, 1, 2900.00, 0),
(13, 22, 1, 550.00, 0),
(14, 6, 1, 2600.00, 5),
(14, 9, 1, 150.00, 0),
(15, 18, 1, 850.00, 0),
(15, 19, 1, 950.00, 0),
(16, 21, 1, 2400.00, 0),
(17, 1, 1, 4500.00, 5),
(17, 4, 2, 550.00, 0),
(18, 10, 1, 350.00, 0),
(18, 3, 2, 1200.00, 0),
(19, 13, 1, 4200.00, 0),
(19, 15, 1, 450.00, 0),
(20, 7, 1, 1900.00, 0),
(20, 23, 1, 320.00, 0);
