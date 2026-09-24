# Aula 03 — Stored Procedures e Programacao PL/pgSQL

> **Professor:** Welington Garcia
> **Disciplina:** Tópicos Avançados em Banco de Dados (4º Semestre)
> **Tema:** Encapsulamento de regras de negocio e logica procedural no PostgreSQL utilizando PL/pgSQL.

## Sumário
- [Objetivo da aula](#objetivo-da-aula)
- [Contexto e pré-requisitos](#contexto-e-pré-requisitos)
- [Conceito de Stored Procedures](#conceito-de-stored-procedures)
- [Introducao ao PL/pgSQL](#introducao-ao-plpgsql)
- [Parametros de entrada e saida em procedures](#parametros-de-entrada-e-saida-em-procedures)
- [Declaracao e uso de variaveis](#declaracao-e-uso-de-variaveis)
- [Estruturas condicionais (IF/ELSE)](#estruturas-condicionais-ifelse)
- [Execucao de comandos DML dentro de procedures](#execucao-de-comandos-dml-dentro-de-procedures)
- [Código da aula](#código-da-aula)
- [Exercícios](#exercícios)
- [Erros comuns e boas práticas](#erros-communs-e-boas-práticas)
- [Links e materiais complementares](#links-e-materiais-complementares)
- [Mapa da aula](#mapa-da-aula)
- [Glossário](#glossário)
- [Pontos-chave para a prova](#pontos-chave-para-prova)
- [Perguntas e respostas (JSONL)](#perguntas-e-respostas-jsonl)
- [Checklist de revisão](#checklist-de-revisão)

## Objetivo da aula

Compreender o conceito de Stored Procedures (procedimentos armazenados) e aprender a criar e manipular procedimentos utilizando a linguagem procedural PL/pgSQL no PostgreSQL. O aluno será capaz de encapsular regras de negócio complexas, utilizar parâmetros de entrada e saída, declarar variáveis com escopo controlado, aplicar estruturas condicionais e executar comandos de manipulação de dados (DML) diretamente no servidor de banco de dados.

## Contexto e pré-requisitos

Até o momento, o curso focou em comandos declarativos (SQL puro) para consulta (`SELECT`) e manipulação básica de dados (`INSERT`, `UPDATE`, `DELETE`), além de modelagem relacional, junções (`JOINs`), subconsultas e visões (`views`). 

A partir desta aula, transicionamos da camada de consulta para a programação procedural dentro do SGBD PostgreSQL. Os pré-requisitos fundamentais incluem o domínio completo da sintaxe SQL padrão, compreensão de transações (`BEGIN`, `COMMIT`, `ROLLBACK`) e noções básicas de lógica de programação (estruturas de controle, variáveis e tipos de dados).

## Conceito de Stored Procedures

### O que e uma Procedure

Uma Stored Procedure (ou procedimento armazenado) é um bloco de código nomeado, compilado e armazenado diretamente no catálogo do sistema do SGBD. Ao contrário de uma função tradicional (`FUNCTION`), que obrigatoriamente retorna um valor através da cláusula `RETURN` e pode ser utilizada em expressões SQL (como dentro de um `SELECT`), uma procedure é executada de forma independente através do comando `CALL` e é projetada para realizar operações que envolvem modificações de estado, lógica procedural e controle de transações.

### Vantagens do Encapsulamento

O principal objetivo de utilizar procedures é o encapsulamento de regras de negócio. Em vez de espalhar lógicas complexas de validação e múltiplos comandos DML na aplicação cliente (backend), centralizamos essas regras no banco de dados. Isso reduz o tráfego de rede entre a aplicação e o SGBD, garante consistência independentemente da linguagem de programação cliente utilizada (Java, Python, C#, etc.) e reforça a segurança através do controle de permissões de execução.

### Diagrama de Sequencia da Execucao de uma Procedure

```sequenceDiagram
participant Aplicacao as Aplicação Cliente
participant SGBD as Servidor PostgreSQL
participant Cache as Catálogo / Cache de Plano
participant Dados as Tabelas do Banco

Aplicacao->>SGBD: CALL sp_registrar_cliente('Ana', 'ana@email.com')
SGBD->>Cache: Verifica plano de execução compilado
Cache-->>SGBD: Retorna plano otimizado
SGBD->>Dados: Executa validações e comandos DML (INSERT)
Dados-->>SGBD: Confirmação de persistência
SGBD-->>Aplicacao: Retorno de sucesso (ou exceção)
```

### Tabela Comparativa: Procedure vs View vs Funcao

| Característica | Stored Procedure | View | Função (Function) |
| :--- | :--- | :--- | :--- |
| **Propósito Principal** | Executar ações e lógica procedural | Simplificar e mascarar consultas complexas | Calcular e retornar valores |
| **Comando de Invocação** | `CALL nome_proc();` | `SELECT * FROM nome_view;` | `SELECT nome_func();` ou `SELECT * FROM...` |
| **Retorno de Valores** | Não obrigatório (pode usar parâmetros `OUT`) | Retorna um conjunto de linhas (`SETOF`) | Obrigatoriamente retorna um valor ou tabela |
| **Uso de Transações** | Permite controle transacional (`COMMIT`/`ROLLBACK`)* | Não permite comandos transacionais | Restrito (depende da volatilidade) |

*Nota complementar: No PostgreSQL, o controle transacional direto (`COMMIT` ou `ROLLBACK`) dentro de blocos PL/pgSQL só é permitido em blocos chamados de blocos autônomos ou sob condições muito específicas, sendo mais comum o gerenciamento de transações na chamada externa ou em blocos de exceção controlados.*

## Introducao ao PL/pgSQL

### Arquitetura de Blocos PL/pgSQL

O PL/pgSQL (*Procedural Language/PostgreSQL*) é a linguagem procedural carregada por padrão na maioria das instâncias do PostgreSQL. Ela estende a linguagem SQL adicionando controle de fluxo, variáveis, tratamento de erros e capacidade de computação imperativa.

Todo bloco de código em PL/pgSQL possui uma estrutura baseada em blocos, dividida conceitualmente em seções opcionais de declaração e uma seção obrigatória de execução:

```
[ DECLARE ]
    -- Seção opcional: declaração de variáveis, constantes e aliases
BEGIN
    -- Seção obrigatória: comandos SQL e procedurais
    -- [ EXCEPTION ]
    --     Seção opcional: tratamento de erros e exceções
END;
```

### Tratamento de Escopo

O escopo das variáveis declaradas na seção `DECLARE` é restrito ao bloco onde foram definidas. É possível aninhar blocos (`BEGIN...END` dentro de outros blocos), criando sub-escopos que herdam ou mascaram variáveis do escopo externo.

### Fluxo de Execucao em PL/pgSQL

```flowchart
TD
    A[Início da Chamada CALL] --> B[Entrada no Bloco BEGIN]
    B --> C{Há Declarações?}
    C -->|Sim| D[Aloca Variáveis e Atribui Defaults]
    C -->|Não| E[Executa Comandos DML / Lógica]
    D --> E
    E --> F{Ocorre Erro?}
    F -->|Sim| G[Desvia para Bloco EXCEPTION]
    F -->|Não| H[Finaliza Execução com Sucesso]
    G --> H
    H --> I[Fim da Procedure]
```

### Tabela Comparativa: SQL Puro vs PL/pgSQL

| Característica | SQL Puro (Declarativo) | PL/pgSQL (Procedural) |
| :--- | :--- | :--- |
| **Foco** | O *que* deve ser retornado/modificado | *Como* o processo deve ser executado passo a passo |
| **Controle de Fluxo** | Inexistente (apenas predicados WHERE/HAVING) | Uso de `IF`, `CASE`, `LOOP`, `WHILE`, `FOR` |
| **Variáveis** | Não suporta variáveis locais de controle | Suporte robusto a variáveis e tipos ancorados |
| **Desempenho** | Ideal para consultas em lote e agregações | Ideal para lógica sequencial e regras de negócio complexas |

## Parametros de entrada e saida em procedures

### Tipos de Parametros

As procedures aceitam parâmetros que definem como os dados entram e saem do bloco procedural. No PostgreSQL, utilizamos três qualificadores principais:
1. `IN`: Parâmetro de entrada (padrão). O valor é fornecido pelo chamador e não pode ser alterado de forma a retornar ao chamador (passagem por valor).
2. `OUT`: Parâmetro de saída. O parâmetro inicia sem valor e seu conteúdo final, atribuído dentro da procedure, é retornado ao chamador.
3. `INOUT`: Parâmetro bidirecional. O chamador fornece um valor inicial e a procedure pode ler e modificar esse valor, retornando o resultado atualizado.

### Sintaxe de Criacao com Parametros

Para criar uma procedure, empregamos o comando `CREATE OR REPLACE PROCEDURE`:

```sql
CREATE OR REPLACE PROCEDURE sp_atualizar_status_pedido(
    IN p_pedido_id INT,
    IN p_novo_status VARCHAR,
    OUT p_linhas_afetadas INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE pedidos
    SET status = p_novo_status,
        data_atualizacao = CURRENT_TIMESTAMP
    WHERE id = p_pedido_id;
    
    -- Retorna a quantidade de linhas afetadas pelo comando anterior
    GET DIAGNOSTICS p_linhas_afetadas = ROW_COUNT;
END;
$$;
```

### Chamada de Procedures com Parametros

A invocação de procedures que possuem parâmetros de saída (`OUT` ou `INOUT`) exige o uso do comando `CALL` repassando variáveis para capturar os retornos:

```sql
DO $$
DECLARE
    v_afetados INT;
BEGIN
    CALL sp_atualizar_status_pedido(1024, 'PROCESSADO', v_afetados);
    RAISE NOTICE 'Linhas atualizadas: %', v_afetados;
END;
$$;
```

### Fluxo de Passagem de Parametros

```flowchart
TD
    A[Chamada CALL com Argumentos] --> B{Tipo do Parâmetro?}
    B -->|IN| C[Copia valor para escopo interno]
    B -->|OUT| D[Reserva espaço sem valor inicial]
    B -->|INOUT| E[Copia valor inicial para escopo interno]
    C --> F[Execução do Corpo da Procedure]
    D --> F
    E --> F
    F --> G[Fim da Execução]
    G --> H[Retorna valores dos parâmetros OUT e INOUT]
```

### Tabela Comparativa: Modificadores de Parametros

| Modificador | Direção do Fluxo | Valor Inicial na Procedure | Valor Final Retornado |
| :--- | :--- | :--- | :--- |
| `IN` | Entrada | Disponível | Ignorado / Não retorna |
| `OUT` | Saída | Nulo (`NULL`) | Retornado ao chamador |
| `INOUT` | Entrada e Saída | Disponível | Modificado e retornado |

## Declaracao e uso de variaveis

### Atribuicao de Valores

Na seção `DECLARE`, declaramos variáveis informando o nome seguidos do tipo de dado. Podemos atribuir valores padrão utilizando a palavra-chave `DEFAULT` ou o operador `:=`. Dentro do bloco `BEGIN`, a atribuição de novos valores a variáveis já declaradas também é feita com o operador `:=` ou através do comando `SELECT INTO`.

```sql
DECLARE
    v_contador INT := 0;
    v_nome_cliente VARCHAR(100);
    v_limite_credito NUMERIC(10,2) DEFAULT 1000.00;
BEGIN
    v_contador := v_contador + 1;
    
    -- Atribuindo resultado de uma consulta à variável
    SELECT nome INTO v_nome_cliente 
    FROM clientes 
    WHERE id = 5;
END;
```

### Uso de Tipos Ancorados (%TYPE e %ROWTYPE)

Uma boa prática avançada no PL/pgSQL é o uso de tipos ancorados. Em vez de hardcodar tipos como `VARCHAR(50)` ou `INT`, utilizamos o sufixo `%TYPE` para herdar o tipo de dado de uma coluna de tabela específica, garantindo que se o esquema do banco mudar, a procedure não quebre por incompatibilidade de tipo.

O modificador `%ROWTYPE` permite declarar uma variável que armazena uma linha inteira de uma tabela ou view, cujos campos são acessados via notação de ponto (`v_cliente.nome`, `v_cliente.email`).

### Representacao do Contexto de Variaveis

```classDiagram
class BlocoPLpgSQL {
    +int v_contador
    +varchar v_nome_cliente
    +numeric v_limite_credito
    +atribuirValor()
    +consultarTabela()
}
class TabelaClientes {
    +int id
    +varchar nome
    +varchar email
}
BlocoPLpgSQL ..> TabelaClientes : Usa %TYPE / %ROWTYPE
```

### Tabela Comparativa: Tipos Primitivos vs Tipos Ancorados

| Abordagem | Exemplo | Vantagem Principal | Risco / Desvantagem |
| :--- | :--- | :--- | :--- |
| **Tipo Primitivo** | `v_salario NUMERIC(10,2)` | Simples de definir e isolado | Quebra se o tipo da coluna mudar na tabela |
| **Tipo Ancorado** | `v_salario funcionarios.salario%TYPE` | Resiliente a alterações de esquema | Dependência estrita da estrutura da tabela |
| **Tipo Linha** | `v_func funcionarios%ROWTYPE` | Facilita manipulação de registros completos | Carrega colunas desnecessárias se poucas forem usadas |

## Estruturas condicionais (IF/ELSE)

### Sintaxe Basica do IF

As estruturas condicionais permitem desviar o fluxo de execução com base no resultado de expressões booleanas. A sintaxe básica suporta `IF...THEN`, `ELSIF`, `ELSE` e o fechamento com `END IF;`.

```sql
CREATE OR REPLACE PROCEDURE sp_avaliar_credito(
    IN p_cliente_id INT,
    OUT p_resultado VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_score INT;
BEGIN
    SELECT score_credito INTO v_score
    FROM clientes
    WHERE id = p_cliente_id;

    IF v_score IS NULL THEN
        p_resultado := 'CLIENTE NAO ENCONTRADO';
    ELSIF v_score < 300 THEN
        p_resultado := 'CREDITO REPROVADO';
    ELSIF v_score BETWEEN 300 AND 700 THEN
        p_resultado := 'CREDITO CONDICIONAL';
    ELSE
        p_resultado := 'CREDITO APROVADO';
    END IF;
END;
$$;
```

### Condicionais Aninhadas

É possível aninhar estruturas `IF` dentro de outras, embora o uso excessivo de aninhamento prejudique a legibilidade, sendo recomendável o uso de múltiplos `ELSIF` ou a estrutura `CASE` quando aplicável.

### Fluxo Condicional

```flowchart
TD
    A[Início da Avaliação] --> B{v_score IS NULL?}
    B -->|Sim| C[p_resultado = NAO ENCONTRADO]
    B -->|Não| D{v_score < 300?}
    D -->|Sim| E[p_resultado = REPROVADO]
    D -->|Não| F{v_score <= 700?}
    F -->|Sim| G[p_resultado = CONDICIONAL]
    F -->|Não| H[p_resultado = APROVADO]
    C --> I[Fim da Procedure]
    E --> I
    G --> I
    H --> I
```

### Tabela Comparativa: Condicionais PL/pgSQL

| Estrutura | Caso de Uso Ideal | Legibilidade | Complexidade de Manutenção |
| :--- | :--- | :--- | :--- |
| **IF / THEN / ELSE** | Verificações binárias ou simples | Alta | Baixa |
| **IF / ELSIF / ELSE** | Múltiplas faixas de valores sequenciais | Média | Média |
| **CASE (Instrução)** | Avaliação de igualdade para múltiplas opções | Muito Alta | Baixa |

## Execucao de comandos DML dentro de procedures

### Manipulacao de Dados (DML)

Uma das maiores vantagens das procedures é a execução nativa de comandos DML (`INSERT`, `UPDATE`, `DELETE`) em conjunto com variáveis procedurais. Podemos capturar erros, validar dados antes da inserção e atualizar registros condicionalmente.

### Transacoes e Consistência

Dentro de procedures, comandos DML executam dentro do contexto transacional estabelecido pela chamada. Se ocorrer um erro não tratado, a transação pode ser revertida.

```sql
CREATE OR REPLACE PROCEDURE sp_transferir_fundos(
    IN p_conta_origem INT,
    IN p_conta_destino INT,
    IN p_valor NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_saldo_atual NUMERIC;
BEGIN
    -- Verifica saldo da conta origem
    SELECT saldo INTO v_saldo_atual
    FROM contas
    WHERE id = p_conta_origem;

    IF v_saldo_atual IS NULL THEN
        RAISE EXCEPTION 'Conta de origem % nao encontrada', p_conta_origem;
    END IF;

    IF v_saldo_atual < p_valor THEN
        RAISE EXCEPTION 'Saldo insuficiente. Saldo atual: %, Valor solicitado: %', v_saldo_atual, p_valor;
    END IF;

    -- Executa o débito
    UPDATE contas 
    SET saldo = saldo - p_valor 
    WHERE id = p_conta_origem;

    -- Executa o crédito
    UPDATE contas 
    SET saldo = saldo + p_valor 
    WHERE id = p_conta_destino;

    -- Registra o histórico
    INSERT INTO historico_transacoes (conta_origem, conta_destino, valor, data_hora)
    VALUES (p_conta_origem, p_conta_destino, p_valor, CURRENT_TIMESTAMP);
    
    RAISE NOTICE 'Transferencia de % realizada com sucesso entre contas % e %', p_valor, p_conta_origem, p_conta_destino;
END;
$$;
```

### Transacoes Dentro de Procedures

```sequenceDiagram
participant Cliente as Aplicação
participant Proc as Procedure sp_transferir_fundos
participant Tabela as Tabela Contas

Cliente->>Proc: CALL sp_transferir_fundos(1, 2, 500.00)
Proc->>Tabela: SELECT saldo (origem)
Tabela-->>Proc: Retorna saldo suficiente
Proc->>Tabela: UPDATE saldo (débito origem)
Proc->>Tabela: UPDATE saldo (crédito destino)
Tabela-->>Proc: Confirmação DML
Proc-->>Cliente: Sucesso (Transação efetivada)
```

### Tabela Comparativa: DML Externo vs DML em Procedure

| Critério | DML na Aplicação Externa | DML em Stored Procedure |
| :--- | :--- | :--- |
| **Tráfego de Rede** | Múltiplas idas e voltas | Uma única chamada (`CALL`) |
| **Acoplamento** | Alto acoplamento da regra no código cliente | Baixo acoplamento (regra centralizada no SGBD) |
| **Manutenibilidade** | Difícil alterar regras replicadas em vários sistemas | Fácil alteração centralizada no banco |

---

## Código da aula

Nesta seção, analisamos os arquivos de código desenvolvidos para consolidar os conceitos da aula. Os arquivos encontram-se no diretório `./codigo/`.

### Exemplos de Procedimentos (`./codigo/exemplos_procedures.sql`)

Este arquivo contém a estrutura base de criação de tabelas de exemplo, seguidas por procedures demonstrando declaração de variáveis, parâmetros `IN`/`OUT`, estruturas condicionais e comandos DML.

```sql
-- Arquivo: ./codigo/exemplos_procedures.sql
-- Descrição: Exemplos práticos de Stored Procedures em PL/pgSQL

DROP TABLE IF EXISTS contas CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    score_credito INT DEFAULT 500
);

CREATE TABLE contas (
    id SERIAL PRIMARY KEY,
    cliente_id INT REFERENCES clientes(id),
    saldo NUMERIC(12,2) DEFAULT 0.00
);

-- Exemplo 1: Procedure simples com inserção e validação de e-mail
CREATE OR REPLACE PROCEDURE sp_cadastrar_cliente(
    IN p_nome VARCHAR,
    IN p_email VARCHAR,
    OUT p_cliente_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM clientes WHERE email = p_email) THEN
        RAISE EXCEPTION 'O e-mail % ja esta cadastrado no sistema.', p_email;
    END IF;

    INSERT INTO clientes (nome, email)
    VALUES (p_nome, p_email)
    RETURNING id INTO p_cliente_id;
    
    RAISE NOTICE 'Cliente cadastrado com sucesso com ID: %', p_cliente_id;
END;
$$;
```

*Explicação do código acima:* O script inicializa o ambiente criando tabelas relacionais de suporte. Em seguida, define a procedure `sp_cadastrar_cliente` que verifica a existência prévia do e-mail utilizando `EXISTS`. Caso o e-mail já exista, uma exceção é levantada via `RAISE EXCEPTION`, interrompendo a execução. Caso contrário, o comando `INSERT ... RETURNING id INTO p_cliente_id` persiste o registro e captura o identificador gerado na variável de saída.

---

## Exercícios

Os exercícios práticos propostos para fixação do conteúdo estão implementados e comentados no arquivo `./codigo/exercicios_procedures.sql`.

### 1. Criação de Procedure de Inserção com Validação

**Enunciado:** Crie uma stored procedure em PL/pgSQL chamada `sp_registrar_cliente` que receba nome e e-mail como parâmetros. A procedure deve verificar se o e-mail já existe na tabela `clientes` antes de realizar o `INSERT`. Caso o e-mail já esteja cadastrado, deve levantar uma exceção com mensagem amigável.

**Raciocínio:** Utilizamos uma verificação condicional baseada na função `EXISTS` combinada com a estrutura `IF`. Se a condição for verdadeira, disparamos um erro com `RAISE EXCEPTION`. Se for falsa, executamos o comando de inserção de forma segura.

**Resolução Comentada:**
```sql
CREATE OR REPLACE PROCEDURE sp_registrar_cliente(
    IN p_nome VARCHAR,
    IN p_email VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validação de duplicidade de e-mail
    IF EXISTS (SELECT 1 FROM clientes WHERE email = p_email) THEN
        RAISE EXCEPTION 'Operacao abortada: O e-mail "%" ja se encontra cadastrado.', p_email;
    END IF;

    -- Inserção do novo registro
    INSERT INTO clientes (nome, email)
    VALUES (p_nome, p_email);

    RAISE NOTICE 'Cliente % cadastrado com sucesso.', p_nome;
END;
$$;
```
*Link para o arquivo de código:* `./codigo/exercicios_procedures.sql`

---

### 2. Procedure para Atualização Salarial com Condicional

**Enunciado:** Desenvolva uma procedure chamada `sp_reajustar_salario` que receba o ID do departamento e o percentual de reajuste. A procedure deve aplicar o reajuste salarial na tabela `funcionarios` apenas para os funcionários do departamento especificado cujo salário atual seja inferior a um determinado limite.

**Raciocínio:** A procedure precisa aceitar parâmetros de entrada (`p_departamento_id`, `p_percentual`, `p_limite_salario`), e executar um comando `UPDATE` com cláusula `WHERE` combinando o departamento e o limite salarial estipulado.

**Resolução Comentada:**
```sql
CREATE OR REPLACE PROCEDURE sp_reajustar_salario(
    IN p_departamento_id INT,
    IN p_percentual NUMERIC,
    IN p_limite_salario NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_linhas_afetadas INT;
BEGIN
    UPDATE funcionarios
    SET salario = salario * (1 + (p_percentual / 100.0))
    WHERE departamento_id = p_departamento_id
      AND salario < p_limite_salario;

    GET DIAGNOSTICS v_linhas_afetadas = ROW_COUNT;
    RAISE NOTICE 'Reajuste aplicado. Funcionarios atualizados: %', v_linhas_afetadas;
END;
$$;
```
*Link para o arquivo de código:* `./codigo/exercicios_procedures.sql`

---

### 3. Procedure de Transferência de Saldos (Controle Transacional)

**Enunciado:** Implemente uma stored procedure `sp_transferir_fundo` que receba `conta_origem`, `conta_destino` e `valor`. A procedure deve verificar se a conta de origem possui saldo suficiente, realizar o débito na origem, o crédito no destino e registrar a movimentação em uma tabela de histórico.

**Raciocínio:** O procedimento requer validações rigorosas de saldo e existência de contas, seguidas por operações sequenciais de atualização de saldos (`UPDATE`) e inserção de log (`INSERT`).

**Resolução Comentada:**
```sql
CREATE OR REPLACE PROCEDURE sp_transferir_fundo(
    IN p_conta_origem INT,
    IN p_conta_destino INT,
    IN p_valor NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_saldo_origem NUMERIC;
BEGIN
    -- Obter e travar saldo da conta de origem
    SELECT saldo INTO v_saldo_origem
    FROM contas
    WHERE id = p_conta_origem
    FOR UPDATE;

    IF v_saldo_origem IS NULL THEN
        RAISE EXCEPTION 'Conta de origem inexistente.';
    END IF;

    IF v_saldo_origem < p_valor THEN
        RAISE EXCEPTION 'Saldo insuficiente para transferencia.';
    END IF;

    -- Atualiza origem
    UPDATE contas SET saldo = saldo - p_valor WHERE id = p_conta_origem;

    -- Atualiza destino
    UPDATE contas SET saldo = saldo + p_valor WHERE id = p_conta_destino;

    -- Registra historico
    INSERT INTO historico_transacoes (conta_origem, conta_destino, valor, data)
    VALUES (p_conta_origem, p_conta_destino, p_valor, CURRENT_TIMESTAMP);
    
    RAISE NOTICE 'Transferencia concluída com sucesso.';
END;
$$;
```
*Link para o arquivo de código:* `./codigo/exercicios_procedures.sql`

---

## Erros comuns e boas práticas

### Erros Comuns
- **Esquecer o ponto e vírgula (`;`)**: Cada instrução individual dentro de blocos PL/pgSQL deve terminar obrigatoriamente com ponto e vírgula, exceto estruturas compostas em sua terminação.
- **Confundir Function com Procedure**: Tentar utilizar uma procedure (`CALL`) dentro de uma instrução `SELECT` em uma query de consulta (as procedures não retornam conjuntos de linhas inline como funções).
- **Falta de tratamento para valores nulos (`NULL`)**: Executar operações aritméticas com variáveis não inicializadas que contenham `NULL`, resultando em propagação de nulos.

### Boas Práticas
- **Utilizar tipos ancorados (`%TYPE`)**: Sempre que possível, vincule variáveis aos tipos das colunas das tabelas para evitar quebras por alterações de tipo no esquema.
- **Validação precoce (*Fail Fast*)**: Valide parâmetros de entrada e restrições de negócio no início da procedure antes de executar operações pesadas de DML.
- **Comentários descritivos**: Documente regras de negócio complexas e restrições transacionais diretamente no código PL/pgSQL.

## Links e materiais complementares

- [Documentação Oficial do PostgreSQL - PL/pgSQL](https://www.postgresql.org/docs/current/plpgsql.html): Guia completo e referência da linguagem procedural.
- [Repositório de Exemplos da Disciplina](./codigo/): Contém todos os scripts `.sql` utilizados nas aulas práticas.
- [Artigo: Boas Práticas em Stored Procedures](https://wiki.postgresql.org/wiki/Plpgsql): Recomendações da comunidade PostgreSQL sobre desempenho e segurança.

## Mapa da aula

```mindmap
root((Stored Procedures & PL/pgSQL))
    Conceito
        Definição
        Encapsulamento
        Vantagens vs Funções
    PL/pgSQL
        Bloco DECLARE
        Bloco BEGIN
        Tratamento de Escopo
    Parâmetros
        IN (Entrada)
        OUT (Saída)
        INOUT (Bidirecional)
    Variáveis
        Tipos Primitivos
        Tipos Ancorados %TYPE
        Tipos Linha %ROWTYPE
    Condicionais
        IF / THEN
        ELSIF / ELSE
        CASE
    Comandos DML
        INSERT / UPDATE / DELETE
        SELECT INTO
        Transações e Histórico
```

## Glossário

| Termo | Definição |
| :--- | :--- |
| **Stored Procedure** | Rotina procedural armazenada no catálogo do SGBD, executada via comando `CALL`. |
| **PL/pgSQL** | Linguagem procedural padrão do PostgreSQL que adiciona estruturas de controle ao SQL. |
| **%TYPE** | Atributo PL/pgSQL utilizado para herdar o tipo de dado de uma coluna de tabela existente. |
| **DML** | *Data Manipulation Language* (comandos como `INSERT`, `UPDATE`, `DELETE`). |
| **Escopo** | Delimitação de visibilidade de variáveis dentro de blocos de código aninhados. |
| **Parâmetro OUT** | Parâmetro utilizado para retornar valores calculados de dentro de uma procedure para o chamador. |

## Pontos-chave para a prova

1. A diferença estrutural e sintática entre uma função (`FUNCTION`) e um procedimento (`PROCEDURE`) no PostgreSQL.
2. A importância e a sintaxe correta do uso de parâmetros `IN`, `OUT` e `INOUT`.
3. A estrutura de blocos do PL/pgSQL (`DECLARE`, `BEGIN`, `END`).
4. O uso de tipos ancorados (`%TYPE`) para garantir robustez frente a alterações no esquema do banco de dados.
5. A capacidade de executar comandos DML e gerenciar lógica condicional (`IF/ELSE`) dentro de procedimentos armazenados.

## Perguntas e respostas (JSONL)

```jsonl
{"pergunta": "Qual comando SQL é utilizado para invocar uma Stored Procedure no PostgreSQL?", "resposta": "O comando CALL.", "dificuldade": "fácil"}
{"pergunta": "Qual é a principal diferença entre uma função e uma procedure no PostgreSQL?", "resposta": "Funções retornam obrigatoriamente valores e podem ser usadas em expressões SQL, enquanto procedures são chamadas via CALL e focam em ações e controle transacional.", "dificuldade": "média"}
{"pergunta": "O que significa o modificador de parâmetro OUT em uma procedure?", "resposta": "Indica que o parâmetro é de saída, servindo para retornar um valor calculado para o escopo chamador.", "dificuldade": "fácil"}
{"pergunta": "Qual é a utilidade do sufixo %TYPE na declaração de variáveis em PL/pgSQL?", "resposta": "Permite herdar o tipo de dado de uma coluna de tabela específica, garantindo resiliência a alterações de esquema.", "dificuldade": "média"}
{"pergunta": "Qual bloco em PL/pgSQL é utilizado para a declaração de variáveis locais?", "resposta": "O bloco DECLARE.", "dificuldade": "fácil"}
{"pergunta": "Como é feita a atribuição de uma consulta SELECT para uma variável dentro de um bloco PL/pgSQL?", "resposta": "Utilizando a cláusula INTO (ex: SELECT coluna INTO variavel FROM tabela).", "dificuldade": "média"}
{"pergunta": "O que ocorre se uma exceção não tratada for levantada dentro de uma procedure em execução?", "resposta": "A execução é interrompida e a transação em curso pode ser revertida.", "dificuldade": "média"}
{"pergunta": "Qual estrutura condicional é nativamente suportada em PL/pgSQL para desvios lógicos?", "resposta": "A estrutura IF / THENC / ELSIF / ELSE e o comando CASE.", "dificuldade": "fácil"}
{"pergunta": "É permitido executar comandos DML como INSERT e UPDATE dentro de procedures?", "resposta": "Sim, procedimentos armazenados são ideais para encapsular operações DML e regras de negócio.", "dificuldade": "fácil"}
{"pergunta": "O que significa a sigla PL/pgSQL?", "resposta": "Procedural Language/PostgreSQL.", "dificuldade": "fácil"}
{"pergunta": "Qual comando é utilizado para capturar o número de linhas afetadas por um comando DML anterior?", "resposta": "GET DIAGNOSTICS variavel = ROW_COUNT;", "dificuldade": "avançada"}
{"pergunta": "Qual é a principal vantagem de encapsular regras de negócio em Stored Procedures?", "resposta": "Redução do tráfego de rede, centralização das regras e independência de linguagem na aplicação cliente.", "dificuldade": "média"}
{"pergunta": "Como é encerrada uma instrução condicional IF em PL/pgSQL?", "resposta": "Com a palavra-chave END IF;", "dificuldade": "fácil"}
{"pergunta": "Qual é a função do operador := em blocos PL/pgSQL?", "resposta": "É o operador de atribuição de valores a variáveis.", "dificuldade": "fácil"}
{"pergunta": "Onde ficam armazenadas as Stored Procedures criadas em um banco PostgreSQL?", "resposta": "No catálogo do sistema do banco de dados.", "dificuldade": "média"}
```

## Checklist de revisão

- [ ] Compreendi a diferença conceitual entre Stored Procedures e Funções.
- [ ] Sei estruturar um bloco PL/pgSQL básico utilizando `DECLARE`, `BEGIN` e `END`.
- [ ] Entendi o funcionamento dos parâmetros `IN`, `OUT` e `INOUT`.
- [ ] Sei declarar variáveis utilizando tipos primitivos e tipos ancorados (`%TYPE`).
- [ ] Apliquei estruturas condicionais (`IF/ELSE`) em cenários de validação.
- [ ] Implementei comandos DML (`INSERT`, `UPDATE`) dentro de procedures.
- [ ] Revisei o código de exemplo e os exercícios práticos no diretório `./codigo/`.
