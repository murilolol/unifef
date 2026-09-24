# Simulados Comentados - Tópicos Avançados em Banco de Dados

> **Professor:** Welington Garcia  
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)  
> **Instituição:** UniFEF  
> **Conteúdo Coberto:** Consultas Avançadas (JOINs e Subselects), Visões (Views e Materialized Views) e Programação Procedural (PL/pgSQL Stored Procedures).

## Sumário
- [Apresentação do material](#apresentação-do-material)
- [Simulado 1 - Questões Objetivas](#simulado-1---questões-objetivas)
- [Simulado 2 - Questões Discursivas](#simulado-2---questões-discursivas)
- [Gabarito Comentado - Simulado 1](#gabarito-comentado---simulado-1)
- [Gabarito Comentado - Simulado 2](#gabarito-comentado---simulado-2)
- [Glossário técnico](#glossário-técnico)

---

## Apresentação do material

Este documento reúne dois simulados completos estruturados para consolidar o aprendizado da disciplina de Tópicos Avançados em Banco de Dados. O Simulado 1 apresenta 12 questões objetivas no estilo ENADE, focando em pegadinhas conceituais, comportamento do otimizador do PostgreSQL e armadilhas sintáticas. O Simulado 2 apresenta 5 questões discursivas de nível avançado, acompanhadas de rubricas detalhadas de correção e respostas modelo que contemplam cenários reais de engenharia de software e modelagem relacional.

---

## Simulado 1 - Questões Objetivas

### Questão 1
Dada uma tabela `clientes` e uma tabela `pedidos`, onde um cliente pode ter zero ou vários pedidos, analise o seguinte comando SQL:

```sql
SELECT c.nome, p.id_pedido
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente;
```

Assinale a alternativa que descreve corretamente o comportamento do SGBD ao executar esta consulta:
- A) A consulta retornará apenas os clientes que possuem pelo menos um pedido registrado.
- B) A consulta retornará todos os pedidos cadastrados, mesmo aqueles que não possuem clientes associados.
- C) A consulta retornará todos os clientes cadastrados. Para os clientes que não possuem pedidos, as colunas referentes ao pedido serão preenchidas com o valor `NULL`.
- D) A consulta gerará um erro de sintaxe, pois o operador `LEFT JOIN` exige obrigatoriamente a presença de uma cláusula `WHERE` associada.
- E) A consulta retornará o produto cartesiano entre clientes e pedidos.

### Questão 2
Considere o mesmo cenário anterior (tabelas `clientes` e `pedidos`). Analise as duas consultas a seguir:

Consulta 1:
```sql
SELECT c.nome, p.id_pedido
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente AND p.status = 'Pago';
```

Consulta 2:
```sql
SELECT c.nome, p.id_pedido
FROM clientes c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
WHERE p.status = 'Pago';
```

Assinale a alternativa que descreve corretamente a diferença de comportamento entre a Consulta 1 e a Consulta 2:
- A) Ambas as consultas produzem exatamente o mesmo resultado, pois filtros em junções `LEFT JOIN` são sempre avaliados após o cruzamento das tabelas.
- B) A Consulta 1 preserva todos os clientes, exibindo `NULL` para clientes sem pedidos pagos. A Consulta 2 descarta os clientes sem pedidos pagos, transformando-se funcionalmente em um `INNER JOIN`.
- C) A Consulta 2 é mais eficiente e preserva todos os clientes, enquanto a Consulta 1 gera um erro de execução no PostgreSQL.
- D) A Consulta 1 gera um produto cartesiano restrito, enquanto a Consulta 2 utiliza indexação em árvore B-Tree obrigatória.
- E) Não há diferença lógica, apenas sintática. O otimizador do PostgreSQL converte ambas para o mesmo plano de execução.

### Questão 3
Uma subconsulta escalar (*scalar subquery*) é aquela que retorna:
- A) Múltiplas linhas e múltiplas colunas, comportando-se como uma tabela derivada.
- B) Uma única linha com múltiplas colunas, utilizada para atribuições em lote.
- C) Estritamente um único valor (uma linha e uma coluna), podendo ser utilizada em expressões onde uma constante ou expressão escalar é aceita.
- D) Um conjunto de dados não estruturados em formato JSON para processamento NoSQL.
- E) Um ponteiro de memória para acesso direto ao buffer pool do SGBD.

### Questão 4
Analise o trecho de código SQL abaixo que utiliza o operador `NOT IN`:

```sql
SELECT nome
FROM clientes
WHERE id_cliente NOT IN (
    SELECT id_cliente
    FROM pedidos
);
```

Se a subconsulta retornar pelo menos um valor `NULL` no conjunto de resultados da tabela `pedidos`, o que acontecerá com a consulta externa?
- A) A consulta retornará normalmente os clientes que não estão na lista de pedidos, ignorando o valor `NULL`.
- B) A consulta gerará um erro de compilação informando incompatibilidade de tipos.
- C) A consulta retornará **zero linhas**, devido ao comportamento da lógica trivaluada do SQL onde qualquer comparação com `NULL` resulta em `UNKNOWN`.
- D) A consulta tratará o `NULL` como o número zero, excluindo clientes com identificadores zerados.
- E) O otimizador converterá automaticamente o `NOT IN` para `NOT EXISTS`, contornando o problema.

### Questão 5
Sobre a utilização dos operadores `EXISTS` e `IN` em subconsultas no PostgreSQL, assinale a alternativa correta:
- A) O operador `IN` é sempre mais performático que o `EXISTS` em grandes volumes de dados, pois utiliza tabelas temporárias em memória RAM.
- B) O operador `EXISTS` avalia a existência de linhas retornadas pela subconsulta e interrompe a busca assim que encontra a primeira correspondência (*short-circuit evaluation*), sendo frequentemente mais eficiente em subconsultas correlacionadas.
- C) O operador `IN` não aceita subconsultas, apenas listas estáticas de valores literais delimitados por parênteses.
- D) O operador `EXISTS` exige obrigatoriamente o uso de funções de agregação como `COUNT(*)` em sua estrutura interna.
- E) Ambos os operadores possuem exatamente o mesmo plano de execução e custo computacional no PostgreSQL.

### Questão 6
Considere os operadores quantificadores `ANY` e `ALL`. Assinale a alternativa que descreve corretamente a expressão `WHERE preco > ALL (SELECT preco FROM produtos WHERE categoria = 2)`:
- A) Retorna produtos cujo preço seja maior do que pelo menos um dos preços dos produtos da categoria 2.
- B) Retorna produtos cujo preço seja estritamente igual à média dos preços da categoria 2.
- C) Retorna produtos cujo preço seja maior do que o **maior** preço encontrado entre todos os produtos da categoria 2.
- D) Retorna produtos cujo preço seja menor do que o menor preço da categoria 2.
- E) A expressão é inválida, pois o operador `ALL` não pode ser combinado com subconsultas.

### Questão 7
Sobre o uso de subconsultas na cláusula `FROM` (também conhecidas como tabelas derivadas ou *inline views*), é correto afirmar que:
- A) Não precisam obrigatoriamente possuir um alias (apelido), diferentemente das tabelas físicas.
- B) Devem possuir obrigatoriamente um alias atribuído, permitindo que a consulta externa faça referência às colunas geradas.
- C) São armazenadas permanentemente no catálogo do sistema como arquivos físicos em disco.
- D) Não podem conter funções de agregação ou cláusulas `GROUP BY`.
- E) São proibidas em bancos de dados relacionais que seguem estritamente a 3ª Forma Normal.

### Questão 8
Sobre Views (visões) em PostgreSQL, analise as afirmativas:
1. Uma view comum armazena fisicamente os dados resultantes da consulta em disco.
2. Uma view comum atua como uma tabela virtual, cuja consulta subjacente é reavaliada a cada acesso.
3. Views que contêm funções de agregação (`GROUP BY`) ou `DISTINCT` são automaticamente atualizáveis via comandos DML (`INSERT`, `UPDATE`, `DELETE`).

Assinale a alternativa correta:
- A) Apenas a afirmativa 1 está correta.
- B) Apenas a afirmativa 2 está correta.
- C) Apenas a afirmativa 3 está correta.
- D) As afirmativas 1 e 3 estão corretas.
- E) Todas as afirmativas estão incorretas.

### Questão 9
Qual é a principal diferença arquitetural entre uma View comum e uma Materialized View no PostgreSQL?
- A) Views comuns não aceitam permissões de segurança via `GRANT`, enquanto Materialized Views aceitam.
- B) Materialized Views armazenam o resultado da consulta fisicamente em disco (cache), exigindo comandos de atualização (`REFRESH`), enquanto views comuns recalculam os dados dinamicamente a cada acesso.
- C) Views comuns podem conter índices B-Tree, enquanto Materialized Views não suportam indexação.
- D) Materialized Views são exclusivas para operações transacionais (OLTP), enquanto views comuns são para analíticas (OLAP).
- E) Não há diferença prática; o termo "Materialized" é apenas um sinônimo comercial adotado pelo PostgreSQL.

### Questão 10
Para garantir que operações de inserção ou atualização através de uma view atualizável não violem as restrições de filtro definidas em sua cláusula `WHERE`, qual cláusula deve ser utilizada na criação da view?
- A) `WITH CHECK OPTION`
- B) `WITH SECURE CHECK`
- C) `VALIDATE DATA`
- D) `FORCE REFRESH`
- E) `CHECK CONSTRAINT`

### Questão 11
Sobre Stored Procedures e a linguagem PL/pgSQL no PostgreSQL, assinale a alternativa incorreta:
- A) Uma stored procedure é executada através do comando `CALL`.
- B) Procedures permitem a utilização de estruturas de controle de fluxo procedural, como `IF`, `LOOP` e `WHILE`.
- C) Uma função (`FUNCTION`) pode ser chamada diretamente em expressões SQL (como dentro de um `SELECT`), enquanto procedures exigem o comando `CALL`.
- D) Procedures não permitem de forma alguma o uso de parâmetros de saída (`OUT` ou `INOUT`).
- E) O PL/pgSQL suporta tratamento de exceções através do bloco `EXCEPTION`.

### Questão 12
Analise o seguinte bloco de código PL/pgSQL:

```sql
CREATE OR REPLACE PROCEDURE sp_ajustar_limite(
    IN p_cliente_id INT,
    INOUT p_limite NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE clientes
    SET limite_credito = limite_credito + p_limite
    WHERE id_cliente = p_cliente_id;
    
    SELECT limite_credito INTO p_limite
    FROM clientes
    WHERE id_cliente = p_cliente_id;
END;
$$;
```

Assinale a alternativa que descreve corretamente o comportamento do parâmetro `p_limite`:
- A) Funciona exclusivamente como parâmetro de entrada, gerando erro de sintaxe ao ser atribuído no `SELECT INTO`.
- B) Atua como parâmetro de entrada e saída (`INOUT`), recebendo um valor inicial na chamada e retornando o valor atualizado do limite de crédito do cliente.
- C) É um parâmetro de saída pura (`OUT`), cujo valor inicial fornecido pelo chamador é obrigatoriamente nulo.
- D) Funciona como uma variável global compartilhada entre todas as sessões ativas do banco de dados.
- E) Gera um erro em tempo de execução porque parâmetros `INOUT` não podem receber novos valores dentro de blocos `BEGIN...END`.

---

## Simulado 2 - Questões Discursivas

### Questão 1
Explique detalhadamente o comportamento conceitual e de processamento de um `LEFT OUTER JOIN` em comparação com um `INNER JOIN`. Em sua resposta, cite um cenário real de negócio onde o uso do `LEFT JOIN` é obrigatório, descreva o problema da armadilha de aplicar filtros da tabela à direita na cláusula `WHERE` em vez da cláusula `ON`, e apresente um exemplo prático de consulta SQL evidenciando a mitigação dessa armadilha.

### Questão 2
Disserte sobre o conceito, motivação e impacto de desempenho das **subconsultas correlacionadas** (*correlated subqueries*) em bancos de dados relacionais. Explique como o otimizador do PostgreSQL processa esse tipo de consulta em comparação com subconsultas independentes e aponte em quais cenários o uso de operadores existenciais (`EXISTS`) é preferível ao uso de conjuntos (`IN`).

### Questão 3
Compare **Views comuns** e **Materialized Views** no PostgreSQL. Discuta os critérios de escolha arquitetural entre ambas considerando ambientes OLTP (*Online Transaction Processing*) e OLAP (*Online Analytical Processing*). Explique o mecanismo de atualização (`REFRESH MATERIALIZED VIEW`) e a importância da criação de índices nesses objetos.

### Questão 4
Elabore uma Stored Procedure em PL/pgSQL chamada `sp_processar_pedido` que receba como parâmetros de entrada o identificador do cliente (`p_cliente_id`) e o valor total do pedido (`p_valor`). A procedure deve:
1. Verificar se o cliente possui limite de crédito suficiente (`limite_credito`) na tabela `clientes`.
2. Caso o limite seja insuficiente, levantar uma exceção customizada utilizando `RAISE EXCEPTION`.
3. Caso o limite seja suficiente, realizar o débito correspondente no limite do cliente e inserir um registro na tabela `pedidos`.
4. Utilizar parâmetros ou variáveis para retornar o ID do pedido gerado (`OUT`).

### Questão 5
Explique a utilidade do comando `EXPLAIN ANALYZE` no PostgreSQL para a engenharia de dados e otimização de consultas. Descreva os principais nós de execução exibidos no plano (como `Seq Scan`, `Index Scan`, `Hash Join` e `Nested Loop`) e discuta como a análise desses custos auxilia na identificação de gargalos de desempenho em consultas complexas com subconsultas e múltiplos `JOINs`.

---

## Gabarito Comentado - Simulado 1

### Q1: C
- **Justificativa Técnica:** O `LEFT JOIN` (ou `LEFT OUTER JOIN`) garante a preservação integral de todas as linhas da tabela à esquerda (`clientes`), independentemente de possuírem correspondência na tabela à direita (`pedidos`). Quando não há match, as colunas da tabela direita são preenchidas com `NULL`.
- **Distratoras:**
  - A: Descreve o comportamento de um `INNER JOIN`.
  - B: Descreve o comportamento de um `RIGHT JOIN` invertido.
  - D: Incorreta, `LEFT JOIN` não exige `WHERE`.
  - E: Descreve o comportamento de um `CROSS JOIN`.

### Q2: B
- **Justificativa Técnica:** Na Consulta 1, o filtro `p.status = 'Pago'` está na cláusula `ON`, o que restringe o escopo do join antes de descartar os clientes, preservando os clientes sem pedidos (com `NULL`). Na Consulta 2, o filtro está no `WHERE`, que é avaliado *após* a junção; como o resultado sem pedidos possui `NULL` em `p.status`, a condição `NULL = 'Pago'` avalia para `UNKNOWN` (falsa), eliminando o cliente e convertendo o `LEFT JOIN` em `INNER JOIN`.
- **Distratoras:**
  - A, D, E: Falsas premissas sobre o processamento lógico do SGBD.
  - C: Incorreta, a Consulta 1 não gera erro.

### Q3: C
- **Justificativa Técnica:** Uma subconsulta escalar retorna uma única linha e uma única coluna, equivalendo semanticamente a um valor atômico (escalar).
- **Distratoras:**
  - A: Descreve subconsulta de tabela.
  - B: Descreve subconsulta de linha.
  - D e E: Conceitos inexistentes ou errôneos no contexto relacional padrão.

### Q4: C
- **Justificativa Técnica:** A lógica de três valores do SQL (`TRUE`, `FALSE`, `UNKNOWN`) dita que `x NOT IN (val1, NULL)` é avaliado como `x <> val1 AND x <> NULL`. Como qualquer comparação com `NULL` resulta em `UNKNOWN`, a expressão inteira falha, resultando em conjunto vazio.
- **Distratoras:**
  - A, B, D, E: Desconsideram o comportamento padrão da lógica booleana trivaluada do SQL frente a valores nulos.

### Q5: B
- **Justificativa Técnica:** O operador `EXISTS` utiliza avaliação de curto-circuito (*short-circuit*), encerrando a busca na subconsulta assim que o primeiro registro correspondente é encontrado.
- **Distratoras:**
  - A: Falsa; `IN` não é universalmente mais performático.
  - C: Falsa; `IN` aceita subconsultas perfeitamente.
  - D e E: Falsas afirmações técnicas.

### Q6: C
- **Justificativa Técnica:** O operador `ALL` exige que a condição seja verdadeira para *todos* os elementos retornados pela subconsulta. Logo, `preco > ALL(...)` significa que o preço é maior que o máximo do conjunto, equivalendo a `preco > (SELECT MAX(...) FROM ...)`.
- **Distratoras:**
  - A: Descreve o operador `ANY`.
  - B, D, E: Incorretas conceitualmente.

### Q7: B
- **Justificativa Técnica:** Tabelas derivadas no `FROM` exigem obrigatoriamente um alias para que a consulta externa possa referenciar o conjunto de resultados temporário.
- **Distratoras:**
  - A: Falsa, o alias é obrigatório no padrão SQL.
  - C, D, E: Conceitos incorretos; tabelas derivadas são voláteis em memória/disco temporário e suportam agregações.

### Q8: B
- **Justificativa Técnica:** Views comuns são virtuais e reavaliadas a cada acesso (af. 2 correta). Views comuns não armazenam dados físicos (af. 1 falsa) e views com agregações não são automaticamente atualizáveis via DML (af. 3 falsa).
- **Distratoras:**
  - A, C, D, E: Contêm afirmativas incorretas sobre armazenamento ou atualizabilidade.

### Q9: B
- **Justificativa Técnica:** Materialized Views armazenam fisicamente o resultado em disco para otimização de consultas pesadas (OLAP), exigindo `REFRESH`, enquanto views comuns são apenas atalhos lógicos.
- **Distratoras:**
  - A, C, D, E: Afirmativas falsas sobre permissões, índices ou propósitos transacionais.

### Q10: A
- **Justificativa Técnica:** A cláusula `WITH CHECK OPTION` impede que operações de inserção ou atualização através de uma view atualizável gerem dados que violem o filtro do `WHERE` da view.
- **Distratoras:** B, C, D, E são termos inválidos ou incorretos no PostgreSQL.

### Q11: D
- **Justificativa Técnica:** Stored procedures suportam perfeitamente parâmetros de saída (`OUT` e `INOUT`), invalidando a afirmativa D.
- **Distratoras:** A, B, C e E descrevem características corretas do PL/pgSQL e procedures.

### Q12: B
- **Justificativa Técnica:** O parâmetro `INOUT` é bidirecional: entra com o valor fornecido pelo chamador e pode ser modificado e retornado ao final da execução.
- **Distratoras:** A, C, D e E contradizem a especificação de parâmetros `INOUT` no PostgreSQL.

---

## Gabarito Comentado - Simulado 2

### Questão 1
- **Resposta Modelo:** O `INNER JOIN` retorna estritamente a interseção dos registros que possuem correspondência em ambas as tabelas. Já o `LEFT OUTER JOIN` preserva todos os registros da tabela à esquerda, mesmo que não haja correspondência na tabela à direita, preenchendo as colunas faltantes com `NULL`. Um cenário de negócio obrigatório para o `LEFT JOIN` é a emissão de um relatório gerencial de clientes inadimplentes ou ativos que liste *todos* os cadastros de clientes, independentemente de terem realizado compras no período.
  A armadilha de colocar filtros da tabela direita no `WHERE` (ex: `WHERE pedidos.status = 'Pago'`) ocorre porque o `WHERE` é executado após a conclusão da junção. Como os clientes sem pedidos possuem `NULL` em `pedidos.status`, a expressão `NULL = 'Pago'` resulta em `UNKNOWN`, eliminando o registro e destruindo o comportamento do `LEFT JOIN`. Para mitigar, o filtro condicional deve residir na cláusula `ON`.
- **Rubrica de Correção:**
  - Conceito de INNER vs LEFT JOIN (até 2,5 pontos).
  - Cenário real de negócio válido (até 2,5 pontos).
  - Explicação teórica da armadilha do WHERE vs ON (até 2,5 pontos).
  - Exemplo prático de mitigação correta (até 2,5 pontos).

### Questão 2
- **Resposta Modelo:** Subconsultas correlacionadas são aquelas que dependem de valores da consulta externa para cada linha avaliada, sendo executadas repetidamente (conceitualmente, linha a linha). Isso pode gerar degradação severa de desempenho (*Full Table Scan* repetido) caso o SGBD não aplique otimizações internas. O PostgreSQL converte frequentemente subconsultas correlacionadas em junções otimizadas (*semi-joins* ou *hash joins*). O uso de `EXISTS` é preferível ao `IN` porque o `EXISTS` utiliza avaliação de curto-circuito, parando de varrer a subconsulta assim que encontra o primeiro match, além de lidar de forma robusta e segura com valores `NULL`, diferentemente do `NOT IN`.
- **Rubrica de Correção:**
  - Definição de subconsulta correlacionada (até 2,5 pontos).
  - Análise de impacto de desempenho (até 2,5 pontos).
  - Vantagens do EXISTS sobre o IN (até 2,5 pontos).
  - Tratamento de valores nulos e curto-circuito (até 2,5 pontos).

### Questão 3
- **Resposta Modelo:** Views comuns são objetos virtuais que armazenam apenas a definição da consulta, sendo ideais para ambientes OLTP (garantindo dados em tempo real, segurança e encapsulamento). Materialized Views armazenam o resultado físico em disco, sendo ideais para ambientes OLAP e relatórios analíticos pesados, pois evitam o reprocessamento de agregações complexas. A desvantagem da Materialized View é a necessidade de atualização assíncrona ou síncrona (`REFRESH MATERIALIZED VIEW`), o que significa que os dados não estão estritamente em tempo real. A criação de índices B-Tree sobre Materialized Views é fundamental para acelerar o acesso aos dados cacheados.
- **Rubrica de Correção:**
  - Diferenciação estrutural e física (até 2,5 pontos).
  - Adequação aos cenários OLTP e OLAP (até 2,5 pontos).
  - Mecanismo de REFRESH e falta de tempo real (until 2,5 pontos).
  - Importância da indexação (até 2,5 pontos).

### Questão 4
- **Resposta Modelo:**
  ```sql
  CREATE OR REPLACE PROCEDURE sp_processar_pedido(
      IN p_cliente_id INT,
      IN p_valor NUMERIC,
      OUT p_pedido_id INT
  )
  LANGUAGE plpgsql
  AS $$
  DECLARE
      v_limite_atual NUMERIC;
  BEGIN
      SELECT limite_credito INTO v_limite_atual
      FROM clientes
      WHERE id_cliente = p_cliente_id;

      IF v_limite_atual IS NULL THEN
          RAISE EXCEPTION 'Cliente com ID % nao encontrado.', p_cliente_id;
      END IF;

      IF v_limite_atual < p_valor THEN
          RAISE EXCEPTION 'Limite de credito insuficiente. Disponivel: %, Solicitado: %', v_limite_atual, p_valor;
      END IF;

      UPDATE clientes
      SET limite_credito = limite_credito - p_valor
      WHERE id_cliente = p_cliente_id;

      INSERT INTO pedidos (data_pedido, status, id_cliente)
      VALUES (CURRENT_DATE, 'Processando', p_cliente_id)
      RETURNING id_pedido INTO p_pedido_id;
      
      RAISE NOTICE 'Pedido % gerado com sucesso.', p_pedido_id;
  END;
  $$;
  ```
- **Rubrica de Correção:**
  - Sintaxe correta de procedure e parâmetros `IN`/`OUT` (até 2,5 pontos).
  - Validação de saldo/limite com consulta prévia (até 2,5 pontos).
  - Disparo adequado de exceção via `RAISE EXCEPTION` (até 2,5 pontos).
  - Execução correta de DML e captura com `RETURNING ... INTO` (até 2,5 pontos).

### Questão 5
- **Resposta Modelo:** O comando `EXPLAIN ANALYZE` instrui o PostgreSQL a planejar, executar efetivamente a consulta e retornar estatísticas reais de tempo, custo estimado e número de linhas afetadas por cada nó. Os principais nós incluem:
  - `Seq Scan`: Varredura sequencial em toda a tabela (custoso em grandes bases).
  - `Index Scan`: Busca otimizada utilizando índices B-Tree.
  - `Hash Join` e `Nested Loop`: Estratégias de junção de tabelas escolhidas pelo otimizador com base na volumetria e cardinalidade.
  Analisar esses custos ajuda engenheiros a identificar gargalos, como a ausência de índices em colunas de junção (`ON`) ou filtros de subconsultas, permitindo reescrever queries ou criar índices adequados.
- **Rubrica de Correção:**
  - Definição e propósito do EXPLAIN ANALYZE (até 2,5 pontos).
  - Explicação correta dos nós Seq Scan, Index Scan e Joins (até 2,5 pontos).
  - Relação entre cardinalidade e plano de execução (until 2,5 pontos).
  - Aplicação prática na identificação de gargalos (até 2,5 pontos).

---

## Glossário técnico

| Termo | Definição |
| :--- | :--- |
| **Alias** | Apelido temporário atribuído a tabelas ou colunas para desambiguação e legibilidade. |
| **Curto-circuito (*Short-circuit*)** | Estratégia de avaliação lógica que encerra o processamento assim que o resultado é determinado. |
| **OLTP** | *Online Transaction Processing*; sistemas transacionais focados em alta concorrência e operações rápidas de DML. |
| **OLAP** | *Online Analytical Processing*; sistemas voltados para suporte à decisão, relatórios complexos e agregações em massa. |
| **PL/pgSQL** | Linguagem procedural padrão do PostgreSQL que estende o SQL com estruturas de controle e variáveis. |

---

## Fontes e Metadados

- Turma no Classroom: Tópicos Avançados em BD - FEF
- Itens processados: 3 materiais, 5 tarefas, 0 avisos
- Gerado em: 23/09/2026, 21:54:21 (BRT) via classroom-sync
