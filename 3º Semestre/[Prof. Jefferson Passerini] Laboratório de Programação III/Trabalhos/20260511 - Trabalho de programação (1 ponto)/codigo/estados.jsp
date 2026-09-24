<%-- 
    Disciplina: Laboratório de Programação III (3º Semestre) - UniFEF
    Professor:  Prof. Jefferson Passerini
    Tema:       Java JSP Cap 5.4 - Desafio 02: Cadastro de Estado
    Exercício:  Exercício 1
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro de Estados - UniFEF LP3</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container my-5">
    <header class="pb-3 mb-4 border-bottom">
        <h1 class="h3 text-primary">Laboratório de Programação III - UniFEF</h1>
        <p class="text-muted mb-0">Capítulo 5.4: Desafio 02 - Módulo de Cadastro de Estado (JSP + Servlet + DAO)</p>
    </header>

    <%-- Notificações de Status --%>
    <c:if test="${param.status == 'operacao_sucesso'}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            Registro gravado com sucesso no banco de dados!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
        </div>
    </c:if>
    <c:if test="${param.status == 'exclusao_sucesso'}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            Estado excluído com sucesso!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
        </div>
    </c:if>
    <c:if test="${param.status == 'operacao_erro' or param.status == 'exclusao_erro'}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            Ocorreu um erro ao processar a operação no banco de dados.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
        </div>
    </c:if>

    <div class="row g-4">
        <%-- Coluna do Formulário --%>
        <div class="col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-primary text-white fw-bold">
                    <c:choose>
                        <c:when test="${not empty estadoEdicao}">Editar Estado #${estadoEdicao.idEstado}</c:when>
                        <c:otherwise>Novo Estado</c:otherwise>
                    </c:choose>
                </div>
                <div class="card-body">
                    <form action="EstadoServlet" method="POST">
                        <input type="hidden" name="acao" value="salvar">
                        <input type="hidden" name="idEstado" value="${estadoEdicao.idEstado}">

                        <div class="mb-3">
                            <label for="nomeEstado" class="form-label fw-semibold">Nome do Estado</label>
                            <input type="text" class="form-control" id="nomeEstado" name="nomeEstado" 
                                   value="${estadoEdicao.nomeEstado}" required maxlength="50" placeholder="Ex: São Paulo">
                        </div>

                        <div class="mb-3">
                            <label for="siglaEstado" class="form-label fw-semibold">Sigla</label>
                            <input type="text" class="form-control text-uppercase" id="siglaEstado" name="siglaEstado" 
                                   value="${estadoEdicao.siglaEstado}" required maxlength="2" placeholder="Ex: SP">
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary flex-grow-1">Salvar</button>
                            <c:if test="${not empty estadoEdicao}">
                                <a href="EstadoServlet?acao=novo" class="btn btn-outline-secondary">Cancelar</a>
                            </c:if>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%-- Coluna da Tabela de Listagem --%>
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                    <h2 class="h5 mb-0 fw-bold text-dark">Estados Cadastrados</h2>
                    <a href="EstadoServlet?acao=listar" class="btn btn-sm btn-outline-primary">Atualizar Lista</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th scope="col" style="width: 80px;">ID</th>
                                    <th scope="col">Nome</th>
                                    <th scope="col" style="width: 100px;">Sigla</th>
                                    <th scope="col" class="text-center" style="width: 160px;">Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty estados}">
                                        <c:forEach var="est" items="${estados}">
                                            <tr>
                                                <td class="fw-bold text-secondary">${est.idEstado}</td>
                                                <td>${est.nomeEstado}</td>
                                                <td><span class="badge bg-secondary">${est.siglaEstado}</span></td>
                                                <td class="text-center">
                                                    <a href="EstadoServlet?acao=editar&idEstado=${est.idEstado}" 
                                                       class="btn btn-sm btn-outline-warning">Editar</a>
                                                    <a href="EstadoServlet?acao=excluir&idEstado=${est.idEstado}" 
                                                       class="btn btn-sm btn-outline-danger" 
                                                       onclick="return confirm('Deseja realmente excluir o estado ${est.nomeEstado}?');">Excluir</a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="4" class="text-center text-muted py-4">
                                                Nenhum estado cadastrado até o momento.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
