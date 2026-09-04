# 📋 Trabalho: 20260427 - Trabalho de Programação (2 pontos)

> **Professor:** Prof. Jefferson Passerini
> **Disciplina:** Laboratório de Programação III (3º Semestre)
> **Prazo de Entrega:** 08/05/2026 às 02:59
> **Pontuação Máxima:** 100 pontos

## Instruções da Atividade
Trabalho de Programação - Java Web

Construir um programa com java web utilizando servlets que faça a manutenção de um cadastro de Livros.

Classe: Livro
- id
- nomeLivro
- isbn
- autor
- dataPublicação
- valorLivro

O seu software deve exibir uma lista de livros cadastrados.

Extra: 
- Implementar a manutenção de livros - incluir, alterar e excluir livros.
- Colocar o projeto fo GIthub

Fazer o trabalho em Grupo de 3 alunos.

## Modelagem da entidade `Livro`

O enunciado fixa os seis atributos que a classe `Livro` deve possuir. O esquema de banco de dados anexo (`schema.sql`, nesta mesma pasta) já traduz esses atributos para uma tabela relacional:

```sql
CREATE TABLE livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_livro VARCHAR(150) NOT NULL,
    isbn VARCHAR(50) NOT NULL UNIQUE,
    autor VARCHAR(100) NOT NULL,
    data_publicacao DATE NOT NULL,
    valor_livro DECIMAL(10, 2) NOT NULL
);
```

## Arquitetura sugerida (mesmo padrão das Aulas 04-06)

Este trabalho aplica, sobre a entidade `Livro`, o mesmo padrão de camadas (Servlet Controller → DAO → JDBC → Banco) construído passo a passo em [Aula 04](../../Aulas/Aula%2004%20-%20Conex%C3%A3o%20com%20o%20Banco%20de%20Dados/detalhes.md), [Aula 05](../../Aulas/Aula%2005%20-%20Listagem%20de%20Usu%C3%A1rios/detalhes.md) e [Aula 06](../../Aulas/Aula%2006%20-%20Implementando%20o%20CRUD/detalhes.md):

1. **Model** (`Livro.java`) — os seis atributos do enunciado, com construtor, getters e setters.
2. **DAO** (`LivroDAO.java`) — `inserir`, `listar`, `alterar`, `excluir`, reutilizando a classe `Conexao` da Aula 04.
3. **Controller** (`LivroServlet.java`) — captura os parâmetros do formulário e decide entre incluir/alterar (ver Aula 06).
4. **View** (`listarLivros.jsp`) — tabela HTML com a listagem, podendo usar `jquery.maskMoney.min.js` (ver Aula 03) para exibir/capturar `valorLivro` com formatação monetária.

## Requisitos e critérios de entrega

- **Obrigatório:** exibir a lista de livros cadastrados (`Read`).
- **Extra (valorizado na nota):** manutenção completa — incluir, alterar e excluir livros (CRUD completo).
- **Extra (valorizado na nota):** publicar o projeto no GitHub, versionado.
- **Formato:** trabalho em grupo de até 3 alunos.

## Material anexo

- [`schema.sql`](./schema.sql) — script de criação da tabela `livros` e dados iniciais de teste (`INSERT`).

> **Nota de fonte:** o enunciado acima é o texto integral publicado pelo professor na atividade do Classroom — não há `.docx` anexado a esta atividade. A seção "Arquitetura sugerida" organiza esse enunciado à luz do padrão de código já ensinado nas Aulas 04-06 desta disciplina, sem introduzir nenhum requisito além dos listados nas Instruções da Atividade.
