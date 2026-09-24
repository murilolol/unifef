/*
 * Disciplina: Engenharia de Software I
 * Professor: Marcelo Boer (UniFEF)
 * Tema: Refinamento e Implementação de Requisitos Funcionais e Não-Funcionais (RF-01 a RF-08 e ISO 25010)
 *
 * Como compilar:
 *   javac ServicoNegociacaoRequisitos.java
 * Como executar:
 *   java ServicoNegociacaoRequisitos
 *
 * Conceitos da aula demonstrados neste arquivo:
 *   1. Decomposição de Metas em Requisitos Atômicos: Refinamento de declarações genéricas
 *      ('Facilitar a venda') em serviços de software comportamentais e verificáveis.
 *   2. RF-03 / RF-06: Cadastro unificado com validação oficial de CPF e hash de senha com salt.
 *   3. RF-02 / RF-07: Publicação com validação de fotos (mínimo 1, máximo 5) e preço positivo.
 *   4. RF-04: Busca com algoritmo de proximidade hiperlocal (Bairro > Cidade > Estado).
 *   5. RF-08: Geração de link parametrizado para contato direto via WhatsApp.
 *   6. RF-05: Histórico cronológico e reputação média das partes.
 *   7. Aferição de RNF-02 (Desempenho < 2.0s) e RNF-04 (Conformidade com LGPD).
 */

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ServicoNegociacaoRequisitos {

    // -------------------------------------------------------------------------
    // 1. Validador Oficial de CPF (Regra de integridade e segurança cadastral)
    // -------------------------------------------------------------------------
    public static class ValidadorDocumento {
        public static boolean isCpfValido(String cpf) {
            if (cpf == null) return false;
            String limpo = cpf.replaceAll("[^0-9]", "");
            if (limpo.length() != 11) return false;

            // Rejeita sequências de dígitos repetidos conhecidas (ex: 111.111.111-11)
            if (limpo.chars().distinct().count() == 1) return false;

            try {
                // 1º Dígito Verificador
                int soma1 = 0;
                for (int i = 0; i < 9; i++) {
                    soma1 += (limpo.charAt(i) - '0') * (10 - i);
                }
                int resto1 = soma1 % 11;
                int digito1 = (resto1 < 2) ? 0 : (11 - resto1);
                if (digito1 != (limpo.charAt(9) - '0')) return false;

                // 2º Dígito Verificador
                int soma2 = 0;
                for (int i = 0; i < 10; i++) {
                    soma2 += (limpo.charAt(i) - '0') * (11 - i);
                }
                int resto2 = soma2 % 11;
                int digito2 = (resto2 < 2) ? 0 : (11 - resto2);
                return digito2 == (limpo.charAt(10) - '0');
            } catch (Exception e) {
                return false;
            }
        }

        public static String mascararCpf(String cpf) {
            String limpo = cpf.replaceAll("[^0-9]", "");
            if (limpo.length() == 11) {
                return "***." + limpo.substring(3, 6) + "." + limpo.substring(6, 9) + "-**";
            }
            return "***.***.***-**";
        }
    }

    // -------------------------------------------------------------------------
    // 2. Criptografia de Senha (RNF-03: Segurança de credenciais)
    // -------------------------------------------------------------------------
    public static class SegurancaAutenticacao {
        public static String gerarHashSenha(String senhaPura, String salt) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt.getBytes(StandardCharsets.UTF_8));
                byte[] bytes = md.digest(senhaPura.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Algoritmo criptográfico SHA-256 indisponível.", e);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 3. Estruturas de Dados do Domínio
    // -------------------------------------------------------------------------
    public static class Localizacao {
        public final String estado;
        public final String cidade;
        public final String bairro;

        public Localizacao(String estado, String cidade, String bairro) {
            this.estado = estado.toUpperCase();
            this.cidade = cidade;
            this.bairro = bairro;
        }
    }

    public static class UsuarioDTO {
        public final int id;
        public final String nomeCompleto;
        public final String email;
        public final String whatsapp;
        public final String cpf;
        public final Localizacao localizacao;
        public final String senhaHash;
        public final String salt;
        public final List<Integer> avaliacoesRecebidas;

        public UsuarioDTO(int id, String nomeCompleto, String email, String whatsapp,
                          String cpf, Localizacao localizacao, String senhaHash, String salt) {
            this.id = id;
            this.nomeCompleto = nomeCompleto;
            this.email = email;
            this.whatsapp = whatsapp;
            this.cpf = cpf;
            this.localizacao = localizacao;
            this.senhaHash = senhaHash;
            this.salt = salt;
            this.avaliacoesRecebidas = new ArrayList<>();
        }

        public double getMediaAvaliacoes() {
            if (avaliacoesRecebidas.isEmpty()) return 5.0;
            int s = 0;
            for (int n : avaliacoesRecebidas) s += n;
            return (double) s / avaliacoesRecebidas.size();
        }
    }

    public static class AnuncioDTO {
        public final int id;
        public final int anuncianteId;
        public final String titulo;
        public final String descricao;
        public final BigDecimal preco;
        public final String categoria;
        public final List<String> fotos;
        public final Localizacao localizacao;
        public boolean ativo;

        public AnuncioDTO(int id, int anuncianteId, String titulo, String descricao,
                          BigDecimal preco, String categoria, List<String> fotos, Localizacao localizacao) {
            this.id = id;
            this.anuncianteId = anuncianteId;
            this.titulo = titulo;
            this.descricao = descricao;
            this.preco = preco;
            this.categoria = categoria;
            this.fotos = new ArrayList<>(fotos);
            this.localizacao = localizacao;
            this.ativo = true;
        }
    }

    public static class ContatoRegistro {
        public final int id;
        public final int anuncioId;
        public final int interessadoId;
        public final int anuncianteId;
        public final LocalDateTime dataHora;
        public final String urlWhatsAppGerada;

        public ContatoRegistro(int id, int anuncioId, int interessadoId, int anuncianteId, String urlWhatsAppGerada) {
            this.id = id;
            this.anuncioId = anuncioId;
            this.interessadoId = interessadoId;
            this.anuncianteId = anuncianteId;
            this.dataHora = LocalDateTime.now();
            this.urlWhatsAppGerada = urlWhatsAppGerada;
        }
    }

    public static class CompraRegistro {
        public final int id;
        public final int anuncioId;
        public final int compradorId;
        public final int vendedorId;
        public final BigDecimal valorPago;
        public final LocalDateTime data;
        public int notaAvaliacao;

        public CompraRegistro(int id, int anuncioId, int compradorId, int vendedorId, BigDecimal valorPago, int notaAvaliacao) {
            this.id = id;
            this.anuncioId = anuncioId;
            this.compradorId = compradorId;
            this.vendedorId = vendedorId;
            this.valorPago = valorPago;
            this.data = LocalDateTime.now();
            this.notaAvaliacao = notaAvaliacao;
        }
    }

    // -------------------------------------------------------------------------
    // 4. Serviço Central do Aplicativo Desapega Já
    // -------------------------------------------------------------------------
    public static class PlataformaDesapegaJaServico {
        private final Map<Integer, UsuarioDTO> usuarios = new HashMap<>();
        private final Map<Integer, AnuncioDTO> anuncios = new HashMap<>();
        private final List<ContatoRegistro> historicoContatos = new ArrayList<>();
        private final List<CompraRegistro> historicoCompras = new ArrayList<>();
        private int proximoUsuarioId = 1;
        private int proximoAnuncioId = 1;
        private int proximoContatoId = 1;
        private int proximaCompraId = 1;

        // Refinamento RF-03 / RF-06: Cadastro unificado de usuários com validações rigorosas
        public UsuarioDTO cadastrarUsuario(String nome, String email, String whatsapp, String senhaPura,
                                          String cpf, Localizacao localizacao) {
            if (nome == null || nome.trim().length() < 3) {
                throw new IllegalArgumentException("Nome completo deve possuir ao menos 3 caracteres.");
            }
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Formato de e-mail inválido.");
            }
            if (!ValidadorDocumento.isCpfValido(cpf)) {
                throw new IllegalArgumentException("CPF inválido perante os dígitos verificadores da Receita Federal: " + cpf);
            }
            if (senhaPura == null || senhaPura.length() < 6) {
                throw new IllegalArgumentException("A senha deve conter no mínimo 6 caracteres.");
            }

            String salt = UUID.randomUUID().toString().substring(0, 8);
            String senhaHash = SegurancaAutenticacao.gerarHashSenha(senhaPura, salt);

            UsuarioDTO novo = new UsuarioDTO(proximoUsuarioId++, nome, email, whatsapp, cpf, localizacao, senhaHash, salt);
            usuarios.put(novo.id, novo);
            return novo;
        }

        // Refinamento RF-02 / RF-07: Publicar anúncio com verificação de fotos e preço
        public AnuncioDTO publicarAnuncio(int anuncianteId, String titulo, String descricao,
                                         BigDecimal preco, String categoria, List<String> fotos) {
            UsuarioDTO anunciante = usuarios.get(anuncianteId);
            if (anunciante == null) {
                throw new IllegalArgumentException("Usuário anunciante não encontrado.");
            }
            if (titulo == null || titulo.trim().length() < 5) {
                throw new IllegalArgumentException("O título do anúncio deve ter ao menos 5 caracteres.");
            }
            if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O preço deve ser superior a zero.");
            }
            if (fotos == null || fotos.isEmpty() || fotos.size() > 5) {
                throw new IllegalArgumentException("O anúncio deve possuir entre 1 e 5 fotografias (Critério RF-02).");
            }

            AnuncioDTO novo = new AnuncioDTO(proximoAnuncioId++, anuncianteId, titulo, descricao, preco, categoria, fotos, anunciante.localizacao);
            anuncios.put(novo.id, novo);
            return novo;
        }

        // Refinamento RF-04: Busca por proximidade geográfica hiperlocal com ranking e medição de tempo (RNF-02)
        public List<AnuncioDTO> buscarPorProximidade(String estado, String cidade, String bairroFiltro) {
            long inicioNano = System.nanoTime();

            List<AnuncioDTO> todosAtivos = anuncios.values().stream()
                    .filter(a -> a.ativo)
                    .filter(a -> a.localizacao.estado.equalsIgnoreCase(estado))
                    .filter(a -> a.localizacao.cidade.equalsIgnoreCase(cidade))
                    .collect(Collectors.toList());

            // Algoritmo de ranking hiperlocal: prioriza o mesmo bairro do usuário
            todosAtivos.sort((a1, a2) -> {
                boolean a1MesmoBairro = a1.localizacao.bairro.equalsIgnoreCase(bairroFiltro);
                boolean a2MesmoBairro = a2.localizacao.bairro.equalsIgnoreCase(bairroFiltro);
                if (a1MesmoBairro && !a2MesmoBairro) return -1;
                if (!a1MesmoBairro && a2MesmoBairro) return 1;
                return a1.titulo.compareToIgnoreCase(a2.titulo);
            });

            long fimNano = System.nanoTime();
            double duracaoMs = (fimNano - inicioNano) / 1_000_000.0;
            System.out.printf("   [RNF-02 Desempenho] Consulta de catálogo executada em %.3f ms (Critério de aceitação: < 2000 ms)%n", duracaoMs);

            return todosAtivos;
        }

        // Refinamento RF-08: Geração do link oficial de contato via WhatsApp com mensagem pré-formatada
        public ContatoRegistro iniciarContatoWhatsApp(int interessadoId, int anuncioId) {
            UsuarioDTO interessado = usuarios.get(interessadoId);
            AnuncioDTO anuncio = anuncios.get(anuncioId);
            if (interessado == null || anuncio == null) {
                throw new IllegalArgumentException("Interessado ou Anúncio inválido.");
            }
            UsuarioDTO anunciante = usuarios.get(anuncio.anuncianteId);

            String telefoneLimpo = anunciante.whatsapp.replaceAll("[^0-9]", "");
            String mensagemTexto = String.format(
                    "Olá %s, vi seu anúncio '%s' no Desapega Já por R$ %.2f. Tenho interesse em negociar! (Sou %s do bairro %s)",
                    anunciante.nomeCompleto, anuncio.titulo, anuncio.preco, interessado.nomeCompleto, interessado.localizacao.bairro
            );

            String urlParametrizada = "https://wa.me/55" + telefoneLimpo + "?text=" +
                    URLEncoder.encode(mensagemTexto, StandardCharsets.UTF_8);

            ContatoRegistro registro = new ContatoRegistro(proximoContatoId++, anuncioId, interessadoId, anunciante.id, urlParametrizada);
            historicoContatos.add(registro);
            return registro;
        }

        // Refinamento RF-05: Conclusão da venda e registro de avaliação mútua
        public CompraRegistro concluirTransacao(int compradorId, int anuncioId, BigDecimal valorFinal, int notaAvaliacao) {
            AnuncioDTO anuncio = anuncios.get(anuncioId);
            if (anuncio == null || !anuncio.ativo) {
                throw new IllegalStateException("Anúncio não disponível para finalização.");
            }
            UsuarioDTO vendedor = usuarios.get(anuncio.anuncianteId);
            if (vendedor == null) throw new IllegalArgumentException("Vendedor inexistente.");

            vendedor.avaliacoesRecebidas.add(notaAvaliacao);
            anuncio.ativo = false; // Desativa o anúncio do catálogo

            CompraRegistro compra = new CompraRegistro(proximaCompraId++, anuncioId, compradorId, vendedor.id, valorFinal, notaAvaliacao);
            historicoCompras.add(compra);
            return compra;
        }

        // RF-05: Emissão do histórico consolidado do usuário
        public void exibirPainelHistoricoUsuario(int usuarioId) {
            UsuarioDTO u = usuarios.get(usuarioId);
            if (u == null) return;

            System.out.println("========================================================================");
            System.out.println("  PAINEL DO USUÁRIO (RF-05 / Conformidade LGPD)");
            System.out.printf("  Nome: %s | Reputação: %.1f★ (%d avaliações)%n", u.nomeCompleto, u.getMediaAvaliacoes(), u.avaliacoesRecebidas.size());
            System.out.printf("  Local: %s - %s/%s%n", u.localizacao.bairro, u.localizacao.cidade, u.localizacao.estado);
            System.out.printf("  Documento Protegido (LGPD): %s%n", ValidadorDocumento.mascararCpf(u.cpf));
            System.out.printf("  Hash de Senha Seguro: %s... (Salt: %s)%n", u.senhaHash.substring(0, 16), u.salt);
            System.out.println("------------------------------------------------------------------------");
            System.out.println("  Contatos que você iniciou:");
            historicoContatos.stream()
                    .filter(c -> c.interessadoId == usuarioId)
                    .forEach(c -> System.out.printf("   * Anúncio #%d em %s (WhatsApp: %s)%n",
                            c.anuncioId, c.dataHora.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")), c.urlWhatsAppGerada));

            System.out.println("  Compras que você realizou:");
            historicoCompras.stream()
                    .filter(c -> c.compradorId == usuarioId)
                    .forEach(c -> System.out.printf("   * Compra #%d | Anúncio #%d | Pago: R$ %.2f | Nota atribuída: %d★%n",
                            c.id, c.anuncioId, c.valorPago, c.notaAvaliacao));

            System.out.println("  Vendas que você realizou:");
            historicoCompras.stream()
                    .filter(c -> c.vendedorId == usuarioId)
                    .forEach(c -> System.out.printf("   * Venda #%d | Anúncio #%d | Recebido: R$ %.2f | Avaliação recebida: %d★%n",
                            c.id, c.anuncioId, c.valorPago, c.notaAvaliacao));
            System.out.println("========================================================================\n");
        }
    }

    // -------------------------------------------------------------------------
    // Execução e Verificação de Todos os Requisitos em Cenário Real
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE I - REFINAMENTO DE REQUISITOS (RF-01 A RF-08)  ");
        System.out.println("========================================================================\n");

        PlataformaDesapegaJaServico plataforma = new PlataformaDesapegaJaServico();

        // 1. Verificação do RF-03 / RF-06 com validação de CPF
        System.out.println("[TESTE 1] Tentativa de cadastro com CPF inválido (Dígito verificador incorreto):");
        try {
            plataforma.cadastrarUsuario(
                    "Usuario Teste Falso",
                    "falso@email.com",
                    "17999990000",
                    "senha123",
                    "12345678900", // CPF com dígito inválido
                    new Localizacao("SP", "Fernandópolis", "Centro")
            );
        } catch (IllegalArgumentException e) {
            System.out.println("   [SUCESSO NO BLOQUEIO] " + e.getMessage());
        }

        // Cadastros legítimos com CPFs matematicamente válidos
        UsuarioDTO carlos = plataforma.cadastrarUsuario(
                "Carlos Eduardo Silva",
                "carlos@email.com",
                "17998765432",
                "senhaForte@2026",
                "12345678909", // CPF Válido
                new Localizacao("SP", "Fernandópolis", "Centro")
        );

        UsuarioDTO ana = plataforma.cadastrarUsuario(
                "Ana Beatriz Moreira",
                "ana@email.com",
                "17981112233",
                "outraSenhaSegura#1",
                "98765432100", // CPF Válido
                new Localizacao("SP", "Fernandópolis", "Brasilita")
        );

        UsuarioDTO marcos = plataforma.cadastrarUsuario(
                "Marcos Vinicius Santos",
                "marcos@email.com",
                "17977778888",
                "marcos@2026!",
                "11144477735", // CPF Válido
                new Localizacao("SP", "Fernandópolis", "Centro")
        );
        System.out.println("   [SUCESSO] 3 Usuários cadastrados com validação de CPF e hash de senha com salt.\n");

        // 2. Verificação do RF-02 / RF-07 (Publicação com fotos e categorias)
        System.out.println("[TESTE 2] Publicação de anúncios com critérios de fotos e preço:");
        AnuncioDTO a1 = plataforma.publicarAnuncio(
                carlos.id,
                "Mesa de Jantar 6 Cadeiras Madeira Maciça",
                "Mesa em excelente estado, acompanha 6 cadeiras estofadas.",
                new BigDecimal("600.00"),
                "Móveis",
                List.of("https://img.desapegaja.com/mesa1.jpg", "https://img.desapegaja.com/mesa2.jpg")
        );

        AnuncioDTO a2 = plataforma.publicarAnuncio(
                ana.id,
                "Micro-ondas 30L Prata Espelhado",
                "Aparelho 110V funcionando perfeitamente, pouquíssimo uso.",
                new BigDecimal("250.00"),
                "Eletrônicos",
                List.of("https://img.desapegaja.com/microondas.jpg")
        );

        AnuncioDTO a3 = plataforma.publicarAnuncio(
                marcos.id,
                "Armário Aéreo de Cozinha 3 Portas",
                "MDF branco, ótimo acabamento para cozinha planejada.",
                new BigDecimal("180.00"),
                "Móveis",
                List.of("https://img.desapegaja.com/armario.jpg")
        );
        System.out.println("   [SUCESSO] 3 Anúncios publicados nos bairros 'Centro' e 'Brasilita'.\n");

        // 3. Verificação do RF-04 (Busca por proximidade hiperlocal centrada no bairro do interessado)
        System.out.println("[TESTE 3] Carlos (residente no 'Centro') busca produtos em Fernandópolis/SP:");
        List<AnuncioDTO> resultados = plataforma.buscarPorProximidade("SP", "Fernandópolis", "Centro");
        System.out.println("   Resultados ordenados por prioridade hiperlocal:");
        for (AnuncioDTO res : resultados) {
            boolean ehMesmoBairro = res.localizacao.bairro.equalsIgnoreCase("Centro");
            System.out.printf("    -> [%s] %s | R$ %.2f | Vendedor ID: %d (%s)%n",
                    (ehMesmoBairro ? "PRIORIDADE: MESMO BAIRRO" : "MESMA CIDADE"),
                    res.titulo, res.preco, res.anuncianteId, res.localizacao.bairro);
        }
        System.out.println();

        // 4. Verificação do RF-08 (Geração de link WhatsApp contextualizado)
        System.out.println("[TESTE 4] Carlos se interessa pelo Micro-ondas de Ana e clica no botão de contato:");
        ContatoRegistro contato = plataforma.iniciarContatoWhatsApp(carlos.id, a2.id);
        System.out.println("   Link do WhatsApp gerado pelo sistema:");
        System.out.println("   " + contato.urlWhatsAppGerada);
        System.out.println();

        // 5. Verificação do RF-05 (Conclusão e Histórico)
        System.out.println("[TESTE 5] Negociação fechada! Registro de compra e avaliação 5 estrelas para Ana:");
        CompraRegistro compra = plataforma.concluirTransacao(carlos.id, a2.id, new BigDecimal("230.00"), 5);
        System.out.printf("   Transação #%d consolidada com sucesso no valor de R$ %.2f!%n%n", compra.id, compra.valorPago);

        // Exibição dos painéis individuais de histórico
        plataforma.exibirPainelHistoricoUsuario(carlos.id);
        plataforma.exibirPainelHistoricoUsuario(ana.id);
    }
}
