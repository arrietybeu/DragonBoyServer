package nro.server.model.templates.task;

/**
 * @author Arriety
 */
public class TalkNpcCondition extends QuestCondition {

    public int npc_id;

    @Override
    public String getKey() {
        return "talk_" + npc_id;
    }
}