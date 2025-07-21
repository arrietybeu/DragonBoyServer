package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.utils.NetworkUtils;
import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.data.CaptionTemplate;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.nio.BufferOverflowException;

/**
 * @author Arriety
 */
@Getter
public final class CaptionData implements IManager {

    private List<CaptionTemplate.CaptionLevel> captionLevels;
    private List<CaptionTemplate> captionTemplates;

    private byte[] traiDat;
    private byte[] namec;
    private byte[] xayda;

    @Override
    public void init() throws Throwable {
        this.captionLevels = YamlDataLoader.loadList("resources/data/update_data/NR_caption_level.yml",
                CaptionTemplate.CaptionLevel.class);
        this.captionTemplates = YamlDataLoader.loadList("resources/data/update_data/NR_caption.yml",
                CaptionTemplate.class);

        setDataCaptionLevel();
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

    private void setDataCaptionLevel() {
        for (int i = 0; i <= 2; i++) {
            List<CaptionTemplate.CaptionLevel> captionLevels = this.getCaptionLevelsByGender((byte) i);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            try {
                buffer.put((byte) captionLevels.size());

                for (CaptionTemplate.CaptionLevel cl : captionLevels) {
                    NetworkUtils.writeString(buffer, cl.name());
                }

                buffer.flip();

                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);

                switch (i) {
                    case 0 -> traiDat = data;
                    case 1 -> namec = data;
                    case 2 -> xayda = data;
                }
            } catch (BufferOverflowException e) {
                switch (i) {
                    case 0 -> traiDat = new byte[0];
                    case 1 -> namec = new byte[0];
                    case 2 -> xayda = new byte[0];
                }
                throw new RuntimeException("Buffer overflow for gender " + i + ": " + e.getMessage());
            }
        }
    }

    public List<CaptionTemplate.CaptionLevel> getCaptionLevelsByGender(byte gender) {
        List<CaptionTemplate.CaptionLevel> result = new ArrayList<>();
        for (CaptionTemplate.CaptionLevel cl : captionLevels) {
            if (cl.gender() == gender) {
                result.add(cl);
            }
        }
        return result;
    }

    private static final class SingletonHolder {
        private static final CaptionData INSTANCE = new CaptionData();
    }

    public static CaptionData getInstance() {
        return CaptionData.SingletonHolder.INSTANCE;
    }
}
