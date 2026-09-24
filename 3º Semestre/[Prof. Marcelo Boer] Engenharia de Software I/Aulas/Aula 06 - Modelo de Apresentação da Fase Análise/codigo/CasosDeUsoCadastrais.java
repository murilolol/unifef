/**
 * Disciplina : Engenharia de Software I - UniFEF
 * Professor  : Marcelo Boer
 * Tema       : Execucao Pratica dos Casos de Uso Cadastrais: UC02 a UC06 (Secao 1.7.2 a 1.7.6)
 *
 * Como compilar e executar:
 *   javac CasosDeUsoCadastrais.java
 *   java CasosDeUsoCadastrais
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CasosDeUsoCadastrais {

    /**
     * Representacao sintetica para retorno tabular do caso de uso Listar (UC03).
     */
    public static class ItemListagemCliente {
        private final Integer idCliente;
        private final String documento;
        private final String razaoSocialOuNome;
        private final String telefone;
        private final Boolean ativo;

        public ItemListagemCliente(Integer idCliente, String documento, String razaoSocialOuNome, String telefone, Boolean ativo) {
            this.idCliente = idCliente;
            this.documento = documento;
            this.razaoSocialOuNome = razaoSocialOuNome;
            this.telefone = telefone;
            this.ativo = ativo;
        }

        @Override
        public String toString() {
            return String.format("| %-4d | %-16s | %-30s | %-15s | %-7s |",
                    idCliente, documento, razaoSocialOuNome, telefone, (ativo ? "ATIVO" : "INATIVO"));
        }
    }

    /**
     * Entidade completa recuperada no caso de uso Carregar (UC04) e atualizada no Alterar (UC05).
     */
    public static class RegistroClienteDetalhado {
        private final Integer idCliente;
        private String tipoPessoa;
        private String documento;
        private String razaoSocialOuNome;
        private String telefone;
        private String email;
        private String enderecoResumido;
        private Boolean ativo;

        public RegistroClienteDetalhado(Integer idCliente, String tipoPessoa, String documento, String razaoSocialOuNome, String telefone, String email, String enderecoResumido) {
            this.idCliente = idCliente;
            this.tipoPessoa = tipoPessoa;
            this.documento = documento;
            this.razaoSocialOuNome = razaoSocialOuNome;
            this.telefone = telefone;
            this.email = email;
            this.enderecoResumido = enderecoResumido;
            this.ativo = Boolean.TRUE;
        }

        public Integer getIdCliente() { return idCliente; }
        public String getDocumento() { return documento; }
        public void setDocumento(String documento) { this.documento = documento; }
        public String getRazaoSocialOuNome() { return razaoSocialOuNome; }
        public void setRazaoSocialOuNome(String razaoSocialOuNome) { this.razaoSocialOuNome = razaoSocialOuNome; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getEnderecoResumido() { return enderecoResumido; }
        public void setEnderecoResumido(String enderecoResumido) { this.enderecoResumido = enderecoResumido; }
        public Boolean getAtivo() { return ativo; }
        public void setAtivo(Boolean ativo) { this.ativo = ativo; }

        @Override
        public String toString() {
            return "DETALHES DO CLIENTE: [ID=" + idCliente + ", Tipo=" + tipoPessoa + ", Doc=" + documento +
                   ", Nome=" + razaoSocialOuNome + ", Tel=" + telefone + ", Email=" + email +
                   ", Endereco=" + enderecoResumido + ", Status=" + (ativo ? "Ativo" : "Inativo") + "]";
        }
    }

    /**
     * Servico conceitual que executa os fluxos principais e alternativos dos Casos de Uso.
     * Mantem em memoria os dados para demonstracao isolada de frameworks ou drivers SQL.
     */
    public static class GestaoClientesService {
        private final Map<Integer, RegistroClienteDetalhado> tabelaClientes = new HashMap<>();
        private final Map<Integer, Integer> pedidosAtivosPorCliente = new HashMap<>();
        private int sequencialId = 1;

        /**
         * Secao 1.7.2: UC02 - Cadastrar
         */
        public String executarUC02Cadastrar(String tipoPessoa, String documento, String nome, String telefone, String email, String endereco) {
            System.out.println("-> Executando UC02: Cadastrar...");

            // Passo 2: Validacao de campos obrigatorios (FA01 -> MSG04)
            List<String> pendencias = new ArrayList<>();
            if (documento == null || documento.trim().isEmpty()) pendencias.add("Documento");
            if (nome == null || nome.trim().isEmpty()) pendencias.add("RazaoSocialOuNome");
            if (telefone == null || telefone.trim().isEmpty()) pendencias.add("Telefone");
            if (!pendencias.isEmpty()) {
                return "[MSG04 - ERRO] Existem campos obrigatorios nao preenchidos: " + String.join(", ", pendencias);
            }

            // Passo 4: Verificacao de documento duplicado (FA02 -> MSG05)
            String docLimpo = documento.replaceAll("[^0-9]", "");
            for (RegistroClienteDetalhado reg : tabelaClientes.values()) {
                if (reg.getDocumento().equals(docLimpo) && reg.getAtivo()) {
                    return "[MSG05 - ERRO] Registro ja cadastrado com os dados informados: " + documento;
                }
            }

            // Passo 5: Persistencia e confirmacao
            int novoId = sequencialId++;
            RegistroClienteDetalhado novo = new RegistroClienteDetalhado(novoId, tipoPessoa, docLimpo, nome, telefone, email, endereco);
            tabelaClientes.put(novoId, novo);
            return "[MSG06 - SUCESSO] Registro gravado com sucesso! (ID Gerado: " + novoId + ")";
        }

        /**
         * Secao 1.7.3: UC03 - Listar
         * Recupera projecoes resumidas dos clientes aplicando filtros.
         */
        public List<ItemListagemCliente> executarUC03Listar(String termoBusca) {
            System.out.println("-> Executando UC03: Listar com filtro: '" + (termoBusca != null ? termoBusca : "<TODOS>") + "'...");
            List<ItemListagemCliente> resultado = new ArrayList<>();

            for (RegistroClienteDetalhado reg : tabelaClientes.values()) {
                boolean coincide = (termoBusca == null || termoBusca.trim().isEmpty())
                        || reg.getRazaoSocialOuNome().toLowerCase().contains(termoBusca.toLowerCase())
                        || reg.getDocumento().contains(termoBusca);
                if (coincide) {
                    resultado.add(new ItemListagemCliente(reg.getIdCliente(), reg.getDocumento(), reg.getRazaoSocialOuNome(), reg.getTelefone(), reg.getAtivo()));
                }
            }

            // FA01: Busca sem correspondencia (MSG07)
            if (resultado.isEmpty()) {
                System.out.println("   [MSG07 - ALERTA] Nenhum registro encontrado para os criterios de busca informados.");
            }
            return Collections.unmodifiableList(resultado);
        }

        /**
         * Secao 1.7.4: UC04 - Carregar
         * Distinto de Listar: busca todos os atributos do registro individual para abrir formulário.
         */
        public RegistroClienteDetalhado executarUC04Carregar(Integer idCliente) {
            System.out.println("-> Executando UC04: Carregar registro ID " + idCliente + "...");
            RegistroClienteDetalhado reg = tabelaClientes.get(idCliente);

            // FA01: Registro inexistente ou removido concorrentemente (MSG08)
            if (reg == null) {
                System.out.println("   [MSG08 - ERRO] Registro selecionado nao foi encontrado ou foi excluido por outro usuario.");
                return null;
            }
            System.out.println("   [SUCESSO] Dados carregados para edicao: " + reg.getRazaoSocialOuNome());
            return reg;
        }

        /**
         * Secao 1.7.5: UC05 - Alterar
         * Modifica os dados previamente carregados.
         */
        public String executarUC05Alterar(Integer idCliente, String novoNome, String novoTelefone, String novoEmail, String novoEndereco) {
            System.out.println("-> Executando UC05: Alterar registro ID " + idCliente + "...");
            RegistroClienteDetalhado reg = tabelaClientes.get(idCliente);
            if (reg == null) {
                return "[MSG08 - ERRO] Registro selecionado nao foi encontrado ou foi excluido por outro usuario.";
            }

            // FA01: Validacao de campos obrigatorios (MSG04)
            if (novoNome == null || novoNome.trim().isEmpty() || novoTelefone == null || novoTelefone.trim().isEmpty()) {
                return "[MSG04 - ERRO] Existem campos obrigatorios nao preenchidos: Nome e Telefone nao podem ficar vazios.";
            }

            // Passo 5: Atualizacao e confirmacao
            reg.setRazaoSocialOuNome(novoNome);
            reg.setTelefone(novoTelefone);
            reg.setEmail(novoEmail);
            reg.setEnderecoResumido(novoEndereco);
            return "[MSG09 - SUCESSO] Alteracoes salvas com sucesso!";
        }

        /**
         * Secao 1.7.6: UC06 - Excluir
         * Remove logicamente respeitando a integridade referencial.
         */
        public String executarUC06Excluir(Integer idCliente, boolean confirmouExclusao) {
            System.out.println("-> Executando UC06: Excluir registro ID " + idCliente + "...");
            RegistroClienteDetalhado reg = tabelaClientes.get(idCliente);
            if (reg == null) {
                return "[MSG08 - ERRO] Registro selecionado nao foi encontrado ou foi excluido por outro usuario.";
            }

            // Passo 2: Exibicao do dialogo MSG10
            System.out.println("   [MSG10 - CONFIRMACAO] Deseja realmente excluir o registro " + reg.getRazaoSocialOuNome() + "? Esta acao nao podera ser desfeita.");

            // FA01: Desistencia do operador
            if (!confirmouExclusao) {
                return "   [CANCELADO] Operacao de exclusao cancelada pelo usuario.";
            }

            // FA02: Bloqueio por integridade referencial (MSG11)
            Integer pedidos = pedidosAtivosPorCliente.getOrDefault(idCliente, 0);
            if (pedidos > 0) {
                return "[MSG11 - ERRO] Nao e possivel excluir o registro pois existem transacoes ativas vinculadas. (Pedidos ativos: " + pedidos + ")";
            }

            // Passo 4 e 5: Exclusao logica efetivada
            reg.setAtivo(Boolean.FALSE);
            return "[MSG12 - SUCESSO] Registro excluido com sucesso!";
        }

        public void vincularPedidosParaTeste(Integer idCliente, int quantidade) {
            pedidosAtivosPorCliente.put(idCliente, quantidade);
        }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("   UNIFEF - ENGENHARIA DE SOFTWARE I - CASOS DE USO CADASTRAIS     ");
        System.out.println("===================================================================\n");

        GestaoClientesService service = new GestaoClientesService();

        System.out.println("--- ETAPA 1: TESTE DO UC02 (CADASTRAR) ---");
        // Teste de cadastro normal
        String r1 = service.executarUC02Cadastrar("JURIDICA", "50.888.777/0001-99", "Fundacao Educacional", "(17) 3465-0000", "contato@fef.br", "Av. Milton Terra Verdi");
        System.out.println(r1);

        String r2 = service.executarUC02Cadastrar("FISICA", "111.222.333-44", "Joao da Silva", "(17) 99999-1111", "joao@gmail.com", "Rua Brasil, 100");
        System.out.println(r2);

        // FA01: Campos em branco (MSG04)
        String rFalha1 = service.executarUC02Cadastrar("FISICA", "", "", "", null, null);
        System.out.println(rFalha1);

        // FA02: Documento duplicado (MSG05)
        String rFalha2 = service.executarUC02Cadastrar("FISICA", "11122233344", "Joao Clone", "(17) 98888-2222", "clone@gmail.com", "Rua Brasil, 100");
        System.out.println(rFalha2);
        System.out.println();

        System.out.println("--- ETAPA 2: TESTE DO UC03 (LISTAR) ---");
        List<ItemListagemCliente> listaGeral = service.executarUC03Listar(null);
        System.out.println("+------+------------------+--------------------------------+-----------------+---------+");
        System.out.println("| ID   | DOCUMENTO        | RAZAO SOCIAL / NOME            | TELEFONE        | STATUS  |");
        System.out.println("+------+------------------+--------------------------------+-----------------+---------+");
        for (ItemListagemCliente item : listaGeral) {
            System.out.println(item);
        }
        System.out.println("+------+------------------+--------------------------------+-----------------+---------+");

        // FA01: Busca vazia (MSG07)
        service.executarUC03Listar("TermoInexistenteXYZ");
        System.out.println();

        System.out.println("--- ETAPA 3: TESTE DO UC04 (CARREGAR) vs UC03 (LISTAR) ---");
        System.out.println("Diferenca conceitual: Listar trouxe as colunas sinteticas acima.");
        System.out.println("Carregar agora busca a entidade integral com todos os atributos pelo ID:");
        RegistroClienteDetalhado detalhe = service.executarUC04Carregar(1);
        System.out.println("   " + detalhe);

        // FA01: Carregar registro inexistente (MSG08)
        service.executarUC04Carregar(999);
        System.out.println();

        System.out.println("--- ETAPA 4: TESTE DO UC05 (ALTERAR) ---");
        String rAlt = service.executarUC05Alterar(1, "Fundacao Educacional de Fernandopolis (UniFEF)", "(17) 3465-1111", "reitoria@unifef.edu.br", "Av. Milton Terra Verdi, 1800");
        System.out.println(rAlt);
        System.out.println("Apos alteracao: " + service.executarUC04Carregar(1));
        System.out.println();

        System.out.println("--- ETAPA 5: TESTE DO UC06 (EXCLUIR) E INTEGRIDADE REFERENCIAL ---");
        // Simulando que o Cliente 1 possui pedidos de vendas vinculados
        service.vincularPedidosParaTeste(1, 3);

        // Tentativa 1: Bloqueio por integridade referencial (MSG11)
        String rExc1 = service.executarUC06Excluir(1, true);
        System.out.println(rExc1);

        // Tentativa 2: Exclusao bem-sucedida do Cliente 2 que nao tem pedidos
        String rExc2 = service.executarUC06Excluir(2, true);
        System.out.println(rExc2);

        System.out.println("\nListagem final apos exclusao logica do Cliente 2:");
        for (ItemListagemCliente item : service.executarUC03Listar(null)) {
            System.out.println(item);
        }
    }
}
