<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${desafio.nome} - Desafios Fitness</title>
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
        <h1>${desafio.nome}</h1>
    </div>

    <c:if test="${not empty erro}">
        <div class="alert alert-erro">${erro}</div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="alert alert-sucesso">${sucesso}</div>
    </c:if>

    <div class="card">
        <dl class="detalhes">
            <dt>Tipo de exercício</dt>
            <dd>${desafio.tipoExercicio}</dd>

            <dt>Meta</dt>
            <dd>${empty desafio.meta ? '-' : desafio.meta}</dd>

            <dt>Período</dt>
            <dd>${desafio.dataInicio} a ${empty desafio.dataFim ? 'sem data definida' : desafio.dataFim}</dd>

            <dt>Descrição</dt>
            <dd>${empty desafio.descricao ? '-' : desafio.descricao}</dd>

            <dt>Criador</dt>
            <dd>${criador.nome}</dd>

            <dt>Participantes</dt>
            <dd>${fn:length(participantes)}</dd>
        </dl>

        <div class="actions">
            <c:if test="${souCriador}">
                <span class="status-pill">Você é o criador deste desafio</span>
            </c:if>

            <c:choose>
                <c:when test="${empty sessionScope.usuarioLogado}">
                    <a class="btn" href="${pageContext.request.contextPath}/login">Faça login para participar</a>
                </c:when>
                <c:when test="${participando}">
                    <span class="status-pill">Você já participa deste desafio</span>
                    <a class="btn" href="${pageContext.request.contextPath}/progresso?acao=novo&desafioId=${desafio.id}">Registrar progresso</a>
                </c:when>
                <c:otherwise>
                    <a class="btn" href="${pageContext.request.contextPath}/desafios?acao=participar&id=${desafio.id}">Participar deste desafio</a>
                </c:otherwise>
            </c:choose>

            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/progresso?acao=porDesafio&desafioId=${desafio.id}">Ver progresso dos participantes</a>

            <c:if test="${souCriador}">
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/desafios?acao=editar&id=${desafio.id}">Editar</a>
                <a class="btn btn-danger" href="${pageContext.request.contextPath}/desafios?acao=excluir&id=${desafio.id}"
                   onclick="return confirm('Excluir este desafio? Participações e registros de progresso relacionados também serão removidos.');">Excluir</a>
            </c:if>
        </div>
    </div>

    <div class="page-header">
        <h2>Participantes</h2>
    </div>

    <div class="table-wrap">
        <c:choose>
            <c:when test="${empty participantes}">
                <p class="empty">Ainda não há participantes neste desafio.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>Participante</th>
                        <th>Login</th>
                        <c:if test="${souCriador}">
                            <th>Ações</th>
                        </c:if>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="participante" items="${participantes}">
                        <tr>
                            <td>${participante.nome}</td>
                            <td>${participante.login}</td>
                            <c:if test="${souCriador}">
                                <td>
                                    <span class="links-inline">
                                        <a href="${pageContext.request.contextPath}/desafios?acao=removerParticipante&id=${desafio.id}&usuarioId=${participante.id}"
                                           onclick="return confirm('Remover ${participante.nome} deste desafio?');">Remover participante</a>
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
