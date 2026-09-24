# Trabalho — Exerciciso - SUbSelect - parte 2

> **Professor:** Welington Garcia
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)
> **Prazo de Entrega:** 02/09/2026 às 23:59
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** [Aula 01 - Consultas Avançadas com Joins e Subselects](../../Aulas/Aula%2001%20-%20Consultas%20Avan%C3%A7adas%20com%20Joins%20e%20Subselects/detalhes.md), [Aula 02 - Views e Materialized Views em PostgreSQL](../../Aulas/Aula%2002%20-%20Views%20e%20Materialized%20Views%20em%20PostgreSQL/detalhes.md), [Aula 03 - Stored Procedures e Programacao PL pgSQL](../../Aulas/Aula%2003%20-%20Stored%20Procedures%20e%20Programacao%20PL%20pgSQL/detalhes.md)

## Sumário
1. [Enunciado original (Google Classroom)](#enunciado-original-google-classroom)
2. [Análise do que é pedido](#análise-do-que-é-pedido)
3. [Fundamentação teórica](#fundamentação-teórica)
4. [Resolução proposta](#resolução-proposta)
5. [Como testar e validar](#como-testar-e-validar)
6. [Critérios de qualidade](#critérios-de-qualidade)
7. [Arquivos de apoio](#arquivos-de-apoio)
8. [Mapa da atividade](#mapa-da-atividade)
9. [Glossário](#glossário)
10. [Pontos-chave para a prova](#pontos-chave-para-a-prova)
11. [Perguntas e respostas (JSONL)](#perguntas-e-respostas-jsonl)
12. [Checklist de revisão](#checklist-de-revisão)

## Enunciado original (Google Classroom)

BANCO DE DADOS
Lista de Exercícios — Subconsultas Avançadas no PostgreSQL
Conteúdo complementar da aula de Subselects / Subconsultas

Objetivo: praticar subconsultas em níveis mais avançados, envolvendo tabelas derivadas, JOINs, agregações, manipulação de dados e análise de desempenho.

### 1. Orientações
- Utilize o mesmo banco de dados criado para a aula de subconsultas.
- Resolva os exercícios utilizando recursos de subconsultas sempre que solicitado.
- Antes de comandos UPDATE ou DELETE, recomenda-se executar um SELECT equivalente para conferir quais registros serão afetados.
- Nos exercícios que utilizarem agregações, observe corretamente o uso de SUM, AVG, GROUP BY e aliases.
- Quando solicitado, utilize EXPLAIN ANALYZE para observar o plano de execução gerado pelo PostgreSQL.

### 2. Conteúdos trabalhados
- Subconsultas no FROM (tabelas derivadas)
- Subconsultas combinadas com JOIN
- Agregações em subconsultas
- Subconsultas aninhadas
- INSERT INTO ... SELECT
- UPDATE com subconsulta
- DELETE com NOT EXISTS
- Subconsulta correlacionada
- EXPLAIN ANALYZE
- Comparação entre IN e EXISTS

### 3. Lista de exercícios
- **Exercício 1 — Subconsulta no FROM: média por categoria:** Crie uma consulta que calcule, em uma subconsulta no FROM, o preço médio dos produtos de cada categoria. Na consulta externa, exiba apenas as categorias cuja média de preços seja superior a R$ 1.000,00.
- **Exercício 2 — Tabela derivada: total de cada pedido:** Crie uma subconsulta no FROM que calcule o valor total de cada pedido utilizando SUM(quantidade * preco_unitario). Na consulta externa, exiba apenas os pedidos cujo valor total seja superior a R$ 3.000,00.
- **Exercício 3 — Subconsulta no FROM combinada com JOIN:** Utilizando uma tabela derivada, calcule o valor total de cada pedido. Em seguida, relacione o resultado com as tabelas pedidos e clientes para exibir: código do pedido, nome do cliente, data do pedido e valor total.
- **Exercício 4 — Clientes que gastaram acima da média:** Calcule o total gasto por cada cliente utilizando uma subconsulta. Depois, exiba apenas os clientes cujo total gasto seja superior à média de gastos de todos os clientes que realizaram compras.
- **Exercício 5 — Categoria com maior preço médio:** Crie uma consulta que determine qual categoria possui o maior preço médio entre seus produtos. Utilize uma subconsulta no FROM para calcular as médias e outra subconsulta para identificar o maior valor.
- **Exercício 6 — INSERT utilizando subconsulta:** Crie uma nova tabela chamada `produtos_promocao` contendo os campos `id_produto`, `nome_produto`, `preco` e `preco_promocional`. Depois, utilizando `INSERT INTO ... SELECT`, insira nessa tabela todos os produtos cujo preço esteja acima da média geral. O `preco_promocional` deverá representar um desconto de 10% sobre o preço original.
- **Exercício 7 — UPDATE utilizando subconsulta:** Crie um comando `UPDATE` que aumente em 10% o limite de crédito dos clientes que já realizaram pelo menos dois pedidos. A identificação desses clientes deverá ser feita por meio de uma subconsulta.
- **Exercício 8 — UPDATE baseado na média da própria categoria:** Atualize o estoque dos produtos cujo preço seja superior à média de preço de sua própria categoria. Para esses produtos, acrescente 5 unidades ao estoque atual. Utilize uma subconsulta correlacionada para comparar cada produto com a média de sua categoria.
- **Exercício 9 — DELETE utilizando NOT EXISTS:** Considere que seja necessário remover do cadastro todos os clientes que nunca realizaram pedidos. Crie o comando `DELETE` utilizando uma subconsulta com `NOT EXISTS`. Antes de executar o `DELETE`, escreva um `SELECT` utilizando a mesma condição para verificar quais registros seriam removidos.
- **Exercício 10 — Análise de desempenho com EXPLAIN ANALYZE:** Crie duas consultas que retornem os clientes que possuem pedidos: uma utilizando `IN` com subconsulta e outra utilizando `EXISTS`. Execute `EXPLAIN ANALYZE` antes de cada consulta e compare os planos de execução. Analise o custo estimado, a quantidade de linhas, o tempo de execução e os tipos de operações utilizadas pelo PostgreSQL.

### 4. Observação final
Os exercícios desta lista complementam a parte inicial da aula de subconsultas e têm como objetivo conduzir o aluno para situações mais próximas de aplicações reais, envolvendo consultas derivadas, atualização e exclusão de dados, além da análise do comportamento do otimizador do PostgreSQL.

---

## Análise do que é pedido

O presente trabalho exige a implementação e validação de 10 exercícios avançados envolvendo subconsultas em PostgreSQL. Para cumprir os requisitos pedagógicos do UniFEF, a entrega se divide em dois artefatos práticos localizados na pasta `./codigo/`:
1. `esquema_e_dados.sql`: Script responsável por estruturar o modelo relacional (tabelas `clientes`, `categorias`, `produtos`, `pedidos`, `itens_pedido`, `produtos_promocao`) e popular o ambiente com massa de dados coesa.
2. `exercicios_subconsultas.sql`: Script contendo as instruções SQL comentadas e estruturadas para resolver cada um dos 10 desafios propostos.

### Critérios implícitos de avaliação
- **Uso correto de escopo e aliases:** Subconsultas no `FROM` exigem apelidos obrigatórios para a tabela derivada e para as colunas calculadas.
- **Segurança em operações DML (`UPDATE`/`DELETE`):** O aluno deve demonstrar o uso prévio de instruções `SELECT` equivalentes para validação de impacto antes de modificar o estado do banco.
- **Análise analítica de custo (`EXPLAIN ANALYZE`):** Interpretação correta dos nós de execução do planejador do PostgreSQL (ex.: `Seq Scan`, `Index Scan`, `Hash Join`, `Nested Loop`).

---

## Fundamentação teórica

As subconsultas (ou *subqueries*) representam consultas SQL aninhadas dentro de outra instrução principal (`SELECT`, `INSERT`, `UPDATE`, `DELETE`). Quando empregadas em cenários avançados, tornam-se ferramentas indispensáveis para modelar regras de negócio complexas sem a necessidade de criar visões físicas ou procedimentos armazenados intermediários.

### 1. Subconsultas no FROM (Tabelas Derivadas)
Uma tabela derivada (ou inline view) é uma subconsulta colocada na cláusula `FROM`. O SGBD processa essa subconsulta primeiro, criando um conjunto de resultados temporário e residente em memória que a consulta externa manipula como se fosse uma tabela física.

- **Definição:** Consulta aninhada na cláusula `FROM` que atua como fonte de dados relacional temporária.
- **Motivação:** Permitir agregações em múltiplos níveis, como calcular médias de agregados sem recorrer a restrições complexas de `HAVING` acopladas.
- **Exemplo:** `SELECT categoria_id, media_preco FROM (SELECT categoria_id, AVG(preco) AS media_preco FROM produtos GROUP BY categoria_id) AS t WHERE media_preco > 1000;`
- **Contraexemplo:** Omitir o alias da tabela derivada (ex: `SELECT * FROM (SELECT * FROM produtos);`), o que gera um erro sintático no PostgreSQL (*subquery in FROM must have an alias*).
- **Armadilhas:** Esquecer de projetar colunas indispensáveis para junções externas ou utilizar aliases conflitantes com colunas das tabelas pai.

### 2. Subconsultas Correlacionadas
Ao contrário de subconsultas independentes (executadas uma única vez), a subconsulta correlacionada depende de valores fornecidos pela linha atual da consulta externa.

- **Definição:** Subconsulta que referencia colunas da tabela externa, sendo reavaliada para cada linha processada pelo bloco principal.
- **Motivação:** Resolver problemas relacionais de comparação linha a linha (ex: "produtos acima da média da sua própria categoria").
- **Armadilhas:** Impacto severo de desempenho em grandes volumes de dados devido ao acionamento repetitivo da subconsulta (comportamento semelhante a um loop aninhado).

### 3. Otimização com EXPLAIN ANALYZE
O comando `EXPLAIN ANALYZE` instrui o PostgreSQL a planejar, executar e retornar estatísticas detalhadas sobre a execução da consulta.

```flowchart TD
A[Início: Envio da Consulta SQL] --> B[Parser & Rewriter]
B --> C[Query Planner / Optimizer]
C --> D[Geração do Plano de Execução]
D --> E[Executor do PostgreSQL]
E --> F[Coleta de Métricas: EXPLAIN ANALYZE]
F --> G[Retorno dos Dados + Custos Reais e Estimados]
```

---

## Resolução proposta

Abaixo encontra-se a resolução detalhada de cada exercício da lista, acompanhada de explicações conceituais e dos scripts correspondentes contidos nos arquivos auxiliares.

### Exercício 1 — Subconsulta no FROM: média por categoria
Calcula o preço médio dos produtos por categoria utilizando uma tabela derivada, filtrando apenas categorias com média superior a R$ 1.000,00.

```sql
SELECT 
    t_media.categoria_id, 
    t_media.media_preco
FROM (
    SELECT 
        categoria_id, 
        AVG(preco) AS media_preco
    FROM produtos
    GROUP BY categoria_id
) AS t_media
WHERE t_media.media_preco > 1000.00;
```

### Exercício 2 — Tabela derivada: total de cada pedido
Calcula o valor total de cada pedido na cláusula `FROM` multiplicando quantidade pelo preço unitário, exibindo apenas pedidos acima de R$ 3.000,00.

```sql
SELECT 
    t_pedidos.id_pedido, 
    t_pedidos.valor_total
FROM (
    SELECT 
        id_pedido, 
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS t_pedidos
WHERE t_pedidos.valor_total > 3000.00;
```

### Exercício 3 — Subconsulta no FROM combinada com JOIN
Combina uma tabela derivada de totais de pedidos com as tabelas físicas `pedidos` e `clientes`.

```sql
SELECT 
    p.id_pedido,
    c.nome_cliente,
    p.data_pedido,
    t_totais.valor_total
FROM pedidos p
JOIN clientes c ON p.id_cliente = c.id_cliente
JOIN (
    SELECT 
        id_pedido, 
        SUM(quantidade * preco_unitario) AS valor_total
    FROM itens_pedido
    GROUP BY id_pedido
) AS t_totais ON p.id_pedido = t_totais.id_pedido;
```

### Exercício 4 — Clientes que gastaram acima da média
Determina o total gasto por cada cliente e compara com a média geral de gastos.

```sql
WITH gastos_clientes AS (
    SELECT 
        p.id_cliente,
        SUM(ip.quantidade * ip.preco_unitario) AS total_gasto
    FROM pedidos p
    JOIN itens_pedido ip ON p.id_pedido = ip.id_pedido
    GROUP BY p.id_cliente
)
SELECT 
    c.id_cliente,
    c.nome_cliente,
    gc.total_gasto
FROM gastos_clientes gc
JOIN clientes c ON gc.id_cliente = c.id_cliente
WHERE gc.total_gasto > (
    SELECT AVG(total_gasto) FROM gastos_clientes
);
```

### Exercício 5 — Categoria com maior preço médio
Identifica qual categoria possui o maior preço médio utilizando subconsultas aninhadas.

```sql
SELECT 
    categoria_id, 
    media_preco
FROM (
    SELECT 
        categoria_id, 
        AVG(preco) AS media_preco
    FROM produtos
    GROUP BY categoria_id
) AS medias
WHERE media_preco = (
    SELECT MAX(media_preco)
    FROM (
        SELECT 
            AVG(preco) AS media_preco
        FROM produtos
        GROUP BY categoria_id
    ) AS sub_medias
);
```

### Exercício 6 — INSERT utilizando subconsulta
Insere produtos com preço acima da média geral na tabela `produtos_promocao` com 10% de desconto.

```sql
CREATE TABLE IF NOT EXISTS produtos_promocao (
    id_produto INT PRIMARY KEY,
    nome_produto VARCHAR(100),
    preco NUMERIC(10,2),
    preco_promocional NUMERIC(10,2)
);

INSERT INTO produtos_promocao (id_produto, nome_produto, preco, preco_promocional)
SELECT 
    id_produto, 
    nome_produto, 
    preco, 
    preco * 0.90 AS preco_promocional
FROM produtos
WHERE preco > (
    SELECT AVG(preco) FROM produtos
);
```

### Exercício 7 — UPDATE utilizando subconsulta
Aumenta em 10% o limite de crédito dos clientes com dois ou mais pedidos.

```sql
-- Validação prévia
SELECT id_cliente, nome_cliente, limite_credito
FROM clientes
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
    GROUP BY id_cliente
    HAVING COUNT(*) >= 2
);

-- Execução do UPDATE
UPDATE clientes
SET limite_credito = limite_credito * 1.10
WHERE id_cliente IN (
    SELECT id_cliente
    FROM pedidos
    GROUP BY id_cliente
    HAVING COUNT(*) >= 2
);
```

### Exercício 8 — UPDATE baseado na média da própria categoria
Atualiza o estoque de produtos cujo preço excede a média da própria categoria.

```sql
UPDATE produtos p
SET estoque = estoque + 5
WHERE preco > (
    SELECT AVG(sub.preco)
    FROM produtos sub
    WHERE sub.categoria_id = p.categoria_id
);
```

### Exercício 9 — DELETE utilizando NOT EXISTS
Remove clientes que nunca realizaram pedidos.

```sql
-- Validação prévia
SELECT c.id_cliente, c.nome_cliente
FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);

-- Execução do DELETE
DELETE FROM clientes c
WHERE NOT EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);
```

### Exercício 10 — Análise de desempenho com EXPLAIN ANALYZE
Compara o plano de execução entre `IN` e `EXISTS`.

```sql
EXPLAIN ANALYZE
SELECT * 
FROM clientes c
WHERE c.id_cliente IN (
    SELECT p.id_cliente 
    FROM pedidos p
);

EXPLAIN ANALYZE
SELECT * 
FROM clientes c
WHERE EXISTS (
    SELECT 1 
    FROM pedidos p 
    WHERE p.id_cliente = c.id_cliente
);
```

---

## Como testar e validar

Para testar os scripts criados neste trabalho em um ambiente PostgreSQL local ou laboratório do UniFEF, siga os passos abaixo:

1. **Criar e popular o banco de dados:**
   Execute o script localizado em `./codigo/esquema_e_dados.sql` utilizando uma ferramenta cliente (DBeaver, pgAdmin ou `psql`):
   ```bash
   psql -U postgres -d seu_banco -f ./codigo/esquema_e_dados.sql
   ```
2. **Executar os exercícios de consulta e manipulação:**
   Execute o script `./codigo/exercicios_subconsultas.sql` linha por linha para conferir os resultados retornados.
3. **Validar os planos de execução:**
   Analise os tempos de resposta e os custos gerados pelo comando `EXPLAIN ANALYZE` no exercício 10.

---

## Critérios de qualidade

Para garantir nota máxima na correção do professor Welington Garcia, o aluno deve observar:
- **Integridade Referencial:** Chaves estrangeiras respeitadas no script DDL.
- **Boas Práticas de Nomenclatura:** Uso consistente de aliases em subconsultas no `FROM`.
- **Prevenção de Efeitos Colaterais:** Execução obrigatória de `SELECT` de homologação antes de instruções `UPDATE` e `DELETE`.
- **Análise Crítica de Desempenho:** Compreensão da diferença prática entre operadores de conjunto (`IN`) e operadores existenciais (`EXISTS`).

---

## Arquivos de apoio

- Script de Estrutura e Massa de Dados: [./codigo/esquema_e_dados.sql](./codigo/esquema_e_dados.sql)
- Script de Resolução dos Exercícios: [./codigo/exercicios_subconsultas.sql](./codigo/exercicios_subconsultas.sql)
- Aula Teórica Relacionada: [Aula 01 - Consultas Avançadas com Joins e Subselects](../../Aulas/Aula%2001%20-%20Consultas%20Avan%C3%A7adas%20com%20Joins%20e%20Subselects/detalhes.md)

---

## Mapa da atividade

```mermaid
mindmap
  root((Subconsultas Avançadas))
  Subconsultas no FROM
    Tabelas Derivadas
    Agregações Aninhadas
    Junção com JOIN
  Manipulação DML
    INSERT com SELECT
    UPDATE com Condição
    DELETE com NOT EXISTS
  Desempenho e Otimização
    EXPLAIN ANALYZE
    IN vs EXISTS
    Subconsultas Correlacionadas
```

---

## Glossário

| Termo | Definição |
| :--- | :--- |
| **Subconsulta Derivada** | Consulta aninhada na cláusula `FROM` que atua como tabela temporária. |
| **Subconsulta Correlacionada** | Consulta aninhada que referencia colunas da consulta externa, sendo avaliada linha a linha. |
| **EXPLAIN ANALYZE** | Comando do PostgreSQL que executa a consulta real e exibe o plano de execução com custos e tempos. |
| **NOT EXISTS** | Operador lógico que retorna verdadeiro se a subconsulta associada não retornar nenhum registro. |
| **Alias** | Apelido temporário atribuído a uma coluna ou tabela derivada para clareza e obrigatoriedade sintática. |

---

## Pontos-chave para a prova

1. **Obrigatoriedade de Alias:** Toda tabela derivada no `FROM` precisa obrigatoriamente de um alias.
2. **Segurança em DML:** Sempre valide com `SELECT` antes de rodar `UPDATE` ou `DELETE` baseados em subconsultas.
3. **EXPLAIN ANALYZE:** Identificar nós do tipo `Seq Scan` versus `Index Scan` na avaliação de desempenho.
4. **Comportamento do EXISTS:** Interrompe a busca assim que encontra o primeiro registro correspondente (*short-circuit evaluation*).

---

## Perguntas e respostas (JSONL)

```jsonl
{"pergunta": "Qual é a exigência sintática obrigatória ao utilizar uma subconsulta na cláusula FROM?", "resposta": "A subconsulta deve possuir obrigatoriamente um alias (apelido).", "dificuldade": "fácil"}
{"pergunta": "O que faz o comando EXPLAIN ANALYZE no PostgreSQL?", "resposta": "Planeja, executa a consulta e retorna métricas detalhadas de tempo e custo de execução.", "dificuldade": "média"}
{"pergunta": "Qual operador é recomendado para verificar a ausência de registros correlacionados em exclusões?", "resposta": "O operador NOT EXISTS.", "dificuldade": "média"}
{"pergunta": "O que caracteriza uma subconsulta correlacionada?", "resposta": "Ela referencia colunas da consulta externa e é reavaliada para cada linha processada.", "dificuldade": "difícil"}
{"pergunta": "Como é chamada uma subconsulta colocada na cláusula FROM?", "resposta": "Tabela derivada ou inline view.", "dificuldade": "fácil"}
{"pergunta": "Qual instrução permite popular uma tabela existente com dados filtrados por subconsulta?", "resposta": "INSERT INTO ... SELECT.", "dificuldade": "fácil"}
{"pergunta": "Por que é recomendado executar um SELECT antes de um UPDATE com subconsulta?", "resposta": "Para verificar exatamente quais registros serão afetados e evitar alterações indesejadas.", "dificuldade": "fácil"}
{"pergunta": "Qual é a principal diferença entre IN e EXISTS em grandes volumes de dados?", "resposta": "EXISTS geralmente avalia por curto-circuito (short-circuit) ao encontrar a primeira correspondência, enquanto IN pode processar todo o conjunto.", "dificuldade": "difícil"}
{"pergunta": "Em qual cláusula SQL as agregações como SUM e AVG são aplicadas em tabelas derivadas?", "resposta": "Na cláusula SELECT da subconsulta interna.", "dificuldade": "média"}
{"pergunta": "O que ocorre se uma tabela derivada no FROM não tiver um alias no PostgreSQL?", "resposta": "O SGBD retorna um erro sintático exigindo um alias para a subconsulta.", "dificuldade": "fácil"}
{"pergunta": "Qual comando SQL é utilizado para remover registros com base em uma subconsulta sem correspondência?", "resposta": "DELETE com NOT EXISTS.", "dificuldade": "média"}
{"pergunta": "Como se calcula o preço promocional com 10% de desconto em SQL?", "resposta": "Multiplicando o preço original por 0.90.", "dificuldade": "fácil"}
{"pergunta": "O que avalia uma subconsulta no WHERE com operador IN?", "resposta": "Se um valor pertence ao conjunto de valores retornado pela subconsulta.", "dificuldade": "fácil"}
{"pergunta": "Qual métrica fornecida pelo EXPLAIN ANALYZE indica o tempo real gasto na execução?", "resposta": "O Execution Time (tempo de execução em milissegundos).", "dificuldade": "média"}
{"pergunta": "Em qual disciplina este trabalho foi aplicado no UniFEF?", "resposta": "Tópicos Avançados em Banco de Dados.", "dificuldade": "fácil"}
```

---

## Checklist de revisão

- [ ] Estrutura do documento em Markdown GitHub puro criada corretamente.
- [ ] Todos os 10 exercícios resolvidos e documentados com trechos SQL válidos.
- [ ] Blocos de código com linguagem declarada (`sql`, `jsonl`, `mermaid`).
- [ ] Diagramas em Mermaid sem estilos customizados ou cores.
- [ ] Seções obrigatórias na ordem exata solicitada.
- [ ] Bloco JSONL com 15 a 25 linhas formatado corretamente.
