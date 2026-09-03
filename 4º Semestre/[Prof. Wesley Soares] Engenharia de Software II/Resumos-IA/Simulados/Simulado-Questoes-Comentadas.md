Com base no conteúdo acadêmico fornecido do **Prof. Ms. Wesley Soares de Souza** (Engenharia de Software e Modelagem II / Engenharia de Software II), elaborou-se o simulado estruturado contendo 10 questões de múltipla escolha (com gabarito comentado) e 5 questões discursivas/estudos de caso práticos.

---

# 📝 SIMULADO DE ENGENHARIA DE SOFTWARE II
**Professor:** Wesley Soares de Souza  
**Tema:** Engenharia de Requisitos, Ciclo de Vida do Software, Projeto Orientado a Objetos e Princípios de Design.

---

## PARTE 1: Questões de Múltipla Escolha

### Questão 1
Durante a fase de elicitação de requisitos, o analista se depara com a seguinte fala de um stakeholder: *"O sistema precisa ser rápido ao processar os relatórios de vendas."* De acordo com os conceitos de Engenharia de Requisitos abordados pelo Prof. Wesley Soares, como essa declaração deve ser tratada pelo analista?
* a) Como um requisito funcional claro, pronto para ser implementado, bastando registrar que a velocidade é prioridade.
* b) Como um requisito não funcional ambíguo que precisa ser investigado e transformado em uma métrica mensurável (ex: o relatório deve ser gerado em até 5 segundos).
* c) Como uma restrição arquitetural que obriga o uso de computação em nuvem e bancos de dados NoSQL.
* d) Como um conflito de stakeholders que deve ser resolvido utilizando a técnica de Brainstorming.
* e) Como um artefato de Análise Documental obsoleto que deve ser descartado.

> **Gabarito Comentado:** **B**  
> *Comentário:* Conforme o material, expressões como "o sistema precisa ser rápido" representam ambiguidades e necessidades não explícitas em termos métricos. O analista precisa investigar e esclarecer o contexto para transformá-lo em um **requisito não funcional** mensurável (ex: tempo de resposta específico), diferenciando-o dos requisitos funcionais (o que o sistema faz).

---

### Questão 2
O levantamento de requisitos apresenta diversas dificuldades inerentes à comunicação humana e aos processos organizacionais. Assinale a alternativa que **NÃO** representa uma dificuldade clássica apresentada na aula de elicitação de requisitos:
* a) As necessidades dos usuários nem sempre estão explícitas.
* b) Diferentes stakeholders podem possuir prioridades conflitantes.
* c) Os clientes conseguem explicar de forma clara, imediata e definitiva tudo o que precisam logo na primeira reunião.
* d) O cliente pode mudar de ideia ao longo do processo.
* e) Problemas reais podem estar escondidos dentro do processo atual utilizado pela empresa.

> **Gabarito Comentado:** **C**  
> *Comentário:* O material afirma explicitamente que *"os stakeholders nem sempre conseguem explicar claramente aquilo que precisam"* e que o cliente pode mudar de ideia. Portanto, a alternativa C descreve o oposto da realidade enfrentada na engenharia de requisitos.

---

### Questão 3
Sobre as técnicas tradicionais e complementares de elicitação de requisitos apresentadas pelo Prof. Wesley Soares, analise as afirmativas abaixo:
I. A entrevista estruturada utiliza perguntas previamente definidas, enquanto a semiestruturada combina perguntas preparadas com liberdade para explorar o assunto.  
II. A observação e a análise documental ajudam a revelar atividades não documentadas, atalhos, exceções e problemas que o usuário considera óbvios.  
III. O Brainstorming deve focar primeiro na avaliação crítica e rigorosa de cada ideia individualmente, para só depois permitir a geração em grande quantidade.  

Estão corretas:
* a) Apenas I.
* b) Apenas III.
* c) Apenas I e II.
* d) Apenas II e III.
* e) I, II e III.

> **Gabarito Comentado:** **C**  
> *Comentário:* A afirmativa III está incorreta porque a regra de ouro do Brainstorming descrita no material é exatamente o oposto: **"Quantidade primeiro, avaliação depois"**. As afirmativas I e II descrevem perfeitamente os conceitos de entrevistas e observação/análise documental.

---

### Questão 4
Ao projetar a interface e validar fluxos de navegação junto aos usuários sem precisar codificar o sistema do zero, o analista pode utilizar ferramentas como Figma ou Penpot. Essa prática de elicitação e validação é conhecida como:
* a) Análise Documental.
* b) Prototipação.
* c) Event Storming.
* d) Engenharia Reversa.
* e) Refatoração.

> **Gabarito Comentado:** **B**  
> *Comentário:* O uso de wireframes e protótipos navegáveis (através de ferramentas como Figma ou Penpot) faz parte da técnica de **Prototipação**, que permite mostrar uma imagem ou modelo visual para descobrir requisitos ocultos que uma entrevista textual não revelaria.

---

### Questão 5
No ciclo de vida de um projeto de software apresentado nas aulas, qual é a principal diferença conceitual entre as fases de **Análise** e **Projeto**?
* a) A Análise define "Como fazer" (tecnologias, códigos e banco de dados), enquanto o Projeto define "O que fazer" (regras de negócio e necessidades).
* b) A Análise traduz o modelo de negócios em código executável, enquanto o Projeto cuida exclusivamente da demissão de equipe e cronograma.
* c) A Análise foca em descobrir "O que o sistema deve fazer" (modelando o domínio e os requisitos), enquanto o Projeto traduz esse modelo em uma solução técnica, definindo "Como fazer".
* d) A Análise ocorre estritamente após a implantação e a manutenção corretiva, enquanto o Projeto ocorre antes do levantamento de requisitos.
* e) Não há diferenças; ambos os termos são sinônimos perfeitos na Engenharia de Software moderna.

> **Gabarito Comentado:** **C**  
> *Comentário:* O material sintetiza perfeitamente esse conceito na aula de Projeto OO: *Análise $\rightarrow$ O que fazer* / *Projeto $\rightarrow$ Como fazer*. A análise compreende o problema e os requisitos, e o projeto estabelece as bases estruturais e técnicas da solução.

---

### Questão 6
Sobre os princípios fundamentais da Orientação a Objetos aplicados ao projeto de software, relacione a definição com seu respectivo princípio: *"Proteger os dados internos de uma classe, permitindo acesso controlado por meio de métodos específicos (Getters e Setters)"*.
* a) Herança
* b) Polimorfismo
* c) Encapsulamento
* d) Abstração
* e) Acoplamento

> **Gabarito Comentado:** **C**  
> *Comentário:* O **Encapsulamento** tem exatamente o papel de proteger o estado interno da classe (geralmente definindo atributos como `private`) e expor formas controladas de manipulação através de métodos de acesso ou regras de negócio validadas.

---

### Questão 7
Dentro das fases de um projeto de software, a etapa que engloba a definição de estilos arquiteturais (como Camadas, MVC, *Clean Architecture* ou *Microservices*), preocupando-se com segurança, escalabilidade e organização de componentes, é chamada de:
* a) Testes End-to-End.
* b) Arquitetura de Software.
* c) Codificação Manual.
* d) Elicitação de Requisitos Funcionais.
* e) Refatoração de Code Smells.

> **Gabarito Comentado:** **B**  
> *Comentário:* Conforme a aula sobre as fases do projeto de software, a **Arquitetura de Software** é a fase responsável pelas grandes decisões estruturais, organização de componentes, persistência, integração e escolha de estilos arquiteturais (MVC, Camadas, Microsserviços, etc.).

---

### Questão 8
O método **MoSCoW** é citado no material como uma ferramenta importante para:
* a) Gerenciar repositórios de código e controle de versão (Git).
* b) Executar testes automatizados de integração contínua.
* c) Priorizar requisitos, ajudando a focar no que é essencial para o projeto.
* d) Desenhar protótipos de alta fidelidade em ferramentas open-source como o Penpot.
* e) Detectar *code smells* e anomalias de código em linguagens orientadas a objetos.

> **Gabarito Comentado:** **C**  
> *Comentário:* O método MoSCoW (*Must have, Should have, Could have, Won't have*) é uma técnica clássica utilizada na Engenharia de Requisitos para **priorizar** funcionalidades e focar no que é essencial.

---

### Questão 9
Sobre os tipos de testes de software abordados no ciclo de projeto, assinale a alternativa correta:
* a) Os testes unitários validam o sistema inteiro de ponta a ponta (End-to-End), testando a infraestrutura de rede e banco de dados simultaneamente.
* b) Os testes unitários verificam pequenas unidades de código isoladamente, enquanto os testes de integração verificam a comunicação correta entre os componentes.
* c) Os testes de aceitação são realizados exclusivamente pelos desenvolvedores antes mesmo de escrever qualquer linha de código de negócio.
* d) Os testes automatizados substituem totalmente a necessidade de elicitação de requisitos.
* e) A garantia da qualidade ocorre unicamente após a entrega final ao cliente, sem etapas intermediárias.

> **Gabarito Comentado:** **B**  
> *Comentário:* O material define de forma clara que os testes unitários verificam pequenas unidades de código, os testes de integração validam a comunicação entre os componentes e os testes End-to-End validam o sistema de ponta a ponta.

---

### Questão 10
Qual das seguintes opções descreve corretamente a diferença entre um **Requisito Funcional** e um **Requisito Não Funcional**, com base nos exemplos apresentados em aula?
* a) O funcional dita a cor da interface gráfica (Figma), enquanto o não funcional dita o salário dos desenvolvedores.
* b) O funcional define *o que o sistema deve fazer* (ex: cadastrar produtos), enquanto o não funcional define *como o sistema deve se comportar ou suas restrições* (ex: tempo de resposta de 2 segundos).
* c) O funcional é opcional para o cliente, ao passo que o não funcional é obrigatório.
* d) O funcional trata de infraestrutura de servidores e contêineres, enquanto o não funcional trata de diagramas UML.
* e) Não existe distinção técnica; ambos pertencem à mesma categoria de documentação de código.

> **Gabarito Comentado:** **B**  
> *Comentário:* Essa é a definição clássica e exata apresentada pelo Prof. Wesley Soares: Requisito Funcional indica o comportamento funcional (*o que o sistema deve fazer*) e Requisito Não Funcional indica atributos de qualidade, restrições e comportamento sistêmico (*como o sistema deve se comportar*).

---

## PARTE 2: Questões Discursivas e Estudos de Caso Práticos

### Questão 11 (Estudo de Caso - Elicitação de Requisitos)
Um analista de sistemas foi contratado para desenvolver um software de controle de pedidos para uma rede de restaurantes locais. Ao perguntar ao gerente o que o sistema precisava fazer, obteve a resposta: *"Preciso apenas de um sistema para controlar meus pedidos e melhorar o meu negócio."* Utilizando os conceitos de elicitação de requisitos ministrados pelo Prof. Wesley Soares:
a) Explique por que essa frase inicial **não** é suficiente para iniciar o desenvolvimento.  
b) Cite pelo menos **quatro** perguntas investigativas que o analista deve fazer para detalhar esse cenário inicial.  

> **Gabarito / Diretrizes de Resposta:**  
> a) A frase é vaga, genérica e esconde regras de negócio essenciais. Como visto em aula, um software não pode ser desenvolvido com base em premissas superficiais, pois necessidades não estão explícitas, há riscos de ambiguidades e o escopo ficaria totalmente desregulado.  
> b) Exemplos de perguntas baseadas no material: *Quem registra os pedidos? Quem consulta? O cliente pode cancelar o pedido? Existe aprovação prévia? Como o pagamento é realizado? O estoque precisa ser atualizado automaticamente?*

---

### Questão 12 (Análise vs. Projeto e Ciclo de Vida)
O ciclo de vida de um projeto de software engloba 10 fases principais, desde a identificação do problema até a operação, manutenção e evolução. Explique a importância crítica da transição entre a fase de **Análise e Especificação de Requisitos** e a fase de **Arquitetura e Projeto Detalhado**, destacando o famoso ditado abordado em aula: *"Faça a coisa certa e faça certo a coisa"*.

> **Gabarito / Diretrizes de Resposta:**  
> A fase de análise assegura que estamos construindo *"a coisa certa"* (compreendendo perfeitamente o problema, modelando o domínio e especificando corretamente o que o software deve fazer). Já a fase de projeto e arquitetura garante que vamos *"fazer certo a coisa"* (definindo a estrutura correta, padrões de projeto, alta coesão, baixo acoplamento e escolhas tecnológicas adequadas). Sem essa transição estruturada, corre-se o risco de desenvolver perfeitamente um software que resolve o problema errado ou que possui uma arquitetura frágil e insustentável.

---

### Questão 13 (Princípios de Design Orientado a Objetos)
Em um sistema de gestão comercial, um desenvolvedor decide criar a classe `Venda`. Para atender aos princípios de design OO e boas práticas de encapsulamento e coesão apresentados na disciplina:  
a) Como os atributos internos dessa classe devem ser declarados em termos de visibilidade, e como o restante do sistema deve interagir com eles?  
b) Dê um exemplo prático de como o encapsulamento protege uma regra de negócio crítica (por exemplo, a manipulação de estoque ou alteração de valores) nessa classe.  

> **Gabarito / Diretrizes de Resposta:**  
> a) Os atributos devem ser declarados como `private` (privados), e o acesso ou modificação deve ocorrer de forma controlada por meio de métodos públicos de acesso (`getters` e `setters`) ou operações de negócio específicas.  
> b) Em vez de permitir que qualquer parte do sistema altere diretamente o estoque subtraindo itens de forma livre, a classe `Venda` deve expor um método controlado (ex: `efetuarBaixaEstoque()` ou `adicionarItem()`) que valida internamente se há saldo disponível em estoque antes de autorizar a alteração, garantindo que as regras de negócio sejam sempre respeitadas.

---

### Questão 14 (Técnicas Complementares: Prototipação e Ferramentas)
O Prof. Wesley Soares destaca que ferramentas como o **Figma** e o **Penpot** são excelentes alternativas na fase de projeto e levantamento. Explique como a utilização de protótipos navegáveis e wireframes pode auxiliar o analista a descobrir requisitos implícitos ou falhas que uma simples entrevista em texto não conseguiria revelar.

> **Gabarito / Diretrizes de Resposta:**  
> A prototipação visual tira o usuário do plano abstrato. Quando o stakeholder interage com um protótipo navegável (