/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Trabalho AV1 - Sistema de Atendimento de Clínica
 * Classe: No
 */
public class No<T> {
    private T informacao;
    private No<T> proximo;

    public No(T informacao) {
        this.informacao = informacao;
        this.proximo = null;
    }

    public T getInformacao() {
        return informacao;
    }

    public void setInformacao(T informacao) {
        this.informacao = informacao;
    }

    public No<T> getProximo() {
        return proximo;
    }

    public void setProximo(No<T> proximo) {
        this.proximo = proximo;
    }
}
