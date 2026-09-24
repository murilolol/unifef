/**
 * Disciplina: Engenharia de Software I - 3º Semestre
 * Professor: Marcelo Boer
 * Instituição: UniFEF (Centro Universitário de Santa Fé do Sul)
 * Tema: Aula 04 — Caso de Uso de Mensageria (DCU15) e Busca por Proximidade (RF04/RF14)
 *
 * Como compilar:
 *   javac CasoDeUsoMensageriaBusca.java
 *
 * Como executar:
 *   java CasoDeUsoMensageriaBusca
 *
 * CONCEITOS IMPLEMENTADOS NESTE ARQUIVO:
 * 1. DCU15 — Trocar Mensagens Anunciante e Cliente:
 *    - Fluxo Normal: envio com sucesso, persistência com status 'ENVIADA',
 *      disparo de push notification simulado e confirmação de leitura ('LIDA').
 *    - Exceção 5.1: Falha de conexão de rede móvel (armazenamento em buffer offline para retry).
 *    - Exceção 6.1: Remetente bloqueado pelo destinatário (moderação de segurança).
 *    - Exceção 6.2: Anúncio pausado ou vendido durante a tentativa de envio.
 * 2. RF04 / RF14 — Busca de Anúncios por Proximidade:
 *    - Filtragem por localidade (Cidade, Bairro) e palavras-chave.
 * 3. Matriz de Rastreabilidade:
 *    - Entradas ('dados_mensagens', 'dados_anuncio') e Saídas ('Dados Mensagens', 'Msg04').
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CasoDeUsoMensageriaBusca {

    // =========================================================================
    // ESTRUTURAS DE DOMÍNIO ESPECÍFICAS DESTE CASO DE USO
    // =========================================================================
    public enum StatusMensagem {
        PENDENTE_OFFLINE, ENVIADA, ENTREGUE, LIDA
    }

    public static class MensagemContato {
        private final UUID id;
        private final UUID idAnuncio;
        private final UUID idRemetente;
        private final UUID idDestinatario;
        private final String textoConteudo;
        private final LocalDateTime dataHora;
        private StatusMensagem status;

        public MensagemContato(UUID idAnuncio, UUID idRemetente, UUID idDestinatario, String textoConteudo) {
            this.id = UUID.randomUUID();
            this.idAnuncio = idAnuncio;
            this.idRemetente = idRemetente;
            this.idDestinatario = idDestinatario;
            this.textoConteudo = textoConteudo;
            this.dataHora = LocalDateTime.now();
            this.status = StatusMensagem.ENVIADA;
        }

        public UUID getId() { return id; }
        public UUID getIdAnuncio() { return idAnuncio; }
        public UUID getIdRemetente() { return idRemetente; }
        public UUID getIdDestinatario() { return idDestinatario; }
        public String getTextoConteudo() { return textoConteudo; }
        public LocalDateTime getDataHora() { return dataHora; }
        public StatusMensagem getStatus() { return status; }
        public void setStatus(StatusMensagem status) { this.status = status; }

        @Override
        public String toString() {
            return String.format("[%s] Em %s: '%s' (Status: %s)",
                    id.toString().substring(0, 8),
                    dataHora.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    textoConteudo, status);
        }
    }

    public enum EstadoPublicacao {
        ATIVO, PAUSADO, VENDIDO
    }

    public static class ItemAnuncio {
        private final UUID id;
        private final UUID idAnunciante;
        private final String titulo;
        private final String categoria;
        private final BigDecimal preco;
        private final String cidade;
        private final String bairro;
        private EstadoPublicacao estado;

        public ItemAnuncio(UUID idAnunciante, String titulo, String categoria, BigDecimal preco, String cidade, String bairro) {
            this.id = UUID.randomUUID();
            this.idAnunciante = idAnunciante;
            this.titulo = titulo;
            this.categoria = categoria;
            this.preco = preco;
            this.cidade = cidade;
            this.bairro = bairro;
            this.estado = EstadoPublicacao.ATIVO;
        }

        public UUID getId() { return id; }
        public UUID getIdAnunciante() { return idAnunciante; }
        public String getTitulo() { return titulo; }
        public String getCategoria() { return categoria; }
        public BigDecimal getPreco() { return preco; }
        public String getCidade() { return cidade; }
        public String getBairro() { return bairro; }
        public EstadoPublicacao getEstado() { return estado; }
        public void setEstado(EstadoPublicacao estado) { this.estado = estado; }

        @Override
        public String toString() {
            return String.format("[%s] %s | R$ %.2f | %s (%s) | Status: %s",
                    id.toString().substring(0, 8), titulo, preco, bairro, cidade, estado);
        }
    }

    // =========================================================================
    // SISTEMA AUXILIAR: NOTIFICAÇÕES PUSH EXTERNAS
    // =========================================================================
    public static class ServicoPushNotification {
        public static void enviarNotificacao(UUID idDestinatario, String titulo, String corpo) {
            System.out.printf("   🔔 [PUSH NOTIFICATION para Usuário %s] %s: '%s'%n",
                    idDestinatario.toString().substring(0, 8), titulo, corpo);
        }
    }

    // =========================================================================
    // SERVIÇO PRINCIPAL: MOTOR DE MENSAGERIA E BUSCA POR PROXIMIDADE
    // =========================================================================
    public static class MotorDesapegaJa {
        private final List<ItemAnuncio> catalogoAnuncios = new ArrayList<>();
        private final List<MensagemContato> historicoMensagens = new ArrayList<>();
        private final List<MensagemContato> bufferFilaOffline = new ArrayList<>();
        private final Set<String> bloqueiosSeguranca = new HashSet<>();

        public void registrarAnuncio(ItemAnuncio anuncio) {
            catalogoAnuncios.add(anuncio);
        }

        public void bloquearUsuario(UUID idBloqueador, UUID idBloqueado) {
            bloqueiosSeguranca.add(idBloqueador + "->" + idBloqueado);
        }

        public boolean estaBloqueado(UUID idRemetente, UUID idDestinatario) {
            return bloqueiosSeguranca.contains(idDestinatario + "->" + idRemetente);
        }

        // ---------------------------------------------------------------------
        // RF04 / RF14: BUSCAR ANÚNCIO POR PROXIMIDADE (BAIRRO / CIDADE)
        // ---------------------------------------------------------------------
        public List<ItemAnuncio> buscarAnunciosPorProximidade(String cidade, String bairro, String termoBusca) {
            System.out.printf("\n🔍 [BUSCA POR PROXIMIDADE] Filtros -> Cidade: '%s', Bairro: '%s', Termo: '%s'%n",
                    cidade, bairro, termoBusca);

            return catalogoAnuncios.stream()
                    .filter(a -> a.getEstado() == EstadoPublicacao.ATIVO)
                    .filter(a -> cidade == null || a.getCidade().equalsIgnoreCase(cidade))
                    .filter(a -> bairro == null || a.getBairro().equalsIgnoreCase(bairro))
                    .filter(a -> termoBusca == null || a.getTitulo().toLowerCase().contains(termoBusca.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // ---------------------------------------------------------------------
        // DCU15: TROCAR MENSAGENS ANUNCIANTE E CLIENTE (FLUXO COMPLETO)
        // ---------------------------------------------------------------------
        public String enviarMensagem(UUID idAnuncio, UUID idRemetente, UUID idDestinatario,
                                     String texto, boolean conexaoInternetAtiva) {

            // Passo 4 do Fluxo Normal: Validação de corpo não vazio
            if (texto == null || texto.trim().isEmpty()) {
                return "ERRO: A mensagem não pode estar vazia.";
            }

            // Localiza o anúncio para verificar integridade de contexto
            ItemAnuncio anuncio = catalogoAnuncios.stream()
                    .filter(a -> a.getId().equals(idAnuncio))
                    .findFirst()
                    .orElse(null);

            if (anuncio == null) {
                return "ERRO: Anúncio de referência não localizado.";
            }

            // Exceção 5.1: Falha de conexão na rede móvel do usuário
            if (!conexaoInternetAtiva) {
                MensagemContato msgPendente = new MensagemContato(idAnuncio, idRemetente, idDestinatario, texto);
                msgPendente.setStatus(StatusMensagem.PENDENTE_OFFLINE);
                bufferFilaOffline.add(msgPendente);
                return "AVISO: Sem sinal de rede. Mensagem salva localmente no dispositivo (ícone de relógio).";
            }

            // Exceção 6.1: Verificação de bloqueio de segurança entre as partes
            if (estaBloqueado(idRemetente, idDestinatario)) {
                return "BLOQUEIO: Não foi possível enviar a mensagem. As mensagens com este usuário foram desativadas.";
            }

            // Exceção 6.2: Anúncio pausado ou vendido durante a interação
            if (anuncio.getEstado() != EstadoPublicacao.ATIVO) {
                MensagemContato msgGravada = new MensagemContato(idAnuncio, idRemetente, idDestinatario, texto);
                historicoMensagens.add(msgGravada);
                return "ATENÇÃO: Mensagem registrada, mas o anúncio não está mais ativo (Produto já desapegado).";
            }

            // Fluxo Normal (Passos 7 a 9): Persistência e Notificação Push
            MensagemContato msgSucesso = new MensagemContato(idAnuncio, idRemetente, idDestinatario, texto);
            historicoMensagens.add(msgSucesso);

            // Disparo de evento ao destinatário
            ServicoPushNotification.enviarNotificacao(
                    idDestinatario,
                    "Nova Mensagem sobre: " + anuncio.getTitulo(),
                    texto
            );

            return "SUCESSO: Mensagem enviada com sucesso (Status: ENVIADA - check simples).";
        }

        public void marcarConversaComoLida(UUID idAnuncio, UUID idLeitor) {
            for (MensagemContato m : historicoMensagens) {
                if (m.getIdAnuncio().equals(idAnuncio) && m.getIdDestinatario().equals(idLeitor)) {
                    m.setStatus(StatusMensagem.LIDA);
                }
            }
            System.out.println("   👁️ [CONFIRMAÇÃO DE LEITURA] Mensagens marcadas como LIDAS (check duplo azul).");
        }

        public void sincronizarBufferOffline() {
            System.out.println("\n🔄 [SINCRONIZAÇÃO EM SEGUNDO PLANO] Restabelecendo conexão móvel...");
            if (bufferFilaOffline.isEmpty()) {
                System.out.println("   Nenhuma mensagem pendente no cache offline.");
                return;
            }
            for (MensagemContato m : new ArrayList<>(bufferFilaOffline)) {
                m.setStatus(StatusMensagem.ENVIADA);
                historicoMensagens.add(m);
                bufferFilaOffline.remove(m);
                System.out.printf("   Mensagem pendente '%s' transmitida ao servidor com sucesso!%n", m.getTextoConteudo());
            }
        }

        public List<MensagemContato> getHistoricoDoAnuncio(UUID idAnuncio) {
            return historicoMensagens.stream()
                    .filter(m -> m.getIdAnuncio().equals(idAnuncio))
                    .collect(Collectors.toList());
        }
    }

    // =========================================================================
    // MÉTODO MAIN: DEMONSTRAÇÃO DOS CENÁRIOS E DAS EXCEÇÕES DE NEGÓCIO
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("  UNIFEF - ENGENHARIA DE SOFTWARE I - PROF. MARCELO BOER");
        System.out.println("  CASOS DE USO: DCU15 (Mensageria) e RF04/RF14 (Busca por Proximidade)");
        System.out.println("====================================================================");

        MotorDesapegaJa motor = new MotorDesapegaJa();

        // Criação de Atores Simulados (IDs)
        UUID idAnuncianteVendedor = UUID.randomUUID();
        UUID idClienteInteressado = UUID.randomUUID();
        UUID idUsuarioNocivo = UUID.randomUUID();

        // Cadastro de Anúncios para popular o catálogo (RF04)
        ItemAnuncio an1 = new ItemAnuncio(idAnuncianteVendedor, "Bicicleta Aro 29 Seminova", "Esporte",
                new BigDecimal("750.00"), "Santa Fé do Sul", "Centro");
        ItemAnuncio an2 = new ItemAnuncio(idAnuncianteVendedor, "Mesa de Escritório em L", "Móveis",
                new BigDecimal("220.00"), "Santa Fé do Sul", "Jardim Europa");
        ItemAnuncio an3 = new ItemAnuncio(idAnuncianteVendedor, "Livro Cálculo I Stewart", "Livros",
                new BigDecimal("85.00"), "Santa Fé do Sul", "Centro");

        motor.registrarAnuncio(an1);
        motor.registrarAnuncio(an2);
        motor.registrarAnuncio(an3);

        // ---------------------------------------------------------------------
        // CENÁRIO 1: BUSCA POR PROXIMIDADE (RF04 / RF14)
        // ---------------------------------------------------------------------
        List<ItemAnuncio> buscaCentro = motor.buscarAnunciosPorProximidade("Santa Fé do Sul", "Centro", null);
        System.out.println("Resultados encontrados no bairro Centro:");
        buscaCentro.forEach(a -> System.out.println("  -> " + a));

        // ---------------------------------------------------------------------
        // CENÁRIO 2: DCU15 - FLUXO NORMAL (TROCA DE MENSAGENS COM SUCESSO)
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TESTE 1] Executando DCU15: Fluxo Normal de Mensageria ---");
        String res1 = motor.enviarMensagem(
                an1.getId(),
                idClienteInteressado,
                idAnuncianteVendedor,
                "Olá! A bicicleta ainda está disponível? Aceita proposta à vista?",
                true // Conexão ativa
        );
        System.out.println("Resposta: " + res1);

        // Destinatário visualiza a conversa (evento LIDA)
        motor.marcarConversaComoLida(an1.getId(), idAnuncianteVendedor);

        // ---------------------------------------------------------------------
        // CENÁRIO 3: DCU15 - EXCEÇÃO 5.1 (INSTABILIDADE DE CONEXÃO MÓVEL)
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TESTE 2] Executando DCU15: Exceção 5.1 (Sem Conexão de Rede Móvel) ---");
        String res2 = motor.enviarMensagem(
                an1.getId(),
                idClienteInteressado,
                idAnuncianteVendedor,
                "Tenho interesse em buscar hoje à tarde no Centro.",
                false // Conexão offline
        );
        System.out.println("Resposta: " + res2);

        // Reconexão automática em segundo plano
        motor.sincronizarBufferOffline();

        // ---------------------------------------------------------------------
        // CENÁRIO 4: DCU15 - EXCEÇÃO 6.1 (REMETENTE BLOQUEADO POR MODERAÇÃO)
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TESTE 3] Executando DCU15: Exceção 6.1 (Usuário Bloqueado) ---");
        motor.bloquearUsuario(idAnuncianteVendedor, idUsuarioNocivo);

        String res3 = motor.enviarMensagem(
                an1.getId(),
                idUsuarioNocivo,
                idAnuncianteVendedor,
                "Mensagem abusiva ou spam.",
                true
        );
        System.out.println("Resposta: " + res3);

        // ---------------------------------------------------------------------
        // CENÁRIO 5: DCU15 - EXCEÇÃO 6.2 (ANÚNCIO FINALIZADO/VENDIDO)
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TESTE 4] Executando DCU15: Exceção 6.2 (Anúncio Já Vendido) ---");
        an1.setEstado(EstadoPublicacao.VENDIDO);

        String res4 = motor.enviarMensagem(
                an1.getId(),
                idClienteInteressado,
                idAnuncianteVendedor,
                "Ainda dá tempo de negociar?",
                true
        );
        System.out.println("Resposta: " + res4);

        // Exibição do Histórico Consolidado de Mensagens
        System.out.println("\n[HISTÓRICO AUDITADO DE MENSAGENS DO ANÚNCIO]:");
        motor.getHistoricoDoAnuncio(an1.getId()).forEach(m -> System.out.println("  -> " + m));

        System.out.println("\n>>> CASO DE USO DCU15 E REQUISITOS DE BUSCA HOMOLOGADOS COM SUCESSO <<<");
    }
}
