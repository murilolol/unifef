/*
 * Disciplina: Laboratório de Programação III
 * Professor: Prof. Jefferson Passerini
 * Tema: Avaliação 1 - Tratamento de Exceções Customizadas
 * Exercício 4
 *
 * Como compilar e executar:
 * javac Exercicio4Excecoes.java
 * java Exercicio4Excecoes
 */

public class Exercicio4Excecoes {

    static class DadosInvalidosException extends Exception {
        public DadosInvalidosException(String mensagem) {
            super(mensagem);
        }
    }

    static class CadastroFuncionario {
        private String nome;
        private int matricula;
        private double salario;

        public void cadastrar(String nome, int matricula, double salario) throws DadosInvalidosException {
            if (nome == null || nome.trim().isEmpty()) {
                throw new DadosInvalidosException("Nome inválido: não pode ser nulo ou vazio.");
            }
            if (matricula <= 0) {
                throw new DadosInvalidosException("Matrícula inválida: deve ser maior que zero (informado: " + matricula + ").");
            }
            if (salario <= 0.0) {
                throw new DadosInvalidosException("Salário inválido: deve ser estritamente maior que zero (informado: R$ " + salario + ").");
            }

            this.nome = nome.trim();
            this.matricula = matricula;
            this.salario = salario;

            System.out.printf("Colaborador '%s' (matrícula %d) cadastrado com sucesso com salário de R$ %.2f.%n",
                    this.nome, this.matricula, this.salario);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Exercício 4: Tratamento de Exceções de Regra de Negócio ===");

        CadastroFuncionario servico = new CadastroFuncionario();

        System.out.println();
        System.out.println("[Caso 1] Tentativa de cadastro com dados válidos:");
        try {
            servico.cadastrar("Juliana Ramos", 401, 5400.00);
        } catch (DadosInvalidosException e) {
            System.err.println("Falha inesperada no cadastro: " + e.getMessage());
        }

        System.out.println();
        System.out.println("[Caso 2] Tentativa de cadastro com nome vazio:");
        try {
            servico.cadastrar("   ", 402, 4000.00);
        } catch (DadosInvalidosException e) {
            System.out.println("Exceção capturada com sucesso: " + e.getMessage());
        }

        System.out.println();
        System.out.println("[Caso 3] Tentativa de cadastro com matrícula negativa:");
        try {
            servico.cadastrar("Paulo Henrique", -10, 4500.00);
        } catch (DadosInvalidosException e) {
            System.out.println("Exceção capturada com sucesso: " + e.getMessage());
        }

        System.out.println();
        System.out.println("[Caso 4] Tentativa de cadastro com salário zerado:");
        try {
            servico.cadastrar("Renata Meireles", 404, 0.0);
        } catch (DadosInvalidosException e) {
            System.out.println("Exceção capturada com sucesso: " + e.getMessage());
        }
    }
}
