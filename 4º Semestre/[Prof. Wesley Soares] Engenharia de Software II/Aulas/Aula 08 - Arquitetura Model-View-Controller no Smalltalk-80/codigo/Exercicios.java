/**
 * Disciplina: Engenharia de Software II (4º Semestre) - UniFEF
 * Professor: Prof. Wesley Soares
 * Tema: Resolução de Exercícios - Arquitetura Model-View-Controller Smalltalk-80
 *
 * Como compilar e executar:
 *   javac Exercicios.java
 *   java Exercicios
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exercicios {

    // =========================================================================
    // Exercício 1: Inspeção da Tríade MVC no System Transcript (Origem: Professor)
    // =========================================================================
    public static class Exercicio1_InspecaoTranscript {
        public static class TextCollector {
            private final StringBuilder contents = new StringBuilder();
            public void append(String text) { contents.append(text); }
            public String getContents() { return contents.toString(); }
        }

        public static class MVCInspectorSimulado {
            private final String titulo;
            private final Object model;
            private final Object view;
            private final Object controller;

            public MVCInspectorSimulado(String titulo, Object m, Object v, Object c) {
                this.titulo = titulo;
                this.model = m;
                this.view = v;
                this.controller = c;
            }

            public void inspecionar() {
                System.out.println("  [MVC Inspector Aberto]: " + titulo);
                System.out.println("    * Model: " + model.getClass().getSimpleName() + " -> valor: '" + model + "'");
                System.out.println("    * View:  " + view.getClass().getSimpleName());
                System.out.println("    * Controller: " + controller.getClass().getSimpleName());
            }
        }

        public static void executar() {
            System.out.println("--- [Exercício 1] Emulação do MVC Inspector no System Transcript ---");
            TextCollector transcriptModel = new TextCollector();
            transcriptModel.append("Smalltalk-80 System Release 2.5 initialized.\nReady.");

            String topView = "StandardSystemView (Transcript Window)";
            String subView = "TextCollectorView (SubView de Texto)";
            String topController = "StandardSystemController";
            String subController = "ParagraphEditorController";

            // Relacionamento registrado na coleção de dependentes do modelo
            List<String> dependentsCollection = new ArrayList<>();
            dependentsCollection.add(topView);
            dependentsCollection.add(subView);

            System.out.println("Passo 1: DependentsCollection allInstances inspect");
            System.out.println("  Dependentes registrados para o TextCollector: " + dependentsCollection);

            System.out.println("\nPasso 2: Inspecionando a TextCollectorView selecionada:");
            MVCInspectorSimulado subInspector = new MVCInspectorSimulado(
                    "TextCollector SubView Inspector", transcriptModel.getContents(), subView, subController);
            subInspector.inspecionar();

            System.out.println("\nPasso 3: Acessando superView da TextCollectorView -> StandardSystemView:");
            MVCInspectorSimulado topInspector = new MVCInspectorSimulado(
                    "TopView Inspector", transcriptModel.getContents(), topView, topController);
            topInspector.inspecionar();
            System.out.println("  Conclusão: Ambas as tríades inspecionadas compartilham a mesma instância de TextCollector.\n");
        }
    }

    // =========================================================================
    // Exercício 2: Mecanismo de Notificação Reativa (Changed/Update) (Origem: Sugerido)
    // =========================================================================
    public static class Exercicio2_NotificacaoReativa {
        public interface ObservadorSmalltalk {
            void update(ModeloReativo modelo, String aspecto);
        }

        public static abstract class ModeloReativo {
            private final List<ObservadorSmalltalk> observadores = new ArrayList<>();

            public void registrar(ObservadorSmalltalk obs) {
                if (!observadores.contains(obs)) observadores.add(obs);
            }

            public void changed(String aspecto) {
                for (ObservadorSmalltalk obs : new ArrayList<>(observadores)) {
                    obs.update(this, aspecto);
                }
            }
        }

        public static class TermostatoModel extends ModeloReativo {
            private double temperatura;
            private String unidade = "Celsius";

            public void setTemperatura(double t) {
                this.temperatura = t;
                changed("temperatura");
            }

            public void setUnidade(String u) {
                this.unidade = u;
                changed("unidade");
            }

            public double getTemperatura() { return temperatura; }
            public String getUnidade() { return unidade; }
        }

        public static class PainelDigitalView implements ObservadorSmalltalk {
            @Override
            public void update(ModeloReativo modelo, String aspecto) {
                TermostatoModel term = (TermostatoModel) modelo;
                System.out.println("  [PainelDigitalView] Atualização recebida para aspecto: #" + aspecto +
                                   " -> Estado atual: " + term.getTemperatura() + " °" + term.getUnidade());
            }
        }

        public static class AlarmeCriticoView implements ObservadorSmalltalk {
            @Override
            public void update(ModeloReativo modelo, String aspecto) {
                // Reage exclusivamente quando o aspecto for 'temperatura'
                if ("temperatura".equals(aspecto)) {
                    TermostatoModel term = (TermostatoModel) modelo;
                    if (term.getTemperatura() > 80.0) {
                        System.out.println("  [AlarmeCriticoView] !!! ALERTA: Temperatura em nível crítico: " +
                                           term.getTemperatura() + " °" + term.getUnidade() + " !!!");
                    }
                }
            }
        }

        public static void executar() {
            System.out.println("--- [Exercício 2] Notificação Reativa via changed e update: ---");
            TermostatoModel termostato = new TermostatoModel();
            termostato.registrar(new PainelDigitalView());
            termostato.registrar(new AlarmeCriticoView());

            System.out.println("Operação A: Alterando temperatura para 25.0 °C:");
            termostato.setTemperatura(25.0);

            System.out.println("\nOperação B: Alterando unidade de medida para Kelvin:");
            termostato.setUnidade("Kelvin");

            System.out.println("\nOperação C: Alterando temperatura para 95.5 Kelvin (acionando alarme):");
            termostato.setTemperatura(95.5);
            System.out.println();
        }
    }

    // =========================================================================
    // Exercício 3: Composição Hierárquica e Coordenadas Relativas (Origem: Sugerido)
    // =========================================================================
    public static class Exercicio3_HierarquiaVisoes {
        public static class RegiaoRelativa {
            public final double x, y, width, height;
            public RegiaoRelativa(double x, double y, double w, double h) {
                this.x = x; this.y = y; this.width = w; this.height = h;
            }
        }

        public static class VisaoComposta {
            private final String nome;
            private final RegiaoRelativa bounds;
            private final List<VisaoComposta> filhas = new ArrayList<>();

            public VisaoComposta(String nome, RegiaoRelativa bounds) {
                this.nome = nome;
                this.bounds = bounds;
            }

            public void adicionarFilha(VisaoComposta filha) {
                filhas.add(filha);
            }

            public void display(int parentPxX, int parentPxY, int parentPxW, int parentPxH) {
                int pixelX = parentPxX + (int) (bounds.x * parentPxW);
                int pixelY = parentPxY + (int) (bounds.y * parentPxH);
                int pixelW = (int) (bounds.width * parentPxW);
                int pixelH = (int) (bounds.height * parentPxH);

                displayBorder(pixelX, pixelY, pixelW, pixelH);
                displayView(pixelX, pixelY, pixelW, pixelH);
                displaySubviews(pixelX, pixelY, pixelW, pixelH);
            }

            protected void displayBorder(int x, int y, int w, int h) {
                System.out.printf("  Borda [%s] em tela: (%d, %d) com dimensões %dx%d px%n", nome, x, y, w, h);
            }

            protected void displayView(int x, int y, int w, int h) {
                System.out.printf("  Conteúdo [%s] renderizado no viewport.%n", nome);
            }

            protected void displaySubviews(int x, int y, int w, int h) {
                for (VisaoComposta f : filhas) {
                    f.display(x, y, w, h);
                }
            }
        }

        public static void executar() {
            System.out.println("--- [Exercício 3] Hierarquia de Visões e SubVisões com Coordenadas Relativas ---");
            // Resolução da tela física do desktop Smalltalk: 1024x768
            int displayScreenWidth = 1024;
            int displayScreenHeight = 768;

            VisaoComposta topView = new VisaoComposta("TopView: BrowserView", new RegiaoRelativa(0.1, 0.1, 0.8, 0.8));
            VisaoComposta listSubView = new VisaoComposta("SubView: SelectionInListView (25%)", new RegiaoRelativa(0.0, 0.0, 1.0, 0.25));
            VisaoComposta codeSubView = new VisaoComposta("SubView: CodeView (75%)", new RegiaoRelativa(0.0, 0.25, 1.0, 0.75));

            topView.adicionarFilha(listSubView);
            topView.adicionarFilha(codeSubView);

            System.out.println("Iniciando pipeline de exibição hierárquico:");
            topView.display(0, 0, displayScreenWidth, displayScreenHeight);
            System.out.println();
        }
    }

    // =========================================================================
    // Exercício 4: Despacho Cooperativo de Controle e Três Botões (Origem: Sugerido)
    // =========================================================================
    public static class Exercicio4_ControleCooperativo {
        public enum BotaoMouse { RED, YELLOW, BLUE }

        public static class ControladorCooperativo {
            protected final String id;
            protected final int x1, y1, x2, y2;
            protected ControladorCooperativo topLevelController;

            public ControladorCooperativo(String id, int x1, int y1, int x2, int y2) {
                this.id = id;
                this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            }

            public void setTopLevelController(ControladorCooperativo top) {
                this.topLevelController = top;
            }

            public boolean viewHasCursor(int mx, int my) {
                return mx >= x1 && mx <= x2 && my >= y1 && my <= y2;
            }

            public void processarEvento(BotaoMouse botao) {
                switch (botao) {
                    case RED:
                        System.out.println("    [" + id + "] Red Button: Seleção de elemento processada.");
                        break;
                    case YELLOW:
                        System.out.println("    [" + id + "] Yellow Button: Exibindo menu específico de " + id + ".");
                        break;
                    case BLUE:
                        if (topLevelController != null) {
                            System.out.println("    [" + id + "] Blue Button: Delegando evento para a TopView...");
                            topLevelController.processarEvento(BotaoMouse.BLUE);
                        } else {
                            System.out.println("    [" + id + "] Blue Button: Menu de Janela (Frame/Close/Collapse).");
                        }
                        break;
                }
            }
        }

        public static class ControlManagerSimulado {
            private final List<ControladorCooperativo> controladoresAtivos = new ArrayList<>();

            public void registrar(ControladorCooperativo c) {
                controladoresAtivos.add(c);
            }

            public void despacharClique(int mouseX, int mouseY, BotaoMouse botao) {
                System.out.printf("ControlManager: Clique com %s na posição (%d, %d):%n", botao, mouseX, mouseY);
                ControladorCooperativo ativo = null;
                // Busca na ordem inversa para priorizar subvisões internas antes de janelas externas
                for (int i = controladoresAtivos.size() - 1; i >= 0; i--) {
                    ControladorCooperativo c = controladoresAtivos.get(i);
                    if (c.viewHasCursor(mouseX, mouseY)) {
                        ativo = c;
                        break;
                    }
                }

                if (ativo != null) {
                    ativo.processarEvento(botao);
                } else {
                    System.out.println("    Nenhum controlador aceitou o controle (fundo da tela).");
                }
            }
        }

        public static void executar() {
            System.out.println("--- [Exercício 4] Despacho Cooperativo de Controle e Três Botões de Mouse ---");
            ControlManagerSimulado scheduledControllers = new ControlManagerSimulado();

            ControladorCooperativo topCtrl = new ControladorCooperativo("TopWindowController", 50, 50, 500, 400);
            ControladorCooperativo subListaCtrl = new ControladorCooperativo("SubViewListController", 60, 60, 490, 150);
            ControladorCooperativo subCodeCtrl = new ControladorCooperativo("SubViewCodeController", 60, 160, 490, 390);

            subListaCtrl.setTopLevelController(topCtrl);
            subCodeCtrl.setTopLevelController(topCtrl);

            scheduledControllers.registrar(topCtrl);
            scheduledControllers.registrar(subListaCtrl);
            scheduledControllers.registrar(subCodeCtrl);

            // Evento 1: Clique do botão amarelo na área da lista (espera menu local da lista)
            scheduledControllers.despacharClique(100, 100, BotaoMouse.YELLOW);

            // Evento 2: Clique do botão azul na área de código (espera delegação para menu de janela)
            scheduledControllers.despacharClique(120, 250, BotaoMouse.BLUE);

            // Evento 3: Clique com botão vermelho na área externa de fundo de tela
            scheduledControllers.despacharClique(10, 10, BotaoMouse.RED);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  ENGENHARIA DE SOFTWARE II - RESOLUÇÃO DOS EXERCÍCIOS MVC SMALLTALK-80 ");
        System.out.println("========================================================================\n");

        Exercicio1_InspecaoTranscript.executar();
        Exercicio2_NotificacaoReativa.executar();
        Exercicio3_HierarquiaVisoes.executar();
        Exercicio4_ControleCooperativo.executar();
    }
}
