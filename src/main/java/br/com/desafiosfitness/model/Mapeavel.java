package br.com.desafiosfitness.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contrato para classes que sabem se preencher a partir de um ResultSet.
 * Usado pelo MysqlDAO#mapearSimples para evitar mapeamento manual repetido em cada DAO.
 */
public interface Mapeavel {

    void preencher(ResultSet rs) throws SQLException;
}
