package repository.jdbc;

import java.util.List;
import java.util.Optional;
import model.MovimentacaoEstoque;
import model.TipoMovimentacao;
import repository.MovimentacaoRepository;

public class JdbcMovimentacaoRepository extends JdbcBase implements MovimentacaoRepository {
    private static final String SELECT = "SELECT m.id, m.tipo, m.quantidade, m.data, m.destino, "
            + Mapeadores.COLUNAS_TIPO_CAFE
            + " FROM movimentacao_estoque m JOIN tipo_cafe t ON t.id = m.tipo_cafe_id";
    private static final MapeadorLinha<MovimentacaoEstoque> MAPEADOR = rs -> new MovimentacaoEstoque(
            rs.getInt("id"), TipoMovimentacao.valueOf(rs.getString("tipo")), rs.getInt("quantidade"),
            rs.getDate("data").toLocalDate(), rs.getString("destino"), Mapeadores.tipoCafe(rs));

    public JdbcMovimentacaoRepository(FonteDeConexao fonte) {
        super(fonte);
    }

    @Override
    public MovimentacaoEstoque salvar(MovimentacaoEstoque m) {
        if (m.getId() != 0) {
            throw new UnsupportedOperationException("O histórico de movimentações não pode ser alterado.");
        }
        m.setId(inserir("INSERT INTO movimentacao_estoque (tipo, quantidade, data, destino, tipo_cafe_id) VALUES (?, ?, ?, ?, ?)",
                m.getTipo(), m.getQuantidade(), m.getData(), m.getDestino(), m.getTipoCafe().getId()));
        return m;
    }

    @Override
    public Optional<MovimentacaoEstoque> buscarPorId(int id) {
        return consultarUm(SELECT + " WHERE m.id = ?", MAPEADOR, id);
    }

    @Override
    public List<MovimentacaoEstoque> listarTodos() {
        return consultar(SELECT + " ORDER BY m.data DESC, m.id DESC", MAPEADOR);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorTipoCafe(int tipoCafeId) {
        return consultar(SELECT + " WHERE m.tipo_cafe_id = ? ORDER BY m.data, m.id", MAPEADOR, tipoCafeId);
    }
}
