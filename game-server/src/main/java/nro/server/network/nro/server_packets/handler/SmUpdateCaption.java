package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.CaptionData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.UPDATE_CAPTION)
public class SmUpdateCaption extends NroServerPacket {

    private final int gender;

    public SmUpdateCaption(int gender) {
        this.gender = gender;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        CaptionData captionData = CaptionData.getInstance();
        byte[] dataToSend;
        switch (gender) {
            case 0 -> dataToSend = captionData.getTraiDat();
            case 1 -> dataToSend = captionData.getNamec();
            case 2 -> dataToSend = captionData.getXayda();
            default -> throw new RuntimeException("SendCaptionForPlayer invalid gender: " + gender);
        }
        writeBytes(dataToSend);
    }
}
