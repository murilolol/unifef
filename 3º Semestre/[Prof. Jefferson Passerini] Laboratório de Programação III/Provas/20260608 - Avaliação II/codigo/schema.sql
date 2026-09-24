-- Disciplina: Laboratório de Programação III (3º Semestre)
-- Professor: Prof. Jefferson Passerini
-- Tema: Avaliação II - Script DDL e DML Idempotente
-- Como executar: sqlite3 avaliacao2.db < schema.sql  OU  mysql -u root -p < schema.sql

-- Exercício 2: Estrutura da Tabela tb_produto
CREATE TABLE IF NOT EXISTS tb_produto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    quantidade_estoque INT NOT NULL,
    data_cadastro VARCHAR(20) NOT NULL
);

-- Carga de dados iniciais para testes de consulta
DELETE FROM tb_produto WHERE id IN (1, 2, 3);

INSERT INTO tb_produto (id, nome, preco, quantidade_estoque, data_cadastro) VALUES
(1, 'Notebook Dell Inspiron', 4250.00, 10, '2026-06-08'),
(2, 'Mouse Sem Fio Logitech', 129.90, 45, '2026-06-08'),
(3, 'Teclado Mecânico Redragon', 289.00, 20, '2026-06-08');
