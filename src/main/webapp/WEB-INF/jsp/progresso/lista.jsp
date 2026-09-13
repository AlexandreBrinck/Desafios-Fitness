<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Progresso - Desafios Fitness</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
<header class="topbar">
    <div class="container">
        <strong>Desafios Fitness</strong>
        <nav>
            <a href="${pageContext.request.contextPath}/home">Home</a>
            <a href="${pageContext.request.contextPath}/usuarios">Usuários</a>
            <a href="${pageContext.request.contextPath}/desafios">Desafios</a>
            <c:if test="${not empty sessionScope.usuarioLogado}">
                <a href="${pageContext.request.contextPath}/desafios?acao=meus">Meus Desafios</a>
                <a href="${pageContext.request.contextPath}/progresso">Progresso</a>
            </c:if>
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogado}">
                    <span>Olá, ${sessionScope.usuarioLogado.nome}</span>
                    <a href="${pageContext.request.contextPath}/logout">Sair</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Entrar</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<main class="container">
    <div class="page-header">
        <h1>
            <c:choose>
                <c:when test="${porDesafio}">Progresso do desafio: ${desafio.nome}</c:when>
                <c:otherwise>Meu progresso</c:otherwise>
            </c:choose>
        </h1>
        <c:if test="${porDesafio}">
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/desafios?acao=visualizar&id=${desafio.id}">Voltar ao desafio</a>
        </c:if>
    </div>

    <c:if test="${not empty erro}">
        <div class="alert alert-erro">${erro}</div>
    </c:if>

    <div class="table-wrap">
        <c:choose>
            <c:when test="${empty registros}">
                <p class="empty">Nenhum registro de progresso ainda.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>Data</th>
                        <c:if test="${not porDesafio}">
                            <th>Desafio</th>
                        </c:if>
                        <th>Valor atingido</th>
                        <th>Observação</th>
                        <c:if test="${not porDesafio}">
                            <th>Ações</th>
                        </c:if>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="registro" items="${registros}">
                        <tr>
                            <td>${registro.dataRegistro}</td>
                            <c:if test="${not porDesafio}">
                                <td>${desafios[registro.desafioId].nome}</td>
                            </c:if>
                            <td>${registro.valorAtingido}</td>
                            <td>${registro.observacao}</td>
                            <c:if test="${not porDesafio}">
                                <td>
                                    <span class="links-inline">
                                        <a href="${pageContext.request.contextPath}/progresso?acao=editar&id=${registro.id}">Editar</a>
                                        <a href="${pageContext.request.contextPath}/progresso?acao=excluir&id=${registro.id}"
                                           onclick="return confirm('Excluir este registro de progresso?');">Excluir</a>
                                    </span>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</main>
</body>
</html>
