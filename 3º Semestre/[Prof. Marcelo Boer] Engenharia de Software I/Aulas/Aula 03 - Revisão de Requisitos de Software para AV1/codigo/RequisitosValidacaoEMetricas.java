/*
 * Disciplina: Engenharia de Software I - UniFEF
 * Professor: Marcelo Boer
 * Tema: Verificacao de Requisitos Funcionais (RF) e Metricas Formais de Requisitos Nao Funcionais (RNF)
 *
 * Como compilar:
 *   javac RequisitosValidacaoEMetricas.java
 * Como executar:
 *   java RequisitosValidacaoEMetricas
 */

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Excecao lancada quando um Requisito Funcional (regra de negocio/operacao) eh violado.
 * Conceito: O sistema falhou em executar O QUE deveria fazer.
 */
class RequisitoFuncionalException extends Exception {
    public RequisitoFuncionalException(String mensagem) {
        super("[FALHA DE RF - REGRA DE NEGOCIO] " + mensagem);
    }
}

/**
 * Excecao lancada quando um Requisito Nao Funcional (qualidade/restricao/desempenho) eh violado.
 * Conceito: O sistema executou a operacao, mas violou COMO ou SOB QUAL RESTRICAO ela deveria operar.
 */
class RequisitoNaoFuncionalException extends Exception {
    public RequisitoNaoFuncionalException(String mensagem) {
        super("[VIOLACAO DE RNF - ATRIBUTO DE QUALIDADE] " + mensagem);
    }
}

/**
 * Representa a entidade ContaBancaria no dominio do sistema.
 */
class Conta {
    private final String titular;
    private double saldo;

    public Conta(String titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void creditar(double valor) {
        this.saldo += valor;
    }

    public void debitar(double valor) {
        this.saldo -= valor;
    }
}

/**
 * Servico central demonstrando a diferenca conceitual entre Requisitos Funcionais e Nao Funcionais.
 */
public class RequisitosValidacaoEMetricas {

    // Base de dados simulada em memoria
    private static final Map<String, String> USUARIOS_CADASTRADOS = new HashMap<>();
    private static final Map<String, Conta> CONTAS = new HashMap<>();

    // Parametros metricos de Requisitos Nao Funcionais (RNF)
    // RNF01 (Desempenho): O tempo de resposta para transferencias deve ser estritamente inferior a 50 ms
    private static final long LIMITE_TEMPO_RESPOSTA_MS = 50;

    // RNF02 (Seguranca): A senha deve conter no minimo 8 caracteres, 1 maiuscula, 1 numero e 1 caractere especial
    private static final Pattern PADRAO_SENHA_SEGURA =
            Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    // RNF03 (Robustez - MTTR): Tempo de recuperacao de falha automatica inferior a 100 ms
    private static final long LIMITE_MTTR_MS = 100;

    /**
     * Implementacao do Caso de Uso: Cadastrar Usuario.
     * - RF01: Permitir cadastro com e-mail unico e senha valida.
     * - RNF02: Validar restricoes formais de seguranca de senha e formato de e-mail.
     */
    public static void cadastrarUsuario(String email, String senha) 
            throws RequisitoFuncionalException, RequisitoNaoFuncionalException {
        
        // Validacao de RF: Unicidade do identificador de usuario
        if (USUARIOS_CADASTRADOS.containsKey(email)) {
            throw new RequisitoFuncionalException("O e-mail '" + email + "' ja esta cadastrado no sistema.");
        }

        // Validacao de RNF: Restricao formal de seguranca da informacao (RNF de Seguranca)
        if (!PADRAO_SENHA_SEGURA.matcher(senha).matches()) {
            throw new RequisitoNaoFuncionalException(
                "A senha informada nao cumpre os criterios de complexidade do RNF02: " +
                "minimo 8 caracteres, 1 letra maiuscula, 1 numero e 1 simbolo especial."
            );
        }

        // Execucao da persistencia (sucesso funcional)
        USUARIOS_CADASTRADOS.put(email, senha);
        System.out.println("[RF ATENDIDO] Usuario '" + email + "' cadastrado com sucesso com senha em conformidade com RNF.");
    }

    /**
     * Implementacao do Caso de Uso: Realizar Transferencia Bancaria.
     * - RF02: O sistema deve debitar a conta origem e creditar a conta destino caso haja saldo.
     * - RNF01: Metrica formal de desempenho - tempo de resposta <= 50 ms.
     */
    public static void realizarTransferencia(String origem, String destino, double valor, boolean simularSobrecarga) 
            throws RequisitoFuncionalException, RequisitoNaoFuncionalException {
        
        long tempoInicio = System.currentTimeMillis();

        Conta cOrigem = CONTAS.get(origem);
        Conta cDestino = CONTAS.get(destino);

        // Validacoes de Requisito Funcional (Regras de Negocio)
        if (cOrigem == null) {
            throw new RequisitoFuncionalException("Conta de origem nao encontrada: " + origem);
        }
        if (cDestino == null) {
            throw new RequisitoFuncionalException("Conta de destino nao encontrada: " + destino);
        }
        if (valor <= 0) {
            throw new RequisitoFuncionalException("O valor da transferencia deve ser estritamente positivo.");
        }
        if (cOrigem.getSaldo() < valor) {
            throw new RequisitoFuncionalException(
                "Saldo insuficiente na conta de " + origem + ". Saldo atual: R$ " + cOrigem.getSaldo() +
                ", Valor solicitado: R$ " + valor
            );
        }

        // Simulacao de latencia para teste de metrica de RNF
        if (simularSobrecarga) {
            try {
                Thread.sleep(85); // Atraso de 85ms simulando gargalo de I/O em banco de dados
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            try {
                Thread.sleep(15); // Operacao nominal em 15ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Execucao da transacao (Consistencia de dados)
        cOrigem.debitar(valor);
        cDestino.creditar(valor);

        long tempoTotalMs = System.currentTimeMillis() - tempoInicio;

        // Afericao da Metrica Formal do RNF (Desempenho)
        if (tempoTotalMs > LIMITE_TEMPO_RESPOSTA_MS) {
            throw new RequisitoNaoFuncionalException(
                "Violacao de SLA de Desempenho (RNF01): A operacao durou " + tempoTotalMs +
                " ms, excedendo o teto tolerado de " + LIMITE_TEMPO_RESPOSTA_MS + " ms."
            );
        }

        System.out.printf("[RF + RNF ATENDIDOS] Transferencia de R$ %.2f de %s para %s concluida em %d ms.%n",
                valor, origem, destino, tempoTotalMs);
    }

    /**
     * Demonstracao da Metrica de Robustez (MTTR - Mean Time To Repair).
     * Simula a falha de um no secundario e a recuperacao proativa do servico.
     */
    public static void testarMetricaDeRobustezMTTR() {
        System.out.println("\n--- Afericao de RNF de Robustez: Medicao de MTTR ---");
        long tempoInicioFalha = System.currentTimeMillis();
        
        System.out.println("[ALERTA DE FALHA] Servico de persistencia offline detectado.");
        
        // Simulacao do processo de failover / recuperacao
        try {
            Thread.sleep(45); // 45ms para religar no redundante
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long tempoRecuperacaoMs = System.currentTimeMillis() - tempoInicioFalha;
        System.out.println("[RECUPERACAO CONCLUIDA] No redundante ativo em " + tempoRecuperacaoMs + " ms.");

        if (tempoRecuperacaoMs <= LIMITE_MTTR_MS) {
            System.out.println("[METRICA HOMOLOGADA] MTTR de " + tempoRecuperacaoMs + " ms esta em conformidade com o RNF (< " + LIMITE_MTTR_MS + " ms).");
        } else {
            System.out.println("[METRICA REPROVADA] MTTR excedeu o limite tolerado.");
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  SIMULADOR DE ENGENHARIA DE REQUISITOS: RF vs RNF COM METRICAS FORMAIS");
        System.out.println("========================================================================\n");

        // Inicializacao de dados de teste
        CONTAS.put("1001", new Conta("Alice", 1500.00));
        CONTAS.put("2002", new Conta("Bob", 300.00));

        // ---------------------------------------------------------------------
        // CENARIO 1: Cadastro de Usuario com sucesso de RF e RNF
        // ---------------------------------------------------------------------
        System.out.println("--- Cenario 1: Cadastro com Senha Forte (Conformidade RF e RNF) ---");
        try {
            cadastrarUsuario("alice@unifef.edu.br", "SenhaForte@2026");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENARIO 2: Violacao de Requisito Nao Funcional de Seguranca (Senha Fraca)
        // ---------------------------------------------------------------------
        System.out.println("\n--- Cenario 2: Tentativa com Senha Fraca (Violacao de RNF) ---");
        try {
            cadastrarUsuario("bob@unifef.edu.br", "123456");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENARIO 3: Violacao de Requisito Funcional (Saldo Insuficiente)
        // ---------------------------------------------------------------------
        System.out.println("\n--- Cenario 3: Transferencia com Saldo Insuficiente (Falha de RF) ---");
        try {
            realizarTransferencia("2002", "1001", 5000.00, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENARIO 4: Sucesso de RF e Sucesso de RNF de Desempenho (< 50ms)
        // ---------------------------------------------------------------------
        System.out.println("\n--- Cenario 4: Transferencia Nominal Eficiente (Conformidade RF e RNF) ---");
        try {
            realizarTransferencia("1001", "2002", 250.00, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENARIO 5: Sucesso no calculo do RF, porem violacao do RNF de Desempenho
        // ---------------------------------------------------------------------
        System.out.println("\n--- Cenario 5: Transferencia Sobrecarregada (Operacao Funciona, RNF Falha) ---");
        try {
            realizarTransferencia("1001", "2002", 100.00, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CENARIO 6: Afericao da Metrica Formal de Robustez (MTTR)
        // ---------------------------------------------------------------------
        testarMetricaDeRobustezMTTR();

        System.out.println("\n========================================================================");
        System.out.println("  EXECUCAO FINALIZADA COM SUCESSO - REVISAO CONCLUIDA");
        System.out.println("========================================================================");
    }
}
