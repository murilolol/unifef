# Prova / Avaliação — Av1

> **Professor:** Marcelo Tadeu Boer
> **Disciplina:** Engenharia de Software I (3º Semestre)
> **Prazo de entrega:** 25/03/2026 às 02:59
> **Pontuação máxima:** 6 pontos

## Descrição da avaliação

A pasta original não contém um arquivo de enunciado escrito próprio para a Av1 — a revisão de conteúdo para essa prova foi disponibilizada pelo professor por meio de dois formulários do Google Forms (ver [`links-recursos.md`](../../Aulas/links-recursos.md) e o post [`2026-03-24 - Forms contendo questes de revises p.md`](../../Aulas/2026-03-24%20-%20Forms%20contendo%20questes%20de%20revises%20p.md)), cobrindo Engenharia de Requisitos e Modelagem UML (Casos de Uso e Classes). Não há, portanto, um enunciado formal em `.docx`/`.pdf` a reproduzir aqui além dos links de revisão — sinalizado para transparência, em vez de conteúdo inventado.

## Formulários de revisão (Google Forms)

- [Formulário 1](https://docs.google.com/forms/d/e/1FAIpQLSek7MXFyyZS-L-izhvtx2obbkb2z-UVScKG6CoUayx4wwsUkA/viewform?usp=publish-editor)
- [Formulário 2](https://docs.google.com/forms/d/e/1FAIpQLSd6zutTuz-i2dDV6McV9dF69tidtgBREhZspVirWI9DDCp8hA/viewform?usp=dialog)

## Resolução

A resolução em [`avaliacao_av1_resolucao.md`](./avaliacao_av1_resolucao.md) desenvolve um estudo de caso completo — o **SGEA (Sistema de Gestão de Eventos Acadêmicos)** — cobrindo:

- **Parte 1 — Engenharia de Requisitos:** Requisitos Funcionais e Não-Funcionais tabelados, Diagrama de Casos de Uso (com relações `<<include>>`/`<<extend>>`) e uma especificação de Caso de Uso detalhada (UC02 – Inscrever-se em Atividade), com fluxo principal, fluxo alternativo e fluxo de exceção.
- **Parte 2 — Modelagem de Sistemas (UML):** Diagrama de Classes completo (`Usuario`, `Aluno`, `Organizador`, `Evento`, `Atividade`, `Inscricao`, `Certificado`) e Diagrama de Sequência do processo de inscrição.

O script complementar [`resolucao_exercicio.sql`](./resolucao_exercicio.sql) traz o modelo de dados derivado dessa modelagem.

## Arquivos entregues

- [`avaliacao_av1_resolucao.md`](./avaliacao_av1_resolucao.md) — resolução completa do estudo de caso SGEA.
- [`resolucao_exercicio.sql`](./resolucao_exercicio.sql) — script complementar do modelo de dados.

## Material relacionado

- [Aula: Casos de Uso, Atores e Ferramenta Astah UML](../../Aulas/Casos%20de%20Uso%2C%20Atores%20e%20Ferramenta%20Astah%20UML/detalhes.md)
- [Aula: Contexto do Aplicativo e Engenharia de Requisitos](../../Aulas/Contexto%20do%20Aplicativo%20e%20Engenharia%20de%20Requisitos/detalhes.md)
- [Resumo executivo, exercícios, simulado e cheatsheet da disciplina](../../README.md#resumo-executivo)
