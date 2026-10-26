package repository.jdbc;

import java.util.List;
import java.util.Optional;
import model.Balaio;
import repository.BalaioRepository;

public class JdbcBalaioRepository extends JdbcBase implements BalaioRepository {
    private static final String SELECT = "SELECT b.id, b.data, b.quantidade, "
            + Mapeadores.COLUNAS_FUNCIONARIO + ", " + Mapeadores.COLUNAS_TIPO_CAFE
            + " FROM balaio b JOIN funcionario f ON f.id = b.funcionario_id"
            + " JOIN tipo_cafe t ON t.id = b.tipo_cafe_id";
    private static final MapeadorLinha<Balaio> MAPEADOR = rs -> new Balaio(rs.getInt("id"),
            Mapeadores.funcionario(rs), Mapeadores.tipoCafe(rs), rs.getInt("quantidade"),
            rs.getDate("data").toLocalDate());

    public JdbcBalaioRepository(FonteDeConexao fonte) {
        super(fonte);
    }

    @Override
    public Balaio salvar(Balaio b) {
        if (b.getId() == 0) {
            b.setId(inserir("INSERT INTO balaio (data, quantidade, funcionario_id, tipo_cafe_id) VALUES (?, ?, ?, ?)",
                    b.getData(), b.getQuantidade(), b.getFuncionario().getId(), b.getTipoCafe().getId()));
        } else {
            atualizar("UPDATE balaio SET data = ?, quantidade = ?, funcionario_id = ?, tipo_cafe_id = ? WHERE id = ?",
                    b.getData(), b.getQuantidade(), b.getFuncionario().getId(), b.getTipoCafe().getId(), b.getId());
        }
        return b;
    }

    @Override
    public Optional<Balaio> buscarPorId(int id) {
        return consultarUm(SELECT + " WHERE b.id = ?", MAPEADOR, id);
    }

    @Override
    public List<Balaio> listarTodos() {
        return consultar(SELECT + " ORDER BY b.data DESC, b.id DESC", MAPEADOR);
    }

    @Override
    public List<Balaio> listarPorFuncionario(int funcionarioId) {
        return consultar(SELECT + " WHERE b.funcionario_id = ? ORDER BY b.data DESC, b.id DESC", MAPEADOR, funcionarioId);
    }
}
