package repository.memory;

import java.util.List;
import java.util.stream.Collectors;
import model.MovimentacaoEstoque;
import repository.MovimentacaoRepository;

public class MemoriaMovimentacaoRepository extends RepositorioEmMemoria<MovimentacaoEstoque> implements MovimentacaoRepository {
    @Override
    public List<MovimentacaoEstoque> listarPorTipoCafe(int tipoCafeId) {
        return todos().filter(m -> m.getTipoCafe().getId() == tipoCafeId).collect(Collectors.toList());
    }
}
