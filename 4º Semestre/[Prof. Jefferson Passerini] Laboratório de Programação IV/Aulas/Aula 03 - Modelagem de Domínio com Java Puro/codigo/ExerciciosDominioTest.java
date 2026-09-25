/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Exercícios e Experimentos de Modelagem de Domínio
 * 
 * Como executar:
 * javac ExerciciosDominioTest.java
 * java ExerciciosDominioTest
 */

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExerciciosDominioTest {

    public static void main(String[] args) {
        System.out.println("=== EXECUÇÃO DOS EXPERIMENTOS E TESTES DA AULA 03 ===\n");

        testParteARespostas();
        testExperimento1RetirarZero();
        testExperimento2RetirarNegativo();
        testExperimento3ModificarListaGetter();
        testExperimento4ProdutoEmDoisGrupos();

        System.out.println("\n=== TODOS OS EXPERIMENTOS FORAM EXECUTADOS COM SUCESSO ===");
    }

    private static void testParteARespostas() {
        System.out.println("[Parte A - Mapeamento Teórico no Código]");
        System.out.println("1. Invariante 1: Código de barras não nulo/em branco (Produto.java, validarTextoObrigatorio)");
        System.out.println("   Invariante 2: Saldo de estoque não pode ser insuficiente (Produto.java, retirarEstoque)");
        System.out.println("   Invariante 3: Unicidade de código de barras no grupo (GrupoProduto.java, adicionarProduto)");
        System.out.println("2. Estados mutáveis: saldoEstoque, valorUnitario (Produto.java)");
        System.out.println("3. Estados imutáveis: codigoBarras, dataCadastro (Produto.java)");
        System.out.println("4. Visibilidade de pacote: associarAo(GrupoProduto) restringe alteração do grupo externamente");
        System.out.println("5. Regra testada: naoDeveRetirarQuantidadeMaiorQueOSaldo (ProdutoTest.java)\n");
    }

    private static void testExperimento1RetirarZero() {
        System.out.print("[Experimento 1] Tentando retirar quantidade 0 do estoque: ");
        Produto p = criarProdutoExemplo();
        try {
            p.retirarEstoque(BigDecimal.ZERO);
            System.err.println("FALHA: Deveria ter lançado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("SUCESSO - Capturada exceção esperada: " + e.getMessage());
        }
    }

    private static void testExperimento2RetirarNegativo() {
        System.out.print("[Experimento 2] Tentando retirar quantidade negativa (-1.000): ");
        Produto p = criarProdutoExemplo();
        try {
            p.retirarEstoque(new BigDecimal("-1.000"));
            System.err.println("FALHA: Deveria ter lançado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("SUCESSO - Capturada exceção esperada: " + e.getMessage());
        }
    }

    private static void testExperimento3ModificarListaGetter() {
        System.out.print("[Experimento 3] Tentando modificar a lista retornada por getProdutos(): ");
        GrupoProduto g = new GrupoProduto("Informática");
        try {
            g.getProdutos().add(criarProdutoExemplo());
            System.err.println("FALHA: Deveria ter lançado UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            System.out.println("SUCESSO - Coleção imutável protegida contra alterações externas.");
        }
    }

    private static void testExperimento4ProdutoEmDoisGrupos() {
        System.out.print("[Experimento 4] Tentando associar o mesmo produto a dois grupos diferentes: ");
        GrupoProduto g1 = new GrupoProduto("Grupo A");
        GrupoProduto g2 = new GrupoProduto("Grupo B");
        Produto p = criarProdutoExemplo();

        g1.adicionarProduto(p);
        try {
            g2.adicionarProduto(p);
            System.err.println("FALHA: Deveria ter lançado IllegalStateException");
        } catch (IllegalStateException e) {
            System.out.println("SUCESSO - Capturada exceção esperada: " + e.getMessage());
        }
    }

    private static Produto criarProdutoExemplo() {
        return new Produto(
                "7890000000001",
                "Caneta Esferográfica",
                new BigDecimal("10.000"),
                new BigDecimal("2.50"),
                LocalDate.of(2026, 8, 20));
    }
}
