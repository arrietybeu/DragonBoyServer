package nro.server.model.templates.item;

import nro.server.model.item.ItemOptionData;

import java.util.List;

/**
 * @author Arriety
 */
public record ItemTemplate(short id, byte type, byte gender, String name, String description, byte level, short iconID,
                           short part, int maxQuantity, int strRequire, short head, short body, short leg,
                           List<ItemOptionData> options, boolean isTrade) {

    public record ArrHead2Frames(int id, List<Integer> frames) {
    }

    public record HeadAvatar(int headId, int avatarId) {
    }
}