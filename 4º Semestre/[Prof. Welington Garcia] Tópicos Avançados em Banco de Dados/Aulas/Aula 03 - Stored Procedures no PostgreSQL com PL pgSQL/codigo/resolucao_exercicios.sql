-- Disciplina: Tópicos Avançados em Banco de Dados
-- Professor: Prof. Welington Garcia
-- Tema: Stored Procedures no PostgreSQL com PL/pgSQL
-- Descrição: Resolução dos exercícios práticos propostos.

-- ========================================== 
-- Exercício 1: Cadastro de Clientes com Validação
-- ==========================================
CREATE OR REPLACE PROCEDURE fef_cadastrar_cliente(
    p_nome VARCHAR,
    p_cpf VARCHAR,
    p_email VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_existe_cpf INT;
BEGIN
    -- Verifica se o CPF já está cadastrado
    SELECT COUNT(*) INTO v_existe_cpf 
    FROM clientes 
    WHERE cpf = p_cpf;

    IF v_existe_cpf > 0 THEN
        RAISE EXCEPTION 'Erro de validação: O CPF % já está cadastrado no sistema.', p_cpf;
    END IF;

    -- Insere o novo cliente
    INSERT INTO clientes (nome, cpf, email) 
    VALUES (p_nome, p_cpf, p_email);

    -- Grava log de auditoria
    INSERT INTO logs_sistema (acao) 
    VALUES ('Cliente cadastrado: ' || p_nome || ' (CPF: ' || p_cpf || ')');

    RAISE NOTICE 'Cliente % cadastrado com sucesso!', p_nome;
END;
$$;

-- ==========================================
-- Exercício 2: Transferência de Saldo entre Contas
-- ==========================================
CREATE OR REPLACE PROCEDURE fef_transferir_saldo(
    p_conta_origem INT,
    p_conta_destino INT,
    p_valor NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_saldo_origem NUMERIC;
BEGIN
    -- Validação do valor de transferência
    IF p_valor <= 0 THEN
        RAISE EXCEPTION 'O valor da transferência deve ser maior que zero.';
    END IF;

    -- Busca o saldo atual da conta de origem
    SELECT saldo INTO v_saldo_origem 
    FROM contas 
    WHERE id = p_conta_origem;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Conta de origem (ID: %) não localizada.', p_conta_origem;
    END IF;

    -- Verifica se há saldo suficiente
    IF v_saldo_origem < p_valor THEN
        RAISE EXCEPTION 'Saldo insuficiente na conta %. Saldo disponível: R$ %', p_conta_origem, v_saldo_origem;
    END IF;

    -- Deduz o saldo da conta de origem
    UPDATE contas 
    SET saldo = saldo - p_valor 
    WHERE id = p_conta_origem;

    -- Adiciona o saldo na conta de destino
    UPDATE contas 
    SET saldo = saldo + p_valor 
    WHERE id = p_conta_destino;

    -- Se a conta de destino não existir, o UPDATE não afetará linhas
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Conta de destino (ID: %) não localizada. Operação cancelada.', p_conta_destino;
    END IF;

    -- Confirmação explícita da transação
    COMMIT;
    RAISE NOTICE 'Transferência de R$ % realizada com sucesso da conta % para a conta %.', p_valor, p_conta_origem, p_conta_destino;

EXCEPTION
    WHEN OTHERS THEN
        -- Em caso de qualquer erro, desfaz as alterações pendentes
        ROLLBACK;
        RAISE EXCEPTION 'Transação abortada devido ao erro: %', SQLERRM;
END;
$$;

-- ==========================================
-- Exercício 3: Atualização em Lote de Preços de Produtos
-- ==========================================
CREATE OR REPLACE PROCEDURE fef_reajustar_precos(
    p_categoria_id INT,
    p_percentual NUMERIC,
    OUT p_total_atualizado INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validação do percentual de reajuste
    IF p_percentual IS NULL OR p_percentual < -100.00 THEN
        RAISE EXCEPTION 'Percentual de reajuste inválido.';
    END IF;

    -- Atualiza os preços dos produtos pertencentes à categoria informada
    UPDATE produtos
    SET preco = preco * (1 + (p_percentual / 100.00))
    WHERE categoria_id = p_categoria_id;

    -- Obtém o número de linhas afetadas pela instrução UPDATE anterior
    GET DIAGNOSTICS p_total_atualizado = ROW_COUNT;

    RAISE NOTICE 'Reajuste de %%% aplicado para % produtos da categoria %.', p_percentual, p_total_atualizado, p_categoria_id;
END;
$$;
