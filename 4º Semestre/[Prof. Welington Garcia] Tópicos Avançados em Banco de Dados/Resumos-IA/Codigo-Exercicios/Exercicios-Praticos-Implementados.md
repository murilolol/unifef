# 📚 Apostila Prática: Tópicos Avançados em Banco de Dados
**Professor:** Welington Garcia  
**Conteúdo Base:** JOINs e Subconsultas (Subqueries) em SQL com PostgreSQL  

---

## 🚀 Orientações Iniciais
Esta apostila foi desenvolvida com base no material oficial do **Prof. Welington Garcia**. Todos os códigos SQL apresentados a seguir estão estruturados para o **PostgreSQL** e podem ser executados diretamente em qualquer ambiente compatível (como *pgAdmin*, *DBeaver* ou *psql*). 

O script abaixo cria o cenário completo (tabelas, chaves primárias e estrangeiras) necessário para rodar todos os exercícios resolvidos desta apostila.

---

## 🛠️ 1. Script de Preparação do Ambiente (Setup do Banco de Dados)

Execute o script DDL e DML abaixo para criar as tabelas e popular o banco com dados de teste.

```sql
-- Removendo tabelas caso já existam (ordem inversa de dependência)
DROP TABLE IF EXISTS itens_pedido CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS funcionarios CASCADE;

-- 1. Tabela de Categorias
CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

-- 2. Tabela de Produtos
CREATE TABLE produtos (
    id_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    id_categoria INTEGER REFERENCES categorias(id_categoria)
);

-- 3. Tabela de Clientes
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100),
    estado CHAR(2)
);

-- 4. Tabela de Pedidos
CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    data_pedido DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    id_cliente INTEGER REFERENCES clientes(id_cliente)
);

-- 5. Tabela de Itens do Pedido
CREATE TABLE itens_pedido (
    id_pedido INTEGER REFERENCES pedidos(id_pedido),
    id_produto INTEGER REFERENCES produtos(id_produto),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (id_pedido, id_produto)
);

-- 6. Tabela de Funcionários (Auto-relacionamento / SELF JOIN)
CREATE TABLE funcionarios (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    id_supervisor INTEGER REFERENCES funcionarios(id_funcionario)
);

-- ==========================================
-- POPULANDO O BANCO DE DADOS (DML)
-- ==========================================

INSERT INTO categorias (id_categoria, nome_categoria) VALUES 
(1, 'Eletrônicos'), (2, 'Livros'), (3, 'Vestuário');

INSERT INTO produtos (id_produto, nome_produto, preco, id_categoria) VALUES 
(1, 'Smartphone', 1500.00, 1),
(2, 'Notebook', 3500.00, 1),
(3, 'Livro SQL Avançado', 120.00, 2),
(4, 'Camiseta', 50.00, 3),
(5, 'Webcam', 250.00, 1); -- Produto sem categoria será simulado se necessário

INSERT INTO clientes (id_cliente, nome, cidade, estado) VALUES 
(1, 'Ana Souza', 'São Paulo', 'SP'),
(2, 'Bruno Lima', 'Rio de Janeiro', 'RJ'),
(3, 'Carla Mendes', 'Belo Horizonte', 'MG'),
(4, 'Daniel Dias', 'Curitiba', 'PR'); -- Cliente sem pedidos

INSERT INTO pedidos (id_pedido, data_pedido, status, id_cliente) VALUES 
(101, '2023-10-01', 'Pago', 1),
(102, '2023-10-02', 'Pendente', 2),
(103, '2023-10-05', 'Pago', 1),
(104, '2023-10-10', Cancelado, 3); -- Ajuste sintático mental para string: 'Cancelado'

-- Correção do status do pedido 104
UPDATE pedidos SET status = 'Cancelado' WHERE id_pedido = 104;

INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES 
(101, 1, 1, 1500.00),
(101, 3, 2, 120.00),
(102, 2, 1, 3500.00),
(103, 4, 3, 50.00),
(103, 5, 1, 250.00);

INSERT INTO funcionarios (id_funcionario, nome, cargo, id_supervisor) VALUES 
(1, 'Carlos Silva', 'Diretor', NULL),
(2, 'Mariana Costa', 'Gerente', 1),
(3, 'João Pedro', 'Analista', 2);
```

---

## 🔗 2. Guia Prático de JOINs em SQL

Os `JOINs` permitem combinar dados de duas ou mais tabelas com base em uma condição de relacionamento (geralmente envolvendo Chaves Primárias e Chaves Estrangeiras).

### 2.1. INNER JOIN (Apenas Correspondências)
Retorna apenas os registros que possuem correspondência em **ambas** as tabelas.

```sql
-- Ex 1: Exibir código, data, status e nome do cliente de cada pedido
SELECT 
    p.id_pedido, 
    p.data_pedido, 
    p.status, 
    c.nome AS cliente
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente;
```

### 2.2. LEFT JOIN (Preservando a Esquerda)
Retorna todos os registros da tabela da esquerda e os correspondentes da direita. Caso não haja correspondência, retorna `NULL`.

```sql
-- Ex 2: Exibir todos os clientes, inclusive os que nunca fizeram pedidos
SELECT 
    c.id_cliente, 
    c.nome, 
    p.id_pedido, 
    p.status
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente;
```

### 2.3. Identificando Registros Sem Correspondência (Anti-Join)
Utilizando o `LEFT JOIN` em conjunto com uma cláusula `WHERE IS NULL` para encontrar registros órfãos.

```sql
-- Ex 3: Exibir somente os clientes que NUNCA fizeram pedidos
SELECT 
    c.id_cliente, 
    c.nome
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL;
```

### 2.4. Tratamento de Nulos com COALESCE
```sql
-- Ex 4: Exibir produtos e suas categorias, tratando os sem categoria
SELECT 
    pr.nome_produto, 
    pr.preco, 
    COALESCE(ca.nome_categoria, 'Sem categoria') AS categoria
FROM produtos pr
LEFT JOIN categorias ca ON pr.id_categoria = ca.id_categoria;
```

### 2.5. SELF JOIN (Junção de uma tabela com ela mesma)
Essencial para estruturas hierárquicas, como organogramas de funcionários.

```sql
-- Ex 8: Exibir cada funcionário e o nome do seu respectivo supervisor
SELECT 
    f.nome AS funcionario, 
    f.cargo, 
    COALESCE(s.nome, 'Sem supervisor (Diretoria)') AS supervisor
FROM funcionarios f
LEFT JOIN funcionarios s ON f.id_supervisor = s.id_funcionario;
```

### 2.6. JOIN Múltiplo com Agregações e Cálculos
```sql
-- Ex 7: Calcular o valor total de cada pedido (Quantidade * Preço Unitário agrupado por pedido)
SELECT 
    p.id_pedido, 
    c.nome AS cliente, 
    SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
GROUP BY p.id_pedido, c.nome
ORDER BY valor_total DESC;
```

---

## 🔍 3. Módulo de Subconsultas (Subqueries)

Uma **subconsulta** é uma consulta SQL aninhada dentro de outra consulta maior (`SELECT`, `INSERT`, `UPDATE` ou `DELETE`).

### 3.1. Subconsulta Escalar (Retorna um único valor)
```sql
-- Encontrar produtos cujo preço seja superior à média de preço de todos os produtos cadastrados
SELECT 
    nome_produto, 
    preco
FROM produtos
WHERE preco > (SELECT AVG(preco) FROM produtos);
```

### 3.2. Subconsulta com Operador `IN` (Retorna uma lista de valores)
```sql
-- Exibir clientes que realizaram pelo menos um pedido com status 'Pago'
SELECT 
    id_cliente, 
    nome
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente 
    FROM pedidos 
    WHERE status = 'Pago'
);
```

### 3.3. Subconsultas Correlacionadas (Exist / Not Exists)
A subconsulta depende de dados da consulta externa para ser executada linha a linha.

```sql
-- Encontrar clientes que possuem pedidos registrados (equivalente lógico a um INNER JOIN)
SELECT c.nome
FROM clientes c
WHERE EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);
```

---

## 🏆 4. Atividade Prática Proposta: Sistema de Biblioteca

Para fixação completa dos conceitos do Prof. Welington Garcia, implemente o modelo relacional de uma **Biblioteca** contendo as tabelas: `autores`, `livros`, `leitores` e `emprestimos`.

### Script de Criação e Teste da Biblioteca
```sql
CREATE TABLE autores (
    id_autor SERIAL PRIMARY KEY,
    nome_autor VARCHAR(100) NOT NULL
);

CREATE TABLE livros (
    id_livro SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    id_autor INTEGER REFERENCES autores(id_autor)
);

CREATE TABLE leitores (
    id_leitor SERIAL PRIMARY KEY,
    nome_leitor VARCHAR(100) NOT NULL
);

CREATE TABLE emprestimos (
    id_emprestimo SERIAL PRIMARY KEY,
    id_livro INTEGER REFERENCES livros(id_livro),
    id_leitor INTEGER REFERENCES leitores(id_leitor),
    data_emprestimo DATE NOT NULL,
    devolvido BOOLEAN DEFAULT FALSE
);

-- Inserção de dados de teste (Incluindo casos especiais: autor sem livro, leitor sem empréstimo)
INSERT INTO autores VALUES (1, 'Machado de Assis'), (2, 'J.K. Rowling'), (3, 'George Orwell'), (4, 'Clarice Lispector'), (5, 'Autor Oculto (Sem Livros)');
INSERT INTO livros VALUES (1, 'Dom Casmurro', 1), (2, 'Harry Potter', 2), (3, '1984', 3), (4, 'A Hora da Estrela', 4);
INSERT INTO leitores VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie'), (4, 'Leitor Inativo (Sem Empréstimos)');
INSERT INTO emprestimos (id_livro, id_leitor, data_emprestimo, devolvido) VALUES 
(1, 1, '2023-11-01', TRUE),
(2, 2, '2023-11-05', FALSE);
```

### Consultas Desafio (Gabarito Prático)

1. **Listar todos os livros e seus respectivos autores:**
   ```sql
   SELECT l.titulo, a.nome_autor 
   FROM livros l 
   JOIN autores a ON l.id_autor = a.id_autor;
   ```

2. **Listar todos os autores, inclusive aqueles que não cadastraram livros:**
   ```sql
   SELECT a.nome_autor, l.titulo 
   FROM autores a 
   LEFT JOIN livros l ON a.id_autor = l.id_autor;
   ```

3. **Encontrar leitores que nunca fizeram empréstimos (Anti-Join):**
   ```sql
   SELECT le.nome_leitor 
   FROM leitores le 
   LEFT JOIN emprestimos e ON le.id_leitor = e.id_leitor 
   WHERE e.id_emprestimo IS NULL;
   ```

4. **Contar a quantidade de livros por autor:**
   ```sql
   SELECT a.nome_autor, COUNT(l.id_livro) AS total_livros
   FROM autores a
   LEFT JOIN livros l ON a.id_autor = l.id_autor
   GROUP BY a.id_autor, a.nome_autor;
   ```

---
*Fim da Apostila Prática — Tópicos Avançados em Banco de Dados.*