package br.cafegestor.web.servico;

import java.time.Clock;
import model.Estoque;
import org.springframework.transaction.annotation.Transactional;
import repository.EstoqueRepository;
import repository.MovimentacaoRepository;
import repository.TipoCafeRepository;
import service.EstoqueService;

/**
 * EstoqueService do núcleo com transação. O núcleo não conhece o Spring; esta subclasse apenas
 * marca como @Transactional as duas operações que gravam em duas tabelas (estoque e movimentacao_estoque),
 * para que ou as duas são gravadas, ou nenhuma. Qualquer RuntimeException desfaz a transação.
 */
public class EstoqueServiceTransacional extends EstoqueService {
    public EstoqueServiceTransacional(EstoqueRepository estoques, MovimentacaoRepository movimentacoes,
                                      TipoCafeRepository tipos, Clock relogio) {
        super(estoques, movimentacoes, tipos, relogio);
    }

    @Override
    @Transactional
    public Estoque registrarEntrada(int tipoCafeId, int quantidade, String nota) {
        return super.registrarEntrada(tipoCafeId, quantidade, nota);
    }

    @Override
    @Transactional
    public Estoque registrarSaida(int tipoCafeId, int quantidade, String destino) {
        return super.registrarSaida(tipoCafeId, quantidade, destino);
    }
}
