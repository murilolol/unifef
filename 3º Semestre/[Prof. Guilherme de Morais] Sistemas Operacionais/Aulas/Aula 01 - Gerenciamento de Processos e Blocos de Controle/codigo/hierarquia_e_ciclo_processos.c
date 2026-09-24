/*
 * Disciplina: Sistemas Operacionais
 * Professor: Guilherme de Morais
 * Tema: Filiação de Processos, Hierarquia Pai-Filho, PIDs e Ciclo de Destruição (Zumbis e Órfãos)
 * Como compilar: gcc -std=c11 -pthread hierarquia_e_ciclo_processos.c -o hierarquia_e_ciclo_processos
 * Como executar: ./hierarquia_e_ciclo_processos
 *
 * Descricao:
 * Este programa ilustra a arvore genealogica de processos no padrao POSIX:
 * 1. Processo-Pai cria dois Processos-Filhos (Filho A e Filho B) usando fork().
 * 2. O Filho A cria um Processo-Neto (recursao de filiacao em arvore).
 * 3. Inspecao dos PIDs e PPIDs (Parent PID) para mapear a linhagem hierarquica.
 * 4. Demonstracao de Processo Zumbi: O filho termina, mas o pai ainda nao fez o wait().
 * 5. Demonstracao de Processo Orfao: O pai termina antes do filho, e o sistema adota o orfao (PID 1).
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>

int main(void) {
    printf("========================================================================\n");
    printf("       ARVORE HIERARQUICA DE PROCESSOS E GERENCIAMENTO DE FILIACAO       \n");
    printf("========================================================================\n\n");

    pid_t pid_pai = getpid();
    printf("[PROCESSO-RAIZ / PAI INICIAL] PID: %d, PPID (Shell/Terminal): %d\n\n", pid_pai, getppid());

    // 1. Criacao do primeiro filho (Filho A)
    pid_t pid_filho_a = fork();

    if (pid_filho_a < 0) {
        perror("Falha na chamada fork() para Filho A");
        exit(EXIT_FAILURE);
    }

    if (pid_filho_a == 0) {
        // ------------------ RAMIFICACAO DO FILHO A ------------------
        printf(" [FILHO A] Criado com sucesso! PID: %d, PPID: %d\n", getpid(), getppid());
        
        // Filho A decide gerar um Processo-Neto
        pid_t pid_neto = fork();

        if (pid_neto < 0) {
            perror("Falha na criacao do neto");
            exit(EXIT_FAILURE);
        }

        if (pid_neto == 0) {
            // Codigo exclusivo do Processo-Neto
            printf("   [NETO] Instanciado! PID: %d, Meu Pai (Filho A): %d, Meu Avo: %d\n",
                   getpid(), getppid(), pid_pai);
            sleep(1);
            printf("   [NETO] Concluindo tarefa e saindo com codigo 42.\n");
            exit(42);
        } else {
            // Filho A aguarda a conclusao do neto (previne geracao de zumbi na sub-arvore)
            int status_neto;
            waitpid(pid_neto, &status_neto, 0);
            if (WIFEXITED(status_neto)) {
                printf(" [FILHO A] O neto (PID: %d) encerrou com status de saida: %d\n",
                       pid_neto, WEXITSTATUS(status_neto));
            }
            printf(" [FILHO A] Finalizando minha execucao.\n");
            exit(10);
        }
    }

    // 2. Criacao do segundo filho (Filho B) para demonstrar adocao de orfao
    pid_t pid_filho_b = fork();

    if (pid_filho_b < 0) {
        perror("Falha na chamada fork() para Filho B");
        exit(EXIT_FAILURE);
    }

    if (pid_filho_b == 0) {
        // ------------------ RAMIFICACAO DO FILHO B ------------------
        printf(" [FILHO B] Criado com sucesso! PID: %d, PPID Original: %d\n", getpid(), getppid());
        printf(" [FILHO B] Vou dormir por 3 segundos para que o pai morra antes de mim...\n");
        sleep(3);
        
        // Quando acordar, o pai original ja tera terminado. Quem sera o novo pai?
        printf(" [FILHO B] Acordei! Verificando meu novo pai (PPID): %d\n", getppid());
        printf(" [FILHO B] Fui adotado pelo processo de inicializacao do sistema (init/systemd)!\n");
        exit(20);
    }

    // ------------------ CODIGO DO PROCESSO-PAI RAIZ ------------------
    printf("[PAI] Criei dois ramos: Filho A (PID: %d) e Filho B (PID: %d)\n", pid_filho_a, pid_filho_b);

    // O Pai coleta o Filho A imediatamente com waitpid()
    int status_a;
    printf("[PAI] Aguardando a conclusao estrita do Filho A...\n");
    waitpid(pid_filho_a, &status_a, 0);
    if (WIFEXITED(status_a)) {
        printf("[PAI] Filho A concluiu com codigo: %d. Estruturas liberadas da tabela de processos.\n",
               WEXITSTATUS(status_a));
    }

    // Demonstracao de politica de destruicao:
    // O Pai decide terminar deliberadamente SEM esperar o Filho B.
    // O Filho B torna-se orfao e sobrevive de forma independente ate ser adotado.
    printf("[PAI] Encerrando o processo-pai propositalmente antes do Filho B.\n");
    printf("[PAI] O Filho B entrara no estado de Orfao e sera transferido ao PID ancestral.\n");

    return 0;
}
