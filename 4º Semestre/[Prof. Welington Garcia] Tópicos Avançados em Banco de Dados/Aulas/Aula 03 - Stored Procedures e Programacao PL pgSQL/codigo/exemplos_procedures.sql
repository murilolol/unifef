/*
 * Disciplina: Tópicos Avançados em Banco de Dados
 * Aula: Stored Procedures e Programação PL/pgSQL
 * Professor: Prof. Welington Garcia
 *
 * Como executar:
 * 1. Abra o psql ou uma ferramenta de administração como o pgAdmin ou DBeaver.
 * 2. Conecte-se ao seu banco de dados PostgreSQL.
 * 3. Execute o script abaixo na íntegra para criar a estrutura, procedimentos e testar as execuções.
 */

-- Ativação da linguagem PL/pgSQL (disponível por padrão no PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "plpgsql";

-- ============================================================================
-- PREPARAÇÃO DO AMBIENTE (Tabelas de Exemplo)
-- ============================================================================

DROP TABLE IF EXISTS log_operacoes CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;

CREATE TABLE IF NOT EXISTS produtos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    quantidade INT NOT NULL DEFAULT 0,
    preco NUMERIC(10, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS log_operacoes (
    id SERIAL PRIMARY KEY,
    descricao TEXT NOT NULL,
    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserção de dados iniciais para testes
INSERT INTO produtos (nome, quantidade, preco)
VALUES 
    ('Notebook', 10, 3500.00),
    ('Mouse Óptico', 50, 80.00);

-- ============================================================================
-- EXEMPLO 1: Stored Procedure Simples (Sem Parâmetros)
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_limpar_logs_antigos()
LANGUAGE plpgsql
AS $$
BEGIN
    -- Remove logs criados há mais de 30 dias
    DELETE FROM log_operacoes 
    WHERE data_operacao < CURRENT_TIMESTAMP - INTERVAL '30 days';
    
    RAISE NOTICE 'Logs antigos foram limpos com sucesso.';
END;
$$;

-- ============================================================================
-- EXEMPLO 2: Stored Procedure com Parâmetros de Entrada (IN) e Lógica de Negócio
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_atualizar_estoque_produto(
    p_produto_id INT,
    p_quantidade_adicionada INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_estoque_atual INT;
    v_nome_produto VARCHAR(100);
BEGIN
    -- Verifica se o produto existe e obtém os dados atuais
    SELECT quantidade, nome INTO v_estoque_atual, v_nome_produto
    FROM produtos
    WHERE id = p_produto_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Erro: Produto com ID % não foi encontrado.', p_produto_id;
    END IF;

    -- Atualiza a quantidade do produto
    UPDATE produtos
    SET quantidade = quantidade + p_quantidade_adicionada
    WHERE id = p_produto_id;

    -- Registra no log de operações
    INSERT INTO log_operacoes (descricao)
    VALUES (FORMAT('Estoque do produto %s (ID: %s) alterado de %s para %s.', 
                   v_nome_produto, p_produto_id, v_estoque_atual, v_estoque_atual + p_quantidade_adicionada));

    RAISE NOTICE 'Estoque atualizado com sucesso para o produto %.', v_nome_produto;
END;
$$;

-- ============================================================================
-- DEMONSTRAÇÃO DE EXECUÇÃO
-- ============================================================================

-- Chamada da procedure de limpeza
CALL sp_limpar_logs_antigos();

-- Consulta estoque inicial
SELECT * FROM produtos;

-- Chamada da procedure para adicionar 15 unidades ao produto ID 1
CALL sp_atualizar_estoque_produto(1, 15);

-- Verificação dos resultados nas tabelas
SELECT * FROM produtos WHERE id = 1;
SELECT * FROM log_operacoes;
