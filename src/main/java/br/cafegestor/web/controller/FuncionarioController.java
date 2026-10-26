package br.cafegestor.web.controller;

import br.cafegestor.web.dto.FuncionarioRequest;
import br.cafegestor.web.dto.FuncionarioResposta;
import br.cafegestor.web.dto.StatusRequest;
import java.util.List;
import model.Funcionario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.FuncionarioService;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {
    private final FuncionarioService funcionarios;

    public FuncionarioController(FuncionarioService funcionarios) {
        this.funcionarios = funcionarios;
    }

    @GetMapping
    public List<FuncionarioResposta> listar() {
        return funcionarios.listar().stream().map(FuncionarioResposta::de).toList();
    }

    @GetMapping("/{id}")
    public FuncionarioResposta buscar(@PathVariable("id") int id) {
        return FuncionarioResposta.de(funcionarios.buscar(id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioResposta> criar(@RequestBody FuncionarioRequest dados) {
        Funcionario criado = funcionarios.cadastrar(dados.nome(), dados.funcao(), dados.contato());
        return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResposta.de(criado));
    }

    @PutMapping("/{id}")
    public FuncionarioResposta atualizar(@PathVariable("id") int id, @RequestBody FuncionarioRequest dados) {
        return FuncionarioResposta.de(funcionarios.editar(id, dados.nome(), dados.funcao(), dados.contato()));
    }

    @PatchMapping("/{id}/status")
    public FuncionarioResposta alterarStatus(@PathVariable("id") int id, @RequestBody StatusRequest dados) {
        if (dados.ativo()) {
            funcionarios.reativar(id);
        } else {
            funcionarios.inativar(id);
        }
        return FuncionarioResposta.de(funcionarios.buscar(id));
    }
}
