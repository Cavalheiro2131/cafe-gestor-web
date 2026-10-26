package repository;

import java.util.List;
import model.Balaio;

public interface BalaioRepository extends Repositorio<Balaio> {
    List<Balaio> listarPorFuncionario(int funcionarioId);
}
