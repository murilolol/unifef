Com base no material acadêmico fornecido (**Tópicos Avançados em Banco de Dados — Módulo de JOINs em SQL com PostgreSQL**), elaborei um simulado completo contendo **10 questões de múltipla escolha** (com gabarito comentado) e **5 questões discursivas / estudos de caso práticos**.

---

# SIMULADO: Tópicos Avançados em Banco de Dados (PostgreSQL — JOINs)

---

## PARTE 1: Questões de Múltipla Escolha

### Questão 1
Qual operador de junção (*JOIN*) no PostgreSQL é utilizado estritamente para retornar **apenas** as linhas que possuem correspondência (match) em ambas as tabelas envolvidas na condição estipulada?
* A) `LEFT OUTER JOIN`
* B) `FULL OUTER JOIN`
* C) `INNER JOIN`
* D) `CROSS JOIN`
* E) `NATURAL JOIN`

---

### Questão 2
Ao desenvolver um relatório gerencial, um analista precisa listar **todos os clientes cadastrados**, independentemente de terem realizado pedidos ou não. Caso o cliente não possua pedidos associados, as colunas referentes aos pedidos devem retornar valores nulos (`NULL`). Qual tipo de junção atende perfeitamente a esse requisito?
* A) `INNER JOIN`
* B) `LEFT JOIN`
* C) `RIGHT JOIN` (considerando a tabela de pedidos à esquerda)
* D) `CROSS JOIN`
* E) `SELF JOIN`

---

### Questão 3
Observe a seguinte instrução SQL executada em um banco de dados PostgreSQL:
```sql
SELECT c.id_cliente, c.nome 
FROM clientes c 
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente 
WHERE p.id_pedido IS NULL;
```
Qual é o objetivo lógico e o resultado esperado dessa consulta?
* A) Listar todos os clientes que realizaram pelo menos um pedido.
* B) Listar todos os pedidos que não possuem clientes cadastrados.
* C) Identificar e listar os clientes que **nunca** fizeram nenhum pedido.
* D) Gerar um produto cartesiano entre clientes e pedidos.
* E) Retornar um erro de sintaxe, pois o operador `IS NULL` não pode ser usado com chaves estrangeiras.

---

### Questão 4
Sobre o comportamento do **`CROSS JOIN`**, analise as afirmativas abaixo:
1. Ele produz o produto cartesiano entre as tabelas envolvidas.
2. Exige obrigatoriamente o uso da cláusula `ON` para definir o relacionamento por chave estrangeira.
3. Se a Tabela A possui 10 linhas e a Tabela B possui 5 linhas, o resultado do `CROSS JOIN` terá exatamente 50 linhas.

Estão corretas:
* A) Apenas 1 e 3.
* B) Apenas 2 e 3.
* C) Apenas 1 e 2.
* D) Todas as afirmativas.
* E) Apenas 3.

---

### Questão 5
Em um sistema de controle de funcionários, existe uma tabela chamada `funcionarios` contendo as colunas `id_funcionario`, `nome` e `id_supervisor` (sendo `id_supervisor` uma chave estrangeira que referencia a própria tabela `funcionarios`). Para listar cada funcionário juntamente com o nome do seu respectivo supervisor (ou exibir "Sem supervisor" caso seja o chefe principal), qual técnica de JOIN deve ser aplicada?
* A) `CROSS JOIN`
* B) `FULL OUTER JOIN`
* C) `SELF JOIN`
* D) `NATURAL JOIN`
* E) `RIGHT JOIN` estrito

---

### Questão 6
Qual é a principal diferença prática entre colocar uma condição de filtro na cláusula `ON` versus na cláusula `WHERE` ao utilizar um `LEFT JOIN`?
* A) Não há diferença; o otimizador do PostgreSQL trata ambas as cláusulas exatamente da mesma forma.
* B) O filtro no `ON` é restrito a funções de agregação, enquanto o `WHERE` lida com colunas literais.
* C) Colocar o filtro no `WHERE` em um `LEFT JOIN` pode transformar o comportamento da consulta em algo semelhante a um `INNER JOIN`, pois descarta linhas onde a tabela da direita virou `NULL`.
* D) O filtro no `ON` elimina registros da tabela da esquerda antes mesmo de iniciar a junção.
* E) A cláusula `WHERE` não pode ser combinada com consultas que utilizam `JOIN`.

---

### Questão 7
O que faz a função **`COALESCE`** quando aplicada em conjunto com um `LEFT JOIN`?
* A) Ela força o PostgreSQL a executar um `INNER JOIN` para otimizar a performance.
* B) Ela substitui valores nulos (`NULL`) resultantes da ausência de correspondência por um valor padrão especificado.
* C) Ela realiza a soma automática de colunas numéricas em formato de agregação.
* D) Ela elimina linhas duplicadas do conjunto resultante, funcionando como um `DISTINCT`.
* E) Ela converte tipos de dados incompatíveis entre chaves primárias e estrangeiras.

---

### Questão 8
Sobre o uso de **`NATURAL JOIN`** e a cláusula **`USING`** em PostgreSQL, assinale a alternativa **correta**:
* A) O `NATURAL JOIN` é altamente recomendado em ambientes de produção corporativa por sua alta legibilidade e segurança.
* B) A cláusula `USING (id_cliente)` exige que a coluna especificada exista e tenha o mesmo nome em ambas as tabelas, tornando a consulta mais explícita e segura que o `NATURAL JOIN`.
* C) O `NATURAL JOIN` requer o uso obrigatório da cláusula `ON` para funcionar.
* D) A cláusula `USING` só pode ser utilizada em operações de `CROSS JOIN`.
* E) Tanto o `NATURAL JOIN` quanto o `USING` impedem o uso de funções de agregação como `SUM` e `COUNT`.

---

### Questão 9
Considere uma consulta SQL que envolve junções de múltiplas tabelas (`clientes`, `pedidos`, `itens_pedido`, `produtos`) e utiliza a função de agregação `SUM` para calcular o valor total de cada pedido. O que é obrigatório fazer em relação às colunas não agregadas que aparecem no `SELECT`?
* A) Elas devem ser inseridas obrigatoriamente na cláusula `GROUP BY`.
* B) Elas devem ser precedidas pela palavra-chave `DISTINCT`.
* C) Elas devem ser omitidas ou tratadas exclusivamente por meio de subconsultas.
* D) Elas devem ser convertidas para o tipo `VARCHAR` através de `CAST`.
* E) Elas devem ser colocadas obrigatoriamente dentro de uma função `COALESCE`.

---

### Questão 10
Qual dos seguintes cenários representa a aplicação típica ideal para um **`FULL OUTER JOIN`**?
* A) Listar todos os produtos e suas respectivas categorias, ignorando produtos órfãos.
* B) Gerar todas as combinações possíveis entre tamanhos e cores de camisas.
* C) Auditoria e comparação de duas bases de dados/tabelas para identificar registros que estão presentes em apenas um dos lados ou em ambos.
* D) Obter a hierarquia corporativa entre diretores, gerentes e analistas.
* E) Filtrar apenas os clientes que possuem volume de compras superior à média da empresa.

---

## GABARITO COMENTADO (Múltipla Escolha)

* **Questão 1: C**
  * *Comentário:* O `INNER JOIN` retorna estritamente a interseção, ou seja, apenas as linhas que possuem correspondência nas tabelas cruzadas.
* **Questão 2: B**
  * *Comentário:* O `LEFT JOIN` preserva todos os registros da tabela da esquerda (clientes), preenchendo com `NULL` as colunas da tabela da direita (pedidos) quando não há correspondência.
* **Questão 3: C**
  * *Comentário:* Ao filtrar `WHERE p.id_pedido IS NULL` após um `LEFT JOIN`, isolamos exatamente os registros da tabela principal (clientes) que não obtiveram casamento na tabela secundária (pedidos), ou seja, clientes sem pedidos.
* **Questão 4: A**
  * *Comentário:* A afirmativa 2 está incorreta porque o `CROSS JOIN` **não** utiliza a cláusula `ON` (ele combina todas as linhas com todas, gerando o produto cartesiano). As afirmativas 1 e 3 estão corretas.
* **Questão 5: C**
  * *Comentário:* O `SELF JOIN` é a técnica onde uma tabela é unida a ela mesma, ideal para relacionamentos hierárquicos internos (como funcionário e supervisor).
* **Questão 6: C**
  * *Comentário:* Colocar um filtro restritivo sobre colunas da tabela da direita no `WHERE` (ex: `p.status = 'Pago'`) transforma o `LEFT JOIN` de fato em um `INNER JOIN`, pois remove as linhas onde a tabela da direita virou `NULL`.
* **Questão 7: B**
  * *Comentário:* A função `COALESCE(valor, 'Padrão')` avalia se o primeiro argumento é `NULL` e, caso seja, retorna o segundo argumento (valor de substituição).
* **Questão 8: B**
  * *Comentário:* A cláusula `USING` exige que a coluna tenha o mesmo nome em ambas as tabelas, sendo uma alternativa mais controlada e segura ao `NATURAL JOIN` (que faz isso de forma totalmente implícita e desaconselhada em produção).
* **Questão 9: A**
  * *Comentário:* Regra padrão do SQL: qualquer coluna presente no `SELECT` que não esteja envolvida em uma função de agregação (`SUM`, `COUNT`, `AVG`, etc.) deve obrigatoriamente constar na cláusula `GROUP BY`.
* **Questão 10: C**
  * *Comentário:* O `FULL OUTER JOIN` traz tudo dos dois mundos (correspondências + sobras da esquerda + sobras da direita), tornando-se excelente para auditorias e reconciliação de dados.

---

## PARTE 2: Questões Discursivas e Estudos de Caso Práticos

### Questão 1 (Estudo de Caso: Relatório com LEFT JOIN e COALESCE)
**Enunciado:** Imagine que você atua como DBA em um e-commerce. O gerente de vendas solicitou uma listagem contendo o nome do produto, o preço e o nome da categoria a qual ele pertence. Contudo, alguns produtos cadastrados recentemente ainda não possuem categoria associada (`id_categoria` é `NULL`). 
* **Tarefa:** Escreva a consulta SQL utilizando PostgreSQL que retorne o nome do produto, o preço e o nome da categoria. Caso o produto não possua categoria, o texto `'Sem categoria'` deve ser exibido no lugar do valor nulo. Utilize aliases claros para as tabelas.

---

### Questão 2 (Análise de Erro de Lógica: ON vs. WHERE)
**Enunciado:** Um desenvolvedor júnior escreveu a seguinte consulta para listar todos os clientes e, caso tenham, os detalhes dos seus pedidos com status `'Cancelado'`:
```sql
SELECT c.id_cliente, c.nome, p.id_pedido, p.status 
FROM clientes c 
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente 
WHERE p.status = 'Cancelado';
```
* **Tarefa:** Explique detalhadamente por que esta consulta **falhou** em retornar os clientes que não possuem pedidos cancelados (comportando-se incorretamente como um `INNER JOIN`). Em seguida, reescreva a consulta corrigindo a lógica para que o filtro de status atue corretamente sem eliminar os clientes sem pedidos da listagem principal.

---

### Questão 3 (Modelagem e Prática de SELF JOIN)
**Enunciado:** Considere uma tabela de funcionários estruturada da seguinte forma:
`funcionarios (id_funcionario, nome, cargo, id_supervisor)`
O campo `id_supervisor` é uma chave estrangeira que aponta para o `id_funcionario` da própria tabela.
* **Tarefa:** Escreva uma consulta SQL em PostgreSQL utilizando um `SELF JOIN` e a função `COALESCE` que exiba o nome do funcionário, o seu cargo e o nome do seu supervisor. Caso o funcionário seja o topo da hierarquia (sem supervisor), o campo supervisor deve exibir a mensagem `'Diretoria Executiva'`.

---

### Questão 4 (Múltiplas Tabelas e Agregação)
**Enunciado:** Em um sistema de vendas estruturado com as tabelas `clientes`, `pedidos`, `itens_pedido` e `produtos`, a gerência deseja saber o **faturamento total gerado por cada cliente**. 
* **Tarefa:** Elabore uma consulta SQL utilizando múltiplos `JOINs` adequados, funções de agregação (`SUM` e a multiplicação de `quantidade * preco_unitario`), e agrupamento (`GROUP BY`) que exiba o identificador do cliente, o nome do cliente e o valor total acumulado de suas compras, ordenando o resultado do maior faturamento para o menor.

---

### Questão 5 (Auditoria com FULL OUTER JOIN)
**Enunciado:** O setor de TI precisa realizar uma auditoria de integridade entre duas tabelas de um sistema legado que foram parcialmente dessincronizadas: `clientes` e `pedidos_migrados`. Deseja-se identificar tanto clientes que não possuem pedidos registrados quanto pedidos que apontam para clientes inexistentes na base principal.
* **Tarefa:** Escreva uma instrução SQL utilizando `FULL OUTER JOIN` que retorne o ID e o nome do cliente (da tabela `clientes`) juntamente com o ID do pedido e o status (da tabela `pedidos_migrados`), evidenciando todas as inconsistências possíveis entre os dois lados. Adicione um filtro (`WHERE`) para isolar apenas as linhas onde ocorreu discrepância (ou seja, onde o ID do cliente é nulo ou o ID do pedido é nulo).

---

## GABARITO (Respostas Esperadas — Discursivas e Práticas)

* **Gabarito Q1:**
  ```sql
  SELECT pr.nome AS nome_produto, pr.preco, COALESCE(ca.nome, 'Sem categoria') AS categoria 
  FROM produtos pr 
  LEFT JOIN categorias ca ON pr.id_categoria = ca.id_categoria;
  ```
* **Gabarito Q2:**
  * *Explicação:* O filtro colocado no `WHERE (p.status = 'Cancelado')` é processado **após** a junção. Como os clientes sem pedidos geram valores `NULL` nas colunas de pedidos, a condição de status falha para eles (pois `NULL = 'Cancelado'` não é verdadeiro), eliminando-os do resultado final e anulando o propósito do `LEFT JOIN`.
  * *Correção (movendo o filtro para a condição `ON`):*
    ```sql
    SELECT c.id_cliente, c.nome, p.id_pedido, p.status 
    FROM clientes c 
    LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente AND p.status = 'Cancelado';
    ```
* **Gabarito Q3:**
  ```sql
  SELECT f.nome AS funcionario, f.cargo, CO