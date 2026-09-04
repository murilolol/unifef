# Aula 03 — Memória e Alocação Dinâmica

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Tema:** Stack, Heap, referências, alocação dinâmica e Garbage Collector

## Objetivo da aula

Em Estrutura de Dados não basta saber "como armazenar um dado" — também é preciso entender "como esse dado é armazenado na memória". Esse conhecimento influencia diretamente desempenho, consumo de memória, acesso aos dados, criação de estruturas, inserção/remoção e escalabilidade.

## Memória RAM

A memória RAM pode ser entendida, de forma simplificada, como uma grande sequência de posições capazes de armazenar informações. Cada posição possui um **endereço** e um **conteúdo**. Por exemplo, `int idade = 30;` faz com que o valor `30` seja gravado em uma posição de memória com um endereço próprio (ex.: `0x1000`).

## Tipos primitivos x referências

```java
int idade = 30;
double salario = 5000.0;
boolean ativo = true;

Pessoa pessoa;
String nome;
int[] numeros;
```

- **Tipos primitivos** (`int`, `double`, `boolean`, ...) armazenam o valor diretamente.
- **Tipos por referência** (objetos, `String`, arrays) armazenam um *ponteiro* para o local, na memória, onde o objeto de fato reside.

## Stack e Heap

| Stack | Heap |
| :--- | :--- |
| Chamadas de métodos | Objetos |
| Variáveis locais | Arrays |
| Referências | Estruturas criadas dinamicamente (via `new`) |

`Pessoa pessoa = new Pessoa();` declara uma variável de referência `pessoa` na Stack; `new Pessoa()` cria um novo objeto `Pessoa` na Heap durante a execução do programa, e a Stack passa a guardar apenas o **endereço** desse objeto.

### Duas referências para o mesmo objeto

```java
Pessoa p1 = new Pessoa();
Pessoa p2 = p1;
p2.setNome("Carlos");
```

`p1` e `p2` apontam para o **mesmo objeto** na Heap. Alterar o objeto através de `p2` também é visível através de `p1` — logo, `p1.getNome()` retorna `"Carlos"`.

### Referências nulas

```java
Pessoa pessoa = null;
pessoa.getNome(); // NullPointerException
```

`pessoa = null` significa que não existe objeto associado à referência; tentar acessar um membro dessa referência lança `NullPointerException`.

### Criando objetos dinamicamente

```java
Pessoa[] pessoas = new Pessoa[3]; // array criado no Heap, com 3 posições null
pessoas[0] = new Pessoa();
pessoas[1] = new Pessoa();
```

**Alocação dinâmica** é o ato de criar estruturas durante a execução do programa, conforme a necessidade, em vez de ter tudo definido em tempo de compilação.

## Garbage Collector

O *Garbage Collector* (GC) identifica objetos que não podem mais ser alcançados pelo programa (nenhuma variável referencia mais aquele endereço) e libera a memória associada a eles automaticamente. Diferente da linguagem C, onde a limpeza de memória é manual (`malloc` e `free`), em Java:

```java
Pessoa p = new Pessoa();
p = null; // o objeto perde sua única referência
```

`p = null` não apaga o objeto imediatamente — apenas o coloca na mira do Garbage Collector, que o removerá quando identificar que nenhuma referência ativa aponta mais para ele.

**Pergunta de fixação (do slide):** em

```java
Pessoa p1 = new Pessoa();
Pessoa p2 = new Pessoa();
p1.setNome("Ana");
p2.setNome("Carlos");
p1 = null;
```

foram criados 2 objetos `Pessoa`; o primeiro objeto (`"Ana"`) fica sem nenhuma referência ativa após `p1 = null` e torna-se elegível para coleta; o segundo objeto (`"Carlos"`) continua acessível através de `p2`.

## Por que isso importa em Estrutura de Dados?

```java
class No {
    int valor;
    No proximo;
}

No primeiro = new No();
No segundo = new No();

primeiro.proximo = segundo;
```

Ao dar a um objeto um atributo que referencia outro objeto do mesmo tipo, criamos um **encadeamento**: `primeiro` aponta para `segundo`, formando o início de uma **lista ligada**. É exatamente o mecanismo de referências entre objetos na Heap que viabiliza estruturas dinâmicas como listas ligadas, pilhas dinâmicas, filas dinâmicas e árvores.

```
Memória → Variáveis → Referências → Objetos → Nós → Listas ligadas
                                                   → Pilhas dinâmicas
                                                   → Filas dinâmicas
                                                   → Árvores
```

---

## Exercício de fixação

Considere o seguinte código:

```java
No n1 = new No(10);
No n2 = new No(20);
No n3 = new No(30);

n1.proximo = n2;
n2.proximo = n3;

n2 = null;
```

1. Quantos objetos `No` foram criados?
2. Quantas referências `No` existem na variável local (Stack) logo após a última linha?
3. Qual é o valor de `n1.proximo.valor`?
4. Qual é o valor de `n1.proximo.proximo.valor`?
5. O que acontece com o objeto que continha o valor `20` depois de `n2 = null`? Ele é imediatamente destruído?

<details>
<summary>Gabarito</summary>

1. Três objetos (`n1`, `n2` e `n3` apontavam, cada um, para um objeto recém-criado).
2. Duas: `n1` e `n3` (a variável `n2` ainda existe, mas seu valor foi trocado para `null`).
3. `20` — `n1.proximo` ainda aponta para o objeto que guarda o valor `20`, pois esse encadeamento foi feito por `n1.proximo = n2` **antes** de `n2` ser anulado; anular a variável `n2` não desfaz a referência que `n1.proximo` já guardava.
4. `30` — `n1.proximo.proximo` é o mesmo objeto que `n2.proximo` apontava, ou seja, o objeto que guarda `30`.
5. Não é destruído imediatamente: o objeto com valor `20` continua acessível através de `n1.proximo`, então ainda possui uma referência ativa e **não** é elegível para o Garbage Collector.

</details>

## Material relacionado

- [Diagrama de classes: No, referências e memória](diagramas/classes-no-referencias.svg)
- [Aula 02: Análise de Algoritmos e Complexidade](../Aula%2002%20-%20Analise%20de%20Algoritmos%20e%20Complexidade/detalhes.md)
- [Aula 04: Listas Lineares Sequenciais](../Aula%2004%20-%20Listas%20Lineares%20Sequenciais/detalhes.md)
- [Aula 05: Listas Ligadas Dinâmicas](../Aula%2005%20-%20Listas%20Ligadas%20Dinamicas/detalhes.md)
- Slides originais: [`Aula 03.pdf`](./Aula%2003.pdf)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
