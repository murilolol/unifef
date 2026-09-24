/**
 * Instituicao: Centro Universitario de Santa Fe do Sul (UniFEF)
 * Curso: Bacharelado em Sistemas de Informacao
 * Disciplina: Engenharia de Software II
 * Docente: Prof. Ms. Wesley Soares de Souza
 * Tema: Mediacao de Conflito de Requisitos em Workshops e JAD (Exercicio 3)
 *       Solucao de Engenharia para o conflito: Comercial vs. Riscos e Seguros
 *       (Implementacao de RF-080, RF-081 e Regra de Seguranca RN-LOG-015)
 * 
 * Como compilar:
 *   javac LogisticaMediacaoConflito.java
 * 
 * Como executar:
 *   java LogisticaMediacaoConflito
 */

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

public class LogisticaMediacaoConflito {

    /**
     * Representa as coordenadas e homologacao de seguranca de um endereco.
     */
    public static class Localizacao {
        private final String logradouro;
        private final String bairro;
        private final String cep;
        private final boolean perimetroSeguroHomologado;

        public Localizacao(String logradouro, String bairro, String cep, boolean perimetroSeguroHomologado) {
            this.logradouro = logradouro;
            this.bairro = bairro;
            this.cep = cep;
            this.perimetroSeguroHomologado = perimetroSeguroHomologado;
        }

        public String getLogradouro() { return logradouro; }
        public String getBairro() { return bairro; }
        public String getCep() { return cep; }
        public boolean isPerimetroSeguroHomologado() { return perimetroSeguroHomologado; }

        @Override
        public String toString() {
            return logradouro + " - Bairro: " + bairro + " (CEP: " + cep + ")";
        }
    }

    /**
     * Dados patrimoniais da carga transportada.
     */
    public static class Carga {
        private final String idCarga;
        private final String descricao;
        private final BigDecimal valorMonetario;

        public Carga(String idCarga, String descricao, BigDecimal valorMonetario) {
            this.idCarga = idCarga;
            this.descricao = descricao;
            this.valorMonetario = valorMonetario;
        }

        public String getIdCarga() { return idCarga; }
        public String getDescricao() { return descricao; }
        public BigDecimal getValorMonetario() { return valorMonetario; }
    }

    /**
     * Secao 11 - Exercicio 3: RF-080 (Solicitacao de Alteracao de Rota em Transito).
     * O motorista nao altera a rota localmente; a solicitacao e aberta pela central.
     */
    public static class SolicitacaoMudancaRota {
        private final String protocolo;
        private final Carga carga;
        private final Localizacao destinoOriginal;
        private final Localizacao destinoDesejado;
        private final String solicitante;
        private final LocalDateTime dataHora;

        public SolicitacaoMudancaRota(Carga carga, Localizacao destinoOriginal, Localizacao destinoDesejado, String solicitante) {
            this.protocolo = "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.carga = carga;
            this.destinoOriginal = destinoOriginal;
            this.destinoDesejado = destinoDesejado;
            this.solicitante = solicitante;
            this.dataHora = LocalDateTime.now();
        }

        public String getProtocolo() { return protocolo; }
        public Carga getCarga() { return carga; }
        public Localizacao getDestinoOriginal() { return destinoOriginal; }
        public Localizacao getDestinoDesejado() { return destinoDesejado; }
        public String getSolicitante() { return solicitante; }
    }

    /**
     * Secao 11 - Exercicio 3: RF-081 (Workflow de Homologacao de Risco).
     * Simula a integracao via WebService com a gerenciadora de riscos e seguradora.
     */
    public static class ServicoHomologacaoRisco {
        public static class ParecerSeguradora {
            private final boolean autorizado;
            private final String tokenAssinaturaDigital;
            private final String justificativa;

            public ParecerSeguradora(boolean autorizado, String tokenAssinaturaDigital, String justificativa) {
                this.autorizado = autorizado;
                this.tokenAssinaturaDigital = tokenAssinaturaDigital;
                this.justificativa = justificativa;
            }

            public boolean isAutorizado() { return autorizado; }
            public String getTokenAssinaturaDigital() { return tokenAssinaturaDigital; }
            public String getJustificativa() { return justificativa; }
        }

        public ParecerSeguradora avaliarSolicitacao(SolicitacaoMudancaRota solicitacao) {
            // Cargas acima de R$ 50.000,00 exigem conformidade estrita com o PGR
            if (solicitacao.getCarga().getValorMonetario().compareTo(new BigDecimal("50000.00")) > 0) {
                if (!solicitacao.getDestinoDesejado().isPerimetroSeguroHomologado()) {
                    return new ParecerSeguradora(
                            false,
                            null,
                            "REJEITADO: Endereco fora do perimetro seguro do PGR. Risco critico de sinistro sem cobertura."
                    );
                }
            }

            // Se aprovado, gera token criptografico de assinatura da seguradora
            String token = gerarAssinaturaDigital(solicitacao.getProtocolo() + solicitacao.getDestinoDesejado().getCep());
            return new ParecerSeguradora(
                    true,
                    token,
                    "APROVADO: Novo destino dentro da mancha de cobertura e monitoramento 24h."
            );
        }

        private String gerarAssinaturaDigital(String payload) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(payload.getBytes());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    sb.append(String.format("%02X", hash[i]));
                }
                return "SIG-PGR-" + sb.toString();
            } catch (NoSuchAlgorithmException e) {
                return "SIG-PGR-OFFLINE-FALLBACK";
            }
        }
    }

    /**
     * Secao 11 - Exercicio 3: RN-LOG-015 (Trava de Seguranca do Sistema de Bordo).
     * O computador de bordo do caminhao so aceita a reprogramacao da rota se
     * houver assinatura digital valida da seguradora; caso contrario, compele o retorno a base.
     */
    public static class SistemaBordoVeiculo {
        private final String placaCaminhao;
        private Localizacao destinoAtual;
        private boolean comandoRetornoBaseAtivo = false;

        public SistemaBordoVeiculo(String placaCaminhao, Localizacao destinoInicial) {
            this.placaCaminhao = placaCaminhao;
            this.destinoAtual = destinoInicial;
        }

        public void aplicarOrdemRota(SolicitacaoMudancaRota solicitacao, ServicoHomologacaoRisco.ParecerSeguradora parecer) {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("[COMPUTADOR DE BORDO] Veiculo Placa: " + placaCaminhao);
            System.out.println("Processando diretriz para solicitacao: " + solicitacao.getProtocolo());

            if (parecer.isAutorizado() && parecer.getTokenAssinaturaDigital() != null) {
                this.destinoAtual = solicitacao.getDestinoDesejado();
                this.comandoRetornoBaseAtivo = false;
                System.out.println("[SUCESSO - RN-LOG-015] Rota atualizada com autorizacao da Seguradora.");
                System.out.println("Token de Homologacao: " + parecer.getTokenAssinaturaDigital());
                System.out.println("Novo Destino Ativo: " + destinoAtual);
            } else {
                this.comandoRetornoBaseAtivo = true;
                System.err.println("[BLOQUEIO - RN-LOG-015] Violacao de Seguranca! Solicitacao negada.");
                System.err.println("Motivo: " + parecer.getJustificativa());
                System.err.println("COMANDO MANDATORIO DE BORDO: Carga travada. Retornar a Central Operacional imediatamente.");
            }
            System.out.println("----------------------------------------------------------------------");
        }

        public Localizacao getDestinoAtual() { return destinoAtual; }
        public boolean isComandoRetornoBaseAtivo() { return comandoRetornoBaseAtivo; }
    }

    /**
     * Demonstracao executavel da solucao mediada entre Comercial e Riscos.
     */
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("UniFEF - Engenharia de Software II | Prof. Ms. Wesley Soares de Souza");
        System.out.println("Exercicio 3: Mediacao de Conflitos em Workshops (RF-080, RF-081 e RN-LOG-015)");
        System.out.println("======================================================================\n");

        ServicoHomologacaoRisco gerenciadoraRisco = new ServicoHomologacaoRisco();

        Localizacao sedeCliente = new Localizacao("Av. Brasil, 1500", "Centro", "15700-000", true);
        Localizacao filialSegura = new Localizacao("Rua das Industrias, 400", "Distrito Industrial", "15705-100", true);
        Localizacao chácaraForaPerimetro = new Localizacao("Estrada Rural do Jacutinga, km 12", "Zona Rural", "15710-999", false);

        Carga cargaEletrodomesticos = new Carga("CARGA-9921", "Lote de Smart TVs e Notebooks", new BigDecimal("185000.00"));
        SistemaBordoVeiculo caminhao = new SistemaBordoVeiculo("FEF-2026", sedeCliente);

        System.out.println("Carga despachada com destino inicial: " + caminhao.getDestinoAtual());
        System.out.println("Valor Patrimonial: R$ " + cargaEletrodomesticos.getValorMonetario() + " (Exige PGR Rigoroso)\n");

        // CENARIO 1: Cliente solicita entrega na filial (Area segura e homologada)
        System.out.println(">>> CENARIO 1: Solicitacao Comercial via Central para Filial Homologada...");
        SolicitacaoMudancaRota solicitacao1 = new SolicitacaoMudancaRota(
                cargaEletrodomesticos, caminhao.getDestinoAtual(), filialSegura, "Departamento Comercial - Solicitado por WhatsApp do Comprador"
        );
        ServicoHomologacaoRisco.ParecerSeguradora parecer1 = gerenciadoraRisco.avaliarSolicitacao(solicitacao1);
        caminhao.aplicarOrdemRota(solicitacao1, parecer1);

        // CENARIO 2: Cliente solicita entrega em sitio ermo (Fora do perimetro seguro do PGR)
        System.out.println("\n>>> CENARIO 2: Solicitacao Comercial de Mudanca para Chacara sem Cobertura...");
        SolicitacaoMudancaRota solicitacao2 = new SolicitacaoMudancaRota(
                cargaEletrodomesticos, caminhao.getDestinoAtual(), chácaraForaPerimetro, "Cliente ligou direto para o vendedor pedindo entrega em chacara"
        );
        ServicoHomologacaoRisco.ParecerSeguradora parecer2 = gerenciadoraRisco.avaliarSolicitacao(solicitacao2);
        caminhao.aplicarOrdemRota(solicitacao2, parecer2);

        System.out.println("\nStatus Final do Veiculo:");
        System.out.println("Destino Vigente: " + caminhao.getDestinoAtual());
        System.out.println("Retorno Obrigatorio a Base Acionado: " + (caminhao.isComandoRetornoBaseAtivo() ? "SIM (Carga Protegida)" : "NAO"));
        System.out.println("\nConclusao: A solucao tecnica de software eliminou o conflito de interesses,\n" +
                           "atendendo a flexibilidade comercial sob rigorosa salvaguarda securitaria.");
    }
}
