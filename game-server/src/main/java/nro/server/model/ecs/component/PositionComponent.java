package nro.server.model.ecs.component;

import com.artemis.Component;
import nro.server.model.map.MapChangeType;

/**
 * @author Arriety
 */
public class PositionComponent extends Component {

    public short mapId;

    private int zoneID;
    private int zoneTarget, mapTarget = -1;

    public short x, y, newX, newY;

    public byte teleport = 0;
    public boolean isDirtyMove;
    public byte isOnGround, isOnGroundNew;
    public boolean wantsToChangeMap;

    public MapChangeType changeType;

    public PositionComponent() {
    }

    public PositionComponent(int mapId, short x, short y, int areaId) {
        this.mapId = (short) mapId;
        this.x = x;
        this.y = y;
        this.zoneID = areaId;
    }

    public void setAreaId(int areaId) {
        if (this.zoneID != areaId)
            this.zoneID = areaId;
    }

    public int getAreaId() {
        return zoneID;
    }

    public int getZoneTarget() {
        return zoneTarget;
    }

    public void setZoneTarget(int zoneTarget) {
        this.zoneTarget = zoneTarget;
    }

    public int getMapTarget() {
        return mapTarget;
    }

    public void setMapTarget(int mapTarget) {
        this.mapTarget = mapTarget;
    }

    @Override
    public String toString() {
        return "PositionComponent{" +
                "mapId=" + mapId +
                ", areaId=" + zoneID +
                ", x=" + x +
                ", y=" + y +
                ", newX=" + newX +
                ", newY=" + newY +
                ", teleport=" + teleport +
                ", isDirtyMove=" + isDirtyMove +
                ", isOnGround=" + isOnGround +
                ", isOnGroundNew=" + isOnGroundNew +
                ", wantsToChangeMap=" + wantsToChangeMap +
                ", changeType=" + changeType +
                ", mapTarget=" + mapTarget +
                ", zoneTarget=" + zoneTarget +
                '}';
    }
}