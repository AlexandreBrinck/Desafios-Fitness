<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
            <a href="${pageContext.request.contextPath}/usuarios">Usuarios</a>
            <a href="${pageContext.request.contextPath}/desafios">Desafios</a>
        </nav>
    </div>
</header>

<main class="container">
    <div class="page-header">
        <h1>Painel</h1>
    </div>
    <p class="subtitulo">
        Aplicativo para criar desafios de exercicios, participar deles e acompanhar o progresso.
    </p>

    <div class="grid-cards">
        <a class="menu-card" href="${pageContext.request.contextPath}/usuarios">
            <strong>Usuarios</strong>
            <span>Cadastrar e listar os usuarios do sistema.</span>
        </a>
        <a class="menu-card em-breve" href="${pageContext.request.contextPath}/desafios">
            <strong>Desafios <span class="badge">Em construcao</span></strong>
            <span>Criar, listar e participar de desafios de exercicios.</span>
        </a>
    </div>
</main>

<footer class="rodape">
    Projeto em desenvolvimento em etapas — primeira etapa concluida.
</footer>
</body>
</html>
