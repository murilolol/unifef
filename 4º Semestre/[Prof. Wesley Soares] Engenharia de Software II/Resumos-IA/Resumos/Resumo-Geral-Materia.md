# Resumo Consolidado: [Prof. Wesley Soares] Engenharia de Software II

---

## 1. Visão Geral e Objetivos da Matéria
A disciplina de **Engenharia de Software II**, ministrada pelo Prof. Ms. Wesley Soares de Souza, aborda o ciclo de vida avançado do desenvolvimento de software, fazendo a transição crítica entre a **Análise de Requisitos (o "o quê fazer")** e o **Projeto Orientado a Objetos e Arquitetura (o "como fazer")**. 

O principal objetivo da matéria é afastar o desenvolvimento de software da ideia de "pastelaria" (produção sem critério) e consolidar práticas de engenharia estruturadas, capacitando o aluno a projetar sistemas funcionais, escaláveis, coesos e de fácil manutenção. A disciplina é fortemente respaldada por um **Projeto Integrador** prático em equipe, que simula desafios reais de mercado em domínios como Comércio, Serviços Públicos, Negócios, Saúde, Educação e Logística.

---

## 2. Conceitos-Chave e Terminologia Fundamental
*   **Engenharia de Requisitos:** Processo sistemático de descobrir, analisar, especificar e validar as necessidades do cliente e do negócio.
*   **Elicitação:** A técnica de extração e descoberta de informações com os *stakeholders* (partes interessadas).
*   **Requisitos Funcionais (RF):** Descrevem *o que* o sistema deve fazer (comportamentos, regras de negócio e funcionalidades). Ex: *"O sistema deve permitir cadastrar produtos."*
*   **Requisitos Não Funcionais (RNF):** Descrevem *como* o sistema deve se comportar (restrições de qualidade, desempenho, segurança). Ex: *"A consulta de produtos deve responder em até 2 segundos."*
*   **Projeto Orientado a Objetos (OO):** Tradução dos modelos de análise para soluções técnicas utilizando pilares de POO.
*   **Princípios de Design OO:** 
    *   *Abstração:* Ocultação de detalhes irrelevantes, focando no essencial.
    *   *Encapsulamento:* Proteção dos dados internos de uma classe, controlando o acesso por meio de interfaces bem definidas.
    *   *Herança:* Reutilização de código e atributos entre classes genéricas e especializadas.
    *   *Polimorfismo:* Capacidade de executar o mesmo método com comportamentos distintos dependendo do contexto do objeto.
    *   *Baixo Acoplamento e Alta Coesão:* Métricas fundamentais de qualidade de código onde módulos são independentes (baixo acoplamento) e possuem responsabilidades únicas e bem definidas (alta coesão).

---

## 3. Principais Módulos / Tópicos Abordados (com explicações técnicas)

### A. O Ciclo do Projeto de Software
O desenvolvimento passa por 10 fases iterativas e estruturadas:
1.  **Identificação do Problema:** Compreender a dor do usuário e o valor gerado.
2.  **Levantamento de Requisitos:** Elicitação com *stakeholders*.
3.  **Análise e Especificação:** Separação entre RF e RNF.
4.  **Planejamento do Projeto:** Escopo, cronograma, riscos e recursos.
5.  **Arquitetura de Software:** Decisões estruturais (Camadas, MVC, Hexagonal, Clean Architecture, Microsserviços).
6.  **Projeto Detalhado:** Aplicação de princípios SOLID, alta coesão, baixo acoplamento e **Padrões de Projeto (Design Patterns)** como *Strategy, Factory, Observer, Adapter e Facade*.
7.  **Implementação:** Codificação, controle de versão e padrões de código.
8.  **Testes e Garantia da Qualidade:** Testes Unitários, de Integração e End-to-End (E2E).
9.  **Integração, Configuração e Entrega (CI/CD):** Automação de *builds*, contêineres e *deploy*.
10. **Operação, Manutenção e Evolução:** Monitoramento, correções e novas entregas.

### B. Técnicas de Elicitação de Requisitos
*   **Tradicionais:** Entrevistas (estruturadas, semiestruturadas, não estruturadas), Questionários, Observação (acompanhar o usuário em campo), Análise Documental (planilhas, sistemas legados, relatórios) e Workshops/Brainstorming (foco em quantidade de ideias primeiro, avaliação depois).
*   **Complementares:** Prototipação (uso de ferramentas como **Figma** ou **Penpot** para wireframes navegáveis que reduzem ambiguidades) e Storytelling/Cenários.

---

## 4. Relações com o Mercado e Prática Profissional
*   **Foco em Processos Reais:** O mercado de TI não busca apenas programadores que escrevem código, mas engenheiros capazes de entender problemas de negócio complexos (*"Faça a coisa certa e faça certo a coisa"*).
*   **Ferramentas Padrão de Mercado:** O uso proficiente de ferramentas de prototipação colaborativa (Figma/Penpot) e modelagem UML prepara o estudante para interagir diretamente com analistas de negócios, product owners (PO) e arquitetos de software.
*   **Trabalho em Equipe (Projeto Integrador):** Simula o ambiente de metodologias ágeis e desenvolvimento corporativo, exigindo divisão de papéis, controle de versão e entregas incrementais.

---

## 5. Dicas de Ouro para Estudo e Provas
1.  **Diferencie Análise de Projeto:** Lembre-se sempre: *Análise* foca no "O quê" (o problema e os requisitos do usuário); *Projeto* foca no "Como" (a solução técnica, classes, arquitetura e padrões).
2.  **Atenção aos Requisitos Funcionais vs. Não Funcionais:** Em questões de prova, analise se a frase descreve uma ação do sistema (Funcional) ou uma restrição/atributo de qualidade como velocidade, segurança e usabilidade (Não Funcional).
3.  **Domine os Pilares de POO:** Compreenda na prática como o *Encapsulamento* protege regras de negócio e como o *Polimorfismo* elimina estruturas condicionais complexas (if/else ou switch/case) no código.
4.  **Valorize o Contexto do Problema:** Nas avaliações práticas e no Projeto Integrador, justifique sempre suas escolhas arquiteturais e de requisitos com base no cenário real apresentado pelo cliente/stakeholder.