Com base no conteúdo programático de **Estrutura de Dados I** ministrado pelo **Prof. Ms. Wesley Soares de Souza**, elaborei um simulado completo contendo 10 questões de múltipla escolha (com gabarito comentado) e 5 questões discursivas/estudos de caso práticos.

---

# 📝 SIMULADO DE ESTRUTURA DE DADOS I
**Professor:** Ms. Wesley Soares de Souza  
**Conteúdo Abrangido:** Introdução, Algoritmos, Análise Assintótica (Big O), Memória (Stack/Heap), Alocação Dinâmica e Listas Lineares Sequenciais.

---

## PARTE 1: Questões de Múltipla Escolha

### Questão 1
Sobre os conceitos de Algoritmo e Programa, assinale a alternativa **correta**, conforme a introdução da disciplina:
* a) Um algoritmo depende estritamente de uma linguagem de programação específica para ser compreendido conceitualmente.
* b) Um programa é a solução conceitual e independente de plataforma, enquanto o algoritmo é a implementação compilada.
* c) Um algoritmo é uma sequência finita e ordenada de passos para resolver um problema, sendo independente de linguagem, ao passo que o programa é a sua implementação executável.
* d) Algoritmo e programa são sinônimos perfeitos na ciência da computação, usados indistintamente.
* e) O algoritmo não precisa ser finito, desde que produza uma saída correta em algum momento do futuro infinito.

### Questão 2
O que define propriamente um **Tipo Abstrato de Dados (TAD)**?
* a) A definição exata da linguagem de programação em que a estrutura será codificada (ex: Java ou C++).
* b) Quais dados existem e quais operações podem ser realizadas, ocultando a forma como os dados são armazenados e como as operações são implementadas.
* c) A especificação detalhada de quantos bytes de memória RAM a estrutura vai consumir obrigatoriamente.
* d) O uso exclusivo de vetores estáticos para garantir o encapsulamento.
* e) A obrigatoriedade de utilizar alocação dinâmica do tipo `malloc` ou `new`.

### Questão 3
Durante a análise de algoritmos, por que evitamos medir o desempenho de um programa contando o seu tempo de execução em segundos (ex: 0,5 segundos)?
* a) Porque o tempo em segundos é imensurável por ferramentas modernas de software.
* b) Porque o tempo de execução depende de fatores externos e variáveis, como processador, memória, sistema operacional, compilador e linguagem utilizada.
* c) Porque o tempo em segundos é sempre constante, independentemente do tamanho da entrada.
* d) Porque a análise matemática descarta o tempo, focando exclusivamente no consumo de energia da máquina.
* e) Porque a medição em segundos só é válida para algoritmos de complexidade exponencial $O(2^n)$.

### Questão 4
Dada a função de custo $T(n) = 3n^2 + 5n + 10$, qual é a sua complexidade assintótica expressa na Notação Big O?
* a) $O(1)$
* b) $O(n)$
* c) $O(n \log n)$
* d) $O(n^2)$
* e) $O(2^n)$

### Questão 5
Analise o trecho de código abaixo:
```java
int x = vetor[5];
```
Independentemente de o vetor ter 10 ou 1.000.000 de elementos, o acesso a uma posição conhecida por índice possui qual classe de complexidade?
* a) $O(n)$
* b) $O(\log n)$
* c) $O(1)$
* d) $O(n^2)$
* e) $O(n \log n)$

### Questão 6
Em relação à organização da memória em linguagens como Java, analise as afirmativas:
I. A **Stack** (Pilha) armazena chamadas de métodos, variáveis locais e referências.
II. O **Heap** é utilizado para armazenar objetos e arrays criados dinamicamente com o operador `new`.
III. O Garbage Collector atua limpando automaticamente a memória de objetos que não possuem mais referências ativas.

Está correto o que se afirma em:
* a) Apenas I.
* b) Apenas II.
* c) Apenas I e II.
* d) Apenas II e III.
* e) I, II e III.

### Questão 7
O que ocorre na memória Heap quando executamos a instrução `p1 = null;`, sabendo que `p1` era uma referência para um objeto criado anteriormente?
* a) O objeto é imediatamente apagado da memória RAM no mesmo nanossegundo.
* b) A referência passa a apontar para o endereço zero da memória física do computador.
* c) O objeto perde sua associação com a variável `p1`, tornando-se elegível (na mira) para ser coletado futuramente pelo Garbage Collector.
* d) O programa lança imediatamente uma exceção do tipo `NullPointerException`.
* e) A memória Stack é desalocada por completo.

### Questão 8
Em uma **Lista Linear Sequencial** implementada por meio de um array (vetor estático), qual é a complexidade de tempo no **pior caso** para **inserir um elemento no início** ou no meio da lista, quando há necessidade de deslocar elementos subsequentes?
* a) $O(1)$
* b) $O(\log n)$
* c) $O(n)$
* d) $O(n^2)$
* e) $O(n!)$

### Questão 9
Qual é a principal diferença conceitual entre o atributo `tamanho` (elementos efetivamente armazenados) e a `capacidade` (tamanho do array alocado) em uma Lista Sequencial?
* a) Não há diferença; ambos representam o número máximo de bytes ocupados na Heap.
* b) A capacidade representa o total de posições disponíveis no array, enquanto o tamanho representa a quantidade atual de elementos válidos inseridos na lista ($0 \le \text{tamanho} \le \text{capacidade}$).
* c) O tamanho representa o limite físico do hardware, e a capacidade representa o número de métodos da classe.
* d) O tamanho é sempre fixo em 10 itens, enquanto a capacidade diminui a cada remoção.
* e) A capacidade é controlada pelo programador via código, e o tamanho é gerenciado exclusivamente pelo Garbage Collector.

### Questão 10
Qual das seguintes estruturas de dados é a mais indicada quando a necessidade principal do sistema é seguir o princípio **LIFO** (*Last In, First Out* — O último a entrar é o primeiro a sair)?
* a) Fila (*Queue*)
* b) Pilha (*Stack*)
* c) Lista Sequencial por Acesso Direto
* d) Grafo
* e) Árvore Binária de Busca

---

## 🛠️ PARTE 2: Questões Discursivas e Estudos de Caso Práticos

### Questão 11 (Estudo de Caso: Refinamento de Algoritmo)
O Prof. Wesley Soares aborda em suas aulas o processo de refinamento sucessivo para transformar um problema complexo em um algoritmo executável. Imagine que você precisa desenvolver um algoritmo para **controlar uma fila de atendimento bancário**. 
Descreva os passos do refinamento (do Nível 1 ao Nível 3) respondendo: 
1. Quais dados precisam ser armazenados? 
2. Como uma pessoa entra e sai da fila? 
3. Quem deve ser atendido primeiro?

### Questão 12 (Análise de Complexidade)
Determine a complexidade assintótica (Notação Big O) do seguinte trecho de pseudocódigo e explique o raciocínio matemático por trás da contagem de operações:
```text
para i de 1 até n faça
    para j de 1 até n faça
        escreva(i, j)
    fim para
end para
```
* O que aconteceria com a complexidade se o segundo laço (`j`) rodasse apenas até $n/2$ vezes? Justifique.

### Questão 13 (Arquitetura de Memória: Stack vs Heap)
Considere o seguinte código em Java:
```java
Pessoa p1 = new Pessoa("Ana");
Pessoa p2 = p1;
p2.setNome("Carlos");
```
Explique detalhadamente o que ocorre na **Stack** e na **Heap** após a execução dessas três linhas. Se o comando `System.out.println(p1.getNome());` for executado ao final, qual será a saída impressa no console e por quê?

### Questão 14 (Programação Prática de Nós - Alocação Dinâmica)
Escreva em Java a declaração da classe `No` (conforme solicitada nos exercícios de alocação dinâmica da aula 03), contendo um atributo inteiro `valor` e uma referência `proximo` do tipo `No`, além de um construtor que inicialize o valor. Em seguida, escreva o trecho de código necessário para instanciar três nós com os valores `10`, `20` e `30`, encadeando-os sequencialmente (`N1 -> N2 -> N3 -> null`).

### Questão 15 (Listas Sequenciais - Capacidade vs Tamanho)
Em uma Lista Linear Sequencial baseada em array, temos a seguinte estrutura declarada:
```java
int capacidade = 10;
int tamanho = 3;
int[] elementos = {10, 20, 30, 0, 0, 0, 0, 0, 0, 0};
```
Se tentarmos realizar a operação `lista.obter(15);`, o que o método deve retornar ou disparar (validando o índice)? Qual é a regra lógica fundamental de validação de limites que deve ser implementada para garantir que o sistema não corrompa a memória ou lance uma exceção de índice inválido (`IndexOutOfBoundsException`)?

---

# 🔑 GABARITO COMENTADO

## Gabarito da Parte 1 (Múltipla Escolha)

1. **Resposta: C**  
   *Comentário:* O algoritmo é a lógica conceitual independente de linguagem, enquanto o programa é a implementação concreta compilada/interpretada.

2. **Resposta: B**  
   *Comentário:* O TAD define o *que* a estrutura faz e quais são suas regras e operações, ocultando o *como* (implementação interna e armazenamento).

3. **Resposta: B**  
   *Comentário:* Medir tempo em segundos varia conforme o hardware, SO, compilador e carga da máquina. A análise assintótica foca no comportamento de crescimento do número de operações.

4. **Resposta: D**  
   *Comentário:* Na notação Big O, descartamos as constantes e os termos de menor ordem ($5n$ e $10$), restando o termo dominante $n^2$.

5. **Resposta: C**  
   *Comentário:* O acesso direto a um vetor por índice fixo consome tempo constante, ou seja, $O(1)$.

6. **Resposta: E**  
   *Comentário:* Todas as afirmações estão corretas segundo a arquitetura de execução em Java apresentada nas aulas sobre Stack, Heap e Garbage Collector.

7. **Resposta: C**  
   *Comentário:* Atribuir `null` a uma referência retira o apontamento para o objeto na Heap, deixando-o órfão e marcado para remoção pelo Garbage Collector.

8. **Resposta: C**  
   *Comentário:* Inserir no início ou meio exige o deslocamento físico de todos os elementos subsequentes no array, gerando um custo linear $O(n)$.

9. **Resposta: B**  
   *Comentário:* A capacidade é o tamanho total alocado do vetor, e o tamanho representa quantos elementos úteis de fato constam na lista no momento.

10. **Resposta: B**  
    *Comentário:* A Pilha (*Stack*) opera sob o princípio LIFO (*Last In, First Out*).

---

## Gabarito da Parte 2 (Discursivas e Práticas)

11. **Resposta Esperada:**  
    * **Dados:** Nome/ID da pessoa, horário de chegada.
    * **Entrada e Saída:** Entra no final da fila (fim/enqueue) e sai do início (frente/dequeue).
    * **Quem é atendido primeiro:** A primeira pessoa que entrou (FIFO). No refinamento sucessivo, passamos da abstração do problema para subproblemas menores (estruturar os dados, criar métodos de inserir, remover e verificar se está vazia).

12. **Resposta Esperada:**  
    * A complexidade é **$O(n^2)$** (Quadrática). O primeiro laço executa $n$ vezes e, para cada volta dele, o segundo laço executa mais $n$ vezes ($n \times n = n^2$).  
    * Se o segundo laço rodasse até $n/2$, o custo total seria $n \times (n/2) = n^2 / 2$. Na análise assintótica, descartamos a constante $1/2$, mantendo a complexidade final como **$O(n^2)$**.

13. **Resposta Esperada:**  
    * **Stack:** Contém as variáveis locais de referência `p1` e `p2`.  
    * **Heap:** Contém o objeto criado (`new Pessoa("Ana")`). Como `p2 = p1`, ambas as referências apontam exatamente para o **mesmo** objeto na Heap.  
    * Quando `p2.setNome("Carlos")` é executado, o nome do objeto compartilhado é alterado. Portanto, ao executar `p1.getNome()`, o resultado impresso será **"Carlos"**.

14. **Resposta Esperada:**  
    ```java
    class No {
        int valor;
        No proximo;

        public No(int valor) {
            this.valor = valor;
            this.proximo = null;
        }
    }

    // Instanciação e encadeamento:
    No n1 = new No(10);
    No n2 = new No(20);
    No n3 = new No(30);

    n1.proximo = n2;
    n2.proximo = n3;
    ```

15. **Resposta Esperada:**  
    * Como o índice `15` está fora do intervalo válido de elementos armazenados ($0 \le \text{índice} < \text{tamanho}$), o método deve lançar uma exceção (ex: `IndexOutOfBoundsException`) ou retornar um erro controlado.  
    * A regra de validação fundamental para consulta ou manipulação segura baseia-se em garantir que o índice informado seja estritamente maior ou igual a `0` e estritamente menor que o `tamanho` atual da lista (`0 <= indice && indice < tamanho`).