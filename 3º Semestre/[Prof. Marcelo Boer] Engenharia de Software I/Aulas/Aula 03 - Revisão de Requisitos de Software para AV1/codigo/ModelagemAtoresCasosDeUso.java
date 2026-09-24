/*
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Modelagem de Atores UML (Primarios, Secundarios, Generalizacao) e Casos de Uso (Include, Extend)
 *
 * Como compilar:
 *   javac ModelagemAtoresCasosDeUso.java
 * Como executar:
 *   java ModelagemAtoresCasosDeUso
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Super-ator abstrato conforme especificacao UML 2.5.
 * Representa uma entidade externa com identidade e papel atribuido perante o software.
 */
abstract class Ator {
    private final String id;
    private final String nome;

    public Ator(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public abstract String getPapel();
}

/**
 * Ator Generalizado (Super-ator).
 * Possui capacidades comuns herdadas por todos os atores especializados.
 */
class UsuarioSistema extends Ator {
    private final String email;
    private String senha;

    public UsuarioSistema(String id, String nome, String email, String senha) {
        super(id, nome);
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public boolean autenticar(String senhaInformada) {
        return this.senha.equals(senhaInformada);
    }

    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
        System.out.println("[CASO DE USO BASE: Alterar Senha] Senha do usuario " + getNome() + " alterada com sucesso.");
    }

    @Override
    public String getPapel() {
        return "Usuario Geral";
    }
}

/**
 * Ator Especializado: Cliente (herda de UsuarioSistema).
 * Ator Primario para saques, pagamentos e consultas de extrato.
 */
class AtorCliente extends UsuarioSistema {
    private double saldo;

    public AtorCliente(String id, String nome, String email, String senha, double saldoInicial) {
        super(id, nome, email, senha);
        this.saldo = saldoInicial;
    }

    public double getSaldo() {
        return saldo;
    }

    public void debitar(double valor) {
        this.saldo -= valor;
    }

    @Override
    public String getPapel() {
        return "Cliente Correntista";
    }
}

/**
 * Ator Especializado: Gerente (herda de UsuarioSistema).
 * Ator Primario para aprovacao de limites de credito e auditoria de seguranca.
 */
class AtorGerente extends UsuarioSistema {
    private final String matriculaFuncional;

    public AtorGerente(String id, String nome, String email, String senha, String matriculaFuncional) {
        super(id, nome, email, senha);
        this.matriculaFuncional = matriculaFuncional;
    }

    public String getMatriculaFuncional() {
        return matriculaFuncional;
    }

    @Override
    public String getPapel() {
        return "Gerente Geral";
    }
}

/**
 * Ator Secundario (Sistema Externo de Apoio).
 * Localizado fora da fronteira do software; providencia autorizacao e liquidacao bancaria.
 */
interface SistemaAutorizadorBancarioSecundario {
    boolean autorizarTransacao(String idConta, double valor);
}

/**
 * Ator Secundario (Dispositivo de Hardware).
 * Representa o hardware dispensador de cedulas fisicas e a impressora termica de comprovantes.
 */
interface DispositivoHardwareTerminalSecundario {
    void liberarNotasFisicas(int valor);
    void imprimirComprovantePapel(String conteudo);
}

/**
 * Implementacao simulada dos Atores Secundarios.
 */
class HardwareTerminalMock implements DispositivoHardwareTerminalSecundario {
    @Override
    public void liberarNotasFisicas(int valor) {
        System.out.println("  [ATOR SECUNDARIO: Hardware Dispensador] Contando e liberando R$ " + valor + " em cedulas fisicas.");
    }

    @Override
    public void imprimirComprovantePapel(String conteudo) {
        System.out.println("  [ATOR SECUNDARIO: Hardware Impressora] Emitindo recibo termico:\n    ---" + conteudo.replace("\n", "\n    ---") + "\n    ---");
    }
}

class BancoCentralMock implements SistemaAutorizadorBancarioSecundario {
    @Override
    public boolean autorizarTransacao(String idConta, double valor) {
        System.out.println("  [ATOR SECUNDARIO: Banco Central / SPB] Transacao de R$ " + valor + " homologada no clearing nacional.");
        return true;
    }
}

/**
 * Representa a Fronteira do Sistema (System Boundary).
 * Contem a logica de orquestracao dos Casos de Uso e seus relacionamentos (<<include>> e <<extend>>).
 */
class FronteiraSistemaCaixaEletronico {
    private final SistemaAutorizadorBancarioSecundario autorizadorBancario;
    private final DispositivoHardwareTerminalSecundario hardwareTerminal;

    public FronteiraSistemaCaixaEletronico(SistemaAutorizadorBancarioSecundario autorizador,
                                          DispositivoHardwareTerminalSecundario hardware) {
        this.autorizadorBancario = autorizador;
        this.hardwareTerminal = hardware;
    }

    /**
     * Caso de Uso Compartilhado: Autenticar Sessao.
     * Relacionamento: <<include>> obrigatorio para todos os casos de uso transacionais.
     */
    public boolean casoDeUsoAutenticar(UsuarioSistema usuario, String senhaInformada) {
        System.out.println("[<<include>> UC_Autenticar] Validando credenciais do ator " + usuario.getNome() + "...");
        boolean autenticado = usuario.autenticar(senhaInformada);
        if (autenticado) {
            System.out.println("  -> Autenticacao com sucesso para papel: " + usuario.getPapel());
        } else {
            System.out.println("  -> Falha na autenticacao: Credenciais invalidas.");
        }
        return autenticado;
    }

    /**
     * Caso de Uso Base: Sacar Dinheiro.
     * - Ator Primario: AtorCliente
     * - Atores Secundarios: SistemaAutorizadorBancarioSecundario e DispositivoHardwareTerminalSecundario
     * - Relacionamento <<include>>: UC_Autenticar
     * - Relacionamento <<extend>>: UC_EmitirComprovanteImpresso (condicional)
     */
    public void casoDeUsoSacarDinheiro(AtorCliente cliente, String senha, double valor, boolean solicitarReciboImpresso) {
        System.out.println("\n--- Executando Caso de Uso: [Sacar Dinheiro] ---");
        System.out.println("Ator Primario solicitante: " + cliente.getNome() + " (" + cliente.getPapel() + ")");

        // 1. Invocacao do relacionamento obrigatorio <<include>>
        if (!casoDeUsoAutenticar(cliente, senha)) {
            System.out.println("[FLUXO DE EXCECAO] Saque abortado por falta de autenticacao valida.");
            return;
        }

        // 2. Fluxo Principal: Validacao de regras de negocio internas
        if (valor <= 0 || valor % 10 != 0) {
            System.out.println("[FLUXO DE EXCECAO] Valor invalido. Terminal so aceita multiplos de R$ 10.");
            return;
        }
        if (cliente.getSaldo() < valor) {
            System.out.println("[FLUXO DE EXCECAO] Saldo insuficiente. Saldo disponivel: R$ " + cliente.getSaldo());
            return;
        }

        // 3. Comunicacao com Ator Secundario (Autorizador Externo)
        boolean autorizacaoExterna = autorizadorBancario.autorizarTransacao(cliente.getId(), valor);
        if (!autorizacaoExterna) {
            System.out.println("[FLUXO DE EXCECAO] Transacao rejeitada pelo autorizador bancario externo.");
            return;
        }

        // 4. Efetivacao da operacao e entrega das notas via hardware terminal
        cliente.debitar(valor);
        hardwareTerminal.liberarNotasFisicas((int) valor);
        System.out.println("[SUCESSO] Debito de R$ " + valor + " efetuado. Novo saldo: R$ " + cliente.getSaldo());

        // 5. Ponto de Extensao: Relacionamento <<extend>> (Condicional)
        if (solicitarReciboImpresso) {
            casoDeUsoEstendidoEmitirComprovante(cliente, valor);
        } else {
            System.out.println("[<<extend>> Omitido] Cliente optou por nao imprimir comprovante.");
        }
    }

    /**
     * Caso de Uso Estendido: Emitir Comprovante Impresso.
     * Relacionamento: <<extend>> condicionado a opcao do cliente.
     */
    private void casoDeUsoEstendidoEmitirComprovante(AtorCliente cliente, double valorSacado) {
        System.out.println("[<<extend>> UC_EmitirComprovante] Ponto de extensao ativado pelo cliente.");
        String comprovante = String.format("RECIBO DE SAQUE\nCliente: %s\nValor: R$ %.2f\nSaldo Restante: R$ %.2f",
                cliente.getNome(), valorSacado, cliente.getSaldo());
        hardwareTerminal.imprimirComprovantePapel(comprovante);
    }

    /**
     * Caso de Uso Exclusivo: Aprovar Limite de Credito.
     * - Ator Primario: AtorGerente (UsuarioSistema comum ou Cliente nao tem acesso a este caso de uso)
     */
    public void casoDeUsoAprovarCredito(UsuarioSistema ator, AtorCliente clienteAlvo, double novoLimite, String senha) {
        System.out.println("\n--- Executando Caso de Uso Exclusivo: [Aprovar Linha de Credito] ---");
        
        // Verificacao de perfil na fronteira do sistema
        if (!(ator instanceof AtorGerente)) {
            System.out.println("[ACESSO NEGADO] O ator " + ator.getNome() + " (" + ator.getPapel() +
                    ") nao possui perfil para executar este caso de uso privativo de Gerente.");
            return;
        }

        AtorGerente gerente = (AtorGerente) ator;
        if (!casoDeUsoAutenticar(gerente, senha)) {
            System.out.println("[FLUXO DE EXCECAO] Falha de autenticacao do gerente.");
            return;
        }

        System.out.println("[SUCESSO] Gerente " + gerente.getNome() + " (Matricula: " + gerente.getMatriculaFuncional() +
                ") aprovou linha de credito de R$ " + novoLimite + " para o cliente " + clienteAlvo.getNome() + ".");
    }
}

/**
 * Classe principal que inicializa o modelo de Atores e executa os Casos de Uso.
 */
public class ModelagemAtoresCasosDeUso {
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  MODELAGEM UML: ATORES PRIMARIOS, SECUNDARIOS, HERANCA E CASOS DE USO");
        System.out.println("========================================================================");

        // Instanciacao dos Atores Secundarios (fora da fronteira do software)
        SistemaAutorizadorBancarioSecundario bancoCentral = new BancoCentralMock();
        DispositivoHardwareTerminalSecundario hardwareATM = new HardwareTerminalMock();

        // Instanciacao da Fronteira do Sistema
        FronteiraSistemaCaixaEletronico sistemaATM = new FronteiraSistemaCaixaEletronico(bancoCentral, hardwareATM);

        // Instanciacao dos Atores Humanos (Generalizacao/Especializacao)
        AtorCliente clienteCarlos = new AtorCliente("C01", "Carlos Eduardo", "carlos@email.com", "senha123", 800.00);
        AtorGerente gerenteMariana = new AtorGerente("G01", "Mariana Costa", "mariana@banco.com", "admin2026", "MTR-9981");

        // ---------------------------------------------------------------------
        // 1. Demonstracao de Heranca de Atores: Ambos usam Caso de Uso Base
        // ---------------------------------------------------------------------
        System.out.println("\n--- Demonstracao de Heranca de Atores: Metodos do Super-ator ---");
        clienteCarlos.alterarSenha("novaSenhaCarlos123");
        gerenteMariana.alterarSenha("novaSenhaGerente2026");

        // ---------------------------------------------------------------------
        // 2. Execucao de Caso de Uso com <<include>> e <<extend>> ativado
        // ---------------------------------------------------------------------
        sistemaATM.casoDeUsoSacarDinheiro(clienteCarlos, "novaSenhaCarlos123", 200.00, true);

        // ---------------------------------------------------------------------
        // 3. Execucao de Caso de Uso com <<extend>> rejeitado pelo ator
        // ---------------------------------------------------------------------
        sistemaATM.casoDeUsoSacarDinheiro(clienteCarlos, "novaSenhaCarlos123", 100.00, false);

        // ---------------------------------------------------------------------
        // 4. Tentativa de Acesso Indevido a Caso de Uso Restrito (Cliente -> Aprovar Credito)
        // ---------------------------------------------------------------------
        sistemaATM.casoDeUsoAprovarCredito(clienteCarlos, clienteCarlos, 5000.00, "novaSenhaCarlos123");

        // ---------------------------------------------------------------------
        // 5. Execucao Legítima do Caso de Uso Restrito pelo Ator Especializado (Gerente)
        // ---------------------------------------------------------------------
        sistemaATM.casoDeUsoAprovarCredito(gerenteMariana, clienteCarlos, 5000.00, "novaSenhaGerente2026");

        System.out.println("\n========================================================================");
        System.out.println("  MODELAGEM EXECUTADA: FRONTEIRAS, ATORES E CASOS DE USO HOMOLOGADOS");
        System.out.println("========================================================================");
    }
}
