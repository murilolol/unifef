-- ============================================================
-- DISCIPLINA: Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR : Prof. Welington Garcia
-- AVALIAÇÃO : Avaliação Prática de PostgreSQL (09/09/2026)
-- SCRIPT    : Resolução Completa dos Exercícios 1 ao 6
-- COMO EXECUTAR: psql -U postgres -d clinica_avaliacao -f resolucao_avaliacao.sql
-- ============================================================

-- ============================================================
-- EXERCÍCIO 1: Consultas com paciente, médico e especialidade (2,0 pontos)
-- Enunciado: Crie uma consulta que exiba código da consulta, data da consulta,
-- nome do paciente, nome do médico, nome da especialidade e status da consulta.
-- Utilize INNER JOIN.
-- ============================================================

SELECT 
    c.id_consulta AS codigo_consulta,
    c.data_consulta,
    p.nome AS nome_paciente,
    m.nome AS nome_medico,
    e.nome AS nome_especialidade,
    c.status AS status_consulta
FROM consultas c
INNER JOIN pacientes p 
    ON c.id_paciente = p.id_paciente
INNER JOIN medicos m 
    ON c.id_medico = m.id_medico
INNER JOIN especialidades e 
    ON m.id_especialidade = e.id_especialidade
ORDER BY c.id_consulta;


-- ============================================================
-- EXERCÍCIO 2: Pacientes sem consultas (1,0 ponto)
-- Enunciado: Exiba todos os pacientes e, quando existir, suas respectivas
-- consultas. Depois filtre o resultado para mostrar somente os pacientes
-- que nunca realizaram nenhuma consulta. Utilize LEFT JOIN.
-- ============================================================

-- Parte A: Exibição de todos os pacientes e respectivas consultas (quando houver)
SELECT 
    p.id_paciente,
    p.nome AS nome_paciente,
    c.id_consulta,
    c.data_consulta,
    c.status AS status_consulta
FROM pacientes p
LEFT JOIN consultas c 
    ON p.id_paciente = c.id_paciente
ORDER BY p.id_paciente, c.id_consulta;

-- Parte B (Filtro final): Apenas pacientes que NUNCA realizaram nenhuma consulta
SELECT 
    p.id_paciente AS codigo_paciente,
    p.nome AS nome_paciente,
    p.cidade,
    p.estado,
    p.data_nascimento,
    p.convenio
FROM pacientes p
LEFT JOIN consultas c 
    ON p.id_paciente = c.id_paciente
WHERE c.id_consulta IS NULL
ORDER BY p.id_paciente;


-- ============================================================
-- EXERCÍCIO 3: Consultas, exames e pagamentos (2,0 pontos)
-- Enunciado: Crie um relatório que apresente código da consulta, nome do paciente,
-- nome do médico, nome do exame, valor do exame, forma de pagamento e valor pago.
-- Todas as consultas devem aparecer, mesmo que ainda não possuam exame ou pagamento.
-- Utilize uma combinação de INNER JOIN e LEFT JOIN.
-- ============================================================

SELECT 
    c.id_consulta AS codigo_consulta,
    p.nome AS nome_paciente,
    m.nome AS nome_medico,
    ex.nome_exame,
    ex.valor AS valor_exame,
    pg.forma_pagamento,
    pg.valor_pago
FROM consultas c
INNER JOIN pacientes p 
    ON c.id_paciente = p.id_paciente
INNER JOIN medicos m 
    ON c.id_medico = m.id_medico
LEFT JOIN exames ex 
    ON c.id_consulta = ex.id_consulta
LEFT JOIN pagamentos pg 
    ON c.id_consulta = pg.id_consulta
ORDER BY c.id_consulta, ex.id_exame;


-- ============================================================
-- EXERCÍCIO 4: Consultas acima da média de valor (1,5 pontos)
-- Enunciado: Exiba todas as consultas cujo valor seja maior que a média
-- de valor de todas as consultas.
-- Campos: código da consulta, data da consulta, valor, status.
-- A média deverá ser calculada por uma subconsulta.
-- ============================================================

SELECT 
    c.id_consulta AS codigo_consulta,
    c.data_consulta,
    c.valor AS valor_consulta,
    c.status AS status_consulta
FROM consultas c
WHERE c.valor > (
    SELECT AVG(valor) 
    FROM consultas
)
ORDER BY c.valor DESC, c.id_consulta;


-- ============================================================
-- EXERCÍCIO 5: Médicos com salário acima da média (1,5 pontos)
-- Enunciado: Exiba os médicos cujo salário seja superior à média salarial
-- de todos os médicos cadastrados.
-- Campos: código do médico, nome, CRM, salário.
-- Utilize uma subconsulta no WHERE.
-- ============================================================

SELECT 
    m.id_medico AS codigo_medico,
    m.nome AS nome_medico,
    m.crm,
    m.salario
FROM medicos m
WHERE m.salario > (
    SELECT AVG(salario) 
    FROM medicos
)
ORDER BY m.salario DESC, m.id_medico;


-- ============================================================
-- EXERCÍCIO 6: View de consultas realizadas (2,0 pontos)
-- Enunciado: Crie uma view chamada: vw_consultas_realizadas.
-- Essa view deverá apresentar somente as consultas com status igual a Realizada.
-- Campos: código da consulta, data da consulta, nome do paciente,
-- nome do médico, especialidade do médico, valor da consulta.
-- Depois, faça uma consulta sobre ela exibindo apenas as consultas com
-- valor superior a R$ 350,00, ordenadas do maior para o menor valor.
-- ============================================================

-- Criação ou substituição da View
CREATE OR REPLACE VIEW vw_consultas_realizadas AS
SELECT 
    c.id_consulta AS codigo_consulta,
    c.data_consulta,
    p.nome AS nome_paciente,
    m.nome AS nome_medico,
    e.nome AS especialidade_medico,
    c.valor AS valor_consulta
FROM consultas c
INNER JOIN pacientes p 
    ON c.id_paciente = p.id_paciente
INNER JOIN medicos m 
    ON c.id_medico = m.id_medico
INNER JOIN especialidades e 
    ON m.id_especialidade = e.id_especialidade
WHERE c.status = 'Realizada';

-- Consulta solicitada sobre a View recém-criada:
SELECT 
    codigo_consulta,
    data_consulta,
    nome_paciente,
    nome_medico,
    especialidade_medico,
    valor_consulta
FROM vw_consultas_realizadas
WHERE valor_consulta > 350.00
ORDER BY valor_consulta DESC, codigo_consulta;
