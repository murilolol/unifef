# Resumo Consolidado: [Prof. Wesley Soares] Estrutura de Dados I

## 1. Visão Geral e Objetivos da Matéria
A disciplina **Estrutura de Dados I** (4º Semestre), ministrada pelo Prof. Ms. Wesley Soares de Souza, fundamenta-se na premissa de que um software de alto desempenho depende tanto de uma lógica algorítmica apurada quanto da organização inteligente dos dados na memória. O curso aborda a transição conceitual do problema real até a solução implementada, cobrindo os fundamentos de projeto e análise de algoritmos, tipos abstratos de dados (TADs), gerenciamento de memória e o estudo aprofundado de estruturas lineares (listas sequenciais, listas ligadas, pilhas, filas e deques).

## 2. Conceitos-Chave e Terminologia Fundamental
*   **Algoritmo:** Sequência finita, ordenada e precisa de passos para resolver um problema (possui entrada, executa passos, produz saída e é executável). Distingue-se de *programa*, que é a implementação computacional em uma linguagem específica.
*   **Tipo Abstrato de Dados (TAD):** Modelo matemático que define *quais* dados existem, *quais* operações podem ser realizadas e *qual* é o comportamento esperado, sem expor *como* os dados são armazenados ou implementados (Princípio do Encapsulamento e Ocultação de Informação).
*   **Tamanho da Entrada ($n$):** A quantidade de dados que o algoritmo precisa processar (ex.: número de elementos em um vetor ou registros a buscar).
*   **Análise Assintótica e Notação Big O ($O$):** Metodologia para avaliar o crescimento do custo computacional (tempo ou espaço) à medida que o tamanho da entrada ($n$) tende ao infinito, desprezando constantes e termos de menor ordem.
*   **Alocação Dinâmica, Stack e Heap:** Mecanismos de gerenciamento de memória onde variáveis locais e referências residem na *Stack* (Pilha), enquanto objetos, instâncias e arrays criados em tempo de execução via `new` residem na *Heap*.

## 3. Principais Módulos / Tópicos Abordados (com explicações técnicas)

### Módulo 1: Introdução ao Projeto de Algoritmos e Refinamento Sucessivo
*   **Do Problema à Solução:** O ciclo engloba Compreensão $\rightarrow$ Modelagem $\rightarrow$ Algoritmo $\rightarrow$ Estrutura de Dados $\rightarrow$ Implementação.
*   **Refinamento Sucessivo:** Técnica de decomposição de problemas complexos em subproblemas menores através de níveis de abstração progressivos (do enunciado textual em alto nível até pseudocódigo executável).

### Módulo 2: Análise de Algoritmos e Complexidade
*   Avaliação de desempenho independente de hardware, focando na contagem de operações e no comportamento de crescimento:
    *   **Melhor Caso:** Mínima quantidade de operações.
    *   **Pior Caso:** Máxima quantidade de operações (foco principal da Notação Big O).
    *   **Caso Médio:** Comportamento esperado estatisticamente.
*   **Classes de Complexidade Comuns:**
    *   $O(1)$ - *Constante:* Acesso direto a elementos por índice.
    *   $O(\log n)$ - *Logarítmica:* Divisão sucessiva do problema (ex.: Busca Binária).
    *   $O(n)$ - *Linear:* Varredura de elementos em laços simples (ex.: Busca Sequencial).
    *   $O(n^2)$ - *Quadrática:* Laços aninhados (ex.: Algoritmos básicos de ordenação como Bubble Sort e Selection Sort).

### Módulo 3: Memória, Referências e Alocação Dinâmica
*   Diferenciação rigorosa entre **tipos primitivos** (armazenam valores diretamente) e **tipos por referência** (apontam para endereços de memória na *Heap*).
*   O uso de referências (`null`, ponteiros lógicos em Java) para construir encadeamentos de nós (`No`), viabilizando estruturas dinâmicas.
*   **Garbage Collector:** Mecanismo automático de varredura que identifica e libera objetos na *Heap* que não possuem mais referências ativas na *Stack*.

### Módulo 4: Estruturas Lineares — Listas Sequenciais
*   Organização de elementos em posições contíguas de memória (arrays).
*   **Capacidade vs. Tamanho:** O *length* representa o limite máximo alocado no vetor, enquanto o atributo *tamanho* controla a quantidade real de elementos úteis preenchidos ($0 \le \text{tamanho} \le \text{capacidade}$).
*   **Complexidade das Operações:** Acesso por índice custa $O(1)$, mas inserções e remoções no início ou no meio exigem deslocamento de elementos, resultando em custo $O(n)$.

## 4. Relações com o Mercado e Prática Profissional
No desenvolvimento de software corporativo, a escolha incorreta de uma estrutura de dados ou de um algoritmo ineficiente (como substituir uma busca logarítmica ou estrutura indexada por uma varredura quadrática) pode inviabilizar sistemas de grande escala ao processar milhões de registros. O domínio de TADs, alocação de memória e análise de complexidade capacita o Engenheiro de Software a projetar arquiteturas resilientes, otimizar consumo de recursos computacionais em nuvem e escrever códigos escaláveis.

## 5. Dicas de Ouro para Estudo e Provas
1.  **Domine a Notação Big O:** Pratique a contagem de instruções em laços simples e aninhados. Em provas, saiba justificar matematicamente por que termos constantes e de menor grau são descartados.
2.  **Diferencie Capacidade de Tamanho:** Em listas sequenciais, lembre-se sempre de validar o limite do array antes de inserir dados e de manter o controle estrito da variável `tamanho`.
3.  **Desenhe a Memória (Stack vs. Heap):** Ao estudar ponteiros, referências e listas ligadas, crie o hábito de desenhar blocos de memória e setas de referência para visualizar o comportamento de objetos e o impacto de atribuições como `p2 = p1` ou `n2 = null`.
4.  **Compreenda o Propósito dos TADs:** Lembre-se de que o TAD define o *contrato* (o que faz), isolando a implementação (como faz). Isso é cobrado conceitualmente em questões teóricas.
5.  **Atenção aos Casos Limites (*Edge Cases*):** Ao implementar operações em estruturas lineares, teste sempre os cenários extremos: lista vazia, lista cheia, inserção na primeira posição e inserção na última posição.