package repository;

import java.util.List;
import java.util.Optional;

/** Operações básicas de persistência comuns a todas as entidades (padrão Repository). */
public interface Repositorio<T> {
    /** Insere (id == 0) ou atualiza a entidade e a devolve com o id preenchido. */
    T salvar(T entidade);

    Optional<T> buscarPorId(int id);

    List<T> listarTodos();
}
