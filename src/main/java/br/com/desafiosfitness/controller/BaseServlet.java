package br.com.desafiosfitness.controller;

import br.com.desafiosfitness.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;

/**
 * Base dos controllers.
 * So helpers de rota/view: ler parametro, encaminhar JSP e redirecionar.
 * Regra de negocio fica no Service. Rotas usam o parametro "acao" (ex: ?acao=novo).
 */
public abstract class BaseServlet extends HttpServlet {

    protected String acao(HttpServletRequest req) {
        String acao = req.getParameter("acao");
        if (acao == null || acao.isBlank()) {
            return "listar";
        }
        return acao;
    }

    protected String param(HttpServletRequest req, String nome) {
        return req.getParameter(nome);
    }

    protected Long paramLong(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Date paramDate(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Date.valueOf(valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Usuario autenticado na sessao atual, ou null se ninguem estiver logado.
     * Usado pelas funcionalidades que dependem de "quem esta fazendo a acao"
     * (criar desafio, participar, registrar progresso).
     */
    protected Usuario usuarioLogado(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String jsp)
            throws ServletException, IOException {
        req.getRequestDispatcher(jsp).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String caminho)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + caminho);
    }
}
