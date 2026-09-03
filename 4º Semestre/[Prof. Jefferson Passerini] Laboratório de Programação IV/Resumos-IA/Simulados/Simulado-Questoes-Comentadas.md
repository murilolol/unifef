Com base no conteúdo oficial da disciplina **Laboratório de Programação IV** (Ministrada pelo Prof. Jefferson Passerini), cujo foco central nos registros fornecidos é a estruturação e o versionamento de código através do ecossistema Git/GitHub (especificamente voltado ao projeto/repositório `suporteos2026`), apresento um simulado completo para avaliação universitária.

---

# 📝 Simulado Universitário: Laboratório de Programação IV
**Professor:** Jefferson Passerini  
**Disciplina:** Laboratório de Programação IV (4º Semestre)  
**Instruções:** Este simulado engloba conceitos fundamentais de controle de versão, colaboração via Git/GitHub e gestão de repositórios aplicados ao desenvolvimento de software, tendo como base o repositório oficial da disciplina (`suporteos2026`).

---

## 🔵 Parte 1: Questões de Múltipla Escolha

### Questão 1
No contexto de controle de versão moderno utilizado na disciplina Laboratório de Programação IV, qual é a principal finalidade de utilizar uma plataforma como o GitHub (conforme o repositório oficial `suporteos2026`) em comparação ao armazenamento local de código?

*   A) Apenas realizar a mineração de dados do código fonte gerado pelos alunos.
*   B) Permitir o versionamento remoto, colaboração em equipe, backup em nuvem e rastreabilidade de alterações.
*   C) Substituir a necessidade de utilizar compiladores ou interpretadores na máquina local.
*   D) Executar o código automaticamente em servidores de produção sem a necessidade de testes.

> **Alternativa Correta: B**
> **Justificativa Técnica:** O GitHub atua como um repositório remoto baseado em nuvem para o Git. Sua principal função é viabilizar o trabalho colaborativo simultâneo, garantir a segurança do código fonte contra perdas locais (backup), manter histórico detalhado de commits (rastreabilidade) e facilitar revisões de código.

---

### Questão 2
Ao clonar o repositório oficial da disciplina (`https://github.com/jeffersonarpasserini/suporteos2026`) em sua máquina local para iniciar as atividades práticas, qual comando do Git deve ser utilizado no terminal?

*   A) `git init`
*   B) `git push origin main`
*   C) `git clone https://github.com/jeffersonarpasserini/suporteos2026.git`
*   D) `git commit -m "Clone repository"`

> **Alternativa Correta: C**
> **Justificativa Técnica:** O comando `git clone` é utilizado para baixar uma cópia completa de um repositório remoto existente (incluindo todo o seu histórico de versões) para o ambiente de desenvolvimento local do aluno. O comando `git init` inicializa um repositório do zero, enquanto `push` envia alterações.

---

### Questão 3
Após realizar alterações em um arquivo de código dentro do seu repositório local clonado da disciplina, o arquivo encontra-se modificado, mas ainda não preparado para o próximo commit. Em qual área de trabalho (*staging area*) o Git armazena temporariamente as alterações após executarmos o comando `git add <arquivo>`?

*   A) No Repositório Remoto (*Remote Repository*).
*   B) No Diretório de Trabalho (*Working Directory*).
*   C) Na Área de Preparação / Índice (*Staging Area* / *Index*).
*   D) Na Lixeira do Sistema Operacional.

> **Alternativa Correta: C**
> **Justificativa Técnica:** O fluxo de trabalho básico do Git divide-se em três estados principais: *Working Directory* (onde os arquivos são editados), *Staging Area* (para onde vão os arquivos preparados via `git add`) e o *Repository* (onde as alterações são consolidadas via `git commit`).

---

### Questão 4
Imagine que você deseja enviar as alterações locais já comitadas do seu projeto de Laboratório de Programação IV para o repositório remoto no GitHub. Qual comando deve ser executado?

*   A) `git fetch`
*   B) `git pull`
*   C) `git push`
*   D) `git status`

> **Alternativa Correta: C**
> **Justificativa Técnica:** O comando `git push` é responsável por enviar os commits realizados localmente para o repositório remoto correspondente (geralmente mapeado como `origin`), atualizando a nuvem com o seu código mais recente.

---

### Questão 5
Para verificar o estado atual do seu repositório local — identificando quais arquivos foram modificados, quais estão na área de preparação (*staging*) e quais não estão sendo rastreados pelo Git —, qual comando deve ser utilizado?

*   A) `git log`
*   B) `git status`
*   C) `git diff`
*   D) `git branch`

> **Alternativa Correta: B**
> **Justificativa Técnica:** O comando `git status` fornece um panorama detalhado do estado atual do diretório de trabalho e da área de preparação, sendo o comando mais utilizado pelos desenvolvedores para checar pendências antes de realizar um `commit`.

---

### Questão 6
O que representa a estrutura `jeffersonarpasserini/suporteos2026` referenciada na disciplina do Prof. Jefferson Passerini?

*   A) Um comando de instalação de dependências via gerenciador de pacotes.
*   B) O caminho padrão de diretório do sistema operacional Windows.
*   C) O identificador composto pelo nome de usuário do proprietário (`jeffersonarpasserini`) e o nome do repositório (`suporteos2026`) no GitHub.
*   D) Uma chave de criptografia SSH para acesso ao servidor da universidade.

> **Alternativa Correta: C**
> **Justificativa Técnica:** No ecossistema GitHub, a notação `usuario/repositorio` identifica unicamente um projeto na plataforma, sendo utilizada em URLs, comandos de clonagem e referências de API.

---

### Questão 7
Qual é a principal utilidade de um arquivo `.gitignore` em um projeto desenvolvido na disciplina de Laboratório de Programação IV?

*   A) Ocultar arquivos confidenciais ou binários gerados pelo compilador (como arquivos temporários, logs e pastas de dependências) para que não sejam enviados ao repositório Git.
*   B) Ignorar erros de sintaxe gerados pelo programador durante a compilação.
*   C) Bloquear o acesso de outros alunos ao repositório remoto.
*   D) Configurar as cores do terminal de comandos.

> **Alternativa Correta: A**
> **Justificativa Técnica:** O arquivo `.gitignore` instrui o Git sobre quais arquivos ou pastas do diretório de trabalho devem ser ignorados e desconsiderados pelo controle de versão, mantendo o repositório limpo apenas com o código-fonte essencial.

---

### Questão 8
Caso você esteja trabalhando em equipe no projeto da disciplina e precise atualizar o seu repositório local com as modificações mais recentes que outros colegas enviaram para o repositório remoto no GitHub (`suporteos2026`), qual comando deve ser executado?

*   A) `git pull`
*   B) `git commit`
*   C) `git push`
*   D) `git rm`

> **Alternativa Correta: A**
> **Justificativa Técnica:** O comando `git pull` realiza uma operação combinada: ele busca (*fetch*) as alterações mais recentes do repositório remoto e, em seguida, as mescla (*merge*) com a branch local atual.

---

### Questão 9
O que significa criar um *Commit* com uma mensagem descritiva no Git?

*   A) Excluir permanentemente o histórico de versões anteriores para economizar espaço em disco.
*   B) Salvar um ponto de restauração definitivo e documentado do estado atual dos arquivos que estão na área de preparação (*staging area*).
*   C) Enviar automaticamente o código para o servidor de produção da faculdade.
*   D) Criar uma nova ramificação (*branch*) paralela no projeto.

> **Alternativa Correta: B**
> **Justificativa Técnica:** O commit cria um snapshot (fotografia) permanente do projeto naquele instante de tempo. A mensagem descritiva serve para documentar *o que* e *por que* aquela alteração foi feita, facilitando auditorias e manutenções futuras.

---

### Questão 10
Qual das alternativas descreve corretamente a relação entre o Git e o GitHub nas aulas de Laboratório de Programação IV?

*   A) O Git é a linguagem de programação utilizada para criar o GitHub.
*   B) O GitHub é o sistema de controle de versão local instalado na máquina, enquanto o Git é a plataforma de hospedagem em nuvem.
*   C) O Git é o sistema de controle de versão distribuído (ferramenta de linha de comando/software), e o GitHub é a plataforma web baseada em nuvem que hospeda repositórios Git.
*   D) São softwares concorrentes criados por empresas diferentes e incompatíveis entre si.

> **Alternativa Correta: C**
> **Justificativa Técnica:** Git é a tecnologia/software open-source de controle de versão criado por Linus Torvalds que roda localmente. O GitHub é um serviço de hospedagem web para repositórios Git que adiciona funcionalidades de colaboração, controle de acesso e interface gráfica.

---

## 🟠 Parte 2: Questões Discursivas e Estudos de Caso

### Estudo de Caso 1: Inicialização e Primeiro Envio ao Repositório
**Contexto:** Você é um aluno da disciplina *Laboratório de Programação IV* do Prof. Jefferson Passerini e precisa estruturar um novo projeto localmente para integrá-lo posteriormente ao ecossistema da disciplina no GitHub. Descreva o passo a passo detalhado, utilizando comandos do Git via terminal, desde a criação do repositório local até o primeiro envio (*push*) para o repositório remoto.

> **Resposta Modelo Detalhada:**
> Para realizar este procedimento, o aluno deve seguir a seguinte sequência lógica de comandos no terminal:
> 1. **Inicialização do repositório local:** Acessar a pasta raiz do projeto e digitar `git init`. Isso cria a estrutura oculta `.git` na máquina.
> 2. **Adição dos arquivos à Staging Area:** Executar `git add .` para preparar todos os arquivos criados do projeto para o versionamento.
> 3. **Criação do primeiro Commit:** Consolidar as alterações com uma mensagem descritiva utilizando `git commit -m "Commit inicial: estrutura base do projeto"`.
> 4. **Vinculação com o Repositório Remoto:** Conectar o repositório local ao repositório remoto no GitHub através do comando `git remote add origin <URL_DO_REPOSITORIO>`.
> 5. **Envio das alterações (Push):** Enviar o código para a nuvem utilizando `git push -u origin main` (ou `master`, dependendo da configuração padrão), definindo a branch principal de rastreio.

---

### Estudo de Caso 2: Resolução de Conflitos em Equipe
**Contexto:** Durante o desenvolvimento colaborativo de uma atividade prática baseada no repositório `suporteos2026`, você e um colega de equipe editaram exatamente a mesma linha de um arquivo de código-fonte. Ao tentar executar o comando `git pull` para atualizar sua máquina, o Git apontou um **conflito de merge** (*Merge Conflict*). 
Explique o que causa esse tipo de conflito e detalhe o procedimento técnico correto que você deve adotar para resolvê-lo e concluir a operação.

> **Resposta Modelo Detalhada:**
> * **Causa do Conflito:** Ocorre quando o Git identifica modificações concorrentes na mesma linha de código (ou em trechos muito próximos) entre a sua versão local e a versão enviada por outro colaborador para o repositório remoto, impedindo o Git de realizar a mesclagem automática (*auto-merge*).
> * **Procedimento de Resolução:**
>   1. Abrir o arquivo apontado com conflito em um editor de código (como VS Code). O Git insere marcadores visuais indicando a divergência (`<<<<<<<`, `=======`, `>>>>>>>`).
>   2. Analisar o código conflitante e decidir manualmente qual trecho deve ser mantido (o seu, o do colega, ou uma combinação lógica de ambos).
>   3. Remover os marcadores de conflito inseridos pelo Git (`<<<<<<<`, `=======`, `>>>>>>>`).
>   4. Adicionar o arquivo corrigido à área de preparação com `git add <arquivo>`.
>   5. Finalizar o processo de merge realizando um commit de resolução com `git commit -m "Resolvendo conflito de merge na funcionalidade X"`.

---

### Estudo de Caso 3: Boas Práticas com o Arquivo `.gitignore`
**Contexto:** Ao verificar o comando `git status` em seu projeto de Laboratório de Programação IV, você percebe que dezenas de arquivos temporários de compilação, arquivos de cache do sistema operacional (*DS_Store* ou *Thumbs.db*) e pastas de dependências (como `node_modules` ou pastas `bin/obj` de ambientes compilados) estão aparecendo como arquivos não rastreados (*untracked*). 
Qual é o impacto de enviar esses arquivos para o repositório do GitHub e como o arquivo `.gitignore` resolve esse problema de forma definitiva?

> **Resposta Modelo Detalhada:**
> * **Impacto Negativo:** Enviar arquivos temporários, de ambiente ou dependências geradas compromete a integridade do repositório. Isso gera redundância de dados (inchaço no tamanho do repositório no GitHub), conflitos desnecessários entre máquinas de desenvolvedores diferentes (visto que sistemas operacionais e ambientes de compilação variam) e possíveis brechas de segurança (caso chaves de API ou dados sensíveis fiquem expostos).
> * **Solução via `.gitignore`:** O arquivo `.gitignore` é um arquivo de texto simples colocado na raiz do projeto onde são listados padrões de nomes de arquivos e diretórios que o Git deve ignorar estritamente. Ao mapear essas extensões e diretórios nele, o comando `git status` passará a ocultá-los, e eles jamais serão incluídos nos comandos `git add` ou enviados acidentalmente via `git push`.

---

### Estudo de Caso 4: O Fluxo de Trabalho (Workflow) Diário no Git
**Contexto:** O Prof. Jefferson Passerini enfatiza a importância de manter um fluxo de trabalho organizado no desenvolvimento de software. Descreva passo a passo a rotina diária ideal de um desenvolvedor ao iniciar o trabalho em sua máquina local, escrever novas linhas de código para uma tarefa específica e garantir que o repositório remoto esteja atualizado ao encerrar o expediente.

> **Resposta Modelo Detalhada:**
> A rotina diária recomendada de *workflow* envolve os seguintes passos:
> 1. **Sincronização Inicial (Início do expediente):** Antes de começar a codificar, executar `git pull` para baixar e integrar eventuais atualizações feitas por outros membros da equipe no repositório remoto.
> 2. **Desenvolvimento:** Escrever, testar e modificar o código no *Working Directory* para implementar a nova funcionalidade ou correção solicitada.
> 3. **Verificação de Estado:** Executar `git status` para inspecionar quais arquivos foram alterados.
> 4. **Seleção de Alterações (Staging):** Adicionar os arquivos relevantes modificados para a área de preparação utilizando `git add .` ou `git add <arquivo>`.
> 5. **Consolidação (Commit):** Gravar um ponto de versão histórico localmente por meio de `git commit -m "feat: implementa a funcionalidade Y"`, mantendo mensagens claras e semânticas.
> 6. **Publicação (Fim do expediente/tarefa):** Enviar as alterações consolidadas para o GitHub utilizando `git push origin main`, garantindo que o backup remoto esteja espelhado com o progresso local.

---

### Estudo de Caso 5: Recuperação e Auditoria com `git log`
**Contexto:** Durante os testes práticos da disciplina de Laboratório de Programação IV, um erro crítico foi introduzido no código do projeto, quebrando uma funcionalidade que funcionava perfeitamente na semana anterior. Você suspeita que o erro foi inserido em um dos commits recentes. 
Como você utilizaria os comandos do Git para auditar o histórico de versões, identificar o commit problemático e analisar as diferenças exatas de código inseridas em cada versão?

> **Resposta Modelo Detalhada:**
> Para realizar a auditoria e rastreamento do erro, o aluno deve empregar a seguinte estratégia com comandos Git:
> 1. **Visualizar o histórico (Log):** Executar o comando `git log` (ou `git log --oneline --graph` para uma visualização resumida e gráfica). Isso exibirá a lista cronológica de todos os commits realizados, acompanhados de seus hashes identificadores únicos, autores e mensagens.
> 2. **Identificar o Commit:** Localizar o hash do commit suspeito que introduziu a falha.
> 3. **Analisar Alterações (Diff):** Utilizar o comando `git diff <hash_commit_anterior> <hash_commit_suspeito>` (ou simplesmente inspecionar um commit específico com `git show <hash>`). O comando `diff` exibirá linha por linha exatamente quais adições (em verde) e remoções (em vermelho) foram feitas naquele ponto específico, permitindo isolar a causa raiz do bug.