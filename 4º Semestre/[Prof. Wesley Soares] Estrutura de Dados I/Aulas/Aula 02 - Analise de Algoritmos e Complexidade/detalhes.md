# Aula 02 — Análise de Algoritmos e Complexidade (Notação Big-O)

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Data de postagem:** 11/08/2026
> **Tema:** Análise assintótica, custo computacional e Notação Big-O

## Objetivo da aula

- Entender por que algoritmos precisam ser analisados;
- Diferenciar tempo de execução e quantidade de operações;
- Relacionar o tamanho da entrada ao custo do algoritmo;
- Identificar melhor caso, pior caso e caso médio;
- Reconhecer as principais classes de complexidade;
- Interpretar e utilizar a Notação O;
- Comparar algoritmos que resolvem o mesmo problema.

## Por que analisar algoritmos?

"Dois algoritmos podem resolver o mesmo problema, mas não necessariamente com o mesmo custo." Imagine dois algoritmos para pesquisar um elemento em uma lista: o Algoritmo A verifica os elementos um por um; o Algoritmo B consegue eliminar metade dos elementos a cada comparação. Para uma lista pequena a diferença é imperceptível — mas para 1.000.000 de elementos, a diferença pode ser enorme.

Em vez de perguntar "quanto tempo o programa levou?" (resposta que depende de processador, memória, linguagem, compilador, sistema operacional, implementação e dados de entrada), perguntamos: **quantas operações o algoritmo precisa realizar à medida que a entrada cresce?**

## Custo computacional

O custo computacional representa os recursos necessários para executar um algoritmo:

- **Tempo:** quantidade de operações necessárias;
- **Espaço:** quantidade de memória utilizada.

Chamamos de **tamanho da entrada** (representado por `n`) a quantidade de dados que o algoritmo precisa processar.

### Contando operações

```
para i = 1 até n
    escreva(i)
fim
```

`escreva(i)` executa `n` vezes → `T(n) = n` → o crescimento é **linear**.

Com dois laços independentes (não aninhados):

```
para i = 1 até n
    escreva(i)
fim
para j = 1 até n
    escreva(j)
fim
```

`T(n) = n + n = 2n`. Para a análise de crescimento, porém, não nos preocupamos com a constante `2` — consideramos apenas `O(n)`.

## Análise assintótica

Na análise assintótica, o interesse principal é como o custo cresce quando `n` aumenta — não estamos interessados em processador específico, tempo exato em segundos ou pequenas diferenças constantes, mas sim no **comportamento de crescimento**.

Para `T(n) = 3n + 10`: em entradas muito grandes o termo dominante é `n`, logo a complexidade é **O(n)**.

## Melhor caso, pior caso e caso médio

Considere uma pesquisa sequencial em `[10, 25, 31, 42, 58, 70, 81]`:

| Caso | Custo |
| :--- | :--- |
| Melhor | menor quantidade de operações (ex.: buscar `10`, 1 comparação) |
| Médio | comportamento esperado (depende da distribuição das posições) |
| Pior | maior quantidade de operações (ex.: buscar `81` ou um valor ausente, `n` comparações) |

## Notação Big-O — O(n)

**Big-O** indica uma ordem de crescimento que o algoritmo não ultrapassa assintoticamente, ignorando constantes e termos de menor ordem. `T(n) = 5n`, `T(n) = 3n + 20` e `T(n) = 100n + 50` são todos **O(n)** — complexidade linear.

### Principais classes de complexidade (da mais para a menos eficiente)

| Notação | Tipo |
| :--- | :--- |
| O(1) | Constante |
| O(log n) | Logarítmica |
| O(n) | Linear |
| O(n log n) | Linear-logarítmica |
| O(n²) | Quadrática |
| O(n³) | Cúbica |
| O(2ⁿ) | Exponencial |
| O(n!) | Fatorial |

### O(1) — Complexidade constante

```
x = vetor[5]
```

Independentemente do tamanho do vetor, acessamos diretamente uma posição — o custo permanece aproximadamente igual quando `n` aumenta. Exemplos: acessar uma posição de um vetor; consultar uma variável; inserir/remover em uma posição conhecida de determinadas estruturas.

### O(log n) — Complexidade logarítmica

Imagine procurar um nome em uma lista ordenada, verificando o elemento do meio e descartando metade do espaço de busca a cada passo (1.000.000 de elementos → verifica o meio → verifica metade → ...). A cada passo, o problema é reduzido aproximadamente à metade.

### O(n) — Complexidade linear

```
para cada elemento
    verificar elemento
fim
```

10 elementos → aproximadamente 10 operações; 100 elementos → aproximadamente 100 operações. Exemplo típico: pesquisa sequencial.

### O(n²) — Complexidade quadrática

```
para i = 1 até n
    para j = 1 até n
        operação
    fim
fim
```

`n × n = n²`. Alguns algoritmos simples de ordenação, como *Bubble Sort* e *Selection Sort*, possuem comportamento quadrático em seus casos típicos/pior caso. Se o segundo laço rodasse apenas metade das vezes, o custo seria `n²/2` — mas, descartando a constante `1/2`, a complexidade continua sendo **O(n²)**.

### Comparando o crescimento

| n | O(1) | O(log n) | O(n) | O(n²) |
| :--- | :--- | :--- | :--- | :--- |
| 10 | 1 | ~3 | 10 | 100 |
| 100 | 1 | ~7 | 100 | 10.000 |
| 1.000 | 1 | ~10 | 1.000 | 1.000.000 |
| 10.000 | 1 | ~13 | 10.000 | 100.000.000 |

A diferença entre algoritmos torna-se cada vez mais importante conforme o tamanho da entrada aumenta.

## Fechamento

Se dois algoritmos resolvem o mesmo problema, por que escolher um em vez do outro? Um **algoritmo correto** resolve o problema; um **algoritmo eficiente** resolve o problema usando os recursos de forma adequada.

---

## Exercícios de revisão (slides originais)

Os slides de revisão da disciplina (`Aula 05 - Exercicios de Revisao.pdf`, anexado nesta pasta) trazem quatro exercícios de fixação sobre complexidade, resolvidos abaixo com a justificativa.

**1.**
```java
int[] dados = new int[n];
for (int i = 0; i < n; i++) {
    dados[i] = i;
}
```
Um único laço percorre `n` posições → **O(n)**.

**2.**
```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        System.out.println(dados[i] + dados[j]);
    }
}
```
Dois laços aninhados, cada um de `0` a `n` → `n × n` → **O(n²)**.

**3.**
```java
for (int i = 0; i < lista.tamanho(); i++) {
    System.out.println(lista.obter(i));
}
```
Um laço de `n` iterações chamando `lista.obter(i)`, um acesso por posição em **O(1)**. Custo total: `n × O(1)` → **O(n)**.

**4. Atenção — armadilha comum:**
```java
for (int i = 0; i < n; i++) {
    lista.buscar(i);
}
```
Aqui `lista.buscar(valor)` percorre a lista internamente (pesquisa sequencial, **O(n)**). Chamar essa busca dentro de um laço externo de `n` iterações resulta em `n × n` → **O(n²)**, mesmo que o código pareça "só um laço" à primeira vista — é preciso olhar para dentro do método chamado.

## Exercício de fixação adicional

1. Classifique a complexidade de: `for (int i = 0; i < n; i += 2) { ... }` (uma única instrução simples dentro do laço).
2. Um algoritmo tem `T(n) = 2n² + 3n + 7`. Qual sua Notação Big-O?
3. Por que a busca binária é O(log n) e não O(n)?

<details>
<summary>Gabarito</summary>

1. O laço ainda cresce proporcionalmente a `n` (incrementar de 2 em 2 apenas reduz a constante pela metade, `n/2`) → **O(n)**.
2. Descartando constantes e termos de menor ordem, resta o termo dominante `n²` → **O(n²)**.
3. Porque a cada comparação o espaço de busca é dividido ao meio, eliminando metade dos elementos restantes — o número de divisões necessárias para reduzir `n` elementos a 1 cresce logaritmicamente, não linearmente.

</details>

## Material relacionado

- [Diagrama: Processo de Análise Assintótica de um Algoritmo](diagramas/atividades-analise-assintotica.svg)
- [Aula 01: Introdução e Projeto de Algoritmos](../Aula%2001%20-%20Introducao%20e%20Projeto%20de%20Algoritmos/detalhes.md)
- [Aula 03: Memória e Alocação Dinâmica](../Aula%2003%20-%20Memoria%20e%20Alocacao%20Dinamica/detalhes.md)
- [Trabalho: Complexidade de Algoritmos](../../Trabalhos/Complexidade%20de%20algoritmos/detalhes.md)
- Slides originais: [`Aula 02.pdf`](./Aula%2002.pdf), [`Aula 05 - Exercicios de Revisao.pdf`](./Aula%2005%20-%20Exercicios%20de%20Revisao.pdf)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
