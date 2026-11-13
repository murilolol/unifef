-- ============================================================================
-- DISCIPLINA : Tópicos Avançados em Banco de Dados (4º Semestre)
-- PROFESSOR  : Prof. Welington Garcia
-- INSTITUIÇÃO: UniFEF - Centro Universitário de Votuporanga
-- TEMA       : Resolução Completa dos Exercícios de Triggers (Gatilhos) em PostgreSQL
-- BANCO      : db_loja_triggers
-- COMO EXECUTAR:
--   psql -U postgres -d db_loja_triggers -f resolucao_exercicios_triggers.sql
-- ============================================================================

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 1: BEFORE INSERT OR UPDATE em produtos: impedir preco < 0
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_valida_preco_produto()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.preco < 0 THEN
        RAISE EXCEPTION 'Regra violada: O preço do produto não pode ser negativo. Valor informado: %', NEW.preco;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_preco_produto ON produtos;
CREATE TRIGGER trg_valida_preco_produto
BEFORE INSERT OR UPDATE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_valida_preco_produto();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 2: BEFORE INSERT OR UPDATE em produtos: impedir estoque < 0
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_valida_estoque_produto()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estoque < 0 THEN
        RAISE EXCEPTION 'Regra violada: A quantidade em estoque não pode ser negativa. Valor informado: %', NEW.estoque;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_estoque_produto ON produtos;
CREATE TRIGGER trg_valida_estoque_produto
BEFORE INSERT OR UPDATE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_valida_estoque_produto();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 3: BEFORE INSERT OR UPDATE em produtos: transformar nome em maiúsculas
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_normaliza_nome_produto()
RETURNS TRIGGER AS $$
BEGIN
    NEW.nome := UPPER(NEW.nome);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_normaliza_nome_produto ON produtos;
CREATE TRIGGER trg_normaliza_nome_produto
BEFORE INSERT OR UPDATE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_normaliza_nome_produto();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 4: AFTER UPDATE OF preco: registrar alteração em auditoria_produtos
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_audita_preco_produto()
RETURNS TRIGGER AS $$
BEGIN
    -- Registra apenas quando o preço sofrer alteração efetiva de valor
    IF OLD.preco IS DISTINCT FROM NEW.preco THEN
        INSERT INTO auditoria_produtos (
            id_produto,
            operacao,
            preco_antigo,
            preco_novo,
            data_operacao
        ) VALUES (
            NEW.id_produto,
            'UPDATE_PRECO',
            OLD.preco,
            NEW.preco,
            CURRENT_TIMESTAMP
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_audita_preco_produto ON produtos;
CREATE TRIGGER trg_audita_preco_produto
AFTER UPDATE OF preco ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_audita_preco_produto();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 5: AFTER UPDATE OF estoque: registrar em historico_estoque
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_historico_alteracao_estoque()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.estoque IS DISTINCT FROM NEW.estoque THEN
        INSERT INTO historico_estoque (
            id_produto,
            estoque_antigo,
            estoque_novo,
            data_alteracao
        ) VALUES (
            NEW.id_produto,
            OLD.estoque,
            NEW.estoque,
            CURRENT_TIMESTAMP
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_historico_alteracao_estoque ON produtos;
CREATE TRIGGER trg_historico_alteracao_estoque
AFTER UPDATE OF estoque ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_historico_alteracao_estoque();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 6: BEFORE DELETE em clientes: salvar dados em clientes_excluidos
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_backup_cliente_excluido()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO clientes_excluidos (
        id_cliente,
        nome,
        email,
        telefone,
        data_exclusao
    ) VALUES (
        OLD.id_cliente,
        OLD.nome,
        OLD.email,
        OLD.telefone,
        CURRENT_TIMESTAMP
    );
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_backup_cliente_excluido ON clientes;
CREATE TRIGGER trg_backup_cliente_excluido
BEFORE DELETE ON clientes
FOR EACH ROW
EXECUTE FUNCTION fn_backup_cliente_excluido();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 7: BEFORE DELETE em produtos: salvar dados em produtos_excluidos
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_backup_produto_excluido()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO produtos_excluidos (
        id_produto,
        nome,
        preco,
        estoque,
        data_exclusao
    ) VALUES (
        OLD.id_produto,
        OLD.nome,
        OLD.preco,
        OLD.estoque,
        CURRENT_TIMESTAMP
    );
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_backup_produto_excluido ON produtos;
CREATE TRIGGER trg_backup_produto_excluido
BEFORE DELETE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_backup_produto_excluido();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 8: Impedir DELETE de produtos quando estoque > 0
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_impede_delete_produto_com_estoque()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.estoque > 0 THEN
        RAISE EXCEPTION 'Exclusão rejeitada: O produto "%" (ID %) possui % unidades em estoque.', OLD.nome, OLD.id_produto, OLD.estoque;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_impede_delete_produto_com_estoque ON produtos;
CREATE TRIGGER trg_impede_delete_produto_com_estoque
BEFORE DELETE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_impede_delete_produto_com_estoque();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 9: BEFORE INSERT OR UPDATE em funcionarios: impedir salario < 1500
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_valida_salario_minimo_funcionario()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.salario < 1500.00 THEN
        RAISE EXCEPTION 'Regra de negócio: O salário não pode ser inferior ao piso de R$ 1.500,00. Informado: %', NEW.salario;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_salario_funcionario ON funcionarios;
CREATE TRIGGER trg_valida_salario_funcionario
BEFORE INSERT OR UPDATE ON funcionarios
FOR EACH ROW
EXECUTE FUNCTION fn_valida_salario_minimo_funcionario();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 10: BEFORE INSERT em itens_venda: impedir quantidade <= 0
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_valida_quantidade_item_venda()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantidade <= 0 THEN
        RAISE EXCEPTION 'A quantidade vendida deve ser obrigatoriamente maior que zero. Informado: %', NEW.quantidade;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_quantidade_item_venda ON itens_venda;
CREATE TRIGGER trg_valida_quantidade_item_venda
BEFORE INSERT ON itens_venda
FOR EACH ROW
EXECUTE FUNCTION fn_valida_quantidade_item_venda();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 11: BEFORE INSERT OR UPDATE em itens_venda: calcular subtotal
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_calcula_subtotal_item_venda()
RETURNS TRIGGER AS $$
BEGIN
    NEW.subtotal := NEW.quantidade * NEW.preco_unitario;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_calcula_subtotal_item_venda ON itens_venda;
CREATE TRIGGER trg_calcula_subtotal_item_venda
BEFORE INSERT OR UPDATE ON itens_venda
FOR EACH ROW
EXECUTE FUNCTION fn_calcula_subtotal_item_venda();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 12: AFTER INSERT em itens_venda: diminuir estoque do produto
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_baixa_estoque_venda()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE produtos
       SET estoque = estoque - NEW.quantidade
     WHERE id_produto = NEW.id_produto;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_baixa_estoque_venda ON itens_venda;
CREATE TRIGGER trg_baixa_estoque_venda
AFTER INSERT ON itens_venda
FOR EACH ROW
EXECUTE FUNCTION fn_baixa_estoque_venda();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 13: Antes de inserir item, verificar se há estoque suficiente
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_verifica_estoque_disponivel()
RETURNS TRIGGER AS $$
DECLARE
    v_estoque_atual INTEGER;
    v_nome_produto VARCHAR(120);
BEGIN
    SELECT estoque, nome
      INTO v_estoque_atual, v_nome_produto
      FROM produtos
     WHERE id_produto = NEW.id_produto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto com ID % não localizado no cadastro.', NEW.id_produto;
    END IF;

    IF v_estoque_atual < NEW.quantidade THEN
        RAISE EXCEPTION 'Estoque insuficiente para o produto "%" (ID %). Saldo disponível: %, Quantidade solicitada: %',
            v_nome_produto, NEW.id_produto, v_estoque_atual, NEW.quantidade;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_verifica_estoque_disponivel ON itens_venda;
CREATE TRIGGER trg_verifica_estoque_disponivel
BEFORE INSERT ON itens_venda
FOR EACH ROW
EXECUTE FUNCTION fn_verifica_estoque_disponivel();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 14: Usar TG_OP para auditar INSERT, UPDATE e DELETE em produtos
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_audita_geral_produtos()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO auditoria_produtos (
            id_produto,
            operacao,
            nome_novo,
            preco_novo,
            estoque_novo,
            data_operacao
        ) VALUES (
            NEW.id_produto,
            'INSERT',
            NEW.nome,
            NEW.preco,
            NEW.estoque,
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO auditoria_produtos (
            id_produto,
            operacao,
            nome_antigo,
            nome_novo,
            preco_antigo,
            preco_novo,
            estoque_antigo,
            estoque_novo,
            data_operacao
        ) VALUES (
            NEW.id_produto,
            'UPDATE',
            OLD.nome,
            NEW.nome,
            OLD.preco,
            NEW.preco,
            OLD.estoque,
            NEW.estoque,
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO auditoria_produtos (
            id_produto,
            operacao,
            nome_antigo,
            preco_antigo,
            estoque_antigo,
            data_operacao
        ) VALUES (
            OLD.id_produto,
            'DELETE',
            OLD.nome,
            OLD.preco,
            OLD.estoque,
            CURRENT_TIMESTAMP
        );
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_audita_geral_produtos ON produtos;
CREATE TRIGGER trg_audita_geral_produtos
AFTER INSERT OR UPDATE OR DELETE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_audita_geral_produtos();

-- ----------------------------------------------------------------------------
-- EXERCÍCIO 15: DESAFIO - Atualizar vendas.valor_total ao manipular itens_venda
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_sincroniza_total_venda()
RETURNS TRIGGER AS $$
DECLARE
    v_id_venda INTEGER;
BEGIN
    -- Determina o identificador da venda associada de acordo com o evento
    IF TG_OP = 'DELETE' THEN
        v_id_venda := OLD.id_venda;
    ELSE
        v_id_venda := NEW.id_venda;
    END IF;

    -- Recalcula o somatório dos subtotais e atualiza o cabeçalho da venda
    UPDATE vendas
       SET valor_total = COALESCE((
           SELECT SUM(subtotal)
             FROM itens_venda
            WHERE id_venda = v_id_venda
       ), 0.00)
     WHERE id_venda = v_id_venda;

    -- Se houve transferência do item para outra venda em um UPDATE, atualiza a anterior também
    IF TG_OP = 'UPDATE' AND OLD.id_venda <> NEW.id_venda THEN
        UPDATE vendas
           SET valor_total = COALESCE((
               SELECT SUM(subtotal)
                 FROM itens_venda
                WHERE id_venda = OLD.id_venda
           ), 0.00)
         WHERE id_venda = OLD.id_venda;
    END IF;

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sincroniza_total_venda ON itens_venda;
CREATE TRIGGER trg_sincroniza_total_venda
AFTER INSERT OR UPDATE OR DELETE ON itens_venda
FOR EACH ROW
EXECUTE FUNCTION fn_sincroniza_total_venda();

-- ============================================================================
-- BATERIA DE TESTES PRÁTICOS PARA VALIDAÇÃO DOS GATILHOS
-- ============================================================================

-- Teste 1 (Ex. 3): Padronização de nome de produto em maiúsculas
-- INSERT INTO produtos (nome, id_categoria, preco, estoque) VALUES ('teclado sem fio bluetooth', 1, 150.00, 10);
-- SELECT nome FROM produtos WHERE nome LIKE '%BLUETOOTH%';

-- Teste 2 (Ex. 1 e 2): Validação de preço e estoque negativos (deve falhar)
-- INSERT INTO produtos (nome, id_categoria, preco, estoque) VALUES ('Produto Inválido', 1, -10.00, 5);
-- UPDATE produtos SET estoque = -5 WHERE id_produto = 1;

-- Teste 3 (Ex. 4 e 5): Auditoria de preço e estoque
-- UPDATE produtos SET preco = 300.00 WHERE id_produto = 1;
-- UPDATE produtos SET estoque = 15 WHERE id_produto = 1;
-- SELECT * FROM auditoria_produtos WHERE id_produto = 1;
-- SELECT * FROM historico_estoque WHERE id_produto = 1;

-- Teste 4 (Ex. 6): Exclusão de cliente e envio para clientes_excluidos
-- DELETE FROM clientes WHERE id_cliente = 6;
-- SELECT * FROM clientes_excluidos;

-- Teste 5 (Ex. 8): Impedir deleção de produto com saldo positivo em estoque (deve falhar)
-- DELETE FROM produtos WHERE id_produto = 1;

-- Teste 6 (Ex. 9): Validação de piso salarial para funcionários (deve falhar)
-- INSERT INTO funcionarios (nome, cargo, salario, data_admissao) VALUES ('Estagiário', 'Auxiliar', 1200.00, CURRENT_DATE);

-- Teste 7 (Ex. 10, 11, 12, 13 e 15): Venda com verificação, cálculo de subtotal, baixa e recálculo da venda
-- INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario) VALUES (3, 2, 2, 180.00);
-- SELECT * FROM itens_venda WHERE id_venda = 3;
-- SELECT id_venda, valor_total FROM vendas WHERE id_venda = 3;
-- SELECT id_produto, estoque FROM produtos WHERE id_produto = 2;
