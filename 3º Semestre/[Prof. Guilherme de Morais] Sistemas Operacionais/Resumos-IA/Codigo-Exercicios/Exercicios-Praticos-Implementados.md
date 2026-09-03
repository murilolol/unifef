# 📘 Apostila Prática: Sistemas Operacionais
**Professor:** Prof. Guilherme de Morais  
**Módulo:** Gerenciamento de Processos, Blocos de Controle (PCB) e Comandos de Execução  

---

## 🎯 Apresentação
Esta apostila prática é baseada nas aulas do **Prof. Guilherme de Morais** sobre **Sistemas Operacionais**. O objetivo deste material é traduzir os conceitos teóricos de Gerenciamento de Processos, Estados, Transições, Blocos de Controle de Processos (PCB) e Troca de Contexto (*Context Switch*) em **códigos executáveis em linguagem C** (padrão POSIX/Linux), acompanhados de comentários detalhados e exercícios resolvidos baseados nos questionários das aulas.

---

## 🛠️ PARTE 1: Simulação em C do Gerenciamento de Processos e PCB

Para entender como o Sistema Operacional gerencia processos, criamos um programa em **Linguagem C** que simula a estrutura de um **Bloco de Controle de Processo (PCB)**, a **Tabela de Processos**, o **Quantum de Tempo** e a transição de estados (*Pronto, Execução, Bloquado*).

### Código Pronto para Rodar (`simulador_processos.c`)

```c
#ativa a biblioteca padrão de entrada e saída
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Definição dos Estados do Processo conforme a teoria do Prof. Guilherme
typedef enum {
    PRONTO,
    EXECUCAO,
    BLOQUEADO,
    FINALIZADO
} EstadoProcesso;

// Conversão do Enum para String para facilitar a visualização
const char* strEstado(EstadoProcesso e) {
    switch (e) {
        if (e == PRONTO) return "PRONTO";
        case EXECUCAO: return "EM EXECUCAO";
        case BLOQUEADO: return "BLOQUEADO (Aguardando E/S)";
        case FINALIZADO: return "FINALIZADO";
        default: return "DESCONHECIDO";
    }
}

// Estrutura do PCB (Process Control Block / Descritor de Processo)
typedef struct {
    int pid;                     // Process Identification Number
    int contador_programa;       // Endereço da próxima instrução
    int prioridade;              // Prioridade de escalonamento
    int quantum_restante;        // Tempo restante de processador (Quantum)
    int pid_pai;                 // Ponteiro para o processo-pai
    EstadoProcesso estado;       // Estado atual do processo
    char nome[30];
} PCB;

// Função para exibir a Tabela de Processos simulada
void exibirTabelaProcessos(PCB processos[], int n) {
    printf("\n==================== TABELA DE PROCESSOS (PCB) ====================\n");
    printf("PID \t NOME \t\t ESTADO \t\t PRIORIDADE \t QUANTUM\n");
    printf("-------------------------------------------------------------------\n");
    for (int i = 0; i < n; i++) {
        printf("%d \t %-10s \t %-20s \t %d \t\t %d\n", 
            processos[i].pid, 
            processos[i].nome, 
            strEstado(processos[i].estado), 
            processos[i].prioridade, 
            processos[i].quantum_restante);
    }
    printf("===================================================================\n\n");
}

int main() {
    printf("--- SIMULADOR DE GERENCIAMENTO DE PROCESSOS (S.O.) ---\n");
    printf("Docente: Prof. Guilherme de Morais\n\n");

    // Criando um cenário com 3 Processos na Tabela de Processos
    int total_processos = 3;
    PCB tabela[3];

    // Inicializando o Processo 1 (Pai)
    tabela[0].pid = 1;
    strcpy(tabela[0].nome, "Proc_Shell");
    tabela[0].contador_programa = 1004;
    tabela[0].prioridade = 2;
    tabela[0].quantum_restante = 4;
    tabela[0].pid_pai = 0; // Processo raiz
    tabela[0].estado = EXECUCAO; // Sendo executado agora

    // Inicializando o Processo 2 (Filho)
    tabela[1].pid = 2;
    strcpy(tabela[1].nome, "Proc_Navegador");
    tabela[1].contador_programa = 5020;
    tabela[1].prioridade = 1;
    tabela[1].quantum_restante = 4;
    tabela[1].pid_pai = 1;
    tabela[1].estado = PRONTO;

    // Inicializando o Processo 3
    tabela[2].pid = 3;
    strcpy(tabela[2].nome, "Proc_Compilador");
    tabela[2].contador_programa = 300;
    tabela[2].prioridade = 3;
    tabela[2].quantum_restante = 4;
    tabela[2].pid_pai = 1;
    tabela[2].estado = PRONTO;

    // Exibindo o estado inicial
    exibirTabelaProcessos(tabela, total_processos);

    // Simulação 1: O Quantum do Processo 1 expira (Interrupção de Hardware / Temporizador)
    printf("[SIMULAÇÃO] O Relógio de Interrupção esgotou o Quantum do Proc_Shell (PID 1).\n");
    printf("[S.O.] Salvando contexto no PCB e realizando Chaveamento de Contexto (Context Switch)...\n");
    
    tabela[0].estado = PRONTO;       // Execução -> Pronto
    tabela[1].estado = EXECUCAO;     // Pronto -> Execução (Despacho pelo Despachante)
    
    exibirTabelaProcessos(tabela, total_processos);

    // Simulação 2: O Processo 2 solicita uma operação de E/S (Entrada/Saída)
    printf("[SIMULAÇÃO] O Proc_Navegador (PID 2) iniciou uma leitura de disco (E/S).\n");
    printf("[S.O.] Processo bloqueia a si mesmo. Transição de EXECUÇÃO para BLOQUEADO.\n");
    
    tabela[1].estado = BLOQUEADO;    // Execução -> Bloqueado
    tabela[2].estado = EXECUCAO;     // O Despachante escolhe o próximo da lista de Prontos (Proc_Compilador)

    exibirTabelaProcessos(tabela, total_processos);

    // Simulação 3: Conclusão do evento de E/S do Processo 2
    printf("[SIMULAÇÃO] A operação de E/S do Proc_Navegador (PID 2) foi concluída (Interrupção de Hardware).\n");
    printf("[S.O.] Promovendo o processo de BLOQUEADO para PRONTO.\n");
    
    tabela[1].estado = PRONTO;       // Bloqueado -> Pronto

    exibirTabelaProcessos(tabela, total_processos);

    return 0;
}
```

---

## 📝 PARTE 2: Exercícios Resolvidos (Baseados nas Aulas)

### Seção 1: Gerenciamento de Processos e Estados

#### **Questão 1:** Como o sistema operacional impede que um processo monopolize um processador?
* **Resposta Baseada na Aula:** O Sistema Operacional utiliza um **relógio de interrupção em hardware** (também conhecido como temporizador de intervalo ou *timer*). Este relógio define um intervalo de tempo específico chamado **Quantum**. Se o processo em execução não devolver o processador voluntariamente antes do tempo expirar, o relógio gera uma interrupção de hardware, forçando o processador a transferir o controle para o núcleo do S.O. O S.O. então altera o estado do processo atual de *em execução* para *pronto* e despacha o próximo processo da lista.

#### **Questão 2:** Qual a diferença entre processos que estão acordados e processos que estão adormecidos?
* **Resposta Baseada na Aula:** 
  * **Processos Acordados:** Estão nos estados **Pronto** ou **Execução**. Eles disputam ativamente o tempo de processamento da CPU.
  * **Processos Adormecidos:** Estão no estado **Bloqueado**. Eles não podem ser executados mesmo se houver um processador disponível, pois estão aguardando a conclusão de um evento externo (como uma operação de Entrada/Saída).

#### **Questão 3:** O que se diz despacho no sistema operacional?
* **Resposta Baseada na Aula:** É o ato de designar um processador ao primeiro processo que se encontra no topo da lista de prontos. Essa transição faz o processo passar do estado *pronto* para o estado *em execução*. Essa tarefa é executada por uma entidade do S.O. chamada de **Despachante**.

#### **Questão 4:** Quais os estados de um processo que é denominado acordado, e quais os estados de um processo é denominado adormecido?
* **Resposta Baseada na Aula:** 
  * Processos **acordados**: Estados de **Pronto** e **Execução**.
  * Processos **adormecidos**: Estado **Bloqueado**.

---

### Seção 2: Blocos de Controle (PCB) e Interrupções

#### **Questão 1:** Como os processadores aceleram e simplificam o chaveamento de contexto? Explique.
* **Resposta Baseada na Aula:** Processadores modernos fornecem instruções de hardware especializadas dedicadas exclusivamente a **salvar e restaurar o contexto de execução** diretamente de e para o PCB (*Process Control Block*). Além disso, alguns chips possuem registradores internos dedicados em hardware que apontam diretamente para o PCB do processo que está executando no momento, otimizando o acesso frequente que o núcleo do S.O. precisa fazer a essas estruturas.

#### **Questão 2:** Qual a diferença entre interrupções assíncronas e síncronas?
* **Resposta Baseada na Aula:**
  * **Interrupções Síncronas (Exceções/Erros):** Ocorrem quando o próprio processo em execução tenta realizar uma ação ilegal (como divisão por zero) ou tenta acessar uma posição de memória protegida.
  * **Interrupções Assíncronas (Hardware):** Ocorrem quando um dispositivo externo de hardware muda de estado e envia um sinal ao processador de forma independente da instrução executada no momento (Ex: teclado, mouse, relógio de intervalo).

#### **Questão 3:** O que é PID e PCB?
* **Resposta Baseada na Aula:**
  * **PID (Process Identification Number):** É um número de identificação único atribuído pelo S.O. a cada processo criado para que possa ser gerenciado e distinguido dos demais.
  * **PCB (Process Control Block / Bloco de Controle de Processo):** É uma estrutura de dados (ou descritor) criada na memória pelo S.O. que armazena todas as informações vitais sobre o processo, incluindo seu estado, contador de programa, prioridade, registradores (contexto), ponteiro para o pai/filhos e arquivos abertos.

#### **Questão 4:** Fale sobre filiação de processos (Processos Pais e Filhos).
* **Resposta Baseada na Aula:** Durante a execução, um processo pode gerar novos processos. O criador é chamado de **Processo-Pai** e o criado de **Processo-Filho**, gerando uma estrutura hierárquica em árvore. A destruição de um processo pai pode variar dependendo do projeto do S.O.: em alguns sistemas, os filhos são destruídos automaticamente com o pai; em outros, os processos filhos continuam executando de forma independente.