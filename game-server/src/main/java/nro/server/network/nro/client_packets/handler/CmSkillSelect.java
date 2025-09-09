package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.model.ecs.component.SkillComponent;
import nro.server.data_holders.repo.SkillData;
import nro.server.model.ecs.component.InfoComponent;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.SKILL_SELECT, validStates = { NroConnection.State.IN_GAME })
public class CmSkillSelect extends NroClientPacket {

    private short skillId;

    public CmSkillSelect(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        skillId = readShort();
    }

    @Override
    protected void runImpl() {
        var entity = getConnection().getEntity();
        var skillComponent = entity.getComponent(SkillComponent.class);
        skillComponent.skillSelect = SkillData.getInstance().getSkillInfoByTemplateId(skillId, entity.getComponent(InfoComponent.class).gender, 1);
    }
}
