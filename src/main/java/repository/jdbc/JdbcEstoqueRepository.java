package repository.jdbc;

import java.util.List;
import java.util.Optional;
import model.Estoque;
import repository.EstoqueRepository;

public class JdbcEstoqueRepository extends JdbcBase implements EstoqueRepository {
    private static final String SELECT = "SELECT e.id, e.quantidade_sacas, " + Mapeadores.COLUNAS_TIPO_CAFE
            + " FROM estoque e JOIN tipo_cafe t ON t.id = e.tipo_cafe_id";
    private static final MapeadorLinha<Estoque> MAPEADOR = rs -> new Estoque(rs.getInt("id"),
            Mapeadores.tipoCafe(rs), rs.getInt("quantidade_sacas"));

    public JdbcEstoqueRepository(FonteDeConexao fonte) {
        super(fonte);
    }

    @Override
    public Estoque salvar(Estoque e) {
        if (e.getId() == 0) {
            e.setId(inserir("INSERT INTO estoque (tipo_cafe_id, quantidade_sacas) VALUES (?, ?)",
                    e.getTipoCafe().getId(), e.getQuantidadeSacas()));
        } else {
            atualizar("UPDATE estoque SET quantidade_sacas = ? WHERE id = ?", e.getQuantidadeSacas(), e.getId());
        }
        return e;
    }

    @Override
    public Optional<Estoque> buscarPorId(int id) {
        return consultarUm(SELECT + " WHERE e.id = ?", MAPEADOR, id);
    }

    @Override
    public Optional<Estoque> buscarPorTipoCafe(int tipoCafeId) {
        return consultarUm(SELECT + " WHERE e.tipo_cafe_id = ?", MAPEADOR, tipoCafeId);
    }

    @Override
    public List<Estoque> listarTodos() {
        return consultar(SELECT + " ORDER BY t.nome", MAPEADOR);
    }
}
