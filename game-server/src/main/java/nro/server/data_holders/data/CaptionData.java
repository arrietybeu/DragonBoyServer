package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.data.CaptionTemplate;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Arriety
 */
@Getter
public final class CaptionData implements IManager {

    private List<CaptionTemplate.CaptionLevel> captionLevels;
    private List<CaptionTemplate> captionTemplates;

    @Override
    public void init() throws Throwable {
        this.captionLevels = YamlDataLoader.loadList("resources/data/update_data/NR_caption_level.yml",
                CaptionTemplate.CaptionLevel.class);
        this.captionTemplates = YamlDataLoader.loadList("resources/data/update_data/NR_caption.yml",
                CaptionTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
        clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        if (captionLevels != null)
            captionLevels.clear();
        captionLevels = null;
        if (captionTemplates != null)
            captionTemplates.clear();
        captionTemplates = null;
    }

    public void setDataCaptionLevel() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

    }

    private static final class SingletonHolder {
        private static final CaptionData INSTANCE = new CaptionData();
    }

    public static CaptionData getInstance() {
        return CaptionData.SingletonHolder.INSTANCE;
    }
}
