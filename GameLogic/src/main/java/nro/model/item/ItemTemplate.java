package nro.model.item;

import java.util.List;

public record ItemTemplate(short id, byte type, byte gender, String name, String description, byte level, short iconID,
                           short part, boolean isUpToUp, int strRequire, List<ItemOption> options) {

    public record ArrHead2Frames(int id, List<Integer> frames) {
    }

}