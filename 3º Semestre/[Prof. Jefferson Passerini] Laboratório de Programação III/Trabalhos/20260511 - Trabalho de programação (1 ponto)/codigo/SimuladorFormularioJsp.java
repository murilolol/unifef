/**
 * Disciplina: Laboratório de Programação III (3º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Processamento e Retorno de Validação de E-mail no Ciclo JSP / Servlet
 * 
 * Como compilar:
 *   javac ValidadorEmail.java SimuladorFormularioJsp.java
 * 
 * Como executar:
 *   java SimuladorFormularioJsp
 */

import java.util.HashMap;
import java.util.Map;

public class SimuladorFormularioJsp {

    /**
     * Simulação de uma requisição HTTP contendo parâmetros do formulário e atributos de requisição.
     */
    public static class RequisicaoSimulada {
        private final Map<String, String> parametros = new HashMap<>();
        private final Map<String, Object> atributos = new HashMap<>();
        private String destinoForward = "";

        public void setParametro(String chave, String valor) {
            parametros.put(chave, valor);
        }

        public String getParameter(String chave) {
            return parametros.get(chave);
        }

        public void setAttribute(String chave, Object valor) {
            atributos.put(chave, valor);
        }

        public Object getAttribute(String chave) {
            return atributos.get(chave);
        }

        public void forward(String jspDestino) {
            this.destinoForward = jspDestino;
        }

        public String getDestinoForward() {
            return destinoForward;
        }
    }

    /**
     * Simula o método doPost() de uma Servlet Java conectada à página JSP.
     */
    public static void processarRequisicao(RequisicaoSimulada request) {
        // Leitura do campo 'email' vindo do <input type="text" name="email" /> da JSP
        String emailInformado = request.getParameter("email");
        String nomeInformado = request.getParameter("nome");

        System.out.println("[SERVLET] Recebida submissão do formulário:");
        System.out.println("          Nome : " + nomeInformado);
        System.out.println("          E-mail: " + emailInformado);

        // Execução da validação através da classe ValidadorEmail
        ValidadorEmail.ResultadoValidacao resultado = ValidadorEmail.validar(emailInformado);

        if (!resultado.isValido()) {
            // Cenário de erro: alimenta atributos para a página JSP reexibir os avisos
            request.setAttribute("temErros", true);
            request.setAttribute("mensagensErro", resultado.getErros());
            // Mantém os dados preenchidos para não forçar o usuário a redigitar tudo
            request.setAttribute("valorEmail", emailInformado);
            request.setAttribute("valorNome", nomeInformado);
            request.forward("/formularioCadastro.jsp");
            System.out.println("[SERVLET] Validação falhou! Redirecionando de volta para: " + request.getDestinoForward());
        } else {
            // Cenário de sucesso: avança para a página de confirmação/sucesso
            request.setAttribute("temErros", false);
            request.setAttribute("emailConfirmado", resultado.getEmailNormalizado());
            request.setAttribute("mensagemSucesso", "Cadastro realizado com sucesso!");
            request.forward("/sucesso.jsp");
            System.out.println("[SERVLET] Validação concluída com sucesso! Encaminhando para: " + request.getDestinoForward());
        }
    }

    /**
     * Simulação de renderização da página JSP recebendo os dados do request.
     */
    public static void renderizarJsp(RequisicaoSimulada request) {
        System.out.println("\n--- [JSP RENDER: " + request.getDestinoForward() + "] ---");
        Boolean temErros = (Boolean) request.getAttribute("temErros");
        
        if (Boolean.TRUE.equals(temErros)) {
            System.out.println("<div class=\"alerta-erro\">");
            System.out.println("   <h4>Por favor, corrija os erros abaixo:</h4>");
            @SuppressWarnings("unchecked")
            java.util.List<String> erros = (java.util.List<String>) request.getAttribute("mensagensErro");
            for (String erro : erros) {
                System.out.println("   <p class=\"erro\">* " + erro + "</p>");
            }
            System.out.println("</div>");
            System.out.println("<input type=\"text\" name=\"email\" value=\"" + request.getAttribute("valorEmail") + "\" class=\"borda-vermelha\" />");
        } else {
            System.out.println("<div class=\"alerta-sucesso\">");
            System.out.println("   <h3>" + request.getAttribute("mensagemSucesso") + "</h3>");
            System.out.println("   <p>E-mail cadastrado: " + request.getAttribute("emailConfirmado") + "</p>");
            System.out.println("</div>");
        }
        System.out.println("---------------------------------------------------\n");
    }

    public static void main(String[] args) {
        System.out.println("=======================================================================");
        System.out.println("   SIMULAÇÃO DO CICLO JSP / SERVLET - PROCESSAMENTO DE VALIDAÇÕES");
        System.out.println("=======================================================================\n");

        // Cenário 1: Usuário submete e-mail inválido
        System.out.println(">>> CENÁRIO 1: Envio de formulário com e-mail incorreto");
        RequisicaoSimulada req1 = new RequisicaoSimulada();
        req1.setParametro("nome", "Carlos Eduardo");
        req1.setParametro("email", "carlos.email_invalido@");
        processarRequisicao(req1);
        renderizarJsp(req1);

        // Cenário 2: Usuário submete e-mail válido
        System.out.println(">>> CENÁRIO 2: Envio de formulário com e-mail devidamente formatado");
        RequisicaoSimulada req2 = new RequisicaoSimulada();
        req2.setParametro("nome", "Mariana Souza");
        req2.setParametro("email", "mariana.souza@unifef.edu.br");
        processarRequisicao(req2);
        renderizarJsp(req2);
    }
}
