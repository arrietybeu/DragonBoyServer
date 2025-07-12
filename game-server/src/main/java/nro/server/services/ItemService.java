package nro.server.services;

import com.artemis.ComponentMapper;
import com.artemis.World;
import nro.server.data_holders.data.ItemData;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.item.*;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.item.ItemOptionData;
import nro.server.utils.factory.IDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Arriety
 */
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final World world;
    private final ComponentMapper<InventoryComponent> inventoryMapper;
    private final ComponentMapper<DatabaseIdComponent> dbIdMapper;
    private final ComponentMapper<OwnershipComponent> ownerMapper;

    private ItemService() {
        this.world = GameWorld.getInstance().getWorld();
        this.inventoryMapper = world.getMapper(InventoryComponent.class);
        this.dbIdMapper = world.getMapper(DatabaseIdComponent.class);
        this.ownerMapper = world.getMapper(OwnershipComponent.class);
    }

    /**
     * Phương thức chính để tạo một item mới và thêm vào túi đồ của người chơi.
     * <p>Đây là phiên bản ECS của ItemFactory.newItem().</p>
     *
     * @param ownerEntityId ID của entity người chơi sẽ sở hữu item.
     * @param templateId    ID template của item cần tạo.
     * @param quantity      Số lượng.
     * @param options       Danh sách các chỉ số (có thể là null nếu dùng chỉ số mặc định).
     * @return ID của entity item đã được tạo, hoặc -1 nếu thất bại.
     */
    public int createNewItemAndGiveToPlayer(int ownerEntityId, int templateId, int quantity, List<ItemOptionData> options) {
        // Kiểm tra xem template có tồn tại không
        var itemTemplate = ItemData.getInstance().getItemTemplates().get((short) templateId);
        if (itemTemplate == null) {
            log.warn("Attempted to create item with invalid templateId: {}", templateId);
            return -1;
        }

        // 1. Lấy một ID vĩnh viễn mới từ Factory
        int newDbId = IDFactory.getInstance().nextId();

        // 2. Tạo một entity ECS mới cho vật phẩm
        int itemEntityId = world.create();
        var editor = world.edit(itemEntityId);

        // 3. Gắn các component cần thiết
        editor.add(new DatabaseIdComponent(newDbId));
        editor.add(new ItemInfoComponent(templateId, quantity));
        editor.add(new OwnershipComponent(ownerEntityId, ItemLocation.BAG));

        // 4. Gắn component chỉ số nếu có
        ItemStatsComponent statsComponent = new ItemStatsComponent();
        if (options != null && !options.isEmpty()) {
            statsComponent.options.addAll(options);
        } else {
            // Nếu không có option truyền vào, lấy option mặc định từ template
            statsComponent.options.addAll(itemTemplate.options());
        }
        editor.add(statsComponent);

        // 5. Thêm item vào túi đồ của người chơi
        InventoryComponent inventory = inventoryMapper.get(ownerEntityId);
        if (inventory != null) {
            inventory.itemsBag.add(itemEntityId);
            // TODO: Gửi packet cập nhật túi đồ về cho client
        } else {
            log.error("Player {} has no InventoryComponent to receive item {}", ownerEntityId, itemEntityId);
        }

        // TODO: Đưa item này vào hàng đợi để lưu vào CSDL
        // ItemDAO.queueForSave(newDbId, templateId, ...);

        log.info("Created new item entityId: {}, dbId: {} for player {}", itemEntityId, newDbId, ownerEntityId);
        return itemEntityId;
    }

    /**
     * Xóa vĩnh viễn một vật phẩm khỏi game.
     *
     * @param itemEntityId ID của entity vật phẩm cần xóa.
     */
    public void deleteItem(int itemEntityId) {
        if (!dbIdMapper.has(itemEntityId)) {
            log.warn("Attempted to delete an item entity {} without a DatabaseIdComponent.", itemEntityId);
            return;
        }

        int dbId = dbIdMapper.get(itemEntityId).dbId;
        int ownerId = ownerMapper.has(itemEntityId) ? ownerMapper.get(itemEntityId).ownerEntityId : -1;

        if (ownerId != -1 && inventoryMapper.has(ownerId)) {
            InventoryComponent inventory = inventoryMapper.get(ownerId);
            inventory.itemsBag.remove((Integer) itemEntityId);
            inventory.itemsBody.remove((Integer) itemEntityId);
            inventory.itemsBox.remove((Integer) itemEntityId);
            // TODO: Gửi packet cập nhật túi đồ về cho client
        }

        // 3. Xóa khỏi CSDL (nên thực hiện bất đồng bộ)
        // ItemDAO.deleteFromDbAsync(dbId);

        // 4. Trả ID vĩnh viễn về cho Factory
        IDFactory.getInstance().releaseId(dbId);

        // 5. Xóa entity khỏi thế giới game
        world.delete(itemEntityId);
        log.info("Deleted item entityId: {}, dbId: {}", itemEntityId, dbId);
    }

    private static final class SingletonHolder {
        private static final ItemService instance = new ItemService();
    }

    public static ItemService getInstance() {
        return SingletonHolder.instance;
    }

}
