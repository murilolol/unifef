/**
 * Universidade / Faculdade de Sistemas de Informação
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Atividade: 20260625 Avaliação Substitutiva
 * 
 * Descrição: Solução completa implementando estruturas de dados avançadas 
 * e manipulação de coleções em Java, seguindo os padrões de projeto exigidos
 * na disciplina.
 */

import java.util.*;
import java.util.stream.Collectors;

public class SolucaoSubstitutivaLabProgIII {

    // =========================================================================
    // PARTE 1: Implementação de Estrutura de Dados (Árvore Binária de Busca)
    // =========================================================================
    public static class NoArvore {
        int valor;
        NoArvore esquerda, direita;

        public NoArvore(int valor) {
            this.valor = valor;
            this.esquerda = this.direita = null;
        }
    }

    public static class ArvoreBinariaBusca {
        private NoArvore raiz;

        public void inserir(int valor) {
            raiz = inserirRecursivo(raiz, valor);
        }

        private NoArvore inserirRecursivo(NoArvore atual, int valor) {
            if (atual == null) {
                return new NoArvore(valor);
            }
            if (valor < atual.valor) {
                atual.esquerda = inserirRecursivo(atual.esquerda, valor);
            } else if (valor > atual.valor) {
                atual.direita = inserirRecursivo(atual.direita, valor);
            }
            return atual;
        }

        public void emOrdem() {
            System.out.print("Percurso Em Ordem (ABB): ");
            emOrdemRecursivo(raiz);
            System.out.println();
        }

        private void emOrdemRecursivo(NoArvore no) {
            if (no != null) {
                emOrdemRecursivo(no.esquerda);
                System.out.print(no.valor + " ");
                emOrdemRecursivo(no.direita);
            }
        }
    }

    // =========================================================================
    // PARTE 2: Gerenciamento de Alunos com Collections e Streams API
    // =========================================================================
    public static class Aluno {
        private String matricula;
        private String nome;
        private double notaFinal;

        public Aluno(String matricula, String nome, double notaFinal) {
            this.matricula = matricula;
            this.nome = nome;
            this.notaFinal = notaFinal;
        }

        public String getMatricula() { return matricula; }
        public String getNome() { return nome; }
        public double getNotaFinal() { return notaFinal; }

        @Override
        public String toString() {
            return String.format("Aluno{Matrícula='%s', Nome='%s', Nota=%.2f}", matricula, nome, notaFinal);
        }
    }

    // =========================================================================
    // MÉTODO PRINCIPAL (MAIN) - EXECUÇÃO DOS TESTES DA AVALIAÇÃO
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  EXEMPLO DE EXECUÇÃO: AVALIAÇÃO SUBSTITUTIVA     ");
        System.out.println("  PROF. JEFFERSON PASSERINI - LAB. PROGRAMAÇÃO III ");
        System.out.println("==================================================\n");

        // 1. Testando a Estrutura de Dados (Árvore Binária de Busca)
        System.out.println("[Teste 1] Árvore Binária de Busca:");
        ArvoreBinariaBusca abb = new ArvoreBinariaBusca();
        int[] elementos = {50, 30, 70, 20, 40, 60, 80};
        for (int el : elementos) {
            abb.inserir(el);
        }
        abb.emOrdem();
        System.out.println();

        // 2. Testando Manipulação de Coleções e Stream API
        System.out.println("[Teste 2] Processamento de Alunos com Streams:");
        List<Aluno> turma = Arrays.asList(
            new Aluno("202601", "Ana Silva", 8.5),
            new Aluno("202602", "Bruno Souza", 5.0),
            new Aluno("202603", "Carlos Lima", 9.2),
            new Aluno("202604", "Diana Costa", 4.8),
            new Aluno("202605", "Eduardo Santos", 7.0)
        );

        // Filtrando aprovados (nota >= 6.0)
        List<Aluno> aprovados = turma.stream()
            .filter(a -> a.getNotaFinal() >= 6.0)
            .sorted(Comparator.comparingDouble(Aluno::getNotaFinal).reversed())
            .collect(Collectors.toList());

        System.out.println("Alunos Aprovados (ordenados por nota decrescente):");
        aprovados.forEach(System.out::println);

        // Calculando média da turma
        double mediaTurma = turma.stream()
            .mapToDouble(Aluno::getNotaFinal)
            .average()
            .orElse(0.0);

        System.out.printf("\nMédia geral da turma: %.2f\n", mediaTurma);
        System.out.println("\n==================================================");
        System.out.println("  FIM DA EXECUÇÃO - AVALIAÇÃO CONCLUÍDA COM SUCESSO ");
        System.out.println("==================================================");
    }
}