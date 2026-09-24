/**
 * Disciplina : Engenharia de Software I - UniFEF
 * Professor  : Marcelo Boer
 * Tema       : Fase de Analise - Diagrama de Classes de Dominio e Catalogo de Mensagens (Secao 1.6 e 2.1)
 *
 * Como compilar e executar:
 *   javac ModeloDominioSisVendas.java
 *   java ModeloDominioSisVendas
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ModeloDominioSisVendas {

    /**
     * Secao 1.6: Dicionario Centralizado de Mensagens do Sistema.
     * Padroniza codigos e mensagens para garantir rastreabilidade com a documentacao.
     */
    public enum MensagemSistema {
        MSG01("MSG01", "Erro", "Usuario ou senha invalidos. Por favor, verifique suas credenciais."),
        MSG02("MSG02", "Alerta", "Usuario inativo no sistema. Contate o administrador."),
        MSG03("MSG03", "Sucesso", "Autenticacao realizada com sucesso. Redirecionando..."),
        MSG04("MSG04", "Erro", "Existem campos obrigatorios nao preenchidos: "),
        MSG05("MSG05", "Erro", "Registro ja cadastrado com os dados informados: "),
        MSG06("MSG06", "Sucesso", "Registro gravado com sucesso!"),
        MSG07("MSG07", "Alerta", "Nenhum registro encontrado para os criterios de busca informados."),
        MSG08("MSG08", "Erro", "Registro selecionado nao foi encontrado ou foi excluido por outro usuario."),
        MSG09("MSG09", "Sucesso", "Alteracoes salvas com sucesso!"),
        MSG10("MSG10", "Confirmacao", "Deseja realmente excluir o registro? Esta acao nao podera ser desfeita."),
        MSG11("MSG11", "Erro", "Nao e possivel excluir o registro pois existem transacoes ativas vinculadas."),
        MSG12("MSG12", "Sucesso", "Registro excluido com sucesso!");

        private final String codigo;
        private final String tipo;
        private final String textoPadrao;

        MensagemSistema(String codigo, String tipo, String textoPadrao) {
            this.codigo = codigo;
            this.tipo = tipo;
            this.textoPadrao = textoPadrao;
        }

        public String getCodigo() { return codigo; }
        public String getTipo() { return tipo; }
        public String getTextoPadrao() { return textoPadrao; }

        public String formatar(String complemento) {
            return "[" + codigo + " - " + tipo.toUpperCase() + "] " + textoPadrao + (complemento != null ? complemento : "");
        }

        @Override
        public String toString() {
            return formatar(null);
        }
    }

    /**
     * Secao 1.3: Perfil de acesso mapeado no Quadro de Atores.
     */
    public enum PerfilAcesso {
        ADMINISTRADOR,
        OPERADOR_VENDAS
    }

    /**
     * Secao 2.1: Classe Conceitual Usuario.
     * Representa a credencial e o perfil de quem opera o sistema.
     */
    public static class Usuario {
        private final Integer idUsuario;
        private final String login;
        private String senhaHash;
        private String nomeCompleto;
        private String email;
        private PerfilAcesso perfilAcesso;
        private Boolean statusAtivo;

        public Usuario(Integer idUsuario, String login, String senhaHash, String nomeCompleto, String email, PerfilAcesso perfilAcesso) {
            this.idUsuario = idUsuario;
            this.login = Objects.requireNonNull(login, "Login e obrigatorio");
            this.senhaHash = Objects.requireNonNull(senhaHash, "Senha e obrigatoria");
            this.nomeCompleto = Objects.requireNonNull(nomeCompleto, "Nome e obrigatorio");
            this.email = email;
            this.perfilAcesso = Objects.requireNonNull(perfilAcesso, "Perfil e obrigatorio");
            this.statusAtivo = Boolean.TRUE;
        }

        public Boolean autenticar(String senhaPlana) {
            if (!this.statusAtivo) {
                return Boolean.FALSE;
            }
            return this.senhaHash.equals(simularHash(senhaPlana));
        }

        public void bloquearAcesso() {
            this.statusAtivo = Boolean.FALSE;
        }

        public void reativarAcesso() {
            this.statusAtivo = Boolean.TRUE;
        }

        public static String simularHash(String senha) {
            return "HASH_" + Integer.toHexString(senha != null ? senha.hashCode() : 0);
        }

        public Integer getIdUsuario() { return idUsuario; }
        public String getLogin() { return login; }
        public String getNomeCompleto() { return nomeCompleto; }
        public PerfilAcesso getPerfilAcesso() { return perfilAcesso; }
        public Boolean getStatusAtivo() { return statusAtivo; }

        @Override
        public String toString() {
            return "Usuario [ID=" + idUsuario + ", Login=" + login + ", Perfil=" + perfilAcesso + ", Ativo=" + statusAtivo + "]";
        }
    }

    /**
     * Secao 2.1: Classe Conceitual Endereco.
     * Participa de relacao de Composicao forte com Cliente (Cliente *-- Endereco).
     */
    public static class Endereco {
        private final Integer idEndereco;
        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String uf;
        private String cep;

        public Endereco(Integer idEndereco, String logradouro, String numero, String complemento, String bairro, String cidade, String uf, String cep) {
            this.idEndereco = idEndereco;
            this.logradouro = logradouro;
            this.numero = numero;
            this.complemento = complemento;
            this.bairro = bairro;
            this.cidade = cidade;
            this.uf = uf;
            this.cep = cep;
        }

        public Boolean validarCep() {
            if (this.cep == null) return Boolean.FALSE;
            String limpo = this.cep.replaceAll("[^0-9]", "");
            return limpo.length() == 8;
        }

        public Integer getIdEndereco() { return idEndereco; }
        public String getLogradouro() { return logradouro; }
        public String getNumero() { return numero; }
        public String getCidade() { return cidade; }
        public String getUf() { return uf; }
        public String getCep() { return cep; }

        @Override
        public String toString() {
            return logradouro + ", " + numero + " (" + bairro + ") - " + cidade + "/" + uf + " CEP: " + cep;
        }
    }

    /**
     * Secao 2.1: Classe Conceitual Cliente.
     * Centraliza as regras de negocio cadastrais e contem enderecos por composicao.
     */
    public static class Cliente {
        private final Integer idCliente;
        private String tipoPessoa; // 'FISICA' ou 'JURIDICA'
        private String documentoIdentificacao; // CPF ou CNPJ
        private String razaoSocialOuNome;
        private String telefonePrincipal;
        private String emailContato;
        private final LocalDateTime dataCadastro;
        private Boolean ativo;
        private final List<Endereco> enderecos; // Composicao 1..*

        public Cliente(Integer idCliente, String tipoPessoa, String documentoIdentificacao, String razaoSocialOuNome, String telefonePrincipal, String emailContato) {
            this.idCliente = idCliente;
            this.tipoPessoa = tipoPessoa;
            this.documentoIdentificacao = sanitizarDocumento(documentoIdentificacao);
            this.razaoSocialOuNome = razaoSocialOuNome;
            this.telefonePrincipal = telefonePrincipal;
            this.emailContato = emailContato;
            this.dataCadastro = LocalDateTime.now();
            this.ativo = Boolean.TRUE;
            this.enderecos = new ArrayList<>();
        }

        public Boolean validarDocumento() {
            if (this.documentoIdentificacao == null) return Boolean.FALSE;
            int len = this.documentoIdentificacao.length();
            return len == 11 || len == 14;
        }

        public void inativarCadastro() {
            this.ativo = Boolean.FALSE;
        }

        public void reativarCadastro() {
            this.ativo = Boolean.TRUE;
        }

        public void adicionarEndereco(Endereco endereco) {
            Objects.requireNonNull(endereco, "Endereco nao pode ser nulo na composicao");
            this.enderecos.add(endereco);
        }

        public List<Endereco> getEnderecos() {
            return Collections.unmodifiableList(this.enderecos);
        }

        private static String sanitizarDocumento(String doc) {
            return doc != null ? doc.replaceAll("[^0-9]", "") : "";
        }

        public Integer getIdCliente() { return idCliente; }
        public String getTipoPessoa() { return tipoPessoa; }
        public String getDocumentoIdentificacao() { return documentoIdentificacao; }
        public String getRazaoSocialOuNome() { return razaoSocialOuNome;
        }
        public void setRazaoSocialOuNome(String razaoSocialOuNome) { this.razaoSocialOuNome = razaoSocialOuNome; }
        public String getTelefonePrincipal() { return telefonePrincipal; }
        public void setTelefonePrincipal(String telefonePrincipal) { this.telefonePrincipal = telefonePrincipal; }
        public String getEmailContato() { return emailContato; }
        public void setEmailContato(String emailContato) { this.emailContato = emailContato; }
        public Boolean getAtivo() { return ativo; }
        public LocalDateTime getDataCadastro() { return dataCadastro; }

        @Override
        public String toString() {
            return "Cliente [ID=" + idCliente + ", Nome/Razao=" + razaoSocialOuNome +
                   ", Doc=" + documentoIdentificacao + ", Ativo=" + ativo + ", QtdEnderecos=" + enderecos.size() + "]";
        }
    }

    /**
     * Secao 2.1: Classe Conceitual AuditoriaOperacao.
     * Registra acoes sensiveis para atender a governanca da aplicacao.
     */
    public static class AuditoriaOperacao {
        private final Integer idLog;
        private final LocalDateTime dataHoraRegistro;
        private final String operacaoRealizada;
        private final String valoresAnteriores;
        private final String valoresNovos;
        private final String ipOrigem;
        private final Integer idUsuario;

        public AuditoriaOperacao(Integer idLog, String operacaoRealizada, String valoresAnteriores, String valoresNovos, String ipOrigem, Integer idUsuario) {
            this.idLog = idLog;
            this.dataHoraRegistro = LocalDateTime.now();
            this.operacaoRealizada = operacaoRealizada;
            this.valoresAnteriores = valoresAnteriores;
            this.valoresNovos = valoresNovos;
            this.ipOrigem = ipOrigem;
            this.idUsuario = idUsuario;
        }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return "Auditoria [ID=" + idLog + ", Data=" + dataHoraRegistro.format(fmt) +
                   ", Operacao=" + operacaoRealizada + ", UsuarioID=" + idUsuario + ", IP=" + ipOrigem + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("   UNIFEF - ENGENHARIA DE SOFTWARE I - MODELO DE DOMINIO (AV2)     ");
        System.out.println("===================================================================\n");

        System.out.println("1. Demonstracao do Dicionario de Mensagens Padronizadas (Secao 1.6):");
        System.out.println("   " + MensagemSistema.MSG01);
        System.out.println("   " + MensagemSistema.MSG04.formatar("Nome, CPF/CNPJ"));
        System.out.println("   " + MensagemSistema.MSG06);
        System.out.println("   " + MensagemSistema.MSG11);
        System.out.println();

        System.out.println("2. Instanciacao de Entidades Conceituais de Dominio (Secao 2.1):");
        Usuario admin = new Usuario(1, "marcelo.boer", Usuario.simularHash("senha123"), "Prof. Marcelo Boer", "marcelo@unifef.edu.br", PerfilAcesso.ADMINISTRADOR);
        System.out.println("   Criado: " + admin);
        System.out.println("   Teste de autenticacao correta: " + admin.autenticar("senha123"));
        System.out.println("   Teste de autenticacao incorreta: " + admin.autenticar("errada"));
        System.out.println();

        System.out.println("3. Demonstracao da Relacao Todo-Parte de Composicao (Cliente *-- Endereco):");
        Cliente cliente = new Cliente(101, "JURIDICA", "50.888.777/0001-99", "UniFEF Educacional Ltda", "(17) 3465-0000", "contato@unifef.edu.br");
        Endereco end1 = new Endereco(501, "Av. Milton Terra Verdi", "1800", "Campus Central", "Centro", "Fernandopolis", "SP", "15600-000");
        Endereco end2 = new Endereco(502, "Rua Brasil", "500", "Predio B", "Jardim America", "Fernandopolis", "SP", "15600-111");
        cliente.adicionarEndereco(end1);
        cliente.adicionarEndereco(end2);

        System.out.println("   " + cliente);
        System.out.println("   Documento valido? " + cliente.validarDocumento());
        System.out.println("   Enderecos vinculados:");
        for (Endereco e : cliente.getEnderecos()) {
            System.out.println("     -> " + e + " [CEP Valido: " + e.validarCep() + "]");
        }
        System.out.println();

        System.out.println("4. Registro de Auditoria Conceitual:");
        AuditoriaOperacao log = new AuditoriaOperacao(1, "CADASTRO_CLIENTE", "novo", cliente.toString(), "192.168.1.10", admin.getIdUsuario());
        System.out.println("   " + log);
        System.out.println("\nEstruturas de modelo validadas com sucesso conforme o Diagrama de Classes da Fase de Analise.");
    }
}
