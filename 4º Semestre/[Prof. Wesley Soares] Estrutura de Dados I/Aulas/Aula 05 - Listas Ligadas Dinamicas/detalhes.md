# Aula 05 — Listas Ligadas Dinâmicas

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Data de postagem:** 01/09/2026
> **Tema:** Listas encadeadas dinâmicas, ponteiros encadeados e manipulação de nós em tempo de execução

> **Nota sobre a fonte:** o material original desta aula foi disponibilizado pelo professor exclusivamente como um link externo do Notion (**[ED-I Aula 05: Listas Ligadas Dinâmicas](https://outgoing-salt-444.notion.site/ED-I-Aula-05-Ligas-ligadas-din-micas-3ce8a4f8f8a780658e71e231669cbb5f)**, listado em `Aulas/links-recursos.md`), sem PDF de slides anexado à turma. A tentativa de acessar essa página não retornou conteúdo (página renderizada via JavaScript, não indexável por busca automatizada de texto). O conteúdo abaixo foi reconstruído a partir de fontes internas e verificadas do próprio repositório: os fundamentos de nós e referências já ensinados na [Aula 03](../Aula%2003%20-%20Memoria%20e%20Alocacao%20Dinamica/detalhes.md), a classe `No<T>` já entregue em [`Trabalhos/Trabalho AV1/No.java`](../../Trabalhos/Trabalho%20AV1/No.java) e o enunciado real do Trabalho AV1. **Se o conteúdo do Notion divergir do que está aqui, ele deve prevalecer** — recomenda-se abrir o link original para conferência.

## Objetivo da aula

Estender o conceito de encadeamento de nós (apresentado na Aula 03) para uma estrutura completa de **Lista Ligada**, capaz de crescer e encolher dinamicamente sem limite de capacidade fixo, ao contrário da lista sequencial da Aula 04.

## Lista sequencial x lista ligada

| | Lista sequencial (Aula 04) | Lista ligada (esta aula) |
| :--- | :--- | :--- |
| Armazenamento | Array, posições contíguas | Nós espalhados na Heap, ligados por referência |
| Capacidade | Fixa (ou exige redimensionamento) | Cresce sob demanda, um nó por vez |
| Acesso por posição | O(1) | O(n) — é preciso percorrer nó a nó |
| Inserção no início | O(n) (desloca tudo) | O(1) (só religa ponteiros) |
| Inserção no final | O(1) amortizado | O(n) sem ponteiro de cauda, O(1) com ponteiro de cauda |

## A estrutura do nó

Retomando a Aula 03: cada elemento da lista é representado por um **nó** (`No`), que guarda o dado e uma referência para o próximo nó da cadeia. A versão genérica, já usada no Trabalho AV1, é:

```java
public class No<T> {
    private T dado;
    private No<T> proximo;

    public No(T dado) {
        this.dado = dado;
        this.proximo = null;
    }

    public T getDado() { return dado; }
    public void setDado(T dado) { this.dado = dado; }
    public No<T> getProximo() { return proximo; }
    public void setProximo(No<T> proximo) { this.proximo = proximo; }
}
```

Uma lista ligada é, na prática, apenas uma referência para o **primeiro** nó da cadeia (`primeiro`); cada nó aponta para o próximo, até o último nó apontar para `null`.

```
primeiro
   │
   ▼
[10 | •]──▶[20 | •]──▶[30 | null]
```

## Implementando a `ListaLigada<T>`

```java
public class ListaLigada<T> {
    private No<T> primeiro;
    private int tamanho;

    public ListaLigada() {
        this.primeiro = null;
        this.tamanho = 0;
    }

    public boolean estaVazia() {
        return primeiro == null;
    }

    public int getTamanho() {
        return tamanho;
    }

    // Inserção no início — O(1)
    public void inserirInicio(T dado) {
        No<T> novo = new No<>(dado);
        novo.setProximo(primeiro);
        primeiro = novo;
        tamanho++;
    }

    // Inserção no final — O(n), pois é preciso percorrer até o último nó
    public void inserirFim(T dado) {
        No<T> novo = new No<>(dado);
        if (estaVazia()) {
            primeiro = novo;
        } else {
            No<T> atual = primeiro;
            while (atual.getProximo() != null) {
                atual = atual.getProximo();
            }
            atual.setProximo(novo);
        }
        tamanho++;
    }

    // Busca — O(n)
    public No<T> buscar(T dado) {
        No<T> atual = primeiro;
        while (atual != null) {
            if (atual.getDado().equals(dado)) {
                return atual;
            }
            atual = atual.getProximo();
        }
        return null;
    }

    // Remoção pelo dado — O(n)
    public boolean remover(T dado) {
        if (estaVazia()) {
            return false;
        }
        if (primeiro.getDado().equals(dado)) {
            primeiro = primeiro.getProximo();
            tamanho--;
            return true;
        }
        No<T> anterior = primeiro;
        No<T> atual = primeiro.getProximo();
        while (atual != null) {
            if (atual.getDado().equals(dado)) {
                anterior.setProximo(atual.getProximo());
                tamanho--;
                return true;
            }
            anterior = atual;
            atual = atual.getProximo();
        }
        return false;
    }

    public void imprimir() {
        No<T> atual = primeiro;
        System.out.print("[ ");
        while (atual != null) {
            System.out.print(atual.getDado() + " ");
            atual = atual.getProximo();
        }
        System.out.println("] (tamanho: " + tamanho + ")");
    }
}
```

### Por que inserir no início é O(1)?

Ao contrário da lista sequencial (onde inserir no início exige deslocar todos os elementos), na lista ligada basta criar o novo nó, apontá-lo para o antigo `primeiro` e atualizar a referência `primeiro` — **três atribuições de ponteiro**, independentes do tamanho da lista.

### Por que a remoção do meio não exige deslocamento?

Remover um nó do meio da cadeia significa apenas religar o ponteiro do nó anterior diretamente para o nó seguinte ao removido (`anterior.setProximo(atual.getProximo())`), "pulando" o nó removido — que fica sem nenhuma referência ativa e se torna candidato ao Garbage Collector (ver Aula 03). Não há necessidade de mover fisicamente nenhum outro elemento — diferente da lista sequencial, onde a remoção desloca todos os elementos subsequentes.

## Estudo de caso — Trabalho AV1

O [Trabalho AV1](../../Trabalhos/Trabalho%20AV1/detalhes.md) aplica exatamente essa estrutura a um problema real: um sistema de atendimento de clínica, em que uma `ListaLigada<Paciente>` guarda a fila de pacientes aguardando consulta, com operações de adicionar paciente (inserir no fim), chamar o próximo paciente (remover do início) e cancelar/consultar por número da consulta (busca e remoção por critério, não apenas pelo dado inteiro completo).

---

## Exercício de fixação

1. Declare a classe `No` (atributo `dado`, referência `proximo`, construtor) e encadeie manualmente três nós: `10 -> 20 -> 30 -> null` (sem usar a classe `ListaLigada`).
2. Dada a lista ligada `10 -> 20 -> 30 -> null`, escreva o trecho de código que remove o nó do meio (`20`) **sem** usar o método `remover` pronto — apenas religando as referências.
3. Qual a complexidade de `buscar(valor)` na lista ligada? É a mesma complexidade da busca na lista sequencial da Aula 04?
4. Por que a lista ligada não sofre do problema de "lista cheia" que a lista sequencial (Aula 04) enfrenta?

<details>
<summary>Gabarito</summary>

1.
```java
No<Integer> n1 = new No<>(10);
No<Integer> n2 = new No<>(20);
No<Integer> n3 = new No<>(30);
n1.setProximo(n2);
n2.setProximo(n3);
```

2.
```java
// n1 -> n2 -> n3  vira  n1 -> n3, "pulando" n2
n1.setProximo(n3);
n2 = null; // n2 perde a referência principal e fica elegível ao Garbage Collector
```

3. O(n) — no pior caso é preciso percorrer todos os nós até encontrar o valor (ou chegar a `null`). É a mesma ordem de complexidade da busca sequencial em array (também O(n)), mas o motivo estrutural é diferente: no array percorremos índices, na lista ligada seguimos ponteiros `proximo`.

4. Porque cada nó é alocado individualmente na Heap sob demanda (`new No<>(dado)`), sem depender de um bloco contíguo de memória pré-reservado — a lista cresce um nó por vez, limitada apenas pela memória disponível no Heap, e não por uma capacidade fixa definida na criação da estrutura.

</details>

## Material relacionado

- [Diagrama de classes: No\<T\> e ListaLigada\<T\>](diagramas/classes-no-lista-ligada.svg)
- [Aula 03: Memória e Alocação Dinâmica](../Aula%2003%20-%20Memoria%20e%20Alocacao%20Dinamica/detalhes.md)
- [Aula 04: Listas Lineares Sequenciais](../Aula%2004%20-%20Listas%20Lineares%20Sequenciais/detalhes.md)
- [Trabalho AV1: Sistema de Atendimento com Lista Ligada](../../Trabalhos/Trabalho%20AV1/detalhes.md)
- Fonte externa original (não indexável automaticamente): [Notion — ED-I Aula 05](https://outgoing-salt-444.notion.site/ED-I-Aula-05-Ligas-ligadas-din-micas-3ce8a4f8f8a780658e71e231669cbb5f)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
