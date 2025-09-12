package nro.server.model.ecs.component.monster;

import com.artemis.Component;

import nro.server.consts.ConstMonster;

/**
 * @author Arriety
 */
public class StateMonsterComponent extends Component {

    public byte status;

    public byte level;
    public byte levelBoss; // sieu quai hay sao ys
    public boolean isBoss;

    public StateMonsterComponent() {
    }

    public StateMonsterComponent(int level) {
        status = ConstMonster.STATUS_WALK;
        this.level = (byte) level;
    }

}
