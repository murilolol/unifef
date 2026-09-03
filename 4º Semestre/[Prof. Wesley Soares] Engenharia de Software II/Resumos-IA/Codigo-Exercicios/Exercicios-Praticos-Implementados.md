# 📘 Apostila Prática: Engenharia de Software e Modelagem II
**Professor:** Ms. Wesley Soares de Souza  
**Instituição / Disciplina:** Engenharia de Software II  

---

## 🚀 Apresentação
Esta apostila prática é baseada no material didático do **Prof. Ms. Wesley Soares de Souza**. O objetivo é unir os fundamentos teóricos de **Elicitação de Requisitos, Ciclo de Vida de Software, Princípios de Projeto Orientado a Objetos (POO) e Priorização (MoSCoW)** com códigos executáveis em **Java** e **TypeScript**, além de exemplos de modelagem aplicados.

---

## 🛠️ Módulo 1: Engenharia de Requisitos e Transformação em Código

### 📌 1.1 Do Requisito ao Código: Requisitos Funcionais e Não Funcionais
Conforme apresentado nas aulas do Prof. Wesley, a elicitação transforma necessidades vagas de *stakeholders* em requisitos claros:
* **Requisito Funcional:** O que o sistema deve fazer (ex: Registrar pedidos e notificar o vendedor).
* **Requisito Não Funcional:** Como o sistema deve se comportar (ex: A notificação deve ocorrer em até 5 segundos).

#### 💻 Exemplo Prático (TypeScript): Implementação de um Pedido com Validação de Requisito Não Funcional (Performance/Tempo limite)

```typescript
// ==========================================
// ARQUIVO: sistema_pedidos.ts
// Descrição: Atende ao requisito funcional de registrar pedido
// e não funcional de notificar em até 5 segundos.
// ==========================================

interface Pedido {
    id: number;
    cliente: string;
    valorTotal: number;
}

class ServicoNotificacao {
    public notificarVendedor(pedido: Pedido): void {
        const inicio = Date.now();

        // Simula o envio da notificação ao vendedor
        console.log(`[NOTIFICAÇÃO] Novo pedido #${pedido.id} registrado para o cliente ${pedido.cliente}.`);

        const fim = Date.now();
        const tempoRespostaMs = fim - inicio;

        // Validação do Requisito Não Funcional: Resposta < 5000ms (5 segundos)
        const limiteMaximoMs = 5000;
        if (tempoRespostaMs <= limiteMaximoMs) {
            console.log(`[RNF OK] Notificação enviada em ${tempoRespostaMs}ms (Dentro do limite de 5s).\n`);
        } else {
            console.log(`[ALERTA RNF] Lentidão detectada: ${tempoRespostaMs}ms.\n`);
        }
    }
}

class ProcessadorDePedidos {
    private notificador: ServicoNotificacao;

    constructor() {
        this.notificador = new ServicoNotificacao();
    }

    public registrarPedido(id: number, cliente: string, valorTotal: number): void {
        const novoPedido: Pedido = { id, cliente, valorTotal };
        console.log(`[PROCESSO] Pedido #${id} gravado com sucesso no banco de dados.`);
        
        // Aciona o requisito funcional dependente
        this.notificador.notificarVendedor(novoPedido);
    }
}

// --- Execução do Código ---
const sistema = new ProcessadorDePedidos();
sistema.registrarPedido(101, "Empresa Construtora Alfa Ltda", 15400.00);
sistema.registrarPedido(102, "Supermercado Compre Bem", 3200.50);
```

---

## 🏛️ Módulo 2: Princípios de Projeto Orientado a Objetos (POO)

O projeto OO traduz a análise (o que fazer) para a solução técnica (como fazer), aplicando pilares fundamentais como **Encapsulamento**, **Herança**, **Polimorfismo**, **Baixo Acoplamento** e **Alta Coesão**.

### 💻 Exemplo Prático (Java): Aplicando Encapsulamento, Herança e Polimorfismo

Conforme abordado nos slides do Prof. Wesley (cálculo de bônus diferenciado para diferentes cargos):

```java
// ==========================================
// ARQUIVO: SistemaBonus.java
// Descrição: Demonstra Herança, Encapsulamento e Polimorfismo.
// ==========================================

// 1. Classe Base com Encapsulamento (Atributos privados e Getters/Setters)
abstract class Colaborador {
    private String nome;
    private double salarioBase;

    public Colaborador(String nome, double salarioBase) {
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public String getNome() {
        return nome;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    // Método abstrato que força o polimorfismo nas subclasses
    public abstract double calcularBonus();
}

// 2. Subclasse Gerente (Herança)
class Gerente extends Colaborador {
    public Gerente(String nome, double salarioBase) {
        super(nome, salarioBase);
    }

    @Override
    public double calcularBonus() {
        // Regra de negócio do Gerente: 20% do salário base
        return getSalarioBase() * 0.20;
    }
}

// 3. Subclasse Vendedor (Herança)
class Vendedor extends Colaborador {
    private double comissaoVendas;

    public Vendedor(String nome, double salarioBase, double comissaoVendas) {
        super(nome, salarioBase);
        this.comissaoVendas = comissaoVendas;
    }

    @Override
    public double calcularBonus() {
        // Regra de negócio do Vendedor: 10% do salário + comissão
        return (getSalarioBase() * 0.10) + (this.comissaoVendas * 0.05);
    }
}

// 4. Classe Executável principal
public class SistemaBonus {
    public static void main(String[] args) {
        Colaborador g = new Gerente("Ana Souza", 8000.00);
        Colaborador v = new Vendedor("Carlos Silva", 3000.00, 20000.00);

        exibirInformacoes(g);
        exibirInformacoes(v);
    }

    // Polimorfismo em ação: o método aceita qualquer 'Colaborador'
    public static void exibirInformacoes(Colaborador c) {
        System.out.println("Colaborador: " + c.getNome());
        System.out.println("Salário Base: R$ " + c.getSalarioBase());
        System.out.println("Bônus Calculado: R$ " + c.calcularBonus());
        System.out.println("----------------------------------------");
    }
}
```

---

## 🎯 Módulo 3: Priorização de Requisitos (Método MoSCoW)

O método **MoSCoW** é amplamente utilizado na Engenharia de Requisitos para separar o que é essencial do que é secundário em um projeto de software:
* **M** — *Must have* (Deve ter - Obrigatório para o sistema funcionar)
* **S** — *Should have* (Deveria ter - Importante, mas não crítico para o lançamento)
* **C** — *Could have* (Poderia ter - Desejável se houver tempo/recursos)
* **W** — *Won't have* (Não terá agora - Ficará para versões futuras)

#### 💻 Exemplo Prático (TypeScript): Validação de Escopo Baseada em MoSCoW

```typescript
// ==========================================
// ARQUIVO: priorizacao_moscow.ts
// Descrição: Simula um avaliador de escopo baseado no método MoSCoW.
// ==========================================

enum CategoriaMoSCoW {
    MUST_HAVE = "Must have",
    SHOULD_HAVE = "Should have",
    COULD_HAVE = "Could have",
    WONT_HAVE = "Won't have"
}

interface RequisitoProjeto {
    id: string;
    descricao: string;
    classificacao: CategoriaMoSCoW;
}

class GerenciadorEscopo {
    private requisitos: RequisitoProjeto[] = [];

    public adicionarRequisito(id: string, descricao: string, classificacao: CategoriaMoSCoW): void {
        this.requisitos.push({ id, descricao, classificacao });
    }

    public filtrarEscopoAtual(): void {
        console.log("=== ESCOPO APROVADO PARA O MVP (Minimum Viable Product) ===");
        // Apenas 'Must have' entram obrigatoriamente no MVP imediato
        this.requisitos
            .filter(r => r.classificacao === CategoriaMoSCoW.MUST_HAVE)
            .forEach(r => console.log(`[${r.id}] ${r.descricao} (${r.classificacao})`));

        console.log("\n=== PLANEJADO PARA A PRÓXIMA FASE (Should / Could) ===");
        this.requisitos
            .filter(r => r.classificacao === CategoriaMoSCoW.SHOULD_HAVE || r.classificacao === CategoriaMoSCoW.COULD_HAVE)
            .forEach(r => console.log(`[${r.id}] ${r.descricao} (${r.classificacao})`));
    }
}

// --- Execução do Código ---
const projeto = new GerenciadorEscopo();

projeto.adicionarRequisito("RF01", "Cadastro de Clientes e Fornecedores", CategoriaMoSCoW.MUST_HAVE);
projeto.adicionarRequisito("RF02", "Emissão de Nota Fiscal Eletrônica", CategoriaMoSCoW.MUST_HAVE);
projeto.adicionarRequisito("RF03", "Relatório avançado de BI em formato PDF", CategoriaMoSCoW.SHOULD_HAVE);
projeto.adicionarRequisito("RF04", "Tema escuro (Dark Mode) na interface", CategoriaMoSCoW.COULD_HAVE);
projeto.adicionarRequisito("RF05", "Integração com Realidade Aumentada", CategoriaMoSCoW.WONT_HAVE);

projeto.filtrarEscopoAtual();
```

---

## 📝 Exercícios Práticos Resolvidos

### Exercício 1 (Análise e Conflito de Requisitos)
**Enunciado:** Durante uma entrevista de elicitação de requisitos para um sistema de e-commerce, o Gerente Comercial afirmou: *"O sistema deve conceder desconto automático de 20% para qualquer compra acima de R$ 500"*. No entanto, o Gerente Financeiro interveio dizendo: *"Não podemos dar 20% em produtos da categoria Eletrônicos, pois a margem de lucro é inferior a 10%"*.  
Como o analista de software deve resolver esse conflito documentando o Requisito Funcional final?

**💡 Resolução:**
O analista identificou um **conflito de stakeholders**. A solução exige o refinamento da regra de negócio junto aos envolvidos. O requisito funcional documentado deve ser condicional:
> **RF12 - Desconto Comercial:** O sistema deve aplicar um desconto de 20% em compras acima de R$ 500, **exceto** para produtos pertencentes à categoria "Eletrônicos", cuja aplicação de desconto máximo permitida deve seguir a regra de margem específica do departamento.

---

### Exercício 2 (Diagrama de Classes conceitual baseado no material de POO)
**Enunciado:** Com base nos princípios de projeto orientado a objetos da Aula 02 do Prof. Wesley, desenhe mentalmente e descreva o modelo conceitual (classes, atributos, métodos e relacionamentos) para um sistema de **Gestão de Clínicas** (opção do Projeto Integrador) focando nas classes `Paciente`, `Consulta` e `Medico`.

**💡 Resolução:**
* **Classe `Paciente`**
  * *Atributos:* `private String cpf`, `private String nome`, `private String telefone`
  * *Métodos:* `getters/setters`, `atualizarDados()`
* **Classe `Medico`**
  * *Atributos:* `private String crm`, `private String nome`, `private String especialidade`
  * *Métodos:* `getters/setters`
* **Classe `Consulta`**
  * *Atributos:* `private Date dataHora`, `private String status`, `private Paciente paciente`, `private Medico medico`
  * *Métodos:* `agendar()`, `cancelar()`, `realizarAtendimento()`
* **Relacionamentos:**
  * Um `Paciente` realiza *muitas* `Consultas` (1 para N).
  * Um `Medico` atende *muitas* `Consultas` (1 para N).
  * Uma `Consulta` possui obrigatoriamente *um* `Paciente` e *um* `Medico`.