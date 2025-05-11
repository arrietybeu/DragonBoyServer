package nro.server.data_holders.data;

import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.template.data.ArrowPaintTemplate;

import java.util.List;

public final class ArrowPaintData implements IManager {

    public List<ArrowPaintTemplate> arrowPaintData;

    @Override
    public void init() throws RuntimeException {
        this.arrowPaintData = YamlDataLoader.loadList("resources/data/update_data/NR_arrow.yml", ArrowPaintTemplate.class);
    }

    @Override
    public void reload() throws RuntimeException {
        clear();
        init();
    }

    @Override
    public void clear() throws RuntimeException {
        if (arrowPaintData != null) arrowPaintData.clear();
        arrowPaintData = null;
    }

    private static final class SingletonHolder {
        private static final ArrowPaintData INSTANCE = new ArrowPaintData();
    }

    public static ArrowPaintData getInstance() {
        return ArrowPaintData.SingletonHolder.INSTANCE;
    }

}
