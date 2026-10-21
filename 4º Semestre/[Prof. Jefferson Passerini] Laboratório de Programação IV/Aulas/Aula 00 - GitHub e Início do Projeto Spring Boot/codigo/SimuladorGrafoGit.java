import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Disciplina: Laboratorio de Programacao IV (4o Semestre - Sistemas de Informacao UniFEF)
 * Professor: Jefferson Passerini
 * Tema da Aula 00: Modelagem de Commits, Branches, Tags Anotadas e Grafo Dirigido Aciclico (DAG) no Git.
 *
 * Como compilar: javac SimuladorGrafoGit.java
 * Como executar: java SimuladorGrafoGit
 *
 * Descricao:
 * Este programa implementa em memoria a estrutura de dados central do Git:
 * 1. Objetos Commit imutaveis identificados por hash criptografico (SHA-1) contendo snapshot completo.
 * 2. Grafo Aciclico Dirigido (DAG) onde cada commit aponta para seus commits pais.
 * 3. Branches como ponteiros moveis (referencias dinamicas) que avancam a cada commit.
 * 4. Tags Anotadas como objetos imutaveis que marcam pontos de quebra pedagogicos (ex: aula-00-inicio).
 * 5. Ponteiro especial HEAD indicando a branch ativa no diretorio de trabalho.
 */
public class SimuladorGrafoGit {

    /**
     * Representa um Commit imutavel no Grafo Aciclico Dirigido (DAG) do Git.
     * Diferente de sistemas legados baseados em deltas (diferencas linha a linha),
     * cada commit referencia um snapshot completo da arvore de arquivos.
     */
    static final class Commit {
        private final String hash;
        private final List<String> hashesPais;
        private final String autorNome;
        private final String autorEmail;
        private final String dataHora;
        private final String mensagem;
        private final Map<String, String> snapshotArquivos;

        public Commit(List<String> hashesPais, String autorNome, String autorEmail,
                      String mensagem, Map<String, String> arquivosAtuais) {
            this.hashesPais = Collections.unmodifiableList(new ArrayList<>(hashesPais));
            this.autorNome = autorNome;
            this.autorEmail = autorEmail;
            this.dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.mensagem = mensagem;
            // Snapshot imutavel da arvore de arquivos naquele instante
            this.snapshotArquivos = Collections.unmodifiableMap(new TreeMap<>(arquivosAtuais));
            this.hash = calcularHashSha1();
        }

        /**
         * Calcula o hash SHA-1 de 40 caracteres hexadecimais a partir do conteudo do snapshot,
         * dos metadados de autoria e dos hashes dos commits pais. Qualquer alteracao muda o hash.
         */
        private String calcularHashSha1() {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                StringBuilder payload = new StringBuilder();
                payload.append("parents:").append(hashesPais).append("\n");
                payload.append("author:").append(autorNome).append(" <").append(autorEmail).append(">\n");
                payload.append("date:").append(dataHora).append("\n");
                payload.append("message:").append(mensagem).append("\n");
                for (Map.Entry<String, String> arquivo : snapshotArquivos.entrySet()) {
                    payload.append("blob:").append(arquivo.getKey()).append("=").append(arquivo.getValue()).append("\n");
                }
                byte[] digest = md.digest(payload.toString().getBytes(StandardCharsets.UTF_8));
                StringBuilder hex = new StringBuilder();
                for (byte b : digest) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Algoritmo SHA-1 indisponivel no ambiente Java", e);
            }
        }

        public String getHash() {
            return hash;
        }

        public String getHashCurto() {
            return hash.substring(0, 7);
        }

        public List<String> getHashesPais() {
            return hashesPais;
        }

        public String getAutorNome() {
            return autorNome;
        }

        public String getAutorEmail() {
            return autorEmail;
        }

        public String getDataHora() {
            return dataHora;
        }

        public String getMensagem() {
            return mensagem;
        }

        public Map<String, String> getSnapshotArquivos() {
            return snapshotArquivos;
        }
    }

    /**
     * Representa uma Tag Anotada (git tag -a). Possui hash proprio, autor, data,
     * mensagem explicativa e aponta permanentemente para um commit especifico.
     */
    static final class TagAnotada {
        private final String nomeTag;
        private final String hashCommitAlvo;
        private final String taggerNome;
        private final String taggerEmail;
        private final String dataHora;
        private final String mensagemAnotacao;
        private final String hashObjetoTag;

        public TagAnotada(String nomeTag, String hashCommitAlvo, String taggerNome,
                          String taggerEmail, String mensagemAnotacao) {
            this.nomeTag = nomeTag;
            this.hashCommitAlvo = hashCommitAlvo;
            this.taggerNome = taggerNome;
            this.taggerEmail = taggerEmail;
            this.dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.mensagemAnotacao = mensagemAnotacao;
            this.hashObjetoTag = gerarHashTag();
        }

        private String gerarHashTag() {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                String dados = "object " + hashCommitAlvo + "\ntag " + nomeTag + "\ntagger "
                        + taggerNome + " <" + taggerEmail + ">\n\n" + mensagemAnotacao;
                byte[] digest = md.digest(dados.getBytes(StandardCharsets.UTF_8));
                StringBuilder hex = new StringBuilder();
                for (byte b : digest) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }

        public String getNomeTag() {
            return nomeTag;
        }

        public String getHashCommitAlvo() {
            return hashCommitAlvo;
        }

        public void exibirDetalhesCompletos(Commit commitAlvo) {
            System.out.println("tag " + nomeTag + " (objeto tag: " + hashObjetoTag.substring(0, 7) + ")");
            System.out.println("Tagger: " + taggerNome + " <" + taggerEmail + ">");
            System.out.println("Date:   " + dataHora);
            System.out.println("\n    " + mensagemAnotacao + "\n");
            System.out.println("commit " + commitAlvo.getHash());
            System.out.println("Author: " + commitAlvo.getAutorNome() + " <" + commitAlvo.getAutorEmail() + ">");
            System.out.println("\n    " + commitAlvo.getMensagem());
        }
    }

    /**
     * Motor simplificado do repositorio Git que gerencia o DAG de commits,
     * ponteiros de branches locais/remotas, tags anotadas e o ponteiro HEAD.
     */
    static class RepositorioGitDAG {
        private final Map<String, Commit> bancoObjetosCommits = new LinkedHashMap<>();
        private final Map<String, String> branchesLocais = new LinkedHashMap<>();
        private final Map<String, String> branchesRemotasOrigin = new LinkedHashMap<>();
        private final Map<String, TagAnotada> tagsAnotadas = new LinkedHashMap<>();
        private String branchAtivaHead = "main";
        private String userName = "Jefferson Passerini";
        private String userEmail = "jefferson.passerini@unifef.edu.br";

        public RepositorioGitDAG() {
            // Inicializa a branch main ainda sem commits (como apos clonar repositorio vazio)
            branchesLocais.put("main", null);
        }

        public void configurarIdentidade(String nome, String email) {
            this.userName = nome;
            this.userEmail = email;
            System.out.println("[git config] Identidade configurada: " + nome + " <" + email + ">");
        }

        public String registrarCommit(String mensagem, Map<String, String> snapshotArquivos) {
            String hashPaiAtual = branchesLocais.get(branchAtivaHead);
            List<String> pais = new ArrayList<>();
            if (hashPaiAtual != null) {
                pais.add(hashPaiAtual);
            }
            Commit novoCommit = new Commit(pais, userName, userEmail, mensagem, snapshotArquivos);
            bancoObjetosCommits.put(novoCommit.getHash(), novoCommit);
            // O ponteiro da branch ativa avanca automaticamente para o novo commit
            branchesLocais.put(branchAtivaHead, novoCommit.getHash());
            System.out.println("[" + branchAtivaHead + " " + novoCommit.getHashCurto() + "] " + mensagem);
            return novoCommit.getHash();
        }

        public void criarTagAnotada(String nomeTag, String mensagem) {
            String hashAtual = branchesLocais.get(branchAtivaHead);
            if (hashAtual == null) {
                throw new IllegalStateException("Nao ha commits na branch atual para ancorar a tag.");
            }
            TagAnotada tag = new TagAnotada(nomeTag, hashAtual, userName, userEmail, mensagem);
            tagsAnotadas.put(nomeTag, tag);
            System.out.println("[git tag -a] Tag anotada '" + nomeTag + "' ancorada de forma estatica no commit "
                    + hashAtual.substring(0, 7));
        }

        public void sincronizarPushOrigin(String nomeBranch) {
            String hashLocal = branchesLocais.get(nomeBranch);
            branchesRemotasOrigin.put("origin/" + nomeBranch, hashLocal);
            System.out.println("[git push -u origin " + nomeBranch + "] Remoto origin/" + nomeBranch
                    + " atualizado para " + hashLocal.substring(0, 7));
        }

        public void criarEMudarBranch(String novaBranch) {
            String hashBase = branchesLocais.get(branchAtivaHead);
            branchesLocais.put(novaBranch, hashBase);
            branchAtivaHead = novaBranch;
            System.out.println("[git switch -c " + novaBranch + "] Switched to a new branch '" + novaBranch + "'");
        }

        public void mostrarBranchAtual() {
            System.out.println("[git branch --show-current] " + branchAtivaHead);
        }

        public void exibirShowTag(String nomeTag) {
            TagAnotada tag = tagsAnotadas.get(nomeTag);
            if (tag != null) {
                Commit commitAlvo = bancoObjetosCommits.get(tag.getHashCommitAlvo());
                tag.exibirDetalhesCompletos(commitAlvo);
            }
        }

        /**
         * Percorre o Grafo Aciclico Dirigido (DAG) a partir do commit apontado pelo HEAD
         * ate o commit raiz, exibindo decoradores de branches, tags e remotos.
         */
        public void exibirLogOnelineDecorate() {
            System.out.println("\n=== Saida de: git log --oneline --decorate ===");
            String cursorHash = branchesLocais.get(branchAtivaHead);
            while (cursorHash != null) {
                Commit c = bancoObjetosCommits.get(cursorHash);
                List<String> decoradores = new ArrayList<>();

                for (Map.Entry<String, String> b : branchesLocais.entrySet()) {
                    if (cursorHash.equals(b.getValue())) {
                        if (b.getKey().equals(branchAtivaHead)) {
                            decoradores.add("HEAD -> " + b.getKey());
                        } else {
                            decoradores.add(b.getKey());
                        }
                    }
                }
                for (TagAnotada t : tagsAnotadas.values()) {
                    if (cursorHash.equals(t.getHashCommitAlvo())) {
                        decoradores.add("tag: " + t.getNomeTag());
                    }
                }
                for (Map.Entry<String, String> r : branchesRemotasOrigin.entrySet()) {
                    if (cursorHash.equals(r.getValue())) {
                        decoradores.add(r.getKey());
                    }
                }

                String infoDecoradores = decoradores.isEmpty() ? "" : " (" + String.join(", ", decoradores) + ")";
                System.out.println(c.getHashCurto() + infoDecoradores + " " + c.getMensagem());

                // Navega para o commit pai no DAG
                cursorHash = c.getHashesPais().isEmpty() ? null : c.getHashesPais().get(0);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" SIMULADOR DO GRAFO ACICLICO DIRIGIDO (DAG) DO GIT - SUPORTE OS 2026");
        System.out.println("======================================================================\n");

        RepositorioGitDAG repo = new RepositorioGitDAG();
        repo.configurarIdentidade("Jefferson Passerini", "jefferson.passerini@unifef.edu.br");

        // 1. Primeiro commit na branch main com os 3 arquivos de governanca da Aula 00
        Map<String, String> arquivosIniciais = new LinkedHashMap<>();
        arquivosIniciais.put("README.md", "# Suporte OS 2026\nAPI didatica de Sistemas de Informacao.");
        arquivosIniciais.put(".editorconfig", "root = true\n[*]\ncharset = utf-8\nend_of_line = lf");
        arquivosIniciais.put(".gitignore", ".idea/\ntarget/\n.env\n!.env.example");

        System.out.println("\n--- Passo 1: Commit inicial e Tag Anotada na branch main ---");
        repo.registrarCommit("Aula 00: inicia o repositorio do curso", arquivosIniciais);
        repo.criarTagAnotada("aula-00-inicio", "Conclusao da Aula 00: Repositorio base configurado");
        repo.sincronizarPushOrigin("main");

        repo.exibirLogOnelineDecorate();

        // 2. Exibindo os metadados internos da tag anotada (git show aula-00-inicio)
        System.out.println("\n--- Passo 2: Inspecao da Tag Anotada (git show aula-00-inicio) ---");
        repo.exibirShowTag("aula-00-inicio");

        // 3. Reproduzindo a Atividade Orientada (Exercicio 2 e 3 da Aula 00)
        System.out.println("\n--- Passo 3: Isolamento em Branch Local (atividade/aula-00) ---");
        repo.criarEMudarBranch("atividade/aula-00");
        repo.mostrarBranchAtual();

        Map<String, String> arquivosAtividade = new LinkedHashMap<>(arquivosIniciais);
        arquivosAtividade.put("README.md", arquivosIniciais.get("README.md")
                + "\n\n## Aprendizados da Aula 00\nGit e o motor local VCS; GitHub e a plataforma em nuvem.");

        repo.registrarCommit("Atividade Aula 00: documenta diferencas entre Git, GitHub, commit e push", arquivosAtividade);

        // 4. Conferencia final do grafo (Exercicio 3): a branch atividade/aula-00 avancou,
        // enquanto main, origin/main e a tag aula-00-inicio permaneceram estaticas no commit anterior.
        repo.exibirLogOnelineDecorate();
    }
}
