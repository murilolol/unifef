-- ============================================================================
-- Instituição: Centro Universitário de Santa Fé do Sul (UniFEF)
-- Curso: Bacharelado em Sistemas de Informação (3º Semestre)
-- Disciplina: Banco de Dados II
-- Professor: Prof. Guilherme de Morais
-- Tema: Comando IN e SQL Mais Complexas (Junções de Tabelas)
-- Data de Referência: 29/04/2026
-- ============================================================================
-- Como executar:
-- 1. Abra um terminal ou cliente SQL conectado ao seu SGBD (PostgreSQL, MySQL,
--    MariaDB, Oracle ou SQLite).
-- 2. Execute este script do início ao fim para recriar as tabelas e povoar a base.
-- 3. Execute cada bloco de consulta SELECT para analisar os resultados práticos.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. ESTRUTURA DDL: LIMPEZA E CRIAÇÃO DAS TABELAS
-- ----------------------------------------------------------------------------

-- Remoção prévia das tabelas respeitando a integridade referencial (filha -> pai)
DROP TABLE IF EXISTS veiculos;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS vendedor;
DROP TABLE IF EXISTS produto;

-- Tabela de Clientes
CREATE TABLE clientes (
    cod_cli INTEGER NOT NULL,
    cpf INTEGER NOT NULL,
    nome VARCHAR(50),
    endereco VARCHAR(50),
    cidade VARCHAR(50),
    estado VARCHAR(02),
    salario INTEGER,
    idade INTEGER,
    CONSTRAINT PK_CPFcliente PRIMARY KEY (cpf)
);

-- Tabela de Veículos
-- Observação técnica: a coluna 'motor' foi definida como NUMERIC(3,1) para
-- acomodar com precisão os valores decimais dos motores (ex: 1.0, 1.4, 1.8, 2.0, 2.8).
CREATE TABLE veiculos (
    chassi VARCHAR(50) NOT NULL,
    placa VARCHAR(10) NOT NULL,
    cor VARCHAR(20),
    modelo VARCHAR(20),
    marca VARCHAR(20),
    ano_fabricacao INTEGER,
    preco_compra INTEGER,
    preco_venda INTEGER,
    motor NUMERIC(3,1),
    cpf_cli INTEGER NOT NULL,
    CONSTRAINT pk_placa PRIMARY KEY (placa)
);

-- Definição de Chave Estrangeira (Relacionamento 1:N entre clientes e veiculos)
ALTER TABLE veiculos 
    ADD CONSTRAINT fk_cpf_cli FOREIGN KEY (cpf_cli) REFERENCES clientes (cpf);

-- Tabelas de apoio para reproduzir fielmente os exemplos teóricos dos slides 2 e 3
CREATE TABLE vendedor (
    codigo_vendedor INTEGER PRIMARY KEY,
    nome_vendedor VARCHAR(50) NOT NULL,
    faixa_comissao VARCHAR(5) NOT NULL
);

CREATE TABLE produto (
    codigo_produto INTEGER PRIMARY KEY,
    descricao VARCHAR(50) NOT NULL,
    unidade VARCHAR(5) NOT NULL,
    val_unit NUMERIC(10,2) NOT NULL
);

-- ----------------------------------------------------------------------------
-- 2. CARGA DE DADOS (DML) FORNECIDA PELO PROFESSOR
-- ----------------------------------------------------------------------------

INSERT INTO clientes (cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES
(1, 11, 'BEATRIZ', 'RUA 01', 'FERNANDOPOLIS', 'SP', 1800, 32),
(2, 12, 'JOANA', 'RUA 031', 'SAO JOSE DO RIO PRETO', 'SP', 2000, 40),
(3, 13, 'LUANA', 'RUA 051', 'VOTUPORANGA', 'SP', 3500, 41),
(4, 114, 'KATIA', 'RUA 015', 'JALES', 'SP', 7000, 32),
(5, 115, 'AMANDA', 'RUA 061', 'FERNANDOPOLIS', 'SP', 5500, 40),
(6, 116, 'GIOVANA', 'RUA 021', 'SAO JOSE DO RIO PRETO', 'SP', 2000, 34),
(7, 117, 'TAIZ', 'RUA 011', 'MACEDONIA', 'SP', 1500, 32),
(8, 181, 'MILENA', 'RUA 013', 'SANTA FE DO SUL', 'SP', 2300, 42),
(9, 911, 'ELIZANGELA', 'RUA 015', 'CATANDUVA', 'SP', 2800, 62),
(10, 101, 'PATRACIA', 'RUA 012', 'ARAGUAINA', 'TO', 8800, 22),
(11, 131, 'MARIA', 'RUA 014', 'PALMAS', 'TO', 9000, 23),
(12, 141, 'FELISBINA', 'RUA 015', 'DOURADO', 'MS', 3100, 32);

INSERT INTO veiculos (chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES
('GM02', 'ESC2033', 'BRANCO', 'HILUX', 'TOYOTA', 2018, 160000, 175000, 1.8, 11),
('FI02', 'WDR4528', 'PRETO', 'COROLLA', 'TOYOTA', 2018, 90000, 95000, 2.0, 141),
('RD02', 'GHY5823', 'AZUL', 'HILUX', 'TOYOTA', 2020, 170000, 175000, 2.8, 131),
('DF024', 'TON5689', 'VERMELHO', 'COROLLA', 'TOYOTA', 2015, 70000, 75000, 2.0, 101),
('FG0452', 'ERG8965', 'BRANCO', 'GOL', 'VW', 2018, 40000, 45000, 1.6, 911),
('GT02', 'EDV5214', 'BRANCO', 'SAVEIRO', 'VW', 2014, 35000, 40000, 1.6, 181),
('GM0322', 'TGB2589', 'PRETO', 'UNO', 'FIAT', 2010, 20000, 25000, 1.0, 181),
('GM0452', 'FGB8521', 'PRETO', 'S10', 'CHEVROLET', 2018, 150000, 175000, 2.8, 117),
('GM02245', 'RFV5235', 'PRATA', 'COROLLA', 'TOYOTA', 2021, 130000, 135000, 2.0, 116),
('GM07892', 'PLK5689', 'PRATA', 'ONIX', 'CHEVROLET', 2018, 145000, 155000, 2.8, 115),
('GM05212', 'UJM5874', 'AZUL', 'KA', 'FORD', 2018, 40000, 45000, 1.0, 114),
('GM02452', 'VCX1458', 'VERMELHO', 'TRACKER', 'CHEVROLET', 2017, 90000, 95000, 1.4, 13),
('GM02785', 'EDC8526', 'BRANCO', 'ONIX', 'CHEVROLET', 2018, 45000, 50000, 1.4, 12),
('GM02752', 'EDCV4569', 'PRETO', 'HILUX', 'TOYOTA', 2018, 165000, 175000, 2.8, 11);

-- Carga de suporte para demonstração completa dos slides 2 e 3
INSERT INTO vendedor (codigo_vendedor, nome_vendedor, faixa_comissao) VALUES
(1, 'CARLOS SILVA', 'A'),
(2, 'MARCOS SOUZA', 'B'),
(3, 'RENATA LIMA', 'C'),
(4, 'PATRICIA DIAS', 'A');

INSERT INTO produto (codigo_produto, descricao, unidade, val_unit) VALUES
(101, 'PARAFUSO ACO', 'M', 0.85),
(102, 'ARRUELA PRESSAO', 'G', 1.00),
(103, 'PORCA ZINCADA', 'L', 1.25),
(104, 'CABO ACO 5MM', 'M', 1.05),
(105, 'TINTA ACRILICA', 'L', 15.50);

-- ----------------------------------------------------------------------------
-- 3. EXEMPLOS APRESENTADOS EM AULA
-- ----------------------------------------------------------------------------

-- Exemplo Slide 2: Operador IN (pesquisa em conjunto de valores discretos)
-- Equivalente a: WHERE estado = 'SP' OR estado = 'MG'
SELECT nome, estado 
FROM clientes 
WHERE estado IN ('SP', 'MG');

-- Exemplo Slide 2: Operador NOT IN (negação de conjunto de valores)
-- Equivalente a: WHERE estado <> 'SP' AND estado <> 'MG'
SELECT nome, estado 
FROM clientes 
WHERE estado NOT IN ('SP', 'MG');

-- Exemplo Slide 3: Vendedores com faixa de comissão 'A' ou 'B'
SELECT nome_vendedor, faixa_comissao 
FROM vendedor 
WHERE faixa_comissao IN ('A', 'B');

-- Exemplo Slide 3: Produtos com unidade 'M', 'G' ou 'L' e valor unitário <= 1.05
SELECT codigo_produto, unidade, descricao, val_unit 
FROM produto 
WHERE unidade IN ('M', 'G', 'L') 
  AND val_unit <= 1.05;

-- Exemplo Slide 7: Junção entre Clientes e Veículos via WHERE
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca 
FROM clientes, veiculos 
WHERE (clientes.cpf = veiculos.cpf_cli);

-- Exemplo Slide 8: Junção com Ordenação por Cidade
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca 
FROM clientes, veiculos 
WHERE (clientes.cpf = veiculos.cpf_cli) 
ORDER BY clientes.cidade;

-- Exemplo Slide 10: Junção com Filtro Adicional (Clientes do Estado de SP)
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca 
FROM clientes, veiculos 
WHERE (clientes.cpf = veiculos.cpf_cli) 
  AND (clientes.estado = 'SP');

-- Exemplo Slide 11: Junção com Filtro Adicional e Ordenação
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca 
FROM clientes, veiculos 
WHERE (clientes.cpf = veiculos.cpf_cli) 
  AND (clientes.estado = 'SP') 
ORDER BY clientes.cidade;
