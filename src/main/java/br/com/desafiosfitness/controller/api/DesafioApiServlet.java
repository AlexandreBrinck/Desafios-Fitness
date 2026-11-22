package br.com.desafiosfitness.controller.api;

import br.com.desafiosfitness.controller.BaseServlet;
import br.com.desafiosfitness.model.Desafio;
import br.com.desafiosfitness.model.Usuario;
import br.com.desafiosfitness.service.DesafioService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller da API REST de Desafios (respostas em JSON).
 *
 * Rotas:
 *   GET    /api/desafios       -> lista todos
 *   GET    /api/desafios/{id}  -> busca por id
 *   POST   /api/desafios       -> cadastra (exige login)
 *   PUT    /api/desafios/{id}  -> atualiza (exige login; somente o criador)
 *   DELETE /api/desafios/{id}  -> exclui   (exige login; somente o criador)
 *
 * Assim como o DesafioServlet (site), este controller nao tem regra de negocio:
 * converte HTTP/JSON <-> Desafio e delega ao {@link DesafioService}. A autenticacao
 * reaproveita a mesma sessao do site (POST /login), pois o Service precisa do id
 * do usuario logado para cadastrar, editar e excluir.
 *
 * Formato das respostas:
 *   sucesso -> {"sucesso": true,  "dados": ..., "mensagem": "..."}
 *   erro    -> {"sucesso": false, "status": 400, "erro": "..."}
 */
@WebServlet("/api/desafios/*")
public class DesafioApiServlet extends BaseServlet {

    private static final String JSON = "application/json";

    // Limites das colunas da tabela "desafios" (init.sql), para devolver 400
    // em vez de um erro de banco quando o texto for maior que a coluna.
    private static final int MAX_NOME = 150;
    private static final int MAX_DESCRICAO = 500;
    private static final int MAX_TIPO_EXERCICIO = 100;
    private static final int MAX_META = 100;

    private final DesafioService desafioService = new DesafioService();
    private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    /**
     * Roteamento proprio (em vez de doGet/doPost/...) para que metodos nao
     * suportados tambem recebam resposta JSON (405), e nao a pagina HTML padrao.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String caminho = req.getPathInfo();
            boolean temId = caminho != null && !caminho.equals("/");
            String metodo = req.getMethod();

            if (!temId) {
                switch (metodo) {
                    case "GET" -> this.listar(resp);
                    case "POST" -> this.cadastrar(req, resp);
                    default -> this.metodoNaoPermitido(resp, "GET, POST");
                }
                return;
            }

            Long id = this.extrairId(caminho);
            if (id == null) {
                this.erro(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Id inválido. Use /api/desafios/{id} com um número inteiro positivo.");
                return;
            }

            switch (metodo) {
                case "GET" -> this.buscar(id, resp);
                case "PUT" -> this.atualizar(id, req, resp);
                case "DELETE" -> this.excluir(id, req, resp);
                default -> this.metodoNaoPermitido(resp, "GET, PUT, DELETE");
            }
        } catch (RuntimeException e) {
            this.log("Erro inesperado na API de desafios", e);
            this.erro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao processar a requisição.");
        }
    }

    // ===================== ENDPOINTS =====================

    private void listar(HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Desafio desafio : this.desafioService.listar()) {
            lista.add(this.toJson(desafio));
        }
        Map<String, Object> corpo = this.sucesso(lista, null);
        corpo.put("total", lista.size());
        this.responder(resp, HttpServletResponse.SC_OK, corpo);
    }

    private void buscar(Long id, HttpServletResponse resp) throws IOException {
        Desafio desafio = this.desafioService.buscarPorId(id);
        if (desafio == null) {
            this.naoEncontrado(resp, id);
            return;
        }
        this.responder(resp, HttpServletResponse.SC_OK, this.sucesso(this.toJson(desafio), null));
    }

    private void cadastrar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario logado = this.exigirLogin(req, resp);
        if (logado == null) {
            return;
        }
        Desafio desafio = this.lerCorpo(req, resp);
        if (desafio == null) {
            return;
        }
        desafio.setId(null); // cadastro: id e gerado pelo banco

        try {
            this.desafioService.salvar(desafio, logado.getId());
        } catch (IllegalArgumentException e) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        // O DAO preenche no proprio objeto o id gerado pelo MySQL.
        this.responder(resp, HttpServletResponse.SC_CREATED,
                this.sucesso(this.toJson(desafio), "Desafio cadastrado com sucesso."));
    }

    private void atualizar(Long id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario logado = this.exigirLogin(req, resp);
        if (logado == null) {
            return;
        }
        if (!this.verificarExistenciaECriador(id, logado, resp, "editar")) {
            return;
        }
        Desafio desafio = this.lerCorpo(req, resp);
        if (desafio == null) {
            return;
        }
        if (desafio.getId() != null && !desafio.getId().equals(id)) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "O id do corpo (" + desafio.getId() + ") difere do id da URL (" + id + ").");
            return;
        }
        desafio.setId(id);

        try {
            this.desafioService.salvar(desafio, logado.getId());
        } catch (IllegalArgumentException e) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        Desafio atualizado = this.desafioService.buscarPorId(id);
        this.responder(resp, HttpServletResponse.SC_OK,
                this.sucesso(this.toJson(atualizado), "Desafio atualizado com sucesso."));
    }

    private void excluir(Long id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario logado = this.exigirLogin(req, resp);
        if (logado == null) {
            return;
        }
        if (!this.verificarExistenciaECriador(id, logado, resp, "excluir")) {
            return;
        }
        try {
            this.desafioService.excluir(id, logado.getId());
        } catch (IllegalArgumentException e) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        this.responder(resp, HttpServletResponse.SC_OK,
                this.sucesso(null, "Desafio " + id + " excluído com sucesso."));
    }

    // ===================== AUTENTICACAO / AUTORIZACAO =====================

    private Usuario exigirLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario logado = this.usuarioLogado(req);
        if (logado == null) {
            this.erro(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    "Autenticação necessária. Faça login em POST /login (campos login e senha).");
        }
        return logado;
    }

    /**
     * Checagens feitas antes de chamar o Service apenas para escolher o codigo HTTP
     * correto (404 / 403). A regra continua sendo aplicada tambem pelo Service.
     */
    private boolean verificarExistenciaECriador(Long id, Usuario logado, HttpServletResponse resp, String acao)
            throws IOException {
        Desafio existente = this.desafioService.buscarPorId(id);
        if (existente == null) {
            this.naoEncontrado(resp, id);
            return false;
        }
        if (!logado.getId().equals(existente.getCriadorId())) {
            this.erro(resp, HttpServletResponse.SC_FORBIDDEN, "Somente o criador pode " + acao + " este desafio.");
            return false;
        }
        return true;
    }

    // ===================== LEITURA / VALIDACAO DO JSON =====================

    /**
     * Le o corpo JSON e monta um Desafio. Valida apenas o formato da requisicao
     * (JSON valido, tipos, datas yyyy-MM-dd, tamanho dos textos); campos
     * obrigatorios e coerencia das datas sao validados pelo Service.
     * Retorna null (com resposta de erro ja enviada) se a requisicao for invalida.
     */
    private Desafio lerCorpo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith(JSON)) {
            this.erro(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Envie o corpo em JSON com o cabeçalho Content-Type: application/json.");
            return null;
        }

        JsonObject json;
        try {
            JsonElement elemento = JsonParser.parseReader(req.getReader());
            if (!elemento.isJsonObject()) {
                this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, "O corpo da requisição deve ser um objeto JSON.");
                return null;
            }
            json = elemento.getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido ou malformado.");
            return null;
        }

        try {
            Desafio desafio = new Desafio();
            desafio.setId(this.lerLong(json, "id"));
            desafio.setNome(this.lerTexto(json, "nome", MAX_NOME));
            desafio.setDescricao(this.lerTexto(json, "descricao", MAX_DESCRICAO));
            desafio.setTipoExercicio(this.lerTexto(json, "tipoExercicio", MAX_TIPO_EXERCICIO));
            desafio.setMeta(this.lerTexto(json, "meta", MAX_META));
            desafio.setDataInicio(this.lerData(json, "dataInicio"));
            desafio.setDataFim(this.lerData(json, "dataFim"));
            return desafio;
        } catch (IllegalArgumentException e) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private String lerTexto(JsonObject json, String campo, int tamanhoMaximo) {
        JsonElement valor = json.get(campo);
        if (valor == null || valor.isJsonNull()) {
            return null;
        }
        if (!valor.isJsonPrimitive() || !valor.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Campo '" + campo + "' deve ser um texto.");
        }
        String texto = valor.getAsString();
        if (texto.trim().length() > tamanhoMaximo) {
            throw new IllegalArgumentException(
                    "Campo '" + campo + "' deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
        return texto;
    }

    private Date lerData(JsonObject json, String campo) {
        String texto = this.lerTexto(json, campo, Integer.MAX_VALUE);
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Date.valueOf(LocalDate.parse(texto.trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Campo '" + campo + "' deve estar no formato yyyy-MM-dd.");
        }
    }

    private Long lerLong(JsonObject json, String campo) {
        JsonElement valor = json.get(campo);
        if (valor == null || valor.isJsonNull()) {
            return null;
        }
        try {
            if (valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isNumber()) {
                return valor.getAsJsonPrimitive().getAsBigDecimal().longValueExact();
            }
        } catch (ArithmeticException e) {
            // cai na mensagem abaixo
        }
        throw new IllegalArgumentException("Campo '" + campo + "' deve ser um número inteiro.");
    }

    private Long extrairId(String caminho) {
        String valor = caminho.substring(1); // remove a "/" inicial
        if (valor.endsWith("/")) {
            valor = valor.substring(0, valor.length() - 1);
        }
        if (!valor.matches("\\d{1,18}")) {
            return null;
        }
        long id = Long.parseLong(valor);
        return id > 0 ? id : null;
    }

    // ===================== RESPOSTAS JSON =====================

    private Map<String, Object> toJson(Desafio desafio) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", desafio.getId());
        mapa.put("nome", desafio.getNome());
        mapa.put("descricao", desafio.getDescricao());
        mapa.put("tipoExercicio", desafio.getTipoExercicio());
        mapa.put("meta", desafio.getMeta());
        mapa.put("dataInicio", desafio.getDataInicio() == null ? null : desafio.getDataInicio().toString());
        mapa.put("dataFim", desafio.getDataFim() == null ? null : desafio.getDataFim().toString());
        mapa.put("criadorId", desafio.getCriadorId());
        return mapa;
    }

    private Map<String, Object> sucesso(Object dados, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("sucesso", true);
        if (mensagem != null) {
            corpo.put("mensagem", mensagem);
        }
        corpo.put("dados", dados);
        return corpo;
    }

    private void naoEncontrado(HttpServletResponse resp, Long id) throws IOException {
        this.erro(resp, HttpServletResponse.SC_NOT_FOUND, "Desafio com id " + id + " não encontrado.");
    }

    private void metodoNaoPermitido(HttpServletResponse resp, String permitidos) throws IOException {
        resp.setHeader("Allow", permitidos);
        this.erro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Método não permitido neste endereço. Permitidos: " + permitidos + ".");
    }

    private void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("sucesso", false);
        corpo.put("status", status);
        corpo.put("erro", mensagem);
        this.responder(resp, status, corpo);
    }

    private void responder(HttpServletResponse resp, int status, Object corpo) throws IOException {
        if (resp.isCommitted()) {
            return;
        }
        resp.setStatus(status);
        resp.setContentType(JSON);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(this.gson.toJson(corpo));
    }
}
