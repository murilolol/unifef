/**
 * Instituicao: Centro Universitario de Santa Fe do Sul (UniFEF)
 * Curso: Bacharelado em Sistemas de Informacao
 * Disciplina: Engenharia de Software II
 * Docente: Prof. Ms. Wesley Soares de Souza
 * Tema: Analise Documental e Engenharia Reversa de Regras de Negocio (Exercicio 2)
 *       Formula Excel: =SE(E(ValorTotalVenda > 500; DistanciaKM <= 15); 0; SE(DistanciaKM <= 15; 35; 35 + ((DistanciaKM - 15) * 2,5)))
 * 
 * Como compilar:
 *   javac MotorCalculoFrete.java
 * 
 * Como executar:
 *   java MotorCalculoFrete
 */

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MotorCalculoFrete {

    /**
     * Objeto de transferencia com auditoria completa da decisao de frete,
     * garantindo rastreabilidade entre a regra de negocio e o resultado numerico.
     */
    public static class ResultadoCalculoFrete {
        private final BigDecimal valorFrete;
        private final String codigoRegra;
        private final String descricaoRegra;
        private final boolean isento;
        private final BigDecimal valorVenda;
        private final double distanciaKm;

        public ResultadoCalculoFrete(BigDecimal valorFrete, String codigoRegra, String descricaoRegra,
                                     boolean isento, BigDecimal valorVenda, double distanciaKm) {
            this.valorFrete = valorFrete.setScale(2, RoundingMode.HALF_UP);
            this.codigoRegra = codigoRegra;
            this.descricaoRegra = descricaoRegra;
            this.isento = isento;
            this.valorVenda = valorVenda.setScale(2, RoundingMode.HALF_UP);
            this.distanciaKm = distanciaKm;
        }

        public BigDecimal getValorFrete() { return valorFrete; }
        public String getCodigoRegra() { return codigoRegra; }
        public String getDescricaoRegra() { return descricaoRegra; }
        public boolean isIsento() { return isento; }
        public BigDecimal getValorVenda() { return valorVenda; }
        public double getDistanciaKm() { return distanciaKm; }

        @Override
        public String toString() {
            return String.format("[Venda: R$ %8.2f | Dist: %5.1f km] -> Frete: R$ %6.2f | Regra: %-14s | Isento: %-5s | Motivo: %s",
                    valorVenda, distanciaKm, valorFrete, codigoRegra, isento ? "SIM" : "NAO", descricaoRegra);
        }
    }

    /**
     * Parametros parametricos da politica de frete elicitados na analise documental.
     */
    public static class PoliticaFreteConfig {
        public static final double LIMITE_RAIO_BASE_KM = 15.0;
        public static final BigDecimal TARIFA_FIXA_BASE = new BigDecimal("35.00");
        public static final BigDecimal VALOR_KM_EXCEDENTE = new BigDecimal("2.50");
        public static final BigDecimal VALOR_MINIMO_ISENCAO = new BigDecimal("500.00");
    }

    /**
     * Motor de calculo que transcreve com rigor formal as regras elicitadas:
     * RN-FRETE-001 (Isencao), RN-FRETE-002 (Tarifa Base) e RN-FRETE-003 (Excedente Quilometrico).
     */
    public static ResultadoCalculoFrete calcular(BigDecimal valorTotalVenda, double distanciaKm) {
        if (valorTotalVenda == null || valorTotalVenda.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor total da venda invalido para calculo de frete.");
        }
        if (distanciaKm < 0.0) {
            throw new IllegalArgumentException("A distancia em quilometros nao pode ser negativa.");
        }

        // Analise do Raio Base (<= 15.0 km)
        if (distanciaKm <= PoliticaFreteConfig.LIMITE_RAIO_BASE_KM) {
            // RN-FRETE-001: Valor estritamente maior que R$ 500,00 garante isencao
            if (valorTotalVenda.compareTo(PoliticaFreteConfig.VALOR_MINIMO_ISENCAO) > 0) {
                return new ResultadoCalculoFrete(
                        BigDecimal.ZERO,
                        "RN-FRETE-001",
                        "Isencao integral por compra acima de R$ 500 dentro do raio de 15 km.",
                        true,
                        valorTotalVenda,
                        distanciaKm
                );
            }
            // RN-FRETE-002: Dentro do raio de 15 km com valor <= R$ 500,00 aplica tarifa fixa
            return new ResultadoCalculoFrete(
                    PoliticaFreteConfig.TARIFA_FIXA_BASE,
                    "RN-FRETE-002",
                    "Tarifa fixa base para entregas em raio curto ate 15 km.",
                    false,
                    valorTotalVenda,
                    distanciaKm
            );
        }

        // RN-FRETE-003: Distancia superior a 15 km cobra taxa base + R$ 2,50 por km adicional
        double kmExcedente = distanciaKm - PoliticaFreteConfig.LIMITE_RAIO_BASE_KM;
        BigDecimal valorAdicional = BigDecimal.valueOf(kmExcedente).multiply(PoliticaFreteConfig.VALOR_KM_EXCEDENTE);
        BigDecimal freteTotal = PoliticaFreteConfig.TARIFA_FIXA_BASE.add(valorAdicional);

        return new ResultadoCalculoFrete(
                freteTotal,
                "RN-FRETE-003",
                String.format("Taxa base de R$ 35,00 acrescida de %.2f km excedentes a R$ 2,50/km.", kmExcedente),
                false,
                valorTotalVenda,
                distanciaKm
        );
    }

    /**
     * Bateria de testes de integridade analitica cobrindo classes de equivalencia
     * e valores limites (fronteiras matematicas).
     */
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("UniFEF - Engenharia de Software II | Prof. Ms. Wesley Soares de Souza");
        System.out.println("Exercicio 2: Engenharia Reversa de Regras de Negocio de Frete (Excel)");
        System.out.println("======================================================================\n");

        // Matriz de Testes de Homologacao
        Object[][] casosDeTeste = {
            // { ValorVenda, DistanciaKm, FreteEsperado, CodigoRegraEsperada }
            { new BigDecimal("600.00"), 10.0, new BigDecimal("0.00"),  "RN-FRETE-001" }, // Compra alta, raio curto
            { new BigDecimal("500.01"), 15.0, new BigDecimal("0.00"),  "RN-FRETE-001" }, // Fronteira superior isencao
            { new BigDecimal("500.00"), 15.0, new BigDecimal("35.00"), "RN-FRETE-002" }, // Fronteira exata (nao isenta)
            { new BigDecimal("250.00"),  8.0, new BigDecimal("35.00"), "RN-FRETE-002" }, // Compra baixa, raio curto
            { new BigDecimal("1200.00"), 20.0, new BigDecimal("47.50"), "RN-FRETE-003" }, // 35 + (5 * 2.5) = 47.50
            { new BigDecimal("100.00"), 25.5, new BigDecimal("61.25"), "RN-FRETE-003" }  // 35 + (10.5 * 2.5) = 61.25
        };

        int sucessos = 0;
        for (int i = 0; i < casosDeTeste.length; i++) {
            BigDecimal venda = (BigDecimal) casosDeTeste[i][0];
            double distancia = (Double) casosDeTeste[i][1];
            BigDecimal esperado = (BigDecimal) casosDeTeste[i][2];
            String regraEsperada = (String) casosDeTeste[i][3];

            ResultadoCalculoFrete resultado = calcular(venda, distancia);
            boolean valorConfere = resultado.getValorFrete().compareTo(esperado) == 0;
            boolean regraConfere = resultado.getCodigoRegra().equals(regraEsperada);

            System.out.println("Teste " + (i + 1) + ": " + resultado);
            if (valorConfere && regraConfere) {
                System.out.println("       -> [APROVADO] Valor e Regra conferem com o gabarito formal.\n");
                sucessos++;
            } else {
                System.err.println("       -> [FALHA] Esperado: R$ " + esperado + " sob " + regraEsperada + "\n");
            }
        }

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Relatorio de Verificacao: " + sucessos + " de " + casosDeTeste.length + " casos de teste aprovados.");
        System.out.println("Validacao de protecao contra entradas invalidas...");
        try {
            calcular(new BigDecimal("-50.00"), 10.0);
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Bloqueio de valor negativo de venda tratado com sucesso: " + e.getMessage());
        }
        try {
            calcular(new BigDecimal("100.00"), -5.0);
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Bloqueio de distancia negativa tratado com sucesso: " + e.getMessage());
        }
        System.out.println("----------------------------------------------------------------------");
    }
}
