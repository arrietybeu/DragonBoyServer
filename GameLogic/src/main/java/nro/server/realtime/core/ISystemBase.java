package nro.server.realtime.core;

public interface ISystemBase {

    void register(Object object);

    void unregister(Object object);

    void update();

    int size();

    default String name() {
        return this.getClass().getSimpleName();
    }

}
