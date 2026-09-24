/**
 * Disciplina: Estrutura de Dados I (4o Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Exercicios de Fixacao sobre Pilhas
 *
 * Como executar:
 * 1. Compile o arquivo: javac ExerciciosResolvidos.java
 * 2. Execute: java ExerciciosResolvidos
 */
import java.util.EmptyStackException;

// Pilha de Caracteres auxiliar para os exercicios 1 e 2
class PilhaChar {
    private char[] elementos;
    private int topo;

    public PilhaChar(int capacidade) {
        elementos = new char[capacidade];
        topo = -1;
    }

    public boolean isEmpty() {
        return topo == -1;
    }

    public void push(char valor) {
        if (topo == elementos.length - 1) {
            throw new IllegalStateException("Pilha cheia");
        }
        topo++;
        elementos[topo] = valor;
    }

    public char pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        char valor = elementos[topo];
        topo--;
        return valor;
    }

    public char peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elementos[topo];
    }
}

// Pilha Redimensionavel para o exercicio 3
class PilhaDinamica {
    private int[] elementos;
    private int topo;

    public PilhaDinamica(int capacidadeInicial) {
        elementos = new int[capacidadeInicial];
        topo = -1;
    }

    public boolean isEmpty() {
        return topo == -1;
    }

    public void push(int valor) {
        if (topo == elementos.length - 1) {
            redimensionar();
        }
        topo++;
        elementos[topo] = valor;
    }

    private void redimensionar() {
        int novaCapacidade = elementos.length * 2;
        int[] novoArray = new int[novaCapacidade];
        System.arraycopy(elementos, 0, novoArray, 0, elementos.length);
        elementos = novoArray;
        System.out.println("-> Pilha redimensionada para capacidade: " + novaCapacidade);
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

    public int getCapacidade() {
        return elementos.length;
    }
}

public class ExerciciosResolvidos {

    // Exercício 1: Inversao de Palavras com Pilha
    public static String inverterPalavra(String palavra) {
        PilhaChar pilha = new PilhaChar(palavra.length());
        for (int i = 0; i < palavra.length(); i++) {
            pilha.push(palavra.charAt(i));
        }
        
        StringBuilder invertida = new StringBuilder();
        while (!pilha.isEmpty()) {
            invertida.append(pilha.pop());
        }
        return invertida.toString();
    }

    // Exercício 2: Verificador de Parenteses e Colchetes
    public static boolean verificarBalanceamento(String expressao) {
        PilhaChar pilha = new PilhaChar(expressao.length());
        for (int i = 0; i < expressao.length(); i++) {
            char caractere = expressao.charAt(i);
            if (caractere == '(' || caractere == '[') {
                pilha.push(caractere);
            } else if (caractere == ')' || caractere == ']') {
                if (pilha.isEmpty()) {
                    return false;
                }
                char topo = pilha.pop();
                if ((caractere == ')' && topo != '(') || (caractere == ']' && topo != '[')) {
                    return false;
                }
            }
        }
        return pilha.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println("=== EXERCICIO 1: Inversao de Palavras ===");
        String original = "ESTRUTURA";
        String invertida = inverterPalavra(original);
        System.out.println("Original: " + original);
        System.out.println("Invertida: " + invertida);
        System.out.println();

        System.out.println("=== EXERCICIO 2: Verificador de Delimitadores ===");
        String expValida = "[(a+b)*c]";
        String expInvalida = "[(a+b]";
        System.out.println("Expressao '" + expValida + "' e valida? " + verificarBalanceamento(expValida));
        System.out.println("Expressao '" + expInvalida + "' e valida? " + verificarBalanceamento(expInvalida));
        System.out.println();

        System.out.println("=== EXERCICIO 3: Pilha Dinamica ===");
        PilhaDinamica pilhaD = new PilhaDinamica(2);
        System.out.println("Capacidade inicial: " + pilhaD.getCapacidade());
        pilhaD.push(100);
        pilhaD.push(200);
        System.out.println("Empilhando terceiro elemento (deve forcar redimensionamento)...");
        pilhaD.push(300);
        System.out.println("Capacidade atual: " + pilhaD.getCapacidade());
        System.out.println("Topo desempilhado: " + pilhaD.pop());
    }
}
