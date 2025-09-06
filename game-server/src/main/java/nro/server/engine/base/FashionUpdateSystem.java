package nro.server.engine.base;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.consts.ConstItem;
import nro.server.data_holders.data.ItemData;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.model.ecs.component.item.ItemInfoComponent;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;
import nro.server.model.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public final class FashionUpdateSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(FashionUpdateSystem.class);

    private ComponentMapper<InventoryComponent> inventoryMapper;
    private ComponentMapper<AppearanceComponent> fashionMapper;
    private ComponentMapper<ItemInfoComponent> itemInfoMapper;
    private ComponentMapper<QuestInstanceComponent> taskMapper;

    public FashionUpdateSystem() {
        super(Aspect.all(
                InventoryComponent.class,
                AppearanceComponent.class,
//                ItemInfoComponent.class,
                QuestInstanceComponent.class
        ));
    }

    @Override
    protected void process(int entityId) {
        try {
            handler(entityId);
        } catch (Throwable thirow) {
            InventoryComponent inv = inventoryMapper.get(entityId);
            if (inv != null) inv.isDirty = false;
            log.error("FashionUpdateSystem error for entityId={}", entityId, thirow);
        }
    }

    private void handler(int entityId) {
        InventoryComponent inventory = inventoryMapper.get(entityId);
        AppearanceComponent appearance = fashionMapper.get(entityId);
        QuestInstanceComponent task = taskMapper.get(entityId);

        if (inventory == null || appearance == null || task == null) return;

        if (!inventory.isDirty) return;

        log.debug("load system fashion update for entity: {}", entityId);
        // khi người người chơi thay đồ, dùng skill thay đổi hình thể thì sẽ reset lại và load lại fashion
        this.resetFashion(appearance);

        for (int i = 0; i < inventory.itemsBody.size(); i++) {
            int itemEntityId = inventory.itemsBody.get(i);

            if (itemEntityId == -1) continue; // ô trống

            ItemInfoComponent itemInfo = itemInfoMapper.get(itemEntityId);
            if (itemInfo == null) continue;

            ItemTemplate template = ItemData.getInstance().getItemTemplates().get(itemInfo.templateId);
            if (template == null) continue;

            switch (i) {
                case ConstItem.TYPE_CAI_TRANG_OR_AVATAR -> {
                    if (template.head() != -1) appearance.head = template.head();
                    if (template.body() != -1) appearance.body = template.body();
                    if (template.leg() != -1) appearance.leg = template.leg();
                    appearance.idHat = template.id();
                }
                case ConstItem.TYPE_AO -> {
                    if (appearance.body == -1) appearance.body = template.body();
                }
                case ConstItem.TYPE_QUAN -> {
                    if (appearance.leg == -1) appearance.leg = template.leg();
                }
                case 8 -> appearance.flagBag = template.part();
            }

            if (appearance.head == -1) {
                appearance.head = appearance.headDefault;
            }
        }


        // hiển thị em bé sau lưng khi đến nhiệm vụ 3-2
        if (appearance.flagBag == -1 && task.questId == 3 && task.currentStep == 2) {
            appearance.flagBag = 28;
        }

        log.debug("head : {}, body: {}, leg: {}, flagBag: {}, aura: {}, effSetItem: {}, idHat: {}", appearance.head, appearance.body, appearance.leg, appearance.flagBag, appearance.aura, appearance.effSetItem, appearance.idHat);
        inventory.isDirty = false;
    }

    private void resetFashion(AppearanceComponent appearance) {
        appearance.head = -1;
        appearance.body = -1;
        appearance.leg = -1;
        appearance.flagBag = -1;
        appearance.aura = -1;
        appearance.effSetItem = -1;
        appearance.idHat = -1;
        appearance.isMonkey = false;
    }
}
