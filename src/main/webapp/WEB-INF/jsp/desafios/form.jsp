<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
        <c:choose>
            <c:when test="${empty desafio.id}">Novo desafio</c:when>
            <c:otherwise>Editar desafio</c:otherwise>
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
                <c:when test="${empty desafio.id}">Novo desafio</c:when>
                <c:otherwise>Editar desafio</c:otherwise>
            </c:choose>
        </h1>
    </div>

    <div class="card">
        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/desafios">
            <input type="hidden" name="id" value="${desafio.id}">

            <div class="form-group">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" value="${desafio.nome}" required>
            </div>

            <div class="form-group">
                <label for="descricao">Descrição</label>
                <textarea id="descricao" name="descricao" rows="3">${desafio.descricao}</textarea>
            </div>

            <div class="form-group">
                <label for="tipoExercicio">Tipo de exercício</label>
                <input type="text" id="tipoExercicio" name="tipoExercicio" value="${desafio.tipoExercicio}"
                       placeholder="Ex.: Corrida, Musculação, Natação" required>
            </div>

            <div class="form-group">
                <label for="meta">Meta</label>
                <input type="text" id="meta" name="meta" value="${desafio.meta}" placeholder="Ex.: 50 km no mês">
            </div>

            <div class="form-group">
                <label for="dataInicio">Data de início</label>
                <input type="date" id="dataInicio" name="dataInicio" value="${desafio.dataInicio}" required>
            </div>

            <div class="form-group">
                <label for="dataFim">Data de fim</label>
                <input type="date" id="dataFim" name="dataFim" value="${desafio.dataFim}">
            </div>

            <div class="actions">
                <button type="submit" class="btn">Salvar</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/desafios">Cancelar</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>
