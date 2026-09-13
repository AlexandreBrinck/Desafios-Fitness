<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Desafios Fitness</title>
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
        <h1>Painel</h1>
    </div>
    <p class="subtitulo">
        Aplicativo para criar desafios de exercícios, participar deles e acompanhar o progresso.
    </p>

    <div class="grid-cards">
        <a class="menu-card" href="${pageContext.request.contextPath}/usuarios">
            <strong>Usuários</strong>
            <span>Cadastrar e listar os usuários do sistema.</span>
        </a>
        <a class="menu-card" href="${pageContext.request.contextPath}/desafios">
            <strong>Desafios</strong>
            <span>Criar, listar, visualizar e participar de desafios de exercícios.</span>
        </a>
        <a class="menu-card" href="${pageContext.request.contextPath}/desafios?acao=meus">
            <strong>Meus Desafios</strong>
            <span>Desafios dos quais você participa.</span>
        </a>
        <a class="menu-card" href="${pageContext.request.contextPath}/progresso">
            <strong>Progresso</strong>
            <span>Registrar e acompanhar seu progresso nos desafios.</span>
        </a>
    </div>
</main>
</body>
</html>
