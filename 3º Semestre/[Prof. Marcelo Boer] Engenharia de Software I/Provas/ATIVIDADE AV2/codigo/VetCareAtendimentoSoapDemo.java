/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Tema: Registro Estruturado de Prontuario SOAP, Inalterabilidade e Validador CRMV
 * Artefatos Relacionados: RF04, RN05, RN06, UC05, UC06, Diagrama de Classes e Sequencia
 *
 * Como compilar:
 *   javac VetCareAtendimentoSoapDemo.java
 * Como executar:
 *   java VetCareAtendimentoSoapDemo
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VetCareAtendimentoSoapDemo {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println(" SISTEMA VETCARE - FLUXO DE ATENDIMENTO CLINICO SOAP (UC05 / UC06) ");
        System.out.println("====================================================================\n");

        ServicoClinico servicoClinico = new ServicoClinico();

        // Profissionais cadastrados no sistema
        VeterinarioClinico vetAtivo = new VeterinarioClinico("54321", "SP", "Dr. Andre Santos", true);
        VeterinarioClinico vetSuspenso = new VeterinarioClinico("99999", "SP", "Dr. Marcio Lima", false);

        // Paciente da clinica
        PetClinico pet = new PetClinico(UUID.randomUUID().toString(), "Thor", 4, 32.4f);

        // --------------------------------------------------------------------
        // 1. Execucao de UC05 (Caminho Feliz) com Validacao de CRMV Ativo (UC06/RN06)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 1] UC05 e UC06: Preenchimento e Fechamento de Prontuario SOAP ---");
        ProntuarioSoap prontuario = servicoClinico.iniciarAtendimento(pet);

        // Preenchimento dos 4 quadrantes SOAP
        prontuario.preencherSubjetivo("Tutor relata apatia, perda de apetite e tosse seca ha 3 dias.");
        prontuario.preencherObjetivo("Temperatura: 38.9 C. FC: 110 bpm. Ausculta pulmonar: estertores em base direita.");
        prontuario.preencherAvaliacao("Suspeita de pneumonia bacteriana leve secundária.");
        prontuario.preencherPlano("Fluidoterapia 500ml + Antibioticoterapia via oral por 10 dias. Retorno em 7 dias.");
        prontuario.adicionarProcedimento("Inalacao Terapeutica");
        prontuario.adicionarProcedimento("Exame de Raio-X Toracico");

        // Fechamento e Assinatura eletronica com validacao de CRMV
        servicoClinico.finalizarEAssinarProntuario(prontuario, vetAtivo);
        System.out.println("Prontuario finalizado com sucesso!");
        System.out.println("ID: " + prontuario.getIdProntuario());
        System.out.println("Status de Fechamento: " + prontuario.isFechado());
        System.out.println("Assinado por: " + prontuario.getCrmvAssinatura() + " em " +
                prontuario.getDataFechamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");

        // --------------------------------------------------------------------
        // 2. Fluxo de Excecao FE01 / RN06: Assinatura por CRMV Inativo/Irregular
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 2] FE01 e RN06: Tentativa de Assinatura por CRMV Inativo ---");
        ProntuarioSoap prontuarioInvalido = servicoClinico.iniciarAtendimento(pet);
        prontuarioInvalido.preencherSubjetivo("Animal com prurido auricular intenso.");
        prontuarioInvalido.preencherObjetivo("Exsudato ceruminoso escuro em ambos os ouvidos.");
        prontuarioInvalido.preencherAvaliacao("Otite fúngica bilateral.");
        prontuarioInvalido.preencherPlano("Limpeza otologica + gotas antifungicas.");

        try {
            servicoClinico.finalizarEAssinarProntuario(prontuarioInvalido, vetSuspenso);
        } catch (SecurityException e) {
            System.out.println("Bloqueio com sucesso (CRMV suspenso): " + e.getMessage());
            System.out.println("Estado do Prontuario permanece em Rascunho (fechado = " + prontuarioInvalido.isFechado() + ")\n");
        }

        // --------------------------------------------------------------------
        // 3. Regra de Negocio RN05: Inalterabilidade do Prontuario Apos 24h e Addendum
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 3] RN05: Inalterabilidade Apos Fechamento e Registro de Adendo (Addendum) ---");

        // Tentativa de alterar o texto de um prontuario fechado diretamente
        try {
            prontuario.preencherSubjetivo("Tentativa de alteracao posterior indevida.");
        } catch (IllegalStateException e) {
            System.out.println("Bloqueio de edicao direta com sucesso: " + e.getMessage());
        }

        // Simulando que se passaram mais de 24 horas do atendimento (RN05)
        prontuario.simularPassagemDeTempo(25); // Avanca 25 horas

        System.out.println("Horas decorridas desde o fechamento: " + prontuario.calcularHorasDesdeFechamento());

        // Adicionando uma retificacao legal via Addendum assinado
        prontuario.adicionarAddendum(vetAtivo, "Resultado de cultura confirmou sensibilidade a Amoxicilina com Clavulanato. Mantido o plano.");
        System.out.println("Adendo (Addendum) registrado com sucesso!");

        System.out.println("\n--- Visualizacao do Prontuario Completo (Auditavel) ---");
        System.out.println(prontuario.gerarEspelhoProntuario());
        System.out.println("====================================================================");
    }
}

// ============================================================================
// MODELO DE DOMINIO - CLASSES DE ANALISE (PRONTUARIO E ENTIDADES)
// ============================================================================

class VeterinarioClinico {
    private final String crmv;
    private final String uf;
    private final String nome;
    private final boolean regular;

    public VeterinarioClinico(String crmv, String uf, String nome, boolean regular) {
        this.crmv = crmv;
        this.uf = uf;
        this.nome = nome;
        this.regular = regular;
    }

    public String getCrmvFormatado() { return crmv + "/" + uf; }
    public String getNome() { return nome; }
    public boolean isRegular() { return regular; }
}

class PetClinico {
    private final String id;
    private final String nome;
    private final int idade;
    private final float peso;

    public PetClinico(String id, String nome, int idade, float peso) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.peso = peso;
    }

    public String getNome() { return nome; }
    public float getPeso() { return peso; }
}

/**
 * Registro aditivo e cumulativo de adendos legais ao prontuario (RN05).
 */
class AddendumProntuario {
    private final LocalDateTime dataRegistro;
    private final String crmvAutor;
    private final String justificativaTexto;

    public AddendumProntuario(LocalDateTime dataRegistro, String crmvAutor, String justificativaTexto) {
        this.dataRegistro = dataRegistro;
        this.crmvAutor = crmvAutor;
        this.justificativaTexto = justificativaTexto;
    }

    public String formatar() {
        return "[" + dataRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                " - Assinado por CRMV: " + crmvAutor + "] " + justificativaTexto;
    }
}

/**
 * Classe central de analise: Prontuario Estruturado SOAP (RF04, RN05).
 */
class ProntuarioSoap {
    private final String idProntuario;
    private final PetClinico pet;
    private final LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private boolean fechado = false;
    private String crmvAssinatura;

    // Quadrantes SOAP
    private String subjetivo = "";
    private String objetivo = "";
    private String avaliacao = "";
    private String plano = "";
    private final List<String> procedimentos = new ArrayList<>();
    private final List<AddendumProntuario> addendums = new ArrayList<>();

    public ProntuarioSoap(String idProntuario, PetClinico pet) {
        this.idProntuario = idProntuario;
        this.pet = pet;
        this.dataAbertura = LocalDateTime.now();
    }

    public void preencherSubjetivo(String s) {
        validarModificacaoPermitida();
        this.subjetivo = s;
    }

    public void preencherObjetivo(String o) {
        validarModificacaoPermitida();
        this.objetivo = o;
    }

    public void preencherAvaliacao(String a) {
        validarModificacaoPermitida();
        this.avaliacao = a;
    }

    public void preencherPlano(String p) {
        validarModificacaoPermitida();
        this.plano = p;
    }

    public void adicionarProcedimento(String proc) {
        validarModificacaoPermitida();
        this.procedimentos.add(proc);
    }

    /**
     * Aplica a RN05: Bloqueia qualquer modificacao direta caso o prontuario ja esteja fechado.
     */
    private void validarModificacaoPermitida() {
        if (this.fechado) {
            throw new IllegalStateException("Inalterabilidade Legal (RN05): Prontuario finalizado nao pode ser alterado diretamente.");
        }
    }

    public void fecharEAssinar(VeterinarioClinico vet) {
        if (subjetivo.isEmpty() || objetivo.isEmpty() || avaliacao.isEmpty() || plano.isEmpty()) {
            throw new IllegalArgumentException("Todos os quadrantes SOAP devem estar preenchidos para homologar o prontuario.");
        }
        this.fechado = true;
        this.dataFechamento = LocalDateTime.now();
        this.crmvAssinatura = vet.getCrmvFormatado();
    }

    public void adicionarAddendum(VeterinarioClinico vet, String texto) {
        if (!this.fechado) {
            throw new IllegalStateException("Adendos so podem ser anexados a prontuarios ja fechados.");
        }
        if (!vet.isRegular()) {
            throw new SecurityException("Veterinario com CRMV irregular nao pode assinar adendo.");
        }
        this.addendums.add(new AddendumProntuario(LocalDateTime.now(), vet.getCrmvFormatado(), texto));
    }

    public void simularPassagemDeTempo(long horas) {
        if (this.dataFechamento != null) {
            this.dataFechamento = this.dataFechamento.minusHours(horas);
        }
    }

    public long calcularHorasDesdeFechamento() {
        if (this.dataFechamento == null) return 0;
        return java.time.Duration.between(this.dataFechamento, LocalDateTime.now()).toHours();
    }

    public String gerarEspelhoProntuario() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PRONTUARIO CLINICO SOAP (#").append(idProntuario).append(") ===\n");
        sb.append("Paciente: ").append(pet.getNome()).append(" | Peso: ").append(pet.getPeso()).append(" kg\n");
        sb.append("[S] SUBJETIVO: ").append(subjetivo).append("\n");
        sb.append("[O] OBJETIVO: ").append(objetivo).append("\n");
        sb.append("[A] AVALIACAO: ").append(avaliacao).append("\n");
        sb.append("[P] PLANO: ").append(plano).append("\n");
        sb.append("Procedimentos Realizados: ").append(String.join(", ", procedimentos)).append("\n");
        sb.append("Assinatura: ").append(crmvAssinatura).append(" em ")
          .append(dataFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        if (!addendums.isEmpty()) {
            sb.append("-- ADENDOS LEGAIS (ADDENDUMS) --\n");
            for (AddendumProntuario add : addendums) {
                sb.append(add.formatar()).append("\n");
            }
        }
        return sb.toString();
    }

    public String getIdProntuario() { return idProntuario; }
    public boolean isFechado() { return fechado; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public String getCrmvAssinatura() { return crmvAssinatura; }
}

// ============================================================================
// CAMADA DE SERVICO - CASO DE USO UC05 / UC06
// ============================================================================

class ServicoClinico {
    public ProntuarioSoap iniciarAtendimento(PetClinico pet) {
        String id = "PRONT-" + UUID.randomUUID().toString().substring(0, 8);
        return new ProntuarioSoap(id, pet);
    }

    /**
     * Caso de Uso UC05: Registrar Atendimento Clinico
     * Acoplado obrigatoriamente a UC06: Autenticar Profissional via CRMV (RN06)
     */
    public void finalizarEAssinarProntuario(ProntuarioSoap prontuario, VeterinarioClinico vet) {
        // UC06 / RN06: Validacao de regularidade do conselho profissional
        if (!vet.isRegular()) {
            throw new SecurityException("CRMV " + vet.getCrmvFormatado() +
                    " encontra-se inativo ou irregular. Assinatura recusada (RN06).");
        }
        prontuario.fecharEAssinar(vet);
    }
}
