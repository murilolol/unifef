/*
 * Disciplina: Laboratório de Programação IV (4º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Guia Conceitual e Respostas Teóricas Fundamentadas para Avaliação 1
 * Como executar:
 *   javac RevisaoConceitosA1.java
 *   java RevisaoConceitosA1
 */

public class RevisaoConceitosA1 {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("GUIA DE REVISÃO E RESPOSTAS TEÓRICAS - AVALIAÇÃO 1 (PROF. JEFFERSON)");
        System.out.println("======================================================================\n");

        imprimirQuestao(
                "1. Quem implementa a interface JpaRepository em tempo de execução?",
                "O Spring Data JPA utiliza Proxies Dinâmicos do Java. Em tempo de inicialização da aplicação " +
                "(IoC Container), o Spring escaneia as interfaces que estendem JpaRepository e cria instâncias " +
                "de proxies dinâmicos em memória (como SimpleJpaRepository). Esses proxies delegam as chamadas " +
                "diretamente ao EntityManager do JPA/Hibernate, poupando a escrita manual de código repetitivo de acesso a dados."
        );

        imprimirQuestao(
                "2. Por que o Service define a fronteira transacional (@Transactional) e não o Repository ou Controller?",
                "A transação deve cobrir o Caso de Uso completo (unidade lógica de negócio). Um caso de uso frequentemente " +
                "envolve múltiplas consultas, validações e alterações em diferentes entidades e repositórios. Se a transação " +
                "ficasse no Repository, cada comando seria um commit isolado, impossibilitando rollback atômico em caso de " +
                "falhas parciais. Se ficasse no Controller, o acoplamento violaria a separação de responsabilidades ao expor " +
                "detalhes de infraestrutura de persistência na camada de apresentação HTTP."
        );

        imprimirQuestao(
                "3. Qual a diferença entre entidade nos estados 'Managed' e 'Detached' no JPA?",
                "- Managed: A entidade está vinculada a um Contexto de Persistência ativo (EntityManager). Qualquer modificação " +
                "em seus atributos é rastreada automaticamente pelo mecanismo de Dirty Checking e persistida no banco ao final " +
                "da transação (flush/commit) sem necessidade de chamar repository.save().\n" +
                "- Detached: A entidade possui um identificador (ID/PK), mas seu Contexto de Persistência foi encerrado (ou foi desanexada). " +
                "Alterações em seus atributos NÃO são refletidas automaticamente no banco. Acessar coleções LAZY em estado detached " +
                "dispara LazyInitializationException."
        );

        imprimirQuestao(
                "4. Por que Dirty Checking não elimina métodos de negócio expressivos no domínio?",
                "Dirty checking é apenas um mecanismo de infraestrutura para sincronização de estado relacional. Se dependermos " +
                "apenas dele com setters indiscriminados, o modelo torna-se anêmico, permitindo estados inválidos (ex: saldo negativo, " +
                "retiradas indevidas). Os métodos expressivos (ex: retirarEstoque, inativar) encapsulam e garantem as invariantes " +
                "do negócio antes que o estado seja alterado."
        );

        imprimirQuestao(
                "5. Por que usar Liquibase com 'ddl-auto=validate' em vez de deixar o Hibernate gerar tabelas?",
                "Em ambientes corporativos e profissionais, o esquema relacional é um ativo crítico com histórico. O Hibernate " +
                "com ddl-auto=update/create não oferece rastreabilidade, pode travar bancos em produção, não executa backfill " +
                "seguro de dados e não suporta rollback determinístico. O Liquibase assume a 'fonte da verdade' através de " +
                "changeSets ordenados, auditáveis e rastreados pela tabela databasechangelog, cabendo ao Hibernate apenas validar " +
                "a convergência das classes."
        );

        imprimirQuestao(
                "6. Por que exceções da camada de serviço não devem carregar códigos de status HTTP?",
                "A camada de aplicação e domínio deve ser agnóstica a protocolos de transporte. O mesmo serviço pode ser acionado " +
                "por uma API REST, por uma fila de mensageria (RabbitMQ/Kafka), por uma rotina agendada (Cron job) ou por comandos " +
                "de linha de terminal (CLI). Fazer o serviço lançar ResponseStatusException(HttpStatus.NOT_FOUND) amarra a regra " +
                "de negócio ao protocolo HTTP, violando o princípio de inversão de dependências e responsabilidade única."
        );

        imprimirQuestao(
                "7. O que é a sequência 'Expand-Migrate-Contract' no Liquibase?",
                "É a estratégia profissional para introduzir colunas obrigatórias (NOT NULL) sem indisponibilidade nem perda de dados:\n" +
                "1. Expand: Adiciona a nova coluna permitindo valores nulos (nullable: true);\n" +
                "2. Migrate: Executa script de atualização (UPDATE/backfill) para preencher as linhas existentes com valor padrão coerente;\n" +
                "3. Contract: Adiciona a restrição NOT NULL e as constraints de integridade definitivas."
        );

        System.out.println("REVISÃO TEÓRICA CONCLUÍDA COM SUCESSO!");
    }

    private static void imprimirQuestao(String titulo, String resposta) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println(titulo);
        System.out.println("----------------------------------------------------------------------");
        System.out.println(resposta);
        System.out.println();
    }
}
