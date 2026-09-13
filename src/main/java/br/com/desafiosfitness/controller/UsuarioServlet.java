package br.com.desafiosfitness.controller;

import br.com.desafiosfitness.model.Usuario;
import br.com.desafiosfitness.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller de Usuario.
 * Ponte entre rota, service e view — sem regra de negocio.
 *
 * Nesta etapa so ha cadastro (?acao=novo) e listagem (padrao). Edicao e
 * exclusao ficam para a proxima etapa (o Service ja esta preparado para isso).
 */
@WebServlet("/usuarios")
public class UsuarioServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/usuarios/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/usuarios/form.jsp";

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> this.forward(req, resp, FORM);
            default -> {
                req.setAttribute("usuarios", this.usuarioService.listar());
                this.forward(req, resp, LISTA);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        Usuario usuario = this.fromRequest(req);

        try {
            this.usuarioService.salvar(usuario);
            this.redirect(req, resp, "/usuarios");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("usuario", usuario);
            this.forward(req, resp, FORM);
        }
    }

    private Usuario fromRequest(HttpServletRequest req) {
        Usuario usuario = new Usuario();
        usuario.setNome(this.param(req, "nome"));
        usuario.setLogin(this.param(req, "login"));
        usuario.setSenha(this.param(req, "senha"));
        usuario.setPerfil(this.param(req, "perfil"));
        return usuario;
    }
}
