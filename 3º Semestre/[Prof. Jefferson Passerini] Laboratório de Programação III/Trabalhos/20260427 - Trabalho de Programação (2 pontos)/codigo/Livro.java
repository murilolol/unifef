/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Manutenção e Cadastro de Livros com Java Web e Servlets
 * 
 * Como executar:
 *   javac Livro.java
 *   java Livro
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Exercício 1: Modelo de Domínio representando a entidade Livro.
 */
public class Livro {

    private int id;
    private String nomeLivro;
    private String isbn;
    private String autor;
    private LocalDate dataPublicacao;
    private double valorLivro;

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FORMATADOR_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public Livro() {
    }

    public Livro(int id, String nomeLivro, String isbn, String autor, LocalDate dataPublicacao, double valorLivro) {
        this.id = id;
        this.nomeLivro = nomeLivro;
        this.isbn = isbn;
        this.autor = autor;
        this.dataPublicacao = dataPublicacao;
        this.valorLivro = valorLivro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public double getValorLivro() {
        return valorLivro;
    }

    public void setValorLivro(double valorLivro) {
        this.valorLivro = valorLivro;
    }

    public String getDataPublicacaoFormatada() {
        if (this.dataPublicacao == null) {
            return "N/D";
        }
        return this.dataPublicacao.format(FORMATADOR_DATA);
    }

    public String getValorLivroFormatado() {
        return FORMATADOR_MOEDA.format(this.valorLivro);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return id == livro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Livro [" +
                "id=" + id +
                ", nomeLivro='" + nomeLivro + '\'' +
                ", isbn='" + isbn + '\'' +
                ", autor='" + autor + '\'' +
                ", dataPublicacao=" + getDataPublicacaoFormatada() +
                ", valorLivro=" + getValorLivroFormatado() +
                ']';
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("UniFEF - Laboratório de Programação III - Prof. Jefferson");
        System.out.println("Exercício 1: Teste Unitário da Entidade Livro");
        System.out.println("==========================================================");

        Livro l1 = new Livro(1, "Java: Como Programar", "978-8543004792", "Paul Deitel", LocalDate.of(2016, 6, 24), 289.90);
        Livro l2 = new Livro(2, "Código Limpo", "978-8576082675", "Robert C. Martin", LocalDate.of(2009, 9, 8), 114.50);

        System.out.println("Livro 1 cadastrado: " + l1);
        System.out.println("Data formatada: " + l1.getDataPublicacaoFormatada());
        System.out.println("Preço formatado: " + l1.getValorLivroFormatado());
        System.out.println("----------------------------------------------------------");
        System.out.println("Livro 2 cadastrado: " + l2);
        System.out.println("Data formatada: " + l2.getDataPublicacaoFormatada());
        System.out.println("Preço formatado: " + l2.getValorLivroFormatado());
        System.out.println("==========================================================");
        System.out.println("Entidade validada com sucesso!");
    }
}
