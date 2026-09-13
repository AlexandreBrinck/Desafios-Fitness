package br.com.desafiosfitness.dao;

import br.com.desafiosfitness.model.Desafio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object (acesso ao banco)
 *
 * Somente SQL. A conversao ResultSet -> {@link Desafio} e feita por
 * mapearSimples(), herdado de {@link MysqlDAO}.
 */
public class DesafioDAO extends MysqlDAO {

    public DesafioDAO() {
        super();
    }

    public List<Desafio> listarTodos() {
        String sql = "SELECT id, nome, descricao, tipo_exercicio, meta, data_inicio, data_fim, criador_id "
                + "FROM desafios ORDER BY data_inicio DESC";
        List<Desafio> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Desafio.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar desafios.", e);
        }
        return lista;
    }

    public Desafio buscarPorId(Long id) {
        String sql = "SELECT id, nome, descricao, tipo_exercicio, meta, data_inicio, data_fim, criador_id "
                + "FROM desafios WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Desafio.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar desafio por id.", e);
        }
        return null;
    }

    public void inserir(Desafio desafio) {
        String sql = "INSERT INTO desafios (nome, descricao, tipo_exercicio, meta, data_inicio, data_fim, criador_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            super.executarUpdate(
                    sql,
                    desafio.getNome(),
                    desafio.getDescricao(),
                    desafio.getTipoExercicio(),
                    desafio.getMeta(),
                    desafio.getDataInicio(),
                    desafio.getDataFim(),
                    desafio.getCriadorId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir desafio.", e);
        }
    }

    public void alterar(Desafio desafio) {
        String sql = "UPDATE desafios SET nome = ?, descricao = ?, tipo_exercicio = ?, meta = ?, "
                + "data_inicio = ?, data_fim = ? WHERE id = ?";
        try {
            super.executarUpdate(
                    sql,
                    desafio.getNome(),
                    desafio.getDescricao(),
                    desafio.getTipoExercicio(),
                    desafio.getMeta(),
                    desafio.getDataInicio(),
                    desafio.getDataFim(),
                    desafio.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar desafio.", e);
        }
    }

    /**
     * Exclui o desafio. Participacoes e registros de progresso relacionados
     * sao removidos automaticamente pelo banco (ON DELETE CASCADE no init.sql).
     */
    public void deletar(Long id) {
        String sql = "DELETE FROM desafios WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar desafio.", e);
        }
    }
}
