package nro.model.template.item;

import java.util.List;

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
    private int w;
    private int h;
    private final int strRequire;

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

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public byte getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte getLevel() {
        return level;
    }

    public short getIconID() {
        return iconID;
    }

    public short getPart() {
        return part;
    }


    public boolean isUpToUp() {
        return isUpToUp;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getStrRequire() {
        return strRequire;
    }

    @Override
    public String toString() {
        return "ItemTemplate{" + "id=" + id + ", type=" + type + ", gender=" + gender + ", name='" + name + '\'' + ", description='" + description + '\'' + ", level=" + level + ", iconID=" + iconID + ", part=" + part + ", isUpToUp=" + isUpToUp + ", w=" + w + ", h=" + h + ", strRequire=" + strRequire + '}';
    }

    public static class ArrHead2Frames {
        private int id;
        private List<Integer> frames;

        public ArrHead2Frames(int id, List<Integer> frames) {
            this.id = id;
            this.frames = frames;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Integer> getFrames() {
            return frames;
        }

        public void setFrames(List<Integer> frames) {
            this.frames = frames;
        }

        @Override
        public String toString() {
            return "ArrHead2Frames{" +
                    "id=" + id +
                    ", frames=" + frames +
                    '}';
        }
    }

}