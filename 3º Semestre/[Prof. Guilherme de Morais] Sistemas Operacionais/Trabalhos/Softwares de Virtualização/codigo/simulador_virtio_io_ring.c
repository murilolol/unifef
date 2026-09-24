/*
 * ============================================================================
 * DISCIPLINA : Sistemas Operacionais (3º Semestre) - UniFEF
 * PROFESSOR  : Me. Guilherme de Morais
 * TEMA       : Virtualização de Entrada e Saída (I/O): Emulação de Hardware Legado
 *              versus Paravirtualização com Buffers Circulares (virtio / virtqueues)
 * COMPILAÇÃO : gcc -std=c11 -pthread simulador_virtio_io_ring.c -o simulador_virtio_io_ring
 * EXECUÇÃO   : ./simulador_virtio_io_ring
 * ============================================================================
 * DESCRIÇÃO:
 * Este simulador quantifica a diferença de desempenho entre:
 * 1. Emulação Completa de Dispositivo Legado (Trap-and-Emulate em portas de I/O):
 *    Cada comando ou byte escrito causa um VM-Exit para o Hipervisor.
 * 2. Paravirtualização de I/O através do padrão aberto virtio:
 *    - Estrutura vring compartilhada em memória (Descriptor Table, Available Ring, Used Ring).
 *    - Agrupamento em lote (Batching) de requisições de I/O.
 *    - Uma única notificação de porta (Doorbell / Kick) para dezenas de requisições.
 * ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#define VRING_SIZE 16 /* Capacidade máxima de descritores na fila circular */

/* Flags para a tabela de descritores do virtio */
#define VRING_DESC_F_NEXT     1 /* Encadeia próximo descritor */
#define VRING_DESC_F_WRITE    2 /* Buffer gravável pelo Host (leitura da VM) */

/* 1. Tabela de Descritores: Aponta para os buffers na memória do convidado */
typedef struct {
    uint64_t addr;   /* Endereço físico do buffer na VM (GPA) */
    uint32_t len;    /* Tamanho do bloco em bytes */
    uint16_t flags;  /* Flags de encadeamento e permissão */
    uint16_t next;   /* Próximo índice de descritor se encadeado */
} VirtioDesc;

/* 2. Available Ring: Anel onde o Guest coloca descritores prontos para o Host */
typedef struct {
    uint16_t flags;
    uint16_t idx;                    /* Índice de inserção do Guest */
    uint16_t ring[VRING_SIZE];       /* Cabeças das cadeias de descritores */
} VirtioAvail;

/* Elemento do anel de usados */
typedef struct {
    uint32_t id;   /* Índice do descritor processado */
    uint32_t len;  /* Quantidade de bytes efetivamente transferidos */
} VirtioUsedElem;

/* 3. Used Ring: Anel onde o Host registra os descritores concluídos */
typedef struct {
    uint16_t flags;
    uint16_t idx;                    /* Índice de conclusão do Host */
    VirtioUsedElem ring[VRING_SIZE];
} VirtioUsed;

/* Estrutura de Comunicação Paravirtualizada virtio */
typedef struct {
    VirtioDesc desc_table[VRING_SIZE];
    VirtioAvail avail_ring;
    VirtioUsed used_ring;
    uint16_t last_avail_idx; /* Ponteiro de consumo do Host */
    uint64_t total_vm_exits;
    uint64_t bytes_transferred;
} Virtqueue;

/* Métricas de benchmark */
typedef struct {
    uint64_t total_ops;
    uint64_t vm_exits;
    double time_elapsed_ms;
    double iops;
} BenchmarkResult;

/* Inicializa a virtqueue */
void init_virtqueue(Virtqueue *vq) {
    memset(vq, 0, sizeof(Virtqueue));
}

/* ============================================================================
 * 1. SIMULAÇÃO DE I/O EMULADO LEGADO (IDE / Placa de Rede e1000)
 * ============================================================================ */
BenchmarkResult simulate_emulated_io(int total_requests) {
    BenchmarkResult res = {0};
    res.total_ops = total_requests;
    
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);

    /* Na emulação IDE ou e1000 clássica, cada operação exige:
     * 1 escrita no registrador de endereço (porta 0x1F0) -> VM-Exit
     * 1 escrita no registrador de tamanho/setor (porta 0x1F2) -> VM-Exit
     * 1 escrita no comando de execução (porta 0x1F7) -> VM-Exit
     * 1 leitura do registrador de status de conclusão -> VM-Exit
     * Total: Mínimo de 4 VM-Exits por bloco de dados! */
    for (int i = 0; i < total_requests; i++) {
        res.vm_exits += 4;
        /* Simula o tempo de troca de contexto de 4 VM-Exits */
        for (volatile int j = 0; j < 400; j++);
    }

    clock_gettime(CLOCK_MONOTONIC, &end);
    res.time_elapsed_ms = (end.tv_sec - start.tv_sec) * 1000.0 + 
                          (end.tv_nsec - start.tv_nsec) / 1000000.0;
    res.iops = (res.total_ops / res.time_elapsed_ms) * 1000.0;
    return res;
}

/* ============================================================================
 * 2. SIMULAÇÃO DE I/O PARAVIRTUALIZADO (virtio com Virtqueues)
 * ============================================================================ */
/* O Convidado prepara descritores e insere na fila sem sair da VM */
void virtio_guest_submit_request(Virtqueue *vq, uint16_t desc_idx, uint64_t gpa, uint32_t len) {
    vq->desc_table[desc_idx].addr = gpa;
    vq->desc_table[desc_idx].len = len;
    vq->desc_table[desc_idx].flags = 0;
    
    /* Insere o índice no Available Ring */
    uint16_t avail_slot = vq->avail_ring.idx % VRING_SIZE;
    vq->avail_ring.ring[avail_slot] = desc_idx;
    vq->avail_ring.idx++;
}

/* O Convidado aciona a porta Doorbell (apenas UMA notificação para todo o lote) */
void virtio_kick_doorbell(Virtqueue *vq) {
    vq->total_vm_exits++; /* Um único VM-Exit dispara o processamento de todo o anel! */
    
    /* O Host (vhost no kernel do hospedeiro) consome as requisições em memória */
    while (vq->last_avail_idx != vq->avail_ring.idx) {
        uint16_t desc_head = vq->avail_ring.ring[vq->last_avail_idx % VRING_SIZE];
        uint32_t len = vq->desc_table[desc_head].len;
        
        /* Simula processamento direto DMA pelo host */
        vq->bytes_transferred += len;
        
        /* Devolve para o Used Ring */
        uint16_t used_slot = vq->used_ring.idx % VRING_SIZE;
        vq->used_ring.ring[used_slot].id = desc_head;
        vq->used_ring.ring[used_slot].len = len;
        vq->used_ring.idx++;
        
        vq->last_avail_idx++;
    }
}

BenchmarkResult simulate_virtio_io(int total_requests, int batch_size) {
    BenchmarkResult res = {0};
    res.total_ops = total_requests;
    Virtqueue vq;
    init_virtqueue(&vq);

    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);

    int queued = 0;
    for (int i = 0; i < total_requests; i++) {
        uint16_t desc_idx = i % VRING_SIZE;
        virtio_guest_submit_request(&vq, desc_idx, 0x10000000 + i * 4096, 4096);
        queued++;

        /* Dispara o Doorbell apenas quando o lote estiver completo ou no final */
        if (queued == batch_size || i == total_requests - 1) {
            virtio_kick_doorbell(&vq);
            queued = 0;
            for (volatile int j = 0; j < 100; j++); /* Simula I/O otimizado */
        }
    }

    clock_gettime(CLOCK_MONOTONIC, &end);
    res.vm_exits = vq.total_vm_exits;
    res.time_elapsed_ms = (end.tv_sec - start.tv_sec) * 1000.0 + 
                          (end.tv_nsec - start.tv_nsec) / 1000000.0;
    res.iops = (res.total_ops / res.time_elapsed_ms) * 1000.0;
    return res;
}

int main(void) {
    printf("####################################################################\n");
    printf("# SIMULADOR DE VIRTUALIZAÇÃO DE I/O: EMULAÇÃO VS VIRTIO (VRING)    #\n");
    printf("# Disciplina: Sistemas Operacionais - UniFEF                       #\n");
    printf("# Docente   : Prof. Me. Guilherme de Morais                        #\n");
    printf("####################################################################\n\n");

    int total_io_requests = 10000;
    int batch_size = 16;

    printf("Iniciando Teste com %d Operações de I/O (Blocos de 4 KB)...\n\n", total_io_requests);

    printf("[1] Executando simulação de I/O Emulado (Controladora Legada IDE/e1000)...\n");
    BenchmarkResult emu_res = simulate_emulated_io(total_io_requests);
    printf("    -> Concluído em %.2f ms | VM-Exits gerados: %lu\n\n",
           emu_res.time_elapsed_ms, emu_res.vm_exits);

    printf("[2] Executando simulação de I/O Paravirtualizado virtio (Batching = %d)...\n", batch_size);
    BenchmarkResult virtio_res = simulate_virtio_io(total_io_requests, batch_size);
    printf("    -> Concluído em %.2f ms | VM-Exits gerados: %lu\n\n",
           virtio_res.time_elapsed_ms, virtio_res.vm_exits);

    printf("\n====================================================================\n");
    printf("TABELA COMPARATIVA DE DESEMPENHO DE ENTRADA E SAÍDA\n");
    printf("====================================================================\n");
    printf("+----------------------+-------------------+-----------------------+\n");
    printf("| Métrica              | Emulação Clássica | virtio Paravirtualizado|\n");
    printf("+----------------------+-------------------+-----------------------+\n");
    printf("| Operações de I/O     | %17lu | %21lu |\n", emu_res.total_ops, virtio_res.total_ops);
    printf("| Total de VM-Exits    | %17lu | %21lu |\n", emu_res.vm_exits, virtio_res.vm_exits);
    
    double exit_reduction = ((double)(emu_res.vm_exits - virtio_res.vm_exits) / emu_res.vm_exits) * 100.0;
    printf("| Redução de VM-Exits  |        -          | %19.2f%% |\n", exit_reduction);
    printf("| Tempo Decorrido (ms) | %17.2f | %21.2f |\n", emu_res.time_elapsed_ms, virtio_res.time_elapsed_ms);
    printf("| Vazão Estimada (IOPS)| %17.0f | %21.0f |\n", emu_res.iops, virtio_res.iops);
    printf("+----------------------+-------------------+-----------------------+\n");
    printf("Análise de Engenharia de Sistemas:\n");
    printf("O virtio elimina a armadilha do Trap-and-Emulate para periféricos,\n");
    printf("substituindo acessos a portas por anéis de memória compartilhada.\n");
    printf("A redução drástica nos VM-Exits permite alcançar taxas de rede de 10-100 Gbps.\n");

    return 0;
}
