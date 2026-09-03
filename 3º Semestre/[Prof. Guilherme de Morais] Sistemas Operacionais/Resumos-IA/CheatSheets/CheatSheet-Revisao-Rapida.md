# 🚀 CheatSheet: Sistemas Operacionais (Revisão Acadêmica)

---

### 1. ⚙️ Tipos de Escalonamento e Estados de Processos
* **Escalonamento:** Gerencia a fila de processos para uso da CPU através do **Despachante** (entidade que faz o *Despacho*).
* **Preempção vs. Cooperativo:** Sistemas modernos usam **Quantum** (Temporizador de Intervalo em hardware) para preempção. Se o quantum expira, o SO força a saída da CPU. Sem quantum (sistemas antigos), dependia-se de liberação voluntária (risco de laço infinito/monopólio).
* **Modelo de Estados:**
  * **Pronto ⇄ Execução:** Feito pelo *Despacho* (Pronto $\rightarrow$ Execução) ou Expiração de Quantum (Execução $\rightarrow$ Pronto). *(Acordados)*
  * **Execução $\rightarrow$ Bloqueado:** Voluntário (início de E/S ou espera por evento). *(Adormecido)*
  * **Bloqueado $\rightarrow$ Pronto:** Conclusão do evento/E/S. *(Adormecido $\rightarrow$ Acordado)*

---

### 2. 🗂️ Bloco de Controle de Processo (PCB) & Threads
* **PCB (Process Control Board / Descritor):** Estrutura criada pelo SO para gerenciar cada processo. Contém:
  * Identificação (**PID** - *Process Identification Number*)
  * **Estado do Processo** (Pronto, Execução, Bloqueado)
  * **Contador de Programa (PC):** Próxima instrução a executar
  * **Contexto de Execução:** Conteúdo dos registradores
  * **Prioridade** e **Credenciais** (recursos acessíveis)
  * **Filiação:** Ponteiros para Processo-Pai e Processos-Filhos
  * **Outros:** Arquivos abertos, ponteiros de E/S.
* **Chaveamento de Contexto (Context Switch):** O SO salva o contexto do processo atual no PCB, carrega o contexto do próximo processo pronto e atualiza registradores de hardware.
* **Interrupções:** 
  * *Síncronas:* Ação ilegal ou acesso a memória protegida.
  * *Assíncronas:* Dispositivos de hardware (teclado, mouse, temporizador). Substituem a **Sondagem (Polling)**.

---

### 3. 🖥️ Memória Virtual e Virtualização (Hipervisores)
* **Gerenciamento de Memória Real e Virtual:** Abstração que permite executar processos maiores que a RAM física, mapeando páginas virtuais em quadros de memória real/disco.
* **Hipervisores (Virtual Machine Monitors - VMM):**
  * **Tipo 1 (Bare-Metal):** Executa direto no hardware físico (ex: *VMware ESXi, Proxmox, KVM*).
  * **Tipo 2 (Hosted):** Executa como aplicação sobre um SO hospedeiro (ex: *Oracle VirtualBox, VMware Workstation*).
* **Infraestrutura como Código (IaC):** Ferramentas como *Vagrant* automatizam o provisionamento de ambientes virtuais reprodutíveis.

---

### 4. 🔒 Deadlocks (Conceitos Básicos)
* **Definição:** Estado onde um conjunto de processos está permanentemente bloqueado aguardando recursos que estão presos entre si no mesmo conjunto.
* **As 4 Condições Necessárias (Coffman):**
  1. *Exclusão Mútua:* Recurso não compartilhavel.
  2. *Posse e Espera:* Processo segura recurso e espera outro.
  3. *Não-preempção:* Recurso não pode ser forçadamente retirado.
  4. *Espera Circular:* Cadeia fechada de processos esperando recursos uns dos outros.
* **Tratamento:** Prevenção, Evitação (ex: Algoritmo do Banqueiro), Detecção/Recuperação ou Ignorar (Avestruz).