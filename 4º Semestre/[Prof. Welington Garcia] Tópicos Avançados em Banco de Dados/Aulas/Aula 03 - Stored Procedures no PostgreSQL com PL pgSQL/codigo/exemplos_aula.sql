-- Disciplina: Tópicos Avançados em Banco de Dados
-- Professor: Prof. Welington Garcia
-- Tema: Stored Procedures no PostgreSQL com PL/pgSQL
-- Descrição: Exemplos práticos de sintaxe e estruturas de controle em PL/pgSQL.

-- Exemplo 1: Bloco anônimo simples demonstrando variáveis e condicionais
DO $$
DECLARE
    v_limite NUMERIC(10,2) := 1000.00;
    v_saldo NUMERIC(10,2) := 1250.50;
    v_status VARCHAR(50);
BEGIN
    IF v_saldo > v_limite THEN
        v_status := 'Saldo acima do limite permitido';
    ELSE
        v_status := 'Saldo dentro do limite';
    END IF;
    
    RAISE NOTICE 'Resultado da análise: %', v_status;
END;
$$;

-- Exemplo 2: Procedure simples para inserção de categoria e registro de log
CREATE OR REPLACE PROCEDURE pr_inserir_categoria(
    p_nome VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Insere o registro
    INSERT INTO categorias (nome) VALUES (p_nome);
    
    -- Registra o log da operação
    INSERT INTO logs_sistema (acao) 
    VALUES ('Categoria criada com sucesso: ' || p_nome);
    
    RAISE NOTICE 'Categoria % cadastrada com sucesso!', p_nome;
END;
$$;

-- Como executar a procedure de exemplo:
-- CALL pr_inserir_categoria('Informática');
-- SELECT * FROM categorias;
-- SELECT * FROM logs_sistema;
