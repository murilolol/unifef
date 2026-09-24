import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Engenharia de Software II - Trabalho semestral: MedClinic
 * Executa os casos de teste da matriz de rastreabilidade (CT01 a CT05) do detalhes.md.
 * Compilar/executar: javac *.java && java MedClinicDemo
 */
public class MedClinicDemo {

    private static int ok, falhas;

    private static void verificar(String ct, boolean condicao, String detalhe) {
        if (condicao) ok++; else falhas++;
        System.out.printf("%-5s %-6s %s%n", ct, condicao ? "OK" : "FALHOU", detalhe);
    }

    private static boolean lanca(Runnable acao) {
        try { acao.run(); return false; } catch (RuntimeException e) { return true; }
    }

    public static void main(String[] args) throws Exception {
        LocalDateTime hoje = LocalDateTime.of(2026, 9, 1, 8, 0);
        AgendaService agenda = new AgendaService(hoje);
        Medico cardio = new Medico("Dra. Silva", "CRM-SP 12345", "Cardiologia");
        Medico derma = new Medico("Dr. Costa", "CRM-SP 67890", "Dermatologia");

        // CT01 - RF01: CPF válido aceito, CPF inválido recusado.
        Paciente ana = new Paciente("Ana Souza", "529.982.247-25", "(16) 99999-0000");
        verificar("CT01", lanca(() -> new Paciente("João", "111.111.111-11", "")), "CPF válido aceito (" + ana.cpf() + ") e CPF inválido recusado");

        // CT02 - RF02: agendamento em vaga livre gera protocolo.
        Consulta c1 = agenda.agendar(ana, cardio, hoje.plusDays(2).withHour(10), new BigDecimal("300.00"), true);
        verificar("CT02", c1.protocolo > 0, "agendado: " + c1);

        // RN01 e RN02 (regras complementares do CSU01).
        verificar("RN01", lanca(() -> agenda.agendar(ana, new Medico("Dr. Lima", "CRM-SP 1", "Cardiologia"), hoje.plusDays(2).withHour(15), BigDecimal.TEN, true)),
                "segunda consulta de Cardiologia no mesmo dia bloqueada");
        verificar("RN02", lanca(() -> agenda.agendar(ana, derma, hoje.plusMinutes(60), BigDecimal.TEN, true)),
                "agendamento com menos de 2h de antecedência bloqueado");

        // CT03 - RF03/RN04: cancelamento com mais de 24h (sem retenção) e com menos de 24h (retém 30%).
        Consulta c2 = agenda.agendar(ana, derma, hoje.plusDays(5).withHour(9), new BigDecimal("200.00"), true);
        BigDecimal retidoCedo = agenda.cancelar(c2);
        agenda.definirAgora(c1.inicio.minusHours(10));
        BigDecimal retidoTarde = agenda.cancelar(c1);
        verificar("CT03", retidoCedo.signum() == 0 && retidoTarde.compareTo(new BigDecimal("90.00")) == 0,
                "cancelado com >24h retém R$ " + retidoCedo + "; com <24h retém R$ " + retidoTarde + " (30% de R$ 300)");

        // CT04 - RF04: duas requisições simultâneas para a mesma vaga; só uma pode vencer.
        agenda.definirAgora(hoje);
        Paciente bruno = new Paciente("Bruno Lima", "390.533.447-05", "");
        Paciente carla = new Paciente("Carla Dias", "153.509.460-56", "");
        LocalDateTime vaga = hoje.plusDays(3).withHour(14);
        AtomicInteger sucessos = new AtomicInteger();
        CountDownLatch largada = new CountDownLatch(1);
        Thread[] threads = new Thread[2];
        Paciente[] disputa = {bruno, carla};
        for (int i = 0; i < 2; i++) {
            Paciente p = disputa[i];
            threads[i] = new Thread(() -> {
                try {
                    largada.await();
                    agenda.agendar(p, cardio, vaga, new BigDecimal("300.00"), false);
                    sucessos.incrementAndGet();
                } catch (Exception ignored) {
                    // perdeu a corrida: horário já ocupado
                }
            });
            threads[i].start();
        }
        largada.countDown();
        for (Thread t : threads) t.join();
        verificar("CT04", sucessos.get() == 1, "concorrência na mesma vaga: " + sucessos.get() + " agendamento(s) efetivado(s)");

        // CT05 - RF05/RN03: prontuário assinado não pode ser editado; correção por adendo.
        Prontuario pr = new Prontuario(ana);
        pr.registrar("Anamnese: dor torácica atípica; CID-10 R07.4");
        pr.assinar();
        boolean bloqueou = lanca(() -> pr.registrar("alteração indevida"));
        pr.adendo("Correção: acrescentar pressão arterial 12x8");
        verificar("CT05", bloqueou && pr.registros().size() == 2, "edição pós-assinatura bloqueada; adendo registrado");

        System.out.printf("%nResultado: %d OK, %d falha(s)%n", ok, falhas);
        if (falhas > 0) System.exit(1);
    }
}
