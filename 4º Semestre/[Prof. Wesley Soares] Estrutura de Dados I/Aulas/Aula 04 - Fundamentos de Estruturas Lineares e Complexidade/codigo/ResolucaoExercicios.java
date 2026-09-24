package unifef.ed1;

/**
 * Disciplina: Estrutura de Dados I
 * Tema: Resolucao de Exercicios (Nos e Referencias)
 * Como executar: Execute a classe para ver as respostas impressas no console.
 */
public class ResolucaoExercicios {
    public static void main(String[] args) {
        System.out.println("--- EXERCICIO 1: Criacao da Lista Ligada ---");
        No n1 = new No(10);
        No n2 = new No(20);
        No n3 = new No(30);
        n1.proximo = n2;
        n2.proximo = n3;
        System.out.println("Lista ligada criada com sucesso!");

        System.out.println("\n--- EXERCICIO 2: Analise de Referencias ---");
        System.out.println("1. Quantos objetos foram criados? R: 3 objetos do tipo No.");
        System.out.println("2. Quantas referencias No existem? R: 5 referencias (n1, n2, n3, n1.proximo, n2.proximo).");
        System.out.println("3. n1.proximo.valor = " + n1.proximo.valor);
        System.out.println("4. n1.proximo.proximo.valor = " + n1.proximo.proximo.valor);
        
        System.out.println("\n--- EXERCICIO 3 (DESAFIO FINAL): n2 = null ---");
        n2 = null;
        System.out.println("Apos fazer n2 = null:");
        System.out.println("O objeto que contem o valor 20 ainda pode ser acessado atraves de n1.proximo?");
        System.out.println("R: Sim! n1.proximo ainda aponta para o objeto com valor 20. Valor: " + n1.proximo.valor);
        System.out.println("O objeto com valor 30 ainda pode ser acessado?");
        System.out.println("R: Sim! Atraves de n1.proximo.proximo. Valor: " + n1.proximo.proximo.valor);
        System.out.println("Nenhum objeto foi para o Garbage Collector pois todos ainda sao alcancaveis a partir de n1.");
    }
}
