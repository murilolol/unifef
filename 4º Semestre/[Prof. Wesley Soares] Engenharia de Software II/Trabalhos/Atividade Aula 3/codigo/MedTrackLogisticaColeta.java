/**
 * ============================================================================
 * DISCIPLINA : Engenharia de Software II (4º Semestre) - UniFEF
 * PROFESSOR  : Wesley Soares
 * TEMA       : Casos de Uso de Coleta, Regra RN01 (Trava de Divergência) e RNF03
 * CONTEXTO   : Atividade da Aula 3 - HealthTech Solutions (Plataforma MedTrack)
 * ============================================================================
 *
 * CONCEITOS COBERTOS:
 * 1. Implementação dos Casos de Uso:
 *    - UC03: Efetuar Coleta Física
 *    - UC04: Escanear QR Code do Equipamento
 *    - UC06: Colher Assinatura Digital com Não-Repúdio
 *    - UC07: Registrar Não-Conformidade de Série
 * 2. Regra de Negócio RN01: Trava de Coleta Divergente (o motorista fica impedido
 *    de coletar se o serial escaneado divergir da Ordem de Coleta original).
 * 3. Requisito Não-Funcional RNF03: Segurança e Não-Repúdio através de carimbo
 *    de data/hora, coordenadas de GPS e cálculo de HASH SHA-256 da assinatura.
 *
 * COMO COMPILAR:
 *   javac MedTrackLogisticaColeta.java
 *
 * COMO EXECUTAR:
 *   java MedTrackLogisticaColeta
 * ============================================================================
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MedTrackLogisticaColeta {

    /**
     * Ator do Sistema: Motorista responsável pela rota de coleta hospitalar.
     */
    public static class Motorista {
        private final int id;
        private final String nome;
        private final String cnh;
        private final String celular;

        public Motorista(int id, String nome, String cnh, String celular) {
            this.id = id;
            this.nome = nome;
            this.cnh = cnh;
            this.celular = celular;
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getCnh() {
            return cnh;
        }

        @Override
        public String toString() {
            return String.format("Motorista[ID=%d, Nome=%s, CNH=%s]", id, nome, cnh);
        }
    }

    /**
     * Ator do Sistema (Stakeholder): Hospital solicitante da devolução/troca.
     */
    public static class Hospital {
        private final int id;
        private final String razaoSocial;
        private final String cnpj;
        private final String alaHospitalar;
        private final double latitudeEsperada;
        private final double longitudeEsperada;

        public Hospital(
                int id,
                String razaoSocial,
                String cnpj,
                String alaHospitalar,
                double latitudeEsperada,
                double longitudeEsperada) {
            this.id = id;
            this.razaoSocial = razaoSocial;
            this.cnpj = cnpj;
            this.alaHospitalar = alaHospitalar;
            this.latitudeEsperada = latitudeEsperada;
            this.longitudeEsperada = longitudeEsperada;
        }

        public String getRazaoSocial() {
            return razaoSocial;
        }

        public String getAlaHospitalar() {
            return alaHospitalar;
        }

        public double getLatitudeEsperada() {
            return latitudeEsperada;
        }

        public double getLongitudeEsperada() {
            return longitudeEsperada;
        }
    }

    /**
     * Entidade que representa a solicitação de serviço formalizada no MedTrack.
     */
    public static class OrdemColeta {
        public enum StatusOrdem {
            ABERTA,
            ATRIBUIDA_A_ROTA,
            EM_ANDAMENTO,
            COLETADA_EM_TRANSITO,
            BLOQUEADA_DIVERGENCIA,
            CANCELADA
        }

        private final String codigoOrdem;
        private final Hospital hospital;
        private final String numeroPatrimonioEsperado;
        private final String numeroSerieEsperado;
        private StatusOrdem status;
        private Motorista motoristaAtribuido;
        private String justificativaBloqueio;

        public OrdemColeta(
                String codigoOrdem,
                Hospital hospital,
                String numeroPatrimonioEsperado,
                String numeroSerieEsperado) {
            this.codigoOrdem = codigoOrdem;
            this.hospital = hospital;
            this.numeroPatrimonioEsperado = numeroPatrimonioEsperado;
            this.numeroSerieEsperado = numeroSerieEsperado;
            this.status = StatusOrdem.ABERTA;
        }

        public String getCodigoOrdem() {
            return codigoOrdem;
        }

        public Hospital getHospital() {
            return hospital;
        }

        public String getNumeroSerieEsperado() {
            return numeroSerieEsperado;
        }

        public StatusOrdem getStatus() {
            return status;
        }

        public void atribuirMotorista(Motorista m) {
            this.motoristaAtribuido = m;
            this.status = StatusOrdem.ATRIBUIDA_A_ROTA;
        }

        public void marcarEmAndamento() {
            this.status = StatusOrdem.EM_ANDAMENTO;
        }

        public void marcarColetado() {
            this.status = StatusOrdem.COLETADA_EM_TRANSITO;
        }

        public void bloquearPorDivergencia(String motivo) {
            this.status = StatusOrdem.BLOQUEADA_DIVERGENCIA;
            this.justificativaBloqueio = motivo;
        }

        public String getJustificativaBloqueio() {
            return justificativaBloqueio;
        }
    }

    /**
     * Comprovante digital emitido com carimbo de não-repúdio (RNF03).
     */
    public static class ComprovanteColetaDigital {
        private final String codigoOrdem;
        private final String serialValidado;
        private final String timestampIso;
        private final double latitudeGps;
        private final double longitudeGps;
        private final String nomeResponsavelHospital;
        private final String hashSha256Assinatura;

        public ComprovanteColetaDigital(
                String codigoOrdem,
                String serialValidado,
                String timestampIso,
                double latitudeGps,
                double longitudeGps,
                String nomeResponsavelHospital,
                String hashSha256Assinatura) {
            this.codigoOrdem = codigoOrdem;
            this.serialValidado = serialValidado;
            this.timestampIso = timestampIso;
            this.latitudeGps = latitudeGps;
            this.longitudeGps = longitudeGps;
            this.nomeResponsavelHospital = nomeResponsavelHospital;
            this.hashSha256Assinatura = hashSha256Assinatura;
        }

        public void imprimirComprovante() {
            System.out.println("========================================================================");
            System.out.println("             COMPROVANTE DIGITAL DE LOGÍSTICA REVERSA (RNF03)           ");
            System.out.println("========================================================================");
            System.out.printf("ORDEM DE COLETA    : %s%n", codigoOrdem);
            System.out.printf("SERIAL VALIDADO    : %s%n", serialValidado);
            System.out.printf("CARIMBO DE TEMPO   : %s%n", timestampIso);
            System.out.printf("LOCALIZAÇÃO GPS    : Lat %.6f, Lon %.6f%n", latitudeGps, longitudeGps);
            System.out.printf("RESPONSÁVEL HOSP.  : %s%n", nomeResponsavelHospital);
            System.out.printf("HASH SHA-256 ASSIN.: %s%n", hashSha256Assinatura);
            System.out.println("GARANTIA JURÍDICA  : Não-repúdio assegurado conforme LGPD e ANVISA.");
            System.out.println("========================================================================\n");
        }
    }

    /**
     * Serviço de Engenharia de Software responsável por executar as regras de negócio
     * de coleta, protegendo a empresa contra perda de ativos e desvios de processo.
     */
    public static class ServicoLogisticaReversa {

        /**
         * Executa a coleta validando a Regra RN01 e o Requisito RNF03.
         */
        public ComprovanteColetaDigital processarColeta(
                OrdemColeta ordem,
                Motorista motorista,
                String serialQrCodeLido,
                double latitudeAtual,
                double longitudeAtual,
                String assinaturaTexto,
                String nomeResponsavelHospital) {

            ordem.marcarEmAndamento();

            // Normalização de strings para comparação
            String serialEsperado = ordem.getNumeroSerieEsperado().trim();
            String serialLido = (serialQrCodeLido != null) ? serialQrCodeLido.trim() : "";

            // REGRA DE NEGÓCIO RN01: Trava de Coleta Divergente
            if (!serialEsperado.equalsIgnoreCase(serialLido)) {
                String erro = String.format(
                        "TRAVA DE SEGURANÇA RN01 DISPARADA: O QR Code escaneado fisicamente ('%s') " +
                        "não corresponde ao serial cadastrado na Ordem %s ('%s'). " +
                        "Coleta impedida para evitar extravio ou troca de equipamento!",
                        serialLido, ordem.getCodigoOrdem(), serialEsperado);
                ordem.bloquearPorDivergencia(erro);
                throw new IllegalStateException(erro);
            }

            // RNF03: Validação de Assinatura e Cálculo Criptográfico SHA-256
            if (assinaturaTexto == null || assinaturaTexto.trim().length() < 10) {
                throw new IllegalArgumentException("Violação de Não-Repúdio: Assinatura digital incompleta ou ausente!");
            }

            String hashSha256 = calcularHashSha256(assinaturaTexto + "::" + serialLido + "::" + latitudeAtual);
            String timestampIso = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            ordem.marcarColetado();

            return new ComprovanteColetaDigital(
                    ordem.getCodigoOrdem(),
                    serialLido,
                    timestampIso,
                    latitudeAtual,
                    longitudeAtual,
                    nomeResponsavelHospital,
                    hashSha256
            );
        }

        private String calcularHashSha256(String entrada) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(entrada.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Algoritmo SHA-256 não disponível na JVM.", e);
            }
        }
    }

    /**
     * Método principal executando simulações reais dos Casos de Uso.
     */
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("     MEDTRACK SOLUTIONS - SIMULADOR DE COLETA E VALIDAÇÃO DE QR CODE    ");
        System.out.println("========================================================================\n");

        ServicoLogisticaReversa servico = new ServicoLogisticaReversa();
        Motorista motorista = new Motorista(101, "José Roberto Santos", "11928374650", "(11) 98765-4321");

        Hospital hospital = new Hospital(
                201,
                "Hospital Israelita Albert Einstein - Unidade Morumbi",
                "60.765.823/0001-30",
                "UTI Adulto - 3º Andar",
                -23.599824,
                -46.714522
        );

        // Ordem criada pelo Gestor Hospitalar (Stakeholder)
        OrdemColeta ordem1 = new OrdemColeta(
                "ORD-2026-8841",
                hospital,
                "PAT-99100",
                "SN-VP-55210-BRA"
        );
        ordem1.atribuirMotorista(motorista);

        System.out.println("[CENÁRIO 1] Execução com Sucesso da Coleta (Serial Coincidente):");
        System.out.println("Motorista aponta a câmera para o QR Code da carcaça do ventilador pulmonar...");
        String qrCodeLidoCorreto = "SN-VP-55210-BRA";
        String assinaturaBase64 = "ASSINATURA_DIGITAL_DRA_HELENA_PINTO_CRM_SP_184920_TIMESTAMP_VAL_10";

        try {
            ComprovanteColetaDigital comprovante = servico.processarColeta(
                    ordem1,
                    motorista,
                    qrCodeLidoCorreto,
                    -23.599820,
                    -46.714520,
                    assinaturaBase64,
                    "Dra. Helena Pinto (Enfermeira-Chefe UTI)"
            );
            System.out.println("-> Status da Ordem no Back-end: " + ordem1.getStatus());
            comprovante.imprimirComprovante();
        } catch (Exception e) {
            System.err.println("Falha inesperada no Cenário 1: " + e.getMessage());
        }

        System.out.println("[CENÁRIO 2] Violação Crítica da RN01: O hospital entregou equipamento trocado!");
        OrdemColeta ordem2 = new OrdemColeta(
                "ORD-2026-8842",
                hospital,
                "PAT-12003",
                "SN-BI-33010-XYZ" // Serial aguardado
        );
        ordem2.atribuirMotorista(motorista);

        // O motorista escaneia a máquina colocada na frente dele, mas é outro número de série!
        String qrCodeLidoDivergente = "SN-BI-99999-DIVERGENTE";
        System.out.println("Serial aguardado na ordem : " + ordem2.getNumeroSerieEsperado());
        System.out.println("Serial lido no QR Code    : " + qrCodeLidoDivergente);

        try {
            servico.processarColeta(
                    ordem2,
                    motorista,
                    qrCodeLidoDivergente,
                    -23.599820,
                    -46.714520,
                    assinaturaBase64,
                    "Enf. Carlos Drumond"
            );
            System.err.println("ERRO: A regra RN01 falhou em bloquear!");
        } catch (IllegalStateException e) {
            System.out.println("\n-> RESPOSTA DO SISTEMA AO MOTORISTA NO APLICATIVO:");
            System.out.println("   [BLOQUEIO EM TELA VERMELHA]");
            System.out.println("   " + e.getMessage());
            System.out.println("-> Status da Ordem atualizado para: " + ordem2.getStatus());
            System.out.println("-> Ocorrência registrada no servidor para intervenção do Coordenador de Logística.");
        }

        System.out.println("\n[CENÁRIO 3] Violação de Não-Repúdio (RNF03): Tentativa de envio com assinatura em branco:");
        try {
            servico.processarColeta(
                    ordem1,
                    motorista,
                    "SN-VP-55210-BRA",
                    -23.599820,
                    -46.714520,
                    "", // Assinatura vazia
                    "Desconhecido"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("-> BLOQUEIO DE SEGURANÇA:");
            System.out.println("   " + e.getMessage());
        }
    }
}
