-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre) - UniFEF
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Exercícios com SQL de Duas Tabelas - Cenário 2 (Clientes com Estado)
-- Execução   : Compatível com PostgreSQL, MySQL 8+, SQL Server e SQLite.
--              Execute o script de forma sequencial para recriar o esquema,
--              popular a base e rodar as consultas de cada exercício.
-- =============================================================================

-- Limpeza idempotente das tabelas
DROP TABLE IF EXISTS Pedidos;
DROP TABLE IF EXISTS Clientes;

-- -----------------------------------------------------------------------------
-- DDL: Criação das Tabelas
-- -----------------------------------------------------------------------------
CREATE TABLE Clientes (
    ClienteID INT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Cidade VARCHAR(100),
    Estado VARCHAR(50)
);

CREATE TABLE Pedidos (
    PedidoID INT PRIMARY KEY,
    ClienteID INT,
    DataPedido DATE,
    Valor DECIMAL(10,2),
    FOREIGN KEY (ClienteID) REFERENCES Clientes(ClienteID)
);

-- -----------------------------------------------------------------------------
-- DML: Carga de Dados de Teste
-- -----------------------------------------------------------------------------
INSERT INTO Clientes VALUES (1, 'Ana Silva', 'São Paulo', 'SP');
INSERT INTO Clientes VALUES (2, 'Carlos Souza', 'Rio de Janeiro', 'RJ');
INSERT INTO Clientes VALUES (3, 'Mariana Costa', 'Belo Horizonte', 'MG');
INSERT INTO Clientes VALUES (4, 'João Pereira', 'Curitiba', 'PR');
INSERT INTO Clientes VALUES (5, 'Fernanda Lima', 'Fortaleza', 'CE');
INSERT INTO Clientes VALUES (6, 'Ricardo Alves', 'Salvador', 'BA');
INSERT INTO Clientes VALUES (7, 'Patrícia Gomes', 'Recife', 'PE');
INSERT INTO Clientes VALUES (8, 'Felipe Rocha', 'Porto Alegre', 'RS');
INSERT INTO Clientes VALUES (9, 'Beatriz Martins', 'Campinas', 'SP');
INSERT INTO Clientes VALUES (10, 'Lucas Fernandes', 'Manaus', 'AM');

INSERT INTO Pedidos VALUES (101, 1, '2026-05-01', 1500.00);
INSERT INTO Pedidos VALUES (102, 1, '2026-05-05', 2300.00);
INSERT INTO Pedidos VALUES (103, 2, '2026-05-03', 500.00);
INSERT INTO Pedidos VALUES (104, 3, '2026-05-04', 1200.00);
INSERT INTO Pedidos VALUES (105, 4, '2026-05-06', 800.00);
INSERT INTO Pedidos VALUES (106, 5, '2026-05-07', 950.00);
INSERT INTO Pedidos VALUES (107, 6, '2026-05-08', 2000.00);
INSERT INTO Pedidos VALUES (108, 7, '2026-05-09', 300.00);
INSERT INTO Pedidos VALUES (109, 8, '2026-05-10', 1800.00);
INSERT INTO Pedidos VALUES (110, 10, '2026-05-11', 2200.00);

-- =============================================================================
-- Resolução dos Exercícios - Cenário 2
-- =============================================================================

-- Exercício 1: Mostrar pedidos com nome do cliente.
-- Motivação: Junção padrão INNER JOIN associando o código do pedido ao titular cadastrado.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
ORDER BY p.PedidoID;

-- Exercício 2: Listar clientes que têm pedidos.
-- Motivação: Filtragem de clientes com ao menos 1 registro em Pedidos, usando DISTINCT
-- para evitar duplicidade de clientes com mais de um pedido.
SELECT DISTINCT
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Estado
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
ORDER BY c.ClienteID;

-- Exercício 3: Total gasto por cliente.
-- Motivação: Totalização financeira com SUM e COALESCE para exibir R$ 0,00 nos clientes sem compras.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Estado,
    COALESCE(SUM(p.Valor), 0.00) AS TotalGasto
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome, c.Estado
ORDER BY TotalGasto DESC;

-- Exercício 4: Clientes sem pedidos com os cidade, nome dos clientes.
-- Motivação: Identificação de clientes inativos/sem compras com projeção de Nome, Cidade e Estado.
SELECT 
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Estado
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.PedidoID IS NULL
ORDER BY c.Nome;

-- Exercício 5: Pedidos acima de 1000 reais com os nomes dos clientes.
-- Motivação: Filtragem de compras de alto ticket médio (Valor > 1000) com junção dos dados do cliente.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.Valor,
    p.DataPedido
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
WHERE p.Valor > 1000.00
ORDER BY p.Valor DESC;

-- Exercício 6: Quantidade de pedidos por cliente.
-- Motivação: Mapeamento de volume transacional de cada cliente cadastrado.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COUNT(p.PedidoID) AS QuantidadePedidos
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY QuantidadePedidos DESC, c.Nome;

-- Exercício 7: Pedidos realizados em maio de 2026.
-- Motivação: Filtro de período específico com INNER JOIN trazendo dados do cliente e do pedido.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
WHERE p.DataPedido >= '2026-05-01' AND p.DataPedido <= '2026-05-31'
ORDER BY p.DataPedido, p.PedidoID;

-- Exercício 8: Maior pedido por cliente.
-- Motivação: Extração do pedido de pico (maior valor unitário) de cada cliente com pedidos.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    MAX(p.Valor) AS MaiorPedido
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY MaiorPedido DESC;
