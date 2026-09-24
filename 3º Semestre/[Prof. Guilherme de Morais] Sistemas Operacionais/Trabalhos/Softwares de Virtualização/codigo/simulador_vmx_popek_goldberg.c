/*
 * ============================================================================
 * DISCIPLINA : Sistemas Operacionais (3º Semestre) - UniFEF
 * PROFESSOR  : Me. Guilherme de Morais
 * TEMA       : Teorema de Popek-Goldberg, Falha da Arquitetura x86 Clássica,
 *              Tradução Binária Dinâmica e Virtualização Assistida por Hardware (Intel VT-x)
 * COMPILAÇÃO : gcc -std=c11 -pthread simulador_vmx_popek_goldberg.c -o simulador_vmx_popek_goldberg
 * EXECUÇÃO   : ./simulador_vmx_popek_goldberg
 * ============================================================================
 * DESCRIÇÃO:
 * Este simulador implementa os fundamentos de virtualização de CPU:
 * 1. O Teorema de Popek-Goldberg e a falha de instruções sensíveis não privilegiadas
 *    da arquitetura x86 clássica (ex: POPF em modo usuário falhando silenciosamente).
 * 2. A técnica de Tradução Binária Dinâmica (reescrita de código em runtime).
 * 3. A Virtualização Assistida por Hardware (Intel VT-x), modelando os modos
 *    VMX Root (Hipervisor) e VMX Non-Root (Guest), transições VM-Entry e VM-Exit,
 *    a estrutura VMCS (Virtual Machine Control Structure) e os custos de ciclo.
 * 4. Sobrealocação de vCPUs (Overcommit) com cálculo da métrica CPU Steal Time.
 * ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>

#define FLAG_INTERRUPT (1 << 9) /* Bit 9 do registrador RFLAGS (IF - Interrupt Flag) */

/* Classificação de Instruções de acordo com Popek-Goldberg */
typedef enum {
    OP_NORMAL_COMPUTE,    /* Instrução normal: ADD, MOV, AVX (execução direta e nativa) */
    OP_PRIVILEGED_TRAP,   /* Instrução privilegiada: HLT, INVD (gera trap em Ring 3) */
    OP_SENSITIVE_UNPRIV,  /* Instrução sensível NÃO privilegiada x86: POPF, SMSW, SGDT */
    OP_HYPERCALL          /* Chamada explícita de paravirtualização para o hipervisor */
} OpcodeClass;

typedef struct {
    const char *mnemonic;
    OpcodeClass op_class;
    uint32_t payload_flag;
} Instruction;

/* Estrutura VMCS (Virtual Machine Control Structure - Intel VT-x) */
typedef struct {
    /* Guest-State Area */
    uint64_t guest_rip;
    uint64_t guest_rflags;
    uint64_t guest_cr0;
    /* Host-State Area */
    uint64_t host_rip;
    uint64_t host_rflags;
    /* VM-Execution Control Fields */
    bool intercept_sensitive_instructions;
    /* VM-Exit Information Fields */
    uint32_t exit_reason;
    const char *exit_qualification;
} VMCS;

/* Motivos de VM-Exit (Exit Reasons) */
#define EXIT_REASON_NONE               0
#define EXIT_REASON_HLT_EXECUTED       1
#define EXIT_REASON_POPF_INTERCEPTED   2
#define EXIT_REASON_CR0_ACCESS         3

/* Estado de uma vCPU */
typedef struct {
    int vcpu_id;
    int assigned_pcpu;
    bool is_vmx_non_root; /* true = VMX Non-Root (Guest), false = VMX Root (Host) */
    int privilege_ring;   /* 0 = Kernel, 3 = User */
    uint64_t rflags;
    VMCS vmcs;
    uint64_t total_instructions_executed;
    uint64_t total_vm_exits;
    uint64_t accumulated_exit_cycles;
    /* Métricas para CPU Steal Time */
    uint64_t ready_time_ms;
    uint64_t running_time_ms;
    uint64_t steal_time_ms;   /* tempo pronta, mas sem núcleo físico (CPU steal) */
} VCPU;

/* Obtenção de tempo em milissegundos para cálculo de concorrência */
static uint64_t get_time_ms(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (uint64_t)(ts.tv_sec * 1000 + ts.tv_nsec / 1000000);
}

/* ============================================================================
 * 1. DEMONSTRAÇÃO DA FALHA DO X86 CLÁSSICO (POPEK-GOLDBERG)
 * ============================================================================ */
void test_classic_x86_flaw(void) {
    printf("\n====================================================================\n");
    printf("[1] DEMONSTRAÇÃO DA FALHA CLÁSSICA DO x86 (Teorema de Popek-Goldberg)\n");
    printf("====================================================================\n");
    printf("Contexto: Guest OS rodando desprivilegiado (Ring 3 no x86 clássico).\n");
    printf("O Guest tenta desabilitar interrupções usando a instrução sensível 'POPF'.\n\n");

    uint64_t rflags_real_host = FLAG_INTERRUPT; /* Host tem interrupções ativas */
    uint64_t rflags_guest_view = FLAG_INTERRUPT;
    int current_ring = 3; /* Forçado em Ring 3 para tentar virtualizar por Trap-and-Emulate */

    printf("Estado Inicial:\n");
    printf("  - Host RFLAGS: 0x%04lX (Interrupt Flag = %s)\n", 
           rflags_real_host, (rflags_real_host & FLAG_INTERRUPT) ? "ATIVADA" : "DESATIVADA");
    printf("  - Ring do Guest: %d\n\n", current_ring);

    printf("-> Executando instrução 'POPF' tentando zerar o bit IF...\n");
    
    /* Na arquitetura x86 clássica (IA-32), POPF em Ring 3 NÃO dispara interrupção (#GP).
     * Ela simplesmente altera os bits de usuário e descarta silenciosamente o bit IF. */
    if (current_ring == 3) {
        printf("   [HARDWARE x86 CLÁSSICO] Instrução sensível NÃO privilegiada detectada!\n");
        printf("   [FALHA SILENCIOSA] Nenhuma interrupção (Trap) foi gerada para o Hipervisor.\n");
        printf("   [RESULTADO] O processador ignorou a tentativa de alteração do bit IF.\n");
        /* O RFLAGS real do processador físico permanece inalterado! */
    }

    printf("\nEstado Após 'POPF':\n");
    printf("  - Host RFLAGS Real: 0x%04lX (Interrupt Flag continua %s)\n",
           rflags_real_host, (rflags_real_host & FLAG_INTERRUPT) ? "ATIVADA" : "DESATIVADA");
    printf("  - Conclusão: Violação do Princípio de Fidelidade de Popek-Goldberg!\n");
    printf("    O hipervisor não foi acionado e o SO convidado não obteve o estado esperado.\n");
}

/* ============================================================================
 * 2. TRADUÇÃO BINÁRIA DINÂMICA (PIONEIRISMO VMWARE)
 * ============================================================================ */
void test_binary_translation(void) {
    printf("\n====================================================================\n");
    printf("[2] TRADUÇÃO BINÁRIA DINÂMICA (Contorno via Software)\n");
    printf("====================================================================\n");
    printf("O Hipervisor analisa blocos básicos de código antes de executá-los,\n");
    printf("substituindo instruções sensíveis não privilegiadas por chamadas seguras.\n\n");

    Instruction guest_code[] = {
        {"MOV EAX, 0x10", OP_NORMAL_COMPUTE, 0},
        {"POPF",           OP_SENSITIVE_UNPRIV, 0}, /* Sensível: seria ignorada */
        {"ADD EAX, 0x05", OP_NORMAL_COMPUTE, 0}
    };
    int code_size = sizeof(guest_code) / sizeof(Instruction);

    printf("Código Original do Guest:\n");
    for (int i = 0; i < code_size; i++) {
        printf("   0x%04X: %s\n", i * 4, guest_code[i].mnemonic);
    }

    printf("\nTradutor Binário Dinâmico em execução...\n");
    for (int i = 0; i < code_size; i++) {
        if (guest_code[i].op_class == OP_SENSITIVE_UNPRIV) {
            printf("   [TRADUÇÃO] Substituindo '%s' por 'HYPERCALL_SAFE_POPF (Trampoline)'\n",
                   guest_code[i].mnemonic);
            guest_code[i].mnemonic = "CALL VMM_SAFE_POPF";
            guest_code[i].op_class = OP_HYPERCALL;
        } else {
            printf("   [EXECUÇÃO DIRETA] Mantendo instrução nativa: '%s'\n", guest_code[i].mnemonic);
        }
    }

    printf("\nResultado: Código reescrito com sucesso. Fidelidade preservada via software,\n");
    printf("porém com overhead de compilação JIT e uso adicional de memória de cache.\n");
}

/* ============================================================================
 * 3. VIRTUALIZAÇÃO ASSISTIDA POR HARDWARE (INTEL VT-x / AMD-V)
 * ============================================================================ */
void handle_vm_exit(VCPU *vcpu) {
    vcpu->total_vm_exits++;
    /* Simulação de overhead de ciclo de hardware: um VM-Exit típico consome de 500 a 1500 ciclos */
    vcpu->accumulated_exit_cycles += 950;

    printf("      [VM-EXIT] Motivo: %d (%s) | Ciclos Gastos: +950\n",
           vcpu->vmcs.exit_reason, vcpu->vmcs.exit_qualification);
    
    /* Hipervisor operando em VMX Root Ring 0 trata o evento */
    if (vcpu->vmcs.exit_reason == EXIT_REASON_POPF_INTERCEPTED) {
        printf("      [VMM TRATAMENTO] Emulando POPF na VMCS do Guest...\n");
        vcpu->vmcs.guest_rflags &= ~FLAG_INTERRUPT; /* Desabilita interrupção virtual na VMCS */
    } else if (vcpu->vmcs.exit_reason == EXIT_REASON_HLT_EXECUTED) {
        printf("      [VMM TRATAMENTO] Convidado entrou em espera (HLT). Suspendendo vCPU...\n");
    }

    /* Avança o ponteiro de instrução da VMCS para a próxima instrução */
    vcpu->vmcs.guest_rip += 4;
    vcpu->vmcs.exit_reason = EXIT_REASON_NONE;
    printf("      [VM-ENTRY] Executando VMRESUME para retornar ao modo VMX Non-Root...\n");
}

void test_hardware_assisted_vtx(void) {
    printf("\n====================================================================\n");
    printf("[3] VIRTUALIZAÇÃO ASSISTIDA POR HARDWARE (Intel VT-x / VMX Root e Non-Root)\n");
    printf("====================================================================\n");
    printf("Transições entre VMX Root (Hipervisor) e VMX Non-Root (VM Convidada)\n");
    printf("governadas pela estrutura VMCS e interceptação nativa por hardware.\n\n");

    VCPU vcpu;
    memset(&vcpu, 0, sizeof(VCPU));
    vcpu.vcpu_id = 0;
    vcpu.privilege_ring = 0; /* O Kernel Guest roda em Ring 0 real em modo Non-Root! */
    vcpu.is_vmx_non_root = true;
    vcpu.vmcs.guest_rip = 0x1000;
    vcpu.vmcs.guest_rflags = FLAG_INTERRUPT;
    vcpu.vmcs.intercept_sensitive_instructions = true;

    Instruction guest_program[] = {
        {"MOV RAX, 0x100", OP_NORMAL_COMPUTE, 0},
        {"POPF (Zerar IF)", OP_SENSITIVE_UNPRIV, 0},
        {"ADD RAX, 0x50",  OP_NORMAL_COMPUTE, 0},
        {"HLT (Sleep)",     OP_PRIVILEGED_TRAP, 0}
    };
    int total_inst = sizeof(guest_program) / sizeof(Instruction);

    printf("Inicializando VM via VMLAUNCH...\n");
    for (int i = 0; i < total_inst; i++) {
        printf("-> [VMX Non-Root Ring 0] RIP: 0x%04lX | Executando: %s\n",
               vcpu.vmcs.guest_rip, guest_program[i].mnemonic);

        if (guest_program[i].op_class == OP_NORMAL_COMPUTE) {
            printf("   [DIRECT EXECUTION] Executada em velocidade de silício puro nativo.\n");
            vcpu.vmcs.guest_rip += 4;
        } else if (guest_program[i].op_class == OP_SENSITIVE_UNPRIV) {
            /* Disparo de VM-Exit pelo circuito de silício */
            vcpu.vmcs.exit_reason = EXIT_REASON_POPF_INTERCEPTED;
            vcpu.vmcs.exit_qualification = "Tentativa de escrita sensível em RFLAGS";
            handle_vm_exit(&vcpu);
        } else if (guest_program[i].op_class == OP_PRIVILEGED_TRAP) {
            vcpu.vmcs.exit_reason = EXIT_REASON_HLT_EXECUTED;
            vcpu.vmcs.exit_qualification = "Instrução HLT interceptada";
            handle_vm_exit(&vcpu);
        }
    }

    printf("\nResumo de Execução VT-x:\n");
    printf("  - Total de VM-Exits interceptados: %lu\n", vcpu.total_vm_exits);
    printf("  - Sobretaxa (Overhead) acumulada de VM-Exit: %lu ciclos de CPU\n",
           vcpu.accumulated_exit_cycles);
}

/* ============================================================================
 * 4. SIMULAÇÃO DE OVERCOMMIT DE vCPUs E CÁLCULO DE CPU STEAL TIME
 * ============================================================================ */
#define NUM_PCPUS 2
#define NUM_VCPUS 4

typedef struct {
    int pcpu_id;
    pthread_t thread_handle;
    bool active;
} PCPU;

static pthread_mutex_t scheduler_mutex = PTHREAD_MUTEX_INITIALIZER;
static VCPU vcpu_cluster[NUM_VCPUS];
static bool simulation_running = true;

void *pcpu_worker(void *arg) {
    int pcpu_id = *((int *)arg);
    free(arg);

    while (simulation_running) {
        VCPU *selected_vcpu = NULL;

        pthread_mutex_lock(&scheduler_mutex);
        /* Escalonador Round-Robin simplificado selecionando a vCPU com maior espera */
        uint64_t max_wait = 0;
        int selected_idx = -1;
        for (int i = 0; i < NUM_VCPUS; i++) {
            if (vcpu_cluster[i].ready_time_ms > max_wait) {
                max_wait = vcpu_cluster[i].ready_time_ms;
                selected_idx = i;
            }
        }

        if (selected_idx >= 0 && max_wait > 0) {
            selected_vcpu = &vcpu_cluster[selected_idx];
            /* Contabiliza o tempo que a vCPU ficou pronta mas sem núcleo (Steal Time) */
            selected_vcpu->steal_time_ms += selected_vcpu->ready_time_ms;
            selected_vcpu->ready_time_ms = 0;
        }
        pthread_mutex_unlock(&scheduler_mutex);

        if (selected_vcpu) {
            /* Simula execução de instruções da VM no núcleo físico */
            usleep(25000); /* 25 ms de quantum de execução */
            pthread_mutex_lock(&scheduler_mutex);
            selected_vcpu->running_time_ms += 25;
            selected_vcpu->total_instructions_executed += 50000;
            pthread_mutex_unlock(&scheduler_mutex);
        } else {
            usleep(5000);
        }

        /* As outras vCPUs que ficaram na fila acumulam tempo de espera (Ready/Steal) */
        pthread_mutex_lock(&scheduler_mutex);
        for (int i = 0; i < NUM_VCPUS; i++) {
            if (&vcpu_cluster[i] != selected_vcpu) {
                vcpu_cluster[i].ready_time_ms += 10;
            }
        }
        pthread_mutex_unlock(&scheduler_mutex);
    }
    return NULL;
}

void test_vcpu_overcommit_and_steal_time(void) {
    printf("\n====================================================================\n");
    printf("[4] SOBREALOCAÇÃO DE vCPUs (Overcommit) E CÁLCULO DE CPU STEAL TIME\n");
    printf("====================================================================\n");
    printf("Cenário de Dimensionamento:\n");
    printf("  - Núcleos Físicos de Processador (pCPUs) : %d\n", NUM_PCPUS);
    printf("  - vCPUs Alocadas concorrentes            : %d\n", NUM_VCPUS);
    printf("  - Razão de Sobrealocação (Overcommit)   : %.1f:1 (200%%)\n",
           (double)NUM_VCPUS / NUM_PCPUS);
    printf("\nExecutando carga de trabalho multithread por 2 segundos...\n");

    for (int i = 0; i < NUM_VCPUS; i++) {
        vcpu_cluster[i].vcpu_id = i;
        vcpu_cluster[i].ready_time_ms = 5;
        vcpu_cluster[i].running_time_ms = 0;
        vcpu_cluster[i].steal_time_ms = 0;
        vcpu_cluster[i].total_instructions_executed = 0;
    }

    pthread_t workers[NUM_PCPUS];
    for (int i = 0; i < NUM_PCPUS; i++) {
        int *id = malloc(sizeof(int));
        *id = i;
        pthread_create(&workers[i], NULL, pcpu_worker, id);
    }

    sleep(2); /* Executa a simulação por 2 segundos */

    pthread_mutex_lock(&scheduler_mutex);
    simulation_running = false;
    pthread_mutex_unlock(&scheduler_mutex);

    for (int i = 0; i < NUM_PCPUS; i++) {
        pthread_join(workers[i], NULL);
    }

    printf("\nResultados do Monitoramento de Contenção de vCPU:\n");
    printf("+--------+----------------+----------------+-----------------+\n");
    printf("| vCPU ID| Running Time   | Steal Time     | %% CPU Steal     |\n");
    printf("+--------+----------------+----------------+-----------------+\n");
    for (int i = 0; i < NUM_VCPUS; i++) {
        uint64_t total = vcpu_cluster[i].running_time_ms + vcpu_cluster[i].steal_time_ms;
        double pct_steal = total > 0 ? ((double)vcpu_cluster[i].steal_time_ms / total) * 100.0 : 0.0;
        printf("| vCPU %02d| %6lu ms       | %6lu ms       | %13.2f%%   |\n",
               vcpu_cluster[i].vcpu_id,
               vcpu_cluster[i].running_time_ms,
               vcpu_cluster[i].steal_time_ms,
               pct_steal);
    }
    printf("+--------+----------------+----------------+-----------------+\n");
    printf("Diagnóstico de Engenharia:\n");
    printf("Valores de %%steal > 10%% indicam contenção severa no host físico,\n");
    printf("causando atrasos em timers de rede e latência em aplicações transacionais.\n");
}

int main(void) {
    printf("####################################################################\n");
    printf("# SIMULADOR DE ARQUITETURA DE VIRTUALIZAÇÃO DE CPU & VT-x          #\n");
    printf("# Disciplina: Sistemas Operacionais - UniFEF                       #\n");
    printf("# Docente   : Prof. Me. Guilherme de Morais                        #\n");
    printf("####################################################################\n");

    test_classic_x86_flaw();
    test_binary_translation();
    test_hardware_assisted_vtx();
    test_vcpu_overcommit_and_steal_time();

    printf("\n[FIM DO PROGRAMA] Todos os testes conceituais executados com sucesso.\n");
    return 0;
}
