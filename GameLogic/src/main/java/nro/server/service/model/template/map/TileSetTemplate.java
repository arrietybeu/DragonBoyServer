package nro.server.service.model.template.map;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class TileSetTemplate {

    private int id;
    private int tileType;
    private List<TileType> tileTypes;

    @Getter
    @Setter
    public static class TileType {
        private int id;
        private int tileSetId;
        private int tileTypeValue;
        private int index;
        private int[] indexValue;
    }

}
