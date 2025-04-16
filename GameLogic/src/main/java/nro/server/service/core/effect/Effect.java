package nro.server.service.core.effect;

import nro.server.service.model.entity.Entity;

public interface Effect {

    long getStartTime();

    long getDuration();

    boolean isExpired(long currentTime);

    // call khi effect được apply
    void onApply(Entity target);

    // call khi effect kết thúc
    void onRemove(Entity target);

    void onUpdate(Entity target, long now);

}
