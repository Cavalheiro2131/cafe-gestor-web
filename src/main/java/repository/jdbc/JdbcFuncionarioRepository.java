package repository.jdbc;

import java.util.List;
import java.util.Optional;
import model.Funcionario;
import repository.FuncionarioRepository;

public class JdbcFuncionarioRepository extends JdbcBase implements FuncionarioRepository {
    private static final String SELECT = "SELECT " + Mapeadores.COLUNAS_FUNCIONARIO + " FROM funcionario f";

    public JdbcFuncionarioRepository(FonteDeConexao fonte) {
        super(fonte);
    }

    @Override
    public Funcionario salvar(Funcionario f) {
        if (f.getId() == 0) {
            f.setId(inserir("INSERT INTO funcionario (nome, funcao, contato, status) VALUES (?, ?, ?, ?)",
                    f.getNome(), f.getFuncao(), f.getContato(), f.isAtivo()));
        } else {
            atualizar("UPDATE funcionario SET nome = ?, funcao = ?, contato = ?, status = ? WHERE id = ?",
                    f.getNome(), f.getFuncao(), f.getContato(), f.isAtivo(), f.getId());
        }
        return f;
    }

    @Override
    public Optional<Funcionario> buscarPorId(int id) {
        return consultarUm(SELECT + " WHERE f.id = ?", Mapeadores::funcionario, id);
    }

    @Override
    public List<Funcionario> listarTodos() {
        return consultar(SELECT + " ORDER BY f.nome", Mapeadores::funcionario);
    }
}
