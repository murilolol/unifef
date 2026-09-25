/*
 * Disciplina: Laboratório de Programação IV (4º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Simulado A1 - Exercício 1: Modelagem de Domínio Rica com Invariantes e BigDecimal
 * Como executar:
 *   javac SimuladoDominioInvariantes.java
 *   java SimuladoDominioInvariantes
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimuladoDominioInvariantes {

    public enum Status {
        ATIVO, INATIVO
    }

    public static class RegraNegocioException extends RuntimeException {
        public RegraNegocioException(String mensagem) {
            super(mensagem);
        }
    }

    public static class GrupoProduto {
        private final String nome;
        private Status status;
        private final List<Produto> produtos = new ArrayList<>();

        public GrupoProduto(String nome) {
            if (nome == null || nome.trim().isEmpty()) {
                throw new RegraNegocioException("Nome do grupo não pode ser nulo ou vazio");
            }
            this.nome = nome.trim();
            this.status = Status.ATIVO;
        }

        public String getNome() {
            return nome;
        }

        public Status getStatus() {
            return status;
        }

        public List<Produto> getProdutos() {
            return Collections.unmodifiableList(produtos);
        }

        public void adicionarProduto(Produto produto) {
            if (produto == null) {
                throw new RegraNegocioException("Produto não pode ser nulo");
            }
            for (Produto existente : produtos) {
                if (existente.getCodigoBarras().equals(produto.getCodigoBarras())) {
                    throw new RegraNegocioException("Grupo já contém um produto com este código de barras");
                }
            }
            this.produtos.add(produto);
            produto.atribuirGrupoInterno(this);
        }

        public void ativar() {
            this.status = Status.ATIVO;
        }

        public void inativar() {
            this.status = Status.INATIVO;
        }
    }

    public static class Produto {
        private final String codigoBarras;
        private String descricao;
        private BigDecimal saldoEstoque;
        private BigDecimal valorUnitario;
        private BigDecimal estoqueMinimo;
        private final LocalDate dataCadastro;
        private Status status;
        private GrupoProduto grupo;

        public Produto(String codigoBarras, String descricao, BigDecimal saldoEstoque,
                       BigDecimal valorUnitario, BigDecimal estoqueMinimo, LocalDate dataCadastro) {
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                throw new RegraNegocioException("Código de barras é obrigatório");
            }
            if (descricao == null || descricao.trim().isEmpty()) {
                throw new RegraNegocioException("Descrição do produto é obrigatória");
            }
            if (saldoEstoque == null || saldoEstoque.compareTo(BigDecimal.ZERO) < 0) {
                throw new RegraNegocioException("Saldo de estoque não pode ser negativo");
            }
            if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) < 0) {
                throw new RegraNegocioException("Valor unitário não pode ser negativo");
            }
            if (estoqueMinimo == null || estoqueMinimo.compareTo(BigDecimal.ZERO) < 0) {
                throw new RegraNegocioException("Estoque mínimo não pode ser negativo");
            }
            if (dataCadastro == null) {
                throw new RegraNegocioException("Data de cadastro é obrigatória");
            }

            this.codigoBarras = codigoBarras.trim();
            this.descricao = descricao.trim();
            this.saldoEstoque = saldoEstoque.setScale(3, RoundingMode.HALF_UP);
            this.valorUnitario = valorUnitario.setScale(2, RoundingMode.HALF_UP);
            this.estoqueMinimo = estoqueMinimo.setScale(3, RoundingMode.HALF_UP);
            this.dataCadastro = dataCadastro;
            this.status = Status.ATIVO;
        }

        public void receberEstoque(BigDecimal quantidade) {
            if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("Quantidade de entrada deve ser estritamente maior que zero");
            }
            this.saldoEstoque = this.saldoEstoque.add(quantidade.setScale(3, RoundingMode.HALF_UP));
        }

        public void retirarEstoque(BigDecimal quantidade) {
            if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("Quantidade de saída deve ser estritamente maior que zero");
            }
            BigDecimal quantidadeAjustada = quantidade.setScale(3, RoundingMode.HALF_UP);
            if (this.saldoEstoque.compareTo(quantidadeAjustada) < 0) {
                throw new RegraNegocioException("Saldo insuficiente para retirada. Disponível: " + this.saldoEstoque);
            }
            this.saldoEstoque = this.saldoEstoque.subtract(quantidadeAjustada);
        }

        public BigDecimal calcularValorEstoque() {
            return this.saldoEstoque
                    .multiply(this.valorUnitario)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        void atribuirGrupoInterno(GrupoProduto grupo) {
            this.grupo = grupo;
        }

        public String getCodigoBarras() { return codigoBarras; }
        public String getDescricao() { return descricao; }
        public BigDecimal getSaldoEstoque() { return saldoEstoque; }
        public BigDecimal getValorUnitario() { return valorUnitario; }
        public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
        public LocalDate getDataCadastro() { return dataCadastro; }
        public Status getStatus() { return status; }
        public GrupoProduto getGrupo() { return grupo; }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("EXECUÇÃO: SIMULADO A1 - EXERCÍCIO 1: DOMÍNIO E INVARIANTES");
        System.out.println("==========================================================");

        GrupoProduto grupo = new GrupoProduto("Informática");
        Produto produto = new Produto(
                "789123456001",
                "Teclado Mecânico RGB",
                new BigDecimal("10.000"),
                new BigDecimal("150.50"),
                new BigDecimal("2.000"),
                LocalDate.now()
        );

        grupo.adicionarProduto(produto);
        System.out.println("[OK] Produto criado e associado ao grupo: " + produto.getDescricao());
        System.out.println("     Saldo Inicial: " + produto.getSaldoEstoque());
        System.out.println("     Valor Unitário: R$ " + produto.getValorUnitario());

        // Teste de cálculo com precisão monetária HALF_UP
        BigDecimal valorEstoque = produto.calcularValorEstoque();
        System.out.println("     Valor Total em Estoque: R$ " + valorEstoque);
        if (!valorEstoque.equals(new BigDecimal("1505.00"))) {
            throw new AssertionError("Erro no cálculo do valor de estoque");
        }

        // Teste de movimentação válida
        produto.receberEstoque(new BigDecimal("5.500"));
        System.out.println("[OK] Entrada de 5.500 realizada. Novo saldo: " + produto.getSaldoEstoque());
        produto.retirarEstoque(new BigDecimal("3.500"));
        System.out.println("[OK] Saída de 3.500 realizada. Novo saldo: " + produto.getSaldoEstoque());

        // Teste de proteção de invariante: retirada além do saldo
        try {
            produto.retirarEstoque(new BigDecimal("100.000"));
            throw new AssertionError("Falha: Invariante violada! Retirada indevida permitida.");
        } catch (RegraNegocioException ex) {
            System.out.println("[OK] Invariante protegida com sucesso contra retirada excessiva: " + ex.getMessage());
        }

        // Teste de proteção de lista imutável no grupo
        try {
            grupo.getProdutos().add(new Produto("000", "Invasor", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, LocalDate.now()));
            throw new AssertionError("Falha: Coleção externa permitiu modificação direta!");
        } catch (UnsupportedOperationException ex) {
            System.out.println("[OK] Encapsulamento protegido com sucesso: getProdutos() é imutável externamente.");
        }

        System.out.println("\nTODOS OS TESTES DE DOMÍNIO FORAM CONCLUÍDOS COM SUCESSO!");
    }
}
