package br.cafegestor.web.dto;

import model.MovimentacaoEstoque;

public record MovimentacaoResposta(int id, String tipo, int tipoCafeId, String tipoCafeNome,
                                   int quantidade, String data, String descricao) {
    public static MovimentacaoResposta de(MovimentacaoEstoque m) {
        return new MovimentacaoResposta(m.getId(), m.getTipo().name(), m.getTipoCafe().getId(),
                m.getTipoCafe().getNome(), m.getQuantidade(), m.getData().toString(), m.getDestino());
    }
}
