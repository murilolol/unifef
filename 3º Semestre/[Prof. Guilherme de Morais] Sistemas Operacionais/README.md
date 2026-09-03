```markdown
# 🎓 [Prof. Guilherme de Morais] Sistemas Operacionais
> **Curso:** Bacharelado em Sistemas de Informação (UniFEF)  
> **Semestre:** 3º Semestre  
> **Professor:** Guilherme de Morais  

---

## 🎯 Objetivos de Aprendizagem e Ementa

A disciplina de **Sistemas Operacionais** tem como propósito fundamental capacitar o aluno do 3º semestre de Sistemas de Informação a compreender o funcionamento interno e a gestão dos recursos de um computador. 

### O que você vai aprender:
- **Evolução Histórica:** Compreender como os sistemas operacionais evoluíram desde as primeiras gerações até os sistemas modernos e distribuídos.
- **Gerenciamento de Processos e Threads:** Entender o ciclo de vida dos processos, escalonamento CPU, concorrência e sincronização.
- **Gerenciamento de Memória:** Dominar conceitos de memória real, virtual, paginação e segmentação.
- **Concorrência Avançada:** Analisar mecanismos como Monitores, Semáforos e a problematica de Deadlocks.
- **Virtualização:** Explorar tecnologias de abstração de hardware e máquinas virtuais aplicadas ao mercado atual.

---

## 🏗️ Arquitetura e Modelagem do Conhecimento

Abaixo está o mapa mental estrutural da disciplina, interligando os principais tópicos abordados nas aulas e trabalhos:

```mermaid
graph TD
    SO[Sistemas Operacionais] --> A[Evolução & Conceitos]
    SO --> B[Processos & Threads]
    SO --> C[Gerenciamento de Memória]
    SO --> D[Concorrência & Sincronização]
    SO --> E[Trabalhos Práticos]

    A --> A1[Sistemas Operacionais e Evolução]
    
    B --> B1[Conceitos de Processos]
    B --> B2[Blocos de Controle - PCB]
    B --> B3[Gerenciamento de Processos]

    C --> C1[Organização e Memória Real]
    
    D --> D1[Monitores]
    D --> D2[Deadlock]

    E --> E1[Softwares de Virtualização]
```

---

## 📂 Estrutura das Pastas e Organização

O repositório está organizado de forma modular para facilitar a navegação e o estudo contínuo:

- **`Aulas/`**: Materiais didáticos oficiais, slides das aulas e comunicados importantes.
- **`Trabalhos/`**: Atividades avaliativas com orientações e gabaritos/resoluções em código.
- **`Provas/`**: Avaliações semestrais anteriores e critérios de correção.
- **`Resumos-IA/`**: Resumos executivos, simulados comentados, trechos de códigos explicativos, flashcards em formato Anki (`.tsv`), *CheatSheets* rápidos e apresentações em PPTX.

---

## 🚀 Como Estudar com Este Material

1. **Pré-aula:** Leia os resumos e slides correspondentes na pasta `Aulas/` antes de ir para a aula expositiva.
2. **Fixação:** Utilize os materiais da pasta `Resumos-IA/` (como os flashcards e cheat sheets) para revisões rápidas.
3. **Prática:** Dedique atenção especial aos trabalhos práticos na pasta `Trabalhos/`, pois eles consolidam a teoria com cenários reais de mercado.
4. **Autoavaliação:** Teste seus conhecimentos utilizando os simulados comentados disponíveis.

---

# 📚 CONTEÚDO ACADÊMICO

---

# 📘 Sistemas operacionais e evolução

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais  

### 📌 Visão Geral
Introdução aos conceitos fundamentais de computação, o papel do Sistema Operacional como máquina estendida e gerenciador de recursos, além de sua evolução histórica (sistemas batch, tempo compartilhado, sistemas pessoais e distribuídos).

---

# 📘 CONCEITOS DE PROCESSOS

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais  

### 📌 Visão Geral
Estudo do conceito central de processos em sistemas operacionais. Diferença entre programa (estático) e processo (dinâmico), estados de um processo (execução, pronto, bloqueado) e as operações básicas de criação e término.

---

# 📘 BLOCOS DE CONTROLE 

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais  

### 📌 Visão Geral
Aprofundamento sobre o **PCB (Process Control Block)** ou Bloco de Controle de Processo. Estrutura de dados utilizada pelo sistema operacional para armazenar todas as informações vitais sobre um processo em execução ou pausado.

---

# 📘 AULA3 - GERENCIAMENTO DE PROCESSO

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais  

### 📌 Visão Geral
Análise aprofundada dos algoritmos de escalonamento da CPU, transições de estado avançadas e comunicação entre processos (IPC - Inter-Process Communication).

---

# 📘 Organizao e Gerenciamento da Memria Real

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais  

### 📌 Visão Geral
Como o sistema operacional interage com a memória física (RAM). Conceitos de alocação contígua, partições fixas e dinâmicas, fragmentação de memória e técnicas iniciais de gerenciamento.

---

# 📘 Monitores e Deadlock em Sistemas Operacionais

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais  

### 📌 Visão Geral
- **Monitores:** Estruturas de sincronização de alto nível que encapsulam dados e procedimentos para acesso seguro por múltiplas threads/processos.
- **Deadlock (Impasse):** Condição em que um conjunto de processos está bloqueado porque cada processo segura um recurso e aguarda outro recurso que está sendo segurado por outro processo da mesma cadeia. Estudo das condições de Coffman, prevenção, detecção e recuperação.

---

# 📋 Trabalho: Softwares de Virtualização 

> **Professor:** Prof. Guilherme de Morais  
> **Disciplina:** Sistemas Operacionais (3º Semestre)  
> **Prazo de Entrega:** 29/04/2026 às 02:59  
> **Pontuação Máxima:** 100 pontos  

## 📝 Instruções da Atividade

### 1. Contextualização
A virtualização é um dos pilares da infraestrutura moderna de TI e da computação em nuvem. Ela permite que múltiplos sistemas operacionais rodem simultaneamente em uma única máquina física através de um *Hypervisor*.

### 2. Objetivo
O aluno deverá pesquisar, instalar e documentar o uso de softwares de virtualização (como VirtualBox, VMware, KVM ou Hyper-V), criando uma máquina virtual (VM), configurando redes e demonstrando o gerenciamento de recursos computacionais (CPU e Memória).

### 3. Entregáveis
- Relatório técnico em PDF contendo o passo a passo da instalação e configuração.
- Evidências visuais (prints de tela) da máquina virtual em funcionamento.
- Conclusão crítica comparando os tipos de *Hypervisors* (Tipo 1 vs Tipo 2).
```