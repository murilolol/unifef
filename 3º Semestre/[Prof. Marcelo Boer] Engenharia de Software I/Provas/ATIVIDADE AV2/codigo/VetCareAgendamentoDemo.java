/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Tema: Implementacao de Requisitos e Regras de Negocio de Agendamento (VetCare)
 * Artefatos Relacionados: RF01, RF02, RF03, RN01, RN03, RN04, UC02, UC03, UC04
 *
 * Como compilar:
 *   javac VetCareAgendamentoDemo.java
 * Como executar:
 *   java VetCareAgendamentoDemo
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VetCareAgendamentoDemo {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println(" SISTEMA VETCARE - DEMONSTRACAO DE REQUISITOS E REGRAS DE NEGOCIO ");
        System.out.println("====================================================================\n");

        ServicoAgendamento servico = new ServicoAgendamento();

        // --------------------------------------------------------------------
        // 1. Validacao de RF01 e RN01 (Validacao Algoritmica de CPF)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 1] RF01 e RN01: Cadastro de Tutor com Validacao de CPF ---");
        try {
            servico.cadastrarCliente("Carlos Silva", "12345678900", "carlos@email.com", "(17) 99999-1111");
        } catch (IllegalArgumentException e) {
            System.out.println("Detectado erro esperado de CPF invalido: " + e.getMessage());
        }

        // CPF matematicamente valido (algoritmo receita federal)
        Cliente tutorValido = servico.cadastrarCliente("Mariana Costa", "09152342084", "mariana@email.com", "(17) 98888-2222");
        Pet pet1 = new Pet(UUID.randomUUID().toString(), "981098123456789", "Rex", "Canina", "Labrador", 25.5f);
        tutorValido.adicionarPet(pet1);
        System.out.println("Tutor cadastrado com sucesso: " + tutorValido.getNome() + " | CPF: " + tutorValido.getCpf());
        System.out.println("Pet vinculado: " + pet1.getNome() + " | Microchip ISO: " + pet1.getMicrochip() + "\n");

        // --------------------------------------------------------------------
        // 2. Execucao de UC02 e UC03: Caminho Feliz de Agendamento (RF02)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 2] UC02 / UC03: Agendamento de Consulta sem Conflito ---");
        Veterinario vet = new Veterinario("12345", "SP", "Dra. Juliana Mendes", "Clinica Geral");
        LocalDateTime dataConsulta = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);

        Agendamento ag1 = servico.agendarConsulta(tutorValido, pet1, vet, dataConsulta, 30, false);
        System.out.println("Agendamento realizado com sucesso!");
        System.out.println("Protocolo: " + ag1.getProtocolo());
        System.out.println("Horario: " + ag1.getDataHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                " as " + ag1.getDataHoraFim().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n");

        // --------------------------------------------------------------------
        // 3. Validacao de RN03 (Conflito de Grade e Margem Minima de 5 Minutos)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 3] RN03: Deteccao de Conflito de Horario na Grade do Veterinario ---");
        Pet pet2 = new Pet(UUID.randomUUID().toString(), "981098987654321", "Thor", "Canina", "Bulldog", 18.0f);
        tutorValido.adicionarPet(pet2);

        // Tentativa de agendamento sobreposto (mesmo horario)
        try {
            servico.agendarConsulta(tutorValido, pet2, vet, dataConsulta, 30, false);
        } catch (IllegalStateException e) {
            System.out.println("Bloqueio com sucesso (Sobreposicao total): " + e.getMessage());
        }

        // Tentativa de agendamento colado sem o intervalo minimo de 5 minutos (RN03)
        LocalDateTime horarioSemIntervalo = ag1.getDataHoraFim().plusMinutes(2);
        try {
            servico.agendarConsulta(tutorValido, pet2, vet, horarioSemIntervalo, 30, false);
        } catch (IllegalStateException e) {
            System.out.println("Bloqueio com sucesso (Violacao de margem de 5 min): " + e.getMessage() + "\n");
        }

        // --------------------------------------------------------------------
        // 4. Validacao de RN04 e UC04 (Politica de No-Show e Bloqueio de Agendamento)
        // --------------------------------------------------------------------
        System.out.println("--- [TESTE 4] RN04 e UC04: Politica de No-Show Tardio e Exigencia de Sinal ---");
        Cliente tutorFaltoso = servico.cadastrarCliente("Lucas Andrade", "89524021008", "lucas@email.com", "(17) 97777-3333");
        Pet petFaltoso = new Pet(UUID.randomUUID().toString(), "981098111222333", "Mel", "Felina", "Siames", 4.2f);
        tutorFaltoso.adicionarPet(petFaltoso);

        // Simula 3 faltas consecutivas sem cancelamento prévio de 2 horas
        tutorFaltoso.incrementarNoShow();
        tutorFaltoso.incrementarNoShow();
        tutorFaltoso.incrementarNoShow();
        System.out.println("Tutor: " + tutorFaltoso.getNome() + " atingiu " + tutorFaltoso.getQuantidadeNoShow() + " faltas (no-show).");

        LocalDateTime dataSemanaQueVem = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0);

        // Tentativa 1: agendar sem sinal -> Bloqueado por RN04
        try {
            servico.agendarConsulta(tutorFaltoso, petFaltoso, vet, dataSemanaQueVem, 30, false);
        } catch (SecurityException e) {
            System.out.println("Bloqueio por falta de sinal (esperado): " + e.getMessage());
        }

        // Tentativa 2: agendar confirmando o pagamento de sinal -> Liberado
        Agendamento agSinal = servico.agendarConsulta(tutorFaltoso, petFaltoso, vet, dataSemanaQueVem, 30, true);
        System.out.println("Agendamento liberado com pagamento de sinal antecipado! Protocolo: " + agSinal.getProtocolo());
        System.out.println("====================================================================");
    }
}

// ============================================================================
// CLASSES DE DOMINIO E ENGENHARIA DE REQUISITOS
// ============================================================================

/**
 * Representa o Tutor cadastrado (RF01).
 * Encapsula a regra de identificacao de CPF e historico de assiduidade (RN01, RN04).
 */
class Cliente {
    private final String nome;
    private final String cpf;
    private final String email;
    private final String telefone;
    private final List<Pet> pets = new ArrayList<>();
    private int quantidadeNoShow = 0;

    public Cliente(String nome, String cpf, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public void adicionarPet(Pet pet) {
        this.pets.add(pet);
    }

    public void incrementarNoShow() {
        this.quantidadeNoShow++;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public int getQuantidadeNoShow() { return quantidadeNoShow; }
    public List<Pet> getPets() { return Collections.unmodifiableList(pets); }
}

/**
 * Representa o Paciente no sistema (RF01 e RN02).
 */
class Pet {
    private final String idPet;
    private final String microchip;
    private final String nome;
    private final String especie;
    private final String raca;
    private float pesoAtual;

    public Pet(String idPet, String microchip, String nome, String especie, String raca, float pesoAtual) {
        this.idPet = idPet;
        this.microchip = microchip;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.pesoAtual = pesoAtual;
    }

    public String getIdPet() { return idPet; }
    public String getMicrochip() { return microchip; }
    public String getNome() { return nome; }
    public float getPesoAtual() { return pesoAtual; }
    public void setPesoAtual(float pesoAtual) { this.pesoAtual = pesoAtual; }
}

/**
 * Representa o profissional veterinario credenciado no CRMV (RN06).
 */
class Veterinario {
    private final String crmv;
    private final String ufCrmv;
    private final String nome;
    private final String especialidade;

    public Veterinario(String crmv, String ufCrmv, String nome, String especialidade) {
        this.crmv = crmv;
        this.ufCrmv = ufCrmv;
        this.nome = nome;
        this.especie = especialidade;
        this.especialidade = especialidade;
    }
    private String especie;

    public String getCrmv() { return crmv; }
    public String getUfCrmv() { return ufCrmv; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
}

/**
 * Entidade Agendamento modelada no Diagrama de Classes de Analise.
 */
class Agendamento {
    private final String protocolo;
    private final Cliente tutor;
    private final Pet pet;
    private final Veterinario veterinario;
    private final LocalDateTime dataHoraInicio;
    private final LocalDateTime dataHoraFim;
    private String status; // RESERVADO, CONFIRMADO, CANCELADO, NO_SHOW

    public Agendamento(String protocolo, Cliente tutor, Pet pet, Veterinario veterinario,
                       LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        this.protocolo = protocolo;
        this.tutor = tutor;
        this.pet = pet;
        this.veterinario = veterinario;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = "RESERVADO";
    }

    public String getProtocolo() { return protocolo; }
    public Cliente getTutor() { return tutor; }
    public Pet getPet() { return pet; }
    public Veterinario getVeterinario() { return veterinario; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// ============================================================================
// SERVICOS DE REGRAS DE NEGOCIO E CASOS DE USO (UC02, UC03, UC04)
// ============================================================================

class ServicoAgendamento {
    private final List<Cliente> clientesCadastrados = new ArrayList<>();
    private final List<Agendamento> agendamentos = new ArrayList<>();

    /**
     * Implementacao de RF01 acoplada a RN01 (Validacao algoritmica estrita do CPF).
     */
    public Cliente cadastrarCliente(String nome, String cpf, String email, String telefone) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        if (!ValidadorCPF.isValido(cpfLimpo)) {
            throw new IllegalArgumentException("CPF invalido perante o algoritmo da Receita Federal: " + cpf);
        }
        for (Cliente c : clientesCadastrados) {
            if (c.getCpf().equals(cpfLimpo)) {
                throw new IllegalArgumentException("CPF ja cadastrado no sistema (Bloqueio de Duplicidade - RN01).");
            }
        }
        Cliente novo = new Cliente(nome, cpfLimpo, email, telefone);
        clientesCadastrados.add(novo);
        return novo;
    }

    /**
     * Caso de Uso UC02: Agendar Consulta/Procedimento
     * Inclui UC03: Validar Disponibilidade de Agenda (RN03)
     * Estende UC04: Politica de No-Show (RN04)
     */
    public Agendamento agendarConsulta(Cliente cliente, Pet pet, Veterinario vet,
                                       LocalDateTime dataHora, int duracaoMinutos, boolean sinalPago) {
        // Ponto de Extensao UC04 / RN04: Cliente com 3 ou mais No-Shows
        if (cliente.getQuantidadeNoShow() >= 3 && !sinalPago) {
            throw new SecurityException("Cliente possui " + cliente.getQuantidadeNoShow() +
                    " no-shows registrados. Exigido pagamento antecipado do sinal (RN04).");
        }

        LocalDateTime dataFim = dataHora.plusMinutes(duracaoMinutos);

        // Validacao UC03 / RN03: Conflito de Grade com tolerancia de 5 min
        validarDisponibilidadeAgenda(vet, dataHora, dataFim);

        String protocolo = "AG-" + System.currentTimeMillis();
        Agendamento novoAgendamento = new Agendamento(protocolo, cliente, pet, vet, dataHora, dataFim);
        agendamentos.add(novoAgendamento);
        return novoAgendamento;
    }

    /**
     * Implementacao de RN03: Margem minima de 5 minutos e ausencia de sobreposicao.
     */
    private void validarDisponibilidadeAgenda(Veterinario vet, LocalDateTime inicio, LocalDateTime fim) {
        for (Agendamento ag : agendamentos) {
            if (!ag.getVeterinario().getCrmv().equals(vet.getCrmv())) {
                continue;
            }
            if ("CANCELADO".equals(ag.getStatus())) {
                continue;
            }

            // Verifica sobreposicao direta: (InicioA < FimB) && (FimA > InicioB)
            boolean sobrepoe = inicio.isBefore(ag.getDataHoraFim()) && fim.isAfter(ag.getDataHoraInicio());
            if (sobrepoe) {
                throw new IllegalStateException("Conflito de horario com agendamento existente (Protocolo: " +
                        ag.getProtocolo() + ").");
            }

            // Verifica regra de 5 minutos de intervalo (RN03)
            long diffMinutosAntes = Math.abs(Duration.between(inicio, ag.getDataHoraFim()).toMinutes());
            long diffMinutosDepois = Math.abs(Duration.between(fim, ag.getDataHoraInicio()).toMinutes());

            if (fim.isBefore(ag.getDataHoraInicio()) && diffMinutosDepois < 5) {
                throw new IllegalStateException("Intervalo inferior a 5 minutos entre consultas do veterinario (RN03).");
            }
            if (inicio.isAfter(ag.getDataHoraFim()) && diffMinutosAntes < 5) {
                throw new IllegalStateException("Intervalo inferior a 5 minutos apos a consulta anterior (RN03).");
            }
        }
    }
}

/**
 * Utilitario formal de verificacao de digitos do CPF (RN01).
 */
class ValidadorCPF {
    public static boolean isValido(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        try {
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += (cpf.charAt(i) - '0') * (10 - i);
            }
            int digito1 = 11 - (soma1 % 11);
            if (digito1 > 9) digito1 = 0;

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += (cpf.charAt(i) - '0') * (11 - i);
            }
            int digito2 = 11 - (soma2 % 11);
            if (digito2 > 9) digito2 = 0;

            return (cpf.charAt(9) - '0' == digito1) && (cpf.charAt(10) - '0' == digito2);
        } catch (Exception e) {
            return false;
        }
    }
}
