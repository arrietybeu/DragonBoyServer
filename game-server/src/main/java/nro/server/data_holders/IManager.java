package nro.server.data_holders;

public interface IManager {

    void init() throws Throwable;

    void reload() throws Throwable;

    void clear() throws Throwable;
}
