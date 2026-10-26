package br.cafegestor.web.seguranca;

import java.io.Serializable;
import model.PerfilUsuario;

/** Dados do usuário guardados na sessão HTTP depois do login (nunca a senha). */
public record UsuarioSessao(int id, String nome, String login, PerfilUsuario perfil) implements Serializable {
}
