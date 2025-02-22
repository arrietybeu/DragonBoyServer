package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.server.manager.MagicTreeManager;

@Getter
@Setter
public class PlayerMagicTree {

    private final Player player;

    private byte level;
    private int currPeas;

    private boolean isUpgrade;

    private long lastTimeUpgrade;
    private long lastTimeHarvest;

    public PlayerMagicTree(Player player) {
        this.player = player;
    }

    public byte getMaxPea() {
        return (byte) ((this.level - 1) * 2 + 5);
    }

    public int getSecondUpgrade() {
        return (int) ((lastTimeUpgrade + getTimeUpgrade() - System.currentTimeMillis()) / 1000);
    }

    private long getTimeUpgrade() {
        var magicTreeTimeUpgrade = MagicTreeManager.getInstance().getMagicTreeTimeUpgrade(this.level);
        int day = magicTreeTimeUpgrade.day();
        int hours = magicTreeTimeUpgrade.hour();
        int minute = magicTreeTimeUpgrade.minute();
        return day * 24 * 60 * 60 * 1000L + hours * 60 * 60 * 1000L + minute * 60 * 1000L;
    }

    public int getSecondPea() {
        short secondPerPea = getSecondPerPea();
        long timePeaRelease = lastTimeHarvest + secondPerPea * 1000;
        int secondLeft = (int) ((timePeaRelease - System.currentTimeMillis()) / 1000);
        return Math.max(secondLeft, 0);
    }

    private short getSecondPerPea() {
        return (short) (this.level * 60);
    }

    public String getTextUpgrade() {
        var magicTreeTimeUpgrade = MagicTreeManager.getInstance().getMagicTreeTimeUpgrade(this.level);
        String text = "Nâng cấp\n";
        int day = magicTreeTimeUpgrade.day();
        int hour = magicTreeTimeUpgrade.hour();
        int minute = magicTreeTimeUpgrade.minute();
        int gold = magicTreeTimeUpgrade.gold();

        if (day > 0) {
            text += day + "d";
        }
        if (hour > 0) {
            text += hour + "h";
        }
        if (minute > 0) {
            text += minute + "'";
        }
        text += "\n" + gold + (this.level <= 3 ? " k" : " Tr") + "\nvàng";
        return text;
    }

    public void update() {
        if (this.isUpgrade) {
            if (this.lastTimeUpgrade <= System.currentTimeMillis()) {
                this.level++;
                this.isUpgrade = false;
                this.lastTimeUpgrade = 0;
            }
        } else {
            if (this.currPeas < this.getMaxPea()) {
                int timeThrow = (int) ((System.currentTimeMillis() - lastTimeHarvest) / 1000);
                int numPeaRelease = timeThrow / getSecondPerPea();
                if (numPeaRelease > 0) {
                    this.currPeas += numPeaRelease;
                    if (this.currPeas >= this.getMaxPea()) {
                        this.currPeas = this.getMaxPea();
                    } else {
                        this.lastTimeHarvest += (long) numPeaRelease * getSecondPerPea() * 1000;
                    }
                }
            }
        }
    }


}
