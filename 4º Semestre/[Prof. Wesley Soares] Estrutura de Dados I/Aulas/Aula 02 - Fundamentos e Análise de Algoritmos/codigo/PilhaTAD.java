import java.util.NoSuchElementException;

/*
 * Estrutura de Dados I - Aula 02: Tipos Abstratos de Dados, abstração e encapsulamento
 * O caso clássico do TAD Pilha (LIFO): UMA interface, DUAS representações.
 *
 * - Interface (o "o quê"): empilhar, desempilhar, topo, vazia, tamanho.
 * - Implementação (o "como"): vetor redimensionável OU encadeamento de nós.
 * O código cliente usa só a interface; trocar a representação não muda o cliente.
 *
 * Compilar/executar: javac PilhaTAD.java && java PilhaTAD
 */
public class PilhaTAD {

    /** Contrato do TAD: operações e comportamento, sem expor a representação. */
    interface Pilha<T> {
        void empilhar(T valor);
        T desempilhar();
        T topo();
        boolean vazia();
        int tamanho();
    }

    /** Representação 1: vetor. Empilhar O(1) amortizado; acesso por índice. */
    static final class PilhaVetor<T> implements Pilha<T> {
        private Object[] dados = new Object[4];
        private int n;                                   // encapsulado: ninguém altera por fora

        public void empilhar(T valor) {
            if (n == dados.length) {
                Object[] maior = new Object[dados.length * 2];   // dobra a capacidade
                System.arraycopy(dados, 0, maior, 0, n);
                dados = maior;
            }
            dados[n++] = valor;
        }

        @SuppressWarnings("unchecked")
        public T desempilhar() {
            if (vazia()) throw new NoSuchElementException("Pilha vazia (underflow)");
            T v = (T) dados[--n];
            dados[n] = null;                             // evita reter referência (vazamento)
            return v;
        }

        @SuppressWarnings("unchecked")
        public T topo() {
            if (vazia()) throw new NoSuchElementException("Pilha vazia");
            return (T) dados[n - 1];
        }

        public boolean vazia() { return n == 0; }
        public int tamanho() { return n; }
    }

    /** Representação 2: nós encadeados. Empilhar/desempilhar O(1) sem realocação. */
    static final class PilhaEncadeada<T> implements Pilha<T> {
        private static final class No<T> {
            final T valor;
            final No<T> abaixo;
            No(T valor, No<T> abaixo) { this.valor = valor; this.abaixo = abaixo; }
        }

        private No<T> topo;
        private int n;

        public void empilhar(T valor) { topo = new No<>(valor, topo); n++; }

        public T desempilhar() {
            if (vazia()) throw new NoSuchElementException("Pilha vazia (underflow)");
            T v = topo.valor;
            topo = topo.abaixo;
            n--;
            return v;
        }

        public T topo() {
            if (vazia()) throw new NoSuchElementException("Pilha vazia");
            return topo.valor;
        }

        public boolean vazia() { return topo == null; }
        public int tamanho() { return n; }
    }

    /** Cliente do TAD: verifica parênteses balanceados usando SÓ a interface. */
    static boolean balanceado(String expressao, Pilha<Character> pilha) {
        for (char c : expressao.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') pilha.empilhar(c);
            else if (c == ')' || c == ']' || c == '}') {
                if (pilha.vazia()) return false;
                char aberto = pilha.desempilhar();
                if ((c == ')' && aberto != '(') || (c == ']' && aberto != '[') || (c == '}' && aberto != '{')) return false;
            }
        }
        return pilha.vazia();
    }

    public static void main(String[] args) {
        String[] expressoes = {"(a + b) * [c - d]", "{[()()]}", "(a + b]", "((x)", "}{"};
        System.out.println("=== Mesmo cliente, duas representações do TAD Pilha ===");
        for (String e : expressoes) {
            boolean vetor = balanceado(e, new PilhaVetor<>());
            boolean encadeada = balanceado(e, new PilhaEncadeada<>());
            System.out.printf("%-20s vetor=%-5s encadeada=%-5s %s%n", e, vetor, encadeada, vetor == encadeada ? "(mesmo resultado)" : "DIVERGÊNCIA");
        }

        Pilha<Integer> p = new PilhaEncadeada<>();
        for (int i = 1; i <= 3; i++) p.empilhar(i * 10);
        System.out.printf("%nLIFO: topo=%d, desempilha %d, %d, %d%n", p.topo(), p.desempilhar(), p.desempilhar(), p.desempilhar());
        try {
            p.desempilhar();
        } catch (NoSuchElementException ex) {
            System.out.println("Underflow tratado: " + ex.getMessage());
        }
    }
}
