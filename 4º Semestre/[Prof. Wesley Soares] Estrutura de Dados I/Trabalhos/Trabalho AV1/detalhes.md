# Trabalho AV1 — Sistema de Atendimento de uma Clínica (Lista Ligada)

> **Professor:** Wesley Soares
> **Disciplina:** Estrutura de Dados I (4º Semestre)
> **Prazo de entrega:** 09/09/2026 às 02:59
> **Pontuação máxima:** 100 pontos
> **Conteúdo cobrado:** Lista Ligada (`ListaLigada<T>`), classe `No`, modelagem orientada a objetos

## Exercício 1 — Sistema de Atendimento de uma Clínica

### Contexto

Uma clínica precisa controlar a fila de pacientes que aguardam atendimento. Por enquanto, o sistema será simples: os pacientes são armazenados em uma lista ligada, e cada paciente possui:

- nome;
- idade;
- número da consulta.

A ideia é utilizar a `ListaLigada` estudada em sala (ver [Aula 05 — Listas Ligadas Dinâmicas](../../Aulas/Aula%2005%20-%20Listas%20Ligadas%20Dinamicas/detalhes.md)) para armazenar os pacientes.

### Requisitos do sistema

O sistema deve permitir:

1. **Adicionar paciente** — o paciente deve ser colocado no final da lista.
2. **Chamar próximo paciente** — o primeiro paciente deve ser removido da lista e retornado.
3. **Cancelar uma consulta** — o sistema deve procurar o paciente pelo número da consulta e removê-lo da lista.
4. **Consultar paciente** — o método deve procurar um paciente pelo número da consulta.

### O que deve ser entregue

Um programa contendo a modelagem:

```
Paciente
   ↓
ListaLigada<Paciente>
   ↓
Sistema de Atendimento
```

E um `main` demonstrando:

- cadastro de pelo menos 5 pacientes;
- impressão da fila;
- chamada do próximo paciente;
- cancelamento de uma consulta;
- busca de um paciente.

## Status da entrega nesta pasta

Até o momento, apenas a classe [`No.java`](./No.java) (genérica, `No<T>`) foi implementada nesta pasta — ainda faltam as classes `Paciente`, `ListaLigada<Paciente>`, `SistemaAtendimento` e a classe `Main` com a demonstração completa exigida no enunciado. A estrutura de referência para `ListaLigada<T>` (inserir no início/fim, buscar, remover, imprimir) já está detalhada na [Aula 05](../../Aulas/Aula%2005%20-%20Listas%20Ligadas%20Dinamicas/detalhes.md#implementando-a-listaligadat) e pode ser adaptada diretamente para este trabalho — bastando trocar `remover(T dado)` por uma busca que compare pelo número da consulta do `Paciente`, e não pelo objeto completo.

## Arquivos entregues

- [`No.java`](./No.java) — classe genérica `No<T>` com `dado` e `proximo`, usada como base para a `ListaLigada<Paciente>`.
- [`trabalho AV1.docx`](./trabalho%20AV1.docx) — enunciado original do professor.

## Material relacionado

- [Aula 05: Listas Ligadas Dinâmicas](../../Aulas/Aula%2005%20-%20Listas%20Ligadas%20Dinamicas/detalhes.md)
- [Aula 03: Memória e Alocação Dinâmica](../../Aulas/Aula%2003%20-%20Memoria%20e%20Alocacao%20Dinamica/detalhes.md)
- [Diagrama de classes: Paciente, ListaLigada e SistemaAtendimento](diagramas/classes-sistema-atendimento.svg)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md#resumo-executivo)
