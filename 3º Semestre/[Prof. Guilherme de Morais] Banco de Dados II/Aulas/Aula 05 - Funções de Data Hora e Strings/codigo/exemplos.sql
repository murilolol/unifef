-- ====================================================================
-- Disciplina: Banco de Dados II (3º Semestre)
-- Professor : Prof. Guilherme de Morais
-- Tema      : Funções de Data, Hora e Concatenação
-- SGBD      : PostgreSQL
-- Execução  : psql -U postgres -d seu_banco -f exemplos.sql
-- ====================================================================

-- --------------------------------------------------------------------
-- 1. FUNÇÕES DE DATA E HORA
-- --------------------------------------------------------------------

-- 1.1 NOW(): Retorna data, hora e fuso horário atuais do sistema
SELECT NOW() AS data_hora_atual;

-- 1.2 DATE(): Converte timestamp e extrai somente a porção da data
SELECT DATE(NOW()) AS somente_data;

-- 1.3 AGE(): Calcula a diferença/intervalo entre duas datas
-- Sintaxe: AGE(data_final, data_inicial)
SELECT AGE('2025-01-01', '2000-01-01') AS diferenca_exemplo;
SELECT AGE(NOW(), '1990-05-10') AS idade_exemplo;

-- 1.4 EXTRACT(): Extrai partes específicas de uma data/timestamp
SELECT EXTRACT(YEAR FROM NOW()) AS ano_atual;
SELECT EXTRACT(MONTH FROM NOW()) AS mes_atual;
SELECT EXTRACT(DAY FROM NOW()) AS dia_atual;
SELECT EXTRACT(HOUR FROM NOW()) AS hora_atual;

-- --------------------------------------------------------------------
-- 2. FUNÇÕES DE STRING
-- --------------------------------------------------------------------

-- 2.1 ASCII(): Retorna o código numérico ASCII do primeiro caractere
SELECT ASCII('A') AS codigo_ascii_a; -- Retorna 65

-- 2.2 Concatenação (||): Une duas ou mais expressões textuais
SELECT 'Olá' || ' Mundo' AS saudacao;

-- 2.3 LENGTH(): Retorna o número de caracteres de uma string
SELECT LENGTH('Banco de Dados') AS total_caracteres;

-- 2.4 LOWER(): Converte a string inteira para letras minúsculas
SELECT LOWER('SQL É LEGAL') AS texto_minusculo;

-- 2.5 UPPER(): Converte a string inteira para letras maiúsculas
SELECT UPPER('sql é legal') AS texto_maiusculo;

-- --------------------------------------------------------------------
-- 3. BASE DE DADOS DE EXEMPLO
-- --------------------------------------------------------------------

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

-- Exemplo prático de concatenação consultando a tabela
SELECT nome || ' - ' || cidade AS funcionario_cidade FROM funcionarios;
