package nro.server.service.model.entity.player;

import nro.consts.ConstOption;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.server.config.ConfigServer;
import nro.server.manager.CaptionManager;
import nro.server.manager.ItemManager;
import nro.server.manager.MapManager;
import nro.server.service.model.entity.Entity;
import nro.server.system.LogServer;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.player.PlayerService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.Points;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.item.Item;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.template.item.ItemOption;
import nro.utils.Util;

import java.util.List;

public class PlayerPoints extends Points {

    private final Player player;

    public PlayerPoints(Entity entity) {
        super(entity);
        player = (Player) this.getOwner();
    }

    @Override
    public Points copy(Entity entity) {
        return null;
    }

    @Override
    public void calculateStats() {
        this.resetBaseStats();
        this.applyItemBonuses();
        this.setPointOverflow();
    }

    @Override
    public void resetBaseStats() {
        this.currentDamage = this.baseDamage;

//        this.currentHP = this.baseHP;
//        this.currentMP = this.baseMP;

        this.maxHP = this.baseHP;
        this.maxMP = this.baseMP;
        this.totalDefense = this.baseDefense;
        this.totalCriticalChance = this.baseCriticalChance;
        this.percentExpPotentia = 0;
        this.isHaveMount = false;
    }

    @Override
    public void reduceMPWhenFlying() {
        if (this.isHaveMount()) return;

        long currentMp = this.getCurrentMP();
        if (currentMp <= 0) return;

        long subMp = Math.max(1, this.getMaxMP() / 100 * 2);
        this.setCurrentMp(Math.max(0, currentMp - subMp));

        PlayerService.getInstance().sendMpForPlayer(player);
    }

    @Override
    public void applyItemBonuses() {
        try {
            List<Item> itemsBody = this.player.getPlayerInventory().getItemsBody();
            if (itemsBody == null) return;

            for (Item item : itemsBody) {
                if (item == null || item.getTemplate() == null) continue;

                if (item.isItemMount() && item.getTemplate().part() >= 0) {
                    this.isHaveMount = true;
                }

                for (ItemOption option : item.getItemOptions()) {
                    if (option == null) continue;

                    switch (option.getId()) {
                        case ConstOption.TAN_CONG, ConstOption.DAMAGE_PERCENT ->
                                this.currentDamage += this.getParamOption(this.currentDamage, option);
                        case ConstOption.HP, ConstOption.HP_K, ConstOption.HP_PERCENT ->
                                this.maxHP += this.getParamOption(maxHP, option);
                        case ConstOption.KI, ConstOption.KI_K, ConstOption.KI_PERCENT ->
                                this.maxMP += this.getParamOption(maxMP, option);
                        case ConstOption.HP_KI_000, ConstOption.HP_KI -> {
                            this.maxHP += this.getParamOption(maxHP, option);
                            this.maxMP += this.getParamOption(maxMP, option);
                        }
                        case ConstOption.DEFENSE -> this.totalDefense += this.getParamOption(totalDefense, option);
                        case ConstOption.CRITICAL ->
                                this.totalCriticalChance += (byte) this.getParamOption(totalCriticalChance, option);
                        case ConstOption.TANG_TIEM_NANG_SUC_MANH_PERCENT ->
                                this.percentExpPotentia += (int) this.getParamOption(this.percentExpPotentia, option);
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("Error khi tinh toan param option: " + ex.getMessage(), ex);
        }
    }

    private void setPointOverflow() {
        if (this.currentHP > this.maxHP) {
            this.currentHP = this.maxHP;
        }

        if (this.currentMP > this.maxMP) {
            this.currentMP = this.maxMP;
        }

        if (this.currentHP < 0) {
            this.currentHP = 0;
        }

        if (this.currentMP < 0) {
            this.currentMP = 0;
        }

        if (this.currentDamage < 0) {
            this.currentDamage = 1;
        }
    }


    @Override
    public long getParamOption(long currentPoint, ItemOption option) {
        if (option == null) return 0;
        return switch (ItemManager.getInstance().findTypeItemOption(option.getId())) {
            case ConstOption.CONG_PARAM, ConstOption.TRA_VE_PARAM -> option.getParam();
            case ConstOption.CONG_PARAM_000, ConstOption.CONG_PARAM_K -> option.getParam() * 1000L;
            case ConstOption.NHAN_PERCENT, ConstOption.CONG_PARAM_PERCENT -> currentPoint * option.getParam() / 100;
            case ConstOption.TRU_PARAM_PERCENT -> -option.getParam() / 100;
            default -> 0;
        };
    }

    @Override
    public void addExp(int type, long exp) {
        try {
            PlayerService playerService = PlayerService.getInstance();
            long step = 5_000_000;
            while (exp > 0) {
                long addAmount = Math.min(exp, step);

                switch (type) {
                    case 0 -> this.power += addAmount;
                    case 1 -> this.potentialPoints += addAmount;
                    case 2 -> {
                        this.power += addAmount;
                        this.potentialPoints += addAmount;
                    }
                }
                playerService.sendPlayerUpExp(this.player, type, (int) addAmount);
                exp -= addAmount;
            }
            this.player.getPlayerTask().checkDoneTaskUpgradeExp(this.getPower());
        } catch (Exception ex) {
            LogServer.LogException("Error adding exp: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void subExp(int type, long exp) {
        if (power < exp || potentialPoints < 0) return;
        PlayerService playerService = PlayerService.getInstance();
        long step = 5_000_000;
        while (exp > 0) {
            long subAmount = Math.min(exp, step);

            switch (type) {
                case 0 -> this.power -= subAmount;
                case 1 -> this.potentialPoints -= subAmount;
                case 2 -> {
                    this.power -= subAmount;
                    this.potentialPoints -= subAmount;
                }
            }
            playerService.sendPlayerUpExp(this.player, type, (int) -subAmount);
            exp -= subAmount;
        }
    }

    @Override
    public void returnTownFromDead() {
        try {
            if (!this.isDead()) return;

            this.currentHP = 1;
            this.player.getPlayerStatus().setLockMove(false);

            short x = 400;
            short y = 5;
            short mapID = (short) (21 + this.player.getGender());

            PlayerService playerService = PlayerService.getInstance();
            playerService.sendPlayerRevive(player);// -16
            GameMap newMap = MapManager.getInstance().findMapById(mapID);
            if (newMap == null) {
                ServerService.getInstance().sendChatGlobal(this.player.getSession(), null, "Map không tồn tại: " + mapID, false);
                return;
            }
            player.setTeleport(1);
            AreaService.getInstance().gotoMap(this.player, newMap.getArea(-1, player), x, y);
        } catch (Exception ex) {
            LogServer.LogException("returnTownFromDead: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
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

    @Override
    public void setLive() {
        this.currentHP = this.maxHP;
        this.player.getPlayerStatus().setLockMove(false);
        PlayerService playerService = PlayerService.getInstance();
        playerService.sendCurrencyHpMp(this.player);
        playerService.sendPlayerRevive(this.player);
        playerService.sendPlayerReviveToArea(this.player);
    }

    @Override
    public void upPotentialPoint(int type, int point) {
        try {
            ServerService serverService = ServerService.getInstance();
            PlayerService playerService = PlayerService.getInstance();

            if (this.potentialPoints < point) {
                ServerService.dialogMessage(this.player.getSession(), String.format("Bạn chỉ có %s điểm tiềm năng. Hãy luyện tập thêm để có đủ %s", Util.numberToString(this.potentialPoints), point));
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
                            serverService.sendHideWaitDialog(player);
                            yield -1;
                        }
                    };

                    if (multiplier == -1) return;

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
                            serverService.sendHideWaitDialog(player);
                            yield -1;
                        }
                    };

                    if (multiplier == -1) return;

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
                            serverService.sendHideWaitDialog(player);
                            yield -1;
                        }
                    };

                    if (multiplier == -1) return;

                    potentiaUse = multiplier;
                    currentPoint = currentDamage;
                }
                case ConstPlayer.UP_POTENTIAL_DEFENSE -> {
                    //TODO fix bug
                    final var currentBaseDefense = this.baseDefense;
                    potentiaUse = point * (500_000 + currentBaseDefense * 100_000L);
                    currentPoint = currentBaseDefense;
                }
                case ConstPlayer.UP_POTENTIAL_CRITICAL -> {
                    final var currentBaseCritical = this.baseCriticalChance;
                    if (currentBaseCritical > ConstPlayer.potentialUse.length) {
                        serverService.sendHideWaitDialog(player);
                        return;
                    }
                    potentiaUse = ConstPlayer.potentialUse[currentBaseCritical];
                    currentPoint = currentBaseCritical;
                }

                default -> {
                    LogServer.LogWarning("upPotentialPoint: status không tồn tại: " + type);
                    serverService.sendHideWaitDialog(player);
                    return;
                }
            }

            if (potentiaUse < 0) return;
            if (potentiaUse > 0 && this.isUpgradePotential(type, potentiaUse, currentPoint, point)) {
                playerService.sendPointForMe(player);
                this.player.getPlayerTask().checkDoneTask(3, 0);
            }
        } catch (Exception ex) {
            LogServer.LogException("upPotentialPoint: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean isUpgradePotential(int type, long potentiaUse, final long currentPoint, int point) {
        if (this.potentialPoints < potentiaUse) {
            ServerService.dialogMessage(this.player.getSession(), String.format("Bạn chỉ có %s điểm tiềm năng. Hãy luyện tập thêm để có đủ %s", Util.numberToString(this.potentialPoints), potentiaUse));
            return false;
        }
        System.out.println("type: " + type);

        switch (type) {
            case ConstPlayer.UP_POTENTIAL_HP -> this.baseHP = (int) (currentPoint + 20L * point);
            case ConstPlayer.UP_POTENTIAL_MP -> this.baseMP = (int) (currentPoint + 20L * point);
            case ConstPlayer.UP_POTENTIAL_DAMAGE -> this.baseDamage = (int) (currentPoint + (long) point);
            case ConstPlayer.UP_POTENTIAL_DEFENSE -> this.baseDefense += point;
            case ConstPlayer.UP_POTENTIAL_CRITICAL -> this.baseCriticalChance += 1;
            default -> {
                LogServer.LogWarning("isUpgradePotential: status không tồn tại: " + type);
                return false;
            }
        }
        System.out.println("potentiaUse: " + potentiaUse);

        this.potentialPoints -= potentiaUse;
        this.calculateStats();
        return true;
    }

    public void healPlayer() {
        PlayerService playerService = PlayerService.getInstance();
        this.setCurrentHp(this.getMaxHP());
        this.setCurrentMp(this.getMaxMP());
        playerService.sendHpForPlayer(player);
        playerService.sendMpForPlayer(player);
    }

    public long getPotentialPointsAttack(Monster monster, long damage) {
        long exp;

        byte levelPlayer = (byte) CaptionManager.getInstance().getLevel(player);
        int levelDiff = levelPlayer - monster.getPoint().getLevel();

        if (monster.getTemplateId() == 0) {
            if (Util.nextInt(100) > 65) {
                return -1;
            }
            return 1;
        }

        double pDameHit = (double) damage * 100 / monster.getPoint().getMaxHp();

        exp = (long) (pDameHit * monster.getPoint().getMaxHp() / 100);

        if (exp <= 0) exp = 1;

        if (levelDiff >= 0) {
            for (int i = 0; i < levelDiff; i++) {
                long sub;
                if (levelDiff >= 3) {
                    if (levelPlayer >= 14) {
                        sub = 35;
                    } else if (levelPlayer == 13) {
                        sub = 15;
                    } else if (levelPlayer == 12) {
                        sub = 7;
                    } else {
                        sub = 0;
                    }
                    sub += Util.nextInt(26) + 20; // 20 -> 45
                } else {
                    sub = Util.nextInt(6) + 10; // 10 -> 15
                }

                long subExp = (exp * sub) / 100;
                if (subExp <= 0) subExp = 1;

                exp -= subExp;
            }
        } else {
            for (int i = 0; i < -levelDiff; i++) {
                if (levelPlayer >= 13) {
                    exp -= exp * (Util.nextInt(26) + 25) / 100; // 25 -> 50%
                    continue;
                }

                long add = (exp * (Util.nextInt(9) + 2)) / 100; // 2 -> 10%
                if (add <= 0) break;

                exp += add;
            }
        }

        if (exp <= 0) exp = 1;

//        if (monster.getPoint().isSieuQuai()) {
//            exp *= 2;
//        }

        // Bonus nếu có từ item
        if (this.percentExpPotentia > 0) {
            exp += (exp * this.percentExpPotentia) / 100;
        }

        exp *= ConfigServer.EXP_RATE;

        if (exp < 1) {
            LogServer.LogException("Player name " + this.player.getName() + " Exception exp < 1: " + exp);
            exp = 1;
        }

        long finalExp = Util.nextInt((int) (exp * 70 / 100), (int) (exp * 120 / 100));

//        System.out.println("exp: " + exp + " finalExp: " + finalExp);
        return finalExp;
    }


}
