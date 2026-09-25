/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Configuração de Ambiente Java Spring Boot
 * 
 * Resolução dos Exercícios Conceituais sobre Configuração de Ambiente.
 * 
 * Como executar:
 * 1. Compile: javac ResolucaoExercicios.java
 * 2. Execute: java ResolucaoExercicios
 */
public class ResolucaoExercicios {

    public static void main(String[] args) {
        System.out.println("=== RESOLUÇÃO DOS EXERCÍCIOS DA AULA 01 ===\n");
        
        exercicio1();
        exercicio2();
        exercicio3();
        exercicio4();
    }

    /**
     * Exercício 1: Verificação e Diagnóstico de Variáveis de Ambiente
     */
    public static void exercicio1() {
        System.out.println("--- Exercício 1: JAVA_HOME vs PATH ---");
        System.out.println("JAVA_HOME aponta para o diretório raiz da instalação do JDK.");
        System.out.println("PATH indica ao sistema operacional a lista de diretórios onde encontrar comandos executáveis (ex: java, javac).");
        System.out.println("Para diagnosticar no Windows (PowerShell):");
        System.out.println("  > java -version");
        System.out.println("  > javac -version");
        System.out.println("  > where.exe java");
        System.out.println("  > $env:JAVA_HOME\n");
    }

    /**
     * Exercício 2: Comparação entre Servidores Externos e Servidores Incorporados
     */
    public static void exercicio2() {
        System.out.println("--- Exercício 2: Servidor Externo vs Servidor Incorporado ---");
        System.out.println("Servidor Externo (.WAR): A aplicação é empacotada e implantada em um contêiner instalado previamente.");
        System.out.println("  - Vantagem: Gerenciamento centralizado de múltiplos deploys.");
        System.out.println("  - Desvantagem: Configuração complexa e divergências entre ambientes de dev e prod.");
        System.out.println("Servidor Incorporado (.JAR): O Spring Boot empacota o Tomcat dentro do próprio arquivo executável.");
        System.out.println("  - Vantagem: Execução direta com 'java -jar', ambiente auto-contido e reprodutível.");
        System.out.println("  - Desvantagem: Maior tamanho do artefato gerado.\n");
    }

    /**
     * Exercício 3: Impacto da Migração do Namespace javax.* para jakarta.*
     */
    public static void exercicio3() {
        System.out.println("--- Exercício 3: Transição de javax.* para jakarta.* ---");
        System.out.println("Com a transferência do Java EE para a Eclipse Foundation (Jakarta EE 9+), o namespace 'javax.*' foi alterado para 'jakarta.*'.");
        System.out.println("Impacto: Servidores mais recentes (ex: Tomcat 10+) esperam a API 'jakarta.servlet.*'.");
        System.out.println("Aplicações legadas compiladas com 'javax.servlet.*' falharão em contêineres modernos se não forem refatoradas ou migradas.\n");
    }

    /**
     * Exercício 4: Importância da Reprodutibilidade com Maven Wrapper
     */
    public static void exercicio4() {
        System.out.println("--- Exercício 4: Importância do Maven Wrapper (mvnw) ---");
        System.out.println("O Maven Wrapper (mvnw / mvnw.cmd) garante que todos os desenvolvedores e servidores de CI/CD utilizem exatamente");
        System.out.println("a mesma versão do Maven especificada no repositório, evitando falhas de build causadas por diferenças entre versões globais da ferramenta.\n");
    }
}
