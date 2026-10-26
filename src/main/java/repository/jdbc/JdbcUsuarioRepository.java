package repository.jdbc;

import java.util.List;
import java.util.Optional;
import model.PerfilUsuario;
import model.Usuario;
import repository.UsuarioRepository;

public class JdbcUsuarioRepository extends JdbcBase implements UsuarioRepository {
    private static final String SELECT = "SELECT id, nome, login, senha, tipo FROM usuario";
    private static final MapeadorLinha<Usuario> MAPEADOR = rs -> new Usuario(rs.getInt("id"),
            rs.getString("nome"), rs.getString("login"), rs.getString("senha"),
            PerfilUsuario.valueOf(rs.getString("tipo")));

    public JdbcUsuarioRepository(FonteDeConexao fonte) {
        super(fonte);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == 0) {
            usuario.setId(inserir("INSERT INTO usuario (nome, login, senha, tipo) VALUES (?, ?, ?, ?)",
                    usuario.getNome(), usuario.getLogin(), usuario.getSenhaCodificada(), usuario.getPerfil()));
        } else {
            atualizar("UPDATE usuario SET nome = ?, senha = ?, tipo = ? WHERE id = ?",
                    usuario.getNome(), usuario.getSenhaCodificada(), usuario.getPerfil(), usuario.getId());
        }
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return consultarUm(SELECT + " WHERE id = ?", MAPEADOR, id);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return consultarUm(SELECT + " WHERE login = ?", MAPEADOR, login);
    }

    @Override
    public List<Usuario> listarTodos() {
        return consultar(SELECT + " ORDER BY nome", MAPEADOR);
    }
}
