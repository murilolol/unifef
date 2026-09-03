Aqui está um simulado completo para a disciplina de **Banco de Dados II** (elaborado com base no material do Professor Prof. Guilherme de Morais), contendo **10 questões de múltipla escolha** (com gabarito comentado) e **5 questões práticas de SQL**.

---

# 📝 SIMULADO DE BANCO DE DADOS II
**Professor:** Prof. Guilherme de Morais  
**Disciplina:** Banco de Dados II  

---

## PARTE 1: Questões de Múltipla Escolha (1 a 10)

### 1. Sobre a linguagem SQL e o comando `SELECT`, assinale a alternativa correta:
A) O comando `SELECT` modifica permanentemente os dados armazenados nas tabelas.  
B) O uso do asterisco (`*`) no comando `SELECT` serve para selecionar apenas a primeira coluna de uma tabela.  
C) O comando `SELECT` é utilizado para buscar e consultar informações em um banco de dados sem modificá-las.  
D) A cláusula `FROM` é opcional em qualquer instrução de consulta `SELECT`.  
E) A cláusula `WHERE` deve vir obrigatoriamente após a cláusula `ORDER BY`.  

---

### 2. Para que serve a cláusula `ORDER BY` em uma consulta SQL?
A) Para filtrar linhas com base em uma condição lógica.  
B) Para agrupar registros que possuem valores idênticos em determinadas colunas.  
C) Para limitar o número de linhas retornadas na consulta.  
D) Para ordenar o resultado da consulta com base em uma ou mais colunas (de forma ascendente ou descendente).  
E) Para unir duas ou mais tabelas através de chaves estrangeiras.  

---

### 3. Analise o comando abaixo utilizado no PostgreSQL:
```sql
SELECT nome, AGE(NOW(), data_nascimento) FROM funcionarios;
```
Qual é a principal função da função `AGE()` neste contexto?  
A) Calcular a média aritmética entre duas datas.  
B) Extrair apenas o ano de uma data específica.  
C) Calcular a diferença (idade) entre duas datas (neste caso, a data atual e a data de nascimento).  
D) Converter uma string de texto em formato de data.  
E) Retornar o código ASCII correspondente à primeira letra da data.  

---

### 4. Sobre o operador `LIKE` e seus caracteres coringas (`%` e `_`), assinale a alternativa correta:
A) O caractere `%` representa exatamente um único caractere obrigatório.  
B) O caractere `_` representa qualquer sequência de 0 (zero) ou mais caracteres.  
C) O operador `LIKE` diferencia maiúsculas de minúsculas no PostgreSQL (sendo case-sensitive), exigindo o uso de `ILIKE` caso queira ignorar essa diferenciação.  
D) O operador `LIKE` só pode ser utilizado em conjunto com a cláusula `GROUP BY`.  
E) A condição `WHERE nome LIKE '%a%'` seleciona apenas os registros que começam obrigatoriamente com a letra 'a'.  

---

### 5. No que diz respeito às funções agregadas do SQL (`AVG`, `COUNT`, `MAX`, `MIN`, `SUM`), é correto afirmar que:
A) Elas podem ser utilizadas diretamente na cláusula `WHERE` para filtrar registros individuais antes do agrupamento.  
B) Devem ser utilizadas preferencialmente entre as cláusulas `SELECT` e `FROM`.  
C) Quando utilizadas em um `SELECT`, todas as demais colunas selecionadas que não possuem funções agregadas devem obrigatoriamente fazer parte da cláusula `GROUP BY`.  
D) A função `COUNT(*)` conta apenas os registros que possuem valores nulos (`NULL`).  
E) A função `SUM` serve para calcular a média dos valores de uma coluna numérica.  

---

### 6. Qual função de string é utilizada no SQL para concatenar (unir) textos e colunas?
A) `JOIN`  
B) `CONCAT()` ou o operador `||`  
C) `MERGE()`  
D) `UNION ALL`  
E) `SUBSTRING()`  

---

### 7. Analise a instrução de criação de chave estrangeira a seguir:
```sql
ALTER TABLE veiculos ADD CONSTRAINT fk_cpf_cli FOREIGN KEY (cpf_cli) REFERENCES clientes (cpf);
```
O que essa linha de comando realiza no banco de dados?  
A) Cria uma nova tabela chamada `veiculos`.  
B) Insere um registro na tabela de clientes.  
C) Define a coluna `cpf_cli` da tabela `veiculos` como uma chave estrangeira que faz referência à coluna `cpf` da tabela `clientes`, garantindo integridade referencial.  
D) Exclui a tabela de clientes e todos os seus veículos associados.  
E) Atualiza o preço de compra de todos os veículos cadastrados.  

---

### 8. Assinale a alternativa que apresenta corretamente a sintaxe para listar produtos cujo valor unitário esteja entre 0.50 e 10.00:
A) `SELECT * FROM PRODUTO WHERE Val_Unit BETWEEN 0.50 AND 10.00;`  
B) `SELECT * FROM PRODUTO WHERE Val_Unit > 0.50 OR Val_Unit < 10.00;`  
C) `SELECT * FROM PRODUTO LIMIT 0.50 TO 10.00;`  
D) `SELECT * FROM PRODUTO ORDER BY Val_Unit 0.50, 10.00;`  
E) `SELECT * FROM PRODUTO GROUP BY Val_Unit BETWEEN 0.50 AND 10.00;`  

---

### 9. O operador `DISTINCT` tem qual utilidade em uma consulta `SELECT`?  
A) Ordenar os dados de forma decrescente.  
B) Contar a quantidade total de linhas de uma tabela.  
C) Remover linhas duplicadas do resultado da consulta, exibindo apenas valores distintos.  
D) Juntar duas tabelas através de um produto cartesiano.  
E) Filtrar registros que contenham valores nulos (`NULL`).  

---

### 10. Qual operador lógico é utilizado para garantir que **ambas** as condições especificadas na cláusula `WHERE` sejam verdadeiras para que o registro seja retornado?  
A) `OR`  
B) `NOT`  
C) `AND`  
D) `UNION`  
E) `LIKE`  

---

## GABARITO COMENTADO (Questões de 1 a 10)

*   **Questão 1: C**  
    *Comentário:* O comando `SELECT` é puramente estruturado para consultas e recuperação de dados, não alterando o estado físico ou os valores armazenados nas tabelas (papel realizado pelo `UPDATE`, `INSERT` ou `DELETE`).
*   **Questão 2: D**  
    *Comentário:* `ORDER BY` organiza a ordenação dos dados retornados, podendo ser em ordem crescente (`ASC`) ou decrescente (`DESC`).
*   **Questão 3: C**  
    *Comentário:* A função `AGE()` (comum em SGBDs como o PostgreSQL) calcula o intervalo de tempo transcorrido entre duas datas, sendo muito usada para calcular a idade exata a partir da data de nascimento (`data_nascimento`).
*   **Questão 4: C**  
    *Comentário:* O `LIKE` diferencia maiúsculas de minúsculas no PostgreSQL. Para ignorar essa diferenciação sensível, utiliza-se o operador `ILIKE`. O `%` representa múltiplos caracteres e o `_` um caractere único.
*   **Questão 5: C**  
    *Comentário:* Regra clássica de SGBDs relacionais: quando utilizamos funções agregadas combinadas com colunas comuns no `SELECT`, todas essas colunas não agregadas devem constar obrigatoriamente na cláusula `GROUP BY`. As funções agregadas **não** podem ir no `WHERE` (para isso usa-se o `HAVING`).
*   **Questão 6: B**  
    *Comentário:* Para unir strings no padrão SQL/PostgreSQL, podemos utilizar o operador de concatenação `||` ou a função `CONCAT()`.
*   **Questão 7: C**  
    *Comentário:* O comando `ALTER TABLE ... ADD CONSTRAINT ... FOREIGN KEY ... REFERENCES` define uma restrição de chave estrangeira, garantindo a integridade referencial entre as tabelas envolvidas.
*   **Questão 8: A**  
    *Comentário:* O operador `BETWEEN` é ideal para filtrar intervalos inclusivos de valores, sendo equivalente a `>= valor1 AND <= valor2`.
*   **Questão 9: C**  
    *Comentário:* `DISTINCT` elimina duplicatas das colunas selecionadas no resultado exibido.
*   **Questão 10: C**  
    *Comentário:* O operador lógico `AND` exige que todas as condições conectadas por ele sejam verdadeiras para que a linha seja selecionada.

---

## PARTE 2: Questões Práticas de SQL (11 a 15)

*Utilize como base as tabelas fornecidas nos materiais de apoio (ex: tabelas `Clientes` e `Pedidos` ou `CLIENTES`, `PRODUTO`, `VENDEDOR`, `ALUNO`).*

### Questão 11 (DDL e Restrições / Chaves Estrangeiras)
Escreva o comando SQL em formato **DDL** para criar uma tabela chamada `ItensPedido` que contenha os seguintes campos:
*   `ItemPedidoID` (Inteiro, Chave Primária)
*   `PedidoID` (Inteiro, Chave Estrangeira que faz referência à tabela `Pedidos` na coluna `PedidoID`)
*   `CodigoProduto` (Inteiro, Chave Estrangeira que faz referência à tabela `PRODUTO` na coluna `CodigoProduto`)
*   `Quantidade` (Inteiro)
*   `PrecoUnitario` (Decimal 10,2)

---

### Questão 12 (DML - Insert e Update)
Considerando a tabela `PRODUTO` (com colunas `CodigoProduto`, `Descricao`, `Unidade`, `Val_Unit`), escreva:
1. Um comando `INSERT` para cadastrar um novo produto: Código `5`, Descrição `'Borracha'`, Unidade `'UN'`, Valor Unitário `1.20`.
2. Um comando `UPDATE` para atualizar o valor unitário (`Val_Unit`) desse mesmo produto para `1.50`.

---

### Questão 13 (JOINs e Filtragem)
Escreva uma consulta SQL utilizando **JOIN** que liste o **Nome do Cliente**, a **Data do Pedido** e o **Valor** de todos os pedidos cadastrados, unindo as tabelas `Clientes` e `Pedidos` com base no identificador do cliente (`ClienteID`).

---

### Questão 14 (Funções Agregadas e GROUP BY)
Utilizando as tabelas `Clientes` e `Pedidos` (onde os pedidos estão vinculados aos clientes por `ClienteID`), escreva uma consulta SQL que retorne o **Nome do Cliente** e o **Valor Total Gasto** em pedidos por cada cliente. Ordene o resultado do maior valor para o menor.

---

### Questão 15 (Funções de String, Data e Concatenação)
Baseando-se nas funções estudadas (como `UPPER`, `EXTRACT`, `AGE` ou concatenação `||`), escreva uma consulta SQL aplicada à tabela `funcionarios` (`nome`, `data_nascimento`, `cidade`) que retorne uma única coluna contendo uma frase formatada exatamente no seguinte padrão:  
*(Exemplo de saída desejada)*: `CARLOS SILVA - 1990 - SÃO PAULO`  
*(Dica: utilize maiúsculas para o nome e a cidade, e extraia o ano de nascimento).*

---
---

## GABARITO DAS QUESTÕES PRÁTICAS

### 11. Resposta:
```sql
CREATE TABLE ItensPedido (
    ItemPedidoID INT PRIMARY KEY,
    PedidoID INT,
    CodigoProduto INT,
    Quantidade INT,
    PrecoUnitario DECIMAL(10,2),
    CONSTRAINT fk_pedido FOREIGN KEY (PedidoID) REFERENCES Pedidos(PedidoID),
    CONSTRAINT fk_produto FOREIGN KEY (CodigoProduto) REFERENCES PRODUTO(CodigoProduto)
);
```

### 12. Resposta:
```sql
-- Inserção
INSERT INTO PRODUTO VALUES (5, 'Borracha', 'UN', 1.20);

-- Atualização
UPDATE PRODUTO 
SET Val_Unit = 1.50 
WHERE CodigoProduto = 5;
```

### 13. Resposta:
```sql
SELECT c.Nome, p.DataPedido, p.Valor
FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID;
```

### 14. Resposta:
```sql
SELECT c.Nome, SUM(p.Valor) AS TotalGasto
FROM Clientes c
JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nome
ORDER BY TotalGasto DESC;
```

### 15. Resposta:
```sql
SELECT UPPER(nome) || ' - ' || EXTRACT(YEAR FROM data_nascimento) || ' - ' || UPPER(cidade) AS frase_formatada
FROM funcionarios;
```