/*
 * Disciplina: Engenharia de Software I (3o Semestre) - UniFEF
 * Professor:  Marcelo Boer
 * Tema:       Diferenca Arquitetural: Desenho Vetorial (Draw.io) vs Repositorio CASE (Astah UML)
 *
 * Como compilar:
 *   javac MetamodeloCaseSimulador.java
 *
 * Como executar:
 *   java MetamodeloCaseSimulador
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Demonstra a arquitetura interna de uma ferramenta CASE formal baseada no metamodelo da UML
 * em contraste direto com ferramentas de desenho puramente vetorial.
 */
public class MetamodeloCaseSimulador {

    // =========================================================================
    // 1. ARQUITETURA DE DESENHO VETORIAL GENERICO (Ex: Draw.io, Canva, Miro)
    // =========================================================================
    public static class CaixaDesenhoGenerico {
        public String idGrafico;
        public double coordenadaX;
        public double coordenadaY;
        public String textoLivre; // Apenas string desconectada de qualquer modelo semantico

        public CaixaDesenhoGenerico(String idGrafico, double x, double y, String texto) {
            this.idGrafico = idGrafico;
            this.coordenadaX = x;
            this.coordenadaY = y;
            this.textoLivre = texto;
        }
    }

    public static class FolhaDesenhoGenerico {
        public String nomeAba;
        public List<CaixaDesenhoGenerico> caixas = new ArrayList<>();

        public FolhaDesenhoGenerico(String nomeAba) {
            this.nomeAba = nomeAba;
        }
    }

    // =========================================================================
    // 2. ARQUITETURA CASE FORMAL (Ex: Astah UML)
    // =========================================================================

    /**
     * Entidade Semantica do Metamodelo UML que reside no Repositorio Central (.asta).
     * Possui identidade unica persistente independente da sua representacao na tela.
     */
    public static class ClasseSemantica {
        private final String idUnicoGlobal;
        private String nomeClasse;
        private String visibilidade;
        private final List<String> atributos;
        private final List<String> operacoes;

        public ClasseSemantica(String nomeClasse) {
            this.idUnicoGlobal = UUID.randomUUID().toString();
            this.nomeClasse = nomeClasse;
            this.visibilidade = "public";
            this.atributos = new ArrayList<>();
            this.operacoes = new ArrayList<>();
        }

        public String getIdUnicoGlobal() {
            return idUnicoGlobal;
        }

        public String getNomeClasse() {
            return nomeClasse;
        }

        public void setNomeClasse(String novoNome) {
            this.nomeClasse = novoNome;
        }

        public void adicionarAtributo(String atributo) {
            this.atributos.add(atributo);
        }

        public void adicionarOperacao(String operacao) {
            this.operacoes.add(operacao);
        }

        public List<String> getAtributos() {
            return atributos;
        }

        public List<String> getOperacoes() {
            return operacoes;
        }
    }

    /**
     * Representacao visual em um diagrama especifico. Nao possui dados proprios,
     * apenas aponta para o elemento semantico real do repositorio.
     */
    public static class VisaoDiagramaElemento {
        public String idElementoVisual;
        public double posicaoX;
        public double posicaoY;
        public ClasseSemantica referenciaSemantica; // Ponteiro para o metamodelo central

        public VisaoDiagramaElemento(String idVisual, double x, double y, ClasseSemantica semantica) {
            this.idElementoVisual = idVisual;
            this.posicaoX = x;
            this.posicaoY = y;
            this.referenciaSemantica = semantica;
        }
    }

    /**
     * Um diagrama UML do Astah (ex: Diagrama de Classes Geral, Diagrama de Sub-pacote).
     */
    public static class DiagramaAstah {
        public String nomeDiagrama;
        public List<VisaoDiagramaElemento> elementosVisuais = new ArrayList<>();

        public DiagramaAstah(String nomeDiagrama) {
            this.nomeDiagrama = nomeDiagrama;
        }
    }

    /**
     * O Repositorio Central do Projeto (.asta) - a "Structure Tree" do Astah.
     */
    public static class RepositorioModeloAstah {
        public Map<String, ClasseSemantica> catalogoClasses = new HashMap<>();
        public List<DiagramaAstah> diagramas = new ArrayList<>();

        public ClasseSemantica criarClasseNoModelo(String nome) {
            ClasseSemantica nova = new ClasseSemantica(nome);
            catalogoClasses.put(nova.getIdUnicoGlobal(), nova);
            return nova;
        }

        /**
         * Exclusao de visao (tecla Delete na tela): remove apenas a caixa do diagrama selecionado.
         */
        public void excluirApenasDaVisao(DiagramaAstah diagrama, VisaoDiagramaElemento elementoVisual) {
            diagrama.elementosVisuais.remove(elementoVisual);
            System.out.println("   [ACAO: DELETE NA TELA] Elemento visual removido do diagrama '" +
                diagrama.nomeDiagrama + "'. A entidade continua viva no Modelo Central!");
        }

        /**
         * Exclusao do modelo (Delete from Model): apaga definitivamente do repositorio e de TODAS as visoes.
         */
        public void excluirDefinitivamenteDoModelo(String idSemantico) {
            ClasseSemantica removida = catalogoClasses.remove(idSemantico);
            if (removida != null) {
                for (DiagramaAstah d : diagramas) {
                    d.elementosVisuais.removeIf(v -> v.referenciaSemantica.getIdUnicoGlobal().equals(idSemantico));
                }
                System.out.println("   [ACAO: DELETE FROM MODEL] Classe '" + removida.getNomeClasse() +
                    "' expurgada de todo o repositorio .asta e de todos os diagramas.");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  SIMULADOR COMPARATIVO DE ARQUITETURA CASE: ASTAH VS EDITORES VETORIAIS");
        System.out.println("  Engenharia de Software I - UniFEF | Prof. Marcelo Boer");
        System.out.println("========================================================================\n");

        // ---------------------------------------------------------------------
        // CENARIO 1: O PROBLEMA DAS FERRAMENTAS DE DESENHO LIVRE (Ex: Draw.io)
        // ---------------------------------------------------------------------
        System.out.println("[TESTE 1] Operando em Editor de Desenho Generico (Sem Metamodelo)...");
        FolhaDesenhoGenerico aba1 = new FolhaDesenhoGenerico("Diagrama de Dominio");
        FolhaDesenhoGenerico aba2 = new FolhaDesenhoGenerico("Diagrama de Integracao");

        CaixaDesenhoGenerico caixaA = new CaixaDesenhoGenerico("BOX_01", 10, 20, "class Cliente { nome: String }");
        CaixaDesenhoGenerico caixaB = new CaixaDesenhoGenerico("BOX_02", 50, 80, "class Cliente { nome: String }");

        aba1.caixas.add(caixaA);
        aba2.caixas.add(caixaB);

        System.out.println(" -> Engenheiro renomeia a classe para 'ClienteCorporativo' apenas na Aba 1...");
        caixaA.textoLivre = "class ClienteCorporativo { nome: String }";

        System.out.println(" -> Conteudo na Aba 1: " + aba1.caixas.get(0).textoLivre);
        System.out.println(" -> Conteudo na Aba 2: " + aba2.caixas.get(0).textoLivre);
        System.out.println(" -> DIAGNOSTICO: INCONSISTENCIA ARQUITETURAL GRAVE! O sistema nao possui");
        System.out.println("    integridade referencial; alteracoes exigem atualizacao manual em cada desenho.\n");

        // ---------------------------------------------------------------------
        // CENARIO 2: A ROBUSTEZ DA FERRAMENTA CASE (Astah UML)
        // ---------------------------------------------------------------------
        System.out.println("[TESTE 2] Operando no Astah UML (Metamodelo Centralizado com Efeito Cascata)...");
        RepositorioModeloAstah projetoAsta = new RepositorioModeloAstah();

        // Criacao do elemento formal na arvore de modelos
        ClasseSemantica classeCliente = projetoAsta.criarClasseNoModelo("Cliente");
        classeCliente.adicionarAtributo("- nome: String");
        classeCliente.adicionarOperacao("+ validarCpf(): boolean");

        // O mesmo elemento e referenciado em dois diagramas distintos
        DiagramaAstah diagClasses = new DiagramaAstah("Diagrama de Classes de Dominio");
        DiagramaAstah diagSubsistema = new DiagramaAstah("Diagrama de Classes de Faturamento");
        projetoAsta.diagramas.add(diagClasses);
        projetoAsta.diagramas.add(diagSubsistema);

        VisaoDiagramaElemento visao1 = new VisaoDiagramaElemento("VIS_01", 100, 150, classeCliente);
        VisaoDiagramaElemento visao2 = new VisaoDiagramaElemento("VIS_02", 300, 450, classeCliente);
        diagClasses.elementosVisuais.add(visao1);
        diagSubsistema.elementosVisuais.add(visao2);

        System.out.println(" -> Classe '" + classeCliente.getNomeClasse() + "' inserida em 2 diagramas distintos.");
        System.out.println(" -> Engenheiro renomeia a classe para 'ClienteCorporativo' a partir do Diagrama 1...");
        // Alteracao feita em uma visao propaga para o objeto semantico central
        visao1.referenciaSemantica.setNomeClasse("ClienteCorporativo");

        System.out.println(" -> Nome exibido no Diagrama 1: " + diagClasses.elementosVisuais.get(0).referenciaSemantica.getNomeClasse());
        System.out.println(" -> Nome exibido no Diagrama 2: " + diagSubsistema.elementosVisuais.get(0).referenciaSemantica.getNomeClasse());
        System.out.println(" -> Nome no Catalogo Central:  " + projetoAsta.catalogoClasses.get(classeCliente.getIdUnicoGlobal()).getNomeClasse());
        System.out.println(" -> DIAGNOSTICO: PROPAGACAO AUTOMATICA INTEGRAL! Consistencia semantica garantida.\n");

        // ---------------------------------------------------------------------
        // CENARIO 3: A ARMADILHA DO 'DELETE' NA TELA VS 'DELETE FROM MODEL'
        // ---------------------------------------------------------------------
        System.out.println("[TESTE 3] Comprovando a armadilha do Delete Simples vs Delete from Model...");
        System.out.println(" -> Pressionando tecla 'Delete' simples no Diagrama 1:");
        projetoAsta.excluirApenasDaVisao(diagClasses, visao1);

        System.out.println("    * Elementos visiveis no Diagrama 1: " + diagClasses.elementosVisuais.size());
        System.out.println("    * Elementos visiveis no Diagrama 2: " + diagSubsistema.elementosVisuais.size());
        System.out.println("    * Entidade no Catalogo Central (.asta): " +
            (projetoAsta.catalogoClasses.containsKey(classeCliente.getIdUnicoGlobal()) ? "EXISTE (Ainda viva no projeto)" : "INEXISTENTE"));

        System.out.println("\n -> Agora executando 'Delete from Model' (Exclusao Definitiva via Structure Tree):");
        projetoAsta.excluirDefinitivamenteDoModelo(classeCliente.getIdUnicoGlobal());

        System.out.println("    * Elementos visiveis no Diagrama 1: " + diagClasses.elementosVisuais.size());
        System.out.println("    * Elementos visiveis no Diagrama 2: " + diagSubsistema.elementosVisuais.size());
        System.out.println("    * Entidade no Catalogo Central (.asta): " +
            (projetoAsta.catalogoClasses.containsKey(classeCliente.getIdUnicoGlobal()) ? "EXISTE" : "EXPURGADA COM SUCESSO"));

        System.out.println("\n========================================================================");
        System.out.println("  Fim da simulacao: os fundamentos do metamodelo CASE estao consolidados.");
        System.out.println("========================================================================");
    }
}
