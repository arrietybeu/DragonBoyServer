package nro.server.model.ecs.component.monster;

import com.artemis.Component;

public class StastMonsterComponent extends Component {

    public long hpMax;
    public long hp;

    public byte level;
    public byte levelBoss; // sieu quai hay sao ys
    public boolean isBoss;


    public StastMonsterComponent() {
    }

    public StastMonsterComponent(long hpMax, byte level) {
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.level = level;
    }

}
