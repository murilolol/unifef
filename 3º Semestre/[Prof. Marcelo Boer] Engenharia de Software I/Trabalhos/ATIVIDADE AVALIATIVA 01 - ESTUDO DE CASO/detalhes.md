# Trabalho — Atividade Avaliativa 01: Estudo de Caso (Açaiteria)

> **Professor:** Marcelo Tadeu Boer
> **Disciplina:** Engenharia de Software I (3º Semestre)
> **Prazo de entrega:** 04/03/2026 às 02:59
> **Pontuação máxima:** 2 pontos
> **Instrução do professor:** responder no próprio arquivo `.docx` da atividade, com o nome do aluno.

## Enunciado — Estudo de Caso: Aplicativo da Açaiteria

A Açaíteria Sabor da Amazônia é um pequeno comércio especializado na venda de açaí na tigela, smoothies e vitaminas, oferecendo também complementos como frutas, granola e leite condensado. Atualmente, os pedidos são feitos apenas de forma presencial ou por WhatsApp. Em horários de maior movimento, o estabelecimento enfrenta problemas como demora no atendimento, erros na montagem dos pedidos e dificuldade no controle de estoque. Além disso, a gestão não possui relatórios organizados de vendas e não consegue controlar adequadamente o programa de fidelidade dos clientes.

Para melhorar o atendimento e organizar seus processos internos, a empresa decidiu desenvolver um aplicativo móvel próprio. No aplicativo, cada cliente deverá realizar um cadastro informando seus dados básicos, como nome e telefone, passando a possuir um identificador no sistema e um controle de pontos acumulados no programa de fidelidade.

O aplicativo permitirá que os usuários visualizem os produtos disponíveis, cada um contendo informações como nome, descrição e preço. Ao selecionar um item, o cliente poderá adicioná-lo ao pedido, que será registrado com um número identificador, data e horário da solicitação, status de acompanhamento (como aguardando, em preparo ou finalizado) e o valor total calculado automaticamente.

Após a finalização do pedido, o sistema deverá registrar o pagamento, armazenando a forma escolhida pelo cliente (como Pix ou cartão) e a situação da transação, indicando se foi aprovado ou não.

Além das funcionalidades voltadas ao cliente, o sistema também deverá permitir que o gerente acompanhe os pedidos realizados, controle as vendas e organize melhor a administração do negócio.

**Identifique, com base nesse contexto:**

1. O(s) usuário(s) do aplicativo.
2. As possíveis classes do projeto com seus atributos.
3. A lista de requisitos funcionais e não funcionais.
4. Os atores do aplicativo.
5. O Diagrama de Classes.

## Resolução

A resolução completa em [`resolucao_estudo_de_caso_acaiteria.md`](./resolucao_estudo_de_caso_acaiteria.md) responde aos cinco itens do enunciado, identificando os usuários (Cliente, Gerente/Administrador e Atendente/Operador de Cozinha), as classes de domínio com atributos tipados, os requisitos funcionais e não-funcionais, os atores e o Diagrama de Classes completo em UML.

## Arquivos entregues

- [`Estudo de Caso – Aplicativo da Açaiteria.docx`](./Estudo%20de%20Caso%20%E2%80%93%20Aplicativo%20da%20A%C3%A7aiteria.docx) — enunciado original do professor.
- [`resolucao_estudo_de_caso_acaiteria.md`](./resolucao_estudo_de_caso_acaiteria.md) — resolução completa dos cinco itens.

## Material relacionado

- [Aula: Contexto do Aplicativo e Engenharia de Requisitos](../../Aulas/Contexto%20do%20Aplicativo%20e%20Engenharia%20de%20Requisitos/detalhes.md) — mesma técnica de identificação de classes/RF/RNF aplicada ao Desapega Já.
- [Trabalho: Atividade Avaliativa Prática 02 — Atores e Diagrama de Classes Fase de Análise](../Atividade%20Avaliativa%20Pr%C3%A1tica%2002%20-%20Atores%20e%20Diagrama%20de%20Classes%20Fase%20de%20An%C3%A1lise/detalhes.md) — continuação desta atividade.
- [Resumo executivo, exercícios, simulado e cheatsheet da disciplina](../../README.md#resumo-executivo)
