import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Disciplina: Laboratorio de Programacao IV (4o Semestre - Sistemas de Informacao UniFEF)
 * Professor: Jefferson Passerini
 * Tema da Aula 00: Os Quatro Estados de um Arquivo, as Tres Areas do Git, .gitignore e Ciclo Basico.
 *
 * Como compilar: javac SimuladorCicloDeVidaGit.java
 * Como executar: java SimuladorCicloDeVidaGit
 *
 * Descricao:
 * Simula fielmente a transicao de estados dos arquivos entre:
 * - Working Directory (Diretorio de Trabalho no disco)
 * - Staging Area / Index (.git/index)
 * - Local Repository (.git/objects - HEAD)
 * - Remote Repository (origin/main no GitHub)
 *
 * Demonstra tambem:
 * 1. O funcionamento da barreira sanitaria .gitignore (incluindo a excecao !.env.example).
 * 2. O mito do .gitignore corretivo (arquivos ja rastreados continuam visiveis mesmo se adicionados ao .gitignore).
 * 3. A diferenca entre 'git diff' (Working vs Index) e 'git diff --staged' (Index vs HEAD).
 */
public class SimuladorCicloDeVidaGit {

    enum EstadoArquivo {
        IGNORED,
        UNTRACKED,
        MODIFIED,
        STAGED,
        COMMITTED
    }

    private final Map<String, String> workingDirectory = new LinkedHashMap<>();
    private final Map<String, String> stagingAreaIndex = new LinkedHashMap<>();
    private final Map<String, String> headCommitSnapshot = new LinkedHashMap<>();
    private final List<String> historicoCommitsLocal = new ArrayList<>();
    private final List<String> historicoCommitsRemotoOrigin = new ArrayList<>();
    private final List<String> regrasGitignore = new ArrayList<>();

    public void configurarRegrasGitignore(List<String> padroes) {
        regrasGitignore.clear();
        regrasGitignore.addAll(padroes);
    }

    /**
     * Avalia se um caminho de arquivo deve ser ignorado pelas regras do .gitignore.
     * Suporta sufixos de pasta (/), curingas (*.ext, .env.*) e operador de negacao (!).
     */
    public boolean correspondeAoGitignore(String caminhoArquivo) {
        boolean ignorado = false;
        for (String regraBruta : regrasGitignore) {
            String regra = regraBruta.trim();
            if (regra.isEmpty() || regra.startsWith("#")) {
                continue;
            }
            boolean ehNegacao = regra.startsWith("!");
            String padrao = ehNegacao ? regra.substring(1).trim() : regra;

            if (casaComPadrao(caminhoArquivo, padrao)) {
                ignorado = !ehNegacao;
            }
        }
        return ignorado;
    }

    private boolean casaComPadrao(String caminho, String padrao) {
        if (padrao.endsWith("/")) {
            return caminho.startsWith(padrao) || caminho.contains("/" + padrao);
        }
        if (padrao.startsWith("*.")) {
            String extensao = padrao.substring(1);
            return caminho.endsWith(extensao);
        }
        if (padrao.endsWith(".*")) {
            String prefixo = padrao.substring(0, padrao.length() - 1);
            return caminho.startsWith(prefixo);
        }
        return caminho.equals(padrao);
    }

    /**
     * Salvar um arquivo na IDE (Ctrl+S) altera apenas o Working Directory.
     * O arquivo nao fica automaticamente versionado.
     */
    public void salvarArquivoNoDisco(String caminho, String conteudo) {
        workingDirectory.put(caminho, conteudo);
        System.out.println("[Disco / Working Directory] Arquivo salvo: " + caminho);
    }

    /**
     * Calcula o estado operacional do arquivo segundo o modelo oficial do Git.
     * Nota importante da Aula 00: O .gitignore afeta SOMENTE arquivos nao rastreados (untracked)!
     */
    public EstadoArquivo diagnosticarEstado(String caminho) {
        boolean estaNoIndex = stagingAreaIndex.containsKey(caminho);
        boolean estaNoHead = headCommitSnapshot.containsKey(caminho);
        boolean jaRastreado = estaNoIndex || estaNoHead;

        if (!jaRastreado) {
            if (correspondeAoGitignore(caminho)) {
                return EstadoArquivo.IGNORED;
            }
            return EstadoArquivo.UNTRACKED;
        }

        String conteudoDisco = workingDirectory.get(caminho);
        String conteudoIndex = stagingAreaIndex.get(caminho);
        String conteudoHead = headCommitSnapshot.get(caminho);

        if (!Objects.equals(conteudoDisco, conteudoIndex)) {
            return EstadoArquivo.MODIFIED;
        }
        if (!Objects.equals(conteudoIndex, conteudoHead)) {
            return EstadoArquivo.STAGED;
        }
        return EstadoArquivo.COMMITTED;
    }

    /**
     * Comando git add <arquivo>: transfere o estado exato do arquivo do Working Directory para o Index.
     */
    public void gitAdd(String caminho) {
        if (!workingDirectory.containsKey(caminho)) {
            System.out.println("fatal: pathspec '" + caminho + "' did not match any files");
            return;
        }
        boolean jaRastreado = stagingAreaIndex.containsKey(caminho) || headCommitSnapshot.containsKey(caminho);
        if (!jaRastreado && correspondeAoGitignore(caminho)) {
            System.out.println("The following paths are ignored by one of your .gitignore files: " + caminho);
            return;
        }
        stagingAreaIndex.put(caminho, workingDirectory.get(caminho));
        System.out.println("[git add " + caminho + "] Alteracao preparada na Staging Area (Index).");
    }

    /**
     * Comando git rm --cached <arquivo>: remove o arquivo do Index sem apaga-lo do disco fisico.
     * Usado para corrigir arquivos (.idea, target, .env) adicionados antes do .gitignore.
     */
    public void gitRmCached(String caminho) {
        stagingAreaIndex.remove(caminho);
        headCommitSnapshot.remove(caminho);
        System.out.println("[git rm --cached " + caminho + "] Removido do rastreamento do Git, mantido no disco.");
    }

    /**
     * Comando git status: inspeciona Working Directory, Staging Area e divergencia com origin/main.
     */
    public void gitStatus() {
        System.out.println("\n--- git status ---");
        System.out.println("On branch main");
        int commitsAFrente = historicoCommitsLocal.size() - historicoCommitsRemotoOrigin.size();
        if (commitsAFrente > 0) {
            System.out.println("Your branch is ahead of 'origin/main' by " + commitsAFrente + " commit(s).");
            System.out.println("  (use \"git push\" to publish your local commits)");
        } else if (!historicoCommitsLocal.isEmpty()) {
            System.out.println("Your branch is up to date with 'origin/main'.");
        }

        List<String> staged = new ArrayList<>();
        List<String> modified = new ArrayList<>();
        List<String> untracked = new ArrayList<>();

        Set<String> todosArquivos = new LinkedHashSet<>();
        todosArquivos.addAll(workingDirectory.keySet());
        todosArquivos.addAll(stagingAreaIndex.keySet());

        for (String arq : todosArquivos) {
            boolean noDisco = workingDirectory.containsKey(arq);
            boolean noIndex = stagingAreaIndex.containsKey(arq);
            boolean noHead = headCommitSnapshot.containsKey(arq);

            if (noIndex && !Objects.equals(stagingAreaIndex.get(arq), headCommitSnapshot.get(arq))) {
                String tipo = noHead ? "modified:   " : "new file:   ";
                staged.add(tipo + arq);
            }
            if (noIndex && noDisco && !Objects.equals(workingDirectory.get(arq), stagingAreaIndex.get(arq))) {
                modified.add("modified:   " + arq);
            }
            if (!noIndex && !noHead && noDisco && !correspondeAoGitignore(arq)) {
                untracked.add(arq);
            }
        }

        if (!staged.isEmpty()) {
            System.out.println("\nChanges to be committed:");
            for (String s : staged) {
                System.out.println("\t" + s);
            }
        }
        if (!modified.isEmpty()) {
            System.out.println("\nChanges not staged for commit:");
            for (String m : modified) {
                System.out.println("\t" + m);
            }
        }
        if (!untracked.isEmpty()) {
            System.out.println("\nUntracked files:");
            for (String u : untracked) {
                System.out.println("\t" + u);
            }
        }
        if (staged.isEmpty() && modified.isEmpty() && untracked.isEmpty()) {
            System.out.println("\nnothing to commit, working tree clean");
        }
        System.out.println("------------------");
    }

    /**
     * Demonstra a diferenca entre git diff (Working vs Index) e git diff --staged (Index vs HEAD).
     */
    public void gitDiff(boolean staged) {
        System.out.println("\n--- " + (staged ? "git diff --staged (Index vs HEAD)" : "git diff (Working Directory vs Index)") + " ---");
        Map<String, String> origem = staged ? headCommitSnapshot : stagingAreaIndex;
        Map<String, String> destino = staged ? stagingAreaIndex : workingDirectory;

        boolean encontrouDiferenca = false;
        for (String arq : destino.keySet()) {
            if (!staged && !stagingAreaIndex.containsKey(arq)) {
                continue; // Arquivos untracked nao aparecem no git diff comum
            }
            String antes = origem.get(arq);
            String depois = destino.get(arq);
            if (!Objects.equals(antes, depois)) {
                encontrouDiferenca = true;
                System.out.println("diff --git a/" + arq + " b/" + arq);
                if (antes != null) {
                    System.out.println("--- a/" + arq + "\n- " + antes.replace("\n", "\n- "));
                }
                if (depois != null) {
                    System.out.println("+++ b/" + arq + "\n+ " + depois.replace("\n", "\n+ "));
                }
            }
        }
        if (!encontrouDiferenca) {
            System.out.println("(Sem diferencas detectadas nesta comparacao)");
        }
    }

    public void gitCommit(String mensagem) {
        if (stagingAreaIndex.equals(headCommitSnapshot)) {
            System.out.println("[git commit] Nada preparado na Staging Area para commitar.");
            return;
        }
        headCommitSnapshot.clear();
        headCommitSnapshot.putAll(stagingAreaIndex);
        historicoCommitsLocal.add(mensagem);
        System.out.println("[git commit -m \"" + mensagem + "\"] Snapshot registrado no repositorio LOCAL (.git).");
    }

    public void gitPushOriginMain() {
        if (historicoCommitsLocal.isEmpty()) {
            System.out.println("error: src refspec main does not match any (Nenhum commit local existe ainda!)");
            return;
        }
        historicoCommitsRemotoOrigin.clear();
        historicoCommitsRemotoOrigin.addAll(historicoCommitsLocal);
        System.out.println("[git push -u origin main] Todos os " + historicoCommitsRemotoOrigin.size()
                + " commit(s) locais foram publicados no GitHub!");
    }

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" SIMULADOR DO CICLO DE VIDA DE ARQUIVOS E AREAS DO GIT - AULA 00");
        System.out.println("======================================================================\n");

        SimuladorCicloDeVidaGit git = new SimuladorCicloDeVidaGit();

        // 1. Tentativa de push antes do primeiro commit (Erro Comum #3 da Aula 00)
        System.out.println("1) Demonstrando erro ao tentar push em repositorio sem commits:");
        git.gitPushOriginMain();

        // 2. Configurando as regras do .gitignore da Aula 00
        git.configurarRegrasGitignore(List.of(
                ".idea/",
                "target/",
                "*.class",
                ".env",
                ".env.*",
                "!.env.example"
        ));

        // 3. Criando arquivos no Working Directory
        System.out.println("\n2) Criando arquivos no disco (Working Directory):");
        git.salvarArquivoNoDisco("README.md", "# Suporte OS 2026");
        git.salvarArquivoNoDisco(".gitignore", ".idea/\ntarget/\n.env\n!.env.example");
        git.salvarArquivoNoDisco(".env", "DB_PASSWORD=segredo_super_confidencial_123");
        git.salvarArquivoNoDisco(".env.example", "DB_PASSWORD= TroquePelaSuaSenhaLocal");
        git.salvarArquivoNoDisco("target/App.class", "CAFEBABE_BYTECODE_COMPILADO");

        System.out.println("\n3) Verificando estados antes do git add (Observe que .env e target/ sao ignorados,");
        System.out.println("   mas !.env.example passa pela regra de excecao!):");
        System.out.println("   Estado de .env         -> " + git.diagnosticarEstado(".env"));
        System.out.println("   Estado de target/App   -> " + git.diagnosticarEstado("target/App.class"));
        System.out.println("   Estado de .env.example -> " + git.diagnosticarEstado(".env.example"));
        git.gitStatus();

        // 4. Preparando arquivos explicitamente (Regra Didatica da Aula 00)
        System.out.println("\n4) Indexando arquivos explicitamente com git add:");
        git.gitAdd("README.md");
        git.gitAdd(".gitignore");
        git.gitAdd(".env.example");

        // 5. Modificando README.md novamente apos o git add para demonstrar que
        // o Index guarda a versao do momento do git add!
        System.out.println("\n5) Editando README.md apos o git add (fica Staged e Modified simultaneamente):");
        git.salvarArquivoNoDisco("README.md", "# Suporte OS 2026\n## Dominio Inicial: Produtos e Grupos");
        git.gitStatus();
        git.gitDiff(false); // Mostra diferenca entre Working Directory e Index
        git.gitDiff(true);  // Mostra diferenca entre Index e HEAD

        // 6. Atualizando o README.md no Index, realizando Commit Local e Push Remoto
        System.out.println("\n6) Atualizando Index, registrando Commit Local e publicando no GitHub:");
        git.gitAdd("README.md");
        git.gitCommit("Aula 00: inicia o repositorio do curso");
        git.gitStatus(); // Mostra que o commit esta local, 1 commit a frente de origin/main
        git.gitPushOriginMain();
        git.gitStatus();
    }
}
