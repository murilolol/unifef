/**
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Configuração de Ambiente Java Spring Boot
 * 
 * Como executar:
 * 1. Compile: javac DiagnosticoAmbiente.java
 * 2. Execute: java DiagnosticoAmbiente
 */
public class DiagnosticoAmbiente {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("        DIAGNÓSTICO DO AMBIENTE RUNTIME JAVA       ");
        System.out.println("==================================================");
        
        // Exibe a versão do Java em execução na JVM
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String javaHome = System.getProperty("java.home");
        
        System.out.println("Versão do Java Runtime (JVM) : " + javaVersion);
        System.out.println("Fabricante da JVM            : " + javaVendor);
        System.out.println("Diretório do Java (JAVA_HOME): " + javaHome);
        
        // Exibe dados do Sistema Operacional
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        
        System.out.println("--------------------------------------------------");
        System.out.println("Sistema Operacional           : " + osName + " (" + osArch + ") v" + osVersion);
        
        // Verificação de conformidade com o curso (Java 21 recomendado)
        System.out.println("--------------------------------------------------");
        if (javaVersion.startsWith("21")) {
            System.out.println("STATUS: Configuração em conformidade com o curso Suporte OS 2026 (Java 21 LTS).");
        } else {
            System.out.println("ATENÇÃO: A versão detectada (" + javaVersion + ") difere do Java 21 LTS estipulado.");
        }
        System.out.println("==================================================");
    }
}
