/*
 * Disciplina: Engenharia de Software II (4º Semestre) - UniFEF
 * Professor: Prof. Wesley Soares
 * Tema: Resolução dos Exercícios de Fixação da Aula 05
 *
 * Como compilar:
 *   javac Exercicios.java
 * Como executar:
 *   java Exercicios
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ==========================================================================
// RESOLUÇÃO - EXERCÍCIO 1: COMPOSIÇÃO VS AGREGAÇÃO
// ==========================================================================
class Ex1Comodo {
    private String identificacao;

    public Ex1Comodo(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getIdentificacao() {
        return identificacao;
    }
}

class Ex1Casa {
    private String matricula;
    // Na composição forte, a lista e o ciclo de vida dos cômodos pertencem exclusivamente à casa
    private List<Ex1Comodo> comodos;

    public Ex1Casa(String matricula) {
        this.matricula = matricula;
        this.comodos = new ArrayList<>();
    }

    public void construirComodo(String nome) {
        this.comodos.add(new Ex1Comodo(nome));
    }

    public void demolir() {
        System.out.println("[Ex1 - Composição] Casa matrícula " + matricula + " sendo demolida...");
        this.comodos.clear();
    }

    public int getTotalComodos() {
        return comodos.size();
    }
}

class Ex1Produto {
    private String sku;
    private String nome;

    public Ex1Produto(String sku, String nome) {
        this.sku = sku;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}

class Ex1ItemVenda {
    private Ex1Produto produto;
    private int quantidade;

    public Ex1ItemVenda(Ex1Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Ex1Produto getProduto() {
        return produto;
    }
}

class Ex1PedidoVenda {
    private int id;
    private List<Ex1ItemVenda> itens;

    public Ex1PedidoVenda(int id) {
        this.id = id;
        this.itens = new ArrayList<>();
    }

    public void associarItem(Ex1Produto produto, int quantidade) {
        this.itens.add(new Ex1ItemVenda(produto, quantidade));
    }

    public void anularPedido() {
        System.out.println("[Ex1 - Agregação] Pedido #" + id + " cancelado. Os produtos persistem no banco de dados.");
        this.itens.clear();
    }
}

// ==========================================================================
// RESOLUÇÃO - EXERCÍCIO 2: ESPECIALIZAÇÃO E VISIBILIDADE PROTEGIDA
// ==========================================================================
abstract class Ex2Funcionario {
    private String matricula;
    private String nome;
    // Atributo protegido acessível pelas subclasses especializadas
    protected double salarioBase;

    public Ex2Funcionario(String matricula, String nome, double salarioBase) {
        this.matricula = matricula;
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public abstract double calcularSalarioTotal();
    public abstract String getCargo();
}

class Ex2Gerente extends Ex2Funcionario {
    private double bonusGestao;

    public Ex2Gerente(String matricula, String nome, double salarioBase, double bonusGestao) {
        super(matricula, nome, salarioBase);
        this.bonusGestao = bonusGestao;
    }

    @Override
    public double calcularSalarioTotal() {
        return this.salarioBase + this.bonusGestao;
    }

    @Override
    public String getCargo() {
        return "Gerente de Projetos";
    }
}

class Ex2Desenvolvedor extends Ex2Funcionario {
    private String nivelSenioridade;

    public Ex2Desenvolvedor(String matricula, String nome, double salarioBase, String nivelSenioridade) {
        super(matricula, nome, salarioBase);
        this.nivelSenioridade = nivelSenioridade;
    }

    @Override
    public double calcularSalarioTotal() {
        double adicional = nivelSenioridade.equalsIgnoreCase("Senior") ? 1500.0 : 500.0;
        return this.salarioBase + adicional;
    }

    @Override
    public String getCargo() {
        return "Desenvolvedor Software (" + nivelSenioridade + ")";
    }
}

// ==========================================================================
// RESOLUÇÃO - EXERCÍCIO 3: RELACIONAMENTO DE DEPENDÊNCIA FRACA
// ==========================================================================
class Ex3ExportadorPDF {
    public void emitirDocumento(String titulo, String corpo) {
        System.out.println("  >>> [ExportadorPDF] Emitindo PDF: '" + titulo + "'");
        System.out.println("      Dados: " + corpo);
    }
}

class Ex3RelatorioService {
    // A dependência existe estritamente durante a chamada do método
    public void gerarRelatorioSprint(String sprint, Ex3ExportadorPDF exportador) {
        System.out.println("[Ex3 - Dependência] RelatorioService executando sem guardar referência persistente...");
        String corpo = "Sprint: " + sprint + " | Status: 100% dos Story Points entregues.";
        exportador.emitirDocumento("Resumo da Sprint", corpo);
    }
}

// ==========================================================================
// RESOLUÇÃO - EXERCÍCIO 4: MULTIPLICIDADE ESTRITA (1..*)
// ==========================================================================
class Ex4Membro {
    private String nome;
    private String especialidade;

    public Ex4Membro(String nome, String especialidade) {
        this.nome = nome;
        this.especialidade = especialidade;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome + " (" + especialidade + ")";
    }
}

class Ex4Equipe {
    private String nomeSquad;
    private List<Ex4Membro> membros;

    // Garante a multiplicidade mínima 1..* na própria assinatura do construtor
    public Ex4Equipe(String nomeSquad, Ex4Membro membroFundador) {
        if (membroFundador == null) {
            throw new IllegalArgumentException("Uma equipe deve possuir obrigatoriamente pelo menos 1 membro inicial.");
        }
        this.nomeSquad = nomeSquad;
        this.membros = new ArrayList<>();
        this.membros.add(membroFundador);
    }

    public void adicionarMembro(Ex4Membro membro) {
        if (membro != null && !this.membros.contains(membro)) {
            this.membros.add(membro);
        }
    }

    public void removerMembro(Ex4Membro membro) {
        // Validação da regra de multiplicidade 1..*
        if (this.membros.size() <= 1) {
            throw new IllegalStateException("Violação de multiplicidade (1..*): A equipe não pode ficar vazia!");
        }
        this.membros.remove(membro);
    }

    public List<Ex4Membro> getMembros() {
        return Collections.unmodifiableList(this.membros);
    }

    public String getNomeSquad() {
        return nomeSquad;
    }
}

// ==========================================================================
// CLASSE EXECUTÁVEL PRINCIPAL COM BATERIA DE TESTES DOS EXERCÍCIOS
// ==========================================================================
public class Exercicios {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("       RESOLUÇÃO DOS EXERCÍCIOS DE ENGENHARIA DE SW II     ");
        System.out.println("==========================================================\n");

        // Teste Exercício 1
        System.out.println("--- [Exercício 1] Composição vs Agregação ---");
        Ex1Casa casa = new Ex1Casa("SP-9921");
        casa.construirComodo("Suíte Master");
        casa.construirComodo("Cozinha Integrada");
        System.out.println("Total de cômodos criados: " + casa.getTotalComodos());
        casa.demolir();
        System.out.println("Cômodos após demolição: " + casa.getTotalComodos());

        Ex1Produto cafe = new Ex1Produto("SKU-01", "Café Arábica Especial");
        Ex1PedidoVenda pedido = new Ex1PedidoVenda(10);
        pedido.associarItem(cafe, 3);
        pedido.anularPedido();
        System.out.println("Produto original no catálogo: " + cafe.getNome());
        System.out.println();

        // Teste Exercício 2
        System.out.println("--- [Exercício 2] Especialização e Polimorfismo ---");
        List<Ex2Funcionario> folha = new ArrayList<>();
        folha.add(new Ex2Gerente("G01", "Ana Souza", 9000.00, 3000.00));
        folha.add(new Ex2Desenvolvedor("D01", "Pedro Rocha", 6000.00, "Senior"));
        folha.add(new Ex2Desenvolvedor("D02", "Mariana Lima", 4000.00, "Pleno"));

        for (Ex2Funcionario f : folha) {
            System.out.printf("Colaborador: %s | Cargo: %s | Salário Total: R$ %.2f%n",
                    f.getNome(), f.getCargo(), f.calcularSalarioTotal());
        }
        System.out.println();

        // Teste Exercício 3
        System.out.println("--- [Exercício 3] Dependência Estrutural ---");
        Ex3RelatorioService relService = new Ex3RelatorioService();
        Ex3ExportadorPDF pdfGen = new Ex3ExportadorPDF();
        relService.gerarRelatorioSprint("Sprint 04 - Diagrama de Classes", pdfGen);
        System.out.println();

        // Teste Exercício 4
        System.out.println("--- [Exercício 4] Multiplicidade Estrita (1..*) ---");
        Ex4Membro lider = new Ex4Membro("Lucas Dev", "Arquiteto de Software");
        Ex4Membro dev = new Ex4Membro("Beatriz QA", "Analista de Testes");
        Ex4Equipe squad = new Ex4Equipe("Squad Alfa", lider);
        squad.adicionarMembro(dev);
        System.out.println("Membros do " + squad.getNomeSquad() + ": " + squad.getMembros());

        squad.removerMembro(dev);
        System.out.println("Membro removido. Membros atuais: " + squad.getMembros());

        try {
            System.out.println("Tentando remover o último membro restante...");
            squad.removerMembro(lider);
        } catch (IllegalStateException e) {
            System.out.println("Regra de multiplicidade validada com sucesso: " + e.getMessage());
        }

        System.out.println("\nTodos os exercícios foram executados e validados com êxito!");
    }
}
