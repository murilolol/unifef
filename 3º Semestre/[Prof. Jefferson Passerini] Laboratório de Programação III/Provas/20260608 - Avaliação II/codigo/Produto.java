// Disciplina: Laboratório de Programação III (3º Semestre)
// Professor: Prof. Jefferson Passerini
// Tema: Avaliação II - Modelo de Domínio (Model)
// Como executar: javac Produto.java && java Produto

import java.util.Objects;

public class Produto {
    // Exercício 1: Atributos privados da entidade
    private Integer id;
    private String nome;
    private Double preco;
    private Integer quantidadeEstoque;
    private String dataCadastro;

    public Produto() {
    }

    public Produto(Integer id, String nome, Double preco, Integer quantidadeEstoque, String dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.dataCadastro = dataCadastro;
    }

    public Produto(String nome, Double preco, Integer quantidadeEstoque, String dataCadastro) {
        this(null, nome, preco, quantidadeEstoque, dataCadastro);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode ser nulo ou vazio.");
        }
        this.nome = nome.trim();
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        if (preco == null || preco < 0.0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior ou igual a zero.");
        }
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return String.format("Produto [ID: %d | Nome: %-25s | Preço: R$ %8.2f | Estoque: %4d | Cadastro: %s]",
                id, nome, preco, quantidadeEstoque, dataCadastro);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static void main(String[] args) {
        System.out.println("=== Teste Unitário da Classe Produto (Exercício 1) ===");
        Produto p = new Produto(1, "Monitor Gamer 27", 1499.90, 15, "2026-06-08");
        System.out.println("Instância criada com sucesso:");
        System.out.println(p);

        try {
            p.setPreco(-50.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Validação de preço negativo funcionou: " + e.getMessage());
        }
    }
}
