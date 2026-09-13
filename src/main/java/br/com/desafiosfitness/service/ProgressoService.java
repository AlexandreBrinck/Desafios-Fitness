package br.com.desafiosfitness.service;

import br.com.desafiosfitness.dao.ParticipacaoDAO;
import br.com.desafiosfitness.dao.ProgressoDAO;
import br.com.desafiosfitness.model.Progresso;

import java.sql.Date;
import java.util.List;

/**
 * SERVICE de Progresso — regras de negocio ficam aqui.
 *
 * Controller so chama estes metodos e decide a view.
 * DAO so executa SQL.
 */
public class ProgressoService {

    private final ProgressoDAO progressoDAO;
    private final ParticipacaoDAO participacaoDAO;

    public ProgressoService() {
        this.progressoDAO = new ProgressoDAO();
        this.participacaoDAO = new ParticipacaoDAO();
    }

    public List<Progresso> listarPorUsuario(Long usuarioId) {
        return this.progressoDAO.listarPorUsuario(usuarioId);
    }

    public List<Progresso> listarPorDesafio(Long desafioId) {
        return this.progressoDAO.listarPorDesafio(desafioId);
    }

    public Progresso buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.progressoDAO.buscarPorId(id);
    }

    /**
     * Regra de salvamento:
     * - desafio e valor atingido sao obrigatorios;
     * - data de registro assume a data atual quando nao informada;
     * - sem id -> cadastro novo (o usuario precisa participar do desafio);
     * - com id -> alteracao (somente o autor do registro pode alterar).
     */
    public void salvar(Progresso progresso, Long usuarioLogadoId) {
        if (progresso == null) {
            throw new IllegalArgumentException("Progresso é obrigatório.");
        }

        this.prepararDados(progresso);
        this.validarCamposObrigatorios(progresso);

        if (progresso.getId() == null) {
            if (!this.participacaoDAO.existeParticipacao(usuarioLogadoId, progresso.getDesafioId())) {
                throw new IllegalArgumentException("Você precisa participar do desafio antes de registrar progresso.");
            }
            progresso.setUsuarioId(usuarioLogadoId);
            this.progressoDAO.inserir(progresso);
            return;
        }

        Progresso existente = this.progressoDAO.buscarPorId(progresso.getId());
        if (existente == null) {
            throw new IllegalArgumentException("Registro de progresso não encontrado para alteração.");
        }
        if (!existente.getUsuarioId().equals(usuarioLogadoId)) {
            throw new IllegalArgumentException("Somente quem registrou o progresso pode alterá-lo.");
        }
        progresso.setUsuarioId(existente.getUsuarioId());
        progresso.setDesafioId(existente.getDesafioId());
        this.progressoDAO.alterar(progresso);
    }

    /**
     * Regra de exclusao:
     * - id obrigatorio;
     * - registro precisa existir;
     * - somente quem registrou pode excluir.
     */
    public void excluir(Long id, Long usuarioLogadoId) {
        if (id == null) {
            throw new IllegalArgumentException("Id é obrigatório para excluir.");
        }
        Progresso existente = this.progressoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Registro de progresso não encontrado.");
        }
        if (!existente.getUsuarioId().equals(usuarioLogadoId)) {
            throw new IllegalArgumentException("Somente quem registrou o progresso pode excluí-lo.");
        }
        this.progressoDAO.deletar(id);
    }

    private void prepararDados(Progresso progresso) {
        progresso.setValorAtingido(this.normalizar(progresso.getValorAtingido()));
        progresso.setObservacao(this.normalizar(progresso.getObservacao()));
        if (progresso.getDataRegistro() == null) {
            progresso.setDataRegistro(new Date(System.currentTimeMillis()));
        }
    }

    private void validarCamposObrigatorios(Progresso progresso) {
        if (progresso.getDesafioId() == null) {
            throw new IllegalArgumentException("Desafio é obrigatório.");
        }
        if (progresso.getValorAtingido() == null) {
            throw new IllegalArgumentException("Informe o valor atingido.");
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
