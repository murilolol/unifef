# Aula — Monitores e Deadlock em Sistemas Operacionais

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Tema:** Gerenciamento de concorrência e prevenção de impasses em multiprocessadores

## Objetivo da aula

Compreender o papel dos monitores como mecanismo de sincronização de alto nível para programação concorrente, seus componentes essenciais e o funcionamento da exclusão mútua garantida por eles; entender o conceito de deadlock, suas causas, consequências e a representação por grafo de alocação de recursos; e reconhecer os limites dos monitores frente ao problema do deadlock.

## Sumário da aula

1. Introdução aos monitores e à concorrência
2. Definição e componentes dos monitores
3. Exclusão mútua e mecanismo de trava
4. Introdução ao problema do deadlock
5. Visualização e limitações dos monitores frente ao deadlock

---

## 1. O problema da concorrência

A programação paralela e o uso de multiprocessadores introduzem desafios no gerenciamento de processos, decorrentes da necessidade de coordenar o acesso a recursos compartilhados entre tarefas executadas simultaneamente. A concorrência está presente em diversos domínios:

- **Sistemas operacionais** — gerenciam a alocação de recursos e a execução de múltiplos processos e threads, sendo o exemplo fundamental de concorrência.
- **Controle de tráfego aéreo** — coordena o movimento de várias aeronaves simultaneamente, evitando colisões.
- **Sistemas de controle em tempo real** — respondem a eventos externos dentro de prazos estritos (robótica industrial, instrumentação médica).
- **Refinarias de petróleo** — monitoram e controlam processos industriais complexos e interligados, onde falhas em um subsistema podem ter impacto em larga escala.

## 2. O que é um monitor?

Para resolver problemas de programação concorrente em máquinas multiprocessadas, o **monitor** é uma ferramenta essencial: um objeto que encapsula dados e procedimentos para gerenciar a alocação de um recurso compartilhado reutilizável, garantindo sincronização e evitando condições de corrida.

### Componentes essenciais de um monitor

| Componente | Função |
| :--- | :--- |
| **Dados privados** | Variáveis internas que encapsulam o estado do recurso compartilhado, acessíveis exclusivamente pelos procedimentos do monitor. |
| **Procedimentos de acesso** | Funções/métodos que definem as operações permitidas sobre os dados privados, garantindo integridade e controle de acesso. |
| **Fila de espera** | Mecanismo que gerencia processos que tentam acessar o recurso quando ele está ocupado, assegurando a exclusão mútua. |

## 3. Exclusão mútua e mecanismo de trava

Para alocar variáveis utilizando monitores, uma thread deve invocar uma rotina de **entrada de monitor**. Esse mecanismo centraliza o acesso controlado aos recursos compartilhados, garantindo a integridade dos dados.

- **Exclusão mútua do monitor:** embora vários threads possam solicitar acesso simultaneamente, a permissão de entrada no monitor é controlada estritamente por sua porta de entrada, garantindo que apenas um thread por vez execute operações dentro de suas seções críticas.
- **Aquisição da trava:** uma thread adquire a trava do monitor ao chamar sua rotina de entrada, desde que nenhum outro thread esteja ativo dentro dele.
- **Monitor ocupado:** enquanto um thread está dentro do monitor, nenhum outro thread pode obter o recurso — estado crucial para evitar condições de corrida.
- **Espera por acesso:** quando um thread tenta acessar um monitor travado, ele entra em estado de espera controlada, aguardando fora do monitor até que a trava seja liberada.

### Benefícios fundamentais dos monitores

- A **exclusão mútua** é garantida, assegurando que apenas um processo por vez possa acessar recursos críticos compartilhados.
- A **programação concorrente é simplificada** pela encapsulação da lógica de sincronização dentro de uma única estrutura.
- A **prevenção de condições de corrida** é eficaz, eliminando inconsistências nos dados ao controlar o acesso exclusivo.

## 4. Introdução ao deadlock

Um problema comum em sistemas de multiprogramação é o **deadlock** (impasse). Um processo ou thread entra em deadlock — estado travado — quando aguarda por um evento que jamais ocorrerá.

Em sistemas de multiprogramação, o compartilhamento de recursos é essencial, mas essa característica pode levar ao deadlock: um ou mais processos ficam travados, incapazes de progredir, aguardando recursos que nunca serão liberados.

### Consequências do deadlock

- **Perda de trabalho acumulado:** operações incompletas podem precisar ser reiniciadas, ou dados não salvos podem se tornar irrecuperáveis.
- **Redução de rendimento geral:** os recursos permanecem bloqueados, degradando severamente o desempenho global do sistema.
- **Falhas no sistema:** a exaustão de recursos ou a impossibilidade de progressão pode exigir reinicialização ou interrupção forçada.

### A analogia do engarrafamento

Um deadlock pode ser comparado a um engarrafamento urbano: vários automóveis tentam transitar por um bairro movimentado e o tráfego trava completamente. Nessa analogia:

- Os carros representam os processos (threads ou tarefas) buscando executar operações.
- As seções da rua simbolizam os recursos (memória, CPU, dispositivos de E/S) necessários à conclusão dos processos.

### Grafo de alocação de recursos

Um **grafo de alocação de recursos** é uma ferramenta visual essencial para identificar deadlocks em sistemas operacionais. Ele representa os processos (retângulos) e os recursos (círculos) do sistema, mostrando suas interações.

**Exemplo de deadlock simples — espera circular:**

- **Processo P1** está alocado ao recurso R1 e, simultaneamente, requisita o recurso R2.
- **Processo P2** está alocado ao recurso R2 e, simultaneamente, requisita o recurso R1.

Cada processo retém um recurso e aguarda a liberação do recurso retido pelo outro — um ciclo de dependência em que a progressão de um processo bloqueia a do outro indefinidamente. Essa é a característica central da **espera circular**, uma das quatro condições necessárias para um deadlock (junto de exclusão mútua, posse e espera, e não preempção — condições de Coffman).

## 5. Monitores e deadlock: uma limitação importante

Embora os monitores sejam essenciais para assegurar a exclusão mútua e evitar condições de corrida, **eles não resolvem totalmente o problema do deadlock**, que pode surgir da disputa por múltiplos recursos simultaneamente. Um monitor garante que o acesso a um único recurso encapsulado seja seguro, mas não impede, por si só, que dois processos fiquem presos aguardando reciprocamente recursos protegidos por monitores distintos — como no exemplo de P1/R1 e P2/R2 acima.

---

## Exercícios de fixação

1. Quais são os três componentes essenciais de um monitor?
2. Por que apenas um thread pode estar "dentro" de um monitor por vez?
3. O que caracteriza a "espera circular" em um deadlock? Descreva com um exemplo de dois processos e dois recursos.
4. Cite três consequências práticas de um deadlock em um sistema de produção.
5. Por que monitores, isoladamente, não eliminam o risco de deadlock?
6. Em um grafo de alocação de recursos, o que representam os retângulos e o que representam os círculos?

<details>
<summary>Gabarito</summary>

1. Dados privados (estado do recurso), procedimentos de acesso (operações permitidas) e fila de espera (gerenciamento dos threads aguardando acesso).
2. Porque a exclusão mútua do monitor é garantida por sua porta de entrada, que só libera a trava para um único thread por vez — isso impede condições de corrida sobre os dados privados encapsulados.
3. Espera circular ocorre quando um conjunto de processos forma um ciclo de dependência: cada processo retém um recurso que o próximo processo do ciclo necessita. Exemplo: P1 retém R1 e aguarda R2; P2 retém R2 e aguarda R1 — nenhum dos dois pode prosseguir, pois cada um espera por um recurso que o outro nunca libera.
4. Perda de trabalho acumulado (operações incompletas precisam ser reiniciadas); redução do rendimento geral do sistema (recursos ficam bloqueados, degradando o desempenho); falhas no sistema, exigindo reinicialização ou interrupção forçada por exaustão de recursos.
5. Porque um monitor garante exclusão mútua apenas sobre o recurso que ele próprio encapsula; quando processos distintos disputam múltiplos recursos protegidos por monitores diferentes, cada um esperando reciprocamente pelo recurso retido pelo outro, forma-se uma espera circular que nenhum monitor individual consegue detectar ou evitar sozinho.
6. Os retângulos representam os processos do sistema; os círculos representam os recursos disputados pelos processos. Setas de recurso para processo indicam alocação; setas de processo para recurso indicam requisição pendente.

</details>

## Material relacionado

- Diagramas desta aula: [Estrutura de um Monitor e seus componentes](diagramas/monitor-componentes-classes.svg) (diagrama de classes) · [Detecção de deadlock por espera circular](diagramas/deadlock-espera-circular-atividades.svg) (diagrama de atividades)
- [Aula: Organização e Gerenciamento da Memória Real](../Organizao%20e%20Gerenciamento%20da%20Memria%20Real/detalhes.md)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md)
- Slide original da aula: [`Monitores e Deadlock em Sistemas Operacionais.pptx`](./Monitores%20e%20Deadlock%20em%20Sistemas%20Operacionais.pptx)
