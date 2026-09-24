/*
 * Disciplina: Engenharia de Software I
 * Professor: Marcelo Boer (UniFEF)
 * Tema: Processo de Abstração e Modelagem Conceitual de Dados (Estudo de Caso: Desapega Já)
 *
 * Como compilar:
 *   javac ModeloDominioDesapegaJa.java
 * Como executar:
 *   java ModeloDominioDesapegaJa
 *
 * Conceitos da aula demonstrados neste arquivo:
 *   1. Abstração e Filtragem de Ruído: Seleção de atributos essenciais ao domínio transacional,
 *      ignorando detalhes irrelevantes (altura, peso, estado civil, tipo sanguíneo).
 *   2. Resolução da Duplicação Cadastral: Eliminação da redundância entre 'Anunciante' e
 *      'Interessado' por meio de uma classe unificada 'Usuario' com papéis contextuais.
 *   3. Proteção e Minimização de Dados (LGPD): Mascaramento de CPF na camada de apresentação.
 *   4. Relacionamentos Estruturais: Modelagem de Categoria, Anuncio, Compra e HistoricoContato.
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModeloDominioDesapegaJa {

    // -------------------------------------------------------------------------
    // 1. Abstração de Localização (Geolocalização simplificada para comércio hiperlocal)
    // -------------------------------------------------------------------------
    public static class Localizacao {
        private final String cidade;
        private final String estado;
        private final String bairro;

        public Localizacao(String cidade, String estado, String bairro) {
            this.cidade = cidade;
            this.estado = estado;
            this.bairro = bairro;
        }

        public String getCidade() { return cidade; }
        public String getEstado() { return estado; }
        public String getBairro() { return bairro; }

        @Override
        public String toString() {
            return bairro + " - " + cidade + "/" + estado;
        }
    }

    // -------------------------------------------------------------------------
    // 2. Abstração de Categorias de Itens (Classificação dos anúncios)
    // -------------------------------------------------------------------------
    public enum CategoriaItem {
        ROUPAS("Roupas e Acessórios"),
        ELETRONICOS("Eletrônicos e Informática"),
        MOVEIS("Móveis e Decoração"),
        LIVROS("Livros e Revistas"),
        OUTROS("Outros Itens");

        private final String descricao;

        CategoriaItem(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // -------------------------------------------------------------------------
    // 3. Abstração Unificada de Usuário (Resolve o dilema Anunciante vs. Interessado)
    // -------------------------------------------------------------------------
    public static class Usuario {
        private final int id;
        private final String nomeCompleto;
        private final String email;
        private final String telefoneWhatsapp;
        private final String senhaHash;
        private final Localizacao localizacao;
        private final String cpf;
        private final LocalDate dataNascimento;
        private final String fotoPerfilUrl;
        private final LocalDateTime dataCadastro;
        private final List<Integer> avaliacoesRecebidas;

        public Usuario(int id, String nomeCompleto, String email, String telefoneWhatsapp,
                       String senhaHash, Localizacao localizacao, String cpf,
                       LocalDate dataNascimento, String fotoPerfilUrl) {
            this.id = id;
            this.nomeCompleto = nomeCompleto;
            this.email = email;
            this.telefoneWhatsapp = telefoneWhatsapp;
            this.senhaHash = senhaHash;
            this.localizacao = localizacao;
            this.cpf = cpf;
            this.dataNascimento = dataNascimento;
            this.fotoPerfilUrl = fotoPerfilUrl;
            this.dataCadastro = LocalDateTime.now();
            this.avaliacoesRecebidas = new ArrayList<>();
        }

        public int getId() { return id; }
        public String getNomeCompleto() { return nomeCompleto; }
        public String getEmail() { return email; }
        public String getTelefoneWhatsapp() { return telefoneWhatsapp; }
        public Localizacao getLocalizacao() { return localizacao; }
        public LocalDate getDataNascimento() { return dataNascimento; }
        public LocalDateTime getDataCadastro() { return dataCadastro; }

        // Conformidade com LGPD: Nunca expor o CPF completo publicamente
        public String getCpfMascarado() {
            String limpo = cpf.replaceAll("[^0-9]", "");
            if (limpo.length() == 11) {
                return "***." + limpo.substring(3, 6) + "." + limpo.substring(6, 9) + "-**";
            }
            return "***.***.***-**";
        }

        public void adicionarAvaliacao(int nota) {
            if (nota < 1 || nota > 5) {
                throw new IllegalArgumentException("A nota de avaliação deve estar entre 1 e 5.");
            }
            avaliacoesRecebidas.add(nota);
        }

        // Automação de reputação mencionada na narrativa do estudo de caso
        public double getReputacaoMedia() {
            if (avaliacoesRecebidas.isEmpty()) {
                return 5.0; // Pontuação inicial neutra para novos cadastros
            }
            int soma = 0;
            for (int nota : avaliacoesRecebidas) {
                soma += nota;
            }
            return (double) soma / avaliacoesRecebidas.size();
        }

        @Override
        public String toString() {
            return String.format("Usuario[ID=%d, Nome=%s, Local=%s, CPF=%s, Reputacao=%.1f★ (%d avaliações)]",
                    id, nomeCompleto, localizacao, getCpfMascarado(), getReputacaoMedia(), avaliacoesRecebidas.size());
        }
    }

    // -------------------------------------------------------------------------
    // 4. Abstração de Anúncio de Produto
    // -------------------------------------------------------------------------
    public static class Anuncio {
        public enum Status { ATIVO, PAUSADO, VENDIDO, CANCELADO }

        private final int id;
        private final int vendedorId; // Chave estrangeira conceitual para Usuario
        private final String titulo;
        private final String descricao;
        private final BigDecimal preco;
        private final CategoriaItem categoria;
        private final List<String> fotosUrls;
        private Status status;
        private final LocalDateTime dataCriacao;

        public Anuncio(int id, int vendedorId, String titulo, String descricao,
                       BigDecimal preco, CategoriaItem categoria, List<String> fotosUrls) {
            this.id = id;
            this.vendedorId = vendedorId;
            this.titulo = titulo;
            this.descricao = descricao;
            this.preco = preco;
            this.categoria = categoria;
            this.fotosUrls = new ArrayList<>(fotosUrls);
            this.status = Status.ATIVO;
            this.dataCriacao = LocalDateTime.now();
        }

        public int getId() { return id; }
        public int getVendedorId() { return vendedorId; }
        public String getTitulo() { return titulo; }
        public String getDescricao() { return descricao; }
        public BigDecimal getPreco() { return preco; }
        public CategoriaItem getCategoria() { return categoria; }
        public List<String> getFotosUrls() { return Collections.unmodifiableList(fotosUrls); }
        public Status getStatus() { return status; }

        public void pausar() { this.status = Status.PAUSADO; }
        public void reativar() { this.status = Status.ATIVO; }
        public void marcarComoVendido() { this.status = Status.VENDIDO; }
        public void cancelar() { this.status = Status.CANCELADO; }

        @Override
        public String toString() {
            return String.format("Anuncio[ID=%d, Titulo='%s', Categoria=%s, Preco=R$ %.2f, Status=%s, Fotos=%d]",
                    id, titulo, categoria.getDescricao(), preco, status, fotosUrls.size());
        }
    }

    // -------------------------------------------------------------------------
    // 5. Histórico de Contatos (Canal de intenção entre Comprador e Vendedor)
    // -------------------------------------------------------------------------
    public static class HistoricoContato {
        private final int id;
        private final int anuncioId;
        private final int compradorId; // Papel contextual de Interessado
        private final int vendedorId;  // Papel contextual de Anunciante
        private final LocalDateTime dataHora;
        private final String canalUtilizado;

        public HistoricoContato(int id, int anuncioId, int compradorId, int vendedorId, String canalUtilizado) {
            this.id = id;
            this.anuncioId = anuncioId;
            this.compradorId = compradorId;
            this.vendedorId = vendedorId;
            this.dataHora = LocalDateTime.now();
            this.canalUtilizado = canalUtilizado;
        }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return String.format("Contato #%d: Comprador ID %d contatou Vendedor ID %d sobre Anuncio #%d via %s em %s",
                    id, compradorId, vendedorId, anuncioId, canalUtilizado, dataHora.format(fmt));
        }
    }

    // -------------------------------------------------------------------------
    // 6. Transação de Compra e Fechamento de Negócio
    // -------------------------------------------------------------------------
    public static class Compra {
        private final int id;
        private final int anuncioId;
        private final int compradorId;
        private final int vendedorId;
        private final BigDecimal valorFinal;
        private final LocalDateTime dataCompra;
        private Integer notaAvaliacao;

        public Compra(int id, int anuncioId, int compradorId, int vendedorId, BigDecimal valorFinal) {
            this.id = id;
            this.anuncioId = anuncioId;
            this.compradorId = compradorId;
            this.vendedorId = vendedorId;
            this.valorFinal = valorFinal;
            this.dataCompra = LocalDateTime.now();
            this.notaAvaliacao = null;
        }

        public void registrarAvaliacao(int nota) {
            this.notaAvaliacao = nota;
        }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return String.format("Compra #%d [Anuncio #%d, Comprador: %d -> Vendedor: %d, Valor: R$ %.2f, Data: %s, Avaliação: %s]",
                    id, anuncioId, compradorId, vendedorId, valorFinal, dataCompra.format(fmt),
                    (notaAvaliacao != null ? notaAvaliacao + "★" : "Pendente"));
        }
    }

    // -------------------------------------------------------------------------
    // Execução e Demonstração Prática do Modelo
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE I - MODELO DE DOMÍNIO CONCEITUAL: DESAPEGA JÁ  ");
        System.out.println("========================================================================\n");

        // 1. Criação dos Usuários com Localização (Abstração Unificada)
        Localizacao locCarlos = new Localizacao("Fernandópolis", "SP", "Centro");
        Usuario carlos = new Usuario(
                101,
                "Carlos Eduardo Silva",
                "carlos.silva@email.com",
                "(17) 99876-5432",
                "$2a$12$e8Yk1.simulacaoHashBcrypt",
                locCarlos,
                "12345678909",
                LocalDate.of(1988, 7, 15),
                "https://storage.desapegaja.com/avatars/carlos.jpg"
        );

        Localizacao locAna = new Localizacao("Fernandópolis", "SP", "Brasilita");
        Usuario ana = new Usuario(
                102,
                "Ana Beatriz Moreira",
                "ana.moreira@email.com",
                "(17) 98111-2233",
                "$2a$12$k3Pz9.simulacaoHashBcrypt",
                locAna,
                "98765432100",
                LocalDate.of(1995, 3, 22),
                "https://storage.desapegaja.com/avatars/ana.jpg"
        );

        System.out.println("[1] Usuários cadastrados (Modelo com papéis contextuais - Sem duplicidade):");
        System.out.println("    " + carlos);
        System.out.println("    " + ana);
        System.out.println();

        // 2. Demonstração de Papel 1: Carlos atua como Vendedor (Anunciante)
        List<String> fotosSofa = List.of(
                "https://storage.desapegaja.com/anuncios/sofa_frente.jpg",
                "https://storage.desapegaja.com/anuncios/sofa_lateral.jpg"
        );
        Anuncio anuncioSofa = new Anuncio(
                501,
                carlos.getId(), // Carlos é o vendedor
                "Sofá 3 Lugares Retrátil Suede Marrom",
                "Sofá em ótimo estado de conservação, sem manchas ou rasgos. Motivo da venda: mudança de residência.",
                new BigDecimal("450.00"),
                CategoriaItem.MOVEIS,
                fotosSofa
        );

        // Demonstração de Papel 2: Ana atua como Vendedora (Anunciante)
        List<String> fotosLivro = List.of("https://storage.desapegaja.com/anuncios/livro_eng_software.jpg");
        Anuncio anuncioLivro = new Anuncio(
                502,
                ana.getId(), // Ana é a vendedora
                "Livro Engenharia de Software - Pressman 8ª Ed.",
                "Livro didático em excelente estado, sem grifos ou anotações.",
                new BigDecimal("120.00"),
                CategoriaItem.LIVROS,
                fotosLivro
        );

        System.out.println("[2] Anúncios publicados no catálogo local:");
        System.out.println("    " + anuncioSofa);
        System.out.println("    " + anuncioLivro);
        System.out.println();

        // 3. Demonstração de Papel Invertido: Ana se interessa pelo Sofá de Carlos (Ana é Interessada/Compradora)
        HistoricoContato contato1 = new HistoricoContato(
                9001,
                anuncioSofa.getId(),
                ana.getId(),    // Interessada (Compradora)
                carlos.getId(), // Anunciante (Vendedor)
                "WhatsApp"
        );

        System.out.println("[3] Registro de interesse e contato (RF-05 / RF-08):");
        System.out.println("    " + contato1);
        System.out.println();

        // 4. Fechamento de Negociação e Registro da Compra
        Compra compraRealizada = new Compra(
                7001,
                anuncioSofa.getId(),
                ana.getId(),
                carlos.getId(),
                new BigDecimal("420.00") // Negociado com desconto
        );
        anuncioSofa.marcarComoVendido();

        // Ana avalia Carlos como excelente vendedor com 5 estrelas
        compraRealizada.registrarAvaliacao(5);
        carlos.adicionarAvaliacao(5);

        System.out.println("[4] Negociação concluída e avaliação registrada:");
        System.out.println("    " + compraRealizada);
        System.out.println("    Estado atual do anúncio: " + anuncioSofa.getStatus());
        System.out.println("    Nova reputação do Carlos: " + carlos.getReputacaoMedia() + "★");
        System.out.println();

        // 5. Demonstração da Inversão de Papel: Carlos agora se interessa pelo Livro de Ana
        HistoricoContato contato2 = new HistoricoContato(
                9002,
                anuncioLivro.getId(),
                carlos.getId(), // Carlos agora atua como Interessado (Comprador)
                ana.getId(),    // Ana atua como Anunciante (Vendedora)
                "Chat Interno"
        );

        System.out.println("[5] Inversão de papéis no mesmo sistema (sem duplicar cadastros):");
        System.out.println("    " + contato2);
        System.out.println();

        System.out.println("========================================================================");
        System.out.println("  CONCLUSÃO DO PROCESSO DE ABSTRAÇÃO:");
        System.out.println("  A entidade 'Usuario' assumiu dinamicamente os papéis de Comprador e");
        System.out.println("  Vendedor sem a criação de tabelas redundantes, preservando histórico,");
        System.out.println("  avaliações e conformidade com a LGPD via CPF mascarado.");
        System.out.println("========================================================================");
    }
}
