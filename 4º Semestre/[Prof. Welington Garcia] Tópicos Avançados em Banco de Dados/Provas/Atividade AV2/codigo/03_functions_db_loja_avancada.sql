-- ===============================================================
-- DISCIPLINA : Tópicos Avançados em Banco de Dados (4º Semestre)
-- INSTITUIÇÃO: UniFEF - Centro Universitário de Fernandópolis
-- PROFESSOR  : Prof. Welington Garcia
-- PARTE B    : 10 Functions em PL/pgSQL (Exercícios B1 a B10)
-- BASE       : db_loja_avancada (PostgreSQL 14+)
-- COMO EXECUTAR:
--   1. Execute previamente o script de schema (01_db_loja_avancada_schema.sql).
--   2. Execute este script para compilar as 10 funções e rodar a bateria de testes.
-- ===============================================================

-- ---------------------------------------------------------------
-- Exercício B1: fn_subtotal_pedido
-- Retorna a soma de (quantidade * preco_unitario) dos itens do pedido.
-- Se o pedido existir mas não tiver itens, retorna 0.00. Sinaliza erro se o ID for inexistente.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_subtotal_pedido(p_id_pedido INTEGER)
RETURNS NUMERIC(12,2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_existe BOOLEAN;
    v_subtotal NUMERIC(12,2);
BEGIN
    SELECT EXISTS(SELECT 1 FROM pedidos WHERE id_pedido = p_id_pedido) INTO v_existe;

    IF NOT v_existe THEN
        RAISE EXCEPTION 'Pedido ID % não encontrado.', p_id_pedido USING ERRCODE = 'P0002';
    END IF;

    SELECT COALESCE(SUM(quantidade * preco_unitario), 0.00)
    INTO v_subtotal
    FROM itens_pedido
    WHERE id_pedido = p_id_pedido;

    RETURN v_subtotal;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B2: fn_total_pedido
-- Retorna o total final do pedido (subtotal - desconto + frete).
-- Reutiliza obrigatoriamente a função fn_subtotal_pedido.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_total_pedido(p_id_pedido INTEGER)
RETURNS NUMERIC(12,2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_subtotal NUMERIC(12,2);
    v_desconto NUMERIC(12,2);
    v_frete NUMERIC(12,2);
BEGIN
    -- Reutilização da função B1 (já valida a existência do pedido)
    v_subtotal := fn_subtotal_pedido(p_id_pedido);

    SELECT desconto, frete INTO v_desconto, v_frete
    FROM pedidos
    WHERE id_pedido = p_id_pedido;

    RETURN ROUND(v_subtotal - v_desconto + v_frete, 2);
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B3: fn_estoque_critico
-- Retorna TRUE se estoque <= estoque_minimo; FALSE caso contrário.
-- Lança exceção amigável para produto inexistente.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_estoque_critico(p_id_produto INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_estoque INTEGER;
    v_minimo INTEGER;
BEGIN
    SELECT estoque, estoque_minimo INTO v_estoque, v_minimo
    FROM produtos
    WHERE id_produto = p_id_produto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', p_id_produto USING ERRCODE = 'P0002';
    END IF;

    RETURN (v_estoque <= v_minimo);
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B4: fn_lucro_unitario
-- Retorna a diferença absoluta entre o preço de venda e o custo do produto.
-- Trata produto inexistente.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_lucro_unitario(p_id_produto INTEGER)
RETURNS NUMERIC(12,2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_preco NUMERIC(12,2);
    v_custo NUMERIC(12,2);
BEGIN
    SELECT preco, custo INTO v_preco, v_custo
    FROM produtos
    WHERE id_produto = p_id_produto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', p_id_produto USING ERRCODE = 'P0002';
    END IF;

    RETURN ROUND(v_preco - v_custo, 2);
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B5: fn_margem_lucro
-- Retorna a margem de lucro percentual sobre a venda: ((preco - custo) / preco) * 100.
-- Arredonda para duas casas decimais e trata produto inexistente.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_margem_lucro(p_id_produto INTEGER)
RETURNS NUMERIC(5,2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_preco NUMERIC(12,2);
    v_custo NUMERIC(12,2);
BEGIN
    SELECT preco, custo INTO v_preco, v_custo
    FROM produtos
    WHERE id_produto = p_id_produto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', p_id_produto USING ERRCODE = 'P0002';
    END IF;

    IF v_preco <= 0 THEN
        RAISE EXCEPTION 'Preço de venda inválido para cálculo de margem (R$ %).', v_preco USING ERRCODE = '22012';
    END IF;

    RETURN ROUND(((v_preco - v_custo) / v_preco) * 100.0, 2);
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B6: fn_total_gasto_cliente
-- Retorna o somatório dos totais de pedidos concluídos ('PAGO' ou 'ENTREGUE') do cliente.
-- Valida a existência do cliente e retorna 0.00 caso não possua compras finalizadas.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_total_gasto_cliente(p_id_cliente INTEGER)
RETURNS NUMERIC(12,2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_existe BOOLEAN;
    v_total NUMERIC(12,2);
BEGIN
    SELECT EXISTS(SELECT 1 FROM clientes WHERE id_cliente = p_id_cliente) INTO v_existe;

    IF NOT v_existe THEN
        RAISE EXCEPTION 'Cliente ID % não encontrado.', p_id_cliente USING ERRCODE = 'P0002';
    END IF;

    SELECT COALESCE(SUM(fn_total_pedido(id_pedido)), 0.00)
    INTO v_total
    FROM pedidos
    WHERE id_cliente = p_id_cliente
      AND status IN ('PAGO', 'ENTREGUE');

    RETURN v_total;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B7: fn_classificar_cliente
-- Classifica clientes pelo histórico financeiro reutilizando fn_total_gasto_cliente:
-- < R$ 500: BRONZE | R$ 500 a < R$ 2000: PRATA | >= R$ 2000: OURO
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_classificar_cliente(p_id_cliente INTEGER)
RETURNS VARCHAR(10)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_gasto NUMERIC(12,2);
BEGIN
    v_gasto := fn_total_gasto_cliente(p_id_cliente);

    IF v_gasto < 500.00 THEN
        RETURN 'BRONZE';
    ELSIF v_gasto < 2000.00 THEN
        RETURN 'PRATA';
    ELSE
        RETURN 'OURO';
    END IF;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B8: fn_quantidade_vendida
-- Retorna o total de unidades vendidas de um produto em pedidos PAGO ou ENTREGUE.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_quantidade_vendida(p_id_produto INTEGER)
RETURNS BIGINT
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_existe BOOLEAN;
    v_unidades BIGINT;
BEGIN
    SELECT EXISTS(SELECT 1 FROM produtos WHERE id_produto = p_id_produto) INTO v_existe;

    IF NOT v_existe THEN
        RAISE EXCEPTION 'Produto ID % não encontrado.', p_id_produto USING ERRCODE = 'P0002';
    END IF;

    SELECT COALESCE(SUM(ip.quantidade), 0)
    INTO v_unidades
    FROM itens_pedido ip
    JOIN pedidos p ON ip.id_pedido = p.id_pedido
    WHERE ip.id_produto = p_id_produto
      AND p.status IN ('PAGO', 'ENTREGUE');

    RETURN v_unidades;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B9: fn_resumo_pedido (Função de Tabela)
-- Retorna uma tabela com os itens do pedido ordenados pelo ID do produto.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_resumo_pedido(p_id_pedido INTEGER)
RETURNS TABLE(
    id_produto INTEGER,
    produto VARCHAR,
    quantidade INTEGER,
    subtotal NUMERIC(12,2)
)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_existe BOOLEAN;
BEGIN
    SELECT EXISTS(SELECT 1 FROM pedidos WHERE id_pedido = p_id_pedido) INTO v_existe;

    IF NOT v_existe THEN
        RAISE EXCEPTION 'Pedido ID % não encontrado.', p_id_pedido USING ERRCODE = 'P0002';
    END IF;

    RETURN QUERY
    SELECT 
        ip.id_produto,
        pr.nome::VARCHAR AS produto,
        ip.quantidade,
        ROUND(ip.quantidade * ip.preco_unitario, 2) AS subtotal
    FROM itens_pedido ip
    JOIN produtos pr ON ip.id_produto = pr.id_produto
    WHERE ip.id_pedido = p_id_pedido
    ORDER BY ip.id_produto ASC;
END;
$$;

-- ---------------------------------------------------------------
-- Exercício B10: fn_ranking_produtos (Função de Tabela)
-- Retorna ranking ordenado por receita decrescente com pedidos PAGO ou ENTREGUE.
-- Inclui produtos sem venda com unidades e receita zeradas.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_ranking_produtos(limite INTEGER)
RETURNS TABLE(
    id_produto INTEGER,
    nome VARCHAR,
    unidades BIGINT,
    receita NUMERIC(12,2)
)
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
    IF limite <= 0 THEN
        RAISE EXCEPTION 'O limite de resultados deve ser estritamente positivo: %', limite USING ERRCODE = '22003';
    END IF;

    RETURN QUERY
    SELECT 
        pr.id_produto,
        pr.nome::VARCHAR,
        COALESCE(SUM(CASE WHEN p.status IN ('PAGO', 'ENTREGUE') THEN ip.quantidade ELSE 0 END), 0)::BIGINT AS unidades,
        COALESCE(SUM(CASE WHEN p.status IN ('PAGO', 'ENTREGUE') THEN ip.quantidade * ip.preco_unitario ELSE 0 END), 0.00)::NUMERIC(12,2) AS receita
    FROM produtos pr
    LEFT JOIN itens_pedido ip ON pr.id_produto = ip.id_produto
    LEFT JOIN pedidos p ON ip.id_pedido = p.id_pedido
    GROUP BY pr.id_produto, pr.nome
    ORDER BY receita DESC, unidades DESC, pr.id_produto ASC
    LIMIT limite;
END;
$$;

-- ===============================================================
-- BATERIA DE TESTES DAS FUNCTIONS
-- ===============================================================

SELECT '=== TESTES B1 e B2: Subtotal e Total de Pedidos ===' AS secao;
SELECT 
    p.id_pedido,
    p.status,
    fn_subtotal_pedido(p.id_pedido) AS subtotal_calculado,
    p.desconto,
    p.frete,
    fn_total_pedido(p.id_pedido) AS total_calculado
FROM pedidos p
ORDER BY p.id_pedido LIMIT 5;

SELECT '=== TESTES B3, B4 e B5: Analise de Produtos ===' AS secao;
SELECT 
    id_produto,
    nome,
    preco,
    custo,
    estoque,
    estoque_minimo,
    fn_estoque_critico(id_produto) AS em_alerta,
    fn_lucro_unitario(id_produto) AS lucro_unit,
    fn_margem_lucro(id_produto) AS margem_pct
FROM produtos
WHERE id_produto IN (1, 2, 8, 12);

SELECT '=== TESTES B6 e B7: Historico e Classificacao de Clientes ===' AS secao;
SELECT 
    id_cliente,
    nome,
    fn_total_gasto_cliente(id_cliente) AS total_gasto,
    fn_classificar_cliente(id_cliente) AS categoria_fidelidade
FROM clientes
WHERE id_cliente IN (1, 2, 3, 5, 7)
ORDER BY total_gasto DESC;

SELECT '=== TESTE B8: Unidades Vendidas por Produto ===' AS secao;
SELECT 
    id_produto,
    nome,
    fn_quantidade_vendida(id_produto) AS total_unidades_faturadas
FROM produtos
WHERE id_produto IN (1, 2, 3, 4, 12);

SELECT '=== TESTE B9: Resumo de Itens do Pedido 1 ===' AS secao;
SELECT * FROM fn_resumo_pedido(1);

SELECT '=== TESTE B10: Top 5 Produtos no Ranking de Vendas ===' AS secao;
SELECT * FROM fn_ranking_produtos(5);
