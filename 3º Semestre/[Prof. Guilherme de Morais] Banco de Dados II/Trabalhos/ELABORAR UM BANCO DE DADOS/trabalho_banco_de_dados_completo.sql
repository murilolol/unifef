-- =====================================================================
-- Instituição: Faculdade de Sistemas de Informação
-- Disciplina: Banco de Dados II (3º Semestre)
-- Professor: Prof. Guilherme de Morais
-- Atividade: Elaborar um Banco de Dados (DER para Modelo Relacional e DDL/DML)
-- Descrição: Este script SQL contém a modelagem relacional completa, criação 
--            de tabelas (DDL) e consultas exemplares (DML) para os 5 exercícios
--            propostos no trabalho prático.
-- =====================================================================

-- Configuração inicial do ambiente relacional
CREATE DATABASE IF NOT EXISTS db_trabalho_bd2;
USE db_trabalho_bd2;

-- Desativar temporariamente verificações de chave estrangeira para limpeza segura
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS item_venda;
DROP TABLE IF EXISTS venda;
DROP TABLE IF EXISTS servico;
DROP TABLE IF EXISTS animal;
DROP TABLE IF EXISTS cliente_pet;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS fornecedor;
DROP TABLE IF EXISTS funcionario_pet;
DROP TABLE IF EXISTS cargo;

DROP TABLE IF EXISTS historico_conserto;
DROP TABLE IF EXISTS carro;
DROP TABLE IF EXISTS categoria_carro;
DROP TABLE IF EXISTS cliente_locadora;

DROP TABLE IF EXISTS remessa;
DROP TABLE IF EXISTS viagem;
DROP TABLE IF EXISTS caminhao;
DROP TABLE IF EXISTS deposito;
DROP TABLE IF EXISTS armazem;

DROP TABLE IF EXISTS alocacao_projeto;
DROP TABLE IF EXISTS projeto;
DROP TABLE IF EXISTS departamento;
DROP TABLE IF EXISTS telefone_funcionario;
DROP TABLE IF EXISTS dependente;
DROP TABLE IF EXISTS funcionario;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- EXERCÍCIO 1: SISTEMA EMPRESA
-- =====================================================================

CREATE TABLE departamento (
    id_departamento INT AUTO_INCREMENT PRIMARY KEY,
    numero_departamento INT NOT NULL UNIQUE,
    nome_departamento VARCHAR(100) NOT NULL,
    local_departamento VARCHAR(100) NOT NULL
);

CREATE TABLE funcionario (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    primeiro_nome VARCHAR(50) NOT NULL,
    segundo_nome VARCHAR(50),
    ultimo_nome VARCHAR(50) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    sexo CHAR(1) NOT NULL CHECK (sexo IN ('M', 'F', 'O')),
    cpf CHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    id_departamento INT NOT NULL,
    id_supervisor INT NULL,
    CONSTRAINT fk_func_depto FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento),
    CONSTRAINT fk_func_supervisor FOREIGN KEY (id_supervisor) REFERENCES funcionario(id_funcionario)
);

-- Adicionar FK de gerente no departamento (relação 1:1 ou 1:N dependendo da regra, aqui departamento tem 1 gerente)
ALTER TABLE departamento ADD COLUMN id_gerente INT NULL;
ALTER TABLE departamento ADD CONSTRAINT fk_depto_gerente FOREIGN KEY (id_gerente) REFERENCES funcionario(id_funcionario);

CREATE TABLE telefone_funcionario (
    id_telefone INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL,
    numero_telefone VARCHAR(20) NOT NULL,
    CONSTRAINT fk_tel_func FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario) ON DELETE CASCADE
);

CREATE TABLE dependente (
    id_dependente INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL,
    primeiro_nome VARCHAR(50) NOT NULL,
    segundo_nome VARCHAR(50),
    ultimo_nome VARCHAR(50) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    sexo CHAR(1) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    CONSTRAINT fk_dep_func FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario) ON DELETE CASCADE
);

CREATE TABLE projeto (
    id_projeto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_projeto VARCHAR(20) NOT NULL UNIQUE,
    nome_projeto VARCHAR(100) NOT NULL,
    data_projeto DATE NOT NULL,
    id_departamento INT NOT NULL,
    CONSTRAINT fk_proj_depto FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento)
);

CREATE TABLE alocacao_projeto (
    id_funcionario INT NOT NULL,
    id_projeto INT NOT NULL,
    quantidade_horas DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (id_funcionario, id_projeto),
    CONSTRAINT fk_aloc_func FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario),
    CONSTRAINT fk_aloc_proj FOREIGN KEY (id_projeto) REFERENCES projeto(id_projeto)
);


-- =====================================================================
-- EXERCÍCIOS 2 e 3: SISTEMA PET-SHOP
-- =====================================================================

CREATE TABLE cargo (
    id_cargo INT AUTO_INCREMENT PRIMARY KEY,
    nome_cargo VARCHAR(50) NOT NULL UNIQUE,
    descricao_cargo VARCHAR(200)
);

CREATE TABLE funcionario_pet (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    cpf CHAR(11) NOT NULL UNIQUE,
    nome_completo VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    telefone_residencial VARCHAR(20),
    telefone_celular VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    id_cargo INT NOT NULL,
    CONSTRAINT fk_func_cargo FOREIGN KEY (id_cargo) REFERENCES cargo(id_cargo)
);

CREATE TABLE fornecedor (
    id_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    nome_fornecedor VARCHAR(150) NOT NULL,
    cnpj VARCHAR(20) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE produto (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_produto VARCHAR(30) NOT NULL UNIQUE,
    nome_produto VARCHAR(100) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL,
    validade DATE NOT NULL,
    id_fornecedor INT NOT NULL,
    CONSTRAINT fk_prod_forn FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(id_fornecedor)
);

CREATE TABLE cliente_pet (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    cpf CHAR(11) NOT NULL UNIQUE,
    nome_completo VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    telefone_comercial VARCHAR(20),
    telefone_residencial VARCHAR(20),
    telefone_celular VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE animal (
    id_animal INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    nome VARCHAR(50) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo CHAR(1) NOT NULL,
    raca VARCHAR(50) NOT NULL,
    cor_predominante VARCHAR(30) NOT NULL,
    tipo_animal VARCHAR(50) NOT NULL,
    CONSTRAINT fk_animal_cliente FOREIGN KEY (id_cliente) REFERENCES cliente_pet(id_cliente)
);

CREATE TABLE servico (
    id_servico INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL,
    id_cliente INT NOT NULL,
    id_animal INT NOT NULL,
    tipo_servico VARCHAR(100) NOT NULL,
    data_servico DATETIME NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_serv_func FOREIGN KEY (id_funcionario) REFERENCES funcionario_pet(id_funcionario),
    CONSTRAINT fk_serv_cli FOREIGN KEY (id_cliente) REFERENCES cliente_pet(id_cliente),
    CONSTRAINT fk_serv_animal FOREIGN KEY (id_animal) REFERENCES animal(id_animal)
);

CREATE TABLE venda (
    id_venda INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_funcionario INT NOT NULL,
    data_venda DATETIME NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_venda_cli FOREIGN KEY (id_cliente) REFERENCES cliente_pet(id_cliente),
    CONSTRAINT fk_venda_func FOREIGN KEY (id_funcionario) REFERENCES funcionario_pet(id_funcionario)
);

CREATE TABLE item_venda (
    id_venda INT NOT NULL,
    id_produto INT NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_venda, id_produto),
    CONSTRAINT fk_item_venda FOREIGN KEY (id_venda) REFERENCES venda(id_venda),
    CONSTRAINT fk_item_produto FOREIGN KEY (id_produto) REFERENCES produto(id_produto)
);


-- =====================================================================
-- EXERCÍCIO 4: LOCADORA DE AUTOMÓVEIS
-- =====================================================================

CREATE TABLE cliente_locadora (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    rg VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    cnh VARCHAR(20) NOT NULL UNIQUE,
    idade INT NOT NULL
);

CREATE TABLE categoria_carro (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    codigo_categoria VARCHAR(20) NOT NULL UNIQUE,
    nome_categoria VARCHAR(50) NOT NULL,
    preco_diaria DECIMAL(10,2) NOT NULL,
    descricao TEXT NOT NULL
);

CREATE TABLE carro (
    id_carro INT AUTO_INCREMENT PRIMARY KEY,
    chassi VARCHAR(30) NOT NULL UNIQUE,
    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    ano INT NOT NULL,
    cor VARCHAR(30) NOT NULL,
    id_categoria INT NOT NULL,
    CONSTRAINT fk_carro_cat FOREIGN KEY (id_categoria) REFERENCES categoria_carro(id_categoria)
);

CREATE TABLE locacao (
    id_locacao INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_carro INT NOT NULL,
    data_hora_locacao DATETIME NOT NULL,
    CONSTRAINT fk_loc_cliente FOREIGN KEY (id_cliente) REFERENCES cliente_locadora(id_cliente),
    CONSTRAINT fk_loc_carro FOREIGN KEY (id_carro) REFERENCES carro(id_carro)
);

CREATE TABLE historico_conserto (
    id_conserto INT AUTO_INCREMENT PRIMARY KEY,
    id_carro INT NOT NULL,
    dia_conserto DATE NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    descricao_servico TEXT NOT NULL,
    oficina VARCHAR(150) NOT NULL,
    CONSTRAINT fk_cons_carro FOREIGN KEY (id_carro) REFERENCES carro(id_carro)
);


-- =====================================================================
-- EXERCÍCIO 5: COMPANHIA DE TRANSPORTE
-- =====================================================================

CREATE TABLE armazem (
    id_armazem INT AUTO_INCREMENT PRIMARY KEY,
    numero_armazem INT NOT NULL UNIQUE
);

CREATE TABLE deposito (
    id_deposito INT AUTO_INCREMENT PRIMARY KEY,
    numero_deposito INT NOT NULL UNIQUE
);

CREATE TABLE caminhao (
    id_caminhao INT AUTO_INCREMENT PRIMARY KEY,
    codigo_licenca VARCHAR(30) NOT NULL UNIQUE,
    capacidade_volume DECIMAL(10,2) NOT NULL,
    capacidade_peso DECIMAL(10,2) NOT NULL
);

CREATE TABLE viagem (
    id_viagem INT AUTO_INCREMENT PRIMARY KEY,
    numero_viagem INT NOT NULL UNIQUE,
    id_caminhao INT NOT NULL,
    id_armazem_origem INT NOT NULL,
    peso_total DECIMAL(10,2) NOT NULL,
    volume_total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_viagem_caminhao FOREIGN KEY (id_caminhao) REFERENCES caminhao(id_caminhao),
    CONSTRAINT fk_viagem_armazem FOREIGN KEY (id_armazem_origem) REFERENCES armazem(id_armazem)
);

CREATE TABLE remessa (
    id_remessa INT AUTO_INCREMENT PRIMARY KEY,
    numero_remessa INT NOT NULL UNIQUE,
    id_viagem INT NOT NULL,
    id_deposito_destino INT NOT NULL,
    volume DECIMAL(10,2) NOT NULL,
    peso DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_remessa_viagem FOREIGN KEY (id_viagem) REFERENCES viagem(id_viagem),
    CONSTRAINT fk_remessa_deposito FOREIGN KEY (id_deposito_destino) REFERENCES deposito(id_deposito)
);


-- =====================================================================
-- POPULAÇÃO DE DADOS DE EXEMPLO (DML) E CONSULTAS ÚTEIS (JOINS / SUBSELECTS)
-- =====================================================================

-- Inserindo dados na Empresa (Ex 1)
INSERT INTO departamento (numero_departamento, nome_departamento, local_departamento) VALUES (10, 'Tecnologia da Informação', 'Bloco A');
INSERT INTO funcionario (primeiro_nome, segundo_nome, ultimo_nome, endereco, sexo, cpf, data_nascimento, id_departamento) 
VALUES ('Carlos', 'Eduardo', 'Silva', 'Rua das Flores, 123', 'M', '11122233344', '1985-04-12', 10);
UPDATE departamento SET id_gerente = 1 WHERE numero_departamento = 10;

INSERT INTO projeto (codigo_projeto, nome_projeto, data_projeto, id_departamento) VALUES ('PRJ-01', 'Migração em Nuvem', '2023-01-10', 10);
INSERT INTO alocacao_projeto (id_funcionario, id_projeto, quantidade_horas) VALUES (1, 1, 40.5);

-- Consulta Completa Exemplo 1: Funcionários e seus Projetos Alocados
SELECT 
    CONCAT(f.primeiro_nome, ' ', f.ultimo_nome) AS funcionario,
    d.nome_departamento,
    p.nome_projeto,
    ap.quantidade_horas
FROM funcionario f
JOIN departamento d ON f.id_departamento = d.id_departamento
JOIN alocacao_projeto ap ON f.id_funcionario = ap.id_funcionario
JOIN projeto p ON ap.id_projeto = p.id_projeto;

-- Consulta Exemplo 5: Viagens e remessas da Companhia de Transporte
SELECT 
    v.numero_viagem,
    c.codigo_licenca,
    v.peso_total,
    v.volume_total,
    r.numero_remessa,
    d.numero_deposito AS deposito_destino
FROM viagem v
JOIN caminhao c ON v.id_caminhao = c.id_caminhao
JOIN remessa r ON v.id_viagem = r.id_viagem
JOIN deposito d ON r.id_deposito_destino = d.id_deposito;