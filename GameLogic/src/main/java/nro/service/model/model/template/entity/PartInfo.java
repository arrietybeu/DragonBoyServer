package nro.service.model.model.template.entity;

import lombok.Data;
import nro.service.model.model.template.PartImageTemplate;

@Data
public class PartInfo {

    private short id;

    private byte type;

    private PartImageTemplate[] pi;

    public short getIcon(int index) {
        return pi[index].icon();
    }

}
