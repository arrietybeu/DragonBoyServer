package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.data.CaptionData;
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
        System.out.println("SmNotMap: " + status);
        this.writeByte(status);
        switch (status) {
            case 1 -> {
            }
            case ALL_DATA_GAME -> {
                sendAllDataGame();
                con.getSessionInfo().setUpdateData(true);
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

}
