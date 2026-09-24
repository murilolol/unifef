// Disciplina: Laboratório de Programação III (3º Semestre)
// Professor: Prof. Jefferson Passerini
// Tema: Avaliação II - Tratamento de Exceções Customizadas
// Como executar: javac NegocioException.java && java NegocioException

public class NegocioException extends Exception {
    // Exercício 3: Construtor com mensagem de erro amigável ao usuário
    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public static void main(String[] args) {
        System.out.println("=== Teste da Exceção NegocioException (Exercício 3) ===");
        try {
            throw new NegocioException("Regra de negócio violada: Estoque insuficiente.");
        } catch (NegocioException e) {
            System.out.println("Exceção capturada com sucesso: " + e.getMessage());
        }
    }
}
