package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SMGetImageSource;
import nro.server.network.nro.server_packets.handler.SMNotLogin;

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
            System.out.println("image source: " + imageSource + " size: " + size + " connect: " + getConnection());
        }
    }

    @Override
    protected void runImpl() {
        sendPacket(new SMGetImageSource(0));
        sendPacket(new SMNotLogin());
    }

}
