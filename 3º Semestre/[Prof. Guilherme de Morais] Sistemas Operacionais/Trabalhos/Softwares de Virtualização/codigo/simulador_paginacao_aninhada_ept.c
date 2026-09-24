/*
 * ============================================================================
 * DISCIPLINA : Sistemas Operacionais (3º Semestre) - UniFEF
 * PROFESSOR  : Me. Guilherme de Morais
 * TEMA       : Virtualização de Memória: Paginação Aninhada (Intel EPT / AMD NPT),
 *              Tradução Bidimensional (2D Page Walk), TLB e HugePages
 * COMPILAÇÃO : gcc -std=c11 -pthread simulador_paginacao_aninhada_ept.c -o simulador_paginacao_aninhada_ept
 * EXECUÇÃO   : ./simulador_paginacao_aninhada_ept
 * ============================================================================
 * DESCRIÇÃO:
 * Este programa implementa um simulador exato do modelo de tradução de memória:
 * 1. Conversão em duas etapas: GVA -> GPA -> HPA.
 *    - GVA (Guest Virtual Address): Endereço virtual do processo na VM.
 *    - GPA (Guest Physical Address): Endereço 'físico' gerenciado pelo Guest OS.
 *    - HPA (Host Physical Address): Endereço real na RAM física do servidor.
 * 2. Simulação da caminhada bidimensional de páginas (2D Page Walk) no silício
 *    e a quantificação da penalidade de acessos sucessivos à RAM em caso de TLB Miss.
 * 3. Cache TLB (Translation Lookaside Buffer) com identificador VPID.
 * 4. Comparação quantitativa entre Páginas Tradicionais de 4 KB e HugePages de 2 MB.
 * ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>

#define PAGE_SIZE_4KB      4096
#define PAGE_SIZE_2MB      2097152
#define TLB_CAPACITY       16
#define MEMORY_POOL_FRAMES 1024

/* Estrutura de entrada do TLB */
typedef struct {
    uint64_t gva_tag;
    uint64_t hpa_result;
    uint16_t vpid;     /* Virtual Processor ID (tag para evitar flush entre VMs) */
    bool valid;
} TLBEntry;

/* Simulação do TLB */
typedef struct {
    TLBEntry entries[TLB_CAPACITY];
    uint64_t hits;
    uint64_t misses;
} TLBCache;

/* Tabela de Páginas do Convidado (Guest Page Table - Camada 1) */
typedef struct {
    uint64_t gva_base;
    uint64_t gpa_target;
    bool is_2mb_hugepage;
    bool present;
} GuestPTE;

/* Tabela de Páginas Estendida do Hipervisor (EPT - Camada 2 em Hardware) */
typedef struct {
    uint64_t gpa_base;
    uint64_t hpa_target;
    bool read_access;
    bool write_access;
    bool exec_access;
    bool present;
} EPTEntry;

/* Estado Geral da Memória de Virtualização */
typedef struct {
    GuestPTE guest_tables[64];
    int guest_entries_count;
    EPTEntry ept_tables[64];
    int ept_entries_count;
    TLBCache tlb;
    uint64_t total_ram_accesses;
} MemoryVirtualizationSystem;

/* Inicialização das estruturas */
void init_system(MemoryVirtualizationSystem *sys) {
    memset(sys, 0, sizeof(MemoryVirtualizationSystem));
    
    /* Mapeamento Guest (GVA -> GPA) */
    /* Processo Convidado alocou 4 páginas normais (4 KB) */
    sys->guest_tables[0] = (GuestPTE){0x00400000, 0x10000000, false, true}; /* Code */
    sys->guest_tables[1] = (GuestPTE){0x00401000, 0x10001000, false, true}; /* Data */
    sys->guest_tables[2] = (GuestPTE){0x00402000, 0x10002000, false, true}; /* Stack */
    sys->guest_tables[3] = (GuestPTE){0x00403000, 0x10003000, false, true}; /* Heap */
    /* E 1 HugePage de 2 MB (ex: buffer de banco de dados da VM) */
    sys->guest_tables[4] = (GuestPTE){0x00600000, 0x20000000, true, true};  /* HugePage 2MB */
    sys->guest_entries_count = 5;

    /* Mapeamento EPT do Hipervisor (GPA -> HPA) */
    sys->ept_tables[0] = (EPTEntry){0x10000000, 0x80000000, true, true, true, true};
    sys->ept_tables[1] = (EPTEntry){0x10001000, 0x80001000, true, true, false, true};
    sys->ept_tables[2] = (EPTEntry){0x10002000, 0x80002000, true, true, false, true};
    sys->ept_tables[3] = (EPTEntry){0x10003000, 0x80003000, true, true, false, true};
    sys->ept_tables[4] = (EPTEntry){0x20000000, 0x90000000, true, true, false, true};
    sys->ept_entries_count = 5;
}

/* Consulta rápida no TLB */
bool lookup_tlb(TLBCache *tlb, uint64_t gva, uint16_t vpid, uint64_t *out_hpa) {
    uint64_t page_tag = gva & ~0xFFFULL;
    for (int i = 0; i < TLB_CAPACITY; i++) {
        if (tlb->entries[i].valid && 
            tlb->entries[i].vpid == vpid && 
            tlb->entries[i].gva_tag == page_tag) {
            tlb->hits++;
            *out_hpa = tlb->entries[i].hpa_result | (gva & 0xFFFULL);
            return true;
        }
    }
    tlb->misses++;
    return false;
}

/* Inserção no TLB (política FIFO simples) */
void insert_tlb(TLBCache *tlb, uint64_t gva, uint64_t hpa, uint16_t vpid) {
    static int next_idx = 0;
    tlb->entries[next_idx].valid = true;
    tlb->entries[next_idx].vpid = vpid;
    tlb->entries[next_idx].gva_tag = gva & ~0xFFFULL;
    tlb->entries[next_idx].hpa_result = hpa & ~0xFFFULL;
    next_idx = (next_idx + 1) % TLB_CAPACITY;
}

/* ============================================================================
 * CAMINHADA BIDIMENSIONAL DE PÁGINAS (2D PAGE WALK)
 * ============================================================================ */
bool translate_two_dimensional(MemoryVirtualizationSystem *sys, uint64_t gva, uint16_t vpid,
                                uint64_t *out_hpa, int *out_ram_dereferences) {
    *out_ram_dereferences = 0;

    /* 1. Checa o TLB em Hardware primeiro */
    if (lookup_tlb(&sys->tlb, gva, vpid, out_hpa)) {
        return true; /* TLB Hit: 0 acessos adicionais à RAM */
    }

    /* TLB Miss! A MMU é forçada a executar a caminhada bidimensional completa */
    printf("      [TLB MISS] Iniciando caminhada bidimensional (2D Page Walk) no silício...\n");

    /* Busca na Tabela de Páginas do Convidado (GVA -> GPA) */
    uint64_t gpa = 0;
    bool is_huge = false;
    bool guest_found = false;
    for (int i = 0; i < sys->guest_entries_count; i++) {
        if (sys->guest_tables[i].present && (gva & ~0xFFFULL) == sys->guest_tables[i].gva_base) {
            gpa = sys->guest_tables[i].gpa_target | (gva & 0xFFFULL);
            is_huge = sys->guest_tables[i].is_2mb_hugepage;
            guest_found = true;
            break;
        }
    }

    if (!guest_found) {
        printf("      [ERRO] Falha de Página no Convidado (#PF - Guest Page Fault)\n");
        return false;
    }

    /* Em arquiteturas x86_64 normais com 4 níveis de tabela no Guest (PML4, PDPT, PD, PT)
     * e 4 níveis de tabela no EPT do Hipervisor:
     * Cada nível do Guest é acessado via GPA, que precisa passar pelos 4 níveis do EPT!
     * Para página de 4 KB: 4 níveis Guest x (4 níveis EPT) + 4 níveis EPT para o dado = até 24 acessos.
     * Em nossa modelagem demonstrativa de 2 níveis (Directory + Table):
     *   - Com 4 KB: 2 níveis Guest * 2 níveis EPT + 2 acessos EPT finais = 6 acessos à RAM.
     *   - Com HugePage de 2 MB: nível PT é eliminado! Economiza 2 acessos inteiros à RAM. */
    int derefs = 0;
    if (is_huge) {
        derefs = 4; /* Nível de tabela reduzido (elimina caminhada de PT) */
        printf("      [HUGEPAGE 2MB] Nível intermediário de tabela ignorado pela MMU!\n");
    } else {
        derefs = 6;
    }
    *out_ram_dereferences = derefs;
    sys->total_ram_accesses += derefs;

    /* Busca na Tabela EPT do Hipervisor (GPA -> HPA) */
    uint64_t hpa = 0;
    bool ept_found = false;
    uint64_t gpa_base = gpa & ~0xFFFULL;
    for (int i = 0; i < sys->ept_entries_count; i++) {
        if (sys->ept_tables[i].present && sys->ept_tables[i].gpa_base == gpa_base) {
            hpa = sys->ept_tables[i].hpa_target | (gpa & 0xFFFULL);
            ept_found = true;
            break;
        }
    }

    if (!ept_found) {
        printf("      [ERRO CRÍTICO] EPT Violation (Falta de página no Hipervisor)!\n");
        return false;
    }

    *out_hpa = hpa;
    /* Popula o TLB com a tradução resolvida para futuros acessos imediatos */
    insert_tlb(&sys->tlb, gva, hpa, vpid);
    return true;
}

int main(void) {
    printf("####################################################################\n");
    printf("# SIMULADOR DE PAGINAÇÃO ANINHADA (EPT) & 2D PAGE TABLE WALK       #\n");
    printf("# Disciplina: Sistemas Operacionais - UniFEF                       #\n");
    printf("# Docente   : Prof. Me. Guilherme de Morais                        #\n");
    printf("####################################################################\n\n");

    MemoryVirtualizationSystem sys;
    init_system(&sys);

    uint16_t vm_vpid = 1; /* Virtual Processor ID da VM */

    printf("[1] DEMONSTRAÇÃO PASSO A PASSO DA TRADUÇÃO BIDIMENSIONAL:\n");
    printf("--------------------------------------------------------------------\n");
    uint64_t test_gva = 0x00401050;
    uint64_t resolved_hpa = 0;
    int ram_accesses = 0;

    printf("Tentando acessar GVA: 0x%08lX (Página de 4 KB)\n", test_gva);
    translate_two_dimensional(&sys, test_gva, vm_vpid, &resolved_hpa, &ram_accesses);
    printf("-> Resultado: GVA 0x%08lX -> HPA 0x%08lX\n", test_gva, resolved_hpa);
    printf("-> Acessos físicos à RAM necessários: %d acessos (Penalidade EPT)\n\n", ram_accesses);

    printf("Acessando o MESMO endereço novamente (Teste de TLB Hit):\n");
    translate_two_dimensional(&sys, test_gva, vm_vpid, &resolved_hpa, &ram_accesses);
    printf("-> [TLB HIT] Resolvido instantaneamente no cache interno da CPU!\n");
    printf("-> Acessos físicos à RAM necessários: %d acessos\n\n", ram_accesses);

    printf("[2] COMPARAÇÃO: PÁGINAS PADRÃO (4 KB) VS HUGEPAGES (2 MB):\n");
    printf("--------------------------------------------------------------------\n");
    uint64_t gva_huge = 0x00600100;
    printf("Acessando GVA em HugePage de 2 MB (0x%08lX):\n", gva_huge);
    translate_two_dimensional(&sys, gva_huge, vm_vpid, &resolved_hpa, &ram_accesses);
    printf("-> Resultado HugePage: GVA 0x%08lX -> HPA 0x%08lX\n", gva_huge, resolved_hpa);
    printf("-> Acessos físicos à RAM necessários: %d acessos\n", ram_accesses);
    printf("-> Economia de dereferências: A profundidade da árvore foi reduzida!\n\n");

    printf("[3] BENCHMARK DE CARGA E TAXA DE EFICIÊNCIA DO TLB:\n");
    printf("--------------------------------------------------------------------\n");
    uint64_t workload[] = {
        0x00400010, 0x00401020, 0x00402030, 0x00400040, 0x00401050,
        0x00400010, 0x00401020, 0x00600000, 0x00600050, 0x00403010
    };
    int total_requests = sizeof(workload) / sizeof(uint64_t);

    for (int i = 0; i < total_requests; i++) {
        translate_two_dimensional(&sys, workload[i], vm_vpid, &resolved_hpa, &ram_accesses);
    }

    printf("\nEstatísticas Consolidadas do Subsistema de Memória:\n");
    printf("+------------------------------------+------------+\n");
    printf("| Métrica de Desempenho              | Valor      |\n");
    printf("+------------------------------------+------------+\n");
    printf("| Total de Consultas de Tradução     | %10d |\n", total_requests + 3);
    printf("| Total de TLB Hits                  | %10lu |\n", sys.tlb.hits);
    printf("| Total de TLB Misses                | %10lu |\n", sys.tlb.misses);
    double hit_rate = ((double)sys.tlb.hits / (sys.tlb.hits + sys.tlb.misses)) * 100.0;
    printf("| Taxa de Acerto (TLB Hit Rate)      | %9.2f%% |\n", hit_rate);
    printf("| Total de Acessos Físicos à RAM     | %10lu |\n", sys.total_ram_accesses);
    printf("+------------------------------------+------------+\n");
    printf("Conclusão Acadêmica:\n");
    printf("A tecnologia EPT/NPT elimina as antigas Shadow Page Tables em software,\n");
    printf("mas penaliza os TLB misses com multiplicações de dereferência.\n");
    printf("O uso de HugePages é mandatório em bancos de dados virtualizados\n");
    printf("para reduzir o custo do caminhamento bidimensional.\n");

    return 0;
}
