package security;

/** Abstração de codificação de senhas: o serviço de login depende dela, não de um algoritmo específico (DIP/OCP). */
public interface CodificadorSenha {
    String codificar(String senha);

    boolean confere(String senha, String senhaCodificada);
}
