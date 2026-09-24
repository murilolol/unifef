/*
 * Disciplina: Engenharia de Software II (4º Semestre) - UniFEF
 * Professor: Prof. Wesley Soares
 * Tema: Exemplos de Diagrama de Classes da Aula 05
 *
 * Como compilar:
 *   javac ExemplosAula.java
 * Como executar:
 *   java ExemplosAula
 */

import java.util.ArrayList;
import java.util.List;

// --- 1. EXEMPLO DE VISIBILIDADE E ATRIBUTOS (Slides 17 e 18) ---
class Pessoa {
    // - privado: visível apenas dentro da própria classe
    private String cpf;
    
    // # protegido: visível para classes filhas e classes do mesmo pacote
    protected String categoria;
    
    // + público: visível por qualquer classe
    public String nome;

    public Pessoa(String nome, String cpf, String categoria) {
        this.nome = nome;
        this.cpf = cpf;
        this.categoria = categoria;
    }

    // + público: método acessor para atributo privado
    public String getNome() {
        return this.nome;
    }

    public String getCpfAnonimizado() {
        return "***." + cpf.substring(4, 7) + ".***-**";
    }
}

// --- 2. EXEMPLO DE COMPOSIÇÃO: CASA e CÔMODO (Slide 23) ---
// Relação todo-parte forte (◆): o cômodo depende da existência da casa.
class Comodo {
    private String nome;
    private double metragem;

    public Comodo(String nome, double metragem) {
        this.nome = nome;
        this.metragem = metragem;
    }

    public String getNome() {
        return nome;
    }

    public double getMetragem() {
        return metragem;
    }
}

class Casa {
    private String endereco;
    // A casa gerencia o ciclo de vida dos cômodos (composição forte)
    private List<Comodo> comodos;

    public Casa(String endereco) {
        this.endereco = endereco;
        this.comodos = new ArrayList<>();
    }

    // O cômodo é instanciado e gerenciado sob o ciclo de vida da Casa
    public void adicionarComodo(String nome, double metragem) {
        this.comodos.add(new Comodo(nome, metragem));
    }

    public void destruirCasa() {
        System.out.println("Demolindo a casa em '" + endereco + "' e todos os seus cômodos...");
        this.comodos.clear(); // Cômodos deixam de existir com a casa
    }

    public void listarComodos() {
        System.out.println("Cômodos da casa (" + endereco + "): " + comodos.size() + " cômodo(s).");
        for (Comodo c : comodos) {
            System.out.println(" - " + c.getNome() + " (" + c.getMetragem() + "m²)");
        }
    }
}

// --- 3. EXEMPLO DE AGREGAÇÃO: PEDIDO e ITEM (Slide 22) ---
// Relação todo-parte fraca (♢): Produto existe independente do Item/Pedido.
class ProdutoCatalogo {
    private int id;
    private String descricao;
    private double preco;

    public ProdutoCatalogo(int id, String descricao, double preco) {
        this.id = id;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }
}

class ItemPedido {
    // Referência por agregação ao produto existente
    private ProdutoCatalogo produto;
    private int quantidade;

    public ItemPedido(ProdutoCatalogo produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }

    public ProdutoCatalogo getProduto() {
        return produto;
    }
}

class Pedido {
    private int numero;
    private List<ItemPedido> itens;

    public Pedido(int numero) {
        this.numero = numero;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ProdutoCatalogo produto, int quantidade) {
        this.itens.add(new ItemPedido(produto, quantidade));
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void cancelarPedido() {
        System.out.println("Cancelando Pedido #" + numero + "... Itens removidos, produtos permanecem no catálogo.");
        this.itens.clear();
    }
}

// --- 4. EXEMPLO DE GENERALIZAÇÃO / HERANÇA (Slide 24) ---
// Funcionario (Superclasse) -> Gerente e Desenvolvedor (Subclasses)
abstract class Funcionario {
    private String nome;
    protected double salarioBase;

    public Funcionario(String nome, double salarioBase) {
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public String getNome() {
        return nome;
    }

    public abstract double calcularSalarioLiquido();
}

class Gerente extends Funcionario {
    private double bonusGerencial;

    public Gerente(String nome, double salarioBase, double bonusGerencial) {
        super(nome, salarioBase);
        this.bonusGerencial = bonusGerencial;
    }

    @Override
    public double calcularSalarioLiquido() {
        // Acesso direto ao atributo protegido 'salarioBase'
        return this.salarioBase + this.bonusGerencial;
    }
}

class Desenvolvedor extends Funcionario {
    private int horasExtras;
    private double valorHoraExtra;

    public Desenvolvedor(String nome, double salarioBase, int horasExtras, double valorHoraExtra) {
        super(nome, salarioBase);
        this.horasExtras = horasExtras;
        this.valorHoraExtra = valorHoraExtra;
    }

    @Override
    public double calcularSalarioLiquido() {
        return this.salarioBase + (this.horasExtras * this.valorHoraExtra);
    }
}

// --- 5. EXEMPLO DE DEPENDÊNCIA (Slide 26) ---
// RelatorioService USA ExportadorPDF temporariamente, sem ter atributo permanente.
class ExportadorPDF {
    public void exportar(String conteudo) {
        System.out.println("[ExportadorPDF] Gerando arquivo .pdf com o conteúdo:");
        System.out.println("    >> " + conteudo);
        System.out.println("[ExportadorPDF] PDF gerado com sucesso!");
    }
}

class RelatorioService {
    // Dependência: O exportador é recebido apenas como parâmetro do método de operação
    public void gerarRelatorioMensal(String dados, ExportadorPDF exportador) {
        System.out.println("[RelatorioService] Compilando dados do relatório...");
        String relatorioFormatado = "RELATÓRIO MENSAL CONSOLIDADO: [" + dados + "]";
        exportador.exportar(relatorioFormatado);
    }
}

// --- CLASSE PRINCIPAL EXECUTÁVEL ---
public class ExemplosAula {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("    ENGENHARIA DE SOFTWARE II - EXEMPLOS DA AULA 05       ");
        System.out.println("==========================================================\n");

        // 1. Visibilidade
        System.out.println("1. Visibilidade e Atributos:");
        Pessoa p = new Pessoa("Carlos Silva", "123.456.789-00", "Docente");
        System.out.println("Nome público: " + p.nome);
        System.out.println("CPF mascarado via método: " + p.getCpfAnonimizado());
        System.out.println();

        // 2. Composição (Casa e Cômodos)
        System.out.println("2. Associação por Composição Forte (Casa ◆— Cômodo):");
        Casa minhaCasa = new Casa("Rua das Palmeiras, 100");
        minhaCasa.adicionarComodo("Sala de Estar", 22.5);
        minhaCasa.adicionarComodo("Quarto Casal", 15.0);
        minhaCasa.adicionarComodo("Cozinha", 12.0);
        minhaCasa.listarComodos();
        minhaCasa.destruirCasa();
        minhaCasa.listarComodos();
        System.out.println();

        // 3. Agregação (Pedido e Itens/Produtos)
        System.out.println("3. Associação por Agregação Fraca (Pedido ♢— Item):");
        ProdutoCatalogo monitor = new ProdutoCatalogo(101, "Monitor 27 pol 144Hz", 1200.00);
        ProdutoCatalogo teclado = new ProdutoCatalogo(102, "Teclado Mecânico RGB", 350.00);

        Pedido pedido1 = new Pedido(5001);
        pedido1.adicionarItem(monitor, 1);
        pedido1.adicionarItem(teclado, 2);
        System.out.printf("Total do Pedido: R$ %.2f%n", pedido1.calcularTotal());
        pedido1.cancelarPedido();
        System.out.println("Catálogo preservado: " + monitor.getDescricao() + " ainda disponível a R$ " + monitor.getPreco());
        System.out.println();

        // 4. Generalização / Herança
        System.out.println("4. Generalização (Herança de Funcionario):");
        Funcionario gerente = new Gerente("Wesley Soares", 8000.00, 2500.00);
        Funcionario dev = new Desenvolvedor("Lucas Ferreira", 5000.00, 10, 50.00);
        System.out.printf("Gerente: %s | Salário Final: R$ %.2f%n", gerente.getNome(), gerente.calcularSalarioLiquido());
        System.out.printf("Dev: %s | Salário Final: R$ %.2f%n", dev.getNome(), dev.calcularSalarioLiquido());
        System.out.println();

        // 5. Dependência
        System.out.println("5. Relacionamento de Dependência (RelatorioService -> ExportadorPDF):");
        RelatorioService relatorioService = new RelatorioService();
        ExportadorPDF exportadorPdf = new ExportadorPDF();
        relatorioService.gerarRelatorioMensal("Faturamento Setembro: R$ 125.000,00", exportadorPdf);
        System.out.println("\nExecução dos exemplos concluída com sucesso!");
    }
}
