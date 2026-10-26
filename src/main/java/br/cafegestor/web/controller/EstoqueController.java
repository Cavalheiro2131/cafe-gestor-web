package br.cafegestor.web.controller;

import br.cafegestor.web.dto.MovimentacaoRequest;
import br.cafegestor.web.dto.MovimentacaoResposta;
import br.cafegestor.web.dto.NovoSaldoResposta;
import br.cafegestor.web.dto.SaldoResposta;
import exception.RegraNegocioException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import model.Estoque;
import model.MovimentacaoEstoque;
import model.TipoCafe;
import model.TipoMovimentacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.EstoqueService;
import service.TipoCafeService;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {
    private final EstoqueService estoque;
    private final TipoCafeService tipos;

    public EstoqueController(EstoqueService estoque, TipoCafeService tipos) {
        this.estoque = estoque;
        this.tipos = tipos;
    }

    /** Saldo de todos os tipos de café, inclusive os que ainda não tiveram nenhuma entrada (saldo 0). */
    @GetMapping("/saldos")
    public List<SaldoResposta> saldos() {
        List<SaldoResposta> saldos = new ArrayList<>();
        for (TipoCafe tipo : tipos.listar()) {
            saldos.add(new SaldoResposta(tipo.getId(), tipo.getNome(), estoque.consultarSaldo(tipo.getId())));
        }
        return saldos;
    }

    /** Histórico de todos os tipos, da movimentação mais recente para a mais antiga. */
    @GetMapping("/historico")
    public List<MovimentacaoResposta> historico() {
        List<MovimentacaoEstoque> todas = new ArrayList<>();
        for (TipoCafe tipo : tipos.listar()) {
            todas.addAll(estoque.historico(tipo.getId()));
        }
        todas.sort(Comparator.comparing(MovimentacaoEstoque::getData)
                .thenComparingInt(MovimentacaoEstoque::getId).reversed());
        return todas.stream().map(MovimentacaoResposta::de).toList();
    }

    @PostMapping("/movimentacoes")
    public ResponseEntity<NovoSaldoResposta> movimentar(@RequestBody MovimentacaoRequest dados) {
        TipoMovimentacao tipo = interpretarTipo(dados.tipo());
        Estoque atualizado = tipo == TipoMovimentacao.ENTRADA
                ? estoque.registrarEntrada(dados.tipoCafeId(), dados.quantidade(), dados.descricao())
                : estoque.registrarSaida(dados.tipoCafeId(), dados.quantidade(), dados.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(new NovoSaldoResposta(atualizado.getQuantidadeSacas()));
    }

    static TipoMovimentacao interpretarTipo(String texto) {
        if (texto != null) {
            try {
                return TipoMovimentacao.valueOf(texto.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException erro) {
                // cai para a mensagem abaixo
            }
        }
        throw new RegraNegocioException("Tipo de movimentação inválido. Use ENTRADA ou SAIDA.");
    }
}
