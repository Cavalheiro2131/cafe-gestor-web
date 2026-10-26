package repository;

import java.util.Optional;
import model.Estoque;

public interface EstoqueRepository extends Repositorio<Estoque> {
    Optional<Estoque> buscarPorTipoCafe(int tipoCafeId);
}
