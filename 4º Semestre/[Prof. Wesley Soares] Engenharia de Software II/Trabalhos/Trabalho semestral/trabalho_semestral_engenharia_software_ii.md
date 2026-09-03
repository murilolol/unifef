# Universidade de Sistemas de Informação
## Disciplina: Engenharia de Software II
**Professor:** Prof. Wesley Soares  
**Aluno:** Desenvolvedor Sênior / Aluno de Sistemas de Informação  
**Data:** 09/09/2026  
**Título da Atividade:** Trabalho Semestral - Documentação de Engenharia de Software

---

# SUMÁRIO
1. [Introdução e Escopo do Sistema](#1-introdução-e-escopo-do-sistema)
2. [Levantamento de Requisitos](#2-levantamento-de-requisitos)
   - 2.1 Requisitos Funcionais (RF)
   - 2.2 Requisitos Não Funcionais (RNF)
3. [Priorização MoSCoW](#3-priorização-moscow)
4. [Diagrama de Casos de Uso Geral (Mermaid)](#4-diagrama-de-casos-de-uso-geral-mermaid)
5. [Casos de Uso Específicos e Documentação Detalhada](#5-casos-de-uso-específicos-e-documentação-detalhada)
   - 5.1 CU01: Realizar Empréstimo de Livro
   - 5.2 CU02: Cadastrar Novo Usuário

---

## 1. Introdução e Escopo do Sistema
O presente documento consolida o trabalho semestral da disciplina de **Engenharia de Software II**. O sistema escolhido para modelagem e especificação é o **Sistema de Gestão de Biblioteca Universitária (SGBU)**. O SGBU tem como objetivo informatizar o acervo bibliográfico, controle de empréstimos, devoluções, multas e cadastro de usuários (alunos, professores e bibliotecários) da instituição de ensino.

---

## 2. Levantamento de Requisitos

### 2.1 Requisitos Funcionais (RF)
* **RF01:** O sistema deve permitir o cadastro, alteração e desativação de usuários (Alunos, Professores e Funcionários).
* **RF02:** O sistema deve permitir o gerenciamento do acervo (cadastrar, atualizar e remover livros e periódicos).
* **RF03:** O sistema deve permitir a realização de empréstimos de exemplares para usuários ativos e sem pendências.
* **RF04:** O sistema deve calcular automaticamente a data de devolução com base no tipo de usuário (ex: 14 dias para alunos, 30 dias para professores).
* **RF05:** O sistema deve registrar a devolução de exemplares e calcular multas por atraso, caso aplicável.
* **RF06:** O sistema deve permitir a consulta ao acervo por título, autor, ISBN ou categoria.
* **RF07:** O sistema deve gerar relatórios de empréstimos ativos, atrasados e histórico por usuário.

### 2.2 Requisitos Não Funcionais (RNF)
* **RNF01 (Desempenho):** As consultas ao acervo devem retornar resultados em no máximo 2 segundos para buscas textuais.
* **RNF02 (Segurança):** As senhas dos usuários devem ser armazenadas utilizando criptografia forte (ex: bcrypt). O acesso deve ser baseado em papéis (RBAC).
* **RNF03 (Disponibilidade):** O sistema deve estar disponível 99,9% do tempo durante os dias úteis.
* **RNF04 (Portabilidade):** O sistema backend deve ser desenvolvido em arquitetura RESTful, acessível por navegadores modernos (Chrome, Firefox, Edge).

---

## 3. Priorização MoSCoW

A técnica MoSCoW foi aplicada para definir o escopo do MVP (Minimum Viable Product):

* **Must have (Deve ter - Essencial para o sistema funcionar):**
  * RF01: Cadastro de Usuários
  * RF02: Gerenciamento de Acervo
  * RF03: Realizar Empréstimo
  * RF05: Registrar Devolução e Multa
  * RF06: Consulta ao Acervo

* **Should have (Deveria ter - Importante, mas não crítico para o MVP):**
  * RF04: Regras diferenciadas de prazo por tipo de usuário
  * RNF02: Criptografia avançada e controle RBAC

* **Could have (Poderia ter - Desejável se houver tempo):**
  * RF07: Relatórios gerenciais avançados em PDF/Excel
  * Notificações automáticas por e-mail de devolução próxima

* **Won't have (Não terá nesta versão - Fora do escopo atual):**
  * Integração com catálogos externos de bibliotecas internacionais
  * Aplicativo mobile nativo (será focado em versão Web Responsiva)

---

## 4. Diagrama de Casos de Uso Geral (Mermaid)

```mermaid
actor Bibliotecario
actor Usuario as "Usuário / Leitor"
actor Sistema as "Sistema de Pagamento / Multas"

rectangle "Sistema de Gestão de Biblioteca (SGBU)" {
    usecase "Cadastrar Usuário" as UC1
    usecase "Gerenciar Acervo" as UC2
    usecase "Consultar Acervo" as UC3
    usecase "Realizar Empréstimo" as UC4
    usecase "Devolver Livro" as UC5
    usecase "Pagar Multa" as UC6
}

Bibliotecario --> UC1
Bibliotecario --> UC2
Bibliotecario --> UC4
Bibliotecario --> UC5

Usuario --> UC3
Usuario --> UC6

UC4 ..> UC3 : <<include>>
UC5 ..> UC6 : <<extend>>
```

---

## 5. Casos de Uso Específicos e Documentação Detalhada

### 5.1 CU01: Realizar Empréstimo de Livro

* **Identificador:** CU01
* **Nome:** Realizar Empréstimo de Livro
* **Ator Principal:** Bibliotecario
* **Atores Secundários:** Sistema de Banco de Dados
* **Pré-condições:** 
  1. O usuário deve estar cadastrado e ativo no sistema.
  2. O usuário não pode possuir multas pendentes ou livros em atraso.
  3. O livro deve possuir ao menos um exemplar disponível no acervo.
* **Pós-condições:** 
  1. O status do exemplar muda de "Disponível" para "Emprestado".
  2. Um registro de empréstimo é criado com a data atual e a data prevista de devolução.

#### Fluxo Principal (Caminho Feliz):
1. O Bibliotecário inicia a operação de empréstimo no sistema.
2. O sistema solicita a identificação do usuário (Matrícula ou CPF).
3. O Bibliotecário insere a identificação.
4. O sistema valida o cadastro, verifica se há débitos ou atrasos (se ok, prossegue).
5. O sistema solicita o código do exemplar do livro (ISBN ou código de barras).
6. O Bibliotecário pistola/insere o código do livro.
7. O sistema valida a disponibilidade do exemplar.
8. O sistema calcula a data de devolução com base no tipo de usuário e registra o empréstimo.
9. O sistema emite o comprovante de empréstimo e encerra o caso de uso.

#### Fluxos Alternativos / Exceções:
* **4a. Usuário com pendências ou multas:**
  1. O sistema exibe mensagem de erro informando o bloqueio por débito/atraso.
  2. O caso de uso é encerrado.
* **7a. Exemplar indisponível:**
  1. O sistema informa que não há exemplares disponíveis no momento.
  2. O Bibliotecário pode optar por registrar na lista de espera (se aplicável) ou cancelar a operação.

---

### 5.2 CU02: Cadastrar Novo Usuário

* **Identificador:** CU02
* **Nome:** Cadastrar Novo Usuário
* **Ator Principal:** Bibliotecario
* **Pré-condições:** O operador deve estar autenticado com perfil de Bibliotecário ou Administrador.
* **Pós-condições:** O novo usuário é salvo no banco de dados e habilitado para realizar empréstimos.

#### Fluxo Principal (Caminho Feliz):
1. O Bibliotecário acessa a opção "Cadastrar Usuário".
2. O sistema apresenta o formulário de cadastro (Nome, CPF, E-mail, Telefone, Tipo de Usuário: Aluno/Professor).
3. O Bibliotecário preenche os dados obrigatórios e submete o formulário.
4. O sistema valida o formato dos dados (CPF válido, e-mail único).
5. O sistema grava o registro no banco de dados e exibe mensagem de sucesso.

#### Fluxo Alternativo / Exceções:
* **4a. CPF já cadastrado ou e-mail duplicado:**
  1. O sistema exibe mensagem de alerta informando que o registro já existe na base de dados.
  2. O Bibliotecário corrige os dados ou cancela o cadastro.