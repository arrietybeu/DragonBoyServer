package nro.model.item;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class ItemTemplate {

    private final short id;
    private final byte type;
    private final byte gender;
    private final String name;
    private final String description;
    private final byte level;
    private final short iconID;
    private final short part;
    private final boolean isUpToUp;
    private final int strRequire;

    private int w;
    private int h;

    public ItemTemplate(short id, byte type, byte gender, String name, String description, byte level, short iconID, short part, boolean isUpToUp, int strRequire) {
        this.id = id;
        this.type = type;
        this.gender = gender;
        this.name = name;
        this.description = description;
        this.level = level;
        this.iconID = iconID;
        this.part = part;
        this.isUpToUp = isUpToUp;
        this.strRequire = strRequire;
    }

    public record ArrHead2Frames(int id, List<Integer> frames) {
    }

}