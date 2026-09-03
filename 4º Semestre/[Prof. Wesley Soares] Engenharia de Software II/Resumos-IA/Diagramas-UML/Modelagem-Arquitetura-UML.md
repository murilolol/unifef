# 📚 Engenharia de Software II - Prof. Wesley Soares
**4º Semestre**

Abaixo estão representados os artefatos visuais baseados nas diretrizes da disciplina, abrangendo modelagem orientada a objetos, fluxos de submissão de atividades e a arquitetura conceitual para o Trabalho Semestral (Requisitos, MoSCoW e Casos de Uso).

---

### 1. Diagrama de Classes UML (Domínio da Matéria)
Este diagrama modela a estrutura acadêmica da disciplina, relacionando Professores, Alunos, Aulas, Trabalhos e as Submissões realizadas pelos grupos.

```mermaid
classDiagram
    class Professor {
        +String nome
        +String email
        +criarAula()
        +criarTrabalho()
    }

    class Aluno {
        +String nome
        +String matricula
        +participarGrupo()
    }

    class Grupo {
        +String nomeProjeto
        +definirPapeis()
    }

    class Aula {
        +int numeroAula
        +Date dataPostagem
        +String conteudo
        +disponibilizarMaterial()
    }

    class Trabalho {
        +String titulo
        +Date prazoEntrega
        +double pontuacaoMaxima
        +validarEntrega()
    }

    class Submissao {
        +Date dataEnvio
        +String anexoArquivo
        +double notaAtribuida
        +enviar()
    }

    Professor "1" --> "*" Aula : ministra/publica
    Professor "1" --> "*" Trabalho : propõe
    Aluno "*" -- "*" Grupo : compõe
    Grupo "1" --> "1" Submissao : gera
    Trabalho "1" --> "*" Submissao : recebe
    Aula "1" --> "*" Trabalho : contextualiza
```

**Explicação do Fluxo:**
O `Professor` gerencia e publica conteúdos nas `Aulas` e propõe `Trabalhos` práticos. Os `Alunos` organizam-se em `Grupos` de projeto para desenvolver as atividades. Cada `Grupo` realiza uma `Submissao` vinculada a um `Trabalho` específico dentro do prazo estipulado.

---

### 2. Diagrama de Sequência (Fluxo de Submissão de Atividade)
Ilustra a interação técnica entre o Aluno, o Sistema de Gestão Acadêmica e o Armazenamento ao submeter um arquivo de trabalho (como exigido na *Atividade Aula 3* ou *Trabalho Semestral*).

```mermaid
sequenceDiagram
    autonumber
    actor Aluno
    participant Sistema as Sistema Acadêmico
    participant Banco as Banco de Dados
    participant Storage as Armazenamento (Files)

    Aluno->>Sistema: Acessa página do Trabalho
    Sistema-->>Aluno: Exibe prazos e instruções
    Aluno->>Sistema: Anexa arquivo (Word/Docs/Diagrama) e envia
    
    activate Sistema
    Sistema->>Storage: Salva arquivo em anexo
    Storage-->>Sistema: Retorna URL/Caminho do arquivo
    
    Sistema->>Banco: Registra Submissão (Data, Grupo, Arquivo, Status)
    Banco-->>Sistema: Confirmação de salvamento
    
    Sistema-->>Aluno: Exibe mensagem de Sucesso ("Enviado com sucesso")
    deactivate Sistema
```

**Explicação do Fluxo:**
O aluno interage com a interface do sistema para enviar a documentação solicitada pelo professor. O sistema valida o recebimento, armazena o arquivo de forma segura no servidor/nuvem e atualiza o banco de dados com os metadados da entrega (data, hora e vínculo com o grupo), finalizando com a confirmação ao usuário.

---

### 3. Diagrama Arquitetural / Entidade-Relacionamento (Trabalho Semestral)
Representa a arquitetura conceitual e o fluxo lógico exigido para o **Trabalho Semestral**, que abrange Levantamento de Requisitos, Priorização MoSCoW e Casos de Uso.

```mermaid
graph TD
    subgraph Levantamento ["1. Levantamento de Requisitos"]
        A[Stakeholders / Clientes] -->|Entrevista| B(Requisitos Funcionais)
        A -->|Entrevista| C(Requisitos Não Funcionais)
    end

    subgraph Priorizacao ["2. Metodologia MoSCoW"]
        B --> D{Classificação}
        D -->|Must have| E[Essencial]
        D -->|Should have| F[Importante]
        D -->|Could have| G[Desejável]
        D -->|Won't have| H[Fora do Escopo Atual]
    end

    subgraph Modelagem ["3. Casos de Uso"]
        E --> I[Ator Principal]
        I --> J([Caso de Uso Geral])
        J --> K([Casos de Uso Específicos])
        K --> L[Documentação Detalhada / Fluxos]
    end

    style A fill:#f9f,stroke:#333,stroke-width:2px
    style E fill:#bbf,stroke:#333,stroke-width:2px
    style L fill:#bfb,stroke:#333,stroke-width:2px
```

**Explicação do Fluxo:**
O processo do trabalho semestral inicia-se com a elicitação de requisitos junto aos *stakeholders*. Em seguida, esses requisitos passam pelo filtro de priorização **MoSCoW** para definir o núcleo do sistema (*Must have*). Por fim, os requisitos essenciais são transformados em **Casos de Uso** (Gerais e Específicos), detalhados e documentados para entrega final via documento do Word/Google Docs.