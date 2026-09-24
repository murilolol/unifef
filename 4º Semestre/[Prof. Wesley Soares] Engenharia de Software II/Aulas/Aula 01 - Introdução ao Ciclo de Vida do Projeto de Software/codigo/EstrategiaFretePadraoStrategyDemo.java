/**
 * Disciplina: Engenharia de Software II
 * Professor: Wesley Soares
 * Tema: Padrão de Projeto GoF Strategy e Princípios SOLID (SRP, OCP, DIP)
 * 
 * Como compilar:
 *   javac EstrategiaFretePadraoStrategyDemo.java
 * Como executar:
 *   java EstrategiaFretePadraoStrategyDemo
 */

import java.util.Objects;

/**
 * Classe principal que demonstra de forma completa e compilável o padrão GoF Strategy
 * aplicado ao domínio de logística apresentado na Aula 01.
 * 
 * Princípios de Engenharia de Software demonstrados:
 * 1. OCP (Open/Closed Principle): Novas estratégias de frete (ex: Bicicleta Sustentável) são adicionadas
 *    sem modificar a classe Pedido ou as estratégias existentes.
 * 2. SRP (Single Responsibility Principle): Pedido gerencia dados da compra; as estratégias gerenciam
 *    estritamente o algoritmo de frete.
 * 3. DIP (Dependency Inversion Principle): Pedido depende da interface abstrata CalculadoraFreteStrategy,
 *    nunca de classes concretas.
 */
public class EstrategiaFretePadraoStrategyDemo {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("  DEMONSTRACAO DO PADRAO DE PROJETO GOF STRATEGY (PRINCIPIOS SOLID)");
        System.out.println("  Engenharia de Software II | Prof. Wesley Soares | UniFEF");
        System.out.println("======================================================================\n");

        // Dados base de um pedido no sistema RotaVerde Express
        String idPedido = "PED-2026-X99";
        double valorMercadorias = 350.00; // R$ 350,00 em mercadorias
        double pesoTotalCargaKg = 12.0;    // 12 kg de produtos hortifruti
        double distanciaEntregaKm = 15.0;  // 15 km de distancia do centro de distribuicao

        // Instanciacao do Pedido com a estrategia padrao (Frete Economico)
        Pedido pedido = new Pedido(idPedido, valorMercadorias, pesoTotalCargaKg, distanciaEntregaKm);
        
        System.out.println("--- CENARIO 1: MODALIDADE FRETE ECONOMICO (RODOVIARIO PADRAO) ---");
        exibirExtratoPedido(pedido);

        // Modificacao dinamica de comportamento em tempo de execucao (Injecao de Dependencia)
        System.out.println("\n--- CENARIO 2: MUDANCA DINAMICA PARA FRETE EXPRESSO (ENTREGA RAPIDA) ---");
        pedido.setEstrategiaFrete(new FreteExpressoStrategy());
        exibirExtratoPedido(pedido);

        // Demonstracao do Principio Aberto/Fechado (OCP):
        // Inserimos a nova modalidade sustentavel (bicicleta de carga urbana) sem mexer no codigo de Pedido
        System.out.println("\n--- CENARIO 3: EXTENSAO COM FRETE SUSTENTAVEL (BICICLETA ELETRICA / OCP) ---");
        pedido.setEstrategiaFrete(new FreteBicicletaSustentavelStrategy());
        exibirExtratoPedido(pedido);

        // Validacao de testes unitarios embutida no main para verificar robustez e tratamento de erros
        executarTestesDeInvariantes();
    }

    private static void exibirExtratoPedido(Pedido pedido) {
        double custoFrete = pedido.calcularApenasFrete();
        double valorFinal = pedido.calcularValorFinal();

        System.out.printf("  Pedido: %s | Modalidade Atual: %s%n", 
                pedido.getIdentificador(), pedido.getNomeEstrategiaAtual());
        System.out.printf("  -> Subtotal das Mercadorias : R$ %8.2f%n", pedido.getValorProdutos());
        System.out.printf("  -> Custo do Frete Calculado : R$ %8.2f%n", custoFrete);
        System.out.printf("  -> Total a Pagar pelo Cliente: R$ %8.2f%n", valorFinal);
    }

    /**
     * Simula verificacoes automatizadas de integridade de codigo.
     */
    private static void executarTestesDeInvariantes() {
        System.out.println("\n--- EXECUTANDO TESTES DE SANIDADE E TRATAMENTO DE EXCECOES ---");
        try {
            System.out.println("  [TESTE 1] Tentando atribuir estrategia nula...");
            Pedido pInvalido = new Pedido("PED-ERR", 100.0, 5.0, 10.0);
            pInvalido.setEstrategiaFrete(null);
            System.err.println("  -> [FALHA] Sistema aceitou estrategia nula.");
        } catch (NullPointerException e) {
            System.out.println("  -> [PASSOU] NullPointerException capturada com sucesso: " + e.getMessage());
        }

        try {
            System.out.println("  [TESTE 2] Tentando calcular frete com peso negativo...");
            CalculadoraFreteStrategy strat = new FreteEconomicoStrategy();
            strat.calcular(-10.0, 50.0);
            System.err.println("  -> [FALHA] Sistema permitiu calculo com peso negativo.");
        } catch (IllegalArgumentException e) {
            System.out.println("  -> [PASSOU] IllegalArgumentException capturada com sucesso: " + e.getMessage());
        }
    }
}

/**
 * Interface que define o contrato Strategy para o calculo de frete.
 * Toda nova modalidade precisa apenas implementar este metodo.
 */
interface CalculadoraFreteStrategy {
    /**
     * Realiza o calculo monetario com base no peso e na distancia.
     */
    double calcular(double pesoEmKg, double distanciaEmKm);

    /**
     * Identificador amigavel para exibicao nos extratos.
     */
    String getNomeModalidade();
}

/**
 * Estrategia Concreta 1: Frete Economico Terrestre.
 */
class FreteEconomicoStrategy implements CalculadoraFreteStrategy {
    private static final double VALOR_BASE = 10.00;
    private static final double TAXA_POR_KM = 0.50;
    private static final double TAXA_POR_KG = 0.20;

    @Override
    public double calcular(double pesoEmKg, double distanciaEmKm) {
        validarEntradas(pesoEmKg, distanciaEmKm);
        return VALOR_BASE + (distanciaEmKm * TAXA_POR_KM) + (pesoEmKg * TAXA_POR_KG);
    }

    @Override
    public String getNomeModalidade() {
        return "Economica (Caminhao Urbano)";
    }

    private void validarEntradas(double peso, double distancia) {
        if (peso <= 0 || distancia <= 0) {
            throw new IllegalArgumentException("Peso e distancia devem ser estritamente positivos.");
        }
    }
}

/**
 * Estrategia Concreta 2: Frete Expresso (Prioridade de entrega no mesmo dia).
 */
class FreteExpressoStrategy implements CalculadoraFreteStrategy {
    private static final double VALOR_BASE = 25.00;
    private static final double TAXA_POR_KM = 1.20;
    private static final double TAXA_POR_KG = 0.80;

    @Override
    public double calcular(double pesoEmKg, double distanciaEmKm) {
        if (pesoEmKg <= 0 || distanciaEmKm <= 0) {
            throw new IllegalArgumentException("Peso e distancia devem ser estritamente positivos.");
        }
        return VALOR_BASE + (distanciaEmKm * TAXA_POR_KM) + (pesoEmKg * TAXA_POR_KG);
    }

    @Override
    public String getNomeModalidade() {
        return "Expressa Prioritaria (Mesmo Dia)";
    }
}

/**
 * Estrategia Concreta 3: Frete Sustentavel por Bicicleta de Carga.
 * Evidencia o Principio Aberto/Fechado (OCP): Nova regra de negocio adicionada
 * sem alterar nenhuma linha de codigo das classes existentes.
 */
class FreteBicicletaSustentavelStrategy implements CalculadoraFreteStrategy {
    private static final double VALOR_BASE = 5.00;
    private static final double TAXA_POR_KM = 0.35;
    private static final double LIMITE_DISTANCIA_KM = 20.0;
    private static final double LIMITE_PESO_KG = 30.0;

    @Override
    public double calcular(double pesoEmKg, double distanciaEmKm) {
        if (pesoEmKg <= 0 || distanciaEmKm <= 0) {
            throw new IllegalArgumentException("Peso e distancia devem ser estritamente positivos.");
        }
        if (distanciaEmKm > LIMITE_DISTANCIA_KM) {
            throw new IllegalArgumentException("Frete por bicicleta restrito a percursos de ate " + LIMITE_DISTANCIA_KM + " km.");
        }
        if (pesoEmKg > LIMITE_PESO_KG) {
            throw new IllegalArgumentException("Carga maxima da bicicleta e de " + LIMITE_PESO_KG + " kg.");
        }
        return VALOR_BASE + (distanciaEmKm * TAXA_POR_KM);
    }

    @Override
    public String getNomeModalidade() {
        return "Sustentavel (Bicicleta Eletrica / Emissao Zero)";
    }
}

/**
 * Entidade Pedido que atua como Contexto no padrao Strategy.
 * Demonstra Alta Coesao (responsabilidade restrita ao pedido comercial)
 * e Baixo Acoplamento (depende apenas da interface do algoritmo).
 */
class Pedido {
    private final String identificador;
    private final double valorProdutos;
    private final double pesoTotalKg;
    private final double distanciaEntregaKm;
    private CalculadoraFreteStrategy estrategiaFrete;

    public Pedido(String identificador, double valorProdutos, double pesoTotalKg, double distanciaEntregaKm) {
        if (valorProdutos < 0) {
            throw new IllegalArgumentException("Valor dos produtos nao pode ser negativo.");
        }
        this.identificador = Objects.requireNonNull(identificador, "Identificador nao pode ser nulo.");
        this.valorProdutos = valorProdutos;
        this.pesoTotalKg = pesoTotalKg;
        this.distanciaEntregaKm = distanciaEntregaKm;
        // Estrategia padrao inicial
        this.estrategiaFrete = new FreteEconomicoStrategy();
    }

    public void setEstrategiaFrete(CalculadoraFreteStrategy novaEstrategia) {
        this.estrategiaFrete = Objects.requireNonNull(novaEstrategia, "A estrategia de frete nao pode ser nula.");
    }

    public double calcularApenasFrete() {
        return this.estrategiaFrete.calcular(this.pesoTotalKg, this.distanciaEntregaKm);
    }

    public double calcularValorFinal() {
        return this.valorProdutos + calcularApenasFrete();
    }

    public String getNomeEstrategiaAtual() {
        return this.estrategiaFrete.getNomeModalidade();
    }

    public String getIdentificador() {
        return identificador;
    }

    public double getValorProdutos() {
        return valorProdutos;
    }
}
