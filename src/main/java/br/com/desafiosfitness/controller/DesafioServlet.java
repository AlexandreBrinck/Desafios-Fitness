package br.com.desafiosfitness.controller;

import br.com.desafiosfitness.service.DesafioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller inicial de Desafio, preparado para a proxima etapa do projeto.
 *
 * Nesta etapa so existe listagem (leitura). Cadastro, edicao, exclusao e
 * participacao de usuarios serao implementados na proxima etapa.
 */
@WebServlet("/desafios")
public class DesafioServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/desafios/lista.jsp";

    private final DesafioService desafioService = new DesafioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("desafios", this.desafioService.listar());
        this.forward(req, resp, LISTA);
    }
}
