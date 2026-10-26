package br.cafegestor.web.dto;

import br.cafegestor.web.seguranca.UsuarioSessao;

public record UsuarioResposta(int id, String nome, String login, String perfil) {
    public static UsuarioResposta de(UsuarioSessao usuario) {
        return new UsuarioResposta(usuario.id(), usuario.nome(), usuario.login(), usuario.perfil().name());
    }
}
