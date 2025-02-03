package nro.model.map;

import lombok.Getter;

@Getter
public class Waypoint {

    private short minX;

    private short minY;

    private short maxX;

    private short maxY;

    private boolean isEnter;

    private boolean isOffline;

    private String name;

}
