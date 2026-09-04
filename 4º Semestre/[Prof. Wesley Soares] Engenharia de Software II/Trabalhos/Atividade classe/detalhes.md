# Trabalho — Atividade de Classe (Aula 4)

> **Professor:** Wesley Soares
> **Disciplina:** Engenharia de Software II (4º Semestre)
> **Prazo de Entrega:** 26/08/2026 às 02:59
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** diagrama de classes discutido na [Aula 04](../../Aulas/04%20Diagrama%20de%20Casos%20de%20Uso/detalhes.md) e nos fundamentos de UML da [Aula 05](../../Aulas/05%20Diagrama%20de%20Classes%20UML/detalhes.md)

## Enunciado original (Google Classroom)

Enviar o diagrama desenvolvido e discutido em sala de aula durante a **Aula 4**.

## Resolução entregue

A equipe modelou um **diagrama de classes de gerenciamento acadêmico e de turmas** (Usuário → Aluno/Professor, Turma, e a classe associativa MatriculaTurma para o relacionamento N:M entre Aluno e Turma), aplicando os princípios de projeto orientado a objetos estudados na disciplina:

- **Herança:** a classe `Usuario` concentra atributos e métodos comuns (`id`, `nome`, `email`, `senha`, `autenticar()`), especializados por `Aluno` e `Professor`.
- **Classe associativa (relacionamento N:M):** `MatriculaTurma` resolve o relacionamento muitos-para-muitos entre `Aluno` e `Turma`, guardando `dataMatricula`, `notaFinal` e `status`.
- **Inversão de dependência (SOLID):** `Professor` depende da interface `SistemaNotificacao`, não de uma implementação concreta como `EmailNotificacao`, permitindo trocar o mecanismo de notificação sem alterar a classe `Professor`.

## Arquivos entregues

- [`atividade_classe_aula4.md`](./atividade_classe_aula4.md) — documento de entrega com a especificação completa do diagrama.
- [`diagrama_classe_aula4.md`](./diagrama_classe_aula4.md) — diagrama de classes em Mermaid com a descrição de cada componente.

## Material relacionado

- [Aula 04 — Diagrama de Casos de Uso](../../Aulas/04%20Diagrama%20de%20Casos%20de%20Uso/detalhes.md)
- [Aula 05 — Diagrama de Classes UML](../../Aulas/05%20Diagrama%20de%20Classes%20UML/detalhes.md) (tipos de relacionamento — associação, agregação, composição, generalização, dependência)
- [Resumo executivo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
