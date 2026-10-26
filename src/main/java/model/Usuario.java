package model;

/** Usuário do sistema. Guarda apenas a senha já codificada (nunca a senha em texto puro). */
public class Usuario implements Identificavel {
    private int id;
    private final String nome;
    private final String login;
    private String senhaCodificada;
    private final PerfilUsuario perfil;

    public Usuario(String nome, String login, String senhaCodificada, PerfilUsuario perfil) {
        this(0, nome, login, senhaCodificada, perfil);
    }

    public Usuario(int id, String nome, String login, String senhaCodificada, PerfilUsuario perfil) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senhaCodificada = senhaCodificada;
        this.perfil = perfil;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getSenhaCodificada() { return senhaCodificada; }
    public void setSenhaCodificada(String senhaCodificada) { this.senhaCodificada = senhaCodificada; }
    public PerfilUsuario getPerfil() { return perfil; }

    @Override
    public String toString() {
        return nome + " (" + login + ", " + perfil + ")";
    }
}
