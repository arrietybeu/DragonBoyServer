package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.engine.quest.QuestEngine;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;
import nro.server.model.templates.task.QuestStep;
import nro.server.model.templates.task.QuestTemplate;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.TASK_GET)
public class SmTaskInfo extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        var entity = con.getEntity();
        var entityId = entity.getId();
        QuestInstanceComponent questData = entity.getWorld().getMapper(QuestInstanceComponent.class).get(entityId);
        if (questData == null)
            throw new NullPointerException("QuestInstanceComponent not found for entity ID: " + entityId);

        QuestTemplate quest = QuestEngine.getInstance().getTask(questData.questId);
        if (quest == null)
            throw new NullPointerException("Quest not found for template ID: " + questData.questId);

        InfoComponent info = entity.getWorld().getMapper(InfoComponent.class).get(entityId);

        if (info == null)
            throw new NullPointerException("InfoComponent not found for entity ID: " + entityId);

        writeShort(quest.id);                        // task ID
        writeByte(questData.currentStep);            // step index
        writeUTF(quest.title.get(info.gender));
        writeUTF(quest.detail.get(info.gender));

        int stepCount = quest.steps.size();
        writeByte(stepCount);

        for (QuestStep step : quest.steps) {
            writeUTF(step.getName(info.gender));
            writeByte(step.getNpcId(info.gender));
            writeShort(step.getMapId(info.gender));
            writeUTF(step.getDetail(info.gender));
        }

        writeShort(quest.steps.get(questData.currentStep).count);

        for (QuestStep step : quest.steps) {
            writeShort(step.count);
        }
    }
}
