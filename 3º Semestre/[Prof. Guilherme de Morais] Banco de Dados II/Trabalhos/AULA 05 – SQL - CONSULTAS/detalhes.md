# Trabalho: AULA 05 – SQL - CONSULTAS

> **Professor:** Prof. Guilherme de Morais
> **Disciplina:** Banco de Dados II (3º Semestre)
> **Prazo de Entrega:** 12/03/2026 às 01:40
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** `LIKE`, `ILIKE`, curingas (`%`, `_`), funções agregadas (`AVG`, `COUNT`, `MAX`, `MIN`, `SUM`)

## Descrição da atividade

Lista de 20 exercícios práticos (10 de padrões de texto com `LIKE`/`ILIKE` e 10 de funções agregadas) sobre o banco `clientes`/`veiculos` apresentado na [Aula 06 — Comando IN](../../Aulas/COMANDO%20IN%20E%20SQL%20MAIS%20COMPLEXAS/detalhes.md). O script de setup completo (DDL + `INSERT`s) está em [`aula05_consultas_sql.sql`](./aula05_consultas_sql.sql).

## Estrutura do banco de apoio

| Tabela | Finalidade |
| :--- | :--- |
| `clientes` | Cadastro de clientes (cpf, nome, endereço, cidade, estado, salário, idade) |
| `veiculos` | Cadastro de veículos, cada um vinculado a um cliente por `cpf_cli` |

## Enunciado — Parte 1: LIKE / ILIKE (10 exercícios)

1. Selecione todos os clientes cujo nome começa com a letra `A`.
2. Liste os clientes cujo nome contém a letra `"i"` em qualquer posição.
3. Selecione os clientes cujo nome termina com a letra `"a"`.
4. Liste os clientes cujo nome tenha exatamente 5 caracteres.
5. Selecione os clientes cujo nome começa com `"Ma"` e termina com `"a"`.
6. Liste os veículos cuja cor seja `BRANCO`.
7. Selecione os veículos cujo modelo contenha `"O"`.
8. Liste os veículos cuja placa termine com `"9"`.
9. Selecione os clientes cujo endereço contenha `"RUA 01"`.
10. Repita o exercício 2 usando `ILIKE` para não diferenciar maiúsculas e minúsculas.

## Enunciado — Parte 2: Funções Agregadas (10 exercícios)

11. Calcule a média dos salários dos clientes.
12. Conte quantos clientes existem na tabela.
13. Conte quantos clientes moram na cidade de `FERNANDÓPOLIS`.
14. Selecione o maior salário entre os clientes.
15. Selecione o menor salário entre os clientes.
16. Calcule a soma dos salários de todos os clientes.
17. Selecione o maior preço de venda dos veículos.
18. Selecione o menor preço de compra dos veículos.
19. Liste a média salarial por cidade.
20. Mostre o maior e menor preço de venda por marca de veículo.

<details>
<summary>Gabarito</summary>

```sql
-- 1.
SELECT * FROM clientes WHERE nome LIKE 'A%';

-- 2.
SELECT * FROM clientes WHERE nome LIKE '%i%';

-- 3.
SELECT * FROM clientes WHERE nome LIKE '%a';

-- 4.
SELECT * FROM clientes WHERE nome LIKE '_____';

-- 5.
SELECT * FROM clientes WHERE nome LIKE 'Ma%a';

-- 6.
SELECT * FROM veiculos WHERE cor = 'BRANCO';

-- 7.
SELECT * FROM veiculos WHERE modelo LIKE '%O%';

-- 8.
SELECT * FROM veiculos WHERE placa LIKE '%9';

-- 9.
SELECT * FROM clientes WHERE endereco LIKE '%RUA 01%';

-- 10.
SELECT * FROM clientes WHERE nome ILIKE '%i%';

-- 11.
SELECT AVG(salario) FROM clientes;

-- 12.
SELECT COUNT(*) FROM clientes;

-- 13.
SELECT COUNT(*) FROM clientes WHERE cidade = 'FERNANDOPOLIS';

-- 14.
SELECT MAX(salario) FROM clientes;

-- 15.
SELECT MIN(salario) FROM clientes;

-- 16.
SELECT SUM(salario) FROM clientes;

-- 17.
SELECT MAX(preco_venda) FROM veiculos;

-- 18.
SELECT MIN(preco_compra) FROM veiculos;

-- 19.
SELECT cidade, AVG(salario) AS media_salarial
FROM clientes
GROUP BY cidade;

-- 20.
SELECT marca, MAX(preco_venda) AS maior_preco, MIN(preco_venda) AS menor_preco
FROM veiculos
GROUP BY marca;
```
</details>

## Arquivos entregues

- [`create table clientes.docx`](./create%20table%20clientes.docx) — enunciado original com o DDL/DML das tabelas `clientes` e `veiculos`.
- [`exercicios da aula 5.docx`](./exercicios%20da%20aula%205.docx) / [`exercicios da aula 5.pdf`](./exercicios%20da%20aula%205.pdf) — enunciado original dos 20 exercícios.
- [`aula05_consultas_sql.sql`](./aula05_consultas_sql.sql) — script completo de setup (DDL + `INSERT`s) do banco de apoio.

## Material relacionado

- [Aula 05 — LIKE/ILIKE e Funções Agregadas](../../Aulas/AULA%2005%20–%20SQL%20-%20CONSULTAS/detalhes.md)
- [Aula 06 — Operador IN e Consultas com Múltiplas Tabelas](../../Aulas/COMANDO%20IN%20E%20SQL%20MAIS%20COMPLEXAS/detalhes.md) — schema `clientes`/`veiculos` usado neste trabalho.
