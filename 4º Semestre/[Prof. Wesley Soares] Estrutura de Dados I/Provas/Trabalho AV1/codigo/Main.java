/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Trabalho AV1 - Sistema de Atendimento de Clínica
 * Classe: Main
 * 
 * Como executar:
 * 1. Compile todos os arquivos: javac Paciente.java No.java ListaLigada.java Main.java
 * 2. Execute a classe principal: java Main
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE ATENDIMENTO DA CLÍNICA ===\n");

        ListaLigada<Paciente> filaClinica = new ListaLigada<>();

        // Requisito: Cadastro de pelo menos 5 pacientes
        System.out.println("--- Cadastrando 5 Pacientes ---");
        filaClinica.adicionar(new Paciente("Ana Souza", 28, 101));
        filaClinica.adicionar(new Paciente("Carlos Lima", 45, 102));
        filaClinica.adicionar(new Paciente("Beatriz Santos", 19, 103));
        filaClinica.adicionar(new Paciente("Daniel Oliveira", 67, 104));
        filaClinica.adicionar(new Paciente("Eduarda Rocha", 35, 105));
        System.out.println("Pacientes cadastrados com sucesso.\n");

        // Requisito: Impressão da fila
        System.out.println("--- Fila de Espera Atual ---");
        filaClinica.imprimir();
        System.out.println();

        // Requisito: Busca de um paciente
        System.out.println("--- Buscando Paciente da Consulta 103 ---");
        Paciente pBuscado = filaClinica.buscarPorConsulta(103);
        if (pBuscado != null) {
            System.out.println("Paciente localizado: " + pBuscado);
        } else {
            System.out.println("Paciente não encontrado.");
        }
        System.out.println();

        // Requisito: Cancelamento de uma consulta
        System.out.println("--- Cancelando Consulta 104 (Daniel Oliveira) ---");
        boolean cancelado = filaClinica.cancelarConsulta(104);
        if (cancelado) {
            System.out.println("Consulta 104 cancelada com sucesso!");
        } else {
            System.out.println("Falha ao cancelar: consulta não localizada.");
        }
        System.out.println();

        System.out.println("--- Fila de Espera Atualizada ---");
        filaClinica.imprimir();
        System.out.println();

        // Requisito: Chamada do próximo paciente
        System.out.println("--- Chamando Próximo Paciente ---");
        Paciente proximo = filaClinica.chamarProximo();
        if (proximo != null) {
            System.out.println("Atendendo agora: " + proximo);
        } else {
            System.out.println("Nenhum paciente na fila.");
        }
        System.out.println();

        System.out.println("--- Fila de Espera Final ---");
        filaClinica.imprimir();
    }
}
