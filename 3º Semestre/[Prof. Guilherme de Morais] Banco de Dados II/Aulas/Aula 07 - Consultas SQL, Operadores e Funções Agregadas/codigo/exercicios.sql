-- =============================================================================
-- Disciplina : Banco de Dados II (3º Semestre)
-- Professor  : Prof. Guilherme de Morais
-- Tema       : Resolução dos Exercícios de Consultas e Junções SQL
-- Como executar:
--   Compatível com PostgreSQL, MySQL 8.0+ e SQLite.
--   Execute o script completo. O script é idempotente, recria o banco de teste
--   com os registros adequados para evidenciar a correção de cada consulta e exibe
--   o resultado de cada um dos 4 exercícios propostos nas aulas 04 e 06.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. PREPARAÇÃO DO AMBIENTE (DDL)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS veiculos;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS produto;

CREATE TABLE clientes (
    cod_cliente INT,
    nome VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cpf VARCHAR(14) PRIMARY KEY
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
    val_unit NUMERIC(10, 2)
);

-- -----------------------------------------------------------------------------
-- 2. CARGA DE DADOS DE TESTE (DML)
-- -----------------------------------------------------------------------------
INSERT INTO clientes (cod_cliente, nome, cidade, estado, cpf) VALUES
(1, 'Carlos Silva', 'Campinas', 'SP', '111.111.111-11'),
(2, 'Beatriz Alcantara', 'Santos', 'SP', '222.222.222-22'),
(3, 'Ricardo Moreira', 'Belo Horizonte', 'MG', '333.333.333-33'),
(4, 'Juliana Mendes', 'Curitiba', 'PR', '444.444.444-44');

INSERT INTO veiculos (chassi, placa, modelo, marca, cpf_cli) VALUES
('9BW100000001', 'TOY-1111', 'Corolla', 'Toyota', '111.111.111-11'),
('9BW100000002', 'YAR-2222', 'Yaris', 'Toyota', '222.222.222-22'),
('9BW100000003', 'GOL-3333', 'Gol', 'VW', '111.111.111-11'),
('9BW100000004', 'POL-4444', 'Polo', 'VW', '333.333.333-33'),
('9BW100000005', 'CIV-5555', 'Civic', 'Honda', '444.444.444-44'),
('9BW100000006', 'FIT-6666', 'Fit', 'Honda', '333.333.333-33');

INSERT INTO produto (codigo_produto, descricao, unidade, val_unit) VALUES
(1, 'Parafuso Aço M1', 'M', 0.11),
(2, 'Parafuso Aço M2', 'M', 1.80),
(3, 'Barra Roscada M3', 'M', 2.00),
(4, 'Porca Sextavada', 'M', 0.50),
(5, 'Arruela de Pressão', 'G', 0.11),
(6, 'Prego Galvanizado', 'L', 1.80);

-- =============================================================================
-- 3. RESOLUÇÃO DOS EXERCÍCIOS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Exercício 1 (Aula 06 - Slide 12)
-- Enunciado:
--   "1- Selecione todos os nome e cpf da tabela cliente e marca e modelo cujo o modelo for igual a Toyota."
--
-- Explicação:
--   O professor solicita a junção das tabelas 'clientes' e 'veiculos' por meio do
--   vínculo de chave estrangeira (clientes.cpf = veiculos.cpf_cli). Como o texto diz
--   literalmente 'cujo o modelo for igual a Toyota', tratamos a igualdade em modelo
--   e também contemplamos a marca correspondente para assegurar a consistência do filtro.
-- -----------------------------------------------------------------------------
SELECT clientes.nome, clientes.cpf, veiculos.marca, veiculos.modelo
FROM clientes, veiculos
WHERE (clientes.cpf = veiculos.cpf_cli)
  AND (veiculos.modelo = 'Toyota' OR veiculos.marca = 'Toyota');


-- -----------------------------------------------------------------------------
-- Exercício 2 (Aula 06 - Slide 12)
-- Enunciado:
--   "2- Seleciona do veículos chassi, placa, modelo e marcar e da tabela clientes nome e cpf , cujo os clientes que residem em estado diferente de São Paulo."
--
-- Explicação:
--   Realiza-se a junção entre 'clientes' e 'veiculos' comparando clientes.cpf com
--   veiculos.cpf_cli e aplica-se a condição de diferença de estado (<> 'SP').
-- -----------------------------------------------------------------------------
SELECT veiculos.chassi, veiculos.placa, veiculos.modelo, veiculos.marca, clientes.nome, clientes.cpf
FROM clientes, veiculos
WHERE (clientes.cpf = veiculos.cpf_cli)
  AND (clientes.estado <> 'SP');


-- -----------------------------------------------------------------------------
-- Exercício 3 (Aula 06 - Slide 12)
-- Enunciado:
--   "3- Selecione todos os veículos da marcar Toyota e VW. Usando o comando in."
--
-- Explicação:
--   Utiliza-se o operador IN na tabela 'veiculos' para verificar se a coluna 'marca'
--   está contida no conjunto ('Toyota', 'VW'), dispensando o uso repetitivo do operador OR.
-- -----------------------------------------------------------------------------
SELECT chassi, placa, modelo, marca, cpf_cli
FROM veiculos
WHERE marca IN ('Toyota', 'VW');


-- -----------------------------------------------------------------------------
-- Exercício 4 (Aula 04 - Slide 16)
-- Enunciado:
--   "Encontre a descrição, unidade e o valor unitário dos produtos de unidade ‘M’ e os valores unitários sejam 0.11 ou 1.8 ou 2."
--
-- Explicação:
--   A consulta filtra produtos que atendam simultaneamente à unidade 'M' e a uma das
--   três opções de preço (0.11, 1.8 ou 2.0). Pode ser resolvida com operadores lógicos
--   combinados (AND com OR encapsulado entre parênteses) ou utilizando o operador IN.
-- -----------------------------------------------------------------------------
-- Solução com AND e OR conforme demonstrado no slide de aula:
SELECT descricao, unidade, val_unit
FROM produto
WHERE unidade = 'M'
  AND (val_unit = 0.11 OR val_unit = 1.8 OR val_unit = 2);

-- Solução alternativa equivalente utilizando o operador IN:
SELECT descricao, unidade, val_unit
FROM produto
WHERE unidade = 'M'
  AND val_unit IN (0.11, 1.8, 2.00);
