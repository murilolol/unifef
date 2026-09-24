-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- PROFESSOR: Prof. Guilherme de Morais
-- TEMA: Aula 05 - Estrutura de Dados e Carga Inicial (Clientes e Veículos)
-- COMO EXECUTAR: psql -U postgres -d seu_banco -f schema.sql
-- ============================================================================

-- Limpeza idempotente das tabelas existentes
DROP TABLE IF EXISTS veiculos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- Criação da tabela clientes
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

-- Criação da tabela veiculos
-- NOTA TÉCNICA: No modelo original a coluna motor foi definida como integer,
-- porém os valores inseridos utilizam decimais (ex: 1.8, 2.0). Definido como NUMERIC(3,1)
-- para manter integridade sintática e compatibilidade com PostgreSQL e SGBDs padrão.
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

-- Relacionamento e chave estrangeira
ALTER TABLE veiculos 
    ADD CONSTRAINT fk_cpf_cli FOREIGN KEY (cpf_cli) REFERENCES clientes (cpf);

-- Carga de dados: clientes
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (1, 11, 'BEATRIZ', 'RUA 01', 'FERNANDOPOLIS', 'SP', 1800, 32);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (2, 12, 'JOANA', 'RUA 031', 'SAO JOSE DO RIO PRETO', 'SP', 2000, 40);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (3, 13, 'LUANA', 'RUA 051', 'VOTUPORANGA', 'SP', 3500, 41);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (4, 114, 'KATIA', 'RUA 015', 'JALES', 'SP', 7000, 32);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (5, 115, 'AMANDA', 'RUA 061', 'FERNANDOPOLIS', 'SP', 5500, 40);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (6, 116, 'GIOVANA', 'RUA 021', 'SAO JOSE DO RIO PRETO', 'SP', 2000, 34);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (7, 117, 'TAIZ', 'RUA 011', 'MACEDONIA', 'SP', 1500, 32);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (8, 181, 'MILENA', 'RUA 013', 'SANTA FE DO SUL', 'SP', 2300, 42);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (9, 911, 'ELIZANGELA', 'RUA 015', 'CATANDUVA', 'SP', 2800, 62);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (10, 101, 'PATRACIA', 'RUA 012', 'ARAGUAINA', 'TO', 8800, 22);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (11, 131, 'MARIA', 'RUA 014', 'PALMAS', 'TO', 9000, 23);
INSERT INTO clientes(cod_cli, cpf, nome, endereco, cidade, estado, salario, idade) VALUES (12, 141, 'FELISBINA', 'RUA 015', 'DOURADO', 'MS', 3100, 32);

-- Carga de dados: veiculos
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM02', 'ESC2033', 'BRANCO', 'HILUX', 'TOYOTA', 2018, 160000, 175000, 1.8, 11);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('FI02', 'WDR4528', 'PRETO', 'COROLLA', 'TOYOTA', 2018, 90000, 95000, 2.0, 141);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('RD02', 'GHY5823', 'AZUL', 'HILUX', 'TOYOTA', 2020, 170000, 175000, 2.8, 131);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('DF024', 'TON5689', 'VERMELHO', 'COROLLA', 'TOYOTA', 2015, 70000, 75000, 2.0, 101);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('FG0452', 'ERG8965', 'BRANCO', 'GOL', 'VW', 2018, 40000, 45000, 1.6, 911);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GT02', 'EDV5214', 'BRANCO', 'SAVEIRO', 'VW', 2014, 35000, 40000, 1.6, 181);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM0322', 'TGB2589', 'PRETO', 'UNO', 'FIAT', 2010, 20000, 25000, 1.0, 181);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM0452', 'FGB8521', 'PRETO', 'S10', 'CHEVROLET', 2018, 150000, 175000, 2.8, 117);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM02245', 'RFV5235', 'PRATA', 'COROLLA', 'TOYOTA', 2021, 130000, 135000, 2.0, 116);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM07892', 'PLK5689', 'PRATA', 'ONIX', 'CHEVROLET', 2018, 145000, 155000, 2.8, 115);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM05212', 'UJM5874', 'AZUL', 'KA', 'FORD', 2018, 40000, 45000, 1.0, 114);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM02452', 'VCX1458', 'VERMELHO', 'TRACKER', 'CHEVROLET', 2017, 90000, 95000, 1.4, 13);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM02785', 'EDC8526', 'BRANCO', 'ONIX', 'CHEVROLET', 2018, 45000, 50000, 1.4, 12);
INSERT INTO veiculos(chassi, placa, cor, modelo, marca, ano_fabricacao, preco_compra, preco_venda, motor, cpf_cli) VALUES ('GM02752', 'EDCV4569', 'PRETO', 'HILUX', 'TOYOTA', 2018, 165000, 175000, 2.8, 11);
