--------------------------------------------------------------------------------
-- FACULDADE DE SISTEMAS DE INFORMAÇÃO
-- Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor: Prof. Welington Garcia
-- Atividade: Exercício - SubSelect - parte 2
-- Aluno / Desenvolvedor Sênior: Gabinete Acadêmico
-- Banco de Dados: PostgreSQL
--------------------------------------------------------------------------------

/* 
  PRÉ-REQUISITO: Estrutura padrão do banco de dados relacional para suporte 
  às consultas, simulações de DML e análises de desempenho (EXPLAIN ANALYZE).
*/

-- Criação das tabelas base (Caso o ambiente de testes precise inicializar o schema)
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    id_categoria INT REFERENCES categorias(id_categoria),
    preco NUMERIC(10, 2) NOT NULL,
    estoque INT NOT NULL
);

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    limite_credito NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES clientes(id_cliente),
    data_pedido DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id_pedido INT REFERENCES pedidos(id_pedido),
    id_produto INT REFERENCES produtos(id_produto),
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,
    PRIMARY KEY (id_pedido, id_produto)
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 1 — Subconsulta no FROM: média por categoria
-- Crie uma consulta que calcule, em uma subconsulta no FROM, o preço médio 
-- dos produtos de cada categoria. Na consulta externa, exiba apenas as 
-- categorias cuja média de preços seja superior a R$ 1.000,00.
--------------------------------------------------------------------------------

SELECT 
    cat_media.id_categoria,
    cat_media.nome_categoria,
    cat_media.preco_medio
FROM (
    SELECT 
        c.id_categoria,
        c.nome_categoria,
        AVG(p.preco) AS preco_medio
    FROM categorias c
    JOIN produtos p ON c.id_categoria = p.id_categoria
    GROUP BY c.id_categoria, c.nome_categoria
) AS cat_media
WHERE cat_media.preco_medio > 1000.00;


--------------------------------------------------------------------------------
-- EXERCÍCIO 2 — Tabela derivada: total de cada pedido
-- Crie uma subconsulta no FROM que calcule o valor total de cada pedido 
-- utilizando SUM(quantidade * preco_unitario). Na consulta externa, exiba 
-- apenas os pedidos cujo valor total seja superior a R$ 3.000,00.
--------------------------------------------------------------------------------

SELECT 
    t_pedidos.id_pedido,
    t_pedidos.valor_total
FROM (
    SELECT 
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS t_pedidos
WHERE t_pedidos.valor_total > 3000.00;


--------------------------------------------------------------------------------
-- EXERCÍCIO 3 — Subconsulta no FROM combinada com JOIN
-- Utilizando uma tabela derivada, calcule o valor total de cada pedido. 
-- Em seguida, relacione o resultado com as tabelas pedidos e clientes para 
-- exibir: código do pedido, nome do cliente, data do pedido e valor total.
--------------------------------------------------------------------------------

SELECT 
    p.id_pedido,
    c.nome_cliente,
    p.data_pedido,
    tp.valor_total
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN (
    SELECT 
        id_pedido,
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS tp ON p.id_pedido = tp.id_pedido;


--------------------------------------------------------------------------------
-- EXERCÍCIO 4 — Clientes que gastaram acima da média
-- Calcule o total gasto por cada cliente utilizando uma subconsulta. Depois, 
-- exiba apenas os clientes cujo total gasto seja superior à média de gastos 
-- de todos os clientes que realizaram compras.
--------------------------------------------------------------------------------

WITH gastos_clientes AS (
    SELECT 
        cli.id_cliente,
        cli.nome_cliente,
        SUM(ip.quantidade * ip.preco_unitario) AS total_gasto
    FROM clientes cli
    JOIN pedidos ped ON cli.id_cliente = ped.id_cliente
    JOIN itens_pedido ip ON ped.id_pedido = ip.id_pedido
    GROUP BY cli.id_cliente, cli.nome_cliente
)
SELECT 
    id_cliente,
    nome_cliente,
    total_gasto
FROM gastos_clientes
WHERE total_gasto > (
    SELECT AVG(total_gasto) 
    FROM gastos_clientes
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 5 — Categoria com maior preço médio
-- Crie uma consulta que determine qual categoria possui o maior preço médio 
-- entre seus produtos. Utilize uma subconsulta no FROM para calcular as médias 
-- e outra subconsulta para identificar o maior valor.
--------------------------------------------------------------------------------

SELECT 
    m_cat.id_categoria,
    m_cat.nome_categoria,
    m_cat.preco_medio
FROM (
    SELECT 
        c.id_categoria,
        c.nome_categoria,
        AVG(p.preco) AS preco_medio
    FROM categorias c
    JOIN produtos p ON c.id_categoria = p.id_categoria
    GROUP BY c.id_categoria, c.nome_categoria
) AS m_cat
WHERE m_cat.preco_medio = (
    SELECT MAX(preco_medio_cat)
    FROM (
        SELECT AVG(p2.preco) AS preco_medio_cat
        FROM produtos p2
        GROUP BY p2.id_categoria
    ) AS sub_max
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 6 — INSERT utilizando subconsulta
-- Crie uma nova tabela chamada produtos_promocao contendo os campos id_produto, 
-- nome_produto, preco e preco_promocional. Depois, utilizando INSERT INTO ... SELECT, 
-- insira nessa tabela todos os produtos cujo preço esteja acima da média geral. 
-- O preco_promocional deverá representar um desconto de 10% sobre o preço original.
--------------------------------------------------------------------------------

-- 1. Criação da tabela de promoção
CREATE TABLE IF NOT EXISTS produtos_promocao (
    id_produto INT PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    preco_promocional NUMERIC(10, 2) NOT NULL
);

-- 2. Inserção baseada em subconsulta com cálculo de 10% de desconto
INSERT INTO produtos_promocao (id_produto, nome_produto, preco, preco_promocional)
SELECT 
    id_produto,
    nome_produto,
    preco,
    preco * 0.90 AS preco_promocional
FROM produtos
WHERE preco > (
    SELECT AVG(preco) 
    FROM produtos
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 7 — UPDATE utilizando subconsulta
-- Crie um comando UPDATE que aumente em 10% o limite de crédito dos clientes 
-- que já realizaram pelo menos dois pedidos. A identificação desses clientes 
-- deverá ser feita por meio de uma subconsulta.
--------------------------------------------------------------------------------

-- A) SELECT prévio para conferência dos registros afetados
SELECT 
    c.id_cliente,
    c.nome_cliente,
    c.limite_credito,
    COUNT(p.id_pedido) AS total_pedidos
FROM clientes c
JOIN pedidos p ON c.id_cliente = p.id_cliente
GROUP BY c.id_cliente, c.nome_cliente, c.limite_credito
HAVING COUNT(p.id_pedido) >= 2;

-- B) Comando UPDATE utilizando subconsulta com IN
UPDATE clientes
SET limite_credito = limite_credito * 1.10
WHERE id_cliente IN (
    SELECT p.id_cliente
    FROM pedidos p
    GROUP BY p.id_cliente
    HAVING COUNT(p.id_pedido) >= 2
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 8 — UPDATE baseado na média da própria categoria
-- Atualize o estoque dos produtos cujo preço seja superior à média de preço de 
-- sua própria categoria. Para esses produtos, acrescente 5 unidades ao estoque atual. 
-- Utilize uma subconsulta correlacionada para comparar cada produto com a média de sua categoria.
--------------------------------------------------------------------------------

-- A) SELECT prévio (Subconsulta correlacionada equivalente)
SELECT 
    p.id_produto,
    p.nome_produto,
    p.preco,
    p.estoque,
    (SELECT AVG(p_sub.preco) FROM produtos p_sub WHERE p_sub.id_categoria = p.id_categoria) AS media_categoria
FROM produtos p
WHERE p.preco > (
    SELECT AVG(p_sub.preco) 
    FROM produtos p_sub 
    WHERE p_sub.id_categoria = p.id_categoria
);

-- B) Comando UPDATE com subconsulta correlacionada
UPDATE produtos p
SET estoque = estoque + 5
WHERE p.preco > (
    SELECT AVG(p_sub.preco)
    FROM produtos p_sub
    WHERE p_sub.id_categoria = p.id_categoria
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 9 — DELETE utilizando NOT EXISTS
-- Considere que seja necessário remover do cadastro todos os clientes que nunca 
-- realizaram pedidos. Crie o comando DELETE utilizando uma subconsulta com NOT EXISTS. 
-- Antes de executar o DELETE, escreva um SELECT utilizando a mesma condição para 
-- verificar quais registros seriam removidos.
--------------------------------------------------------------------------------

-- A) SELECT prévio para conferência dos clientes sem pedidos
SELECT 
    c.id_cliente,
    c.nome_cliente
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);

-- B) Comando DELETE utilizando NOT EXISTS
DELETE FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);


--------------------------------------------------------------------------------
-- EXERCÍCIO 10 — Análise de desempenho com EXPLAIN ANALYZE
-- Crie duas consultas que retornem os clientes que possuem pedidos: 
-- uma utilizando IN com subconsulta e outra utilizando EXISTS. 
-- Execute EXPLAIN ANALYZE antes de cada consulta e compare os planos de execução.
--------------------------------------------------------------------------------

/* 
  NOTA DO DESENVOLVEDOR SÊNIOR:
  O comando EXPLAIN ANALYZE executa de fato a query, exibindo o custo estimado inicial, 
  o custo total, o número real de linhas retornadas (actual rows), o tempo de execução 
  em milissegundos e as estratégias físicas adotadas pelo otimizador do PostgreSQL 
  (como Seq Scan, Hash Join, Nested Loop, Anti/Semi Join).
*/

-- Consulta 1: Utilizando IN com subconsulta
EXPLAIN ANALYZE
SELECT 
    id_cliente,
    nome_cliente
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente 
    FROM pedidos
);

-- Consulta 2: Utilizando EXISTS (Subconsulta correlacionada)
EXPLAIN ANALYZE
SELECT 
    c.id_cliente,
    c.nome_cliente
FROM clientes c
WHERE EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);

/*
  ANÁLISE COMPARATIVA DO PLANO DE EXECUÇÃO (EXPLAIN ANALYZE):
  1. Comportamento do IN: Em versões modernas do PostgreSQL, o otimizador frequentemente 
     converte cláusulas IN em um Semi Join ou Hash Join interno, otimizando drasticamente 
     o desempenho e equiparando-se ao comportamento do EXISTS. No entanto, se o subselect 
     retornar valores NULL, o IN pode apresentar comportamento complexo (três valores lógicos).
  2. Comportamento do EXISTS: O otimizador avalia a existência linha por linha (ou via otimização 
     de Hash Semi Join). Para cada cliente, o EXISTS cessa a busca na tabela de pedidos assim 
     que encontra o primeiro registro correspondente (short-circuit evaluation).
  3. Custos e Linhas: O custo estimado (Startup Cost / Total Cost) costuma ser inferior ou 
     muito próximo em ambas se houver chave estrangeira e índices adequados na coluna 
     'id_cliente' da tabela 'pedidos'. O tempo de execução real em bases volumétricas demonstra 
     que o EXISTS e o IN otimizado evitam gargalos de varredura completa (Seq Scan).
*/