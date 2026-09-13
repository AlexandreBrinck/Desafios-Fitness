<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
        <c:choose>
            <c:when test="${empty progresso.id}">Registrar progresso</c:when>
            <c:otherwise>Editar progresso</c:otherwise>
        </c:choose>
        - Desafios Fitness
    </title>
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
                <c:when test="${empty progresso.id}">Registrar progresso</c:when>
                <c:otherwise>Editar progresso</c:otherwise>
            </c:choose>
        </h1>
    </div>
    <p class="subtitulo">Desafio: ${desafio.nome}</p>

    <div class="card">
        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/progresso">
            <input type="hidden" name="id" value="${progresso.id}">
            <input type="hidden" name="desafioId" value="${desafio.id}">

            <div class="form-group">
                <label for="dataRegistro">Data</label>
                <input type="date" id="dataRegistro" name="dataRegistro" value="${progresso.dataRegistro}" required>
            </div>

            <div class="form-group">
                <label for="valorAtingido">Valor atingido</label>
                <input type="text" id="valorAtingido" name="valorAtingido" value="${progresso.valorAtingido}"
                       placeholder="Ex.: 5 km, 30 minutos" required>
            </div>

            <div class="form-group">
                <label for="observacao">Observação</label>
                <textarea id="observacao" name="observacao" rows="3">${progresso.observacao}</textarea>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Salvar</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/progresso">Cancelar</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>
