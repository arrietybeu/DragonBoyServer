package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstSkill;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.map.GameMap;
import nro.model.template.entity.SkillInfo;
import nro.server.LogServer;
import nro.server.manager.MapManager;
import nro.service.AreaService;
import nro.service.PlayerService;
import nro.service.Service;

@Getter
@Setter
public class PlayerPoints {

    private final Player player;

    // chỉ số cơ bản, chỉ số gốc
    private int baseHP, baseMP;
    private int baseDamage;
    private int baseDefense;
    private byte baseCriticalChance;
    private byte movementSpeed;
    private short stamina;

    // chi so hien tai
    private long currentHP, currentMP, currentDamage;

    // chi so max
    private long maxHP, maxMP;
    private short maxStamina;

    // chi so tong hop cam lon
    private long totalDefense;
    private byte totalCriticalChance;

    private short expPerStatIncrease;
    private byte hpPer1000Potential;
    private byte mpPer1000Potential;
    private byte damagePer1000Potential;

    private short eff5BuffHp, eff5BuffMp;

    // power , tiem nang
    private long power, potentialPoints;

    private short tlHutHp, tlHutMp, tlHutHpMob;

    public PlayerPoints(Player player) {
        this.player = player;
    }

    public boolean isDead() {
        return this.currentHP <= 0;
    }

    public void setCurrentHp(long hp) {
        if (hp < 0) {
            this.currentHP = 0;
        } else this.currentHP = Math.min(hp, this.maxHP);
    }

    public void setCurrentMp(long mp) {
        if (mp < 0) {
            this.currentMP = 0;
        } else this.currentMP = Math.min(mp, this.maxMP);
    }

    public void subCurrentHp(long hp) {
        this.currentHP -= hp;
        if (this.currentHP < 0) {
            this.currentHP = 0;
        }
    }

    public long getDameAttack() {
        long dame = this.getCurrentDamage();
        long dameSkill = getDameSkill();
        if (dameSkill != 0) {
            dame = dame * dameSkill / 100;
        }
        return dame;
    }

    public void setPoint() {
        this.setHp();
        this.setMp();
        this.setDame();
    }

    private void setHp() {
        long hpKi000 = this.getParamOption(2, 0, 0) * 1000;
        long hpK = this.getParamOption(22, 0, 0) * 1000;
        long hp = this.getParamOption(6, 0, 0);
        long hpKi = this.getParamOption(48, 0, 0);

        this.maxHP = this.baseHP + hpKi000 + hpK + hp + hpKi;
        this.maxHP = this.getParamOption(77, 1, this.maxHP);
    }

    private void setMp() {
        long mpKi000 = this.getParamOption(2, 0, 0) * 1000;
        long mpK = this.getParamOption(23, 0, 0) * 1000;
        long mp = this.getParamOption(7, 0, 0);
        long mpKi = this.getParamOption(48, 0, 0);

        this.maxMP = this.baseMP + mpKi000 + mpK + mp + mpKi;
        this.maxMP = this.getParamOption(103, 1, this.maxMP);
    }

    private void setDame() {
        this.currentDamage = this.baseDamage;
        long tanCong = this.getParamOption(0, 0, 0);

        this.currentDamage = this.currentDamage + tanCong;
    }

    private long getParamOption(int id, int type, long quantity) {
        long param = 0;
        if (type == 1 || type == 2) {
            param = quantity;
        }
        for (int i = 0; i < this.player.getPlayerInventory().getItemsBody().size(); i++) {
            Item itemBody = this.player.getPlayerInventory().getItemsBody().get(i);
            if (itemBody.getTemplate() == null) continue;
            var countOption = itemBody.getItemOptions().size();
            for (int o = 0; o < countOption; o++) {
                ItemOption itemOption = itemBody.getItemOptions().get(o);
                if (itemOption == null) continue;
                if (itemOption.getId() == id) {
                    switch (type) {
                        case 1 -> param = param + param * itemOption.getParam() / 100;
                        case 2 -> param = param * itemOption.getParam() / 100;
                        case 3 -> {
                            return 1;
                        }
                        case 4 -> {
                            return itemOption.getParam();
                        }
                        default -> param += itemOption.getParam();
                    }
                }
            }
        }
        return param;
    }

    private long getDameSkill() {
        try {
            SkillInfo skillSelect = this.player.getPlayerSkill().skillSelect;
            return switch (skillSelect.getTemplate().getId()) {
                case ConstSkill.DRAGON, ConstSkill.DEMON, ConstSkill.GALICK -> skillSelect.getDamage();
                default -> 0;
            };
        } catch (Exception ex) {
            LogServer.LogException(" getSkillDamageMultiplier: " + ex.getStackTrace());
            return 0;
        }
    }

    public void addExp(int type, int exp) {
        switch (type) {
            case 0 -> this.power += exp;
            case 1 -> this.potentialPoints += exp;
            case 2 -> {
                this.power += exp;
                this.potentialPoints += exp;
            }
        }
        PlayerService playerService = PlayerService.getInstance();
        playerService.sendPlayerUpExp(this.player, type, exp);
    }

    public void returnTownFromDead() {
        try {
            if (!this.isDead()) return;

            this.currentHP = 1;
            this.player.getPlayerStatus().setLockMove(false);

            short x = 400;
            short y = 5;
            short mapID = (short) (21 + this.player.getGender());

            PlayerService playerService = PlayerService.getInstance();
            playerService.sendPlayerRevive(player);//-16
            GameMap newMap = MapManager.getInstance().findMapById(mapID);
            if (newMap == null) {
                Service.getInstance().sendChatGlobal(this.player.getSession(), null, "Map không tồn tại: " + mapID, false);
                return;
            }
            player.setTeleport(1);
            AreaService.getInstance().gotoMap(this.player, newMap, x, y);
        } catch (Exception ex) {
            LogServer.LogException("returnTownFromDead: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void setDie() {
        // TODO xử lý khi chết
        // set lai location
        // lock move,
        // cancel trade
        // huy skill
        this.currentHP = 0;

        this.player.getPlayerStatus().setLockMove(true);
        PlayerService playerService = PlayerService.getInstance();
        playerService.sendCurrencyHpMp(this.player);
        playerService.sendPlayerDie(this.player);
        playerService.sendPlayerDeathToArea(this.player);
    }

    public void setLive() {
        this.currentHP = this.maxHP;
        this.player.getPlayerStatus().setLockMove(false);
        PlayerService playerService = PlayerService.getInstance();
        playerService.sendCurrencyHpMp(this.player);
        playerService.sendPlayerRevive(this.player);
        playerService.sendPlayerReviveToArea(this.player);
    }

    @Override
    public String toString() {
        return "";
    }

}
