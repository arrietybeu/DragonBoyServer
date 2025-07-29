package nro.server.services;

import com.artemis.ComponentMapper;
import com.artemis.World;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.item.*;
import nro.server.model.ecs.component.player.InventoryComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final class SingletonHolder {
        private static final ItemService instance = new ItemService();
    }

    public static ItemService getInstance() {
        return SingletonHolder.instance;
    }

}
