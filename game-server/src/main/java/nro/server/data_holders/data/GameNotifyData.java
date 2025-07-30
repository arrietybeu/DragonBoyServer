package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.utils.NetworkUtils;
import nro.server.data_holders.GameEngine;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.data.GameNotify;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Arriety
 */
public class GameNotifyData implements GameEngine {

    private List<GameNotify> notifyList;

    @Getter
    private byte[] dataGameNotify;

    @Override
    public void init() throws Throwable {
        notifyList = YamlDataLoader.loadList("resources/data_holder/game_notify.yml", GameNotify.class);
        setDataGameNotify();
    }

    @Override
    public void reload() throws Throwable {
        clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        notifyList.clear();
        dataGameNotify = null;
    }

    private void setDataGameNotify() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put((byte) notifyList.size());

        for (GameNotify notify : notifyList) {
            buffer.putShort(notify.id());
            NetworkUtils.writeString(buffer, notify.main());
            NetworkUtils.writeString(buffer, notify.content());
        }

        buffer.flip();

        dataGameNotify = new byte[buffer.remaining()];
        buffer.get(dataGameNotify);

        notifyList.clear();
        notifyList = null;
    }

    private static final class SingletonHolder {
        private static final GameNotifyData INSTANCE = new GameNotifyData();
    }

    public static GameNotifyData getInstance() {
        return GameNotifyData.SingletonHolder.INSTANCE;
    }

}
