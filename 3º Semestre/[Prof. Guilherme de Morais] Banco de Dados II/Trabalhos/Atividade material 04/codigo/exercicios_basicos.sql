/* ==============================================================================
   DISCIPLINA: Banco de Dados II (3º Semestre) - UniFEF
   PROFESSOR:  Prof. Guilherme de Morais
   TEMA:       Resolução da Lista de Exercícios Básicos (1 a 25) - Material 04
   ==============================================================================
   COMO EXECUTAR:
   1. Certifique-se de ter executado previamente o script 'schema_e_dados.sql'.
   2. Execute cada consulta individualmente ou em bloco para verificar os resultados.
   ============================================================================== */

-- -----------------------------------------------------------------------------
-- Exercício 1: Liste todos os dados da tabela CLIENTE.
-- Conceito: Projeção irrestrita utilizando o predicado '*'. Recupera todas as colunas de todas as tuplas.
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE;

-- -----------------------------------------------------------------------------
-- Exercício 2: Liste apenas nome e idade dos clientes.
-- Conceito: Projeção restrita de colunas no plano relacional, reduzindo o tráfego de dados e uso de memória.
-- -----------------------------------------------------------------------------
SELECT nome, idade
FROM CLIENTE;

-- -----------------------------------------------------------------------------
-- Exercício 3: Liste código e descrição dos produtos.
-- Conceito: Projeção de atributos chave e identificadores descritivos.
-- -----------------------------------------------------------------------------
SELECT cod_produto, descricao
FROM PRODUTO;

-- -----------------------------------------------------------------------------
-- Exercício 4: Liste os clientes que moram no estado SP.
-- Conceito: Seleção relacional (WHERE) com predicado de igualdade em coluna literal CHAR(2).
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
WHERE estado = 'SP';

-- -----------------------------------------------------------------------------
-- Exercício 5: Liste os clientes com idade maior que 35 anos.
-- Conceito: Operador relacional '>' aplicado a dado numérico inteiro.
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
WHERE idade > 35;

-- -----------------------------------------------------------------------------
-- Exercício 6: Liste os produtos com valor unitário menor que 10.
-- Conceito: Operador relacional '<' aplicado a tipos de ponto fixo / monetários.
-- -----------------------------------------------------------------------------
SELECT *
FROM PRODUTO
WHERE valor_unitario < 10.00;

-- -----------------------------------------------------------------------------
-- Exercício 7: Liste os vendedores com salário maior ou igual a 3000.
-- Conceito: Operador relacional de fronteira inclusiva '>='.
-- -----------------------------------------------------------------------------
SELECT *
FROM VENDEDOR
WHERE salario >= 3000.00;

-- -----------------------------------------------------------------------------
-- Exercício 8: Liste todos os clientes ordenados por nome.
-- Conceito: Cláusula ORDER BY simples. O padrão léxico ascendente (ASC) é implícito.
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
ORDER BY nome ASC;

-- -----------------------------------------------------------------------------
-- Exercício 9: Liste os clientes ordenados por estado e idade.
-- Conceito: Ordenação composta (multinível). Desempata tuplas do mesmo estado pelo critério de idade.
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
ORDER BY estado ASC, idade ASC;

-- -----------------------------------------------------------------------------
-- Exercício 10: Liste os produtos ordenados pelo valor unitário (do maior para o menor).
-- Conceito: Ordenação decrescente explícita via modificador DESC.
-- -----------------------------------------------------------------------------
SELECT *
FROM PRODUTO
ORDER BY valor_unitario DESC;

-- -----------------------------------------------------------------------------
-- Exercício 11: Liste os clientes do estado SP e com idade maior que 30.
-- Conceito: Conjunção booleana (AND). Ambas as condições devem ser estritamente verdadeiras.
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
WHERE estado = 'SP'
  AND idade > 30;

-- -----------------------------------------------------------------------------
-- Exercício 12: Liste os clientes do estado RJ ou MG.
-- Conceito: Disjunção booleana (OR). A tupla é selecionada se satisfizer ao menos uma das alternativas.
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
WHERE estado = 'RJ'
   OR estado = 'MG';

-- -----------------------------------------------------------------------------
-- Exercício 13: Liste os produtos com valor maior que 10 e unidade 'KG'.
-- Conceito: Filtragem combinada entre magnitude numérica e classificação categórica.
-- -----------------------------------------------------------------------------
SELECT *
FROM PRODUTO
WHERE valor_unitario > 10.00
  AND unidade = 'KG';

-- -----------------------------------------------------------------------------
-- Exercício 14: Liste os vendedores com salário igual a 2800 ou 3000.
-- Conceito: Disjunção inclusiva pontual sobre a mesma coluna salarial.
-- -----------------------------------------------------------------------------
SELECT *
FROM VENDEDOR
WHERE salario = 2800.00
   OR salario = 3000.00;

-- -----------------------------------------------------------------------------
-- Exercício 15: Liste os produtos cuja unidade seja 'UN' e valor menor que 10.
-- Conceito: Conjunção lógica (AND) associando restrição literal de unidade e teto financeiro.
-- -----------------------------------------------------------------------------
SELECT *
FROM PRODUTO
WHERE unidade = 'UN'
  AND valor_unitario < 10.00;

-- -----------------------------------------------------------------------------
-- Exercício 16: Liste todas as cidades distintas dos clientes.
-- Conceito: Cláusula DISTINCT para supressão de duplicidades escalares no conjunto resultante.
-- -----------------------------------------------------------------------------
SELECT DISTINCT cidade
FROM CLIENTE;

-- -----------------------------------------------------------------------------
-- Exercício 17: Liste todos os estados distintos cadastrados.
-- Conceito: Deduplicação de unidades federativas presentes na base populada.
-- -----------------------------------------------------------------------------
SELECT DISTINCT estado
FROM CLIENTE;

-- -----------------------------------------------------------------------------
-- Exercício 18: Liste os produtos com valor entre 5 e 20.
-- Conceito: Operador BETWEEN para faixa fechada [5.00, 20.00] (inclusivo em ambos os limites).
-- -----------------------------------------------------------------------------
SELECT *
FROM PRODUTO
WHERE valor_unitario BETWEEN 5.00 AND 20.00;

-- -----------------------------------------------------------------------------
-- Exercício 19: Liste os vendedores com salário entre 2500 e 3500.
-- Conceito: Operador BETWEEN aplicado a intervalo de remuneração fixa.
-- -----------------------------------------------------------------------------
SELECT *
FROM VENDEDOR
WHERE salario BETWEEN 2500.00 AND 3500.00;

-- -----------------------------------------------------------------------------
-- Exercício 20: Liste os clientes com idade entre 25 e 40 anos.
-- Conceito: Operador BETWEEN aplicado a inteiros (equivalente a: idade >= 25 AND idade <= 40).
-- -----------------------------------------------------------------------------
SELECT *
FROM CLIENTE
WHERE idade BETWEEN 25 AND 40;

-- -----------------------------------------------------------------------------
-- Exercício 21: Mostre o nome do produto e o valor com aumento de 20%.
-- Conceito: Expressão aritmética de projeção computada (multiplicação por 1.20) com apelido de coluna (ALIAS).
-- -----------------------------------------------------------------------------
SELECT descricao,
       valor_unitario AS valor_original,
       ROUND(valor_unitario * 1.20, 2) AS valor_com_aumento
FROM PRODUTO;

-- -----------------------------------------------------------------------------
-- Exercício 22: Mostre o nome do produto e o valor com desconto de 10%.
-- Conceito: Cálculo aritmético proporcional de redução (multiplicação por 0.90 ou valor - (valor * 0.10)).
-- -----------------------------------------------------------------------------
SELECT descricao,
       valor_unitario AS valor_original,
       ROUND(valor_unitario * 0.90, 2) AS valor_com_desconto
FROM PRODUTO;

-- -----------------------------------------------------------------------------
-- Exercício 23: Liste os vendedores com salário e o salário com bônus de 500 reais.
-- Conceito: Operador aritmético de adição (+) sobre coluna numérica contínua.
-- -----------------------------------------------------------------------------
SELECT nome,
       salario AS salario_base,
       (salario + 500.00) AS salario_com_bonus
FROM VENDEDOR;

-- -----------------------------------------------------------------------------
-- Exercício 24: Liste todos os pedidos realizados por um cliente específico (ex: cliente 10).
-- Conceito: Filtragem relacional em tabela de fatos/transações via chave estrangeira.
-- -----------------------------------------------------------------------------
SELECT *
FROM PEDIDO
WHERE cod_cliente = 10;

-- -----------------------------------------------------------------------------
-- Exercício 25: Liste todos os pedidos realizados após a data '2024-02-01'.
-- Conceito: Operador relacional de comparação cronológica estrita '>' sobre tipo DATE no formato ISO 8601.
-- -----------------------------------------------------------------------------
SELECT *
FROM PEDIDO
WHERE data_pedido > '2024-02-01'
ORDER BY data_pedido ASC;
