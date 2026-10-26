package br.cafegestor.web.controller;

import br.cafegestor.web.dto.TipoCafeRequest;
import br.cafegestor.web.dto.TipoCafeResposta;
import java.util.List;
import model.TipoCafe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.TipoCafeService;

@RestController
@RequestMapping("/api/tipos-cafe")
public class TipoCafeController {
    private final TipoCafeService tipos;

    public TipoCafeController(TipoCafeService tipos) {
        this.tipos = tipos;
    }

    @GetMapping
    public List<TipoCafeResposta> listar() {
        return tipos.listar().stream().map(TipoCafeResposta::de).toList();
    }

    @PostMapping
    public ResponseEntity<TipoCafeResposta> criar(@RequestBody TipoCafeRequest dados) {
        TipoCafe criado = tipos.cadastrar(dados.nome(), dados.variedade(), dados.observacoes());
        return ResponseEntity.status(HttpStatus.CREATED).body(TipoCafeResposta.de(criado));
    }
}
