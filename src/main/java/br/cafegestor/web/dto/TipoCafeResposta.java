package br.cafegestor.web.dto;

import model.TipoCafe;

public record TipoCafeResposta(int id, String nome, String variedade, String observacoes) {
    public static TipoCafeResposta de(TipoCafe t) {
        return new TipoCafeResposta(t.getId(), t.getNome(), t.getVariedade(), t.getObservacoes());
    }
}
