/**
 * ============================================================================
 * DISCIPLINA : Engenharia de Software II (4º Semestre) - UniFEF
 * PROFESSOR  : Wesley Soares
 * TEMA       : Arquitetura Offline-First (RNF02) e Buffer Transacional de Coletas
 * CONTEXTO   : Atividade da Aula 3 - HealthTech Solutions (Plataforma MedTrack)
 * ============================================================================
 *
 * CONCEITOS COBERTOS:
 * 1. Requisito Não-Funcional RNF02: Operação e Confiabilidade Offline-First.
 *    Permite aos motoristas registrar vistorias e ler QR Codes em subsolos
 *    e áreas isoladas de hospitais sem sinal celular 4G/5G.
 * 2. Persistência local em fila transacional (simulando SQLite embarcado no app).
 * 3. Mecanismo de sincronização assíncrona bidirecional com tratamento de
 *    idempotência e confirmação de recebimento pela API Central.
 *
 * COMO COMPILAR:
 *   javac MedTrackOfflineSync.java
 *
 * COMO EXECUTAR:
 *   java MedTrackOfflineSync
 * ============================================================================
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedTrackOfflineSync {

    /**
     * Estado da sincronização do registro no dispositivo do motorista.
     */
    public enum EstadoSincronizacao {
        PENDENTE_ENVIO,
        SINCRONIZADO,
        ERRO_FALHA_CONEXAO
    }

    /**
     * Registro de vistoria de coleta hospitalar capturado localmente no smartphone.
     */
    public static class RegistroColetaLocal {
        private final String idTransacaoLocal;
        private final String codigoOrdem;
        private final String serialLido;
        private final String timestampLocal;
        private final double latitudeGps;
        private final double longitudeGps;
        private final String assinaturaBase64;
        private EstadoSincronizacao estadoSinc;
        private String mensagemServidor;

        public RegistroColetaLocal(
                String idTransacaoLocal,
                String codigoOrdem,
                String serialLido,
                String timestampLocal,
                double latitudeGps,
                double longitudeGps,
                String assinaturaBase64) {
            this.idTransacaoLocal = idTransacaoLocal;
            this.codigoOrdem = codigoOrdem;
            this.serialLido = serialLido;
            this.timestampLocal = timestampLocal;
            this.latitudeGps = latitudeGps;
            this.longitudeGps = longitudeGps;
            this.assinaturaBase64 = assinaturaBase64;
            this.estadoSinc = EstadoSincronizacao.PENDENTE_ENVIO;
        }

        public String getIdTransacaoLocal() {
            return idTransacaoLocal;
        }

        public String getCodigoOrdem() {
            return codigoOrdem;
        }

        public String getSerialLido() {
            return serialLido;
        }

        public String getTimestampLocal() {
            return timestampLocal;
        }

        public double getLatitudeGps() {
            return latitudeGps;
        }

        public double getLongitudeGps() {
            return longitudeGps;
        }

        public String getAssinaturaBase64() {
            return assinaturaBase64;
        }

        public EstadoSincronizacao getEstadoSinc() {
            return estadoSinc;
        }

        public void marcarComoSincronizado(String respostaServidor) {
            this.estadoSinc = EstadoSincronizacao.SINCRONIZADO;
            this.mensagemServidor = respostaServidor;
        }

        public void marcarErroConexao(String motivo) {
            this.estadoSinc = EstadoSincronizacao.ERRO_FALHA_CONEXAO;
            this.mensagemServidor = motivo;
        }

        public String getMensagemServidor() {
            return mensagemServidor;
        }
    }

    /**
     * Simulação do Banco de Dados Local SQLite no aplicativo móvel.
     */
    public static class BancoDadosLocalDispositivo {
        private final List<RegistroColetaLocal> tabelaVistoriasLocais = new ArrayList<>();

        public void salvarVistoriaOffline(RegistroColetaLocal vistoria) {
            tabelaVistoriasLocais.add(vistoria);
            System.out.printf("   [SQLite Local] Transação %s (Ordem %s) salva na memória interna do dispositivo.%n",
                    vistoria.getIdTransacaoLocal(), vistoria.getCodigoOrdem());
        }

        public List<RegistroColetaLocal> listarPendentesSincronizacao() {
            List<RegistroColetaLocal> pendentes = new ArrayList<>();
            for (RegistroColetaLocal reg : tabelaVistoriasLocais) {
                if (reg.getEstadoSinc() != EstadoSincronizacao.SINCRONIZADO) {
                    pendentes.add(reg);
                }
            }
            return pendentes;
        }

        public int contarPendentes() {
            return listarPendentesSincronizacao().size();
        }
    }

    /**
     * Simulação do Servidor Central na Nuvem da HealthTech Solutions.
     */
    public static class ServidorCentralHealthTech {
        private final Map<String, RegistroColetaLocal> bancoCentralRelacional = new HashMap<>();

        public boolean processarRecebimentoLote(RegistroColetaLocal registro) {
            // Idempotência: impede duplicação caso a rede caia durante o envio
            if (bancoCentralRelacional.containsKey(registro.getIdTransacaoLocal())) {
                return true; // Já processado com sucesso anteriormente
            }

            // Persistência definitiva no servidor
            bancoCentralRelacional.put(registro.getIdTransacaoLocal(), registro);
            return true;
        }

        public int getTotalRegistrosCentrais() {
            return bancoCentralRelacional.size();
        }
    }

    /**
     * Mecanismo de Sincronização em Segundo Plano (Background Sync Worker).
     */
    public static class SincronizadorSegundoPlano {
        private final BancoDadosLocalDispositivo storageLocal;
        private final ServidorCentralHealthTech servidorCentral;
        private boolean conexaoRedeAtiva;

        public SincronizadorSegundoPlano(
                BancoDadosLocalDispositivo storageLocal,
                ServidorCentralHealthTech servidorCentral) {
            this.storageLocal = storageLocal;
            this.servidorCentral = servidorCentral;
            this.conexaoRedeAtiva = false; // Inicia sem conexão (no subsolo)
        }

        public void setConexaoRedeAtiva(boolean status) {
            this.conexaoRedeAtiva = status;
            System.out.printf("-> EVENTO DE REDE: Conectividade móvel alterada para [%s]%n",
                    status ? "ONLINE 4G/5G" : "OFFLINE / SEM SINAL");
        }

        public void executarCicloSincronizacao() {
            List<RegistroColetaLocal> pendentes = storageLocal.listarPendentesSincronizacao();

            if (pendentes.isEmpty()) {
                System.out.println("-> Sincronizador: Nenhuma transação pendente no dispositivo.");
                return;
            }

            System.out.printf("-> Sincronizador: Identificadas %d vistorias aguardando envio para a nuvem...%n",
                    pendentes.size());

            if (!conexaoRedeAtiva) {
                System.out.println("-> Sincronizador ABORTADO: Sem conexão de rede disponível. Os dados continuam seguros localmente!");
                for (RegistroColetaLocal p : pendentes) {
                    p.marcarErroConexao("Aguardando restabelecimento de sinal móvel.");
                }
                return;
            }

            int enviadasComSucesso = 0;
            for (RegistroColetaLocal reg : pendentes) {
                try {
                    boolean ok = servidorCentral.processarRecebimentoLote(reg);
                    if (ok) {
                        reg.marcarComoSincronizado("Sincronizado com sucesso na base central HealthTech.");
                        enviadasComSucesso++;
                    }
                } catch (Exception e) {
                    reg.marcarErroConexao("Falha na API: " + e.getMessage());
                }
            }

            System.out.printf("-> Sincronizador CONCLUÍDO: %d/%d registros sincronizados com a nuvem com sucesso!%n",
                    enviadasComSucesso, pendentes.size());
        }
    }

    /**
     * Simulação completa do fluxo em campo.
     */
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("     MEDTRACK SOLUTIONS - SIMULADOR OFFLINE-FIRST E SINCRONIZAÇÃO (RNF02)");
        System.out.println("========================================================================\n");

        BancoDadosLocalDispositivo bancoLocal = new BancoDadosLocalDispositivo();
        ServidorCentralHealthTech servidorCentral = new ServidorCentralHealthTech();
        SincronizadorSegundoPlano workerSync = new SincronizadorSegundoPlano(bancoLocal, servidorCentral);

        // ETAPA 1: O motorista desce até o 3º subsolo do Hospital das Clínicas (Sem sinal de celular)
        System.out.println("[ETAPA 1] Motorista ingressa no 3º Subsolo do Hospital (Sem sinal de internet):");
        workerSync.setConexaoRedeAtiva(false);

        String agora = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        RegistroColetaLocal coletaSubsolo1 = new RegistroColetaLocal(
                "UUID-LOC-001",
                "ORD-2026-9011",
                "SN-VP-99881-A",
                agora,
                -23.557112,
                -46.668901,
                "HASH_ASSINATURA_DRA_MARCIA_CRM_8819"
        );

        RegistroColetaLocal coletaSubsolo2 = new RegistroColetaLocal(
                "UUID-LOC-002",
                "ORD-2026-9012",
                "SN-BI-33211-K",
                agora,
                -23.557115,
                -46.668905,
                "HASH_ASSINATURA_ENF_LUCAS_COREN_5541"
        );

        System.out.println("\nRealizando vistorias e leituras de QR Code no modo Offline:");
        bancoLocal.salvarVistoriaOffline(coletaSubsolo1);
        bancoLocal.salvarVistoriaOffline(coletaSubsolo2);

        // Dispara sincronizador no subsolo (deve proteger os dados e não perder nada)
        System.out.println("\nTentando envio automático em segundo plano enquanto no subsolo:");
        workerSync.executarCicloSincronizacao();

        System.out.printf("\nStatus no Dispositivo Móvel: %d registros locais com status [%s]%n",
                bancoLocal.contarPendentes(), coletaSubsolo1.getEstadoSinc());
        System.out.printf("Total gravado no Servidor Central até agora: %d registros%n\n",
                servidorCentral.getTotalRegistrosCentrais());

        // ETAPA 2: O motorista sai do subsolo e ingressa no pátio externo (Sinal 5G conectado)
        System.out.println("[ETAPA 2] Motorista retorna à superfície e recupera sinal 5G:");
        workerSync.setConexaoRedeAtiva(true);

        // O sistema detecta o restabelecimento da rede e dispara a sincronização assíncrona
        System.out.println("\nDisparando worker de sincronização com a rede restabelecida:");
        workerSync.executarCicloSincronizacao();

        System.out.println("\n[CONFERÊNCIA FINAL DE INTEGRIDADE APÓS SINCRONIZAÇÃO]:");
        System.out.printf("Registros Pendentes no Dispositivo Móvel : %d%n", bancoLocal.contarPendentes());
        System.out.printf("Registros Confirmados no Servidor Central: %d%n", servidorCentral.getTotalRegistrosCentrais());
        System.out.printf("Status da Transação UUID-LOC-001          : %s (%s)%n",
                coletaSubsolo1.getEstadoSinc(), coletaSubsolo1.getMensagemServidor());
        System.out.printf("Status da Transação UUID-LOC-002          : %s (%s)%n",
                coletaSubsolo2.getEstadoSinc(), coletaSubsolo2.getMensagemServidor());
        System.out.println("========================================================================");
        System.out.println("-> Requisito RNF02 atendido: Zero perda de dados operacionais em áreas de sombra.");
        System.out.println("========================================================================");
    }
}
