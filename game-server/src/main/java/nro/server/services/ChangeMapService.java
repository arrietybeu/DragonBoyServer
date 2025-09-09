package nro.server.services;

import com.artemis.Entity;
import lombok.NoArgsConstructor;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.map.MapChangeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ChangeMapService {

    private static final Logger log = LoggerFactory.getLogger(ChangeMapService.class);

    public static void requestChangeMap(Entity entity, MapChangeType changeType, short mapId, int zoneId, short x,
                                        short y) {
        if (entity == null) {
            log.warn("[ChangeMapService] Entity null khi request chuyển map");
            return;
        }
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        if (pos == null) {
            log.warn("[ChangeMapService] Entity {} không có PositionComponent", entity.getId());
            return;
        }
        switch (changeType) {
            case MapChangeType.DEFAULT, MapChangeType.ZONE -> {
                pos.changeType = changeType;
                pos.setMapTarget(mapId);
                pos.setZoneTarget(zoneId);
                pos.newX = x;
                pos.newY = y;
                pos.teleport = 0;
                pos.wantsToChangeMap = true;
            }
            case MapChangeType.SHIP -> {
                pos.changeType = changeType;
                pos.setMapTarget(mapId);
                pos.setZoneTarget(zoneId);
                pos.newX = x;
                pos.newY = y;
                pos.teleport = TypeTeleport.SPACE_SHIP_FOR_GENDER.getValue();
                pos.wantsToChangeMap = true;
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + changeType);
        }
    }
}
