package nro.server.model.ecs.component.monster;

import com.artemis.Component;

public class MonsterComponent extends Component {

    public int templateID;
    public String name;

    public MonsterComponent() {}

    public MonsterComponent(int templateID, String name) {
        this.templateID = templateID;
        this.name = name;
    }

}
