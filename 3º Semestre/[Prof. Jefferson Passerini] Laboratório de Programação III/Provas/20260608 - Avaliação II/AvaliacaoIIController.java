package com.jeffersonpasserini.labprog3.avaliacao2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootDataJpa;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Avaliação II - Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Curso: Sistemas de Informação
 */
@SpringBootApplication
@RestController
@RequestMapping("/api/v1/avaliacoes")
public class AvaliacaoIIController {

    private final AlunoRepository alunoRepository;

    public AvaliacaoIIController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(AvaliacaoIIController.class, args);
    }

    @GetMapping
    public ResponseEntity<List<Aluno>> listarAlunos() {
        return ResponseEntity.ok(alunoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Aluno> criarAluno(@Valid @RequestBody Aluno aluno) {
        Aluno salvo = alunoRepository.save(aluno);
        return ResponseEntity.status(201).body(salvo);
    }
}

@Entity
class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O RA é obrigatório")
    private String ra;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }
}

interface AlunoRepository extends JpaRepository<Aluno, Long> {
}