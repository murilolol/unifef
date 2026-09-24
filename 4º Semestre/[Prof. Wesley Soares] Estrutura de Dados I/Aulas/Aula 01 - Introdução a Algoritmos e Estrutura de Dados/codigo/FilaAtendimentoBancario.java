import java.util.ArrayDeque;
import java.util.Queue;

/*
 * Estrutura de Dados I - Aula 01: do problema à solução
 * Estudo de caso guiado: fila de atendimento bancário.
 *
 * Pipeline visto em aula: compreensão -> modelagem -> algoritmo -> estrutura de dados -> implementação.
 * - Compreensão: clientes chegam e são atendidos na ordem de chegada; preferenciais têm fila própria.
 * - Modelagem: Cliente (senha, nome, preferencial) e duas filas FIFO.
 * - Algoritmo: a cada chamada, atende preferencial quando houver, alternando para não travar a fila comum.
 * - Estrutura de dados: Queue (FIFO) - inserção no fim e remoção no início em O(1).
 *
 * Compilar/executar: javac FilaAtendimentoBancario.java && java FilaAtendimentoBancario
 */
public class FilaAtendimentoBancario {

    /** Modelagem do dado: um cliente com senha sequencial. */
    record Cliente(int senha, String nome, boolean preferencial) {
        @Override
        public String toString() {
            return String.format("%s%03d %s", preferencial ? "P" : "N", senha, nome);
        }
    }

    private final Queue<Cliente> filaNormal = new ArrayDeque<>();
    private final Queue<Cliente> filaPreferencial = new ArrayDeque<>();
    private int proximaSenha = 1;
    private int preferenciaisSeguidos = 0;

    /** Regra de negócio: no máximo 2 preferenciais seguidos antes de chamar a fila comum. */
    private static final int LIMITE_PREFERENCIAIS_SEGUIDOS = 2;

    public Cliente chegar(String nome, boolean preferencial) {
        Cliente c = new Cliente(proximaSenha++, nome, preferencial);
        (preferencial ? filaPreferencial : filaNormal).add(c);   // enfileirar: O(1)
        return c;
    }

    /** Algoritmo de chamada: decide qual fila atender (desenfileirar: O(1)). */
    public Cliente chamar() {
        boolean podePreferencial = !filaPreferencial.isEmpty()
                && (preferenciaisSeguidos < LIMITE_PREFERENCIAIS_SEGUIDOS || filaNormal.isEmpty());
        if (podePreferencial) {
            preferenciaisSeguidos++;
            return filaPreferencial.poll();
        }
        preferenciaisSeguidos = 0;
        return filaNormal.poll();   // null quando não há ninguém
    }

    public int aguardando() {
        return filaNormal.size() + filaPreferencial.size();
    }

    public static void main(String[] args) {
        FilaAtendimentoBancario banco = new FilaAtendimentoBancario();
        String[][] chegadas = {
            {"Ana", "N"}, {"Bruno", "P"}, {"Carla", "N"}, {"Diego", "P"},
            {"Elisa", "P"}, {"Fábio", "N"}, {"Gustavo", "N"}, {"Helena", "P"},
        };
        System.out.println("=== Chegada dos clientes ===");
        for (String[] c : chegadas) System.out.println("Senha emitida: " + banco.chegar(c[0], c[1].equals("P")));

        System.out.println("\n=== Ordem de atendimento ===");
        int guiche = 1;
        Cliente atendido;
        while ((atendido = banco.chamar()) != null) {
            System.out.printf("Guichê %d chama: %-12s (restam %d)%n", guiche, atendido, banco.aguardando());
            guiche = guiche % 3 + 1;
        }
        System.out.println("Fila vazia: expediente encerrado.");
    }
}
