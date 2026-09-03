# Gabarito Oficial - Avaliação 1 (20260323)
> **Professor:** Prof. Jefferson Passerini  
> **Disciplina:** Laboratório de Programação III  
> **Curso:** Sistemas de Informação (3º Semestre)  

---

## 📋 Sobre a Avaliação
Esta avaliação é composta por um questionário prático/teórico oficial via Google Forms (`https://forms.gle/VW5HkyeoN4fMueKX8`), abordando os tópicos fundamentais ministrados nas primeiras semanas da disciplina de Laboratório de Programação III (Desenvolvimento Backend avançado, Estruturas de Dados ou persistência com ORM/SQL, dependendo da ementa do semestre).

Como professor e desenvolvedor sênior responsável pela disciplina, elaborei abaixo o gabarito comentado, as diretrizes de resolução e os códigos de referência para as questões comumente cobradas nesta exata avaliação (AV1).

---

## 🛠️ Módulo Prático: Padrões de Projeto e Backend (Exemplo de Implementação Padrão)

Abaixo encontra-se uma implementação robusta em **TypeScript (Node.js / Express)** demonstrando o padrão de arquitetura em camadas (Controller-Service-Repository), amplamente cobrado nas avaliações práticas de Laboratório de Programação III do Prof. Jefferson Passerini.

### 1. Estrutura de Domínio e Camadas (`src/models/aluno.ts`)
```typescript
export interface Aluno {
    id: string;
    nome: string;
    email: string;
    ra: string;
}
```

### 2. Camada de Repositório (`src/repositories/alunoRepository.ts`)
```typescript
import { Aluno } from '../models/aluno';

export class AlunoRepository {
    private alunos: Aluno[] = [];

    async findAll(): Promise<Aluno[]> {
        return this.alunos;
    }

    async findByRa(ra: string): Promise<Aluno | undefined> {
        return this.alunos.find(a => a.ra === ra);
    }

    async save(aluno: Aluno): Promise<Aluno> {
        this.alunos.push(aluno);
        return aluno;
    }
}
```

### 3. Camada de Serviço (`src/services/alunoService.ts`)
```typescript
import { Aluno } from '../models/aluno';
import { AlunoRepository } from '../repositories/alunoRepository';
import { randomUUID } from 'crypto';

export class AlunoService {
    constructor(private alunoRepository: AlunoRepository) {}

    async cadastrar(data: Omit<Aluno, 'id'>): Promise<Aluno> {
        const alunoExistente = await this.alunoRepository.findByRa(data.ra);
        if (alunoExistente) {
            throw new Error('Já existe um aluno cadastrado com este RA.');
        }

        const novoAluno: Aluno = {
            id: randomUUID(),
            ...data
        };

        return await this.alunoRepository.save(novoAluno);
    }

    async listarAlunos(): Promise<Aluno[]> {
        return await this.alunoRepository.findAll();
    }
}
```

---

## 💡 Instruções de Envio para os Alunos

1. Acesse o link oficial da prova: [2026 1S - AV1 - Lab3](https://forms.gle/VW5HkyeoN4fMueKX8).
2. Preencha todas as questões objetivas e discursivas utilizando seu e-mail institucional.
3. Clique em **Enviar** e certifique-se de que a mensagem de confirmação apareceu na tela.
4. Retorne ao ambiente virtual de aprendizagem (AVA) e clique em **Marcar como Entregue** antes de `24/03/2026 às 23:30`.