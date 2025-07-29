package nro.server.engine.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.consts.ConstItem;
import nro.server.data_holders.data.ItemData;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.model.ecs.component.item.ItemInfoComponent;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.ecs.component.player.QuestComponent;
import nro.server.model.templates.item.ItemTemplate;

/**
 * @author Arriety
 */
public class FashionUpdateSystem extends IteratingSystem {

    private ComponentMapper<InventoryComponent> inventoryMapper;
    private ComponentMapper<AppearanceComponent> fashionMapper;
    private ComponentMapper<ItemInfoComponent> itemInfoMapper;
    private ComponentMapper<QuestComponent> taskMapper;

    public FashionUpdateSystem() {
        super(Aspect.all(InventoryComponent.class, AppearanceComponent.class));
    }

    @Override
    protected void process(int entityId) {
        InventoryComponent inventory = inventoryMapper.get(entityId);

        if (!inventory.isDirty) return;
        AppearanceComponent appearance = fashionMapper.get(entityId);
        System.out.println("load system fashion update for entity: " + entityId );
        // reset
        appearance.head = appearance.body = appearance.leg = appearance.flagBag = appearance.aura = appearance.effSetItem = appearance.idHat = -1;
        appearance.isMonkey = false;

        for (int i = 0; i < inventory.itemsBody.size(); i++) {
            int itemEntityId = inventory.itemsBody.get(i);

            if (itemEntityId == -1) continue; // Ô trống

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
                    System.out.println("entity id: " + entityId + " cho cai ao : " + appearance.body);
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

        QuestComponent task = taskMapper.get(entityId);
        if (appearance.flagBag == -1 && task != null && task.taskId == 3 && task.taskIndex == 2) {
            appearance.flagBag = 28;
        }

        System.out.println("head : " + appearance.head + ", body: " + appearance.body + ", leg: " + appearance.leg +
                ", flagBag: " + appearance.flagBag + ", aura: " + appearance.aura + ", effSetItem: " + appearance.effSetItem +
                ", idHat: " + appearance.idHat);
        inventory.isDirty = false;
    }
}
