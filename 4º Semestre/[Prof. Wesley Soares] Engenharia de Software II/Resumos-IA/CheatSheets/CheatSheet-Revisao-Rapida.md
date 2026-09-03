# 📄 Cheat Sheet: Engenharia de Software II
**Professor:** Wesley Soares de Souza | **Foco:** Revisão para Prova (Conceitos Essenciais e Técnicas)

---

## 🔄 1. O Ciclo de Vida de um Projeto de Software
*“Faça a coisa certa (análise) e faça certo a coisa (projeto).”*

1. **Identificação do Problema:** Entender o problema, quem o enfrenta, o contexto atual e o valor gerado.
2. **Levantamento e Requisitos (Elicitação):** Descobrir necessidades com stakeholders.
3. **Análise e Especificação:** Organizar e documentar (Funcionais vs. Não Funcionais).
4. **Planejamento:** Escopo, cronograma, equipe, riscos e entregas.
5. **Arquitetura de Software:** Decisões estruturais (Camadas, MVC, Hexagonal, Clean Arch, Microservices).
6. **Projeto Detalhado (OO):** Classes, interfaces, SOLID, Alta Coesão e Baixo Acoplamento.
7. **Implementação:** Codificação, controle de versão, padrões de projeto e commits frequentes.
8. **Testes e Qualidade:** Unitários, de Integração e End-to-End (E2E).
9. **Integração e Entrega (CI/CD):** Pipelines, contêineres e deploy automatizado.
10. **Operação e Manutenção:** Correções, melhorias e evolução contínua.

---

## 🎯 2. Engenharia e Elicitação de Requisitos
* **Elicitação:** Processo de descobrir, coletar e compreender necessidades (`Necessidade -> Problema -> Contexto -> Expectativa -> Requisito`).
* **Dificuldades Comuns:** Requisitos implícitos, conflitos entre stakeholders, termos diferentes, mudança de ideia do cliente.

### 🛠️ Técnicas de Elicitação
| Tradicionais | Complementares |
| :--- | :--- |
| **Entrevista:** Estruturada, Semiestruturada ou Não estruturada. | **Prototipação:** Wireframes / Telas (Figma, Penpot) para tangibilizar o requisito. |
| **Questionário:** Coleta ampla de dados. | **Cenários & Storytelling:** Descrição de histórias de uso e fluxos. |
| **Observação:** Acompanhar o usuário no trabalho real (revela atalhos/exceções). | **Event Storming:** Mapeamento rápido de eventos de domínio. |
| **Análise Documental:** Ler planilhas, manuais, sistemas antigos. | **Workshops & Brainstorming:** Reuniões coletivas (*Quantidade primeiro, avaliação depois*). |

### 📌 Tipos de Requisitos
* **Requisito Funcional (O que faz):** *"O sistema deve notificar o vendedor quando um pedido for registrado."*
* **Requisito Não Funcional (Como se comporta / Restrições):** *"A notificação deve ocorrer em até 5 segundos."*

---

## 🧱 3. Projeto Orientado a Objetos (OO) & Princípios de Design
* **Análise:** O que fazer | **Projeto:** Como fazer.

### ⚙️ Princípios Fundamentais (OO)
* **Abstração:** Ocultar detalhes irrelevantes, focando apenas nos conceitos essenciais do domínio.
* **Encapsulamento:** Proteger dados internos (`private`), permitindo acesso restrito via `Getters` e `Setters` para garantir regras de negócio.
* **Herança:** Reutilizar atributos e comportamentos criando subclasses a partir de classes genéricas (`Pessoa` ➔ `Cliente`).
* **Polimorfismo:** Mesma assinatura de método com comportamentos diferentes dependendo do objeto (`calcularBonus()` para `Gerente` vs. `Vendedor`).
* **Baixo Acoplamento:** Módulos independentes; alterações em um afetam pouco os outros.
* **Alta Coesão:** Cada classe/módulo executa uma única responsabilidade de forma clara.

### 📐 Padrões de Projeto (Design Patterns) citados:
* **Criação / Estruturais / Comportamentais:** `Strategy`, `Factory`, `Observer`, `Adapter`, `Facade`.

---

## 💡 Dicas Rápidas para a Prova
* **Funcional vs. Não Funcional:** Lembre-se: Funcional é *função/ação* do sistema; Não Funcional é *qualidade* (velocidade, segurança, usabilidade).
* **MoSCoW:** Método de priorização de requisitos citado para focar no que é essencial (Must, Should, Could, Would/Won't).
* **Brainstorming:** A regra de ouro é separar a geração de ideias da avaliação (primeiro quantidade, depois filtragem).