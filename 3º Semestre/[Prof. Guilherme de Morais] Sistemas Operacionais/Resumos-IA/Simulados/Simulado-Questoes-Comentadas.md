Com base no material fornecido pelo **Prof. Guilherme de Morais** (disciplina de Sistemas Operacionais), preparei um simulado completo contendo 10 questões de múltipla escolha (com gabarito comentado) e 5 questões discursivas/estudos de caso práticos, focados nos tópicos de Gerenciamento de Processos, Blocos de Controle (PCB) e Interrupções.

---

# 📝 SIMULADO DE SISTEMAS OPERACIONAIS
**Professor:** Prof. Guilherme de Morais

---

## PARTE 1: Questões de Múltipla Escolha

### Questão 1
O ato de designar um processador ao primeiro processo da lista de pronto é uma tarefa fundamental do Sistema Operacional. Como é denominada essa entidade/ação?
A) Chaveamento de contexto.
B) Despacho (Despachante).
C) Polling (Sondagem).
D) Tratamento de interrupção.
E) Quantum de tempo.

### Questão 2
Para evitar que processos monopolizem o processador de forma maliciosa ou acidental (como em um laço infinito), o Sistema Operacional utiliza um mecanismo de hardware. Qual é o nome desse mecanismo?
A) Bloco de Controle de Processo (PCB).
B) Tabela de Processos.
C) Relógio de interrupção em hardware (Temporizador de intervalo).
D) Ponteiro de Processo-pai.
E) Identificador de Processo (PID).

### Questão 3
Em relação aos estados de um processo no modelo apresentado, assinale a alternativa correta sobre as transições de estado:
A) O único estado de transição iniciado pelo próprio processo de usuário é o **despacho**.
B) Quando o quantum de um processo expira, ele transita do estado de *execução* para o estado de *bloqueado*.
C) Processos nos estados de pronto ou de execução são considerados *adormecidos*.
D) Quando um processo acorda devido à conclusão de um evento, ele transita de *bloqueado* para *pronto*.
E) A transição de *execução* para *pronto* ocorre quando o processo inicia voluntariamente uma operação de E/S.

### Questão 4
O que acontece com um processo que está executando e decide iniciar uma operação de entrada/saída (E/S) antes que o seu quantum expire?
A) Ele é imediatamente destruído pelo Sistema Operacional.
B) Ele continua executando em paralelo com a operação de E/S.
C) Ele entrega voluntariamente o processador e bloqueia a si mesmo, aguardando a conclusão da E/S.
D) Ele transita diretamente para o final da lista de prontos.
E) Ele gera uma interrupção síncrona ilegal.

### Questão 5
O que é armazenado no Bloco de Controle de Processo (PCB) quando o Sistema Operacional realiza um chaveamento de contexto de um processo que estava em execução?
A) Apenas o número de identificação (PID).
B) O contexto de execução (conteúdo dos registradores) e o contador de programa.
C) Somente os ponteiros para os processos-filhos.
D) A lista completa de todos os arquivos do disco rígido.
E) O código-fonte original do programa executado.

### Questão 6
Sobre a filiação de processos (processo-pai e processo-filho), assinale a alternativa correta de acordo com o material:
A) A criação de um processo gera uma estrutura hierárquica.
B) Processos-filhos são sempre destruídos instantaneamente quando realizam operações de E/S.
C) O processo-pai não possui ponteiros no PCB para identificar seus filhos.
D) A destruição de um processo é simples e nunca envolve a liberação de memória.
E) Em todos os sistemas operacionais, os filhos são obrigatoriamente destruídos junto com o pai.

### Questão 7
O que caracteriza uma **interrupção síncrona**?
A) Quando um dispositivo de hardware (como teclado ou mouse) muda de estado e avisa o processador.
B) Quando o processador realiza varreduras repetitivas nos dispositivos de E/S (*polling*).
C) Quando um processo tenta realizar uma ação ilegal ou se referir a uma localização de memória protegida.
D) Quando o temporizador de intervalo (quantum) expira por hardware.
E) Quando dois processos entram em conflito por um recurso compartilhado.

### Questão 8
Historicamente, sistemas operacionais mais antigos utilizavam uma abordagem em que os processadores pesquisavam repetidamente o estado de cada dispositivo para saber se havia algo a relatar. Como era chamada essa técnica?
A) Chaveamento de contexto.
B) Sondagem (*Polling*).
C) Tratamento de interrupção assíncrona.
D) Despacho por quantum.
E) Hierarquia de processos.

### Questão 9
Sobre o **Chaveamento de Contexto**, o núcleo do Sistema Operacional realiza uma série de passos. Qual é a sequência lógica inicial para interromper um processo em execução e iniciar outro?
A) Carregar o contexto do novo processo antes de salvar o anterior.
B) Destruir o processo atual e criar um novo PCB.
C) Salvar o contexto de execução do processo em execução no seu PCB e, em seguida, carregar o contexto do próximo processo pronto.
D) Mudar o processo de execução diretamente para o estado bloqueado.
E) Aguardar o término de todas as interrupções assíncronas pendentes.

### Questão 10
Qual das alternativas abaixo **NÃO** faz parte das informações comumente encontradas em um Bloco de Controle de Processo (PCB)?
A) Estado de um processo (pronto, execução, bloqueado).
B) Contador de programa.
C) Credenciais de acesso a recursos.
D) Código-fonte completo da aplicação em linguagem de alto nível.
E) Ponteiro para o Processo-pai.

---

## 🔍 Gabarito Comentado (Múltipla Escolha)

* **Questão 1: B**
  * *Comentário:* O ato de designar o processador ao primeiro da lista de pronto chama-se **despacho**, realizado pela entidade **despachante**.
* **Questão 2: C**
  * *Comentário:* O SO utiliza o **relógio de interrupção em hardware** (ou temporizador de intervalo) para limitar o quantum de execução e evitar que um processo monopolize a CPU.
* **Questão 3: D**
  * *Comentário:* Quando o evento pelo qual um processo espera (como E/S) é concluído, o SO promove sua transição de *bloqueado* para *pronto*. O bloqueio é a única transição iniciada pelo usuário; estouro de quantum vai de execução para pronto; prontos e executando estão acordados.
* **Questão 4: C**
  * *Comentário:* Se um processo precisa esperar por uma E/S antes do fim do quantum, ele desiste voluntariamente da CPU e se bloqueia.
* **Questão 5: B**
  * *Comentário:* O PCB armazena o contexto de execução (registradores) e o contador de programa para permitir a retomada futura do processo.
* **Questão 6: A**
  * *Comentário:* O processo criador é o pai e o criado é o filho, gerando uma estrutura estritamente hierárquica.
* **Questão 7: C**
  * *Comentário:* Interrupções síncronas ocorrem por ações internas do processo (ex: violação de memória ou instrução ilegal). Dispositivos externos geram interrupções assíncronas.
* **Questão 8: B**
  * *Comentário:* O teste repetitivo de dispositivos era chamado de *polling* (sondagem), técnica ineficiente substituída por interrupções.
* **Questão 9: C**
  * *Comentário:* Para trocar de processo, o núcleo primeiro salva o contexto atual no PCB de origem e depois carrega o contexto do PCB de destino.
* **Questão 10: D**
  * *Comentário:* O PCB guarda metadados de controle do SO (registradores, estado, ponteiros, prioridade), e não o código-fonte do programa.

---

## PARTE 2: Questões Discursivas e Estudos de Caso Práticos

### Questão 1 (Discursiva)
**Explique detalhadamente como o Temporizador de Intervalo (Relógio de Interrupção) atua para garantir que nenhum processo monopolize o processador.**

> *Gabarito/Resolução Esperada:* O temporizador de intervalo concede um tempo específico de execução chamado *quantum* a cada processo. Caso o processo execute até estourar esse tempo sem devolver a CPU voluntariamente, o hardware gera uma interrupção. Isso força o Sistema Operacional a retomar o controle do processador, rebaixando o processo atual do estado de "execução" para "pronto", e despachando o próximo processo da fila.

---

### Questão 2 (Estudo de Caso Prático)
**Um processo em execução precisa ler um arquivo grande do disco rígido (operação de Entrada/Saída) na metade do seu quantum alocado. Descreva passo a passo o comportamento do processo, as mudanças de estado e o papel do Sistema Operacional nessa situação.**

> *Gabarito/Resolução Esperada:* 
> 1. O processo está no estado de **execução**.
> 2. Ao requisitar a E/S, ele percebe que não pode prosseguir sem o dado do disco.
> 3. O processo entrega voluntariamente o processador (única transição de usuário).
> 4. Ocorre a transição de estado de **execução** para **bloqueado**. O processo fica adormecido.
> 5. O SO realiza um chaveamento de contexto para colocar outro processo pronto na CPU.
> 6. Quando a operação de E/S é concluída, o SO acorda o processo, promovendo sua transição de **bloqueado** para **pronto**, inserindo-o na lista de prontos para ser despachado novamente no futuro.

---

### Questão 3 (Discursiva)
**O que é o Bloco de Controle de Processo (PCB) e qual é a sua importância crítica para a realização bem-sucedida de um chaveamento de contexto (*context switch*)?**

> *Gabarito/Resolução Esperada:* O PCB (Process Control Board ou Descritor de Processo) é a estrutura de dados onde o SO armazena todas as informações vitais para gerenciar um processo (PID, estado, ponteiros pai/filho, prioridade, contador de programa e registradores). Sua importância no chaveamento de contexto reside no fato de que, ao interromper um processo, o núcleo precisa salvar seu estado atual exato (contexto de execução) dentro do PCB. Posteriormente, ao retomar o processo, o SO lê esses dados do PCB para restaurar perfeitamente o estado anterior do processador.

---

### Questão 4 (Discursiva)
**Diferencie Interrupções Síncronas de Interrupções Assíncronas, fornecendo exemplos práticos citados no material de aula.**

> *Gabarito/Resolução Esperada:* 
> * **Interrupções Síncronas:** Ocorrem internamente quando um processo tenta realizar uma ação ilegal ou acessar uma posição de memória protegida.
> * **Interrupções Assíncronas:** Ocorrem quando um dispositivo de hardware externo muda de estado e comunica o processador de forma assíncrona. Exemplos clássicos citados: eventos gerados pelo teclado ou pelo mouse.

---

### Questão 5 (Estudo de Caso Prático / Comparativo)
**Explique por que os sistemas operacionais modernos abandonaram a técnica de *Polling* (Sondagem) em favor de arquiteturas orientadas a interrupções. Quais são os riscos associados a sistemas baseados em interrupções?**

> *Gabarito/Resolução Esperada:* No *polling*, o processador gastava ciclos valiosos pesquisando repetidamente o estado dos dispositivos de E/S para ver se havia algo a fazer, o que se torna extremamente ineficiente e inviável à medida que o sistema cresce em complexidade. As interrupções eliminam essa sondagem, permitindo que o processador foque em tarefas úteis e só atenda aos dispositivos quando solicitado. Contudo, o risco dos sistemas orientados a interrupções é a sobrecarga (*overload*) caso as interrupções cheguem em um volume massivo e muito rápido (como no exemplo de tráfego aéreo), fazendo com que o sistema operacional não consiga processá-las a tempo.