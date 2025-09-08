package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.SkillComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */

@ServerPacketCommand(ConstsCmd.CHANGE_ONSKILL)
public final class SmChangeOnSkill extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        var skillComponent = con.getEntity().getComponent(SkillComponent.class);
        if (skillComponent == null) {
            throw new RuntimeException("SkillComponent not found for player ID: " + con.getPlayerID());
        }
        System.out.println(" djtme jake: " + skillComponent.skillShortCut.length);
        for (int i = 0; i < skillComponent.skillShortCut.length; i++) {

            writeByte(skillComponent.skillShortCut[i]);
        }
    }

}
