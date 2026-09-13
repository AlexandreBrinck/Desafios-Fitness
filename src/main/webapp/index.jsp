<%--
  Redireciona a raiz da aplicacao para a Home.
--%>
<%
    response.sendRedirect(request.getContextPath() + "/home");
%>
