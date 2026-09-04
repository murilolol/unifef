# Aula — Organização e Gerenciamento da Memória Real

> **Professor:** Guilherme de Morais
> **Disciplina:** Sistemas Operacionais (3º Semestre)
> **Tema:** Fundamentos, hierarquia e estratégias de alocação de memória no sistema operacional

## Objetivo da aula

Compreender o papel da memória real (principal/física) no sistema computacional, a hierarquia de memória (cache, principal, secundária), as estratégias de gerenciamento (busca, posicionamento, substituição), as técnicas de alocação contígua e não contígua (paginação e segmentação), o conceito de memória virtual e os principais desafios de proteção, compartilhamento e desempenho envolvidos.

## Introdução à memória real

A **Memória Real** (ou **Memória Principal**, **Memória Física**, **Memória Primária**) é o componente essencial para armazenamento de programas e dados, permitindo acesso direto e rápido pelo processador. Sua organização e gerenciamento são cruciais para otimizar o desempenho e a estabilidade dos sistemas modernos.

### Memória principal × armazenamento secundário

| Aspecto | Memória Real (Principal) | Armazenamento Secundário |
| :--- | :--- | :--- |
| Localização | Próxima ao processador, acesso direto e rápido. | Dispositivos como HDDs e SSDs. |
| Custo | Elevado por *byte*. | Baixo custo, capacidade massiva. |
| Velocidade | Alta — essencial para execução de programas ativos. | Lenta, não diretamente acessível ao processador. |
| Uso típico | Espaço de trabalho ativo e imediato da CPU. | *Backup* e arquivamento de dados. |

Historicamente, a memória principal sempre foi mais cara que a secundária, impactando fortemente o projeto dos sistemas. Ainda que os preços tenham caído com o tempo, a organização e o gerenciamento da memória principal permanecem aspectos críticos do projeto de qualquer sistema operacional.

### Desafios da organização da memória

- **Multiprogramação — processos simultâneos:** a questão central é decidir se o sistema deve alocar a memória principal a um único processo por vez, ou permitir que múltiplos processos residam e sejam executados simultaneamente. Essa decisão impacta diretamente o aproveitamento da CPU e o *throughput* do sistema.
- **Particionamento — espaço da memória:** se a multiprogramação é adotada, surge o desafio de dividir a memória principal. É possível atribuir a cada processo a mesma quantidade de espaço, ou segmentar a memória em partições de tamanhos diferentes, otimizando a utilização e evitando fragmentação.

### Desafios de particionamento e alocação

- **Definição de partições:** podem ser definidas de forma **estática** (rígida, para longos períodos) ou **dinâmica** (o sistema se adapta às mudanças de necessidade dos processos).
- **Alocação de processos:** o sistema pode exigir que processos executem em partições específicas, ou permitir a flexibilidade de alocá-los onde houver espaço disponível — impactando eficiência e fragmentação.
- **Blocos contíguos:** a decisão de alocar processos em blocos contíguos de memória, ou permitir sua divisão em blocos separados (paginação, segmentação), é crucial para gerenciar a fragmentação externa e a utilização da memória principal.

## Hierarquia de memória

A necessidade de melhorar o desempenho do sistema, frente às crescentes demandas de memória, levou à criação de uma **hierarquia de memória**, com diferentes níveis de velocidade e custo:

- **Cache** — o nível mais alto da hierarquia, um tipo de memória muito mais rápida que a memória principal, estrategicamente localizada no processador (ou muito próxima a ele) para acesso rápido aos dados. É extremamente cara em comparação com a memória principal, o que justifica seu tamanho reduzido.
- **Memória principal (RAM)** — espaço de trabalho ativo e imediato da CPU.
- **Armazenamento secundário** — disco, usado para *backup*, arquivamento e como extensão via memória virtual.

O processador acessa dados e programas primeiro em seu *cache*, buscando otimizar a velocidade. Em caso de **falha de cache** (*cache miss*), o acesso é estendido à memória principal e, em seguida, ao armazenamento secundário, seguindo a ordem crescente de latência.

## Estratégias de gerenciamento de memória

O gerenciamento de memória é uma função crítica do sistema operacional, normalmente controlada por software, determinando como o espaço de memória disponível é alocado eficientemente entre os processos em execução. Três estratégias fundamentais orientam essa alocação:

### 1. Estratégia de busca

Determina **quando** um bloco de dados deve ser carregado da memória secundária para a memória principal.

| Estratégia | Comportamento | Vantagem | Risco |
| :--- | :--- | :--- | :--- |
| **Busca sob demanda** | Dados são transferidos para a memória principal apenas quando ocorre uma falha de página (*page fault*) ou de segmento, indicando que são imediatamente necessários. | Minimiza o consumo de memória. | Introduz latência no primeiro acesso aos dados. Base da paginação e segmentação sob demanda. |
| **Busca antecipada** | O sistema prediz quais dados serão solicitados em breve e os carrega proativamente (pré-paginação). | Reduz o tempo de espera futuro. | Risco de carregar dados desnecessários, desperdiçando memória e E/S se a predição estiver incorreta. |

### 2. Estratégia de posicionamento

Define **onde**, na memória principal, um bloco de dados será alocado:

| Estratégia | Comportamento | Observações |
| :--- | :--- | :--- |
| **First-fit** | Aloca o primeiro bloco livre grande o suficiente para o processo. | Simples e rápida, mas pode levar a fragmentação externa significativa ao longo do tempo. |
| **Best-fit** | Aloca o menor bloco livre grande o suficiente para o processo, minimizando o desperdício de espaço dentro do bloco alocado. | Em teoria eficiente, mas frequentemente gera muitos fragmentos pequenos, aumentando o tempo de busca. |
| **Worst-fit** | Aloca o maior bloco livre disponível, na expectativa de que o fragmento remanescente seja útil para alocações futuras. | Pode consumir rapidamente os maiores blocos, dificultando alocação de processos grandes posteriormente. |

### 3. Estratégia de substituição

Estabelece qual bloco de dados deve ser removido da memória principal quando não há espaço disponível para um novo bloco — essencial para evitar o *thrashing* (troca excessiva de páginas):

| Algoritmo | Critério de remoção |
| :--- | :--- |
| **FIFO** (*First-In, First-Out*) | Remove a página que está na memória há mais tempo, independentemente de seu uso recente. |
| **LRU** (*Least Recently Used*) | Descarta a página que não foi referenciada pela CPU pelo período mais longo. |
| **LFU** (*Least Frequently Used*) | Substitui a página com o menor contador de frequência de uso. |
| **Ótima (Optimal)** | Ideal teórico: remove a página que não será utilizada por mais tempo no futuro — impraticável na implementação real, pois exige conhecimento do futuro. |

## Alocação contígua × alocação não contígua

### Alocação contígua

Nesse método, o sistema operacional necessita de um bloco único e contíguo de memória principal para acomodar um programa inteiro.

- **Restrição de tamanho:** se o espaço contíguo disponível fosse insuficiente, o programa simplesmente não seria executado, limitando a capacidade de rodar aplicações maiores.
- **Uso ineficiente:** essa abordagem resulta em fragmentação interna e externa, subaproveitando a memória e dificultando a multiprogramação.

### Alocação não contígua

Permite que programas sejam divididos em blocos, ocupando espaços não adjacentes na memória principal. Isso possibilita um uso mais eficiente, reduz a fragmentação externa e permite a execução de programas maiores que a memória física contígua disponível. É a base das técnicas de **paginação** e **segmentação**.

### Fragmentação interna × externa

| Tipo | Ocorre quando | Consequência |
| :--- | :--- | :--- |
| **Fragmentação interna** | A unidade de alocação (bloco, página, segmento) é maior que o espaço solicitado pela entidade (processo, programa). | O excedente de memória dentro do bloco alocado permanece não utilizado, sendo desperdiçado mesmo estando reservado. |
| **Fragmentação externa** | A memória livre total é suficiente para uma solicitação, mas os blocos disponíveis estão dispersos em porções não contíguas. | Impede a alocação de um único bloco maior, mesmo que a soma dos fragmentos seja adequada. Mitigada por técnicas de compactação de memória. |

## Paginação

A memória física é dividida em blocos de tamanho fixo, chamados **frames** (quadros de página), cada um com endereço físico único. A memória lógica (espaço de endereço do processo) é dividida em blocos de tamanho idêntico aos *frames*, chamados **páginas**.

Uma **tabela de páginas** é uma estrutura de dados mantida pelo sistema operacional que estabelece o mapeamento entre os endereços lógicos das páginas e os endereços físicos dos *frames*, permitindo acesso não contíguo. Em esquemas de paginação multinível, esse mapeamento é dividido em um Diretório de Páginas e Tabelas de Páginas de nível inferior, reduzindo o tamanho das estruturas de tradução mantidas em memória.

## Segmentação de memória

**Segmentação** é uma técnica de alocação não contígua baseada na visão **lógica** do programa, dividindo-o em unidades variáveis chamadas **segmentos** (código, dados, pilha), que podem ser carregadas em qualquer lugar da memória física.

Uma **tabela de segmentos**, mantida pelo sistema operacional para cada processo, mapeia endereços lógicos para físicos. A principal vantagem da segmentação é facilitar a proteção e o compartilhamento eficiente de código e dados entre diferentes processos, já que cada segmento pode ter permissões próprias.

## Memória virtual

A **Memória Virtual** cria a ilusão de que um processo tem acesso a uma memória muito maior do que a memória física realmente disponível, utilizando o armazenamento secundário (disco) como extensão da memória principal.

- Baseia-se em paginação ou segmentação para gerenciar a troca de dados entre a memória principal e o disco (*swapping*/*paging*).
- Permite a execução de programas maiores do que a memória física, otimizando o uso dos recursos do sistema.
- Aumenta o grau de multiprogramação, permitindo que mais programas sejam executados simultaneamente.
- Simplifica o gerenciamento de memória para o programador, abstraindo a complexidade da memória física subjacente.

### Swapping × Paging

| Conceito | Descrição |
| :--- | :--- |
| **Swapping** | Troca um processo inteiro, ou um segmento grande, entre a memória principal e o disco. |
| **Paging** (paginação) | Abordagem mais comum e granular: carrega na memória principal apenas as páginas do processo que são realmente necessárias. |
| **Falha de página** (*page fault*) | Ocorre quando uma página requerida não está presente na memória principal, acionando seu carregamento a partir do disco. |

## Proteção e compartilhamento de memória

### Proteção de memória

A estabilidade do sistema é mantida ao impedir que um processo acesse a memória de outro sem permissão — crucial para a segurança. A prevenção de corrupção garante que nenhum processo danifique o sistema operacional ou outros programas em execução. Essa proteção normalmente é implementada via hardware, utilizando **Unidades de Gerenciamento de Memória** (MMU — *Memory Management Unit*):

- **Registradores de limite e base** delimitam o intervalo de endereços que cada processo pode acessar legalmente.
- **Tabelas de páginas/segmentos** incluem bits de proteção que definem permissões de leitura, escrita e execução para cada parte da memória.

### Compartilhamento de memória

Permite que múltiplos processos acessem a mesma região da memória principal, otimizando o uso de recursos:

- **IPC eficiente:** facilita comunicação rápida e direta entre processos.
- **Otimização de memória:** reduz a duplicação de bibliotecas de código e dados comuns, economizando espaço.
- **Sincronização necessária:** exige mecanismos como semáforos ou monitores para coordenar acessos e evitar conflitos.
- **Segurança:** é necessário implementar controles para garantir que o compartilhamento ocorra apenas entre processos autorizados.

## Alocação dinâmica de memória e coleta de lixo

A **alocação dinâmica** permite que programas solicitem e liberem memória em tempo de execução, adaptando-se a necessidades variáveis. O **Heap** é a região de memória dedicada a esse tipo de alocação. Em linguagens como C/C++, funções como `malloc()`, `calloc()`, `realloc()` e `free()` são essenciais para manipular a memória dinamicamente.

- O gerenciamento de blocos livres é um desafio, pois o sistema precisa registrar e otimizar os espaços de memória disponíveis.
- A fragmentação (interna ou externa) pode reduzir a eficiência do uso da memória, se não for adequadamente controlada.
- **Vazamentos de memória** (*memory leaks*) ocorrem quando memória alocada não é liberada, levando ao esgotamento gradual dos recursos disponíveis.

A **coleta de lixo** (*garbage collection*), presente em linguagens como Java e Python, automatiza a identificação e liberação de memória não referenciada, reduzindo vazamentos e simplificando o trabalho do programador — ao custo de possíveis pausas (*stalls*) na execução durante o processo de liberação.

## Tendências e desafios atuais

- **Escalabilidade:** gerenciar eficazmente memória em sistemas que lidam com terabytes de dados e milhares de núcleos de processamento.
- **Segurança da memória:** proteger contra ataques como *Rowhammer*, *Spectre* e *Meltdown*.
- **Heterogeneidade:** integrar eficientemente diversos tipos de memória (DRAM, memória persistente, memória de GPUs).
- **Memória Persistente (PMEM):** integra a velocidade da RAM com a persistência do armazenamento secundário — retém dados de forma não volátil mesmo sem energia elétrica, com aplicações em bancos de dados de alta performance.
- **IA no gerenciamento:** uso de inteligência artificial para prever padrões de acesso e aprimorar a alocação e substituição de páginas.

---

## Exercícios de fixação

1. Por que a memória cache, apesar de mais rápida, tem tamanho muito menor que a memória principal?
2. Diferencie fragmentação interna de fragmentação externa, com um exemplo de cada.
3. Compare as estratégias de posicionamento first-fit, best-fit e worst-fit — cite uma vantagem e uma desvantagem de cada.
4. Explique a diferença entre paginação e segmentação quanto ao critério de divisão da memória lógica.
5. O que é uma falha de página (*page fault*) e o que o sistema operacional faz quando ela ocorre?
6. Por que a memória virtual permite executar programas maiores que a memória física disponível?
7. Cite dois mecanismos de proteção de memória implementados via hardware.

<details>
<summary>Gabarito</summary>

1. Porque a memória cache é significativamente mais cara por *byte* que a memória principal; seu alto custo de fabricação justifica um tamanho reduzido, reservado apenas para os dados mais acessados.
2. Fragmentação interna ocorre quando o bloco alocado é maior que o necessário, desperdiçando espaço dentro do próprio bloco (ex.: um processo de 18 KB alocado em um bloco fixo de 20 KB desperdiça 2 KB). Fragmentação externa ocorre quando há memória livre suficiente no total, mas dispersa em blocos pequenos e não contíguos, impedindo a alocação de um processo maior mesmo que a soma dos fragmentos seja suficiente.
3. First-fit: rápida e simples, mas tende a gerar fragmentação externa ao longo do tempo. Best-fit: minimiza o desperdício dentro do bloco alocado, mas costuma gerar muitos fragmentos pequenos inúteis, aumentando o tempo de busca. Worst-fit: tenta deixar fragmentos remanescentes grandes e reutilizáveis, mas consome rapidamente os blocos maiores, dificultando alocações grandes futuras.
4. A paginação divide a memória em blocos de tamanho fixo (páginas/frames), sem relação com a estrutura lógica do programa. A segmentação divide o programa em unidades de tamanho variável baseadas em sua estrutura lógica (código, dados, pilha), o que facilita proteção e compartilhamento por função.
5. É a situação em que uma página requisitada pelo processador não está presente na memória principal. O sistema operacional interrompe a execução, localiza a página no armazenamento secundário, carrega-a em um frame livre (ou substitui outra página via estratégia de substituição) e retoma a execução do processo.
6. Porque, através de paginação/segmentação sob demanda, apenas as partes do programa efetivamente necessárias em cada instante são mantidas na memória principal; o restante permanece no disco, que atua como extensão da memória, criando a ilusão de um espaço de endereçamento maior que o fisicamente disponível.
7. Registradores de limite e base, que delimitam o intervalo de endereços legal de cada processo; e bits de proteção nas tabelas de páginas/segmentos, que definem permissões de leitura, escrita e execução para cada região de memória — normalmente aplicados pela Unidade de Gerenciamento de Memória (MMU).

</details>

## Material relacionado

- Diagramas desta aula: [Modelo de classes da hierarquia e gerenciador de memória](diagramas/gerenciador-memoria-classes.svg) (diagrama de classes) · [Fluxo de tratamento de falha de página](diagramas/falha-pagina-atividades.svg) (diagrama de atividades)
- [Aula: Blocos de Controle (PCB)](../BLOCOS%20DE%20CONTROLE/detalhes.md)
- [Aula: Monitores e Deadlock](../Monitores%20e%20Deadlock%20em%20Sistemas%20Operacionais/detalhes.md)
- [Resumo, simulado comentado e cheatsheet da disciplina](../../README.md)
- Slide original da aula: [`Organizao e Gerenciamento da Memria Real.pdf`](./Organizao%20e%20Gerenciamento%20da%20Memria%20Real.pdf)
