package br.cafegestor.web.dto;

import model.Balaio;

public record BalaioResposta(int id, String data, int quantidade, int funcionarioId, String funcionarioNome,
                             int tipoCafeId, String tipoCafeNome) {
    public static BalaioResposta de(Balaio b) {
        return new BalaioResposta(b.getId(), b.getData().toString(), b.getQuantidade(),
                b.getFuncionario().getId(), b.getFuncionario().getNome(),
                b.getTipoCafe().getId(), b.getTipoCafe().getNome());
    }
}
