# Trabalho — Atividade Aula 3

> **Professor:** Wesley Soares
> **Disciplina:** Engenharia de Software II (4º Semestre)
> **Prazo de Entrega:** 19/08/2026 às 02:59
> **Pontuação Máxima:** 100 pontos
> **Conteúdo cobrado:** identificação de stakeholders/analistas, situação-problema e solução proposta (fases 1 e 2 do ciclo de vida do projeto — ver [Aula 01](../../Aulas/01%20Ciclo%20de%20Vida%20do%20Projeto%20de%20Software/detalhes.md))

## Enunciado original (Google Classroom)

Coloque em anexo um arquivo com as respostas contendo:

1. Integrantes do grupo com seus respectivos papéis de **Stakeholder** e de **Analista**.
2. Descrição clara da **situação-problema**.
3. Descrição detalhada da **solução proposta**.

## Resolução entregue

A equipe optou por um sistema de **Matrícula Online e Rematrícula (SisMat)**, aplicando os papéis híbridos de Stakeholder (dor de negócio) e Analista (responsabilidade técnica) discutidos na [Aula 01](../../Aulas/01%20Ciclo%20de%20Vida%20do%20Projeto%20de%20Software/detalhes.md):

| Integrante | Papel de Stakeholder | Papel de Analista |
| :--- | :--- | :--- |
| Ana Souza | Gerente de Operações Acadêmicas | Analista de Requisitos e Processos |
| Carlos Silva | Coordenador de Infraestrutura de TI | Arquiteto de Software e DevOps |
| Mariana Costa | Representante do Corpo Discente | Analista de UX/UI e Frontend |
| João Pedro | Administrador de Banco de Dados (DBA) | Engenheiro de Dados e Segurança da Informação |

**Situação-problema:** o sistema legado de matrícula, de arquitetura monolítica e síncrona, apresenta quedas de performance, *timeouts* e inconsistência de dados no pico de acessos simultâneos do primeiro dia de abertura dos prazos — causas identificadas: concorrência descontrolada no banco de dados, falta de feedback em tempo real sobre vagas disponíveis, validação síncrona e bloqueante de pré-requisitos, e sobrecarga da central de atendimento.

**Solução proposta:** reengenharia do módulo de matrícula com **Arquitetura Orientada a Eventos (EDA)** e **microsserviços**, desacoplando a solicitação da efetivação da matrícula por meio de API Gateway com *rate limiting*, microsserviço de validação com cache em Redis, fila de mensagens (Apache Kafka) e *worker* de processamento assíncrono, com notificação ao aluno via WebSocket.

## Arquivos entregues

- [`relatorio_atividade_aula3.md`](./relatorio_atividade_aula3.md) — versão inicial do relatório, com integrantes, papéis e descrição da situação-problema.
- [`relatorio_final_atividade_aula3.md`](./relatorio_final_atividade_aula3.md) — relatório completo e final, incluindo a solução proposta detalhada, os diagramas de arquitetura e de sequência do fluxo de matrícula assíncrona, e a implementação de referência.

## Material relacionado

- [Aula 01 — Ciclo de Vida do Projeto de Software](../../Aulas/01%20Ciclo%20de%20Vida%20do%20Projeto%20de%20Software/detalhes.md) (fases de Identificação do Problema e Levantamento de Requisitos)
- [Resumo executivo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
