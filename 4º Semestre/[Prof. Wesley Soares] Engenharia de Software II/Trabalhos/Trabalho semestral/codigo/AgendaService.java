import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Engenharia de Software II - Trabalho semestral: MedClinic
 * Serviço de agenda: implementa os casos de uso CSU01 (Agendar Consulta, com <<include>> Validar Grade)
 * e CSU03 (Cancelar Agendamento), aplicando as regras de negócio RN01, RN02 e RN04.
 */
public class AgendaService {

    private final List<Consulta> consultas = new ArrayList<>();
    /** Relógio injetável: permite testar regras de antecedência sem depender da hora real. */
    private LocalDateTime agora;

    public AgendaService(LocalDateTime agora) { this.agora = agora; }

    public void definirAgora(LocalDateTime agora) { this.agora = agora; }

    /** CSU01 - Agendar Consulta. synchronized: CT04 simula duas requisições para a mesma vaga. */
    public synchronized Consulta agendar(Paciente p, Medico m, LocalDateTime inicio, BigDecimal valor, boolean particular) {
        // RN02: antecedência mínima de 2 horas.
        if (Duration.between(agora, inicio).toMinutes() < 120)
            throw new IllegalStateException("RN02: agendamento exige antecedência mínima de 2 horas");

        // RF04 (<<include>> Validar Grade de Horários): sem sobreposição na agenda do médico.
        for (Consulta c : ativas())
            if (c.medico.equals(m) && inicio.isBefore(c.fim()) && c.inicio.isBefore(inicio.plusMinutes(30)))
                throw new IllegalStateException("RF04: horário indisponível para " + m.nome());

        // RN01: mesmo paciente, mesma especialidade, mesma data -> bloqueado.
        for (Consulta c : ativas())
            if (c.paciente.cpf().equals(p.cpf()) && c.medico.especialidade().equals(m.especialidade())
                    && c.inicio.toLocalDate().equals(inicio.toLocalDate()))
                throw new IllegalStateException("RN01: paciente já tem consulta de " + m.especialidade() + " nessa data");

        Consulta nova = new Consulta(p, m, inicio, valor, particular);
        consultas.add(nova);
        return nova;
    }

    /**
     * CSU03 - Cancelar Agendamento. Retorna o valor retido.
     * RN04: consultas particulares canceladas com menos de 24h retêm 30% do valor.
     */
    public synchronized BigDecimal cancelar(Consulta c) {
        if (c.status != StatusConsulta.AGENDADA) throw new IllegalStateException("Consulta não está agendada");
        c.status = StatusConsulta.CANCELADA;
        boolean emCimaDaHora = Duration.between(agora, c.inicio).toHours() < 24;
        return c.particular && emCimaDaHora
                ? c.valor.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
    }

    public List<Consulta> ativas() {
        return consultas.stream().filter(c -> c.status == StatusConsulta.AGENDADA).toList();
    }
}
