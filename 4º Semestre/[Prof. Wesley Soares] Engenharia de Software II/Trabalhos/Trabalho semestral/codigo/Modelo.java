import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Engenharia de Software II - Trabalho semestral: MedClinic
 * Modelo de domínio derivado do diagrama de classes/casos de uso do detalhes.md.
 * Entidades: Paciente (RF01), Medico, Consulta (RF02/RF03), Prontuario (RF05, RN03).
 * Compilar/executar junto com os demais: javac *.java && java MedClinicDemo
 */
public final class Modelo {
    private Modelo() {}
}

/** RF01: dados cadastrais do paciente; o CPF é validado pelos dígitos verificadores. */
record Paciente(String nome, String cpf, String telefone) {
    Paciente {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        cpf = cpf.replaceAll("\\D", "");
        if (!cpfValido(cpf)) throw new IllegalArgumentException("CPF inválido: " + cpf);
    }

    static boolean cpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;
        for (int t = 9; t < 11; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) soma += (cpf.charAt(i) - '0') * (t + 1 - i);
            int digito = (soma * 10) % 11 % 10;
            if (cpf.charAt(t) - '0' != digito) return false;
        }
        return true;
    }
}

record Medico(String nome, String crm, String especialidade) {}

enum StatusConsulta { AGENDADA, CANCELADA, REALIZADA }

/** RF02/RF03: consulta agendada; o valor é usado na regra de retenção do cancelamento (RN04). */
final class Consulta {
    private static int sequencia = 1;
    final int protocolo = sequencia++;
    final Paciente paciente;
    final Medico medico;
    final LocalDateTime inicio;
    final BigDecimal valor;
    final boolean particular;
    StatusConsulta status = StatusConsulta.AGENDADA;

    Consulta(Paciente paciente, Medico medico, LocalDateTime inicio, BigDecimal valor, boolean particular) {
        this.paciente = paciente;
        this.medico = medico;
        this.inicio = inicio;
        this.valor = valor;
        this.particular = particular;
    }

    LocalDateTime fim() { return inicio.plusMinutes(30); }

    @Override
    public String toString() {
        return String.format("#%d %s com %s (%s) em %s [%s]", protocolo, paciente.nome(), medico.nome(), medico.especialidade(), inicio, status);
    }
}

/** RF05 + RN03: prontuário eletrônico imutável após assinatura; correções viram adendos. */
final class Prontuario {
    private final Paciente paciente;
    private final List<String> registros = new ArrayList<>();
    private boolean assinado;

    Prontuario(Paciente paciente) { this.paciente = paciente; }

    void registrar(String anamnese) {
        if (assinado) throw new IllegalStateException("RN03: prontuário assinado não pode ser editado; registre um adendo");
        registros.add(anamnese);
    }

    void assinar() { assinado = true; }

    void adendo(String texto) { registros.add("[ADENDO " + LocalDateTime.now().withNano(0) + "] " + texto); }

    List<String> registros() { return Collections.unmodifiableList(registros); }

    Paciente paciente() { return paciente; }
}
