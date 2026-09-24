/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Trabalho AV1 - Sistema de Atendimento de Clínica
 * Classe: Paciente
 */
public class Paciente {
    private String nome;
    private int idade;
    private int numeroConsulta;

    public Paciente(String nome, int idade, int numeroConsulta) {
        this.nome = nome;
        this.idade = idade;
        this.numeroConsulta = numeroConsulta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public int getNumeroConsulta() {
        return numeroConsulta;
    }

    public void setNumeroConsulta(int numeroConsulta) {
        this.numeroConsulta = numeroConsulta;
    }

    @Override
    public String toString() {
        return "[Consulta #" + numeroConsulta + "] " + nome + " (" + idade + " anos)";
    }
}
