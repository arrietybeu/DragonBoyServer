package nro.server.manager;

import lombok.Getter;

public class ItemMapManager implements IManager {
    @Getter
    private static final ItemMapManager instance = new ItemMapManager();

    @Override
    public void init() {
    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
    }

}
