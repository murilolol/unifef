/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Evolução do modelo e geração assistida de changelogs
 *
 * Como executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ExemplosAula {

    public enum Status {
        ATIVO,
        INATIVO
    }

    public static class GrupoProduto {
        private Long id;
        private String nome;
        private Status status;
        private final List<Produto> produtos = new ArrayList<>();

        public GrupoProduto(Long id, String nome) {
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Nome do grupo é obrigatório");
            }
            this.id = id;
            this.nome = nome.trim();
            this.status = Status.ATIVO;
        }

        public void adicionarProduto(Produto produto) {
            Objects.requireNonNull(produto, "Produto não pode ser nulo");
            this.produtos.add(produto);
            produto.associarAo(this);
        }

        public Long getId() { return id; }
        public String getNome() { return nome; }
        public Status getStatus() { return status; }
        public List<Produto> getProdutos() { return Collections.unmodifiableList(produtos); }

        @Override
        public String toString() {
            return "GrupoProduto[id=" + id + ", nome='" + nome + "', status=" + status + "]";
        }
    }

    public static class Fornecedor {
        private Long id;
        private String razaoSocial;
        private String cnpj;
        private Status status;

        public Fornecedor(Long id, String razaoSocial, String cnpj) {
            this.id = id;
            this.razaoSocial = validarTextoObrigatorio(razaoSocial, "Razão social é obrigatória");
            this.cnpj = validarCnpj(cnpj);
            this.status = Status.ATIVO;
        }

        public void ativar() { this.status = Status.ATIVO; }
        public void inativar() { this.status = Status.INATIVO; }

        public Long getId() { return id; }
        public String getRazaoSocial() { return razaoSocial; }
        public String getCnpj() { return cnpj; }
        public Status getStatus() { return status; }

        private static String validarTextoObrigatorio(String texto, String mensagem) {
            if (texto == null || texto.isBlank()) {
                throw new IllegalArgumentException(mensagem);
            }
            return texto.trim();
        }

        private static String validarCnpj(String cnpj) {
            String valor = validarTextoObrigatorio(cnpj, "CNPJ é obrigatório");
            if (!valor.matches("\\d{14}")) {
                throw new IllegalArgumentException("CNPJ deve possuir 14 dígitos numéricos");
            }
            return valor;
        }

        @Override
        public String toString() {
            return "Fornecedor[id=" + id + ", razaoSocial='" + razaoSocial + "', cnpj='" + cnpj + "', status=" + status + "]";
        }
    }

    public static class Produto {
        private Long id;
        private String codigoBarras;
        private String descricao;
        private BigDecimal saldoEstoque;
        private BigDecimal valorUnitario;
        private BigDecimal estoqueMinimo;
        private LocalDate dataCadastro;
        private Status status;
        private GrupoProduto grupo;
        private Fornecedor fornecedor;

        // Construtor legado mantido na Aula 06 para preservar compatibilidade de código cliente
        public Produto(Long id, String codigoBarras, String descricao, BigDecimal saldoEstoque,
                       BigDecimal valorUnitario, LocalDate dataCadastro) {
            this(id, codigoBarras, descricao, saldoEstoque, valorUnitario, BigDecimal.ZERO, dataCadastro);
        }

        // Construtor completo expandido com estoqueMinimo
        public Produto(Long id, String codigoBarras, String descricao, BigDecimal saldoEstoque,
                       BigDecimal valorUnitario, BigDecimal estoqueMinimo, LocalDate dataCadastro) {
            this.id = id;
            this.codigoBarras = validarTextoObrigatorio(codigoBarras, "Código de barras é obrigatório");
            this.descricao = validarTextoObrigatorio(descricao, "Descrição é obrigatória");
            this.saldoEstoque = validarNaoNegativo(saldoEstoque, "Saldo de estoque não pode ser negativo");
            this.valorUnitario = validarNaoNegativo(valorUnitario, "Valor unitário não pode ser negativo");
            this.estoqueMinimo = validarNaoNegativo(estoqueMinimo, "Estoque mínimo não pode ser negativo");
            this.dataCadastro = Objects.requireNonNull(dataCadastro, "Data de cadastro é obrigatória");
            this.status = Status.ATIVO;
        }

        public BigDecimal calcularValorEstoque() {
            return saldoEstoque.multiply(valorUnitario).setScale(2, RoundingMode.HALF_UP);
        }

        public void receberEstoque(BigDecimal quantidade) {
            validarPositivo(quantidade, "Quantidade recebida deve ser maior que zero");
            this.saldoEstoque = this.saldoEstoque.add(quantidade);
        }

        public void retirarEstoque(BigDecimal quantidade) {
            validarPositivo(quantidade, "Quantidade retirada deve ser maior que zero");
            if (saldoEstoque.compareTo(quantidade) < 0) {
                throw new IllegalArgumentException("Saldo de estoque insuficiente para retirada");
            }
            this.saldoEstoque = this.saldoEstoque.subtract(quantidade);
        }

        void associarAo(GrupoProduto grupo) {
            Objects.requireNonNull(grupo, "Grupo de produto é obrigatório");
            if (this.grupo != null && !this.grupo.equals(grupo)) {
                throw new IllegalStateException("Produto já pertence a outro grupo");
            }
            this.grupo = grupo;
        }

        public void associarFornecedor(Fornecedor fornecedor) {
            this.fornecedor = Objects.requireNonNull(fornecedor, "Fornecedor é obrigatório");
        }

        public Long getId() { return id; }
        public String getCodigoBarras() { return codigoBarras; }
        public String getDescricao() { return descricao; }
        public BigDecimal getSaldoEstoque() { return saldoEstoque; }
        public BigDecimal getValorUnitario() { return valorUnitario; }
        public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
        public LocalDate getDataCadastro() { return dataCadastro; }
        public Status getStatus() { return status; }
        public GrupoProduto getGrupo() { return grupo; }
        public Fornecedor getFornecedor() { return fornecedor; }

        private static String validarTextoObrigatorio(String texto, String mensagem) {
            if (texto == null || texto.isBlank()) {
                throw new IllegalArgumentException(mensagem);
            }
            return texto.trim();
        }

        private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
            Objects.requireNonNull(valor, mensagem);
            if (valor.signum() < 0) {
                throw new IllegalArgumentException(mensagem);
            }
            return valor;
        }

        private static void validarPositivo(BigDecimal valor, String mensagem) {
            Objects.requireNonNull(valor, mensagem);
            if (valor.signum() <= 0) {
                throw new IllegalArgumentException(mensagem);
            }
        }

        @Override
        public String toString() {
            return "Produto[id=" + id + ", codigo='" + codigoBarras + "', desc='" + descricao + "'" +
                    ", saldo=" + saldoEstoque + ", preco=" + valorUnitario + ", estMin=" + estoqueMinimo +
                    ", grupo=" + (grupo != null ? grupo.getNome() : "null") +
                    ", fornecedor=" + (fornecedor != null ? fornecedor.getRazaoSocial() : "sem fornecedor") + "]";
        }
    }

    // Simulação em memória do ciclo de banco de dados e migração Liquibase
    public static class SimuladorMigracaoBanco {
        public static class RegistroTabelaProduto {
            public Long id;
            public String codigoBarras;
            public BigDecimal estoqueMinimo; // Pode ser nulo durante a fase de transição (expand)

            public RegistroTabelaProduto(Long id, String codigoBarras, BigDecimal estoqueMinimo) {
                this.id = id;
                this.codigoBarras = codigoBarras;
                this.estoqueMinimo = estoqueMinimo;
            }
        }

        public static void demonstrarExpandMigrateContract() {
            System.out.println("--- SIMULAÇÃO DE BANCO: PADRÃO EXPAND-MIGRATE-CONTRACT ---");
            List<RegistroTabelaProduto> tabelaBanco = new ArrayList<>();

            // Linhas existentes no banco (legadas da Aula 04/05)
            tabelaBanco.add(new RegistroTabelaProduto(1L, "78910001", null));
            tabelaBanco.add(new RegistroTabelaProduto(2L, "78910002", null));
            System.out.println("1. Estado inicial do banco: linhas legadas sem a nova coluna.");
            tabelaBanco.forEach(r -> System.out.println("   ID " + r.id + " -> estoque_minimo: " + r.estoqueMinimo));

            // Passo 1: EXPAND (003-04-add-estoque-minimo-produto)
            // Adiciona a coluna permitindo nulo para não quebrar tabelas existentes
            System.out.println("\n2. ChangeSet 003-04 (EXPAND): addColumn 'estoque_minimo' NUMERIC(18,3) NULL");
            System.out.println("   Sucesso! Linhas existentes preservadas sem erro de violação de integridade.");

            // Passo 2: MIGRATE / BACKFILL (003-05-fill-estoque-minimo-produto)
            // Atualiza registros legados com valor default seguro
            System.out.println("\n3. ChangeSet 003-05 (MIGRATE): UPDATE produto SET estoque_minimo = 0 WHERE estoque_minimo IS NULL");
            for (RegistroTabelaProduto reg : tabelaBanco) {
                if (reg.estoqueMinimo == null) {
                    reg.estoqueMinimo = BigDecimal.ZERO;
                }
            }
            tabelaBanco.forEach(r -> System.out.println("   ID " + r.id + " -> estoque_minimo pós-update: " + r.estoqueMinimo));

            // Passo 3: CONTRACT (003-06-not-null-estoque-minimo-produto e 003-07)
            // Aplica constraint NOT NULL e CHECK após garantir que não há registros violadores
            System.out.println("\n4. ChangeSet 003-06/07 (CONTRACT): addNotNullConstraint e CHECK (estoque_minimo >= 0)");
            boolean existeNulo = tabelaBanco.stream().anyMatch(r -> r.estoqueMinimo == null);
            if (!existeNulo) {
                System.out.println("   Sucesso! Constraint NOT NULL aplicada com 100% de convergência e dados intactos.");
            } else {
                System.err.println("   FALHA: Existem valores nulos, a constraint iria quebrar a migração!");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("  LABORATÓRIO DE PROGRAMAÇÃO IV - AULA 06 (EXEMPLOS)");
        System.out.println("  Prof. Jefferson Passerini");
        System.out.println("==============================================================\n");

        // 1. Instanciando Fornecedor com validação de CNPJ
        System.out.println("1. Criando Fornecedor com validação:");
        Fornecedor fornecedor = new Fornecedor(10L, "Distribuidora Nacional de Peças LTDA", "12345678000199");
        System.out.println("   " + fornecedor);

        // 2. Instanciando Grupo de Produtos
        GrupoProduto grupoHardware = new GrupoProduto(1L, "Hardware e Periféricos");

        // 3. Criando produto usando construtor legado (retrocompatibilidade da entidade)
        System.out.println("\n2. Criando Produto via construtor legado (estoqueMinimo delegado para ZERO):");
        Produto produtoLegado = new Produto(
                101L,
                "789000111222",
                "Teclado Mecânico ABNT2",
                new BigDecimal("15.000"),
                new BigDecimal("189.90"),
                LocalDate.now()
        );
        grupoHardware.adicionarProduto(produtoLegado);
        System.out.println("   " + produtoLegado);

        // 4. Criando produto completo com novo campo e associando fornecedor opcional
        System.out.println("\n3. Criando Produto com estoqueMinimo explícito e Fornecedor associado:");
        Produto produtoNovo = new Produto(
                102L,
                "789000111333",
                "Mouse Óptico 16000 DPI",
                new BigDecimal("50.000"),
                new BigDecimal("120.00"),
                new BigDecimal("10.000"),
                LocalDate.now()
        );
        grupoHardware.adicionarProduto(produtoNovo);
        produtoNovo.associarFornecedor(fornecedor);
        System.out.println("   " + produtoNovo);
        System.out.println("   Valor total em estoque: R$ " + produtoNovo.calcularValorEstoque());

        // 5. Movimentação de estoque
        System.out.println("\n4. Realizando operações de entrada e saída de estoque:");
        produtoNovo.receberEstoque(new BigDecimal("25.000"));
        System.out.println("   Após entrada (+25): saldo = " + produtoNovo.getSaldoEstoque());
        produtoNovo.retirarEstoque(new BigDecimal("5.000"));
        System.out.println("   Após saída (-5): saldo = " + produtoNovo.getSaldoEstoque());
        System.out.println("   Novo valor total: R$ " + produtoNovo.calcularValorEstoque());

        // 6. Teste de exceção: CNPJ inválido
        System.out.println("\n5. Testando proteção de domínio contra CNPJ incorreto:");
        try {
            new Fornecedor(11L, "Fornecedor Inválido", "12345");
        } catch (IllegalArgumentException e) {
            System.out.println("   Exceção capturada com sucesso: " + e.getMessage());
        }

        // 7. Simulação da migração segura
        System.out.println();
        SimuladorMigracaoBanco.demonstrarExpandMigrateContract();
        System.out.println("\nExecução dos exemplos da Aula 06 concluída com sucesso.");
    }
}
