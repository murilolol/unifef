/**
 * Disciplina: Engenharia de Software II
 * Tema: Diferenciação entre <<include>> e <<extend>> na UML (Sistema Hospitalar)
 * 
 * Como compilar e executar:
 *   javac HospitalProntoAtendimentoApp.java
 *   java HospitalProntoAtendimentoApp
 */

// ============================================================================
// MODELO DE DOMÍNIO E CLASSIFICAÇÃO DE RISCO (TRIAGEM MANCHESTER)
// ============================================================================

enum GravidadeTriagem {
    VERDE_LEVE("Atendimento ambulatorial eletivo ou de baixo risco"),
    AMARELO_MODERADO("Urgência relativa com monitoramento em enfermaria"),
    VERMELHO_EMERGENCIA("Emergência crítica com iminência de parada cardiorrespiratória");

    private final String descricao;
    GravidadeTriagem(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}

class Paciente {
    private final String prontuario;
    private final String nome;
    private final int pressaoSistolica;
    private final int batimentosPorMinuto;

    public Paciente(String prontuario, String nome, int pressaoSistolica, int batimentosPorMinuto) {
        this.prontuario = prontuario;
        this.nome = nome;
        this.pressaoSistolica = pressaoSistolica;
        this.batimentosPorMinuto = batimentosPorMinuto;
    }

    public String getProntuario() { return prontuario; }
    public String getNome() { return nome; }
    public int getPressaoSistolica() { return pressaoSistolica; }
    public int getBatimentosPorMinuto() { return batimentosPorMinuto; }
}

// ============================================================================
// CASOS DE USO E RELACIONAMENTOS FORMAIS DA UML
// ============================================================================

/**
 * Caso de Uso Incluído: Realizar Triagem (<<include>>).
 * SEMÂNTICA TÉCNICA: O caso base não tem autonomia para concluir sem a triagem.
 * A triagem é executada incondicionalmente em 100% dos atendimentos médicos.
 */
class RealizarTriagemUseCase {
    public GravidadeTriagem executar(Paciente paciente) {
        System.out.println("   [<<include>> Realizar Triagem] Coletando sinais vitais do paciente: " + paciente.getNome());
        System.out.println("   PA: " + paciente.getPressaoSistolica() + " mmHg | FC: " + paciente.getBatimentosPorMinuto() + " bpm");

        // Regra de Classificação de Risco
        if (paciente.getBatimentosPorMinuto() > 140 || paciente.getPressaoSistolica() > 190) {
            System.out.println("   => Classificação: VERMELHO_EMERGENCIA (Instabilidade hemodinâmica)");
            return GravidadeTriagem.VERMELHO_EMERGENCIA;
        } else if (paciente.getBatimentosPorMinuto() > 100) {
            System.out.println("   => Classificação: AMARELO_MODERADO");
            return GravidadeTriagem.AMARELO_MODERADO;
        }
        System.out.println("   => Classificação: VERDE_LEVE");
        return GravidadeTriagem.VERDE_LEVE;
    }
}

/**
 * Caso de Uso de Extensão: Acionar Equipe de Trauma (<<extend>>).
 * SEMÂNTICA TÉCNICA: Trata-se de um comportamento condicional e excepcional.
 * Apenas estende a base no ponto de extensão 'Severidade Crítica' caso a guarda seja verdadeira.
 */
class AcionarEquipeTraumaUseCase {
    public void executar(Paciente paciente) {
        System.out.println("   [<<extend>> Acionar Equipe de Trauma] PONTO DE EXTENSÃO ATIVADO!");
        System.out.println("   Alarme Sonoro de Código Vermelho disparado para sala cirúrgica.");
        System.out.println("   Cirurgião de plantão e anestesista mobilizados para o paciente: " + paciente.getNome());
    }
}

/**
 * Caso de Uso Base: Atender Paciente.
 * Conecta-se ao Ator Primário Enfermeiro e orquestra as dependências.
 */
class AtenderPacienteUseCase {
    private final RealizarTriagemUseCase triagemUC;
    private final AcionarEquipeTraumaUseCase equipeTraumaUC;

    public AtenderPacienteUseCase(RealizarTriagemUseCase triagemUC, AcionarEquipeTraumaUseCase equipeTraumaUC) {
        this.triagemUC = triagemUC;
        this.equipeTraumaUC = equipeTraumaUC;
    }

    public void executar(Paciente paciente) {
        System.out.println("\n======================================================================");
        System.out.println("Caso de Uso Base: Atender Paciente (Prontuário: " + paciente.getProntuario() + ")");
        System.out.println("======================================================================");

        // 1. Relacionamento <<include>> (Obrigatório e incondicional)
        // A seta no diagrama parte de Atender Paciente em direção a Realizar Triagem
        GravidadeTriagem risco = triagemUC.executar(paciente);

        // 2. Ponto de Extensão Formal: 'Severidade Crítica'
        // A seta no diagrama parte de Acionar Equipe de Trauma em direção a Atender Paciente
        // Condição de Guarda: [risco == VERMELHO_EMERGENCIA]
        if (risco == GravidadeTriagem.VERMELHO_EMERGENCIA) {
            System.out.println(" -> Condição de guarda satisfeita para o relacionamento <<extend>>.");
            equipeTraumaUC.executar(paciente);
        } else {
            System.out.println(" -> Condição de guarda NÃO satisfeita. Fluxo de extensão mantido inativo.");
            System.out.println("   Paciente encaminhado à sala de espera ambulatorial padrão.");
        }

        System.out.println("Caso de Uso Base finalizado com segurança.");
    }
}

// ============================================================================
// CLASSE PRINCIPAL EXECUTÁVEL
// ============================================================================
public class HospitalProntoAtendimentoApp {
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   ENGENHARIA DE SOFTWARE II - DEMONSTRAÇÃO DE RELACIONAMENTOS UML");
        System.out.println("   Comparativo Rigoroso: <<include>> (Mandatório) vs <<extend>> (Opcional)");
        System.out.println("====================================================================");

        RealizarTriagemUseCase triagemUC = new RealizarTriagemUseCase();
        AcionarEquipeTraumaUseCase traumaUC = new AcionarEquipeTraumaUseCase();
        AtenderPacienteUseCase atendimentoUC = new AtenderPacienteUseCase(triagemUC, traumaUC);

        // Cenário A: Paciente com sintomas leves (Verde)
        // Deve executar a triagem (include), mas NÃO deve estender para equipe de trauma
        Paciente p1 = new Paciente("HOSP-2026-01", "Luciana Ribeiro", 120, 75);
        atendimentoUC.executar(p1);

        // Cenário B: Paciente com taquicardia severa e hipertensão aguda (Vermelho)
        // Deve executar a triagem (include) E ativar a extensão excepcional (extend)
        Paciente p2 = new Paciente("HOSP-2026-02", "Marcos Vinicius", 210, 165);
        atendimentoUC.executar(p2);
    }
}
