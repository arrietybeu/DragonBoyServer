package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.TileImageData;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SMGetImageSource;

import java.io.File;
import java.util.List;
import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.GET_IMAGE_SOURCE, validStates = {NroConnection.State.CONNECTED})
public class CMGetImageSource extends NroClientPacket {

    private byte action;

    public CMGetImageSource(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.action = this.readByte();
        if (action == 2) {
            if (getRemainingBytes() < 2) {
                System.err.println("Không đủ bytes để đọc resourceIndexSize");
                return;
            }
            var resourceIndexSize = this.readShort();
            for (int i = 0; i < resourceIndexSize; i++) {
                var concacj = readShort();
                System.out.println("Concacj: " + concacj);
            }
        }
    }

    @Override
    protected void runImpl() {
        int zoomLevel = this.getConnection().getSessionInfo().getClientDeviceInfo().getZoomLevel();
        List<File> files = TileImageData.getInstance().getTileImageByZoomLevel(zoomLevel);

        switch (action) {
            case 1 -> sendPacket(new SMGetImageSource(action, (short) files.size()));
            case 2 -> {
                for (File file : files) {
                    sendPacket(new SMGetImageSource(action, file));
                }
                sendPacket(new SMGetImageSource(3, (short) files.size()));
            }
            case 3 -> System.out.println("client download image done");
        }
    }
}
