package nro.server.model.ecs.component.player;


import com.artemis.Component;

/**
 * @author Arriety
 */
public class CurrencyComponent extends Component {
    public long gold;
    public int gem;
    public int ruby;

    public CurrencyComponent() {}

    public CurrencyComponent(long gold, int gem, int ruby) {
        this.gold = gold;
        this.gem = gem;
        this.ruby = ruby;
    }
}