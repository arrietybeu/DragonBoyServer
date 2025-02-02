package nro.model.map;

import lombok.Data;

@Data
public class GameMap {

    private int id;
    private String name;

    public GameMap(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
