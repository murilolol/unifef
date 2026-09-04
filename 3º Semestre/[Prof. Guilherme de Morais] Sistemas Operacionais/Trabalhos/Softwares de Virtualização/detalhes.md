# Trabalho — Softwares de Virtualização

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Prazo de Entrega:** 29/04/2026 às 02:59
> **Pontuação Máxima:** 100 pontos

## Descrição da atividade

A virtualização é um dos pilares da infraestrutura moderna de TI e da computação em nuvem — permite que múltiplos sistemas operacionais rodem simultaneamente em uma única máquina física através de um *Hypervisor*.

**Objetivo:** pesquisar, instalar e documentar o uso de softwares de virtualização (VirtualBox, VMware, KVM ou Hyper-V), criando uma máquina virtual (VM), configurando redes e demonstrando o gerenciamento de recursos computacionais (CPU e Memória).

**Entregáveis solicitados:**
- Relatório técnico em PDF com o passo a passo da instalação e configuração.
- Evidências visuais (*prints* de tela) da máquina virtual em funcionamento.
- Conclusão crítica comparando os tipos de Hypervisors (Tipo 1 *vs.* Tipo 2).

## Resolução entregue

O trabalho foi resolvido em [`trabalho_virtualizacao_sistemas_operacionais.md`](./trabalho_virtualizacao_sistemas_operacionais.md), estruturado em quatro partes:

1. **Introdução teórica e arquitetural** — distinção entre Hipervisor Tipo 1 (*Bare-Metal*: VMware ESXi, Proxmox VE, KVM) e Tipo 2 (*Hosted*: Oracle VirtualBox, VMware Workstation), com diagrama comparativo em Mermaid.
2. **Estudo prático de provisionamento** — automação da criação de uma VM Ubuntu Server via **Vagrant** + **Oracle VirtualBox**, com `Vagrantfile` completo (2 GB de RAM, 2 vCPUs, redirecionamento de porta SSH/HTTP) e provisionamento via *shell script* (instalação e inicialização do Nginx).
3. **Gerenciamento de processos e recursos do Hipervisor no host** — script em Bash que verifica consumo de CPU/memória do host e identifica processos de virtualização ativos (`ps aux | grep -E "vbox|qemu|kvm"`).
4. **Conclusão** — consolidação dos conceitos de isolamento de recursos, virtualização assistida por hardware (VT-x/AMD-V) e Infraestrutura como Código (IaC).

## Arquivos entregues

- [`trabalho_virtualizacao_sistemas_operacionais.md`](./trabalho_virtualizacao_sistemas_operacionais.md) — relatório completo com teoria, `Vagrantfile` e scripts de monitoramento comentados.

> **Observação sobre o anexo `grad-direito-artigo-nota-rodape.doc`:** este arquivo, presente nesta mesma pasta, é um modelo de artigo acadêmico em normas ABNT de um curso de **Direito** (metadados do documento confirmam título "Modelo de Artigo Periódico - ABNT" e autoria da Unisinos), sem qualquer relação com Sistemas Operacionais ou com o enunciado desta atividade. Não foi utilizado como fonte e não constitui o enunciado original do trabalho — provavelmente foi anexado à pasta por engano. Não foi encontrado, na pasta, o enunciado original completo distribuído pelo professor (apenas o resumo de prazo/pontuação/entregáveis acima, já presente no material da disciplina).

## Material relacionado

- [Aula: Monitores e Deadlock em Sistemas Operacionais](../../Aulas/Monitores%20e%20Deadlock%20em%20Sistemas%20Operacionais/detalhes.md) — menciona virtualização como tecnologia de abstração de hardware.
- [README da disciplina](../../README.md)
