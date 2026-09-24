/**
 * Disciplina: Engenharia de Software II (4º Semestre) - UniFEF
 * Professor: Prof. Wesley Soares
 * Tema: Arquitetura Model-View-Controller no Smalltalk-80 (Steve Burbeck, Ph.D.)
 *
 * Como compilar e executar:
 *   javac ExemplosAula.java
 *   java ExemplosAula
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExemplosAula {

    /**
     * Interface de Notificação equivalente ao protocolo de atualização (#update:) do Smalltalk-80.
     */
    public interface ViewDependent {
        void update(SmalltalkModel model);
        void update(SmalltalkModel model, Object aspect);
    }

    /**
     * Classe base equivalente à classe Model do Smalltalk-80 v2.5.
     * Gerencia a lista de dependentes (visões registradas) e o despacho de mudanças.
     */
    public static abstract class SmalltalkModel {
        private final List<ViewDependent> dependents = new ArrayList<>();

        public void addDependent(ViewDependent dependent) {
            if (dependent != null && !dependents.contains(dependent)) {
                dependents.add(dependent);
            }
        }

        public void removeDependent(ViewDependent dependent) {
            dependents.remove(dependent);
        }

        public void changed() {
            changed(null);
        }

        public void changed(Object aspect) {
            // Itera sobre cópia para evitar ConcurrentModificationException
            List<ViewDependent> targets = new ArrayList<>(dependents);
            for (ViewDependent dep : targets) {
                if (aspect == null) {
                    dep.update(this);
                } else {
                    dep.update(this, aspect);
                }
            }
        }
    }

    /**
     * Modelo concreto simulando o MethodListBrowser citado no artigo de Burbeck.
     */
    public static class MethodListBrowserModel extends SmalltalkModel {
        private final List<String> methodList;
        private String selectedMethod;
        private String currentSourceCode;

        public MethodListBrowserModel(List<String> methods) {
            this.methodList = new ArrayList<>(methods);
            this.selectedMethod = methods.isEmpty() ? null : methods.get(0);
            this.currentSourceCode = "";
        }

        public List<String> getMethodList() {
            return Collections.unmodifiableList(methodList);
        }

        public String getSelectedMethod() {
            return selectedMethod;
        }

        public void setSelectedMethod(String method) {
            this.selectedMethod = method;
            this.currentSourceCode = "\"Código fonte do método " + method + "\"\n" +
                                     method + "\n\t^'Resultado de execução de " + method + "'";
            // Notifica observadores que a seleção e o texto do código mudaram
            changed("methodName");
            changed("text");
        }

        public String getCurrentSourceCode() {
            return currentSourceCode;
        }

        public void acceptText(String newText) {
            this.currentSourceCode = newText;
            System.out.println("[Model] Código do método compilado no repositório com sucesso.");
            changed("text");
        }
    }

    /**
     * Classe base para Visões, gerenciando a hierarquia superView/subViews
     * e o pipeline canônico de renderização (display, displayBorder, displayView, displaySubviews).
     */
    public static class SmalltalkView implements ViewDependent {
        protected SmalltalkModel model;
        protected SmalltalkController controller;
        protected SmalltalkView superView;
        protected final List<SmalltalkView> subViews = new ArrayList<>();
        protected double relX, relY, relWidth, relHeight;
        protected int borderWidth = 1;

        public void setModelAndController(SmalltalkModel model, SmalltalkController controller) {
            if (this.model != null) {
                this.model.removeDependent(this);
            }
            this.model = model;
            if (this.model != null) {
                this.model.addDependent(this);
            }
            this.controller = controller;
            if (this.controller != null) {
                this.controller.setView(this);
                this.controller.setModel(model);
            }
        }

        public void addSubView(SmalltalkView subView, double x, double y, double w, double h) {
            subView.superView = this;
            subView.relX = x;
            subView.relY = y;
            subView.relWidth = w;
            subView.relHeight = h;
            this.subViews.add(subView);
        }

        public void display() {
            displayBorder();
            displayView();
            displaySubviews();
        }

        public void displayBorder() {
            // Renderização padrão de borda da janela
        }

        public void displayView() {
            // Renderização do conteúdo próprio
        }

        public void displaySubviews() {
            for (SmalltalkView sub : subViews) {
                sub.display();
            }
        }

        @Override
        public void update(SmalltalkModel model) {
            display();
        }

        @Override
        public void update(SmalltalkModel model, Object aspect) {
            display();
        }

        public boolean containsCursor(int screenX, int screenY, int parentWidth, int parentHeight) {
            int absoluteX = (int) (relX * parentWidth);
            int absoluteY = (int) (relY * parentHeight);
            int absoluteW = (int) (relWidth * parentWidth);
            int absoluteH = (int) (relHeight * parentHeight);
            return screenX >= absoluteX && screenX <= (absoluteX + absoluteW) &&
                   screenY >= absoluteY && screenY <= (absoluteY + absoluteH);
        }

        public SmalltalkController getController() {
            return controller;
        }
    }

    /**
     * TopView padrão representando uma janela gerenciável no desktop Smalltalk-80.
     */
    public static class StandardSystemView extends SmalltalkView {
        private String label;

        public StandardSystemView(String label) {
            this.label = label;
            this.relX = 0;
            this.relY = 0;
            this.relWidth = 1.0;
            this.relHeight = 1.0;
        }

        @Override
        public void displayBorder() {
            System.out.println("============================================================");
            System.out.println("| [Janela TopView]: " + label + " (Borda: " + borderWidth + "px) |");
            System.out.println("============================================================");
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * Pluggable View para seleção de itens em lista (SelectionInListView).
     */
    public static class SelectionInListView extends SmalltalkView {
        private final String aspectKey;

        public SelectionInListView(SmalltalkModel model, String aspectKey) {
            this.aspectKey = aspectKey;
            setModelAndController(model, new SmalltalkController());
        }

        @Override
        public void displayView() {
            if (model instanceof MethodListBrowserModel) {
                MethodListBrowserModel browser = (MethodListBrowserModel) model;
                System.out.println("  -> [SubView Superior: SelectionInListView] (aspect: #" + aspectKey + ")");
                System.out.println("     Itens disponíveis: " + browser.getMethodList());
                System.out.println("     Seleção ativa: [" + browser.getSelectedMethod() + "]");
            }
        }

        @Override
        public void update(SmalltalkModel m, Object aspect) {
            if (aspectKey.equals(aspect) || aspect == null) {
                displayView();
            }
        }
    }

    /**
     * Pluggable View para edição de texto/código (CodeView).
     */
    public static class CodeView extends SmalltalkView {
        private final String aspectKey;

        public CodeView(SmalltalkModel model, String aspectKey) {
            this.aspectKey = aspectKey;
            setModelAndController(model, new SmalltalkController());
        }

        @Override
        public void displayView() {
            if (model instanceof MethodListBrowserModel) {
                MethodListBrowserModel browser = (MethodListBrowserModel) model;
                System.out.println("  -> [SubView Inferior: CodeView] (aspect: #" + aspectKey + ")");
                String code = browser.getCurrentSourceCode();
                for (String line : code.split("\n")) {
                    System.out.println("     | " + line);
                }
            }
        }

        @Override
        public void update(SmalltalkModel m, Object aspect) {
            if (aspectKey.equals(aspect) || aspect == null) {
                displayView();
            }
        }
    }

    /**
     * Controlador base gerenciando conexões e tratamento de eventos de mouse.
     */
    public static class SmalltalkController {
        protected SmalltalkModel model;
        protected SmalltalkView view;

        public void setModel(SmalltalkModel model) {
            this.model = model;
        }

        public void setView(SmalltalkView view) {
            this.view = view;
        }

        public boolean isControlWanted(int mouseX, int mouseY, int w, int h) {
            return view != null && view.containsCursor(mouseX, mouseY, w, h);
        }

        public void redButtonActivity() {
            System.out.println("[Controller] Red Button pressionado: Seleção primária acionada.");
        }

        public void yellowButtonActivity() {
            System.out.println("[Controller] Yellow Button pressionado: Exibindo menu de contexto da subvisão.");
        }

        public void blueButtonActivity() {
            if (view != null && view.superView != null) {
                // Delegação transparente para o controlador da TopView
                System.out.println("[Controller] Roteando Blue Button para a TopView...");
                view.superView.getController().blueButtonActivity();
            } else {
                System.out.println("[Controller] Blue Button na TopView: Menu de Janela (Frame, Close, Collapse).");
            }
        }
    }

    /**
     * Controlador de TopView equivalente a StandardSystemController.
     */
    public static class StandardSystemController extends SmalltalkController {
        public void open() {
            System.out.println("[StandardSystemController] Iniciando o processo de enquadramento (framing) e exibição.");
            if (view != null) {
                view.display();
            }
        }

        @Override
        public void blueButtonActivity() {
            System.out.println("[StandardSystemController] Blue Button acionado no nível superior: [frame, close, collapse, under].");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DO MVC ORIGINAL SMALLTALK-80 ===\n");

        // 1. Instanciação da coleção de métodos e do modelo de domínio
        List<String> metodosIniciais = new ArrayList<>();
        metodosIniciais.add("changed");
        metodosIniciais.add("changed:");
        metodosIniciais.add("update:");
        MethodListBrowserModel browserModel = new MethodListBrowserModel(metodosIniciais);

        // 2. Instanciação da TopView e seu StandardSystemController correspondente
        StandardSystemView topView = new StandardSystemView("Method List Browser");
        StandardSystemController topController = new StandardSystemController();
        topView.setModelAndController(browserModel, topController);

        // 3. Adição das subviews conectadas como no código openListBrowserOn: do artigo
        // SubView superior ocupando 25% da altura vertical (SelectionInListView)
        SelectionInListView listView = new SelectionInListView(browserModel, "methodName");
        topView.addSubView(listView, 0.0, 0.0, 1.0, 0.25);

        // SubView inferior ocupando os 75% restantes da altura vertical (CodeView)
        CodeView codeView = new CodeView(browserModel, "text");
        topView.addSubView(codeView, 0.0, 0.25, 1.0, 0.75);

        // 4. Abertura da janela pelo controlador principal
        topController.open();

        // 5. Simulação de interação do usuário: seleção de um novo método na lista
        System.out.println("\n--- Usuário clica no método 'update:' na SelectionInListView ---");
        browserModel.setSelectedMethod("update:");

        // 6. Simulação de clique com botão azul na subview inferior
        System.out.println("\n--- Usuário clica com Botão Azul (gerenciamento de janela) dentro do CodeView ---");
        codeView.getController().blueButtonActivity();
    }
}
