package br.com.desafiosfitness.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Model preparado nesta etapa. O CRUD completo (criar, editar, excluir) e a
 * participacao de usuarios serao implementados na proxima etapa do projeto.
 */
public class Desafio implements Mapeavel {

    private Long id;
    private String nome;
    private String descricao;
    private String tipoExercicio;
    private String meta;
    private Date dataInicio;
    private Date dataFim;
    private Long criadorId;

    @Override
    public void preencher(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.nome = rs.getString("nome");
        this.descricao = rs.getString("descricao");
        this.tipoExercicio = rs.getString("tipo_exercicio");
        this.meta = rs.getString("meta");
        this.dataInicio = rs.getDate("data_inicio");
        this.dataFim = rs.getDate("data_fim");
        this.criadorId = rs.getLong("criador_id");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoExercicio() {
        return tipoExercicio;
    }

    public void setTipoExercicio(String tipoExercicio) {
        this.tipoExercicio = tipoExercicio;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Long getCriadorId() {
        return criadorId;
    }

    public void setCriadorId(Long criadorId) {
        this.criadorId = criadorId;
    }
}
