package nro.server.network.nro.client_packets.handler;

import java.util.Set;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroConnection.State;
import nro.server.network.nro.client_packets.AClientPacketHandler;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.PLAYER_MOVE, validStates = {NroConnection.State.IN_GAME})
public class CmPlayerMove extends NroClientPacket {

    private byte isOnGround;
    private short newX;
    private short newY;

    public CmPlayerMove(int command, Set<State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var pos = getConnection().getEntity().getComponent(PositionComponent.class);
        this.isOnGround = this.readByte();
        this.newX = this.readShort();
        this.newY = pos.y;
        if(this.getRemainingBytes() > 0) {
            this.newY = this.readShort();
        }
    }

    @Override
    protected void runImpl() {
        NroConnection con = getConnection();
        if (con == null || con.getEntity() == null) return;
        var pos = con.getEntity().getComponent(PositionComponent.class);
        if (pos != null) {
            pos.x = newX;
            pos.y = newY;
            pos.isOnGround = this.isOnGround;
            pos.isDirty = true;
        }
    }
}
