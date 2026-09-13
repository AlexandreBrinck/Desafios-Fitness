package br.com.desafiosfitness.controller;

import br.com.desafiosfitness.model.Desafio;
import br.com.desafiosfitness.model.Progresso;
import br.com.desafiosfitness.model.Usuario;
import br.com.desafiosfitness.service.DesafioService;
import br.com.desafiosfitness.service.ProgressoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller de Progresso.
 * Ponte entre rota, service e view — sem regra de negocio.
 *
 * Toda a area de Progresso exige login (garantido pelo AuthFilter em
 * "/progresso" e "/progresso/*"), pois todo registro pertence a um usuario.
 */
@WebServlet("/progresso")
public class ProgressoServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/progresso/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/progresso/form.jsp";

    private final ProgressoService progressoService = new ProgressoService();
    private final DesafioService desafioService = new DesafioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario logado = this.usuarioLogado(req);
        if (logado == null) {
            this.redirect(req, resp, "/login");
            return;
        }

        switch (this.acao(req)) {
            case "novo" -> {
                req.setAttribute("desafio", this.desafioService.buscarPorId(this.paramLong(req, "desafioId")));
                this.forward(req, resp, FORM);
            }
            case "editar" -> {
                Progresso progresso = this.progressoService.buscarPorId(this.paramLong(req, "id"));
                if (progresso == null || !logado.getId().equals(progresso.getUsuarioId())) {
                    this.redirect(req, resp, "/progresso");
                    return;
                }
                req.setAttribute("progresso", progresso);
                req.setAttribute("desafio", this.desafioService.buscarPorId(progresso.getDesafioId()));
                this.forward(req, resp, FORM);
            }
            case "excluir" -> {
                try {
                    this.progressoService.excluir(this.paramLong(req, "id"), logado.getId());
                } catch (IllegalArgumentException e) {
                    req.setAttribute("erro", e.getMessage());
                    req.setAttribute("registros", this.progressoService.listarPorUsuario(logado.getId()));
                    req.setAttribute("desafios", this.mapaDesafios());
                    this.forward(req, resp, LISTA);
                    return;
                }
                this.redirect(req, resp, "/progresso");
            }
            case "porDesafio" -> {
                Long desafioId = this.paramLong(req, "desafioId");
                req.setAttribute("desafio", this.desafioService.buscarPorId(desafioId));
                req.setAttribute("registros", this.progressoService.listarPorDesafio(desafioId));
                req.setAttribute("porDesafio", true);
                this.forward(req, resp, LISTA);
            }
            default -> {
                req.setAttribute("registros", this.progressoService.listarPorUsuario(logado.getId()));
                req.setAttribute("desafios", this.mapaDesafios());
                this.forward(req, resp, LISTA);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        Usuario logado = this.usuarioLogado(req);
        if (logado == null) {
            this.redirect(req, resp, "/login");
            return;
        }

        Progresso progresso = this.fromRequest(req);
        try {
            this.progressoService.salvar(progresso, logado.getId());
            this.redirect(req, resp, "/progresso");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("progresso", progresso);
            req.setAttribute("desafio", this.desafioService.buscarPorId(progresso.getDesafioId()));
            this.forward(req, resp, FORM);
        }
    }

    private Progresso fromRequest(HttpServletRequest req) {
        Progresso progresso = new Progresso();
        progresso.setId(this.paramLong(req, "id"));
        progresso.setDesafioId(this.paramLong(req, "desafioId"));
        progresso.setDataRegistro(this.paramDate(req, "dataRegistro"));
        progresso.setValorAtingido(this.param(req, "valorAtingido"));
        progresso.setObservacao(this.param(req, "observacao"));
        return progresso;
    }

    private Map<Long, Desafio> mapaDesafios() {
        Map<Long, Desafio> mapa = new HashMap<>();
        for (Desafio desafio : this.desafioService.listar()) {
            mapa.put(desafio.getId(), desafio);
        }
        return mapa;
    }
}
