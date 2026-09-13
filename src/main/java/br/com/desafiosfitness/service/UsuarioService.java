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

    /**
     * Regra de autenticacao:
     * - login e senha obrigatorios
     * - so libera acesso se existir usuario com esse login/senha
     */
    public Usuario autenticar(String login, String senha) {
        login = this.normalizar(login);
        senha = this.normalizar(senha);

        if (login == null || senha == null) {
            throw new IllegalArgumentException("Informe login e senha.");
        }

        Usuario usuario = this.usuarioDAO.buscarPorLogin(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Login ou senha inválidos.");
        }
        return usuario;
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
            throw new IllegalArgumentException("Usuário é obrigatório.");
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
            throw new IllegalArgumentException("Usuário não encontrado para alteração.");
        }
        this.usuarioDAO.alterar(usuario);
    }

    // Preparado para a proxima etapa: tela de exclusao de usuarios.
    public void deletar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id é obrigatório para excluir.");
        }
        if (this.usuarioDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
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
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (usuario.getLogin() == null) {
            throw new IllegalArgumentException("Login é obrigatório.");
        }
        if (usuario.getSenha() == null) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (usuario.getPerfil() == null) {
            throw new IllegalArgumentException("Perfil é obrigatório.");
        }
    }

    private void validarSenha(String senha) {
        if (senha.length() < SENHA_MINIMA) {
            throw new IllegalArgumentException("Senha deve ter no mínimo " + SENHA_MINIMA + " caracteres.");
        }
    }

    private void validarLoginUnico(Usuario usuario) {
        Usuario existente = this.usuarioDAO.buscarPorLogin(usuario.getLogin());
        if (existente == null) {
            return;
        }
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("Já existe um usuário com este login.");
        }
        if (!existente.getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Já existe um usuário com este login.");
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
