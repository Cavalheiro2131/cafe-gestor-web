package exception;

/** Falha técnica de persistência (por exemplo, SQLException), sem expor detalhes de JDBC às camadas superiores. */
public class RepositorioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RepositorioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
