-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre) - UniFEF
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Resolução dos Exercícios com SQL de Duas Tabelas
-- Execução   : Execute cada bloco de consulta sequencialmente após a criação
--              das respectivas tabelas e inserção dos dados de teste.
-- =============================================================================

-- =============================================================================
-- BLOCO 1: Exercícios baseados no Cenário 1 (Tabela Clientes com Email)
-- =============================================================================

-- Exercício 1: Listar todos os clientes e seus pedidos.
-- Motivação: Correlacionar cada pedido ao respectivo cliente via chave estrangeira.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    p.PedidoID,
    p.DataPedido,
    p.Valor
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
ORDER BY c.ClienteID, p.DataPedido;

-- Exercício 2: Mostrar o valor total de pedidos por cliente.
-- Motivação: Agregar os valores com SUM e agrupar com GROUP BY. Utiliza-se LEFT JOIN
-- para contemplar clientes que não possuem pedidos (retornando total zero com COALESCE).
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COALESCE(SUM(p.Valor), 0.00) AS ValorTotalPedidos
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY ValorTotalPedidos DESC;

-- Exercício 3: Exibir clientes sem pedidos.
-- Motivação: Identificar registros da tabela pai sem correspondência na tabela filha.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Email
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.PedidoID IS NULL;

-- Exercício 4: Listar pedidos acima de 300 reais com nome do cliente.
-- Motivação: Filtrar registros na junção aplicando restrição numérica com WHERE.
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
-- Motivação: Contar a quantidade de pedidos usando COUNT(p.PedidoID).
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COUNT(p.PedidoID) AS QuantidadePedidos
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY QuantidadePedidos DESC;

-- Exercício 6: Listar clientes que fizeram pedidos em maio de 2026.
-- Motivação: Filtrar por intervalo de datas e eliminar duplicidades com DISTINCT.
SELECT DISTINCT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Cidade
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.DataPedido BETWEEN '2026-05-01' AND '2026-05-31'
ORDER BY c.Nome;

-- Exercício 7: Mostrar pedido mais caro de cada cliente.
-- Motivação: Encontrar o teto de gastos por cliente através da função MAX.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    MAX(p.Valor) AS ValorMaiorPedido
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY ValorMaiorPedido DESC;

-- Exercício 8: Listar clientes com mais de um pedido.
-- Motivação: Filtrar agregações através da cláusula HAVING após o agrupamento.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COUNT(p.PedidoID) AS TotalPedidos
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
HAVING COUNT(p.PedidoID) > 1;

-- Exercício 9: Exibir pedidos com nome do cliente e data.
-- Motivação: Projeção de colunas específicas de ambas as tabelas em uma junção.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
ORDER BY p.DataPedido;

-- Exercício 10: Calcular média de pedidos por cliente.
-- Motivação: Determinar o ticket médio por cliente através da função AVG.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    ROUND(AVG(p.Valor), 2) AS MediaValorPedidos
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY MediaValorPedidos DESC;


-- =============================================================================
-- BLOCO 2: Exercícios baseados no Cenário 2 (Tabela Clientes com Estado)
-- =============================================================================

-- Recriação das tabelas para o Cenário 2
DROP TABLE IF EXISTS Pedidos;
DROP TABLE IF EXISTS Clientes;

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

INSERT INTO Clientes (ClienteID, Nome, Cidade, Estado) VALUES
(1, 'Ana Silva', 'São Paulo', 'SP'),
(2, 'Carlos Souza', 'Rio de Janeiro', 'RJ'),
(3, 'Mariana Costa', 'Belo Horizonte', 'MG'),
(4, 'João Pereira', 'Curitiba', 'PR'),
(5, 'Fernanda Lima', 'Fortaleza', 'CE'),
(6, 'Ricardo Alves', 'Salvador', 'BA'),
(7, 'Patrícia Gomes', 'Recife', 'PE'),
(8, 'Felipe Rocha', 'Porto Alegre', 'RS'),
(9, 'Beatriz Martins', 'Campinas', 'SP'),
(10, 'Lucas Fernandes', 'Manaus', 'AM');

INSERT INTO Pedidos (PedidoID, ClienteID, DataPedido, Valor) VALUES
(101, 1, '2026-05-01', 1500.00),
(102, 1, '2026-05-05', 2300.00),
(103, 2, '2026-05-03', 500.00),
(104, 3, '2026-05-04', 1200.00),
(105, 4, '2026-05-06', 800.00),
(106, 5, '2026-05-07', 950.00),
(107, 6, '2026-05-08', 2000.00),
(108, 7, '2026-05-09', 300.00),
(109, 8, '2026-05-10', 1800.00),
(110, 10, '2026-05-11', 2200.00);

-- Exercício 11: Mostrar pedidos com nome do cliente.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
ORDER BY p.PedidoID;

-- Exercício 12: Listar clientes que têm pedidos.
SELECT DISTINCT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Estado
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
ORDER BY c.ClienteID;

-- Exercício 13: Total gasto por cliente.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COALESCE(SUM(p.Valor), 0.00) AS TotalGasto
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY TotalGasto DESC;

-- Exercício 14: Clientes sem pedidos com os cidade, nome dos clientes.
SELECT 
    c.Nome AS NomeCliente,
    c.Cidade,
    c.Estado
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
WHERE p.PedidoID IS NULL;

-- Exercício 15: Pedidos acima de 1000 reais com os nomes dos clientes.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.Valor,
    p.DataPedido
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
WHERE p.Valor > 1000.00
ORDER BY p.Valor DESC;

-- Exercício 16: Quantidade de pedidos por cliente.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    COUNT(p.PedidoID) AS QuantidadePedidos
FROM Clientes c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY QuantidadePedidos DESC;

-- Exercício 17: Pedidos realizados em maio de 2026.
SELECT 
    p.PedidoID,
    c.Nome AS NomeCliente,
    p.DataPedido,
    p.Valor
FROM Pedidos p
INNER JOIN Clientes c ON p.ClienteID = c.ClienteID
WHERE p.DataPedido BETWEEN '2026-05-01' AND '2026-05-31'
ORDER BY p.DataPedido;

-- Exercício 18: Maior pedido por cliente.
SELECT 
    c.ClienteID,
    c.Nome AS NomeCliente,
    MAX(p.Valor) AS MaiorPedido
FROM Clientes c
INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY MaiorPedido DESC;
