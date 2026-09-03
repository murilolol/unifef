# 📄 CHEAT SHEET: Tópicos Avançados em Banco de Dados
**Professor:** Welington Garcia | **Escopo:** JOINs e Subconsultas em SQL (PostgreSQL)

---

## 1. Guia Visual e Sintaxe dos JOINs
Os JOINs combinam dados de duas ou mais tabelas com base em uma condição de relacionamento (chaves primárias e estrangeiras).

| Tipo de JOIN | Comportamento Principal | O que ocorre se não houver correspondência? |
| :--- | :--- | :--- |
| **`INNER JOIN`** | Retorna **apenas** a interseção. | A linha é **descartada** em ambos os lados. |
| **`LEFT JOIN`** | Retorna tudo da esquerda + correspondências da direita. | Colunas da direita recebem **`NULL`**. |
| **`RIGHT JOIN`** | Retorna tudo da direita + correspondências da esquerda. | Colunas da esquerda recebem **`NULL`**. |
| **`FULL OUTER JOIN`** | Retorna tudo de **ambas** as tabelas. | Lados sem correspondência recebem **`NULL`**. |
| **`CROSS JOIN`** | Produto cartesiano (todas as combinações possíveis). | Multiplica linhas ($N \times M$). **Cuidado com tabelas grandes!** |
| **`SELF JOIN`** | Une a tabela com ela mesma (ex: hierarquia). | Exige uso obrigatório de **Aliases** diferentes. |

### 📌 Sintaxe Padrão de um JOIN Múltiplo com Filtros e Agregação
```sql
SELECT 
    p.id_pedido, 
    c.nome AS cliente, 
    SUM(ip.quantidade * ip.preco_unitario) AS valor_total
FROM pedidos p
INNER JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
WHERE p.status = 'Pago'              -- Filtro pós-junção
GROUP BY p.id_pedido, c.nome         -- Agrupamento obrigatório
ORDER BY valor_total DESC;           -- Ordenação
```

---

## 2. Ponto Crítico: `ON` vs `WHERE` em LEFT JOIN
A posição do filtro altera totalmente o resultado da consulta:

* **Filtro no `WHERE`**: Transforma um `LEFT JOIN` em um `INNER JOIN` disfarçado, pois descarta os valores `NULL` gerados pela esquerda.
  ```sql
  -- Filtra os nulos gerados, eliminando clientes sem pedidos pagos
  SELECT * FROM clientes c LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente WHERE p.status = 'Pago';
  ```
* **Filtro no `ON`**: Mantém todos os registros da tabela da esquerda e restringe apenas o lado direito da associação.
  ```sql
  -- Mantém TODOS os clientes, mas só traz dados do pedido se estiver 'Pago'
  SELECT * FROM clientes c LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente AND p.status = 'Pago';
  ```

---

## 3. Padrões Úteis e Funções Auxiliares

* **Encontrar registros órfãos (ex: Clientes sem pedidos):**
  ```sql
  SELECT c.id_cliente, c.nome 
  FROM clientes c 
  LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente 
  WHERE p.id_pedido IS NULL;
  ```
* **Tratamento de `NULL` com `COALESCE`:**
  ```sql
  SELECT pr.nome_produto, COALESCE(ca.nome_categoria, 'Sem categoria') AS categoria 
  FROM produtos pr 
  LEFT JOIN categorias ca ON pr.id_categoria = ca.id_categoria;
  ```
* **Atalhos (Evite em produção por falta de clareza):**
  * `USING (id_cliente)`: Usado quando a coluna possui o **mesmo nome** exato em ambas as tabelas.
  * `NATURAL JOIN`: Une automaticamente tabelas por colunas de nomes iguais (Altamente **não recomendado** em sistemas reais).

---

## 4. Conceitos Essenciais para Prova
1. **Chave Primária (`PRIMARY KEY`)**: Identifica unicamente cada registro na tabela.
2. **Chave Estrangeira (`FOREIGN KEY`)**: Cria a integridade referencial apontando para a PK de outra tabela, impedindo registros órfãos na inserção.
3. **Regra do `GROUP BY`**: Tudo o que está no `SELECT` e **não** faz parte de uma função de agregação (`SUM`, `COUNT`, `AVG`, `MAX`, `MIN`) **deve obrigatoriamente** constar na cláusula `GROUP BY`.
4. **Aliases (`AS`)**: Essenciais para legibilidade, economia de código e obrigatórios em `SELF JOIN` para diferenciar as instâncias da mesma tabela (ex: `funcionarios f` e `funcionarios s`).