package nro.server.model.ecs.component.monster;

import com.artemis.Component;

public class InfoMonsterComponent extends Component {

    public int templateID;
    public String name;

    public InfoMonsterComponent() {}

    public InfoMonsterComponent(int templateID, String name) {
        this.templateID = templateID;
        this.name = name;
    }

}
