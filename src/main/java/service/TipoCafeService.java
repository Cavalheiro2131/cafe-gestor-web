package service;

import exception.RegraNegocioException;
import java.util.List;
import model.TipoCafe;
import repository.TipoCafeRepository;
import util.Validacoes;

/** Casos de uso de tipo de café (cadastro e consulta). */
public class TipoCafeService {
    private final TipoCafeRepository tipos;

    public TipoCafeService(TipoCafeRepository tipos) {
        this.tipos = tipos;
    }

    public TipoCafe cadastrar(String nome, String variedade, String observacoes) {
        return tipos.salvar(new TipoCafe(
                Validacoes.textoObrigatorio(nome, "nome", 50),
                Validacoes.textoOpcional(variedade, "variedade", 50),
                Validacoes.textoOpcional(observacoes, "observações", 1000)));
    }

    public TipoCafe buscar(int id) {
        return tipos.buscarPorId(id).orElseThrow(() -> new RegraNegocioException("Tipo de café não encontrado."));
    }

    public List<TipoCafe> listar() {
        return tipos.listarTodos();
    }
}
