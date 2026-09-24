-- ============================================================================
-- DISCIPLINA: Banco de Dados II (3º Semestre)
-- INSTITUIÇÃO: Centro Universitário Municipal de Franca (UniFEF)
-- PROFESSOR: Prof. Guilherme de Morais
-- TEMA: Exercícios 2 e 3 - DER Pet-Shop (Modelo Relacional DDL e DML)
-- EXECUÇÃO: Compatível com PostgreSQL 12+, MySQL 8.0+ e SQLite 3
-- ============================================================================

-- Limpeza de tabelas para garantir idempotência
DROP TABLE IF EXISTS item_venda CASCADE;
DROP TABLE IF EXISTS venda CASCADE;
DROP TABLE IF EXISTS servico_executado CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS fornecedor CASCADE;
DROP TABLE IF EXISTS animal CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS funcionario CASCADE;
DROP TABLE IF EXISTS cargo CASCADE;

-- ----------------------------------------------------------------------------
-- 1. TABELA: cargo
-- Quadro geral de cargos dos funcionários do pet-shop
-- ----------------------------------------------------------------------------
CREATE TABLE cargo (
    id_cargo INT PRIMARY KEY,
    nome_cargo VARCHAR(80) NOT NULL UNIQUE,
    salario_base DECIMAL(10,2) NOT NULL CHECK (salario_base >= 0)
);

-- ----------------------------------------------------------------------------
-- 2. TABELA: funcionario
-- Funcionários registrados obrigatoriamente em apenas um cargo
-- ----------------------------------------------------------------------------
CREATE TABLE funcionario (
    cpf CHAR(11) PRIMARY KEY,
    nome_completo VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    telefone_residencial VARCHAR(20),
    telefone_celular VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    id_cargo INT NOT NULL,
    CONSTRAINT fk_func_cargo FOREIGN KEY (id_cargo)
        REFERENCES cargo(id_cargo) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 3. TABELA: cliente
-- Clientes cadastrados no pet-shop
-- ----------------------------------------------------------------------------
CREATE TABLE cliente (
    cpf CHAR(11) PRIMARY KEY,
    nome_completo VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    telefone_comercial VARCHAR(20),
    telefone_residencial VARCHAR(20),
    telefone_celular VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE
);

-- ----------------------------------------------------------------------------
-- 4. TABELA: animal
-- Animais dos clientes (cada cliente possui pelo menos um animal)
-- ----------------------------------------------------------------------------
CREATE TABLE animal (
    id_animal INT PRIMARY KEY,
    cpf_cliente CHAR(11) NOT NULL,
    nome VARCHAR(80) NOT NULL,
    data_nascimento DATE,
    sexo CHAR(1) NOT NULL CHECK (sexo IN ('M', 'F')),
    raca VARCHAR(60) NOT NULL,
    cor_predominante VARCHAR(40) NOT NULL,
    tipo_animal VARCHAR(40) NOT NULL, -- Ex: Cão, Gato, Ave
    CONSTRAINT fk_animal_cliente FOREIGN KEY (cpf_cliente)
        REFERENCES cliente(cpf) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 5. TABELA: fornecedor
-- Fornecedores do pet-shop (podem fornecer múltiplos produtos)
-- ----------------------------------------------------------------------------
CREATE TABLE fornecedor (
    id_fornecedor INT PRIMARY KEY,
    razao_social VARCHAR(150) NOT NULL,
    cnpj CHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL
);

-- ----------------------------------------------------------------------------
-- 6. TABELA: produto
-- Produtos comercializados. Cada produto possui rigorosamente um fornecedor único
-- ----------------------------------------------------------------------------
CREATE TABLE produto (
    codigo INT PRIMARY KEY,
    produto VARCHAR(120) NOT NULL,
    marca VARCHAR(80) NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL CHECK (valor_unitario >= 0),
    validade DATE NOT NULL,
    id_fornecedor INT NOT NULL,
    CONSTRAINT fk_prod_fornecedor FOREIGN KEY (id_fornecedor)
        REFERENCES fornecedor(id_fornecedor) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 7. TABELA: servico_executado
-- Registro operacional de serviços prestados (envolve funcionário, cliente e animal)
-- ----------------------------------------------------------------------------
CREATE TABLE servico_executado (
    id_servico INT PRIMARY KEY,
    cpf_funcionario CHAR(11) NOT NULL,
    cpf_cliente CHAR(11) NOT NULL,
    id_animal INT NOT NULL,
    tipo_servico VARCHAR(100) NOT NULL, -- Ex: Banho e Tosa, Consulta Veterinária
    data_execucao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor DECIMAL(10,2) NOT NULL CHECK (valor >= 0),
    CONSTRAINT fk_serv_func FOREIGN KEY (cpf_funcionario)
        REFERENCES funcionario(cpf) ON DELETE RESTRICT,
    CONSTRAINT fk_serv_cli FOREIGN KEY (cpf_cliente)
        REFERENCES cliente(cpf) ON DELETE RESTRICT,
    CONSTRAINT fk_serv_anim FOREIGN KEY (id_animal)
        REFERENCES animal(id_animal) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 8. TABELA: venda
-- Cabeçalho da venda no balcão (envolve cliente, funcionário e pagamento)
-- ----------------------------------------------------------------------------
CREATE TABLE venda (
    id_venda INT PRIMARY KEY,
    cpf_cliente CHAR(11) NOT NULL,
    cpf_funcionario CHAR(11) NOT NULL,
    data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(50) NOT NULL CHECK (forma_pagamento IN ('Dinheiro', 'Cartao Credito', 'Cartao Debito', 'PIX')),
    valor_total DECIMAL(10,2) NOT NULL CHECK (valor_total >= 0),
    CONSTRAINT fk_venda_cli FOREIGN KEY (cpf_cliente)
        REFERENCES cliente(cpf) ON DELETE RESTRICT,
    CONSTRAINT fk_venda_func FOREIGN KEY (cpf_funcionario)
        REFERENCES funcionario(cpf) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 9. TABELA: item_venda
-- Linha de itens da venda (relacionamento N:N entre venda e produtos)
-- ----------------------------------------------------------------------------
CREATE TABLE item_venda (
    id_venda INT NOT NULL,
    codigo_produto INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    valor_unitario DECIMAL(10,2) NOT NULL CHECK (valor_unitario >= 0),
    PRIMARY KEY (id_venda, codigo_produto),
    CONSTRAINT fk_iv_venda FOREIGN KEY (id_venda)
        REFERENCES venda(id_venda) ON DELETE CASCADE,
    CONSTRAINT fk_iv_produto FOREIGN KEY (codigo_produto)
        REFERENCES produto(codigo) ON DELETE RESTRICT
);

-- ============================================================================
-- CARGA DE DADOS DE TESTE (DML)
-- ============================================================================

-- Cargos
INSERT INTO cargo (id_cargo, nome_cargo, salario_base) VALUES
(1, 'Veterinário(a)', 5800.00),
(2, 'Tosador / Banhista', 2200.00),
(3, 'Atendente de Loja', 1800.00);

-- Funcionários
INSERT INTO funcionario (cpf, nome_completo, endereco, telefone_residencial, telefone_celular, data_nascimento, id_cargo) VALUES
('10101010101', 'Dra. Camila Toledo', 'Rua São Paulo, 310, Franca-SP', '(16) 3701-1111', '(16) 99701-1111', '1990-04-12', 1),
('20202020202', 'Marcos Vinicius Rezende', 'Rua Bahia, 540, Franca-SP', NULL, '(16) 99802-2222', '1996-08-25', 2),
('30303030303', 'Renata Cristina Dias', 'Av. Orlando Quinteiro, 78, Franca-SP', '(16) 3725-3333', '(16) 99603-3333', '1999-12-05', 3);

-- Clientes
INSERT INTO cliente (cpf, nome_completo, endereco, telefone_comercial, telefone_residencial, telefone_celular, data_nascimento, email) VALUES
('40404040404', 'Juliana Alencar', 'Rua General Carneiro, 990, Franca-SP', '(16) 3711-4444', '(16) 3722-4444', '(16) 99104-4444', '1985-02-18', 'juliana.alencar@email.com'),
('50505050505', 'Rodrigo Peixoto', 'Av. Champagnat, 1500, Franca-SP', NULL, NULL, '(16) 99205-5555', '1992-06-30', 'rodrigo.peixoto@email.com');

-- Animais dos clientes
INSERT INTO animal (id_animal, cpf_cliente, nome, data_nascimento, sexo, raca, cor_predominante, tipo_animal) VALUES
(1, '40404040404', 'Thor', '2021-03-10', 'M', 'Golden Retriever', 'Dourado', 'Cão'),
(2, '40404040404', 'Mimi', '2022-09-01', 'F', 'Siamês', 'Branco e Marrom', 'Gato'),
(3, '50505050505', 'Bob', '2020-11-15', 'M', 'Bulldog Francês', 'Preto', 'Cão');

-- Fornecedores
INSERT INTO fornecedor (id_fornecedor, razao_social, cnpj, telefone, email) VALUES
(1, 'NutriPet Nutrição Animal Ltda', '12345678000199', '(19) 3800-1000', 'pedidos@nutripet.com.br'),
(2, 'VetCare Farmacêutica Pet S/A', '98765432000188', '(11) 4500-2000', 'contato@vetcare.com.br');

-- Produtos
INSERT INTO produto (codigo, produto, marca, valor_unitario, validade, id_fornecedor) VALUES
(1001, 'Ração Premium Adulto 15kg', 'NutriPet Max', 249.90, '2027-01-30', 1),
(1002, 'Antipulgas e Carrapatos 10-20kg', 'VetCare Shield', 89.50, '2027-08-15', 2),
(1003, 'Shampoo Hipoalergênico 500ml', 'NutriPet Care', 42.00, '2026-11-20', 1);

-- Registro de serviços prestados
INSERT INTO servico_executado (id_servico, cpf_funcionario, cpf_cliente, id_animal, tipo_servico, data_execucao, valor) VALUES
(1, '20202020202', '40404040404', 1, 'Banho e Tosa Completa', '2026-02-12 09:30:00', 110.00),
(2, '10101010101', '50505050505', 3, 'Consulta Clínica Geral', '2026-02-12 11:00:00', 150.00);

-- Registro de venda e itens
INSERT INTO venda (id_venda, cpf_cliente, cpf_funcionario, data_venda, forma_pagamento, valor_total) VALUES
(5001, '40404040404', '30303030303', '2026-02-12 10:15:00', 'Cartao Credito', 339.40);

INSERT INTO item_venda (id_venda, codigo_produto, quantidade, valor_unitario) VALUES
(5001, 1001, 1, 249.90),
(5001, 1002, 1, 89.50);

-- Consulta de validação: Relatório de faturamento cruzando serviços e vendas
SELECT 
    v.id_venda,
    c.nome_completo AS cliente,
    f.nome_completo AS atendente,
    p.produto,
    iv.quantidade,
    iv.valor_unitario,
    (iv.quantidade * iv.valor_unitario) AS subtotal,
    v.forma_pagamento
FROM venda v
JOIN cliente c ON v.cpf_cliente = c.cpf
JOIN funcionario f ON v.cpf_funcionario = f.cpf
JOIN item_venda iv ON v.id_venda = iv.id_venda
JOIN produto p ON iv.codigo_produto = p.codigo;
