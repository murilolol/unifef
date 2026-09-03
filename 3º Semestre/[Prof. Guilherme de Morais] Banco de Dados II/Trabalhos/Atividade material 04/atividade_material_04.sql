FILENAME: atividade_material_04.sql
---CODE_START---
--------------------------------------------------------------------------------
-- DISCIPLINA: Banco de Dados II (3º Semestre - Sistemas de Informação)
-- PROFESSOR: Prof. Guilherme de Morais
-- ATIVIDADE: Atividade material 04 - Consultando Dados 1
-- AUTOR: Professor & Desenvolvedor Sênior
-- DESCRIÇÃO: Script completo contendo a criação das tabelas, carga de dados 
--            e resolução de todas as consultas solicitadas na atividade.
--------------------------------------------------------------------------------

-- ============================================================================
-- 0. CRIAÇÃO DAS TABELAS E CARGA DE DADOS (SETUP DO AMBIENTE)
-- ============================================================================

DROP TABLE IF EXISTS PEDIDO;
DROP TABLE IF EXISTS VENDEDOR;
DROP TABLE IF EXISTS PRODUTO;
DROP TABLE IF EXISTS CLIENTE;

CREATE TABLE CLIENTE (
    cod_cliente INT PRIMARY KEY,
    nome VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    idade INT
);

CREATE TABLE PRODUTO (
    cod_produto INT PRIMARY KEY,
    descricao VARCHAR(100),
    unidade VARCHAR(10),
    valor_unitario NUMERIC(10,2)
);

CREATE TABLE VENDEDOR (
    cod_vendedor INT PRIMARY KEY,
    nome VARCHAR(100),
    salario NUMERIC(10,2)
);

CREATE TABLE PEDIDO (
    cod_pedido INT PRIMARY KEY,
    cod_cliente INT,
    cod_vendedor INT,
    data_pedido DATE,
    FOREIGN KEY (cod_cliente) REFERENCES CLIENTE(cod_cliente),
    FOREIGN KEY (cod_vendedor) REFERENCES VENDEDOR(cod_vendedor)
);

-- Carga de Dados: CLIENTE
INSERT INTO CLIENTE (cod_cliente, nome, cidade, estado, idade) VALUES
(1,'Ana Souza','São Paulo','SP',25),
(2,'Bruno Lima','Campinas','SP',32),
(3,'Carlos Mendes','Rio de Janeiro','RJ',41),
(4,'Daniela Rocha','Belo Horizonte','MG',29),
(5,'Eduardo Alves','Curitiba','PR',35),
(6,'Fernanda Costa','Fortaleza','CE',28),
(7,'Gabriel Santos','Salvador','BA',22),
(8,'Helena Martins','Recife','PE',31),
(9,'Igor Fernandes','Porto Alegre','RS',27),
(10,'Juliana Ribeiro','Manaus','AM',34),
(11,'Kleber Batista','São Paulo','SP',45),
(12,'Larissa Gomes','Campinas','SP',26),
(13,'Marcos Vinicius','Rio de Janeiro','RJ',38),
(14,'Natália Freitas','Belo Horizonte','MG',24),
(15,'Otávio Teixeira','Curitiba','PR',36),
(16,'Patrícia Nunes','Fortaleza','CE',30),
(17,'Rafael Duarte','Salvador','BA',33),
(18,'Simone Pires','Recife','PE',29),
(19,'Tiago Barros','Porto Alegre','RS',40),
(20,'Vanessa Melo','Manaus','AM',27),
(21,'William Castro','São Paulo','SP',31),
(22,'Xavier Rocha','Campinas','SP',28),
(23,'Yasmin Alves','Rio de Janeiro','RJ',23),
(24,'Zeca Ramos','Belo Horizonte','MG',37),
(25,'Aline Barbosa','Curitiba','PR',34),
(26,'Bruna Carvalho','Fortaleza','CE',26),
(27,'Caio Lopes','Salvador','BA',39),
(28,'Diego Monteiro','Recife','PE',28),
(29,'Elaine Farias','Porto Alegre','RS',35),
(30,'Felipe Araújo','Manaus','AM',32),
(31,'Gisele Dias','São Paulo','SP',41),
(32,'Hugo Pereira','Campinas','SP',29),
(33,'Isabela Moura','Rio de Janeiro','RJ',27),
(34,'João Pedro','Belo Horizonte','MG',30),
(35,'Karen Souza','Curitiba','PR',33),
(36,'Leonardo Costa','Fortaleza','CE',38),
(37,'Mariana Rocha','Salvador','BA',25),
(38,'Nicolas Lima','Recife','PE',42),
(39,'Olívia Martins','Porto Alegre','RS',31),
(40,'Paulo Sérgio','Manaus','AM',36),
(41,'Renata Duarte','São Paulo','SP',28),
(42,'Sandro Alves','Campinas','SP',44),
(43,'Tatiane Gomes','Rio de Janeiro','RJ',29),
(44,'Ubirajara Silva','Belo Horizonte','MG',50),
(45,'Viviane Freitas','Curitiba','PR',26),
(46,'Wesley Santos','Fortaleza','CE',34),
(47,'Ximena Pires','Salvador','BA',28),
(48,'Yuri Barros','Recife','PE',37),
(49,'Zilda Melo','Porto Alegre','RS',33),
(50,'André Nunes','Manaus','AM',40);

-- Carga de Dados: PRODUTO
INSERT INTO PRODUTO (cod_produto, descricao, unidade, valor_unitario) VALUES
(1,'Arroz','KG',5.50),
(2,'Feijão','KG',7.20),
(3,'Macarrão','UN',4.00),
(4,'Açúcar','KG',3.80),
(5,'Café','KG',12.00),
(6,'Leite','L',4.50),
(7,'Óleo','L',6.70),
(8,'Farinha','KG',5.10),
(9,'Sal','KG',2.50),
(10,'Margarina','UN',6.00),
(11,'Queijo','KG',25.00),
(12,'Presunto','KG',22.00),
(13,'Pão','KG',10.00),
(14,'Refrigerante','L',8.00),
(15,'Suco','L',7.50),
(16,'Biscoito','UN',3.20),
(17,'Chocolate','UN',5.80),
(18,'Detergente','UN',2.90),
(19,'Sabão','UN',4.60),
(20,'Shampoo','UN',12.50),
(21,'Condicionador','UN',13.00),
(22,'Papel Higiênico','UN',9.00),
(23,'Escova de Dente','UN',6.50),
(24,'Creme Dental','UN',5.90),
(25,'Sabonete','UN',2.80),
(26,'Carne Bovina','KG',35.00),
(27,'Frango','KG',15.00),
(28,'Peixe','KG',28.00),
(29,'Linguiça','KG',18.00),
(30,'Ovos','DZ',10.00),
(31,'Tomate','KG',6.00),
(32,'Batata','KG',4.50),
(33,'Cebola','KG',5.20),
(34,'Alface','UN',3.00),
(35,'Cenoura','KG',4.00),
(36,'Maçã','KG',7.00),
(37,'Banana','KG',5.50),
(38,'Laranja','KG',4.80),
(39,'Uva','KG',12.00),
(40,'Melancia','UN',15.00),
(41,'Melão','UN',12.00),
(42,'Abacaxi','UN',8.50),
(43,'Manga','KG',6.30),
(44,'Pera','KG',9.00),
(45,'Kiwi','KG',14.00),
(46,'Morango','KG',18.00),
(47,'Amendoim','KG',11.00),
(48,'Castanha','KG',30.00),
(49,'Granola','UN',12.00),
(50,'Iogurte','UN',4.50);

-- Carga de Dados: VENDEDOR
INSERT INTO VENDEDOR (cod_vendedor, nome, salario) VALUES
(1,'Carlos Silva',2500),
(2,'Marcos Oliveira',3000),
(3,'Ana Paula',2800),
(4,'João Mendes',3200),
(5,'Fernanda Souza',2700),
(6,'Lucas Rocha',3500),
(7,'Paula Lima',2900),
(8,'Ricardo Alves',3100),
(9,'Juliana Castro',2600),
(10,'Bruno Santos',3300),
(11,'Amanda Dias',2800),
(12,'Felipe Nunes',3400),
(13,'Patrícia Gomes',3000),
(14,'Gustavo Ribeiro',3600),
(15,'Camila Freitas',2900),
(16,'Rafael Pereira',3700),
(17,'Simone Duarte',2800),
(18,'Eduardo Martins',3900),
(19,'Natália Melo',3000),
(20,'André Costa',3200),
(21,'Vanessa Rocha',2700),
(22,'Leonardo Silva',3500),
(23,'Carla Lopes',2900),
(24,'Diego Alves',3100),
(25,'Elaine Souza',2600),
(26,'Fábio Santos',3300),
(27,'Gisele Castro',2800),
(28,'Hugo Lima',3400),
(29,'Isabela Mendes',3000),
(30,'Jorge Pereira',3600),
(31,'Karen Dias',2900),
(32,'Luis Nunes',3700),
(33,'Mariana Gomes',2800),
(34,'Nicolas Ribeiro',3900),
(35,'Olivia Freitas',3000),
(36,'Paulo Martins',3200),
(37,'Renata Melo',2700),
(38,'Sandro Costa',3500),
(39,'Tatiane Rocha',2900),
(40,'Ubirajara Silva',3100),
(41,'Viviane Lopes',2600),
(42,'Wesley Alves',3300),
(43,'Ximena Souza',2800),
(44,'Yuri Santos',3400),
(45,'Zilda Castro',3000),
(46,'Alan Lima',3600),
(47,'Bianca Mendes',2900),
(48,'Caio Pereira',3700),
(49,'Denise Dias',2800),
(50,'Everton Nunes',3900);

-- Carga de Dados: PEDIDO
INSERT INTO PEDIDO (cod_pedido, cod_cliente, cod_vendedor, data_pedido) VALUES
(1,1,1,'2024-01-10'),
(2,2,2,'2024-01-11'),
(3,3,3,'2024-01-12'),
(4,4,4,'2024-01-13'),
(5,5,5,'2024-01-14'),
(6,6,6,'2024-01-15'),
(7,7,7,'2024-01-16'),
(8,8,8,'2024-01-17'),
(9,9,9,'2024-01-18'),
(10,10,10,'2024-01-19'),
(11,11,11,'2024-01-20'),
(12,12,12,'2024-01-21'),
(13,13,13,'2024-01-22'),
(14,14,14,'2024-01-23'),
(15,15,15,'2024-01-24'),
(16,16,16,'2024-01-25'),
(17,17,17,'2024-01-26'),
(18,18,18,'2024-01-27'),
(19,19,19,'2024-01-28'),
(20,20,20,'2024-01-29'),
(21,21,21,'2024-02-01'),
(22,22,22,'2024-02-02'),
(23,23,23,'2024-02-03'),
(24,24,24,'2024-02-04'),
(25,25,25,'2024-02-05'),
(26,26,26,'2024-02-06'),
(27,27,27,'2024-02-07'),
(28,28,28,'2024-02-08'),
(29,29,29,'2024-02-09'),
(30,30,30,'2024-02-10'),
(31,31,31,'2024-02-11'),
(32,32,32,'2024-02-12'),
(33,33,33,'2024-02-13'),
(34,34,34,'2024-02-14'),
(35,35,35,'2024-02-15'),
(36,36,36,'2024-02-16'),
(37,37,37,'2024-02-17'),
(38,38,38,'2024-02-18'),
(39,39,39,'2024-02-19'),
(40,40,40,'2024-02-20'),
(41,41,41,'2024-02-21'),
(42,42,42,'2024-02-22'),
(43,43,43,'2024-02-23'),
(44,44,44,'2024-02-24'),
(45,45,45,'2024-02-25'),
(46,46,46,'2024-02-26'),
(47,47,47,'2024-02-27'),
(48,48,48,'2024-02-28'),
(49,49,49,'2024-02-29'),
(50,50,50,'2024-03-01');


-- ============================================================================
-- PARTE 1: EXERCÍCIOS BÁSICOS (1 a 25)
-- ============================================================================

-- 1. Liste todos os dados da tabela CLIENTE.
SELECT * 
FROM CLIENTE;

-- 2. Liste apenas nome e idade dos clientes.
SELECT nome, idade 
FROM CLIENTE;