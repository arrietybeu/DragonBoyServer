package nro.utils.test.network;

import nro.commons.network.packet.*;

public abstract class ClientPacket extends BaseClientPacket<TestConnection> {

    public ClientPacket(int command) {
        super(command);
    }
}
