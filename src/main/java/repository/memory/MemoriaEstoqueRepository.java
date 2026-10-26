package repository.memory;

import java.util.Optional;
import model.Estoque;
import repository.EstoqueRepository;

public class MemoriaEstoqueRepository extends RepositorioEmMemoria<Estoque> implements EstoqueRepository {
    @Override
    public Optional<Estoque> buscarPorTipoCafe(int tipoCafeId) {
        return todos().filter(e -> e.getTipoCafe().getId() == tipoCafeId).findFirst();
    }
}
