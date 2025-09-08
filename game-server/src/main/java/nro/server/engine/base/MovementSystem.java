package nro.server.engine.base;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.server_packets.handler.SmPlayerMove;
import nro.server.services.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public final class MovementSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(MovementSystem.class);
    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<InfoComponent> playerInfoMapper;

    public MovementSystem() {
        super(Aspect.all(PositionComponent.class, InfoComponent.class));
    }

    @Override
    protected void process(int entityId) {
        try {
            handler(entityId);
        } catch (Throwable e) {
            PositionComponent pos = posMapper.get(entityId);
            if (pos != null) pos.isDirtyMove = false;
            log.error("MovementSystem error for entityId={}", entityId, e);
        }
    }

    private void handler(int entityId) {
        PositionComponent pos = posMapper.get(entityId);
        InfoComponent playerInfo = playerInfoMapper.get(entityId);
        if (pos == null)
            throw new RuntimeException("No position found for entityId=" + entityId);

        if (!pos.isDirtyMove) return;

        pos.x = pos.newX;
        pos.y = pos.newY;
        pos.isOnGround = pos.isOnGroundNew;

//        System.out.println("entity di chuyen : " + entityId + " to " + pos.x + ", " + pos.y);
        if (pos.isOnGround == 1) {
//         player.getPoints().reduceMPWhenFlying();
        }

        AreaService.getInstance().sendPacketForALLPlayerInAreaNotMe(entityId, pos.mapId, pos.getAreaId(),
                new SmPlayerMove(playerInfo.id, pos.x, pos.y));

        pos.isDirtyMove = false;
    }

}
