package repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface MapeadorLinha<T> {
    T mapear(ResultSet linha) throws SQLException;
}
