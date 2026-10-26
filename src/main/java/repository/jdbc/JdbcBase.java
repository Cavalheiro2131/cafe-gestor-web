package repository.jdbc;

import exception.RepositorioException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Concentra o código JDBC repetitivo (abrir conexão, preparar, vincular parâmetros, fechar,
 * traduzir SQLException). Os repositórios concretos só descrevem o SQL e o mapeamento.
 */
abstract class JdbcBase {
    private final FonteDeConexao fonte;

    protected JdbcBase(FonteDeConexao fonte) {
        this.fonte = fonte;
    }

    protected <T> List<T> consultar(String sql, MapeadorLinha<T> mapeador, Object... parametros) {
        try (Connection conexao = fonte.obter();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            vincular(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapeador.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao consultar o banco de dados.", e);
        }
    }

    protected <T> Optional<T> consultarUm(String sql, MapeadorLinha<T> mapeador, Object... parametros) {
        return consultar(sql, mapeador, parametros).stream().findFirst();
    }

    /** Executa um INSERT e devolve a chave gerada (AUTO_INCREMENT). */
    protected int inserir(String sql, Object... parametros) {
        try (Connection conexao = fonte.obter();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            vincular(ps, parametros);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new RepositorioException("O banco não devolveu o id do registro inserido.", null);
                }
                return chaves.getInt(1);
            }
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao inserir no banco de dados.", e);
        }
    }

    protected void atualizar(String sql, Object... parametros) {
        try (Connection conexao = fonte.obter();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            vincular(ps, parametros);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao atualizar o banco de dados.", e);
        }
    }

    private static void vincular(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i];
            if (valor instanceof LocalDate data) {
                ps.setDate(i + 1, Date.valueOf(data));
            } else if (valor instanceof Enum<?> constante) {
                ps.setString(i + 1, constante.name());
            } else {
                ps.setObject(i + 1, valor);
            }
        }
    }
}
