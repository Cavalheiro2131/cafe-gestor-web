package repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Funcionario;
import model.TipoCafe;

/** Trechos de SQL e mapeamentos compartilhados pelos repositórios (alias f_ = funcionario, t_ = tipo_cafe). */
final class Mapeadores {
    static final String COLUNAS_FUNCIONARIO =
            "f.id AS f_id, f.nome AS f_nome, f.funcao AS f_funcao, f.contato AS f_contato, f.status AS f_status";
    static final String COLUNAS_TIPO_CAFE =
            "t.id AS t_id, t.nome AS t_nome, t.variedade AS t_variedade, t.observacoes AS t_observacoes";

    private Mapeadores() { }

    static Funcionario funcionario(ResultSet rs) throws SQLException {
        return new Funcionario(rs.getInt("f_id"), rs.getString("f_nome"), rs.getString("f_funcao"),
                rs.getString("f_contato"), rs.getBoolean("f_status"));
    }

    static TipoCafe tipoCafe(ResultSet rs) throws SQLException {
        return new TipoCafe(rs.getInt("t_id"), rs.getString("t_nome"),
                rs.getString("t_variedade"), rs.getString("t_observacoes"));
    }
}
