# Laboratório de Programação IV

> **Semestre:** 4º Semestre
> **Professor:** Jefferson Passerini
> **Curso:** Bacharelado em Sistemas de Informação (UniFEF)

## Sumário

- [Aulas](#aulas)
- [Provas](#provas)
- [Revisão](#revisão)
- [Estrutura da pasta](#estrutura-da-pasta)

## Aulas

| Aula | Tema | Código |
| :--- | :--- | :---: |
| [Aula 00 - GitHub e Início do Projeto Spring Boot](Aulas/Aula%2000%20-%20GitHub%20e%20In%C3%ADcio%20do%20Projeto%20Spring%20Boot/detalhes.md) | Fundamentos de controle de versão distribuído com Git, colaboração no GitHub, inicialização do repositório oficial e configuração dos arquivos base do projeto Suporte OS 2026. | - |
| [Aula 01 - Configuração de Ambiente Java Spring Boot](Aulas/Aula%2001%20-%20Configura%C3%A7%C3%A3o%20de%20Ambiente%20Java%20Spring%20Boot/detalhes.md) | Fundamentos da plataforma Java, ferramentas de compilação, variáveis de ambiente e preparação do ecossistema Spring Boot 2026 | [codigo](Aulas/Aula%2001%20-%20Configura%C3%A7%C3%A3o%20de%20Ambiente%20Java%20Spring%20Boot/codigo) |
| [Aula 02 - Spring Boot Criacao do Projeto e Fundamentos REST](Aulas/Aula%2002%20-%20Spring%20Boot%20Criacao%20do%20Projeto%20e%20Fundamentos%20REST/detalhes.md) | Inicialização de projetos com Spring Boot, Maven Wrapper, anatomia do protocolo HTTP e fundamentos de arquitetura REST. | [codigo](Aulas/Aula%2002%20-%20Spring%20Boot%20Criacao%20do%20Projeto%20e%20Fundamentos%20REST/codigo) |
| [Aula 03 - Modelagem de Domínio com Java Puro](Aulas/Aula%2003%20-%20Modelagem%20de%20Dom%C3%ADnio%20com%20Java%20Puro/detalhes.md) | Modelagem orientada a domínio em Java puro, isolamento de regras de negócio, encapsulamento de invariantes e validação por testes unitários com JUnit 5. | [codigo](Aulas/Aula%2003%20-%20Modelagem%20de%20Dom%C3%ADnio%20com%20Java%20Puro/codigo) |
| [Aula 04 - Persistência com JPA PostgreSQL e Liquibase](Aulas/Aula%2004%20-%20Persist%C3%AAncia%20com%20JPA%20PostgreSQL%20e%20Liquibase/detalhes.md) | Fundamentos de persistência relacional com JPA, Hibernate e PostgreSQL, versionamento estrito de esquema via Liquibase, isolamento de ambientes com perfis Spring e blindagem de credenciais. | [codigo](Aulas/Aula%2004%20-%20Persist%C3%AAncia%20com%20JPA%20PostgreSQL%20e%20Liquibase/codigo) |
| [Aula 05 - Spring Data JPA Repositórios Serviços Transações](Aulas/Aula%2005%20-%20Spring%20Data%20JPA%20Reposit%C3%B3rios%20Servi%C3%A7os%20Transa%C3%A7%C3%B5es/detalhes.md) | Organização de arquitetura em camadas com Spring Data JPA, proxies dinâmicos, repositórios, serviços de aplicação e fronteiras transacionais | [codigo](Aulas/Aula%2005%20-%20Spring%20Data%20JPA%20Reposit%C3%B3rios%20Servi%C3%A7os%20Transa%C3%A7%C3%B5es/codigo) |
| [Aula 06 - Evolução do Modelo e Changelogs Assistidos](Aulas/Aula%2006%20-%20Evolu%C3%A7%C3%A3o%20do%20Modelo%20e%20Changelogs%20Assistidos/detalhes.md) | Evolução incremental de entidades JPA, geração assistida de changelogs com liquibase:diff e migrações seguras com padrão expand-migrate-contract | [codigo](Aulas/Aula%2006%20-%20Evolu%C3%A7%C3%A3o%20do%20Modelo%20e%20Changelogs%20Assistidos/codigo) |
| [Aula 07 - API REST DTOs Mapeadores e Postman](Aulas/Aula%2007%20-%20API%20REST%20DTOs%20Mapeadores%20e%20Postman/detalhes.md) | Construção de APIs REST com Spring Boot, DTOs imutáveis, validação declarativa, mapeadores manuais, tratamento global de exceções e automação de testes com MockMvc e Postman. | [codigo](Aulas/Aula%2007%20-%20API%20REST%20DTOs%20Mapeadores%20e%20Postman/codigo) |

## Provas

| Atividade | Prazo | Pontuação |
| :--- | :--- | :--- |
| [20260917 - Avaliação 1](Provas/20260917%20-%20Avalia%C3%A7%C3%A3o%201/detalhes.md) | 17/09/2026 às 23:59 | 100 pontos |

## Revisão

- [Caderno Consolidado](Resumos-IA/Caderno-Consolidado.md): toda a matéria em um documento
- [Simulados Comentados](Resumos-IA/Simulados-Comentados.md): questões objetivas e discursivas com gabarito
- [Mural da Disciplina](Avisos.md): todas as postagens do Classroom em ordem cronológica

## Estrutura da pasta

```text
Laboratório de Programação IV/
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
