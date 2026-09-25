/*
 * Disciplina: Laboratorio de Programacao IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Criacao do projeto Spring Boot e Endpoint de Health Check
 *
 * Como executar:
 * 1. Certifique-se de ter o JDK 21 instalado.
 * 2. Execute a classe principal usando sua IDE ou via terminal:
 *    ./mvnw spring-boot:run
 * 3. Acesse no navegador ou cURL: http://localhost:8080/api/health
 */

package com.curso.suporteos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SuporteOsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuporteOsApplication.class, args);
    }
}

@RestController
class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
