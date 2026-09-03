# 📘 BD II — CHEAT SHEET DE REVISÃO (Prof. Guilherme de Morais)

---

## 1. DDL, DML & RESTRIÇÕES (CONSTRAINTS)
```sql
-- Criação de Tabelas com Chave Primária (PK) e Estrangeira (FK)
CREATE TABLE clientes (
    cpf INT NOT NULL, nome VARCHAR(50), cidade VARCHAR(50), estado CHAR(2), salario INT, idade INT,
    CONSTRAINT PK_CPFcliente PRIMARY KEY (cpf)
);

CREATE TABLE veiculos (
    placa VARCHAR(10) NOT NULL, modelo VARCHAR(20), preco_venda INT, cpf_cli INT NOT NULL,
    CONSTRAINT pk_placa PRIMARY KEY (placa)
);

-- Adicionar FK pós-criação (Alter Table)
ALTER TABLE veiculos ADD CONSTRAINT fk_cpf_cli FOREIGN KEY (cpf_cli) REFERENCES clientes (cpf);
```

---

## 2. FILTRAGEM AVANÇADA (WHERE, LIKE, BETWEEN, IN)
*   **`LIKE` vs `ILIKE`**: `LIKE` é *case-sensitive* (diferencia maiúsculas/minúsculas). `ILIKE` é *case-insensitive*.
*   **Curingas**: `%` (qualquer sequência de 0 ou mais caracteres) | `_` (exatamente um caractere qualquer).
    *   `WHERE nome LIKE 'A_%S'`: Começa com 'A', tem pelo menos uma letra no meio, e termina com 'S'.
*   **`BETWEEN`**: Intervalo inclusivo. `WHERE salario BETWEEN 2000 AND 4000`.
*   **`IN`**: Múltiplos valores possíveis. `WHERE estado IN ('SP', 'RJ', 'MG')`.

---

## 3. FUNÇÕES DE STRING, DATA E HORA

### 📅 Funções de Data e Hora
*   **`NOW()`**: Retorna data e hora atual do sistema.
*   **`DATE(NOW())`**: Extrai apenas a data (AAAA-MM-DD).
*   **`AGE(data_fim, data_inicio)`**: Calcula o intervalo entre duas datas.
    *   *Exemplo*: `AGE(NOW(), data_nascimento)` $\rightarrow$ Retorna a idade exata.
*   **`EXTRACT(part FROM data)`**: Extrai `YEAR`, `MONTH`, `DAY`, `HOUR`.
    *   *Exemplo*: `EXTRACT(YEAR FROM NOW())` $\rightarrow$ Ano atual.
*   **Filtro por Intervalo**: `WHERE AGE(NOW(), data_nasc) > INTERVAL '30 years'`.

### 🔤 Funções de String (Manipulação de Texto)
*   **`||` (Concatenação)**: Une strings. `'Olá' || ' Mundo'` ou `nome || ' mora em ' || cidade`.
*   **`LENGTH(texto)`**: Retorna a quantidade de caracteres.
*   **`LOWER(texto)` / `UPPER(texto)`**: Converte para minúsculo / maiúsculo.
*   **`ASCII(caractere)`**: Código ASCII do primeiro caractere. `ASCII('A')` $\rightarrow$ `65`.
*   **`SUBSTRING(texto FROM inicio FOR tamanho)`**: Extrai pedaço do texto.
    *   *Exemplo*: `SUBSTRING(nome FROM 1 FOR 1)` $\rightarrow$ Primeira letra do nome.

---

## 4. FUNÇÕES AGREGADAS & AGRUPAMENTO (`GROUP BY` / `HAVING`)
*   **Funções**: `AVG(col)` (Média), `COUNT(col)` (Contagem), `MAX(col)` (Máximo), `MIN(col)` (Mínimo), `SUM(col)` (Soma).
*   **⚠️ Regras de Ouro**:
    1.  Funções agregadas **NÃO** podem ser usadas na cláusula `WHERE`.
    2.  Qualquer coluna no `SELECT` que não esteja dentro de uma função agregada **DEVE** constar na cláusula `GROUP BY`.
    3.  Para filtrar resultados agregados, usa-se **`HAVING`** após o `GROUP BY`.

```sql
-- Exemplo: Quantidade de pedidos e total gasto por cliente (apenas clientes com mais de 1 pedido)
SELECT ClienteID, COUNT(PedidoID) AS Qtd, SUM(Valor) AS Total
FROM Pedidos
GROUP BY ClienteID
HAVING COUNT(PedidoID) > 1;
```

---

## 5. CONSULTAS MULTI-TABELAS (JOINS)
*   **`INNER JOIN`**: Retorna apenas registros que possuem correspondência em ambas as tabelas.
*   **`LEFT JOIN`**: Retorna todos os registros da tabela da esquerda, e os correspondentes da direita. Se não houver correspondência, retorna `NULL` (essencial para encontrar "órfãos" ou "sem associação").

```sql
-- 1. Listar todos os clientes e seus respectivos pedidos (Apenas quem comprou)
SELECT C.Nome, P.PedidoID, P.Valor 
FROM Clientes C 
INNER JOIN Pedidos P ON C.ClienteID = P.ClienteID;

-- 2. Exibir clientes que NÃO realizaram nenhum pedido (Uso do LEFT JOIN + IS NULL)
SELECT C.Nome, C.Cidade 
FROM Clientes C 
LEFT JOIN Pedidos P ON C.ClienteID = P.ClienteID 
WHERE P.PedidoID IS NULL;
```

---

## 6. RESOLUÇÃO RÁPIDA DE EXERCÍCIOS TÍPICOS (GABARITO DE SINTAXE)

| Objetivo / Exercício | Sintaxe SQL Resolvida |
| :--- | :--- |
| **Aumento de 10% no salário** | `SELECT nome, Salario_Fixo * 1.10 AS NovoSalario FROM VENDEDOR;` |
| **Desconto de 12% para unidade 'M'** | `SELECT Descricao, Val_Unit * 0.88 FROM PRODUTO WHERE Unidade = 'M';` |
| **Alunos de Campinas pós-1990** | `SELECT * FROM ALUNO WHERE Cidade = 'Campinas' AND Data_Nasc > '1990-12-31';` |
| **Produtos unidade 'M' com valor 0.50 ou 2.00** | `SELECT * FROM PRODUTO WHERE Unidade = 'M' AND Val_Unit IN (0