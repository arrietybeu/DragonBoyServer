package nro.model.npc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nro.model.LiveObject;
import nro.model.template.NpcTemplate;
import nro.server.manager.NpcManager;

@EqualsAndHashCode(callSuper = true)
@Data
public class Npc extends LiveObject {

    private int status;
    private int templateId;
    private int avatar;

    public String findNameNpcByTemplate() {
        NpcTemplate npc = NpcManager.getInstance().getNpcTemplate(this.templateId);
        return npc.name();
    }
}
