package nro.server.data_holders;

public interface GameEngine {

    void init() throws Throwable;

    void reload() throws Throwable;

    void clear() throws Throwable;
}
