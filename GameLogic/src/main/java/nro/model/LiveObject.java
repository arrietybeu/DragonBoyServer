package nro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class LiveObject {

    private static final short[][] BIRD_FRAMES = {
            {281, 361, 351},
            {512, 513, 536},
            {514, 515, 537}
    };

    private static final String[][] BIRD_NAMES = {
            {"Puaru"}, {"Piano"}, {"Icarus"}
    };

    private int id;
    private String name = "";
    private byte typeObject;

    private short aura;
    private byte idEffSetItem = -1;
    private short idHat;

    private byte gender;
    private byte typePk;

    private short x;
    private short y;

    public short[] getPlayerBirdFrames() {
        if (this.gender < 0 || this.gender >= BIRD_FRAMES.length) {
            this.gender = 2;
        }
        return BIRD_FRAMES[this.gender];
    }

    public String[] getPlayerBirdNames(){
        if (this.gender < 0 || this.gender >= BIRD_NAMES.length) {
            this.gender = 2;
        }
        return BIRD_NAMES[this.gender];
    }

}
