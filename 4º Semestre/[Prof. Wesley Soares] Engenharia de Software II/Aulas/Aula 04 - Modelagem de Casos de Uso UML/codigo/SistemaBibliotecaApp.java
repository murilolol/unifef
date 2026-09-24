/**
 * Disciplina: Engenharia de Software II
 * Tema: Modelagem de Casos de Uso UML - Sistema de Informatização de Biblioteca
 * 
 * Como compilar e executar:
 *   javac SistemaBibliotecaApp.java
 *   java SistemaBibliotecaApp
 */

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// ============================================================================
// ELEMENTO UML: ATORES DA BIBLIOTECA
// ============================================================================

/**
 * Ator Primário: Usuário da Biblioteca.
 * Interage diretamente com as funções de pesquisa e retirada.
 */
class UsuarioBiblioteca {
    private final String matricula;
    private final String nome;
    private boolean possuiMultaPendente;
    private int totalLivrosEmprestados;

    public UsuarioBiblioteca(String matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
        this.possuiMultaPendente = false;
        this.totalLivrosEmprestados = 0;
    }

    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public boolean isPossuiMultaPendente() { return possuiMultaPendente; }
    public void setPossuiMultaPendente(boolean status) { this.possuiMultaPendente = status; }
    public int getTotalLivrosEmprestados() { return totalLivrosEmprestados; }
    public void incrementarEmprestimos() { this.totalLivrosEmprestados++; }
    public void decrementarEmprestimos() { this.totalLivrosEmprestados = Math.max(0, this.totalLivrosEmprestados - 1); }
}

/**
 * Ator Secundário (Externo): Sistema de Pagamento de Multas.
 * Sistema automatizado responsável pelo cálculo financeiro de mora.
 */
class SistemaExternoPagamento {
    private static final double TAXA_DIARIA_MULTA = 2.50;

    public double calcularMulta(long diasAtraso) {
        System.out.println("   [Ator Secundário: Sistema de Pagamento] Processando mora para " + diasAtraso + " dia(s) de atraso.");
        return diasAtraso * TAXA_DIARIA_MULTA;
    }
}

// ============================================================================
// ENTIDADES DE DOMÍNIO
// ============================================================================

enum StatusExemplar {
    DISPONIVEL, EMPRESTADO, EM_MANUTENCAO
}

class LivroExemplar {
    private final String isbn;
    private final String titulo;
    private final String autor;
    private StatusExemplar status;

    public LivroExemplar(String isbn, String titulo, String autor) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.status = StatusExemplar.DISPONIVEL;
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public StatusExemplar getStatus() { return status; }
    public void setStatus(StatusExemplar status) { this.status = status; }
}

class RegistroEmprestimo {
    private final String idEmprestimo;
    private final LivroExemplar livro;
    private final UsuarioBiblioteca usuario;
    private final LocalDate dataRetirada;
    private final LocalDate dataLimiteDevolucao;
    private LocalDate dataDevolucaoReal;
    private double valorMulta;
    private boolean concluido;

    public RegistroEmprestimo(String idEmprestimo, LivroExemplar livro, UsuarioBiblioteca usuario, LocalDate dataRetirada, int diasPrazo) {
        this.idEmprestimo = idEmprestimo;
        this.livro = livro;
        this.usuario = usuario;
        this.dataRetirada = dataRetirada;
        this.dataLimiteDevolucao = dataRetirada.plusDays(diasPrazo);
        this.concluido = false;
    }

    public String getIdEmprestimo() { return idEmprestimo; }
    public LivroExemplar getLivro() { return livro; }
    public UsuarioBiblioteca getUsuario() { return usuario; }
    public LocalDate getDataLimiteDevolucao() { return dataLimiteDevolucao; }
    public void setDevolucao(LocalDate dataReal, double multa) {
        this.dataDevolucaoReal = dataReal;
        this.valorMulta = multa;
        this.concluido = true;
    }
    public boolean isConcluido() { return concluido; }
    public double getValorMulta() { return valorMulta; }
}

// ============================================================================
// REPOSITÓRIO SIMULADO (BANCO DE DADOS EM MEMÓRIA)
// ============================================================================

class AcervoBibliotecaRepository {
    private final List<LivroExemplar> acervo = new ArrayList<>();
    private final List<RegistroEmprestimo> emprestimos = new ArrayList<>();

    public void salvarLivro(LivroExemplar livro) {
        acervo.add(livro);
    }

    public Optional<LivroExemplar> buscarPorTitulo(String termo) {
        return acervo.stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(termo.toLowerCase()))
                .findFirst();
    }

    public void registrarEmprestimo(RegistroEmprestimo emp) {
        emprestimos.add(emp);
    }

    public Optional<RegistroEmprestimo> buscarEmprestimoAtivo(String id) {
        return emprestimos.stream()
                .filter(e -> e.getIdEmprestimo().equals(id) && !e.isConcluido())
                .findFirst();
    }
}

// ============================================================================
// ESPECIFICAÇÃO DE CASOS DE USO
// ============================================================================

/**
 * Caso de Uso Incluído: Verificar Disponibilidade do Exemplar (<<include>>).
 * Invocado obrigatoriamente por Realizar Empréstimo.
 */
class VerificarDisponibilidadeUseCase {
    public boolean executar(LivroExemplar livro) {
        System.out.println(" -> Executando <<include>>: Verificar Disponibilidade do Exemplar");
        boolean disponivel = livro.getStatus() == StatusExemplar.DISPONIVEL;
        System.out.println("    Status atual de '" + livro.getTitulo() + "': " + livro.getStatus());
        return disponivel;
    }
}

/**
 * Caso de Uso Base: Realizar Empréstimo (UC_BIB_02).
 * Pré-condições:
 *   1. Usuário com cadastro ativo e sem multas.
 *   2. Usuário não pode ultrapassar o teto de 3 livros simultâneos.
 *   3. Livro com status DISPONIVEL (validado via include).
 * Pós-condições:
 *   1. Registro de empréstimo persistido com prazo de devolução.
 *   2. Livro com status alterado para EMPRESTADO.
 */
class RealizarEmprestimoUseCase {
    private final VerificarDisponibilidadeUseCase verificarDispUC;
    private final AcervoBibliotecaRepository repository;

    public RealizarEmprestimoUseCase(VerificarDisponibilidadeUseCase verificarDispUC, AcervoBibliotecaRepository repository) {
        this.verificarDispUC = verificarDispUC;
        this.repository = repository;
    }

    public RegistroEmprestimo executar(UsuarioBiblioteca usuario, LivroExemplar livro, LocalDate dataHoje) {
        System.out.println("\n--- Caso de Uso Base: Realizar Empréstimo [Usuário: " + usuario.getNome() + "] ---");

        // Validação das Pré-condições
        if (usuario.isPossuiMultaPendente()) {
            throw new IllegalStateException("Fluxo de Exceção FE01: Usuário possui multas ativas não pagas.");
        }
        if (usuario.getTotalLivrosEmprestados() >= 3) {
            throw new IllegalStateException("Fluxo de Exceção FE01: Limite máximo de empréstimos (3) atingido.");
        }

        // Invocação compulsória do <<include>>
        boolean disponivel = verificarDispUC.executar(livro);
        if (!disponivel) {
            throw new IllegalStateException("Fluxo de Exceção FE02: O exemplar solicitado não está disponível.");
        }

        // Caminho Feliz (Happy Path)
        String idEmprestimo = "EMP-" + System.currentTimeMillis();
        RegistroEmprestimo novoEmprestimo = new RegistroEmprestimo(idEmprestimo, livro, usuario, dataHoje, 14);
        
        // Pós-condições
        livro.setStatus(StatusExemplar.EMPRESTADO);
        usuario.incrementarEmprestimos();
        repository.registrarEmprestimo(novoEmprestimo);

        System.out.println(" -> Pós-condição Satisfeita: Empréstimo gerado com sucesso [ID: " + idEmprestimo + "]");
        System.out.println("    Data de Retirada: " + dataHoje + " | Prazo Limite: " + novoEmprestimo.getDataLimiteDevolucao());
        return novoEmprestimo;
    }
}

/**
 * Caso de Uso de Extensão: Calcular Multa por Atraso (<<extend>>).
 * Disparado condicionalmente na devolução apenas se dataDevolucao > dataLimite.
 */
class CalcularMultaPorAtrasoUseCase {
    private final SistemaExternoPagamento sistemaPagamento;

    public CalcularMultaPorAtrasoUseCase(SistemaExternoPagamento sistemaPagamento) {
        this.sistemaPagamento = sistemaPagamento;
    }

    public double executar(RegistroEmprestimo emprestimo, LocalDate dataDevolucao) {
        System.out.println(" -> Executando <<extend>>: Calcular Multa por Atraso (Ponto de Extensão Acionado)");
        long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataLimiteDevolucao(), dataDevolucao);
        
        if (diasAtraso <= 0) {
            return 0.0;
        }
        
        double valor = sistemaPagamento.calcularMulta(diasAtraso);
        System.out.println("    Notificação: Multa aplicada de R$ " + String.format("%.2f", valor) + " por " + diasAtraso + " dia(s) de atraso.");
        return valor;
    }
}

/**
 * Caso de Uso Base: Devolver Livro.
 * Contém o Ponto de Extensão para o cálculo condicional de penalidades.
 */
class DevolverLivroUseCase {
    private final CalcularMultaPorAtrasoUseCase calcularMultaUC;
    private final AcervoBibliotecaRepository repository;

    public DevolverLivroUseCase(CalcularMultaPorAtrasoUseCase calcularMultaUC, AcervoBibliotecaRepository repository) {
        this.calcularMultaUC = calcularMultaUC;
        this.repository = repository;
    }

    public void executar(String idEmprestimo, LocalDate dataDevolucao) {
        System.out.println("\n--- Caso de Uso Base: Devolver Livro [Empréstimo: " + idEmprestimo + "] ---");

        // Pré-condição
        RegistroEmprestimo emprestimo = repository.buscarEmprestimoAtivo(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Pré-condição violada: Empréstimo ativo não encontrado."));

        double multaCalculada = 0.0;

        // Ponto de Extensão: Condição de guarda [dataDevolucao > dataLimiteDevolucao]
        if (dataDevolucao.isAfter(emprestimo.getDataLimiteDevolucao())) {
            multaCalculada = calcularMultaUC.executar(emprestimo, dataDevolucao);
            emprestimo.getUsuario().setPossuiMultaPendente(true);
        } else {
            System.out.println(" -> Devolução realizada rigorosamente dentro do prazo regulamentar. Sem multas.");
        }

        // Pós-condição
        emprestimo.getLivro().setStatus(StatusExemplar.DISPONIVEL);
        emprestimo.getUsuario().decrementarEmprestimos();
        emprestimo.setDevolucao(dataDevolucao, multaCalculada);

        System.out.println(" -> Pós-condição Satisfeita: Livro '" + emprestimo.getLivro().getTitulo() 
                + "' retornado ao acervo com status DISPONIVEL.");
    }
}

// ============================================================================
// CLASSE PRINCIPAL EXECUTÁVEL
// ============================================================================
public class SistemaBibliotecaApp {
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   ENGENHARIA DE SOFTWARE II - SISTEMA DE BIBLIOTECA");
        System.out.println("   Modelagem Comportamental: Pré/Pós-Condições, Include e Extend");
        System.out.println("====================================================================");

        // Setup de Repositório e Serviços
        AcervoBibliotecaRepository repo = new AcervoBibliotecaRepository();
        SistemaExternoPagamento sisPagamento = new SistemaExternoPagamento();

        VerificarDisponibilidadeUseCase verificarDispUC = new VerificarDisponibilidadeUseCase();
        RealizarEmprestimoUseCase emprestimoUC = new RealizarEmprestimoUseCase(verificarDispUC, repo);
        CalcularMultaPorAtrasoUseCase multaUC = new CalcularMultaPorAtrasoUseCase(sisPagamento);
        DevolverLivroUseCase devolverUC = new DevolverLivroUseCase(multaUC, repo);

        // Carga Inicial do Acervo
        LivroExemplar livroJava = new LivroExemplar("ISBN-001", "Core Java Volume I", "Cay Horstmann");
        LivroExemplar livroUml = new LivroExemplar("ISBN-002", "Utilizando UML e Padrões", "Craig Larman");
        repo.salvarLivro(livroJava);
        repo.salvarLivro(livroUml);

        UsuarioBiblioteca usuarioAna = new UsuarioBiblioteca("ALUNO-2026-A", "Ana Beatriz");
        UsuarioBiblioteca usuarioCarlos = new UsuarioBiblioteca("ALUNO-2026-C", "Carlos Eduardo");

        LocalDate hoje = LocalDate.of(2026, 4, 1);

        // --------------------------------------------------------------------
        // CENÁRIO 1: Fluxo Principal de Empréstimo (Caminho Feliz)
        // --------------------------------------------------------------------
        RegistroEmprestimo emp1 = emprestimoUC.executar(usuarioAna, livroJava, hoje);

        // --------------------------------------------------------------------
        // CENÁRIO 2: Fluxo de Exceção - Tentativa de empréstimo de exemplar já emprestado
        // --------------------------------------------------------------------
        try {
            System.out.println("\nTentativa concorrente de empréstimo do mesmo livro:");
            emprestimoUC.executar(usuarioCarlos, livroJava, hoje);
        } catch (IllegalStateException e) {
            System.out.println("Interceptação de Exceção: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CENÁRIO 3: Devolução no prazo (Ponto de Extensão NÃO ativado)
        // --------------------------------------------------------------------
        LocalDate devolucaoNoPrazo = hoje.plusDays(10); // Limite é 14 dias
        devolverUC.executar(emp1.getIdEmprestimo(), devolucaoNoPrazo);

        // --------------------------------------------------------------------
        // CENÁRIO 4: Novo empréstimo seguido de devolução com atraso (Disparo de <<extend>>)
        // --------------------------------------------------------------------
        RegistroEmprestimo emp2 = emprestimoUC.executar(usuarioCarlos, livroUml, hoje);
        LocalDate devolucaoComAtraso = hoje.plusDays(20); // 6 dias após o prazo limite de 14
        devolverUC.executar(emp2.getIdEmprestimo(), devolucaoComAtraso);
    }
}
