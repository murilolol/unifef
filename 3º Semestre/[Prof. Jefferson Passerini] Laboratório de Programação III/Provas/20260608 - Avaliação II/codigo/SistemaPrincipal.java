// Disciplina: Laboratório de Programação III (3º Semestre)
// Professor: Prof. Jefferson Passerini
// Tema: Avaliação II - Sistema Principal e Menu Interativo
// Como executar: javac *.java && java SistemaPrincipal

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class SistemaPrincipal {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProdutoDAO produtoDAO = new ProdutoDAO();

    public static void main(String[] args) {
        int opcao = -1;
        System.out.println("==========================================================");
        System.out.println("   UNIFEF - SISTEMAS DE INFORMAÇÃO - 3º SEMESTRE");
        System.out.println("   LABORATÓRIO DE PROGRAMAÇÃO III - AVALIAÇÃO II");
        System.out.println("   Prof. Jefferson Passerini");
        System.out.println("==========================================================");

        while (opcao != 0) {
            exibirMenu();
            try {
                System.out.print("Escolha uma opção: ");
                String entrada = scanner.nextLine();
                opcao = Integer.parseInt(entrada.trim());

                switch (opcao) {
                    case 1:
                        cadastrarProduto();
                        break;
                    case 2:
                        listarProdutos();
                        break;
                    case 3:
                        consultarPorId();
                        break;
                    case 4:
                        atualizarProduto();
                        break;
                    case 5:
                        excluirProduto();
                        break;
                    case 0:
                        System.out.println("Encerrando o sistema. Sucesso na Avaliação II!");
                        break;
                    default:
                        System.out.println("Opção inválida! Escolha entre 0 e 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Digite apenas números.");
            } catch (NegocioException e) {
                System.out.println("Aviso de Negócio: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Erro de Banco de Dados (JDBC): " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("----------------------------------------------------------");
        System.out.println(" 1 - Cadastrar Produto");
        System.out.println(" 2 - Listar Todos os Produtos");
        System.out.println(" 3 - Consultar Produto por ID");
        System.out.println(" 4 - Atualizar Dados de Produto");
        System.out.println(" 5 - Excluir Produto");
        System.out.println(" 0 - Sair");
        System.out.println("----------------------------------------------------------");
    }

    // Exercício 3: Cadastrar com validação
    private static void cadastrarProduto() throws NegocioException, SQLException {
        System.out.println(">> CADASTRO DE PRODUTO");
        System.out.print("Nome do produto: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) {
            throw new NegocioException("O nome do produto é obrigatório.");
        }

        System.out.print("Preço unitário (ex: 150.50): ");
        double preco;
        try {
            preco = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            if (preco < 0) {
                throw new NegocioException("O preço não pode ser negativo.");
            }
        } catch (NumberFormatException e) {
            throw new NegocioException("Valor de preço inválido.");
        }

        System.out.print("Quantidade inicial em estoque: ");
        int estoque;
        try {
            estoque = Integer.parseInt(scanner.nextLine().trim());
            if (estoque < 0) {
                throw new NegocioException("A quantidade em estoque não pode ser negativa.");
            }
        } catch (NumberFormatException e) {
            throw new NegocioException("Valor de estoque inválido.");
        }

        String dataHoje = LocalDate.now().toString();
        Produto novoProduto = new Produto(nome, preco, estoque, dataHoje);
        produtoDAO.inserir(novoProduto);
        System.out.println("Produto cadastrado com sucesso! ID gerado: " + novoProduto.getId());
    }

    // Exercício 3: Listar todos os registros
    private static void listarProdutos() throws SQLException {
        System.out.println(">> LISTAGEM GERAL DE PRODUTOS");
        List<Produto> lista = produtoDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum produto cadastrado no momento.");
            return;
        }
        for (Produto p : lista) {
            System.out.println(p);
        }
        System.out.println("Total de itens listados: " + lista.size());
    }

    // Exercício 3: Consulta individual por ID
    private static void consultarPorId() throws NegocioException, SQLException {
        System.out.println(">> CONSULTA POR CÓDIGO (ID)");
        System.out.print("Informe o ID do produto: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Produto p = produtoDAO.buscarPorId(id);
            if (p == null) {
                System.out.println("Produto não encontrado para o ID: " + id);
            } else {
                System.out.println("Registro localizado:");
                System.out.println(p);
            }
        } catch (NumberFormatException e) {
            throw new NegocioException("ID deve ser um número inteiro válido.");
        }
    }

    // Exercício 3: Atualização de registro
    private static void atualizarProduto() throws NegocioException, SQLException {
        System.out.println(">> ATUALIZAÇÃO DE PRODUTO");
        System.out.print("Informe o ID do produto que deseja atualizar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new NegocioException("ID inválido.");
        }

        Produto existente = produtoDAO.buscarPorId(id);
        if (existente == null) {
            System.out.println("Produto não encontrado para alteração.");
            return;
        }

        System.out.println("Dados atuais: " + existente);
        System.out.print("Novo nome (pressione ENTER para manter): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.trim().isEmpty()) {
            existente.setNome(novoNome);
        }

        System.out.print("Novo preço (pressione ENTER para manter): ");
        String precoStr = scanner.nextLine();
        if (!precoStr.trim().isEmpty()) {
            try {
                double novoPreco = Double.parseDouble(precoStr.trim().replace(",", "."));
                if (novoPreco < 0) throw new NegocioException("Preço não pode ser negativo.");
                existente.setPreco(novoPreco);
            } catch (NumberFormatException e) {
                throw new NegocioException("Preço digitado é inválido.");
            }
        }

        System.out.print("Nova quantidade (pressione ENTER para manter): ");
        String estoqueStr = scanner.nextLine();
        if (!estoqueStr.trim().isEmpty()) {
            try {
                int novoEstoque = Integer.parseInt(estoqueStr.trim());
                if (novoEstoque < 0) throw new NegocioException("Estoque não pode ser negativo.");
                existente.setQuantidadeEstoque(novoEstoque);
            } catch (NumberFormatException e) {
                throw new NegocioException("Quantidade digitada é inválida.");
            }
        }

        boolean atualizado = produtoDAO.atualizar(existente);
        if (atualizado) {
            System.out.println("Produto ID " + id + " atualizado com sucesso!");
        } else {
            System.out.println("Não foi possível concluir a atualização.");
        }
    }

    // Exercício 3: Exclusão com confirmação
    private static void excluirProduto() throws NegocioException, SQLException {
        System.out.println(">> EXCLUSÃO DE PRODUTO");
        System.out.print("Informe o ID do produto a remover: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new NegocioException("ID inválido.");
        }

        Produto existente = produtoDAO.buscarPorId(id);
        if (existente == null) {
            System.out.println("Produto não localizado.");
            return;
        }

        System.out.println("Produto selecionado: " + existente.getNome());
        System.out.print("Tem certeza que deseja excluir? (S/N): ");
        String confirmacao = scanner.nextLine().trim();
        if (confirmacao.equalsIgnoreCase("S")) {
            boolean removido = produtoDAO.excluir(id);
            if (removido) {
                System.out.println("Produto excluído com sucesso do banco de dados!");
            } else {
                System.out.println("Não foi possível excluir o produto.");
            }
        } else {
            System.out.println("Operação de exclusão cancelada pelo usuário.");
        }
    }
}
