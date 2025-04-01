package nro.server.service.model.template.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transport {

    private String name;
    private String[] planetName;
    private short[] mapIds;

    private int x;
    private int y;

    public String getPlanetNameByGender(int gender) {
        if (planetName.length == 0) {
            return "Arriety";
        }
        if (planetName.length == 1) {
            return planetName[0];
        }
        return planetName[gender];
    }

    public short getMapIdByGender(int gender) {
        if (mapIds.length == 0) {
            return -1;
        }
        if (mapIds.length == 1) {
            return mapIds[0];
        }
        return mapIds[gender];
    }

}
