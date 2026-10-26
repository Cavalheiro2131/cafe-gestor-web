package br.cafegestor.web.controller;

import br.cafegestor.web.dto.ResumoResposta;
import model.Balaio;
import model.TipoCafe;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.BalaioService;
import service.EstoqueService;
import service.FuncionarioService;
import service.TipoCafeService;

/** Números da tela inicial. */
@RestController
@RequestMapping("/api/resumo")
public class ResumoController {
    private final FuncionarioService funcionarios;
    private final BalaioService balaios;
    private final EstoqueService estoque;
    private final TipoCafeService tipos;

    public ResumoController(FuncionarioService funcionarios, BalaioService balaios,
                            EstoqueService estoque, TipoCafeService tipos) {
        this.funcionarios = funcionarios;
        this.balaios = balaios;
        this.estoque = estoque;
        this.tipos = tipos;
    }

    @GetMapping
    public ResumoResposta resumo() {
        int totalBalaios = balaios.listar().stream().mapToInt(Balaio::getQuantidade).sum();
        int totalSacas = 0;
        for (TipoCafe tipo : tipos.listar()) {
            totalSacas += estoque.consultarSaldo(tipo.getId());
        }
        return new ResumoResposta(funcionarios.listarAtivos().size(), totalBalaios, totalSacas);
    }
}
