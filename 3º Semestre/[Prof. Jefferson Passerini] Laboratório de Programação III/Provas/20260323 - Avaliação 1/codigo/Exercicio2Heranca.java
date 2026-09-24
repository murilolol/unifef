/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Avaliação 1 - Herança e Sobrescrita de Métodos
 * Exercício 2
 *
 * Como compilar e executar:
 * javac Exercicio2Heranca.java
 * java Exercicio2Heranca
 */

public class Exercicio2Heranca {

    static class Funcionario {
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

        public void setSalarioBase(double salarioBase) {
            this.salarioBase = salarioBase;
        }

        public double calcularSalario() {
            return salarioBase;
        }

        public String getCargo() {
            return "Funcionário Padrão";
        }
    }

    static class Gerente extends Funcionario {
        private double bonusAnual;

        public Gerente(String nome, int matricula, double salarioBase, double bonusAnual) {
            super(nome, matricula, salarioBase);
            this.bonusAnual = bonusAnual;
        }

        public double getBonusAnual() {
            return bonusAnual;
        }

        public void setBonusAnual(double bonusAnual) {
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

        public double getAdicionalNoturno() {
            return adicionalNoturno;
        }

        public void setAdicionalNoturno(double adicionalNoturno) {
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

    public static void main(String[] args) {
        System.out.println("=== Exercício 2: Herança e Sobrescrita de Métodos ===");

        Funcionario f = new Funcionario("Roberto Silva", 201, 3000.00);
        Gerente g = new Gerente("Mariana Costa", 202, 8000.00, 24000.00);
        Desenvolvedor d = new Desenvolvedor("Lucas Mendes", 203, 5500.00, 800.00);

        System.out.println("Colaborador: " + f.getNome() + " | Cargo: " + f.getCargo());
        System.out.printf("Salário: R$ %.2f%n", f.calcularSalario());
        System.out.println();

        System.out.println("Colaborador: " + g.getNome() + " | Cargo: " + g.getCargo());
        System.out.printf("Salário Base: R$ %.2f | Bônus Anual: R$ %.2f%n", g.getSalarioBase(), g.getBonusAnual());
        System.out.printf("Salário Total Calculado: R$ %.2f%n", g.calcularSalario());
        System.out.println();

        System.out.println("Colaborador: " + d.getNome() + " | Cargo: " + d.getCargo());
        System.out.printf("Salário Base: R$ %.2f | Adicional Noturno: R$ %.2f%n", d.getSalarioBase(), d.getAdicionalNoturno());
        System.out.printf("Salário Total Calculado: R$ %.2f%n", d.calcularSalario());
    }
}
