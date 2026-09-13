package br.com.desafiosfitness.dao;

import br.com.desafiosfitness.model.Progresso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object (acesso ao banco)
 *
 * Somente SQL. A conversao ResultSet -> {@link Progresso} e feita por
 * mapearSimples(), herdado de {@link MysqlDAO}.
 */
public class ProgressoDAO extends MysqlDAO {

    public ProgressoDAO() {
        super();
    }

    public List<Progresso> listarPorUsuario(Long usuarioId) {
        String sql = "SELECT id, usuario_id, desafio_id, data_registro, valor_atingido, observacao "
                + "FROM progresso WHERE usuario_id = ? ORDER BY data_registro DESC";
        List<Progresso> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, usuarioId)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Progresso.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar progresso do usuário.", e);
        }
        return lista;
    }

    public List<Progresso> listarPorDesafio(Long desafioId) {
        String sql = "SELECT id, usuario_id, desafio_id, data_registro, valor_atingido, observacao "
                + "FROM progresso WHERE desafio_id = ? ORDER BY data_registro DESC";
        List<Progresso> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, desafioId)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Progresso.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar progresso do desafio.", e);
        }
        return lista;
    }

    public Progresso buscarPorId(Long id) {
        String sql = "SELECT id, usuario_id, desafio_id, data_registro, valor_atingido, observacao "
                + "FROM progresso WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Progresso.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar progresso por id.", e);
        }
        return null;
    }

    public void inserir(Progresso progresso) {
        String sql = "INSERT INTO progresso (usuario_id, desafio_id, data_registro, valor_atingido, observacao) "
                + "VALUES (?, ?, ?, ?, ?)";
        try {
            super.executarUpdate(
                    sql,
                    progresso.getUsuarioId(),
                    progresso.getDesafioId(),
                    progresso.getDataRegistro(),
                    progresso.getValorAtingido(),
                    progresso.getObservacao());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir progresso.", e);
        }
    }

    public void alterar(Progresso progresso) {
        String sql = "UPDATE progresso SET data_registro = ?, valor_atingido = ?, observacao = ? WHERE id = ?";
        try {
            super.executarUpdate(
                    sql,
                    progresso.getDataRegistro(),
                    progresso.getValorAtingido(),
                    progresso.getObservacao(),
                    progresso.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar progresso.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM progresso WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar progresso.", e);
        }
    }
}
