# 📘 Apostila Prática: Laboratório de Programação IV
**Professor Responsável:** Prof. Jefferson Passerini  
**Semestre:** 4º Semestre  
**Módulo Base:** Configuração de Ambiente, Versionamento e Arquitetura de Projetos Base (`suporteos2026`)

---

## 🎯 Apresentação da Disciplina

Esta apostila prática foi desenvolvida com base nas diretrizes da disciplina de **Laboratório de Programação IV**, ministrada pelo **Prof. Jefferson Passerini**. O foco do laboratório no 4º semestre é a consolidação de boas práticas de engenharia de software, controle de versão avançado com Git/GitHub, modelagem de sistemas robustos e implementação de código limpo utilizando as tecnologias padrão da indústria (como Java, TypeScript, C e SQL, dependendo do escopo do projeto base `suporteos2026`).

---

## 🛠️ Módulo 1: Configuração do Repositório Oficial e Boas Práticas Git

Como ponto de partida para todas as implementações da disciplina, utilizamos o repositório base fornecido pelo professor: **[github.com/jeffersonarpasserini/suporteos2026](https://github.com/jeffersonarpasserini/suporteos2026)**.

### 📐 Diagrama de Fluxo de Trabalho (Git Workflow)
```text
[Repositório Remoto: suporteos2026]
       ▲                         │
       │ (push)                  │ (clone / pull)
       │                         ▼
[Branch: feature/xxx] ──(merge)──► [Branch: main / local]
```

---

## 💻 Módulo 2: Implementação Prática e Padrões de Projeto

Abaixo, apresentamos uma implementação padrão estruturada para o ecossistema da disciplina, aplicando **Princípios SOLID**, **Separação de Responsabilidades** e **Comentários Linha a Linha**.

### 📄 Exemplo em TypeScript / Node.js (Arquitetura Orientada a Serviços)

Imagine o cenário de controle de Ordens de Serviço (OS) alinhado ao contexto do repositório `suporteos2026`.

```typescript
// ==========================================
// ARQUIVO: OrdemServicoService.ts
// Descrição: Serviço responsável pelas regras 
// de negócio de Ordem de Serviço.
// ==========================================

// Importação de interfaces para tipagem estática rigorosa
import { IOrdemServico } from './IOrdemServico';
import { IRepository } from './IRepository';

export class OrdemServicoService {
    
    // Injeção de dependência do repositório de dados para desacoplamento
    constructor(private readonly osRepository: IRepository<IOrdemServico>) {}

    /**
     * Método responsável por abrir uma nova Ordem de Serviço
     * @param dados Objeto contendo os dados iniciais da OS
     * @returns A Ordem de Serviço criada e persistida
     */
    public async criarOrdemServico(dados: Omit<IOrdemServico, 'id' | 'dataCriacao'>): Promise<IOrdemServico> {
        
        // Validação básica de regra de negócio: Descrição obrigatória
        if (!dados.descricao || dados.descricao.trim() === '') {
            throw new Error('A descrição da Ordem de Serviço não pode ser vazia.');
        }

        // Montagem do objeto completo aplicando valores padrão (Data atual e Status Inicial)
        const novaOS: IOrdemServico = {
            id: this.gerarIdUnico(),
            descricao: dados.descricao.trim(),
            status: 'ABERTA',
            dataCriacao: new Date()
        };

        // Persistência dos dados utilizando o repositório injetado
        const osSalva = await this.osRepository.salvar(novaOS);

        // Retorno da entidade persistida
        return osSalva;
    }

    /**
     * Método auxiliar privado para geração de identificador único (Simulação UUID)
     */
    private gerarIdUnico(): string {
        return Math.random().toString(36).substring(2, 9);
    }
}
```

---

## 🔍 Módulo 3: Scripts de Banco de Dados Relacional (SQL)

Para suportar o backend de sistemas de Ordem de Serviço (`suporteos2026`), é fundamental estruturar o banco de dados relacional com integridade referencial e indexação adequada.

```sql
-- =====================================================
-- SCRIPT SQL: Estruturação Inicial do Banco de Dados
-- Disciplina: Laboratório de Programação IV
-- =====================================================

-- Criação da tabela de Clientes
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de Ordens de Serviço vinculada a Clientes
CREATE TABLE ordens_servico (
    id SERIAL PRIMARY KEY,
    cliente_id INT NOT NULL,
    descricao TEXT NOT NULL,
    status VARCHAR(30) DEFAULT 'ABERTA', -- ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA
    valor_total NUMERIC(10, 2) DEFAULT 0.00,
    data_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Definição de Chave Estrangeira com integridade referencial
    CONSTRAINT fk_cliente
        FOREIGN KEY (cliente_id) 
        REFERENCES clientes(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Criação de índice para otimização de buscas por status e cliente
CREATE INDEX idx_os_status ON ordens_servico(status);
CREATE INDEX idx_os_cliente ON ordens_servico(cliente_id);
```

---

## 📝 Lista de Exercícios Práticos Recomendados

1. **Clonagem e Configuração:** Faça o clone do repositório oficial da disciplina (`https://github.com/jeffersonarpasserini/suporteos2026`) em sua máquina local.
2. **Versionamento:** Crie uma branch própria no padrão `feature/nome-sobrenome` para realizar suas implementações semanais.
3. **Refatoração:** Aplique o princípio da Responsabilidade Única (SRP) nas classes de conexão com o banco de dados fornecidas no esqueleto do repositório.
4. **Testes Unitários:** Escreva testes cobrindo o método `criarOrdemServico` demonstrado no Módulo 2 utilizando Jest ou framework equivalente da linguagem escolhida em aula.