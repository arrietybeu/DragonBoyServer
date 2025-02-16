package nro.model.monster;

import lombok.Data;

@Data
public class MonsterStatus {

    private byte status = 5;
    private byte sys;

    private boolean isDisable;
    private boolean isDontMove;
    private boolean isFire;
    private boolean isIce;
    private boolean isWind;

    public void resetStatus() {
        this.isDisable = false;
        this.isDontMove = false;
        this.isFire = false;
        this.isIce = false;
        this.isWind = false;
    }
}