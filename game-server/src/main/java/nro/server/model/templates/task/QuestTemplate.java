package nro.server.model.templates.task;

/**
 * @author Arriety
 */
public class QuestTemplate {

    public int id;
    public String name;
    public int start_npc;
    public int end_npc;
    public List<QuestCondition> conditions;
    public List<QuestReward> rewards;
}
