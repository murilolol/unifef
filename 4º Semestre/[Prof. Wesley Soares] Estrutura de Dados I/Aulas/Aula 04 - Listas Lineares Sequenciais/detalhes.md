# Aula 04 — Listas Lineares Sequenciais

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Tema:** Listas sequenciais implementadas sobre arrays: inserção, remoção, busca e complexidade

## Objetivo da aula

Estudar a primeira estrutura linear da disciplina — a lista sequencial — entendendo como ela é organizada na memória, a diferença entre capacidade e tamanho, e a complexidade de cada operação (acesso, inserção, remoção e busca).

## O que é uma lista?

Uma **lista** é uma coleção de elementos organizados em uma determinada sequência, sobre a qual podemos realizar operações como inserir, remover, consultar, pesquisar, alterar e percorrer.

**Lista** é o conceito (uma sequência de elementos); **array** é uma possível estrutura de armazenamento para essa sequência. Uma lista pode ter:

- **Implementação sequencial** (array) — assunto desta aula;
- **Implementação ligada** (ponteiros/nós) — assunto da [Aula 05](../Aula%2005%20-%20Listas%20Ligadas%20Dinamicas/detalhes.md).

## O que significa "sequencial"?

Na lista sequencial, os elementos são armazenados em posições consecutivas de um array:

```java
int[] valores = new int[6];
// [10, 20, 30, null, null, null]
//   0    1    2    3     4     5
```

Acessar uma posição conhecida do array é **O(1)** — endereçamento direto.

### O problema de inserir mantendo a ordem

Em uma lista ordenada `[10, 20, 30, 40, 50, 60]`, inserir o valor `25` na posição correta exige deslocar todos os elementos a partir daquela posição uma casa à frente: `[10, 20, 25, 30, 40, 50, 60]`. No pior caso, **todos** os `n` elementos precisam ser deslocados → complexidade **O(n)**.

## Modelando a lista sequencial

A ideia central: um array de **elementos** (onde os dados ficam) e um contador de **tamanho** (quantos elementos estão realmente em uso).

```java
public class ListaSequencial {
    private int[] elementos;
    private int tamanho; // quantidade de elementos armazenados

    public ListaSequencial(int capacidade) {
        elementos = new int[capacidade];
        tamanho = 0;
    }
}
```

### Capacidade x tamanho

Com `int[] elementos = new int[10]` e 4 elementos preenchidos: **capacidade = 10** (`elementos.length`, o total de posições físicas do array); **tamanho = 4** (quantos elementos realmente úteis existem). Regra sempre válida: `0 ≤ tamanho ≤ capacidade`. Usamos a variável `tamanho` — e não `length` — porque `length` é fixo e representa a capacidade total, enquanto `tamanho` varia conforme elementos são inseridos/removidos.

### Inserindo no final — O(1)

```java
public void adicionar(int valor) {
    elementos[tamanho] = valor;
    tamanho++;
}
```

### Percorrendo a lista — O(n)

```java
public void imprimir() {
    for (int i = 0; i < tamanho; i++) {
        System.out.println(elementos[i]);
    }
}
```

### Consultando por posição — O(1)

```java
public int obter(int indice) {
    if (indice < 0 || indice >= tamanho) {
        throw new IndexOutOfBoundsException();
    }
    return elementos[indice];
}
```

A validação garante que só se acesse posições dentro do intervalo `[0, tamanho)`, mesmo que a capacidade física do array seja maior.

### Pesquisando um elemento — O(n)

```java
public int buscar(int valor) {
    for (int i = 0; i < tamanho; i++) {
        if (elementos[i] == valor) {
            return i;
        }
    }
    return -1;
}
```

No pior caso (elemento na última posição ou ausente), o algoritmo percorre todas as `n` posições.

### Inserindo no meio — O(n)

Para inserir `25` entre `20` e `30` em `[10, 20, 30, 40]`, todos os elementos a partir da posição de destino precisam ser deslocados uma casa para a direita antes da inserção:

```java
public void adicionar(int indice, int valor) {
    if (indice < 0 || indice > tamanho) {
        throw new IndexOutOfBoundsException();
    }
    if (tamanho == elementos.length) {
        throw new IllegalStateException("Lista cheia");
    }
    for (int i = tamanho; i > indice; i--) {
        elementos[i] = elementos[i - 1];
    }
    elementos[indice] = valor;
    tamanho++;
}
```

O algoritmo: (1) valida se o índice não é maior que o tamanho; (2) verifica se o tamanho não é igual à capacidade; (3) reorganiza a lista deslocando elementos para abrir espaço. **Complexidade da inserção:** O(n) para deslocamento no início ou no meio; **O(1)** apenas quando a inserção ocorre no final e há espaço livre.

### Removendo um elemento — O(n)

Remover a posição de `30` em `[10, 20, 25, 30, 40]` exige deslocar os elementos seguintes uma casa para trás, cobrindo o espaço aberto:

```java
public int remover(int indice) {
    if (indice < 0 || indice >= tamanho) {
        throw new IndexOutOfBoundsException();
    }
    int removido = elementos[indice];
    for (int i = indice; i < tamanho - 1; i++) {
        elementos[i] = elementos[i + 1];
    }
    tamanho--;
    return removido;
}
```

### Lista cheia — e agora?

Isso significa que uma lista sequencial nunca pode crescer além da capacidade inicial? Não — há duas soluções: implementar uma **lista dinâmica** (redimensionar o array internamente quando ele enche, criando um novo array maior e copiando os elementos) ou aumentar a capacidade do array manualmente antes de uma operação que exceda o limite atual.

## Resumo de complexidade

| Operação | Complexidade | Motivo |
| :--- | :--- | :--- |
| Acessar por índice (`obter(i)`) | O(1) | Endereçamento direto por posição |
| Pesquisar valor (`buscar(valor)`) | O(n) | Pior caso exige varredura completa |
| Inserir no final (com espaço livre) | O(1) | Acesso direto ao fim |
| Inserir/remover no início ou meio | O(n) | Exige deslocamento de elementos |

---

## Exercício de fixação

1. Implemente, em Java, uma classe `ListaSequencial` com capacidade fixa de 10 posições e os métodos `adicionar(valor)`, `obter(indice)`, `buscar(valor)` e `remover(indice)`, seguindo as assinaturas apresentadas nesta aula.
2. Com `capacidade = 10`, `tamanho = 3`, `elementos = {10, 20, 30, 0, 0, 0, 0, 0, 0, 0}`, o que `lista.obter(15)` deve retornar ou disparar? Qual a regra de validação de limites?
3. Se a lista `[10, 20, 30, 40]` (tamanho 4) remover o elemento do índice `1` (valor `20`), qual o estado final do array e do `tamanho`?

<details>
<summary>Gabarito</summary>

1. Ver implementação completa de referência na seção "Modelando a lista sequencial" acima (métodos `adicionar`, `adicionar(indice, valor)`, `obter`, `buscar` e `remover`).
2. Deve lançar `IndexOutOfBoundsException` (ou erro equivalente) — a regra de validação é `0 ≤ índice < tamanho`; como `15 ≥ tamanho (3)`, o índice é inválido mesmo estando dentro da capacidade física de 10.
3. `[10, 30, 40, 40]` fisicamente no array (a última posição válida repete o antigo valor, mas é ignorada), com `tamanho = 3` — logo, para fins da lista, o conteúdo lógico é `[10, 30, 40]`.

</details>

## Material relacionado

- [Diagrama de classes: ListaSequencial](diagramas/classes-lista-sequencial.svg)
- [Diagrama de atividades: Inserção com deslocamento](diagramas/atividades-insercao-lista-sequencial.svg)
- [Aula 03: Memória e Alocação Dinâmica](../Aula%2003%20-%20Memoria%20e%20Alocacao%20Dinamica/detalhes.md)
- [Aula 05: Listas Ligadas Dinâmicas](../Aula%2005%20-%20Listas%20Ligadas%20Dinamicas/detalhes.md)
- Slides originais: [`Aula 04.pdf`](./Aula%2004.pdf)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
