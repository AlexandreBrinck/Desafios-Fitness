package br.com.desafiosfitness.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Model preparado nesta etapa apenas para dar suporte a tabela "progresso"
 * (ja criada no init.sql). O DAO, o Service e as telas de registro de
 * progresso serao implementados na proxima etapa do projeto.
 */
public class Progresso implements Mapeavel {

    private Long id;
    private Long usuarioId;
    private Long desafioId;
    private Date dataRegistro;
    private String valorAtingido;
    private String observacao;

    @Override
    public void preencher(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.usuarioId = rs.getLong("usuario_id");
        this.desafioId = rs.getLong("desafio_id");
        this.dataRegistro = rs.getDate("data_registro");
        this.valorAtingido = rs.getString("valor_atingido");
        this.observacao = rs.getString("observacao");
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

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getValorAtingido() {
        return valorAtingido;
    }

    public void setValorAtingido(String valorAtingido) {
        this.valorAtingido = valorAtingido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
