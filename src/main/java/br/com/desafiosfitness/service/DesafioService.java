package br.com.desafiosfitness.service;

import br.com.desafiosfitness.dao.DesafioDAO;
import br.com.desafiosfitness.model.Desafio;

import java.util.List;

/**
 * SERVICE de Desafio — regras de negocio ficam aqui.
 *
 * Controller so chama estes metodos e decide a view.
 * DAO so executa SQL.
 */
public class DesafioService {

    // Tamanhos maximos das colunas da tabela "desafios" (init.sql).
    private static final int MAX_NOME = 150;
    private static final int MAX_DESCRICAO = 500;
    private static final int MAX_TIPO_EXERCICIO = 100;
    private static final int MAX_META = 100;

    private final DesafioDAO desafioDAO;

    public DesafioService() {
        this.desafioDAO = new DesafioDAO();
    }

    public List<Desafio> listar() {
        return this.desafioDAO.listarTodos();
    }

    public Desafio buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.desafioDAO.buscarPorId(id);
    }

    /**
     * Regra de salvamento:
     * - nome, tipo de exercicio e data de inicio sao obrigatorios;
     * - textos nao podem ultrapassar o tamanho das colunas do banco;
     * - data de fim (quando informada) nao pode ser anterior a data de inicio;
     * - sem id -> cadastro novo, o criador e sempre o usuario logado
     *   (o id gerado pelo banco e preenchido no proprio objeto pelo DAO);
     * - com id -> alteracao, somente o criador original pode editar.
     */
    public void salvar(Desafio desafio, Long usuarioLogadoId) {
        if (desafio == null) {
            throw new IllegalArgumentException("Desafio é obrigatório.");
        }
        this.validarUsuarioLogado(usuarioLogadoId);

        this.prepararDados(desafio);
        this.validarCamposObrigatorios(desafio);
        this.validarTamanhos(desafio);
        this.validarDatas(desafio);

        if (desafio.getId() == null) {
            desafio.setCriadorId(usuarioLogadoId);
            this.desafioDAO.inserir(desafio);
            return;
        }

        Desafio existente = this.desafioDAO.buscarPorId(desafio.getId());
        if (existente == null) {
            throw new IllegalArgumentException("Desafio não encontrado para alteração.");
        }
        if (!existente.getCriadorId().equals(usuarioLogadoId)) {
            throw new IllegalArgumentException("Somente o criador pode editar este desafio.");
        }
        desafio.setCriadorId(existente.getCriadorId());
        this.desafioDAO.alterar(desafio);
    }

    /**
     * Regra de exclusao:
     * - id obrigatorio;
     * - desafio precisa existir;
     * - somente o criador pode excluir (participacoes e progresso relacionados
     *   sao removidos pelo proprio banco, via ON DELETE CASCADE).
     */
    public void excluir(Long id, Long usuarioLogadoId) {
        if (id == null) {
            throw new IllegalArgumentException("Id é obrigatório para excluir.");
        }
        this.validarUsuarioLogado(usuarioLogadoId);
        Desafio existente = this.desafioDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Desafio não encontrado.");
        }
        if (!existente.getCriadorId().equals(usuarioLogadoId)) {
            throw new IllegalArgumentException("Somente o criador pode excluir este desafio.");
        }
        this.desafioDAO.deletar(id);
    }

    private void prepararDados(Desafio desafio) {
        desafio.setNome(this.normalizar(desafio.getNome()));
        desafio.setDescricao(this.normalizar(desafio.getDescricao()));
        desafio.setTipoExercicio(this.normalizar(desafio.getTipoExercicio()));
        desafio.setMeta(this.normalizar(desafio.getMeta()));
    }

    private void validarCamposObrigatorios(Desafio desafio) {
        if (desafio.getNome() == null) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (desafio.getTipoExercicio() == null) {
            throw new IllegalArgumentException("Tipo de exercício é obrigatório.");
        }
        if (desafio.getDataInicio() == null) {
            throw new IllegalArgumentException("Data de início é obrigatória.");
        }
    }

    private void validarUsuarioLogado(Long usuarioLogadoId) {
        if (usuarioLogadoId == null) {
            throw new IllegalArgumentException("Usuário logado é obrigatório para esta operação.");
        }
    }

    private void validarTamanhos(Desafio desafio) {
        this.validarTamanho(desafio.getNome(), "Nome", MAX_NOME);
        this.validarTamanho(desafio.getDescricao(), "Descrição", MAX_DESCRICAO);
        this.validarTamanho(desafio.getTipoExercicio(), "Tipo de exercício", MAX_TIPO_EXERCICIO);
        this.validarTamanho(desafio.getMeta(), "Meta", MAX_META);
    }

    private void validarTamanho(String valor, String campo, int maximo) {
        if (valor != null && valor.length() > maximo) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + maximo + " caracteres.");
        }
    }

    private void validarDatas(Desafio desafio) {
        if (desafio.getDataFim() != null && desafio.getDataFim().before(desafio.getDataInicio())) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
