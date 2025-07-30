package nro.server.model.templates.task;

import java.util.List;

/**
 * @author Arriety
 */
public class QuestStep {

    public int index;
    public List<String> name;
    public List<String> detail;
    public List<Integer> npc_id;
    public List<Integer> map_id;
    public int count;
    public List<ItemReward> reward;

    public static class ItemReward {
        public int item_id;
        public int quantity;
    }
}