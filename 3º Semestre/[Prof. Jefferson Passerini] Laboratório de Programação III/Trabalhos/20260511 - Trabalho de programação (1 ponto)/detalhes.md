# 📋 Trabalho: 20260511 - Trabalho de Programação (1 ponto)

> **Professor:** Prof. Jefferson Passerini
> **Disciplina:** Laboratório de Programação III (3º Semestre)
> **Prazo de Entrega:** 26/05/2026 às 02:59
> **Pontuação Máxima:** 100 pontos

## Instruções da Atividade
Implemente conforme instruções

## Desafios propostos

Conforme os links do material complementar (`links-recursos.md`, nesta mesma pasta), o trabalho é dividido em dois desafios:

1. **Desafio 01 — Validação do campo e-mail:** implementar, no back-end, a validação de formato do campo `email` antes de persistir o cadastro. A validação client-side (JavaScript, ver [Aula 03](../../Aulas/Aula%2003%20-%20Estrutura%20do%20Projeto%20-%20Front-end/detalhes.md)) deve ser reforçada obrigatoriamente no servidor, pois o front-end pode ser contornado.
2. **Desafio 02 — Cadastro de Estado:** implementar o cadastro (CRUD) de uma nova entidade `Estado` no projeto, seguindo o mesmo padrão Model/DAO/Servlet já construído nas Aulas 05 e 06 para `Usuario`.

## Solução anexa — `EmailValidator.java`

O arquivo [`EmailValidator.java`](./EmailValidator.java), anexo a esta pasta, resolve o Desafio 01 com uma classe utilitária de validação por expressão regular:

```java
package com.sistema.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_REGEX =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email.trim());
        return matcher.matches();
    }
}
```

Essa classe deve ser chamada pelo Servlet/Model **antes** do `INSERT`/`UPDATE` do cadastro (ex.: `if (!EmailValidator.isValid(usuario.getEmail())) { /* rejeitar */ }`), reforçando no servidor a mesma verificação já feita visualmente no front-end.

## Desafio 02 — Estado (não implementado nos anexos)

Não há, nesta pasta, um arquivo de solução para o cadastro de `Estado` (apenas o link do desafio no Notion, listado em `links-recursos.md`). Seguindo o mesmo padrão do Desafio 01 e das Aulas 05/06, a entidade `Estado` deveria receber sua própria classe de modelo, DAO e Servlet, com pelo menos os atributos de identificação (ex.: `id`, `nome`, `sigla`).

> **Nota de fonte:** o enunciado é o texto integral do post da atividade no Classroom, complementado pelos dois links de desafio (Notion) já listados em `links-recursos.md`. Não há `.docx` anexado. Não foi encontrado, nesta pasta, código de solução do Desafio 02 (Estado) — apontado aqui como lacuna de material, e não preenchido com uma implementação fictícia.
