/* ==============================================================================
   DISCIPLINA: Banco de Dados II (3º Semestre) - UniFEF
   PROFESSOR:  Prof. Guilherme de Morais
   TEMA:       Exemplos da Apresentação Teórica (Slides Aula 04 - Consultando Dados 1)
   ==============================================================================
   COMO EXECUTAR:
   1. Execute este script após popular o banco com 'schema_e_dados.sql'.
   2. Cada bloco demonstra um conceito específico discutido nos slides.
   ============================================================================== */

-- -----------------------------------------------------------------------------
-- SLIDES 3 e 4: SINTAXE GERAL E PROJEÇÃO BÁSICA
-- -----------------------------------------------------------------------------
-- Consulta de todos os campos de todos os registros da tabela CLIENTE
SELECT *
FROM CLIENTE;

-- Consulta de campos específicos de todos os registros
SELECT cod_cliente, nome, cidade
FROM CLIENTE;

-- -----------------------------------------------------------------------------
-- SLIDES 5 e 6: ORDENAÇÃO DE DADOS COM ORDER BY
-- -----------------------------------------------------------------------------
-- Ordenação alfabética simples pelo nome do cliente
SELECT *
FROM CLIENTE
ORDER BY nome;

-- Ordenação composta: primeiro alfabeticamente pelo nome, desempate pela idade
SELECT cod_cliente, nome, idade
FROM CLIENTE
ORDER BY nome, idade;

-- -----------------------------------------------------------------------------
-- SLIDES 7 e 8: FILTRANDO DADOS COM A CLÁUSULA WHERE
-- -----------------------------------------------------------------------------
-- Filtro de todas as colunas de clientes residentes em São Paulo
SELECT *
FROM CLIENTE
WHERE estado = 'SP';

-- Filtro com projeção selecionada e operadores de comparação disponíveis:
-- '=', '<>', '>', '<', '>=', '<='
SELECT cod_cliente, nome, cidade
FROM CLIENTE
WHERE estado = 'SP';

-- -----------------------------------------------------------------------------
-- SLIDES 9 e 10: COMBINANDO FILTRO (WHERE) E ORDENAÇÃO (ORDER BY)
-- Regra de Execução: O SGBD primeiro filtra as tuplas no disco (WHERE) e só então ordena o conjunto restante (ORDER BY).
-- -----------------------------------------------------------------------------
SELECT cod_cliente, nome, cidade
FROM CLIENTE
WHERE estado = 'SP'
ORDER BY nome;

-- -----------------------------------------------------------------------------
-- SLIDES 11 e 12: CONDIÇÕES SELETIVAS E OPERADORES RELACIONAIS
-- -----------------------------------------------------------------------------
SELECT descricao
FROM PRODUTO
WHERE valor_unitario >= 1.00;

SELECT *
FROM PEDIDO
WHERE cod_vendedor = 10;

-- -----------------------------------------------------------------------------
-- SLIDES 13 e 14: OPERADOR LÓGICO AND E A ARMADILHA DA CONDIÇÃO CONTRADITÓRIA
-- -----------------------------------------------------------------------------
-- Exemplo válido: faixa de valores com duas condições unidas por AND
SELECT descricao
FROM PRODUTO
WHERE valor_unitario >= 0.50
  AND valor_unitario <= 2.00;

-- ARMADILHA CLÁSSICA (Slide 14):
-- A consulta abaixo SEMPRE retornará conjunto vazio (0 linhas)!
-- Motivo: Para uma mesma linha (tupla), o campo 'cidade' é atômico e não pode ser 'Campinas' E 'São Paulo' simultaneamente.
SELECT nome
FROM CLIENTE
WHERE cidade = 'Campinas'
  AND cidade = 'São Paulo';

-- Forma correta (usando disjunção lógica OR):
SELECT nome
FROM CLIENTE
WHERE cidade = 'Campinas'
   OR cidade = 'São Paulo';

-- -----------------------------------------------------------------------------
-- SLIDES 15 e 16: OPERADOR LÓGICO OR E PRECEDÊNCIA COM PARÊNTESES
-- -----------------------------------------------------------------------------
-- Exemplo com OR simples
SELECT cod_vendedor, nome, salario
FROM VENDEDOR
WHERE salario = 2700.00
   OR salario = 3500.00;

-- Exercício resolvido do Slide 16:
-- Encontre a descrição, unidade e valor dos produtos cuja unidade seja 'M'
-- e os valores unitários sejam 0.11, 1.8 ou 2.
SELECT unidade, descricao, valor_unitario
FROM PRODUTO
WHERE unidade = 'KG'
  AND (valor_unitario = 5.50
   OR  valor_unitario = 7.20
   OR  valor_unitario = 12.00);

-- -----------------------------------------------------------------------------
-- SLIDE 17: ELIMINAÇÃO DE DUPLICATAS COM DISTINCT
-- -----------------------------------------------------------------------------
-- Remove repetições na projeção da coluna 'cidade'
SELECT DISTINCT cidade
FROM CLIENTE;

-- Distinct composto: elimina linhas onde o par (cidade, estado) seja idêntico
SELECT DISTINCT cidade, estado
FROM CLIENTE;

-- -----------------------------------------------------------------------------
-- SLIDES 18 e 19: EXPRESSÕES ARITMÉTICAS E APELIDOS (ALIASES)
-- -----------------------------------------------------------------------------
-- Simulação de reajuste salarial de 10% no vendedor
SELECT nome,
       salario AS "Salário Atual",
       ROUND(salario * 1.10, 2) AS "Salário com Reajuste"
FROM VENDEDOR;

-- Simulação de aumento de 25% nos produtos
SELECT descricao,
       unidade,
       valor_unitario AS "Preço Atual",
       ROUND(valor_unitario * 1.25, 2) AS "Preço com Aumento"
FROM PRODUTO;

-- Simulação de desconto de 12% em produtos de uma unidade específica
SELECT descricao,
       unidade,
       valor_unitario AS "Preço Atual",
       ROUND(valor_unitario - (valor_unitario * 0.12), 2) AS "Preço com Desconto"
FROM PRODUTO
WHERE unidade = 'KG';

-- -----------------------------------------------------------------------------
-- SLIDES 20 e 21: OPERADOR BETWEEN E ARMADILHA DE LIMITES INVERTIDOS
-- -----------------------------------------------------------------------------
-- Sintaxe correta: BETWEEN <limite_inferior> AND <limite_superior>
SELECT *
FROM VENDEDOR
WHERE salario BETWEEN 2000.00 AND 3000.00;

-- ARMADILHA DO BETWEEN INVERTIDO (Slide 20):
-- Em SQL padrão, 'BETWEEN A AND B' é avaliado internamente como 'campo >= A AND campo <= B'.
-- Se colocarmos 'BETWEEN 3000 AND 2000', a expressão torna-se 'salario >= 3000 AND salario <= 2000',
-- o que é matematicamente impossível para um escalar real, resultando em 0 linhas!
SELECT *
FROM VENDEDOR
WHERE salario BETWEEN 3000.00 AND 2000.00; -- ATENÇÃO: Retorna conjunto vazio propositalmente.

-- Exemplo do Slide 21: Faixa de valores decimais em produtos
SELECT cod_produto, descricao, valor_unitario
FROM PRODUTO
WHERE valor_unitario BETWEEN 0.32 AND 5.50;
