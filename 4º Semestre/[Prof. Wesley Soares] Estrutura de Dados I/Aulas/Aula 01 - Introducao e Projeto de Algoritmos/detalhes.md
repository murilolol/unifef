# Aula 01 — Introdução à Estrutura de Dados e Projeto de Algoritmos

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Data de postagem:** 06/08/2026
> **Tema:** Fundamentos de algoritmos, tipos abstratos de dados e refinamento sucessivo

## Objetivo da aula

Apresentar a disciplina (ementa, conteúdo programático e critérios de avaliação) e estabelecer a base conceitual sobre a qual toda a matéria se apoia: o que é um algoritmo, o que é uma estrutura de dados e como transformar um problema real em uma solução implementável, passando pelo conceito de Tipo Abstrato de Dados (TAD).

## Ementa da disciplina

- Fundamentos de projeto e análise de algoritmos, tipos abstratos de dados, complexidade computacional, organização da memória e alocação dinâmica.
- Estudo, implementação e análise de estruturas de dados lineares, incluindo listas sequenciais e ligadas, listas circulares, pilhas, filas, deques e estruturas compostas, com aplicação de técnicas de desenvolvimento e avaliação de algoritmos.

### Conteúdo programático

1. Introdução à Estrutura de Dados e Projeto de Algoritmos
2. Análise de Algoritmos
3. Memória e Alocação Dinâmica
4. Listas Lineares Sequenciais
5. Listas Ligadas
6. Pilhas
7. Filas e Deques Dinâmicos
8. Estruturas Compostas
9. Projeto e Aplicação de Estruturas Lineares

### Critérios de avaliação

A média final é calculada por:

```
[(AV1 × 0.8) + (T1 × 0.2)] + [(AV2 × 0.8) + (T2 × 0.2)]
─────────────────────────────────────────────────────── 
                         2
```

- **Avaliação 1 (AV1):** trabalho valendo 10 pontos com peso 2, e uma prova valendo 10 pontos com peso 8.
- **Avaliação 2 (AV2):** trabalho valendo 10 pontos com peso 2, e uma prova valendo 10 pontos com peso 8.

### Bibliografia

- MONTEIRO, Mario A. *Introdução à Organização de Computadores*. 4. ed. Rio de Janeiro: LTC, 2002.
- STALLINGS, William. *Arquitetura e Organização de Computadores*. 8. ed. São Paulo: Pearson, 2010.
- TANENBAUM, Andrew S. *Organização Estruturada de Computadores*. 3. ed. São Paulo: Prentice-Hall do Brasil, 1990.
- IDOETA, Ivan Valeije; CAPUANO, Francisco Gabriel. *Elementos de Eletrônica Digital*. 32. ed. São Paulo: Érica, 2001.
- PATTERSON, David A. *Organização e Projeto de Computadores*. 2. ed. Rio de Janeiro: LTC, 1998.
- WEBER, Raul Fernando. *Arquitetura de Computadores Pessoais*. 2. ed. Porto Alegre: Sagra, 2001.
- WEBER, Raul. *Fundamentos de Arquitetura de Computadores* — Série Livros Didáticos Informática UFRGS vol. 8. 4. ed. Bookman, 2012.

---

## Introdução à Estrutura de Dados e Projeto de Algoritmos

Um bom programa depende tanto de um bom algoritmo quanto da forma como os dados são organizados. A aula cobre cinco perguntas centrais: o que é um algoritmo, o que são dados, o que é uma estrutura de dados, como projetar uma solução e o que são tipos abstratos de dados.

### O que é um algoritmo?

Um **algoritmo** é uma sequência finita e ordenada de passos para resolver um problema. Suas características:

- Possui uma entrada;
- Executa uma sequência de passos;
- Produz uma saída;
- Deve ser finito;
- Deve ser preciso;
- Deve ser executável.

### Algoritmo não é programa

| Algoritmo | Programa |
| :--- | :--- |
| Solução conceitual | Implementação |
| Independente de linguagem | Depende de uma linguagem |
| Descreve a lógica | Executa a lógica |
| Pode ser representado por pseudocódigo | É compilado ou interpretado |

### Do problema à solução

O caminho do enunciado de um problema até o software funcionando segue um fluxo em cascata:

```
Problema → Compreensão → Modelagem → Algoritmo → Estrutura de Dados → Implementação → Solução
```

**Exemplo — controlar uma fila de atendimento.** Antes de programar, é preciso responder:

- Quais dados precisam ser armazenados?
- Em que ordem as pessoas serão atendidas?
- Quais operações serão realizadas?
- Como representar essa informação?

### Projeto de algoritmos

O projeto de algoritmos é o processo de transformar um problema em uma solução sistemática, seguindo as etapas: entender o problema → identificar entradas e saídas → decompor o problema → definir a estratégia de solução → especificar o algoritmo → testar e refinar.

**Exemplo — encontrar o maior número de uma lista.** Perguntas de projeto: qual é a entrada? qual é a saída? precisamos percorrer todos os elementos? como armazenar o maior valor encontrado?

### Refinamento sucessivo

Técnica que começa com uma solução geral e a detalha progressivamente até que possa ser implementada — de um problema complexo para subproblemas menores, até passos simples que formam um algoritmo implementável.

**Exemplo — calcular a média de três notas.**

- **Nível 1 (abstração):** calcular a média das notas.
- **Nível 2 (refinamento):**
  1. Obter as três notas;
  2. Somar as notas;
  3. Dividir a soma por três;
  4. Exibir a média.
- **Nível 3 (mais detalhado, pseudocódigo executável):**

```
1. Ler nota1
2. Ler nota2
3. Ler nota3
4. soma ← nota1 + nota2 + nota3
5. media ← soma / 3
6. Exibir media
```

### Tipos Abstratos de Dados (TAD)

Um **Tipo Abstrato de Dados** define:

- Quais dados existem;
- Quais operações podem ser realizadas;
- Qual o comportamento esperado.

Sem definir necessariamente:

- Como os dados serão armazenados;
- Como as operações serão implementadas.

Esse é o princípio de **abstração e encapsulamento**: a *interface* descreve **o que** a estrutura faz (ex.: uma Pilha expõe `empilhar()`, `desempilhar()`, `topo()`), enquanto a *implementação* descreve **como** ela faz — podendo ser trocada (vetor ou lista ligada) sem alterar o comportamento externo observado pelo usuário da estrutura.

---

## Exercício de fixação

1. Explique, com suas palavras, a diferença entre algoritmo e programa.
2. Aplique o refinamento sucessivo (3 níveis) ao problema: "calcular a área de um retângulo a partir da base e da altura informadas pelo usuário".
3. Um TAD "Fila" expõe as operações `enfileirar(dado)`, `desenfileirar()` e `estaVazia()`. Isso é suficiente para o usuário da fila? O usuário precisa saber se a fila foi implementada com array ou com lista ligada para utilizá-la corretamente?
4. Cite as seis características que um algoritmo deve possuir.

<details>
<summary>Gabarito</summary>

1. O algoritmo é a solução conceitual, independente de linguagem, que descreve a lógica; o programa é a implementação executável dessa lógica em uma linguagem específica (compilada ou interpretada).
2. Nível 1: "Calcular a área do retângulo". Nível 2: (1) obter base; (2) obter altura; (3) calcular área; (4) exibir área. Nível 3: `1. Ler base` / `2. Ler altura` / `3. area ← base × altura` / `4. Exibir area`.
3. Sim, é suficiente — esse é justamente o princípio de TAD: o usuário só precisa conhecer a interface (o que a fila faz). Não é necessário conhecer a implementação interna (como ela faz) para usar a estrutura corretamente.
4. Possuir uma entrada; executar uma sequência de passos; produzir uma saída; ser finito; ser preciso; ser executável.

</details>

## Material relacionado

- [Diagrama: Refinamento Sucessivo — Do Problema à Solução](diagramas/atividades-refinamento-sucessivo.svg)
- [Aula 02: Análise de Algoritmos e Complexidade](../Aula%2002%20-%20Analise%20de%20Algoritmos%20e%20Complexidade/detalhes.md)
- Slides originais: [`Aula 01.pdf`](./Aula%2001.pdf)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
