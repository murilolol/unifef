# 📌 ENG. DE SOFTWARE I — PROF. MARCELO BOER | CHEAT SHEET (AV1/AV2)

---

## 1. ENGENHARIA DE REQUISITOS: APP "DESAPEGA JÁ"
*Requisitos Funcionais (RF)* definem as **funções/ações** do sistema. *Requisitos Não-Funcionais (RNF)* definem as **qualidades/restrições** de operação.

### Requisitos Funcionais (RF)
*   **RF01:** Facilitar a venda de produtos da mesma região.
*   **RF02:** Permitir anúncio de produtos (fotos, descrição, preço, categoria).
*   **RF03:** Permitir cadastro de pessoas interessadas (compradores).
*   **RF04:** Facilitar a busca de produtos por proximidade (bairro/cidade).
*   **RF05:** Gerar automaticamente histórico de contatos/compras e avaliações.
*   **RF06:** Permitir cadastro de anunciantes (vendedores).
*   **RF07:** Permitir o cadastro de produtos a serem vendidos.
*   **RF08:** Possibilitar troca de mensagens entre as partes.

### Requisitos Não-Funcionais (RNF)
*   **RNF01:** Fácil usabilidade (interface intuitiva para dispositivos móveis).
*   **RNF02:** Segurança (validação de CPF e proteção de dados de login).
*   **RNF03:** Prático e acessível (plataforma móvel responsiva).
*   **RNF04:** Integridade de dados (consistência no histórico e avaliações).

---

## 2. MODELAGEM DE CASOS DE USO (UML)
*   **Atores Primários:** `Pessoa Anunciante` (Vendedor) e `Pessoa Cliente` (Interessado/Comprador).

### Tabela de Casos de Uso Essenciais (Mapeamento de Fluxo)

| N° | Caso de Uso | Ator Principal | Entrada (Dados) | Saída (Resultado/Mensagem) |
| :--- | :--- | :--- | :--- | :--- |
| **01** | Realizar Login Aplicativo | Anunciante / Cliente | e-mail, senha | Msg01 (Sucesso) / Tela Principal |
| **02** | Cadastrar Pessoa Anunciante | Pessoa Anunciante | dados_pessoa_anunciante | Msg02 (Cadastro Realizado) |
| **03** | Listar Pessoa Anunciante | Pessoa Anunciante | - | Dados do Perfil do Anunciante |
| **04** | Editar Pessoa Anunciante | Pessoa Anunciante | dados_pessoa_anunciante | Msg03 / Dados Atualizados |
| **05** | Cadastrar Produto | Pessoa Anunciante | dados_produto | Msg02 (Cadastro Realizado) |
| **06** | Listar Produtos | appDesapegaJá (Sistema) | - | Dados do Produto na Tela |
| **07** | Editar Produto | Pessoa Anunciante | id_produto | Dados do Produto para Alteração |
| **08** | Excluir Produto | Pessoa Anunciante | id_produto | Msg04 (Excluído com Sucesso) |
| **10** | Cadastrar Anúncio | Pessoa Anunciante | dados_anuncio | Msg02 (Cadastro Realizado) |
| **15** | Trocar Mensagens | Anunciante e Cliente | dados_mensagens | Histórico de Mensagens na Tela |

---

## 3. TEMPLATE DE DESCRIÇÃO DE CASO DE USO (PADRÃO BOER)
*Item obrigatório de prova. Siga rigorosamente a estrutura de numeração decimal para fluxos alternativos.*

### Quadro X – DCU Individual: [Nome do Caso de Uso]
*   **Ator Principal:** [Ator que inicia a ação]
*   **Descrição da Ação:**