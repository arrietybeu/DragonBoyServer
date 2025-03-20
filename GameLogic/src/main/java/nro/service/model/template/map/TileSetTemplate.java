package nro.service.model.template.map;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileSetTemplate {
    private int id;
    private int tile_type;
    private List<TileType> tileTypes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TileType {
        private int id;
        private int tileSetId;
        private int tileTypeValue;
        private int index;
        private int[] index_value;
    }
    
}
