package br.com.desafiosfitness.service;

import br.com.desafiosfitness.dao.UsuarioDAO;
import br.com.desafiosfitness.model.Usuario;

import java.util.List;

/**
 * SERVICE de Usuario — regras de negocio ficam aqui.
 *
 * Controller so chama estes metodos e decide a view.
 * DAO so executa SQL.
 */
public class UsuarioService {

    private static final int SENHA_MINIMA = 6;

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> listar() {
        return this.usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.usuarioDAO.buscarPorId(id);
    }

    /**
     * Regra de salvamento:
     * - sem id -> cadastro novo
     * - com id -> alteracao (usuario precisa existir) — preparado para a proxima etapa,
     *   ja que o cadastro desta etapa so envia formularios sem id.
     */
    public void salvar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario e obrigatorio.");
        }

        this.prepararDados(usuario);
        this.validarCamposObrigatorios(usuario);
        this.validarSenha(usuario.getSenha());
        this.validarLoginUnico(usuario);

        if (usuario.getId() == null) {
            this.usuarioDAO.inserir(usuario);
            return;
        }

        if (this.usuarioDAO.buscarPorId(usuario.getId()) == null) {
            throw new IllegalArgumentException("Usuario nao encontrado para alteracao.");
        }
        this.usuarioDAO.alterar(usuario);
    }

    // Preparado para a proxima etapa: tela de exclusao de usuarios.
    public void deletar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id e obrigatorio para excluir.");
        }
        if (this.usuarioDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Usuario nao encontrado.");
        }
        this.usuarioDAO.deletar(id);
    }

    private void prepararDados(Usuario usuario) {
        usuario.setNome(this.normalizar(usuario.getNome()));
        usuario.setLogin(this.normalizar(usuario.getLogin()));
        usuario.setSenha(this.normalizar(usuario.getSenha()));
        usuario.setPerfil(this.normalizar(usuario.getPerfil()));
    }

    private void validarCamposObrigatorios(Usuario usuario) {
        if (usuario.getNome() == null) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (usuario.getLogin() == null) {
            throw new IllegalArgumentException("Login e obrigatorio.");
        }
        if (usuario.getSenha() == null) {
            throw new IllegalArgumentException("Senha e obrigatoria.");
        }
        if (usuario.getPerfil() == null) {
            throw new IllegalArgumentException("Perfil e obrigatorio.");
        }
    }

    private void validarSenha(String senha) {
        if (senha.length() < SENHA_MINIMA) {
            throw new IllegalArgumentException("Senha deve ter no minimo " + SENHA_MINIMA + " caracteres.");
        }
    }

    private void validarLoginUnico(Usuario usuario) {
        Usuario existente = this.usuarioDAO.buscarPorLogin(usuario.getLogin());
        if (existente == null) {
            return;
        }
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("Ja existe um usuario com este login.");
        }
        if (!existente.getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ja existe um usuario com este login.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
