/**
 * Disciplina : Engenharia de Software I (3º Semestre) - UniFEF
 * Professor  : Marcelo Boer
 * Tema       : Modelagem de Casos de Uso (UML), Relacionamentos <<include>> e <<extend>>, e Regras de Negócio
 *
 * Como compilar e executar:
 *   javac SistemaBibliotecaEmprestimo.java
 *   java SistemaBibliotecaEmprestimo
 *
 * CONCEITOS DEMONSTRADOS:
 * 1. Caso de Uso Base: UC-01 Realizar Empréstimo de Livro.
 * 2. Relacionamento <<include>>: UC-02 Validar Débitos e Pendências (execução mandatória e pré-requisito).
 * 3. Relacionamento <<extend>>: UC-03 Notificar Empréstimo por SMS (disparo condicional via Ponto de Extensão).
 * 4. Regras de Negócio: RN-01 (bloqueio por pendência financeira) e RN-02 (limite de 3 livros simultâneos).
 * 5. Fluxos: Fluxo Principal, Exceção 4a (débito), Exceção 5a (limite) e Alternativo 8a (SMS).
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// --- ATOR PRINCIPAL ---
class Aluno {
    private final String ra;
    private final String nome;
    private double saldoDevedorMultas;
    private final String telefone;
    private final List<Livro> livrosEmPosse;

    public Aluno(String ra, String nome, double saldoDevedorMultas, String telefone) {
        this.ra = ra;
        this.nome = nome;
        this.saldoDevedorMultas = saldoDevedorMultas;
        this.telefone = telefone;
        this.livrosEmPosse = new ArrayList<>();
    }

    public String getRa() { return ra; }
    public String getNome() { return nome; }
    public double getSaldoDevedorMultas() { return saldoDevedorMultas; }
    public void liquidarMultas() { this.saldoDevedorMultas = 0.0; }
    public String getTelefone() { return telefone; }
    public List<Livro> getLivrosEmPosse() { return Collections.unmodifiableList(livrosEmPosse); }

    public void adicionarLivroEmprestado(Livro livro) {
        this.livrosEmPosse.add(livro);
    }
}

// --- ENTIDADE DO DOMÍNIO ---
class Livro {
    private final String codigo;
    private final String titulo;
    private boolean disponivel;

    public Livro(String codigo, String titulo, boolean disponivel) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.disponivel = disponivel;
    }

    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}

// --- ARTEFATO DE SAÍDA (Pós-condição do Caso de Uso) ---
class ComprovanteEmprestimo {
    private final String codigoAutenticacao;
    private final Aluno aluno;
    private final List<Livro> livrosEmprestados;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private final boolean notificadoPorSms;

    public ComprovanteEmprestimo(Aluno aluno, List<Livro> livrosEmprestados, LocalDate dataEmprestimo, LocalDate dataDevolucao, boolean notificadoPorSms) {
        this.codigoAutenticacao = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.aluno = aluno;
        this.livrosEmprestados = new ArrayList<>(livrosEmprestados);
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataDevolucao;
        this.notificadoPorSms = notificadoPorSms;
    }

    public void imprimir() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("========================================================");
        System.out.println("        COMPROVANTE DE EMPRÉSTIMO BIBLIOTECÁRIO");
        System.out.println("========================================================");
        System.out.println("Código Autenticação : " + codigoAutenticacao);
        System.out.println("Aluno               : " + aluno.getNome() + " (RA: " + aluno.getRa() + ")");
        System.out.println("Data do Empréstimo  : " + dataEmprestimo.format(fmt));
        System.out.println("Data de Devolução   : " + dataPrevistaDevolucao.format(fmt) + " (Prazo regulamentar de 14 dias)");
        System.out.println("Obras Emprestadas   :");
        for (Livro l : livrosEmprestados) {
            System.out.println("  * [" + l.getCodigo() + "] " + l.getTitulo());
        }
        System.out.println("Notificação SMS     : " + (notificadoPorSms ? "Enviada via Gateway Telefonia" : "Não solicitada / Inativa"));
        System.out.println("========================================================\n");
    }
}

// --- EXCEÇÕES DO CASO DE USO (Fluxos de Exceção 4a e 5a) ---
class DebitoPendenteException extends Exception {
    public DebitoPendenteException(String mensagem) {
        super(mensagem);
    }
}

class LimiteLivrosExcedidoException extends Exception {
    public LimiteLivrosExcedidoException(String mensagem) {
        super(mensagem);
    }
}

class LivroIndisponivelException extends Exception {
    public LivroIndisponivelException(String mensagem) {
        super(mensagem);
    }
}

// --- ATOR SECUNDÁRIO: SERVIÇO EXTERNO DE TELEFONIA ---
class GatewayTelefoniaStub {
    public boolean dispararSms(String numeroTelefone, String texto) {
        System.out.println("  [GATEWAY EXTERNO] Transmitindo SMS para " + numeroTelefone + ": '" + texto + "'");
        return true;
    }
}

/**
 * CASO DE USO INCLUÍDO (<<include>>): UC-02 Validar Débitos e Pendências
 * 
 * Por que é <<include>>?
 * 1. O caso base (Realizar Empréstimo) NÃO pode ser completado sem essa verificação.
 * 2. O caso incluído é reutilizável por outros casos de uso (ex.: Renovar Empréstimo, Emitir Diploma).
 * 3. O sentido da seta UML aponta do Caso Base para o Incluído (Empréstimo -> ValidarDébitos).
 */
class ValidarDebitosService {
    public void validarSituacaoFinanceira(Aluno aluno) throws DebitoPendenteException {
        System.out.println("  [<<include>> UC-02] Verificando regularidade financeira do aluno " + aluno.getRa() + "...");
        if (aluno.getSaldoDevedorMultas() > 0.0) {
            throw new DebitoPendenteException("Operação Bloqueada: O aluno possui débito financeiro ativo de R$ " 
                + String.format("%.2f", aluno.getSaldoDevedorMultas()) 
                + " referente a atrasos anteriores. Regularize a situação na tesouraria.");
        }
        System.out.println("  [<<include>> UC-02] Aluno sem débitos pendentes. Validação aprovada com sucesso.");
    }
}

/**
 * CASO DE USO EXTENSOR (<<extend>>): UC-03 Notificar Empréstimo por SMS
 * 
 * Por que é <<extend>>?
 * 1. O caso base (Realizar Empréstimo) é completo e autônomo sem o SMS.
 * 2. Só é executado se uma condição for satisfeita no PONTO DE EXTENSÃO (opt-in do usuário + telefone válido).
 * 3. O sentido da seta UML aponta do Extensor para o Caso Base (NotificarSMS -> Empréstimo).
 */
class NotificarSmsService {
    private final GatewayTelefoniaStub gatewayTelefonia;

    public NotificarSmsService(GatewayTelefoniaStub gatewayTelefonia) {
        this.gatewayTelefonia = gatewayTelefonia;
    }

    public boolean executarNotificacao(Aluno aluno, List<Livro> livros, LocalDate dataDevolucao) {
        System.out.println("  [<<extend>> UC-03] Ponto de Extensão acionado: enviando notificação de empréstimo...");
        if (aluno.getTelefone() == null || aluno.getTelefone().trim().isEmpty()) {
            System.out.println("  [<<extend>> UC-03] Telefone não cadastrado. Notificação abortada.");
            return false;
        }
        String mensagem = "UniFEF Biblioteca: Emprestimo de " + livros.size() + " livro(s) realizado. Devolucao ate " 
            + dataDevolucao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".";
        return gatewayTelefonia.dispararSms(aluno.getTelefone(), mensagem);
    }
}

/**
 * CASO DE USO BASE: UC-01 Realizar Empréstimo de Livro
 * Implementa o Fluxo Principal e coordena as chamadas <<include>> e <<extend>>.
 */
class EmprestimoController {
    public static final int LIMITE_MAXIMO_LIVROS = 3; // Regra de Negócio RN-02
    public static final int PRAZO_EMPRESTIMO_DIAS = 14;

    private final ValidarDebitosService validarDebitosService;
    private final NotificarSmsService notificarSmsService;

    public EmprestimoController(ValidarDebitosService validarDebitosService, NotificarSmsService notificarSmsService) {
        this.validarDebitosService = validarDebitosService;
        this.notificarSmsService = notificarSmsService;
    }

    public ComprovanteEmprestimo processarEmprestimo(Aluno aluno, List<Livro> livrosDesejados, boolean desejaNotificacaoSms)
            throws DebitoPendenteException, LimiteLivrosExcedidoException, LivroIndisponivelException {
        
        System.out.println("--- [UC-01: INÍCIO] Processando solicitação de empréstimo para: " + aluno.getNome() + " ---");

        // Passo 2 e 3 do Fluxo Principal: Validar disponibilidade física dos exemplares
        for (Livro livro : livrosDesejados) {
            if (!livro.isDisponivel()) {
                throw new LivroIndisponivelException("Exemplar '" + livro.getTitulo() + "' (Cód: " + livro.getCodigo() + ") não está disponível para empréstimo.");
            }
        }

        // Passo 4 do Fluxo Principal: Invocação OBRIGATÓRIA do Caso de Uso <<include>> UC-02
        // Se houver pendência, o fluxo de exceção 4a é disparado interrompendo o caso base.
        validarDebitosService.validarSituacaoFinanceira(aluno);

        // Passo 5 do Fluxo Principal: Validação da Regra de Negócio de limite de exemplares (Exceção 5a)
        int totalAposEmprestimo = aluno.getLivrosEmPosse().size() + livrosDesejados.size();
        if (totalAposEmprestimo > LIMITE_MAXIMO_LIVROS) {
            throw new LimiteLivrosExcedidoException("Não foi possível concluir o empréstimo: Você já possui " 
                + aluno.getLivrosEmPosse().size() + " exemplar(es) em posse e solicitou mais " + livrosDesejados.size() 
                + ". O limite institucional máximo permitido é de " + LIMITE_MAXIMO_LIVROS + " obras simultâneas.");
        }

        // Passo 6 e 7 do Fluxo Principal: Atualizar status e vincular exemplares
        LocalDate dataHoje = LocalDate.now();
        LocalDate dataDevolucao = dataHoje.plusDays(PRAZO_EMPRESTIMO_DIAS);
        for (Livro livro : livrosDesejados) {
            livro.setDisponivel(false);
            aluno.adicionarLivroEmprestado(livro);
        }
        System.out.println("  [UC-01] Livros vinculados ao aluno. Situação no catálogo alterada para 'Emprestado'.");

        // Passo 8: PONTO DE EXTENSÃO: Notificação Adicional (<<extend>> UC-03)
        // Só é acionado se a condição de extensão (desejaNotificacaoSms) for verdadeira
        boolean smsEnviado = false;
        if (desejaNotificacaoSms) {
            smsEnviado = notificarSmsService.executarNotificacao(aluno, livrosDesejados, dataDevolucao);
        } else {
            System.out.println("  [UC-01] Ponto de Extensão ignorado: aluno optou por não receber SMS.");
        }

        // Passo 9: Finalização com geração do comprovante
        ComprovanteEmprestimo comprovante = new ComprovanteEmprestimo(aluno, livrosDesejados, dataHoje, dataDevolucao, smsEnviado);
        System.out.println("--- [UC-01: FIM] Empréstimo concluído com sucesso ---\n");
        return comprovante;
    }
}

// --- CLASSE PRINCIPAL EXECUTÁVEL ---
public class SistemaBibliotecaEmprestimo {
    public static void main(String[] args) {
        System.out.println("################################################################");
        System.out.println("#  SIMULADOR DE CASOS DE USO UML - BIBLIOTECA UNIVERSITÁRIA     #");
        System.out.println("#  Disciplina: Engenharia de Software I | Prof. Marcelo Boer   #");
        System.out.println("################################################################\n");

        // 1. Instanciação dos serviços e controladores
        GatewayTelefoniaStub gatewaySms = new GatewayTelefoniaStub();
        ValidarDebitosService servicoValidarDebitos = new ValidarDebitosService();
        NotificarSmsService servicoNotificarSms = new NotificarSmsService(gatewaySms);
        EmprestimoController controller = new EmprestimoController(servicoValidarDebitos, servicoNotificarSms);

        // 2. Base de dados de exemplares
        Livro livro1 = new Livro("ES-001", "Engenharia de Software (Ian Sommerville)", true);
        Livro livro2 = new Livro("UML-002", "UML Guia do Usuário (Booch, Rumbaugh, Jacobson)", true);
        Livro livro3 = new Livro("PAD-003", "Padrões de Projeto GoF (Gamma et al.)", true);
        Livro livro4 = new Livro("REQ-004", "Engenharia de Requisitos (Wiegers & Beatty)", true);

        // =========================================================================
        // CENÁRIO 1: Fluxo Principal Completo + Disparo do <<extend>> SMS
        // =========================================================================
        System.out.println(">>> TESTE 1: Fluxo Principal Regular com Acionamento do <<extend>> SMS");
        Aluno alunoRegular = new Aluno("RA-202601", "Carlos Eduardo Silva", 0.0, "(17) 99123-4567");
        List<Livro> pedido1 = new ArrayList<>();
        pedido1.add(livro1);
        pedido1.add(livro2);

        try {
            ComprovanteEmprestimo comprovante = controller.processarEmprestimo(alunoRegular, pedido1, true);
            comprovante.imprimir();
        } catch (Exception e) {
            System.err.println("Erro inesperado no Teste 1: " + e.getMessage());
        }

        // =========================================================================
        // CENÁRIO 2: Fluxo de Exceção 4a (Bloqueio pelo <<include>> Validar Débitos)
        // =========================================================================
        System.out.println(">>> TESTE 2: Fluxo de Exceção 4a - Aluno com Débito Pendente (<<include>>)");
        Aluno alunoInadimplente = new Aluno("RA-202602", "Mariana Costa Souza", 15.00, "(17) 99765-4321");
        List<Livro> pedido2 = new ArrayList<>();
        pedido2.add(livro3);

        try {
            controller.processarEmprestimo(alunoInadimplente, pedido2, true);
        } catch (DebitoPendenteException e) {
            System.out.println("  [EXCEÇÃO TRATADA COM SUCESSO] " + e.getMessage());
            System.out.println("  -> Demonstração: O caso base abortou porque o <<include>> falhou.\n");
        } catch (Exception e) {
            System.err.println("Erro inesperado no Teste 2: " + e.getMessage());
        }

        // =========================================================================
        // CENÁRIO 3: Fluxo de Exceção 5a (Violação da Regra de Negócio RN-02: Limite > 3)
        // =========================================================================
        System.out.println(">>> TESTE 3: Fluxo de Exceção 5a - Limite Máximo de 3 Livros Excedido");
        // O alunoRegular já pegou 2 livros no Teste 1. Se tentar pegar mais 2 livros, violará a regra.
        List<Livro> pedido3 = new ArrayList<>();
        pedido3.add(livro3);
        pedido3.add(livro4);

        try {
            controller.processarEmprestimo(alunoRegular, pedido3, false);
        } catch (LimiteLivrosExcedidoException e) {
            System.out.println("  [EXCEÇÃO TRATADA COM SUCESSO] " + e.getMessage());
            System.out.println("  -> Demonstração: A Regra de Negócio institucional protegeu o acervo.\n");
        } catch (Exception e) {
            System.err.println("Erro inesperado no Teste 3: " + e.getMessage());
        }

        // =========================================================================
        // CENÁRIO 4: Fluxo Principal Sem SMS (Comprova a Autonomia da Base sobre o <<extend>>)
        // =========================================================================
        System.out.println(">>> TESTE 4: Sucesso sem SMS (Comprova que <<extend>> é puramente opcional)");
        Aluno alunoSemSms = new Aluno("RA-202603", "Beatriz Helena Ramos", 0.0, null);
        List<Livro> pedido4 = new ArrayList<>();
        pedido4.add(livro3);

        try {
            ComprovanteEmprestimo comprovante = controller.processarEmprestimo(alunoSemSms, pedido4, false);
            comprovante.imprimir();
        } catch (Exception e) {
            System.err.println("Erro inesperado no Teste 4: " + e.getMessage());
        }

        System.out.println("[SIMULAÇÃO CONCLUÍDA] Todos os fluxos UML e regras foram validados com precisão.");
    }
}
