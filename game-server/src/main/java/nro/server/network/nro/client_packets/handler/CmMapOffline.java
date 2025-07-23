package nro.server.network.nro.client_packets.handler;

import com.artemis.ComponentMapper;
import nro.commons.consts.ConstsCmd;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.MAP_OFFLINE)
public class CmMapOffline extends NroClientPacket {

    public CmMapOffline(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {

    }

    @Override
    protected void runImpl() {
        NroConnection con = getConnection();
        if (con == null || con.getEntity() == null) return;

        int entityId = con.getEntity().getId();

        ComponentMapper<PositionComponent> posMapper = GameWorld.getInstance().getWorld().getMapper(PositionComponent.class);
        PositionComponent pos = posMapper.get(entityId);

        if (pos != null) {
            pos.wantsToChangeMap = true; // Set flag để trigger MapChangeSystem
            pos.isDirty = true; // Trigger process ngay tick tiếp theo
        }
    }
}
