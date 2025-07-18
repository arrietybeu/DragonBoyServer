package nro.server.model.ecs.component;


import com.artemis.Component;

/**
 * @author Arriety
 */
public class PositionComponent extends Component {

    public int mapId;
    public byte areaId;

    public short x;
    public short y;

    public short teleport = 0;

    public PositionComponent() {
    }

    public PositionComponent(int mapId, int areaId, short x, short y) {
        this.mapId = mapId;
        this.x = x;
        this.y = y;
        this.areaId = (byte) areaId;
    }

}