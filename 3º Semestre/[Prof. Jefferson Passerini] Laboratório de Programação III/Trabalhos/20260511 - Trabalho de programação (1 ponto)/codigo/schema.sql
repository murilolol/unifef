-- ============================================================================
-- Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
-- Professor:  Prof. Jefferson Passerini
-- Tema:       Java JSP Cap 5.4 - Desafio 02: Cadastro de Estado
-- Exercício:  Exercício 1
-- Execução:   mysql -u root -p < schema.sql
-- ============================================================================

-- Criação do banco de dados da aplicação
CREATE DATABASE IF NOT EXISTS lp3_projeto CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lp3_projeto;

-- Criação da tabela estado
CREATE TABLE IF NOT EXISTS estado (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nome_estado VARCHAR(50) NOT NULL,
    sigla_estado CHAR(2) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Carga inicial de dados de exemplo (idempotente)
INSERT INTO estado (id_estado, nome_estado, sigla_estado) VALUES
(1, 'São Paulo', 'SP'),
(2, 'Minas Gerais', 'MG'),
(3, 'Rio de Janeiro', 'RJ'),
(4, 'Espírito Santo', 'ES'),
(5, 'Paraná', 'PR'),
(6, 'Santa Catarina', 'SC'),
(7, 'Rio Grande do Sul', 'RS'),
(8, 'Bahia', 'BA'),
(9, 'Goiás', 'GO'),
(10, 'Mato Grosso do Sul', 'MS')
ON DUPLICATE KEY UPDATE 
    nome_estado = VALUES(nome_estado),
    sigla_estado = VALUES(sigla_estado);
