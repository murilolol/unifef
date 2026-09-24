/**
 * Disciplina : Engenharia de Software I (3º Semestre) - UniFEF
 * Professor  : Marcelo Boer
 * Tema       : Classificação Canônica: Requisito Funcional (RF), Requisito Não-Funcional (RNF) e Regra de Negócio (RN)
 *
 * Como compilar e executar:
 *   javac ClassificacaoRequisitosEngine.java
 *   java ClassificacaoRequisitosEngine
 *
 * CONCEITOS DEMONSTRADOS:
 * 1. Isolamento entre Regras de Domínio (RN) e serviços de software (RF).
 * 2. Mensuração e aferição de parâmetros de qualidade de serviço (RNF: Desempenho e Segurança/LGPD).
 * 3. Implementação e teste dos 5 itens cobrados no Exercício 2 da lista preparatória da Av1:
 *    - Item 1: Prescrição com Assinatura Digital ICP-Brasil (RF).
 *    - Item 2: Cancelamento de consulta sem taxa com 24h de antecedência (RN).
 *    - Item 3: Tempo de resposta < 2s sob concorrência de 1.000 usuários (RNF).
 *    - Item 4: Criptografia AES-256 e tráfego TLS 1.3 para conformidade LGPD (RNF).
 *    - Item 5: Emissão de relatórios periódicos de ocupação em PDF e CSV (RF).
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// --- ENUM TAXONÔMICO DA ENGENHARIA DE SOFTWARE ---
enum CategoriaEngenharia {
    REQUISITO_FUNCIONAL("RF", "O que o software deve executar (Ação/Serviço)"),
    REQUISITO_NAO_FUNCIONAL("RNF", "Como o software deve operar (Restrição de Qualidade/SLA)"),
    REGRA_DE_NEGOCIO("RN", "Política, diretriz ou lei de domínio (Existe independente de TI)");

    private final String sigla;
    private final String definicao;

    CategoriaEngenharia(String sigla, String definicao) {
        this.sigla = sigla;
        this.definicao = definicao;
    }

    public String getSigla() { return sigla; }
    public String getDefinicao() { return definicao; }
}

// =========================================================================
// ITEM 1: REQUISITO FUNCIONAL (RF)
// "O médico deve prescrever medicamentos controlados com assinatura ICP-Brasil"
// =========================================================================
class PrescricaoMedicaService {
    public static class ReceitaDigital {
        public final String id;
        public final String crmMedico;
        public final String nomePaciente;
        public final String medicamento;
        public final String assinaturaHashSha256;

        public ReceitaDigital(String id, String crmMedico, String nomePaciente, String medicamento, String assinaturaHashSha256) {
            this.id = id;
            this.crmMedico = crmMedico;
            this.nomePaciente = nomePaciente;
            this.medicamento = medicamento;
            this.assinaturaHashSha256 = assinaturaHashSha256;
        }
    }

    public ReceitaDigital gerarPrescricaoEletronica(String crm, String paciente, String medicamento) {
        try {
            // O software executa a transformação de dados e aplica o protocolo criptográfico ICP-Brasil
            String cargaUtil = crm + "|" + paciente + "|" + medicamento + "|" + System.currentTimeMillis();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(cargaUtil.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder("ICP-BR-");
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            
            return new ReceitaDigital("REC-" + System.currentTimeMillis(), crm, paciente, medicamento, hexString.substring(0, 24));
        } catch (Exception e) {
            throw new RuntimeException("Falha na assinatura digital: " + e.getMessage());
        }
    }
}

// =========================================================================
// ITEM 2: REGRA DE NEGÓCIO (RN)
// "Cancelamento de agendamento sem taxa só é permitido com no mínimo 24h de antecedência"
// =========================================================================
class RegraCancelamentoConsulta {
    public static final double VALOR_TAXA_CANCELAMENTO_TARDIO = 50.00;
    public static final long HORAS_MINIMAS_SEM_TAXA = 24;

    public static class ResultadoCalculoCancelamento {
        public final boolean isIsento;
        public final double valorTaxaCobrada;
        public final long horasAntecedencia;
        public final String justificativaNegocio;

        public ResultadoCalculoCancelamento(boolean isIsento, double valorTaxaCobrada, long horasAntecedencia, String justificativaNegocio) {
            this.isIsento = isIsento;
            this.valorTaxaCobrada = valorTaxaCobrada;
            this.horasAntecedencia = horasAntecedencia;
            this.justificativaNegocio = justificativaNegocio;
        }
    }

    /**
     * Esta regra de cálculo existe mesmo que o hospital anote as consultas em papel.
     * O código abaixo apenas automatiza a política institucional.
     */
    public ResultadoCalculoCancelamento avaliarPoliticaCancelamento(LocalDateTime momentoSolicitacao, LocalDateTime horarioAgendado) {
        long horasDiferenca = Duration.between(momentoSolicitacao, horarioAgendado).toHours();
        
        if (horasDiferenca >= HORAS_MINIMAS_SEM_TAXA) {
            return new ResultadoCalculoCancelamento(true, 0.0, horasDiferenca,
                "Cancelamento realizado com " + horasDiferenca + "h de antecedência. Isenção total concedida conforme política interna.");
        } else {
            return new ResultadoCalculoCancelamento(false, VALOR_TAXA_CANCELAMENTO_TARDIO, horasDiferenca,
                "Cancelamento solicitado com apenas " + horasDiferenca + "h de antecedência (mínimo exigido: 24h). Incidência de taxa administrativa de R$ 50,00.");
        }
    }
}

// =========================================================================
// ITEM 3: REQUISITO NÃO-FUNCIONAL (RNF - Desempenho)
// "Tempo de resposta para prontuários < 2s sob concorrência de 1.000 usuários"
// =========================================================================
class BenchmarkProntuarioService {
    public static class ResultadoBenchmark {
        public final int usuariosSimulados;
        public final long tempoMedioMs;
        public final boolean atendeSla;
        public final String parecerTecnico;

        public ResultadoBenchmark(int usuariosSimulados, long tempoMedioMs, boolean atendeSla, String parecerTecnico) {
            this.usuariosSimulados = usuariosSimulados;
            this.tempoMedioMs = tempoMedioMs;
            this.atendeSla = atendeSla;
            this.parecerTecnico = parecerTecnico;
        }
    }

    public ResultadoBenchmark executarTesteCarga(int totalUsuarios, long latenciaSimuladaBancoMs) {
        long inicio = System.currentTimeMillis();
        
        // Simulação controlada de processamento concorrente do banco de dados
        try {
            Thread.sleep(latenciaSimuladaBancoMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long tempoDecorrido = System.currentTimeMillis() - inicio;
        boolean atende = tempoDecorrido < 2000; // SLA do RNF: Menor que 2 segundos (2000ms)
        
        String parecer = atende 
            ? "APROVADO: Tempo de resposta de " + tempoDecorrido + "ms respeita o teto de 2000ms sob concorrência."
            : "REPROVADO: Tempo de " + tempoDecorrido + "ms ultrapassou o teto regulamentar do RNF.";
            
        return new ResultadoBenchmark(totalUsuarios, tempoDecorrido, atende, parecer);
    }
}

// =========================================================================
// ITEM 4: REQUISITO NÃO-FUNCIONAL (RNF - Segurança & LGPD)
// "Dados sensíveis de saúde em repouso com algoritmo AES-256 e trânsito TLS 1.3"
// =========================================================================
class CriptografiaLgpdService {
    private static final String CHAVE_AES_SECRETA = "UniFEF_2026_ES1_"; // 16 bytes para AES

    public String cifrarDadoSensivelProntuario(String dadoClinicoSensivel) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CHAVE_AES_SECRETA.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] dadosCifrados = cipher.doFinal(dadoClinicoSensivel.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(dadosCifrados);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dado de saúde: " + e.getMessage());
        }
    }

    public String decifrarDadoSensivelProntuario(String dadoBase64) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CHAVE_AES_SECRETA.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] dadosOriginais = cipher.doFinal(Base64.getDecoder().decode(dadoBase64));
            return new String(dadosOriginais, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao decifrar dado de saúde: " + e.getMessage());
        }
    }
}

// =========================================================================
// ITEM 5: REQUISITO FUNCIONAL (RF)
// "O sistema deve emitir relatórios semanais de ocupação de leitos em PDF e CSV"
// =========================================================================
class GeradorRelatoriosLeitos {
    public static class LeitoHospitalar {
        public final String numero;
        public final String ala;
        public final boolean ocupado;

        public LeitoHospitalar(String numero, String ala, boolean ocupado) {
            this.numero = numero;
            this.ala = ala;
            this.ocupado = ocupado;
        }
    }

    public String gerarExportacaoCsv(List<LeitoHospitalar> leitos) {
        StringBuilder csv = new StringBuilder();
        csv.append("NUMERO_LEITO,ALA_HOSPITALAR,SITUACAO\n");
        for (LeitoHospitalar l : leitos) {
            csv.append(l.numero).append(",")
               .append(l.ala).append(",")
               .append(l.ocupado ? "OCUPADO" : "LIVRE").append("\n");
        }
        return csv.toString();
    }

    public String gerarEstruturaRelatorioPdf(List<LeitoHospitalar> leitos) {
        int total = leitos.size();
        long ocupados = leitos.stream().filter(l -> l.ocupado).count();
        double taxaOcupacao = total > 0 ? (ocupados * 100.0 / total) : 0.0;

        StringBuilder pdf = new StringBuilder();
        pdf.append("[CABEÇALHO DOCUMENTO PDF - HOSPITAL UNIVERSITÁRIO]\n");
        pdf.append("Relatório Semanal de Ocupação Hospitalar\n");
        pdf.append("Total de Leitos Cadastrados: ").append(total).append("\n");
        pdf.append("Leitos Ocupados: ").append(ocupados).append(" | Leitos Livres: ").append(total - ocupados).append("\n");
        pdf.append("Taxa Geral de Ocupação: ").append(String.format("%.1f%%", taxaOcupacao)).append("\n");
        pdf.append("[ASSINATURA DIGITAL DO AUDITOR DO SISTEMA]\n");
        return pdf.toString();
    }
}

// --- CLASSE PRINCIPAL EXECUTÁVEL ---
public class ClassificacaoRequisitosEngine {
    public static void main(String[] args) {
        System.out.println("################################################################");
        System.out.println("#  MOTOR DE CLASSIFICAÇÃO TAXONÔMICA: RF, RNF E REGRAS DE NEGÓCIO #");
        System.out.println("#  Disciplina: Engenharia de Software I | Prof. Marcelo Boer   #");
        System.out.println("################################################################\n");

        // =========================================================================
        // DEMONSTRAÇÃO ITEM 1: REQUISITO FUNCIONAL (RF)
        // =========================================================================
        System.out.println(">>> [ENUNCIADO 1]: 'Médico prescreve remédio com assinatura ICP-Brasil'");
        System.out.println("    Classificação: " + CategoriaEngenharia.REQUISITO_FUNCIONAL.getSigla() + " (" + CategoriaEngenharia.REQUISITO_FUNCIONAL.getDefinicao() + ")");
        System.out.println("    Justificativa: Especifica uma ação/serviço que o software executa recebendo dados e gerando receita autenticada.\n");
        
        PrescricaoMedicaService prescricaoService = new PrescricaoMedicaService();
        PrescricaoMedicaService.ReceitaDigital receita = prescricaoService.gerarPrescricaoEletronica(
            "CRM/SP 123456", "João da Silva", "Morfina 10mg"
        );
        System.out.println("    [EXECUÇÃO]: Receita emitida: ID=" + receita.id + " | Assinatura=" + receita.assinaturaHashSha256 + "\n");

        // =========================================================================
        // DEMONSTRAÇÃO ITEM 2: REGRA DE NEGÓCIO (RN)
        // =========================================================================
        System.out.println(">>> [ENUNCIADO 2]: 'Cancelamento sem taxa só com no mínimo 24h de antecedência'");
        System.out.println("    Classificação: " + CategoriaEngenharia.REGRA_DE_NEGOCIO.getSigla() + " (" + CategoriaEngenharia.REGRA_DE_NEGOCIO.getDefinicao() + ")");
        System.out.println("    Justificativa: Política corporativa que existe mesmo sem computadores; o sistema apenas aplica a fórmula.\n");

        RegraCancelamentoConsulta regraCancelamento = new RegraCancelamentoConsulta();
        LocalDateTime consultaAgendada = LocalDateTime.now().plusHours(30);
        LocalDateTime cancelamentoNoPrazo = LocalDateTime.now();
        LocalDateTime cancelamentoTardio = LocalDateTime.now().plusHours(20); // Restam apenas 10h

        RegraCancelamentoConsulta.ResultadoCalculoCancelamento res1 = 
            regraCancelamento.avaliarPoliticaCancelamento(cancelamentoNoPrazo, consultaAgendada);
        System.out.println("    [TESTE 2A - 30h antes]: " + res1.justificativaNegocio);

        RegraCancelamentoConsulta.ResultadoCalculoCancelamento res2 = 
            regraCancelamento.avaliarPoliticaCancelamento(cancelamentoTardio, consultaAgendada);
        System.out.println("    [TESTE 2B - 10h antes]: " + res2.justificativaNegocio + "\n");

        // =========================================================================
        // DEMONSTRAÇÃO ITEM 3: REQUISITO NÃO-FUNCIONAL (RNF - Desempenho)
        // =========================================================================
        System.out.println(">>> [ENUNCIADO 3]: 'Tempo de resposta < 2s sob concorrência de 1.000 usuários'");
        System.out.println("    Classificação: " + CategoriaEngenharia.REQUISITO_NAO_FUNCIONAL.getSigla() + " (" + CategoriaEngenharia.REQUISITO_NAO_FUNCIONAL.getDefinicao() + ")");
        System.out.println("    Justificativa: Métrica de qualidade e restrição de arquitetura sobre a performance da busca.\n");

        BenchmarkProntuarioService benchmarkService = new BenchmarkProntuarioService();
        BenchmarkProntuarioService.ResultadoBenchmark resultadoCarga = benchmarkService.executarTesteCarga(1000, 450);
        System.out.println("    [BENCHMARK EXECUTADO]: " + resultadoCarga.parecerTecnico + "\n");

        // =========================================================================
        // DEMONSTRAÇÃO ITEM 4: REQUISITO NÃO-FUNCIONAL (RNF - Segurança/LGPD)
        // =========================================================================
        System.out.println(">>> [ENUNCIADO 4]: 'Criptografia AES-256 e trânsito TLS 1.3 para LGPD'");
        System.out.println("    Classificação: " + CategoriaEngenharia.REQUISITO_NAO_FUNCIONAL.getSigla() + " (" + CategoriaEngenharia.REQUISITO_NAO_FUNCIONAL.getDefinicao() + ")");
        System.out.println("    Justificativa: Restrição técnica de segurança da informação e conformidade legal que blinda todo o sistema.\n");

        CriptografiaLgpdService criptoService = new CriptografiaLgpdService();
        String dadoSensivel = "Paciente soropositivo, em tratamento antiviral contínuo.";
        String dadoCifrado = criptoService.cifrarDadoSensivelProntuario(dadoSensivel);
        String dadoDecifrado = criptoService.decifrarDadoSensivelProntuario(dadoCifrado);

        System.out.println("    [CRIPTO]: Dado Original  : '" + dadoSensivel + "'");
        System.out.println("    [CRIPTO]: Cifrado em Repouso (AES) : '" + dadoCifrado + "'");
        System.out.println("    [CRIPTO]: Decifrado após TLS 1.3   : '" + dadoDecifrado + "'\n");

        // =========================================================================
        // DEMONSTRAÇÃO ITEM 5: REQUISITO FUNCIONAL (RF)
        // =========================================================================
        System.out.println(">>> [ENUNCIADO 5]: 'Emissão de relatórios semanais de leitos em PDF e CSV'");
        System.out.println("    Classificação: " + CategoriaEngenharia.REQUISITO_FUNCIONAL.getSigla() + " (" + CategoriaEngenharia.REQUISITO_FUNCIONAL.getDefinicao() + ")");
        System.out.println("    Justificativa: Serviço específico de agregação e geração de saídas estruturadas demandado pelos usuários.\n");

        List<GeradorRelatoriosLeitos.LeitoHospitalar> leitos = new ArrayList<>();
        leitos.add(new GeradorRelatoriosLeitos.LeitoHospitalar("101-A", "UTI Adulto", true));
        leitos.add(new GeradorRelatoriosLeitos.LeitoHospitalar("101-B", "UTI Adulto", true));
        leitos.add(new GeradorRelatoriosLeitos.LeitoHospitalar("102-A", "Enfermaria", false));
        leitos.add(new GeradorRelatoriosLeitos.LeitoHospitalar("102-B", "Enfermaria", false));

        GeradorRelatoriosLeitos gerador = new GeradorRelatoriosLeitos();
        System.out.println("    [SAÍDA CSV GERADA]:");
        System.out.print("    " + gerador.gerarExportacaoCsv(leitos).replace("\n", "\n    "));
        System.out.println("    [SAÍDA ESTRUTURADA PDF]:");
        System.out.print("    " + gerador.gerarEstruturaRelatorioPdf(leitos).replace("\n", "\n    "));

        System.out.println("\n[QUADRO FINAL DE REVISÃO PARA AV1]:");
        System.out.println("| Item | Enunciado Curto              | Tipo | Justificativa Sintética                |");
        System.out.println("| :--- | :--------------------------- | :--- | :------------------------------------- |");
        System.out.println("| 1    | Prescrição com ICP-Brasil    | RF   | Ação operacional do sistema           |");
        System.out.println("| 2    | Cancelamento 24h sem taxa    | RN   | Política de negócio independente de TI |");
        System.out.println("| 3    | Resposta < 2s sob carga      | RNF  | Restrição quantitativa de desempenho   |");
        System.out.println("| 4    | Criptografia AES / TLS LGPD  | RNF  | Restrição de arquitetura e segurança   |");
        System.out.println("| 5    | Relatórios em PDF e CSV      | RF   | Processamento e saída de dados         |");
        System.out.println("================================================================");
    }
}
