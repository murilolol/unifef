# 📋 Resumo Executivo: Engenharia de Software I
**Disciplina:** Engenharia de Software I (3º Semestre)  
**Professor Responsável:** Prof. Marcelo Boer  
**Instituição:** Centro Universitário de Fernandópolis (FEF)  
**Foco do Documento:** Processos de Software, Ciclo de Vida, Requisitos, Modelagem UML (Astah UML) e Casos de Uso aplicados ao estudo de caso **"Desapega Já"**.

---

## 📱 1. Contexto do Aplicativo: "Desapega Já"

O aplicativo **Desapega Já** é uma plataforma móvel de economia colaborativa voltada para conectar compradores e vendedores de uma mesma região. Seu objetivo principal é incentivar a reutilização de produtos inativados (como roupas, eletrônicos, móveis e livros) e promover o consumo consciente.

* **Modelo de Negócio:** 
  * **Vendedores (Anunciantes):** Podem anunciar produtos livremente informando *fotos, descrição, preço e categoria*, sem obrigatoriedade de cadastro prévio para anunciar.
  * **Compradores (Interessados):** Devem realizar um cadastro simples para negociar e contatar os vendedores.

---

## ⚙️ 2. Engenharia de Requisitos

### Requisitos Funcionais (RF)
Representam as ações e comportamentos que o sistema deve executar para atender às necessidades dos usuários:
1. **RF01:** Facilitar a venda de produtos.
2. **RF02:** Permitir anúncio de produtos.
3. **RF03:** Permitir o cadastro de pessoas interessadas nas compras (Clientes).
4. **RF04:** Facilitar a busca de produtos por proximidade (geolocalização/bairro).
5. **RF05:** Gerar histórico de contatos, compras realizadas e avaliações recebidas.
6. **RF06:** Permitir o cadastro de pessoas que farão a oferta de produtos (Anunciantes).
7. **RF07:** Permitir o cadastro de produtos a serem vendidos.
8. **RF08:** Possibilitar a troca de mensagens entre as partes interessadas.
9. **RF09 a RF15:** Incluem operações completas de CRUD (Cadastrar, Listar, Editar, Excluir e Buscar) para perfis, produtos e anúncios, além de controle de login.

### Requisitos Não-Funcionais (RNF)
Definem os atributos de qualidade, restrições e características de desempenho do sistema:
1. **RNF01:** Fácil usabilidade (Interface intuitiva e amigável).
2. **RNF02:** Segurança (Proteção de dados e credenciais).
3. **RNF03:** Prático e acessível.
4. **RNF04:** Integridade de dados.

---

## 🗂️ 3. Modelo de Domínio e Classes Principais

O sistema é estruturado através das seguintes classes e atributos essenciais mapeados no projeto:

* **Anunciante / Interessado:** Nome completo, e-mail, telefone (com WhatsApp), senha de acesso, cidade, estado, foto de perfil, CPF, data de nascimento e bairro.
* **Anúncio:** Fotos, descrição, preço e categoria.
* **Categoria:** Nome da categoria.
* **Compra:** Data da compra, valor, produtos, interessado e anunciante.
* **Histórico de Contatos:** Relacionamento entre interessado e anunciante.

---

## 📐 4. Modelagem de Casos de Uso (Padrão Astah UML)

O desenvolvimento prático utilizando o **Astah UML** exige a especificação detalhada de Casos de Uso (DCU). Os fluxos principais exigidos no escopo da disciplina seguem a estrutura padrão:

### Operações Fundamentais a Descrever:
1. **Logar:** Autenticação de usuário no sistema via e-mail e senha.
2. **Cadastrar:** Registro de novos usuários e dados de perfil.
3. **Listar:** Exibição de registros (produtos, anúncios, dados de perfil).
4. **Carregar:** Inicialização de informações e dados dinâmicos.
5. **Alterar (Editar):** Modificação de dados cadastrados (perfil, produtos, anúncios).
6. **Excluir:** Remoção lógica ou física de itens do sistema.

### Estrutura de Documentação de um DCU (Exemplo: Realizar Login)
* **Ator Principal:** Usuário Cliente / Anunciante.
* **Pré-requisito:** O usuário deve estar pré-cadastrado no aplicativo.
* **Fluxo Normal:**
  1. O usuário acessa o aplicativo.
  2. O aplicativo exibe a tela de login.
  3. O usuário informa e-mail e senha e clica em "Logar".
  4. O sistema valida os dados e exibe a tela principal.
* **Fluxo Alternativo:**
  * Se o usuário não estiver cadastrado ou errar as credenciais, o sistema exibe uma mensagem de erro (*"Usuário não cadastrado"* ou *"E-mail/Senha inválidos"*) e redireciona para a tela correspondente.

---

## 🔄 5. Processos de Software e Ciclo de Vida (SDLC)

Conforme as diretrizes para a apresentação dos métodos de engenharia de software, o ciclo de vida de desenvolvimento deve abranger:
* **Estrutura do Modelo de Processo:** Apresentação conceitual, histórico (origem, criadores e motivação), fases metodológicas ilustradas, exemplos práticos de uso e análise de custos de implantação.
* **Atividades de Framework:** Comunicação, Planejamento, Modelagem, Construção e Implantação.

---

## 🔗 6. Recursos e Links Úteis
* **Questionários de Revisão (AV1):** Disponibilizados via Google Forms para fixação de conteúdo pelo Prof. Marcelo Boer.
* **Ferramenta de Modelagem:** *Astah UML* (Licenciamento acadêmico para diagramação de Casos de Uso, Classes e Atores).