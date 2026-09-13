package br.com.desafiosfitness.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Usuario implements Mapeavel {

    private Long id;
    private String nome;
    private String login;
    private String senha;
    private String perfil;

    @Override
    public void preencher(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.nome = rs.getString("nome");
        this.login = rs.getString("login");
        this.senha = rs.getString("senha");
        this.perfil = rs.getString("perfil");
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
