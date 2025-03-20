package nro.service.model.template.item;

import java.util.List;

public record ItemTemplate(short id, byte type, byte gender, String name, String description, byte level, short iconID,
                           short part, int maxQuantity, int strRequire, short head, short body, short leg, List<ItemOption> options) {

    public record ArrHead2Frames(int id, List<Integer> frames) {
    }

    public record HeadAvatar(int headId, int avatarId) {
    }
}