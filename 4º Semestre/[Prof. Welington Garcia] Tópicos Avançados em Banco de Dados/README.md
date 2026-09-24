# Tópicos Avançados em Banco de Dados

> **Semestre:** 4º Semestre
> **Professor:** Welington Garcia
> **Curso:** Bacharelado em Sistemas de Informação (UniFEF)

## Sumário

- [Aulas](#aulas)
- [Trabalhos](#trabalhos)
- [Provas](#provas)
- [Revisão](#revisão)
- [Estrutura da pasta](#estrutura-da-pasta)

## Aulas

| Aula | Tema | Código |
| :--- | :--- | :---: |
| [Aula 02 - Views e Materialized Views em PostgreSQL](Aulas/Aula%2002%20-%20Views%20e%20Materialized%20Views%20em%20PostgreSQL/detalhes.md) | Construção de consultas virtuais reutilizáveis, integridade referencial com WITH CHECK OPTION, segurança de acesso e otimização física com Materialized Views no PostgreSQL. | [codigo](Aulas/Aula%2002%20-%20Views%20e%20Materialized%20Views%20em%20PostgreSQL/codigo) |
| [Aula 03 - Stored Procedures no PostgreSQL com PL pgSQL](Aulas/Aula%2003%20-%20Stored%20Procedures%20no%20PostgreSQL%20com%20PL%20pgSQL/detalhes.md) | Programação procedural no PostgreSQL: criação de procedimentos armazenados, variáveis, controle de fluxo e gestão transacional com PL/pgSQL | [codigo](Aulas/Aula%2003%20-%20Stored%20Procedures%20no%20PostgreSQL%20com%20PL%20pgSQL/codigo) |
| [Aula 04 - Junções e Subconsultas em PostgreSQL](Aulas/Aula%2004%20-%20Jun%C3%A7%C3%B5es%20e%20Subconsultas%20em%20PostgreSQL/detalhes.md) | Álgebra relacional aplicada, técnicas de junção de dados (JOINs), subconsultas escalares, correlacionadas e tabulares, expressões de tabela comuns (CTEs) e otimização de consultas no PostgreSQL. | [codigo](Aulas/Aula%2004%20-%20Jun%C3%A7%C3%B5es%20e%20Subconsultas%20em%20PostgreSQL/codigo) |

## Trabalhos

| Atividade | Prazo | Pontuação |
| :--- | :--- | :--- |
| [Exercicios Joins](Trabalhos/Exercicios%20Joins/detalhes.md) | sem prazo | 100 pontos |
| [Exercicios SubSelects - parte 01](Trabalhos/Exercicios%20SubSelects%20-%20parte%2001/detalhes.md) | sem prazo | 100 pontos |
| [Exerciciso - SUbSelect - parte 2](Trabalhos/Exerciciso%20-%20SUbSelect%20-%20parte%202/detalhes.md) | 02/09/2026 às 23:59 | 100 pontos |
| [Lista de exercícios](Trabalhos/Lista%20de%20exerc%C3%ADcios/detalhes.md) | 09/09/2026 às 19:59 | 100 pontos |

## Provas

| Atividade | Prazo | Pontuação |
| :--- | :--- | :--- |
| [Avaliação de TABD](Provas/Avalia%C3%A7%C3%A3o%20de%20TABD/detalhes.md) | 10/09/2026 às 20:59 | 100 pontos |

## Revisão

- [Caderno Consolidado](Resumos-IA/Caderno-Consolidado.md): toda a matéria em um documento
- [Simulados Comentados](Resumos-IA/Simulados-Comentados.md): questões objetivas e discursivas com gabarito
- [Mural da Disciplina](Avisos.md): todas as postagens do Classroom em ordem cronológica

## Estrutura da pasta

```text
Tópicos Avançados em Banco de Dados/
├── README.md              índice da matéria
├── Avisos.md              mural completo do Classroom
├── Aulas/<Aula NN - Tema>/
│   ├── detalhes.md        explicação completa, diagramas e exercícios
│   ├── codigo/            exercícios e exemplos resolvidos
│   ├── links-recursos.md  links do professor
│   └── <anexos>           PDFs e arquivos originais
├── Trabalhos/ e Provas/   enunciado, resolução proposta e código
└── Resumos-IA/            caderno consolidado e simulados
```
