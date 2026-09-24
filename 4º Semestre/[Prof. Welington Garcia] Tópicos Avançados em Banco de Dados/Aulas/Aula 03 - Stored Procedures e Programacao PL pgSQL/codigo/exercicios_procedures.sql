/*
 * Disciplina: Tópicos Avançados em Banco de Dados
 * Aula: Stored Procedures e Programação PL/pgSQL - Resolução dos Exercícios
 * Professor: Prof. Welington Garcia
 *
 * Como executar:
 * 1. Conecte-se ao banco de dados PostgreSQL.
 * 2. Execute este script do início ao fim para criar a estrutura de tabelas,
 *    as procedures resolvidas e os testes de execução de cada exercício.
 */

-- ============================================================================
-- ESTRUTURA DE BANCO DE DADOS PARA OS EXERCÍCIOS
-- ============================================================================

DROP TABLE IF EXISTS historico_movimentacoes CASCADE;
DROP TABLE IF EXISTS contas CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;
DROP TABLE IF EXISTS departamentos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- Estrutura para Exercício 1
CREATE TABLE IF NOT EXISTS clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Estrutura para Exercício 2
CREATE TABLE IF NOT EXISTS departamentos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS funcionarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    salario NUMERIC(10, 2) NOT NULL,
    departamento_id INT REFERENCES departamentos(id)
);

-- Estrutura para Exercício 3
CREATE TABLE IF NOT EXISTS contas (
    id SERIAL PRIMARY KEY,
    titular VARCHAR(100) NOT NULL,
    saldo NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS historico_movimentacoes (
    id SERIAL PRIMARY KEY,
    conta_origem_id INT REFERENCES contas(id),
    conta_destino_id INT REFERENCES contas(id),
    valor NUMERIC(12, 2) NOT NULL,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dados para Testes
INSERT INTO departamentos (nome) VALUES ('Tecnologia'), ('Financeiro');

INSERT INTO funcionarios (nome, salario, departamento_id) VALUES 
    ('Carlos Silva', 3000.00, 1),
    ('Ana Souza', 5500.00, 1),
    ('Roberto Alves', 2800.00, 2);

INSERT INTO contas (titular, saldo) VALUES 
    ('João Pereira', 1500.00),
    ('Maria Oliveira', 500.00);

-- ============================================================================
-- EXERCÍCIO 1: Criacao de Procedure de Insercao com Validacao
-- Enunciado: Crie uma stored procedure em PL/pgSQL chamada 'sp_registrar_cliente'
-- que receba nome e e-mail como parametros. A procedure deve verificar se o
-- e-mail ja existe na tabela 'clientes' antes de realizar o INSERT. Caso o
-- e-mail ja esteja cadastrado, deve levantar uma excecao ou exibir mensagem.
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_registrar_cliente(
    p_nome VARCHAR(100),
    p_email VARCHAR(100)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_existe_email INT;
BEGIN
    -- Verifica se o e-mail já existe no banco
    SELECT COUNT(1) INTO v_existe_email
    FROM clientes
    WHERE LOWER(email) = LOWER(p_email);

    IF v_existe_email > 0 THEN
        RAISE EXCEPTION 'Erro: O e-mail "%" já está cadastrado no sistema.', p_email;
    END IF;

    -- Inserção do cliente caso a validação passe
    INSERT INTO clientes (nome, email)
    VALUES (p_nome, p_email);

    RAISE NOTICE 'Cliente "%" cadastrado com sucesso!', p_nome;
END;
$$;

-- ============================================================================
-- EXERCÍCIO 2: Procedure para Atualizacao Salarial com Condicional
-- Enunciado: Desenvolva uma procedure chamada 'sp_reajustar_salario' que receba
-- o ID do departamento e o percentual de reajuste. A procedure deve aplicar
-- o reajuste salarial na tabela 'funcionarios' apenas para os funcionarios do
-- departamento especificado cujo salario atual seja inferior a um limite.
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_reajustar_salario(
    p_departamento_id INT,
    p_percentual_reajuste NUMERIC(5, 2),
    p_limite_salario NUMERIC(10, 2) DEFAULT 4000.00
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_linhas_afetadas INT;
BEGIN
    -- Valida percentual positivo
    IF p_percentual_reajuste <= 0 THEN
        RAISE EXCEPTION 'O percentual de reajuste deve ser maior que zero.';
    END IF;

    -- Aplica o reajuste apenas nos funcionários do departamento especificado com salário inferior ao limite
    UPDATE funcionarios
    SET salario = salario + (salario * (p_percentual_reajuste / 100.0))
    WHERE departamento_id = p_departamento_id
      AND salario < p_limite_salario;

    GET DIAGNOSTICS v_linhas_afetadas = ROW_COUNT;

    IF v_linhas_afetadas = 0 THEN
        RAISE NOTICE 'Nenhum funcionário atendeu aos critérios para reajuste no departamento %.', p_departamento_id;
    ELSE
        RAISE NOTICE 'Reajuste salarial de %% aplicado a % funcionario(s) do departamento %.', 
                     p_percentual_reajuste, v_linhas_afetadas, p_departamento_id;
    END IF;
END;
$$;

-- ============================================================================
-- EXERCÍCIO 3: Procedure de Transferencia de Saldos (Controle Transacional)
-- Enunciado: Implemente uma stored procedure 'sp_transferir_fundo' que receba
-- conta_origem, conta_destino e valor. A procedure deve verificar se a
-- conta_origem possui saldo suficiente, realizar o debito na conta de origem,
-- o credito na conta de destino e registrar a movimentacao no historico.
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_transferir_fundo(
    p_conta_origem INT,
    p_conta_destino INT,
    p_valor NUMERIC(12, 2)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_saldo_origem NUMERIC(12, 2);
    v_existe_destino INT;
BEGIN
    -- Validação do valor da transferência
    IF p_valor <= 0 THEN
        RAISE EXCEPTION 'O valor da transferência deve ser superior a zero.';
    END IF;

    -- Impedir transferência para a mesma conta
    IF p_conta_origem = p_conta_destino THEN
        RAISE EXCEPTION 'A conta de origem e de destino não podem ser iguais.';
    END IF;

    -- Verifica se a conta de origem existe e obtém saldo com trava de linha (FOR UPDATE)
    SELECT saldo INTO v_saldo_origem
    FROM contas
    WHERE id = p_conta_origem
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Conta de origem (ID %) não encontrada.', p_conta_origem;
    END IF;

    -- Verifica se a conta de destino existe
    SELECT COUNT(1) INTO v_existe_destino
    FROM contas
    WHERE id = p_conta_destino;

    IF v_existe_destino = 0 THEN
        RAISE EXCEPTION 'Conta de destino (ID %) não encontrada.', p_conta_destino;
    END IF;

    -- Verifica saldo disponível na conta de origem
    IF v_saldo_origem < p_valor THEN
        RAISE EXCEPTION 'Saldo insuficiente na conta de origem. Saldo atual: %, Valor solicitado: %.', 
                        v_saldo_origem, p_valor;
    END IF;

    -- Realiza o débito na conta de origem
    UPDATE contas
    SET saldo = saldo - p_valor
    WHERE id = p_conta_origem;

    -- Realiza o crédito na conta de destino
    UPDATE contas
    SET saldo = saldo + p_valor
    WHERE id = p_conta_destino;

    -- Registra a movimentação no histórico
    INSERT INTO historico_movimentacoes (conta_origem_id, conta_destino_id, valor)
    VALUES (p_conta_origem, p_conta_destino, p_valor);

    RAISE NOTICE 'Transferência de R$ % realizada com sucesso de Conta % para Conta %.', 
                 p_valor, p_conta_origem, p_conta_destino;
END;
$$;

-- ============================================================================
-- TESTES DOS EXERCÍCIOS
-- ============================================================================

-- Teste Exercício 1:
CALL sp_registrar_cliente('Lucas Mendes', 'lucas@email.com');
-- Tentativa com e-mail duplicado (Deve lançar exceção):
-- CALL sp_registrar_cliente('Lucas Outro', 'lucas@email.com');
SELECT * FROM clientes;

-- Teste Exercício 2:
SELECT * FROM funcionarios WHERE departamento_id = 1;
-- Aplica 10% de reajuste para o depto 1 onde salário < 4000.00 (Carlos será reajustado, Ana não)
CALL sp_reajustar_salario(1, 10.00, 4000.00);
SELECT * FROM funcionarios WHERE departamento_id = 1;

-- Teste Exercício 3:
SELECT * FROM contas;
-- Transfere 300.00 da Conta 1 para Conta 2
CALL sp_transferir_fundo(1, 2, 300.00);
SELECT * FROM contas;
SELECT * FROM historico_movimentacoes;
