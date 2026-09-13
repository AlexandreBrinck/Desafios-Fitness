package br.com.desafiosfitness.dao;

import br.com.desafiosfitness.model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object (acesso ao banco)
 *
 * Somente SQL. A conversao ResultSet -> {@link Usuario} e feita por
 * mapearSimples(), herdado de {@link MysqlDAO}.
 * Quem decide "quando" chamar cada metodo e o Service / Controller.
 */
public class UsuarioDAO extends MysqlDAO {

    public UsuarioDAO() {
        super();
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT id, nome, login, senha, perfil FROM usuarios ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Usuario.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários.", e);
        }
        return lista;
    }

    public Usuario buscarPorId(Long id) {
        String sql = "SELECT id, nome, login, senha, perfil FROM usuarios WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Usuario.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por id.", e);
        }
        return null;
    }

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT id, nome, login, senha, perfil FROM usuarios WHERE login = ?";
        try (ResultSet rs = super.executar(sql, login)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Usuario.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por login.", e);
        }
        return null;
    }

    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, login, senha, perfil) VALUES (?, ?, ?, ?)";
        try {
            super.executarUpdate(
                    sql,
                    usuario.getNome(),
                    usuario.getLogin(),
                    usuario.getSenha(),
                    usuario.getPerfil());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário.", e);
        }
    }

    // Preparado para a proxima etapa: tela de edicao de usuarios.
    public void alterar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, login = ?, senha = ?, perfil = ? WHERE id = ?";
        try {
            super.executarUpdate(
                    sql,
                    usuario.getNome(),
                    usuario.getLogin(),
                    usuario.getSenha(),
                    usuario.getPerfil(),
                    usuario.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar usuário.", e);
        }
    }

    // Preparado para a proxima etapa: tela de exclusao de usuarios.
    public void deletar(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário.", e);
        }
    }
}
