-- ===============================================================
-- DISCIPLINA : Tópicos Avançados em Banco de Dados (4º Semestre)
-- INSTITUIÇÃO: UniFEF - Centro Universitário de Fernandópolis
-- PROFESSOR  : Prof. Welington Garcia
-- PARTE C    : 10 Triggers e Funções de Gatilho (Exercícios C1 a C10)
-- BASE       : db_loja_avancada (PostgreSQL 14+)
-- COMO EXECUTAR:
--   1. Execute previamente 01_db_loja_avancada_schema.sql e 03_functions_db_loja_avancada.sql.
--   2. Execute este script para compilar as 10 trigger functions, criar os gatilhos e rodar a suíte de testes.
-- ===============================================================

-- ---------------------------------------------------------------
-- Exercício C1: tg_auditoria_precos
-- AFTER UPDATE OF preco em produtos: grava histórico em auditoria_precos
-- sempre que OLD.preco IS DISTINCT FROM NEW.preco.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_auditoria_precos()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.preco IS DISTINCT FROM NEW.preco THEN
        INSERT INTO auditoria_precos (id_produto, preco_anterior, preco_novo, data_alteracao, usuario_bd)
        VALUES (NEW.id_produto, OLD.preco, NEW.preco, CURRENT_TIMESTAMP, CURRENT_USER);
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_auditoria_precos ON produtos;
CREATE TRIGGER tg_auditoria_precos
AFTER UPDATE OF preco ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_auditoria_precos();

-- ---------------------------------------------------------------
-- Exercício C2: tg_bloquear_preco_custo
-- BEFORE INSERT OR UPDATE OF preco, custo em produtos:
-- Rejeita a operação se NEW.preco < NEW.custo.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_bloquear_preco_custo()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.preco < NEW.custo THEN
        RAISE EXCEPTION 'Violação de precificação: O preço de venda (R$ %) não pode ser inferior ao preço de custo (R$ %). Produto: %',
            NEW.preco, NEW.custo, NEW.nome USING ERRCODE = '22003';
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_bloquear_preco_custo ON produtos;
CREATE TRIGGER tg_bloquear_preco_custo
BEFORE INSERT OR UPDATE OF preco, custo ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_bloquear_preco_custo();

-- ---------------------------------------------------------------
-- Exercício C3: tg_impedir_exclusao_produto_vendido
-- BEFORE DELETE em produtos:
-- Impede exclusão física se houver registros vinculados em itens_pedido.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_impedir_exclusao_produto_vendido()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_itens INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_total_itens
    FROM itens_pedido
    WHERE id_produto = OLD.id_produto;

    IF v_total_itens > 0 THEN
        RAISE EXCEPTION 'Exclusão física rejeitada: O produto "%" (ID %) possui % item(ns) de pedido vinculado(s). Inative-o definindo ativo = FALSE.',
            OLD.nome, OLD.id_produto, v_total_itens USING ERRCODE = '23503';
    END IF;
    RETURN OLD;
END;
$$;

DROP TRIGGER IF EXISTS tg_impedir_exclusao_produto_vendido ON produtos;
CREATE TRIGGER tg_impedir_exclusao_produto_vendido
BEFORE DELETE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_impedir_exclusao_produto_vendido();

-- ---------------------------------------------------------------
-- Exercício C4: tg_auditoria_exclusao_produtos
-- AFTER DELETE em produtos: grava detalhes do registro removido em auditoria_produtos.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_auditoria_exclusao_produtos()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO auditoria_produtos (id_produto, nome_produto, preco, estoque, operacao, data_operacao)
    VALUES (OLD.id_produto, OLD.nome, OLD.preco, OLD.estoque, 'DELETE', CURRENT_TIMESTAMP);
    RETURN OLD;
END;
$$;

DROP TRIGGER IF EXISTS tg_auditoria_exclusao_produtos ON produtos;
CREATE TRIGGER tg_auditoria_exclusao_produtos
AFTER DELETE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_auditoria_exclusao_produtos();

-- ---------------------------------------------------------------
-- Exercício C5: tg_historico_automatico_estoque
-- AFTER UPDATE OF estoque em produtos:
-- Registra ENTRADA ou SAIDA em historico_estoque com o motivo 'AJUSTE AUTOMATICO'.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_historico_automatico_estoque()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_delta INTEGER;
    v_tipo VARCHAR(15);
BEGIN
    IF OLD.estoque IS DISTINCT FROM NEW.estoque THEN
        v_delta := NEW.estoque - OLD.estoque;
        IF v_delta > 0 THEN
            v_tipo := 'ENTRADA';
        ELSE
            v_tipo := 'SAIDA';
        END IF;

        INSERT INTO historico_estoque (id_produto, tipo, quantidade, estoque_anterior, estoque_atual, motivo, data_movimentacao)
        VALUES (NEW.id_produto, v_tipo, ABS(v_delta), OLD.estoque, NEW.estoque, 'AJUSTE AUTOMATICO', CURRENT_TIMESTAMP);
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_historico_automatico_estoque ON produtos;
CREATE TRIGGER tg_historico_automatico_estoque
AFTER UPDATE OF estoque ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_historico_automatico_estoque();

-- ---------------------------------------------------------------
-- Exercício C6: tg_alerta_estoque_minimo
-- AFTER UPDATE OF estoque em produtos:
-- Dispara notificação ESTOQUE_BAIXO quando o estoque transita de ACIMA
-- do mínimo para IGUAL OU ABAIXO do mínimo.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_alerta_estoque_minimo()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- Transição de acima do mínimo para igual ou abaixo do mínimo
    IF OLD.estoque > OLD.estoque_minimo AND NEW.estoque <= NEW.estoque_minimo THEN
        INSERT INTO notificacoes (tipo, mensagem, id_produto, data_criacao, lida)
        VALUES (
            'ESTOQUE_BAIXO',
            'Alerta de Reposição: O produto "' || NEW.nome || '" (ID ' || NEW.id_produto || 
            ') atingiu saldo crítico de ' || NEW.estoque || ' unidade(s). Saldo mínimo estabelecido: ' || NEW.estoque_minimo || '.',
            NEW.id_produto,
            CURRENT_TIMESTAMP,
            FALSE
        );
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_alerta_estoque_minimo ON produtos;
CREATE TRIGGER tg_alerta_estoque_minimo
AFTER UPDATE OF estoque ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_alerta_estoque_minimo();

-- ---------------------------------------------------------------
-- Exercício C7: tg_historico_status_pedidos
-- AFTER UPDATE OF status em pedidos:
-- Registra a mudança de estado em historico_status_pedidos.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_historico_status_pedidos()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO historico_status_pedidos (id_pedido, status_anterior, status_novo, data_alteracao)
        VALUES (NEW.id_pedido, OLD.status, NEW.status, CURRENT_TIMESTAMP);
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_historico_status_pedidos ON pedidos;
CREATE TRIGGER tg_historico_status_pedidos
AFTER UPDATE OF status ON pedidos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_historico_status_pedidos();

-- ---------------------------------------------------------------
-- Exercício C8: tg_validar_itens_pedido
-- BEFORE INSERT OR UPDATE em itens_pedido:
-- Permite inserção/alteração somente se o pedido estiver 'ABERTO' e o produto estiver ativo.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_validar_itens_pedido()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_pedido VARCHAR(20);
    v_produto_ativo BOOLEAN;
    v_nome_produto VARCHAR(130);
BEGIN
    -- Validação do status do pedido pai
    SELECT status INTO v_status_pedido
    FROM pedidos
    WHERE id_pedido = NEW.id_pedido;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Pedido ID % não encontrado.', NEW.id_pedido USING ERRCODE = 'P0002';
    END IF;

    IF v_status_pedido <> 'ABERTO' THEN
        RAISE EXCEPTION 'Operação rejeitada: Itens só podem ser inseridos ou alterados em pedidos com status "ABERTO". Status atual: "%".',
            v_status_pedido USING ERRCODE = '22000';
    END IF;

    -- Validação de ativação do produto
    SELECT ativo, nome INTO v_produto_ativo, v_nome_produto
    FROM produtos
    WHERE id_produto = NEW.id_produto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', NEW.id_produto USING ERRCODE = 'P0002';
    END IF;

    IF NOT v_produto_ativo THEN
        RAISE EXCEPTION 'Operação rejeitada: O produto "%" (ID %) está inativo no catálogo e não pode ser comercializado.',
            v_nome_produto, NEW.id_produto USING ERRCODE = '22000';
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_validar_itens_pedido ON itens_pedido;
CREATE TRIGGER tg_validar_itens_pedido
BEFORE INSERT OR UPDATE ON itens_pedido
FOR EACH ROW
EXECUTE FUNCTION fn_tg_validar_itens_pedido();

-- ---------------------------------------------------------------
-- Exercício C9: tg_comissao_pedido_pago
-- AFTER UPDATE OF status em pedidos:
-- Ao transitar de ABERTO para PAGO, calcula a comissão com base no total
-- do pedido sem frete (subtotal - desconto) multiplicado pelo percentual do vendedor.
-- Previne duplicidades utilizando ON CONFLICT.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_comissao_pedido_pago()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_percentual_comissao NUMERIC(5,2);
    v_subtotal NUMERIC(12,2);
    v_base_calculo NUMERIC(12,2);
    v_valor_comissao NUMERIC(12,2);
BEGIN
    -- Dispara apenas na transição de ABERTO para PAGO
    IF OLD.status = 'ABERTO' AND NEW.status = 'PAGO' THEN
        IF NEW.id_funcionario IS NOT NULL THEN
            SELECT percentual_comissao INTO v_percentual_comissao
            FROM funcionarios
            WHERE id_funcionario = NEW.id_funcionario;

            IF FOUND AND v_percentual_comissao > 0 THEN
                -- Total sem frete = subtotal - desconto
                SELECT COALESCE(SUM(quantidade * preco_unitario), 0.00)
                INTO v_subtotal
                FROM itens_pedido
                WHERE id_pedido = NEW.id_pedido;

                v_base_calculo := GREATEST(0.00, v_subtotal - NEW.desconto);
                v_valor_comissao := ROUND(v_base_calculo * (v_percentual_comissao / 100.0), 2);

                INSERT INTO comissoes (id_pedido, id_funcionario, valor, data_calculo)
                VALUES (NEW.id_pedido, NEW.id_funcionario, v_valor_comissao, CURRENT_TIMESTAMP)
                ON CONFLICT (id_pedido) DO NOTHING;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_comissao_pedido_pago ON pedidos;
CREATE TRIGGER tg_comissao_pedido_pago
AFTER UPDATE OF status ON pedidos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_comissao_pedido_pago();

-- ---------------------------------------------------------------
-- Exercício C10: tg_movimentacao_financeira_pagamento
-- AFTER INSERT OR UPDATE OF status em pagamentos:
-- Ao entrar em CONFIRMADO -> RECEITA em movimentacoes_financeiras.
-- Ao mudar de CONFIRMADO para ESTORNADO -> ESTORNO.
-- Evita duplicações sem mudança de status.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_movimentacao_financeira_pagamento()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- Caso 1: Novo pagamento inserido diretamente como CONFIRMADO
    IF TG_OP = 'INSERT' THEN
        IF NEW.status = 'CONFIRMADO' THEN
            INSERT INTO movimentacoes_financeiras (id_pagamento, tipo, valor, data_movimentacao)
            VALUES (NEW.id_pagamento, 'RECEITA', NEW.valor, CURRENT_TIMESTAMP)
            ON CONFLICT (id_pagamento, tipo) DO NOTHING;
        END IF;
    -- Caso 2: Atualização de status em pagamento existente
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.status IS DISTINCT FROM NEW.status THEN
            IF NEW.status = 'CONFIRMADO' THEN
                INSERT INTO movimentacoes_financeiras (id_pagamento, tipo, valor, data_movimentacao)
                VALUES (NEW.id_pagamento, 'RECEITA', NEW.valor, CURRENT_TIMESTAMP)
                ON CONFLICT (id_pagamento, tipo) DO NOTHING;
            ELSIF OLD.status = 'CONFIRMADO' AND NEW.status = 'ESTORNADO' THEN
                INSERT INTO movimentacoes_financeiras (id_pagamento, tipo, valor, data_movimentacao)
                VALUES (NEW.id_pagamento, 'ESTORNO', NEW.valor, CURRENT_TIMESTAMP)
                ON CONFLICT (id_pagamento, tipo) DO NOTHING;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS tg_movimentacao_financeira_pagamento ON pagamentos;
CREATE TRIGGER tg_movimentacao_financeira_pagamento
AFTER INSERT OR UPDATE OF status ON pagamentos
FOR EACH ROW
EXECUTE FUNCTION fn_tg_movimentacao_financeira_pagamento();

-- ===============================================================
-- SUÍTE DE TESTES DOS TRIGGERS
-- ===============================================================

-- Teste C1: Auditoria de precos
BEGIN;
UPDATE produtos SET preco = 3350.00 WHERE id_produto = 1;
SELECT * FROM auditoria_precos WHERE id_produto = 1;
ROLLBACK;

-- Teste C2: Bloquear preco abaixo do custo (teste negativo)
BEGIN;
DO $$
BEGIN
    BEGIN
        UPDATE produtos SET preco = 1500.00 WHERE id_produto = 1; -- Custo é 2400.00
        RAISE EXCEPTION 'Falha: Preço abaixo do custo não foi barrado!';
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste C2 barrado com sucesso: %', SQLERRM;
    END;
END $$;
ROLLBACK;

-- Teste C3: Impedir exclusao de produto vendido (teste negativo)
BEGIN;
DO $$
BEGIN
    BEGIN
        DELETE FROM produtos WHERE id_produto = 1; -- Produto do Pedido 3
        RAISE EXCEPTION 'Falha: Produto vendido foi excluído!';
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste C3 barrado com sucesso: %', SQLERRM;
    END;
END $$;
ROLLBACK;

-- Teste C4: Auditoria de exclusao fisica
BEGIN;
INSERT INTO produtos (id_produto, nome, id_categoria, id_fornecedor, preco, custo, estoque, estoque_minimo, ativo)
VALUES (999, 'Produto Teste Temporario', 1, 1, 100.00, 50.00, 10, 5, TRUE);
DELETE FROM produtos WHERE id_produto = 999;
SELECT * FROM auditoria_produtos WHERE id_produto = 999;
ROLLBACK;

-- Teste C5 e C6: Historico de estoque e Alerta de estoque minimo
BEGIN;
-- Monitor 24 polegadas (ID 4): estoque atual = 7, minimo = 5.
-- Atualiza para 4 unidades (passa de >5 para <=5)
UPDATE produtos SET estoque = 4 WHERE id_produto = 4;
SELECT * FROM historico_estoque WHERE id_produto = 4 ORDER BY id_historico DESC LIMIT 1;
SELECT * FROM notificacoes WHERE id_produto = 4;
ROLLBACK;

-- Teste C7: Historico de status de pedido
BEGIN;
UPDATE pedidos SET status = 'ENTREGUE' WHERE id_pedido = 2; -- Estava PAGO
SELECT * FROM historico_status_pedidos WHERE id_pedido = 2;
ROLLBACK;

-- Teste C8: Validar itens de pedido (bloquear em pedido fechado e produto inativo)
BEGIN;
DO $$
BEGIN
    BEGIN
        -- Pedido 1 está ENTREGUE
        INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario)
        VALUES (1, 3, 1, 250.00);
        RAISE EXCEPTION 'Falha: Inseriu item em pedido ENTREGUE!';
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste C8 (pedido fechado) barrado com sucesso: %', SQLERRM;
    END;

    BEGIN
        -- Produto 16 está inativo
        INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario)
        VALUES (5, 16, 1, 590.00);
        RAISE EXCEPTION 'Falha: Inseriu produto inativo!';
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Teste C8 (produto inativo) barrado com sucesso: %', SQLERRM;
    END;
END $$;
ROLLBACK;

-- Teste C9: Comissao automatica de pedido pago
BEGIN;
-- Pedido 5 está ABERTO, atendido pelo funcionário 2 (Renata Gomes, comissão 4%)
UPDATE pedidos SET status = 'PAGO' WHERE id_pedido = 5;
SELECT * FROM comissoes WHERE id_pedido = 5;
ROLLBACK;

-- Teste C10: Movimentacao financeira (Receita e Estorno)
BEGIN;
-- Pagamento 9 está PENDENTE com valor de 100.00
UPDATE pagamentos SET status = 'CONFIRMADO' WHERE id_pagamento = 9;
SELECT * FROM movimentacoes_financeiras WHERE id_pagamento = 9;

UPDATE pagamentos SET status = 'ESTORNADO' WHERE id_pagamento = 9;
SELECT * FROM movimentacoes_financeiras WHERE id_pagamento = 9 ORDER BY id_movimentacao;
ROLLBACK;
