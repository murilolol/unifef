/**
 * Disciplina: Engenharia de Software I - 3º Semestre
 * Professor: Marcelo Boer
 * Instituição: UniFEF (Centro Universitário de Santa Fé do Sul)
 * Tema: Aula 04 — Especificação Textual e Execução dos Casos de Uso DCU01 e DCU02
 *
 * Como compilar:
 *   javac CasoDeUsoAutenticacaoCadastro.java
 *
 * Como executar:
 *   java CasoDeUsoAutenticacaoCadastro
 *
 * CONCEITOS IMPLEMENTADOS NESTE ARQUIVO:
 * 1. DCU01 - Realizar Login Aplicativo:
 *    - Fluxo Normal: validação de credenciais ativas e retorno de sessão autenticada.
 *    - Fluxo Alternativo 5.1: campos vazios ("Email/Senha não informados").
 *    - Fluxo Alternativo 5.2: credenciais inválidas ou usuário inexistente ("Usuário Cliente não cadastrado").
 * 2. DCU02 - Cadastrar Usuário Cliente:
 *    - Fluxo Normal: validação sintática, verificação de duplicidade, hash de senha e persistência.
 *    - Fluxo Alternativo 6.1: validação de dados inválidos (algoritmo formal de CPF e força de senha).
 *    - Fluxo Alternativo 8.1: detecção de e-mail duplicado na base.
 *    - Fluxo Alternativo 8.2: detecção de CPF já registrado na base de dados.
 * 3. Rastreabilidade com RNFs:
 *    - RNF02 (Segurança): Criptografia SHA-256 com salt sobre senhas.
 *    - RNF04 (Integridade): Garantia de unicidade de chave para CPF e E-mail.
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CasoDeUsoAutenticacaoCadastro {

    // =========================================================================
    // ENTIDADE PERSISTIDA NO BANCO DE DADOS EM MEMÓRIA
    // =========================================================================
    public static class RegistroUsuario {
        private final UUID id;
        private final String nomeCompleto;
        private final String email;
        private final String senhaHash;
        private final String cpf;
        private final String telefone;
        private final LocalDate dataNascimento;
        private final String cidade;
        private final String estado;
        private final String bairro;

        public RegistroUsuario(String nomeCompleto, String email, String senhaHash, String cpf,
                               String telefone, LocalDate dataNascimento, String cidade, String estado, String bairro) {
            this.id = UUID.randomUUID();
            this.nomeCompleto = nomeCompleto;
            this.email = email.toLowerCase().trim();
            this.senhaHash = senhaHash;
            this.cpf = cpf.replaceAll("\\D", "");
            this.telefone = telefone;
            this.dataNascimento = dataNascimento;
            this.cidade = cidade;
            this.estado = estado;
            this.bairro = bairro;
        }

        public UUID getId() { return id; }
        public String getNomeCompleto() { return nomeCompleto; }
        public String getEmail() { return email; }
        public String getSenhaHash() { return senhaHash; }
        public String getCpf() { return cpf; }
        public String getBairro() { return bairro; }
    }

    // =========================================================================
    // CAMADA DE REPOSITÓRIO (BANCO DE DADOS EM MEMÓRIA COM RESTRIÇÕES RNF04)
    // =========================================================================
    public static class RepositorioUsuarios {
        private final Map<UUID, RegistroUsuario> tabelaUsuarios = new HashMap<>();
        private final Map<String, UUID> indiceEmail = new HashMap<>();
        private final Map<String, UUID> indiceCpf = new HashMap<>();

        public boolean existeEmail(String email) {
            return indiceEmail.containsKey(email.toLowerCase().trim());
        }

        public boolean existeCpf(String cpf) {
            return indiceCpf.containsKey(cpf.replaceAll("\\D", ""));
        }

        public void salvar(RegistroUsuario usuario) {
            tabelaUsuarios.put(usuario.getId(), usuario);
            indiceEmail.put(usuario.getEmail(), usuario.getId());
            indiceCpf.put(usuario.getCpf(), usuario.getId());
        }

        public RegistroUsuario buscarPorEmail(String email) {
            UUID id = indiceEmail.get(email.toLowerCase().trim());
            return id != null ? tabelaUsuarios.get(id) : null;
        }
    }

    // =========================================================================
    // SERVIÇOS AUXILIARES: CRIPTOGRAFIA (RNF02) E VALIDAÇÃO DE DADOS (REGRA)
    // =========================================================================
    public static class ServicoSeguranca {
        public static String gerarHashSenha(String senhaPlana) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String salting = "salt_seguro_unifef_desapega_ja_" + senhaPlana;
                byte[] hash = digest.digest(salting.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Algoritmo de hash não disponível no ambiente", e);
            }
        }

        public static boolean validarCpf(String cpfRaw) {
            if (cpfRaw == null) return false;
            String cpf = cpfRaw.replaceAll("\\D", "");
            if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

            try {
                int soma = 0;
                for (int i = 0; i < 9; i++) {
                    soma += (cpf.charAt(i) - '0') * (10 - i);
                }
                int digito1 = 11 - (soma % 11);
                if (digito1 >= 10) digito1 = 0;
                if (digito1 != (cpf.charAt(9) - '0')) return false;

                soma = 0;
                for (int i = 0; i < 10; i++) {
                    soma += (cpf.charAt(i) - '0') * (11 - i);
                }
                int digito2 = 11 - (soma % 11);
                if (digito2 >= 10) digito2 = 0;
                return digito2 == (cpf.charAt(10) - '0');
            } catch (Exception e) {
                return false;
            }
        }
    }

    // =========================================================================
    // DTOs DE ENTRADA E RESPOSTA DA MATRIZ DE RASTREABILIDADE
    // =========================================================================
    public static class DadosCadastroEntrada {
        public String nomeCompleto;
        public String email;
        public String senha;
        public String confirmacaoSenha;
        public String telefone;
        public String cpf;
        public LocalDate dataNascimento;
        public String cidade;
        public String estado;
        public String bairro;

        public DadosCadastroEntrada(String nomeCompleto, String email, String senha, String confirmacaoSenha,
                                    String telefone, String cpf, LocalDate dataNascimento,
                                    String cidade, String estado, String bairro) {
            this.nomeCompleto = nomeCompleto;
            this.email = email;
            this.senha = senha;
            this.confirmacaoSenha = confirmacaoSenha;
            this.telefone = telefone;
            this.cpf = cpf;
            this.dataNascimento = dataNascimento;
            this.cidade = cidade;
            this.estado = estado;
            this.bairro = bairro;
        }
    }

    public static class RespostaCasoDeUso {
        private final boolean sucesso;
        private final String codigoMensagem; // ex: Msg01, Msg02, MsgErro
        private final String descricao;
        private final Object dadosRetorno;

        public RespostaCasoDeUso(boolean sucesso, String codigoMensagem, String descricao, Object dadosRetorno) {
            this.sucesso = sucesso;
            this.codigoMensagem = codigoMensagem;
            this.descricao = descricao;
            this.dadosRetorno = dadosRetorno;
        }

        public boolean isSucesso() { return sucesso; }
        public String getCodigoMensagem() { return codigoMensagem; }
        public String getDescricao() { return descricao; }
        public Object getDadosRetorno() { return dadosRetorno; }

        @Override
        public String toString() {
            return String.format("[%s] %s -> %s",
                    sucesso ? "SUCESSO" : "FALHA/EXCEÇÃO", codigoMensagem, descricao);
        }
    }

    // =========================================================================
    // ESPECIFICAÇÃO EXECUTÁVEL DOS CASOS DE USO
    // =========================================================================
    public static class ServicoCasosDeUso {
        private final RepositorioUsuarios repositorio;

        public ServicoCasosDeUso(RepositorioUsuarios repositorio) {
            this.repositorio = repositorio;
        }

        /**
         * DCU02 — Cadastrar Usuário Cliente
         * Ator Principal: Pessoa Cliente (Interessado)
         */
        public RespostaCasoDeUso executarDCU02_CadastrarUsuarioCliente(DadosCadastroEntrada entrada) {
            // Passo 6 do Fluxo Normal: validação local de regras
            if (entrada.nomeCompleto == null || entrada.nomeCompleto.trim().isEmpty()) {
                return new RespostaCasoDeUso(false, "MsgErro6.1", "Nome completo não informado", null);
            }
            if (!ServicoSeguranca.validarCpf(entrada.cpf)) {
                // Fluxo Alternativo 6.1: CPF com dígitos verificadores inválidos
                return new RespostaCasoDeUso(false, "MsgErro6.1", "CPF inválido. Verifique os números digitados", null);
            }
            if (entrada.senha == null || entrada.senha.length() < 8 || !entrada.senha.matches(".*\\d.*") || !entrada.senha.matches(".*[a-zA-Z].*")) {
                // Fluxo Alternativo 6.1: Senha fraca
                return new RespostaCasoDeUso(false, "MsgErro6.1", "A senha deve possuir no mínimo 8 caracteres, contendo letras e números", null);
            }
            if (!Objects.equals(entrada.senha, entrada.confirmacaoSenha)) {
                // Fluxo Alternativo 6.1: Divergência de confirmação
                return new RespostaCasoDeUso(false, "MsgErro6.1", "As senhas informadas não coincidem", null);
            }

            // Passo 8 do Fluxo Normal: Validação de duplicidade no servidor
            if (repositorio.existeEmail(entrada.email)) {
                // Fluxo Alternativo 8.1: E-mail já existente
                return new RespostaCasoDeUso(false, "MsgErro8.1", "Este e-mail já está cadastrado. Deseja realizar login ou recuperar a senha?", null);
            }
            if (repositorio.existeCpf(entrada.cpf)) {
                // Fluxo Alternativo 8.2: CPF já cadastrado
                return new RespostaCasoDeUso(false, "MsgErro8.2", "CPF já cadastrado na plataforma", null);
            }

            // Passo 9: Persistência com senha criptografada (RNF02)
            String hash = ServicoSeguranca.gerarHashSenha(entrada.senha);
            RegistroUsuario novoUsuario = new RegistroUsuario(
                    entrada.nomeCompleto, entrada.email, hash, entrada.cpf,
                    entrada.telefone, entrada.dataNascimento, entrada.cidade, entrada.estado, entrada.bairro
            );
            repositorio.salvar(novoUsuario);

            // Passo 10: Retorno com sucesso (Msg02)
            return new RespostaCasoDeUso(true, "Msg02",
                    "Cadastro realizado com sucesso! Bem-vindo ao Desapega Já", novoUsuario.getId());
        }

        /**
         * DCU01 — Realizar Login Aplicativo
         * Ator Principal: Usuário Cliente ou Usuário Anunciante
         */
        public RespostaCasoDeUso executarDCU01_RealizarLogin(String email, String senha) {
            // Passo 5 do Fluxo Normal (Validação)
            if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
                // Fluxo Alternativo 5.1: Campos vazios
                return new RespostaCasoDeUso(false, "MsgErro5.1", "Email/Senha não informados", null);
            }

            RegistroUsuario usuario = repositorio.buscarPorEmail(email);
            if (usuario == null) {
                // Fluxo Alternativo 5.2: Usuário não localizado
                return new RespostaCasoDeUso(false, "MsgErro5.2", "Usuário Cliente não cadastrado", null);
            }

            // Verificação do Hash de Senha (RNF02)
            String hashEntrada = ServicoSeguranca.gerarHashSenha(senha);
            if (!usuario.getSenhaHash().equals(hashEntrada)) {
                // Fluxo Alternativo 5.2: Senha incorreta
                return new RespostaCasoDeUso(false, "MsgErro5.2", "Usuário Cliente não cadastrado ou credencial incorreta", null);
            }

            // Passo 6: Sessão autenticada (Msg01)
            String tokenSessaoSimulado = "SESSION_TOKEN_" + UUID.randomUUID().toString().substring(0, 16);
            return new RespostaCasoDeUso(true, "Msg01",
                    "Login efetuado com sucesso. Carregando catálogo...", tokenSessaoSimulado);
        }
    }

    // =========================================================================
    // MAIN: BATERIA COMPLETA DE TESTES DOS FLUXOS NORMAIS E ALTERNATIVOS
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("  UNIFEF - ENGENHARIA DE SOFTWARE I - PROF. MARCELO BOER");
        System.out.println("  SIMULAÇÃO DE CASOS DE USO: DCU01 (Login) e DCU02 (Cadastro)");
        System.out.println("====================================================================\n");

        RepositorioUsuarios repo = new RepositorioUsuarios();
        ServicoCasosDeUso servico = new ServicoCasosDeUso(repo);

        // ---------------------------------------------------------------------
        // CENÁRIO 1: DCU02 - CADASTRO COM SUCESSO (FLUXO NORMAL)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 1] Executando DCU02: Fluxo Normal de Cadastro ---");
        DadosCadastroEntrada entradaValida = new DadosCadastroEntrada(
                "Mariana Lima Costa",
                "mariana.lima@unifef.edu.br",
                "SenhaSegura2026",
                "SenhaSegura2026",
                "(17) 99771-8899",
                "529.982.247-25", // CPF válido (algoritmo formal)
                LocalDate.of(2000, 3, 10),
                "Santa Fé do Sul",
                "SP",
                "Bela Vista"
        );
        RespostaCasoDeUso resp1 = servico.executarDCU02_CadastrarUsuarioCliente(entradaValida);
        System.out.println("Resultado: " + resp1);
        System.out.println("ID Gerado: " + resp1.getDadosRetorno() + "\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 2: DCU02 - FLUXO ALTERNATIVO 6.1 (CPF INVÁLIDO)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 2] Executando DCU02: Fluxo Alternativo 6.1 (CPF Inválido) ---");
        DadosCadastroEntrada entradaCpfInvalido = new DadosCadastroEntrada(
                "João Silva", "joao@email.com", "Senha1234", "Senha1234",
                "(17) 99111-2222", "111.222.333-99", LocalDate.of(1995, 1, 1),
                "Santa Fé do Sul", "SP", "Centro"
        );
        RespostaCasoDeUso resp2 = servico.executarDCU02_CadastrarUsuarioCliente(entradaCpfInvalido);
        System.out.println("Resultado: " + resp2 + "\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 3: DCU02 - FLUXO ALTERNATIVO 8.1 (E-MAIL DUPLICADO)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 3] Executando DCU02: Fluxo Alternativo 8.1 (E-mail Já Cadastrado) ---");
        DadosCadastroEntrada entradaEmailDuplicado = new DadosCadastroEntrada(
                "Mariana Clone", "mariana.lima@unifef.edu.br", "OutraSenha88", "OutraSenha88",
                "(17) 99999-0000", "853.513.330-00", LocalDate.of(2002, 7, 15),
                "Santa Fé do Sul", "SP", "Centro"
        );
        RespostaCasoDeUso resp3 = servico.executarDCU02_CadastrarUsuarioCliente(entradaEmailDuplicado);
        System.out.println("Resultado: " + resp3 + "\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 4: DCU02 - FLUXO ALTERNATIVO 8.2 (CPF DUPLICADO)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 4] Executando DCU02: Fluxo Alternativo 8.2 (CPF Já Cadastrado) ---");
        DadosCadastroEntrada entradaCpfDuplicado = new DadosCadastroEntrada(
                "Outra Pessoa", "outro.email@provedor.com", "SenhaValida1", "SenhaValida1",
                "(17) 98888-7777", "529.982.247-25", LocalDate.of(1998, 12, 5),
                "Santa Fé do Sul", "SP", "Centro"
        );
        RespostaCasoDeUso resp4 = servico.executarDCU02_CadastrarUsuarioCliente(entradaCpfDuplicado);
        System.out.println("Resultado: " + resp4 + "\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 5: DCU01 - LOGIN COM SUCESSO (FLUXO NORMAL)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 5] Executando DCU01: Fluxo Normal de Login ---");
        RespostaCasoDeUso loginSucesso = servico.executarDCU01_RealizarLogin(
                "mariana.lima@unifef.edu.br", "SenhaSegura2026");
        System.out.println("Resultado: " + loginSucesso);
        System.out.println("Sessão Criada: " + loginSucesso.getDadosRetorno() + "\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 6: DCU01 - FLUXO ALTERNATIVO 5.1 (CAMPOS VAZIOS)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 6] Executando DCU01: Fluxo Alternativo 5.1 (Campos Não Informados) ---");
        RespostaCasoDeUso loginVazio = servico.executarDCU01_RealizarLogin("", "");
        System.out.println("Resultado: " + loginVazio + "\n");

        // ---------------------------------------------------------------------
        // CENÁRIO 7: DCU01 - FLUXO ALTERNATIVO 5.2 (CREDENCIAIS INVÁLIDAS)
        // ---------------------------------------------------------------------
        System.out.println("--- [TESTE 7] Executando DCU01: Fluxo Alternativo 5.2 (Senha Incorreta) ---");
        RespostaCasoDeUso loginSenhaErrada = servico.executarDCU01_RealizarLogin(
                "mariana.lima@unifef.edu.br", "SenhaErrada123");
        System.out.println("Resultado: " + loginSenhaErrada + "\n");

        System.out.println(">>> TODOS OS FLUXOS DOS CASOS DE USO DCU01 E DCU02 FORAM EXECUTADOS COM SUCESSO <<<");
    }
}
