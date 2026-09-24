/*
 * Disciplina: Engenharia de Software I (3o Semestre) - UniFEF
 * Professor:  Marcelo Boer
 * Tema:       Implementacao das Classes e Casos de Uso Modelados na Aula 02 do Astah UML
 *
 * Como compilar:
 *   javac SistemaAcademicoModel.java
 *
 * Como executar:
 *   java SistemaAcademicoModel
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidade modelada no Diagrama de Classes da Aula 02.
 * Representa o curso de graduacao ofertado pela instituicao.
 */
class Curso {
    private int codigo;
    private String nome;

    public Curso(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "Curso [codigo=" + codigo + ", nome=" + nome + "]";
    }
}

/**
 * Entidade modelada no Diagrama de Classes da Aula 02.
 * Possui associacao com Professor e compoe o curriculo pedagogico.
 */
class Disciplina {
    private String codigo;
    private String nome;
    private int cargaHoraria;
    private List<String> planoDeEnsino;

    public Disciplina(String codigo, String nome, int cargaHoraria) {
        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.planoDeEnsino = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    // Metodo formal modelado no diagrama UML do Astah
    public void adicionarConteudo(String item) {
        if (item != null && !item.trim().isEmpty()) {
            this.planoDeEnsino.add(item);
            System.out.println("   [Disciplina " + codigo + "] Conteudo inserido: '" + item + "'");
        }
    }

    public List<String> getPlanoDeEnsino() {
        return Collections.unmodifiableList(planoDeEnsino);
    }

    @Override
    public String toString() {
        return "Disciplina {" + codigo + " - " + nome + " (" + cargaHoraria + "h)}";
    }
}

/**
 * Entidade modelada no Diagrama de Classes da Aula 02.
 * Representa o docente responsavel por ministrar uma ou mais disciplinas (Multiplicidade 1 -> 1..*).
 */
class Professor {
    private String nome;
    private String matricula;
    private List<Disciplina> disciplinasMinistradas;

    public Professor(String matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
        this.disciplinasMinistradas = new ArrayList<>();
    }

    public void atribuirDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinasMinistradas.contains(disciplina)) {
            disciplinasMinistradas.add(disciplina);
        }
    }

    // Operacao formal modelada no diagrama do Astah
    public void ministrarAula() {
        System.out.println(" -> Professor(a) " + nome + " (Matr: " + matricula + ") iniciando transmissao de aula.");
        for (Disciplina d : disciplinasMinistradas) {
            System.out.println("    * Lecionando topicos da disciplina: " + d.getNome());
        }
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }
}

/**
 * Entidade e Ator modelado nos diagramas de Classes e Casos de Uso.
 * Realiza solicitacao de matricula e pode trancar vinculo estudantil.
 */
class Aluno {
    private String matricula;
    private String nome;
    private Curso curso;
    private boolean matriculaAtiva;
    private List<Disciplina> disciplinasInscritas;

    public Aluno(String matricula, String nome, Curso curso) {
        this.matricula = matricula;
        this.nome = nome;
        this.curso = curso;
        this.matriculaAtiva = true;
        this.disciplinasInscritas = new ArrayList<>();
    }

    // Operacao modelada no exercicio pratico do Astah
    public boolean trancarMatricula() {
        if (this.matriculaAtiva) {
            this.matriculaAtiva = false;
            this.disciplinasInscritas.clear();
            System.out.println(" -> Aluno " + nome + " trancou a matricula no curso " + curso.getNome());
            return true;
        }
        System.out.println(" -> Aluno " + nome + " ja se encontra com vinculo inativo.");
        return false;
    }

    public void adicionarDisciplina(Disciplina d) {
        if (matriculaAtiva && !disciplinasInscritas.contains(d)) {
            disciplinasInscritas.add(d);
        }
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public Curso getCurso() {
        return curso;
    }

    public boolean isMatriculaAtiva() {
        return matriculaAtiva;
    }

    public List<Disciplina> getDisciplinasInscritas() {
        return Collections.unmodifiableList(disciplinasInscritas);
    }
}

/**
 * Ator secundario do Caso de Uso 'UC_02: Homologar Matricula'.
 * Demonstra a separacao entre regras de negocio e interface de atores externos.
 */
class SecretariaAcademica {
    private String setor;

    public SecretariaAcademica(String setor) {
        this.setor = setor;
    }

    /**
     * Caso de Uso UC_02: Homologar Matricula.
     * No diagrama de Casos de Uso, este fluxo e chamado via <<include>> pelo caso de uso 'Solicitar Matricula'.
     */
    public boolean homologarMatricula(Aluno aluno, List<Disciplina> disciplinasDesejadas) {
        System.out.println("   [Setor: " + setor + "] Auditando pre-requisitos de matricula para o aluno: " + aluno.getNome());
        if (!aluno.isMatriculaAtiva()) {
            System.out.println("   [RECUSA] Aluno com matricula trancada nao pode receber homologacao.");
            return false;
        }
        if (disciplinasDesejadas.isEmpty()) {
            System.out.println("   [RECUSA] Nenhuma disciplina informada para inscricao.");
            return false;
        }

        for (Disciplina d : disciplinasDesejadas) {
            aluno.adicionarDisciplina(d);
            System.out.println("   [DEFERIDO] Inscricao confirmada em: " + d.getNome());
        }
        return true;
    }
}

/**
 * Classe principal que instancia e executa os fluxos correspondentes aos modelos UML da aula.
 */
public class SistemaAcademicoModel {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  EXECUCAO DO MODELO CONCEITUAL UML - ENGENHARIA DE SOFTWARE I");
        System.out.println("  Validacao de Classes e Casos de Uso modelados no Astah UML");
        System.out.println("========================================================================\n");

        // 1. Instanciacao dos elementos estaticos (Diagrama de Classes)
        System.out.println("[CENARIO 1] Instanciando entidades do Diagrama de Classes...");
        Curso bsi = new Curso(101, "Bacharelado em Sistemas de Informacao");
        System.out.println(" -> Curso criado: " + bsi);

        Disciplina es1 = new Disciplina("ES-01", "Engenharia de Software I", 80);
        Disciplina poo = new Disciplina("POO-02", "Programacao Orientada a Objetos", 80);
        es1.adicionarConteudo("Aula 02: Configuracao e Licenciamento do Astah UML");
        es1.adicionarConteudo("Aula 03: Diagramas de Casos de Uso e Metamodelo OMG");

        Professor profBoer = new Professor("DOC-5542", "Marcelo Boer");
        profBoer.atribuirDisciplina(es1);
        profBoer.atribuirDisciplina(poo);

        // Disparo do comportamento modelado do Professor
        profBoer.ministrarAula();
        System.out.println();

        // 2. Instanciacao do Aluno (Ator Primario)
        System.out.println("[CENARIO 2] Instanciando Aluno e executando Casos de Uso...");
        Aluno aluno1 = new Aluno("RA-2025001", "Mariana Costa", bsi);
        SecretariaAcademica secretaria = new SecretariaAcademica("Secretaria Central de Registros Academicos");

        // Simulacao do Caso de Uso: 'Solicitar Matricula' que inclui (<<include>>) 'Homologar Matricula'
        System.out.println("\n-> Disparando Caso de Uso: [UC01 - Solicitar Matricula]");
        List<Disciplina> disciplinasSolicitadas = new ArrayList<>();
        disciplinasSolicitadas.add(es1);
        disciplinasSolicitadas.add(poo);

        boolean sucesso = secretaria.homologarMatricula(aluno1, disciplinasSolicitadas);
        System.out.println("-> Status final da solicitacao de matricula: " + (sucesso ? "SUCESSO" : "FALHA"));

        System.out.println("\n-> Grade atual do estudante " + aluno1.getNome() + ":");
        for (Disciplina d : aluno1.getDisciplinasInscritas()) {
            System.out.println("   - " + d);
        }

        // 3. Teste de Operacao Estrutural (Trancar Matricula)
        System.out.println("\n[CENARIO 3] Teste da operacao trancarMatricula()...");
        aluno1.trancarMatricula();
        System.out.println(" -> Situacao atual da matricula: " + (aluno1.isMatriculaAtiva() ? "ATIVA" : "TRANCADA"));

        // Tentativa de nova matricula com vinculo inativo
        System.out.println("\n-> Tentando matricular aluno com vinculo inativo:");
        boolean novaTentativa = secretaria.homologarMatricula(aluno1, disciplinasSolicitadas);
        System.out.println("-> Resultado da tentativa: " + (novaTentativa ? "PERMITIDO" : "BLOQUEADO PELA REGRA DE NEGOCIO"));

        System.out.println("\n========================================================================");
        System.out.println("  Integridade dos modelos UML validada com sucesso no runtime Java.");
        System.out.println("========================================================================");
    }
}
