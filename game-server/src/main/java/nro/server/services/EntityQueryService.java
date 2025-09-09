package nro.server.services;


import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;

import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;

/**
 * @author Arriety
 */
public final class EntityQueryService {

    private final World world;
    private final ComponentMapper<PlayerComponent> pcMapper;
    private final ComponentMapper<PositionComponent> posMapper;

    public EntityQueryService(World world) {
        this.world = world;
        this.pcMapper = world.getMapper(PlayerComponent.class);
        this.posMapper = world.getMapper(PositionComponent.class);
    }

    public Entity getPlayerEntity(int entityId) {
        if (!world.getEntityManager().isActive(entityId)) return null;
        if (!pcMapper.has(entityId)) return null;
        return world.getEntity(entityId);
    }

    public ImmutableBag<Entity> getAllPlayerEntity() {
        return null;
    }

    /**
     * giải thích qua null ở đây là nếu khi get safe xong mà entity đó không có cái
     * component đó thì sẽ trả về componnent đã truyền vào trong fallback
     *
     * @param entityId
     * @return
     */
    public PlayerComponent getPlayer(int entityId) {
        return this.pcMapper.getSafe(entityId, null); // null nếu không có
    }

    public PositionComponent getPosition(int entityId) {
        return this.posMapper.getSafe(entityId, null);
    }

}
