/*
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Manutenção e Cadastro de Livros com Java Web e Servlets
 * 
 * Como executar:
 *   javac Livro.java LivroDAO.java
 *   java LivroDAO
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercício 1: Camada de Acesso a Dados (DAO) com suporte a operações CRUD completas.
 */
public class LivroDAO {

    private static LivroDAO instancia;
    private final List<Livro> tabelaLivros;
    private final AtomicInteger geradorId;

    private LivroDAO() {
        this.tabelaLivros = new CopyOnWriteArrayList<>();
        this.geradorId = new AtomicInteger(0);
        inicializarDadosExemplo();
    }

    public static synchronized LivroDAO getInstancia() {
        if (instancia == null) {
            instancia = new LivroDAO();
        }
        return instancia;
    }

    private void inicializarDadosExemplo() {
        inserir(new Livro(0, "Java: Como Programar", "978-8543004792", "Paul Deitel", LocalDate.of(2016, 6, 24), 289.90));
        inserir(new Livro(0, "Código Limpo", "978-8576082675", "Robert C. Martin", LocalDate.of(2009, 9, 8), 114.50));
        inserir(new Livro(0, "Padrões de Projetos", "978-8573076103", "Erich Gamma et al.", LocalDate.of(2000, 1, 1), 149.00));
        inserir(new Livro(0, "Arquitetura Limpa", "978-8550804606", "Robert C. Martin", LocalDate.of(2019, 5, 23), 98.00));
    }

    public List<Livro> listarTodos() {
        List<Livro> listaOrdenada = new ArrayList<>(tabelaLivros);
        listaOrdenada.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        return Collections.unmodifiableList(listaOrdenada);
    }

    public Livro buscarPorId(int id) {
        for (Livro livro : tabelaLivros) {
            if (livro.getId() == id) {
                return livro;
            }
        }
        return null;
    }

    public synchronized void inserir(Livro livro) {
        if (livro.getId() <= 0) {
            livro.setId(geradorId.incrementAndGet());
        } else if (livro.getId() > geradorId.get()) {
            geradorId.set(livro.getId());
        }
        tabelaLivros.add(livro);
    }

    public synchronized boolean atualizar(Livro livroAtualizado) {
        for (int i = 0; i < tabelaLivros.size(); i++) {
            Livro atual = tabelaLivros.get(i);
            if (atual.getId() == livroAtualizado.getId()) {
                atual.setNomeLivro(livroAtualizado.getNomeLivro());
                atual.setIsbn(livroAtualizado.getIsbn());
                atual.setAutor(livroAtualizado.getAutor());
                atual.setDataPublicacao(livroAtualizado.getDataPublicacao());
                atual.setValorLivro(livroAtualizado.getValorLivro());
                return true;
            }
        }
        return false;
    }

    public synchronized boolean excluir(int id) {
        return tabelaLivros.removeIf(livro -> livro.getId() == id);
    }

    public int contarTotal() {
        return tabelaLivros.size();
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("UniFEF - Laboratório de Programação III - Prof. Jefferson");
        System.out.println("Exercício 1: Testes Unitários das Operações do LivroDAO");
        System.out.println("==========================================================");

        LivroDAO dao = LivroDAO.getInstancia();
        System.out.println("1. Carga Inicial de Livros:");
        for (Livro l : dao.listarTodos()) {
            System.out.println("   -> " + l);
        }

        System.out.println("\n2. Testando Inclusão de Novo Livro:");
        Livro novo = new Livro(0, "Refatoração", "978-8575227244", "Martin Fowler", LocalDate.of(2020, 1, 15), 135.00);
        dao.inserir(novo);
        System.out.println("   Livro inserido com ID atribuído: " + novo.getId());

        System.out.println("\n3. Testando Busca por ID (ID = " + novo.getId() + "):");
        Livro buscado = dao.buscarPorId(novo.getId());
        System.out.println("   Resultado da busca: " + buscado);

        System.out.println("\n4. Testando Alteração do Livro:");
        buscado.setValorLivro(119.90);
        buscado.setNomeLivro("Refatoração - 2ª Edição");
        boolean atualizado = dao.atualizar(buscado);
        System.out.println("   Atualização bem-sucedida? " + atualizado);
        System.out.println("   Livro após atualização: " + dao.buscarPorId(novo.getId()));

        System.out.println("\n5. Testando Exclusão do Livro (ID = " + novo.getId() + "):");
        boolean excluido = dao.excluir(novo.getId());
        System.out.println("   Exclusão realizada? " + excluido);
        System.out.println("   Busca pós-exclusão: " + dao.buscarPorId(novo.getId()));

        System.out.println("\nTotal final de livros no repositório: " + dao.contarTotal());
        System.out.println("==========================================================");
    }
}
