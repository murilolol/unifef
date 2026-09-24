-- ============================================================
-- Disciplina: Tópicos Avançados em Banco de Dados
-- Professor: Prof. Welington Garcia
-- Resolução Oficial da Avaliação Prática (09/09/2026)
-- DBMS: PostgreSQL
-- ============================================================

-- ------------------------------------------------------------
-- Exercício 1: Consultas com paciente, médico e especialidade
-- Requisitos: INNER JOIN, código, data, paciente, médico, especialidade, status.
-- ------------------------------------------------------------
SELECT 
    c.id_consulta AS codigo_consulta,
    c.data_consulta,
    p.nome AS nome_paciente,
    m.nome AS nome_medico,
    e.nome AS nome_especialidade,
    c.status AS status_consulta
FROM consultas c
INNER JOIN pacientes p ON c.id_paciente = p.id_paciente
INNER JOIN medicos m ON c.id_medico = m.id_medico
INNER JOIN especialidades e ON m.id_especialidade = e.id_especialidade;

-- ------------------------------------------------------------
-- Exercício 2: Pacientes sem consultas
-- Requisitos: LEFT JOIN, filtrar somente os que nunca realizaram nenhuma consulta.
-- ------------------------------------------------------------
SELECT 
    p.id_paciente,
    p.nome AS nome_paciente,
    p.cidade,
    p.estado,
    c.id_consulta,
    c.data_consulta
FROM pacientes p
LEFT JOIN consultas c ON p.id_paciente = c.id_paciente
WHERE c.id_consulta IS NULL;

-- ------------------------------------------------------------
-- Exercício 3: Consultas, exames e pagamentos
-- Requisitos: Combinação de INNER e LEFT JOIN. Todas as consultas devem aparecer.
-- ------------------------------------------------------------
SELECT 
    c.id_consulta AS codigo_consulta,
    p.nome AS nome_paciente,
    m.nome AS nome_medico,
    ex.nome_exame,
    ex.valor AS valor_exame,
    pag.forma_pagamento,
    pag.valor_pago
FROM consultas c
INNER JOIN pacientes p ON c.id_paciente = p.id_paciente
INNER JOIN medicos m ON c.id_medico = m.id_medico
LEFT JOIN exames ex ON c.id_consulta = ex.id_consulta
LEFT JOIN pagamentos pag ON c.id_consulta = pag.id_consulta;

-- ------------------------------------------------------------
-- Exercício 4: Consultas acima da média de valor
-- Requisitos: Subconsulta para calcular a média de valor de todas as consultas.
-- ------------------------------------------------------------
SELECT 
    id_consulta AS codigo_consulta,
    data_consulta,
    valor,
    status
FROM consultas
WHERE valor > (SELECT AVG(valor) FROM consultas);

-- ------------------------------------------------------------
-- Exercício 5: Médicos com salário acima da média
-- Requisitos: Subconsulta no WHERE para calcular a média salarial.
-- ------------------------------------------------------------
SELECT 
    id_medico AS codigo_medico,
    nome,
    crm,
    salario
FROM medicos
WHERE salario > (SELECT AVG(salario) FROM medicos);

-- ------------------------------------------------------------
-- Exercício 6: View vw_consultas_realizadas e consulta de filtro
-- Requisitos: Criar view com status 'Realizada'. Consulta posterior > 350.00 ordenada DESC.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW vw_consultas_realizadas AS
SELECT 
    c.id_consulta AS codigo_consulta,
    c.data_consulta,
    p.nome AS nome_paciente,
    m.nome AS nome_medico,
    e.nome AS especialidade_medico,
    c.valor AS valor_consulta
FROM consultas c
INNER JOIN pacientes p ON c.id_paciente = p.id_paciente
INNER JOIN medicos m ON c.id_medico = m.id_medico
INNER JOIN especialidades e ON m.id_especialidade = e.id_especialidade
WHERE c.status = 'Realizada';

-- Consulta sobre a View criada:
SELECT 
    codigo_consulta,
    data_consulta,
    nome_paciente,
    nome_medico,
    especialidade_medico,
    valor_consulta
FROM vw_consultas_realizadas
WHERE valor_consulta > 350.00
ORDER BY valor_consulta DESC;
