-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- PROFESSOR: Prof. Guilherme de Morais
-- TEMA: Aula 05 - SQL - CONSULTAS (11/03/2026)
-- COMO EXECUTAR: psql -U postgres -d seu_banco -f exercicios.sql
-- PRÉ-REQUISITO: Executar schema.sql previamente para popular as tabelas
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Exercício 1: Selecione todos os clientes cujo nome começa com a letra A.
-- Explicação: Utiliza-se o coringa '%' para indicar qualquer sequência de caracteres após 'A'.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE nome LIKE 'A%';

-- ----------------------------------------------------------------------------
-- Exercício 2: Liste os clientes cujo nome contém a letra "i" em qualquer posição.
-- Explicação: Coringas '%' antes e depois capturam a letra em qualquer posição.
-- Como o banco armazena em caixa alta, busca-se 'I' ou aplica-se UPPER.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE UPPER(nome) LIKE '%I%';

-- ----------------------------------------------------------------------------
-- Exercício 3: Selecione os clientes cujo nome termina com a letra "a".
-- Explicação: O coringa '%' no início garante que termine exatamente com 'A'.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE UPPER(nome) LIKE '%A';

-- ----------------------------------------------------------------------------
-- Exercício 4: Liste os clientes cujo nome tenha exatamente 5 caracteres.
-- Explicação: O caractere '_' representa exatamente um caractere qualquer.
-- 5 underlines ('_____') filtram nomes com tamanho estrito de 5 caracteres.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE nome LIKE '_____';

-- ----------------------------------------------------------------------------
-- Exercício 5: Selecione os clientes cujo nome começa com "Ma" e termina com "a".
-- Explicação: Combinação de prefixo 'MA' e sufixo 'A' com '%' intermediário.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE UPPER(nome) LIKE 'MA%A';

-- ----------------------------------------------------------------------------
-- Exercício 6: Liste os veículos cuja cor seja BRANCO.
-- Explicação: Comparação exata de string com o predicado de igualdade.
-- ----------------------------------------------------------------------------
SELECT chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli
FROM veiculos
WHERE UPPER(cor) = 'BRANCO';

-- ----------------------------------------------------------------------------
-- Exercício 7: Selecione os veículos cujo modelo contenha "O".
-- Explicação: Operador LIKE com coringas '%O%' para encontrar a letra 'O' no modelo.
-- ----------------------------------------------------------------------------
SELECT chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli
FROM veiculos
WHERE UPPER(modelo) LIKE '%O%';

-- ----------------------------------------------------------------------------
-- Exercício 8: Liste os veículos cuja placa termine com "9".
-- Explicação: O coringa '%9' filtra placas cujo último caractere numérico seja 9.
-- ----------------------------------------------------------------------------
SELECT chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli
FROM veiculos
WHERE placa LIKE '%9';

-- ----------------------------------------------------------------------------
-- Exercício 9: Selecione os clientes cujo endereço contenha "RUA 01".
-- Explicação: Filtra logradouros que possuam o padrão 'RUA 01' em qualquer segmento.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE UPPER(endereco) LIKE '%RUA 01%';

-- ----------------------------------------------------------------------------
-- Exercício 10: Repita o exercício 2 usando ILIKE para não diferenciar maiúsculas e minúsculas.
-- Explicação: O operador ILIKE é nativo do PostgreSQL e efetua busca insensível a maiúsculas/minúsculas.
-- ----------------------------------------------------------------------------
SELECT cod_cli, cpf, nome, endereco, cidade, estado, salario, idade
FROM clientes
WHERE nome ILIKE '%i%';

-- ----------------------------------------------------------------------------
-- Exercício 11: Calcule a média dos salários dos clientes.
-- Explicação: A função AVG calcula a média aritmética dos valores não nulos.
-- ----------------------------------------------------------------------------
SELECT ROUND(AVG(salario), 2) AS media_salarial
FROM clientes;

-- ----------------------------------------------------------------------------
-- Exercício 12: Conte quantos clientes existem na tabela.
-- Explicação: A função COUNT(*) computa o número total de registros da relação.
-- ----------------------------------------------------------------------------
SELECT COUNT(*) AS total_clientes
FROM clientes;

-- ----------------------------------------------------------------------------
-- Exercício 13: Conte quantos clientes moram na cidade de FERNANDÓPOLIS.
-- Explicação: COUNT com cláusula WHERE filtrando a cidade desejada.
-- ----------------------------------------------------------------------------
SELECT COUNT(*) AS total_clientes_fernandopolis
FROM clientes
WHERE UPPER(cidade) = 'FERNANDOPOLIS';

-- ----------------------------------------------------------------------------
-- Exercício 14: Selecione o maior salário entre os clientes.
-- Explicação: MAX identifica o valor máximo presente na coluna indicada.
-- ----------------------------------------------------------------------------
SELECT MAX(salario) AS maior_salario
FROM clientes;

-- ----------------------------------------------------------------------------
-- Exercício 15: Selecione o menor salário entre os clientes.
-- Explicação: MIN identifica o valor mínimo presente na coluna indicada.
-- ----------------------------------------------------------------------------
SELECT MIN(salario) AS menor_salario
FROM clientes;

-- ----------------------------------------------------------------------------
-- Exercício 16: Calcule a soma dos salários de todos os clientes.
-- Explicação: SUM totaliza o somatório numérico de todos os salários.
-- ----------------------------------------------------------------------------
SELECT SUM(salario) AS soma_salarios
FROM clientes;

-- ----------------------------------------------------------------------------
-- Exercício 17: Selecione o maior preço de venda dos veículos.
-- Explicação: MAX aplicado ao atributo preco_venda da tabela veiculos.
-- ----------------------------------------------------------------------------
SELECT MAX(preco_venda) AS maior_preco_venda
FROM veiculos;

-- ----------------------------------------------------------------------------
-- Exercício 18: Selecione o menor preço de compra dos veículos.
-- Explicação: MIN aplicado ao atributo preco_compra da tabela veiculos.
-- ----------------------------------------------------------------------------
SELECT MIN(preco_compra) AS menor_preco_compra
FROM veiculos;

-- ----------------------------------------------------------------------------
-- Exercício 19: Liste a média salarial por cidade.
-- Explicação: A cláusula GROUP BY agrupa os clientes por cidade e calcula a média salarial de cada grupo.
-- ----------------------------------------------------------------------------
SELECT cidade, ROUND(AVG(salario), 2) AS media_salarial
FROM clientes
GROUP BY cidade
ORDER BY media_salarial DESC;

-- ----------------------------------------------------------------------------
-- Exercício 20: Mostre o maior e menor preço de venda por marca de veículo.
-- Explicação: GROUP BY por marca combinando as funções de agregação MAX e MIN.
-- ----------------------------------------------------------------------------
SELECT marca, 
       MAX(preco_venda) AS maior_preco_venda, 
       MIN(preco_venda) AS menor_preco_venda
FROM veiculos
GROUP BY marca
ORDER BY marca ASC;
