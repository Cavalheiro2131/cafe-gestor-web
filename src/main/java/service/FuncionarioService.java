package service;

import exception.RegraNegocioException;
import java.util.List;
import java.util.stream.Collectors;
import model.Funcionario;
import repository.FuncionarioRepository;
import util.Validacoes;

/** Casos de uso de funcionário: cadastrar, editar, ativar/inativar e consultar. */
public class FuncionarioService {
    private final FuncionarioRepository funcionarios;

    public FuncionarioService(FuncionarioRepository funcionarios) {
        this.funcionarios = funcionarios;
    }

    public Funcionario cadastrar(String nome, String funcao, String contato) {
        return funcionarios.salvar(new Funcionario(
                Validacoes.textoObrigatorio(nome, "nome", 100),
                Validacoes.textoObrigatorio(funcao, "função", 50),
                Validacoes.textoObrigatorio(contato, "contato", 50)));
    }

    public Funcionario editar(int id, String nome, String funcao, String contato) {
        Funcionario funcionario = buscar(id);
        funcionario.atualizarDados(
                Validacoes.textoObrigatorio(nome, "nome", 100),
                Validacoes.textoObrigatorio(funcao, "função", 50),
                Validacoes.textoObrigatorio(contato, "contato", 50));
        return funcionarios.salvar(funcionario);
    }

    public void inativar(int id) {
        alterarStatus(id, false);
    }

    public void reativar(int id) {
        alterarStatus(id, true);
    }

    public Funcionario buscar(int id) {
        return funcionarios.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
    }

    public List<Funcionario> listar() {
        return funcionarios.listarTodos();
    }

    public List<Funcionario> listarAtivos() {
        return funcionarios.listarTodos().stream().filter(Funcionario::isAtivo).collect(Collectors.toList());
    }

    private void alterarStatus(int id, boolean ativo) {
        Funcionario funcionario = buscar(id);
        funcionario.setAtivo(ativo);
        funcionarios.salvar(funcionario);
    }
}
