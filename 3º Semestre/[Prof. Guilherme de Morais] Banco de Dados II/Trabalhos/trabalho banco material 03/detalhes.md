# Trabalho: trabalho banco material 03

> **Professor:** Prof. Guilherme de Morais
> **Disciplina:** Banco de Dados II (3º Semestre)
> **Prazo de Entrega:** 05/03/2026 às 01:30
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** `INSERT` (com e sem declaração de colunas, valores omitidos), `UPDATE` (com e sem `WHERE`, múltiplas colunas, `IS NULL`), `DELETE` (com e sem `WHERE`)

## Descrição da atividade

Bateria de 20 exercícios de manipulação de dados (DML) sobre uma única tabela `FUNCIONARIO`, aprofundando o conteúdo de [Aula 03 — INSERT, UPDATE e DELETE](../../Aulas/aula%2003%20-%20insert%20delete%20e%20update%2C%20aula%2004%20consultado/detalhes.md). A resolução completa e comentada está em [`solucao_trabalho_banco_material_03.sql`](./solucao_trabalho_banco_material_03.sql).

## Estrutura do banco de apoio

```sql
CREATE TABLE FUNCIONARIO (
    cpf INTEGER NOT NULL,
    nome VARCHAR(50),
    funcao VARCHAR(30),
    salario DECIMAL(10,2),
    data_nasc DATE,
    departamento VARCHAR(30),
    CONSTRAINT pk_fun_cpf PRIMARY KEY (cpf)
);
```

## Enunciado — INSERT (7 exercícios)

1. Insira um funcionário com todos os campos preenchidos.
2. Insira três funcionários em departamentos diferentes utilizando a sintaxe com declaração explícita das colunas.
3. Insira um funcionário informando apenas `cpf`, `nome` e `data_nasc`.
4. Cadastre um funcionário cujo salário seja `8750.90` e função `"ANALISTA DE SISTEMAS"`.
5. Insira dois funcionários com a mesma função, porém salários diferentes.
6. Cadastre cinco funcionários do departamento `"TI"`.
7. Insira um registro omitindo propositalmente o campo `funcao`.

## Enunciado — UPDATE (7 exercícios)

8. Atualize o salário de um funcionário específico pelo CPF.
9. Atualize a função de todos os funcionários do departamento `"TI"` para `"DESENVOLVEDOR"`.
10. Aumente em 10% o salário de todos os funcionários.
11. Altere o departamento de um funcionário específico.
12. Atualize a função e o salário simultaneamente de um funcionário.
13. Defina o salário como `0` para funcionários que não possuem salário cadastrado.
14. Altere a função para `"GERENTE"` apenas para funcionários com salário superior a 8000.

## Enunciado — DELETE (6 exercícios)

15. Exclua um funcionário específico pelo CPF.
16. Exclua todos os funcionários do departamento `"RH"`.
17. Remova funcionários com salário inferior a 1500.
18. Apague os registros de funcionários cuja função esteja vazia.
19. Exclua todos os funcionários nascidos antes de 1980.
20. Apague todos os registros da tabela.

<details>
<summary>Gabarito</summary>

```sql
-- 1.
INSERT INTO FUNCIONARIO VALUES (
    111111111, 'João Silva', 'Analista de Suporte', 4500.00, '1990-05-15', 'Suporte Técnico'
);

-- 2.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES
(222222222, 'Maria Souza', 'Gerente de Vendas', 8500.00, '1985-08-20', 'Vendas'),
(333333333, 'Pedro Santos', 'Analista de RH', 3800.00, '1995-03-10', 'RH'),
(444444444, 'Ana Oliveira', 'Contadora', 5200.00, '1992-11-12', 'Financeiro');

-- 3.
INSERT INTO FUNCIONARIO (cpf, nome, data_nasc)
VALUES (555555555, 'Carlos Lima', '1988-07-25');

-- 4.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES (666666666, 'Beatriz Costa', 'ANALISTA DE SISTEMAS', 8750.90, '1991-01-30', 'TI');

-- 5.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES
(777777777, 'Lucas Rocha', 'Suporte', 2000.00, '1997-04-05', 'TI'),
(888888888, 'Juliana Alves', 'Suporte', 2800.00, '1996-09-18', 'TI');

-- 6.
INSERT INTO FUNCIONARIO (cpf, nome, funcao, salario, data_nasc, departamento)
VALUES
(999999991, 'Fernanda Dias', 'Desenvolvedor', 6000.00, '1993-02-14', 'TI'),
(999999992, 'Ricardo Melo', 'Desenvolvedor', 6200.00, '1994-06-22', 'TI'),
(999999993, 'Camila Cruz', 'Estagiário', 1200.00, '2001-10-05', 'TI'),
(999999994, 'Gabriel Neves', 'Coordenador', 9000.00, '1982-12-01', 'TI'),
(999999995, 'Amanda Reis', 'DBA', 7500.00, '1989-08-15', 'TI');

-- 7.
INSERT INTO FUNCIONARIO (cpf, nome, salario, data_nasc, departamento)
VALUES (123456789, 'Roberto Carlos', 3100.00, '1975-04-19', 'Marketing');

-- 8.
UPDATE FUNCIONARIO SET salario = 5500.00 WHERE cpf = 111111111;

-- 9.
UPDATE FUNCIONARIO SET funcao = 'DESENVOLVEDOR' WHERE departamento = 'TI';

-- 10.
UPDATE FUNCIONARIO SET salario = salario * 1.10;

-- 11.
UPDATE FUNCIONARIO SET departamento = 'Diretoria' WHERE cpf = 222222222;

-- 12.
UPDATE FUNCIONARIO SET funcao = 'Coordenador de TI', salario = 10500.00
WHERE cpf = 999999994;

-- 13.
UPDATE FUNCIONARIO SET salario = 0.00 WHERE salario IS NULL;

-- 14.
UPDATE FUNCIONARIO SET funcao = 'GERENTE' WHERE salario > 8000.00;

-- 15.
DELETE FROM FUNCIONARIO WHERE cpf = 555555555;

-- 16.
DELETE FROM FUNCIONARIO WHERE departamento = 'RH';

-- 17.
DELETE FROM FUNCIONARIO WHERE salario < 1500;

-- 18.
DELETE FROM FUNCIONARIO WHERE funcao = '' OR funcao IS NULL;

-- 19.
DELETE FROM FUNCIONARIO WHERE data_nasc < '1980-01-01';

-- 20.
DELETE FROM FUNCIONARIO;
```

**Notas acadêmicas do gabarito:**
- Na inserção implícita (sem declarar colunas, exercício 1), a ordem dos valores deve seguir exatamente a ordem de definição das colunas na tabela.
- Campos omitidos na inserção (exercício 3) assumem `NULL`, pois não têm restrição `NOT NULL` nem valor `DEFAULT`.
- Comparar com valores nulos exige o operador `IS NULL` (exercício 13) — `salario = NULL` nunca é verdadeiro em SQL.
- `UPDATE`/`DELETE` sem `WHERE` (exercícios 10 e 20) afetam **toda** a tabela.
</details>

## Arquivos entregues

- [`trabalho banco materiai 03.docx`](./trabalho%20banco%20materiai%2003.docx) — enunciado original com os 20 exercícios.
- [`solucao_trabalho_banco_material_03.sql`](./solucao_trabalho_banco_material_03.sql) — gabarito oficial completo e comentado.

## Material relacionado

- [Aula 03/04 — INSERT, DELETE, UPDATE e SELECT básico](../../Aulas/aula%2003%20-%20insert%20delete%20e%20update%2C%20aula%2004%20consultado/detalhes.md)
- [Trabalho: Elaborar um Banco de Dados](../ELABORAR%20UM%20BANCO%20DE%20DADOS/detalhes.md)
