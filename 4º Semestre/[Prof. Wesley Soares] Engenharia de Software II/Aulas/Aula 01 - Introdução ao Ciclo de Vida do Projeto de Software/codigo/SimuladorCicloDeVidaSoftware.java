/**
 * Disciplina: Engenharia de Software II
 * Professor: Wesley Soares
 * Tema: As 10 Fases do Ciclo de Vida de Desenvolvimento de Software
 * 
 * Como compilar:
 *   javac SimuladorCicloDeVidaSoftware.java
 * Como executar:
 *   java SimuladorCicloDeVidaSoftware
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal executável que simula a transição ordenada pelas dez fases
 * do ciclo de vida de desenvolvimento de software abordadas na Aula 01.
 * 
 * O exemplo concreto modela o sistema fictício 'RotaVerde Express' (domínio de Logística),
 * demonstrando artefatos de entrada, ações técnicas e artefatos de saída em cada fase,
 * além do ciclo contínuo de manutenção pós-deploy.
 */
public class SimuladorCicloDeVidaSoftware {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("  SIMULADOR DO CICLO DE VIDA DE SOFTWARE — SISTEMA ROTA VERDE EXPRESS");
        System.out.println("  Engenharia de Software II | Prof. Wesley Soares | UniFEF");
        System.out.println("======================================================================\n");

        // Execução sequencial das 10 fases da engenharia de software
        fase1IdentificacaoProblema();
        fase2EngenhariaRequisitos();
        fase3PlanejamentoProjeto();
        fase4ArquiteturaSoftware();
        fase5ProjetoDetalhado();
        fase6Implementacao();
        fase7TestesQualidade();
        fase8IntegracaoConfiguracao();
        fase9EntregaDeploy();
        fase10ManutencaoEEvolucao();

        System.out.println("\n======================================================================");
        System.out.println("  CICLO DE VIDA CONCLUIDO COM SUCESSO — QUALIDADE GARANTIDA PONTAS A PONTA");
        System.out.println("======================================================================");
    }

    /**
     * FASE 1: Identificação do Problema
     * Conceito: Toda engenharia começa com uma dor real de negócio. 'Software não é pastelaria'.
     * Entrada: Dores de mercado e gargalos operacionais.
     * Saída: Declaração Formal do Problema e Proposta de Valor.
     */
    private static void fase1IdentificacaoProblema() {
        System.out.println("[FASE 1] IDENTIFICACAO DO PROBLEMA E PROPOSTA DE VALOR");
        System.out.println("  - Entrada: Microtransportadoras perdem 35% do tempo com rotas manuais em papel.");
        System.out.println("  - Analise de Negocio (Fazer a coisa certa): Mapear dor real antes do codigo.");
        System.out.println("  - Saida: Documento de Visao — Otimizar entregas de ultima milha com frotas limpas.\n");
    }

    /**
     * FASE 2: Engenharia de Requisitos
     * Conceito: Elicitação investigativa, separando Requisitos Funcionais (o que o sistema faz)
     * de Requisitos Não Funcionais (critérios de qualidade mensuráveis).
     * Entrada: Documento de visão e entrevistas com despachantes e motoristas.
     * Saída: Documento de Requisitos de Software (SRS) validado.
     */
    private static void fase2EngenhariaRequisitos() {
        System.out.println("[FASE 2] LEVANTAMENTO E ESPECIFICACAO DE REQUISITOS");
        System.out.println("  - Elicitacao realizada via Shadowing (acompanhamento de rotina do entregador).");
        System.out.println("  - Requisito Funcional (RF01): O sistema deve sequenciar paradas de entrega por proximidade.");
        System.out.println("  - Requisito Nao Funcional (RNF01): O calculo da rota de 50 paradas deve responder em < 3 segundos.");
        System.out.println("  - Saida: SRS aprovado pelos stakeholders.\n");
    }

    /**
     * FASE 3: Planejamento do Projeto
     * Conceito: Definição de escopo, estimativa de esforço, divisão de sprints e mitigação de riscos.
     * Entrada: Requisitos priorizados (ex: técnica MoSCoW).
     * Saída: Backlog organizado, cronograma de marcos e matriz de riscos técnicos.
     */
    private static void fase3PlanejamentoProjeto() {
        System.out.println("[FASE 3] PLANEJAMENTO DO PROJETO (ESCOPO, CRONOGRAMA, RISCOS)");
        System.out.println("  - Equipe: 3 integrantes (Analista, Arquiteto, Engenheiro de Qualidade).");
        System.out.println("  - Duracao: 4 Sprints quinzenais alinhadas aos marcos AV1, AV2 e PJ.");
        System.out.println("  - Risco Critico Identificado: Instabilidade de API de mapas externa.");
        System.out.println("  - Mitigacao: Implementar Circuit Breaker e cache local de coordenadas.\n");
    }

    /**
     * FASE 4: Arquitetura de Software
     * Conceito: Decisões estruturais macro de alto impacto e difícil reversão.
     * Entrada: Requisitos Não Funcionais críticos (escalabilidade, tolerância a falhas).
     * Saída: Documento de Arquitetura de Software (SAD) e diagrama de subsistemas.
     */
    private static void fase4ArquiteturaSoftware() {
        System.out.println("[FASE 4] ARQUITETURA DE SOFTWARE E ESTILOS ARQUITETURAIS");
        System.out.println("  - Decisao: Arquitetura Hexagonal (Ports & Adapters) para isolar o dominio de frete.");
        System.out.println("  - Justificativa: Permite testar calculos centrais sem depender de banco de dados ou HTTP.");
        System.out.println("  - Saida: Diagrama de componentes macro e definicao das interfaces de saida.\n");
    }

    /**
     * FASE 5: Projeto Detalhado (Design Orientado a Objetos)
     * Conceito: 'Fazer certo a coisa'. Aplicação de SOLID, alta coesão, baixo acoplamento e GoF.
     * Entrada: Arquitetura macro aprovada.
     * Saída: Diagramas de classes UML e contratos de interfaces.
     */
    private static void fase5ProjetoDetalhado() {
        System.out.println("[FASE 5] PROJETO DETALHADO E PRINCIPIOS DE DESIGN (SOLID & GOF)");
        System.out.println("  - Single Responsibility Principle (SRP): Pedido apenas gerencia itens e totais.");
        System.out.println("  - Open/Closed Principle (OCP): Padrao Strategy adotado para variacoes de frete.");
        System.out.println("  - Dependency Inversion Principle (DIP): Modulos dependem da interface do algoritmo.");
        System.out.println("  - Saida: Diagrama de classes detalhado com Strategy para calculo de frete.\n");
    }

    /**
     * FASE 6: Implementação e Construção
     * Conceito: Escrita de código-fonte tipado, limpo, versionado em branches e auditável.
     * Entrada: Especificações técnicas e contratos de classes.
     * Saída: Código-fonte versionado no Git com revisões por pares (Pull Requests).
     */
    private static void fase6Implementacao() {
        System.out.println("[FASE 6] IMPLEMENTACAO E BOAS PRATICAS DE ENGENHARIA");
        System.out.println("  - Padrao de branches: feature/calculo-frete-strategy -> develop.");
        System.out.println("  - Revisao de Codigo (Code Review): Aprovacao obrigatoria de 1 colega de equipe.");
        System.out.println("  - Saida: Classes Java compiladas e documentadas com JavaDoc.\n");
    }

    /**
     * FASE 7: Testes e Garantia da Qualidade (QA)
     * Conceito: Pirâmide de testes (Unitários, Integração e E2E) para evitar regressões.
     * Entrada: Código compilável e critérios de aceitação.
     * Saída: Relatórios de cobertura e matriz de testes aprovada.
     */
    private static void fase7TestesQualidade() {
        System.out.println("[FASE 7] TESTES E GARANTIA DA QUALIDADE (PIRAMIDE DE TESTES)");
        // Teste unitario em memoria simulando o comportamento da suite automatizada
        double pesoTesteKg = 15.0;
        double distanciaTesteKm = 20.0;
        double custoEsperado = 10.00 + (distanciaTesteKm * 0.50) + (pesoTesteKg * 0.20); // 10 + 10 + 3 = 23.00
        double custoCalculado = 23.00;

        boolean testePassou = Double.compare(custoEsperado, custoCalculado) == 0;
        System.out.println("  - Executando teste unitario: CalculoFreteEconomico...");
        System.out.println("  - Resultado: " + (testePassou ? "[SUCESSO] 100% dos testes unitarios passaram." : "[FALHA] Regressao detectada."));
        System.out.println("  - Saida: Relatorio de QA com 92% de cobertura de linhas no modulo de Dominio.\n");
    }

    /**
     * FASE 8: Integração Contínua (CI)
     * Conceito: Automação de compilação, testes e empacotamento para eliminar o 'integration hell'.
     * Entrada: Branches integradas no repositório remoto.
     * Saída: Imagem Docker imutável testada e aprovada para homologação.
     */
    private static void fase8IntegracaoConfiguracao() {
        System.out.println("[FASE 8] INTEGRACAO CONTINUA E GERENCIA DE CONFIGURACAO (CI)");
        System.out.println("  - Pipeline acionada automaticamente pelo webhook do Git push.");
        System.out.println("  - Etapas da pipeline: Lint -> Build -> Testes Automatizados -> Analise Estatica.");
        System.out.println("  - Saida: Artefato 'rotaverde-api:v1.0.0' empacotado em imagem Docker.\n");
    }

    /**
     * FASE 9: Entrega e Liberação (Deploy / CD)
     * Conceito: Disponibilização controlada da versão compilada em ambiente de produção.
     * Entrada: Imagem de contêiner homologada.
     * Saída: Sistema em execução e disponível para os usuários finais com telemetria ativa.
     */
    private static void fase9EntregaDeploy() {
        System.out.println("[FASE 9] ENTREGA (DEPLOY) E ENTRADA EM PRODUCAO");
        System.out.println("  - Estrategia de entrega: Canary Deployment (liberado inicialmente para 5% dos entregadores).");
        System.out.println("  - Verificacao de saude (Healthcheck): Status HTTP 200 OK nos endpoints centrais.");
        System.out.println("  - Saida: Sistema RotaVerde Express ativo no cluster de producao.\n");
    }

    /**
     * FASE 10: Operação, Manutenção e Evolução
     * Conceito: Cerca de 70% do TCO do software ocorre pós-deploy. Classificação formal das manutenções:
     * Corretiva, Adaptativa, Evolutiva (Perfectiva) e Preventiva.
     * Entrada: Métricas de monitoramento e feedbacks dos usuários.
     * Saída: Patches corretivos e novos ciclos de engenharia.
     */
    private static void fase10ManutencaoEEvolucao() {
        System.out.println("[FASE 10] OPERACAO, MANUTENCAO E EVOLUCAO CONTINUA");
        
        List<String> chamadosOperacionais = new ArrayList<>();
        chamadosOperacionais.add("Manutencao Corretiva: Bug no calculo de frete com casas decimais arredondadas para baixo.");
        chamadosOperacionais.add("Manutencao Adaptativa: Adequacao a nova tabela de aliquota fiscal do ICMS de transporte.");
        chamadosOperacionais.add("Manutencao Evolutiva: Adicao da modalidade de entrega por drones e bicicletas de carga.");
        chamadosOperacionais.add("Manutencao Preventiva: Reindexacao do banco de dados para evitar lentidao sob 500k rotas.");

        for (String chamado : chamadosOperacionais) {
            System.out.println("  -> " + chamado);
        }
        System.out.println("  - Feedback continuo realimenta a Fase 1 (Novos Requisitos e Melhorias).");
    }
}
