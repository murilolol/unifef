/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Avaliação 1 - Encapsulamento e Modelagem de Classes
 * Exercício 1
 *
 * Como compilar e executar:
 * javac Exercicio1Funcionario.java
 * java Exercicio1Funcionario
 */

public class Exercicio1Funcionario {

    static class Funcionario {
        private String nome;
        private int matricula;
        private double salarioBase;

        public Funcionario(String nome, int matricula, double salarioBase) {
            setNome(nome);
            setMatricula(matricula);
            setSalarioBase(salarioBase);
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do funcionário não pode ser nulo ou vazio.");
            }
            this.nome = nome.trim();
        }

        public int getMatricula() {
            return matricula;
        }

        public void setMatricula(int matricula) {
            if (matricula <= 0) {
                throw new IllegalArgumentException("Matrícula deve ser um número positivo maior que zero.");
            }
            this.matricula = matricula;
        }

        public double getSalarioBase() {
            return salarioBase;
        }

        public void setSalarioBase(double salarioBase) {
            if (salarioBase <= 0.0) {
                throw new IllegalArgumentException("Salário base deve ser estritamente maior que zero.");
            }
            this.salarioBase = salarioBase;
        }

        public double calcularSalario() {
            return salarioBase;
        }

        public void exibirContracheque() {
            System.out.println("----------------------------------------");
            System.out.println("Matrícula: " + matricula);
            System.out.println("Nome: " + nome);
            System.out.printf("Salário Base: R$ %.2f%n", salarioBase);
            System.out.printf("Salário Final: R$ %.2f%n", calcularSalario());
            System.out.println("----------------------------------------");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Exercício 1: Encapsulamento e Modelagem de Funcionário ===");

        try {
            Funcionario f1 = new Funcionario("Ana Beatriz", 101, 3500.00);
            Funcionario f2 = new Funcionario("Carlos Eduardo", 102, 4200.00);

            f1.exibirContracheque();
            f2.exibirContracheque();

            System.out.println("Atualizando o salário base do colaborador 101...");
            f1.setSalarioBase(3900.00);
            f1.exibirContracheque();

        } catch (IllegalArgumentException e) {
            System.err.println("Erro na validação de dados: " + e.getMessage());
        }
    }
}
