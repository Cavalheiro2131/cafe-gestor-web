package br.cafegestor.web.controller;

import br.cafegestor.web.dto.SimulacaoResposta;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.CalculadoraProducao;

/** Expõe a CalculadoraProducao (cálculo puro criado na Etapa 7) para o simulador da tela inicial. */
@RestController
@RequestMapping("/api/producao")
public class ProducaoController {

    @GetMapping("/simulacao")
    public SimulacaoResposta simular(@RequestParam("balaios") int balaios,
                                     @RequestParam("balaiosPorSaca") int balaiosPorSaca) {
        CalculadoraProducao calculadora = new CalculadoraProducao(balaiosPorSaca);
        return new SimulacaoResposta(balaios, balaiosPorSaca,
                calculadora.sacasCompletas(balaios), calculadora.balaiosRestantes(balaios));
    }
}
