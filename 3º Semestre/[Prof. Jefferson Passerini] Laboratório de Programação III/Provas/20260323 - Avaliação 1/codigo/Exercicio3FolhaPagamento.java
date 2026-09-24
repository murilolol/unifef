/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Avaliação 1 - Polimorfismo e Coleções com ArrayList
 * Exercício 3
 *
 * Como compilar e executar:
 * javac Exercicio3FolhaPagamento.java
 * java Exercicio3FolhaPagamento
 */

import java.util.ArrayList;
import java.util.List;

public class Exercicio3FolhaPagamento {

    abstract static class Funcionario {
        private String nome;
        private int matricula;
        private double salarioBase;

        public Funcionario(String nome, int matricula, double salarioBase) {
            this.nome = nome;
            this.matricula = matricula;
            this.salarioBase = salarioBase;
        }

        public String getNome() {
            return nome;
        }

        public int getMatricula() {
            return matricula;
        }

        public double getSalarioBase() {
            return salarioBase;
        }

        public abstract double calcularSalario();
        public abstract String getCargo();
    }

    static class Gerente extends Funcionario {
        private double bonusAnual;

        public Gerente(String nome, int matricula, double salarioBase, double bonusAnual) {
            super(nome, matricula, salarioBase);
            this.bonusAnual = bonusAnual;
        }

        @Override
        public double calcularSalario() {
            return getSalarioBase() + (bonusAnual / 12.0);
        }

        @Override
        public String getCargo() {
            return "Gerente";
        }
    }

    static class Desenvolvedor extends Funcionario {
        private double adicionalNoturno;

        public Desenvolvedor(String nome, int matricula, double salarioBase, double adicionalNoturno) {
            super(nome, matricula, salarioBase);
            this.adicionalNoturno = adicionalNoturno;
        }

        @Override
        public double calcularSalario() {
            return getSalarioBase() + adicionalNoturno;
        }

        @Override
        public String getCargo() {
            return "Desenvolvedor";
        }
    }

    static class Estagiario extends Funcionario {
        private double auxilioTransporte;

        public Estagiario(String nome, int matricula, double salarioBase, double auxilioTransporte) {
            super(nome, matricula, salarioBase);
            this.auxilioTransporte = auxilioTransporte;
        }

        @Override
        public double calcularSalario() {
            return getSalarioBase() + auxilioTransporte;
        }

        @Override
        public String getCargo() {
            return "Estagiário";
        }
    }

    static class FolhaPagamento {
        private List<Funcionario> funcionarios;

        public FolhaPagamento() {
            this.funcionarios = new ArrayList<>();
        }

        public void adicionarFuncionario(Funcionario f) {
            if (f != null) {
                funcionarios.add(f);
            }
        }

        public void listarFolha() {
            System.out.println("===============================================================");
            System.out.printf("%-10s %-20s %-15s %-15s%n", "MATRÍCULA", "NOME", "CARGO", "SALÁRIO FINAL");
            System.out.println("===============================================================");
            for (Funcionario f : funcionarios) {
                System.out.printf("%-10d %-20s %-15s R$ %11.2f%n",
                        f.getMatricula(), f.getNome(), f.getCargo(), f.calcularSalario());
            }
            System.out.println("===============================================================");
        }

        public double calcularTotalGasto() {
            double total = 0.0;
            for (Funcionario f : funcionarios) {
                total += f.calcularSalario();
            }
            return total;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Exercício 3: Polimorfismo e Folha de Pagamento ===");

        FolhaPagamento folha = new FolhaPagamento();

        folha.adicionarFuncionario(new Gerente("Clara Alencar", 301, 9500.00, 30000.00));
        folha.adicionarFuncionario(new Desenvolvedor("Diego Santos", 302, 6000.00, 750.00));
        folha.adicionarFuncionario(new Desenvolvedor("Fernanda Lima", 303, 7200.00, 1100.00));
        folha.adicionarFuncionario(new Estagiario("Gustavo Ribeiro", 304, 1400.00, 350.00));

        folha.listarFolha();

        double total = folha.calcularTotalGasto();
        System.out.printf("Gasto Total com Folha de Pagamento: R$ %.2f%n", total);
    }
}
