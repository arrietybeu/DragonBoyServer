package nro.server.realtime.core;

public interface ISystemBase {

    void register(Object object);

    void unregister(Object object);

    void removeAll();

    void update();

    int size();

    String name();

}
