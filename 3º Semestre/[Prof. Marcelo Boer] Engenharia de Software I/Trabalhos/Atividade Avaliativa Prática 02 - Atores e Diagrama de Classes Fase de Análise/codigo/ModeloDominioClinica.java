/**
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Atividade Avaliativa Pratica 02 - Diagrama de Classes da Fase de Analise
 *
 * Descricao:
 * Implementacao orientada a objetos das classes conceituais do Modelo de Dominio
 * do sistema MediSys (Clinica Medica). Este arquivo traduz com fidelidade estrita
 * os conceitos da Fase de Analise: Generalizacao (Pessoa -> Medico, Paciente),
 * Composicao Estrita (Paciente *-- Prontuario; Consulta *-- Prescricao *-- ItemPrescricao),
 * Agregacao Compartilhada (Prontuario o-- Consulta) e Associacoes Simples com multiplicidades,
 * sem qualquer contaminacao por frameworks, DAOs, Controllers ou chaves estrangeiras relacionais.
 *
 * Como compilar:
 *   javac ModeloDominioClinica.java
 *
 * Como executar:
 *   java ModeloDominioClinica
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ModeloDominioClinica {

    // =========================================================================
    // 1. GENERALIZACAO: CLASSE ABSTRATA DE DOMINIO
    // =========================================================================

    /**
     * Superclasse conceitual Pessoa.
     * Na Fase de Analise, encapsula dados civis e de contato comuns a todos os
     * individuos atendidos ou contratados pela instituicao.
     */
    public static abstract class Pessoa {
        private String nomeCompleto;
        private String cpf;
        private LocalDate dataNascimento;
        private String telefoneContato;
        private String email;
        private String enderecoResidencial;

        public Pessoa(String nomeCompleto, String cpf, LocalDate dataNascimento,
                      String telefoneContato, String email, String enderecoResidencial) {
            this.nomeCompleto = Objects.requireNonNull(nomeCompleto, "Nome completo e obrigatorio");
            this.cpf = Objects.requireNonNull(cpf, "CPF e obrigatorio");
            this.dataNascimento = Objects.requireNonNull(dataNascimento, "Data de nascimento e obrigatoria");
            this.telefoneContato = telefoneContato;
            this.email = email;
            this.enderecoResidencial = enderecoResidencial;
        }

        public String getNomeCompleto() { return nomeCompleto; }
        public String getCpf() { return cpf; }
        public LocalDate getDataNascimento() { return dataNascimento; }
        public String getTelefoneContato() { return telefoneContato; }
        public String getEmail() { return email; }
        public String getEnderecoResidencial() { return enderecoResidencial; }

        /**
         * Atributo derivado (/idade na UML):
         * Em modelagem conceitual, atributos computaveis nao devem ser persistidos
         * como valores brutos, mas calculados a partir da data de nascimento.
         */
        public int getIdade() {
            return Period.between(this.dataNascimento, LocalDate.now()).getYears();
        }
    }

    // =========================================================================
    // 2. ESPECIALIZACOES (HERANCA CONCEITUAL)
    // =========================================================================

    /**
     * Especializacao: Paciente e uma Pessoa que recebe assistencia clinica.
     * Mantem vinculo de COMPOSICAO estrita 1..1 com seu Prontuario.
     */
    public static class Paciente extends Pessoa {
        private String numeroRegistroGeral;
        private String tipoSanguineo;
        private String contatoEmergencia;
        private CarteiraConvenio carteiraConvenio; // Associacao 0..1
        private final Prontuario prontuario;        // Composicao estrita 1..1

        public Paciente(String nomeCompleto, String cpf, LocalDate dataNascimento,
                        String telefoneContato, String email, String enderecoResidencial,
                        String numeroRegistroGeral, String tipoSanguineo, String contatoEmergencia,
                        String identificadorProntuario) {
            super(nomeCompleto, cpf, dataNascimento, telefoneContato, email, enderecoResidencial);
            this.numeroRegistroGeral = numeroRegistroGeral;
            this.tipoSanguineo = tipoSanguineo;
            this.contatoEmergencia = contatoEmergencia;

            // REGRA ESTRUTURAL DE COMPOSICAO: o Prontuario nasce atrelado ao Paciente.
            this.prontuario = new Prontuario(identificadorProntuario, LocalDate.now(), this.tipoSanguineo);
        }

        public String getNumeroRegistroGeral() { return numeroRegistroGeral; }
        public String getTipoSanguineo() { return tipoSanguineo; }
        public String getContatoEmergencia() { return contatoEmergencia; }
        public CarteiraConvenio getCarteiraConvenio() { return carteiraConvenio; }
        public void setCarteiraConvenio(CarteiraConvenio carteiraConvenio) { this.carteiraConvenio = carteiraConvenio; }
        public Prontuario getProntuario() { return prontuario; }
    }

    /**
     * Especializacao: Medico e uma Pessoa legalmente habilitada para atos clinicos.
     * Possui associacao com uma ou mais Especialidades (1..*).
     */
    public static class Medico extends Pessoa {
        private String registroCRM;
        private String ufCRM;
        private int tempoConsultaPadraoMinutos;
        private final List<Especialidade> especialidades = new ArrayList<>();

        public Medico(String nomeCompleto, String cpf, LocalDate dataNascimento,
                      String telefoneContato, String email, String enderecoResidencial,
                      String registroCRM, String ufCRM, int tempoConsultaPadraoMinutos) {
            super(nomeCompleto, cpf, dataNascimento, telefoneContato, email, enderecoResidencial);
            this.registroCRM = Objects.requireNonNull(registroCRM, "Registro CRM e obrigatorio");
            this.ufCRM = Objects.requireNonNull(ufCRM, "UF do CRM e obrigatoria");
            this.tempoConsultaPadraoMinutos = tempoConsultaPadraoMinutos;
        }

        public void adicionarEspecialidade(Especialidade especialidade) {
            Objects.requireNonNull(especialidade, "Especialidade nao pode ser nula");
            if (!this.especialidades.contains(especialidade)) {
                this.especialidades.add(especialidade);
            }
        }

        public String getRegistroCRM() { return registroCRM; }
        public String getUfCRM() { return ufCRM; }
        public int getTempoConsultaPadraoMinutos() { return tempoConsultaPadraoMinutos; }
        public List<Especialidade> getEspecialidades() { return Collections.unmodifiableList(especialidades); }
    }

    // =========================================================================
    // 3. CATALOGOS E CONVENIOS (ASSOCIACOES SIMPLES)
    // =========================================================================

    public static class Especialidade {
        private String nome;
        private String descricao;
        private String codigoConselho;

        public Especialidade(String nome, String descricao, String codigoConselho) {
            this.nome = nome;
            this.descricao = descricao;
            this.codigoConselho = codigoConselho;
        }

        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public String getCodigoConselho() { return codigoConselho; }
    }

    public static class PlanoDeSaude {
        private String razaoSocial;
        private String registroANS;
        private String telefoneCentral;

        public PlanoDeSaude(String razaoSocial, String registroANS, String telefoneCentral) {
            this.razaoSocial = razaoSocial;
            this.registroANS = registroANS;
            this.telefoneCentral = telefoneCentral;
        }

        public String getRazaoSocial() { return razaoSocial; }
        public String getRegistroANS() { return registroANS; }
        public String getTelefoneCentral() { return telefoneCentral; }
    }

    public static class CarteiraConvenio {
        private String numeroCarteira;
        private LocalDate dataValidade;
        private String tipoAcomodacao;
        private PlanoDeSaude planoDeSaude; // Associacao 1..1 com a operadora

        public CarteiraConvenio(String numeroCarteira, LocalDate dataValidade,
                                String tipoAcomodacao, PlanoDeSaude planoDeSaude) {
            this.numeroCarteira = numeroCarteira;
            this.dataValidade = dataValidade;
            this.tipoAcomodacao = tipoAcomodacao;
            this.planoDeSaude = Objects.requireNonNull(planoDeSaude, "Plano de saude e obrigatorio");
        }

        public String getNumeroCarteira() { return numeroCarteira; }
        public LocalDate getDataValidade() { return dataValidade; }
        public String getTipoAcomodacao() { return tipoAcomodacao; }
        public PlanoDeSaude getPlanoDeSaude() { return planoDeSaude; }
        public boolean estaValida() { return !dataValidade.isBefore(LocalDate.now()); }
    }

    // =========================================================================
    // 4. ENTIDADE TRANSACIONAL: CONSULTA E PRONTUARIO (AGREGACAO)
    // =========================================================================

    public enum StatusConsulta {
        AGENDADA, CONFIRMADA, EM_ATENDIMENTO, CONCLUIDA, CANCELADA
    }

    public static class Consulta {
        private String protocoloAtendimento;
        private LocalDateTime dataHoraAgendada;
        private LocalDateTime dataHoraRealizada;
        private StatusConsulta status;
        private String motivoPrincipal;
        private String observacoesClinicas;
        private final Medico medico;     // Associacao 1..1 (atendido por 1 medico)
        private final Paciente paciente; // Associacao 1..1 (referente a 1 paciente)
        private Prescricao prescricao;   // Composicao 0..1 (gerada durante a consulta)

        public Consulta(String protocoloAtendimento, LocalDateTime dataHoraAgendada,
                        String motivoPrincipal, Medico medico, Paciente paciente) {
            this.protocoloAtendimento = Objects.requireNonNull(protocoloAtendimento, "Protocolo e obrigatorio");
            this.dataHoraAgendada = Objects.requireNonNull(dataHoraAgendada, "Data agendada e obrigatoria");
            this.motivoPrincipal = motivoPrincipal;
            this.medico = Objects.requireNonNull(medico, "Medico e obrigatorio na consulta");
            this.paciente = Objects.requireNonNull(paciente, "Paciente e obrigatorio na consulta");
            this.status = StatusConsulta.AGENDADA;
        }

        public void confirmarPresenca() {
            this.status = StatusConsulta.CONFIRMADA;
        }

        public void iniciarAtendimento() {
            this.status = StatusConsulta.EM_ATENDIMENTO;
            this.dataHoraRealizada = LocalDateTime.now();
        }

        public void concluirAtendimento(String parecerClinico, Prescricao prescricaoEmitida) {
            this.observacoesClinicas = parecerClinico;
            this.prescricao = prescricaoEmitida;
            this.status = StatusConsulta.CONCLUIDA;
            // Registra a consulta realizada no prontuario por agregacao
            this.paciente.getProntuario().adicionarConsultaAoHistorico(this);
        }

        public String getProtocoloAtendimento() { return protocoloAtendimento; }
        public LocalDateTime getDataHoraAgendada() { return dataHoraAgendada; }
        public LocalDateTime getDataHoraRealizada() { return dataHoraRealizada; }
        public StatusConsulta getStatus() { return status; }
        public String getMotivoPrincipal() { return motivoPrincipal; }
        public String getObservacoesClinicas() { return observacoesClinicas; }
        public Medico getMedico() { return medico; }
        public Paciente getPaciente() { return paciente; }
        public Prescricao getPrescricao() { return prescricao; }
    }

    /**
     * Prontuario Clinico:
     * Mantem relacao de AGREGACAO COMPARTILHADA (o--) com Consulta.
     * As consultas continuam existindo historicamente, e o prontuario atua
     * como agregador do historico do paciente.
     */
    public static class Prontuario {
        private String identificadorUnico;
        private LocalDate dataCriacao;
        private String historicoAlergias;
        private String tipoFatorRh;
        private final List<Consulta> historicoConsultas = new ArrayList<>();

        public Prontuario(String identificadorUnico, LocalDate dataCriacao, String tipoFatorRh) {
            this.identificadorUnico = identificadorUnico;
            this.dataCriacao = dataCriacao;
            this.tipoFatorRh = tipoFatorRh;
            this.historicoAlergias = "Nenhuma alergia relatada na admissao.";
        }

        public void atualizarAlergias(String novasAlergias) {
            this.historicoAlergias = novasAlergias;
        }

        public void adicionarConsultaAoHistorico(Consulta consulta) {
            Objects.requireNonNull(consulta, "Consulta historica nao pode ser nula");
            this.historicoConsultas.add(consulta);
        }

        public String getIdentificadorUnico() { return identificadorUnico; }
        public LocalDate getDataCriacao() { return dataCriacao; }
        public String getHistoricoAlergias() { return historicoAlergias; }
        public String getTipoFatorRh() { return tipoFatorRh; }
        public List<Consulta> getHistoricoConsultas() { return Collections.unmodifiableList(historicoConsultas); }
    }

    // =========================================================================
    // 5. COMPOSICAO FORTE: PRESCRICAO E ITENS DE PRESCRICAO
    // =========================================================================

    public static class Medicamento {
        private String nomeGenerico;
        private String nomeComercial;
        private String concentracao;
        private String formaFarmaceutica;

        public Medicamento(String nomeGenerico, String nomeComercial, String concentracao, String formaFarmaceutica) {
            this.nomeGenerico = nomeGenerico;
            this.nomeComercial = nomeComercial;
            this.concentracao = concentracao;
            this.formaFarmaceutica = formaFarmaceutica;
        }

        public String getNomeGenerico() { return nomeGenerico; }
        public String getNomeComercial() { return nomeComercial; }
        public String getConcentracao() { return concentracao; }
        public String getFormaFarmaceutica() { return formaFarmaceutica; }
    }

    /**
     * ItemPrescricao compoe estritamente a Prescricao (1..*).
     * Nao tem razao existencial sem estar vinculado a uma prescricao emitida.
     */
    public static class ItemPrescricao {
        private String posologia;
        private String viaAdministracao;
        private String duracaoTratamento;
        private int quantidadeDispensada;
        private final Medicamento medicamento; // Associacao 1..1 com catalogo

        public ItemPrescricao(Medicamento medicamento, String posologia,
                              String viaAdministracao, String duracaoTratamento, int quantidadeDispensada) {
            this.medicamento = Objects.requireNonNull(medicamento, "Medicamento e obrigatorio no item");
            this.posologia = posologia;
            this.viaAdministracao = viaAdministracao;
            this.duracaoTratamento = duracaoTratamento;
            this.quantidadeDispensada = quantidadeDispensada;
        }

        public Medicamento getMedicamento() { return medicamento; }
        public String getPosologia() { return posologia; }
        public String getViaAdministracao() { return viaAdministracao; }
        public String getDuracaoTratamento() { return duracaoTratamento; }
        public int getQuantidadeDispensada() { return quantidadeDispensada; }
    }

    /**
     * Prescricao gerada na Consulta (*-- 1..* ItemPrescricao).
     * Exige no minimo 1 item para ser valida conceitualmente.
     */
    public static class Prescricao {
        private String codigoAutenticidade;
        private LocalDateTime dataEmissao;
        private String orientacoesGerais;
        private final List<ItemPrescricao> itens = new ArrayList<>();

        public Prescricao(String codigoAutenticidade, String orientacoesGerais) {
            this.codigoAutenticidade = codigoAutenticidade;
            this.dataEmissao = LocalDateTime.now();
            this.orientacoesGerais = orientacoesGerais;
        }

        public void adicionarItem(ItemPrescricao item) {
            Objects.requireNonNull(item, "Item da prescricao nao pode ser nulo");
            this.itens.add(item);
        }

        public String getCodigoAutenticidade() { return codigoAutenticidade;
        }
        public LocalDateTime getDataEmissao() { return dataEmissao; }
        public String getOrientacoesGerais() { return orientacoesGerais; }
        public List<ItemPrescricao> getItens() {
            if (this.itens.isEmpty()) {
                throw new IllegalStateException("Violacao de Multiplicidade UML: Prescricao deve conter ao menos 1 ItemPrescricao (1..*).");
            }
            return Collections.unmodifiableList(itens);
        }
    }

    // =========================================================================
    // 6. METODO MAIN EXECUTAVEL DE DEMONSTRACAO PEDAGOGICA
    // =========================================================================

    public static void main(String[] args) {
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("======================================================================");
        System.out.println("UniFEF - Engenharia de Software I - Prof. Marcelo Boer");
        System.out.println("Demonstracao Executavel: Modelo de Dominio Conceitual (Fase de Analise)");
        System.out.println("======================================================================\n");

        // 1. Instanciacao de Catalogos e Medico
        Especialidade cardiologia = new Especialidade("Cardiologia", "Diagnostico e tratamento de doencas do coracao", "RQE-1042");
        Medico drEduardo = new Medico(
            "Dr. Eduardo Nakashima", "111.222.333-44", LocalDate.of(1980, 5, 12),
            "(17) 99888-1122", "eduardo.med@medisys.com.br", "Av. Brasil, 450 - Centro",
            "CRM/SP 123456", "SP", 30
        );
        drEduardo.adicionarEspecialidade(cardiologia);

        // 2. Instanciacao de Plano de Saude e Carteira
        PlanoDeSaude unimed = new PlanoDeSaude("Unimed Seguros Saude", "ANS-305987", "0800-016-6655");
        CarteiraConvenio carteira = new CarteiraConvenio(
            "9876543210001", LocalDate.of(2027, 12, 31), "Apartamento Individual", unimed
        );

        // 3. Instanciacao de Paciente (O Prontuario e criado automaticamente por composicao estrita)
        Paciente pacienteCarlos = new Paciente(
            "Carlos Alberto Silva", "555.666.777-88", LocalDate.of(1992, 8, 20),
            "(17) 98111-2233", "carlos.silva@email.com", "Rua das Flores, 120 - Jd. Europa",
            "44.555.666-X", "O+", "Esposa (Mariana Silva): (17) 98111-2244",
            "PRONT-2026-0042"
        );
        pacienteCarlos.setCarteiraConvenio(carteira);
        pacienteCarlos.getProntuario().atualizarAlergias("Sensibilidade a Dipirona sodica e derivados.");

        // 4. Instanciacao da Consulta transacional
        LocalDateTime horarioAgendado = LocalDateTime.of(2026, 3, 15, 14, 30);
        Consulta consulta1 = new Consulta(
            "PROT-20260315-01", horarioAgendado,
            "Palpitacoes esporadicas e dor toracica aos esforcos", drEduardo, pacienteCarlos
        );

        // Fluxo de evolucao da consulta
        consulta1.confirmarPresenca();
        consulta1.iniciarAtendimento();

        // 5. Prescricao Medica com Itens (Composicao 1..*)
        Medicamento atenolol = new Medicamento("Atenolol", "Atenol", "50mg", "Comprimido revestido");
        Medicamento acidoAcetil = new Medicamento("Acido Acetilsalicilico", "Aspirina Prevent", "100mg", "Comprimido");

        Prescricao receita = new Prescricao("AUTH-HASH-8849-2026", "Manter repouso relativo e evitar ingestao de cafeina.");
        receita.adicionarItem(new ItemPrescricao(atenolol, "1 comprimido pela manha em jejum", "Oral", "30 dias", 1));
        receita.adicionarItem(new ItemPrescricao(acidoAcetil, "1 comprimido apos o almoco", "Oral", "30 dias", 1));

        // Conclusao do atendimento e vinculo com prontuario
        consulta1.concluirAtendimento(
            "Paciente eupneico, PA 130x85 mmHg. Ritmo cardiaco regular. Hipotese: Arritmia benigna induzida por estresse.",
            receita
        );

        // =========================================================================
        // EXIBICAO DO RELATORIO ESTATICO DE CONFORMIDADE COM O MODELO
        // =========================================================================
        System.out.println("1. DADOS DO PACIENTE (Heranca de Pessoa + Composicao Prontuario):");
        System.out.println("   - Nome: " + pacienteCarlos.getNomeCompleto() + " | Idade (/idade derivada): " + pacienteCarlos.getIdade() + " anos");
        System.out.println("   - CPF: " + pacienteCarlos.getCpf() + " | RG: " + pacienteCarlos.getNumeroRegistroGeral());
        System.out.println("   - Prontuario Unico (1..1 estrito): " + pacienteCarlos.getProntuario().getIdentificadorUnico());
        System.out.println("   - Alergias declaradas: " + pacienteCarlos.getProntuario().getHistoricoAlergias());
        System.out.println("   - Convenio: " + pacienteCarlos.getCarteiraConvenio().getPlanoDeSaude().getRazaoSocial() +
                           " (Carteira: " + pacienteCarlos.getCarteiraConvenio().getNumeroCarteira() + ")\n");

        System.out.println("2. DADOS DO MEDICO ASSISTENTE (Heranca de Pessoa + Associacao Especialidade):");
        System.out.println("   - Profissional: " + drEduardo.getNomeCompleto() + " (" + drEduardo.getRegistroCRM() + "/" + drEduardo.getUfCRM() + ")");
        System.out.println("   - Especialidade Principal: " + drEduardo.getEspecialidades().get(0).getNome() +
                           " - Cod: " + drEduardo.getEspecialidades().get(0).getCodigoConselho() + "\n");

        System.out.println("3. ATO CLINICO DA CONSULTA (Associacao Transacional Medico-Paciente):");
        System.out.println("   - Protocolo: " + consulta1.getProtocoloAtendimento());
        System.out.println("   - Horario Agendado: " + consulta1.getDataHoraAgendada().format(fmtDataHora));
        System.out.println("   - Horario Realizado: " + consulta1.getDataHoraRealizada().format(fmtDataHora));
        System.out.println("   - Status Final: " + consulta1.getStatus());
        System.out.println("   - Motivo: " + consulta1.getMotivoPrincipal());
        System.out.println("   - Parecer Clinico: " + consulta1.getObservacoesClinicas() + "\n");

        System.out.println("4. PRESCRICAO ELETRONICA (Composicao 1..* ItemPrescricao):");
        System.out.println("   - Codigo Autenticidade: " + consulta1.getPrescricao().getCodigoAutenticidade());
        System.out.println("   - Data Emissao: " + consulta1.getPrescricao().getDataEmissao().format(fmtDataHora));
        System.out.println("   - Itens Prescritos (Total: " + consulta1.getPrescricao().getItens().size() + "):");
        for (ItemPrescricao item : consulta1.getPrescricao().getItens()) {
            System.out.println("     * " + item.getMedicamento().getNomeComercial() + " (" + item.getMedicamento().getNomeGenerico() +
                               " " + item.getMedicamento().getConcentracao() + ") - Posologia: " + item.getPosologia() +
                               " [Duracao: " + item.getDuracaoTratamento() + "]");
        }

        System.out.println("\n5. HISTORICO AGREGADO NO PRONTUARIO (Agregacao Compartilhada o--):");
        System.out.println("   - Consultas registradas no Prontuario " + pacienteCarlos.getProntuario().getIdentificadorUnico() + ": " +
                           pacienteCarlos.getProntuario().getHistoricoConsultas().size() + " registro(s) encontrado(s).");
        System.out.println("======================================================================");
        System.out.println("Conclusao: Modelo de Dominio de Analise instanciado e validado com sucesso!");
        System.out.println("======================================================================");
    }
}
