-- ====================================================================
-- Disciplina: Banco de Dados II (3º Semestre)
-- Professor : Prof. Guilherme de Morais
-- Tema      : Resolução dos Exercícios de Data, Hora e Strings
-- SGBD      : PostgreSQL
-- Execução  : psql -U postgres -d seu_banco -f exercicios.sql
-- ====================================================================

-- Garantia da existência da tabela e dos dados para os exercícios
DROP TABLE IF EXISTS funcionarios CASCADE;

CREATE TABLE funcionarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    cidade VARCHAR(100) NOT NULL
);

INSERT INTO funcionarios (nome, data_nascimento, cidade) VALUES
('Carlos Silva', '1990-05-10', 'São Paulo'),
('Ana Souza', '1985-08-22', 'Rio de Janeiro'),
('João Lima', '2000-01-15', 'Belo Horizonte');

-- --------------------------------------------------------------------
-- 4. EXERCÍCIOS PROPOSTOS
-- --------------------------------------------------------------------

-- NÍVEL BÁSICO

-- Exercício 1: Exiba a data e hora atual.
SELECT NOW() AS data_hora_atual;

-- Exercício 2: Mostre apenas a data atual.
SELECT DATE(NOW()) AS data_atual;

-- Exercício 3: Converta a frase "banco de dados" para maiúsculo.
SELECT UPPER('banco de dados') AS texto_maiusculo;

-- Exercício 4: Mostre o tamanho da palavra "Universidade".
SELECT LENGTH('Universidade') AS tamanho_palavra;

-- Exercício 5: Retorne o código ASCII da letra 'Z'.
SELECT ASCII('Z') AS codigo_ascii_z;

-- NÍVEL INTERMEDIÁRIO

-- Exercício 6: Liste o nome dos funcionários em letras minúsculas.
SELECT LOWER(nome) AS nome_minusculo 
FROM funcionarios;

-- Exercício 7: Mostre o nome concatenado com a cidade.
SELECT nome || ' mora em ' || cidade AS descricao_moradia 
FROM funcionarios;

-- Exercício 8: Extraia o ano de nascimento dos funcionários.
SELECT nome, EXTRACT(YEAR FROM data_nascimento) AS ano_nascimento 
FROM funcionarios;

-- Exercício 9: Calcule a idade dos funcionários.
SELECT nome, AGE(NOW(), data_nascimento) AS idade_completa 
FROM funcionarios;

-- Exercício 10: Mostre quantos caracteres tem cada nome.
SELECT nome, LENGTH(nome) AS total_letras 
FROM funcionarios;

-- NÍVEL AVANÇADO

-- Exercício 11: Liste funcionários com mais de 30 anos.
SELECT nome, AGE(NOW(), data_nascimento) AS idade
FROM funcionarios
WHERE AGE(NOW(), data_nascimento) > INTERVAL '30 years';

-- Exercício 12: Mostre nomes em maiúsculo junto com o ano de nascimento.
SELECT UPPER(nome) AS nome_maiusculo, EXTRACT(YEAR FROM data_nascimento) AS ano_nascimento
FROM funcionarios;

-- Exercício 13: Crie uma frase: "Nome - Idade - Cidade".
SELECT nome || ' - ' || AGE(NOW(), data_nascimento) || ' - ' || cidade AS relatorio_funcionario
FROM funcionarios;

-- Exercício 14: Mostre apenas funcionários nascidos no mês de janeiro.
SELECT nome, data_nascimento
FROM funcionarios
WHERE EXTRACT(MONTH FROM data_nascimento) = 1;

-- Exercício 15: Exiba o primeiro caractere do nome e seu código ASCII.
SELECT 
    nome, 
    SUBSTRING(nome FROM 1 FOR 1) AS primeiro_caractere,
    ASCII(SUBSTRING(nome FROM 1 FOR 1)) AS codigo_ascii
FROM funcionarios;

-- --------------------------------------------------------------------
-- 5. ATIVIDADE DESAFIO
-- --------------------------------------------------------------------

-- Exercício 16 (Desafio): Consulta com projeção composta:
-- Nome em maiúsculo | Idade | Cidade em minúsculo | Ano de nascimento
-- Saída esperada: CARLOS SILVA | 34 years | são paulo | 1990

SELECT 
    UPPER(nome) 
    || ' | ' || 
    AGE(NOW(), data_nascimento) 
    || ' | ' || 
    LOWER(cidade) 
    || ' | ' || 
    EXTRACT(YEAR FROM data_nascimento) AS resultado_desafio
FROM funcionarios;
