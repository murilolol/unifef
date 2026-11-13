-- ===============================================================
-- DISCIPLINA : Tópicos Avançados em Banco de Dados (4º Semestre)
-- INSTITUIÇÃO: UniFEF - Centro Universitário de Fernandópolis
-- PROFESSOR  : Prof. Welington Garcia
-- PARTE A    : 10 Stored Procedures em PL/pgSQL (Exercícios A1 a A10)
-- BASE       : db_loja_avancada (PostgreSQL 14+)
-- COMO EXECUTAR:
--   1. Execute o script 01_db_loja_avancada_schema.sql.
--   2. Execute este script para compilar as 10 procedures e rodar a suíte de testes.
-- ===============================================================

-- ---------------------------------------------------------------
-- Exercício A1: sp_cadastrar_cliente
-- Cadastra cliente com limite de crédito padrão (R$ 1.000,00 se omitido),
-- rejeitando nome ou e-mail nulos/em branco e tratando e-mails duplicados.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_cadastrar_cliente(
    p_nome VARCHAR,
    p_email VARCHAR,
    p_telefone VARCHAR,
    p_cidade VARCHAR,
    p_uf CHAR(2),
    p_limite_credito NUMERIC(12,2) DEFAULT 1000.00
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validação de campos obrigatórios
    IF p_nome IS NULL OR TRIM(p_nome) = '' THEN
        RAISE EXCEPTION 'Nome do cliente não pode ser vazio ou nulo.' USING ERRCODE = '22000';
    END IF;

    IF p_email IS NULL OR TRIM(p_email) = '' THEN
        RAISE EXCEPTION 'E-mail do cliente não pode ser vazio ou nulo.' USING ERRCODE = '22000';
    END IF;

    IF p_cidade IS NULL OR TRIM(p_cidade) = '' THEN
        RAISE EXCEPTION 'Cidade não pode ser vazia ou nula.' USING ERRCODE = '22000';
    END IF;

    IF p_uf IS NULL OR LENGTH(TRIM(p_uf)) <> 2 THEN
        RAISE EXCEPTION 'UF inválida: deve conter exatamente 2 caracteres.' USING ERRCODE = '22000';
    END IF;

    IF p_limite_credito < 0 THEN
        RAISE EXCEPTION 'Limite de crédito não pode ser negativo: %', p_limite_credito USING ERRCODE = '22003';
    END IF;

    -- Tratamento preventivo de duplicidade de e-mail
    IF EXISTS (SELECT 1 FROM clientes WHERE LOWER(TRIM(email)) = LOWER(TRIM(p_email))) THEN
        RAISE EXCEPTION 'Já existe um cliente cadastrado com o e-mail: %', p_email USING ERRCODE = '23505';
    END IF;

    INSERT INTO clientes (nome, email, telefone, cidade, uf, limite_credito, ativo, data_cadastro)
    VALUES (TRIM(p_nome), LOWER(TRIM(p_email)), TRIM(p_telefone), TRIM(p_cidade), UPPER(TRIM(p_uf)), p_limite_credito, TRUE, CURRENT_DATE);

    RAISE NOTICE 'Cliente "%" cadastrado com sucesso!', p_nome;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A2: sp_reajustar_preco_produto
-- Reajusta o preço de um produto com base em percentual e arredonda para 2 casas.
-- Rejeita produto inexistente e preço final menor ou igual a zero.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_reajustar_preco_produto(
    p_id_produto INTEGER,
    p_percentual NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_preco_atual NUMERIC(12,2);
    v_novo_preco NUMERIC(12,2);
BEGIN
    SELECT preco INTO v_preco_atual
    FROM produtos
    WHERE id_produto = p_id_produto
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', p_id_produto USING ERRCODE = 'P0002';
    END IF;

    v_novo_preco := ROUND(v_preco_atual * (1 + (p_percentual / 100.0)), 2);

    IF v_novo_preco <= 0 THEN
        RAISE EXCEPTION 'Preço final resultante (R$ %) não pode ser menor ou igual a zero.', v_novo_preco USING ERRCODE = '22003';
    END IF;

    UPDATE produtos
    SET preco = v_novo_preco
    WHERE id_produto = p_id_produto;

    RAISE NOTICE 'Produto ID % reajustado com sucesso de R$ % para R$ % (reajuste: % %%)', 
        p_id_produto, v_preco_atual, v_novo_preco, p_percentual;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A3: sp_alterar_status_cliente
-- Altera a flag de situação (ativo) do cliente e sinaliza ID inválido.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_alterar_status_cliente(
    p_id_cliente INTEGER,
    p_ativo BOOLEAN
)
LANGUAGE plpgsql
AS $$ 
DECLARE
    v_nome VARCHAR(120);
    v_status_anterior BOOLEAN;
BEGIN
    SELECT nome, ativo INTO v_nome, v_status_anterior
    FROM clientes
    WHERE id_cliente = p_id_cliente
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Cliente ID % não encontrado no sistema.', p_id_cliente USING ERRCODE = 'P0002';
    END IF;

    UPDATE clientes
    SET ativo = p_ativo
    WHERE id_cliente = p_id_cliente;

    RAISE NOTICE 'Situação do cliente "%" (ID %) alterada com sucesso de % para %.',
        v_nome, p_id_cliente, 
        CASE WHEN v_status_anterior THEN 'ATIVO' ELSE 'INATIVO' END,
        CASE WHEN p_ativo THEN 'ATIVO' ELSE 'INATIVO' END;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A4: sp_repor_estoque
-- Aumenta estoque e registra entrada em historico_estoque.
-- Observação de integração: Caso o trigger C5 esteja habilitado,
-- a gravação manual em historico_estoque deve ser desativada para evitar duplicações.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_repor_estoque(
    p_id_produto INTEGER,
    p_quantidade INTEGER,
    p_motivo VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_estoque_anterior INTEGER;
    v_estoque_novo INTEGER;
    v_trigger_ativo BOOLEAN := FALSE;
BEGIN
    IF p_quantidade <= 0 THEN
        RAISE EXCEPTION 'A quantidade para reposição deve ser estritamente positiva: %', p_quantidade USING ERRCODE = '22003';
    END IF;

    IF p_motivo IS NULL OR TRIM(p_motivo) = '' THEN
        RAISE EXCEPTION 'O motivo da reposição deve ser informado.' USING ERRCODE = '22000';
    END IF;

    SELECT estoque INTO v_estoque_anterior
    FROM produtos
    WHERE id_produto = p_id_produto
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', p_id_produto USING ERRCODE = 'P0002';
    END IF;

    v_estoque_novo := v_estoque_anterior + p_quantidade;

    UPDATE produtos
    SET estoque = v_estoque_novo
    WHERE id_produto = p_id_produto;

    -- Verifica se o trigger C5 está ativo na tabela produtos
    SELECT EXISTS (
        SELECT 1 FROM pg_trigger t
        JOIN pg_class c ON t.tgrelid = c.oid
        WHERE c.relname = 'produtos' AND t.tgname = 'tg_historico_automatico_estoque' AND t.tgenabled <> 'D'
    ) INTO v_trigger_ativo;

    -- Registra manualmente apenas se o trigger C5 não estiver ativo
    IF NOT v_trigger_ativo THEN
        INSERT INTO historico_estoque (id_produto, tipo, quantidade, estoque_anterior, estoque_atual, motivo)
        VALUES (p_id_produto, 'ENTRADA', p_quantidade, v_estoque_anterior, v_estoque_novo, p_motivo);
    END IF;

    RAISE NOTICE 'Estoque do produto ID % reposto: anterior = %, novo = % (+%).',
        p_id_produto, v_estoque_anterior, v_estoque_novo, p_quantidade;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A5: sp_aplicar_desconto_categoria
-- Aplica desconto em produtos ativos de uma categoria sem deixar preço <= 0.
-- Retorna a quantidade de produtos alterados por parâmetro INOUT.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_aplicar_desconto_categoria(
    p_nome_categoria VARCHAR,
    p_percentual NUMERIC,
    INOUT p_total_alterados INTEGER DEFAULT 0
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_categoria INTEGER;
BEGIN
    IF p_percentual <= 0 OR p_percentual >= 100 THEN
        RAISE EXCEPTION 'Percentual de desconto deve estar entre 0 e 100 exclusivos. Informado: %', p_percentual USING ERRCODE = '22003';
    END IF;

    SELECT id_categoria INTO v_id_categoria
    FROM categorias
    WHERE LOWER(TRIM(nome)) = LOWER(TRIM(p_nome_categoria));

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Categoria "%" não encontrada.', p_nome_categoria USING ERRCODE = 'P0002';
    END IF;

    UPDATE produtos
    SET preco = ROUND(preco * (1 - (p_percentual / 100.0)), 2)
    WHERE id_categoria = v_id_categoria
      AND ativo = TRUE
      AND ROUND(preco * (1 - (p_percentual / 100.0)), 2) > 0;

    GET DIAGNOSTICS p_total_alterados = ROW_COUNT;

    RAISE NOTICE 'Desconto de % %% aplicado na categoria "%". Total de produtos atualizados: %.',
        p_percentual, p_nome_categoria, p_total_alterados;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A6: sp_reajustar_salarios_cargo
-- Reajusta salários de funcionários ativos de um determinado cargo.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_reajustar_salarios_cargo(
    p_cargo VARCHAR,
    p_percentual NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_linhas_afetadas INTEGER;
BEGIN
    IF p_cargo IS NULL OR TRIM(p_cargo) = '' THEN
        RAISE EXCEPTION 'O cargo deve ser informado.' USING ERRCODE = '22000';
    END IF;

    IF p_percentual < -99.99 THEN
        RAISE EXCEPTION 'Percentual de reajuste inviabiliza salário positivo: %', p_percentual USING ERRCODE = '22003';
    END IF;

    UPDATE funcionarios
    SET salario = ROUND(salario * (1 + (p_percentual / 100.0)), 2)
    WHERE UPPER(TRIM(cargo)) = UPPER(TRIM(p_cargo))
      AND ativo = TRUE;

    GET DIAGNOSTICS v_linhas_afetadas = ROW_COUNT;

    IF v_linhas_afetadas = 0 THEN
        RAISE NOTICE 'Nenhum funcionário ativo encontrado no cargo "%" para reajuste.', p_cargo;
    ELSE
        RAISE NOTICE 'Reajuste de % %% aplicado a % funcionário(s) ativo(s) no cargo "%".',
            p_percentual, v_linhas_afetadas, p_cargo;
    END IF;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A7: sp_cancelar_pedido
-- Cancela pedidos em status ABERTO e registra a justificativa em observacao.
-- Rejeita cancelamento de pedidos já PAGO, ENTREGUE ou CANCELADO.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_cancelar_pedido(
    p_id_pedido INTEGER,
    p_motivo TEXT DEFAULT 'Cancelamento solicitado pelo cliente.'
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_atual VARCHAR(20);
    v_obs_atual TEXT;
BEGIN
    SELECT status, observacao INTO v_status_atual, v_obs_atual
    FROM pedidos
    WHERE id_pedido = p_id_pedido
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Pedido ID % não encontrado.', p_id_pedido USING ERRCODE = 'P0002';
    END IF;

    IF v_status_atual <> 'ABERTO' THEN
        RAISE EXCEPTION 'Apenas pedidos com status "ABERTO" podem ser cancelados. O pedido % está com status "%".',
            p_id_pedido, v_status_atual USING ERRCODE = '22000';
    END IF;

    UPDATE pedidos
    SET status = 'CANCELADO',
        observacao = CASE 
            WHEN v_obs_atual IS NULL OR v_obs_atual = '' THEN 'Cancelado: ' || p_motivo
            ELSE v_obs_atual || ' | Cancelado: ' || p_motivo
        END
    WHERE id_pedido = p_id_pedido;

    RAISE NOTICE 'Pedido ID % cancelado com sucesso. Motivo registrado.', p_id_pedido;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A8: sp_registrar_pagamento
-- Registra pagamento integral (saldo devedor) para pedidos ABERTO ou PAGO.
-- Insere pagamento CONFIRMADO e atualiza o pedido para PAGO.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_registrar_pagamento(
    p_id_pedido INTEGER,
    p_valor NUMERIC(12,2),
    p_forma VARCHAR(20)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_pedido VARCHAR(20);
    v_desconto NUMERIC(12,2);
    v_frete NUMERIC(12,2);
    v_subtotal NUMERIC(12,2);
    v_total_pedido NUMERIC(12,2);
    v_total_pago NUMERIC(12,2);
    v_saldo_devedor NUMERIC(12,2);
BEGIN
    IF p_valor <= 0 THEN
        RAISE EXCEPTION 'O valor do pagamento deve ser estritamente positivo: %', p_valor USING ERRCODE = '22003';
    END IF;

    IF UPPER(p_forma) NOT IN ('PIX','CARTAO','BOLETO','DINHEIRO') THEN
        RAISE EXCEPTION 'Forma de pagamento "%" inválida. Opções: PIX, CARTAO, BOLETO, DINHEIRO.', p_forma USING ERRCODE = '22000';
    END IF;

    SELECT status, desconto, frete INTO v_status_pedido, v_desconto, v_frete
    FROM pedidos
    WHERE id_pedido = p_id_pedido
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Pedido ID % não encontrado.', p_id_pedido USING ERRCODE = 'P0002';
    END IF;

    IF v_status_pedido NOT IN ('ABERTO', 'PAGO') THEN
        RAISE EXCEPTION 'Não é permitido registrar pagamento em pedido com status "%".', v_status_pedido USING ERRCODE = '22000';
    END IF;

    -- Calcula total do pedido
    SELECT COALESCE(SUM(quantidade * preco_unitario), 0.00)
    INTO v_subtotal
    FROM itens_pedido
    WHERE id_pedido = p_id_pedido;

    v_total_pedido := v_subtotal - v_desconto + v_frete;

    -- Total de pagamentos já confirmados
    SELECT COALESCE(SUM(valor), 0.00)
    INTO v_total_pago
    FROM pagamentos
    WHERE id_pedido = p_id_pedido
      AND status = 'CONFIRMADO';

    v_saldo_devedor := v_total_pedido - v_total_pago;

    IF v_saldo_devedor <= 0 THEN
        RAISE EXCEPTION 'O pedido ID % já está integralmente quitado.', p_id_pedido USING ERRCODE = '22000';
    END IF;

    IF p_valor <> v_saldo_devedor THEN
        RAISE EXCEPTION 'Valor informado (R$ %) diverge do saldo devedor exato (R$ %). Exige-se pagamento integral.',
            p_valor, v_saldo_devedor USING ERRCODE = '22003';
    END IF;

    INSERT INTO pagamentos (id_pedido, valor, forma, data_pagamento, status)
    VALUES (p_id_pedido, p_valor, UPPER(p_forma), CURRENT_TIMESTAMP, 'CONFIRMADO');

    UPDATE pedidos
    SET status = 'PAGO'
    WHERE id_pedido = p_id_pedido;

    RAISE NOTICE 'Pagamento de R$ % (%) registrado para o pedido ID %. Situação atualizada para PAGO.',
        p_valor, p_forma, p_id_pedido;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A9: sp_transferir_estoque
-- Transfere atomicamente unidades entre dois produtos distintos (SKUs),
-- validando saldos e registrando saída no produto de origem e entrada no de destino.
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_transferir_estoque(
    p_id_origem INTEGER,
    p_id_destino INTEGER,
    p_quantidade INTEGER,
    p_motivo VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_estoque_origem INTEGER;
    v_estoque_destino INTEGER;
    v_trigger_ativo BOOLEAN := FALSE;
    v_primeiro_id INTEGER;
    v_segundo_id INTEGER;
BEGIN
    IF p_id_origem = p_id_destino THEN
        RAISE EXCEPTION 'O produto de origem e destino devem ser distintos.' USING ERRCODE = '22000';
    END IF;

    IF p_quantidade <= 0 THEN
        RAISE EXCEPTION 'A quantidade transferida deve ser maior que zero: %', p_quantidade USING ERRCODE = '22003';
    END IF;

    IF p_motivo IS NULL OR TRIM(p_motivo) = '' THEN
        RAISE EXCEPTION 'O motivo da transferência é obrigatório.' USING ERRCODE = '22000';
    END IF;

    -- Bloqueio em ordem determinística para prevenir deadlocks
    IF p_id_origem < p_id_destino THEN
        v_primeiro_id := p_id_origem;
        v_segundo_id := p_id_destino;
    ELSE
        v_primeiro_id := p_id_destino;
        v_segundo_id := p_id_origem;
    END IF;

    PERFORM 1 FROM produtos WHERE id_produto = v_primeiro_id FOR UPDATE;
    PERFORM 1 FROM produtos WHERE id_produto = v_segundo_id FOR UPDATE;

    SELECT estoque INTO v_estoque_origem FROM produtos WHERE id_produto = p_id_origem;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto de origem ID % não encontrado.', p_id_origem USING ERRCODE = 'P0002';
    END IF;

    SELECT estoque INTO v_estoque_destino FROM produtos WHERE id_produto = p_id_destino;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto de destino ID % não encontrado.', p_id_destino USING ERRCODE = 'P0002';
    END IF;

    IF v_estoque_origem < p_quantidade THEN
        RAISE EXCEPTION 'Saldo insuficiente no produto de origem ID %. Disponível: %, Solicitado: %.',
            p_id_origem, v_estoque_origem, p_quantidade USING ERRCODE = '22003';
    END IF;

    -- Atualização atômica dos saldos
    UPDATE produtos SET estoque = estoque - p_quantidade WHERE id_produto = p_id_origem;
    UPDATE produtos SET estoque = estoque + p_quantidade WHERE id_produto = p_id_destino;

    -- Detecção de integração com o trigger C5
    SELECT EXISTS (
        SELECT 1 FROM pg_trigger t
        JOIN pg_class c ON t.tgrelid = c.oid
        WHERE c.relname = 'produtos' AND t.tgname = 'tg_historico_automatico_estoque' AND t.tgenabled <> 'D'
    ) INTO v_trigger_ativo;

    IF NOT v_trigger_ativo THEN
        -- Lançamento manual de saída
        INSERT INTO historico_estoque (id_produto, tipo, quantidade, estoque_anterior, estoque_atual, motivo)
        VALUES (p_id_origem, 'SAIDA', p_quantidade, v_estoque_origem, v_estoque_origem - p_quantidade,
                'Transferência saída para SKU ' || p_id_destino || ': ' || p_motivo);

        -- Lançamento manual de entrada
        INSERT INTO historico_estoque (id_produto, tipo, quantidade, estoque_anterior, estoque_atual, motivo)
        VALUES (p_id_destino, 'ENTRADA', p_quantidade, v_estoque_destino, v_estoque_destino + p_quantidade,
                'Transferência entrada do SKU ' || p_id_origem || ': ' || p_motivo);
    END IF;

    RAISE NOTICE 'Transferência de % unidades do produto % para o produto % concluída.',
        p_quantidade, p_id_origem, p_id_destino;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício A10: sp_bonus_clientes_fieis
-- Concede aumento de 10% no limite de crédito de clientes ativos que possuam
-- pelo menos 2 pedidos concluídos (PAGO ou ENTREGUE).
-- ---------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_bonus_clientes_fieis()
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_beneficiados INTEGER;
BEGIN
    UPDATE clientes
    SET limite_credito = ROUND(limite_credito * 1.10, 2)
    WHERE ativo = TRUE
      AND id_cliente IN (
          SELECT id_cliente
          FROM pedidos
          WHERE status IN ('PAGO', 'ENTREGUE')
          GROUP BY id_cliente
          HAVING COUNT(id_pedido) >= 2
      );

    GET DIAGNOSTICS v_total_beneficiados = ROW_COUNT;

    RAISE NOTICE 'Bônus de 10%% no limite de crédito concedido a % cliente(s) fiel(is).',
        v_total_beneficiados;
END;
$$;

-- ===============================================================
-- SUÍTE DE TESTES DAS PROCEDURES (COM TRANSAÇÕES ISOLADAS)
-- ===============================================================

-- Teste A1: Cadastrar cliente
DO $$ 
BEGIN
    RAISE NOTICE '>>> TESTE A1: sp_cadastrar_cliente';
END $$;

BEGIN;
-- Teste positivo
CALL sp_cadastrar_cliente('Lucas Santana', 'lucas.santana@teste.com', '17998877665', 'Jales', 'SP', 1500.00);
SELECT id_cliente, nome, email, limite_credito FROM clientes WHERE email = 'lucas.santana@teste.com';

-- Teste negativo: e-mail duplicado
DO $$
BEGIN
    BEGIN
        CALL sp_cadastrar_cliente('Lucas Clone', 'lucas.santana@teste.com', '17998877665', 'Jales', 'SP');
        RAISE EXCEPTION 'Falha: Não bloqueou e-mail duplicado.';
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste negativo A1 OK: %', SQLERRM;
    END;
END $$;
ROLLBACK;

-- Teste A2: Reajustar preco produto
BEGIN;
CALL sp_reajustar_preco_produto(2, 10.0); -- Mouse USB 55 -> 60.50
SELECT id_produto, nome, preco FROM produtos WHERE id_produto = 2;

-- Teste negativo: produto inexistente
DO $$
BEGIN
    BEGIN
        CALL sp_reajustar_preco_produto(9999, 10.0);
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste negativo A2 OK: %', SQLERRM;
    END;
END $$;
ROLLBACK;

-- Teste A3: Alterar status cliente
BEGIN;
CALL sp_alterar_status_cliente(6, TRUE); -- Felipe Nunes estava inativo
SELECT id_cliente, nome, ativo FROM clientes WHERE id_cliente = 6;
ROLLBACK;

-- Teste A4: Repor estoque
BEGIN;
CALL sp_repor_estoque(12, 15, 'Recebimento de Lote do Fornecedor'); -- Hub USB estava com 0
SELECT id_produto, nome, estoque FROM produtos WHERE id_produto = 12;
SELECT * FROM historico_estoque WHERE id_produto = 12 ORDER BY id_historico DESC LIMIT 1;
ROLLBACK;

-- Teste A5: Aplicar desconto categoria
BEGIN;
CALL sp_aplicar_desconto_categoria('Papelaria', 5.0);
SELECT id_produto, nome, preco FROM produtos WHERE id_categoria = 2;
ROLLBACK;

-- Teste A6: Reajustar salarios por cargo
BEGIN;
CALL sp_reajustar_salarios_cargo('VENDEDOR', 5.0);
SELECT id_funcionario, nome, cargo, salario FROM funcionarios WHERE cargo = 'VENDEDOR';
ROLLBACK;

-- Teste A7: Cancelar pedido
BEGIN;
CALL sp_cancelar_pedido(5, 'Desistência formal antes do envio');
SELECT id_pedido, status, observacao FROM pedidos WHERE id_pedido = 5;

-- Teste negativo: pedido já entregue
DO $$
BEGIN
    BEGIN
        CALL sp_cancelar_pedido(1, 'Tentativa em pedido entregue');
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste negativo A7 OK: %', SQLERRM;
    END;
END $$;
ROLLBACK;

-- Teste A8: Registrar pagamento integral
BEGIN;
-- Pedido 8 (Aberto, subtotal = 180 + 320 = 500, frete 0, desc 0)
CALL sp_registrar_pagamento(8, 500.00, 'PIX');
SELECT id_pedido, status FROM pedidos WHERE id_pedido = 8;
SELECT * FROM pagamentos WHERE id_pedido = 8;
ROLLBACK;

-- Teste A9: Transferir estoque
BEGIN;
CALL sp_transferir_estoque(5, 11, 10, 'Rebalanceamento de papelaria');
SELECT id_produto, nome, estoque FROM produtos WHERE id_produto IN (5, 11);
SELECT * FROM historico_estoque ORDER BY id_historico DESC LIMIT 2;
ROLLBACK;

-- Teste A10: Bonus clientes fieis
BEGIN;
CALL sp_bonus_clientes_fieis();
SELECT id_cliente, nome, limite_credito FROM clientes WHERE id_cliente = 1; -- Ana Souza tem pedidos 1 (entregue) e 11 (aberto)
ROLLBACK;
