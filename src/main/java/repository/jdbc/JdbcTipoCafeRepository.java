package repository.jdbc;

import java.util.List;
import java.util.Optional;
import model.TipoCafe;
import repository.TipoCafeRepository;

public class JdbcTipoCafeRepository extends JdbcBase implements TipoCafeRepository {
    private static final String SELECT = "SELECT " + Mapeadores.COLUNAS_TIPO_CAFE + " FROM tipo_cafe t";

    public JdbcTipoCafeRepository(FonteDeConexao fonte) {
        super(fonte);
    }

    @Override
    public TipoCafe salvar(TipoCafe t) {
        if (t.getId() == 0) {
            t.setId(inserir("INSERT INTO tipo_cafe (nome, variedade, observacoes) VALUES (?, ?, ?)",
                    t.getNome(), t.getVariedade(), t.getObservacoes()));
        } else {
            atualizar("UPDATE tipo_cafe SET nome = ?, variedade = ?, observacoes = ? WHERE id = ?",
                    t.getNome(), t.getVariedade(), t.getObservacoes(), t.getId());
        }
        return t;
    }

    @Override
    public Optional<TipoCafe> buscarPorId(int id) {
        return consultarUm(SELECT + " WHERE t.id = ?", Mapeadores::tipoCafe, id);
    }

    @Override
    public List<TipoCafe> listarTodos() {
        return consultar(SELECT + " ORDER BY t.nome", Mapeadores::tipoCafe);
    }
}
