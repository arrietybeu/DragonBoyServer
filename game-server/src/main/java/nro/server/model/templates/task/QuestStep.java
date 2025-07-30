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

    public int getNpcId(int index) {
        if (npc_id == null || npc_id.isEmpty()) {
            return -1;
        }
        return npc_id.get(index);
    }

    public int getMapId(int index) {
        if (map_id == null || map_id.isEmpty()) {
            return -1;
        }
        return map_id.get(index);
    }

    public String getName(int index) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return name.get(index);
    }

    public String getDetail(int index) {
        if (detail == null || detail.isEmpty()) {
            return "";
        }
        return detail.get(index);
    }
}