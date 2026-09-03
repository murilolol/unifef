# [Prof. Wesley Soares] Estrutura de Dados I - Modelagem e Arquitetura

Com base nas diretrizes da disciplina de **Estrutura de Dados I** (4º Semestre) e nos requisitos específicos do **Trabalho AV1** (que exige a implementação das classes `Main`, `No` e `ListaLigada`), apresentamos a seguir a modelagem completa em diagramas **Mermaid**.

---

## 1. Diagrama de Classes UML (Domínio da Matéria)

Este diagrama modela a estrutura orientada a objetos fundamental para listas encadeadas dinâmicas (visto na Aula 05), contemplando os requisitos de envio do trabalho AV1.

```mermaid
classDiagram
    direction LR
    class Main {
        +main(String[] args)
    }

    class No {
        -int dado
        -No proximo
        +No(int dado)
        +getDado() int
        +setDado(int dado) void
        +getProximo() No
        +setProximo(No proximo) void
    }

    class ListaLigada {
        -No primeiro
        -int tamanho
        +ListaLigada()
        +estaVazia() boolean
        +inserirInicio(int dado) void
        +inserirFim(int dado) void
        +remover(int dado) boolean
        +imprimir() void
    }

    Main --> ListaLigada : utiliza
    ListaLigada o-- No : gerencia
    No --> No : aponta para próximo
```

### 💡 Explicação do Diagrama de Classes
* **`Main`**: Classe de ponto de entrada responsável por instanciar a estrutura e testar suas operações.
* **`No`**: Representa a unidade básica de armazenamento (nó), contendo a carga útil (`dado`) e uma referência (`proximo`) para o próximo elemento da cadeia.
* **`ListaLigada`**: Gerencia a sequência de nós através de um ponteiro de início (`primeiro`). Ela implementa os métodos de manipulação dinâmica dos dados exigidos na disciplina.

---

## 2. Diagrama de Sequência (Fluxo de Inserção em Lista Ligada)

O diagrama abaixo detalha o fluxo técnico de execução quando o sistema realiza a inserção de um novo elemento no início de uma lista ligada dinâmica.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario as Main
    participant Lista as ListaLigada
    participant NovoNo as No

    Usuario->>Lista: inserirInicio(valor)
    activate Lista
    
    Note over Lista: Cria o novo nó com o valor
    Lista->>NovoNo: new No(valor)
    activate NovoNo
    NovoNo-->>Lista: referência do nó
    deactivate NovoNo

    Lista->>Lista: estaVazia()?
    alt A lista está vazia
        Lista->>NovoNo: setProximo(null)
    else A lista já possui elementos
        Note over Lista,NovoNo: O novo nó aponta para onde o antigo primeiro apontava
        Lista->>Lista: proximoAtual = primeiro
        Lista->>NovoNo: setProximo(proximoAtual)
    end

    Note over Lista: Atualiza o ponteiro da lista
    Lista->>Lista: primeiro = novoNo
    Lista->>Lista: tamanho++
    
    Lista-->>Usuario: Operação concluída
    deactivate Lista
```

### 💡 Explicação do Fluxo de Sequência
1. **Solicitação**: A classe `Main` invoca o método `inserirInicio(valor)` na instância de `ListaLigada`.
2. **Alocação Dinâmica**: A lista cria um novo objeto `No` alocando memória dinamicamente.
3. **Verificação de Estado**: Se a lista estiver vazia, o ponteiro de próximo do novo nó aponta para `null`. Caso contrário, ele assume a referência do antigo primeiro elemento.
4. **Atualização de Ponteiros**: O ponteiro principal da lista (`primeiro`) é atualizado para apontar para o novo nó recém-criado, concluindo a inserção em tempo $O(1)$.

---

## 3. Diagrama Arquitetural / Entidade-Relacionamento (Organização de Arquivos do Trabalho AV1)

Este diagrama arquitetural ilustra a estrutura de arquivos e dependências exigidas nas instruções de entrega do **Trabalho AV1**.

```mermaid
graph TD
    subgraph Projeto [Trabalho AV1 - Estrutura de Dados I]
        M[Main.java <br/><i>Classe Principal / Testes</i>]
        L[ListaLigada.java <br/><i>Lógica de Manipulação Dinâmica</i>]
        N[No.java <br/><i>Unidade de Armazenamento</i>]
    end

    M -->|Instancia e manipula| L
    L -->|Composta por múltiplos| N
    N -->|Referencia o próximo| N

    style Projeto fill:#f9f,stroke:#333,stroke-width:2px
    style M fill:#bbf,stroke:#333,stroke-width:1px
    style L fill:#bfb,stroke:#333,stroke-width:1px
    style N fill:#ff9,stroke:#333,stroke-width:1px
```

### 💡 Explicação da Arquitetura do Projeto
* **Divisão de Responsabilidades**: O projeto segue o padrão modular requisitado pelo Prof. Wesley Soares para o AV1.
* **`Main.java`** atua estritamente na interface de execução.
* **`ListaLigada.java`** concentra os algoritmos de complexidade e manipulação de ponteiros.
* **`No.java`** modela o encapsulamento do dado e do encadeamento de memória, garantindo o correto funcionamento das listas dinâmicas vistas na Aula 05.