package repository.memory;

import java.util.Optional;
import model.Usuario;
import repository.UsuarioRepository;

public class MemoriaUsuarioRepository extends RepositorioEmMemoria<Usuario> implements UsuarioRepository {
    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return todos().filter(u -> u.getLogin().equalsIgnoreCase(login)).findFirst();
    }
}
