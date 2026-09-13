<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Novo usuario - Desafios Fitness</title>
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
        <h1>Novo usuario</h1>
    </div>

    <div class="card">
        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/usuarios">
            <div class="form-group">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" value="${usuario.nome}" required>
            </div>

            <div class="form-group">
                <label for="login">Login</label>
                <input type="text" id="login" name="login" value="${usuario.login}" required>
            </div>

            <div class="form-group">
                <label for="senha">Senha</label>
                <input type="text" id="senha" name="senha" value="${usuario.senha}" required>
            </div>

            <div class="form-group">
                <label for="perfil">Perfil</label>
                <select id="perfil" name="perfil" required>
                    <option value="">Selecione</option>
                    <option value="Administrador" <c:if test="${usuario.perfil == 'Administrador'}">selected</c:if>>Administrador</option>
                    <option value="Participante" <c:if test="${usuario.perfil == 'Participante'}">selected</c:if>>Participante</option>
                </select>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Salvar</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/usuarios">Cancelar</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>
