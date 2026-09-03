FILENAME: documentacao_projeto_medsync.md
---CODE_START---
# 📋 DOCUMENTAÇÃO DE PROJETO DE SOFTWARE: SISTEMA MEDSYNC
**Disciplina:** Engenharia de Software I (3º Semestre)  
**Professor:** Prof. Marcelo Boer  
**Atividade:** ATIVIDADE AV2  
**Curso:** Bacharelado em Sistemas de Informação  

---

## 1. INTRODUÇÃO E ESCOPO DO PROJETO

### 1.1. Visão Geral do Sistema
O **MedSync** é um sistema web e mobile integrado para gestão de clínicas médicas, agendamento de consultas e Prontuário Eletrônico do Paciente (PEP). O sistema foi concebido para resolver gargalos operacionais em clínicas de médio porte, unificando a jornada do paciente desde o agendamento online até o atendimento clínico e emissão de receitas médicas.

### 1.2. Objetivos do Projeto
*   **Objetivo Geral:** Desenvolver uma plataforma de software segura, intuitiva e em conformidade com a LGPD (Lei Geral de Proteção de Dados) para otimizar o fluxo de trabalho de clínicas médicas.
*   **Objetivos Específicos:**
    *   Reduzir o tempo de espera para agendamento de consultas em até 60%.
    *   Garantir a integridade e confidencialidade dos prontuários médicos.
    *   Facilitar a comunicação entre médicos, secretários e pacientes.
    *   Disponibilizar relatórios gerenciais para tomada de decisão administrativa.

### 1.3. Justificativa
Muitas clínicas médicas ainda utilizam processos manuais ou sistemas legados descentralizados, o que acarreta perda de dados, erros de agendamento e vulnerabilidades de segurança. O MedSync centraliza essas operações em nuvem, garantindo alta disponibilidade, segurança robusta e facilidade de acesso para todos os stakeholders.

---

## 2. ENGENHARIA DE REQUISITOS

### 2.1. Requisitos Funcionais (RF)

| Identificador | Nome do Requisito | Descrição | Prioridade |
| :--- | :--- | :--- | :--- |
| **RF-001** | Autenticação e Autorização | O sistema deve permitir o login de Pacientes, Médicos e Recepcionistas com diferentes níveis de acesso (RBAC). | Essencial |
| **RF-002** | Cadastro de Pacientes | O sistema deve permitir que a recepção ou o próprio paciente realize o cadastro de dados pessoais e anamnese básica. | Essencial |
| **RF-003** | Agendamento de Consultas | O paciente ou recepcionista deve poder agendar, reagendar ou cancelar consultas com base na grade de horários dos médicos. | Essencial |
| **RF-004** | Prontuário Eletrônico (PEP) | O médico deve poder registrar a evolução clínica, diagnósticos (CID-10), alergias e histórico médico do paciente. | Essencial |
| **RF-005** | Emissão de Prescrições | O médico deve poder emitir receitas digitais, atestados e pedidos de exames com assinatura digital. | Importante |
| **RF-006** | Notificações Automáticas | O sistema deve enviar confirmações e lembretes de consulta via WhatsApp/E-mail 24h antes do atendimento. | Importante |
| **RF-007** | Painel Financeiro | O administrador deve visualizar relatórios de faturamento, consultas realizadas e glosas de convênios. | Desejável |

### 2.2. Requisitos Não-Funcionais (RNF)

| Identificador | Categoria | Descrição | Prioridade |
| :--- | :--- | :--- | :--- |
| **RNF-001** | Segurança | Todos os dados sensíveis de saúde devem ser criptografados em repouso (AES-256) e em trânsito (HTTPS/TLS), em conformidade com a LGPD. | Essencial |
| **RNF-002** | Desempenho | O tempo de resposta para carregamento de prontuários e buscas não deve exceder 2 segundos sob carga normal. | Importante |
| **RNF-003** | Disponibilidade | O sistema deve possuir uma disponibilidade mínima de 99.9% (SLA), utilizando infraestrutura multi-região na nuvem. | Essencial |
| **RNF-004** | Usabilidade | A interface deve ser responsiva (adaptável a desktops, tablets e smartphones) e seguir as diretrizes de acessibilidade WCAG 2.1. | Importante |
| **RNF-005** | Escalabilidade | A arquitetura deve ser baseada em microsserviços para permitir escalabilidade horizontal independente do módulo de agendamento. | Desejável |

### 2.3. Regras de Negócio (RN)

*   **RN-001 (Cancelamento):** O paciente só pode cancelar ou reagendar uma consulta sem custos adicionais com até 4 horas de antecedência.
*   **RN-002 (Edição de Prontuário):** O prontuário médico, após finalizado e assinado digitalmente pelo médico, não poderá ser editado. Alterações posteriores devem ser feitas via "Termo Aditivo" (Evolução).
*   **RN-003 (Conflito de Horários):** O sistema não deve permitir o agendamento de duas consultas para o mesmo médico no mesmo intervalo de tempo.

---

## 3. MODELAGEM DO SISTEMA (UML)

### 3.1. Diagrama de Casos de Uso
O diagrama abaixo ilustra as interações dos diferentes atores (Paciente, Médico, Recepcionista) com as funcionalidades do sistema MedSync.

gestureRightToLeft
flowchart TD
    %% Atores
    Paciente((Paciente))
    Recepcionista((Recepcionista))
    Medico((Médico))

    %% Casos de Uso
    UC1(Efetuar Login)
    UC2(Cadastrar Paciente)
    UC3(Agendar Consulta)
    UC4(Visualizar Prontuário)
    UC5(Registrar Evolução Clínica)
    UC6(Emitir Prescrição Médica)
    UC7(Enviar Lembrete de Consulta)

    %% Associações Paciente
    Paciente --> UC1
    Paciente --> UC3
    Paciente --> UC4

    %% Associações Recepcionista
    Recepcionista --> UC1
    Recepcionista --> UC2
    Recepcionista --> UC3

    %% Associações Médico
    Medico --> UC1
    Medico --> UC4
    Medico --> UC5
    Medico --> UC6

    %% Relacionamentos de Casos de Uso
    UC3 -.->|<<include>>| UC1
    UC5 -.->|<<include>>| UC4
    UC6 -.->|<<include>>| UC5
    UC3 -.->|<<extend>>| UC7

---

### 3.2. Diagrama de Classes de Domínio
Este diagrama representa a estrutura estática do sistema, mapeando as entidades de negócio, seus atributos, métodos e relacionamentos.

classDiagram
    class Usuario {
        +int id
        +string nome
        +string email
        +string senhaHash
        +string telefone
        +login() bool
        +logout() bool
    }

    class Paciente {
        +string cpf
        +Date dataNascimento
        +string tipoSanguineo
        +visualizarHistorico() List
    }

    class Medico {
        +string crm
        +string especialidade
        +double valorConsulta
        +atenderPaciente() void
    }

    class Recepcionista {
        +string carteiraTrabalho
        +cadastrarPaciente() void
    }

    class Consulta {
        +int id
        +DateTime dataHora
        +string status
        +string observacoes
        +confirmar() void
        +cancelar() void
    }

    class Prontuario {
        +int id
        +DateTime dataCriacao
        +string historicoFamiliar
        +string alergias
        +adicionarEvolucao(string texto) void
    }

    class Prescricao {
        +int id
        +string medicamentos
        +string posologia
        +string assinaturaDigital
        +gerarPDF() string
    }

    Usuario <|-- Paciente
    Usuario <|-- Medico
    Usuario <|-- Recepcionista

    Paciente "1" -- "0..*" Consulta
    Medico "1" -- "0..*" Consulta
    Consulta "1" -- "0..1" Prontuario
    Prontuario "1" -- "0..*" Prescricao

---

### 3.3. Diagrama de Sequência: Agendamento de Consulta
O diagrama de sequência a seguir descreve o fluxo de mensagens para o cenário de sucesso de um agendamento de consulta realizado por um paciente.

sequenceDiagram
    autonumber
    actor P as Paciente
    participant Interface as Interface Web/Mobile
    participant Controller as AgendamentoController
    participant Service as AgendamentoService
    participant DB as Banco de Dados (PostgreSQL)

    P->>Interface: Seleciona Especialidade, Médico e Horário
    Interface->>Controller: POST /api/v1/agendamentos (dados)
    Note over Controller: Valida token JWT do Paciente
    Controller->>Service: criarAgendamento(pacienteId, medicoId, dataHora)
    
    Service->>DB: Verificar disponibilidade do horário
    activate DB
    DB-->>Service: Horário Disponível (true)
    deactivate DB

    Service->>DB: Salvar novo registro de Consulta (Status: Pendente)
    activate DB
    DB-->>Service: Consulta salva com ID 402
    deactivate DB

    Service-->>Controller: Objeto Consulta criado com sucesso
    Controller-->>Interface: HTTP 201 Created (JSON)
    Interface-->>P: Exibe mensagem de sucesso e dados da consulta

---

## 4. ARQUITETURA DE SOFTWARE E TECNOLOGIAS

O MedSync adota uma arquitetura baseada em **Clean Architecture** (Arquitetura Limpa), dividida em camadas bem definidas para garantir testabilidade, manutenibilidade e independência de frameworks.

┌───────────────────────────────────────────────────────────┐
│                    Camada de Apresentação                 │
│             (React.js / React Native / Tailwind)          │
└─────────────────────────────┬─────────────────────────────┘
                              │ API REST (JSON)
┌─────────────────────────────▼─────────────────────────────┐
│                     Camada de Aplicação                   │
│             (Controllers, DTOs, Use Cases)                │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                      Camada de Domínio                    │
│             (Entidades, Regras de Negócio, Interfaces)    │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                  Camada de Infraestrutura                 │
│         (PostgreSQL, Spring Boot, Docker, AWS S3)         │
└───────────────────────────────────────────────────────────┘

### 4.1. Stack Tecnológica Escolhida
*   **Frontend (Web & Mobile):** React.js (Web) e React Native (Mobile) com TypeScript. Proporciona uma experiência de usuário fluida (SPA) e reaproveitamento de código de lógica de negócios entre plataformas.
*   **Backend:** Java com Spring Boot. Escolhido pela robustez, ecossistema maduro de segurança (Spring Security + JWT) e facilidade de integração com APIs governamentais e de assinatura digital.
*   **Banco de Dados:** PostgreSQL. Banco relacional robusto, ideal para garantir a consistência ACID necessária em transações financeiras e prontuários médicos.
*   **Mensageria e Notificações:** Redis (para filas de envio de e-mail/WhatsApp) e Twilio API.
*   **Hospedagem/Cloud:** AWS (Amazon Web Services) utilizando ECS (Elastic Container Service), RDS (Relational Database Service) e S3 para armazenamento seguro de documentos digitalizados.

---

## 5. MODELAGEM FÍSICA DO BANCO DE DADOS (SQL)

Abaixo está o script DDL (Data Definition Language