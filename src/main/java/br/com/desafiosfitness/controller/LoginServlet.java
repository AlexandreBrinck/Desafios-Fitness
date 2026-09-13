package br.com.desafiosfitness.controller;

import br.com.desafiosfitness.model.Usuario;
import br.com.desafiosfitness.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller de Login.
 * Necessario para saber "quem" esta criando desafios, participando e
 * registrando progresso — sem regra de negocio, so chama o Service e
 * encaminha o resultado.
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private static final String VIEW = "/WEB-INF/jsp/login.jsp";

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (this.usuarioLogado(req) != null) {
            this.redirect(req, resp, "/home");
            return;
        }
        this.forward(req, resp, VIEW);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        try {
            Usuario usuario = this.usuarioService.autenticar(
                    this.param(req, "login"),
                    this.param(req, "senha"));

            req.getSession(true).setAttribute("usuarioLogado", usuario);
            this.redirect(req, resp, "/home");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            this.forward(req, resp, VIEW);
        }
    }
}
