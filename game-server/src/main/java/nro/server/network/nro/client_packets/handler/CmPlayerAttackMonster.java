package nro.server.network.nro.client_packets.handler;

import java.util.Set;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.engine.EntityState;
import nro.server.model.ecs.component.StateComponent;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection.State;

/**
 * @author Arriety
 * @see <a href="https://github.com/arrietybeu">github.com/arrietybeu</a>
 */
@AClientPacketHandler(command = ConstsCmd.PLAYER_ATTACK_MOB, validStates = { State.IN_GAME })
public class CmPlayerAttackMonster extends NroClientPacket {

    private int monsterId;

    public CmPlayerAttackMonster(int command, Set<State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        // client mặc định write bytes tôi đã sửa cho nó write int
        monsterId = readInt();
        if (monsterId == -1) {
            // read mob me
            monsterId = readInt();
        }
    }

    @Override
    protected void runImpl() {
        var entity = getConnection().getEntity();
        var stateComponent = entity.getComponent(StateComponent.class);
        stateComponent.state = EntityState.ATTACKING;
        stateComponent.targetId = monsterId;
        stateComponent.isDirty = true;
        log.debug("CmPlayerAttackMonster: {}", monsterId);
    }
}
