/**
 * Disciplina: Engenharia de Software I - 3º Semestre
 * Professor: Marcelo Boer
 * Instituição: UniFEF (Centro Universitário de Santa Fé do Sul)
 * Tema: Aula 04 — Abstração e Modelagem Preliminar de Domínio (Projeto Desapega Já)
 *
 * Como compilar:
 *   javac ModeloDominioDesapegaJa.java
 *
 * Como executar:
 *   java ModeloDominioDesapegaJa
 *
 * CONCEITOS IMPLEMENTADOS NESTE ARQUIVO:
 * 1. Processo de Abstração: isolamento cirúrgico de propriedades relevantes ao negócio
 *    (nome, CPF, bairro, fotos, preço) e descarte do ruído irrelevante (altura, hobby, tipo sanguíneo).
 * 2. Eliminação de Redundância via Herança (DRY - Don't Repeat Yourself):
 *    Unificação das entidades 'Anunciante' e 'Interessado' através da superclasse abstrata 'Usuario'.
 * 3. Separação de Responsabilidades de Negócio:
 *    Diferenciação clara entre 'Produto' (bem físico catalogado) e 'Anuncio' (oferta comercial ativa).
 * 4. Rastreabilidade de Regras:
 *    Validação de preços não negativos, integridade de status e transação de compra.
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ModeloDominioDesapegaJa {

    // =========================================================================
    // 1. ABSTRAÇÃO DO DOMÍNIO: SUPERCLASSE ABSTRATA DE USUÁRIO
    // =========================================================================
    /**
     * Superclasse abstrata que materializa o resultado da análise de redundância.
     * Em sala de aula, identificou-se que Anunciante e Interessado compartilhavam
     * exatamente os mesmos atributos cadastrais (nome, email, cpf, bairro, etc).
     * A herança elimina duplicidade estrutural e permite papéis polimórficos.
     */
    public static abstract class Usuario {
        private final UUID id;
        private String nomeCompleto;
        private String email;
        private String senhaHash;
        private String telefoneWhatsapp;
        private String cpf;
        private LocalDate dataNascimento;
        private String cidade;
        private String estado;
        private String bairro;
        private String fotoPerfilUrl;
        private final LocalDateTime dataCadastro;

        public Usuario(String nomeCompleto, String email, String senhaHash,
                       String telefoneWhatsapp, String cpf, LocalDate dataNascimento,
                       String cidade, String estado, String bairro, String fotoPerfilUrl) {
            this.id = UUID.randomUUID();
            this.nomeCompleto = Objects.requireNonNull(nomeCompleto, "Nome é obrigatório");
            this.email = Objects.requireNonNull(email, "E-mail é obrigatório");
            this.senhaHash = Objects.requireNonNull(senhaHash, "Hash de senha é obrigatório");
            this.telefoneWhatsapp = Objects.requireNonNull(telefoneWhatsapp, "Telefone é obrigatório");
            this.cpf = Objects.requireNonNull(cpf, "CPF é obrigatório");
            this.dataNascimento = Objects.requireNonNull(dataNascimento, "Data de nascimento é obrigatória");
            this.cidade = Objects.requireNonNull(cidade, "Cidade é obrigatória");
            this.estado = Objects.requireNonNull(estado, "Estado é obrigatório");
            this.bairro = Objects.requireNonNull(bairro, "Bairro é obrigatório para busca por proximidade");
            this.fotoPerfilUrl = fotoPerfilUrl;
            this.dataCadastro = LocalDateTime.now();
        }

        public UUID getId() { return id; }
        public String getNomeCompleto() { return nomeCompleto; }
        public String getEmail() { return email; }
        public String getTelefoneWhatsapp() { return telefoneWhatsapp; }
        public String getCpf() { return cpf; }
        public String getCidade() { return cidade; }
        public String getEstado() { return estado; }
        public String getBairro() { return bairro; }
        public LocalDateTime getDataCadastro() { return dataCadastro; }

        public String getLocalizacaoFormatada() {
            return String.format("%s, %s - %s", bairro, cidade, estado);
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (CPF: %s, Local: %s)",
                    getClass().getSimpleName(), nomeCompleto, cpf, getLocalizacaoFormatada());
        }
    }

    // =========================================================================
    // 2. ESPECIALIZAÇÃO DE PAPÉIS (ATORES PRIMÁRIOS DO SISTEMA)
    // =========================================================================
    /**
     * Ator Primário: Pessoa Anunciante.
     * Especialização que encapsula a responsabilidade de manter catálogo de produtos e publicar ofertas.
     */
    public static class Anunciante extends Usuario {
        private final List<Produto> produtosCadastrados = new ArrayList<>();
        private final List<Anuncio> anunciosPublicados = new ArrayList<>();
        private double avaliacaoMediaVendedor = 5.0;

        public Anunciante(String nomeCompleto, String email, String senhaHash,
                          String telefoneWhatsapp, String cpf, LocalDate dataNascimento,
                          String cidade, String estado, String bairro, String fotoPerfilUrl) {
            super(nomeCompleto, email, senhaHash, telefoneWhatsapp, cpf, dataNascimento, cidade, estado, bairro, fotoPerfilUrl);
        }

        public void cadastrarProduto(Produto produto) {
            Objects.requireNonNull(produto, "Produto não pode ser nulo");
            produtosCadastrados.add(produto);
        }

        public Anuncio publicarAnuncio(Produto produto, BigDecimal preco, List<String> fotos) {
            Objects.requireNonNull(produto, "Produto deve estar previamente cadastrado");
            if (!produtosCadastrados.contains(produto)) {
                throw new IllegalStateException("O anunciante só pode anunciar produtos do seu catálogo próprio.");
            }
            Anuncio anuncio = new Anuncio(this, produto, preco, fotos, this.getBairro());
            anunciosPublicados.add(anuncio);
            return anuncio;
        }

        public List<Produto> getProdutosCadastrados() {
            return Collections.unmodifiableList(produtosCadastrados);
        }

        public List<Anuncio> getAnunciosPublicados() {
            return Collections.unmodifiableList(anunciosPublicados);
        }

        public double getAvaliacaoMediaVendedor() { return avaliacaoMediaVendedor; }
    }

    /**
     * Ator Primário: Pessoa Cliente / Interessado.
     * Especialização que encapsula a busca, interesse de compra e histórico de transações.
     */
    public static class Interessado extends Usuario {
        private final List<Compra> historicoCompras = new ArrayList<>();

        public Interessado(String nomeCompleto, String email, String senhaHash,
                           String telefoneWhatsapp, String cpf, LocalDate dataNascimento,
                           String cidade, String estado, String bairro, String fotoPerfilUrl) {
            super(nomeCompleto, email, senhaHash, telefoneWhatsapp, cpf, dataNascimento, cidade, estado, bairro, fotoPerfilUrl);
        }

        public Compra realizarCompra(Anuncio anuncio) {
            Objects.requireNonNull(anuncio, "Anúncio não pode ser nulo");
            if (anuncio.getStatus() != StatusAnuncio.ATIVO) {
                throw new IllegalStateException("Não é possível comprar um anúncio com status: " + anuncio.getStatus());
            }
            Compra novaCompra = new Compra(anuncio, this);
            anuncio.marcarComoVendido();
            historicoCompras.add(novaCompra);
            return novaCompra;
        }

        public List<Compra> getHistoricoCompras() {
            return Collections.unmodifiableList(historicoCompras);
        }
    }

    // =========================================================================
    // 3. ENTIDADES DO CATÁLOGO E OFERTA COMERCIAL
    // =========================================================================
    public enum EstadoConservacao {
        NOVO, SEMINOVO, USADO
    }

    public static class Categoria {
        private final int id;
        private final String nome;

        public Categoria(int id, String nome) {
            this.id = id;
            this.nome = Objects.requireNonNull(nome, "Nome da categoria obrigatório");
        }

        public int getId() { return id; }
        public String getNome() { return nome; }

        @Override
        public String toString() { return nome; }
    }

    /**
     * Representa o item patrimonial do anunciante.
     * Decisão de Arquitetura: Produto é separado de Anúncio para permitir
     * que o usuário mantenha itens no acervo sem obrigatoriamente expô-los à venda ativa.
     */
    public static class Produto {
        private final UUID id;
        private final String titulo;
        private final String descricao;
        private final Categoria categoria;
        private final EstadoConservacao estadoConservacao;

        public Produto(String titulo, String descricao, Categoria categoria, EstadoConservacao estadoConservacao) {
            this.id = UUID.randomUUID();
            this.titulo = Objects.requireNonNull(titulo, "Título obrigatório");
            this.descricao = Objects.requireNonNull(descricao, "Descrição obrigatória");
            this.categoria = Objects.requireNonNull(categoria, "Categoria obrigatória");
            this.estadoConservacao = Objects.requireNonNull(estadoConservacao, "Estado de conservação obrigatório");
        }

        public UUID getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getDescricao() { return descricao; }
        public Categoria getCategoria() { return categoria; }
        public EstadoConservacao getEstadoConservacao() { return estadoConservacao; }

        @Override
        public String toString() {
            return String.format("Produto['%s', Categoria=%s, Estado=%s]", titulo, categoria, estadoConservacao);
        }
    }

    public enum StatusAnuncio {
        ATIVO, PAUSADO, VENDIDO
    }

    /**
     * Representa a oferta pública no marketplace do Desapega Já.
     */
    public static class Anuncio {
        private final UUID id;
        private final Anunciante anunciante;
        private final Produto produto;
        private BigDecimal preco;
        private final List<String> fotosUrls;
        private final String bairro;
        private StatusAnuncio status;
        private final LocalDateTime dataPublicacao;

        public Anuncio(Anunciante anunciante, Produto produto, BigDecimal preco,
                       List<String> fotosUrls, String bairro) {
            this.id = UUID.randomUUID();
            this.anunciante = Objects.requireNonNull(anunciante, "Anunciante obrigatório");
            this.produto = Objects.requireNonNull(produto, "Produto obrigatório");
            if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Preço do anúncio não pode ser nulo ou negativo.");
            }
            this.preco = preco;
            this.fotosUrls = new ArrayList<>(fotosUrls != null ? fotosUrls : Collections.emptyList());
            this.bairro = Objects.requireNonNull(bairro, "Bairro de localização obrigatório para busca por proximidade");
            this.status = StatusAnuncio.ATIVO;
            this.dataPublicacao = LocalDateTime.now();
        }

        public UUID getId() { return id; }
        public Anunciante getAnunciante() { return anunciante; }
        public Produto getProduto() { return produto; }
        public BigDecimal getPreco() { return preco; }
        public String getBairro() { return bairro; }
        public StatusAnuncio getStatus() { return status; }
        public LocalDateTime getDataPublicacao() { return dataPublicacao; }
        public List<String> getFotosUrls() { return Collections.unmodifiableList(fotosUrls); }

        public void pausar() {
            if (status == StatusAnuncio.VENDIDO) {
                throw new IllegalStateException("Anúncio já vendido não pode ser pausado.");
            }
            this.status = StatusAnuncio.PAUSADO;
        }

        public void ativar() {
            if (status == StatusAnuncio.VENDIDO) {
                throw new IllegalStateException("Anúncio já vendido não pode ser reativado.");
            }
            this.status = StatusAnuncio.ATIVO;
        }

        public void marcarComoVendido() {
            this.status = StatusAnuncio.VENDIDO;
        }

        @Override
        public String toString() {
            return String.format("Anúncio #%s: %s | Preço: R$ %.2f | Bairro: %s | Status: %s",
                    id.toString().substring(0, 8), produto.getTitulo(), preco, bairro, status);
        }
    }

    // =========================================================================
    // 4. ENTIDADE DE REGISTRO DE TRANSAÇÃO (COMPRA)
    // =========================================================================
    public static class Compra {
        private final UUID idCompra;
        private final Anuncio anuncio;
        private final Interessado comprador;
        private final BigDecimal valorTransacionado;
        private final LocalDateTime dataHora;

        public Compra(Anuncio anuncio, Interessado comprador) {
            this.idCompra = UUID.randomUUID();
            this.anuncio = Objects.requireNonNull(anuncio, "Anúncio obrigatório");
            this.comprador = Objects.requireNonNull(comprador, "Comprador obrigatório");
            this.valorTransacionado = anuncio.getPreco();
            this.dataHora = LocalDateTime.now();
        }

        public UUID getIdCompra() { return idCompra; }
        public Anuncio getAnuncio() { return anuncio; }
        public Interessado getComprador() { return comprador; }
        public BigDecimal getValorTransacionado() { return valorTransacionado; }
        public LocalDateTime getDataHora() { return dataHora; }

        @Override
        public String toString() {
            return String.format("Compra #%s efetuada por %s referente ao item '%s' por R$ %.2f em %s",
                    idCompra.toString().substring(0, 8), comprador.getNomeCompleto(),
                    anuncio.getProduto().getTitulo(), valorTransacionado, dataHora);
        }
    }

    // =========================================================================
    // MÉTODO PRINCIPAL: DEMONSTRAÇÃO PRÁTICA DA ABSTRAÇÃO E RELACIONAMENTOS
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("  UNIFEF - ENGENHARIA DE SOFTWARE I - PROF. MARCELO BOER");
        System.out.println("  EXECUÇÃO: Abstração e Modelagem do Domínio (Desapega Já)");
        System.out.println("====================================================================\n");

        // Passo 1: Instanciação das Categorias de Negócio
        Categoria catLivros = new Categoria(1, "Livros e Material Didático");
        Categoria catMoveis = new Categoria(2, "Móveis e Decoração");
        System.out.println("[DOMÍNIO] Categorias criadas: " + catLivros + ", " + catMoveis);

        // Passo 2: Demonstração da Abstração e Generalização de Usuários
        // Note: Atributos físicos irrelevantes (cor dos olhos, altura) foram abstraídos/ignorados.
        Anunciante vendedor = new Anunciante(
                "Carlos Eduardo Silva",
                "carlos.vendas@email.com",
                "hash_argon2_seguro_123",
                "(17) 99881-2233",
                "123.456.789-00",
                LocalDate.of(1992, 5, 14),
                "Santa Fé do Sul",
                "SP",
                "Centro",
                "https://cdn.desapegaja.com/fotos/perfil_carlos.jpg"
        );

        Interessado comprador = new Interessado(
                "Beatriz Helena Santos",
                "beatriz.leitora@email.com",
                "hash_argon2_seguro_456",
                "(17) 99112-4455",
                "987.654.321-11",
                LocalDate.of(2001, 8, 22),
                "Santa Fé do Sul",
                "SP",
                "Jardim Europa",
                "https://cdn.desapegaja.com/fotos/perfil_beatriz.jpg"
        );

        System.out.println("\n[USUÁRIOS MODELADOS (HERANÇA DE USUARIO)]:");
        System.out.println("  -> " + vendedor);
        System.out.println("  -> " + comprador);

        // Passo 3: Cadastro de Produtos no Acervo Pessoal do Anunciante
        Produto livroEngSoft = new Produto(
                "Livro Engenharia de Software: Uma Abordagem Profissional (Pressman)",
                "Livro em ótimo estado de conservação, sem rasuras ou anotações.",
                catLivros,
                EstadoConservacao.SEMINOVO
        );
        vendedor.cadastrarProduto(livroEngSoft);
        System.out.println("\n[CATÁLOGO] Produto registrado pelo anunciante: " + livroEngSoft.getTitulo());

        // Passo 4: Publicação do Anúncio vinculado ao Produto (RF02 e RF07)
        List<String> fotos = List.of(
                "https://cdn.desapegaja.com/anuncios/pressman_capa.jpg",
                "https://cdn.desapegaja.com/anuncios/pressman_paginas.jpg"
        );
        Anuncio anuncioLivro = vendedor.publicarAnuncio(livroEngSoft, new BigDecimal("120.00"), fotos);
        System.out.println("[OFERTA PUBLICADA] " + anuncioLivro);

        // Passo 5: Verificação de Validação de Domínio (Contraexemplo: Preço Negativo)
        try {
            System.out.println("\n[TESTE DE REGRA] Tentando criar anúncio com preço inválido (-15.00)...");
            new Anuncio(vendedor, livroEngSoft, new BigDecimal("-15.00"), fotos, "Centro");
        } catch (IllegalArgumentException ex) {
            System.out.println("  [SUCESSO NA PROTEÇÃO DE REGRA] Exceção capturada: " + ex.getMessage());
        }

        // Passo 6: Realização da Compra pelo Interessado (RF05)
        System.out.println("\n[TRANSAÇÃO DE COMPRA] Interessada '" + comprador.getNomeCompleto() +
                "' finalizando compra do anúncio...");
        Compra transacao = comprador.realizarCompra(anuncioLivro);
        System.out.println("  -> " + transacao);
        System.out.println("  -> Novo status do anúncio: " + anuncioLivro.getStatus());

        // Passo 7: Tentativa de Compra Duplicada de Item Já Vendido (Contraexemplo de Estado)
        try {
            System.out.println("\n[TESTE DE CONSISTÊNCIA] Tentativa de comprar novamente anúncio VENDIDO...");
            comprador.realizarCompra(anuncioLivro);
        } catch (IllegalStateException ex) {
            System.out.println("  [SUCESSO NA TRANSAÇÃO ACID] Bloqueio efetuado: " + ex.getMessage());
        }

        System.out.println("\n>>> MODELO DE DOMÍNIO VALIDADO CONFORME AS ESPECIFICAÇÕES DA AULA 04 <<<");
    }
}
