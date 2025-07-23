package nro.server.network.nro.client_packets.handler;

import java.util.Set;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroConnection.State;
import nro.server.network.nro.client_packets.AClientPacketHandler;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.PLAYER_MOVE, validStates = { NroConnection.State.IN_GAME })
public class CmPlayerMove extends NroClientPacket {

    public CmPlayerMove(int command, Set<State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {

        byte isOnGround = this.readByte();//  0: on ground, 1: in air

        if (isOnGround == 1) {
//            player.getPoints().reduceMPWhenFlying();
        }

        short newX = this.readShort();
        short newY /*= player.getY()*/;

        if (this.getRemainingBytes() > 0) {
            newY = this.readShort();
        }

//        player.setX(newX);
//        player.setY(newY);

//        if (player.getPlayerTask().getTaskMain().getId() == 0) {
//            player.getPlayerTask().checkDoneTaskGoMap();
//        }

//        AreaService.getInstance().playerMove(player);

    }

    @Override
    protected void runImpl() {
    }
}
