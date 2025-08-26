package nro.server.model.ecs.component;


import com.artemis.Component;
import lombok.Getter;

/**
 * @author Arriety
 */
public class PositionComponent extends Component {

    public short mapId;

    @Getter
    private int areaId;

    public short x, y, newX, newY;

    public short teleport = 0;
    public boolean isDirtyMove;
    public byte isOnGround, isOnGroundNew;
    public boolean wantsToChangeMap;

    public PositionComponent() {
    }

    public PositionComponent(int mapId, short x, short y, int areaId) {
        this.mapId = (short) mapId;
        this.x = x;
        this.y = y;
        this.areaId = areaId;
    }

    public void setAreaId(int areaId) {
        if (this.areaId != areaId) this.areaId = areaId;
//        Utils.logCall();
    }

}