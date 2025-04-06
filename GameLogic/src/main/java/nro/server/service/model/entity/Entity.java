package nro.server.service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nro.server.service.model.map.areas.Area;

@Getter
@Setter
@NoArgsConstructor
public abstract class Entity {

    protected int id;
    private String name = "";
    private int typeObject;

    private byte gender;
    private byte typePk;
    private int teleport = 0;
    private short mount = -1;

    protected short x;
    protected short y;

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


    public abstract long handleAttack(Entity entity, int type, long damage);

    public abstract void dispose();

}
