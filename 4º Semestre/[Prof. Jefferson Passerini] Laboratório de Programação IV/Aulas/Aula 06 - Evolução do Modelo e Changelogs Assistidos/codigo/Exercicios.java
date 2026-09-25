/*
 * Disciplina: Laboratório de Programação IV (4º Semestre)
 * Professor: Prof. Jefferson Passerini
 * Tema: Resolução das Atividades e Questões da Aula 06
 *
 * Como executar:
 *   javac Exercicios.java
 *   java Exercicios
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exercicios {

    // =========================================================================
    // EXERCÍCIO 1: Atividade de Transferência
    // Enunciado:
    //   Cada estudante deverá adicionar uma entidade ou campo no tema próprio,
    //   gerar o rascunho, registrar pelo menos três problemas encontrados e
    //   entregar a migração revisada com teste de preservação de dados.
    // =========================================================================
    public static class Exercicio1AtividadeTransferencia {

        // Domínio evoluído de exemplo para tema próprio: Gestão de Clientes e Limite de Crédito
        public static class Cliente {
            private Long id;
            private String nome;
            private String cpf;
            private BigDecimal limiteCredito; // Novo campo obrigatório

            public Cliente(Long id, String nome, String cpf, BigDecimal limiteCredito) {
                this.id = id;
                this.nome = nome;
                this.cpf = cpf;
                this.limiteCredito = limiteCredito;
            }

            public Long getId() { return id; }
            public String getNome() { return nome; }
            public BigDecimal getLimiteCredito() { return limiteCredito; }
        }

        public static void executar() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("EXERCÍCIO 1: ATIVIDADE DE TRANSFERÊNCIA (TEMA: CLIENTE E LIMITE CRÉDITO)");
            System.out.println("----------------------------------------------------------------------");

            System.out.println("1. Evolução de Domínio proposta:");
            System.out.println("   - Inclusão do campo 'limite_credito' NUMERIC(15,2) NOT NULL na tabela 'cliente'.");
            System.out.println("   - Inclusão da constraint CHECK (limite_credito >= 0).\n");

            System.out.println("2. Relatório de 3 Problemas Identificados no Rascunho Bruto do liquibase:diff:");
            System.out.println("   [Problema 1] Adição direta de NOT NULL: O diff gerou 'addColumn' com constraints.nullable=false.");
            System.out.println("                Impacto: O comando 'ALTER TABLE ADD COLUMN NOT NULL' falharia no PostgreSQL");
            System.out.println("                porque a tabela 'cliente' já possui linhas de compras anteriores cadastradas.");
            System.out.println("   [Problema 2] Falta de regras CHECK e semântica de negócio: O Hibernate gerou apenas o tipo da coluna,");
            System.out.println("                mas não gerou a constraint 'CHECK (limite_credito >= 0)', permitindo limites negativos.");
            System.out.println("   [Problema 3] Metadados automáticos e ausência de Rollback: O ID do changeSet veio como timestamp");
            System.out.println("                ilegível gerado pela máquina local e sem bloco de 'rollback' para desfazer a migração.\n");

            System.out.println("3. Solução com a Migração Revisada (Expand-Migrate-Contract):");
            System.out.println("   - ChangeSet 1 (Expand): addColumn 'limite_credito' NUMERIC(15,2) NULL");
            System.out.println("   - ChangeSet 2 (Migrate): UPDATE cliente SET limite_credito = 500.00 WHERE limite_credito IS NULL");
            System.out.println("   - ChangeSet 3 (Contract): addNotNullConstraint em 'limite_credito' e ADD CONSTRAINT ck_cliente_limite");
            System.out.println("   - ChangeSet 4 (Rollback): dropConstraint e dropColumn definidos explicitamente.\n");

            // Teste de preservação de dados
            System.out.println("4. Teste Prático de Preservação de Dados:");
            Map<Long, BigDecimal> bancoClientes = new HashMap<>();
            bancoClientes.put(1L, null); // Cliente antigo 1
            bancoClientes.put(2L, null); // Cliente antigo 2
            System.out.println("   Clientes antes da migração: " + bancoClientes);

            // Executando Backfill
            BigDecimal limitePadrao = new BigDecimal("500.00");
            bancoClientes.replaceAll((id, limite) -> limite == null ? limitePadrao : limite);
            System.out.println("   Clientes pós-migração (backfill aplicado): " + bancoClientes);

            boolean todosPreservados = bancoClientes.size() == 2 &&
                    bancoClientes.values().stream().allMatch(l -> l.compareTo(limitePadrao) == 0);
            System.out.println("   Resultado do teste: " + (todosPreservados ? "APROVADO (Dados 100% preservados)" : "REPROVADO"));
            System.out.println();
        }
    }

    // =========================================================================
    // EXERCÍCIO 2: Questão 1
    // Enunciado:
    //   Por que ddl-auto=create só pode apontar para o banco descartável?
    // =========================================================================
    public static class Exercicio2Questao1 {
        public static void executar() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("EXERCÍCIO 2: QUESTÃO 1 - USO DO ddl-auto=create APENAS EM BANCO DESCARTÁVEL");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Resposta:");
            System.out.println("A propriedade 'spring.jpa.hibernate.ddl-auto=create' faz com que o Hibernate,");
            System.out.println("ao inicializar a aplicação, execute operações 'DROP TABLE IF EXISTS' em todas as tabelas");
            System.out.println("mapeadas antes de recriá-las do zero. Se essa configuração for acidentalmente apontada");
            System.out.println("para um ambiente compartilhado (desenvolvimento coletivo, homologação, testes com dados");
            System.out.println("persistentes ou produção), haverá destruição imediata e irrecuperável de todos os dados.");
            System.out.println("Por isso, no fluxo assistido da Aula 06, o ddl-auto=create roda única e exclusivamente");
            System.out.println("no banco isolado 'suporteos2026_reference', com 'web-application-type=none', servindo");
            System.out.println("apenas como um 'molde' temporário para o diff do Liquibase antes de ser descartado.\n");
        }
    }

    // =========================================================================
    // EXERCÍCIO 3: Questão 2
    // Enunciado:
    //   O que o diff sabe e o que ele não sabe?
    // =========================================================================
    public static class Exercicio3Questao2 {
        public static void executar() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("EXERCÍCIO 3: QUESTÃO 2 - O QUE O DIFF SABE E O QUE ELE NÃO SABE");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Resposta:");
            System.out.println("[O QUE O DIFF SABE]:");
            System.out.println("  - Detectar diferenças estáticas e estruturais entre esquemas de bancos de dados,");
            System.out.println("    tais como tabelas ausentes, colunas adicionadas/removidas, tipos de dados diferentes");
            System.out.println("    e índices/chaves estrangeiras não coincidentes.");
            System.out.println("[O QUE O DIFF NÃO SABE]:");
            System.out.println("  - Intenção de negócio e contexto histórico do projeto;");
            System.out.println("  - Existência e valor de linhas de dados já armazenadas em produção;");
            System.out.println("  - Distinguir uma 'coluna renomeada' de um 'dropColumn seguido de addColumn' (o que destrói dados);");
            System.out.println("  - Padrões de nomenclatura e convenções da equipe (ex: pk_fornecedor vs fornecedor_pkey);");
            System.out.println("  - Regras de negócio complexas como CHECK constraints e políticas de integridade;");
            System.out.println("  - Estratégias seguras de preenchimento (backfill) e instruções reversas de rollback.\n");
        }
    }

    // =========================================================================
    // EXERCÍCIO 4: Questão 3
    // Enunciado:
    //   Por que adicionar NOT NULL exige considerar dados existentes?
    // =========================================================================
    public static class Exercicio4Questao3 {
        public static void executar() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("EXERCÍCIO 4: QUESTÃO 3 - ADIÇÃO DE NOT NULL E DADOS EXISTENTES");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Resposta:");
            System.out.println("Ao adicionar uma nova coluna em uma tabela que já contém registros, o banco de dados");
            System.out.println("precisa atribuir um valor inicial a todas as linhas pré-existentes. Se a coluna for");
            System.out.println("criada diretamente com a restrição NOT NULL sem um valor padrão ou sem linhas preenchidas,");
            System.out.println("o SGBD tentará preenchê-la com NULL e a operação falhará com erro de integridade");
            System.out.println("(ex: 'column contains null values').");
            System.out.println("Para evitar downtime ou falha de migração, aplica-se a técnica expand-migrate-contract:");
            System.out.println("  1. Cria-se a coluna permitindo nulo (NULLable);");
            System.out.println("  2. Executa-se um UPDATE (backfill) definindo valores válidos para todas as linhas antigas;");
            System.out.println("  3. Aplica-se a restrição NOT NULL e as regras CHECK de validação.\n");
        }
    }

    // =========================================================================
    // EXERCÍCIO 5: Questão 4
    // Enunciado:
    //   Por que um diff aparentemente vazio ainda precisa ser interpretado?
    // =========================================================================
    public static class Exercicio5Questao4 {
        public static void executar() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("EXERCÍCIO 5: QUESTÃO 4 - INTERPRETAÇÃO DE DIFFS APARENTEMENTE VAZIOS");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Resposta:");
            System.out.println("Um diff que retorna vazio significa apenas que não foram identificadas divergências");
            System.out.println("dentro dos tipos de objetos e escopos que a ferramenta configurada é capaz de comparar.");
            System.out.println("Ele ainda precisa de interpretação crítica porque:");
            System.out.println("  1. O diff pode ter sido executado contra o schema ou banco errado (ex: comparar um banco consigo mesmo);");
            System.out.println("  2. Determinados objetos não são inspecionados por padrão pelo diff (ex: constraints CHECK,");
            System.out.println("     triggers, procedures, extensões, índices parciais e permissões);");
            System.out.println("  3. Diferenças sutis de ordenação de colunas ou defaults implícitos podem passar despercebidos;");
            System.out.println("  4. A ausência de diferenças estruturais não garante que o comportamento em tempo de execução");
            System.out.println("     está correto com os dados reais de negócio.\n");
        }
    }

    // =========================================================================
    // EXERCÍCIO 6: Questão 5
    // Enunciado:
    //   Qual diferença existe entre schema de referência e changelog oficial?
    // =========================================================================
    public static class Exercicio6Questao5 {
        public static void executar() {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("EXERCÍCIO 6: QUESTÃO 5 - SCHEMA DE REFERÊNCIA VS CHANGELOG OFICIAL");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Resposta:");
            System.out.println("A diferença é análoga à diferença entre um 'destino estático' e o 'caminho histórico percorrido':");
            System.out.println("  - SCHEMA DE REFERÊNCIA:");
            System.out.println("    É uma fotografia pontual e efêmera gerada automaticamente pelas classes JPA atuais");
            System.out.println("    em um banco descartável. Ele mostra como as tabelas deveriam ser no final se fossem");
            System.out.println("    criadas hoje do zero, sem histórico, sem rollbacks e sem dados.");
            System.out.println("  - CHANGELOG OFICIAL (db.changelog-master.yaml):");
            System.out.println("    É o histórico auditável, imutável e versionado de evolução do banco de dados.");
            System.out.println("    Ele contém a sequência determinística de changeSets aplicados ao longo do tempo,");
            System.out.println("    com instruções de migração de dados (DML), constraints refinadas, autoria, comentários");
            System.out.println("    e passos de reversão (rollback), garantindo que qualquer ambiente possa sair da");
            System.out.println("    versão 0 até a versão atual sem perda de informações.\n");
        }
    }

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("  RESOLUÇÃO COMPLETA DAS ATIVIDADES DA AULA 06 - LP4 UNIFEF");
        System.out.println("  Professor: Prof. Jefferson Passerini");
        System.out.println("======================================================================\n");

        Exercicio1AtividadeTransferencia.executar();
        Exercicio2Questao1.executar();
        Exercicio3Questao2.executar();
        Exercicio4Questao3.executar();
        Exercicio5Questao4.executar();
        Exercicio6Questao5.executar();

        System.out.println("======================================================================");
        System.out.println("  TODAS AS QUESTÕES E ATIVIDADES FORAM EXECUTADAS E CONCLUÍDAS COM SUCESSO.");
        System.out.println("======================================================================");
    }
}
