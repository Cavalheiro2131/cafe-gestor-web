package repository;

import java.util.List;
import model.MovimentacaoEstoque;

public interface MovimentacaoRepository extends Repositorio<MovimentacaoEstoque> {
    List<MovimentacaoEstoque> listarPorTipoCafe(int tipoCafeId);
}
