package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.data.CaptionData;
import nro.server.data_holders.data.MapData;
import nro.server.data_holders.data.MonsterData;
import nro.server.data_holders.data.NpcData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.NOT_MAP)
public class SmNotMap extends NroServerPacket {

    public static final byte ALL_DATA_GAME = 4;
    public static final byte UPDATE_MAP = 6;
    public static final byte UPDATE_SKILL = 7;
    public static final byte UPDATE_ITEM = 8;

    private final byte status;

    public SmNotMap(int status) {
        this.status = (byte) status;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        this.writeByte(status);
        switch (status) {
            case ALL_DATA_GAME -> {
                sendAllDataGame();
                con.getSessionInfo().setUpdateData(true);
            }
            case UPDATE_MAP -> {
                sendUpdateMap();
                con.getSessionInfo().setUpdateMap(true);
            }
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
        var mapTemplates = MapData.getInstance().getWorldMaps();
        writeByte(ConfigServer.VERSION_DATA_MAP);
        writeUnsignedByte(mapTemplates.size());

        for (var map : mapTemplates) {
            writeUTF(map.getName());
        }

        var npcTemplates = NpcData.getInstance().getNpcTemplates();
        // write npc
        writeUnsignedByte(npcTemplates.size());
        for (var npc : npcTemplates) {
            this.writeUTF(npc.name());
            this.writeShort(npc.head());
            this.writeShort(npc.body());
            this.writeShort(npc.leg());
            this.writeByte(1);
            this.writeByte(1);
            this.writeUTF("Nói chuyện");
        }

        var monsterTemplates = MonsterData.getInstance().getMonsters();
        writeUnsignedByte(monsterTemplates.size());
        for (var monster : monsterTemplates) {
            this.writeByte(monster.type());
            this.writeUTF(monster.NAME());
            this.writeInt((int) monster.hp());
            this.writeByte(monster.rangeMove());
            this.writeByte(monster.speed());
            this.writeByte(monster.dartType());
        }
    }

}
