package nro.commons.database;

/**
 * @author Arriety
 */
@FunctionalInterface
public interface SQLFunction<T, R> {
    R apply(T t) throws Exception;
}