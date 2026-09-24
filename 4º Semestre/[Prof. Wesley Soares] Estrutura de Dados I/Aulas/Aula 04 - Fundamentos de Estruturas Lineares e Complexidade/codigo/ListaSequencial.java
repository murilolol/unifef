package unifef.ed1;

/**
 * Disciplina: Estrutura de Dados I
 * Tema: Lista Linear Sequencial
 * Como executar: Compile e execute esta classe para testar as operacoes da lista sequencial.
 */
public class ListaSequencial {
    private int[] elementos;
    private int tamanho;

    public ListaSequencial(int capacidade) {
        this.elementos = new int[capacidade];
        this.tamanho = 0;
    }

    public void adicionar(int valor) {
        if (tamanho == elementos.length) {
            throw new IllegalStateException("Lista cheia");
        }
        elementos[tamanho] = valor;
        tamanho++;
    }

    public void adicionar(int indice, int valor) {
        if (indice < 0 || indice > tamanho) {
            throw new IndexOutOfBoundsException("Indice invalido");
        }
        if (tamanho == elementos.length) {
            throw new IllegalStateException("Lista cheia");
        }
        for (int i = tamanho; i > indice; i--) {
            elementos[i] = elementos[i - 1];
        }
        elementos[indice] = valor;
        tamanho++;
    }

    public int obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Indice fora dos limites");
        }
        return elementos[indice];
    }

    public int buscar(int valor) {
        for (int i = 0; i < tamanho; i++) {
            if (elementos[i] == valor) {
                return i;
            }
        }
        return -1;
    }

    public int remover(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Indice fora dos limites");
        }
        int removido = elementos[indice];
        for (int i = indice; i < tamanho - 1; i++) {
            elementos[i] = elementos[i + 1];
        }
        tamanho--;
        return removido;
    }

    public void imprimir() {
        System.out.print("Lista: [");
        for (int i = 0; i < tamanho; i++) {
            System.out.print(elementos[i] + (i < tamanho - 1 ? ", " : ""));
        }
        System.out.println("] (Tamanho: " + tamanho + ")");
    }

    public static void main(String[] args) {
        ListaSequencial lista = new ListaSequencial(10);
        lista.adicionar(10);
        lista.adicionar(20);
        lista.adicionar(30);
        lista.imprimir();
        
        lista.adicionar(2, 25);
        lista.imprimir();
        
        System.out.println("Elemento no indice 2: " + lista.obter(2));
        System.out.println("Posicao do valor 30: " + lista.buscar(30));
        
        lista.remover(2);
        lista.imprimir();
    }
}
