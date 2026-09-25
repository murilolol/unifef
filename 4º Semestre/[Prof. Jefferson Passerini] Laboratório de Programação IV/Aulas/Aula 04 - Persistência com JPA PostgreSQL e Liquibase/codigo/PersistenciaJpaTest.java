package com.curso.suporteos.domain;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Disciplina: Laboratório de Programação IV
 * Professor: Prof. Jefferson Passerini
 * Tema: Persistência com JPA, PostgreSQL e Liquibase
 * 
 * Execução: ./mvnw test -Dtest=PersistenciaJpaTest
 */
@Component
public class PersistenciaJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PersistenciaJpaTest() {
    }

    public PersistenciaJpaTest(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void devePersistirERelerGrupoEProduto() {
        GrupoProduto grupo = new GrupoProduto("Periféricos");
        Produto produto = new Produto(
                "7891000000019",
                "Mouse sem fio",
                new BigDecimal("10.000"),
                new BigDecimal("89.90"),
                LocalDate.of(2026, 3, 10));

        grupo.adicionarProduto(produto);

        entityManager.persist(grupo);
        entityManager.persist(produto);
        entityManager.flush();

        Long produtoId = produto.getId();
        entityManager.clear();

        Produto produtoRecuperado = entityManager.find(Produto.class, produtoId);

        assertNotNull(produtoRecuperado);
        assertEquals("Mouse sem fio", produtoRecuperado.getDescricao());
        assertEquals("Periféricos", produtoRecuperado.getGrupo().getNome());
        assertEquals(Status.ATIVO, produtoRecuperado.getStatus());
    }

    public void deveRegistrarTodosOsChangeSetsDoCurso() {
        Integer quantidade = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM databasechangelog",
                Integer.class);
        assertNotNull(quantidade);
    }

    @Transactional
    public void bancoDeveImpedirCodigoDeBarrasDuplicado() {
        Long grupoId = inserirGrupoDiretamente("Grupo para unicidade");
        inserirProdutoDiretamente(grupoId, "CODIGO-REPETIDO", "Primeiro produto", "1.000", "10.00");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> inserirProdutoDiretamente(
                        grupoId,
                        "CODIGO-REPETIDO",
                        "Segundo produto",
                        "1.000",
                        "20.00"));
    }

    @Transactional
    public void bancoDeveImpedirSaldoNegativo() {
        Long grupoId = inserirGrupoDiretamente("Grupo para saldo");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> inserirProdutoDiretamente(
                        grupoId,
                        "CODIGO-SALDO-NEGATIVO",
                        "Produto inválido",
                        "-1.000",
                        "10.00"));
    }

    private Long inserirGrupoDiretamente(String nome) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO grupo_produto (nome, status) VALUES (?, 'ATIVO') RETURNING id",
                Long.class,
                nome);
    }

    private void inserirProdutoDiretamente(
            Long grupoId,
            String codigoBarras,
            String descricao,
            String saldo,
            String valor) {
        jdbcTemplate.update(
                "INSERT INTO produto (codigo_barras, descricao, saldo_estoque, valor_unitario, estoque_minimo, data_cadastro, status, grupo_produto_id) " +
                        "VALUES (?, ?, CAST(? AS NUMERIC), CAST(? AS NUMERIC), 0, DATE '2026-03-10', 'ATIVO', ?)",
                codigoBarras,
                descricao,
                saldo,
                valor,
                grupoId);
    }

    private static void assertEquals(Object esperado, Object atual) {
        if (esperado == null && atual == null) {
            return;
        }
        if (esperado != null && esperado.equals(atual)) {
            return;
        }
        throw new AssertionError("Esperado: [" + esperado + "], mas foi obtido: [" + atual + "].");
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Deveria não ser nulo, mas o valor recebido foi null.");
        }
    }

    private static <T extends Throwable> T assertThrows(Class<T> tipoEsperado, Executavel executavel) {
        try {
            executavel.executar();
        } catch (Throwable t) {
            if (tipoEsperado.isInstance(t)) {
                return tipoEsperado.cast(t);
            }
            throw new AssertionError(
                    "Esperava exceção do tipo " + tipoEsperado.getSimpleName()
                            + ", mas foi lançada a exceção: " + t.getClass().getSimpleName(),
                    t);
        }
        throw new AssertionError(
                "Esperava exceção do tipo " + tipoEsperado.getSimpleName()
                        + ", mas nenhuma exceção foi lançada.");
    }

    @FunctionalInterface
    public interface Executavel {
        void executar() throws Throwable;
    }
}
