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

    private byte pIsOnGround;
    private short pNewX, pNewY;

    public CmPlayerMove(int command, Set<State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        pIsOnGround = this.readByte();
        pNewX = this.readShort();
        pNewY = getConnection().getEntity()
                .getComponent(PositionComponent.class).y;
        if (this.getRemainingBytes() > 0) pNewY = this.readShort();
    }

    @Override
    protected void runImpl() {
        var con = getConnection();
        if (con == null || con.getEntity() == null) return;
        final byte isGround = pIsOnGround;
        final short nx = pNewX, ny = pNewY;

        var pos = con.getEntity().getComponent(PositionComponent.class);
        if (pos == null) return;
        pos.isOnGroundNew = isGround;
        pos.newX = nx;
        pos.newY = ny;
        pos.isDirtyMove = true;
    }

}
