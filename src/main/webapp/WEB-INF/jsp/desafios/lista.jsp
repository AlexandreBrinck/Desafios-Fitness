<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Desafios - Desafios Fitness</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
<header class="topbar">
    <div class="container">
        <strong>Desafios Fitness</strong>
        <nav>
            <a href="${pageContext.request.contextPath}/home">Home</a>
            <a href="${pageContext.request.contextPath}/usuarios">Usuarios</a>
            <a href="${pageContext.request.contextPath}/desafios">Desafios</a>
        </nav>
    </div>
</header>

<main class="container">
    <div class="page-header">
        <h1>Desafios</h1>
    </div>

    <div class="alert alert-info">
        Esta area esta em construcao. O cadastro, a edicao, a exclusao e a participacao
        em desafios serao implementados na proxima etapa do projeto.
    </div>

    <div class="table-wrap">
        <c:choose>
            <c:when test="${empty desafios}">
                <p class="empty">Nenhum desafio cadastrado ainda.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome</th>
                        <th>Tipo de exercicio</th>
                        <th>Meta</th>
                        <th>Inicio</th>
                        <th>Fim</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="desafio" items="${desafios}">
                        <tr>
                            <td>${desafio.id}</td>
                            <td>${desafio.nome}</td>
                            <td>${desafio.tipoExercicio}</td>
                            <td>${desafio.meta}</td>
                            <td>${desafio.dataInicio}</td>
                            <td>${desafio.dataFim}</td>
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
