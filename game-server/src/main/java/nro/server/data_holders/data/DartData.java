package nro.server.data_holders.data;

import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.template.data.DartTemplate;

import java.util.List;

public final class DartData implements IManager {

    public List<DartTemplate> darts;

    @Override
    public void init() throws RuntimeException {
        this.darts = YamlDataLoader.loadList("resources/data/update_data/NR_dart.yml", DartTemplate.class);
    }

    @Override
    public void reload() throws Exception {
        clear();
        init();
    }

    @Override
    public void clear() throws Exception {
        if (darts != null) darts.clear();
        darts = null;
    }

    public int size() {
        return darts != null ? darts.size() : 0;
    }

    public DartTemplate getById(int id) {
        return darts.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }

    private static final class SingletonHolder {
        private static final DartData INSTANCE = new DartData();
    }

    public static DartData getInstance() {
        return DartData.SingletonHolder.INSTANCE;
    }

}
