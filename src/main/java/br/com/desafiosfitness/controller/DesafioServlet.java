package br.com.desafiosfitness.controller;

import br.com.desafiosfitness.model.Desafio;
import br.com.desafiosfitness.model.Usuario;
import br.com.desafiosfitness.service.DesafioService;
import br.com.desafiosfitness.service.ParticipacaoService;
import br.com.desafiosfitness.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller de Desafio.
 * Ponte entre rota, service e view — sem regra de negocio.
 *
 * Listar e visualizar sao publicos. Criar, editar, excluir, participar e
 * "meus desafios" exigem login (verificado aqui, pois a listagem/visualizacao
 * do mesmo Servlet continua publica).
 */
@WebServlet("/desafios")
public class DesafioServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/desafios/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/desafios/form.jsp";
    private static final String VISUALIZAR = "/WEB-INF/jsp/desafios/visualizar.jsp";

    private final DesafioService desafioService = new DesafioService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final ParticipacaoService participacaoService = new ParticipacaoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> {
                if (this.usuarioLogado(req) == null) {
                    this.redirect(req, resp, "/login");
                    return;
                }
                this.forward(req, resp, FORM);
            }
            case "editar" -> {
                Usuario logado = this.usuarioLogado(req);
                if (logado == null) {
                    this.redirect(req, resp, "/login");
                    return;
                }
                Desafio desafio = this.desafioService.buscarPorId(this.paramLong(req, "id"));
                if (desafio == null || !logado.getId().equals(desafio.getCriadorId())) {
                    this.redirect(req, resp, "/desafios");
                    return;
                }
                req.setAttribute("desafio", desafio);
                this.forward(req, resp, FORM);
            }
            case "excluir" -> {
                Usuario logado = this.usuarioLogado(req);
                if (logado == null) {
                    this.redirect(req, resp, "/login");
                    return;
                }
                try {
                    this.desafioService.excluir(this.paramLong(req, "id"), logado.getId());
                } catch (IllegalArgumentException e) {
                    req.setAttribute("erro", e.getMessage());
                    req.setAttribute("desafios", this.desafioService.listar());
                    req.setAttribute("usuarios", this.mapaUsuarios());
                    this.forward(req, resp, LISTA);
                    return;
                }
                this.redirect(req, resp, "/desafios");
            }
            case "visualizar" -> this.visualizar(req, resp, this.paramLong(req, "id"), null);
            case "participar" -> {
                Usuario logado = this.usuarioLogado(req);
                if (logado == null) {
                    this.redirect(req, resp, "/login");
                    return;
                }
                Long id = this.paramLong(req, "id");
                String sucesso = null;
                try {
                    this.participacaoService.participar(logado.getId(), id);
                    sucesso = "Você agora está participando deste desafio!";
                } catch (IllegalArgumentException e) {
                    req.setAttribute("erro", e.getMessage());
                }
                this.visualizar(req, resp, id, sucesso);
            }
            case "removerParticipante" -> {
                Usuario logado = this.usuarioLogado(req);
                if (logado == null) {
                    this.redirect(req, resp, "/login");
                    return;
                }
                Long id = this.paramLong(req, "id");
                String sucesso = null;
                try {
                    this.participacaoService.removerParticipante(id, this.paramLong(req, "usuarioId"), logado.getId());
                    sucesso = "Participante removido do desafio.";
                } catch (IllegalArgumentException e) {
                    req.setAttribute("erro", e.getMessage());
                }
                this.visualizar(req, resp, id, sucesso);
            }
            case "meus" -> {
                Usuario logado = this.usuarioLogado(req);
                if (logado == null) {
                    this.redirect(req, resp, "/login");
                    return;
                }
                req.setAttribute("desafios", this.participacaoService.listarDesafiosDoUsuario(logado.getId()));
                req.setAttribute("usuarios", this.mapaUsuarios());
                req.setAttribute("meusDesafios", true);
                this.forward(req, resp, LISTA);
            }
            default -> {
                req.setAttribute("desafios", this.desafioService.listar());
                req.setAttribute("usuarios", this.mapaUsuarios());
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

        Desafio desafio = this.fromRequest(req);
        try {
            this.desafioService.salvar(desafio, logado.getId());
            this.redirect(req, resp, "/desafios");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("desafio", desafio);
            this.forward(req, resp, FORM);
        }
    }

    private void visualizar(HttpServletRequest req, HttpServletResponse resp, Long id, String sucesso)
            throws ServletException, IOException {

        Desafio desafio = this.desafioService.buscarPorId(id);
        if (desafio == null) {
            this.redirect(req, resp, "/desafios");
            return;
        }

        Usuario logado = this.usuarioLogado(req);
        req.setAttribute("desafio", desafio);
        req.setAttribute("criador", this.usuarioService.buscarPorId(desafio.getCriadorId()));
        req.setAttribute("participantes", this.participacaoService.listarParticipantes(desafio.getId()));
        req.setAttribute("participando",
                logado != null && this.participacaoService.existeParticipacao(logado.getId(), desafio.getId()));
        req.setAttribute("souCriador", logado != null && logado.getId().equals(desafio.getCriadorId()));
        if (sucesso != null) {
            req.setAttribute("sucesso", sucesso);
        }
        this.forward(req, resp, VISUALIZAR);
    }

    private Desafio fromRequest(HttpServletRequest req) {
        Desafio desafio = new Desafio();
        desafio.setId(this.paramLong(req, "id"));
        desafio.setNome(this.param(req, "nome"));
        desafio.setDescricao(this.param(req, "descricao"));
        desafio.setTipoExercicio(this.param(req, "tipoExercicio"));
        desafio.setMeta(this.param(req, "meta"));
        desafio.setDataInicio(this.paramDate(req, "dataInicio"));
        desafio.setDataFim(this.paramDate(req, "dataFim"));
        return desafio;
    }

    private Map<Long, Usuario> mapaUsuarios() {
        Map<Long, Usuario> mapa = new HashMap<>();
        for (Usuario usuario : this.usuarioService.listar()) {
            mapa.put(usuario.getId(), usuario);
        }
        return mapa;
    }
}
