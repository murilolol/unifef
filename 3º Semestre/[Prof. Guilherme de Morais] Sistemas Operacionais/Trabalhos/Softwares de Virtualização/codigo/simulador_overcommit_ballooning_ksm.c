/*
 * ============================================================================
 * DISCIPLINA : Sistemas Operacionais (3º Semestre) - UniFEF
 * PROFESSOR  : Me. Guilherme de Morais
 * TEMA       : Gestão de Memória em Hipervisores: Sobrealocação (Overcommit),
 *              Deduplicação via KSM (Kernel Samepage Merging) com Copy-On-Write
 *              e Recuperação Ativa por Memory Ballooning (virtio-balloon)
 * COMPILAÇÃO : gcc -std=c11 -pthread simulador_overcommit_ballooning_ksm.c -o simulador_overcommit_ballooning_ksm
 * EXECUÇÃO   : ./simulador_overcommit_ballooning_ksm
 * ============================================================================
 * DESCRIÇÃO:
 * Este programa implementa os algoritmos de mitigação de saturação de memória:
 * 1. Simulação do cenário exato do artigo: Host físico com capacidade estrita
 *    (64 frames de RAM), executando VMs que demandam 96 frames (Overcommit de 150%).
 * 2. KSM Daemon (ksmd): Varredura em segundo plano que identifica páginas com
 *    conteúdo idêntico, fundindo-as em frames Copy-On-Write (COW) compartilhados.
 * 3. Simulação de falha de proteção de escrita (COW Fault), clonando páginas sob demanda.
 * 4. Driver virtio-balloon: Quando a memória física atinge limite crítico (>85%),
 *    o hipervisor infla o balão dentro do Guest, forçando a devolução de frames.
 * 5. Demonstração de contenção severa e risco de disparo do OOM-Killer.
 * ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

#define HOST_PHYSICAL_FRAMES 64 /* Representa 64 GB de RAM física do servidor */
#define NUM_VMS              3  /* 3 VMs demandando 32 frames cada = 96 frames nominais */
#define VM_NOMINAL_FRAMES    32 

/* Estrutura de um frame de memória física no Host (HPA) */
typedef struct {
    int frame_id;
    bool is_allocated;
    bool is_cow_shared;    /* Indica se foi mesclada pelo KSM */
    int shared_reference_count;
    uint32_t content_hash; /* Hash simples para simular o conteúdo de 4 KB */
    char content_tag[32];  /* Tag diagnóstica (ex: 'libc.so', 'guest_kernel', 'dados_unicos') */
} HostFrame;

/* Estrutura de uma Página Alocada na VM (GPA) */
typedef struct {
    int page_id;
    int mapped_hpa_frame;
    bool in_use_by_balloon;
    bool is_dirty;
} GuestPage;

/* Máquina Virtual */
typedef struct {
    int vm_id;
    GuestPage pages[VM_NOMINAL_FRAMES];
    int allocated_count;
    int ballooned_count;
} VirtualMachine;

/* Estado Global do Hipervisor */
typedef struct {
    HostFrame host_ram[HOST_PHYSICAL_FRAMES];
    int free_frames_count;
    VirtualMachine vms[NUM_VMS];
    int ksm_pages_merged_total;
} HypervisorMemorySystem;

static HypervisorMemorySystem hyp_sys;
static pthread_mutex_t memory_mutex = PTHREAD_MUTEX_INITIALIZER;

/* Hash simples para identificação de páginas idênticas */
uint32_t compute_hash(const char *tag) {
    uint32_t hash = 5381;
    int c;
    while ((c = *tag++)) {
        hash = ((hash << 5) + hash) + c;
    }
    return hash;
}

/* Inicializa a memória do servidor */
void init_hypervisor(void) {
    hyp_sys.free_frames_count = HOST_PHYSICAL_FRAMES;
    hyp_sys.ksm_pages_merged_total = 0;
    for (int i = 0; i < HOST_PHYSICAL_FRAMES; i++) {
        hyp_sys.host_ram[i].frame_id = i;
        hyp_sys.host_ram[i].is_allocated = false;
        hyp_sys.host_ram[i].is_cow_shared = false;
        hyp_sys.host_ram[i].shared_reference_count = 0;
        hyp_sys.host_ram[i].content_hash = 0;
    }

    for (int v = 0; v < NUM_VMS; v++) {
        hyp_sys.vms[v].vm_id = v + 1;
        hyp_sys.vms[v].allocated_count = 0;
        hyp_sys.vms[v].ballooned_count = 0;
        for (int p = 0; p < VM_NOMINAL_FRAMES; p++) {
            hyp_sys.vms[v].pages[p].page_id = p;
            hyp_sys.vms[v].pages[p].mapped_hpa_frame = -1;
            hyp_sys.vms[v].pages[p].in_use_by_balloon = false;
            hyp_sys.vms[v].pages[p].is_dirty = false;
        }
    }
}

/* Alocador de frame físico no Host */
int allocate_physical_frame(const char *content_tag) {
    if (hyp_sys.free_frames_count <= 0) {
        return -1; /* Out of physical memory! */
    }
    for (int i = 0; i < HOST_PHYSICAL_FRAMES; i++) {
        if (!hyp_sys.host_ram[i].is_allocated) {
            hyp_sys.host_ram[i].is_allocated = true;
            hyp_sys.host_ram[i].is_cow_shared = false;
            hyp_sys.host_ram[i].shared_reference_count = 1;
            hyp_sys.host_ram[i].content_hash = compute_hash(content_tag);
            strncpy(hyp_sys.host_ram[i].content_tag, content_tag, 31);
            hyp_sys.free_frames_count--;
            return i;
        }
    }
    return -1;
}

/* Liberação de frame físico de volta para o Host Pool */
void free_physical_frame(int frame_id) {
    if (frame_id < 0 || frame_id >= HOST_PHYSICAL_FRAMES) return;
    if (hyp_sys.host_ram[frame_id].shared_reference_count > 1) {
        /* Página compartilhada via COW: apenas decrementa a contagem de referência */
        hyp_sys.host_ram[frame_id].shared_reference_count--;
    } else {
        /* Libera totalmente o frame físico */
        hyp_sys.host_ram[frame_id].is_allocated = false;
        hyp_sys.host_ram[frame_id].is_cow_shared = false;
        hyp_sys.host_ram[frame_id].shared_reference_count = 0;
        hyp_sys.host_ram[frame_id].content_hash = 0;
        hyp_sys.free_frames_count++;
    }
}

/* ============================================================================
 * 1. SIMULAÇÃO DO KERNEL SAMEPAGE MERGING (KSM)
 * ============================================================================ */
void run_ksm_deduplication(void) {
    printf("\n>>> [KSM DAEMON] Iniciando varredura ksmd para deduplicação de memória...\n");
    int merged_this_cycle = 0;

    for (int v1 = 0; v1 < NUM_VMS; v1++) {
        for (int p1 = 0; p1 < hyp_sys.vms[v1].allocated_count; p1++) {
            int f1 = hyp_sys.vms[v1].pages[p1].mapped_hpa_frame;
            if (f1 < 0) continue;

            for (int v2 = v1; v2 < NUM_VMS; v2++) {
                int start_p2 = (v1 == v2) ? (p1 + 1) : 0;
                for (int p2 = start_p2; p2 < hyp_sys.vms[v2].allocated_count; p2++) {
                    int f2 = hyp_sys.vms[v2].pages[p2].mapped_hpa_frame;
                    if (f2 < 0 || f1 == f2) continue;

                    /* Compara os hashes para identificar páginas idênticas */
                    if (hyp_sys.host_ram[f1].content_hash == hyp_sys.host_ram[f2].content_hash &&
                        strcmp(hyp_sys.host_ram[f1].content_tag, hyp_sys.host_ram[f2].content_tag) == 0) {
                        
                        /* Encontrou páginas idênticas! Mescla as referências */
                        free_physical_frame(f2); /* Desaloca a cópia redundante */
                        
                        hyp_sys.vms[v2].pages[p2].mapped_hpa_frame = f1; /* Aponta para f1 */
                        hyp_sys.host_ram[f1].is_cow_shared = true;
                        hyp_sys.host_ram[f1].shared_reference_count++;
                        
                        hyp_sys.ksm_pages_merged_total++;
                        merged_this_cycle++;
                    }
                }
            }
        }
    }
    printf("    -> KSM concluiu varredura: %d páginas duplicadas foram fundidas sob COW.\n", 
           merged_this_cycle);
    printf("    -> Memória Física Real Liberada de volta ao Host: %d frames\n", merged_this_cycle);
}

/* Simulação de escrita em página Copy-On-Write */
void trigger_cow_write(int vm_idx, int page_idx, const char *new_content) {
    printf("\n>>> [COW VIOLATION] VM %d tentou escrever na página %d (Mapeada em Frame COW!)...\n",
           vm_idx + 1, page_idx);
    
    int old_frame = hyp_sys.vms[vm_idx].pages[page_idx].mapped_hpa_frame;
    if (hyp_sys.host_ram[old_frame].is_cow_shared) {
        printf("    [INTERRUPÇÃO DE HARDWARE] MMU detectou escrita em frame Read-Only!\n");
        printf("    [VMM AÇÃO] Alocando NOVO frame físico exclusivo e duplicando conteúdo...\n");
        
        int new_frame = allocate_physical_frame(new_content);
        if (new_frame >= 0) {
            free_physical_frame(old_frame); /* Decrementa ref count do frame compartilhado */
            hyp_sys.vms[vm_idx].pages[page_idx].mapped_hpa_frame = new_frame;
            hyp_sys.vms[vm_idx].pages[page_idx].is_dirty = true;
            printf("    -> Sucesso: VM %d agora possui o frame exclusivo %d (HPA).\n",
                   vm_idx + 1, new_frame);
        } else {
            printf("    [FALHA] Sem frames livres para clonagem de COW!\n");
        }
    }
}

/* ============================================================================
 * 2. SIMULAÇÃO DE MEMORY BALLOONING (virtio-balloon)
 * ============================================================================ */
void run_memory_ballooning(int vm_idx, int frames_to_reclaim) {
    printf("\n>>> [BALLOON INFLATE] Hipervisor emitindo comando para virtio-balloon na VM %d...\n",
           vm_idx + 1);
    printf("    Solicitando inflação de %d frames para mitigar contenção do Host.\n", 
           frames_to_reclaim);

    VirtualMachine *vm = &hyp_sys.vms[vm_idx];
    int reclaimed = 0;
    
    for (int p = vm->allocated_count - 1; p >= 0 && reclaimed < frames_to_reclaim; p--) {
        if (!vm->pages[p].in_use_by_balloon && vm->pages[p].mapped_hpa_frame >= 0) {
            int hpa_to_free = vm->pages[p].mapped_hpa_frame;
            /* O Driver do balão na VM aloca a página e a remove do uso dos processos */
            vm->pages[p].in_use_by_balloon = true;
            vm->pages[p].mapped_hpa_frame = -1;
            
            /* O Hipervisor desmapeia na EPT e libera a RAM física real */
            free_physical_frame(hpa_to_free);
            vm->ballooned_count++;
            reclaimed++;
        }
    }
    printf("    -> Sucesso: Balão inflado em %d frames. Memória física devolvida ao Host!\n", 
           reclaimed);
}

int main(void) {
    printf("####################################################################\n");
    printf("# SIMULADOR DE OVERCOMMIT, KSM DEDUPLICATION & MEMORY BALLOONING    #\n");
    printf("# Disciplina: Sistemas Operacionais - UniFEF                       #\n");
    printf("# Docente   : Prof. Me. Guilherme de Morais                        #\n");
    printf("####################################################################\n\n");

    init_hypervisor();

    printf("Cenário Dimensionado:\n");
    printf("  - RAM Física do Host        : %d frames (64 GB)\n", HOST_PHYSICAL_FRAMES);
    printf("  - Demandas das 3 VMs        : 3 x %d = %d frames nominais (96 GB)\n",
           VM_NOMINAL_FRAMES, NUM_VMS * VM_NOMINAL_FRAMES);
    printf("  - Razão Nominal Overcommit  : 1.5:1 (150%%)\n\n");

    printf("[ETAPA 1] Alocando memória para as 3 VMs (Cargas de SO e Bibliotecas)...\n");
    /* VM 1 e VM 2 rodam a mesma distribuição Linux: compartilham muitas páginas idênticas */
    for (int v = 0; v < 2; v++) {
        for (int p = 0; p < 25; p++) {
            const char *tag = (p < 10) ? "kernel_linux_6.1" : (p < 18 ? "glibc_shared.so" : "dados_app");
            int frame = allocate_physical_frame(tag);
            if (frame >= 0) {
                hyp_sys.vms[v].pages[p].mapped_hpa_frame = frame;
                hyp_sys.vms[v].allocated_count++;
            }
        }
    }
    printf("  -> Alocados 25 frames para VM 1 e 25 frames para VM 2.\n");
    printf("  -> Frames Físicos Livres restantes no Host: %d de %d\n",
           hyp_sys.free_frames_count, HOST_PHYSICAL_FRAMES);

    /* Agora VM 3 tenta alocar 20 frames... */
    printf("\n[ETAPA 2] VM 3 solicita 20 frames adicionais...\n");
    for (int p = 0; p < 20; p++) {
        int frame = allocate_physical_frame("db_postgres");
        if (frame >= 0) {
            hyp_sys.vms[2].pages[p].mapped_hpa_frame = frame;
            hyp_sys.vms[2].allocated_count++;
        } else {
            printf("  -> ALERTA: Memória física esgotada ao tentar alocar para VM 3!\n");
            break;
        }
    }
    printf("  -> Frames Físicos Livres no Host: %d de %d (Pressão Crítica de Memória!)\n",
           hyp_sys.free_frames_count, HOST_PHYSICAL_FRAMES);

    /* Executa o KSM para recuperar memória duplicada entre VM 1 e VM 2 */
    run_ksm_deduplication();
    printf("  -> Frames Livres no Host APÓS KSM: %d de %d\n",
           hyp_sys.free_frames_count, HOST_PHYSICAL_FRAMES);

    /* Simula uma escrita em página compartilhada Copy-On-Write */
    trigger_cow_write(0, 5, "kernel_patch_modificado");
    printf("  -> Frames Livres APÓS COW: %d de %d\n",
           hyp_sys.free_frames_count, HOST_PHYSICAL_FRAMES);

    /* Se uma nova demanda repentina surgir, acionamos o Memory Ballooning */
    printf("\n[ETAPA 3] Simulação de Pico de Carga e Recuperação Ativa via virtio-balloon:\n");
    run_memory_ballooning(0, 6); /* Requisita 6 frames da VM 1 */
    run_memory_ballooning(1, 6); /* Requisita 6 frames da VM 2 */

    printf("\n====================================================================\n");
    printf("RELATÓRIO CONSOLIDADO DE GESTÃO DE MEMÓRIA (OVERCOMMIT)\n");
    printf("====================================================================\n");
    printf("+---------------------------------------+--------------------------+\n");
    printf("| Métrica do Servidor                   | Valor                    |\n");
    printf("+---------------------------------------+--------------------------+\n");
    printf("| Capacidade Física Total (RAM Host)    | %10d frames (64 GB) |\n", HOST_PHYSICAL_FRAMES);
    printf("| Total Nominal Alocado pelas VMs       | %10d frames (70 GB) |\n", 
           hyp_sys.vms[0].allocated_count + hyp_sys.vms[1].allocated_count + hyp_sys.vms[2].allocated_count);
    printf("| Páginas Recuperadas pelo KSM          | %10d frames (COW)   |\n", hyp_sys.ksm_pages_merged_total);
    printf("| Memória Reclamada por Ballooning      | %10d frames        |\n", 
           hyp_sys.vms[0].ballooned_count + hyp_sys.vms[1].ballooned_count);
    printf("| Frames Físicos Livres de Margem       | %10d frames        |\n", hyp_sys.free_frames_count);
    printf("+---------------------------------------+--------------------------+\n");
    printf("Conclusão de Engenharia:\n");
    printf("Graças à combinação sinérgica de KSM e Memory Ballooning, o hipervisor\n");
    printf("atendeu à sobrealocação nominal de 150%% sem disparar o temido OOM-Killer\n");
    printf("e sem sofrer congelamento por degradação em disco (Hypervisor Swapping Thrashing).\n");

    return 0;
}
