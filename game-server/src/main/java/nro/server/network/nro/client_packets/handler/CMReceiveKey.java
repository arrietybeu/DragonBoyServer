package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.commons.network.Crypt;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SMSendKey;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.GET_SESSION_ID, validStates = {NroConnection.State.CONNECTED})
public class CMReceiveKey extends NroClientPacket {

    public CMReceiveKey(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        sendPacket(new SMSendKey());
//        Crypt crypt = getConnection().getCrypt();
//        crypt.resetKeyIndex();
//        crypt.setSendKey(true);
//        log.info("Activated encryption for connection from {}", getConnection().getIP());
    }
}
