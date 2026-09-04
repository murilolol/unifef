# Prova / Avaliação — Exercícios SubSelects (parte 2)

> **Professor:** Welington Garcia
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)
> **Prazo de entrega:** 03/09/2026 às 02:59
> **Pontuação máxima:** 100 pontos
> **Escopo:** conteúdo complementar de subconsultas — tabelas derivadas no `FROM`, subconsultas combinadas com `JOIN`, agregações em subconsultas, subconsultas aninhadas, `INSERT INTO ... SELECT`, `UPDATE`/`DELETE` com subconsulta, subconsulta correlacionada e `EXPLAIN ANALYZE`.

## Descrição da atividade

Continuação avançada da avaliação de subselects sobre o mesmo banco de dados da parte 1. Esta parte introduz subconsultas correlacionadas em cenários de escrita (`INSERT`/`UPDATE`/`DELETE`) e o uso de operadores avançados (`EXISTS`, `NOT EXISTS`), testando a performance e o raciocínio lógico em cenários de banco de dados corporativos.

## Lista de exercícios (10)

1. **Subconsulta no `FROM` — média por categoria.** Calcule, numa subconsulta no `FROM`, o preço médio dos produtos de cada categoria; na consulta externa, exiba apenas as categorias com média de preços superior a R$ 1.000,00.
2. **Tabela derivada — total de cada pedido.** Subconsulta no `FROM` com `SUM(quantidade * preco_unitario)`; exiba apenas pedidos com valor total superior a R$ 3.000,00.
3. **Subconsulta no `FROM` combinada com `JOIN`.** A partir do total por pedido (tabela derivada), relacione com `pedidos` e `clientes` para exibir código do pedido, nome do cliente, data e valor total.
4. **Clientes que gastaram acima da média.** Calcule o total gasto por cliente e exiba apenas os que gastaram acima da média de gastos de todos os clientes que compraram.
5. **Categoria com maior preço médio.** Combine uma subconsulta no `FROM` (médias por categoria) com outra subconsulta para identificar o maior valor entre elas.
6. **`INSERT` utilizando subconsulta.** Crie a tabela `produtos_promocao` (`id_produto`, `nome_produto`, `preco`, `preco_promocional`) e, com `INSERT INTO ... SELECT`, insira os produtos com preço acima da média geral, aplicando 10% de desconto no `preco_promocional`.
7. **`UPDATE` utilizando subconsulta.** Aumente em 10% o limite de crédito dos clientes que já realizaram pelo menos dois pedidos (identificados por subconsulta).
8. **`UPDATE` baseado na média da própria categoria.** Some 5 unidades ao estoque dos produtos cujo preço seja superior à média de preço da própria categoria, via subconsulta correlacionada.
9. **`DELETE` utilizando `NOT EXISTS`.** Remova os clientes que nunca realizaram pedidos. Antes do `DELETE`, escreva um `SELECT` equivalente para conferir os registros afetados.
10. **Análise de desempenho com `EXPLAIN ANALYZE`.** Escreva duas consultas equivalentes para "clientes com pedidos" — uma com `IN`, outra com `EXISTS` — e compare os planos de execução (custo estimado, linhas, tempo, operações).

**Observação:** utiliza o mesmo banco de dados criado na parte 1 (ver [detalhes da parte 1](../Exercicios%20SubSelects%20-%20parte%2001/detalhes.md)). Antes de qualquer `UPDATE`/`DELETE`, o enunciado recomenda executar o `SELECT` equivalente para conferir os registros afetados.

## Arquivos entregues

- [`Lista_Exercicios_Subconsultas_PostgreSQL_parte2.docx`](./Lista_Exercicios_Subconsultas_PostgreSQL_parte2.docx) — enunciado original completo.
- [`resolucao_subconsultas_parte2.sql`](./resolucao_subconsultas_parte2.sql) — resolução comentada dos 10 exercícios (inclui os `CREATE TABLE`/`INSERT`s de apoio).

## Material relacionado

- [Prova — parte 1 (subconsultas escalares, `IN`/`NOT IN`, `EXISTS`, `ANY`/`ALL`, correlacionadas)](../Exercicios%20SubSelects%20-%20parte%2001/detalhes.md)
- [Aula: Joins e Subconsultas](../../Aulas/Aulas%20Joins%20e%20Sub%20selects/detalhes.md)
