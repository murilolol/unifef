/**
 * Disciplina: Estrutura de Dados I (4o Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Pilhas: Conceito e Implementacao
 *
 * Como executar:
 * 1. Compile o arquivo: javac Pilha.java
 * 2. Execute a classe de teste: java Pilha
 */
public class Pilha {

    private int[] elementos;
    private int topo;

    public Pilha(int capacidade) {
        elementos = new int[capacidade];
        topo = -1;
    }

    public boolean isEmpty() {
        return topo == -1;
    }

    public void push(int valor) {
        if (topo == elementos.length - 1) {
            throw new IllegalStateException("Pilha cheia");
        }
        topo++;
        elementos[topo] = valor;
    }

    public int pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Pilha vazia");
        }
        int valor = elementos[topo];
        topo--;
        return valor;
    }

    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Pilha vazia");
        }
        return elementos[topo];
    }

    // Metodo principal para demonstracao do codigo do professor
    public static void main(String[] args) {
        System.out.println("=== Teste da Pilha do Professor ===");
        Pilha pilha = new Pilha(5);
        
        System.out.println("Pilha vazia? " + pilha.isEmpty());
        
        System.out.println("Empilhando 10, 20 e 30...");
        pilha.push(10);
        pilha.push(20);
        pilha.push(30);
        
        System.out.println("Elemento no topo (peek): " + pilha.peek());
        System.out.println("Desempilhando (pop): " + pilha.pop());
        System.out.println("Novo elemento no topo (peek): " + pilha.peek());
        System.out.println("Pilha vazia? " + pilha.isEmpty());
    }
}
