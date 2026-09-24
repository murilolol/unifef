-- Disciplina: Laboratório de Programação III
-- Tema: Conexão com Banco de Dados e Servlet Filters
-- Como executar: Execute no pgAdmin Query Tool ou via linha de comando no PostgreSQL.

DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    datanascimento DATE NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(20) NOT NULL,
    salario DECIMAL(15,2) NOT NULL
);

INSERT INTO usuario (nome, datanascimento, cpf, email, senha, salario)
VALUES ('João José Gomes da Silva', '1990-08-10', '08243060073', 'joaojosegomes@gmail.com', 'senha123', 5200.00);

SELECT * FROM usuario;
