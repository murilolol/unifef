/*
 * Disciplina: Laboratorio de Programacao IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Resolucao dos Exercicios Praticos - Dominio Biblioteca
 *
 * Como executar:
 * 1. Compile e execute esta classe no Java 21.
 * 2. Teste o endpoint disponivel em: http://localhost:8080/api/health
 */

package com.curso.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
        System.out.println("=== Aplicacao Biblioteca 2026 iniciada com sucesso ===");
    }
}

// Exercicios 1 e 3: Implementacao do Controller de Saude e demonstracao dos DTOs do Dominio
@RestController
@RequestMapping("/api")
class HealthCheckController {

    // Exercicio 3: Endpoint de verificacao de saude
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    // Exercicio 1: Endpoint auxiliar demonstrativo do dominio modelado (Livro e Categoria)
    @GetMapping("/exemplo-dominio")
    public ExemploLivroDTO obterExemploLivro() {
        CategoriaLivroDTO categoria = new CategoriaLivroDTO(1L, "Computacao", true);
        return new ExemploLivroDTO(
            100L,
            "978-85-359-0277-7",
            "Codigo Limpo",
            15,
            new BigDecimal("89.90"),
            LocalDate.now(),
            true,
            categoria
        );
    }
}

// Exercicios 1: Registros de apoio representando os contratos de dados do tema
record CategoriaLivroDTO(Long id, String descricao, Boolean ativo) {}

record ExemploLivroDTO(
    Long id,
    String isbn,
    String titulo,
    Integer quantidadeExemplares,
    BigDecimal valorReposicao,
    LocalDate dataCadastro,
    Boolean ativo,
    CategoriaLivroDTO categoria
) {}
