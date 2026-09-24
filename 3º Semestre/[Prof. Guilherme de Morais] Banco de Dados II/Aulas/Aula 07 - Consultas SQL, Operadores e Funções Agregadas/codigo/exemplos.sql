-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre)
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Exemplos de Consultas SQL, Operadores e Funções Agregadas
-- Como executar:
--   Compatível com PostgreSQL, MySQL 8.0+ e SQLite.
--   Basta executar este script completo no seu SGBD ou cliente SQL (psql, DBeaver,
--   pgAdmin, MySQL Workbench). O script limpa objetos anteriores de forma segura,
--   cria as tabelas, popula dados de teste e executa as consultas da aula.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. LIMPEZA PREVENTIVA DO BANCO (IDEMPOTÊNCIA)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS veiculos;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS vendedor;
DROP TABLE IF EXISTS alu_aluno;
DROP TABLE IF EXISTS alu_instrutor;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS empregado;

-- -----------------------------------------------------------------------------
-- 2. CRIAÇÃO DAS TABELAS DE EXEMPLO
-- -----------------------------------------------------------------------------
CREATE TABLE clientes (
    cod_cliente INT,
    nome VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cpf VARCHAR(14) PRIMARY KEY
);

CREATE TABLE cliente (
    codigo_cliente INT PRIMARY KEY,
    nome_cliente VARCHAR(100),
    end_cliente VARCHAR(150),
    cidade VARCHAR(100),
    uf VARCHAR(2),
    idade INT
);

CREATE TABLE veiculos (
    chassi VARCHAR(30) PRIMARY KEY,
    placa VARCHAR(10),
    modelo VARCHAR(50),
    marca VARCHAR(50),
    cpf_cli VARCHAR(14),
    CONSTRAINT fk_veiculos_clientes FOREIGN KEY (cpf_cli) REFERENCES clientes(cpf)
);

CREATE TABLE produto (
    codigo_produto INT PRIMARY KEY,
    descricao VARCHAR(100),
    unidade VARCHAR(10),
    val_unit NUMERIC(10, 2),
    preco NUMERIC(10, 2)
);

CREATE TABLE vendedor (
    codigo_vendedor INT PRIMARY KEY,
    nome_vendedor VARCHAR(100),
    salario_fixo NUMERIC(10, 2),
    faixa_comissao VARCHAR(5)
);

CREATE TABLE alu_aluno (
    matricula INT PRIMARY KEY,
    nome_aluno VARCHAR(100),
    tel_aluno VARCHAR(20),
    cidade_aluno VARCHAR(100),
    data_nasc_aluno DATE
);

CREATE TABLE alu_instrutor (
    cod_instrut INT PRIMARY KEY,
    nome_instrut VARCHAR(100),
    data_admissao_instrut DATE
);

CREATE TABLE pedido (
    num_pedido INT PRIMARY KEY,
    codigo_vendedor INT,
    data_pedido DATE,
    valor_total NUMERIC(10, 2)
);

CREATE TABLE empregado (
    id_empregado INT PRIMARY KEY,
    pnome VARCHAR(50),
    cargo VARCHAR(50),
    salario NUMERIC(10, 2)
);

-- -----------------------------------------------------------------------------
-- 3. CARGA DE DADOS DE TESTE
-- -----------------------------------------------------------------------------
INSERT INTO clientes (cod_cliente, nome, cidade, estado, cpf) VALUES
(1, 'Carlos Eduardo', 'Campinas', 'SP', '111.111.111-11'),
(2, 'Mariana Silva', 'São Paulo', 'SP', '222.222.222-22'),
(3, 'Bento Ferreira', 'Belo Horizonte', 'MG', '333.333.333-33'),
(4, 'Ana Beatriz', 'Curitiba', 'PR', '444.444.444-44');

INSERT INTO cliente (codigo_cliente, nome_cliente, end_cliente, cidade, uf, idade) VALUES
(101, 'Ana Clara', 'Rua das Flores, 123', 'Campinas', 'SP', 25),
(102, 'Bruno Ramos', 'Av. Paulista, 900', 'São Paulo', 'SP', 34),
(103, 'Carlos Eduardo', 'Rua Minas, 45', 'Belo Horizonte', 'MG', 29),
(104, 'Daniel Souza', 'Av. Brasil, 50', 'Rio de Janeiro', 'RJ', 42);

INSERT INTO veiculos (chassi, placa, modelo, marca, cpf_cli) VALUES
('9BW111111111', 'ABC-1234', 'Corolla', 'Toyota', '111.111.111-11'),
('9BW222222222', 'XYZ-5678', 'Gol', 'VW', '111.111.111-11'),
('9BW333333333', 'KJH-9012', 'Civic', 'Honda', '222.222.222-22'),
('9BW444444444', 'POU-3456', 'Yaris', 'Toyota', '333.333.333-33'),
('9BW555555555', 'LMN-7890', 'Polo', 'VW', '444.444.444-44');

INSERT INTO produto (codigo_produto, descricao, unidade, val_unit, preco) VALUES
(1, 'Parafuso Sextavado', 'M', 0.11, 0.11),
(2, 'Porca de Ferro', 'M', 1.80, 1.80),
(3, 'Arruela Lisa', 'M', 2.00, 2.00),
(4, 'Prego de Aço', 'M', 0.85, 0.85),
(5, 'Tubo PVC 100mm', 'G', 1.05, 1.05),
(6, 'Tinta Acrílica Lata', 'L', 0.95, 0.95),
(7, 'Verniz Marítimo', 'L', 2.50, 2.50);

INSERT INTO vendedor (codigo_vendedor, nome_vendedor, salario_fixo, faixa_comissao) VALUES
(101, 'João Roberto', 2780.00, 'A'),
(102, 'Lucas Martins', 4600.00, 'B'),
(103, 'Carla Dias', 2300.00, 'C'),
(104, 'Marcos Lima', 3000.00, 'A');

INSERT INTO alu_aluno (matricula, nome_aluno, tel_aluno, cidade_aluno, data_nasc_aluno) VALUES
(1001, 'Alice Pereira', '(19) 98888-1111', 'Campinas', '1982-05-10'),
(1002, 'Gabriel Santos', '(19) 97777-2222', 'Campinas', '1979-11-20'),
(1003, 'Renata Castro', '(11) 96666-3333', 'São Paulo', '1985-03-15');

INSERT INTO alu_instrutor (cod_instrut, nome_instrut, data_admissao_instrut) VALUES
(1, 'Professor Marcos', '1998-03-01'),
(2, 'Professora Silvia', '1995-07-20');

INSERT INTO pedido (num_pedido, codigo_vendedor, data_pedido, valor_total) VALUES
(501, 101, '2026-05-01', 1500.00),
(502, 102, '2026-05-02', 3200.00);

INSERT INTO empregado (id_empregado, pnome, cargo, salario) VALUES
(1, 'Antonio', 'Gerente', 8500.00),
(2, 'Aline', 'Analista', 4200.00),
(3, 'Amanda', 'Gerente', 9100.00),
(4, 'Carlos', 'Assistente', 2500.00),
(5, 'Beatriz', 'Supervisora', 5300.00);

-- =============================================================================
-- 4. CONSULTAS DE EXEMPLO - AULA 04: CONSULTANDO DADOS 1
-- =============================================================================

-- 4.1. Projeção Total (*)
SELECT * FROM cliente;

-- 4.2. Projeção de Campos Específicos
SELECT codigo_cliente, nome_cliente, end_cliente FROM cliente;

-- 4.3. Ordenação Simples com ORDER BY
SELECT * FROM cliente ORDER BY nome_cliente;

-- 4.4. Ordenação Múltipla (Nome e depois Idade)
SELECT codigo_cliente, nome_cliente, idade FROM cliente ORDER BY nome_cliente, idade;

-- 4.5. Filtragem Simples com WHERE
SELECT * FROM cliente WHERE uf = 'SP';
SELECT codigo_cliente, nome_cliente, end_cliente FROM cliente WHERE uf = 'SP';

-- 4.6. Filtragem e Ordenação Combinadas
SELECT codigo_cliente, nome_cliente, end_cliente FROM cliente WHERE uf = 'SP' ORDER BY nome_cliente;

-- 4.7. Operadores Relacionais (>= 1.00)
SELECT descricao FROM produto WHERE val_unit >= 1.00;

-- 4.8. Exemplos com Datas e Códigos
SELECT nome_aluno, data_nasc_aluno FROM alu_aluno WHERE data_nasc_aluno >= '1980-01-30';
SELECT * FROM pedido WHERE codigo_vendedor = 101;
SELECT cod_instrut, nome_instrut FROM alu_instrutor WHERE data_admissao_instrut >= '1997-01-15';

-- 4.9. Operador Lógico AND
SELECT descricao FROM produto WHERE val_unit >= 0.50 AND val_unit <= 2.00;
SELECT nome_aluno, tel_aluno FROM alu_aluno WHERE data_nasc_aluno >= '1981-02-24' AND cidade_aluno = 'Campinas';

-- 4.10. Operador Lógico OR
SELECT codigo_vendedor, nome_vendedor, salario_fixo, faixa_comissao FROM vendedor WHERE salario_fixo = 2780 OR salario_fixo = 4600;

-- 4.11. Eliminação de Duplicatas com DISTINCT
SELECT DISTINCT cidade FROM cliente;

-- 4.12. Operadores Aritméticos e Criação de Aliases (AS)
SELECT salario_fixo * 1.10 AS salario_com_reajuste FROM vendedor;
SELECT descricao, unidade, val_unit AS "Preço Atual", val_unit * 1.25 AS "Preço com Aumento" FROM produto;
SELECT descricao, unidade, val_unit AS "Preço Atual", val_unit - (val_unit * 0.12) AS "Preço com Desconto" FROM produto WHERE unidade = 'M';

-- 4.13. Filtragem de Intervalos com BETWEEN
SELECT * FROM vendedor WHERE salario_fixo BETWEEN 2000 AND 3000;
SELECT codigo_produto, descricao, val_unit FROM produto WHERE val_unit BETWEEN 0.32 AND 2.00;
SELECT matricula, nome_aluno, data_nasc_aluno FROM alu_aluno WHERE data_nasc_aluno BETWEEN '1980-02-01' AND '1990-10-30';

-- =============================================================================
-- 5. CONSULTAS DE EXEMPLO - AULA 05: LIKE, ILIKE E FUNÇÕES AGREGADAS
-- =============================================================================

-- 5.1. Casamento de Padrões com LIKE (Primeira letra 'A')
SELECT * FROM empregado WHERE pnome LIKE 'A%';

-- 5.2. Casamento de Padrões contendo 'a'
SELECT pnome, cargo FROM empregado WHERE pnome LIKE '%a%';

-- 5.3. Casamento Case-Insensitive com ILIKE (No PostgreSQL; no MySQL o LIKE padrão já é insensitive em collation padrão)
SELECT pnome, cargo FROM empregado WHERE pnome ILIKE '%a%';

-- 5.4. Média Salarial com AVG
SELECT avg(salario) AS media_salarial FROM empregado;

-- 5.5. Contagem com COUNT e Filtro WHERE
SELECT count(*) AS "Quantidade de Gerente na Empresa" FROM empregado WHERE cargo = 'Gerente';

-- 5.6. Extremos com MAX e MIN
SELECT max(salario) AS maior_salario, min(salario) AS menor_salario FROM empregado;
SELECT max(preco) AS MAIOR_PRECO FROM produto;

-- 5.7. Totalização com SUM
SELECT sum(salario) AS "Soma salarial" FROM empregado;

-- =============================================================================
-- 6. CONSULTAS DE EXEMPLO - AULA 06: OPERADOR IN E JUNÇÕES DE TABELAS
-- =============================================================================

-- 6.1. Operadores IN e NOT IN
SELECT nome_cliente, uf FROM cliente WHERE uf IN ('SP', 'MG');
SELECT nome_cliente, uf FROM cliente WHERE uf NOT IN ('SP', 'MG');
SELECT nome_vendedor, faixa_comissao FROM vendedor WHERE faixa_comissao IN ('A', 'B');
SELECT codigo_produto, unidade, descricao, val_unit FROM produto WHERE unidade IN ('M', 'G', 'L') AND val_unit <= 1.05;

-- 6.2. Junção Básica (Relacionamento Clientes e Veiculos)
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca
FROM clientes, veiculos
WHERE (clientes.cpf = veiculos.cpf_cli);

-- 6.3. Junção com Ordenação
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca
FROM clientes, veiculos
WHERE (clientes.cpf = veiculos.cpf_cli)
ORDER BY clientes.cidade;

-- 6.4. Junção com Filtros Adicionais (WHERE ... AND ...)
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca
FROM clientes, veiculos
WHERE (clientes.cpf = veiculos.cpf_cli)
  AND (clientes.estado = 'SP');

-- 6.5. Junção Completa (Filtro e Ordenação)
SELECT clientes.nome, clientes.cidade, veiculos.modelo, veiculos.marca
FROM clientes, veiculos
WHERE (clientes.cpf = veiculos.cpf_cli)
  AND (clientes.estado = 'SP')
ORDER BY clientes.cidade;
