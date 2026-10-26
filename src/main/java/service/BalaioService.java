package service;

import exception.RegraNegocioException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import model.Balaio;
import model.Funcionario;
import model.TipoCafe;
import repository.BalaioRepository;
import repository.FuncionarioRepository;
import repository.TipoCafeRepository;
import util.Validacoes;

/** Caso de uso "registrar produção de balaios". Não mexe no estoque: a conversão balaio -> saca é outra regra. */
public class BalaioService {
    private final BalaioRepository balaios;
    private final FuncionarioRepository funcionarios;
    private final TipoCafeRepository tipos;
    private final Clock relogio;

    public BalaioService(BalaioRepository balaios, FuncionarioRepository funcionarios,
                         TipoCafeRepository tipos, Clock relogio) {
        this.balaios = balaios;
        this.funcionarios = funcionarios;
        this.tipos = tipos;
        this.relogio = relogio;
    }

    public Balaio registrar(int funcionarioId, int tipoCafeId, int quantidade, LocalDate data) {
        Validacoes.inteiroPositivo(quantidade, "quantidade");
        if (data == null) {
            throw new RegraNegocioException("Informe a data do registro.");
        }
        if (data.isAfter(LocalDate.now(relogio))) {
            throw new RegraNegocioException("A data do registro não pode estar no futuro.");
        }
        Funcionario funcionario = funcionarios.buscarPorId(funcionarioId)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
        if (!funcionario.isAtivo()) {
            throw new RegraNegocioException("O funcionário " + funcionario.getNome() + " está inativo.");
        }
        TipoCafe tipoCafe = tipos.buscarPorId(tipoCafeId)
                .orElseThrow(() -> new RegraNegocioException("Tipo de café não encontrado."));
        return balaios.salvar(new Balaio(funcionario, tipoCafe, quantidade, data));
    }

    public List<Balaio> listar() {
        return balaios.listarTodos();
    }

    public int totalDoFuncionario(int funcionarioId) {
        return balaios.listarPorFuncionario(funcionarioId).stream().mapToInt(Balaio::getQuantidade).sum();
    }
}
