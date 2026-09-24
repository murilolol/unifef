-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre) - UniFEF
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Exercícios com SQL de Duas Tabelas - Cenário 1 (Clientes com Email)
-- Execução   : Compatível com PostgreSQL, MySQL 8+, SQL Server e SQLite.
--              Execute o script de forma sequencial para recriar o esquema,
--              popular a base e rodar as consultas de cada exercício.
-- =============================================================================

-- Limpeza idempotente das tabelas (remoção na ordem correta devido à FK)
DROP TABLE IF EXISTS Pedidos;
DROP TABLE IF EXISTS Clientes;

-- -----------------------------------------------------------------------------
-- DDL: Criação das Tabelas
-- -----------------------------------------------------------------------------
CREATE TABLE Clientes (
    ClienteID INT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Cidade VARCHAR(100),
    Email VARCHAR(100)
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
INSERT INTO Clientes VALUES (1, 'Ana Silva', 'São Paulo', 'ana@email.com');
INSERT INTO Clientes VALUES (2, 'Carlos Souza', 'Rio de Janeiro', 'carlos@email.com');
INSERT INTO Clientes VALUES (3, 'Mariana Lima', 'Belo Horizonte', 'mariana@email.com');
INSERT INTO Clientes VALUES (4, 'João Pedro', 'Curitiba', 'joao@email.com');
INSERT INTO Clientes VALUES (5, 'Fernanda Costa', 'Porto Alegre', 'fernanda@email.com');
INSERT INTO Clientes VALUES (6, 'Ricardo Alves', 'Salvador', 'ricardo@email.com');
INSERT INTO Clientes VALUES (7, 'Patrícia Gomes', 'Fortaleza', 'patricia@email.com');
INSERT INTO Clientes VALUES (8, 'Lucas Martins', 'Recife', 'lucas@email.com');
INSERT INTO Clientes VALUES (9, 'Beatriz Rocha', 'Manaus', 'beatriz@email.com');
INSERT INTO Clientes VALUES (10, 'Felipe Santos', 'Brasília', 'felipe@email.com');

INSERT INTO Pedidos VALUES (101, 1, '2026-05-01', 250.00);
INSERT INTO Pedidos VALUES (102, 1, '2026-05-05', 180.00);
INSERT INTO Pedidos VALUES (103, 2, '2026-05-03', 500.00);
INSERT INTO Pedidos VALUES (104, 3, '2026-05-06', 320.00);
INSERT INTO Pedidos VALUES (105, 4, '2026-05-07', 150.00);
INSERT INTO Pedidos VALUES (106, 5, '2026-05-08', 700.00);
INSERT INTO Pedidos VALUES (107, 6, '2026-05-09', 90.00);
INSERT INTO Pedidos VALUES (108, 7, '2026-05-10', 400.00);
INSERT INTO Pedidos VALUES (109, 8, '2026-05-11', 220.00);
INSERT INTO Pedidos VALUES (110, 9, '2026-05-12', 350.00);

-- =============================================================================
-- Resolução dos Exercícios - Cenário 1
-- =============================================================================

-- Exercício 1: Listar todos os clientes e seus pedidos.
-- Motivação: O uso de LEFT JOIN garante que clientes sem pedidos (ex: Felipe Santos) 
-- também apareçam no relatório com valores nulos nos campos de pedidos.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Email,
    p.PedidoID,
    p.DataPedido,
    p.Valor
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
ORDER BY c.ClienteID, p.PedidoID;

-- Exercício 2: Mostrar o valor total de pedidos por cliente.
-- Motivação: Agregação financeira com SUM. A função COALESCE converte NULL para 0.00
-- para clientes que ainda não efetuaram compras.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COALESCE(SUM(p.Valor), 0.00) AS ValorTotalPedidos
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY ValorTotalPedidos DESC;

-- Exercício 3: Exibir clientes sem pedidos.
-- Motivação: Padrão Anti-Join utilizando LEFT JOIN combinado com filtro IS NULL
-- na chave da tabela da direita.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Email
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.PedidoID IS NULL;

-- Exercício 4: Listar pedidos acima de 300 reais com nome do cliente.
-- Motivação: Junção interna (INNER JOIN) filtrada por predicado de valor monetário.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
WHERE p.Valor > 300.00
ORDER BY p.Valor DESC;

-- Exercício 5: Contar pedidos por cliente.
-- Motivação: O uso de COUNT(p.PedidoID) com LEFT JOIN contabiliza 0 quando o cliente
-- não possui pedidos (COUNT(coluna) ignora valores NULL).
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COUNT(p.PedidoID) AS TotalPedidos
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY TotalPedidos DESC, c.Nome;

-- Exercício 6: Listar clientes que fizeram pedidos em maio de 2026.
-- Motivação: Filtragem temporal com cláusula BETWEEN e DISTINCT para evitar
-- nomes duplicados caso o cliente tenha mais de um pedido no mesmo mês.
SELECT DISTINCT
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Email
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.DataPedido BETWEEN '2026-05-01' AND '2026-05-31'
ORDER BY c.Nome;

-- Exercício 7: Mostrar pedido mais caro de cada cliente.
-- Motivação: Utilização da função de agregação MAX() agrupada por cliente.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    MAX(p.Valor) AS MaiorValorPedido
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY MaiorValorPedido DESC;

-- Exercício 8: Listar clientes com mais de um pedido.
-- Motivação: Aplicação de filtro sobre agregados via cláusula HAVING.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COUNT(p.PedidoID) AS QuantidadePedidos
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
HAVING COUNT(p.PedidoID) > 1;

-- Exercício 9: Exibir pedidos com nome do cliente e data.
-- Motivação: Projeção direta dos atributos do pedido enriquecidos com a identificação do cliente.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
ORDER BY p.DataPedido, p.PedidoID;

-- Exercício 10: Calcular média de pedidos por cliente.
-- Motivação: Cálculo estatístico de média financeira com AVG() e arredondamento para 2 casas decimais.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    ROUND(AVG(p.Valor), 2) AS MediaValorPedidos
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY MediaValorPedidos DESC;
