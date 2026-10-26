package model;

/**
 * Perfis de acesso do sistema. A regra "quem pode alterar dados" fica junto do perfil,
 * evitando comparações de String espalhadas pela interface (Desktop ou Web).
 */
public enum PerfilUsuario {
    ADMIN(true),
    GERENTE(true),
    OPERADOR(false);

    private final boolean podeAlterarDados;

    PerfilUsuario(boolean podeAlterarDados) {
        this.podeAlterarDados = podeAlterarDados;
    }

    public boolean podeAlterarDados() {
        return podeAlterarDados;
    }
}
