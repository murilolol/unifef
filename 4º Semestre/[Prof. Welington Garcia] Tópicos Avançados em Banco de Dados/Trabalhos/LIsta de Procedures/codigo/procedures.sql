-- ============================================================================
-- Disciplina: Tópicos Avançados em Banco de Dados (4º Semestre)
-- Professor : Prof. Welington Garcia
-- Tema      : Resolução da Lista de Stored Procedures
-- Como executar:
--   psql -U <usuario> -d <banco_de_dados> -f procedures.sql
-- ============================================================================

-- ============================================================================
-- Exercício 1
-- Crie uma procedure chamada aumentar_salario_funcionario que receba:
--   • p_id_funcionario
--   • p_percentual
-- E aumente o salário do funcionário informado de acordo com o percentual.
-- ============================================================================
CREATE OR REPLACE PROCEDURE aumentar_salario_funcionario(
    p_id_funcionario INT,
    p_percentual DECIMAL(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE funcionarios
    SET salario = salario + (salario * (p_percentual / 100.0))
    WHERE id_funcionario = p_id_funcionario;

    IF NOT FOUND THEN
        RAISE NOTICE 'Funcionário com ID % não encontrado.', p_id_funcionario;
    END IF;
END;
$$;

-- ============================================================================
-- Exercício 2
-- Crie uma procedure chamada atualizar_estoque_produto que receba:
--   • p_id_produto
--   • p_novo_estoque
-- E atualize a quantidade em estoque do produto.
-- ============================================================================
CREATE OR REPLACE PROCEDURE atualizar_estoque_produto(
    p_id_produto INT,
    p_novo_estoque INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_novo_estoque < 0 THEN
        RAISE EXCEPTION 'A quantidade em estoque não pode ser negativa: %', p_novo_estoque;
    END IF;

    UPDATE produtos
    SET estoque = p_novo_estoque
    WHERE id_produto = p_id_produto;

    IF NOT FOUND THEN
        RAISE NOTICE 'Produto com ID % não encontrado.', p_id_produto;
    END IF;
END;
$$;

-- ============================================================================
-- Exercício 3
-- Crie uma procedure chamada adicionar_saldo_cliente que receba:
--   • p_id_cliente
--   • p_valor
-- E some esse valor ao saldo atual do cliente.
-- ============================================================================
CREATE OR REPLACE PROCEDURE adicionar_saldo_cliente(
    p_id_cliente INT,
    p_valor DECIMAL(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_valor <= 0 THEN
        RAISE EXCEPTION 'O valor a adicionar deve ser maior que zero: %', p_valor;
    END IF;

    UPDATE clientes
    SET saldo = saldo + p_valor
    WHERE id_cliente = p_id_cliente;

    IF NOT FOUND THEN
        RAISE NOTICE 'Cliente com ID % não encontrado.', p_id_cliente;
    END IF;
END;
$$;

-- ============================================================================
-- Exercício 4
-- Crie uma procedure chamada descontar_saldo_cliente que receba:
--   • p_id_cliente
--   • p_valor
-- E desconte do saldo do cliente. A procedure deve impedir que o saldo fique negativo.
-- ============================================================================
CREATE OR REPLACE PROCEDURE descontar_saldo_cliente(
    p_id_cliente INT,
    p_valor DECIMAL(10,2)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_saldo_atual DECIMAL(10,2);
BEGIN
    IF p_valor <= 0 THEN
        RAISE EXCEPTION 'O valor a descontar deve ser maior que zero: %', p_valor;
    END IF;

    SELECT saldo INTO v_saldo_atual
    FROM clientes
    WHERE id_cliente = p_id_cliente;

    IF NOT FOUND THEN
        RAISE NOTICE 'Cliente com ID % não encontrado.', p_id_cliente;
        RETURN;
    END IF;

    IF (v_saldo_atual - p_valor) < 0 THEN
        RAISE EXCEPTION 'Saldo insuficiente. Saldo atual: %, Valor solicitado: %', v_saldo_atual, p_valor;
    END IF;

    UPDATE clientes
    SET saldo = saldo - p_valor
    WHERE id_cliente = p_id_cliente;
END;
$$;

-- ============================================================================
-- Exercício 5
-- Crie uma procedure chamada cadastrar_produto que receba:
--   • nome
--   • categoria
--   • preço
--   • estoque
-- E insira um novo produto na tabela produtos.
-- ============================================================================
CREATE OR REPLACE PROCEDURE cadastrar_produto(
    p_nome VARCHAR(100),
    p_categoria VARCHAR(50),
    p_preco DECIMAL(10,2),
    p_estoque INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_preco < 0 THEN
        RAISE EXCEPTION 'O preço não pode ser negativo: %', p_preco;
    END IF;

    IF p_estoque < 0 THEN
        RAISE EXCEPTION 'O estoque inicial não pode ser negativo: %', p_estoque;
    END IF;

    INSERT INTO produtos (nome, categoria, preco, estoque)
    VALUES (p_nome, p_categoria, p_preco, p_estoque);
END;
$$;

-- ============================================================================
-- Exercício 6
-- Crie uma procedure chamada cancelar_pedido que receba:
--   • p_id_pedido
-- E altere o status do pedido para CANCELADO.
-- ============================================================================
CREATE OR REPLACE PROCEDURE cancelar_pedido(
    p_id_pedido INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE pedidos
    SET status = 'CANCELADO'
    WHERE id_pedido = p_id_pedido;

    IF NOT FOUND THEN
        RAISE NOTICE 'Pedido com ID % não encontrado.', p_id_pedido;
    END IF;
END;
$$;

-- ============================================================================
-- Exercício 7
-- Crie uma procedure chamada fechar_pedido que receba:
--   • p_id_pedido
-- E altere o status para FECHADO, mas somente se ele estiver como ABERTO.
-- ============================================================================
CREATE OR REPLACE PROCEDURE fechar_pedido(
    p_id_pedido INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status VARCHAR(30);
BEGIN
    SELECT status INTO v_status
    FROM pedidos
    WHERE id_pedido = p_id_pedido;

    IF NOT FOUND THEN
        RAISE NOTICE 'Pedido com ID % não encontrado.', p_id_pedido;
        RETURN;
    END IF;

    IF v_status = 'ABERTO' THEN
        UPDATE pedidos
        SET status = 'FECHADO'
        WHERE id_pedido = p_id_pedido;
    ELSE
        RAISE NOTICE 'Pedido % não pode ser fechado pois seu status atual é %.', p_id_pedido, v_status;
    END IF;
END;
$$;

-- ============================================================================
-- Exercício 8
-- Crie uma procedure chamada recalcular_valor_pedido que receba:
--   • p_id_pedido
-- E atualize o campo valor_total da tabela pedidos, somando todos os subtotais dos itens daquele pedido.
-- ============================================================================
CREATE OR REPLACE PROCEDURE recalcular_valor_pedido(
    p_id_pedido INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_total DECIMAL(10,2);
BEGIN
    SELECT COALESCE(SUM(subtotal), 0.00)
    INTO v_total
    FROM itens_pedido
    WHERE id_pedido = p_id_pedido;

    UPDATE pedidos
    SET valor_total = v_total
    WHERE id_pedido = p_id_pedido;

    IF NOT FOUND THEN
        RAISE NOTICE 'Pedido com ID % não encontrado.', p_id_pedido;
    END IF;
END;
$$;

-- ============================================================================
-- Exercício 9
-- Crie uma procedure chamada inserir_item_pedido que receba:
--   • p_id_pedido
--   • p_id_produto
--   • p_quantidade
-- A procedure deve:
--   • buscar o preço do produto
--   • calcular o subtotal
--   • inserir em itens_pedido
--   • diminuir o estoque do produto
--   • recalcular o valor total do pedido
-- ============================================================================
CREATE OR REPLACE PROCEDURE inserir_item_pedido(
    p_id_pedido INT,
    p_id_produto INT,
    p_quantidade INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_preco DECIMAL(10,2);
    v_estoque INT;
    v_subtotal DECIMAL(10,2);
    v_status_pedido VARCHAR(30);
BEGIN
    IF p_quantidade <= 0 THEN
        RAISE EXCEPTION 'A quantidade informada deve ser maior que zero: %', p_quantidade;
    END IF;

    -- Validação do pedido
    SELECT status INTO v_status_pedido
    FROM pedidos
    WHERE id_pedido = p_id_pedido;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Pedido com ID % não encontrado.', p_id_pedido;
    END IF;

    IF v_status_pedido <> 'ABERTO' THEN
        RAISE EXCEPTION 'Itens só podem ser inseridos em pedidos com status ABERTO. Status atual: %', v_status_pedido;
    END IF;

    -- Validação e busca do produto
    SELECT preco, estoque
    INTO v_preco, v_estoque
    FROM produtos
    WHERE id_produto = p_id_produto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto com ID % não encontrado.', p_id_produto;
    END IF;

    IF v_estoque < p_quantidade THEN
        RAISE EXCEPTION 'Estoque insuficiente para o produto %. Disponível: %, Solicitado: %',
            p_id_produto, v_estoque, p_quantidade;
    END IF;

    -- Cálculo do subtotal
    v_subtotal := v_preco * p_quantidade;

    -- Inserção na tabela itens_pedido
    INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario, subtotal)
    VALUES (p_id_pedido, p_id_produto, p_quantidade, v_preco, v_subtotal);

    -- Atualização do estoque do produto
    UPDATE produtos
    SET estoque = estoque - p_quantidade
    WHERE id_produto = p_id_produto;

    -- Recalculo do valor total do pedido reutilizando a procedure do Exercício 8
    CALL recalcular_valor_pedido(p_id_pedido);
END;
$$;

-- ============================================================================
-- Exercício 10
-- Crie uma procedure chamada remover_cliente_sem_pedidos que receba:
--   • p_id_cliente
-- E exclua o cliente somente se ele não possuir pedidos cadastrados.
-- ============================================================================
CREATE OR REPLACE PROCEDURE remover_cliente_sem_pedidos(
    p_id_cliente INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_pedidos INT;
BEGIN
    SELECT COUNT(*)
    INTO v_total_pedidos
    FROM pedidos
    WHERE id_cliente = p_id_cliente;

    IF v_total_pedidos > 0 THEN
        RAISE NOTICE 'Cliente % possui % pedido(s) cadastrado(s) e não pode ser removido.', p_id_cliente, v_total_pedidos;
    ELSE
        DELETE FROM clientes
        WHERE id_cliente = p_id_cliente;

        IF NOT FOUND THEN
            RAISE NOTICE 'Cliente com ID % não encontrado.', p_id_cliente;
        ELSE
            RAISE NOTICE 'Cliente % removido com sucesso.', p_id_cliente;
        END IF;
    END IF;
END;
$$;

-- ============================================================================
-- BATERIA DE TESTES DAS PROCEDURES
-- ============================================================================
-- Teste Ex 1: Aumentar 10% do salário do funcionário 2 (Fernanda Costa)
CALL aumentar_salario_funcionario(2, 10.00);

-- Teste Ex 2: Atualizar estoque do produto 3 para 25
CALL atualizar_estoque_produto(3, 25);

-- Teste Ex 3: Adicionar R$ 200 ao saldo do cliente 1
CALL adicionar_saldo_cliente(1, 200.00);

-- Teste Ex 4: Descontar R$ 100 do saldo do cliente 1
CALL descontar_saldo_cliente(1, 100.00);

-- Teste Ex 5: Cadastrar novo produto
CALL cadastrar_produto('Teclado Sem Fio', 'Informática', 190.00, 15);

-- Teste Ex 6: Cancelar pedido 4
CALL cancelar_pedido(4);

-- Teste Ex 7: Fechar pedido 2 (status ABERTO)
CALL fechar_pedido(2);

-- Teste Ex 8: Recalcular pedido 1
CALL recalcular_valor_pedido(1);

-- Teste Ex 9: Inserir item no pedido 5 (status ABERTO)
CALL inserir_item_pedido(5, 2, 3);

-- Teste Ex 10: Tentar remover cliente sem pedidos (inserir cliente temporário e remover)
INSERT INTO clientes (nome, email, cidade, saldo) VALUES ('Cliente Temporario', 'temp@teste.com', 'Jales', 0.00);
CALL remover_cliente_sem_pedidos((SELECT id_cliente FROM clientes WHERE email = 'temp@teste.com'));
