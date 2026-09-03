# Resumo Consolidado: [Prof. Guilherme de Morais] Banco de Dados II

## 1. Visão Geral e Objetivos da Matéria
A disciplina de **Banco de Dados II**, ministrada pelo **Prof. Guilherme de Morais**, aprofunda o conhecimento em Sistemas de Gerenciamento de Bancos de Dados Relacionais (SGBDRs) com foco intenso na manipulação, consulta e tratamento de dados através da linguagem **SQL (Structured Query Language)**. 

O objetivo central é capacitar o estudante de Sistemas de Informação a extrair inteligência de negócios a partir de bases de dados relacionais estruturadas. Isso é feito por meio de operações que vão desde simples filtragens e ordenações até junções (*joins*) complexas, funções agregadas e manipulação avançada de tipos de dados (como datas, horas e strings), preparando o aluno para os desafios reais do mercado de desenvolvimento e análise de dados.

---

## 2. Conceitos-Chave e Terminologia Fundamental
* **SGBD (Sistema de Gerenciamento de Banco de Dados):** Software responsável por gerenciar o banco, garantindo integridade, segurança e eficiência (ex: PostgreSQL, MySQL).
* **Chave Primária (`PRIMARY KEY`):** Identificador único para cada registro em uma tabela, garantindo a unicidade e a integridade entitária.
* **Chave Estrangeira (`FOREIGN KEY`):** Campo ou conjunto de campos que faz referência à chave primária de outra tabela, estabelecendo o relacionamento relacional (integridade referencial).
* **Predicado `SELECT`:** O comando fundamental para recuperação de dados, atuando como a interface entre o usuário e o armazenamento persistente sem modificar os dados originais.
* **Case Sensitivity:** Sensibilidade a maiúsculas e minúsculas (crucial na diferenciação entre operadores como `LIKE` e `ILIKE`).

---

## 3. Principais Módulos / Tópicos Abordados (com explicações técnicas)

### Módulo 1: Fundamentos de Consultas e Filtragem (`SELECT`, `WHERE`, `ORDER BY`, `DISTINCT`)
* **Projeção e Seleção:** Uso do `SELECT` para escolher colunas específicas e do `WHERE` para filtrar linhas baseadas em operadores relacionais (`=`, `>`, `<`, `<=`, `<>`).
* **Operadores Lógicos (`AND`, `OR`):** Combinam múltiplos critérios de filtragem para refinar resultados.
* **Ordenação (`ORDER BY`):** Organiza o resultado da consulta de forma ascendente (`ASC`) ou descendente (`DESC`).
* **Eliminação de Duplicidades (`DISTINCT`):** Retorna apenas registros únicos para uma determinada coluna ou projeção.

### Módulo 2: Manipulação Avançada de Padrões e Textos (`LIKE` / `ILIKE`)
* O operador `LIKE` permite buscas baseadas em padrões de strings utilizando curingas:
  * `%`: Representa zero ou mais caracteres.
  * `_`: Representa exatamente um caractere coringa.
* *Nota técnica:* O `LIKE` é *case-sensitive*, enquanto o `ILIKE` (comum em SGBDs como o PostgreSQL) ignora diferenças entre maiúsculas e minúsculas.

### Módulo 3: Funções Agregadas e Agrupamento (`GROUP BY`)
* Utilizadas para realizar cálculos estatísticos em conjuntos de linhas:
  * `AVG(coluna)`: Calcula a média aritmética.
  * `COUNT(*)` / `COUNT(coluna)`: Conta o número de ocorrências ou registros.
  * `MAX(coluna)` e `MIN(coluna)`: Retornam os valores extremos.
  * `SUM(coluna)`: Totaliza os valores numéricos.
* *Regra de Ouro:* Em consultas com funções agregadas, todas as colunas projetadas que não fazem parte de uma função agregada **devem** constar obrigatoriamente na cláusula `GROUP BY`. O `WHERE` filtra antes da agregação.

### Módulo 4: Manipulação de Datas, Horas e Strings
* **Data e Hora:** Funções como `NOW()` (data/hora atual), `DATE()` (extração de data pura), `AGE()` (cálculo de intervalos/idade) e `EXTRACT(PART FROM DATE)` (para extrair ano, mês, dia ou hora).
* **Manipulação de Strings:** Concatenação (`||`), `LENGTH()` (tamanho da string), `LOWER()` / `UPPER()` (conversão de caixa) e `ASCII()` (conversão de caracteres para códigos numéricos).

### Módulo 5: Relacionamentos entre Múltiplas Tabelas e Chaves Estrangeiras
* Integração de dados de duas ou mais tabelas (ex: `Clientes` e `Pedidos`, ou `Clientes` e `Veículos`) através de junções relacionais utilizando as chaves primárias e estrangeiras. Permite responder a perguntas complexas de negócio, como o total gasto por cliente, maiores pedidos individuais e identificação de clientes inativos.

---

## 4. Relações com o Mercado e Prática Profissional
No mercado de Tecnologia da Informação, a proficiência em SQL não é apenas um diferencial, mas um pré-requisito fundamental para Desenvolvedores Back-End, Cientistas de Dados, Analistas de BI (Business Intelligence) e Engenheiros de Dados. 

As competências trabalhadas nesta disciplina refletem diretamente cenários reais do dia a dia corporativo:
* **Construção de Relatórios Gerenciais:** Uso de funções agregadas e agrupamentos para dashboards financeiros e operacionais.
* **Integridade de Dados:** Modelagem correta com chaves primárias e estrangeiras, evitando anomalias de inserção, atualização e exclusão em sistemas transacionais (OLTP).
* **Performance e Otimização:** Capacidade de escrever consultas eficientes que reduzem o custo computacional no acesso a grandes volumes de dados.

---

## 5. Dicas de Ouro para Estudo e Provas

1. **Atenção à Ordem de Execução do SQL:** Lembre-se de que o SGBD processa as consultas em uma ordem lógica específica (`FROM` ➔ `WHERE` ➔ `GROUP BY` ➔ `HAVING` ➔ `SELECT` ➔ `ORDER BY`). Isso explica por que você não pode usar uma coluna criada no `SELECT` diretamente no `WHERE`.
2. **Cuidado com Funções Agregadas no `WHERE`:** O `WHERE` filtra linhas individuais *antes* do agrupamento. Se precisar filtrar o resultado de uma função agregada (ex: "mostrar apenas clientes com mais de 3 pedidos"), utilize a cláusula `HAVING`.
3. **Domine os Curingas do `LIKE`:** Em questões de prova, revise bem o uso do `%` e do `_`. Diferencie buscas que começam com uma letra (`A%`), terminam (`%A`) ou contêm (`%A%`).
4. **Pratique a Concatenação e Funções de Data:** Exercícios práticos envolvendo `AGE()`, `EXTRACT()` e o operador `||` costumam aparecer em avaliações para testar a manipulação e formatação de saídas textuais e temporais.
5. **Valide a Lógica dos Relacionamentos:** Sempre verifique se as consultas envolvendo duas ou mais tabelas possuem a condição de junção correta (`ON TabelaA.Chave = TabelaB.Chave`), evitando a geração de produtos cartesianos indesejados.