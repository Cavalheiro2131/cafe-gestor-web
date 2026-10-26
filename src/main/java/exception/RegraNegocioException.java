package exception;

/** Violação de uma regra de negócio. A mensagem é amigável e pode ser exibida direto ao usuário. */
public class RegraNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
