package br.com.desafiosfitness.dao;

import br.com.desafiosfitness.model.Desafio;
import br.com.desafiosfitness.model.Participacao;
import br.com.desafiosfitness.model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da participacao de usuarios em desafios (tabela "participacoes").
 *
 * As consultas com JOIN continuam usando mapearSimples(), pois selecionam
 * exatamente as colunas que Usuario/Desafio ja sabem ler de um ResultSet.
 */
public class ParticipacaoDAO extends MysqlDAO {

    public ParticipacaoDAO() {
        super();
    }

    public boolean existeParticipacao(Long usuarioId, Long desafioId) {
        String sql = "SELECT COUNT(*) AS total FROM participacoes WHERE usuario_id = ? AND desafio_id = ?";
        try (ResultSet rs = super.executar(sql, usuarioId, desafioId)) {
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar participação.", e);
        }
        return false;
    }

    public void inserir(Participacao participacao) {
        String sql = "INSERT INTO participacoes (usuario_id, desafio_id, data_participacao) VALUES (?, ?, ?)";
        try {
            super.executarUpdate(
                    sql,
                    participacao.getUsuarioId(),
                    participacao.getDesafioId(),
                    participacao.getDataParticipacao());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar participação.", e);
        }
    }

    public List<Desafio> listarDesafiosDoUsuario(Long usuarioId) {
        String sql = "SELECT d.id, d.nome, d.descricao, d.tipo_exercicio, d.meta, d.data_inicio, d.data_fim, d.criador_id "
                + "FROM desafios d "
                + "INNER JOIN participacoes p ON p.desafio_id = d.id "
                + "WHERE p.usuario_id = ? "
                + "ORDER BY d.data_inicio DESC";
        List<Desafio> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, usuarioId)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Desafio.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar desafios do usuário.", e);
        }
        return lista;
    }

    public void deletar(Long usuarioId, Long desafioId) {
        String sql = "DELETE FROM participacoes WHERE usuario_id = ? AND desafio_id = ?";
        try {
            super.executarUpdate(sql, usuarioId, desafioId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover participação.", e);
        }
    }

    public List<Usuario> listarParticipantes(Long desafioId) {
        String sql = "SELECT u.id, u.nome, u.login, u.senha, u.perfil "
                + "FROM usuarios u "
                + "INNER JOIN participacoes p ON p.usuario_id = u.id "
                + "WHERE p.desafio_id = ? "
                + "ORDER BY u.nome";
        List<Usuario> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, desafioId)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Usuario.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar participantes do desafio.", e);
        }
        return lista;
    }
}
