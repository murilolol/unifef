-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre) - UniFEF
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Resolução dos 20 Exercícios Práticos de Consultas SQL
-- Como Executar: Certifique-se de ter executado 'exemplos.sql' previamente.
--                mysql -u seu_usuario -p EscolaDB < exercicios.sql
-- =============================================================================

USE EscolaDB;

-- -----------------------------------------------------------------------------
-- Bloco 1: SELECT basico
-- -----------------------------------------------------------------------------

-- Exercício 1: Liste todos os dados da tabela CLIENTES.
SELECT CodCliente, NomeCliente, EndCliente, Estado, Idade
FROM CLIENTES;

-- Exercício 2: Liste apenas NomeCliente e Estado.
SELECT NomeCliente, Estado
FROM CLIENTES;

-- -----------------------------------------------------------------------------
-- Bloco 2: ORDER BY
-- -----------------------------------------------------------------------------

-- Exercício 3: Liste os clientes ordenados por nome.
SELECT CodCliente, NomeCliente, EndCliente, Estado, Idade
FROM CLIENTES
ORDER BY NomeCliente ASC;

-- Exercício 4: Liste clientes ordenados por estado e idade.
SELECT CodCliente, NomeCliente, EndCliente, Estado, Idade
FROM CLIENTES
ORDER BY Estado ASC, Idade ASC;

-- -----------------------------------------------------------------------------
-- Bloco 3: WHERE
-- -----------------------------------------------------------------------------

-- Exercício 5: Liste clientes do estado 'SP'.
SELECT CodCliente, NomeCliente, EndCliente, Estado, Idade
FROM CLIENTES
WHERE Estado = 'SP';

-- Exercício 6: Liste clientes com idade maior que 25.
SELECT CodCliente, NomeCliente, EndCliente, Estado, Idade
FROM CLIENTES
WHERE Idade > 25;

-- -----------------------------------------------------------------------------
-- Bloco 4: WHERE + ORDER BY
-- -----------------------------------------------------------------------------

-- Exercício 7: Liste clientes de SP ordenados pelo nome.
SELECT CodCliente, NomeCliente, EndCliente, Estado, Idade
FROM CLIENTES
WHERE Estado = 'SP'
ORDER BY NomeCliente ASC;

-- -----------------------------------------------------------------------------
-- Bloco 5: Operadores relacionais
-- -----------------------------------------------------------------------------

-- Exercício 8: Liste produtos com valor maior que 2.00.
SELECT CodigoProduto, Descricao, Unidade, Val_Unit
FROM PRODUTO
WHERE Val_Unit > 2.00;

-- Exercício 9: Liste produtos com valor menor ou igual a 2.00.
SELECT CodigoProduto, Descricao, Unidade, Val_Unit
FROM PRODUTO
WHERE Val_Unit <= 2.00;

-- -----------------------------------------------------------------------------
-- Bloco 6: AND
-- -----------------------------------------------------------------------------

-- Exercício 10: Liste produtos com valor entre 0.50 e 10.00.
SELECT CodigoProduto, Descricao, Unidade, Val_Unit
FROM PRODUTO
WHERE Val_Unit >= 0.50 AND Val_Unit <= 10.00;

-- Exercício 11: Liste alunos de Campinas com data de nascimento após 1990.
SELECT Matricula, Nome_Aluno, Data_Nasc, Cidade
FROM ALUNO
WHERE Cidade = 'Campinas'
  AND Data_Nasc > '1990-12-31';

-- -----------------------------------------------------------------------------
-- Bloco 7: OR
-- -----------------------------------------------------------------------------

-- Exercício 12: Liste vendedores com salário 2500 ou 3000.
SELECT CodigoVendedor, NomeVendedor, Salario_Fixo
FROM VENDEDOR
WHERE Salario_Fixo = 2500.00 OR Salario_Fixo = 3000.00;

-- Exercício 13: Liste produtos com valor 0.50 ou 2.00.
SELECT CodigoProduto, Descricao, Unidade, Val_Unit
FROM PRODUTO
WHERE Val_Unit = 0.50 OR Val_Unit = 2.00;

-- -----------------------------------------------------------------------------
-- Bloco 8: AND + OR
-- -----------------------------------------------------------------------------

-- Exercício 14: Liste produtos da unidade 'M' com valor 0.50 ou 2.00.
SELECT CodigoProduto, Descricao, Unidade, Val_Unit
FROM PRODUTO
WHERE Unidade = 'M'
  AND (Val_Unit = 0.50 OR Val_Unit = 2.00);

-- -----------------------------------------------------------------------------
-- Bloco 9: DISTINCT
-- -----------------------------------------------------------------------------

-- Exercício 15: Liste os estados distintos dos clientes.
SELECT DISTINCT Estado
FROM CLIENTES;

-- -----------------------------------------------------------------------------
-- Bloco 10: Operadores aritméticos
-- -----------------------------------------------------------------------------

-- Exercício 16: Mostre salário com aumento de 10%.
SELECT CodigoVendedor,
       NomeVendedor,
       Salario_Fixo,
       Salario_Fixo * 1.10 AS Salario_Com_Aumento
FROM VENDEDOR;

-- Exercício 17: Mostre preço dos produtos com aumento de 25%.
SELECT CodigoProduto,
       Descricao,
       Val_Unit,
       Val_Unit * 1.25 AS Preco_Com_Aumento
FROM PRODUTO;

-- Exercício 18: Mostre preço dos produtos com desconto de 12% (unidade 'M').
SELECT CodigoProduto,
       Descricao,
       Unidade,
       Val_Unit,
       Val_Unit * (1 - 0.12) AS Preco_Com_Desconto
FROM PRODUTO
WHERE Unidade = 'M';

-- -----------------------------------------------------------------------------
-- Bloco 11: BETWEEN
-- -----------------------------------------------------------------------------

-- Exercício 19: Liste vendedores com salário entre 2000 e 4000.
SELECT CodigoVendedor, NomeVendedor, Salario_Fixo
FROM VENDEDOR
WHERE Salario_Fixo BETWEEN 2000.00 AND 4000.00;

-- Exercício 20: Liste alunos nascidos entre 1985 e 2000.
SELECT Matricula, Nome_Aluno, Data_Nasc, Cidade
FROM ALUNO
WHERE Data_Nasc BETWEEN '1985-01-01' AND '2000-12-31';
