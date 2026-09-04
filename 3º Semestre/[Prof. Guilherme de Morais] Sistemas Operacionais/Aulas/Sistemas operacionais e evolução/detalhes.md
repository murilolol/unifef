# Aula — Sistemas Operacionais: Evolução e Arquiteturas

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Tema:** Da simbiose hardware-software às estratégias de paralelismo moderno

## Objetivo da aula

Compreender como a evolução do hardware moldou a complexidade dos sistemas operacionais, distinguir os conceitos de Job, Processo e Thread, classificar os sistemas operacionais quanto ao número de programas e usuários simultâneos, e entender as estratégias de multiprogramação e escalabilidade (acoplamento forte e fraco) que sustentam a computação de alto desempenho.

## Sumário da aula

1. Simbiose hardware-software
2. Jobs, Processos e Threads
3. Classificação dos sistemas operacionais
4. Multiprogramação: eficiência e desempenho
5. Escalabilidade: acoplamento e Lei de Amdahl

---

## 1. Simbiose hardware-software

A evolução do hardware — de válvulas a transistores, e destes aos processadores multinúcleo atuais — impulsionou sistemas operacionais cada vez mais complexos. Essa interdependência intrínseca entre hardware e software é o que define a computação moderna: novos recursos de hardware (mais núcleos, memória, dispositivos) só se tornam úteis quando o sistema operacional evolui para gerenciá-los.

## 2. Jobs, Processos e Threads

| Conceito | Definição |
| :--- | :--- |
| **Job** | Bloco de trabalho bruto a ser executado sequencialmente pelo sistema (herança dos sistemas em lote). |
| **Processo** | Instância de um programa em execução, com recursos dedicados e isolamento de memória. |
| **Thread** | Unidade de execução dentro de um processo, permitindo concorrência e paralelismo intrínseco ao próprio processo. |

A transição de execução puramente sequencial (Jobs) para Processos e Threads foi o que permitiu ao sistema operacional oferecer paralelismo lógico — a ilusão de que várias tarefas avançam ao mesmo tempo, mesmo em uma única CPU.

## 3. Classificação dos sistemas operacionais

A classificação dos SOs é importante para justificar a escolha ideal em cada cenário: um ambiente de IoT, um servidor de alto desempenho e um desktop pessoal exigem características e otimizações distintas. Essa diferenciação orienta a alocação eficaz de recursos.

### 3.1. Quanto ao número de programas: monoprogramável × multiprogramável

**Sistemas monoprogramáveis (monotarefa).** Apenas um programa é executado por vez, em modelo estritamente sequencial, com alocação exclusiva de todos os recursos do sistema a essa única tarefa.

- **O gargalo da ociosidade:** em sistemas monoprogramáveis, a UCP permanece inativa durante operações de E/S (entrada/saída), pois não há outro programa para ocupar o processador nesse intervalo. Isso gera desperdício de ciclos de clock e limita a produtividade — a velocidade da UCP fica subutilizada por depender de periféricos lentos.
- **Relevância atual:** embora a multitarefa domine os sistemas modernos, a arquitetura monotarefa persiste deliberadamente em nichos especializados — microcontroladores e sistemas embarcados simples — como escolha de design para baixo custo e alta eficiência energética.

**Sistemas multiprogramáveis (multitarefa).** Representam o salto de eficiência computacional ao permitir simultaneidade (real ou ilusória) entre processos, otimizando o uso de recursos:

- **Time-sharing:** o SO aloca fatias de tempo (*quantum*) da CPU para vários processos, alternando-os rapidamente.
- **Gerência de memória:** o SO organiza o espaço de memória para evitar conflitos entre os dados e o código de diferentes programas.
- **Virtualização:** cria ambientes lógicos isolados para cada processo ou usuário.
- **Interrupções e preempção:** mecanismos essenciais para o controle do tempo de CPU — permitem que o SO suspenda um processo em execução para alocar o processador a outro, garantindo multitarefa eficiente.
- **Acesso concorrente:** o gerenciamento de acesso concorrente previne *race conditions* (condições de corrida), assegurando que operações críticas sobre dados compartilhados sejam executadas de forma atômica, preservando a consistência dos dados.

**Turnaround e throughput.** Dois indicadores centrais de desempenho da multiprogramação:

| Métrica | O que mede | Benefício |
| :--- | :--- | :--- |
| **Turnaround** | Tempo total de processamento de uma tarefa. | Redução melhora a eficiência da execução. |
| **Throughput** | Vazão — quantidade de tarefas processadas por unidade de tempo. | Aumento maximiza o uso da CPU. |

A multiprogramação também traz vantagem econômica: ao compartilhar recursos entre múltiplos processos, reduz custos operacionais ao otimizar o desempenho do sistema como um todo.

### 3.2. Quanto ao número de usuários: monousuário × multiusuário

| Tipo | Características | Exemplos |
| :--- | :--- | :--- |
| **Monousuário** | Focado em um único usuário; permite multitarefa para otimizar a experiência individual. | Windows 11 Desktop, onde múltiplos processos executam em um só contexto de usuário. |
| **Multiusuário** | Projetado para atender múltiplos usuários simultaneamente, cada qual com seu ambiente e recursos dedicados. | Servidores Linux e Mainframes, que gerenciam a concorrência de acessos eficientemente. |

## 4. Sistemas de multiprocessamento

Sistemas de multiprocessamento empregam duas ou mais UCPs (Unidades Centrais de Processamento) que compartilham barramento, relógio e, frequentemente, memória principal. Isso permite **paralelismo real**, em contraste com a concorrência (paralelismo lógico) obtida em sistemas de UCP única.

### Pilares do multiprocessamento

- **Escalabilidade intrínseca:** ganho de performance proporcional à adição de recursos de hardware — princípio fundamental para o crescimento do poder computacional.
- **Disponibilidade contínua:** tolerância a falhas por meio de degradação graciosa, minimizando interrupções e mantendo a operabilidade do sistema.
- **Balanceamento de carga:** distribuição eficiente das requisições entre os processadores (*load balancing*), evitando sobrecargas e gargalos.

O multiprocessamento é essencial para aplicações de alto desempenho — simulações climáticas de alta resolução, prospecção de petróleo, renderização cinematográfica — que exigem poder computacional massivo.

## 5. Escalabilidade: acoplamento forte e fraco

| Atributo | Fortemente acoplado (SMP) | Fracamente acoplado (Clusters) |
| :--- | :--- | :--- |
| Memória | Compartilhada | Distribuída |
| Latência | Baixa | Alta |
| Escalabilidade | Limitada | Alta |
| Sistema Operacional | Único | Múltiplos |

**Sistemas fortemente acoplados (SMP — Multiprocessamento Simétrico):** todos os processadores acessam uma única memória física compartilhada, simplificando o modelo de programação e garantindo baixa latência na comunicação interprocessador, já que o acesso à memória comum é direto. Múltiplos processadores idênticos compartilham inclusive o próprio sistema operacional.

**Sistemas fracamente acoplados (clusters):** cada nó possui sua própria memória distribuída, comunicando-se via troca de mensagens (*message passing*) através da rede. Essa arquitetura facilita a expansão geográfica e modular, sendo a base de data centers modernos.

### Lei de Amdahl e o fim da Lei de Moore

Com o esgotamento da Lei de Moore (o ritmo histórico de miniaturização e barateamento dos transistores), a discussão sobre acoplamento forte *versus* sistemas distribuídos torna-se central para o desenvolvedor: a **Lei de Amdahl** demonstra que a escalabilidade obtida por paralelismo é limitada pela porção estritamente serial (não paralelizável) do código — por mais processadores que se adicionem, o trecho sequencial do algoritmo impõe um teto de desempenho que hardware adicional sozinho não resolve.

---

## Exercícios de fixação

1. Por que a UCP permanece ociosa durante operações de E/S em um sistema monoprogramável, e por que isso é um problema de desempenho?
2. Diferencie Job, Processo e Thread.
3. Explique a diferença entre filtro de escalonamento por *time-sharing* e execução puramente sequencial.
4. Um sistema precisa executar simulações climáticas de altíssima resolução, com múltiplas CPUs compartilhando a mesma memória física. Que tipo de arquitetura de acoplamento é mais adequado? Justifique.
5. Um sistema precisa escalar horizontalmente adicionando novos servidores geograficamente distribuídos, cada um com seu próprio SO. Que tipo de acoplamento é esse?
6. O que a Lei de Amdahl afirma sobre os limites da paralelização?
7. Cite dois exemplos de sistemas monousuário e dois de sistemas multiusuário, justificando a classificação.

<details>
<summary>Gabarito</summary>

1. Porque, em um sistema monoprogramável, apenas um programa pode ocupar o processador por vez; quando esse programa aguarda uma operação de E/S (tipicamente muito mais lenta que a CPU), não há outro processo para ocupar o processador enquanto isso, resultando em ciclos de clock desperdiçados e baixa produtividade.
2. Job é um bloco de trabalho bruto executado sequencialmente (modelo de sistemas em lote); Processo é uma instância de um programa em execução, com recursos e memória isolados; Thread é uma unidade de execução dentro de um processo, permitindo concorrência/paralelismo interno a esse processo.
3. *Time-sharing* aloca fatias de tempo da CPU entre múltiplos processos, criando a ilusão de simultaneidade (multiprogramação); execução sequencial processa um único programa do início ao fim antes de iniciar o próximo, sem intercalação.
4. Arquitetura fortemente acoplada (SMP), pois a memória compartilhada garante baixa latência de comunicação entre processadores, essencial para problemas de simulação massivamente paralelos que dependem de dados comuns.
5. Arquitetura fracamente acoplada (cluster), com memória distribuída e comunicação via *message passing* pela rede.
6. Que o ganho de desempenho obtido ao paralelizar um algoritmo é limitado pela fração do código que permanece inerentemente serial — adicionar mais processadores não elimina esse limite superior de escalabilidade.
7. Monousuário: Windows 11 Desktop, macOS em uma estação de trabalho pessoal. Multiusuário: servidores Linux, Mainframes — sistemas que atendem múltiplos usuários concorrentes, cada um com ambiente e recursos próprios.

</details>

## Material relacionado

- Diagramas desta aula: [Job × Processo × Thread e classificação dos SOs](diagramas/hierarquia-job-processo-thread-classes.svg) (diagrama de classes) · [Fluxo de decisão em sistema multiprogramável](diagramas/fluxo-multiprogramacao-atividades.svg) (diagrama de atividades)
- [Aula: Conceitos de Processos](../CONCEITOS%20DE%20PROCESSOS/detalhes.md)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md)
- Slide original da aula: [`Sistemas Operacionais Evoluo e Arquiteturas.pptx`](./Sistemas%20Operacionais%20Evoluo%20e%20Arquiteturas.pptx)
