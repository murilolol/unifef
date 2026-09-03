# Apostila Prática: Estrutura de Dados I
**Professor Responsável:** Prof. Ms. Wesley Soares de Souza  
**Instituição/Curso:** Superior em Tecnologia / Engenharia de Software / Ciência da Computação  
**Semestre:** 4º Semestre  

---

## 📚 Apresentação da Disciplina

Esta apostila prática foi desenvolvida com base nas diretrizes e aulas ministradas pelo **Prof. Ms. Wesley Soares de Souza**, engenheiro de software sênior com vasta experiência acadêmica e de mercado. O objetivo deste material é traduzir os fundamentos teóricos de **Estrutura de Dados I** em códigos limpos, comentados e prontos para execução (focando em **Java**, linguagem padrão da disciplina), cobrindo desde a análise assintótica de algoritmos até a implementação de estruturas lineares dinâmicas.

---

## Módulo 1: Fundamentos, Análise de Algoritmos e Complexidade

### 1.1 Introdução à Análise Assintótica (Notação Big-O)
A análise de algoritmos estuda o comportamento do custo computacional (tempo e espaço) à medida que o tamanho da entrada ($N$) cresce. Abaixo, apresentamos exemplos práticos em Java das principais classes de complexidade vistas em aula.

#### Exemplo Prático: Classes de Complexidade Big-O
```java
public class AnaliseComplexidade {

    // O(1) - Complexidade Constante
    // O tempo de execução não depende do tamanho do vetor.
    public static int obterPrimeiroElemento(int[] vetor) {
        if (vetor.length == 0) return -1;
        return vetor[0]; // Acesso direto por índice
    }

    // O(n) - Complexidade Linear
    // O número de operações cresce proporcionalmente a N.
    public static boolean pesquisaSequencial(int[] vetor, int valorProcurado) {
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] == valorProcurado) {
                return true; // Encontrou
            }
        }
        return false;
    }

    // O(n²) - Complexidade Quadrática
    // Laços aninhados percorrem a entrada multiplicando as iterações.
    public static void imprimirMatrizDePares(int n) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                System.out.println("Par: (" + i + ", " + j + ")");
            }
        }
    }

    public static void main(String[] args) {
        int[] dados = {10, 25, 31, 42, 58, 70, 81};

        System.out.println("--- Testando O(1) ---");
        System.out.println("Primeiro elemento: " + obterPrimeiroElemento(dados));

        System.out.println("\n--- Testando O(n) ---");
        System.out.println("O valor 42 está presente? " + pesquisaSequencial(dados, 42));

        System.out.println("\n--- Testando O(n²) para N = 3 ---");
        imprimirMatrizDePares(3);
    }
}
```

---

## Módulo 2: Memória, Alocação Dinâmica e Referências (Stack vs. Heap)

### 2.1 O Modelo de Memória em Java
Em Estrutura de Dados, precisamos compreender como a memória RAM armazena variáveis primitivas (na **Stack**) e objetos/referências (na **Heap**). O operador `new` aloca dinamicamente espaço na Heap.

#### Exemplo Prático: Ponteiros e Encadeamento de Nós (`No`)
```java
// Definição da classe No para estruturas encadeadas
class No {
    int valor;
    No proximo;

    // Construtor
    public No(int valor) {
        this.valor = valor;
        this.proximo = null;
    }
}

public class TesteMemoriaHeap {
    public static void main(String[] args) {
        // Alocação dinâmica de nós na Heap
        No n1 = new No(10);
        No n2 = new No(20);
        No n3 = new No(30);

        // Encadeando os nós: n1 -> n2 -> n3 -> null
        n1.proximo = n2;
        n2.proximo = n3;

        // Percorrendo e imprimindo a estrutura encadeada
        No atual = n1;
        System.out.print("Estrutura Encadeada: ");
        while (atual != null) {
            System.out.print(atual.valor + " -> ");
            atual = atual.proximo;
        }
        System.out.println("NULL");

        // Demonstração de Coleta de Lixo (Garbage Collector)
        // Se desconectarmos n2: n2 = null, o objeto 20 ainda é acessível via n1.proximo
        n2 = null; 
        System.out.println("Após n2 = null, n1.proximo.valor ainda é: " + n1.proximo.valor);
    }
}
```

---

## Módulo 3: Listas Lineares Sequenciais

### 3.1 Implementação de Lista Sequencial baseada em Array
Uma Lista Sequencial armazena elementos em posições contíguas de memória. Ela diferencia **Capacidade** (tamanho total do vetor físico) de **Tamanho Atual** (elementos efetivamente inseridos).

#### Exemplo Prático: Classe `ListaSequencial` Completa
```java
public class ListaSequencial {
    private int[] elementos;
    private int tamanho; // Quantidade de elementos armazenados atualmente
    private int capacidade; // Tamanho máximo do array

    // Construtor
    public ListaSequencial(ancaidadeCapacidade) {
        this.capacidade = ancaidadeCapacidade;
        this.elementos = new int[this.capacidade];
        this.tamanho = 0;
    }

    // Sobrecarga de construtor padrão (capacidade 10)
    public ListaSequencial() {
        this(10);
    }

    // Retorna o tamanho atual da lista
    public int getTamanho() {
        return this.tamanho;
    }

    // Verifica se a lista está cheia
    public boolean estaCheia() {
        return this.tamanho == this.capacidade;
    }

    // Inserção no final da lista - O(1)
    public boolean adicionar(int valor) {
        if (estaCheia()) {
            System.out.println("Erro: Lista cheia!");
            return false;
        }
        this.elementos[this.tamanho] = valor;
        this.tamanho++;
        return true;
    }

    // Consulta por posição - O(1)
    public int obter( posicao) {
        if (posicao < 0 || posicao >= this.tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido para a lista.");
        }
        return this.elementos[posicao];
    }

    // Inserção no meio com deslocamento - O(n)
    public boolean inserirNaPosicao(int posicao, int valor) {
        if (estaCheia()) {
            System.out.println("Erro: Lista cheia!");
            return false;
        }
        if (posicao < 0 || posicao > this.tamanho) {
            System.out.println("Erro: Posição inválida!");
            return false;
        }

        // Deslocamento dos elementos para a direita
        for (int i = this.tamanho - 1; i >= posicao; i--) {
            this.elementos[i + 1] = this.elementos[i];
        }

        this.elementos[posicao] = valor;
        this.tamanho++;
        return true;
    }

    // Método para imprimir a lista
    public void imprimir() {
        System.out.print("[ ");
        for (int i = 0; i < this.tamanho; i++) {
            System.out.print(this.elementos[i] + " ");
        }
        System.out.println("] (Tamanho: " + this.tamanho + "/" + this.capacidade + ")");
    }

    // Método Principal para Testes
    public static void main(String[] args) {
        ListaSequencial lista = new ListaSequencial(5);

        lista.adicionar(10);
        lista.adicionar(20);
        lista.adicionar(30);
        
        System.out.println("Estado inicial da lista:");
        lista.imprimir();

        System.out.println("Inserindo o valor 25 na posição 1 (índice 1):");
        lista.inserirNaPosicao(1, 25);
        lista.imprimir();

        System.out.println("Elemento na posição 2: " + lista.obter(2));
    }
}
```

---

## 📝 Lista de Exercícios Práticos Resolvidos

### Exercício 1: Custo Computacional de Laços Aninhados
**Enunciado:** Determine a complexidade assintótica (Notação Big-O) do seguinte trecho de pseudocódigo apresentado em aula:
```text
para i = 1 até n
    para j = 1 até i
        escreva(i, j)
    fim
fim
```
* **Solução e Comentários:**
  - O primeiro laço roda $N$ vezes.
  - O segundo laço roda dependendo de $i$: quando $i=1$, roda 1 vez; quando $i=2$, roda 2 vezes; até $N$ vezes.
  - O total de execuções é a soma da Progressão Aritmética: $1 + 2 + 3 + \dots + N = \frac{N(N + 1)}{2} = \frac{N^2 + N}{2}$.
  - Pela regra da análise assintótica, descartamos as constantes e os termos de menor ordem, resultando em **$O(N^2)$ (Complexidade Quadrática)**.

---

### Exercício 2: Manipulação de Referências em Java
**Enunciado:** Com base nos conceitos de alocação dinâmica da Aula 03, escreva um programa que crie três nós encadeados contendo os valores `10`, `20` e `30`, execute a remoção da referência intermediária e exiba o resultado no console.

* **Código Resolvido:**
```java
public class ExercicioReferencias {
    public static void main(String[] args) {
        // Criando os nós dinamicamente
        No n1 = new No(10);
        No n2 = new No(20);
        No n3 = new No(30);

        // Ligando os nós
        n1.proximo = n2;
        n2.proximo = n3;

        // Removendo o acesso direto ao n2 (simulando isolamento para o Garbage Collector)
        // O nó 20 ainda pode ser alcançado via n1.proximo antes da alteração, 
        // mas vamos alterar o ponteiro de n1 para pular direto para n3:
        n1.proximo = n3;
        n2 = null; // n2 perde a referência principal da stack

        // Verificando a nova estrutura
        System.out.println("--- Após saltar o nó central ---");
        System.out.println("N1 aponta para o valor: " + n1.valor);
        System.out.println("O próximo de N1 aponta para o valor: " + n1.proximo.valor); // Deve ser 30
    }
}
```

---

### Exercício 3: Busca de Elemento em Lista Sequencial
**Enunciado:** Adicione à classe `ListaSequencial` um método chamado `pesquisar(int valor)` que retorne o índice onde o elemento se encontra, ou `-1` caso ele não exista na lista. Qual é a complexidade deste método no pior caso?

* **Implementação do Método:**
```java
    // Pesquisa sequencial - Pior caso: O(n)
    public int pesquisar(int valor) {
        for (int i = 0; i < this.tamanho; i++) {
            if (this.elementos[i] == valor) {
                return i; // Retorna o índice encontrado
            }
        }
        return -1; // Não encontrado
    }
```
* **Análise de Complexidade:** No pior caso (quando o elemento está na última posição ou não existe na lista), o algoritmo precisa percorrer todos os elementos armazenados ($N$ posições). Logo, a complexidade temporal é **$O(N)$**.