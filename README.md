# Acervo Acadêmico - Sistemas de Informação (UniFEF)

> Material de estudo vivo do Bacharelado em Sistemas de Informação do Centro Universitário UniFEF: cada aula, trabalho e prova postados pelos professores no Google Classroom, documentados em profundidade, com diagramas, código resolvido e material de revisão.

**Última sincronização:** 25 de setembro de 2026 às 00:19 (horário de Brasília)

## Sumário

- [Sobre o projeto](#sobre-o-projeto)
- [Por que este acervo existe](#por-que-este-acervo-existe)
- [O acervo em números](#o-acervo-em-números)
- [Disciplinas](#disciplinas)
- [Como estudar com este material](#como-estudar-com-este-material)
- [Anatomia de uma disciplina](#anatomia-de-uma-disciplina)
- [Como o acervo é construído](#como-o-acervo-é-construído)
- [Tecnologias do motor de sincronização](#tecnologias-do-motor-de-sincronização)
- [Princípios de qualidade](#princípios-de-qualidade)
- [Aviso acadêmico](#aviso-acadêmico)
- [Autor e licença](#autor-e-licença)

## Sobre o projeto

Este repositório reúne, organiza e explica todo o conteúdo das disciplinas cursadas no Bacharelado em Sistemas de Informação da UniFEF. Ele não é um depósito de arquivos soltos: cada postagem do professor passa por um processo que baixa os anexos, lê os links citados (repositórios GitHub, páginas do Notion, formulários) e transforma tudo em documentação técnica navegável.

O resultado, para cada disciplina, é:

- **Aulas documentadas**: explicação aprofundada de cada tópico, com diagramas UML, ER e de fluxo, tabelas comparativas, glossário e exercícios resolvidos passo a passo.
- **Código pronto para executar**: exemplos do professor organizados e comentados, e resolução completa dos exercícios na linguagem da disciplina.
- **Trabalhos e provas**: enunciado original, análise do que é pedido, fundamentação teórica e uma resolução proposta comentada.
- **Revisão**: um caderno consolidado com toda a matéria e simulados comentados no estilo ENADE.
- **Mural completo**: todas as postagens do Classroom preservadas em ordem cronológica.

## Por que este acervo existe

| Problema no dia a dia do curso | Como o acervo resolve |
| :--- | :--- |
| Material espalhado entre Classroom, Drive, Notion, GitHub e formulários | Tudo consolidado em uma pasta por disciplina, com links preservados |
| Slides e PDFs resumidos demais para estudar sozinho | Cada aula vira um documento explicativo com contexto, exemplos e armadilhas comuns |
| Exercícios sem gabarito | Resolução completa e comentada, com código executável em `codigo/` |
| Revisar um semestre inteiro antes da prova | Caderno consolidado por disciplina e simulados com justificativa de cada alternativa |
| Conteúdo que some quando a turma do Classroom é arquivada | Histórico permanente e versionado em Git |

Além de apoio aos estudos, o projeto é também um exercício prático de engenharia de software: integração com APIs do Google Workspace, processamento de documentos, orquestração de modelos de linguagem com tolerância a falhas e automação de publicação.

## O acervo em números

| Indicador | Total |
| :--- | ---: |
| Semestres | 2 |
| Disciplinas | 8 |
| Aulas documentadas | 46 |
| Trabalhos e provas | 28 |
| Arquivos de código resolvido | 216 |
| Linhas de documentação (Markdown) | 116.576 |
| Linguagens nos códigos | C, Java, SQL |

## Disciplinas

### 4º Semestre

| Disciplina | Professor | Aulas | Trabalhos e provas | Código | Revisão |
| :--- | :--- | :---: | :---: | :--- | :--- |
| [Laboratório de Programação IV](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/README.md) | Jefferson Passerini | 8 | 1 | Java, SQL | [Caderno](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Resumos-IA/Caderno-Consolidado.md) · [Simulados](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Resumos-IA/Simulados-Comentados.md) |
| [Tópicos Avançados em Banco de Dados](4%C2%BA%20Semestre/%5BProf.%20Welington%20Garcia%5D%20T%C3%B3picos%20Avan%C3%A7ados%20em%20Banco%20de%20Dados/README.md) | Welington Garcia | 3 | 5 | SQL | [Caderno](4%C2%BA%20Semestre/%5BProf.%20Welington%20Garcia%5D%20T%C3%B3picos%20Avan%C3%A7ados%20em%20Banco%20de%20Dados/Resumos-IA/Caderno-Consolidado.md) · [Simulados](4%C2%BA%20Semestre/%5BProf.%20Welington%20Garcia%5D%20T%C3%B3picos%20Avan%C3%A7ados%20em%20Banco%20de%20Dados/Resumos-IA/Simulados-Comentados.md) |
| [Engenharia de Software II](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/README.md) | Wesley Soares | 7 | 3 | Java | [Caderno](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Resumos-IA/Caderno-Consolidado.md) · [Simulados](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Resumos-IA/Simulados-Comentados.md) |
| [Estrutura de Dados I](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/README.md) | Wesley Soares | 6 | 2 | Java | [Caderno](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Resumos-IA/Caderno-Consolidado.md) · [Simulados](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Resumos-IA/Simulados-Comentados.md) |

**Laboratório de Programação IV** - conteúdo das aulas:

- [Aula 00 - GitHub e Início do Projeto Spring Boot](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2000%20-%20GitHub%20e%20In%C3%ADcio%20do%20Projeto%20Spring%20Boot/detalhes.md)
- [Aula 01 - Configuração de Ambiente Java Spring Boot](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2001%20-%20Configura%C3%A7%C3%A3o%20de%20Ambiente%20Java%20Spring%20Boot/detalhes.md)
- [Aula 02 - Spring Boot Criacao do Projeto e Fundamentos REST](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2002%20-%20Spring%20Boot%20Criacao%20do%20Projeto%20e%20Fundamentos%20REST/detalhes.md)
- [Aula 03 - Modelagem de Domínio com Java Puro](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2003%20-%20Modelagem%20de%20Dom%C3%ADnio%20com%20Java%20Puro/detalhes.md)
- [Aula 04 - Persistência com JPA PostgreSQL e Liquibase](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2004%20-%20Persist%C3%AAncia%20com%20JPA%20PostgreSQL%20e%20Liquibase/detalhes.md)
- [Aula 05 - Spring Data JPA Repositórios Serviços Transações](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2005%20-%20Spring%20Data%20JPA%20Reposit%C3%B3rios%20Servi%C3%A7os%20Transa%C3%A7%C3%B5es/detalhes.md)
- [Aula 06 - Evolução do Modelo e Changelogs Assistidos](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2006%20-%20Evolu%C3%A7%C3%A3o%20do%20Modelo%20e%20Changelogs%20Assistidos/detalhes.md)
- [Aula 07 - API REST DTOs Mapeadores e Postman](4%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20IV/Aulas/Aula%2007%20-%20API%20REST%20DTOs%20Mapeadores%20e%20Postman/detalhes.md)

**Tópicos Avançados em Banco de Dados** - conteúdo das aulas:

- [Aula 02 - Views e Materialized Views em PostgreSQL](4%C2%BA%20Semestre/%5BProf.%20Welington%20Garcia%5D%20T%C3%B3picos%20Avan%C3%A7ados%20em%20Banco%20de%20Dados/Aulas/Aula%2002%20-%20Views%20e%20Materialized%20Views%20em%20PostgreSQL/detalhes.md)
- [Aula 03 - Stored Procedures no PostgreSQL com PL pgSQL](4%C2%BA%20Semestre/%5BProf.%20Welington%20Garcia%5D%20T%C3%B3picos%20Avan%C3%A7ados%20em%20Banco%20de%20Dados/Aulas/Aula%2003%20-%20Stored%20Procedures%20no%20PostgreSQL%20com%20PL%20pgSQL/detalhes.md)
- [Aula 04 - Junções e Subconsultas em PostgreSQL](4%C2%BA%20Semestre/%5BProf.%20Welington%20Garcia%5D%20T%C3%B3picos%20Avan%C3%A7ados%20em%20Banco%20de%20Dados/Aulas/Aula%2004%20-%20Jun%C3%A7%C3%B5es%20e%20Subconsultas%20em%20PostgreSQL/detalhes.md)

**Engenharia de Software II** - conteúdo das aulas:

- [Aula 01 - Introdução ao Ciclo de Vida do Projeto de Software](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2001%20-%20Introdu%C3%A7%C3%A3o%20ao%20Ciclo%20de%20Vida%20do%20Projeto%20de%20Software/detalhes.md)
- [Aula 02 - Fundamentos de Projeto Orientado a Objetos](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2002%20-%20Fundamentos%20de%20Projeto%20Orientado%20a%20Objetos/detalhes.md)
- [Aula 03 - Técnicas de Elicitação e Levantamento de Requisitos](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2003%20-%20T%C3%A9cnicas%20de%20Elicita%C3%A7%C3%A3o%20e%20Levantamento%20de%20Requisitos/detalhes.md)
- [Aula 04 - Modelagem de Casos de Uso UML](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2004%20-%20Modelagem%20de%20Casos%20de%20Uso%20UML/detalhes.md)
- [Aula 06 - Modelagem de Requisitos e Casos de Uso](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2006%20-%20Modelagem%20de%20Requisitos%20e%20Casos%20de%20Uso/detalhes.md)
- [Aula 07 - Fundamentos da UML e Diagrama de Classes](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2007%20-%20Fundamentos%20da%20UML%20e%20Diagrama%20de%20Classes/detalhes.md)
- [Aula 08 - Arquitetura Model-View-Controller no Smalltalk-80](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Engenharia%20de%20Software%20II/Aulas/Aula%2008%20-%20Arquitetura%20Model-View-Controller%20no%20Smalltalk-80/detalhes.md)

**Estrutura de Dados I** - conteúdo das aulas:

- [Aula 01 - Introdução a Algoritmos e Estrutura de Dados](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Aulas/Aula%2001%20-%20Introdu%C3%A7%C3%A3o%20a%20Algoritmos%20e%20Estrutura%20de%20Dados/detalhes.md)
- [Aula 02 - Fundamentos e Análise de Algoritmos](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Aulas/Aula%2002%20-%20Fundamentos%20e%20An%C3%A1lise%20de%20Algoritmos/detalhes.md)
- [Aula 03 - Listas ligadas dinâmicas](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Aulas/Aula%2003%20-%20Listas%20ligadas%20din%C3%A2micas/detalhes.md)
- [Aula 04 - Fundamentos de Estruturas Lineares e Complexidade](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Aulas/Aula%2004%20-%20Fundamentos%20de%20Estruturas%20Lineares%20e%20Complexidade/detalhes.md)
- [Aula 06 - Pilhas Conceito e Implementacao](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Aulas/Aula%2006%20-%20Pilhas%20Conceito%20e%20Implementacao/detalhes.md)
- [Aula 07 - Operações em Lista Ligada e Desempenho](4%C2%BA%20Semestre/%5BProf.%20Wesley%20Soares%5D%20Estrutura%20de%20Dados%20I/Aulas/Aula%2007%20-%20Opera%C3%A7%C3%B5es%20em%20Lista%20Ligada%20e%20Desempenho/detalhes.md)

### 3º Semestre

| Disciplina | Professor | Aulas | Trabalhos e provas | Código | Revisão |
| :--- | :--- | :---: | :---: | :--- | :--- |
| [Banco de Dados II](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/README.md) | Guilherme de Morais | 7 | 5 | SQL | [Caderno](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Resumos-IA/Caderno-Consolidado.md) · [Simulados](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Resumos-IA/Simulados-Comentados.md) |
| [Sistemas Operacionais](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/README.md) | Guilherme de Morais | 4 | 1 | C | [Caderno](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/Resumos-IA/Caderno-Consolidado.md) · [Simulados](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/Resumos-IA/Simulados-Comentados.md) |
| [Laboratório de Programação III](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/README.md) | Jefferson Passerini | 5 | 5 | Java, SQL | [Caderno](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Resumos-IA/Caderno-Consolidado.md) · [Simulados](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Resumos-IA/Simulados-Comentados.md) |
| [Engenharia de Software I](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/README.md) | Marcelo Boer | 6 | 6 | Java | [Caderno](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Resumos-IA/Caderno-Consolidado.md) · [Simulados](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Resumos-IA/Simulados-Comentados.md) |

**Banco de Dados II** - conteúdo das aulas:

- [Aula 01 - Manipulação e Consulta de Dados em SQL](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2001%20-%20Manipula%C3%A7%C3%A3o%20e%20Consulta%20de%20Dados%20em%20SQL/detalhes.md)
- [Aula 02 - Chave Estrangeira e Modificações com DDL](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2002%20-%20Chave%20Estrangeira%20e%20Modifica%C3%A7%C3%B5es%20com%20DDL/detalhes.md)
- [Aula 03 - Consultas Práticas e Filtros em SQL](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2003%20-%20Consultas%20Pr%C3%A1ticas%20e%20Filtros%20em%20SQL/detalhes.md)
- [Aula 04 - Comando IN e Junções em SQL](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2004%20-%20Comando%20IN%20e%20Jun%C3%A7%C3%B5es%20em%20SQL/detalhes.md)
- [Aula 05 - Funções de Data Hora e Strings](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2005%20-%20Fun%C3%A7%C3%B5es%20de%20Data%20Hora%20e%20Strings/detalhes.md)
- [Aula 06 - Junções e Agrupamentos em Duas Tabelas](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2006%20-%20Jun%C3%A7%C3%B5es%20e%20Agrupamentos%20em%20Duas%20Tabelas/detalhes.md)
- [Aula 07 - Consultas SQL, Operadores e Funções Agregadas](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Banco%20de%20Dados%20II/Aulas/Aula%2007%20-%20Consultas%20SQL%2C%20Operadores%20e%20Fun%C3%A7%C3%B5es%20Agregadas/detalhes.md)

**Sistemas Operacionais** - conteúdo das aulas:

- [Aula 01 - Gerenciamento de Processos e Blocos de Controle](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/Aulas/Aula%2001%20-%20Gerenciamento%20de%20Processos%20e%20Blocos%20de%20Controle/detalhes.md)
- [Aula 02 - Evolução dos Sistemas Operacionais e Arquiteturas](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/Aulas/Aula%2002%20-%20Evolu%C3%A7%C3%A3o%20dos%20Sistemas%20Operacionais%20e%20Arquiteturas/detalhes.md)
- [Aula 04 - Organização e Gerenciamento da Memória Real](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/Aulas/Aula%2004%20-%20Organiza%C3%A7%C3%A3o%20e%20Gerenciamento%20da%20Mem%C3%B3ria%20Real/detalhes.md)
- [Aula 05 - Monitores e Deadlock em Sistemas Operacionais](3%C2%BA%20Semestre/%5BProf.%20Guilherme%20de%20Morais%5D%20Sistemas%20Operacionais/Aulas/Aula%2005%20-%20Monitores%20e%20Deadlock%20em%20Sistemas%20Operacionais/detalhes.md)

**Laboratório de Programação III** - conteúdo das aulas:

- [Aula 01 - Configuração de Ambiente e Projeto Java Web](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Aulas/Aula%2001%20-%20Configura%C3%A7%C3%A3o%20de%20Ambiente%20e%20Projeto%20Java%20Web/detalhes.md)
- [Aula 02 - Estruturação da Interface Frontend com JSP](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Aulas/Aula%2002%20-%20Estrutura%C3%A7%C3%A3o%20da%20Interface%20Frontend%20com%20JSP/detalhes.md)
- [Aula 03 - Conexão com Banco de Dados PostgreSQL](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Aulas/Aula%2003%20-%20Conex%C3%A3o%20com%20Banco%20de%20Dados%20PostgreSQL/detalhes.md)
- [Aula 04 - Implementação do Listar Usuário com MVC](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Aulas/Aula%2004%20-%20Implementa%C3%A7%C3%A3o%20do%20Listar%20Usu%C3%A1rio%20com%20MVC/detalhes.md)
- [Aula 05 - Operação de Manutenção do Cadastro de Usuários](3%C2%BA%20Semestre/%5BProf.%20Jefferson%20Passerini%5D%20Laborat%C3%B3rio%20de%20Programa%C3%A7%C3%A3o%20III/Aulas/Aula%2005%20-%20Opera%C3%A7%C3%A3o%20de%20Manuten%C3%A7%C3%A3o%20do%20Cadastro%20de%20Usu%C3%A1rios/detalhes.md)

**Engenharia de Software I** - conteúdo das aulas:

- [Aula 01 - Processo de Abstração e Levantamento de Requisitos](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Aulas/Aula%2001%20-%20Processo%20de%20Abstra%C3%A7%C3%A3o%20e%20Levantamento%20de%20Requisitos/detalhes.md)
- [Aula 02 - Configuração e Licenciamento do Astah UML](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Aulas/Aula%2002%20-%20Configura%C3%A7%C3%A3o%20e%20Licenciamento%20do%20Astah%20UML/detalhes.md)
- [Aula 03 - Revisão de Requisitos de Software para AV1](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Aulas/Aula%2003%20-%20Revis%C3%A3o%20de%20Requisitos%20de%20Software%20para%20AV1/detalhes.md)
- [Aula 04 - Abstração e Modelagem de Requisitos](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Aulas/Aula%2004%20-%20Abstra%C3%A7%C3%A3o%20e%20Modelagem%20de%20Requisitos/detalhes.md)
- [Aula 05 - Descrição Textual de Casos de Uso](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Aulas/Aula%2005%20-%20Descri%C3%A7%C3%A3o%20Textual%20de%20Casos%20de%20Uso/detalhes.md)
- [Aula 06 - Modelo de Apresentação da Fase Análise](3%C2%BA%20Semestre/%5BProf.%20Marcelo%20Boer%5D%20Engenharia%20de%20Software%20I/Aulas/Aula%2006%20-%20Modelo%20de%20Apresenta%C3%A7%C3%A3o%20da%20Fase%20An%C3%A1lise/detalhes.md)

## Como estudar com este material

1. **Comece pelo README da disciplina.** Ele lista as aulas em ordem, os trabalhos com prazo e pontuação e os links de revisão.
2. **Estude aula por aula pelo `detalhes.md`.** A seção "Mapa da aula" dá a visão geral; depois cada tópico traz explicação, diagrama e exemplos.
3. **Pratique com o código.** Tente resolver os exercícios antes de abrir `codigo/`; depois compare com a resolução comentada.
4. **Revise com o checklist e as perguntas.** Todo `detalhes.md` termina com pontos-chave, perguntas e respostas e um checklist de revisão.
5. **Antes da prova, use o caderno e os simulados.** O `Caderno-Consolidado.md` cobre a disciplina inteira; os `Simulados-Comentados.md` treinam questões objetivas e discursivas com gabarito justificado.
6. **Confira o original quando houver dúvida.** O post do professor e os anexos ficam na mesma pasta da aula, e o `Avisos.md` guarda o mural completo.

## Anatomia de uma disciplina

```text
Nº Semestre/[Prof. Nome] Disciplina/
├── README.md                  índice: aulas, trabalhos, provas e revisão
├── Avisos.md                  mural completo do Classroom, em ordem cronológica
├── Aulas/
│   └── Aula NN - Tema/
│       ├── detalhes.md        explicação completa, diagramas, exercícios e checklist
│       ├── codigo/            exemplos e exercícios resolvidos
│       ├── links-recursos.md  links do professor e o que cada um contém
│       ├── AAAA-MM-DD - Post.md  post original do Classroom
│       └── anexos             PDFs e arquivos originais
├── Trabalhos/                 enunciado, análise, resolução proposta e código
├── Provas/                    avaliações com a mesma estrutura
└── Resumos-IA/
    ├── Caderno-Consolidado.md toda a disciplina em um documento
    └── Simulados-Comentados.md questões com gabarito comentado
```

Cada `detalhes.md` segue a mesma estrutura, o que facilita estudar qualquer disciplina do mesmo jeito:

| Seção | Conteúdo |
| :--- | :--- |
| Objetivo e contexto | O que a aula ensina e o que é preciso saber antes |
| Um bloco por tópico | Explicação, diagrama Mermaid, tabela comparativa e exemplos |
| Código da aula | Arquivos de `codigo/` explicados trecho a trecho |
| Exercícios | Enunciado, raciocínio e resolução completa |
| Erros comuns e boas práticas | Armadilhas frequentes em prova e na prática |
| Glossário e pontos-chave | Termos da aula e o essencial para a prova |
| Perguntas e respostas | Pares em JSONL, prontos para flashcards |
| Checklist de revisão | Lista para marcar o que já foi dominado |

## Como o acervo é construído

O conteúdo é gerado por um motor de sincronização próprio, o **classroom-sync**, escrito em TypeScript. Ele roda localmente, lê o Classroom com permissões somente leitura e publica o resultado neste repositório.

```mermaid
flowchart TD
  A[Google Classroom] --> B[Coleta de avisos, tarefas e materiais]
  B --> C[Anexos do Drive]
  B --> D[Links citados nos posts]
  C --> C1[PDF, Docs, Slides, DOCX, PPTX, ZIP e TAR]
  D --> D1[GitHub, Notion, Google Forms, YouTube, páginas web]
  C1 --> E[Análise por aula e trabalho]
  D1 --> E
  E --> F[detalhes.md e código resolvido]
  F --> G[Caderno consolidado e simulados]
  G --> H[README, índices e publicação no GitHub]
```

```mermaid
sequenceDiagram
  participant S as classroom-sync
  participant C as Classroom e Drive
  participant L as Fontes externas
  participant M as Modelos de linguagem
  participant G as GitHub
  S->>C: lista turmas, posts e anexos (somente leitura)
  C-->>S: posts, PDFs e documentos
  S->>L: lê repositórios, páginas e formulários
  L-->>S: código-fonte e textos completos
  S->>M: análise, documentação e código por aula
  M-->>S: detalhes.md e arquivos de código
  S->>S: valida, grava por aula e atualiza índices
  S->>G: commit e push do acervo
```

Etapas em detalhe:

1. **Coleta**: turmas ativas, avisos, tarefas e materiais pela API do Google Classroom.
2. **Extração**: Google Docs e Planilhas viram texto, apresentações viram PDF, arquivos Word e PowerPoint são convertidos, compactados são descompactados e lidos, repositórios do GitHub são clonados e páginas do Notion são lidas na íntegra.
3. **Detecção de novidades**: o que já está no acervo é reconhecido pela data, título, links e anexos; só o que é novo é processado.
4. **Documentação**: para cada aula e trabalho, uma análise estruturada identifica tema, linguagem, tópicos e exercícios; em seguida é produzido o `detalhes.md` completo e o código resolvido.
5. **Revisão**: com as aulas prontas, são gerados o caderno consolidado e os simulados da disciplina.
6. **Publicação**: índices e este README são reconstruídos a partir das pastas, e o resultado é versionado no GitHub.

## Tecnologias do motor de sincronização

| Camada | Tecnologia |
| :--- | :--- |
| Linguagem e execução | TypeScript, Node.js, tsx |
| Integração Google | Google Classroom API, Google Drive API, OAuth 2.0 com escopos somente leitura |
| Modelos de linguagem | Google Gemini (várias chaves e modelos com rodízio automático) e Claude como alternativa |
| Processamento de documentos | Files API do Gemini, textutil do macOS, extração de PPTX, ZIP e TAR |
| Fontes externas | Git (clone raso), API pública do Notion, Google Forms, oEmbed do YouTube |
| Documentação | Markdown do GitHub e diagramas Mermaid |
| Publicação | Git com commit e push automáticos |

## Princípios de qualidade

- **Fidelidade ao material**: o conteúdo parte do que o professor postou; complementos de conhecimento geral são sinalizados como tal.
- **Profundidade**: documentos de aula e trabalho têm centenas de linhas, com diagramas, tabelas, exemplos e exercícios resolvidos.
- **Código executável**: exemplos completos, comentados, sem trechos omitidos.
- **Diagramas nativos**: Mermaid renderizado pelo próprio GitHub, sem cores fixas, acompanhando o tema claro ou escuro.
- **Estrutura previsível**: todas as disciplinas seguem a mesma organização de pastas e seções.
- **Rastreabilidade**: post original, anexos e links do professor ficam ao lado da documentação.
- **Resiliência**: falhas de rede ou de cota não perdem trabalho; cada aula é gravada ao ficar pronta e o processo retoma de onde parou.

## Aviso acadêmico

Este acervo é material de apoio ao estudo e não substitui as aulas, a bibliografia oficial nem a orientação dos professores. Os materiais originais (slides, PDFs e enunciados) pertencem aos respectivos docentes e à UniFEF e estão aqui apenas como referência de estudo. As resoluções propostas servem para aprendizado e conferência; a produção dos trabalhos avaliativos deve respeitar as regras de integridade acadêmica da instituição.

## Autor e licença

- **Autor:** Murilo Rocha Silva, Bacharelado em Sistemas de Informação, UniFEF.
- **Licença:** veja [LICENSE](LICENSE).
