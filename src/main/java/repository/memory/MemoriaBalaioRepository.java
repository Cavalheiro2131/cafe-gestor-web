package repository.memory;

import java.util.List;
import java.util.stream.Collectors;
import model.Balaio;
import repository.BalaioRepository;

public class MemoriaBalaioRepository extends RepositorioEmMemoria<Balaio> implements BalaioRepository {
    @Override
    public List<Balaio> listarPorFuncionario(int funcionarioId) {
        return todos().filter(b -> b.getFuncionario().getId() == funcionarioId).collect(Collectors.toList());
    }
}
