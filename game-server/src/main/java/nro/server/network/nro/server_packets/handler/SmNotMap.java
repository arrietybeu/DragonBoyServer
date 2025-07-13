package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.data.*;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

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

    private final byte status;
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
    protected void writeImpl(NroConnection con) throws RuntimeException {
        this.writeByte(status);
        switch (status) {
            case ALL_DATA_GAME -> {
                sendAllDataGame();
                con.getSessionInfo().setUpdateData(true);
            }
            case UPDATE_MAP -> sendUpdateMap();
            case UPDATE_SKILL -> sendUpdateSkill();
            case UPDATE_ITEM -> sendUpdateItem(type);
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
        for (var caption : captionTemplates) {
            this.writeLong(caption.exp());
        }
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
