# 🎓 Prof. Marcelo Boer - Engenharia de Software I
> **Semestre:** 3º Semestre | UniFEF  
> **Professor Responsável:** Prof. Esp. Marcelo Tadeu Boer  
> **Projeto Base:** Aplicativo Móvel **Desapega Já**

---

## 🎯 Objetivos de Aprendizagem e Ementa

A disciplina de **Engenharia de Software I** tem como objetivo capacitar o aluno nos fundamentos do ciclo de vida de desenvolvimento de sistemas (SDLC), aplicando técnicas de Engenharia e Modelagem de Software orientadas à prática de mercado. 

### Ementa e Tópicos Abordados:
1. **Introdução aos Processos de Software:** Estudo comparativo e apresentação de Modelos de Processos de Desenvolvimento (Cascata, Ágil, Scrum, RUP, etc.).
2. **Engenharia de Requisitos:** Levantamento, especificação e classificação de Requisitos Funcionais e Não-Funcionais através do contexto de aplicações reais (*Desapega Já*).
3. **Análise Orientada a Objetos (UML):**
   * Diagrama de Classes (Atributos, Associações e Entidades).
   * Diagrama de Casos de Uso (Atores Primários, Fluxos Normais e Alternativos).
   * Descrição Detalhada de Casos de Uso (Quadros de Especificação: Logar, Cadastrar, Listar, Carregar, Alterar, Excluir).
4. **Ferramentas de Modelagem:** Utilização prática do *Astah UML* para desenho de diagramas padronizados.

---

## 🏗️ Modelagem do Conhecimento

Abaixo está representado o Diagrama de Classes conceitual estruturado para o aplicativo **Desapega Já**, contemplando as entidades centrais mapeadas no processo de abstração da disciplina:

```mermaid
classDiagram
    class Interessado {
        +int id_usuario
        +String nome_completo
        +String email
        +String telefone_whatsapp
        +String senha_acesso
        +String cidade
        +String estado
        +String foto_perfil
        +String cpf
        +Date data_nascimento
        +String bairro
        +Date data_cadastro
        +realizarLogin()
        +cadastrar()
        +editarPerfil()
    }

    class Anunciante {
        +int id_usuario
        +String nome_completo
        +String email
        +String telefone_whatsapp
        +String senha_acesso
        +String cidade
        +String estado
        +String foto_perfil
        +String cpf
        +Date data_nascimento
        +String bairro
        +Date data_cadastro
        +cadastrarProduto()
        +cadastrarAnuncio()
    }

    class Anuncio {
        +int id_anuncio
        +String fotos
        +String descricao
        +float preco
        +cadastrarAnuncio()
        +editarAnuncio()
        +excluirAnuncio()
    }

    class Categoria {
        +int id_categoria
        +String nome_categoria
    }

    class Compra {
        +int id_compra
        +Date data_compra
        +float valor_compra
        +realizarCompra()
    }

    class HistoricoContatos {
        +int id_historico
        +Date data_contato
        +trocarMensagens()
    }

    Anunciante --> Anuncio : cadastra / gerencia
    Anuncio --> Categoria : pertence a
    Compra --> Anuncio : contém produtos
    Compra --> Interessado : efetuada por
    Compra --> Anunciante : vendida por
    HistoricoContatos --> Interessado : envolve
    HistoricoContatos --> Anunciante : envolve
```

---

## 📂 Estrutura de Pastas

Estrutura real deste repositório, alinhada ao padrão das demais matérias do acervo:

```text
.
├── 📂 Aulas/                          # Notas de aula, materiais do professor e recursos
│   ├── 📄 2026-02-24 - Contexto do Aplicativo.md
│   ├── 📄 2026-02-24 - Documento ESM1 do dia 24022026.md
│   ├── 📄 2026-03-03 - Licença Astah UML.md
│   ├── 📄 2026-03-24 - Forms contendo questões de revisão.md
│   ├── 📄 2026-03-31 - Apresentação Métodos de Engenharia de S.md
│   ├── 📄 2026-05-12 - Documento desenvolvido em sala de aula.md
│   ├── 📄 2026-06-02 - Desenvolver a descrição dos seguintes.md
│   ├── 📄 2026-06-02 - Exemplo Descrição de Caso de Uso.md
│   ├── 📄 2026-06-09 - Modelo de Apresentação a ser seguido.md
│   ├── 📄 links-recursos.md
│   └── 📎 ENGENHARIA E MODELAGEM DE SOFTWARE I.docx/.pdf, ExemploDescriçãoDiagramaDCU.docx, ModeloEntregaAv2Final.docx, licença Astah UML (.zip)
├── 📂 Trabalhos/                      # Atividades avaliativas resolvidas
│   ├── 📂 APRESENTAÇÃO MÉTODOS DE PROCESSOS DE SOFTWARES/
│   ├── 📂 ATIVIDADE AV2/
│   ├── 📂 ATIVIDADE AVALIATIVA 01 - ESTUDO DE CASO/
│   ├── 📂 Atividade Avaliativa Modelos de Processos de Software/
│   └── 📂 Atividade Avaliativa Prática 02 - Atores e Diagrama de Classes Fase de Análise/
├── 📂 Provas/                         # Avaliações e resoluções
│   └── 📂 Avaliação Av1/
└── 📂 Resumos-IA/                     # Material de apoio gerado por IA — tudo em um único README
    ├── 📄 README.md                   # Resumo, exercícios, simulado, cheatsheet e diagramas
    ├── 📊 Slides-Revisao-[Prof. Marcelo Boer] Engenharia de Software I.pptx
    ├── 📇 flashcards-anki.tsv
    └── 🤖 dataset-estudo-qa.jsonl
```

Cada subpasta de `Trabalhos/` e `Provas/` contém um `detalhes.md` com o enunciado/contexto original e os arquivos entregues.

---

## 🚀 Guia de Estudos

Para obter sucesso na disciplina ministrada pelo **Prof. Marcelo Boer**, siga o roteiro de estudos recomendado:

1. **Compreensão do Domínio do Problema:**
   * Leia atentamente o documento do **Contexto I: Aplicativo Móvel Desapega Já**. Entenda o modelo de negócio voltado à economia colaborativa, reutilização e consumo consciente.
2. **Levantamento de Requisitos:**
   * Analise a diferença entre **Requisitos Funcionais** (ex: cadastro de produtos, busca por proximidade, troca de mensagens) e **Requisitos Não-Funcionais** (ex: fácil usabilidade, segurança, integridade de dados).
3. **Modelagem UML Prática:**
   * Utilize a licença do **Astah UML** fornecida nas aulas para desenhar Diagramas de Classes, Diagramas de Atores e Diagramas de Casos de Uso (DCU).
   * Treine a escrita dos quadros de especificação de Casos de Uso cobrindo os métodos fundamentais solicitados em laboratório: **Logar, Cadastrar, Listar, Carregar, Alterar e Excluir**.
4. **Avaliações (AV1 & AV2):**
   * Responda aos questionários de revisão disponibilizados via Google Forms (*ver `Aulas/links-recursos.md`*).
   * Siga rigorosamente o template do documento `ModeloEntregaAv2Final.docx` para a elaboração e formatação da entrega final do projeto prático.
5. **Revise com o Resumos-IA:** [`Resumos-IA/README.md`](./Resumos-IA/README.md) reúne resumo executivo, exercícios comentados, simulado com gabarito, cheat sheet e diagramas — tudo em um único documento — além do deck de slides (`.pptx`), flashcards para Anki (`.tsv`) e dataset de perguntas e respostas (`.jsonl`).