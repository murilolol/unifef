# ⚡ CHEAT SHEET: Estrutura de Dados I (Prof. Wesley Soares)

---

## 1. Fundamentos e Algoritmos
* **Algoritmo:** Sequência finita, ordenada e precisa de passos para resolver um problema. Diferente do programa (que é a implementação em linguagem de programação).
* **Refinamento Sucessivo:** Técnica de projeto que vai da abstração (nível geral) até os detalhes implementáveis.
* **TAD (Tipo Abstrato de Dados):** Define *o que* a estrutura faz e quais são suas operações/comportamentos, sem expor *como* os dados são armazenados ou implementados (Ex: Pilha, Fila). Abstração + Encapsulamento.

---

## 2. Análise Assintótica e Notação Big O
Avalia o crescimento do custo computacional (tempo/operações) conforme o tamanho da entrada ($n$) aumenta, ignorando constantes.

| Complexidade | Nome | Exemplo / Comportamento |
| :--- | :--- | :--- |
| **$O(1)$** | Constante | Acessar um vetor por índice (`vetor[5]`) |
| **$O(\log n)$** | Logarítmica | Busca Binária (reduz o problema à metade a cada passo) |
| **$O(n)$** | Linear | Pesquisa sequencial / Percorrer um vetor simples |
| **$O(n \log n)$** | Linear-logarítmica | Algoritmos eficientes de ordenação (*Merge Sort*) |
| **$O(n^2)$** | Quadrática | Laços aninhados simples (*Bubble Sort*, *Selection Sort*) |
| **$O(2^n)$** | Exponencial | Recursões ingênuas (fibonacci sem memoization) |

* **Casos de Análise:** Melhor caso (mínimo de operações), Pior caso ($O$ - teto máximo) e Caso médio (comportamento esperado).

---

## 3. Memória, Stack vs. Heap e Alocação Dinâmica
* **Stack (Pilha):** Armazena variáveis locais, chamadas de métodos e **referências**.
* **Heap:** Armazena os **objetos** e arrays criados dinamicamente em tempo de execução via operador `new`.
* **Referências e Null:** Variáveis de referência apontam para endereços na Heap. Se apontarem para `null`, geram `NullPointerException` ao tentar acessar membros.
* **Garbage Collector (GC):** Identifica e limpa da Heap objetos que não possuem mais referências ativas.

---

## 4. Estruturas Lineares: Listas Sequenciais (Arrays)
* **Conceito:** Elementos armazenados em posições físicas **consecutivas** de um array.
* **Capacidade vs. Tamanho:** 
  * `Capacidade`: Tamanho total alocado do vetor (`length`).
  * `Tamanho`: Quantidade real de elementos atualmente armazenados ($0 \le \text{tamanho} \le \text{capacidade}$).

### Operações e Complexidade (Lista Sequencial)
| Operação | Complexidade | Motivo |
| :--- | :--- | :--- |
| Acessar por índice (`obter(i)`) | **$O(1)$** | Endereçamento direto por posição. |
| Pesquisar valor (`pesquisar(val)`) | **$O(n)$** | Pior caso exige varredura completa. |
| Inserir no final | **$O(1)$** *(se houver espaço)* | Acesso direto ao fim. |
| Inserir/Remover no início ou meio | **$O(n)$** | Exige deslocamento de elementos para abrir/fechar espaço. |