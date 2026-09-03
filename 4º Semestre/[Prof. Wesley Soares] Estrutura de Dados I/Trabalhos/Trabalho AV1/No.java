/**
 * Classe No (Nó) para estruturar a Lista Ligada.
 * Disciplina: Estrutura de Dados I - Prof. Wesley Soares
 * 
 * @param <T> Tipo genérico de dado armazenado no nó.
 */
public class No<T> {
    private T dado;
    private No<T> proximo;

    /**
     * Construtor que inicializa o nó com um dado.
     * @habilita o encadeamento definindo o próximo como null.
     */
    public No(T dado) {
        this.dado = dado;
        this.proximo = null;
    }

    public T getDado() {
        return dado;
    }

    public void setDado(T dado) {
        this.dado = dado;
    }

    public No<T> getProximo() {
        return proximo;
    }

    public void setProximo(No<T> proximo) {
        this.proximo = proximo;
    }

    @Override
    public String toString() {
        return dado.toString();
    }
}