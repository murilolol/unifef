# Aula — Blocos de Controle de Processo (PCBs)

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Tema:** PID, PCB, tabela de processos, filiação, chaveamento de contexto e interrupções

## Objetivo da aula

Compreender a estrutura de dados que o sistema operacional utiliza para gerenciar cada processo — o Bloco de Controle de Processo (PCB) —, entender como o SO organiza os PCBs em uma tabela de processos, como ocorre a filiação (hierarquia) entre processos, o mecanismo de chaveamento de contexto e a diferença entre interrupções síncronas e assíncronas.

## Identificação do processo: PID

O sistema operacional normalmente executa diversas operações quando cria um processo. A primeira delas é ser capaz de identificar cada processo de forma única; para isso, o SO designa a cada processo um **número de identificação de processo** (*Process Identification Number* — **PID**).

## O Bloco de Controle de Processo (PCB)

Em seguida, o sistema operacional cria um **Bloco de Controle de Processo** (*Process Control Block* — **PCB**), também denominado **descritor de processo**, que mantém as informações que o SO necessita para gerenciar cada processo. Os PCBs comumente incluem:

- **Estado do processo** (em execução, pronto ou bloqueado);
- **Contador de programa** — valor que determina qual instrução o processo deve executar em seguida;
- **Prioridade de escalonamento**;
- **Credenciais** — dados que determinam quais recursos aquele processo pode acessar;
- **Ponteiro para o processo-pai** (o processo que criou este processo);
- **Ponteiro(s) para processos-filhos** (processos criados por este processo), caso existam.

O PCB também armazena o conteúdo dos registradores — denominado **contexto de execução** — do processador no qual o processo estava sendo executado da última vez em que saiu do estado de execução. Isso permite ao sistema operacional restaurar exatamente o contexto de execução de um processo quando ele retorna ao estado de execução.

### Operações sobre um processo

O sistema operacional oferece um conjunto padrão de operações sobre processos:

- Criar um processo;
- Destruir um processo;
- Suspender um processo;
- Retomar (retornar) um processo;
- Alterar a prioridade de um processo;
- Bloquear um processo;
- Acordar um processo;
- Despachar um processo;
- Habilitar um processo a se comunicar com outro (**comunicação interprocessos** — IPC).

## Tabela de processos

O sistema operacional organiza os PCBs em uma **Tabela de Processos**, estrutura que relaciona cada PID ao seu respectivo PCB. Cada entrada da tabela aponta para um bloco de controle contendo contador de programa, registradores, estado, prioridade, ponteiros para pai e filhos, arquivos abertos e outras sinalizações — permitindo ao SO localizar rapidamente qualquer processo em execução no sistema.

## Filiação de processos

Um processo pode gerar um novo processo. O processo criador é denominado **processo-pai**, e o processo criado é denominado **processo-filho**. Essa criação sucessiva leva a uma **estrutura hierárquica** (uma árvore de processos), em que cada processo pode ter múltiplos filhos, e cada filho pode, por sua vez, gerar seus próprios filhos.

### Destruição de um processo

Destruir um processo envolve liberar o processador, a memória e outros dispositivos que estavam alocados a ele. A destruição de um processo é mais complexa quando ele gerou outros processos: em alguns sistemas operacionais, cada processo-filho é destruído automaticamente quando seu pai é destruído (destruição em cascata); em outros, os processos-filhos prosseguem de forma independente, e a destruição do pai não tem qualquer efeito sobre eles.

## Chaveamento de contexto

O sistema operacional realiza um **chaveamento de contexto** (*context switch*) para interromper um processo em execução e iniciar a execução de um processo previamente pronto. Para isso, o núcleo (kernel) deve:

1. Salvar o contexto de execução do processo atualmente em execução no seu PCB;
2. Carregar o contexto de execução do próximo processo pronto.

Como o sistema operacional acessa os PCBs com muita frequência, alguns processadores otimizam esse acesso mantendo, em hardware, um registrador próprio que indica o PCB do processo atualmente em execução, facilitando o chaveamento de contexto. Processadores modernos vão além, fornecendo instruções dedicadas que salvam e restauram automaticamente o contexto de execução de/para o PCB de um processo, acelerando ainda mais essa operação crítica.

## Interrupções

O sistema operacional pode especificar um conjunto de instruções — o **tratador de interrupção** — a ser executado em resposta a cada tipo de interrupção. Isso permite ao SO retomar o controle do processador sempre que necessário para gerenciar recursos do sistema.

| Tipo | Definição | Exemplo |
| :--- | :--- | :--- |
| **Síncrona** | O próprio processo tenta realizar uma ação ilegal ou referenciar uma localização de memória protegida. | Divisão por zero, acesso indevido a memória. |
| **Assíncrona** | Um dispositivo de hardware muda de estado e comunica o processador, independentemente da instrução em curso. | Teclado, mouse, temporizador. |

Interrupções são o meio de baixo custo de obter a atenção do processador. Em sistemas operacionais mais antigos, os processadores pesquisavam repetidamente (**sondagem** ou *polling*) o estado de cada dispositivo — abordagem cujo custo cresce junto com a complexidade do sistema de computador. Interrupções síncronas e assíncronas eliminam essa necessidade de sondagem repetida (o exemplo clássico é o forno de micro-ondas, que reage por interrupção, não por sondagem contínua).

Sistemas orientados a interrupções também podem ficar sobrecarregados se as interrupções chegarem rápido demais para que o sistema consiga acompanhá-las — analogia clássica: o tráfego aéreo, em que uma torre de controle sobrecarregada de sinais não consegue processar todos a tempo.

---

## Exercícios de fixação

1. Como os processadores aceleram e simplificam o chaveamento de contexto?
2. Qual a diferença entre interrupções assíncronas e síncronas?
3. O que são PID e PCB?
4. Explique a filiação de processos (processo-pai e processo-filho).
5. Por que a destruição de um processo é mais complicada quando ele gerou processos-filhos?

<details>
<summary>Gabarito</summary>

1. Fornecendo instruções de hardware dedicadas para salvar e restaurar diretamente o contexto de execução do/para o PCB, e mantendo, em alguns processadores, um registrador que aponta diretamente para o PCB do processo em execução, evitando buscas adicionais na tabela de processos.
2. Interrupções **síncronas** são causadas pelo próprio processo em execução, ao tentar uma ação ilegal ou acessar memória protegida; interrupções **assíncronas** são causadas por um dispositivo de hardware externo (teclado, mouse, temporizador) que muda de estado e sinaliza o processador, independentemente do que este estava executando.
3. **PID** é o número único que o sistema operacional atribui a cada processo para identificá-lo. **PCB** é a estrutura de dados (bloco de controle de processo) que armazena todas as informações necessárias para o SO gerenciar aquele processo: estado, contador de programa, prioridade, credenciais, ponteiros de filiação e contexto de execução.
4. O processo que cria um novo processo é chamado de processo-pai; o processo criado é o processo-filho. Sucessivas criações formam uma estrutura hierárquica em árvore, em que cada processo pode ter vários filhos.
5. Porque o sistema operacional precisa decidir o que fazer com os processos-filhos: em alguns SOs, eles são destruídos automaticamente junto com o pai (cascata); em outros, prosseguem de forma independente, exigindo que o SO reorganize seus ponteiros de filiação sem afetar sua execução.

</details>

## Material relacionado

- Diagramas desta aula: [Estrutura do PCB e Tabela de Processos](diagramas/pcb-tabela-processos-classes.svg) (diagrama de classes) · [Fluxo do chaveamento de contexto](diagramas/chaveamento-contexto-atividades.svg) (diagrama de atividades)
- [Aula: Conceitos de Processos](../CONCEITOS%20DE%20PROCESSOS/detalhes.md)
- [Aula: Aula 3 — Gerenciamento de Processo](../AULA3%20-GERENCIAMENTO_DE_PROCESSO/detalhes.md)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md)
- Slide original da aula: [`AULA4- blocos de controle.pdf`](./AULA4-%20blocos%20de%20controle.pdf)
