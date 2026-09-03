# 📘 Apostila Prática de Engenharia de Software I
**Disciplina:** Engenharia de Software I (3º Semestre)  
**Professor Responsável:** Prof. Esp. Marcelo Tadeu Boer  
**Estudo de Caso Base:** Aplicativo Móvel *Desapega Já*  

---

## 🚀 Apresentação da Apostila

Esta apostila prática foi desenvolvida para consolidar os conceitos teóricos e práticos da disciplina de **Engenharia de Software I**. Utilizando como estudo de caso o aplicativo **Desapega Já** — uma plataforma de economia colaborativa para negociação de produtos de segunda mão entre usuários da mesma região —, este material traz templates de requisitos, especificações detalhadas de Casos de Uso (CRUD completo: Logar, Cadastrar, Listar, Carregar, Alterar e Excluir), diagramas em **Mermaid** e exercícios resolvidos baseados nas diretrizes oficiais da disciplina.

---

## 📑 Sumário
1. [Módulo 1: Engenharia de Requisitos e Processo de Abstração](#módulo-1-engenharia-de-requisitos-e-processo-de-abstração)
2. [Módulo 2: Modelagem de Atores e Casos de Uso](#módulo-2-modelagem-de-atores-e-casos-de-uso)
3. [Módulo 3: Exercícios Resolvidos de Especificação de Casos de Uso (CRUD)](#módulo-3-exercícios-resolvidos-de-especificação-de-casos-de-uso-crud)
4. [Módulo 4: Diagramas de Caso de Uso em Mermaid](#módulo-4-diagramas-de-caso-de-uso-em-mermaid)
5. [Módulo 5: Projeto de Classes e Estrutura de Dados](#módulo-5-projeto-de-classes-e-estrutura-de-dados)

---

<div style="page-break-after: always;"></div>

## Módulo 1: Engenharia de Requisitos e Processo de Abstração

### 1.1 Contexto do Aplicativo (*Desapega Já*)
Muitas pessoas possuem em casa produtos que não utilizam mais (roupas, eletrônicos, móveis, livros), que acabam guardados ou descartados. O aplicativo **Desapega Já** é uma plataforma móvel prática e acessível que conecta vendedores e compradores da mesma região para incentivar a reutilização, a economia colaborativa e o consumo consciente.

### 1.2 Template de Requisitos Funcionais (RF)
O levantamento de requisitos funcionais descreve as ações e funções que o sistema obrigatoriamente deve realizar.

| Nº | Requisito Funcional | Descrição Detalhada |
|:---:|:---|:---|
| **RF01** | Facilitar a venda de produtos | Permitir a conexão ágil entre anunciantes e compradores locais. |
| **RF02** | Permitir anúncio de produtos | Permitir o cadastro de itens informando fotos, descrição, preço e categoria. |
| **RF03** | Cadastro de Interessados (Clientes) | Permitir o cadastro simples de compradores para negociação. |
| **RF04** | Busca por proximidade | Facilitar a filtragem de produtos com base no bairro, cidade e estado. |
| **RF05** | Geração de Histórico e Avaliações | Gerar automaticamente ID do usuário, data de cadastro, histórico e avaliações. |
| **RF06** | Cadastro de Anunciantes | Permitir o registro de pessoas que ofertam os produtos na plataforma. |
| **RF07** | Cadastro de Produtos | Registrar itens físicos associados a categorias e preços. |
| **RF08** | Troca de Mensagens | Possibilitar chat direto entre comprador e vendedor interessados. |

### 1.3 Template de Requisitos Não-Funcionais (RNF)
Os requisitos não-funcionais estabelecem restrições de qualidade, desempenho e usabilidade do sistema.

| Nº | Requisito Não-Funcional | Descrição Técnica |
|:---:|:---|:---|
| **RNF01** | Fácil Usabilidade | Interface intuitiva para usuários de diferentes faixas etárias (UX amigável). |
| **RNF02** | Segurança | Criptografia de senhas (hash) e proteção de dados pessoais (LGPD). |
| **RNF03** | Prático e Acessível | Funcionamento otimizado para dispositivos móveis Android e iOS de entrada. |
| **RNF04** | Integridade de Dados | Validação rigorosa de campos obrigatórios (CPF, e-mail, telefone com WhatsApp). |

---

<div style="page-break-after: always;"></div>

## Módulo 2: Modelagem de Atores e Casos de Uso

### 2.1 Identificação dos Atores do Sistema
Os principais usuários que interagem diretamente com o aplicativo *Desapega Já* são:
* **Pessoa Anunciante (Vendedor):** Usuário que cadastra produtos, gerencia anúncios e negocia com interessados.
* **Pessoa Cliente (Interessado/Comprador):** Usuário cadastrado que busca produtos na região, visualiza detalhes e troca mensagens.

### 2.2 Tabela de Mapeamento de Casos de Uso (Lista de Requisitos e Mensagens)

| Nº | Descrição da Ação | Caso de Uso | Entrada | Saída |
|:---:|:---|:---|:---|:---|
| **01** | Realizar Login | Realizar Login Aplicativo | e-mail / senha | Msg01 / Tela Principal |
| **02** | Cadastrar Anunciante | Cadastrar Pessoa Anunciante | dados_anunciante | Msg02 (Sucesso ao cadastrar) |
| **03** | Listar Perfil | Listar Pessoa Anunciante | - | Dados do Anunciante |
| **04** | Editar Perfil | Editar Pessoa Anunciante | dados_anunciante | Msg03 / Dados Atualizados |
| **05** | Cadastrar Produto | Cadastrar Produto | dados_produto | Msg02 |
| **06** | Listar Produtos | Listar Produtos | - | Lista de Dados do Produto |
| **07** | Editar Produto | Editar Produto | id_produto | Dados do Produto |
| **08** | Excluir Produto | Excluir Produto | id_produto | Msg04 (Produto Excluído) |
| **09** | Buscar Produto | Buscar Produto | termo_busca | Dados Filtrados |
| **10** | Cadastrar Anúncio | Cadastrar Anúncio | dados_anuncio | Msg02 |

---

<div style="page-break-after: always;"></div>

## Módulo 3: Exercícios Resolvidos de Especificação de Casos de Uso (CRUD)

Conforme diretriz da disciplina (Desenvolver a descrição dos Casos de Uso: **Logar**, **Cadastrar**, **Listar**, **Carregar**, **Alterar** e **Excluir**), apresentamos os quadros padrão estruturados:

### 3.1 Caso de Uso: Logar (Realizar Login)
* **Ator Principal:** Pessoa Anunciante / Pessoa Cliente
* **Descrição da Ação:** O usuário deseja realizar o login no aplicativo informando e-mail e senha para acessar o painel principal.
* **Pré-requisito:** O usuário deve estar previamente cadastrado no sistema.

| Passo | Fluxo Normal |
|:---:|:---|
| **01** | O usuário acessa o aplicativo móvel. |
| **02** | O aplicativo exibe a tela de login. |
| **03** | O usuário informa e-mail e senha nos campos solicitados. |
| **04** | O usuário clica na opção "Logar". |
| **05** | O aplicativo valida as credenciais informadas no banco de dados. |
| **06** | O aplicativo exibe a página inicial correspondente ao perfil do usuário. |

* **Fluxo Alternativo:**
  * **2.1.** Se o usuário não possui cadastro, clica no link "Cadastre-se aqui".
  * **2.2.** O sistema direciona para o Caso de Uso *Cadastrar*.
  * **5.1.** Se e-mail/senha estiverem incorretos ou em branco, exibe a mensagem: *"E-mail ou senha inválidos"*.
  * **5.1.1.** O sistema retorna ao passo 03.
* **Dados Envolvidos:** E-mail e Senha.

---

### 3.2 Caso de Uso: Cadastrar
* **Ator Principal:** Pessoa Anunciante / Pessoa Cliente
* **Descrição da Ação:** O novo usuário preenche o formulário de cadastro com dados pessoais e de contato para criação de conta.
* **Pré-requisito:** O usuário não possuir cadastro prévio com o mesmo e-mail ou CPF.

| Passo | Fluxo Normal |
|:---:|:---|
| **01** | O usuário seleciona a opção de criar nova conta no aplicativo. |
| **02** | O aplicativo apresenta o formulário de cadastro. |
| **03** | O usuário preenche: nome, e-mail, telefone/WhatsApp, senha, cidade, estado, CPF, data de nascimento e bairro. |
| **04** | O usuário clica em "Salvar Cadastro". |
| **05** | O sistema valida a unicidade do e-mail/CPF e grava os dados gerando um ID de usuário único. |
| **06** | O sistema exibe a mensagem de sucesso (*Msg02*) e direciona para a tela inicial. |

* **Fluxo Alternativo:**
  * **5.1.** Se houver campos obrigatórios vazios, o sistema exibe: *"Preencha todos os campos obrigatórios"*.
  * **5.1.1.** O aplicativo retorna ao passo 03.
* **Dados Envolvidos:** Nome, e-mail, telefone, senha, cidade, estado, CPF, data de nascimento, bairro e foto de perfil.

---

<div style="page-break-after: always;"></div>

### 3.3 Caso de Uso: Listar
* **Ator Principal:** Pessoa Anunciante / Pessoa Cliente
* **Descrição da Ação:** O sistema recupera e exibe uma listagem consolidada de registros (produtos, anúncios ou histórico de contatos) disponíveis na plataforma.
* **Pré-requisito:** O usuário estar autenticado no aplicativo.

| Passo | Fluxo Normal |
|:---:|:---|
| **01** | O usuário acessa a seção de listagem (ex: "Produtos Disponíveis" ou "Meus Anúncios"). |
| **02** | O aplicativo solicita ao servidor a coleção de registros cadastrados. |
| **03** | O sistema consulta o banco de dados aplicando os filtros de região/proximidade. |
| **04** | O aplicativo exibe a listagem formatada em cartões (cards) contendo fotos, títulos e preços. |

* **Fluxo Alternativo:**
  * **03.1.** Se nenhum registro for encontrado na região, o sistema exibe a mensagem: *"Nenhum produto encontrado por perto"*.
* **Dados Envolvidos:** Lista de produtos, categorias e dados resumidos do anunciante.

---

### 3.4 Caso de Uso: Carregar (Detalhar)
* **Ator Principal:** Pessoa Anunciante / Pessoa Cliente
* **Descrição da Ação:** O usuário seleciona um item específico na listagem para visualizar seus detalhes completos.
* **Pré-requisito:** Existir ao menos um registro listado na tela anterior.

| Passo | Fluxo Normal |
|:---:|:---|
| **01** | O usuário clica sobre um anúncio ou produto específico na listagem. |
| **02** | O aplicativo envia a identificação única (`id_produto` ou `id_anuncio`) para o servidor. |
| **03** | O sistema recupera todas as informações detalhadas do banco de dados. |
| **04** | O aplicativo carrega a tela de detalhes contendo fotos em alta resolução, descrição completa, categoria, preço e dados do anunciante. |

* **Fluxo Alternativo:**
  * **03.1.** Se o produto tiver sido removido, o sistema exibe: *"Este anúncio não está mais disponível"* e retorna à listagem.
* **Dados Envolvidos:** ID do produto, fotos, descrição, preço, categoria e contatos do anunciante.

---

<div style="page-break-after: always;"></div>

### 3.5 Caso de Uso: Alterar (Editar)
* **Ator Principal:** Pessoa Anunciante
* **Descrição da Ação:** O anunciante modifica informações de um produto ou de seu próprio perfil previamente cadastrado.
* **Pré-requisito:** O usuário deve ser o proprietário do anúncio/perfil e estar logado.

| Passo | Fluxo Normal |
|:---:|:---|
| **01** | O usuário acessa os detalhes de seu anúncio e seleciona a opção "Editar". |
| **02** | O aplicativo carrega o formulário pré-preenchido com os dados atuais do registro. |
| **03** | O usuário altera os campos desejados (ex: preço, descrição ou fotos). |
| **04** | O usuário clica em "Atualizar Alterações". |
| **05** | O sistema valida os novos dados e atualiza o registro no banco de dados. |
| **06** | O sistema exibe a mensagem de confirmação (*Msg03: "Alterações salvas com sucesso"*). |

* **Fluxo Alternativo:**
  * **04.1.** Se houver falha de conexão ou dados inválidos, o sistema exibe mensagem de erro e mantém o formulário ativo para correção.
* **Dados Envolvidos:** `id_produto`, novos dados de preço, descrição, fotos e categoria.

---

### 3.6 Caso de Uso: Excluir
* **Ator Principal:** Pessoa Anunciante
* **Descrição da Ação:** O anunciante remove permanentemente um produto ou anúncio inativo da plataforma.
* **Pré-requisito:** O usuário deve possuir permissão de propriedade sobre o anúncio selecionado.

| Passo | Fluxo Normal |
|:---:|:---|
| **01** | O usuário seleciona a opção "Excluir Anúncio" em seu painel de gestão. |
| **02** | O aplicativo exibe uma janela de confirmação: *"Deseja realmente excluir este anúncio?"*. |
| **03** | O usuário confirma a exclusão clicando em "Sim, excluir". |
| **04** | O aplicativo envia o `id_anuncio` para o servidor remover o registro. |
| **05** | O sistema remove os dados do banco e exibe a mensagem de sucesso (*Msg04: "Anúncio excluído com sucesso"*). |

* **Fluxo Alternativo:**
  * **03.1.** Se o usuário clicar em "Cancelar", o processo é abortado e a tela de gestão é restaurada.
* **Dados Envolvidos:** `id_anuncio` (Identificador único do anúncio).

---

<div style="page-break-after: always;"></div>

## Módulo 4: Diagramas de Caso de Uso em Mermaid

Abaixo estão representados os diagramas estruturados