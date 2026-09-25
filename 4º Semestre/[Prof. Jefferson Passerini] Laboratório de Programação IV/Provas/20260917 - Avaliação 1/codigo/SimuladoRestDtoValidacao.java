/*
 * Disciplina: Laboratório de Programação IV (4º Semestre) - UniFEF
 * Professor: Prof. Jefferson Passerini
 * Tema: Simulado A1 - Exercício 3: API REST, DTOs (Records), Mappers e Códigos HTTP
 * Como executar:
 *   javac SimuladoRestDtoValidacao.java
 *   java SimuladoRestDtoValidacao
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class SimuladoRestDtoValidacao {

    // DTO de entrada imutável usando Java 21 Record
    public record ProdutoRequest(
            String codigoBarras,
            String descricao,
            BigDecimal saldoEstoque,
            BigDecimal valorUnitario,
            BigDecimal estoqueMinimo,
            Long grupoId
    ) {
        // Validação interna simulando o comportamento do Bean Validation (@NotBlank, @PositiveOrZero)
        public List<String> validar() {
            List<String> erros = new ArrayList<>();
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                erros.add("Código de barras é obrigatório");
            }
            if (descricao == null || descricao.trim().isEmpty()) {
                erros.add("Descrição é obrigatória");
            }
            if (saldoEstoque == null || saldoEstoque.compareTo(BigDecimal.ZERO) < 0) {
                erros.add("Saldo de estoque não pode ser nulo ou negativo");
            }
            if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) < 0) {
                erros.add("Valor unitário não pode ser nulo ou negativo");
            }
            if (estoqueMinimo == null || estoqueMinimo.compareTo(BigDecimal.ZERO) < 0) {
                erros.add("Estoque mínimo não pode ser nulo ou negativo");
            }
            if (grupoId == null || grupoId <= 0) {
                erros.add("Identificador do grupo é obrigatório e positivo");
            }
            return erros;
        }
    }

    // DTO de saída imutável protegendo a entidade JPA
    public record ProdutoResponse(
            Long id,
            String codigoBarras,
            String descricao,
            BigDecimal saldoEstoque,
            BigDecimal valorUnitario,
            BigDecimal estoqueMinimo,
            String status,
            Long grupoId
    ) {}

    // Entidade simplificada de modelo
    public static class ProdutoModelo {
        Long id;
        String codigoBarras;
        String descricao;
        BigDecimal saldoEstoque;
        BigDecimal valorUnitario;
        BigDecimal estoqueMinimo;
        String status;
        Long grupoId;
    }

    // Componente Mapeador (Mapper)
    public static class ProdutoMapper {
        public static ProdutoModelo toEntity(ProdutoRequest req) {
            ProdutoModelo p = new ProdutoModelo();
            p.codigoBarras = req.codigoBarras();
            p.descricao = req.descricao();
            p.saldoEstoque = req.saldoEstoque();
            p.valorUnitario = req.valorUnitario();
            p.estoqueMinimo = req.estoqueMinimo();
            p.status = "ATIVO";
            p.grupoId = req.grupoId();
            return p;
        }

        public static ProdutoResponse toResponse(ProdutoModelo modelo) {
            return new ProdutoResponse(
                    modelo.id,
                    modelo.codigoBarras,
                    modelo.descricao,
                    modelo.saldoEstoque,
                    modelo.valorUnitario,
                    modelo.estoqueMinimo,
                    modelo.status,
                    modelo.grupoId
            );
        }
    }

    // Simulação do Controller REST e Resposta HTTP
    public record RespostaHttp<T>(int status, String headerLocation, T corpo, List<String> erros) {}

    public static class ProdutoRestController {
        private final Map<Long, ProdutoModelo> bancoMock = new HashMap<>();
        private long sequence = 100L;

        public RespostaHttp<?> cadastrar(ProdutoRequest request) {
            List<String> erros = request.validar();
            if (!erros.isEmpty()) {
                // 400 Bad Request
                return new RespostaHttp<>(400, null, null, erros);
            }

            // Verifica duplicidade
            boolean duplicado = bancoMock.values().stream()
                    .anyMatch(p -> p.codigoBarras.equals(request.codigoBarras()));
            if (duplicado) {
                // 409 Conflict
                return new RespostaHttp<>(409, null, null, List.of("Código de barras já existente"));
            }

            ProdutoModelo entidade = ProdutoMapper.toEntity(request);
            entidade.id = ++sequence;
            bancoMock.put(entidade.id, entidade);

            ProdutoResponse response = ProdutoMapper.toResponse(entidade);
            String location = "/api/produtos/" + entidade.id;
            // 201 Created com Location
            return new RespostaHttp<>(201, location, response, Collections.emptyList());
        }

        public RespostaHttp<?> buscarPorId(Long id) {
            ProdutoModelo modelo = bancoMock.get(id);
            if (modelo == null) {
                // 404 Not Found
                return new RespostaHttp<>(404, null, null, List.of("Produto não localizado para o ID: " + id));
            }
            return new RespostaHttp<>(200, null, ProdutoMapper.toResponse(modelo), Collections.emptyList());
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("EXECUÇÃO: SIMULADO A1 - EXERCÍCIO 3: REST, DTOS E STATUS");
        System.out.println("==========================================================");

        ProdutoRestController controller = new ProdutoRestController();

        // 1. Requisição válida -> Espera 201 Created
        ProdutoRequest reqValida = new ProdutoRequest(
                "7890001",
                "Cadeira Ergonômica",
                new BigDecimal("15.000"),
                new BigDecimal("850.00"),
                new BigDecimal("3.000"),
                1L
        );
        RespostaHttp<?> res1 = controller.cadastrar(reqValida);
        System.out.println("[POST /api/produtos] Requisição Válida:");
        System.out.println("     Status HTTP: " + res1.status());
        System.out.println("     Header Location: " + res1.headerLocation());
        System.out.println("     Payload Retornado: " + res1.corpo());
        if (res1.status() != 201) throw new AssertionError("Esperava 201 Created");

        // 2. Requisição inválida com campos em branco/negativos -> Espera 400 Bad Request
        ProdutoRequest reqInvalida = new ProdutoRequest(
                "",
                "",
                new BigDecimal("-1"),
                new BigDecimal("-10"),
                null,
                null
        );
        RespostaHttp<?> res2 = controller.cadastrar(reqInvalida);
        System.out.println("\n[POST /api/produtos] Requisição Inválida:");
        System.out.println("     Status HTTP: " + res2.status());
        System.out.println("     Erros de Validação: " + res2.erros());
        if (res2.status() != 400) throw new AssertionError("Esperava 400 Bad Request");

        // 3. Conflito por código repetido -> Espera 409 Conflict
        RespostaHttp<?> res3 = controller.cadastrar(reqValida);
        System.out.println("\n[POST /api/produtos] Tentativa Duplicada:");
        System.out.println("     Status HTTP: " + res3.status());
        System.out.println("     Erros: " + res3.erros());
        if (res3.status() != 409) throw new AssertionError("Esperava 409 Conflict");

        // 4. Busca por ID existente -> Espera 200 OK
        Long idGerado = ((ProdutoResponse) res1.corpo()).id();
        RespostaHttp<?> res4 = controller.buscarPorId(idGerado);
        System.out.println("\n[GET /api/produtos/" + idGerado + "] Consulta por ID:");
        System.out.println("     Status HTTP: " + res4.status());
        System.out.println("     Payload: " + res4.corpo());
        if (res4.status() != 200) throw new AssertionError("Esperava 200 OK");

        // 5. Busca por ID inexistente -> Espera 404 Not Found
        RespostaHttp<?> res5 = controller.buscarPorId(9999L);
        System.out.println("\n[GET /api/produtos/9999] Consulta Inexistente:");
        System.out.println("     Status HTTP: " + res5.status());
        System.out.println("     Erros: " + res5.erros());
        if (res5.status() != 404) throw new AssertionError("Esperava 404 Not Found");

        System.out.println("\nTODOS OS TESTES DE REST E DTO FORAM CONCLUÍDOS COM SUCESSO!");
    }
}
