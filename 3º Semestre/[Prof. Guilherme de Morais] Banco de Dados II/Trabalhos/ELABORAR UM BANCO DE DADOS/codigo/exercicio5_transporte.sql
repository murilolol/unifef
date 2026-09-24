-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- INSTITUIÇÃO: Centro Universitário Municipal de Franca (UniFEF)
-- PROFESSOR: Prof. Guilherme de Morais
-- TEMA: Exercício 5 - DER Companhia de Transporte (Modelo Relacional DDL e DML)
-- EXECUÇÃO: Compatível com PostgreSQL 12+, MySQL 8.0+ e SQLite 3
-- ============================================================================

-- Limpeza de tabelas para garantir idempotência
DROP TABLE IF EXISTS remessa CASCADE;
DROP TABLE IF EXISTS viagem CASCADE;
DROP TABLE IF EXISTS caminhao CASCADE;
DROP TABLE IF EXISTS deposito CASCADE;
DROP TABLE IF EXISTS armazem CASCADE;

-- ----------------------------------------------------------------------------
-- 1. TABELA: armazem
-- Representa os 6 armazéns de distribuição de onde partem as remessas.
-- ----------------------------------------------------------------------------
CREATE TABLE armazem (
    numero_armazem INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    localizacao VARCHAR(200) NOT NULL
);

-- ----------------------------------------------------------------------------
-- 2. TABELA: deposito
-- Representa os 45 depósitos de destino vinculados às lojas de varejo.
-- ----------------------------------------------------------------------------
CREATE TABLE deposito (
    numero_deposito INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    localizacao VARCHAR(200) NOT NULL
);

-- ----------------------------------------------------------------------------
-- 3. TABELA: caminhao
-- Frota de 150 caminhões com código de licença, capacidade de peso (kg) e volume (m³).
-- ----------------------------------------------------------------------------
CREATE TABLE caminhao (
    codigo_licenca VARCHAR(20) PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    capacidade_peso DECIMAL(10,2) NOT NULL CHECK (capacidade_peso > 0), -- em kg
    capacidade_volume DECIMAL(10,2) NOT NULL CHECK (capacidade_volume > 0) -- em m³
);

-- ----------------------------------------------------------------------------
-- 4. TABELA: viagem
-- Uma viagem sai de um único armazém de origem, conduzida por um caminhão,
-- com monitoramento consolidado do peso e volume transportados.
-- ----------------------------------------------------------------------------
CREATE TABLE viagem (
    numero_viagem INT PRIMARY KEY,
    codigo_licenca VARCHAR(20) NOT NULL,
    numero_armazem_origem INT NOT NULL,
    data_saida DATE NOT NULL,
    peso_total DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (peso_total >= 0),
    volume_total DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (volume_total >= 0),
    CONSTRAINT fk_viagem_caminhao FOREIGN KEY (codigo_licenca)
        REFERENCES caminhao(codigo_licenca) ON DELETE RESTRICT,
    CONSTRAINT fk_viagem_armazem FOREIGN KEY (numero_armazem_origem)
        REFERENCES armazem(numero_armazem) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 5. TABELA: remessa
-- Remessas carregadas durante uma viagem para entrega em múltiplos depósitos.
-- Contém atributos de peso individual, volume individual e destino específico.
-- ----------------------------------------------------------------------------
CREATE TABLE remessa (
    numero_remessa INT PRIMARY KEY,
    numero_viagem INT NOT NULL,
    numero_deposito_destino INT NOT NULL,
    peso DECIMAL(10,2) NOT NULL CHECK (peso > 0),
    volume DECIMAL(10,2) NOT NULL CHECK (volume > 0),
    descricao_conteudo VARCHAR(200) NOT NULL,
    CONSTRAINT fk_remessa_viagem FOREIGN KEY (numero_viagem)
        REFERENCES viagem(numero_viagem) ON DELETE CASCADE,
    CONSTRAINT fk_remessa_deposito FOREIGN KEY (numero_deposito_destino)
        REFERENCES deposito(numero_deposito) ON DELETE RESTRICT
);

-- ============================================================================
-- CARGA DE DADOS DE TESTE (DML)
-- ============================================================================

-- Armazéns centrais de origem (amostra dos 6 existentes)
INSERT INTO armazem (numero_armazem, nome, localizacao) VALUES
(1, 'Armazém Central Sudeste', 'Rod. Anhanguera, km 310, Ribeirão Preto-SP'),
(2, 'Armazém Metropolitano', 'Marginal Tietê, 4500, São Paulo-SP'),
(3, 'Armazém Sul', 'Av. das Indústrias, 1200, Curitiba-PR');

-- Depósitos regionais de destino (amostra dos 45 existentes)
INSERT INTO deposito (numero_deposito, nome, localizacao) VALUES
(10, 'Depósito Franca Centro', 'Av. Brasil, 800, Franca-SP'),
(11, 'Depósito Franca Estação', 'Rua General Teles, 1400, Franca-SP'),
(12, 'Depósito Batatais', 'Rua Santos Dumont, 250, Batatais-SP'),
(13, 'Depósito Barretos', 'Av. 23, 700, Barretos-SP');

-- Caminhões da frota (capacidade de carga e cubagem)
INSERT INTO caminhao (codigo_licenca, marca, modelo, capacidade_peso, capacidade_volume) VALUES
('LIC-TRK-001', 'Scania', 'R450 6x2', 25000.00, 95.00),
('LIC-TRK-002', 'Volvo', 'FH 540 6x4', 32000.00, 115.00),
('LIC-TRK-003', 'Mercedes-Benz', 'Actros 2651', 27000.00, 100.00);

-- Viagem nº 5001: Sai do Armazém Central Sudeste (1) transportando várias remessas
INSERT INTO viagem (numero_viagem, codigo_licenca, numero_armazem_origem, data_saida, peso_total, volume_total) VALUES
(5001, 'LIC-TRK-001', 1, '2026-02-14', 18500.00, 72.50);

-- Remessas vinculadas à Viagem 5001 distribuídas em múltiplos depósitos de destino
INSERT INTO remessa (numero_remessa, numero_viagem, numero_deposito_destino, peso, volume, descricao_conteudo) VALUES
(901, 5001, 10, 6500.00, 24.00, 'Eletrodomésticos linha branca - Lojas Franca Centro'),
(902, 5001, 11, 4800.00, 18.50, 'Smart TVs e Eletrônicos - Depósito Estação'),
(903, 5001, 12, 7200.00, 30.00, 'Móveis planejados e colchões - Depósito Batatais');

-- Consulta de validação: Romaneio de viagem com múltiplos depósitos e auditoria de capacidade
SELECT 
    v.numero_viagem,
    v.data_saida,
    a.nome AS armazem_origem,
    c.codigo_licenca AS caminhao,
    c.capacidade_peso AS limite_peso_kg,
    c.capacidade_volume AS limite_vol_m3,
    r.numero_remessa,
    r.descricao_conteudo,
    d.nome AS deposito_destino,
    r.peso AS remessa_peso_kg,
    r.volume AS remessa_vol_m3
FROM viagem v
JOIN armazem a ON v.numero_armazem_origem = a.numero_armazem
JOIN caminhao c ON v.codigo_licenca = c.codigo_licenca
JOIN remessa r ON v.numero_viagem = r.numero_viagem
JOIN deposito d ON r.numero_deposito_destino = d.numero_deposito
WHERE v.numero_viagem = 5001
ORDER BY r.numero_remessa;
