-- ============================================================================
-- Disciplina : Topicos Avancados em Banco de Dados (4o Semestre)
-- Professor  : Prof. Welington Garcia
-- Tema       : Resolucao dos Exercicios Praticos de Triggers
-- Execucao   : psql -U postgres -d seu_banco -f exercicios.sql
-- ============================================================================

-- Limpeza idempotente geral
DROP TABLE IF EXISTS log_funcionarios CASCADE;
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;

-- ============================================================================
-- EXERCICIO 1: Auditoria Completa de Dados com Triggers
-- ============================================================================
CREATE TABLE funcionarios (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    salario NUMERIC(10, 2) NOT NULL
);

CREATE TABLE log_funcionarios (
    id_log SERIAL PRIMARY KEY,
    id_funcionario INT,
    operacao VARCHAR(10) NOT NULL,
    usuario_bd VARCHAR(50) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valores_antigos JSONB,
    valores_novos JSONB
);

CREATE OR REPLACE FUNCTION fn_auditoria_funcionarios()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO log_funcionarios (id_funcionario, operacao, usuario_bd, valores_antigos, valores_novos)
        VALUES (NEW.id_funcionario, TG_OP, CURRENT_USER, NULL, to_jsonb(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO log_funcionarios (id_funcionario, operacao, usuario_bd, valores_antigos, valores_novos)
        VALUES (NEW.id_funcionario, TG_OP, CURRENT_USER, to_jsonb(OLD), to_jsonb(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO log_funcionarios (id_funcionario, operacao, usuario_bd, valores_antigos, valores_novos)
        VALUES (OLD.id_funcionario, TG_OP, CURRENT_USER, to_jsonb(OLD), NULL);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$;

CREATE TRIGGER trg_auditoria_funcionarios
AFTER INSERT OR UPDATE OR DELETE ON funcionarios
FOR EACH ROW
EXECUTE FUNCTION fn_auditoria_funcionarios();

-- ============================================================================
-- EXERCICIO 2: Validacao de Regras de Negocio com Trigger BEFORE
-- ============================================================================
CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    preco_custo NUMERIC(10, 2) NOT NULL,
    preco_venda NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INT NOT NULL DEFAULT 0
);

CREATE OR REPLACE FUNCTION fn_valida_regras_produto()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- O preco de venda deve ser maior que zero
    IF NEW.preco_venda <= 0 THEN
        RAISE EXCEPTION 'Regra violada: O preco de venda deve ser maior que zero. Informado: %', NEW.preco_venda;
    END IF;

    -- O preco de venda nao pode ser inferior ao preco de custo
    IF NEW.preco_venda < NEW.preco_custo THEN
        RAISE EXCEPTION 'Regra violada: O preco de venda (%) nao pode ser menor que o preco de custo (%).', NEW.preco_venda, NEW.preco_custo;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_valida_produto
BEFORE INSERT OR UPDATE ON produtos
FOR EACH ROW
EXECUTE FUNCTION fn_valida_regras_produto();

-- ============================================================================
-- EXERCICIO 3: Sincronizacao e Atualizacao Automatica de Estoque
-- ============================================================================
CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) DEFAULT 'ABERTO',
    valor_total NUMERIC(10, 2) DEFAULT 0.00
);

CREATE TABLE itens_pedido (
    id_item SERIAL PRIMARY KEY,
    id_pedido INT REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    id_produto INT REFERENCES produtos(id_produto),
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL
);

CREATE OR REPLACE FUNCTION fn_atualiza_estoque_item()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_estoque_atual INT;
BEGIN
    IF (TG_OP = 'INSERT') THEN
        SELECT quantidade_estoque INTO v_estoque_atual 
        FROM produtos 
        WHERE id_produto = NEW.id_produto;

        IF v_estoque_atual < NEW.quantidade THEN
            RAISE EXCEPTION 'Estoque insuficiente para o produto ID %. Disponivel: %, Solicitado: %',
                NEW.id_produto, v_estoque_atual, NEW.quantidade;
        END IF;

        UPDATE produtos
        SET quantidade_estoque = quantidade_estoque - NEW.quantidade
        WHERE id_produto = NEW.id_produto;

        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE produtos
        SET quantidade_estoque = quantidade_estoque + OLD.quantidade
        WHERE id_produto = OLD.id_produto;

        RETURN OLD;

    ELSIF (TG_OP = 'UPDATE') THEN
        IF (NEW.id_produto <> OLD.id_produto) THEN
            RAISE EXCEPTION 'Nao e permitido alterar o produto de um item de pedido ja cadastrado.';
        END IF;

        -- Ajusta a diferenca entre a quantidade nova e antiga
        UPDATE produtos
        SET quantidade_estoque = quantidade_estoque - (NEW.quantidade - OLD.quantidade)
        WHERE id_produto = NEW.id_produto;

        RETURN NEW;
    END IF;

    RETURN NULL;
END;
$$;

CREATE TRIGGER trg_atualiza_estoque
AFTER INSERT OR UPDATE OR DELETE ON itens_pedido
FOR EACH ROW
EXECUTE FUNCTION fn_atualiza_estoque_item();

-- ============================================================================
-- EXERCICIO 4: Controle de Modificacao com Clausula WHEN
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_atualiza_timestamp_pedido()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.data_atualizacao := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_atualiza_timestamp_pedido
BEFORE UPDATE ON pedidos
FOR EACH ROW
WHEN (
    OLD.status IS DISTINCT FROM NEW.status OR
    OLD.valor_total IS DISTINCT FROM NEW.valor_total
)
EXECUTE FUNCTION fn_atualiza_timestamp_pedido();

-- ============================================================================
-- BATERIA DE TESTES E VALIDACAO DOS EXERCICIOS
-- ============================================================================
-- Teste Exercicio 1: Auditoria
INSERT INTO funcionarios (nome, cargo, salario) VALUES ('Carlos Souza', 'Analista de BD', 5500.00);
UPDATE funcionarios SET salario = 6200.00 WHERE nome = 'Carlos Souza';
DELETE FROM funcionarios WHERE nome = 'Carlos Souza';
SELECT id_log, operacao, usuario_bd, data_hora, valores_antigos, valores_novos FROM log_funcionarios;

-- Teste Exercicio 2: Validacao de Produto
INSERT INTO produtos (descricao, preco_custo, preco_venda, quantidade_estoque) 
VALUES ('Teclado Mecanico', 120.00, 250.00, 30);

-- Teste Exercicio 3: Movimentacao de Estoque
INSERT INTO pedidos (cliente, valor_total) VALUES ('UniFEF Laboratorio', 500.00);
INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES (1, 1, 5, 250.00);

-- Validando reducao do estoque para 25
SELECT id_produto, descricao, quantidade_estoque FROM produtos WHERE id_produto = 1;

-- Teste Exercicio 4: Clausula WHEN em Pedidos
SELECT id_pedido, status, valor_total, data_atualizacao FROM pedidos WHERE id_pedido = 1;
UPDATE pedidos SET status = 'CONCLUIDO' WHERE id_pedido = 1;
SELECT id_pedido, status, valor_total, data_atualizacao FROM pedidos WHERE id_pedido = 1;
