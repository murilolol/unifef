package unifef.ed1.aula05;

/**
 * Disciplina: Estrutura de Dados I
 * Tema: Listas ligadas dinâmicas
 * Professor: Prof. Wesley Soares
 *
 * Como executar:
 * 1. Compile todos os arquivos: javac -d bin No.java ListaLigada.java Main.java
 * 2. Execute a classe principal: java -cp bin unifef.ed1.aula05.Main
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("--- EXERCÍCIO 1 & 2: Criação Manual e Análise de Referências ---");
        No n1 = new No(10);
        No n2 = new No(20);
        No n3 = new No(30);
        n1.proximo = n2;
        n2.proximo = n3;

        System.out.println("1. Quantos objetos foram criados? Resposta: 3 objetos do tipo No.");
        System.out.println("2. Quantas referências No existem? Resposta: 5 referências (n1, n2, n3, n1.proximo, n2.proximo).");
        System.out.println("3. n1.proximo.valor = " + n1.proximo.valor);
        System.out.println("4. n1.proximo.proximo.valor = " + n1.proximo.proximo.valor);
        
        n2 = null;
        System.out.println("5. Após n2 = null, o nó com valor 20 ainda é acessível por n1.proximo? " + (n1.proximo != null));

        System.out.println("\n--- DESAFIO FINAL: Análise de Acessibilidade ---");
        System.out.println("Mesmo com n2 = null, o nó com valor 20 NÃO é coletado pelo Garbage Collector");
        System.out.println("porque n1.proximo ainda mantém uma referência ativa para ele no Heap.");

        System.out.println("\n--- EXERCÍCIO 4: Teste da Classe ListaLigada ---");
        ListaLigada lista = new ListaLigada();
        lista.adicionar(10);
        lista.adicionar(20);
        lista.adicionar(30);
        lista.adicionar(40);
        lista.adicionar(50);
        lista.adicionar(60);
        System.out.print("Lista inicial: ");
        lista.imprimir();

        System.out.println("Adicionando 5 no início:");
        lista.adicionarInicio(5);
        lista.imprimir();

        System.out.println("Elemento no índice 3: " + lista.obter(3));

        System.out.println("Removendo do início:");
        lista.removerInicio();
        lista.imprimir();

        System.out.println("Removendo elemento do índice 2 (valor 30):");
        lista.removerMeio(2);
        lista.imprimir();
    }
}
