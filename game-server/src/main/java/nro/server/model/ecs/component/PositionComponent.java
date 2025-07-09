package nro.server.model.ecs.component;


import com.artemis.Component;

/**
 * @author Arriety
 */
public class PositionComponent extends Component {

    public int mapId;
    public short x;
    public short y;

    public PositionComponent(int mapId, short x, short y) {
        this.mapId = mapId;
        this.x = x;
        this.y = y;
    }

}