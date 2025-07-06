package nro.commons.database;

import java.sql.SQLException;

/**
 * @author Arriety
 */
@FunctionalInterface
public interface SQLConsumer<T> {
    void accept(T t) throws SQLException;
}
