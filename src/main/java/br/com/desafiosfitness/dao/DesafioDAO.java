package br.com.desafiosfitness.dao;

import br.com.desafiosfitness.model.Desafio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO inicial de Desafio, preparado para a proxima etapa do projeto.
 *
 * Nesta etapa so ha consulta (listar/buscar), o suficiente para a estrutura
 * de navegacao existir. inserir/alterar/deletar e as regras de participacao
 * ficam para a proxima etapa, junto com o Controller completo.
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
}
