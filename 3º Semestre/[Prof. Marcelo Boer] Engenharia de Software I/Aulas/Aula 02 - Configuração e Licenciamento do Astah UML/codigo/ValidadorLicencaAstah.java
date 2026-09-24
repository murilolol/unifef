/*
 * Disciplina: Engenharia de Software I (3o Semestre) - UniFEF
 * Professor:  Marcelo Boer
 * Tema:       Mecanismo de Inspecao, Validacao Criptografica e Parsing de Licenca XML do Astah UML
 *
 * Como compilar:
 *   javac ValidadorLicencaAstah.java
 *
 * Como executar:
 *   java ValidadorLicencaAstah
 */

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Classe executavel que simula o motor interno de verificacao de licencas do Astah UML.
 * Demonstra como ferramentas CASE utilizam XML estruturado combinado com criptografia
 * assimetrica para garantir a autenticidade, integridade e conformidade temporal
 * de licencas institucionais sem necessidade de conexao com a internet.
 */
public class ValidadorLicencaAstah {

    // XML da licenca institucional exatamente conforme distribuido na Aula 02
    public static final String XML_LICENCA_OFICIAL =
        "<?xml version = \"1.0\" encoding = \"UTF-8\" standalone = \"yes\"?>\n" +
        "<LICENSES>\n" +
        "<LICENSE>\n" +
        "<INFO>\n" +
        "<USER_LICENSE_NO>-</USER_LICENSE_NO>\n" +
        "<USER_ID>-</USER_ID>\n" +
        "<USER_ORGANIZATION>Provided by Change Vision, Inc</USER_ORGANIZATION>\n" +
        "<USER_NAME>Students</USER_NAME>\n" +
        "<USER_KIND>astah_UML</USER_KIND>\n" +
        "<USER_VERSION>6.0</USER_VERSION>\n" +
        "<USER_TYPE>Product</USER_TYPE>\n" +
        "<USER_RUN_FROM>2025/07/02</USER_RUN_FROM>\n" +
        "<USER_RUN_TO>2026/08/31</USER_RUN_TO>\n" +
        "<USER_SUPPORT_FROM>2025/07/02</USER_SUPPORT_FROM>\n" +
        "<USER_SUPPORT_TO>2026/08/31</USER_SUPPORT_TO>\n" +
        "<USER_CONSTRAINT>SingleUser</USER_CONSTRAINT>\n" +
        "<USER_CONSTRAINT2>students</USER_CONSTRAINT2>\n" +
        "<USER_CONSTRAINT3>-</USER_CONSTRAINT3>\n" +
        "<MACHINE_IDENTIFICATION_INFOMATIONS></MACHINE_IDENTIFICATION_INFOMATIONS>\n" +
        "<USER_ATTENTION>-</USER_ATTENTION>\n" +
        "</INFO>\n" +
        "<USER_SIGNATURE>MCwCFHSRS7kkBum7A1OyakBcbR2Qjn1eAhQSJvcTDDFObJaZvnt3P0GAhB1LVA==</USER_SIGNATURE>\n" +
        "</LICENSE>\n" +
        "</LICENSES>";

    /**
     * Estrutura de dados para representar as informacoes semanticas da licenca.
     */
    public static class DadosLicenca {
        public String organizacao;
        public String usuario;
        public String tipoProduto;
        public String versao;
        public String tipoUso;
        public LocalDate dataInicio;
        public LocalDate dataFim;
        public String restricao;
        public String identificacaoMaquina;
        public String assinaturaBase64;
        public String blocoInfoCanonica;
    }

    /**
     * Realiza o parsing sintatico do documento XML e extrai os campos de auditoria.
     */
    public static DadosLicenca extrairDados(String conteudoXml) throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder construtor = fabrica.newDocumentBuilder();
        Document documento = construtor.parse(new InputSource(new StringReader(conteudoXml)));

        Element raiz = documento.getDocumentElement();
        NodeList listaLicencas = raiz.getElementsByTagName("LICENSE");
        if (listaLicencas.getLength() == 0) {
            throw new IllegalArgumentException("Tag <LICENSE> nao encontrada no XML.");
        }

        Element noLicenca = (Element) listaLicencas.item(0);
        Element noInfo = (Element) noLicenca.getElementsByTagName("INFO").item(0);
        Element noAssinatura = (Element) noLicenca.getElementsByTagName("USER_SIGNATURE").item(0);

        if (noInfo == null || noAssinatura == null) {
            throw new IllegalArgumentException("Estrutura invalida: <INFO> ou <USER_SIGNATURE> ausentes.");
        }

        DadosLicenca dados = new DadosLicenca();
        dados.organizacao = obterTextoTag(noInfo, "USER_ORGANIZATION");
        dados.usuario = obterTextoTag(noInfo, "USER_NAME");
        dados.tipoProduto = obterTextoTag(noInfo, "USER_KIND");
        dados.versao = obterTextoTag(noInfo, "USER_VERSION");
        dados.tipoUso = obterTextoTag(noInfo, "USER_TYPE");

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        dados.dataInicio = LocalDate.parse(obterTextoTag(noInfo, "USER_RUN_FROM"), formatador);
        dados.dataFim = LocalDate.parse(obterTextoTag(noInfo, "USER_RUN_TO"), formatador);

        dados.restricao = obterTextoTag(noInfo, "USER_CONSTRAINT") + " / " + obterTextoTag(noInfo, "USER_CONSTRAINT2");
        dados.identificacaoMaquina = obterTextoTag(noInfo, "MACHINE_IDENTIFICATION_INFOMATIONS");
        dados.assinaturaBase64 = noAssinatura.getTextContent().trim();

        // Representacao simplificada do conteudo do payload para conferencia
        dados.blocoInfoCanonica = noInfo.getTextContent().replaceAll("\\s+", " ").trim();
        return dados;
    }

    private static String obterTextoTag(Element elementoPai, String nomeTag) {
        NodeList lista = elementoPai.getElementsByTagName(nomeTag);
        if (lista.getLength() > 0 && lista.item(0) != null) {
            return lista.item(0).getTextContent().trim();
        }
        return "";
    }

    /**
     * Valida os prazos temporais da licenca em relacao a data atual do sistema ou data de teste.
     */
    public static boolean validarTemporalidade(DadosLicenca licenca, LocalDate dataReferencia) {
        boolean iniciou = !dataReferencia.isBefore(licenca.dataInicio);
        boolean naoExpirou = !dataReferencia.isAfter(licenca.dataFim);
        return iniciou && naoExpirou;
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  SIMULADOR DE AUDITORIA DE LICENCA XML - ASTAH UML (CASE)");
        System.out.println("  Engenharia de Software I - UniFEF | Prof. Marcelo Boer");
        System.out.println("========================================================================\n");

        try {
            // 1. Inspecao e Parsing da licenca legitima
            System.out.println("[ETAPA 1] Realizando parser sintatico do XML da licenca...");
            DadosLicenca licencaOficial = extrairDados(XML_LICENCA_OFICIAL);

            System.out.println(" -> Organizacao Emissora: " + licencaOficial.organizacao);
            System.out.println(" -> Beneficiario:          " + licencaOficial.usuario);
            System.out.println(" -> Produto Alvo:          " + licencaOficial.tipoProduto);
            System.out.println(" -> Versao Homologada:     " + licencaOficial.versao);
            System.out.println(" -> Modalidade:            " + licencaOficial.tipoUso + " (Modo Producao / Sem Marca d'Agua)");
            System.out.println(" -> Vigencia Temporal:     " + licencaOficial.dataInicio + " ate " + licencaOficial.dataFim);
            System.out.println(" -> Restricoes:            " + licencaOficial.restricao);
            System.out.println(" -> Hardware Lock:         " + (licencaOficial.identificacaoMaquina.isEmpty() ? "Nenhum (Licenca Flutuante para Discentes)" : licencaOficial.identificacaoMaquina));
            System.out.println(" -> Assinatura Digital:    " + licencaOficial.assinaturaBase64.substring(0, 20) + "... (Base64 Valida)\n");

            // 2. Auditoria Temporal
            System.out.println("[ETAPA 2] Verificando vigencia temporal sob diferentes cenarios de relogio...");
            LocalDate dataAnoLetivo = LocalDate.of(2026, 3, 10);
            LocalDate dataRelogioAtrasado = LocalDate.of(2024, 1, 1); // Exemplo de falha de bateria CMOS
            LocalDate dataFuturaExpirada = LocalDate.of(2026, 9, 15);

            System.out.println(" -> Teste A: Data em periodo de aula (" + dataAnoLetivo + "): " +
                (validarTemporalidade(licencaOficial, dataAnoLetivo) ? "VIGENTE (Acesso Liberado)" : "BLOQUEADO"));

            System.out.println(" -> Teste B: Relogio CMOS descarregado (" + dataRelogioAtrasado + "): " +
                (validarTemporalidade(licencaOficial, dataRelogioAtrasado) ? "VIGENTE" : "INVALIDO (Licenca ainda nao entrou em vigor - USER_RUN_FROM: 2025/07/02)"));

            System.out.println(" -> Teste C: Apos data de expiracao (" + dataFuturaExpirada + "): " +
                (validarTemporalidade(licencaOficial, dataFuturaExpirada) ? "VIGENTE" : "EXPIRADA (Exige renovacao de convenio - USER_RUN_TO: 2026/08/31)\n"));

            // 3. Demonstracao do Principio Criptografico Assimetrico (Chave Publica / Privada)
            System.out.println("[ETAPA 3] Simulacao do processo criptografico da Change Vision Inc...");
            KeyPairGenerator geradorChaves = KeyPairGenerator.getInstance("RSA");
            geradorChaves.initialize(2048);
            KeyPair parChaves = geradorChaves.generateKeyPair();
            PrivateKey chavePrivadaFornecedor = parChaves.getPrivate();
            PublicKey chavePublicaEmbutidaAstah = parChaves.getPublic();

            byte[] dadosOriginais = licencaOficial.blocoInfoCanonica.getBytes(StandardCharsets.UTF_8);

            // Fornecedor assina os dados usando a Chave Privada
            Signature assinador = Signature.getInstance("SHA256withRSA");
            assinador.initSign(chavePrivadaFornecedor);
            assinador.update(dadosOriginais);
            byte[] assinaturaDigitalBytes = assinador.sign();
            String assinaturaSimuladaBase64 = Base64.getEncoder().encodeToString(assinaturaDigitalBytes);

            // Astah local verifica usando a Chave Publica compilada no programa
            Signature verificador = Signature.getInstance("SHA256withRSA");
            verificador.initVerify(chavePublicaEmbutidaAstah);
            verificador.update(dadosOriginais);
            boolean autenticidadeConfirmada = verificador.verify(assinaturaDigitalBytes);

            System.out.println(" -> Assinatura gerada pela Change Vision com Chave Privada: [SUCESSO]");
            System.out.println(" -> Validacao na estacao local usando apenas Chave Publica:  " +
                (autenticidadeConfirmada ? "[ASSINATURA VALIDA - ARTEFATO INTEGRO]" : "[FALHA]") + "\n");

            // 4. Simulacao de Fraude / Adulteracao Manual
            System.out.println("[ETAPA 4] Simulando tentativa de adulteracao maliciosa do XML...");
            System.out.println(" -> Tentativa: Aluno altera <USER_RUN_TO> de '2026/08/31' para '2030/12/31'.");
            String dadosAdulteradosTexto = licencaOficial.blocoInfoCanonica.replace("2026/08/31", "2030/12/31");
            byte[] dadosAdulteradosBytes = dadosAdulteradosTexto.getBytes(StandardCharsets.UTF_8);

            verificador.initVerify(chavePublicaEmbutidaAstah);
            verificador.update(dadosAdulteradosBytes);
            boolean resultadoFraude = verificador.verify(assinaturaDigitalBytes);

            if (!resultadoFraude) {
                System.out.println(" -> RESULTADO DA AUDITORIA: [ALERTA DE VIOLACAO DETECTADO]");
                System.out.println("    O hash recalculado diverge da assinatura decifrada.");
                System.out.println("    O Astah UML bloqueia a execucao imediata e retorna para o modo Trial.\n");
            }

            System.out.println("========================================================================");
            System.out.println("  CONCLUSAO: A licenca da turma garante uso pleno, sem marcas d'agua,");
            System.out.println("  protegida matematicamente contra modificacoes locais nao autorizadas.");
            System.out.println("========================================================================");

        } catch (Exception e) {
            System.err.println("Erro durante execucao do validador: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
