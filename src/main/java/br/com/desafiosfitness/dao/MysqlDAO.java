package br.com.desafiosfitness.dao;

import br.com.desafiosfitness.config.MysqlSingleton;
import br.com.desafiosfitness.model.Mapeavel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe base dos DAOs que usam MySQL.
 *
 * Guarda referencia ao {@link MysqlSingleton} (uma conexao reutilizada).
 * Os DAOs filhos usam executar() para SELECT e executarUpdate() para INSERT/UPDATE/DELETE.
 *
 * mapearSimples() converte uma linha do ResultSet direto para um Model, desde
 * que o Model implemente {@link Mapeavel} e saiba se preencher sozinho — assim
 * os DAOs filhos nao precisam repetir um metodo "mapear" manual para cada entidade.
 */
public class MysqlDAO {

    protected final MysqlSingleton banco;

    public MysqlDAO() {
        this.banco = MysqlSingleton.getInstance();
    }

    protected ResultSet executar(String sql, Object... parametros) throws SQLException {
        return this.banco.executar(sql, parametros);
    }

    protected int executarUpdate(String sql, Object... parametros) throws SQLException {
        return this.banco.executarUpdate(sql, parametros);
    }

    protected <T extends Mapeavel> T mapearSimples(ResultSet rs, Class<T> classe) {
        try {
            T objeto = classe.getDeclaredConstructor().newInstance();
            objeto.preencher(rs);
            return objeto;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear objeto: " + e.getMessage(), e);
        }
    }
}
