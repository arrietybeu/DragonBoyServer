package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmNotMap;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.NOT_MAP, validStates = {NroConnection.State.CONNECTED})
public class CmNotMap extends NroClientPacket {

    public CmNotMap(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var status = readByte();
        System.out.println("CmNotMap status: " + status + " connection: " + getConnection());
        switch (status) {
            case SmNotMap.UPDATE_MAP -> {
                if (!getConnection().getSessionInfo().isUpdateMap())
                    sendPacket(new SmNotMap(status));
            }
        }
    }

    /**
     * Có những con thuyền không bao giờ cập bến, có những cuộc tình không bao giờ thành đôi.
     */
    @Override
    protected void runImpl() {
    }

}
