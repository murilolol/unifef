# Aula — Conceitos de Processos

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Tema:** Definição de processo, espaço de endereço e estados básicos de execução

## Objetivo da aula

Compreender o que caracteriza um processo em Sistemas Operacionais, distinguir processo de programa, identificar as regiões que compõem o espaço de endereço de um processo e conhecer os três estados fundamentais pelos quais um processo transita durante seu ciclo de vida.

## Introdução

Muitos sistemas de natureza diversa têm a capacidade de realizar várias ações no mesmo intervalo de tempo. Por exemplo, o corpo humano realiza várias operações em paralelo, ou **concorrentemente**. Os computadores também executam tarefas concorrentemente: enviar um arquivo para a impressora, exibir uma página web e enviar/receber mensagens de correio eletrônico, tudo "ao mesmo tempo" do ponto de vista do usuário.

## Definição de processo

O termo **processo** foi usado pela primeira vez pelos projetistas do sistema Multics, na década de 1960. Desde então, o termo ganhou diversas definições, entre elas:

- Um programa em execução;
- Uma atividade assíncrona;
- O "espírito animado" de um procedimento.

Duas dessas definições concentram os conceitos fundamentais estudados nesta aula.

### Primeiro conceito — o processo como entidade

Um **processo** é uma entidade. Cada processo possui seu próprio espaço de endereço, que normalmente consiste em três regiões:

| Região | Conteúdo |
| :--- | :--- |
| **Região de Texto** | Armazena o código que o processador executa. |
| **Região de Dados** | Armazena variáveis e memória alocada dinamicamente, usadas pelo processo durante a execução. |
| **Região de Pilha** | Armazena instruções e variáveis locais das chamadas ativas a procedimentos. Cresce à medida que o processo emite chamadas aninhadas a procedimentos, e diminui quando o procedimento chamado retorna. |

### Segundo conceito — processo × programa

Todo processo é um programa em execução. Um **programa** é uma entidade inanimada — apenas um conjunto de instruções armazenado; somente quando um processador "sopra vida" a esse programa é que ele se torna a entidade ativa chamada de **processo**. Isto é: o mesmo programa pode dar origem a múltiplos processos (por exemplo, várias instâncias abertas de um editor de texto), cada um com seu próprio espaço de endereço e estado de execução independentes.

## Estados de um processo: ciclo de vida

O sistema operacional deve assegurar que cada processo receba uma quantidade suficiente de tempo de processador. Em qualquer sistema real, o número de processos verdadeiramente em execução simultânea é, no máximo, igual ao número de processadores disponíveis — mas há, em geral, muito mais processos do que processadores. Logo, a qualquer instante, alguns processos são executados e outros não.

Durante seu tempo de vida, um processo passa por vários estados; para que um processo mude de estado, é necessário que ocorra um **evento**. Embora existam outros estados possíveis em modelos mais completos, esta aula concentra-se nos três estados mais importantes:

| Estado | Descrição |
| :--- | :--- |
| **Em execução** | O processo está sendo processado pela UCP naquele instante. |
| **Pronto** | O processo poderia estar em execução se houvesse um processador disponível — está apenas aguardando sua vez. |
| **Bloqueado** | O processo está esperando que algum evento aconteça (tipicamente uma operação de E/S). |

Em um sistema **uniprocessado**, apenas um processo pode estar em execução por vez, mas vários outros podem estar simultaneamente prontos ou bloqueados:

- Os processos **prontos** são organizados por ordem de prioridade: o processo seguinte a receber o processador é sempre o primeiro da lista (o de maior prioridade).
- A lista de processos **bloqueados**, em geral, é desordenada — os processos não se desbloqueiam em ordem de chegada à fila, mas sim na ordem em que ocorrem os eventos externos pelos quais estão esperando.

---

## Exercícios de fixação

1. (V/F) Os termos "processo" e "programa" são sinônimos?
2. Por que o espaço de endereço de um processo é dividido em várias regiões?
3. Quantos processos podem estar verdadeiramente em execução ao mesmo tempo em um sistema uniprocessado?
4. Um processo entra no estado de bloqueado quando está esperando que um evento ocorra. Cite diversos eventos que podem levar um processo a esse estado.
5. Qual a diferença entre a ordenação da lista de processos prontos e da lista de processos bloqueados?

<details>
<summary>Gabarito</summary>

1. **Falso.** Um processo é um programa em execução; um programa, isoladamente, é uma entidade inanimada (apenas código armazenado).
2. Porque processos leem e escrevem dados em ordens diversas, e separar o espaço de endereço em regiões (texto, dados, pilha) permite ao sistema operacional impor regras de acesso distintas a cada uma — por exemplo, proteger a região de texto (código) contra escrita acidental, enquanto a pilha e os dados permanecem graváveis.
3. Apenas um processo por vez em um sistema uniprocessado — isto é, um processo por processador disponível.
4. Requisição de dados em um dispositivo de latência alta (como leitura em disco rígido); requisição de um recurso alocado a outro processo e indisponível no momento (por exemplo, uma impressora); espera por entrada do usuário, como uso do teclado ou movimentação do mouse.
5. A lista de prontos é ordenada por prioridade — o primeiro da fila é sempre o próximo a ser despachado. A lista de bloqueados é tipicamente desordenada, pois os processos são desbloqueados conforme a ordem de conclusão dos eventos externos pelos quais aguardam, não por posição na fila.

</details>

## Material relacionado

- Diagramas desta aula: [Regiões do espaço de endereço de um processo](diagramas/processo-regioes-classes.svg) (diagrama de classes) · [Ciclo básico de três estados](diagramas/ciclo-tres-estados-atividades.svg) (diagrama de atividades)
- [Aula: Blocos de Controle (PCB)](../BLOCOS%20DE%20CONTROLE/detalhes.md)
- [Aula: Aula 3 — Gerenciamento de Processo](../AULA3%20-GERENCIAMENTO_DE_PROCESSO/detalhes.md) — aprofunda as transições entre estes três estados.
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md)
- Slide original da aula: [`AULA2-Sistemas_Operacionais.pptx`](./AULA2-Sistemas_Operacionais.pptx)
