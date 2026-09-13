package br.com.desafiosfitness.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Relacionamento N:N entre Usuario e Desafio (tabela "participacoes").
 */
public class Participacao implements Mapeavel {

    private Long id;
    private Long usuarioId;
    private Long desafioId;
    private Date dataParticipacao;

    @Override
    public void preencher(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.usuarioId = rs.getLong("usuario_id");
        this.desafioId = rs.getLong("desafio_id");
        this.dataParticipacao = rs.getDate("data_participacao");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getDesafioId() {
        return desafioId;
    }

    public void setDesafioId(Long desafioId) {
        this.desafioId = desafioId;
    }

    public Date getDataParticipacao() {
        return dataParticipacao;
    }

    public void setDataParticipacao(Date dataParticipacao) {
        this.dataParticipacao = dataParticipacao;
    }
}
