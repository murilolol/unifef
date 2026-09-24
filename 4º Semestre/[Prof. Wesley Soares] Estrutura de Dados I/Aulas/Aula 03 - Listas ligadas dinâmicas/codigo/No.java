package unifef.ed1.aula05;

/**
 * Disciplina: Estrutura de Dados I
 * Tema: Listas ligadas dinâmicas
 * Professor: Prof. Wesley Soares
 *
 * Classe que representa um nó na lista encadeada.
 */
public class No {
    public int valor;
    public No proximo;

    public No(int valor) {
        this.valor = valor;
        this.proximo = null;
    }
}
