# Trabalho — Trabalho Semestral

> **Professor:** Wesley Soares
> **Disciplina:** Engenharia de Software II (4º Semestre)
> **Prazo de Entrega:** 09/09/2026 às 02:59
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** Engenharia de Requisitos ([Aula 03](../../Aulas/03%20Elicitacao%20e%20Levantamento%20de%20Requisitos/detalhes.md)), priorização MoSCoW ([Aula 02](../../Aulas/02%20Projeto%20Orientado%20a%20Objetos%20e%20Metodo%20MoSCoW/detalhes.md)) e Casos de Uso ([Aula 04](../../Aulas/04%20Diagrama%20de%20Casos%20de%20Uso/detalhes.md))

## Enunciado original (Google Classroom)

Subir documento (Word ou Google Docs) contendo:

1. **Levantamento de Requisitos** completo.
2. **Diagrama / Matriz MoSCoW** (Must have, Should have, Could have, Won't have).
3. **Casos de Uso Geral e Específico**, acompanhados de suas respectivas documentações e descrições de fluxo.

Este trabalho consolida, em um único entregável, o **Projeto Integrador da disciplina** apresentado na [Aula 01](../../Aulas/01%20Ciclo%20de%20Vida%20do%20Projeto%20de%20Software/detalhes.md#projeto-integrador-da-disciplina) — a 1ª Etapa do projeto (Engenharia de Requisitos), formada por equipes de 3 pessoas.

## Resolução entregue

A equipe escolheu como sistema fictício o **Sistema de Gestão de Biblioteca Universitária (SGBU)**, informatizando acervo, empréstimos, devoluções, multas e cadastro de usuários (alunos, professores e bibliotecários). O documento entregue contém:

- **Requisitos Funcionais (RF01–RF07):** cadastro de usuários, gerenciamento de acervo, empréstimo, cálculo de prazo por tipo de usuário, devolução com cálculo de multa, consulta ao acervo e relatórios.
- **Requisitos Não Funcionais (RNF01–RNF04):** desempenho (buscas em até 2s), segurança (criptografia forte e RBAC), disponibilidade (99,9%) e portabilidade (arquitetura RESTful).
- **Matriz MoSCoW:** Must have (cadastro, acervo, empréstimo, devolução/multa, consulta), Should have (prazos diferenciados por tipo de usuário, criptografia avançada), Could have (relatórios gerenciais em PDF/Excel, notificações por e-mail), Won't have (integração com catálogos externos, aplicativo mobile nativo).
- **Diagrama de Casos de Uso Geral** (Mermaid), com os atores Bibliotecário, Usuário/Leitor e Sistema de Pagamento/Multas.
- **Casos de Uso Específicos documentados**, incluindo CU01 (Realizar Empréstimo de Livro) e CU02 (Cadastrar Novo Usuário), com fluxo principal e alternativo detalhados.

## Arquivos entregues

- [`trabalho_semestral_engenharia_software_ii.md`](./trabalho_semestral_engenharia_software_ii.md) — documento completo: levantamento de requisitos, priorização MoSCoW, diagrama de casos de uso geral e documentação dos casos de uso específicos.

## Material relacionado

- [Aula 01 — Ciclo de Vida do Projeto de Software](../../Aulas/01%20Ciclo%20de%20Vida%20do%20Projeto%20de%20Software/detalhes.md) (Projeto Integrador)
- [Aula 02 — Projeto Orientado a Objetos e Método MoSCoW](../../Aulas/02%20Projeto%20Orientado%20a%20Objetos%20e%20Metodo%20MoSCoW/detalhes.md)
- [Aula 03 — Elicitação e Levantamento de Requisitos](../../Aulas/03%20Elicitacao%20e%20Levantamento%20de%20Requisitos/detalhes.md)
- [Aula 04 — Diagrama de Casos de Uso](../../Aulas/04%20Diagrama%20de%20Casos%20de%20Uso/detalhes.md)
- [Resumo executivo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
