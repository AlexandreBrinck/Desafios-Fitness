package br.com.desafiosfitness.service;

import br.com.desafiosfitness.dao.DesafioDAO;
import br.com.desafiosfitness.dao.ParticipacaoDAO;
import br.com.desafiosfitness.model.Desafio;
import br.com.desafiosfitness.model.Participacao;
import br.com.desafiosfitness.model.Usuario;

import java.sql.Date;
import java.util.List;

/**
 * SERVICE de participacao — regras de negocio ficam aqui.
 */
public class ParticipacaoService {

    private final ParticipacaoDAO participacaoDAO;
    private final DesafioDAO desafioDAO;

    public ParticipacaoService() {
        this.participacaoDAO = new ParticipacaoDAO();
        this.desafioDAO = new DesafioDAO();
    }

    /**
     * Regra de participacao:
     * - usuario e desafio sao obrigatorios;
     * - o desafio precisa existir;
     * - o mesmo usuario nao pode participar duas vezes do mesmo desafio.
     */
    public void participar(Long usuarioId, Long desafioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuário inválido.");
        }
        if (desafioId == null) {
            throw new IllegalArgumentException("Desafio inválido.");
        }
        if (this.desafioDAO.buscarPorId(desafioId) == null) {
            throw new IllegalArgumentException("Desafio não encontrado.");
        }
        if (this.participacaoDAO.existeParticipacao(usuarioId, desafioId)) {
            throw new IllegalArgumentException("Você já está participando deste desafio.");
        }

        Participacao participacao = new Participacao();
        participacao.setUsuarioId(usuarioId);
        participacao.setDesafioId(desafioId);
        participacao.setDataParticipacao(new Date(System.currentTimeMillis()));
        this.participacaoDAO.inserir(participacao);
    }

    /**
     * Regra de remocao de participante:
     * - desafio e participante sao obrigatorios;
     * - o desafio precisa existir;
     * - somente o criador do desafio pode remover participantes;
     * - o participante precisa realmente estar participando.
     *
     * So apaga o registro em "participacoes". O historico em "progresso" e
     * preservado (nenhuma regra atual exige apagar os registros antigos).
     */
    public void removerParticipante(Long desafioId, Long participanteId, Long usuarioLogadoId) {
        if (desafioId == null) {
            throw new IllegalArgumentException("Desafio inválido.");
        }
        if (participanteId == null) {
            throw new IllegalArgumentException("Participante inválido.");
        }
        Desafio desafio = this.desafioDAO.buscarPorId(desafioId);
        if (desafio == null) {
            throw new IllegalArgumentException("Desafio não encontrado.");
        }
        if (!desafio.getCriadorId().equals(usuarioLogadoId)) {
            throw new IllegalArgumentException("Somente o criador pode remover participantes deste desafio.");
        }
        if (!this.participacaoDAO.existeParticipacao(participanteId, desafioId)) {
            throw new IllegalArgumentException("Este usuário não participa deste desafio.");
        }
        this.participacaoDAO.deletar(participanteId, desafioId);
    }

    public boolean existeParticipacao(Long usuarioId, Long desafioId) {
        if (usuarioId == null || desafioId == null) {
            return false;
        }
        return this.participacaoDAO.existeParticipacao(usuarioId, desafioId);
    }

    public List<Desafio> listarDesafiosDoUsuario(Long usuarioId) {
        return this.participacaoDAO.listarDesafiosDoUsuario(usuarioId);
    }

    public List<Usuario> listarParticipantes(Long desafioId) {
        return this.participacaoDAO.listarParticipantes(desafioId);
    }
}
