# 📚 Banco de Dados II - Prof. Guilherme de Morais

Material estruturado com base nos conteúdos de manipulação de dados (INSERT, DELETE, UPDATE), consultas avançadas (JOINs, operador IN, funções de data, hora e concatenação) e elaboração de projetos de banco de dados.

---

## 1. Diagrama de Classes UML (Domínio da Matéria)

O diagrama abaixo representa a estrutura lógica de controle acadêmico e submissão de atividades e trabalhos da disciplina.

```mermaid
classDiagram
    class Professor {
        +String nome
        +String disciplina
        +criarAtividade()
        +avaliarTrabalho()
    }

    class Aluno {
        +String nome
        +String matricula
        +enviarTrabalho()
    }

    class Atividade {
        +String titulo
        +DateTime prazoEntrega
        +float pontuacaoMaxima
        +consultarDados()
    }

    class Trabalho {
        +DateTime dataEnvio
        +float notaObtida
        +statusSubmissao()
    }

    Professor "1" --> "*" Atividade : gerencia
    Aluno "*" --> "*" Trabalho : submete
    Atividade "1" --> "*" Trabalho : baseia-se
```

### Explicação do Diagrama
* **Professor**: Responsável por criar e gerenciar as atividades/trabalhos e avaliar o desempenho.
* **Aluno**: Executa as tarefas práticas (como os exercícios de SQL) e submete os trabalhos no sistema.
* **Atividade**: Representa os materiais de aula (Aulas 03 a 05, manipulação de dados, consultas complexas, datas e concatenação).
* **Trabalho**: Vincula o aluno à atividade entregue, registrando prazos e pontuações máximas (ex: 100 pontos).

---

## 2. Diagrama de Sequência (Execução de Consulta SQL Complexa)

Este fluxo demonstra a interação entre o usuário/aluno, a camada de aplicação e o SGBD ao executar uma consulta avançada (envolvendo junções de tabelas, operador `IN`, filtros de data/hora e concatenação).

```mermaid
sequenceDiagram
    autonumber
    actor Aluno
    participant App as Aplicação / Interface
    participant SGBD as Sistema de Banco de Dados

    Aluno->>App: Solicita execução de consulta SQL (JOIN, IN, CONCAT)
    App->>SGBD: Envia query SQL otimizada
    activate SGBD
    Note over SGBD: Processa varredura nas tabelas,<br/>filtra dados com IN, une tabelas<br/>e formata datas/strings.
    SGBD-->>App: Retorna conjunto de resultados (ResultSet)
    deactivate SGBD
    App-->>Aluno: Exibe tabela formatada com os dados consultados
```

### Explicação do Fluxo
1. **Solicitação**: O aluno insere ou dispara uma query SQL complexa (abordada nos módulos de consultas, operações com duas tabelas e manipulação de datas/concatenação).
2. **Processamento**: O SGBD interpreta os comandos (como `SELECT`, `WHERE ... IN (...)`, `JOIN` e funções de manipulação de string/tempo).
3. **Retorno**: Os dados processados são devolvidos à interface e apresentados em formato tabular.

---

## 3. Diagrama Entidade-Relacionamento (ER)

Representação conceitual do banco de dados relacional abordado nas aulas práticas de elaboração de banco de dados e consultas com múltiplas tabelas.

```mermaid
erDiagram
    PROFESSOR ||--o{ ATIVIDADE : ministra
    ALUNO ||--o{ TRABALHO : entrega
    ATIVIDADE ||--o{ TRABALHO : possui

    PROFESSOR {
        int id_professor PK
        string nome
    }

    ALUNO {
        int id_aluno PK
        string nome
        string matricula
    }

    ATIVIDADE {
        int id_atividade PK
        string titulo
        datetime prazo
        float pontuacao
    }

    TRABALHO {
        int id_trabalho PK
        int id_aluno FK
        int id_atividade FK
        datetime data_envio
        float nota
    }
```

### Explicação do Diagrama
* **Relacionamentos**: 
  * Um `PROFESSOR` pode criar várias `ATIVIDADES`.
  * Um `ALUNO` pode realizar múltiplos `TRABALHOS`.
  * Uma `ATIVIDADE` possui várias submissões de `TRABALHO` feitas por diferentes alunos.
* **Uso Prático**: Serve de base para exercitar comandos de criação (`CREATE`), inserção (`INSERT`), atualização (`UPDATE`), remoção (`DELETE`) e consultas relacionais (`INNER JOIN`, `LEFT JOIN`, etc.).