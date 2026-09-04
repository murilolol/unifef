# Trabalho — Complexidade de Algoritmos

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Prazo de entrega:** 19/08/2026 às 02:59
> **Pontuação máxima:** 100 pontos
> **Conteúdo cobrado:** Notação Big-O, algoritmos de busca (linear e binária) e de ordenação (Bubble Sort e Merge Sort), análise empírica de tempo de execução

## Descrição da atividade

Atividade prática focada na análise de tempo de execução e consumo de memória de diferentes algoritmos de busca e ordenação utilizando a Notação Big-O, conforme os conceitos da [Aula 02 — Análise de Algoritmos e Complexidade](../../Aulas/Aula%2002%20-%20Analise%20de%20Algoritmos%20e%20Complexidade/detalhes.md). Não há um enunciado formal em `.docx` anexado a esta pasta — o material entregue é a própria implementação em [`ComplexidadeAlgoritmos.java`](./ComplexidadeAlgoritmos.java), que serve simultaneamente como enunciado prático e como resolução.

## O que o programa implementa

O programa único (`ComplexidadeAlgoritmos.java`) implementa, testa e mede empiricamente quatro algoritmos clássicos, comparando a previsão teórica de complexidade com o tempo real de execução:

| Algoritmo | Complexidade (pior caso) | Método |
| :--- | :--- | :--- |
| Busca Linear | O(n) | `buscaLinear(int[] vetor, int valor)` |
| Busca Binária | O(log n) | `buscaBinaria(int[] vetorOrdenado, int valor)` |
| Bubble Sort | O(n²) | `bubbleSort(int[] vetor)` |
| Merge Sort | O(n log n) | `mergeSort(int[] vetor, int inicio, int fim)` |

### Estrutura da execução (`main`)

1. **`executarTestesBusca()`** — gera 1 milhão de valores pares ordenados e compara o tempo de `buscaLinear` contra `buscaBinaria` para encontrar o mesmo valor, medindo com `System.nanoTime()`.
2. **`executarTestesOrdenacao()`** — gera um vetor aleatório de 20.000 elementos e compara o tempo de `bubbleSort` (O(n²)) contra `mergeSort` (O(n log n)) para ordenar cópias idênticas do mesmo vetor.
3. **`executarAnaliseEscalabilidade()`** — imprime uma tabela comparando, para `n = 1.000`, `10.000` e `50.000`, a contagem teórica de operações de `O(1)`, `O(log n)`, `O(n)`, `O(n log n)` e `O(n²)`, evidenciando numericamente como cada classe de complexidade cresce.

### Detalhes de implementação notáveis

- `bubbleSort` inclui a otimização de parada antecipada (`if (!trocou) break;`) quando nenhuma troca ocorre em uma passada — o vetor já está ordenado.
- `mergeSort` segue a estratégia de divisão e conquista clássica, dividindo o vetor recursivamente ao meio e mesclando (`merge`) as duas metades já ordenadas.
- `buscaBinaria` exige que o vetor de entrada esteja previamente ordenado — por isso os testes usam `dados[i] = i * 2` (sequência crescente).

## Arquivos entregues

- [`ComplexidadeAlgoritmos.java`](./ComplexidadeAlgoritmos.java) — classe única contendo os quatro algoritmos, a bateria de testes de desempenho e a análise de escalabilidade.

## Material relacionado

- [Aula 02: Análise de Algoritmos e Complexidade](../../Aulas/Aula%2002%20-%20Analise%20de%20Algoritmos%20e%20Complexidade/detalhes.md)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
