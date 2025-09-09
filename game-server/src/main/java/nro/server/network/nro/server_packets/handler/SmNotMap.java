package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.consts.ConstMsgNotMap;
import nro.server.data_holders.data.*;
import nro.server.data_holders.repo.ItemData;
import nro.server.data_holders.repo.MapData;
import nro.server.data_holders.repo.SkillData;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.NOT_MAP)
public class SmNotMap extends NroServerPacket {

    public static final byte CREATE_CHARACTER = 2;
    public static final byte ALL_DATA_GAME = 4;
    public static final byte UPDATE_MAP = 6;
    public static final byte UPDATE_SKILL = 7;
    public static final byte UPDATE_ITEM = 8;

    public static final byte ITEM_OPTION = 0;
    public static final byte ITEM_TEMPLATE = 1;
    public static final byte ITEM_ARR_HEAD_2FR = 100;

    private final int status;
    private byte type;

    public SmNotMap(int status) {
        this.status = (byte) status;
    }

    /**
     * Packet gửi item
     *
     * @param status
     * @param type
     */
    public SmNotMap(int status, int type) {
        this.status = (byte) status;
        this.type = (byte) type;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        this.writeByte(status);
        switch (status) {
            case ConstMsgNotMap.REQUEST_MAP_TEMPLATE -> this.requestMapTemplate(con);
            case ConstMsgNotMap.SEND_VERSION -> {
                sendAllDataGame();
                con.getSessionInfo().setUpdateData(true);
            }
            case ConstMsgNotMap.UPDATE_MAP -> sendUpdateMap();
            case ConstMsgNotMap.UPDATE_SKILL -> sendUpdateSkill();
            case ConstMsgNotMap.UPDATE_ITEM -> sendUpdateItem(type);
        }
    }

    private void sendAllDataGame() {
        this.writeByte(ConfigServer.VERSION_DATA);
        this.writeByte(ConfigServer.VERSION_DATA_MAP);
        this.writeByte(ConfigServer.VERSION_DATA_SKILL);
        this.writeByte(ConfigServer.VERSION_DATA_ITEM);
        this.writeByte(0);

        var captionTemplates = CaptionData.getInstance().getCaptionTemplates();
        this.writeByte(captionTemplates.size());
        for (var caption : captionTemplates)
            this.writeLong(caption.exp());
    }

    public void requestMapTemplate(NroConnection client) {
        PositionComponent position = client.getEntity().getComponent(PositionComponent.class);

        if (position == null) {
            throw new IllegalArgumentException("PositionComponent cannot be null for SmNotMap requestMapTemplate");
        }

        var map = MapData.getInstance().getWorldMapTemplate(position.mapId);

        this.writeByte(map.getTileMap().width());
        this.writeByte(map.getTileMap().height());
        for (int i = 0; i < map.getTileMap().tiles().length; i++) {
            this.writeByte(map.getTileMap().tiles()[i]);
        }

        PacketHelper.writeMapInfo(this, map, position);
        writeByte(map.getIsMapDouble());

    }

    private void sendUpdateMap() {
        writeBytes(MapData.getInstance().dataMapData);
    }

    private void sendUpdateSkill() {
        writeBytes(SkillData.getInstance().skillData);
    }

    private void sendUpdateItem(int type) {
        this.writeByte(ConfigServer.VERSION_DATA_ITEM);
        switch (type) {
            case ITEM_OPTION -> sendUpdateOption();
            case ITEM_ARR_HEAD_2FR -> sendItemArr_Head_2Fr();
            default -> throw new IllegalArgumentException("Unknown item update type: " + type);
        }
    }

    private void sendUpdateOption() {
        writeBytes(ItemData.getInstance().getDataItemOption());
    }

    private void sendItemArr_Head_2Fr() {
        writeBytes(ItemData.getInstance().getDataArrHead2Fr());
    }
}
