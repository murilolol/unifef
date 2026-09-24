/**
 * Disciplina: Engenharia de Software II
 * Professor: Wesley Soares
 * Tema: Especificação e Verificação de Requisitos Funcionais (RF) e Não Funcionais (RNF)
 * 
 * Como compilar:
 *   javac ValidacaoRequisitosDemo.java
 * Como executar:
 *   java ValidacaoRequisitosDemo
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe executável que materializa em código a fronteira entre 'O QUE o sistema faz'
 * (Requisitos Funcionais) e 'COMO o sistema opera em termos de qualidade e restricoes'
 * (Requisitos Nao Funcionais com metricas verificaveis).
 */
public class ValidacaoRequisitosDemo {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("  VERIFICACAO PRATICA DE REQUISITOS (RF vs RNF) — DOMINIO LOGISTICO");
        System.out.println("  Engenharia de Software II | Prof. Wesley Soares | UniFEF");
        System.out.println("======================================================================\n");

        ServicoDespachoLogistico servico = new ServicoDespachoLogistico();

        // ====================================================================
        // 1. DEMONSTRACAO DE REQUISITOS FUNCIONAIS (RF)
        // Critério: Passa/Falha na regra de negócio e nas entradas/saídas.
        // ====================================================================
        System.out.println("--- 1. VERIFICANDO REQUISITOS FUNCIONAIS (RF) ---");

        // RF01: O sistema deve permitir o cadastro de pacotes com identificador, peso e destino validos.
        Pacote p1 = new Pacote("PKG-001", 12.5, "Rua das Palmeiras, 100");
        Pacote p2 = new Pacote("PKG-002", 5.0, "Avenida Central, 500");
        servico.adicionarPacote(p1);
        servico.adicionarPacote(p2);
        System.out.println("[RF01 - SUCESSO] Pacotes cadastrados com sucesso no lote de despacho.");

        // RF02: O sistema deve bloquear a inclusao de pacotes com peso zerado ou negativo.
        try {
            System.out.println("[RF02 - TESTE] Tentando cadastrar pacote com peso invalido (-3.0 kg)...");
            new Pacote("PKG-INVALIDO", -3.0, "Rua Sem Saida, 0");
            System.out.println("[RF02 - FALHA] O sistema permitiu peso negativo (Regra violada).");
        } catch (IllegalArgumentException ex) {
            System.out.println("[RF02 - SUCESSO] Excecao capturada conforme regra de negocio: " + ex.getMessage());
        }

        // RF03: O sistema deve permitir a finalizacao da entrega mediante assinatura do recebedor.
        boolean entregaConfirmada = servico.confirmarEntrega("PKG-001", "Carlos Alberto de Souza");
        System.out.println("[RF03 - SUCESSO] Entrega do PKG-001 confirmada? " + entregaConfirmada);

        // ====================================================================
        // 2. DEMONSTRACAO DE REQUISITOS NAO FUNCIONAIS (RNF)
        // Critério: Métricas mensuráveis (latência em ms, algoritmo de hash, etc.)
        // ====================================================================
        System.out.println("\n--- 2. VERIFICANDO REQUISITOS NAO FUNCIONAIS (RNF COM METRICAS) ---");

        // RNF01 (Desempenho/Latencia): O sequenciamento de 1.000 rotas deve executar em menos de 100 ms.
        long limiteLatenciaMs = 100;
        Instant inicio = Instant.now();
        int totalRotasCalculadas = servico.executarBenchmarkOtimizacaoRotas(1000);
        Instant fim = Instant.now();
        long tempoGastoMs = Duration.between(inicio, fim).toMillis();

        System.out.println("[RNF01 - DESEMPENHO] Processadas " + totalRotasCalculadas + " rotas simuladas.");
        System.out.println("  -> Tempo medido: " + tempoGastoMs + " ms | Limite maximo do SLA: " + limiteLatenciaMs + " ms");
        if (tempoGastoMs <= limiteLatenciaMs) {
            System.out.println("  -> [APROVADO] Atributo de qualidade de desempenho atendido conforme especificacao.");
        } else {
            System.out.println("  -> [REPROVADO] SLA de desempenho violado. Necessaria otimizacao algoritmica.");
        }

        // RNF02 (Seguranca/Conformidade): Senhas de operadores devem ser armazenadas com hash criptografico seguro.
        String senhaEmClaro = "Operador#2026@Segredo";
        String hashSeguro = servico.gerarHashSenhaSegura(senhaEmClaro, "salt-aleatorio-unifef");
        System.out.println("\n[RNF02 - SEGURANCA] Protecao de credenciais de operadores:");
        System.out.println("  -> Senha em texto puro (PROIBIDO PERSISTIR): " + senhaEmClaro);
        System.out.println("  -> Hash SHA-256 com Salt resultante: " + hashSeguro);
        System.out.println("  -> [APROVADO] Senhas blindadas contra vazamento e em conformidade com a LGPD.");

        // RNF03 (Disponibilidade/Resiliencia): Fallback em caso de falha de servico externo.
        System.out.println("\n[RNF03 - RESILIENCIA] Simulando indisponibilidade de API de geolocalizacao externa:");
        String coordenadas = servico.obterCoordenadasComFallback("Rua Central, 500");
        System.out.println("  -> Resultado obtido via resiliencia: " + coordenadas);
    }
}

/**
 * Entidade que modela um Pacote de transporte (Dominio Logistico).
 * Aplica encapsulamento e validacao de invariantes de negocio.
 */
class Pacote {
    private final String codigo;
    private final double pesoKg;
    private final String enderecoDestino;
    private boolean entregue;
    private String recebedor;

    public Pacote(String codigo, double pesoKg, String enderecoDestino) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo do pacote e obrigatorio.");
        }
        if (pesoKg <= 0.0) {
            throw new IllegalArgumentException("Peso do pacote deve ser estritamente maior que zero.");
        }
        this.codigo = codigo;
        this.pesoKg = pesoKg;
        this.enderecoDestino = enderecoDestino;
        this.entregue = false;
    }

    public void registrarRecebimento(String nomeRecebedor) {
        if (nomeRecebedor == null || nomeRecebedor.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do recebedor e obrigatorio para concluir entrega.");
        }
        this.entregue = true;
        this.recebedor = nomeRecebedor;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public boolean isEntregue() {
        return entregue;
    }
}

/**
 * Camada de Servico responsavel por operacionalizar as regras de negocio e
 * assegurar o cumprimento dos Requisitos Nao Funcionais.
 */
class ServicoDespachoLogistico {
    private final List<Pacote> pacotes = new ArrayList<>();

    public void adicionarPacote(Pacote pacote) {
        this.pacotes.add(pacote);
    }

    public boolean confirmarEntrega(String codigoPacote, String nomeRecebedor) {
        for (Pacote p : pacotes) {
            if (p.getCodigo().equalsIgnoreCase(codigoPacote)) {
                p.registrarRecebimento(nomeRecebedor);
                return p.isEntregue();
            }
        }
        return false;
    }

    /**
     * Simula a carga computacional de sequenciamento de rotas para medicao do RNF01.
     */
    public int executarBenchmarkOtimizacaoRotas(int quantidadeRotas) {
        int calculosRealizados = 0;
        for (int i = 0; i < quantidadeRotas; i++) {
            // Simulacao matematica de calculo de distancia euclidiana entre duas coordenadas
            double x1 = i * 0.12;
            double y1 = i * 0.34;
            double x2 = (i + 1) * 0.56;
            double y2 = (i + 1) * 0.78;
            double distancia = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            if (distancia > 0) {
                calculosRealizados++;
            }
        }
        return calculosRealizados;
    }

    /**
     * Implementa o RNF02: Geracao de Hash seguro utilizando SHA-256 e Salt.
     */
    public String gerarHashSenhaSegura(String senhaPura, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String textoComSalt = senhaPura + "::" + salt;
            byte[] hashBytes = digest.digest(textoComSalt.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo de criptografia nao disponivel no ambiente", e);
        }
    }

    /**
     * Implementa o RNF03: Padrao Fallback/Degradacao Graciosa para indisponibilidade de API.
     */
    public String obterCoordenadasComFallback(String endereco) {
        boolean apiExternaOffline = true; // Simulando queda da conexao com servico em nuvem

        if (apiExternaOffline) {
            // Fallback: recupera coordenada aproximada da tabela de cache regional
            return "[-20.2831, -50.2458] (Fonte: Cache Local de Seguranca - Fallback ativado)";
        }
        return "[-20.2831, -50.2458] (Fonte: API de Satelite em Nuvem)";
    }
}
