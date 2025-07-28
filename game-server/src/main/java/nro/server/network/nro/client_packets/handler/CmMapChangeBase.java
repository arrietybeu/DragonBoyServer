package nro.server.network.nro.client_packets.handler;

import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;

import java.util.Set;

/**
 * @author Arriety
 */
public abstract class CmMapChangeBase extends NroClientPacket {

    public CmMapChangeBase(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        // No data to read for map change packets
    }

    @Override
    protected void runImpl() {
        NroConnection con = getConnection();
        if (con == null || con.getEntity() == null) {
            return;
        }

        PositionComponent pos = con.getEntity().getComponent(PositionComponent.class);
        if (pos != null) {
            pos.wantsToChangeMap = true; // Trigger MapChangeSystem
            pos.isDirty = true; // Process in next tick
        }
    }
}