package repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexão com o MySQL. Os dados vêm de propriedades do sistema (-DCAFE_DB_URL=...) ou de
 * variáveis de ambiente, para que usuário e senha do banco não fiquem no código nem no GitHub.
 */
public class ConexaoMySql implements FonteDeConexao {
    private static final String URL_PADRAO =
            "jdbc:mysql://localhost:3306/cafe_gestor?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoMySql(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public static ConexaoMySql doAmbiente() {
        return new ConexaoMySql(
                ler("CAFE_DB_URL", URL_PADRAO),
                ler("CAFE_DB_USER", "root"),
                ler("CAFE_DB_PASS", ""));
    }

    @Override
    public Connection obter() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    private static String ler(String chave, String padrao) {
        String valor = System.getProperty(chave);
        if (valor == null) {
            valor = System.getenv(chave);
        }
        return valor == null ? padrao : valor;
    }
}
