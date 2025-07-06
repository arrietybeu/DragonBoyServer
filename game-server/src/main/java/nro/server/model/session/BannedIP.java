package nro.server.model.session;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Arriety
 */

@Getter
@Setter
public class BannedIP {
    private Integer id;

    private String mask;

    private Timestamp timeEnd;

    public boolean isActive() {
        return timeEnd == null || timeEnd.getTime() > System.currentTimeMillis();
    }

    @Override
    public int hashCode() {
        return mask != null ? mask.hashCode() : 0;
    }
}
