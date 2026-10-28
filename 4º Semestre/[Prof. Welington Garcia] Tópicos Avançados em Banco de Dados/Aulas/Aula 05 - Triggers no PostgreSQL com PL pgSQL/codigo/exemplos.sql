-- ============================================================================
-- Disciplina : Topicos Avancados em Banco de Dados (4o Semestre)
-- Professor  : Prof. Welington Garcia
-- Tema       : Triggers no PostgreSQL com PL/pgSQL
-- Execucao   : psql -U postgres -d seu_banco -f exemplos.sql
-- ============================================================================

-- Limpeza idempotente de objetos anteriores
DROP TABLE IF EXISTS auditoria_clientes CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP FUNCTION IF EXISTS fn_valida_cliente_before CASCADE;
DROP FUNCTION IF EXISTS fn_audita_cliente_after CASCADE;

-- ----------------------------------------------------------------------------
-- 1. ESTRUTURA DE TABELAS
-- ----------------------------------------------------------------------------
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    limite_credito NUMERIC(10, 2) DEFAULT 0.00,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditoria_clientes (
    id_auditoria SERIAL PRIMARY KEY,
    id_cliente INT,
    operacao VARCHAR(10) NOT NULL,
    dados_antigos JSONB,
    dados_novos JSONB,
    usuario_bd VARCHAR(50) DEFAULT CURRENT_USER,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 2. TRIGGER FUNCTION: VALIDACAO (BEFORE INSERT OR UPDATE)
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_valida_cliente_before()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validacao: o limite de credito nao pode ser negativo
    IF NEW.limite_credito < 0 THEN
        RAISE EXCEPTION 'O limite de credito nao pode ser negativo. Valor informado: %', NEW.limite_credito;
    END IF;

    -- Padronizacao: salvar e-mail em letras minusculas
    NEW.email := LOWER(TRIM(NEW.email));

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_valida_cliente
BEFORE INSERT OR UPDATE ON clientes
FOR EACH ROW
EXECUTE FUNCTION fn_valida_cliente_before();

-- ----------------------------------------------------------------------------
-- 3. TRIGGER FUNCTION: AUDITORIA (AFTER INSERT OR UPDATE OR DELETE)
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_audita_cliente_after()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO auditoria_clientes (id_cliente, operacao, dados_antigos, dados_novos)
        VALUES (NEW.id, TG_OP, NULL, to_jsonb(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO auditoria_clientes (id_cliente, operacao, dados_antigos, dados_novos)
        VALUES (NEW.id, TG_OP, to_jsonb(OLD), to_jsonb(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO auditoria_clientes (id_cliente, operacao, dados_antigos, dados_novos)
        VALUES (OLD.id, TG_OP, to_jsonb(OLD), NULL);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$;

CREATE TRIGGER trg_audita_cliente
AFTER INSERT OR UPDATE OR DELETE ON clientes
FOR EACH ROW
EXECUTE FUNCTION fn_audita_cliente_after();

-- ----------------------------------------------------------------------------
-- 4. TESTES E DEMONSTRACAO DOS GATILHOS
-- ----------------------------------------------------------------------------
-- Teste de INSERT com sucesso (o e-mail sera convertido para minusculas)
INSERT INTO clientes (nome, email, limite_credito) 
VALUES ('Maria Silva', 'MARIA.SILVA@EMAIL.COM', 1500.00);

-- Teste de UPDATE com sucesso
UPDATE clientes 
SET limite_credito = 2200.00 
WHERE id = 1;

-- Consulta dos dados da tabela principal e da auditoria
SELECT * FROM clientes;
SELECT * FROM auditoria_clientes;
