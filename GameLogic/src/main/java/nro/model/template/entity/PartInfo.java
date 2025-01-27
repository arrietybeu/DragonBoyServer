package nro.model.template.entity;

import lombok.Data;
import nro.model.template.PartImageTemplate;

@Data
public class PartInfo {

    private short id;

    private byte type;

    private PartImageTemplate[] pi;

    public short getIcon(int index) {
        return pi[index].icon();
    }

}
