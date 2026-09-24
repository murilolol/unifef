/* ==============================================================================
   DISCIPLINA: Banco de Dados II (3º Semestre) - UniFEF
   PROFESSOR:  Prof. Guilherme de Morais
   TEMA:       Resolução dos Exercícios Complementares e Desafios (1 a 10) - Material 04
   ==============================================================================
   COMO EXECUTAR:
   1. Certifique-se de ter executado previamente o script 'schema_e_dados.sql'.
   2. Execute cada consulta para estudar a composição avançada de cláusulas SQL.
   ============================================================================== */

-- -----------------------------------------------------------------------------
-- Exercício 26 (Avançado 1):
-- Liste o nome dos clientes, cidade e idade, apenas daqueles:
--   • que moram em SP
--   • e possuem idade entre 25 e 40 anos
--   • ordenados por idade (decrescente)
-- -----------------------------------------------------------------------------
SELECT nome,
       cidade,
       idade
FROM CLIENTE
WHERE estado = 'SP'
  AND idade BETWEEN 25 AND 40
ORDER BY idade DESC;

-- -----------------------------------------------------------------------------
-- Exercício 27 (Avançado 2):
-- Liste os produtos contendo:
--   • descrição
--   • valor atual
--   • valor com aumento de 30% (usar alias)
-- Filtrar apenas produtos:
--   • com valor entre 5 e 20
-- -----------------------------------------------------------------------------
SELECT descricao,
       valor_unitario AS valor_atual,
       ROUND(valor_unitario * 1.30, 2) AS valor_aumento_30
FROM PRODUTO
WHERE valor_unitario BETWEEN 5.00 AND 20.00;

-- -----------------------------------------------------------------------------
-- Exercício 28 (Avançado 3):
-- Liste os vendedores com:
--   • nome
--   • salário atual
--   • salário com bônus de 15%
-- Somente vendedores:
--   • com salário entre 2500 e 3500
-- Ordenar pelo maior salário ajustado
-- -----------------------------------------------------------------------------
SELECT nome,
       salario AS salario_atual,
       ROUND(salario * 1.15, 2) AS salario_ajustado
FROM VENDEDOR
WHERE salario BETWEEN 2500.00 AND 3500.00
ORDER BY salario_ajustado DESC;

-- -----------------------------------------------------------------------------
-- Exercício 29 (Avançado 4):
-- Liste os clientes que:
--   • moram em SP ou RJ
--   • e têm idade maior que 30
-- Exibir: nome, estado e idade. Ordenar por estado e nome.
-- Nota Didática: O uso de parênteses em (estado = 'SP' OR estado = 'RJ') é indispensável,
-- pois o operador AND possui precedência natural mais alta que o OR.
-- -----------------------------------------------------------------------------
SELECT nome,
       estado,
       idade
FROM CLIENTE
WHERE (estado = 'SP' OR estado = 'RJ')
  AND idade > 30
ORDER BY estado ASC, nome ASC;

-- -----------------------------------------------------------------------------
-- Exercício 30 (Avançado 5):
-- Liste os produtos que:
--   • possuem unidade 'KG'
--   • e valor menor que 10 ou maior que 30
-- Exibir: descrição, unidade e valor.
-- -----------------------------------------------------------------------------
SELECT descricao,
       unidade,
       valor_unitario
FROM PRODUTO
WHERE unidade = 'KG'
  AND (valor_unitario < 10.00 OR valor_unitario > 30.00);

-- -----------------------------------------------------------------------------
-- Exercício 31 (Avançado 6):
-- Liste os pedidos que:
--   • foram realizados após '2024-02-10'
--   • e pertencem a vendedores com código entre 10 e 30
-- Ordenar por data do pedido.
-- -----------------------------------------------------------------------------
SELECT cod_pedido,
       cod_cliente,
       cod_vendedor,
       data_pedido
FROM PEDIDO
WHERE data_pedido > '2024-02-10'
  AND cod_vendedor BETWEEN 10 AND 30
ORDER BY data_pedido ASC;

-- -----------------------------------------------------------------------------
-- Exercício 32 (Avançado 7 - Desafio Raciocínio Lógico sem GROUP BY):
-- Liste os clientes distintos por cidade, exibindo:
--   • cidade
--   • quantidade de clientes
--
-- Abordagem A (Solução conforme o desafio: Subconsulta Correlacionada Escalar):
-- Permite calcular a frequência de cada cidade sobre uma lista de cidades distintas,
-- sem utilizar a cláusula de agregação formal GROUP BY.
-- -----------------------------------------------------------------------------
SELECT DISTINCT c1.cidade,
       (SELECT COUNT(*)
        FROM CLIENTE c2
        WHERE c2.cidade = c1.cidade) AS quantidade_clientes
FROM CLIENTE c1
ORDER BY quantidade_clientes DESC, c1.cidade ASC;

-- Abordagem B (Solução Canônica em Produção via GROUP BY):
SELECT cidade,
       COUNT(*) AS quantidade_clientes
FROM CLIENTE
GROUP BY cidade
ORDER BY quantidade_clientes DESC, cidade ASC;

-- -----------------------------------------------------------------------------
-- Exercício 33 (Avançado 8):
-- Liste os produtos com:
--   • valor atual
--   • valor com desconto de 20%
--   • valor com aumento de 10%
-- Filtrar apenas:
--   • produtos com unidade 'UN'
-- -----------------------------------------------------------------------------
SELECT descricao,
       valor_unitario AS valor_atual,
       ROUND(valor_unitario * 0.80, 2) AS valor_desconto_20,
       ROUND(valor_unitario * 1.10, 2) AS valor_aumento_10
FROM PRODUTO
WHERE unidade = 'UN';

-- -----------------------------------------------------------------------------
-- Exercício 34 (Avançado 9):
-- Liste os vendedores que:
--   • NÃO possuem salário entre 2800 e 3200
-- Exibir: nome e salário. Ordenar por salário crescente.
-- -----------------------------------------------------------------------------
-- Sintaxe com NOT BETWEEN:
SELECT nome,
       salario
FROM VENDEDOR
WHERE salario NOT BETWEEN 2800.00 AND 3200.00
ORDER BY salario ASC;

-- Sintaxe alternativa com operadores relacionais disjuntivos:
-- SELECT nome, salario FROM VENDEDOR WHERE salario < 2800.00 OR salario > 3200.00 ORDER BY salario ASC;

-- -----------------------------------------------------------------------------
-- Exercício 35 (Avançado 10 - Desafio de Alto Nível):
-- Liste os pedidos exibindo:
--   • código do pedido
--   • código do cliente
--   • código do vendedor
-- Com as condições:
--   • pedidos realizados em fevereiro de 2024
--   • cliente entre 10 e 30
--   • vendedor com salário maior que 3000
-- Ordenar por: cliente e data do pedido
--
-- Abordagem A (Via Subconsulta IN - Compatível com o escopo atual de DQL simples):
-- -----------------------------------------------------------------------------
SELECT cod_pedido,
       cod_cliente,
       cod_vendedor
FROM PEDIDO
WHERE data_pedido BETWEEN '2024-02-01' AND '2024-02-29'
  AND cod_cliente BETWEEN 10 AND 30
  AND cod_vendedor IN (
      SELECT cod_vendedor
      FROM VENDEDOR
      WHERE salario > 3000.00
  )
ORDER BY cod_cliente ASC, data_pedido ASC;

-- Abordagem B (Via Junção ANSI INNER JOIN - Padrão de Engenharia de Software):
SELECT p.cod_pedido,
       p.cod_cliente,
       p.cod_vendedor
FROM PEDIDO p
INNER JOIN VENDEDOR v ON p.cod_vendedor = v.cod_vendedor
WHERE p.data_pedido BETWEEN '2024-02-01' AND '2024-02-29'
  AND p.cod_cliente BETWEEN 10 AND 30
  AND v.salario > 3000.00
ORDER BY p.cod_cliente ASC, p.data_pedido ASC;
