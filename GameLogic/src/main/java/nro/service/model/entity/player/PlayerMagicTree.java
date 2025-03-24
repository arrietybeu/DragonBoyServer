package nro.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.service.model.item.Item;
import nro.server.system.LogServer;
import nro.server.manager.MagicTreeManager;
import nro.service.core.system.ServerService;
import nro.service.core.item.ItemFactory;
import nro.service.core.npc.NpcService;

@Getter
@Setter
public class PlayerMagicTree {

    private final Player player;

    private byte level;
    private int currPeas;

    private boolean isUpgrade;

    private long lastTimeUpgrade;
    private long lastTimeHarvest;
    private long lastUsePea;

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
        StringBuilder text = new StringBuilder("Nâng cấp\n");
        int day = magicTreeTimeUpgrade.day();
        int hour = magicTreeTimeUpgrade.hour();
        int minute = magicTreeTimeUpgrade.minute();

        if (day > 0)
            text.append(day).append("d");
        if (hour > 0)
            text.append(hour).append("h");
        if (minute > 0)
            text.append(minute).append("'");

        text.append("\n").append(this.getGold());
        return text.toString();
    }

    public String getGold() {
        var magicTreeTimeUpgrade = MagicTreeManager.getInstance().getMagicTreeTimeUpgrade(this.level);
        return magicTreeTimeUpgrade.gold() + (this.level <= 3 ? " k" : " Tr") + "\nvàng";
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

    public void fastUpgradeMagicTree() {
        if (this.level < 10) {
            this.level++;
        }
        this.currPeas = this.getMaxPea();
        this.isUpgrade = false;
        this.lastTimeUpgrade = 0;
        NpcService.getInstance().loadMagicTree(this.player, 0, null);
    }

    public void cancelUpgradeMagicTree() {
        var gold = MagicTreeManager.getInstance().getMagicTreeTimeUpgrade(this.level).gold();
        var goldRefund = (gold * (this.level <= 3 ? 1000 : 1000000)) / 2;
        this.player.getPlayerCurrencies().addGold(goldRefund);
        this.isUpgrade = false;
        this.lastTimeUpgrade = 0;
        NpcService.getInstance().loadMagicTree(this.player, 0, null);
    }

    public void harvestPea() {
        if (this.currPeas > 0) {
            byte currPeasTemp = (byte) this.currPeas;
            this.addPeaHarvenst(currPeasTemp);
            this.currPeas = 0;
            this.lastTimeHarvest = System.currentTimeMillis();
            NpcService.getInstance().loadMagicTree(this.player, 2, null);
        }
    }

    public void upgradeMagicTree() {
        // long gold =
        // MagicTreeManager.getInstance().getMagicTreeTimeUpgrade(this.level).gold();
        this.isUpgrade = true;
        this.lastTimeUpgrade = System.currentTimeMillis() + getTimeUpgrade();
        NpcService.getInstance().loadMagicTree(this.player, 0, null);
    }

    public void resetPea() {
        this.currPeas = this.getMaxPea();
        this.lastTimeHarvest = System.currentTimeMillis();
        NpcService.getInstance().loadMagicTree(this.player, 0, null);
    }

    private void addPeaHarvenst(int quantity) {
        try {
            var magicTreeLevel = MagicTreeManager.getInstance().getMagicTreeLevel(this.level);
            Item pea = ItemFactory.getInstance().createItemNotOptionsBase(magicTreeLevel.itemId(), quantity);
            pea.addOption(magicTreeLevel.optionId(), magicTreeLevel.optionParam());

            String name = pea.getTemplate().name();

            if (player.getPlayerInventory().addItemBag(pea)) {
                String text = "Bạn vừa thu hoạch được " + quantity + " hạt " + name;
                ServerService.getInstance().sendChatGlobal(this.player.getSession(), null, text, false);
            }

        } catch (Exception ex) {
            LogServer.LogException("PlayerMagicTree.addPeaHarvenst" + ex.getMessage(), ex);
        }
    }
}
