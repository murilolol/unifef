/*
 * Disciplina: Laboratorio de Programacao III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Avaliacao Substitutiva - Revisao Pratica de POO em Java
 *
 * Como compilar e executar:
 *   javac AvaliacaoSubstitutiva.java
 *   java AvaliacaoSubstitutiva
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// ==========================================================================
// EXCECAO PERSONALIZADA (Exercicio 3)
// ==========================================================================
class FuncionarioNaoEncontradoException extends Exception {
    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

// ==========================================================================
// INTERFACE DE CONTRATO (Exercicio 2)
// ==========================================================================
interface Tributavel {
    double calcularTributos();
}

// ==========================================================================
// CLASSE ABSTRATA BASE (Exercicio 1)
// ==========================================================================
abstract class Funcionario {
    private String nome;
    private String matricula;
    private double salarioBase;

    public Funcionario(String nome, String matricula, double salarioBase) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome nao pode ser nulo ou vazio.");
        }
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("Matricula nao pode ser nula ou vazia.");
        }
        if (salarioBase < 0) {
            throw new IllegalArgumentException("Salario base nao pode ser negativo.");
        }
        this.nome = nome;
        this.matricula = matricula;
        this.salarioBase = salarioBase;
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public abstract double calcularSalarioLiquido();

    @Override
    public String toString() {
        return String.format("[%s] %s | Salario Base: R$ %.2f | Liquido: R$ %.2f",
                matricula, nome, salarioBase, calcularSalarioLiquido());
    }
}

// ==========================================================================
// SUBCLASSE 1: ASSALARIADO (Exercicio 1)
// ==========================================================================
class FuncionarioAssalariado extends Funcionario {
    private double adicionalFixo;

    public FuncionarioAssalariado(String nome, String matricula, double salarioBase, double adicionalFixo) {
        super(nome, matricula, salarioBase);
        if (adicionalFixo < 0) {
            throw new IllegalArgumentException("Adicional fixo nao pode ser negativo.");
        }
        this.adicionalFixo = adicionalFixo;
    }

    @Override
    public double calcularSalarioLiquido() {
        return getSalarioBase() + adicionalFixo;
    }
}

// ==========================================================================
// SUBCLASSE 2: COMISSIONADO E TRIBUTAVEL (Exercicios 1 e 2)
// ==========================================================================
class FuncionarioComissionado extends Funcionario implements Tributavel {
    private double totalVendas;
    private double taxaComissao; // Exemplo: 0.05 para 5%

    public FuncionarioComissionado(String nome, String matricula, double salarioBase, double totalVendas, double taxaComissao) {
        super(nome, matricula, salarioBase);
        if (totalVendas < 0 || taxaComissao < 0) {
            throw new IllegalArgumentException("Vendas e taxa de comissao devem ser nao-negativas.");
        }
        this.totalVendas = totalVendas;
        this.taxaComissao = taxaComissao;
    }

    public double getComissao() {
        return totalVendas * taxaComissao;
    }

    public double getSalarioBruto() {
        return getSalarioBase() + getComissao();
    }

    @Override
    public double calcularTributos() {
        // 11% de previdencia sobre o rendimento bruto
        return getSalarioBruto() * 0.11;
    }

    @Override
    public double calcularSalarioLiquido() {
        return getSalarioBruto() - calcularTributos();
    }
}

// ==========================================================================
// SERVICO: CALCULADORA DE TRIBUTOS (Exercicio 2)
// ==========================================================================
class CalculadoraTributos {
    public static double totalizarTributos(List<Tributavel> itensTributaveis) {
        double total = 0.0;
        for (Tributavel item : itensTributaveis) {
            total += item.calcularTributos();
        }
        return total;
    }
}

// ==========================================================================
// GERENCIADOR DE DEPARTAMENTO (Exercicios 3 e 4)
// ==========================================================================
class Departamento {
    private String nome;
    private List<Funcionario> funcionarios;

    public Departamento(String nome) {
        this.nome = nome;
        this.funcionarios = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionario nao pode ser nulo.");
        }
        funcionarios.add(funcionario);
    }

    public Funcionario buscarPorMatricula(String matricula) throws FuncionarioNaoEncontradoException {
        for (Funcionario f : funcionarios) {
            if (f.getMatricula().equalsIgnoreCase(matricula)) {
                return f;
            }
        }
        throw new FuncionarioNaoEncontradoException("Funcionario com matricula '" + matricula + "' nao localizado.");
    }

    public double calcularFolhaTotal() {
        double total = 0.0;
        for (Funcionario f : funcionarios) {
            total += f.calcularSalarioLiquido();
        }
        return total;
    }

    public Funcionario obterMaiorSalario() {
        if (funcionarios.isEmpty()) {
            return null;
        }
        Funcionario maior = funcionarios.get(0);
        for (Funcionario f : funcionarios) {
            if (f.calcularSalarioLiquido() > maior.calcularSalarioLiquido()) {
                maior = f;
            }
        }
        return maior;
    }

    public List<Funcionario> listarOrdenadosPorSalarioDecrescente() {
        List<Funcionario> copia = new ArrayList<>(funcionarios);
        Collections.sort(copia, new Comparator<Funcionario>() {
            @Override
            public int compare(Funcionario f1, Funcionario f2) {
                return Double.compare(f2.calcularSalarioLiquido(), f1.calcularSalarioLiquido());
            }
        });
        return copia;
    }

    public List<Funcionario> getFuncionarios() {
        return Collections.unmodifiableList(funcionarios);
    }
}

// ==========================================================================
// CLASSE PRINCIPAL EXECUTAVEL
// ==========================================================================
public class AvaliacaoSubstitutiva {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   LABORATORIO DE PROGRAMACAO III - AVALIACAO SUBSTITUTIVA     ");
        System.out.println("   Prof. Jefferson Passerini - UniFEF                         ");
        System.out.println("===============================================================\n");

        // Instanciacao do Departamento (Exercicio 3)
        Departamento ti = new Departamento("Tecnologia da Informacao");

        // Criacao de Objetos com Polimorfismo (Exercicio 1)
        Funcionario f1 = new FuncionarioAssalariado("Ana Silva", "MAT-101", 4500.0, 500.0);
        Funcionario f2 = new FuncionarioAssalariado("Carlos Souza", "MAT-102", 3800.0, 300.0);
        Funcionario f3 = new FuncionarioComissionado("Beatriz Lima", "MAT-103", 2500.0, 60000.0, 0.06);
        Funcionario f4 = new FuncionarioComissionado("Diego Rocha", "MAT-104", 2200.0, 45000.0, 0.05);

        ti.adicionarFuncionario(f1);
        ti.adicionarFuncionario(f2);
        ti.adicionarFuncionario(f3);
        ti.adicionarFuncionario(f4);

        // Demostracao Exercicio 1 e 4: Listagem e Folha Total
        System.out.println("[EXERCICIO 1 & 4] Quadro de Funcionarios do Departamento: " + ti.getNome());
        for (Funcionario f : ti.getFuncionarios()) {
            System.out.println(" - " + f);
        }
        System.out.printf("\nFolha de Pagamento Total Liquida: R$ %.2f\n", ti.calcularFolhaTotal());

        Funcionario destaque = ti.obterMaiorSalario();
        if (destaque != null) {
            System.out.printf("Maior remuneracao: %s (R$ %.2f)\n\n", destaque.getNome(), destaque.calcularSalarioLiquido());
        }

        // Demonstracao Exercicio 2: Interfaces e Calculadora de Tributos
        System.out.println("[EXERCICIO 2] Apuracao de Tributos sobre Comissionados:");
        List<Tributavel> listaTributavel = new ArrayList<>();
        for (Funcionario f : ti.getFuncionarios()) {
            if (f instanceof Tributavel) {
                Tributavel t = (Tributavel) f;
                listaTributavel.add(t);
                System.out.printf(" - %s: Tributo retido: R$ %.2f\n", f.getNome(), t.calcularTributos());
            }
        }
        double totalImpostos = CalculadoraTributos.totalizarTributos(listaTributavel);
        System.out.printf("Total de Tributos Retidos: R$ %.2f\n\n", totalImpostos);

        // Demonstracao Exercicio 3: Tratamento de Excecoes
        System.out.println("[EXERCICIO 3] Teste de Busca e Tratamento de Excecoes:");
        testarBusca(ti, "MAT-103"); // Caso de sucesso
        testarBusca(ti, "MAT-999"); // Caso de falha (dispara FuncionarioNaoEncontradoException)

        // Demonstracao Exercicio 4: Ordenacao de Colecao
        System.out.println("\n[EXERCICIO 4] Funcionarios Ordenados por Salario (Decrescente):");
        List<Funcionario> ordenados = ti.listarOrdenadosPorSalarioDecrescente();
        for (int i = 0; i < ordenados.size(); i++) {
            Funcionario f = ordenados.get(i);
            System.out.printf(" %dº lugar: %-15s -> R$ %.2f\n", (i + 1), f.getNome(), f.calcularSalarioLiquido());
        }

        System.out.println("\nExecucao concluida com sucesso.");
    }

    private static void testarBusca(Departamento depto, String matricula) {
        try {
            Funcionario encontrado = depto.buscarPorMatricula(matricula);
            System.out.println(" [SUCESSO] Localizado: " + encontrado);
        } catch (FuncionarioNaoEncontradoException e) {
            System.out.println(" [ERRO CAPTURADO] " + e.getMessage());
        }
    }
}
