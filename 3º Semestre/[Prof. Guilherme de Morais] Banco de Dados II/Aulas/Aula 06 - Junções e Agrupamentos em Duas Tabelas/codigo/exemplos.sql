-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre) - UniFEF
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Junções e Agrupamentos em Duas Tabelas - Esquemas e Carga
-- Execução   : Execute este script em qualquer SGBD compatível com SQL padrão
--              (PostgreSQL, MySQL, MariaDB, SQL Server ou SQLite).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- CENÁRIO 1: Clientes (com Email) e Pedidos
-- -----------------------------------------------------------------------------

DROP TABLE IF EXISTS Pedidos;
DROP TABLE IF EXISTS Clientes;

-- Criação da tabela de Clientes (Cenário 1)
CREATE TABLE Clientes (
    ClienteID INT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Cidade VARCHAR(100),
    Email VARCHAR(100)
);

-- Criação da tabela de Pedidos (Cenário 1)
CREATE TABLE Pedidos (
    PedidoID INT PRIMARY KEY,
    ClienteID INT,
    DataPedido DATE,
    Valor DECIMAL(10,2),
    FOREIGN KEY (ClienteID) REFERENCES Clientes(ClienteID)
);

-- Inserção de dados na tabela Clientes
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (1, 'Ana Silva', 'São Paulo', 'ana@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (2, 'Carlos Souza', 'Rio de Janeiro', 'carlos@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (3, 'Mariana Lima', 'Belo Horizonte', 'mariana@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (4, 'João Pedro', 'Curitiba', 'joao@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (5, 'Fernanda Costa', 'Porto Alegre', 'fernanda@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (6, 'Ricardo Alves', 'Salvador', 'ricardo@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (7, 'Patrícia Gomes', 'Fortaleza', 'patricia@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (8, 'Lucas Martins', 'Recife', 'lucas@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (9, 'Beatriz Rocha', 'Manaus', 'beatriz@email.com');
INSERT INTO Clientes (ClienteID, Nome, Cidade, Email) VALUES (10, 'Felipe Santos', 'Brasília', 'felipe@email.com');

-- Inserção de dados na tabela Pedidos
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (101, 1, '2026-05-01', 250.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (102, 1, '2026-05-05', 180.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (103, 2, '2026-05-03', 500.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (104, 3, '2026-05-06', 320.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (105, 4, '2026-05-07', 150.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (106, 5, '2026-05-08', 700.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (107, 6, '2026-05-09', 90.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (108, 7, '2026-05-10', 400.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (109, 8, '2026-05-11', 220.00);
INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES (110, 9, '2026-05-12', 350.00);
