package br.cafegestor.web.dto;

/** Formato único de erro da API: {"mensagem": "..."}; o front-end exibe a mensagem ao usuário. */
public record ErroResposta(String mensagem) {
}
