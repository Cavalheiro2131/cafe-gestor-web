package br.cafegestor.web.dto;

/** tipo: "ENTRADA" ou "SAIDA". descricao: nota (entrada) ou destino (saída, obrigatório). */
public record MovimentacaoRequest(String tipo, int tipoCafeId, int quantidade, String descricao) {
}
