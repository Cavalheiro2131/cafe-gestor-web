package repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import model.Identificavel;
import repository.Repositorio;

/** Base das implementações em memória: usadas nos testes do main() e para demonstrar a troca de persistência (LSP/DIP). */
public abstract class RepositorioEmMemoria<T extends Identificavel> implements Repositorio<T> {
    private final Map<Integer, T> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public T salvar(T entidade) {
        if (entidade.getId() == 0) {
            entidade.setId(proximoId++);
        }
        dados.put(entidade.getId(), entidade);
        return entidade;
    }

    @Override
    public Optional<T> buscarPorId(int id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    protected Stream<T> todos() {
        return dados.values().stream();
    }
}
