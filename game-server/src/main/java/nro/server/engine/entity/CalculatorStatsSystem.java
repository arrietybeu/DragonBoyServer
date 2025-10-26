package nro.server.engine.entity;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.engine.InventoryState;
import nro.server.consts.ConstOption;
import nro.server.data_holders.repo.ItemData;
import nro.server.model.ecs.component.item.ItemInfoComponent;
import nro.server.model.ecs.component.item.ItemStatsComponent;
import nro.server.model.ecs.component.HealthComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.item.ItemOptionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Arriety
 */
public class CalculatorStatsSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(CalculatorStatsSystem.class);

    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<StatsComponent> statsMapper;
    private ComponentMapper<InventoryComponent> inventory;

    public CalculatorStatsSystem() {
        super(Aspect.all(InventoryComponent.class, StatsComponent.class, HealthComponent.class));
    }

    @Override
    protected void process(int entityId) {
        InventoryComponent inventory = this.inventory.get(entityId);
        if (inventory.state != InventoryState.LOAD_STATS)
            return;
        try {
            handler(entityId, inventory);
        } catch (Throwable e) {
            log.error("CalculatorStatsSystem error for entityId={}", entityId, e);
        }
    }

    private void handler(int entityId, InventoryComponent inventoryComponent) {
        StatsComponent stats = statsMapper.get(entityId);
        HealthComponent health = healthMapper.get(entityId);
        resetBaseStats(stats, health);
        applyItemBonuses(entityId, inventoryComponent, stats, health);

        inventoryComponent.state = InventoryState.IDLE;
    }

    private void resetBaseStats(StatsComponent stats, HealthComponent health) {
        stats.currentDamage = stats.baseDamage;

        // this.currentHP = this.baseHP;
        // this.currentMP = this.baseMP;
        health.maxHP = stats.baseHp;
        health.maxMP = stats.baseMp;
        stats.totalDefense = stats.baseDefense;
        stats.totalCriticalChance = stats.baseCrit;
    }

    public void applyItemBonuses(int entityId, InventoryComponent inventory, StatsComponent stats,
            HealthComponent health) {

        List<Integer> itemsBody = inventory.itemsBody;
        if (itemsBody == null)
            return;

        for (Integer itemEntityId : itemsBody) {
            ItemInfoComponent itemInfo = world.getMapper(ItemInfoComponent.class).get(itemEntityId);
            ItemStatsComponent itemStats = world.getMapper(ItemStatsComponent.class).get(itemEntityId);
            if (itemInfo == null || itemStats == null)
                continue;

            for (ItemOptionData option : itemStats.options) {
                switch (option.id()) {
                    case ConstOption.TAN_CONG, ConstOption.DAMAGE_PERCENT -> {
                        stats.currentDamage += this.getParamOption(stats.currentDamage, option);
                    }
                    case ConstOption.HP, ConstOption.HP_K, ConstOption.HP_PERCENT -> {
                        health.maxHP += this.getParamOption(health.maxHP, option);
                    }
                    case ConstOption.KI, ConstOption.KI_K, ConstOption.KI_PERCENT -> {
                        health.maxMP += this.getParamOption(health.maxMP, option);
                    }
                    case ConstOption.HP_KI_000, ConstOption.HP_KI -> {
                        health.maxHP += this.getParamOption(health.maxHP, option);
                        health.maxMP += this.getParamOption(health.maxMP, option);
                    }
                    case ConstOption.DEFENSE -> {
                        stats.totalDefense += this.getParamOption(stats.totalDefense, option);
                    }
                    case ConstOption.CRITICAL -> {
                        stats.totalCriticalChance += (byte) this.getParamOption(stats.totalCriticalChance, option);
                    }

                }
            }
        }
    }

    private long getParamOption(long currentPoint, ItemOptionData option) {
        if (option == null)
            return 0;
        return switch (ItemData.getInstance().findTypeItemOption(option.id())) {
            case ConstOption.CONG_PARAM, ConstOption.TRA_VE_PARAM -> option.param();
            case ConstOption.CONG_PARAM_000, ConstOption.CONG_PARAM_K -> option.param() * 1000L;
            case ConstOption.NHAN_PERCENT, ConstOption.CONG_PARAM_PERCENT -> currentPoint * option.param() / 100;
            case ConstOption.TRU_PARAM_PERCENT -> -option.param() / 100;
            default -> 0;
        };
    }

    /**
     * public void applyItemBonuses() {
     * try {
     * List<Item> itemsBody = this.player.getPlayerInventory().getItemsBody();
     * if (itemsBody == null) return;
     * 
     * for (Item item : itemsBody) {
     * if (item == null || item.getTemplate() == null) continue;
     * 
     * if (item.isItemMount()) {
     * this.isHaveMount = true;
     * }
     * 
     * for (ItemOption option : item.getItemOptions()) {
     * if (option == null) continue;
     * 
     * switch (option.getId()) {
     * case ConstOption.TAN_CONG, ConstOption.DAMAGE_PERCENT ->
     * this.currentDamage += this.getParamOption(this.currentDamage, option);
     * case ConstOption.HP, ConstOption.HP_K, ConstOption.HP_PERCENT ->
     * this.maxHP += this.getParamOption(maxHP, option);
     * case ConstOption.KI, ConstOption.KI_K, ConstOption.KI_PERCENT ->
     * this.maxMP += this.getParamOption(maxMP, option);
     * case ConstOption.HP_KI_000, ConstOption.HP_KI -> {
     * this.maxHP += this.getParamOption(maxHP, option);
     * this.maxMP += this.getParamOption(maxMP, option);
     * }
     * case ConstOption.DEFENSE -> this.totalDefense +=
     * this.getParamOption(totalDefense, option);
     * case ConstOption.CRITICAL ->
     * this.totalCriticalChance += (byte) this.getParamOption(totalCriticalChance,
     * option);
     * case ConstOption.TANG_TIEM_NANG_SUC_MANH_PERCENT ->
     * this.percentExpPotentia += (int) this.getParamOption(this.percentExpPotentia,
     * option);
     * }
     * }
     * }
     * } catch (Exception ex) {
     * LogServer.LogException("Error khi tinh toan param option: " +
     * ex.getMessage(), ex);
     * }
     * }
     */
}
