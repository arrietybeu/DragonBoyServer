package nro.server.model.templates.task;

/**
 * @author Arriety
 */
public class KillMobCondition extends QuestCondition {
    public int mob_id;
    public int count;

    @Override
    public String getKey() {
        return "kill_" + mob_id;
    }
}