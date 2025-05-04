package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SMGetImageSource;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.GET_IMAGE_SOURCE2, validStates = {NroConnection.State.CONNECTED})
public class CMGetImageSource2 extends NroClientPacket {

    public CMGetImageSource2(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var size = readShort();

        for (int i = 0; i < size; i++) {
            var imageSource = readUTF();
            System.out.println("image source: " + imageSource + " size: " + size);
        }
    }

    @Override
    protected void runImpl() {
        System.out.println("hihi server bat duoc goi -111 roi nha");
        sendPacket(new SMGetImageSource());
    }

}
