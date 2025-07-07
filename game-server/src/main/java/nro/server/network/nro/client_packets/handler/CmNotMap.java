package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.*;

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
            case SmNotMap.UPDATE_SKILL -> {
                if (!getConnection().getSessionInfo().isUpdateSkill())
                    sendPacket(new SmNotMap(status));
            }
            case SmNotMap.UPDATE_ITEM -> {
                if (!getConnection().getSessionInfo().isUpdateItem()) {
                    sendPacket(new SmNotMap(status, SmNotMap.ITEM_OPTION));
                    sendPacket(new SmNotMap(status, SmNotMap.ITEM_TEMPLATE));
                    sendPacket(new SmNotMap(status, SmNotMap.ITEM_TEMPLATE2));
                    sendPacket(new SmNotMap(status, SmNotMap.ITEM_ARR_HEAD_2FR));
                    getConnection().getSessionInfo().setUpdateItem(true);
                }
            }
            case 13 -> {
                if (getConnection().getSessionInfo().isClientOk())
                    throw new RuntimeException(
                            "Client already sent NOT_MAP with status 13, but connection is still in CONNECTED state. This should not happen."
                    );
                // client ok
                // sendDataBackgroundMapTemplate
                // sendTileSetInfo
                // sendSmallVersion
                // sendBackgroundVersion

                sendPacket(new SmItemBackground());
                sendPacket(new SmTileSet());
                sendPacket(new SmSmallImageVersion());
                sendPacket(new SmBackgroundItemVersion());

                getConnection().setState(NroConnection.State.AUTHED);
                getConnection().getSessionInfo().setClientOk(true);
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
