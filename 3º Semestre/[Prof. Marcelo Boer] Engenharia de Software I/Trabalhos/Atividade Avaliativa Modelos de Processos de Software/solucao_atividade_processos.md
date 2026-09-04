FILENAME: solucao_atividade_processos.md
---CODE_START---
# 🎓 Resolução da Atividade Avaliativa: Modelos de Processos de Software
**Disciplina:** Engenharia de Software I (3º Semestre)  
**Professor:** Prof. Marcelo Boer  
**Curso:** Bacharelado em Sistemas de Informação  
**Status:** Trabalho Resolvido por Completo (Padrão de Excelência Acadêmica e Profissional)

---

## 📌 Introdução do Professor-Orientador

Como docente e desenvolvedor sênior, estruturei esta resolução com base nas referências clássicas da Engenharia de Software (**Roger Pressman** e **Ian Sommerville**). Cada resposta foi elaborada não apenas para obter a nota máxima (2.0 pontos), mas para servir como um guia prático de tomada de decisão arquitetural e metodológica em cenários reais de desenvolvimento corporativo.

---

## 1. MATRIZ COMPARATIVA DE MODELOS DE PROCESSO

Abaixo está a análise comparativa detalhada dos principais modelos de processo de software, avaliados sob critérios críticos de engenharia, gerenciamento de riscos e viabilidade comercial.

| Critério de Comparação | Cascata (Waterfall) | Prototipação (Prototyping) | Espiral (Spiral) | Processo Unificado (RUP) | Scrum (Ágil) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Estabilidade dos Requisitos** | Exige requisitos 100% estáveis e definidos no início. | Ideal para requisitos altamente instáveis/desconhecidos. | Suporta mudanças, mas exige reavaliação formal de riscos. | Requisitos evoluem por fases (Incepção/Elaboração). | Requisitos dinâmicos, priorizados no *Product Backlog*. |
| **Gerenciamento de Riscos** | Baixo/Tardio (riscos só aparecem na fase de testes). | Médio (foco em riscos de interface e usabilidade). | **Altíssimo** (orientado a riscos em cada iteração). | Alto (foco em mitigar riscos técnicos logo no início). | Alto/Contínuo (mitigados a cada Sprint de 1 a 4 semanas). |
| **Envolvimento do Cliente** | Baixo (apenas no início e na entrega final). | Altíssimo (validação constante das telas/fluxos). | Médio-Alto (participa das revisões de cada ciclo). | Médio (focado nos marcos de transição de fase). | **Máximo** (presença diária ou semanal via Product Owner). |
| **Frequência de Entregas** | Única entrega ao final do ciclo de vida. | Entregas rápidas de modelos não funcionais/parciais. | Entregas incrementais ao final de cada ciclo espiral. | Entregas incrementais ao final de cada iteração. | **Entregas constantes** de incrementos potencialmente usáveis. |
| **Custo de Mudança** | Extremamente alto se a mudança ocorrer no fim do ciclo. | Baixo nas fases iniciais; médio após codificação final. | Controlado (avaliado financeiramente antes de prosseguir). | Moderado (gerenciado por controle de mudança formal). | **Muito Baixo** (mudanças são bem-vindas no backlog). |

---

## 2. ESTUDOS DE CASO: SELEÇÃO E JUSTIFICATIVA DE MODELOS

Abaixo, analisamos três cenários reais de mercado. Para cada um, foi selecionado o modelo de processo ideal, acompanhado de justificativa técnica e o mapeamento do ciclo de vida em diagramas de fluxo.

### 🏥 Cenário A: Sistema de Controle de Infusão de Medicamentos Hospitalar (Software Crítico)
* **Contexto:** Software embarcado em hardware médico. Falhas podem levar a óbito. Requisitos de hardware e dosagem médica altamente rígidos e regulados pela ANVISA.
* **Modelo Escolhido:** **Modelo em V (V-Model) integrado com Análise de Riscos da Espiral**.
* **Justificativa Técnica:** Sistemas críticos (*safety-critical systems*) exigem verificação e validação (V&V) rigorosas em cada nível de abstração. O Modelo em V garante que para cada fase de especificação exista uma fase de teste correspondente, garantindo rastreabilidade total. A flexibilidade ágil aqui é um risco de engenharia inaceitável.

graph TD
    subgraph Engenharia_e_Especificacao [Fase de Engenharia]
        A[Requisitos do Sistema] --> B[Arquitetura do Software]
        B --> C[Design de Componentes]
        C --> D[Codificação do Firmware]
    end

    subgraph Verificacao_e_Validacao [Fase de V & V]
        D --> E[Testes Unitários]
        E --> F[Testes de Integração]
        F --> G[Testes de Sistema]
        G --> H[Testes de Aceitação/Homologação]
    end

    %% Relações de correspondência (V-Model)
    A -.->|Rastreabilidade de Teste| H
    B -.->|Rastreabilidade de Teste| G
    C -.->|Rastreabilidade de Teste| F
    D -.->|Rastreabilidade de Teste| E

    style D fill:#f9f,stroke:#333,stroke-width:2px
    style H fill:#8f8,stroke:#333,stroke-width:2px

---

### 🍔 Cenário B: MVP de uma Plataforma de Delivery de Nicho (Startup)
* **Contexto:** Orçamento limitado, necessidade de entrar rápido no mercado para validar a hipótese de negócio, concorrência acirrada e requisitos altamente mutáveis baseados no feedback dos usuários.
* **Modelo Escolhido:** **Scrum (Framework Ágil)**.
* **Justificativa Técnica:** O foco de uma startup é o *Time-to-Market* e o aprendizado validado. O Scrum permite focar no MVP (*Minimum Viable Product*), entregando valor em Sprints curtas (2 semanas). Se a hipótese de negócio falhar, o desperdício financeiro é minimizado.

stateDiagram-v2
    [*] --> Product_Backlog : Visão do Produto
    Product_Backlog --> Sprint_Planning : Seleção de Itens Prioritários
    state Sprint_Cycle {
        Sprint_Planning --> Sprint_Backlog
        Sprint_Backlog --> Daily_Scrum : Execução (1 a 4 semanas)
        Daily_Scrum --> Daily_Scrum : Inspeção Diária (15 min)
    }
    Sprint_Cycle --> Sprint_Review : Demonstração do Incremento
    Sprint_Review --> Sprint_Retrospective : Melhoria Contínua do Processo
    Sprint_Retrospective --> Incremento_Pronto : Software Funcional
    Incremento_Pronto --> [*]

---

### 🏦 Cenário C: Migração de Sistema de Faturamento de Grande Porte (Legado Bancário)
* **Contexto:** Sistema legado complexo, milhões de transações diárias, regras de negócio obscuras e sem documentação atualizada. Alto risco de impacto financeiro em caso de parada.
* **Modelo Escolhido:** **Processo Unificado (RUP - Rational Unified Process)**.
* **Justificativa Técnica:** A migração de um core bancário exige uma fase de **Elaboração** robusta para mitigar riscos arquiteturais e mapear as regras de negócio legadas antes da construção em larga escala. O RUP divide o projeto em fases claras com marcos (*milestones*) rígidos de governança, ideais para ambientes corporativos tradicionais.

gantt
    title Ciclo de Vida RUP - Migração de Faturamento Bancário
    dateFormat  YYYY-MM-DD
    section Incepção
    Definição do Escopo e Viabilidade     :active, des1, 2026-01-01, 2026-01-31
    section Elaboração
    Arquitetura de Referência e Riscos    :active, des2, 2026-02-01, 2026-03-31
    section Construção
    Migração dos Módulos (Iterativo)      :active, des3, 2026-04-01, 2026-08-31
    section Transição
    Testes de Carga e Rollout Gradual     :active, des4, 2026-09-01, 2026-10-31

---

## 3. MAPEAMENTO DAS ATIVIDADES DE ARCABOUÇO (FRAMEWORK ACTIVITIES)

De acordo com Pressman, todo processo de software é povoado por um conjunto de **Atividades de Arcabouço** (Framework Activities). Abaixo, demonstramos como essas atividades universais se manifestam de formas radicalmente diferentes no modelo **Cascata** e no framework **Scrum**.

graph TD
    subgraph Atividades_de_Arcabouco [Atividades de Arcabouço Universais]
        A[Comunicação]
        B[Planejamento]
        C[Modelagem]
        D[Construção]
        E[Implantação]
    end

### 1. Comunicação
* **No Cascata:** Ocorre de forma massiva e formal no início do projeto. Resulta no documento de **SRS (Software Requirement Specification)** assinado pelo cliente. Após essa fase, o contato com o cliente é drasticamente reduzido.
* **No Scrum:** É contínua, diária e colaborativa. Ocorre através das cerimônias (*Daily*, *Planning*, *Review*) e do refinamento constante do backlog com o Product Owner (PO).

### 2. Planejamento
* **No Cascata:** Planejamento preditivo e de longo prazo. Cria-se um cronograma estático (Gráfico de Gantt) definindo datas para todas as entregas até o fim do projeto. Desvios são tratados como falhas de planejamento.
* **No Scrum:** Planejamento adaptativo e em múltiplos níveis (Cebola do Planejamento Ágil). O planejamento detalhado ocorre a cada início de Sprint (*Sprint Planning*), permitindo que o plano mude conforme o aprendizado do time.

### 3. Modelagem
* **No Cascata:** Fase dedicada à criação de diagramas UML exaustivos (Casos de Uso, Classes, Sequência, Entidade-Relacionamento) antes de qualquer linha de código ser escrita.
* **No Scrum:** Modelagem ágil (*Just-in-Time*). Diagramas são criados apenas quando necessários para resolver um problema complexo de design de código, priorizando código limpo e arquitetura emergente sobre documentação extensiva.

### 4. Construção
* **No Cascata:** Fase de codificação pura baseada estritamente nos documentos gerados na fase de modelagem. Os testes unitários e de integração ocorrem sequencialmente após o término da codificação de todo o sistema.
* **No Scrum:** Codificação e testes ocorrem em paralelo dentro da mesma Sprint. Práticas como TDD (Test-Driven Development) e Integração Contínua (CI) garantem que cada funcionalidade seja construída e testada imediatamente.

### 5. Implantação
* **No Cascata:** Ocorre uma única vez ao final de todo o ciclo de desenvolvimento (frequentemente meses ou anos após o início