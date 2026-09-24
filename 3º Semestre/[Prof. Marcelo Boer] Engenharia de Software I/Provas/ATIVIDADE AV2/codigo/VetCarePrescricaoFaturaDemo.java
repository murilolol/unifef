/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Tema: Prescricao com Calculo de Dosagem Segura e Faturamento em Cascata
 * Artefatos Relacionados: RF05, RF07, RN07, RN09, UC07, UC08, UC10
 *
 * Como compilar:
 *   javac VetCarePrescricaoFaturaDemo.java
 * Como executar:
 *   java VetCarePrescricaoFaturaDemo
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VetCarePrescricaoFaturaDemo {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println(" SISTEMA VETCARE - PRESCRICAO MEDICAMENTOSA E FATURAMENTO (RN07/RN09)");
        System.out.println("====================================================================\n");

        ServicoFarmaceutico servicoFarmacia = new ServicoFarmaceutico();
        ServicoFinanceiro servicoFinanceiro = new ServicoFinanceiro();

        // Cadastro de medicamentos no compendio oficial (RF05, RN07)
        Medicamento amoxicilina = new Medicamento("ANV-10293", "Amoxicilina Vet", "Amoxicilina", 10.0f, 20.0f, false);
        Medicamento tramadol = new Medicamento("ANV-88312", "Cloridrato de Tramadol", "Tramadol", 1.0f, 4.0f, true);

        float pesoPet = 15.0f; // Animal com 15 kg
        Prescricao prescricao = new Prescricao(UUID.randomUUID().toString());

        // --------------------------------------------------------------------
        // 1. Prescricao de Medicamento Comum dentro da Faixa Segura (RN07)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 1] RF05 e RN07: Prescricao Normal dentro da Faixa Terapeutica ---");
        // Faixa segura para 15kg de amoxicilina (10 a 20 mg/kg): 150 mg a 300 mg
        float doseNormal = 200.0f;
        ItemPrescricao item1 = servicoFarmacia.prescreverMedicamento(amoxicilina, pesoPet, doseNormal, "A cada 12h", 7, null);
        prescricao.adicionarItem(item1);
        System.out.println("Medicamento: " + amoxicilina.getNomeComercial() + " prescrito com sucesso!");
        System.out.println("Dose: " + item1.getDosePrescritaMg() + " mg | Frequencia: " + item1.getFrequencia() + "\n");

        // --------------------------------------------------------------------
        // 2. Deteccao de Sobredosagem em Controlado sem Justificativa (UC08 / RN07)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 2] UC08 e RN07: Alerta de Sobredosagem (> 20%) sem Justificativa ---");
        // Faixa de tramadol para 15kg (1 a 4 mg/kg): 15 mg a 60 mg. Limite com 20%: 72 mg.
        float doseExcessiva = 95.0f; // Excede mais de 50% da dose maxima

        try {
            servicoFarmacia.prescreverMedicamento(tramadol, pesoPet, doseExcessiva, "A cada 8h", 3, null);
        } catch (IllegalArgumentException e) {
            System.out.println("Bloqueio com sucesso (Alerta UC08 ativado): " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // 3. Sobredosagem com Justificativa Formal Registrada em Auditoria (RN07)
        // --------------------------------------------------------------------
        System.out.println("\n--- [TESTE 3] RN07: Homologacao de Dose com Justificativa Clinica Gravada ---");
        String justificativa = "Paciente oncologico em crise aguda de dor refrataria a analgesia padrao.";
        ItemPrescricao itemControlado = servicoFarmacia.prescreverMedicamento(tramadol, pesoPet, doseExcessiva, "A cada 8h", 3, justificativa);
        prescricao.adicionarItem(itemControlado);
        System.out.println("Item homologado com auditoria! Justificativa arquivada: \"" + itemControlado.getJustificativaAuditoria() + "\"\n");

        // --------------------------------------------------------------------
        // 4. Fechamento de Fatura em Cascata e Regra de Alta Medica (RF07 / RN09)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 4] RF07 e RN09: Fechamento Financeiro e Liberacao de Alta Hospitalar ---");
        InternacaoAtendimento atendimento = new InternacaoAtendimento("Thor (Labrador)");
        Fatura fatura = servicoFinanceiro.gerarFatura(atendimento);

        fatura.adicionarLancamento("Diaria de Internacao UTI", 350.00);
        fatura.adicionarLancamento("Procedimento de Fluidoterapia", 85.00);
        fatura.adicionarLancamento("Insumos Medicamentosos", 120.50);
        System.out.println("Fatura aberta (#" + fatura.getIdFatura() + "). Valor total consolidado: R$ " + fatura.getValorTotal());

        // Tentativa de conceder alta medica com fatura PENDENTE
        try {
            atendimento.concederAltaMedica();
        } catch (IllegalStateException e) {
            System.out.println("Bloqueio de saida com sucesso: " + e.getMessage());
        }

        // Realizacao do pagamento na recepcao
        servicoFinanceiro.liquidarFatura(fatura, "Cartao de Credito 3x");
        System.out.println("Pagamento confirmado. Status da fatura: " + fatura.getStatusPagamento());

        // Nova tentativa de concessao de alta medica apos liquidacao (RN09)
        atendimento.concederAltaMedica();
        System.out.println("Alta hospitalar liberada com sucesso para o paciente!");
        System.out.println("Status final do atendimento: " + atendimento.getStatusAtendimento());
        System.out.println("====================================================================");
    }
}

// ============================================================================
// DOMINIO DE FARMACIA E PRESCRICAO (RF05, RN07, UC07, UC08)
// ============================================================================

class Medicamento {
    private final String codigoAnvisa;
    private final String nomeComercial;
    private final String principioAtivo;
    private final float doseMinimaPorKg;
    private final float doseMaximaPorKg;
    private final boolean controlado;

    public Medicamento(String codigoAnvisa, String nomeComercial, String principioAtivo,
                       float doseMinimaPorKg, float doseMaximaPorKg, boolean controlado) {
        this.codigoAnvisa = codigoAnvisa;
        this.nomeComercial = nomeComercial;
        this.principioAtivo = principioAtivo;
        this.doseMinimaPorKg = doseMinimaPorKg;
        this.doseMaximaPorKg = doseMaximaPorKg;
        this.controlado = controlado;
    }

    public String getCodigoAnvisa() { return codigoAnvisa; }
    public String getNomeComercial() { return nomeComercial; }
    public float getDoseMinimaPorKg() { return doseMinimaPorKg; }
    public float getDoseMaximaPorKg() { return doseMaximaPorKg; }
    public boolean isControlado() { return controlado; }
}

class ItemPrescricao {
    private final Medicamento medicamento;
    private final float dosePrescritaMg;
    private final String frequencia;
    private final int duracaoDias;
    private final String justificativaAuditoria;

    public ItemPrescricao(Medicamento medicamento, float dosePrescritaMg, String frequencia,
                          int duracaoDias, String justificativaAuditoria) {
        this.medicamento = medicamento;
        this.dosePrescritaMg = dosePrescritaMg;
        this.frequencia = frequencia;
        this.duracaoDias = duracaoDias;
        this.justificativaAuditoria = justificativaAuditoria;
    }

    public Medicamento getMedicamento() { return medicamento; }
    public float getDosePrescritaMg() { return dosePrescritaMg; }
    public String getFrequencia() { return frequencia; }
    public String getJustificativaAuditoria() { return justificativaAuditoria; }
}

class Prescricao {
    private final String idPrescricao;
    private final LocalDateTime dataEmissao;
    private final List<ItemPrescricao> itens = new ArrayList<>();

    public Prescricao(String idPrescricao) {
        this.idPrescricao = idPrescricao;
        this.dataEmissao = LocalDateTime.now();
    }

    public void adicionarItem(ItemPrescricao item) {
        this.itens.add(item);
    }

    public List<ItemPrescricao> getItens() { return Collections.unmodifiableList(itens); }
}

class ServicoFarmaceutico {
    /**
     * Aplica o Caso de Uso UC07 e UC08 (Ponto de Extensao de Sobredosagem - RN07).
     */
    public ItemPrescricao prescreverMedicamento(Medicamento med, float pesoKg, float doseInformadaMg,
                                               String frequencia, int duracaoDias, String justificativa) {
        float doseMinRecomendada = pesoKg * med.getDoseMinimaPorKg();
        float doseMaxRecomendada = pesoKg * med.getDoseMaximaPorKg();

        // Tolerancia de 20% acima da dose maxima ou abaixo da minima (RN07)
        float limiteSuperiorTolerado = doseMaxRecomendada * 1.20f;
        float limiteInferiorTolerado = doseMinRecomendada * 0.80f;

        boolean doseDivergente = (doseInformadaMg > limiteSuperiorTolerado) || (doseInformadaMg < limiteInferiorTolerado);

        if (doseDivergente) {
            if (justificativa == null || justificativa.trim().length() < 15) {
                throw new IllegalArgumentException("Dose de " + doseInformadaMg + " mg excede em >20% a faixa segura (" +
                        doseMinRecomendada + " a " + doseMaxRecomendada + " mg) para paciente de " + pesoKg + " kg. " +
                        "Exige justificativa formal de auditoria (RN07 / UC08).");
            }
        }

        return new ItemPrescricao(med, doseInformadaMg, frequencia, duracaoDias, justificativa);
    }
}

// ============================================================================
// DOMINIO FINANCEIRO E CASO DE USO DE FATURAMENTO (RF07, RN09, UC10)
// ============================================================================

class Fatura {
    private final String idFatura;
    private double valorTotal = 0.0;
    private String statusPagamento = "PENDENTE"; // PENDENTE, LIQUIDADA, PARCELADA
    private final List<String> discriminacaoItens = new ArrayList<>();

    public Fatura(String idFatura) {
        this.idFatura = idFatura;
    }

    public void adicionarLancamento(String descricao, double valor) {
        this.discriminacaoItens.add(descricao + " - R$ " + valor);
        this.valorTotal += valor;
    }

    public void pagar(String modalidade) {
        this.statusPagamento = "LIQUIDADA";
    }

    public String getIdFatura() { return idFatura; }
    public double getValorTotal() { return valorTotal; }
    public String getStatusPagamento() { return statusPagamento; }
}

class InternacaoAtendimento {
    private final String nomePet;
    private Fatura fatura;
    private String statusAtendimento = "EM_ANDAMENTO"; // EM_ANDAMENTO, ALTA_LIBERADA

    public InternacaoAtendimento(String nomePet) {
        this.nomePet = nomePet;
    }

    public void vincularFatura(Fatura fatura) {
        this.fatura = fatura;
    }

    /**
     * Aplica a RN09: Bloqueio de alta medica enquanto houver pendencia financeira.
     */
    public void concederAltaMedica() {
        if (this.fatura == null) {
            throw new IllegalStateException("Atendimento sem fatura gerada. Impossivel liquidar alta (RN09).");
        }
        if (!"LIQUIDADA".equals(this.fatura.getStatusPagamento()) && !"PARCELADA".equals(this.fatura.getStatusPagamento())) {
            throw new IllegalStateException("Retencao de Alta (RN09): Paciente nao pode ser liberado com fatura " +
                    this.fatura.getStatusPagamento() + ". Realize a quitacao na recepcao.");
        }
        this.statusAtendimento = "ALTA_LIBERADA";
    }

    public String getStatusAtendimento() { return statusAtendimento; }
}

class ServicoFinanceiro {
    public Fatura gerarFatura(InternacaoAtendimento atendimento) {
        Fatura f = new Fatura("FAT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        atendimento.vincularFatura(f);
        return f;
    }

    public void liquidarFatura(Fatura fatura, String modalidade) {
        fatura.pagar(modalidade);
    }
}
