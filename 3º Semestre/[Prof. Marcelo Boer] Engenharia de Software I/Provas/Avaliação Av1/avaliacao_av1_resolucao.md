FILENAME: avaliacao_av1_resolucao.md
---CODE_START---
# Avaliação Av1 - Engenharia de Software I
**Curso:** Bacharelado em Sistemas de Informação (3º Semestre)  
**Professor:** Prof. Marcelo Boer  
**Aluno/Desenvolvedor:** Solução Oficial e Completa  

---

## Contextualização do Projeto: Sistema de Gestão de Eventos Acadêmicos (SGEA)

Para responder de forma prática, robusta e alinhada às diretrizes de Engenharia de Software, foi concebido o **SGEA (Sistema de Gestão de Eventos Acadêmicos)**. Este sistema resolve o problema real de universidades que necessitam gerenciar inscrições, submissões de atividades, controle de presença e emissão automatizada de certificados para eventos como semanas de tecnologia, simpósios e congressos.

---

## PARTE 1: Engenharia de Requisitos

### 1.1. Requisitos Funcionais (RF)
Os Requisitos Funcionais descrevem as ações que o sistema deve ser capaz de executar.

| ID | Nome | Descrição | Prioridade |
| :--- | :--- | :--- | :--- |
| **RF-001** | Cadastro de Usuários | O sistema deve permitir o cadastro de alunos, professores e organizadores com nome, e-mail, CPF e senha. | Essencial |
| **RF-002** | Criação de Eventos | O organizador deve poder criar um evento informando título, descrição, data de início, data de término e carga horária total. | Essencial |
| **RF-003** | Inscrição em Atividades | O aluno deve poder se inscrever em atividades (palestras, minicursos) desde que haja vagas disponíveis e não haja choque de horário. | Essencial |
| **RF-004** | Registro de Presença | O organizador ou palestrante deve poder registrar a presença dos alunos nas atividades por meio de um código de validação ou lista manual. | Importante |
| **RF-005** | Emissão de Certificado | O sistema deve gerar automaticamente um certificado em PDF para o aluno que obtiver no mínimo 75% de presença no evento/atividade. | Importante |

### 1.2. Requisitos Não-Funcionais (RNF)
Os Requisitos Não-Funcionais especificam critérios de qualidade, usabilidade, desempenho e segurança.

| ID | Categoria | Descrição | Métrica / Critério de Aceitação |
| :--- | :--- | :--- | :--- |
| **RNF-001** | Desempenho | O tempo de resposta para a emissão e download do certificado digital não deve ultrapassar 3 segundos sob carga normal. | Tempo de resposta < 3s |
| **RNF-002** | Segurança | Todas as senhas de usuários devem ser criptografadas no banco de dados utilizando algoritmos de hash robustos (ex: BCrypt). | Hash BCrypt ativo |
| **RNF-003** | Concorrência | O sistema de inscrições deve suportar até 500 requisições simultâneas no momento de abertura de vagas sem apresentar falhas de concorrência (Race Condition). | Teste de carga com 500 VU/s |

---

### 1.3. Diagrama de Casos de Uso (UML)

O diagrama abaixo modela as interações dos atores com as principais funcionalidades do SGEA.

graph TD
    %% Atores
    Aluno((Aluno))
    Organizador((Organizador))
    Palestrante((Palestrante))

    %% Casos de Uso
    UC01(UC01: Cadastrar-se no Sistema)
    UC02(UC02: Inscrever-se em Atividade)
    UC03(UC03: Criar Evento/Atividade)
    UC04(UC04: Registrar Presença)
    UC05(UC05: Emitir Certificado)
    UC06(UC06: Validar Conflito de Horário)

    %% Associações Aluno
    Aluno --> UC01
    Aluno --> UC02
    Aluno --> UC05

    %% Associações Organizador
    Organizador --> UC03
    Organizador --> UC04
    Organizador --> UC05

    %% Associações Palestrante
    Palestrante --> UC04

    %% Relacionamentos de Casos de Uso
    UC02 -.->|<< include >>| UC06
    UC05 -.->|<< extend >>| UC04

---

### 1.4. Especificação de Caso de Uso Detalhada (UC02: Inscrever-se em Atividade)

*   **Nome do Caso de Uso:** UC02 - Inscrever-se em Atividade
*   **Atores:** Aluno
*   **Pré-condições:** O Aluno deve estar autenticado no sistema e a atividade deve possuir vagas disponíveis.
*   **Pós-condições:** A inscrição é registrada com o status "Pendente" ou "Confirmada", e a vaga é reservada.

#### Fluxo Principal (Caminho Feliz):
1. O Aluno navega pela lista de eventos e seleciona a Atividade desejada.
2. O Aluno clica no botão "Realizar Inscrição".
3. O sistema valida se o Aluno já está inscrito em outra atividade no mesmo horário (**UC06**).
4. O sistema verifica a disponibilidade de vagas.
5. O sistema registra a inscrição do Aluno com o status "Confirmada".
6. O sistema decrementa em 1 o número de vagas disponíveis da atividade.
7. O sistema exibe uma mensagem de sucesso na tela.

#### Fluxo Alternativo (FA01 - Lista de Espera):
*   *No passo 4 do Fluxo Principal, se não houver vagas disponíveis:*
    1. O sistema informa que não há vagas e pergunta se o Aluno deseja entrar na lista de espera.
    2. O Aluno confirma a opção.
    3. O sistema registra a inscrição com o status "Lista de Espera".
    4. O caso de uso é encerrado.

#### Fluxo de Exceção (FE01 - Conflito de Horário):
*   *No passo 3 do Fluxo Principal, se houver choque de horário:*
    1. O sistema emite um alerta informando o conflito com outra atividade já agendada.
    2. A inscrição é abortada.
    3. O caso de uso é encerrado.

---

## PARTE 2: Modelagem de Sistemas (UML)

### 2.1. Diagrama de Classes

Este diagrama representa a estrutura estática do SGEA, mapeando as entidades de domínio, seus atributos, métodos e relacionamentos.

classDiagram
    class Usuario {
        <<Abstract>>
        +int id
        +string nome
        +string email
        +string senha
        +autenticar(string email, string senha) bool
    }

    class Aluno {
        +string matricula
        +inscreverEmAtividade(Atividade atividade) Inscricao
    }

    class Organizador {
        +string cargo
        +criarEvento(string nome, string data) Evento
        +emitirCertificados(Evento evento) list
    }

    class Evento {
        +int id
        +string nome
        +Date dataInicio
        +Date dataFim
        +list~Atividade~ atividades
        +adicionarAtividade(Atividade atividade) void
    }

    class Atividade {
        +int id
        +string titulo
        +Date horario
        +int vagasTotais
        +int vagasDisponiveis
        +double cargaHoraria
        +verificarDisponibilidade() bool
    }

    class Inscricao {
        +int id
        +Date dataInscricao
        +string status
        +bool presencaConfirmada
        +confirmarPresenca() void
    }

    class Certificado {
        +string codigoAutenticidade
        +Date dataEmissao
        +double cargaHoraria
        +gerarPDF() string
    }

    Usuario <|-- Aluno
    Usuario <|-- Organizador
    Evento "1" *-- "1..*" Atividade : Contém
    Aluno "1" --> "0..*" Inscricao : Realiza
    Atividade "1" <-- "0..*" Inscricao : Recebe
    Inscricao "1" --> "0..1" Certificado : Gera
    Organizador "1" --> "0..*" Evento : Gerencia

---

### 2.2. Diagrama de Sequência (Processo de Inscrição)

O diagrama abaixo ilustra a troca de mensagens entre os objetos do sistema durante a execução do caso de uso de inscrição.

sequenceDiagram
    autonumber
    actor Aluno as Aluno (Client)
    participant Controller as InscricaoController
    participant Service