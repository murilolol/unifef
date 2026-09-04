# Aula 3 — Gerenciamento de Processo

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Tema:** Serviços do SO aos processos, despacho, quantum e as quatro transições de estado

## Objetivo da aula

Aprofundar o modelo de estados de processo apresentado na aula de Conceitos de Processos, detalhando os serviços que o sistema operacional presta a cada processo, o mecanismo de despacho, o papel do temporizador de intervalo (quantum) na preempção, e as quatro transições de estado possíveis em um modelo de três estados.

## Gerenciamento de processo

O sistema operacional intercala a execução de seus processos e deve gerenciá-los cuidadosamente para assegurar que não ocorra nenhum erro quando eles são interrompidos e retomados. Para isso, o SO presta certos serviços fundamentais a cada processo:

- Criar processos;
- Destruir processos;
- Suspender processos;
- Retomar processos;
- Alterar a prioridade de um processo;
- Bloquear processos;
- Acordar processos;
- Despachar processos.

## Estado de processo e estado de transição

### Despacho e o Despachante

Quando um programa é executado, processos são criados e inseridos na **lista de pronto**. Um processo avança nessa lista à medida que outros processos concluem sua vez de usar o processador. Quando um processo chega ao topo da lista e há um processador disponível, esse processo é designado ao processador — diz-se que ele fez uma **transição de estado**, passando do estado de **pronto** para o estado de **execução**.

O ato de designar um processador ao primeiro processo da lista de pronto é chamado de **DESPACHO**, e é realizado por uma entidade chamada **DESPACHANTE**.

### Acordado × Adormecido

Os processos nos estados de **pronto** ou de **execução** estão **acordados**, porque disputam ativamente o tempo do processador. Já os processos no estado **bloqueado** estão **adormecidos**, pois não podem executar mesmo que um processador fique disponível — estão à espera da conclusão de algum evento externo.

### O relógio de interrupção (temporizador de intervalo)

Para evitar que qualquer processo monopolize o sistema — acidental ou maliciosamente —, o sistema operacional estabelece um **relógio de interrupção em hardware**, também chamado **temporizador de intervalo**. Esse temporizador permite que um processo execute durante um intervalo de tempo específico, o **quantum**.

Se o processo não devolver o processador voluntariamente antes que seu quantum se esgote, o relógio de interrupção gera uma interrupção, forçando o sistema operacional a retomar o controle do processador. Nesse momento, o SO muda o estado do processo de **execução** para **pronto**, e despacha o primeiro processo da lista de pronto, mudando o estado deste de pronto para execução.

### Bloqueio voluntário por E/S

Se um processo em execução iniciar uma operação de entrada/saída antes que seu quantum expire, ele precisará esperar a conclusão dessa operação antes de poder usar o processador novamente. Nesse caso, o processo **entrega voluntariamente** o processador — diz-se que ele **bloqueou a si mesmo**, ficando suspenso até a conclusão da operação de E/S.

Quando a operação de E/S (ou o evento pelo qual o processo aguardava) é concluída, o sistema operacional promove a transição do processo do estado de **bloqueado** para **pronto**.

## As quatro transições de estado

| Transição | Disparada por | Descrição |
| :--- | :--- | :--- |
| **Pronto → Execução** | Sistema operacional (despachante) | Despacho: o SO designa um processador ao primeiro processo da lista de pronto. |
| **Execução → Pronto** | Sistema operacional | O quantum do processo expira; o relógio de interrupção força a devolução do processador. |
| **Execução → Bloqueado** | Processo (usuário) | O processo requisita uma operação de E/S e entrega o processador voluntariamente. |
| **Bloqueado → Pronto** | Sistema operacional | Conclusão do evento pelo qual o processo esperava (ex.: fim da operação de E/S). |

O único estado de transição **iniciado pelo próprio processo de usuário** é o bloqueio (Execução → Bloqueado); as outras três transições são sempre disparadas pelo sistema operacional.

### Sistemas sem relógio de interrupção

Alguns sistemas operacionais mais antigos, executando em processadores sem relógio de interrupção, dependiam de que cada processo devolvesse voluntariamente o processador antes que outro pudesse executar. Esse modelo sem quantum é raramente usado em sistemas atuais, pois permite que processos monopolizem um processador — por exemplo, entrando em laço infinito ou simplesmente recusando-se a entregar o processador na hora certa.

---

## Exercícios de fixação

1. Como o sistema operacional impede que um processo monopolize um processador?
2. Qual a diferença entre processos que estão acordados e processos que estão adormecidos?
3. O que significa "despacho" no sistema operacional, e quem o realiza?
4. Quais estados de um processo são classificados como "acordado", e quais são classificados como "adormecido"?
5. Descreva, passo a passo, a transição de estado de um processo que estava em execução e precisou ler um arquivo grande do disco na metade do seu quantum.
6. Por que um sistema sem relógio de interrupção (quantum) é considerado inseguro para uso em produção atualmente?

<details>
<summary>Gabarito</summary>

1. Por meio de um relógio de interrupção em hardware (temporizador de intervalo) que concede a cada processo um quantum de tempo. Se o processo não devolver o processador voluntariamente antes que o quantum se esgote, o relógio gera uma interrupção que devolve o controle do processador ao sistema operacional, forçando a transição do processo de execução para pronto.
2. Processos acordados (pronto ou execução) disputam ativamente o tempo do processador; processos adormecidos (bloqueado) não disputam o processador, mesmo que ele esteja ocioso, pois aguardam a conclusão de um evento externo.
3. Despacho é o ato de designar um processador ao primeiro processo da lista de pronto, fazendo-o transitar de pronto para execução. É realizado pelo despachante, parte do sistema operacional.
4. Acordado: pronto e execução. Adormecido: bloqueado.
5. (1) O processo está em execução; (2) ao requisitar a leitura do disco, entrega voluntariamente o processador (única transição iniciada pelo usuário); (3) o processo transita de execução para bloqueado; (4) o SO realiza um chaveamento de contexto e despacha outro processo da lista de pronto; (5) ao concluir a leitura do disco, o SO promove o processo de bloqueado para pronto.
6. Porque, sem um mecanismo de preempção por hardware, um processo pode monopolizar o processador indefinidamente — por exemplo, ao entrar em laço infinito ou simplesmente se recusar a devolver o processador — impedindo que qualquer outro processo seja executado.

</details>

## Material relacionado

- Diagramas desta aula: [As quatro transições de estado de um processo](diagramas/quatro-transicoes-estado-atividades.svg) (diagrama de atividades)
- [Aula: Conceitos de Processos](../CONCEITOS%20DE%20PROCESSOS/detalhes.md) — introduz o modelo básico de três estados aprofundado nesta aula.
- [Aula: Blocos de Controle (PCB)](../BLOCOS%20DE%20CONTROLE/detalhes.md) — estrutura de dados que registra o estado de cada processo.
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md)
- Slide original da aula: [`AULA3 -GERENCIAMENTO_DE_PROCESSO.pdf`](./AULA3%20-GERENCIAMENTO_DE_PROCESSO.pdf)
