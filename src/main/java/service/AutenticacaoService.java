package service;

import exception.RegraNegocioException;
import java.util.List;
import model.PerfilUsuario;
import model.Usuario;
import repository.UsuarioRepository;
import security.CodificadorSenha;
import util.Validacoes;

/** Casos de uso de usuário: login, cadastro e troca de senha (antes em Usuario.autenticar() e na tela de login). */
public class AutenticacaoService {
    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarios;
    private final CodificadorSenha codificador;

    public AutenticacaoService(UsuarioRepository usuarios, CodificadorSenha codificador) {
        this.usuarios = usuarios;
        this.codificador = codificador;
    }

    public Usuario autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            throw new RegraNegocioException("Informe o login e a senha.");
        }
        // Mesma mensagem para usuário inexistente e senha errada: não revela quais logins existem.
        return usuarios.buscarPorLogin(login.trim())
                .filter(u -> codificador.confere(senha, u.getSenhaCodificada()))
                .orElseThrow(() -> new RegraNegocioException("Login ou senha inválidos."));
    }

    public Usuario cadastrar(String nome, String login, String senha, PerfilUsuario perfil) {
        String nomeValido = Validacoes.textoObrigatorio(nome, "nome", 100);
        String loginValido = Validacoes.textoObrigatorio(login, "login", 50);
        validarSenha(senha);
        if (perfil == null) {
            throw new RegraNegocioException("Informe o perfil do usuário.");
        }
        if (usuarios.buscarPorLogin(loginValido).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário com o login " + loginValido + ".");
        }
        return usuarios.salvar(new Usuario(nomeValido, loginValido, codificador.codificar(senha), perfil));
    }

    public void alterarSenha(int usuarioId, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarios.buscarPorId(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));
        if (senhaAtual == null || !codificador.confere(senhaAtual, usuario.getSenhaCodificada())) {
            throw new RegraNegocioException("A senha atual está incorreta.");
        }
        validarSenha(novaSenha);
        usuario.setSenhaCodificada(codificador.codificar(novaSenha));
        usuarios.salvar(usuario);
    }

    public List<Usuario> listar() {
        return usuarios.listarTodos();
    }

    private static void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }
}
