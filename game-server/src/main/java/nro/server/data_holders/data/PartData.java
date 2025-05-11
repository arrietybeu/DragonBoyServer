package nro.server.data_holders.data;

import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.template.data.PartTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PartData implements IManager {

    public Map<Integer, PartTemplate> partTemplateMap = new LinkedHashMap<>();

    @Override
    public void init() throws Throwable {
        partTemplateMap = YamlDataLoader.loadMap("resources/data/update_data/NR_part.yml", Integer.class, PartTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
        clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        if (partTemplateMap != null) partTemplateMap.clear();
        partTemplateMap = null;
    }

    private static final class SingletonHolder {
        private static final PartData INSTANCE = new PartData();
    }

    public static PartData getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
