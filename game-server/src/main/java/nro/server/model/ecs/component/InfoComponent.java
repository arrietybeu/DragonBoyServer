package nro.server.model.ecs.component;


import com.artemis.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Arriety
 */
public class InfoComponent extends Component {

    public int id;
    public int accountId;
    public String name;
    public byte gender;
    public boolean isOnline;
    public Instant createdAt;

    public byte maxBagSize;
    public byte maxBoxSize;

    // điều này làm sai hướng với ecs nhưng mà thôi ngắn gọn là dc
    public boolean isNewPlayer() {
        long days = this.getDaysSinceCreation();
        return days < 30;
    }

    private long getDaysSinceCreation() {
        return ChronoUnit.DAYS.between(createdAt, Instant.now());
    }
}
