package nro.model.npc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nro.model.LiveObject;

@EqualsAndHashCode(callSuper = true)
@Data
public class Npc extends LiveObject {

    private int status;
    private int templateId;
    private int avatar;

}
