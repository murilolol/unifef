FILENAME: relatorio_final_atividade_aula3.md
---CODE_START---
# 📋 Relatório Técnico de Engenharia de Software: Sistema de Matrícula Online (SisMat)
> **Disciplina:** Engenharia de Software II (4º Semestre)  
> **Professor:** Prof. Wesley Soares  
> **Prazo de Entrega:** 19/08/2026 às 02:59  
> **Status:** Solução Completa de Engenharia de Software (Arquitetura, Modelagem e Código)  
> **Pontuação Máxima:** 100 pontos  

---

## 1. Integrantes do Grupo, Stakeholders e Papéis de Analistas

Para garantir o alinhamento entre as necessidades de negócio e a viabilidade técnica, a equipe foi estruturada utilizando papéis híbridos. Cada integrante atua como um **Stakeholder** (representando as dores e requisitos do negócio) e como um **Analista Técnico** (responsável por projetar e implementar a solução).

| Integrante | Papel de Stakeholder (Negócio) | Papel de Analista (Técnico) | Responsabilidades no Projeto |
| :--- | :--- | :--- | :--- |
| **Ana Souza** | Gerente de Operações Acadêmicas | Analista de Requisitos e Processos (*Business Analyst*) | Mapeamento de regras de negócio, definição de fluxos de validação de pré-requisitos e critérios de prioridade de matrícula. |
| **Carlos Silva** | Coordenador de Infraestrutura de TI | Arquiteto de Software e DevOps | Desenho da arquitetura de microsserviços, configuração do API Gateway, mensageria (Kafka) e estratégias de escalabilidade em nuvem. |
| **Mariana Costa** | Representante do Corpo Discente (Alunos) | Analista de Experiência do Usuário (*UX/UI & Frontend*) | Prototipação da interface reativa, design de fluxos de feedback em tempo real e integração de WebSockets para comunicação assíncrona. |
| **João Pedro** | Administrador de Banco de Dados (DBA) | Engenheiro de Dados e Segurança da Informação | Modelagem do banco de dados PostgreSQL, otimização de queries, garantia de consistência eventual e segurança de dados (LGPD). |

---

## 2. Descrição Detalhada da Situação-Problema

A instituição de ensino superior enfrenta um gargalo crítico no processo de **Matrícula Online e Rematrícula** de semestres letivos. O sistema legado atual, baseado em uma arquitetura monolítica e síncrona, sofre com severas quedas de performance, travamentos e inconsistência de dados devido ao pico de acessos simultâneos no primeiro dia de abertura dos prazos.

### Análise Técnica das Causas Raiz (Dores Identificadas):

1. **Concorrência Descontrolada e Esgotamento de Recursos:**
   * No momento da abertura do sistema, milhares de alunos tentam realizar requisições simultâneas. O servidor monolítico esgota seu *thread pool* rapidamente.
   * O banco de dados relacional sofre com contenção de travas (*locks*) nas tabelas de turmas e matrículas, resultando em *deadlocks* e *timeouts* generalizados (Erros HTTP 504 Gateway Timeout).

2. **Falta de Feedback Visual e Ansiedade do Usuário:**
   * O sistema atual não atualiza a quantidade de vagas disponíveis em tempo real. O aluno seleciona uma disciplina que aparenta ter vagas, mas, ao submeter o formulário, recebe um erro informando que a vaga já foi preenchida por outro usuário.
   * Isso gera um comportamento de "recarregamento frenético" (F5) por parte dos alunos, piorando exponencialmente a carga sobre o servidor.

3. **Validação Síncrona e Bloqueante de Pré-requisitos:**
   * Para cada tentativa de matrícula, o sistema realiza consultas complexas e síncronas no histórico escolar do aluno para validar se ele possui os pré-requisitos necessários. Essa operação consome muito processamento de CPU e I/O de disco do banco de dados principal.

4. **Indisponibilidade de Suporte e Perda de Prazos:**
   * Devido às falhas sistêmicas, a central de atendimento (Service Desk) fica sobrecarregada com chamados de alunos que não conseguiram se matricular, gerando retrabalho administrativo e insatisfação generalizada.

---

## 3. Descrição da Solução Proposta

Para sanar definitivamente a situação-problema, propõe-se a reengenharia do módulo de matrículas através da adoção de uma **Arquitetura Orientada a Eventos (EDA - Event-Driven Architecture)** e **Microsserviços**, desacoplando o processo de solicitação do processo de efetivação da matrícula.

### Componentes da Arquitetura e Tecnologias:

* **API Gateway (Kong / Spring Cloud Gateway):** Atua como ponto único de entrada. Implementa *Rate Limiting* por IP/Usuário para evitar sobrecarga e distribui as requisições entre os microsserviços.
* **Microsserviço de Validação de Pré-requisitos (Node.js / TypeScript):** Um serviço leve que valida rapidamente se o aluno pode cursar a disciplina solicitada. Utiliza cache em memória (**Redis**) para ler o histórico do aluno de forma ultra-rápida (sub-milissegundos).
* **Fila de Mensagens (Apache Kafka):** Se a validação inicial passar, a solicitação de matrícula é enfileirada no tópico `solicitacoes-matricula`. O aluno recebe imediatamente um status de *"Solicitação Recebida - Processando"*.
* **Worker de Processamento Assíncrono (Java / Spring Boot):** Consome as mensagens do Kafka de forma ordenada e controlada, realizando a baixa da vaga e a gravação no banco de dados PostgreSQL sem gerar concorrência destrutiva.
* **Frontend Reativo (React.js) & WebSockets:** O frontend estabelece uma conexão WebSocket com o servidor. Assim que o Worker processa a matrícula, ele envia uma notificação em tempo real para o navegador do aluno confirmando o sucesso ou informando o motivo da falha.

---

## 4. Diagramas de Engenharia de Software

### 4.1. Diagrama de Arquitetura de Microsserviços (C4 Model - Container Level)

graph TD
    subgraph Clientes [Camada de Apresentação]
        A[Navegador do Aluno - React.js]
    end

    subgraph Gateway [Camada de Entrada]
        B[API Gateway & Rate Limiter - Kong]
    end

    subgraph Microsservicos [Camada de Serviços]
        C[Microsserviço de Validação - Node.js]
        D[Microsserviço de Notificação - WebSocket Server]
        E[Worker de Processamento - Spring Boot]
    end

    subgraph Cache_Mensageria [Camada de Cache e Eventos]
        F[(Cache de Históricos - Redis)]
        G[Fila de Mensagens - Apache Kafka]
    end

    subgraph Persistencia [Camada de Dados]
        H[(Banco de Dados Relacional - PostgreSQL)]
    end

    %% Fluxo de Comunicação
    A -->|1. HTTPS / Solicitar Matrícula| B
    A <-->|WebSocket / Status em Tempo Real| D
    B -->|2. Encaminha Requisição| C
    C -->|3. Consulta Rápida de Histórico| F
    C -->|4. Envia Evento de Solicitação| G
    G -->|5. Consome Eventos de Forma Controlada| E
    E -->|6. Persiste Matrícula e Decrementa Vaga| H
    E -->|7. Publica Evento de Confirmação| G
    G -->|8. Consome Confirmação| D
    D -->|9. Notifica Cliente| A

### 4.2. Diagrama de Sequência do Processo de Matrícula Assíncrona

sequenceDiagram
    autonumber
    actor Aluno as Aluno (React App)
    participant Gateway as API Gateway
    participant Validador as MS Validador (Node.js)
    participant Redis as Redis Cache
    participant Kafka as Apache Kafka
    participant Worker as Worker Matrícula (Java)
    participant DB as PostgreSQL

    Aluno->>Gateway: POST /matriculas/solicitar (AlunoId, TurmaId)
    Gateway->>Validador: Encaminha Requisição
    Validador->>Redis: Buscar Histórico e Pré-requisitos do Aluno
    Redis-->>Validador: Retorna Dados do Aluno
    Note over Validador: Valida se o aluno possui os pré-requisitos
    
    alt Pré-requisitos Inválidos
        Validador-->>Gateway: Retorna Erro 400 (Pré-requisito não atendido)
        Gateway-->>Aluno: Exibe erro na tela imediatamente
    else Pré-requisitos Válidos
        Validador->>Kafka: Publicar Evento "SolicitacaoMatriculaEvent"
        Kafka-->>Validador: Confirmar recebimento da mensagem
        Validador-->>Gateway: Retorna HTTP 202 (Accepted - Protocolo Gerado)
        Gateway-->>Aluno: Exibe "Processando sua matrícula..."
    end

    Note over Worker: Consome mensagem do Kafka de forma assíncrona
    Worker->>DB: Inicia Transação (Verifica vaga e insere matrícula)
    DB-->>Worker: Sucesso na gravação
    Worker->>Kafka: Publicar Evento "MatriculaConfirmadaEvent"
    Kafka->>Aluno: Notificação via WebSocket: "Matrícula Realizada com Sucesso!"

---

## 5. Implementação Prática (