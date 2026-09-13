package br.com.desafiosfitness.service;

import br.com.desafiosfitness.dao.DesafioDAO;
import br.com.desafiosfitness.model.Desafio;

import java.util.List;

/**
 * SERVICE inicial de Desafio, preparado para a proxima etapa do projeto.
 *
 * As regras de criacao/edicao/exclusao de desafios e de participacao dos
 * usuarios serao implementadas na proxima etapa, junto com o Controller completo.
 */
public class DesafioService {

    private final DesafioDAO desafioDAO;

    public DesafioService() {
        this.desafioDAO = new DesafioDAO();
    }

    public List<Desafio> listar() {
        return this.desafioDAO.listarTodos();
    }

    public Desafio buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.desafioDAO.buscarPorId(id);
    }
}
