# Trabalho — Atividade Avaliativa Prática 02: Atores e Diagrama de Classes (Fase de Análise)

> **Professor:** Marcelo Tadeu Boer
> **Disciplina:** Engenharia de Software I (3º Semestre)
> **Prazo de entrega:** 11/03/2026 às 23:59
> **Pontuação máxima:** 2 pontos

## Descrição da atividade

"O aluno deve, no final do arquivo entregue na Atividade 01, adicionar os itens referentes aos Atores do Aplicativo e ao Diagrama de Classes." Trata-se da continuação direta do [Estudo de Caso da Açaiteria](../ATIVIDADE%20AVALIATIVA%2001%20-%20ESTUDO%20DE%20CASO/detalhes.md): sobre o mesmo contexto de negócio, o aluno deve complementar a resposta anterior com a identificação formal dos atores e a construção do Diagrama de Classes da fase de análise.

## Resolução

A resolução em [`resolucao_atividade_02_engenharia_software.md`](./resolucao_atividade_02_engenharia_software.md) adota como contexto o **Sistema de Gestão de Pedidos e Entregas (DeliveryFlow)** e traz:

1. **Tabela de Atores** — Cliente, Restaurante/Estabelecimento, Entregador, Administrador do Sistema e Gateway de Pagamento (externo), cada um com tipo e descrição de responsabilidades.
2. **Diagrama de Classes (Fase de Análise)** em UML (Mermaid), modelando `Usuario` (superclasse), `Cliente`, `Restaurante`, `Entregador`, `Administrador`, `ItemCardapio`, `Pedido`, `ItemPedido`, `Pagamento` e `Avaliacao`, com atributos, métodos e multiplicidades de relacionamento.
3. **Dicionário de classes** explicando o papel de cada entidade do domínio.

## Material relacionado

- [Aula: Casos de Uso, Atores e Ferramenta Astah UML](../../Aulas/Casos%20de%20Uso%2C%20Atores%20e%20Ferramenta%20Astah%20UML/detalhes.md) — teoria de atores e estrutura de DCU aplicada ao Desapega Já.
- [Aula: Contexto do Aplicativo e Engenharia de Requisitos](../../Aulas/Contexto%20do%20Aplicativo%20e%20Engenharia%20de%20Requisitos/detalhes.md) — Diagrama de Classes de referência do Desapega Já.
- [Trabalho: Atividade Avaliativa 01 — Estudo de Caso](../ATIVIDADE%20AVALIATIVA%2001%20-%20ESTUDO%20DE%20CASO/detalhes.md) — atividade base que esta complementa.
- [Resumo executivo, exercícios, simulado e cheatsheet da disciplina](../../README.md#resumo-executivo)
