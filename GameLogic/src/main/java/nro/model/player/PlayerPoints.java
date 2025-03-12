package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstOption;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.map.GameMap;
import nro.model.template.entity.SkillInfo;
import nro.server.LogServer;
import nro.server.manager.ItemManager;
import nro.server.manager.MapManager;
import nro.service.AreaService;
import nro.service.PlayerService;
import nro.service.Service;
import nro.utils.Util;

@Getter
@Setter
@ToString
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
        } else
            this.currentHP = Math.min(hp, this.maxHP);
    }

    public void setCurrentMp(long mp) {
        if (mp < 0) {
            this.currentMP = 0;
        } else
            this.currentMP = Math.min(mp, this.maxMP);
    }

    public void subCurrentHp(long hp) {
        this.currentHP -= hp;
        if (this.currentHP < 0) {
            this.currentHP = 0;
        }
    }

    public long getDameAttack() {
        long dame = this.currentDamage;
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
        this.setDefense();
        this.setCritical();
    }

    private void setHp() {
        long hpKi000 = this.getParamOption(ConstOption.HP_KI_000, 0, 0) * 1000;
        long hpK = this.getParamOption(ConstOption.HP_K, 2, 0) * 1000;
        long hp = this.getParamOption(ConstOption.HP, 1, 0);
        long hpKi = this.getParamOption(ConstOption.HP_KI, 2, 0);

        this.maxHP = this.baseHP + hpKi000 + hpK + hp + hpKi;
        this.maxHP = this.getParamOption(ConstOption.HP_PERCENT, 0, this.maxHP);
    }

    private void setMp() {
        long mpKi000 = this.getParamOption(ConstOption.HP_KI_000, 0, 0) * 1000;
        long mpK = this.getParamOption(ConstOption.KI_K, 2, 0) * 1000;
        long mp = this.getParamOption(ConstOption.KI, 1, 0);
        long mpKi = this.getParamOption(ConstOption.HP_KI, 2, 0);

        this.maxMP = this.baseMP + mpKi000 + mpK + mp + mpKi;
        this.maxMP = this.getParamOption(ConstOption.KI_PERCENT, 0, this.maxMP);
    }

    private void setDame() {
        this.currentDamage = this.baseDamage;
        long tanCong = this.getParamOption(ConstOption.TAN_CONG, 0, 0);
        this.currentDamage = this.currentDamage + tanCong;
    }

    private void setDefense() {
        long defense = this.getParamOption(ConstOption.DEFENSE, 0, 0);
        this.totalDefense = this.baseDefense + defense;
    }

    private void setCritical() {
        long critical = this.getParamOption(ConstOption.CRITICAL, 2, 0);
        this.totalCriticalChance = (byte) (this.baseCriticalChance + critical);
    }

    private long getDameSkill() {
        try {
            SkillInfo skillSelect = this.player.getPlayerSkill().skillSelect;
            return switch (skillSelect.getTemplate().getId()) {
                case ConstSkill.DRAGON, ConstSkill.DEMON, ConstSkill.GALICK -> skillSelect.getDamage();
                default -> 0;
            };
        } catch (Exception ex) {
            LogServer.LogException(" getSkillDamageMultiplier: " + ex.getMessage(), ex);
            return 0;
        }
    }

    private long getParamOption(int id, int type, long quantity) {
        try {
            long param = 0;

//            type = ItemManager.getInstance().findTypeItemOption(id);

            if (type == 1 || type == 2) {
                param = quantity;
            }

            for (int i = 0; i < this.player.getPlayerInventory().getItemsBody().size(); i++) {
                Item itemBody = this.player.getPlayerInventory().getItemsBody().get(i);
                if (itemBody.getTemplate() == null)
                    continue;
                var countOption = itemBody.getItemOptions().size();
                for (int o = 0; o < countOption; o++) {
                    ItemOption itemOption = itemBody.getItemOptions().get(o);
                    if (itemOption == null)
                        continue;
                    if (itemOption.getId() == id) {
                        switch (type) {
                            case 1 -> param = param + param * itemOption.getParam() / 100;
                            case 0, 2 -> param = param * itemOption.getParam() / 100;
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
        } catch (Exception ex) {
            LogServer.LogException("getParamOption: " + ex.getMessage(), ex);
            return 1;
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
            if (!this.isDead())
                return;

            this.currentHP = 1;
            this.player.getPlayerStatus().setLockMove(false);

            short x = 400;
            short y = 5;
            short mapID = (short) (21 + this.player.getGender());

            PlayerService playerService = PlayerService.getInstance();
            playerService.sendPlayerRevive(player);// -16
            GameMap newMap = MapManager.getInstance().findMapById(mapID);
            if (newMap == null) {
                Service.getInstance().sendChatGlobal(this.player.getSession(), null, "Map không tồn tại: " + mapID,
                        false);
                return;
            }
            player.getPlayerStatus().setTeleport(1);
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

    public void upPotentialPoint(int type, int point) {
        try {
            Service service = Service.getInstance();
            PlayerService playerService = PlayerService.getInstance();

            if (this.potentialPoints < point) {
                Service.dialogMessage(this.player.getSession(),
                        String.format("Bạn chỉ có %s điểm tiềm năng. Hãy luyện tập thêm để có đủ %s",
                                Util.numberToString(this.potentialPoints), point));
                return;
            }

            long potentiaUse;
            long currentPoint;
            switch (type) {
                case ConstPlayer.UP_POTENTIAL_HP -> {
                    final var currentBaseHp = this.baseHP;
                    final long multiplier = switch (point) {
                        case 1 -> 1;
                        case 10 -> 10;
                        case 100 -> 100;
                        default -> {
                            LogServer.LogWarning("upPotentialPoint HP: point không tồn tại: " + point);
                            service.sendHideWaitDialog(player);
                            yield -1;
                        }
                    };

                    if (multiplier == -1)
                        return;

                    potentiaUse = multiplier * (2L * (currentBaseHp + 1000) + (multiplier == 1 ? 0 : (multiplier == 10 ? 180 : 1980))) / 2;
                    currentPoint = currentBaseHp;
                }

                case ConstPlayer.UP_POTENTIAL_MP -> {
                    final var currentBaseMp = this.baseMP;
                    final long multiplier = switch (point) {
                        case 1 -> 1;
                        case 10 -> 10;
                        case 100 -> 100;
                        default -> {
                            LogServer.LogWarning("upPotentialPoint MP: point không tồn tại: " + point);
                            service.sendHideWaitDialog(player);
                            yield -1;
                        }
                    };

                    if (multiplier == -1)
                        return;

                    potentiaUse = multiplier * (2L * (currentBaseMp + 1000) + (multiplier == 1 ? 0 : (multiplier == 10 ? 180 : 1980))) / 2;
                    currentPoint = currentBaseMp;
                }
                case ConstPlayer.UP_POTENTIAL_DAMAGE -> {
                    final long currentDamage = this.baseDamage;

                    final long multiplier = switch (point) {
                        case 1 -> currentDamage * 100;
                        case 10 -> 10 * (2 * currentDamage + 9) / 2 * 100;
                        case 100 -> 100 * (2 * currentDamage + 99) / 2 * 100;
                        default -> {
                            LogServer.LogWarning("upPotentialPoint MP: point không tồn tại: " + point);
                            service.sendHideWaitDialog(player);
                            yield -1;
                        }
                    };

                    if (multiplier == -1)
                        return;

                    potentiaUse = multiplier;
                    currentPoint = currentDamage;
                }
                case ConstPlayer.UP_POTENTIAL_DEFENSE -> {
                    final var currentBaseDefense = this.baseDefense;
                    potentiaUse = 2L * (currentBaseDefense + 5) * 2 / 100000;
                    currentPoint = currentBaseDefense;
                }
                case ConstPlayer.UP_POTENTIAL_CRITICAL -> {
                    final var currentBaseCritical = this.baseCriticalChance;
                    potentiaUse = (long) (50_000_000 * Math.pow(5, point));
                    currentPoint = currentBaseCritical;
                }
                default -> {
                    LogServer.LogWarning("upPotentialPoint: type không tồn tại: " + type);
                    service.sendHideWaitDialog(player);
                    return;
                }
            }

            if (potentiaUse > 0 && this.isUpgradePotential(type, potentiaUse, currentPoint, point)) {
                playerService.sendPointForMe(player);
                this.player.getPlayerTask().checkDoneTaskUpgradedPotential();
            }
        } catch (Exception ex) {
            LogServer.LogException("upPotentialPoint: " + ex.getMessage(), ex);
        }
    }

    public void healPlayer() {
        PlayerService playerService = PlayerService.getInstance();
        this.setCurrentHp(this.getMaxHP());
        this.setCurrentMp(this.getMaxMP());
        playerService.sendHpForPlayer(player);
        playerService.sendMpForPlayer(player);
    }

    private boolean isUpgradePotential(int type, long potentiaUse, final long currentPoint, int point) throws Exception {
        if (this.potentialPoints < potentiaUse) {
            Service.dialogMessage(this.player.getSession(),
                    String.format("Bạn chỉ có %s điểm tiềm năng. Hãy luyện tập thêm để có đủ %s",
                            Util.numberToString(this.potentialPoints), potentiaUse));
            return false;
        }

        switch (type) {
            case ConstPlayer.UP_POTENTIAL_HP -> this.baseHP = (int) (currentPoint + 20L * point);
            case ConstPlayer.UP_POTENTIAL_MP -> this.baseMP = (int) (currentPoint + 20L * point);
            case ConstPlayer.UP_POTENTIAL_DAMAGE -> this.baseDamage = (int) (currentPoint + (long) point);
            case ConstPlayer.UP_POTENTIAL_DEFENSE -> this.baseDefense += 1;
            case ConstPlayer.UP_POTENTIAL_CRITICAL -> this.baseCriticalChance += 1;
            default -> {
                LogServer.LogWarning("isUpgradePotential: type không tồn tại: " + type);
                return false;
            }
        }

        this.potentialPoints -= potentiaUse;
        this.setPoint();
        return true;
    }
}
