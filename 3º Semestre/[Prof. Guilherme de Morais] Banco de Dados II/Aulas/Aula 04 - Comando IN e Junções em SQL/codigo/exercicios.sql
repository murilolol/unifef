-- ============================================================================
-- Instituição: Centro Universitário de Santa Fé do Sul (UniFEF)
-- Curso: Bacharelado em Sistemas de Informação (3º Semestre)
-- Disciplina: Banco de Dados II
-- Professor: Prof. Guilherme de Morais
-- Tema: Resolução dos Exercícios de Aula (Slide 12)
-- Data de Referência: 29/04/2026
-- ============================================================================
-- Como executar:
-- Certifique-se de ter executado previamente o script 'schema_e_exemplos.sql'
-- para que as tabelas 'clientes' e 'veiculos' estejam criadas e populadas.
-- ============================================================================

-- ============================================================================
-- Exercício 1
-- Enunciado:
-- "1- Selecione todos os nome e cpf da tabela cliente e marca e modelo cujo o 
--     modelo for igual a Toyota."
-- ============================================================================
-- Análise Técnica:
-- No modelo de dados fornecido no anexo, 'TOYOTA' é classificado no campo MARCA
-- (enquanto os modelos são 'COROLLA', 'HILUX', etc.). Apresentamos abaixo:
-- 1) A resolução literal conforme o texto estrito do enunciado (veiculos.modelo = 'TOYOTA');
-- 2) A resolução semanticamente corrigida com a coluna correta (veiculos.marca = 'TOYOTA');
-- 3) A resolução moderna equivalente no padrão ANSI SQL (INNER JOIN).

-- [Abordagem 1A: Conforme sintaxe de aula e filtro literal de modelo]
SELECT 
    clientes.nome,
    clientes.cpf,
    veiculos.marca,
    veiculos.modelo
FROM 
    clientes,
    veiculos
WHERE 
    (clientes.cpf = veiculos.cpf_cli)
    AND (veiculos.modelo = 'TOYOTA');

-- [Abordagem 1B: Correção semântica considerando que TOYOTA é uma MARCA]
SELECT 
    clientes.nome,
    clientes.cpf,
    veiculos.marca,
    veiculos.modelo
FROM 
    clientes,
    veiculos
WHERE 
    (clientes.cpf = veiculos.cpf_cli)
    AND (veiculos.marca = 'TOYOTA');

-- [Abordagem 1C: Equivalente moderno em sintaxe ANSI SQL INNER JOIN]
SELECT 
    c.nome,
    c.cpf,
    v.marca,
    v.modelo
FROM 
    clientes c
INNER JOIN 
    veiculos v ON c.cpf = v.cpf_cli
WHERE 
    v.marca = 'TOYOTA';


-- ============================================================================
-- Exercício 2
-- Enunciado:
-- "2- Seleciona do veículos chassi, placa, modelo e marcar e da tabela clientes 
--     nome e cpf , cujo os clientes que residem em estado diferente de São Paulo."
-- ============================================================================
-- Análise Técnica:
-- A consulta requer a junção entre veículos e clientes com filtro de exclusão
-- para o estado de São Paulo ('SP'). Utiliza-se o operador de diferença (<> ou !=)
-- ou a combinação do operador NOT / NOT IN.

-- [Abordagem 2A: Conforme sintaxe de aula usando operador de diferença <>]
SELECT 
    veiculos.chassi,
    veiculos.placa,
    veiculos.modelo,
    veiculos.marca,
    clientes.nome,
    clientes.cpf
FROM 
    veiculos,
    clientes
WHERE 
    (clientes.cpf = veiculos.cpf_cli)
    AND (clientes.estado <> 'SP');

-- [Abordagem 2B: Conforme sintaxe de aula aplicando o operador NOT IN]
SELECT 
    veiculos.chassi,
    veiculos.placa,
    veiculos.modelo,
    veiculos.marca,
    clientes.nome,
    clientes.cpf
FROM 
    veiculos,
    clientes
WHERE 
    (clientes.cpf = veiculos.cpf_cli)
    AND (clientes.estado NOT IN ('SP'));

-- [Abordagem 2C: Equivalente moderno em sintaxe ANSI SQL INNER JOIN]
SELECT 
    v.chassi,
    v.placa,
    v.modelo,
    v.marca,
    c.nome,
    c.cpf
FROM 
    veiculos v
INNER JOIN 
    clientes c ON c.cpf = v.cpf_cli
WHERE 
    c.estado <> 'SP';


-- ============================================================================
-- Exercício 3
-- Enunciado:
-- "3- Selecione todos os veículos da marcar Toyota e VW. Usando o comando in."
-- ============================================================================
-- Análise Técnica:
-- O exercício exige a aplicação direta do operador IN sobre a coluna de marca
-- na tabela de veículos, selecionando registros onde a marca seja 'TOYOTA' ou 'VW'.

-- [Abordagem 3A: Seleção de todos os campos com asterisco (*)]
SELECT * 
FROM veiculos 
WHERE marca IN ('TOYOTA', 'VW');

-- [Abordagem 3B: Seleção de colunas com qualificação de tabela e ordenação]
SELECT 
    veiculos.placa,
    veiculos.modelo,
    veiculos.marca,
    veiculos.cor,
    veiculos.ano_fabricacao,
    veiculos.preco_venda
FROM 
    veiculos
WHERE 
    veiculos.marca IN ('TOYOTA', 'VW')
ORDER BY 
    veiculos.marca, 
    veiculos.modelo;
