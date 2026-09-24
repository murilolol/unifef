/**
 * Disciplina: Engenharia de Software II
 * Professor: Prof. Wesley Soares
 * Tema: Fundamentos de Projeto Orientado a Objetos
 * Como executar: javac SistemaBiblioteca.java && java SistemaBiblioteca
 */

import java.util.ArrayList;
import java.util.List;

abstract class UsuarioBiblioteca {
    private String nome;
    private String id;

    public UsuarioBiblioteca(String nome, String id) {
        this.nome = nome;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public abstract void exibirPermissoes();
}

class Estudante extends UsuarioBiblioteca {
    public Estudante(String nome, String id) {
        super(nome, id);
    }

    @Override
    public void exibirPermissoes() {
        System.out.println("Estudante: Permissao para emprestar ate 3 livros.");
    }
}

class Professor extends UsuarioBiblioteca {
    public Professor(String nome, String id) {
        super(nome, id);
    }

    @Override
    public void exibirPermissoes() {
        System.out.println("Professor: Permissao para emprestar ate 5 livros.");
    }
}

public class SistemaBiblioteca {
    public static void main(String[] args) {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        usuarios.add(new Estudante("Ana Silva", "E001"));
        usuarios.add(new Professor("Carlos Souza", "P001"));

        for (UsuarioBiblioteca u : usuarios) {
            System.out.println("Usuario: " + u.getNome());
            u.exibirPermissoes();
        }
    }
}
