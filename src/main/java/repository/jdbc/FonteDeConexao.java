package repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/** Abstrai de onde vem a conexão (DriverManager hoje; pool ou DataSource do servidor na versão Web). */
public interface FonteDeConexao {
    Connection obter() throws SQLException;
}
