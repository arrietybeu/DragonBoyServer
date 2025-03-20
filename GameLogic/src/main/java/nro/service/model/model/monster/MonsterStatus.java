package nro.service.model.model.monster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonsterStatus {

    private final Monster monster;

    private byte status = 5;
    private byte sys;
    private boolean isDisable;
    private boolean isDontMove;
    private boolean isFire;
    private boolean isIce;
    private boolean isWind;

    public MonsterStatus(Monster monster) {
        this.monster = monster;
    }

    public void resetStatus() {
        this.isDisable = false;
        this.isDontMove = false;
        this.isFire = false;
        this.isIce = false;
        this.isWind = false;
    }
}