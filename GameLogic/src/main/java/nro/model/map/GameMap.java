package nro.model.map;

public class GameMap {

    private int id;
    private String name;

    public GameMap(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
