package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.GET_IMG_BY_NAME, validStates = {NroConnection.State.IN_GAME})
public class CmGetImgByName extends NroClientPacket {

    public CmGetImgByName(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var name = this.readUTF();
        System.out.println("client get image by name: " + name);
    }

    @Override
    protected void runImpl() {

    }
}
