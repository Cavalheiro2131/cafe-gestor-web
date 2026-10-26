package repository;

import java.util.Optional;
import model.Usuario;

public interface UsuarioRepository extends Repositorio<Usuario> {
    Optional<Usuario> buscarPorLogin(String login);
}
