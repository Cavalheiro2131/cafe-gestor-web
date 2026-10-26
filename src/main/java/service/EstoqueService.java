package service;

import exception.RegraNegocioException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import model.Estoque;
import model.MovimentacaoEstoque;
import model.TipoCafe;
import model.TipoMovimentacao;
import repository.EstoqueRepository;
import repository.MovimentacaoRepository;
import repository.TipoCafeRepository;
import util.Validacoes;

/**
 * Casos de uso de estoque. Uma única rotina privada atende entrada e saída
 * (antes eram dois handlers de botão quase idênticos).
 */
public class EstoqueService {
    private final EstoqueRepository estoques;
    private final MovimentacaoRepository movimentacoes;
    private final TipoCafeRepository tipos;
    private final Clock relogio;

    public EstoqueService(EstoqueRepository estoques, MovimentacaoRepository movimentacoes,
                          TipoCafeRepository tipos, Clock relogio) {
        this.estoques = estoques;
        this.movimentacoes = movimentacoes;
        this.tipos = tipos;
        this.relogio = relogio;
    }

    /** @param nota observação opcional da entrada */
    public Estoque registrarEntrada(int tipoCafeId, int quantidade, String nota) {
        return registrar(TipoMovimentacao.ENTRADA, tipoCafeId, quantidade,
                Validacoes.textoOpcional(nota, "nota", 100));
    }

    /** @param destino para onde as sacas foram (obrigatório), por exemplo "Venda" */
    public Estoque registrarSaida(int tipoCafeId, int quantidade, String destino) {
        return registrar(TipoMovimentacao.SAIDA, tipoCafeId, quantidade,
                Validacoes.textoObrigatorio(destino, "destino", 100));
    }

    public int consultarSaldo(int tipoCafeId) {
        return estoques.buscarPorTipoCafe(tipoCafeId).map(Estoque::getQuantidadeSacas).orElse(0);
    }

    public List<Estoque> listarSaldos() {
        return estoques.listarTodos();
    }

    public List<MovimentacaoEstoque> historico(int tipoCafeId) {
        return movimentacoes.listarPorTipoCafe(tipoCafeId);
    }

    private Estoque registrar(TipoMovimentacao tipo, int tipoCafeId, int quantidade, String descricao) {
        Validacoes.inteiroPositivo(quantidade, "quantidade");
        TipoCafe tipoCafe = tipos.buscarPorId(tipoCafeId)
                .orElseThrow(() -> new RegraNegocioException("Tipo de café não encontrado."));
        Estoque estoque = estoques.buscarPorTipoCafe(tipoCafeId).orElseGet(() -> new Estoque(tipoCafe, 0));

        tipo.aplicar(estoque, quantidade); // lança RegraNegocioException se a saída exceder o saldo

        estoques.salvar(estoque);
        movimentacoes.salvar(new MovimentacaoEstoque(tipo, quantidade, LocalDate.now(relogio), descricao, tipoCafe));
        return estoque;
    }
}
