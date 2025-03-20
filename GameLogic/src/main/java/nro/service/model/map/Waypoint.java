package nro.service.model.map;

import lombok.Data;

@Data
public class Waypoint {

    private short minX;
    private short minY;

    private short maxX;
    private short maxY;

    private boolean isEnter;
    private boolean isOffline;
    private String name;

    private int goMap;
    private short goX;
    private short goY;

}
