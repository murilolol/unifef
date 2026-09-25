/*
 * Disciplina: Laboratório de Programação IV (4º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Esquema Relacional e Migrações de Banco de Dados (PostgreSQL / Liquibase)
 * Como executar no psql / DBeaver:
 *   psql -U suporteos_app -d suporteos2026_dev -f exemplos_migracao.sql
 */

-- 1. Criação da Tabela de Grupo de Produtos (Equivalente ao changeSet 001)
CREATE TABLE IF NOT EXISTS grupo_produto (
    id BIGSERIAL CONSTRAINT pk_grupo_produto PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT uk_grupo_produto_nome UNIQUE (nome),
    CONSTRAINT chk_grupo_produto_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

-- 2. Criação da Tabela Fornecedor (Equivalente à evolução da Aula 06)
CREATE TABLE IF NOT EXISTS fornecedor (
    id BIGSERIAL CONSTRAINT pk_fornecedor PRIMARY KEY,
    razao_social VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT uk_fornecedor_cnpj UNIQUE (cnpj),
    CONSTRAINT chk_fornecedor_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

-- 3. Criação da Tabela de Produtos com Restrições Fortes de Integridade (Equivalente ao changeSet 002)
CREATE TABLE IF NOT EXISTS produto (
    id BIGSERIAL CONSTRAINT pk_produto PRIMARY KEY,
    codigo_barras VARCHAR(50) NOT NULL,
    descricao VARCHAR(150) NOT NULL,
    saldo_estoque NUMERIC(18, 3) NOT NULL,
    valor_unitario NUMERIC(18, 2) NOT NULL,
    data_cadastro DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    grupo_id BIGINT NOT NULL,
    CONSTRAINT uk_produto_codigo_barras UNIQUE (codigo_barras),
    CONSTRAINT chk_produto_saldo_nao_negativo CHECK (saldo_estoque >= 0),
    CONSTRAINT chk_produto_valor_unitario_nao_negativo CHECK (valor_unitario >= 0),
    CONSTRAINT chk_produto_status CHECK (status IN ('ATIVO', 'INATIVO')),
    CONSTRAINT fk_produto_grupo_produto FOREIGN KEY (grupo_id)
        REFERENCES grupo_produto (id) ON DELETE RESTRICT
);

-- 4. Exemplo prático de migração segura 'Expand-Migrate-Contract' (Aula 06: estoque_minimo e fornecedor)
-- Passo A: EXPAND (Adicionar coluna permitindo nulo temporariamente)
ALTER TABLE produto ADD COLUMN IF NOT EXISTS estoque_minimo NUMERIC(18, 3);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS fornecedor_id BIGINT;

-- Passo B: MIGRATE (Backfill de dados em linhas antigas existentes)
UPDATE produto SET estoque_minimo = 0.000 WHERE estoque_minimo IS NULL;

-- Passo C: CONTRACT (Aplicar restrições NOT NULL, CHECK e FOREIGN KEY)
ALTER TABLE produto ALTER COLUMN estoque_minimo SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_produto_estoque_minimo') THEN
        ALTER TABLE produto ADD CONSTRAINT chk_produto_estoque_minimo CHECK (estoque_minimo >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_produto_fornecedor') THEN
        ALTER TABLE produto ADD CONSTRAINT fk_produto_fornecedor FOREIGN KEY (fornecedor_id)
            REFERENCES fornecedor (id) ON DELETE RESTRICT;
    END IF;
END $$;

-- 5. Carga de Dados Inicial Idempotente para Testes
INSERT INTO grupo_produto (id, nome, status)
VALUES (1, 'Informática', 'ATIVO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO grupo_produto (id, nome, status)
VALUES (2, 'Papelaria', 'ATIVO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO produto (codigo_barras, descricao, saldo_estoque, valor_unitario, estoque_minimo, data_cadastro, status, grupo_id)
VALUES ('7890000000001', 'Caderno Universitário 10 Matérias', 50.000, 22.90, 5.000, CURRENT_DATE, 'ATIVO', 2)
ON CONFLICT (codigo_barras) DO NOTHING;

-- Consulta de Verificação
SELECT 
    p.id,
    p.codigo_barras,
    p.descricao,
    p.saldo_estoque,
    p.valor_unitario,
    p.estoque_minimo,
    g.nome AS nome_grupo
FROM produto p
JOIN grupo_produto g ON g.id = p.grupo_id;
