package br.cafegestor.web.controller;

import br.cafegestor.web.dto.BalaioRequest;
import br.cafegestor.web.dto.BalaioResposta;
import exception.RegraNegocioException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import model.Balaio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.BalaioService;

@RestController
@RequestMapping("/api/balaios")
public class BalaioController {
    private final BalaioService balaios;

    public BalaioController(BalaioService balaios) {
        this.balaios = balaios;
    }

    @GetMapping
    public List<BalaioResposta> listar() {
        return balaios.listar().stream().map(BalaioResposta::de).toList();
    }

    @PostMapping
    public ResponseEntity<BalaioResposta> registrar(@RequestBody BalaioRequest dados) {
        Balaio criado = balaios.registrar(dados.funcionarioId(), dados.tipoCafeId(), dados.quantidade(),
                interpretarData(dados.data()));
        return ResponseEntity.status(HttpStatus.CREATED).body(BalaioResposta.de(criado));
    }

    /** Data ISO (aaaa-mm-dd). Texto vazio vira null, e o serviço do núcleo responde "Informe a data do registro.". */
    static LocalDate interpretarData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException erro) {
            throw new RegraNegocioException("O campo data deve ser uma data válida.");
        }
    }
}
