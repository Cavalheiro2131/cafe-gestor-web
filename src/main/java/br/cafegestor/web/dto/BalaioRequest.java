package br.cafegestor.web.dto;

/** A data chega como texto ISO (aaaa-mm-dd), o mesmo formato do campo type="date" do HTML. */
public record BalaioRequest(int funcionarioId, int tipoCafeId, int quantidade, String data) {
}
