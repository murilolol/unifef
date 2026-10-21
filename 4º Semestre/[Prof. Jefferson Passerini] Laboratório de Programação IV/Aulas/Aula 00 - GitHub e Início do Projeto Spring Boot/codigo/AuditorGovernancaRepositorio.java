import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Disciplina: Laboratorio de Programacao IV (4o Semestre - Sistemas de Informacao UniFEF)
 * Professor: Jefferson Passerini
 * Tema da Aula 00: Auditoria de Governanca (.editorconfig, .gitignore, README.md) e Prevencao de Vazamento de Segredos.
 *
 * Como compilar: javac AuditorGovernancaRepositorio.java
 * Como executar: java AuditorGovernancaRepositorio
 *
 * Descricao:
 * Simula um verificador de conformidade pre-commit e pre-push para o projeto Suporte OS 2026:
 * 1. Valida se o arquivo .editorconfig define UTF-8, quebras de linha LF (evitando conflitos CRLF no Windows),
 *    indentacao de 4 espacos para *.java e desativacao de trim_trailing_whitespace para *.md.
 * 2. Valida se o .gitignore protege contra artefatos de compilacao (target/, *.class), metadados de IDE (.idea/)
 *    e arquivos de variaveis de ambiente (.env).
 * 3. Executa Secret Scanning (varredura de segredos) para barrar commits contendo chaves privadas SSH (id_ed25519),
 *    Personal Access Tokens (PAT) do GitHub ou senhas de banco de dados hardcoded.
 */
public class AuditorGovernancaRepositorio {

    /**
     * Resultado estruturado da auditoria de seguranca e governanca.
     */
    static class RelatorioAuditoria {
        private final List<String> conformidades = new ArrayList<>();
        private final List<String> violacoesCriticas = new ArrayList<>();

        public void registrarOk(String mensagem) {
            conformidades.add(mensagem);
        }

        public void registrarViolacao(String mensagem) {
            violacoesCriticas.add(mensagem);
        }

        public boolean aprovado() {
            return violacoesCriticas.isEmpty();
        }

        public void imprimirRelatorio(String tituloCenario) {
            System.out.println("\n======================================================================");
            System.out.println(" RELATORIO DE AUDITORIA: " + tituloCenario);
            System.out.println("======================================================================");
            for (String ok : conformidades) {
                System.out.println("  [OK] " + ok);
            }
            for (String erro : violacoesCriticas) {
                System.out.println("  [VIOLACAO CRITICA] " + erro);
            }
            System.out.println("----------------------------------------------------------------------");
            if (aprovado()) {
                System.out.println("  STATUS FINAL: APROVADO PARA COMMIT E PUSH NO GITHUB");
            } else {
                System.out.println("  STATUS FINAL: BLOQUEADO! Corrija as " + violacoesCriticas.size()
                        + " violacao(oes) antes de prosseguir.");
            }
            System.out.println("======================================================================");
        }
    }

    // Padroes de expressoes regulares para deteccao de credenciais vazadas (Secret Scanning)
    private static final Pattern PADRAO_CHAVE_PRIVADA_SSH =
            Pattern.compile("-----BEGIN (OPENSSH|RSA|EC) PRIVATE KEY-----");
    private static final Pattern PADRAO_GITHUB_PAT =
            Pattern.compile("(ghp_[A-Za-z0-9]{20,}|github_pat_[A-Za-z0-9_]{20,})");
    private static final Pattern PADRAO_SENHA_EXPOSTA =
            Pattern.compile("(?i)(password|senha|secret|api_key)\\s*[:=]\\s*['\"]?(?!TroquePelaSuaSenhaLocal|ficticio|exemplo)[A-Za-z0-9@#$%!^&*_-]{6,}");

    /**
     * Audita o conjunto de arquivos preparados na Staging Area antes de permitir o commit.
     */
    public RelatorioAuditoria auditarStagingArea(Map<String, String> arquivosStaged) {
        RelatorioAuditoria relatorio = new RelatorioAuditoria();

        // 1. Verifica presenca dos 3 arquivos obrigatorios de governanca da Aula 00
        validarPresencaGovernanca(arquivosStaged, relatorio);

        // 2. Audita diretivas do .editorconfig
        if (arquivosStaged.containsKey(".editorconfig")) {
            validarEditorConfig(arquivosStaged.get(".editorconfig"), relatorio);
        }

        // 3. Audita regras sanitarias do .gitignore
        if (arquivosStaged.containsKey(".gitignore")) {
            validarGitignore(arquivosStaged.get(".gitignore"), relatorio);
        }

        // 4. Verifica higienizacao de quebras de linha (LF vs CRLF) e Secret Scanning em todos os arquivos
        for (Map.Entry<String, String> entrada : arquivosStaged.entrySet()) {
            String nomeArquivo = entrada.getKey();
            String conteudo = entrada.getValue();

            // Verifica contaminacao por quebras de linha CRLF (\r\n)
            if (conteudo.contains("\r\n")) {
                relatorio.registrarViolacao("O arquivo '" + nomeArquivo
                        + "' contem quebras de linha CRLF (\\r\\n). Padronize para LF (\\n) conforme .editorconfig.");
            }

            // Verifica se artefatos proibidos entraram no Staging
            if (nomeArquivo.startsWith(".idea/") || nomeArquivo.startsWith("target/") || nomeArquivo.endsWith(".class")) {
                relatorio.registrarViolacao("Artefato de IDE ou compilacao ('" + nomeArquivo
                        + "') detectado na Staging Area! Execute: git rm -r --cached " + nomeArquivo);
            }

            // Verifica se arquivo .env real ou chave privada SSH foi indexado
            if (nomeArquivo.equals(".env") || nomeArquivo.endsWith("id_ed25519") || nomeArquivo.endsWith("id_rsa")) {
                relatorio.registrarViolacao("Arquivo confidencial ('" + nomeArquivo
                        + "') jamais deve ser versionado! Remova do Index imediatamente.");
            }

            // Executa Secret Scanning no conteudo do arquivo
            inspecionarSegredosNoConteudo(nomeArquivo, conteudo, relatorio);
        }

        return relatorio;
    }

    private void validarPresencaGovernanca(Map<String, String> arquivos, RelatorioAuditoria relatorio) {
        String[] obrigatorios = {"README.md", ".editorconfig", ".gitignore"};
        for (String arq : obrigatorios) {
            if (arquivos.containsKey(arq)) {
                relatorio.registrarOk("Arquivo base de governanca presente: " + arq);
            } else {
                relatorio.registrarViolacao("Arquivo obrigatorio ausente na raiz do projeto: " + arq);
            }
        }
    }

    private void validarEditorConfig(String conteudo, RelatorioAuditoria relatorio) {
        if (conteudo.contains("root = true")) {
            relatorio.registrarOk(".editorconfig define 'root = true' na raiz.");
        } else {
            relatorio.registrarViolacao(".editorconfig nao contem 'root = true'.");
        }
        if (conteudo.contains("charset = utf-8") && conteudo.contains("end_of_line = lf")) {
            relatorio.registrarOk(".editorconfig padroniza codificacao UTF-8 e quebra de linha LF.");
        } else {
            relatorio.registrarViolacao(".editorconfig deve especificar 'charset = utf-8' e 'end_of_line = lf'.");
        }
        if (conteudo.contains("[*.java]") && conteudo.contains("indent_size = 4")) {
            relatorio.registrarOk(".editorconfig define indentacao de 4 espacos para arquivos Java.");
        } else {
            relatorio.registrarViolacao(".editorconfig nao define indentacao de 4 espacos para [*.java].");
        }
        if (conteudo.contains("[*.md]") && conteudo.contains("trim_trailing_whitespace = false")) {
            relatorio.registrarOk(".editorconfig preserva espacos duplos de quebra de linha em Markdown ([*.md]).");
        } else {
            relatorio.registrarViolacao(".editorconfig deve desativar trim_trailing_whitespace para [*.md].");
        }
    }

    private void validarGitignore(String conteudo, RelatorioAuditoria relatorio) {
        String[] regrasEssenciais = {".idea/", "target/", "*.class", ".env", "!.env.example"};
        for (String regra : regrasEssenciais) {
            if (conteudo.contains(regra)) {
                relatorio.registrarOk(".gitignore contem a regra essencial: " + regra);
            } else {
                relatorio.registrarViolacao(".gitignore nao contempla a regra obrigatoria: " + regra);
            }
        }
    }

    private void inspecionarSegredosNoConteudo(String nomeArquivo, String conteudo, RelatorioAuditoria relatorio) {
        if (PADRAO_CHAVE_PRIVADA_SSH.matcher(conteudo).find()) {
            relatorio.registrarViolacao("VAZAMENTO DE CHAVE PRIVADA SSH detectado em '" + nomeArquivo
                    + "'! Jamais publique id_ed25519 ou id_rsa. Cadastre no GitHub apenas a chave publica (.pub).");
        }
        if (PADRAO_GITHUB_PAT.matcher(conteudo).find()) {
            relatorio.registrarViolacao("VAZAMENTO DE PERSONAL ACCESS TOKEN (PAT) do GitHub detectado em '"
                    + nomeArquivo + "'! Revogue o token imediatamente na plataforma.");
        }
        if (PADRAO_SENHA_EXPOSTA.matcher(conteudo).find()) {
            relatorio.registrarViolacao("Credencial ou senha explicita detectada em '" + nomeArquivo
                    + "'! Utilize variaveis de ambiente e mantenha apenas valores ficticios no .env.example.");
        }
    }

    public static void main(String[] args) {
        AuditorGovernancaRepositorio auditor = new AuditorGovernancaRepositorio();

        // CENARIO 1: Desenvolvedor descuidado que usou 'git add .' sem revisar (Armadilhas da Aula 00)
        Map<String, String> cenarioInseguro = new LinkedHashMap<>();
        cenarioInseguro.put("README.md", "# Suporte OS 2026\r\nTexto salvo com CRLF no Windows.");
        cenarioInseguro.put(".editorconfig", "root = true\n[*]\ncharset = utf-8\nend_of_line = lf\n[*.java]\nindent_size = 4\n[*.md]\ntrim_trailing_whitespace = false\n");
        cenarioInseguro.put(".gitignore", ".idea/\ntarget/\n*.class\n.env\n!.env.example\n");
        cenarioInseguro.put("target/Main.class", "BYTECODE_BINARIO");
        cenarioInseguro.put(".env", "DB_PASSWORD=postgres_prod_998877");
        cenarioInseguro.put("id_ed25519", "-----BEGIN OPENSSH PRIVATE KEY-----\nb3BlbnNzaC1rZXktdjEAAAAABG5vbmU=");

        RelatorioAuditoria relatorio1 = auditor.auditarStagingArea(cenarioInseguro);
        relatorio1.imprimirRelatorio("CENARIO 1 - Commit Acidental com Segredos, Binarios e CRLF");

        // CENARIO 2: Repositorio oficial Suporte OS 2026 higienizado conforme a Aula 00
        Map<String, String> cenarioOficialAula00 = new LinkedHashMap<>();
        cenarioOficialAula00.put("README.md",
                "# Suporte OS 2026\n\nAPI didatica desenvolvida na disciplina de Programacao.\n");
        cenarioOficialAula00.put(".editorconfig",
                "root = true\n\n[*]\ncharset = utf-8\nend_of_line = lf\ninsert_final_newline = true\n"
                + "trim_trailing_whitespace = true\n\n[*.java]\nindent_style = space\nindent_size = 4\n\n"
                + "[*.{xml,yml,yaml,properties,md}]\nindent_style = space\nindent_size = 2\n\n"
                + "[*.md]\ntrim_trailing_whitespace = false\n");
        cenarioOficialAula00.put(".gitignore",
                "# IntelliJ IDEA\n.idea/\n*.iml\n\n# Maven e Java\ntarget/\n*.class\n*.jar\n\n"
                + "# Variaveis de ambiente\n.env\n.env.*\n!.env.example\n");
        cenarioOficialAula00.put(".env.example",
                "DB_PASSWORD=TroquePelaSuaSenhaLocal\n");

        RelatorioAuditoria relatorio2 = auditor.auditarStagingArea(cenarioOficialAula00);
        relatorio2.imprimirRelatorio("CENARIO 2 - Repositorio Oficial Suporte OS 2026 (Padrao Aula 00)");
    }
}
