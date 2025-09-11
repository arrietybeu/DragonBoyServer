package nro.server.model.ecs.component;

import com.artemis.Component;

import nro.server.engine.EntityState;

/**
 * @author Arriety
 */
public class StateComponent extends Component {


    public EntityState state;

    public int targetId = -1;
    
    public byte pkFlag = 0; // Cờ PK
    public byte typePk = 0;

    public boolean isDirty;

}
