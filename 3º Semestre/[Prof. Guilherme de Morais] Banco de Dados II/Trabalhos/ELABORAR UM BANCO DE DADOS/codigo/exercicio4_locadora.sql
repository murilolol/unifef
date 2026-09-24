-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- INSTITUIÇÃO: Centro Universitário Municipal de Franca (UniFEF)
-- PROFESSOR: Prof. Guilherme de Morais
-- TEMA: Exercício 4 - DER Locadora de Automóveis (Modelo Relacional DDL e DML)
-- EXECUÇÃO: Compatível com PostgreSQL 12+, MySQL 8.0+ e SQLite 3
-- ============================================================================

-- Limpeza de tabelas para idempotência
DROP TABLE IF EXISTS conserto CASCADE;
DROP TABLE IF EXISTS locacao CASCADE;
DROP TABLE IF EXISTS carro CASCADE;
DROP TABLE IF EXISTS categoria CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;

-- ----------------------------------------------------------------------------
-- 1. TABELA: cliente
-- Dados cadastrais dos condutores. CNH definida como chave primária natural.
-- Restrição CHECK garante idade mínima legal para locação.
-- ----------------------------------------------------------------------------
CREATE TABLE cliente (
    cnh VARCHAR(20) PRIMARY KEY,
    rg VARCHAR(20) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    idade INT NOT NULL CHECK (idade >= 18)
);

-- ----------------------------------------------------------------------------
-- 2. TABELA: categoria
-- Categorização da frota (ex: Primeira Classe, SUV, Econômico) e valor da diária.
-- ----------------------------------------------------------------------------
CREATE TABLE categoria (
    codigo INT PRIMARY KEY,
    nome_categoria VARCHAR(60) NOT NULL UNIQUE,
    preco_diaria DECIMAL(10,2) NOT NULL CHECK (preco_diaria > 0),
    descricao TEXT NOT NULL
);

-- ----------------------------------------------------------------------------
-- 3. TABELA: carro
-- Veículos que compõem a frota ativa. Cada carro pertence a uma categoria.
-- Chassi e placa possuem restrição de unicidade física.
-- ----------------------------------------------------------------------------
CREATE TABLE carro (
    chassi VARCHAR(30) PRIMARY KEY,
    placa CHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    ano INT NOT NULL CHECK (ano >= 1990),
    cor VARCHAR(30) NOT NULL,
    codigo_categoria INT NOT NULL,
    CONSTRAINT fk_carro_categoria FOREIGN KEY (codigo_categoria)
        REFERENCES categoria(codigo) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 4. TABELA: locacao
-- Histórico de alocações de veículos efetuadas pelos clientes com data e hora.
-- ----------------------------------------------------------------------------
CREATE TABLE locacao (
    id_locacao INT PRIMARY KEY,
    cnh_cliente VARCHAR(20) NOT NULL,
    chassi_carro VARCHAR(30) NOT NULL,
    data_hora_locacao TIMESTAMP NOT NULL,
    data_hora_devolucao TIMESTAMP,
    valor_total DECIMAL(10,2) CHECK (valor_total >= 0),
    CONSTRAINT fk_loc_cliente FOREIGN KEY (cnh_cliente)
        REFERENCES cliente(cnh) ON DELETE RESTRICT,
    CONSTRAINT fk_loc_carro FOREIGN KEY (chassi_carro)
        REFERENCES carro(chassi) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 5. TABELA: conserto
-- Histórico de manutenções e reparos realizados por carro em oficinas externas.
-- ----------------------------------------------------------------------------
CREATE TABLE conserto (
    id_conserto INT PRIMARY KEY,
    chassi_carro VARCHAR(30) NOT NULL,
    dia DATE NOT NULL,
    valor DECIMAL(10,2) NOT NULL CHECK (valor >= 0),
    descricao_servico TEXT NOT NULL,
    oficina VARCHAR(150) NOT NULL,
    CONSTRAINT fk_conserto_carro FOREIGN KEY (chassi_carro)
        REFERENCES carro(chassi) ON DELETE CASCADE
);

-- ============================================================================
-- CARGA DE DADOS DE TESTE (DML)
-- ============================================================================

-- Clientes habilitados
INSERT INTO cliente (cnh, rg, nome, endereco, idade) VALUES
('00123456789', 'MG-12.345.678', 'Mariana Ferreira Lopes', 'Rua Voluntários da Franca, 1100, Franca-SP', 29),
('00987654321', 'SP-98.765.432', 'Gabriel Souza Bueno', 'Av. Presidente Vargas, 600, Franca-SP', 42);

-- Categorias de veículos com tarifas diárias
INSERT INTO categoria (codigo, nome_categoria, preco_diaria, descricao) VALUES
(1, 'Primeira Classe', 450.00, 'Sedans de luxo blindados, câmbio automático e bancos de couro'),
(2, 'SUV Premium', 320.00, 'Tração 4x4, amplo porta-malas e central multimídia avançada'),
(3, 'Econômico com Ar', 140.00, 'Hatch compacto 1.0 flex com direção elétrica e ar-condicionado');

-- Carros da frota
INSERT INTO carro (chassi, placa, marca, modelo, ano, cor, codigo_categoria) VALUES
('9BWZZZ377VT001001', 'BRA2E19', 'Audi', 'A4 2.0 TFSI', 2024, 'Preto Ninja', 1),
('9BDZZZ377VT002002', 'FRN4H88', 'Jeep', 'Compass Longitude', 2023, 'Prata Metálico', 2),
('9BFZZZ377VT003003', 'UNI3A26', 'Chevrolet', 'Onix Premier', 2024, 'Branco Polar', 3);

-- Locações realizadas
INSERT INTO locacao (id_locacao, cnh_cliente, chassi_carro, data_hora_locacao, data_hora_devolucao, valor_total) VALUES
(1001, '00123456789', '9BWZZZ377VT001001', '2026-02-10 08:30:00', '2026-02-13 18:00:00', 1800.00),
(1002, '00987654321', '9BDZZZ377VT002002', '2026-02-11 14:00:00', NULL, NULL);

-- Consertos e manutenções
INSERT INTO conserto (id_conserto, chassi_carro, dia, valor, descricao_servico, oficina) VALUES
(1, '9BWZZZ377VT001001', '2026-01-15', 680.00, 'Substituição das pastilhas de freio dianteiras e alinhamento', 'Centro Automotivo Francano Ltda'),
(2, '9BDZZZ377VT002002', '2026-01-20', 350.00, 'Troca de óleo sintético 5W30 e filtro de cabine', 'Oficina Mecânica São José');

-- Consulta de validação: Extrato de frotas ativas, categoria e histórico de locações
SELECT 
    c.placa,
    c.modelo,
    cat.nome_categoria,
    cat.preco_diaria,
    cli.nome AS cliente_locatario,
    l.data_hora_locacao,
    l.data_hora_devolucao,
    l.valor_total
FROM carro c
JOIN categoria cat ON c.codigo_categoria = cat.codigo
LEFT JOIN locacao l ON c.chassi = l.chassi_carro
LEFT JOIN cliente cli ON l.cnh_cliente = cli.cnh
ORDER BY c.modelo;
