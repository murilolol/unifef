-- Disciplina: Laboratório de Programação IV
-- Professor: Prof. Jefferson Passerini
-- Tema: Persistência com JPA, PostgreSQL e Liquibase

-- 1. CONSULTA DE VERIFICAÇÃO DO HISTÓRICO DO LIQUIBASE
SELECT id, author, filename, dateexecuted, orderexecuted, md5sum 
FROM databasechangelog 
ORDER BY orderexecuted;

-- 2. CONSULTA DE VERIFICAÇÃO DAS TABELAS CRIADAS NO SCHEMA PUBLIC
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

/*
RESPOSTAS DAS QUESTÕES DE REVISÃO:

1. ddl-auto=update conflita com o Liquibase porque o Hibernate tenta alterar a estrutura do banco de forma imperativa e automática,
   enquanto o Liquibase exige que toda alteração seja declarativa, versionada e incremental. Usar ddl-auto=validate garante que
   o Liquibase seja a única fonte de verdade estrutural.

2. JPA (Jakarta Persistence API) é uma especificação padrão do Java baseada em interfaces e anotações. O Hibernate é o framework ORM
   que implementa essa especificação, traduzindo as operações de objetos para comandos SQL.

3. O Liquibase reconhece as mudanças aplicadas consultando a tabela 'databasechangelog'. Ele compara a combinação de 'id', 'author'
   e o caminho do arquivo 'filename' para saber se o changeSet já foi executado.

4. Editar um changeSet já executado altera o seu conteúdo, o que faz com que o Liquibase gere um hash MD5 (checksum) diferente do
   registrado na tabela 'databasechangelog', disparando um erro de validação (Validation Failed).

5. A validação no Java fornece feedback rápido ao usuário na linguagem do domínio. A validação no banco (CHECK constraints) garante
   a integridade dos dados contra inserções externas (scripts, integrações ou outras aplicações), agindo como defesa em profundidade.

6. O lado proprietário da associação (que contém a chave estrangeira) é mapeado com @ManyToOne e @JoinColumn. O lado inverso usa
   o atributo 'mappedBy' dentro de @OneToMany para indicar qual campo na classe filha gerencia o relacionamento.

7. Usamos EnumType.STRING para gravar o nome textual da constante (ex: 'ATIVO') no banco. Se usássemos o padrão ordinal (numérico),
   mudar a ordem das constantes no Enum Java corromperia os dados existentes.

8. Bancos em memória como H2 possuem dialetos e comportamentos diferentes do PostgreSQL (ex: tratamento de tipos, constraints e concorrência).
   O curso não adotou H2 para garantir fidelidade tecnológica absoluta entre desenvolvimento, testes e produção.

9. Para adicionar uma coluna obrigatória (NOT NULL) em uma tabela populada: cria-se a coluna permitindo nulos, executa-se um script
   para preencher os registros existentes com um valor padrão, e finalmente aplica-se a restrição NOT NULL.

10. Antes de alterar o código em caso de 'Connection refused', deve-se verificar: se o contêiner do PostgreSQL está em execução,
    se a porta (5432) está acessível, se as credenciais no arquivo .env estão corretas e se o profile correto foi ativado.
*/
