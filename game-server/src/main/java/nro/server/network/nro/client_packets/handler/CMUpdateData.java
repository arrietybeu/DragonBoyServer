package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SMUpdateData;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.UPDATE_DATA, validStates = {NroConnection.State.CONNECTED})
public class CMUpdateData extends NroClientPacket {

    public CMUpdateData(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        sendPacket(new SMUpdateData());
    }

}
