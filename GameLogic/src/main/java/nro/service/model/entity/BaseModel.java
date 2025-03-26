package nro.service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nro.service.model.entity.player.*;
import nro.service.model.map.areas.Area;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseModel {

    private int id;
    private String name = "";
    private int typeObject;

    private byte gender;
    private byte typePk;
    private int teleport = 0;
    private short mount = -1;

    private short x;
    private short y;

    protected Points points;
    protected Skills skills;
    protected Fusion fusion;
    protected Fashion fashion;
    protected Area area;

    private static final short[][] BIRD_FRAMES = {
            {281, 361, 351},
            {512, 513, 536},
            {514, 515, 537}
    };

    private static final String[][] BIRD_NAMES = {
            {"Puaru"}, {"Piano"}, {"Icarus"}
    };

    public short getAura() {
        return -1;
    }

    public byte getEffSetItem() {
        return -1;
    }

    public short getIdHat() {
        return -1;
    }

    public short[] getPlayerBirdFrames() {
        var gender = this.getGender();
        if (gender < 0 || gender >= BIRD_FRAMES.length) {
            gender = 2;
        }
        return BIRD_FRAMES[gender];
    }

    public String[] getPlayerBirdNames() {
        var gender = this.getGender();
        if (gender < 0 || gender >= BIRD_NAMES.length) {
            gender = 2;
        }
        return BIRD_NAMES[gender];
    }

    public abstract void update();

    public abstract long handleAttack(Player player, int type, long damage);

    public abstract void dispose();

}
