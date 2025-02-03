package nro.model.npc;

import lombok.Getter;
import nro.model.LiveObject;

@Getter
public class Npc extends LiveObject {

    private int npcId;
    private int status;
    private int templateId;
    private int avatar;
}
