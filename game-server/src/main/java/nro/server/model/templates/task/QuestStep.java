package nro.server.model.templates.task;

import java.util.List;

/**
 * @author Arriety
 */
public class QuestStep {

    public int index;
    public String[] name;
    public String[] detail;
    public short[] npc_id;
    public short[] map_id;
    public String[] content;
    public int count;
    public List<ItemReward> reward;

    public static class ItemReward {
        public int item_id;
        public int quantity;
    }

    public int getNpcId(int index) {
        if (npc_id == null || npc_id.length == 0) {
            return -1;
        }
        if (npc_id.length <= index) {
            return npc_id[0];
        }
        return npc_id[index];
    }

    public int getMapId(int index) {
        if (map_id == null || map_id.length == 0) {
            return -1;
        }
        if (map_id.length <= index) {
            return map_id[0];
        }
        return map_id[index];
    }

    public String getName(int index) {
        if (name == null || name.length == 0) {
            return "";
        }
        if (name.length <= index) {
            return name[0];
        }
        return name[index];
    }

    public String getDetail(int index) {
        if (detail == null || detail.length == 0) {
            return "";
        }
        if (detail.length <= index) {
            return detail[0];
        }
        return detail[index];
    }

    public String getContent(int index) {
        if (content == null || content.length == 0) {
            return "";
        }
        if (content.length <= index) {
            return content[0];
        }
        return content[index];
    }
}