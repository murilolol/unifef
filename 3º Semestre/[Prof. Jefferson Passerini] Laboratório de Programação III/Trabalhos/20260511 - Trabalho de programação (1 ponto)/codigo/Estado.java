/*
 * Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
 * Professor:  Prof. Jefferson Passerini
 * Tema:       Java JSP Cap 5.4 - Desafio 02: Cadastro de Estado
 * Exercício:  Exercício 1
 * Como executar: javac Estado.java && java Estado
 */

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe de modelo representando a entidade Estado no padrão JavaBean.
 */
public class Estado implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idEstado;
    private String nomeEstado;
    private String siglaEstado;

    public Estado() {
    }

    public Estado(String nomeEstado, String siglaEstado) {
        this.nomeEstado = nomeEstado;
        this.siglaEstado = siglaEstado != null ? siglaEstado.toUpperCase() : null;
    }

    public Estado(Integer idEstado, String nomeEstado, String siglaEstado) {
        this.idEstado = idEstado;
        this.nomeEstado = nomeEstado;
        this.siglaEstado = siglaEstado != null ? siglaEstado.toUpperCase() : null;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNomeEstado() {
        return nomeEstado;
    }

    public void setNomeEstado(String nomeEstado) {
        this.nomeEstado = nomeEstado;
    }

    public String getSiglaEstado() {
        return siglaEstado;
    }

    public void setSiglaEstado(String siglaEstado) {
        this.siglaEstado = siglaEstado != null ? siglaEstado.toUpperCase() : null;
    }

    @Override
    public String toString() {
        return "Estado [idEstado=" + idEstado + ", nomeEstado=" + nomeEstado + ", siglaEstado=" + siglaEstado + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estado estado = (Estado) o;
        return Objects.equals(idEstado, estado.idEstado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEstado);
    }

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("UniFEF - LP3 - Teste da Classe de Modelo Estado");
        System.out.println("====================================================");

        Estado estado1 = new Estado(1, "São Paulo", "sp");
        Estado estado2 = new Estado("Minas Gerais", "mg");

        System.out.println("Instância 1: " + estado1);
        System.out.println("Instância 2: " + estado2);
        System.out.println("Sigla normalizada em maiúsculo: " + estado1.getSiglaEstado());
        System.out.println("Teste de validação concluído com sucesso.");
    }
}
