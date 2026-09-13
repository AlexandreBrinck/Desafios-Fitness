<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - Desafios Fitness</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
<div class="login-page">
    <div class="card login-card">
        <h1>Desafios Fitness</h1>
        <p>Entre com seu login e senha para criar desafios, participar e registrar seu progresso.</p>

        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="login">Login</label>
                <input type="text" id="login" name="login" required autofocus>
            </div>
            <div class="form-group">
                <label for="senha">Senha</label>
                <input type="password" id="senha" name="senha" required>
            </div>
            <div class="actions">
                <button type="submit" class="btn">Entrar</button>
            </div>
        </form>

        <p class="subtitulo">
            Ainda não tem conta? <a href="${pageContext.request.contextPath}/usuarios?acao=novo">Cadastre-se</a>.
            Você também pode <a href="${pageContext.request.contextPath}/desafios">ver os desafios</a> sem fazer login.
        </p>
    </div>
</div>
</body>
</html>
